// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// Description:  Search bean
// Date:	         04-22-2003     
// Developer:    Robert Iacobucci	            
// *************************************************************************
//   MODIFICATION INDEX 
//   
//    Index     User Id      Date      Project            Desciption
//   -------   ---------- ----------- -----------------  ----------------------------------------------------------
//    x001      DTC9028    04/22/03    SR28586 PCR3       Created the searchBean program
//    x002      DTC9028    05/06/03    SR28586 PCR3       Added which status to ignore
//    RI003     DTC9028    07/28/05    SR31413 PCR19      Implement datasource modification
// ****************************************************************************************************************

package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import etrieveweb.utility.sqlBean;

public class searchBean extends sqlBean implements java.io.Serializable {

	String company = "";

	ResultSet result = null, result2 = null;
	Statement stmt = null, stmt2 = null;

	public searchBean() {
		super();
	}

	public String Company() {
		return company;
	}
	
	public void setCompany(String input) {
		company = input;
	}

	public synchronized boolean getRows( int customer_num,  int contract_num, String company,   String datalib,  int start_num)   throws Exception {

		String SQLstatement = "";

		SQLstatement =
			"select RHCON#, RHISEQ, RHTYPE, RHDATO, RHPO#, RHSYSD, RHOTYP "
				+ "from "
				+ datalib
				+ ".RACHDRFL "
				+ "where RHCMP='"
				+ company
				+ "' and RHCUS# = "
				+ customer_num
				+ " and RHCON# = " + contract_num
				+ " and RHTYPE not in ('C', 'E', 'T', 'I') "
				+ " and RHOTYP not in ('E', 'I', 'M') "
				+ " and RHSTTS not in ( 'CM', 'CN', '  ') "
			+ " union all " 
			+ "select RHCON#, 0 as RHISEQ, RHTYPE, RHDATO, RHPO#, RHSYSD, ' ' as RHOTYP "
				+ "from "
				+ datalib
				+ ".RAOHDRFL "
				+ "where RHCMP='"
				+ company
				+ "' and RHCUS# = "
				+ customer_num
				+ "  and RHCON# = " + contract_num
				+ " and RHTYPE not in ('C', 'E', 'T', 'I') "
				+ " order by RHCON#, RHISEQ ";
        
		// RI003	stmt =   conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		stmt =   conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI003
		
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

	public synchronized String getNumRows( int customer_num,  int contract_num, String company,   String datalib  )    throws Exception {

		String SQLstatement2 = "";

		SQLstatement2 =
			"select RHCON#, RHISEQ, RHTYPE, RHDATO, RHPO# "
				+ "from "
				+ datalib
				+ ".RACHDRFL "
				+ "where RHCMP='"
				+ company
				+ "' and RHCUS# = "
				+ customer_num
				+ "  and RHCON# = " + contract_num
				+ " and RHTYPE not in ('C', 'E', 'I') "
				+ " and RHOTYP not in ('E', 'I', 'M') "
				+ " and RHSTTS not in ( 'CM', 'CN', '  ') "
			+ " union all " 
			+ "select RHCON#, 0 as RHISEQ, RHTYPE, RHDATO, RHPO# "
				+ "from "
				+ datalib
				+ ".RAOHDRFL "
				+ "where RHCMP='"
				+ company
				+ "' and RHCUS# = "
				+ customer_num
				+ "  and RHCON# = " + contract_num
				+ " and RHTYPE not in ('C', 'E', 'I') "
				+ " order by RHDATO desc";

		// RI003	stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);		// RI003
		
		result2 = stmt2.executeQuery(SQLstatement2);
		
		int totalrecords = 0;
		
		while (result2.next()) {
			totalrecords = result2.getRow();
		}
		
		return Integer.toString(totalrecords);
	}

	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI003
		endcurrentResultset(result2);	// RI003
		endcurrentStatement(stmt);		// RI003
		endcurrentStatement(stmt2);		// RI003
	}

}