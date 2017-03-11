package com.idc.rm.equipmentOnRent;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.idc.rm.SQLBean;

public class PendingTransactionsBean extends SQLBean implements Serializable {

	ResultSet result = null, result2 = null;
	Statement stmt = null, stmt2 = null;

	// ***************************************************
	// Check for transactions are pending by customer
	// ***************************************************

	public synchronized boolean getPendingTrans (String customer_num, int countryCode) throws Exception {
		try {
			String SQLstatement = "select * " + " From " + getDataLibrary (countryCode)
					+ ".ETWHDRFL2 left outer join " + getDataLibrary (countryCode) + ".ETWDETFL on "
					+ " THCMP = TDCMP " + " AND THTRN# = TDTRN# "
					+ " where THCMP='" + getCompanyCode (countryCode) + "' " + " and THCUS# = "
					+ customer_num;
			stmt = getConnection().createStatement();
			result = stmt.executeQuery(SQLstatement);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return false;
		}
		return (result != null);
	}

	public boolean getNext() throws Exception {return result.next();}
	public String getColumn(String colNum) throws Exception {return result.getString(colNum);}

	// ***************************************************
	// Check for transactions are pending by contract
	// ***************************************************

	public synchronized boolean getPendingConTrans (String contract, int countryCode) throws Exception {
		try {
			String SQLstatement2 = "select * " + " From " + getDataLibrary (countryCode)
					+ ".ETWHDRFL3 left outer join " + getDataLibrary (countryCode) + ".ETWDETFL on "
					+ " THCMP = TDCMP " + " AND THTRN# = TDTRN# "
					+ " where THCMP='" + getCompanyCode (countryCode) + "' " + " and THCON# = "
					+ contract;
			stmt2 = getConnection().createStatement();
			result2 = stmt2.executeQuery(SQLstatement2);
		}
		catch (SQLException e) {
			System.err.println(e.getMessage());
			return false;
		}
		return (result2 != null);
	}

	public boolean getNext2() throws Exception {return result2.next();}
	public String getColumn2(String colNum) throws Exception {return result2.getString(colNum);}

	public void cleanup() throws Exception {
		endcurrentResultset(result);
		endcurrentResultset(result2);
		endcurrentStatement(stmt);
		endcurrentStatement(stmt2);
	}
}
