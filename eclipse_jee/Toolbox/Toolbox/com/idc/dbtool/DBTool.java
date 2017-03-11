package com.idc.dbtool;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.time.StopWatch;

import com.idc.sql.lib.DB;
import com.idc.sql.lib.DBDescribe;
import com.idc.sql.lib.DBLoadFile;
import com.idc.sql.lib.DBMakeSQL;
import com.idc.sql.lib.DBQuery;
import com.idc.sql.lib.DBUpdate;
import com.idc.sql.lib.OutputFile;
import com.idc.sql.lib.OutputTTY;
import com.idc.sql.lib.StatementInfo;
import com.idc.sql.lib.StatementItemInfo;
import com.idc.trace.LogHelper;

public class DBTool {
	public static final File REPORTSDIR = new File ("c:/jvWork/dbtool");

	private DBToolGui m_app;
	public DBTool (DBToolGui app) {m_app = app;}
	private StatementInfo m_statementInfo = new StatementInfo();
	public boolean isExecutionStopped() {return m_app.getAppThread().getStopStatus();}

	private void handleProgressIndicator() {m_app.getAppInput().getProgressBar().setProgressBar();}
	private void addMessage () {m_app.getAppOutput().getMessagesArea().add();}
	private void addMessage (String msg) {m_app.getAppOutput().getMessagesArea().add(msg);}

	public void doDBTool (String configurationFile, String configuration, String sql) {
		if (! REPORTSDIR.exists()) REPORTSDIR.mkdir();
		handleProgressIndicator();

		if (isExecutionStopped()) return;
		handleProgressIndicator();
		makeStatements(sql);
		
		if (isExecutionStopped()) return;
		handleProgressIndicator();
		DB db = new DB(configurationFile);
		if (! connect (db, configuration)) return;

		if (isExecutionStopped()) return;
		handleProgressIndicator();
		Iterator<StatementItemInfo> iter = m_statementInfo.getItems();
		while (iter.hasNext()) {
			if (isExecutionStopped()) return;
			handleProgressIndicator();
			StatementItemInfo statementItemInfo = (StatementItemInfo) iter.next();
			addMessage();
			addMessage(statementItemInfo.getSql());
			addMessage();
			System.out.println("statementItemInfo "+statementItemInfo);
			if (statementItemInfo.isSelect())
				executeSelect (db, statementItemInfo.getSql());
			else if (statementItemInfo.isDescribe())
				executeDescribe (db, statementItemInfo.getSql());
			else if (statementItemInfo.isCreateProcedure())
				executeUpdate (db, statementItemInfo.getSql());
			else if (statementItemInfo.isMakeSQL())
				executeMakeSQL (db, statementItemInfo.getSql());
			else if (statementItemInfo.isMakeFile())
				executeMakeFile (db, statementItemInfo.getSql());
			else if (statementItemInfo.isLoadFile())
				executeLoadFile (db, statementItemInfo.getSql());
			else if (statementItemInfo.isHelp())
				executeHelp();
			else
				executeUpdate (db, statementItemInfo.getSql());
		}

		handleProgressIndicator();
		disconnect (db);
		handleProgressIndicator();
		LogHelper.info("exiting...");
	}
	private boolean connect (DB db, String configuration) {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		addMessage("Connecting to database using "+configuration);
		boolean bConnected = db.getConnection (configuration);
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		if (bConnected) {
			addMessage ("Connected in "+millis+" millisecs");
			return true;
		}
		addMessage("Could not connect to the database. Giving up...");
		return false;
	}
	private void disconnect (DB db) {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		addMessage();
		addMessage("Disconnecting from database");
		addMessage();
		db.disConnect();
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		addMessage ("Disconnected in "+millis+" millisecs");
	}
	private void executeSelect (DB db, String sql) {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = new DBQuery(db, sql);
		dbQuery.executeQuery();
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		addMessage (dbQuery.reportQuery(millis));
	}
	private void executeUpdate (DB db, String sql) {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBUpdate dbUpdate = new DBUpdate(db, sql);
		dbUpdate.executeUpdate();
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		addMessage (dbUpdate.reportUpdate(millis));
	}
	private void executeDescribe (DB db, String sql) {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBDescribe dbDescribe = new DBDescribe(db, sql);
		dbDescribe.execute();
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		addMessage (dbDescribe.report(millis));
	}

	private void executeMakeSQL (DB db, String sql) {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		OutputTTY outputTTY = new OutputTTY (m_app.getAppOutput().getMessagesArea());
		DBMakeSQL dbMakeSQL = new DBMakeSQL (outputTTY, db, sql);
		dbMakeSQL.executeMakeSQL();
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		addMessage (dbMakeSQL.reportQuery(millis));
	}

