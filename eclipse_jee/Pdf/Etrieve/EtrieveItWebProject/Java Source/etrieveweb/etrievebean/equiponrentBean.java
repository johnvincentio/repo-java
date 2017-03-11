// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
//   MODIFICATION INDEX 
//   
//    Index     User Id       Date      Project            Desciption
//    -------  ----------  ----------- -----------------  -----------------------------------------------------
//    x001      DTC9028     02/11/03    SR26609 PCR1       Added RHPO# to the SQL
//    x002      DTC9028     02/11/03    SR26609 PCR1       Changed the ordered by to be RDDATO
//    x003      DTC2073     03/04/04    SR28586 PCR19      Add  the field RHORDB to the SQL statements
//    x004      DTC2073     04/09/04    SR31413 PCR11      Add RDSEQ# to the SQLs in getRows and getNumRow
//    x005      DTC2073     04/09/04    SR31412 PCR11      Added the getPickup method
//	  x006     	DTC2073  	08/19/04    SR31413 PCR5       Add the order by variable to the logic. 
//    x007      DTC9028     01/12/05    TT404162           Corrected qty error for bulk items   
//    RI008     DTC9028     04/29/05    SR31413 PCR29      Add Estimated Return Date
//    RI009     DTC9028     07/28/05    SR31413 PCR19      Implement datasource modification
//    RI010     DTC2073     10/20/05    SR35420            Data Retrieval Limit
//    RI011     DTC2073     11/15/05    SR35880 		   Added a method to retrieve order comments  
//	  RI012     DTC2073     01/11/06    SR35879            Add Job Name to the report.
//    RI013     DTC2073     02/07/06    SR35873 		   Abbreviated equipment release
// ****************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import etrieveweb.utility.sqlBean;

public class equiponrentBean extends sqlBean implements java.io.Serializable {

	String company = "";

	ResultSet result = null, result2 = null, result3 = null;
	Statement stmt = null, stmt2 = null, stmt3 = null;

    public equiponrentBean() {
        super();
    }

    public String Company() {
        return company;
    }
    public void setCompany(String input) {
        company = input;
    }

    public synchronized boolean getRows(
        String customer_list,
        int customer_num,
        String company,
        String datalib,
        int start_num,
        String jobnumber,
		String str_orderBy)
        throws Exception {

        String SQLstatement = "";

		// ** Note: The previous hard-coded order by was RHDATO desc, RDCON# desc

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;
			
        if (jobnumber.equals(""))
            SQLstatement =
                "select distinct RDCON#, RDSEQ#, RDITEM, RDCATG ,RDCLAS, (RDQTY-RDQTYR) as RDQTY, RDLOC, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHERDT, CJNAME  "	// RI013
                    + "from "
                    + datalib
                    + ".RAODETFL, "
                    + datalib
                    + ".EQPCCMFL, "
                    + datalib
                    + ".RAOHDRFL "
                    + "  Left outer join "		// RI012
                    + datalib 					// RI012
                    + ".CUSJOBFL On RHCMP=CJCMP and RHCUS#=CJCUS#  and RHJOB#=CJJOB# "	// RI012 
                    + "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and "	// RI010
                    + "(RHCMP=RDCMP and RHCON#=RDCON#) and "
                    + "RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " " + str_orderBy;

        else
            SQLstatement =
                "select distinct RDCON#, RDSEQ#, RDITEM, RDCATG ,RDCLAS, (RDQTY-RDQTYR) as RDQTY, RDLOC, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHERDT, CJNAME  "	// RI013
                    + "from "
                    + datalib
                    + ".RAODETFL, "
                    + datalib
                    + ".EQPCCMFL, "
                    + datalib
                    + ".RAOHDRFL "
                    + "  Left outer join "		// RI012
                    + datalib					// RI012 
                    + ".CUSJOBFL On RHCMP=CJCMP and RHCUS#=CJCUS#  and RHJOB#=CJJOB# "	// RI012 
                    + "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and "	// RI010
                    + "(RHCMP=RDCMP and RHCON#=RDCON#) and "
                    + "RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDCON# in "
                    + "(select RHCON# from "
                    + datalib
                    + ".RAOHDRFL "
                    + "where RHCMP='"
                    + company
                    + "' and RHJOB# ='"
                    + jobnumber
                    + "') "
                    + " " + str_orderBy;

        // RI009	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
		stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI009
                
        result = stmt.executeQuery(SQLstatement);
        
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
        String customer_list,
        int customer_num,
        String company,
        String datalib,
        String jobnumber)
        throws Exception {

        String SQLstatement2 = "";

        if (jobnumber.equals(""))
            SQLstatement2 =
                "select distinct RDCON#, RDITEM, RDQTY, RDLOC, RHJOBL, RHPO#, ECDESC, RHORDB, RDSEQ#  "
                    + "from "
                    + datalib
                    + ".RAODETFL, "
                    + datalib
                    + ".EQPCCMFL, "
                    + datalib
                    + ".RAOHDRFL "
                    + "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and "	// RI010
                    + "(RHCMP=RDCMP and RHCON#=RDCON#) and "
                    + "RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list;
        else
            SQLstatement2 =
                "select distinct RDCON#, RDITEM, RDQTY, RDLOC, RHJOBL, RHPO#, ECDESC, RHORDB, RDSEQ#  "
                    + "from "
                    + datalib
                    + ".RAODETFL, "
                    + datalib
                    + ".EQPCCMFL, "
                    + datalib
                    + ".RAOHDRFL "
                    + "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and "	// RI010
                    + "(RHCMP=RDCMP and RHCON#=RDCON#) and "
                    + "RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RDCON# in "
                    + "(select RHCON# from "
                    + datalib
                    + ".RAOHDRFL "
                    + "where RHCMP='"
                    + company
                    + "' and RHJOB# ='"
                    + jobnumber
                    + "') ";

        // RI009	stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
        stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);		// RI009
        
