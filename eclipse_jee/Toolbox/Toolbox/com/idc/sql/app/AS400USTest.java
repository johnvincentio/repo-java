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
import com.idc.utils.HercDate;

/**
 * Test AS400USBox
 */
public class AS400USTest {
	private DB m_db;

	public AS400USTest() {}

	public void doWork() {

		System.out.println("Connecting to database");
		m_db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! m_db.getConnection ("as400US")) {
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
				+ " where  CMCMP = 'HG' and CMCUS# = " + account;
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
	@SuppressWarnings("unused")
	private DBQuery getNarpAccountNumberForSubAccount(String account) {//2658154
		String sql = "select CMNATLCON# from CUSMS2FL where CMCMP2 = 'HG' and CMCUS#2 =  " + account;
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
	@SuppressWarnings("unused")
	private DBQuery getCorpLinkAccountForSubAccount(String account) {//2658154
		String sql = "select b.CMCRP# from CUSMASFL b inner join CUSMS2FL a on " 
				+ "(a.CMCMP2 = b.CMCMP and a.CMCUS#2 = b.CMCUS#) " 
				+ "where a.CMCMP2 ='HG' and a.CMNATLCON# = " + account;
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
	@SuppressWarnings("unused")
	private DBQuery getSubAccountsUnderNarp(String subAccount) {//2658154
		String sql = "select a.CMCUS#, a.cmname, a.cmadr1, a.cmadr2, a.cmcity, a.cmstat, a.cmzip, a.cmlidt "
				+ " from CUSMASFL a inner join CUSMS2F2 b on (b.CMCMP2 = a.CMCMP and b.CMCUS#2 = a.CMCUS#)  "
				+ " where  b.CMCMP2 = 'HG' and b.CMNATLCON# = " + subAccount + " order by a.CMCMP" ;
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
	@SuppressWarnings("unused")
	private DBQuery getNarpForCorpLinkAccount(String corpLinkAccount) {//2658154
		String sql = "select cmname, cmadr1, cmadr2, cmcity, cmstat, cmzip, cmlidt from CUSMASF4  " 
				+ "where  CMCMP = 'HG' and CMCRP# = " + corpLinkAccount + " and CMCUS# = " + corpLinkAccount ;
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
	@SuppressWarnings("unused")
	private DBQuery getContractLocations(String account) {//2658154
		String today = new HercDate().getRentalManTodayDateFormat();
		String sql = "select distinct d.E5LOC from EQPCLRFL d, EQPCPRFL a, EQPCPHFL b, EQPHPRFL c, EQPDSCFL e " 
				+ " where a.EQCNT# = b.EYCNT# and a.EQCNT# = c.E6CNT# and a.EQCNT# = d.E5CNT# " 
				+ " and a.EQCNT# = e.EDCNT# " 
				+ " and b.EYSTDT &lt;= " + today + " and b.EYENDT &gt;= " + today + " and a.EQCUS# = " + account;
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
	@SuppressWarnings("unused")
	private DBQuery getContractRentalItems(String account) {//2658154
		String today = new HercDate().getRentalManTodayDateFormat();
		String sql = "select distinct e.EDCATG, e.EDCLAS from EQPDSCFL e, EQPCPRFL a, EQPCPHFL b, EQPHPRFL c, EQPCLRFL d " 
				+ " where a.EQCNT# = b.EYCNT# and a.EQCNT# = c.E6CNT# and a.EQCNT# = d.E5CNT# " 
				+ " and a.EQCNT# = e.EDCNT# " 
				+ " and b.EYSTDT &lt;= " + today + " and b.EYENDT &gt;= " + today + " and a.EQCUS# = " + account;
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
}
