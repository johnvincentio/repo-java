package com.idc.sql.lib;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.file.JVFile;

public class DBLoadFile {
	OutputFile m_outputFile;
	private DB m_db;
	private String m_strFile;
	private String m_sqlErrorMessage;

	public DBLoadFile (OutputFile outputFile, DB db, String strFile) {
		m_outputFile = outputFile;
		m_db = db;
		m_strFile = strFile;
	}

	public void execute() {
		System.out.println(">>> DBLoadFile::execute");
		StringBuffer buf = JVFile.readFile (m_strFile);
		StatementInfo statementInfo = makeStatements (buf.toString());
//		System.out.println("statementInfo "+statementInfo);
		Iterator<StatementItemInfo> iter = statementInfo.getItems();
		while (iter.hasNext()) {
			StatementItemInfo statementItemInfo = iter.next();
			if (statementItemInfo.getType() != 2) continue;
			executeInserts (statementItemInfo);
		}
		System.out.println("<<< DBLoadFile::execute");
		return;
	}
	public void executeInserts (StatementItemInfo statementItemInfo) {
		m_sqlErrorMessage = "";
		PreparedStatement ps = null;
		try {
			m_outputFile.writeNL("Processing: "+statementItemInfo.getSql());
			ps = m_db.getDBConnection().prepareStatement (statementItemInfo.getSql());
			ps.execute();
		}
		catch (SQLException sqlException) {
			System.out.println("Error on insert; Exception "+sqlException.getMessage()+" sql :"+statementItemInfo.getSql()+":");
			m_outputFile.writeNL("Error on insert; Exception "+sqlException.getMessage()+" sql :"+statementItemInfo.getSql()+":");
			m_sqlErrorMessage = sqlException.getMessage();
		}
		finally {
			if (ps != null) {
				try {
					ps.close();
				}
				catch (SQLException e) {}
			}
		}
		return;
	}
	private StatementInfo makeStatements (String input) {
		StatementInfo statementInfo = new StatementInfo();
		String[] lines = input.split("\\r?\\n");
		ArrayList<String> alist = new ArrayList<String>();
		String current = "";
		for (int i = 0; i < lines.length; i++) {
//			System.out.println("line number "+i+" string "+lines[i]);
			String line = lines[i];
			if (line == null) continue;
			if (line.trim().length() < 1) continue;
			if (line.trim().startsWith("--")) continue;
			current += line;
			if (line.trim().endsWith(";")) {
				alist.add (current);
				current = "";
			}
		}
		if (current.trim().length() > 0) alist.add (current);

		for (int i = 0; i < alist.size(); i++) {
//			System.out.println("Line number "+i+" SQL :"+alist.get(i)+":");
			statementInfo.add (new StatementItemInfo (alist.get(i)));
		}
		return statementInfo;
	}

	public String report (long millis) {
		StringBuffer buf = new StringBuffer();
		if (m_sqlErrorMessage != null && m_sqlErrorMessage.length() > 0) {
			return m_sqlErrorMessage;
		}
		buf.append ("LoadFile took ").append(millis).append(" millisecs");
		return buf.toString();
	}
}
/*
	private StatementInfo makeStatements_2 (String input) {
		StatementInfo statementInfo = new StatementInfo();
		String[] statements = input.split(";");
		int count1 = statements.length;
		for (int num1=0; num1<count1; num1++) {
			String statement = statements[num1].trim();
			if (statement.length() < 1) continue;

			String sql = "";
			String[] lines = statement.split("\n");
			int count2 = lines.length;
//			LogHelper.info("count2 "+count2);
			for (int num2=0; num2<count2; num2++) {
				String line = lines[num2];
//				LogHelper.info("line "+line);
				if (line.length() < 1) continue;
				if (line.startsWith("--")) continue;
				sql += line;
			}
//			LogHelper.info("sql :"+sql+":");
			String str = sql.trim().toUpperCase();
			if (! str.startsWith("INSERT")) continue;
			statementInfo.add (new StatementItemInfo (sql));
		}
		return statementInfo;
	}
*/
