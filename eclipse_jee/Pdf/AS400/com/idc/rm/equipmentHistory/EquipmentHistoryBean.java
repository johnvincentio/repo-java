package com.idc.rm.equipmentHistory;

import java.sql.ResultSet;
import java.sql.Statement;

import com.idc.rm.SQLBean;

public class EquipmentHistoryBean extends SQLBean implements java.io.Serializable {
	ResultSet result = null;
	Statement stmt = null;

	public synchronized boolean getRows(String customer_list, int customer_num,
			String company, String datalib, int start_num, String jobnumber,
			String str_orderBy, String selectedYear) throws Exception {

		String SQLstatement = "";
		if (! str_orderBy.trim().equals("")) str_orderBy = " order by " + str_orderBy;

		if (jobnumber.equals(""))
			SQLstatement = "select RDCATG, RDCLAS, ECDESC, sum(RDRDYS) as totalrentdays, sum(RDRHRS) as totalrenthours,count(rdcon#) as totalTransactions, sum(RDAMT$) as totalrentamount "
					+ "from "
					+ datalib
					+ ".RACDETH3 left outer join "
					+ datalib
					+ ".EQPCCMF1 "
					+ "on ECCMP= RDCMP and ECCATG=RDCATG and ECCLAS=RDCLAS"
					+ " where ECCMP='"
					+ company
					+ "' and RDTYPE='RI' and RDCUS# in "
					+ customer_list
					+ " and RDSYSD >= " + selectedYear + "0101" // RI006
					+ " and RDSYSD < " + selectedYear + "1299" // RI006
					+ " group by RDCATG, RDCLAS, ECDESC " + str_orderBy;
		else
			SQLstatement = "select RDCATG, RDCLAS, ECDESC, sum(RDRDYS) as totalrentdays, sum(RDRHRS) as totalrenthours, count(rdcon#) as totalTransactions, sum(RDAMT$) as totalrentamount, "
					+ "from "
					+ datalib
					+ ".RACDETH3 left outer join "
					+ datalib
					+ ".EQPCCMF1 "
					+ "on ECCMP= RDCMP and ECCATG=RDCATG and ECCLAS=RDCLAS"
					+ " where ECCMP='"
					+ company
					+ "' and RDTYPE='RI' and RDCUS# in "
					+ customer_list
					+ " and RDSYSD >= "
					+ selectedYear
					+ "0101" // RI006
					+ " and RDSYSD < "
					+ selectedYear
					+ "1299" // RI006
					+ " and RDCON# in "
					+ "(select RHCON# from "
					+ datalib
					+ ".RACHDRFL "
					+ "where RHCMP='"
					+ company
					+ "' and RHJOB# ='"
					+ jobnumber
					+ "')"
					+ " group by RDCATG, RDCLAS, ECDESC " + str_orderBy;

		stmt = getConnection().createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); // RI004

		result = stmt.executeQuery(SQLstatement);

		return (result != null);
	}

	public boolean getNext() throws Exception {return result.next();}
	public String getColumn(String colNum) throws Exception {return result.getString(colNum);}

	public void cleanup() throws Exception {
		endcurrentResultset(result);
		endcurrentStatement(stmt);
	}
}
