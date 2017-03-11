// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// ********************************************************************************************************************
//   MODIFICATION INDEX 
//   
//    Index       User Id        Date          Project                       Desciption
//   ---------   -----------  -----------  ---------------   ----------------------------------------------------------
//    x001        DTC9028      11/26/02    SR26609 PCR1      Select only records with a balance
//    x002        DTC9028      02/11/03    SR26609 PCR1      Added date selection to display aging buckets
//    x003        DTC9028      03/02/04    SR28586 PCR27     Include credit balances for Canadia Etrieve (company = CR)
//    x004        DTC9028      03/29/04    SR31413 PCR18     Change ORDER BY in the getRows method to be AHINVD desc
//    x005        DTC2073	   02/06/04	   SR31413 PCR 7     Include 'Validated Credit' balance invoices as Open Invoices.
//	  x006     	  DTC2073      08/19/04    SR31413 PCR5      Add the order by variable to the logic. 
//    RI007       DTC9028      07/22/05    SR31413 PCR19     Implement datasource
// ********************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import etrieveweb.utility.sqlBean;

public class openinvoiceBean extends sqlBean implements java.io.Serializable {

	ResultSet result = null, result2 = null;
	Statement stmt = null, stmt2 = null;

	public openinvoiceBean() {
		super();
	}

