// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// ************************************************************************** 
// * Description:  Corp Link Accounts
// * Date:	 02-27-2001     
// * Developer:    Yichun Erica Lin	            
// *************************************************************************
// **************************************************************************************************************
//   MODIFICATION INDEX 
//   
//    Index    User Id       Date       Project             Desciption
//    ------  -----------  ----------  -----------------   --------------------------------------------
//    x001     DTC2073      04/15/04    SR28586 PCR23       Added new methods to retrieve the last activity date
//    RI002    DTC9028      06/21/05    SR31413 PCR19       Implement datasource
// **************************************************************************************************************

package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import java.util.*;
import etrieveweb.utility.sqlBean;

public class corplinkaccountBean
    extends sqlBean
    implements java.io.Serializable {

    ResultSet result = null, result2 = null, result3 = null, result4 = null;
    Statement stmt = null, stmt2 = null, stmt3 = null, stmt4 = null;
    String allCustList;


    public corplinkaccountBean() {
        super();
    }

    public synchronized boolean getRows(
        int customer_num,
        String company,
        String datalib,
        int start_num)
        throws Exception {

        String SQLstatement =
            "select * "
                + "from "
                + datalib
                + ".CUSMASF4 "
                + " where CMCMP='"
                + company
                + "' and CMCRP#="
                + customer_num
                + " order by CMNAME";

        stmt =
            conn.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        result = stmt.executeQuery(SQLstatement);

        // ********************************************
        // Execute the SQL to retrieve last activity date
        // ********************************************

        getAllCustList( customer_num, company, datalib);
    
        getDateQry(company, datalib);



        if (start_num > 0)
            result.absolute(start_num);

        return (result != null);

    }

    public boolean getNext() throws Exception {
        return result.next();
    }

    public String getColumn(String colNum) throws Exception {
        return result.getString(colNum);
    }

    public synchronized String getNumRows(
        int corplink_num,
        String company,
        String datalib)
        throws Exception {

        String SQLstatement2 =
            "select count(*) as total_num_rows "
                + "from "
                + datalib
                + ".CUSMASF4 "
                + " where CMCMP='"
                + company
                + "' and CMCRP#="
                + corplink_num;

        stmt2 = conn.createStatement();
        result2 = stmt2.executeQuery(SQLstatement2);
        result2.next();
        return result2.getString("total_num_rows");
    }

    // *********************************************************************
    //  Build the selection list that will contain all the accounts for the NARP#  
    // *********************************************************************

    public void getAllCustList( int customer_num, String company, String datalib) throws Exception {

	String SQLstatement4 =   
		"select CMCUS# "
		+ "from "
                		+ datalib
                		+ ".CUSMASF4 "
                		+ " where CMCMP='"
                		+ company
                		+ "' and CMCRP#="
                		+ customer_num
                		+ " order by CMCUS# ";

	stmt4 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        	result4 = stmt4.executeQuery(SQLstatement4);

	if (result4.next() )   {

		allCustList = "( " + result4.getString("CMCUS#");

		while ( result4.next() )
			allCustList += ","  + result4.getString("CMCUS#");

		allCustList += ")" ;

	} else
		allCustList = "()" ;

   }


   // ************************************************
   //  Retrieve the last activity date for the customer list
   // ************************************************

   public void getDateQry( String company, String datalib) throws Exception {

 	Calendar Today = Calendar.getInstance();
	Calendar cutoffDate = Calendar.getInstance();

	 int temptoday = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);
        	 String agingdate = Integer.toString(temptoday);

	int year = 0, month = 0, day = 0;

	year = Integer.valueOf(agingdate.substring(0, 4)).intValue();
	month = Integer.valueOf(agingdate.substring(4, 6)).intValue();
	day = Integer.valueOf(agingdate.substring(6, 8)).intValue();

	// *************************************************************
	// Account for the Java date handling where January = 0 and December = 11
	// *************************************************************

	month = month -1;

	// ****************************************************
	// Create the cut-off date to be 365 days previous to today's date
	// ****************************************************

	cutoffDate.set(year, month, day);
	cutoffDate.add(Calendar.DATE,     -365);

	 temptoday = cutoffDate.get(Calendar.YEAR)*10000 + (cutoffDate.get(Calendar.MONTH)+1)*100 + cutoffDate.get(Calendar.DAY_OF_MONTH);
        	 agingdate = Integer.toString(temptoday);

	String SQLstatement1 =
		"SELECT RHCMP, RHCUS#,  max(MaxDates) as RHSYSD from  ( "  +                                                          
			"( SELECT    RHCMP ,RHCUS# , MAX(RHSYSD) as MaxDates " +
				" from " + datalib +  ".RACHDRH4 " +
				" where  RHCMP = '" + company.trim() + "'" + 
				" and RHCUS# in " +  allCustList + 
				" and RHSYSD >= " + agingdate +
				" and RHCON# > 0 " +
				" and RHTYPE in ('O', 'Q', 'S') " +
				" group by RHCMP, RHCUS# )" +                
			" UNION "  +
			" ( SELECT  RHCMP ,RHCUS# , MAX(RHSYSD) as MaxDates " +
				" from " + datalib +  ".RAOHDRFL " +
				" where  RHCMP = '" + company.trim() + "'" +
				" and RHCON# > 0 " +
				" and RHCUS# in " +  allCustList + 
				" and RHSYSD >= " + agingdate +
				" and  RHTYPE in ('O', 'Q', 'S') " +
				" group by RHCMP, RHCUS#  ) " +                
		")  AS MaxTable Group By  RHCMP ,RHCUS#" ;


	// RI002	stmt3 =   conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE,  ResultSet.CONCUR_READ_ONLY);
	
	stmt3 =   conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,  ResultSet.CONCUR_READ_ONLY);	// RI002

	result3 = stmt3.executeQuery(SQLstatement1);

   }

  // *****************************************
  //  Retrieve the last activity date for an account  
  // *****************************************

   public String getDate()  {

	try {

		int cusno = Integer.valueOf(result.getString("CMCUS#")).intValue();

		result3.beforeFirst();

		while (result3.next())

			if ( cusno == Integer.valueOf(result3.getString("RHCUS#")).intValue()) 
				return result3.getString("RHSYSD") ;


	}   catch (Exception e)   {
		System.out.println(e);
	}
		return "";	
   }
	

    public void cleanup() throws Exception {
		// RI002	stmt.close();
		endcurrentResultset(result);			// RI002
		endcurrentStatement(stmt);				// RI002
		endcurrentResultset(result2);			// RI002
		endcurrentStatement(stmt2);				// RI002
		endcurrentResultset(result3);			// RI002
		endcurrentStatement(stmt3);				// RI002
		endcurrentResultset(result4);			// RI002
		endcurrentStatement(stmt4);				// RI002
    }

}