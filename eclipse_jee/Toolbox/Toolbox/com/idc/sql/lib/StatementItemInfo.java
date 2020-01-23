package com.idc.sql.lib;

import java.io.Serializable;

//import com.idc.dbtool.DBTool;

public class StatementItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String NL = "\n";
	private static final String TAB = "\t";
/*
 * Type;
 * 
 * 0 - unknown
 * 1 - select
 * 2 - update/insert/delete
 * 3 - describe table
 * 4 - create procedure
 * 5 - makesql table
 * 6 - makefile table
 * 7 - loadfile pathname
 * 8 - help or ?
 */
	private String m_sql;
	private int m_type;
	public StatementItemInfo (String sql) {
		int type = 0;		// unknown
		String str = sql.trim().toUpperCase();
		String[] subs = str.split(" ");
		if (str.startsWith("SELECT"))
			type = 1;
		else if (str.startsWith("INSERT"))
			type = 2;
		else if (str.startsWith("UPDATE"))
			type = 2;
		else if (str.startsWith("DELETE"))
			type = 2;
		else if (str.startsWith("DESCRIBE TABLE")) {
			type = 3;
			sql = subs[2];
		}
		else if (str.startsWith("DESCRIBE")) {
			type = 4;
//			sql = subs[1];
		}
		else if (str.startsWith("CREATE PROCEDURE"))
			type = 4;
		else if (str.startsWith("MAKESQL"))
			type = 5;
		else if (str.startsWith("MAKEFILE"))
			type = 6;
		else if (str.startsWith("LOADFILE"))
			type = 7;
		else if (str.startsWith("HELP") || str.startsWith("?"))
			type = 8;
		else
			type = 0;
		this.m_type = type;
		this.m_sql = sql;
	}

	public String getSql() {return m_sql;}
	public int getType() {return m_type;}

	public boolean isSelect() {return getType() == 1;}
	public boolean isDescribe() {return getType() == 3;}
	public boolean isCreateProcedure() {return getType() == 4;}
	public boolean isMakeSQL() {return getType() == 5;}
	public boolean isMakeFile() {return getType() == 6;}
	public boolean isLoadFile() {return getType() == 7;}
	public boolean isHelp() {return getType() == 8;}
	public String toString() {
		return "("+getSql()+","+getType()+")";
	}
	public static int getHelpCount() {return 8;}
	public static StringBuffer getHelpInfo (int type) {
		StringBuffer buf = new StringBuffer();
		switch (type) {
		case 1:
			buf.append ("select").append(NL);
			buf.append ("example:").append(NL);
			buf.append(TAB).append ("select * from hercdb.members where username = 'john' order by memberid;").append(NL);
			break;
		case 2:
			buf.append ("insert or update or delete").append(NL);
			buf.append ("example:").append(NL);
			buf.append(TAB).append ("insert into hercdb.members(memberid, username) values (16, 'john');").append(NL);
			buf.append(TAB).append ("update hercdb.members set username = 'john' where memberid = 16';").append(NL);
			buf.append(TAB).append ("delete from hercdb.members where username = 'john';").append(NL);
			break;
		case 3:
			buf.append ("describe table <schema.table_name>").append(NL);
			buf.append ("example:").append(NL);
			buf.append(TAB).append ("describe table hercdb.members;").append(NL);
			break;
		case 4:
			buf.append ("describe").append(NL);
			buf.append ("describe (no idea!).").append(NL);
			buf.append ("example:").append(NL);
			buf.append(TAB).append ("describe <tbd>;").append(NL).append(NL);
			
			buf.append ("create procedure").append(NL);
			buf.append ("runs a stored procedure.").append(NL);
			buf.append ("Will assume the stored procedure: starts with ").append(NL);
			buf.append(TAB).append ("starts with: create procedure").append(NL);
			buf.append(TAB).append ("ends with: END @").append(NL);
			buf.append ("Everything in between is stored procedure code.").append(NL);
			buf.append ("example:").append(NL);
			buf.append(TAB).append ("create procedure").append(NL);
			buf.append(TAB).append ("code....").append(NL);
			buf.append(TAB).append ("more code....").append(NL);
			buf.append(TAB).append ("END @").append(NL);
			break;
		case 5:
			buf.append ("makesql <schema.table_name>").append(NL);
			buf.append ("writes SQL to the Output tab.").append(NL);
			buf.append ("example:").append(NL);
			buf.append(TAB).append ("makesql hercdb.members;").append(NL);
			break;
//		case 6:
//			buf.append ("makefile").append(NL);
//			buf.append ("writes SQL to a file in "+DBTool.REPORTSDIR.getPath()).append(NL);
//			buf.append ("example:").append(NL);
//			buf.append(TAB).append ("makefile hercdb.members;").append(NL);
//			break;
//		case 7:
//			buf.append ("loadfile <pathname>").append(NL);
//			buf.append ("loads SQL insert statements from the file and reports to a file in "+DBTool.REPORTSDIR.getPath()).append(NL);
//			buf.append ("example:").append(NL);
//			buf.append(TAB).append ("loadfile C:/tmp/data/demodata2/inserts/adminprofiles.sql;").append(NL);
//			break;
		case 8:
			buf.append ("Help or ?").append(NL);
			break;
		default:
			;
		}
		return buf;
	}
}
