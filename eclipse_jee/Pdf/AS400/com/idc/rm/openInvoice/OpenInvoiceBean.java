package com.idc.rm.openInvoice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.idc.rm.SQLBean;

public class OpenInvoiceBean extends SQLBean implements java.io.Serializable {

	ResultSet result = null, result2 = null;
	Statement stmt = null, stmt2 = null;

	public synchronized boolean getRows (
			String customer_list, int customer_num, String company, String datalib,
			String str_dateselect, String str_orderBy) throws Exception {

		if (! str_orderBy.trim().equals(""))
			str_orderBy = " order by " + str_orderBy;
			
		// *******************************************************************************************
		// Canada (company=CR) will ALWAYS select balances not equal to zeros (including negatives)
		// *******************************************************************************************

		String SQLstatement;
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
		stmt = getConnection().createStatement();
		result = stmt.executeQuery(SQLstatement);
		return (result != null);
	}

	public boolean getNext() throws Exception {
		return result.next();
	}

	public String getColumn(String colNum) throws Exception {
		return result.getString(colNum);
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

			stmt2 = getConnection().createStatement();

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
