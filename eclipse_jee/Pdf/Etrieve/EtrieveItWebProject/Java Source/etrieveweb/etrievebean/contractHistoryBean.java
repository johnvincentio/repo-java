// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
//************************************************************************** 
// Description:  Contract History List
// Date:	       12-14-2003     
// Developer:    Bob Iacobucci	            
// *************************************************************************    
// *************************************************************************
//   MODIFICATION INDEX 
//   
//    Index    User Id      Date     Project           Desciption
//   -------- ---------- ---------- ----------------  ----------------------------------------------------------
//    x001     DTC9028    11/14/03   SR28586 PCR8      Created bean
//    RI002    DTC9028    08/01/05   SR31413 PCR19     Datasource implementation modification
//    RI003    DTC2073    10/19/05   SR35420		   Data Retrieval Limit
// *************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import etrieveweb.utility.sqlBean;

public class contractHistoryBean extends sqlBean implements java.io.Serializable {

	String company = "";

	ResultSet result = null, result2 = null;
	Statement stmt = null, stmt2 = null;

	public contractHistoryBean() {
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
		String str_contract, 
		String str_sequence)
		throws Exception {

		String SQLstatement =
			"select distinct RHCON#, RHISEQ, RHDATO, RHTYPE, RHPO#, RHJOB#, RHSYSD, RHOTYP  "
				+ "from "
				+ datalib
				+ ".RACHDRFL "		// RI003                  
				+ "where RHCMP='"
				+ company
				+ "' and RHCON# = " + str_contract
				+ "  and RHTYPE not in (' ', 'E', 'X', 'Y', 'M', 'U') "
				+ "  and RHEMP# <> ' ' "
			+ " union "
				+" select distinct RHCON#, 0 as RHISEQ, RHDATO, RHTYPE, RHPO#, RHJOB#, RHSYSD, ' ' as RHOTYP  "
				+ "from "
				+ datalib
				+ ".RAOHDRF5 "                  
				+ "where RHCMP='"
				+ company
				+ "' and RHCON# = " + str_contract
				+ "  and RHTYPE not in (' ', 'E', 'X', 'Y', 'M', 'U') "
				+ "  and RHEMP# <> ' ' "
				+  "  ORDER BY  RHCON#, RHISEQ  " ;

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
		String str_contract,
		String str_sequence)
		throws Exception {

		String SQLstatement2 =
			"select RHCMP, sum(total) as Total from ("
				+ "select RHCMP, count(*) as total "
					+ "from "
					+ datalib
					+ ".RACHDRFL "		// RI003                  
					+ "where RHCMP='"
					+ company
					+ "' and RHCON# = " + str_contract
					+ "  and RHTYPE not in (' ', 'E', 'X', 'Y', 'M', 'U') "
					+ "  and RHEMP# <> ' ' "
					+ " GROUP BY rhcmp"
				+ " union "
				+" select RHCMP, count(*) as total "
					+ "from "
					+ datalib
					+ ".RAOHDRF5 "                  
					+ "where RHCMP='"
					+ company
					+ "' and RHCON# = " + str_contract
					+ "  and RHTYPE not in (' ', 'E', 'X', 'Y', 'M', 'U') "
					+ "  and RHEMP# <> ' ' "
					+ " GROUP BY rhcmp "
			+ ") AS MAX  "    
			+ " GROUP BY RHCMP";

			// RI002	stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			stmt2 = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI002
			
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