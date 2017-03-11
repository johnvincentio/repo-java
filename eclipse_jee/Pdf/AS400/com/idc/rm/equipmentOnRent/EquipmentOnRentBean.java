package com.idc.rm.equipmentOnRent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.idc.rm.SQLBean;

public class EquipmentOnRentBean extends SQLBean {

	ResultSet result = null, result2 = null, result3 = null;
	Statement stmt = null, stmt2 = null, stmt3 = null;

	public synchronized boolean getRows (String customer_list, int countryCode, String jobnumber, String str_orderBy) throws Exception {

		String SQLstatement = "";
		if (! str_orderBy.trim().equals("")) str_orderBy = " order by " + str_orderBy;
		if (jobnumber.equals("")) {
			if (countryCode != 1) {
				SQLstatement = "select distinct RDCON#, RDSEQ#, RDITEM, RDCATG ,RDCLAS, (RDQTY-RDQTYR) as RDQTY, RDLOC, RDDYRT, RDWKRT, RDMORT, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHERDT, CJNAME, 0 as RNBILL, 0 as RNACCR, 0 as RNRCST  " // RI014
						+ "from "
						+ getDataLibrary (countryCode)
						+ ".RAODETFL, "
						+ getDataLibrary (countryCode)
						+ ".EQPCCMFL, "
						+ getDataLibrary (countryCode)
						+ ".RAOHDRFL "
						+ "  Left outer join " // RI012
						+ getDataLibrary (countryCode) // RI012
						+ ".CUSJOBFL On RHCMP=CJCMP and RHCUS#=CJCUS#  and RHJOB#=CJJOB# " // RI012
						+ "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and " // RI010
						+ "(RHCMP=RDCMP and RHCON#=RDCON#) and "
						+ "RDCMP='"
						+ getCompanyCode (countryCode)
						+ "' and RDTYPE='RI' and RDCUS# in "
						+ customer_list + " " + str_orderBy;

			} else {
				SQLstatement = "select distinct T.RDCMP, T.RDCON#, T.RDSEQ#, T.RDCUS#, T.RDITEM, T.RDCATG, T.RDCLAS, T.RDQTY, T.RDLOC, T.RDDYRT, T.RDWKRT, T.RDMORT, T.ECDESC, RHJOBL, "
						+ " RHDATO, RHPO#, RHORDB, RHERDT, RHJOB#, CJNAME, T.RNBILL, T.RNACCR, T.RNRCST "
						+ "from "
						+ "( select RDCMP, RDCON#, RDSEQ#, RDCUS#, RDITEM, RDCATG, RDCLAS,(RDQTY-RDQTYR) as RDQTY, RDLOC, RDDYRT, RDWKRT, RDMORT, ECDESC, RNBILL, (RNBILL + RNACCR) as RNACCR, RNRCST "
						+ " from "
						+ getDataLibrary (countryCode)
						+ ".EQPCCMFL,"
						+ getDataLibrary (countryCode)
						+ ".RAODETFL"
						+ "  Left outer join "
						+ getDataLibrary (countryCode)
						+ ".RAOAMTFL On RDCMP = RNCMP and RDCON# = RNCON# and RDSEQ# = RNSEQ# "
						+ " WHERE ECCMP = RDCMP "
						+ " and ECCATG=RDCATG AND ECCLAS=RDCLAS "
						+ " and ECCMP='"
						+ getCompanyCode (countryCode)
						+ "' and RDCUS# in "
						+ customer_list
						+ " and RDTYPE='RI' )as T , "
						+ getDataLibrary (countryCode)
						+ ".RAOHDRFL, "
						+ getDataLibrary (countryCode)
						+ ".CUSJOBFL "
						+ "where "
						+ "t.RDCMP= RHCMP "
						+ " and T.RDCON# = RHCON# "
						+ " and RHCMP = CJCMP "
						+ " and RHCUS# = CJCUS# "
						+ " and RHJOB# = CJJOB# "
						+ " and RHCMP='"
						+ getCompanyCode (countryCode)
						+ "' and RHCUS# in "
						+ customer_list
						+ " and RHTYPE='O' " + " " + str_orderBy;
			}
		}
		else {
			if (countryCode == 1) {
				SQLstatement = "select distinct RDCON#, RDSEQ#, RDITEM, RDCATG ,RDCLAS, (RDQTY-RDQTYR) as RDQTY, RDLOC, RDDYRT, RDWKRT, RDMORT, RHJOBL, RHDATO, RHPO#, ECDESC, RHORDB, RDSEQ#, RHERDT, CJNAME, 0 as RNBILL, 0 as RNACCR, 0 as RNRCST  " // RI014
						+ "from "
						+ getDataLibrary (countryCode)
						+ ".RAODETFL, "
						+ getDataLibrary (countryCode)
						+ ".EQPCCMFL, "
						+ getDataLibrary (countryCode)
						+ ".RAOHDRFL "
						+ "  Left outer join " // RI012
						+ getDataLibrary (countryCode) // RI012
						+ ".CUSJOBFL On RHCMP=CJCMP and RHCUS#=CJCUS#  and RHJOB#=CJJOB# " // RI012
						+ "where (RDCMP=ECCMP and RDCATG=ECCATG and RDCLAS=ECCLAS) and " // RI010
						+ "(RHCMP=RDCMP and RHCON#=RDCON#) and "
						+ "RDCMP='"
						+ getCompanyCode (countryCode)
						+ "' and RDTYPE='RI' and RDCUS# in "
						+ customer_list
						+ " and RDCON# in "
						+ "(select RHCON# from "
						+ getDataLibrary (countryCode)
						+ ".RAOHDRFL "
						+ "where RHCMP='"
						+ getCompanyCode (countryCode)
						+ "' and RHJOB# ='"
						+ jobnumber + "') " + " " + str_orderBy;
			}
			else {
				SQLstatement = "select distinct T.RDCMP, T.RDCON#, T.RDSEQ#, T.RDCUS#, T.RDITEM, T.RDCATG, T.RDCLAS, T.RDQTY, T.RDLOC, T.RDDYRT, T.RDWKRT, T.RDMORT, T.ECDESC, RHJOBL, "
						+ " RHDATO, RHPO#, RHORDB, RHERDT, RHJOB#, CJNAME, T.RNBILL, T.RNACCR, T.RNRCST "
						+ "from "
						+ "( select RDCMP, RDCON#, RDSEQ#, RDCUS#, RDITEM, RDCATG, RDCLAS,(RDQTY-RDQTYR) as RDQTY, RDLOC, RDDYRT, RDWKRT, RDMORT, ECDESC, RNBILL, (RNBILL + RNACCR) as RNACCR, RNRCST "
						+ " from "
						+ getDataLibrary (countryCode)
						+ ".EQPCCMFL,"
						+ getDataLibrary (countryCode)
						+ ".RAODETFL"
						+ "  Left outer join "
						+ getDataLibrary (countryCode)
						+ ".RAOAMTFL On RDCMP = RNCMP and RDCON# = RNCON# and RDSEQ# = RNSEQ# "
						+ " WHERE ECCMP = RDCMP "
						+ " and ECCATG=RDCATG AND ECCLAS=RDCLAS "
						+ " and ECCMP='"
						+ getCompanyCode (countryCode)
						+ "' and RDCUS# in "
						+ customer_list
						+ " and RDTYPE='RI' )as T , "
						+ getDataLibrary (countryCode)
						+ ".RAOHDRFL, "
						+ getDataLibrary (countryCode)
						+ ".CUSJOBFL "
						+ "where "
						+ "t.RDCMP= RHCMP "
						+ " and T.RDCON# = RHCON# "
						+ " and RHCMP = CJCMP "
						+ " and RHCUS# = CJCUS# "
						+ " and RHJOB# = CJJOB# "
						+ " and RHCMP='"
						+ getCompanyCode (countryCode)
						+ "' and RHCUS# in "
						+ customer_list
						+ " and RHTYPE='O' "
						+ " and RDCON# in "
						+ "(select RHCON# from "
						+ getDataLibrary (countryCode)
						+ ".RAOHDRFL "
						+ "where RHCMP='"
						+ getCompanyCode (countryCode)
						+ "' and RHJOB# ='"
						+ jobnumber + "') " + " " + str_orderBy;
			}
		}
		stmt = getConnection().createStatement();
		result = stmt.executeQuery(SQLstatement);
		return (result != null);
	}

