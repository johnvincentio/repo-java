package com.idc.sql.app;

import org.apache.commons.lang.time.StopWatch;

import com.idc.sql.lib.DB;
import com.idc.sql.lib.DBQuery;
import com.idc.trace.LogHelper;

public class IdevDBTest {
	private DB m_db;

	public static void main(String[] args) {
		IdevDBTest test = new IdevDBTest();
		test.doWork();
	}

	public void doWork() {
		LogHelper.info("Connecting to database");
		m_db = new DB ("c:/jv/utils/dbtoolgui.xml");
		if (! m_db.getConnection ("herc")) {
			LogHelper.info("giving up...");
			System.exit(1);
		}
		doTest1();
		LogHelper.info("Disconnecting from database");
		m_db.disConnect();
		LogHelper.info("exiting...");
	}

	private void doTest1() {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = new DBQuery(m_db, "select account, branch from HERCDB.JVC1");
		dbQuery.executeQuery();
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		LogHelper.info (dbQuery.reportQuery(millis));
	}
}
