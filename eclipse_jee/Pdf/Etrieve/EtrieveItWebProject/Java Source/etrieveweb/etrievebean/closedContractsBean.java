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
//    Index     User Id       Date       Project          Desciption
//   -------   ----------  ----------   ---------------   ----------------------------------------
//    RI001     DTC2073     07/15/05     SR31413 PCR23     Added closed contracts report.
//    RI002     DTC9028     07/28/05     SR31413 PCR19     Implement datasource modifications
//    RI003     DTC2073     10/21/05     SR35420    	   Data Retrieval Limit
//	  RI004     DTC2073     01/11/06     SR35879           Add Job Name to the report.
// ****************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.Date;
import java.util.Calendar;
import etrieveweb.utility.sqlBean;

public class closedContractsBean extends sqlBean implements java.io.Serializable {

	String company = "";

	ResultSet result = null, result2 = null;
	Statement stmt = null, stmt2 = null;

	public closedContractsBean() {
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
		String str_orderBy,
		String selectedYear)		// RI004
		throws Exception {

		Calendar Today = Calendar.getInstance();

		Today.add(Calendar.MONTH, - 6);
		
		int cutoffDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

		// *************************************************************
		// RI004 - Create date filter based on the page selection.  
		// The default is the last 6 months.
		// *************************************************************
			
		String cutoffStartDate = (String) Integer.toString(cutoffDate) ;	// RI004
		String cutoffEndDate = "99999999";									// RI004
		
		if (!selectedYear.trim().equals("Last 6 Months")) {	// RI004
			cutoffStartDate = selectedYear.trim() + "0101";		// RI004
			cutoffEndDate   = selectedYear.trim() + "1299";		// RI004
		}														// RI004 

		String SQLstatement = "";

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;
		
		if (jobnumber.equals("") ) {

			SQLstatement =
				"select distinct RDCON#, RDITEM, RDQTY, RDLOC, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHLRDT ,CJNAME  "
					+ "from "
					+ datalib + ".RACHDRH4, "
					+ datalib + ".RACDETH3, "
					+ datalib + ".EQPCCMFL "
					+ "  Left outer join "		// RI004
                    + datalib 					// RI004
                    + ".CUSJOBFL On RHCMP=CJCMP and RHCUS#=CJCUS#  and RHJOB#=CJJOB# "		// RI004
					+ " where RHCMP='" + company + "' "
					+ " and RHCUS# in "
					+ customer_list
					+ " and RHSYSD > 0 " 					// RI004
					+ " and RHTYPE in ('O','F') "
					+ " and RHOTYP ='F' "
					+ " and RHLRDT >= " + cutoffStartDate	// RI004
					+ " and RHLRDT <= " + cutoffEndDate		// RI004
					+ " and RHCMP = RDCMP "
					+ " and RDTYPE='RI' "
					+ " and RDCUS# in " 
					+ customer_list
					+ " and RHCON# = RDCON#  "		
					+ " and RHISEQ = RDISEQ  "				
					+ " and RDSTTS <> 'EX'  "
					+ " and RDCMP = ECCMP "
					+ " and RDCATG = ECCATG "
					+ " and RDCLAS = ECCLAS "		
					+ " " + str_orderBy;
					
		} else {

			SQLstatement =
				"select distinct RDCON#, RDITEM, RDQTY, RDLOC, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHLRDT, CJNAME  "
					+ "from "
					+ datalib + ".RACHDRH4, "
					+ datalib + ".RACDETH3, "
					+ datalib + ".EQPCCMFL "
					+ "  Left outer join "		// RI004
                    + datalib 					// RI004
                    + ".CUSJOBFL On RHCMP=CJCMP and RHCUS#=CJCUS#  and RHJOB#=CJJOB# "		// RI004
					+ " where RHCMP='" + company + "' "
					+ " and RHCUS# in "
					+ customer_list
					+ " and RHSYSD > 0 " 					// RI004
					+ " and RHTYPE in ('O','F') "
					+ " and RHOTYP ='F' "
					+ " and RHLRDT >= " + cutoffStartDate	// RI004
					+ " and RHLRDT <= " + cutoffEndDate		// RI004
					+ " and RHCMP = RDCMP "
					+ " and RDTYPE='RI' "
					+ " and RDCUS# in " 
					+ customer_list
					+ " and RHCON# = RDCON#  "		
					+ " and RHISEQ = RDISEQ  "				
					+ " and RDSTTS <> 'EX'  "
					+ " and RDCMP = ECCMP "
					+ " and RDCATG = ECCATG "
					+ " and RDCLAS = ECCLAS "		
					+ " and RDCON# in "
					+ "(select RHCON# from "
					+ datalib
					+ ".RACHDRFL "
					+ "where RHCMP='"
					+ company
					+ "' and RHJOB# ='"
					+ jobnumber
					+ "') "
					+ " " + str_orderBy;
		}

		// RI002	stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
	
		stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI002
		
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
		String jobnumber,
		String selectedYear)		// RI004
		throws Exception {

		Calendar Today = Calendar.getInstance();

		Today.add(Calendar.MONTH, - 6);
		
		int cutoffDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

		// *************************************************************
		// RI004 - Create date filter based on the page selection.  
		// The default is the last 6 months.
		// *************************************************************
			
		String cutoffStartDate = (String) Integer.toString(cutoffDate) ;	// RI004
		String cutoffEndDate = "99999999";									// RI004
		
		if (!selectedYear.trim().equals("Last 6 Months")) {	// RI004
			cutoffStartDate = selectedYear.trim() + "0101";		// RI004
			cutoffEndDate   = selectedYear.trim() + "1299";		// RI004
		}														// RI004 

		String SQLstatement2 = "";

		if (jobnumber.equals(""))  {


			SQLstatement2 =
				" select rdcmp, sum(total) as total from ( "
					+ " select distinct RDCMP, RDCON#, RDITEM, RDQTY, RDLOC, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHLRDT, 1 as total  "
						+ "from "
						+ datalib + ".RACHDRH4, "
						+ datalib + ".RACDETH3, "
						+ datalib + ".EQPCCMFL "
						+ " where RHCMP='" + company + "' "
						+ " and RHCUS# in "
						+ customer_list
						+ " and RHSYSD > 0 " 					// RI004
						+ " and RHTYPE in ('O','F')  "
						+ " and RHOTYP ='F' "
						+ " and RHLRDT >= " + cutoffStartDate	// RI004
						+ " and RHLRDT <= " + cutoffEndDate		// RI004
						+ " and RHCMP = RDCMP "
						+ " and RDTYPE='RI' "
						+ " and RDCUS# in " 
						+ customer_list
						+ " and RHCON# = RDCON#  "		
						+ " and RHISEQ = RDISEQ  "				
						+ " and RDSTTS <> 'EX' "
						+ " and RDCMP = ECCMP "
						+ " and RDCATG = ECCATG "
						+ " and RDCLAS = ECCLAS "
						+ " group by  RDCMP, RDCON#, RDITEM, RDQTY, RDLOC, RHJOBL, RHDATO, RHPO#,ECDESC, RHORDB, RDSEQ#, RHLRDT"
				+ ") as max "               
				+ "group by rdcmp";	
				
		} else {
			
			SQLstatement2 =
				" select rdcmp, sum(total) as total from ( "
					+ " select distinct RDCMP, RDCON#, RDITEM, RDQTY, RDLOC, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHLRDT, 1 as total  "
						+ "from "
						+ datalib + ".RACHDRH4, "
						+ datalib + ".RACDETH3, "
						+ datalib + ".EQPCCMFL "
						+ " where RHCMP='" + company + "' "
						+ " and RHCUS# in "
						+ customer_list
						+ " and RHSYSD > 0 "					// RI004
						+ " and RHTYPE in ('O','F')  "
						+ " and RHOTYP ='F' "
						+ " and RHLRDT >= " + cutoffStartDate	// RI004
						+ " and RHLRDT <= " + cutoffEndDate		// RI004
						+ " and RHCMP = RDCMP "
						+ " and RDTYPE='RI' "
						+ " and RDCUS# in " 
						+ customer_list
						+ " and RHCON# = RDCON#  "		
						+ " and RHISEQ = RDISEQ  "				
						+ " and RDSTTS <> 'EX' "
						+ " and RDCMP = ECCMP "
						+ " and RDCATG = ECCATG "
						+ " and RDCLAS = ECCLAS "
						+ " and RDCON# in "
						+ "(select RHCON# from "
						+ datalib
						+ ".RACHDRFL "
						+ "where RHCMP='"
						+ company
						+ "' and RHJOB# ='"
						+ jobnumber
						+ "') "
						+ " group by  RDCMP, RDCON#, RDITEM, RDQTY, RDLOC, RHJOBL, RHDATO, RHPO#,ECDESC, RHORDB, RDSEQ#, RHLRDT"
				+ ") as max "               
				+ "group by rdcmp";
				
						
		}
		
		// RI002	stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		stmt2 = conn.createStatement();	// RI002
		
		result2 = stmt2.executeQuery(SQLstatement2);
        
		String totalrecords = "0";
        
		while (result2.next()) {
			totalrecords = result2.getString("Total");

		}
		
		return totalrecords;
	}

	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI002
		endcurrentResultset(result2);	// RI002
		endcurrentStatement(stmt);		// RI002
		endcurrentStatement(stmt2);		// RI002
	}

}