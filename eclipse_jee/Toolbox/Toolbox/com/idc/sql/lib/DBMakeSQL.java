
package com.idc.sql.lib;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.idc.trace.LogHelper;

public class DBMakeSQL {
	private Output m_output;
	private DB m_db;
	private String m_statement;
	ArrayList<TableInfo> allTableInfo = new ArrayList<TableInfo>();
	private String m_sqlErrorMessage;

	public DBMakeSQL (Output output, DB db, String statement) {
		m_output = output;
		m_db = db;
		m_statement = statement;
		System.out.println("m_statement :"+m_statement+":");
	}
//	makesql hercdb.MakesModels;
	public void executeMakeSQL() {
		String str = m_statement.substring(8, m_statement.length()).trim();
		System.out.println("str :"+str+":");
		if (str.endsWith(";")) str = str.substring(0, str.length() - 1);
		Pattern pattern = Pattern.compile("\\.");
		String[] splitStrings = pattern.split (str);
		System.out.println("splitStrings "+splitStrings.length);
		String schema = splitStrings[0].trim();
		String table = splitStrings[1].trim();
		System.out.println("schema :"+schema+":");
		System.out.println("table :"+table+":");

		IncludeTableInfo includeTableInfo = new IncludeTableInfo();
		includeTableInfo.add (new IncludeTableItemInfo (table));

		LogHelper.info("number of tables "+includeTableInfo.getSize());

		allTableInfo = m_db.makeTableInfoWithIncludeList (includeTableInfo);
		LogHelper.info("number of tables "+allTableInfo.size());

		boolean bDropTables = true;
		boolean bCreateTables = true;
		boolean bCreateIndexes = true;
		boolean bCreateGrants = true;
		boolean bInserts = true;

		if (bDropTables) {
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.dropTable (m_output, schema, tableInfo);
			}
		}

		if (bCreateTables) {
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeCreateTable (m_output, schema, tableInfo);
			}
		}

		if (bCreateIndexes) {
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeIndexes (m_output, schema, tableInfo);
			}
		}

		if (bCreateGrants) {
			for (int i = 0; bCreateGrants && i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeGrants (m_output, schema, tableInfo.getName());
			}
		}

		for (int i = 0; bInserts && i < allTableInfo.size(); i++) {
			TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
			MakeSQL.makeCreateInserts (m_db, m_output, schema, tableInfo);
		}
	}

	public String reportQuery (long millis) {
		StringBuffer buf = new StringBuffer();
		if (m_sqlErrorMessage != null && m_sqlErrorMessage.length() > 0) {
			return m_sqlErrorMessage;
		}
		buf.append ("makesql took ").append(millis).append(" millisecs");
		return buf.toString();
	}
}
