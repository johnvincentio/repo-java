/********************************************************************
*			Copyright (c) 2006 The Hertz Corporation				*
*			  All Rights Reserved.  (Unpublished.)					*
*																	*
*		The information contained herein is confidential and		*
*		proprietary to The Hertz Corporation and may not be			*
*		duplicated, disclosed to third parties, or used for any		*
*		purpose not expressly authorized by it.  Any unauthorized	*
*		use, duplication, or disclosure is prohibited by law.		*
*																	*
*********************************************************************/

package com.idc.sql.app;

import org.apache.commons.lang.time.StopWatch;

import com.idc.sql.lib.DB;
import com.idc.sql.lib.DBQuery;

/**
 * Test AS400CA Box
 */
public class AS400CATest {

	private DB m_db;
	public AS400CATest() {}

	public void doWork() {
		System.out.println("Connecting to database");
		m_db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! m_db.getConnection ("as400CA")) {
			System.out.println("giving up...");
			System.exit(1);
		}
		doTest4();

		System.out.println("Disconnecting from database");
		m_db.disConnect();
		System.out.println("exiting...");
	}
	private void doTest4() {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = getNonNarpAccountInfo("2821787");
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		System.out.println (dbQuery.reportQuery(millis));
	}
	private DBQuery getNonNarpAccountInfo(String account) {
		String sql = "select cmname, cmadr1, cmadr2, cmcity, cmstat, cmzip, cmlidt from CUSMASFL " 
				+ " where CMCMP = 'CR' and CMCUS# = " + account;
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}

	@SuppressWarnings("unused")
	private DBQuery getNarpAccountNumberForSubAccount(String account) {//2387138, and returned NARP 9231004
		String sql = "select CMCRP# from CUSMASFL where CMCMP = 'CR' and CMCUS# = " + account;
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
	@SuppressWarnings("unused")
	private DBQuery getNarpAccountDetails(String narpAccount) {//9231004
		String sql = "select cmname, cmadr1, cmadr2, cmcity, cmstat, cmzip, cmlidt " 
				+ " from CUSMASFL where  CMCMP = 'CR' and CMCRP# = " + narpAccount + " and CMCUS# = CMCRP#";
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
	@SuppressWarnings("unused")	
	private DBQuery getSubAccountsUnderNarp(String narpAccount) {//9231004
		String sql = "select CMCUS#, cmname, cmadr1, cmadr2, cmcity, cmstat, cmzip, cmlidt from " 
				+ " CUSMASFL  where  CMCMP = 'CR' and CMCRP# = " + narpAccount + " order by CMCUS# ";
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
}