	private void executeMakeFile (DB db, String sql) {
		String tmpFile = Long.toString (System.currentTimeMillis());
		File file = new File (REPORTSDIR + "/" + tmpFile + ".sql");
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		OutputFile outputFile = new OutputFile (file);
		outputFile.open();
		DBMakeSQL dbMakeSQL = new DBMakeSQL (outputFile, db, sql);
		dbMakeSQL.executeMakeSQL();
		outputFile.close();
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		addMessage ("Results were written to: "+file.getPath());
		addMessage ();
		addMessage (dbMakeSQL.reportQuery(millis));
	}

	private void executeLoadFile (DB db, String sql) {
		String strFile = sql.substring(8, sql.length()).trim();
		if (strFile.endsWith(";")) strFile = strFile.substring(0, strFile.lastIndexOf(";"));
		addMessage ("Loading file :"+strFile+":");
		addMessage ();
		String tmpFile = Long.toString (System.currentTimeMillis());
		File file = new File (REPORTSDIR + "/" + tmpFile + ".sql");
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		OutputFile outputFile = new OutputFile (file);
		outputFile.open();
		DBLoadFile dbLoadFile = new DBLoadFile (outputFile, db, strFile);
		dbLoadFile.execute();
		outputFile.close();
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		addMessage ("Results were written to: "+file.getPath());
		addMessage ();
		addMessage (dbLoadFile.report (millis));
	}

	private void makeStatements (String input) {
		int pos1 = input.toUpperCase().indexOf("CREATE PROCEDURE");
		if (pos1 > -1) {
			System.out.println("Found procedure");
			int pend = input.toUpperCase().indexOf("END @", pos1) + 4;
			m_statementInfo.add (new StatementItemInfo (input.substring(pos1, pend)));
//			LogHelper.info(m_statementInfo.toString());
			return;
		}

		String[] lines = input.split("\\r?\\n");
		ArrayList<String> alist = new ArrayList<String>();
		String current = "";
		for (int i = 0; i < lines.length; i++) {
			System.out.println("line number "+i+" string "+lines[i]);
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
			System.out.println("Line number "+i+" SQL :"+alist.get(i)+":");
			m_statementInfo.add (new StatementItemInfo (alist.get(i)));
		}
		LogHelper.info (m_statementInfo.toString());
	}
	private void executeHelp() {
		addMessage ("Help:");
		addMessage ();
		addMessage ("Commands are:");
		for (int i = 0; i < StatementItemInfo.getHelpCount(); i++) {
			addMessage (StatementItemInfo.getHelpInfo(i).toString());
		}
	}
}

/*
	private void makeStatements (String input) {
		int pos1 = input.toUpperCase().indexOf("CREATE PROCEDURE");
		if (pos1 > -1) {
			System.out.println("Found procedure");
			int pend = input.toUpperCase().indexOf("END @", pos1) + 4;
			m_statementInfo.add (new StatementItemInfo(input.substring(pos1, pend), 4));
//			LogHelper.info(m_statementInfo.toString());
			return;
		}

		String[] jv = input.split("\\r?\\n");
		for (int gg = 0; gg < jv.length; gg++) {
			System.out.println("gg "+gg+" string "+jv[gg]);
		}

		System.out.println("input "+input);
		String[] statements = input.split(";");
		int count1 = statements.length;
		for (int num1=0; num1<count1; num1++) {
			String statement = statements[num1].trim();
			if (statement.length() < 1) continue;

			String sql = "";
			String[] lines = statement.split("\n");
			int count2 = lines.length;
			LogHelper.info("count2 "+count2);
			for (int num2=0; num2<count2; num2++) {
				String line = lines[num2];
				LogHelper.info("line "+line);
				if (line.length() < 1) continue;
				if (line.startsWith("--")) continue;
				sql += line;
			}
			LogHelper.info("sql :"+sql+":");
			int type = 1;		// select
			String str = sql.trim().toUpperCase();
			if (! str.startsWith("SELECT")) {
				if (str.startsWith("DESCRIBE")) {
					String[] subs = str.split(" ");
					int scount = subs.length;
					if (scount < 2) continue;
					if ("TABLE".equals(subs[1]))
						type = 3;
					else
						type = 4;
					sql = subs[2];
				}
				else if (str.startsWith("MAKESQL"))
					type = 5;
				else if (str.startsWith("MAKEFILE"))
					type = 6;
				else if (str.startsWith("LOADFILE"))
					type = 7;
				else if (str.startsWith("HELP") || str.startsWith("?"))
					type = 8;
				else
					type = 2;
			}
			m_statementInfo.add (new StatementItemInfo(sql, type));
		}
		LogHelper.info(m_statementInfo.toString());
	}
*/
