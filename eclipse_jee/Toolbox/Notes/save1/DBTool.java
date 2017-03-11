package com.idc.dbtool;

import java.util.Iterator;

import org.apache.commons.lang.time.StopWatch;

import com.idc.sql.lib.DB;
import com.idc.sql.lib.DBQuery;
import com.idc.sql.lib.DBUpdate;
import com.idc.sql.lib.StatementInfo;
import com.idc.sql.lib.StatementItemInfo;

public class DBTool {
	private DBToolGui m_app;
	public DBTool (DBToolGui app) {m_app = app;}
	private StatementInfo m_statementInfo = new StatementInfo();
	public boolean isExecutionStopped() {return m_app.getAppThread().getStopStatus();}
	private void handleProgressIndicator() {m_app.handleProgressIndicator();}
	private void addMessage () {m_app.appendMessagesArea("");}
	private void addMessage (String msg) {m_app.appendMessagesArea(msg);}

	public void doDBTool (String configuration, String sql) {
//		sql = "select account, branch from HERCDB.JVC1";
//		sql = "select account, branch from hercdb.RMContractLocationsUS";
//		sql = "select account, category, classification from hercdb.RMContractRentalEquipmentUS";
		handleProgressIndicator();
		addMessage();
		addMessage(sql);
		addMessage();

		if (isExecutionStopped()) return;
		handleProgressIndicator();
		makeStatements(sql);

		if (isExecutionStopped()) return;
		handleProgressIndicator();
		addMessage("Connecting to database using "+configuration);
		addMessage();
		DB m_db = new DB();
		if (! m_db.getConnection (configuration)) {
			System.out.println("giving up...");
			System.exit(1);
		}

		if (isExecutionStopped()) return;
		handleProgressIndicator();
		Iterator iter = m_statementInfo.getItems();
		while (iter.hasNext()) {
			if (isExecutionStopped()) return;
			handleProgressIndicator();
			StatementItemInfo statementItemInfo = (StatementItemInfo) iter.next();
			addMessage();
			addMessage(statementItemInfo.getSql());
			addMessage();
			if (statementItemInfo.isSelect())
				executeSelect (m_db, statementItemInfo.getSql());
			else
				executeUpdate (m_db, statementItemInfo.getSql());
		}

		handleProgressIndicator();
		addMessage();
		addMessage("Disconnecting from database");
		addMessage();
		m_db.disConnect();
		handleProgressIndicator();
		System.out.println("exiting...");
	}
	private void executeSelect (DB db, String sql) {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = new DBQuery(db, sql);
		dbQuery.executeQuery();
		addMessage (dbQuery.reportQuery());
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		addMessage();
		addMessage("took "+millis+" milli-seconds");
		addMessage();
	}
	private void executeUpdate (DB db, String sql) {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBUpdate dbUpdate = new DBUpdate(db, sql);
		dbUpdate.executeUpdate();
		addMessage (dbUpdate.reportUpdate());
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		addMessage();
		addMessage("took "+millis+" milli-seconds");
		addMessage();
	}
	private void makeStatements (String imput) {
		String[] statements = imput.split(";");
		int count1 = statements.length;
		for (int num1=0; num1<count1; num1++) {
			String statement = statements[num1].trim();
			if (statement.length() < 1) continue;

			String sql = "";
			String[] lines = statement.split("\n");
			int count2 = lines.length;
//			System.out.println("count2 "+count2);
			for (int num2=0; num2<count2; num2++) {
				String line = lines[num2];
//				System.out.println("line "+line);
				if (line.length() < 1) continue;
				if (line.startsWith("--")) continue;
				sql += line;
			}
//			System.out.println("sql :"+sql+":");
			int type = 1;		// select
			String str = sql.trim().toUpperCase();
			if (! str.startsWith("SELECT")) type = 2;
			m_statementInfo.add (new StatementItemInfo(sql, type));
		}
//		System.out.println(m_statementInfo);
	}
}