	public synchronized boolean getRows(
		String customer_list,
		int customer_num,
		String company,
		String datalib,
		String str_dateselect,
		int start_num,
		String str_orderBy)
		throws Exception {

		String SQLstatement;

		// ** Note for CANADA: The previous hard-coded order by was RDSYSD desc, RDCON# desc, RDISEQ desc
		// ** Note for USA: The previous hard-coded order by was A.AHCMP, A.AHCUS#, A.AHINVD desc, A.AHINV#, A.AHISEQ

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;
			
		// *******************************************************************************************
		// Canada (company=CR) will ALWAYS select balances not equal to zeros (including negatives)
		// *******************************************************************************************

		if (company.trim().equals("CR")) {

			SQLstatement =
				"SELECT A.*, B.*, (AHAMT$-AHCBAL) as CurrBalance "
					+ " FROM "
					+ datalib
					+ ".arihdrf2 A "
					+ " LEFT OUTER JOIN "
					+ datalib
					+ ".rachdrfl B ON AHCMP=RHCMP AND ahcus# = rhcus# and ahinv# = rhcon# and ahiseq = rhiseq"
					+ " WHERE (ahcmp = '"
					+ company
					+ "' AND ahcus# in "
					+ customer_list
					+ ") and AHSTTS <> 'PD' and AHCBAL  <> 0 "
					+ str_dateselect.trim()
					+ " " + str_orderBy;
		} else {

			// ****************************************************************************************
			// Domestic will select invoices with a positive balance or invoices with a negative 
			// balance that are considered to be 'Valid Credit' credits.  A valid credit is 
			// determined by the AHCOLS field having the code range from B001 to B498.  - x005
			// ****************************************************************************************

			SQLstatement =
				"SELECT A.*, C.*, (A.AHAMT$-A.AHCBAL) as CurrBalance, AHCOLS "
					+ " FROM "
					+ datalib
					+ ".arihdrf2 A "
					+ " LEFT OUTER JOIN "
					+ datalib
					+ ".arihd2fl B ON A.ahcmp=B.ahcmp AND A.ahcus# = B.ahcus# and "
					+ "  A.ahinv# = B.ahinv# and A.ahiseq = B.ahiseq "
					+ "  LEFT OUTER JOIN "
					+ datalib
					+ ".rachdrfl C ON A.AHCMP=RHCMP AND A.ahcus# = rhcus# and A.ahinv# = rhcon# and A.ahiseq = rhiseq"
					+ " WHERE (A.ahcmp = '"
					+ company
					+ "' AND A.ahcus# in "
					+ customer_list
					+ ") and A.AHSTTS <> 'PD' and "
					+ " ( ( A.AHCBAL > 0  ) OR "
					+ "   ( A.AHCBAL < 0 AND B.AHCOLS >= 'B001' AND B.AHCOLS <= 'B498') ) "
					+ str_dateselect.trim()
					+ " " + str_orderBy;
		}

		//System.err.println(SQLstatement);

		stmt =
			conn.createStatement(
				ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

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
		String str_dateselect)
		throws Exception {

		String SQLstatement2 = "";

		// *******************************************************************************************
		// Canada (company=CR) will ALWAYS select balances not equal to zeros (including negatives)
		// *******************************************************************************************

		if (company.trim().equals("CR")) {

			SQLstatement2 =
				"SELECT count(*) as total_num_rows "
					+ " FROM "
					+ datalib
					+ ".arihdrf2 "
					+ " WHERE (ahcmp = '"
					+ company
					+ "' AND ahcus# in "
					+ customer_list
					+ ") and AHSTTS <> 'PD'  and AHCBAL  <> 0 "
					+ str_dateselect.trim();

		} else {

			// ****************************************************************************************
			// Domestic will select invoices with a positive balance or invoices with a negative 
			// balance that are considered to be 'Valid Credit' credits.  A valid credit is 
			// determined by the AHCOLS field having the code range from B001 to B498.  - x005
			// ****************************************************************************************

			SQLstatement2 =
				"SELECT count(*) as total_num_rows  "
					+ "FROM  "
					+ datalib
					+ ".arihdrf2  A  "
					+ "  LEFT OUTER JOIN "
					+ datalib
					+ ".arihd2fl B ON A.ahcmp=B.ahcmp AND A.ahcus# = B.ahcus# and "
					+ "  A.ahinv# = B.ahinv# and A.ahiseq = B.ahiseq "
					+ " WHERE (A.ahcmp = '"
					+ company
					+ "' AND A.ahcus# in "
					+ customer_list
					+ ") and A.AHSTTS <> 'PD' and "
					+ " ( ( A.AHCBAL > 0  ) OR "
					+ "   ( A.AHCBAL < 0 AND B.AHCOLS >= 'B001' AND B.AHCOLS <= 'B498') ) "
					+ str_dateselect.trim();

		}

		stmt2 = conn.createStatement();
		result2 = stmt2.executeQuery(SQLstatement2);
		result2.next();
		return result2.getString("total_num_rows");
	}

	public synchronized String getTotalCurr(
		String customer_list,
		int customer_num,
		String company,
		String datalib)
		throws Exception {

		String SQLstatement2;

		// *******************************************************************************************
		// Canada (company=CR) will ALWAYS select balances not equal to zeros (including negatives)
		// *******************************************************************************************

		if (company.trim().equals("CR")) {

			SQLstatement2 =
				"select SUM(AHCBAL) as total_current_balance "
					+ " FROM "
					+ datalib
					+ ".arihdrf2 "
					+ " WHERE (ahcmp = '"
					+ company
					+ "' AND ahcus# in "
					+ customer_list
					+ ") and AHSTTS <> 'PD'  and AHCBAL  <> 0 ";

		} else {

			// ****************************************************************************************
			// Domestic will select invoices with a positive balance or invoices with a negative 
			// balance that are considered to be 'Valid Credit' credits.  A valid credit is 
			// determined by the AHCOLS field having the code range from B001 to B498.  - x005
			// ****************************************************************************************

			SQLstatement2 =
				"select SUM(AHCBAL) as total_current_balance "
					+ "FROM  "
					+ datalib
					+ ".arihdrf2  A  "
					+ "  LEFT OUTER JOIN "
					+ datalib
					+ ".arihd2fl B ON A.ahcmp=B.ahcmp AND A.ahcus# = B.ahcus# and "
					+ "  A.ahinv# = B.ahinv# and A.ahiseq = B.ahiseq "
					+ " WHERE (A.ahcmp = '"
					+ company
					+ "' AND A.ahcus# in "
					+ customer_list
					+ ") and A.AHSTTS <> 'PD' and "
					+ " ( ( A.AHCBAL > 0  ) OR "
					+ "   ( A.AHCBAL < 0 AND B.AHCOLS >= 'B001' AND B.AHCOLS <= 'B498') ) ";
		}

		try {

			stmt2 = conn.createStatement();

			result2 = stmt2.executeQuery(SQLstatement2);

			result2.next();

			String testresult = result2.getString("total_current_balance");

			if (testresult != null)
				return testresult;
			else
				return "0.00";

		} catch (SQLException sqle) {
			System.err.println(sqle.getMessage());
			return "0.00";

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return "0.00";
		}

	}

	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI007
		endcurrentStatement(stmt);		// RI007
		endcurrentResultset(result2);	// RI007
		endcurrentStatement(stmt2);		// RI007
	}

}