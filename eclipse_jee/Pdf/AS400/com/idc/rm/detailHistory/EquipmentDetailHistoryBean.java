package com.idc.rm.detailHistory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.idc.rm.SQLBean;

public class EquipmentDetailHistoryBean extends SQLBean {

	ResultSet result = null;
	Statement stmt = null;

	public synchronized boolean getRows (String customer_list, 
			String company, String datalib, String str_category,
			String str_class, String str_jobnumber, 
			String str_orderBy, String selectedYear) // RI005
			throws Exception {

		String SQLstatement = "";
		if (! str_orderBy.trim().equals("")) str_orderBy = " order by " + str_orderBy;
		if (str_jobnumber.equals(""))
			SQLstatement = "select RDITEM, RDCON#, RDISEQ, RDSEQ#, RDDYRT, RDWKRT, RDMORT, RDRATU ,RDAMT$, RDSYSD, RDDATO, RDDATI "
					+ "from "
					+ datalib
					+ ".RACDETF3 "
					+ " where RDCMP='"
					+ company
					+ "' and RDCATG="
					+ str_category
					+ " and RDCLAS ="
					+ str_class
					+ " and RDSYSD >= "
					+ selectedYear
					+ "0101" // RI005
					+ " and RDSYSD < "
					+ selectedYear
					+ "1299" // RI005
					+ " and RDTYPE='RI' and RDCUS# in "
					+ customer_list
					+ " "
					+ str_orderBy;
		else
			SQLstatement = "select RDITEM, RDCON#, RDISEQ, RDSEQ#, RDDYRT, RDWKRT, RDMORT, RDRATU ,RDAMT$, RDSYSD, RDDATO, RDDATI "
					+ "from "
					+ datalib
					+ ".RACDETF3 "
					+ " where RDCMP='"
					+ company
					+ "' and RDCATG="
					+ str_category
					+ " and RDCLAS ="
					+ str_class
					+ " and RDSYSD >= "
					+ selectedYear
					+ "0101" // RI005
					+ " and RDSYSD < "
					+ selectedYear
					+ "1299" // RI005
					+ " and RDTYPE='RI' and RDCUS# in "
					+ customer_list
					+ " and RDCON# in "
					+ "(select RHCON# from "
					+ datalib
					+ ".RACHDRFL "
					+ "where RHCMP='"
					+ company
					+ "' and RHJOB# ='"
					+ str_jobnumber
					+ "') "
					+ " "
					+ str_orderBy;

		stmt = getConnection().createStatement();
		result = stmt.executeQuery(SQLstatement);
		return (result != null);
	}

	public boolean getNext() throws Exception {return result.next();}
	public String getColumn(String colNum) throws Exception {return result.getString(colNum);}

	public synchronized String getItemComments (String company, String datalib,
					String contract, String seqno, String lineno) throws Exception {

		String itemcomments = "";
		ResultSet result4 = null;
		Statement stmt4 = null;

		try {
			String SQLstatement4 = "";
			SQLstatement4 = "select OCCMNT" + "  From " + datalib
					+ ".ORDCOMFL  " + "  where OCCMP='" + company + "'   "
					+ "  and OCREF#= " + contract + "  and OCISEQ=  " + seqno
					+ "  and OCTYPE= 'R' " + "  and OCASEQ= " + lineno;

			stmt4 = getConnection().createStatement();
			result4 = stmt4.executeQuery(SQLstatement4);
			if (result4 != null) {
				while (result4.next()) {
					itemcomments = itemcomments.trim() + " " + result4.getString("OCCMNT").trim();
				}
			}
		}
		catch (SQLException e) {
			System.err.println(e.getMessage());
			itemcomments = "";
		}
		endcurrentResultset(result4);
		endcurrentStatement(stmt4);
		return itemcomments;
	}

	public void cleanup() throws Exception {
		endcurrentResultset(result); // RI003
		endcurrentStatement(stmt); // RI003
	}
}