	public boolean getNext() throws Exception {return result.next();}
	public String getColumn(String colNum) throws Exception {return result.getString(colNum);}

	public synchronized boolean getPickup (String customer_list,
					String customer_num, int countryCode) throws Exception {
		String SQLstatement3 = "select RUPKU#, RUCON#, RUSEQ#, RUEQP#  " + "from "
				+ getDataLibrary (countryCode) + ".RAPKUPF4 " + "where RUCMP='" + getCompanyCode (countryCode)
				+ "' and RUCUS# in " + customer_list
				+ " and RUCON# > 0 and RUSTTS = 'OP' ";
		stmt3 = getConnection().createStatement();
		result3 = stmt3.executeQuery(SQLstatement3);
		return (result3 != null);
	}

	public boolean getNext3() throws Exception {return result3.next();}
	public String getColumn3(String colNum) throws Exception {return result3.getString(colNum);}

	public synchronized String getItemComments (int countryCode, String contract, String desq) throws Exception {

		String itemcomments = "";
		ResultSet result4 = null;
		Statement stmt4 = null;

		try {
			String SQLstatement4 = "select OCCMNT" + "  From " + getDataLibrary (countryCode)
					+ ".ORDCOMFL  " + "  where OCCMP='" + getCompanyCode (countryCode) + "'   "
					+ "  and OCREF#= " + contract + "  and OCISEQ=  0 "
					+ "  and OCTYPE= 'R' " + "  and OCASEQ= " + desq;
			stmt4 = getConnection().createStatement();
			result4 = stmt4.executeQuery(SQLstatement4);
			if (result4 != null) {
				while (result4.next()) {
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
		endcurrentResultset(result); // RI009
		endcurrentResultset(result2); // RI009
		endcurrentResultset(result3); // RI009
		endcurrentStatement(stmt); // RI009
		endcurrentStatement(stmt2); // RI009
		endcurrentStatement(stmt3); // RI009
	}
}
