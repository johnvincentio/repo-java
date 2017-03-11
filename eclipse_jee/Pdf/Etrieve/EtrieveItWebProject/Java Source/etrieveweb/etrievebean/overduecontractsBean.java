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
//    RI001     DTC2073     04/22/05    SR31413 PCR29      Created new over due contracts report
//    RI002     DTC9028     05/20/05    SR31413 PCR30      Date modification (current date minus 2 days)
//    RI003     DTC9028     07/28/05    SR31413 PCR19      Implement datasource modification
//    RI004     DTC2073     11/10/05    SR35420            Implement retrieval limit
//    RI005     DTC2073     11/15/05    SR35880 		   Added a method to retrieve order comments 
//	  RI006     DTC2073     01/11/06    SR35879            Add Job Name to the report.
// ****************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.io.*;
import etrieveweb.utility.sqlBean;

public class overduecontractsBean extends sqlBean implements java.io.Serializable {

    String company = "";

    ResultSet result = null, result2 = null, result3 = null;
    Statement stmt = null, stmt2 = null, stmt3 = null;
		
    public overduecontractsBean() {
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

		Calendar Today = Calendar.getInstance();

		Today.add(Calendar.DATE, + 2); // RI002
		
		int todayDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

		// ** Note: The previous hard-coded order by was RHDATO desc, RDCON# desc

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;
			
        if (jobnumber.equals(""))
            SQLstatement =
                "select distinct RDCON#, RDITEM, RDCATG ,RDCLAS, (RDQTY-RDQTYR) as RDQTY, RDLOC, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHERDT, RHERTM, CJNAME  "
                    + "from "
                    + datalib
                    + ".RAODETFL, "
                    + datalib
                    + ".EQPCCMFL, "
                    + datalib
                    + ".RAOHDRFL "
                    + "  Left outer join "		// RI006
                    + datalib 					// RI006
                    + ".CUSJOBFL On RHCMP=CJCMP and RHCUS#=CJCUS#  and RHJOB#=CJJOB# "	// RI006 
                    + "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and "		// RI004
                    + "(RHCMP=RDCMP and RHCON#=RDCON#) and "
                    + "RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
					+ " and RHERDT <=" + todayDate
                    + " " + str_orderBy;
                    
        else
            SQLstatement =
                "select distinct RDCON#, RDITEM, RDCATG ,RDCLAS, (RDQTY-RDQTYR) as RDQTY, RDLOC, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHERDT, RHERTM, CJNAME  "
                    + "from "
                    + datalib
                    + ".RAODETFL, "
                    + datalib
                    + ".EQPCCMFL, "
                    + datalib
                    + ".RAOHDRFL "
                    + "  Left outer join "		// RI006
                    + datalib 					// RI006
                    + ".CUSJOBFL On RHCMP=CJCMP and RHCUS#=CJCUS#  and RHJOB#=CJJOB# "	// RI006
                    + "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and "		// RI004
                    + "(RHCMP=RDCMP and RHCON#=RDCON#) and "
                    + "RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RHERDT <=" + todayDate
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

        // RI003	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
        stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI003
        
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
        
        Calendar Today = Calendar.getInstance();
        
        Today.add(Calendar.DATE, +2); // RI002
        		
		int todayDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

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
                    + "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and "		// RI004
                    + "(RHCMP=RDCMP and RHCON#=RDCON#) and "
                    + "RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RHERDT <=" + todayDate;
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
                    + "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and "		// RI004
                    + "(RHCMP=RDCMP and RHCON#=RDCON#) and "
                    + "RDCMP='"
                    + company
                    + "' and RDTYPE='RI' and RDCUS# in "
                    + customer_list
                    + " and RHERDT <=" + todayDate
                    + " and RDCON# in "
                    + "(select RHCON# from "
                    + datalib
                    + ".RAOHDRFL "
                    + "where RHCMP='"
                    + company
                    + "' and RHJOB# ='"
                    + jobnumber
                    + "') ";

        // RI003	stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
        stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI003
        
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

        // RI003	stmt3 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
        stmt3 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI003
        
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
	// RI005 - Get line item comments
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
		endcurrentResultset(result);	// RI003
		endcurrentResultset(result2);	// RI003
		endcurrentResultset(result3);	// RI003
		endcurrentStatement(stmt);		// RI003
		endcurrentStatement(stmt2);		// RI003
		endcurrentStatement(stmt3);		// RI003
    }

}