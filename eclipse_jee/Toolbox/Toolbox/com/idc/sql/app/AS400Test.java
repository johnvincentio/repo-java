package com.idc.sql.app;

import org.apache.commons.lang.time.StopWatch;

import com.idc.sql.lib.DB;
import com.idc.sql.lib.DBQuery;
import com.idc.trace.LogHelper;

public class AS400Test {
	private DB m_db;

	public static void main(String[] args) {
		AS400Test test = new AS400Test();
		test.doWork();
	}

	public void doWork() {
		LogHelper.info("Connecting to database");
		m_db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! m_db.getConnection ("as400US")) {
			LogHelper.info("giving up...");
			System.exit(1);
		}
		doTest2();

		LogHelper.info("Disconnecting from database");
		m_db.disConnect();
		LogHelper.info("exiting...");
	}
	@SuppressWarnings ("unused")
	private void doTest1() {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = new DBQuery(m_db, "select CMCRP# from WSDATA.CUSMASFL where CMCMP = 'CR' and CMCUS# = 2391505");
		dbQuery.executeQuery();
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		LogHelper.info (dbQuery.reportQuery(millis));
	}
	private void doTest2() {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = new DBQuery(m_db, 
				"select distinct d.E5LOC from RMHCQDATA.EQPCLRFL d, RMHCQDATA.EQPCPRFL a, RMHCQDATA.EQPCPHFL b, RMHCQDATA.EQPHPRFL c, RMHCQDATA.EQPDSCFL e "+ 
"where a.EQCNT# = b.EYCNT# and a.EQCNT# = c.E6CNT# and a.EQCNT# = d.E5CNT#"+ 
" and a.EQCNT# = e.EDCNT# and b.EYSTDT <= '20101201' and b.EYENDT >= '20060101' and a.EQCUS# = '2801270'");
		dbQuery.executeQuery();
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		LogHelper.info (dbQuery.reportQuery(millis));
	}
}