        result2 = stmt2.executeQuery(SQLstatement2);
        
        int totalrecords = 0;
        
        while (result2.next()) {
            totalrecords = result2.getRow();
        }
        
        return Integer.toString(totalrecords);
    }


    public synchronized boolean getPickup(
        String customer_list,
        int customer_num,
        String company,
        String datalib)
        throws Exception {

        String SQLstatement3 = "";

            SQLstatement3 =
                "select RUCON#, RUSEQ#, RUEQP#  "
                    + "from "
                    + datalib
                    + ".RAPKUPF4 "
                    + "where RUCMP='"
                    + company.trim()
                    + "' and RUCUS# in "
                    + customer_list
                    + " and RUCON# > 0 and RUSTTS = 'OP' ";

        // RI009	stmt3 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
        stmt3 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);		// RI009
        
        result3 = stmt3.executeQuery(SQLstatement3);
        
        return (result3 != null);
    }

    public boolean getNext3() throws Exception {
        return result3.next();
    }

    public String getColumn3(String colNum) throws Exception {
        return result3.getString(colNum);
    }


	//*******************************************
	// RI011 - Get line item comments
	//******************************************
			
	public synchronized String getItemComments(String company, String datalib, int contract, int lineno) throws Exception { 

		String itemcomments = "";
		ResultSet result4=null ;
		Statement stmt4=null ;
				
		try 
		{		
			
			String SQLstatement4 = "";
				
			SQLstatement4 = 
				"select OCCMNT" +
				"  From "+ datalib + ".ORDCOMFL  " +
				"  where OCCMP='" + company + "'   " +
				"  and OCREF#= " +  contract +
				"  and OCISEQ=  0 " +
				"  and OCTYPE= 'R' " +
				"  and OCASEQ= " + lineno ;
						
			stmt4=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	
				
			result4=stmt4.executeQuery(SQLstatement4);
					
			if (result4 != null)
			{		
					
				while(result4.next())
				{
					itemcomments = itemcomments.trim() + " " + result4.getString("OCCMNT").trim();
				}
									 							
			}										
		 		
		} catch (SQLException e) {
			
			System.err.println(e.getMessage());
			itemcomments = "";
		}
        
		endcurrentResultset(result4);			
		endcurrentStatement(stmt4);
				
		return itemcomments;

	}
	
    public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI009
		endcurrentResultset(result2);	// RI009
		endcurrentResultset(result3);	// RI009
		endcurrentStatement(stmt);		// RI009
		endcurrentStatement(stmt2);		// RI009
		endcurrentStatement(stmt3);		// RI009
    }

}