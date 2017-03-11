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
* @author John Vincent
*/

public class AS400TestRM {
	private DB m_db;

	public static void main(String[] args) {
		AS400TestRM test = new AS400TestRM();
		test.doWork();
	}

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
/*
Connecting to database
dbdriver com.ibm.as400.access.AS400JDBCDriver
url jdbc:as400://AS4DEV
username prc4031
17:55:49; Start...
17:55:49; Before connection...
17:55:49; Before query...
17:55:50; Before metadata...
cols 1
queryColumnInfo (select b.CMCRP# from RMHCQDATA.CUSMASFL b, RMHCQDATA.CUSMS2FL a where a.CMCMP2 = b.CMCMP and a.CMCMP2 ='HG' and a.CMCUS#2 = b.CMCUS# and a.CMNATLCON# = 4017,1),((CMCRP#,3))
17:55:50; Before getting rows...
17:55:50; Done getting rows; rows = 1
17:55:50; Finished...
(1),(('6003093'))
Disconnecting from database
exiting...
*/
	@SuppressWarnings("unused")
	private void doTest1() {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = getNarpPart1("4017");
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		System.out.println (dbQuery.reportQuery(millis));
	}
/*
Connecting to database
dbdriver com.ibm.as400.access.AS400JDBCDriver
url jdbc:as400://AS4DEV
username prc4031
17:56:46; Start...
17:56:46; Before connection...
17:56:46; Before query...
17:56:47; Before metadata...
cols 7
queryColumnInfo (select a.CMCUS#, a.cmname, a.cmadr1, a.cmadr2, a.cmcity, a.cmstat, a.cmzip from RMHCQDATA.CUSMS2F2 b, RMHCQDATA.CUSMASFL a where b.CMCUS#2 = a.CMCUS# and b.CMCMP2 = 'HG' and b.CMNATLCON# = 4017 order by a.CMCMP,7),((CMCUS#,2)(CMNAME,1)(CMADR1,1)(CMADR2,1)(CMCITY,1)(CMSTAT,1)(CMZIP,1))
17:56:47; Before getting rows...
17:56:47; Done getting rows; rows = 1
17:56:47; Finished...
(1),((6003093)('ARB INC                       ')('26000 COMMERCENTRE DR         ')('                              ')('LAKE FOREST         ')('CA')('92630     '))
Disconnecting from database
exiting...
*/
	@SuppressWarnings("unused")
	private void doTest2() {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = getNarpPart2("4017");
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		System.out.println (dbQuery.reportQuery(millis));
	}
/*
Connecting to database
dbdriver com.ibm.as400.access.AS400JDBCDriver
url jdbc:as400://AS4DEV
username prc4031
17:57:14; Start...
17:57:14; Before connection...
17:57:15; Before query...
17:57:15; Before metadata...
cols 6
queryColumnInfo (select cmname, cmadr1, cmadr2, cmcity, cmstat, cmzip from RMHCQDATA.CUSMASF4 where CMCMP = 'HG' and CMCRP# = 6003093 and CMCUS# = 6003093,6),((CMNAME,1)(CMADR1,1)(CMADR2,1)(CMCITY,1)(CMSTAT,1)(CMZIP,1))
17:57:15; Before getting rows...
17:57:15; Done getting rows; rows = 1
17:57:15; Finished...
(1),(('ARB INC                       ')('26000 COMMERCENTRE DR         ')('                              ')('LAKE FOREST         ')('CA')('92630     '))
Disconnecting from database
exiting...
*/
	@SuppressWarnings("unused")
	private void doTest3() {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = getNarpPart3("6003093");
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		System.out.println (dbQuery.reportQuery(millis));
	}
/*
Connecting to database
dbdriver com.ibm.as400.access.AS400JDBCDriver
url jdbc:as400://AS4DEV
username prc4031
10:13:07; Start...
10:13:07; Before connection...
10:13:07; Before query...
10:13:08; Before metadata...
cols 1
queryColumnInfo (select b.CMCRP# from RMHCQDATA.CUSMASFL b, RMHCQDATA.CUSMS2FL a where a.CMCMP2 = b.CMCMP and a.CMCMP2 ='HG' and a.CMCUS#2 = b.CMCUS# and a.CMNATLCON# = 6003093,1),((CMCRP#,3))
10:13:08; Before getting rows...
10:13:08; Done getting rows; rows = 0
10:13:08; Finished...
(0),()
10:13:08; Start...
10:13:08; Before connection...
10:13:08; Before query...
10:13:08; Before metadata...
cols 6
queryColumnInfo (select cmname, cmadr1, cmadr2, cmcity, cmstat, cmzip from RMHCQDATA.CUSMASF4 where CMCMP = 'HG' and CMCRP# = 6003093 and CMCUS# = 6003093,6),((CMNAME,1)(CMADR1,1)(CMADR2,1)(CMCITY,1)(CMSTAT,1)(CMZIP,1))
10:13:08; Before getting rows...
10:13:08; Done getting rows; rows = 1
10:13:08; Finished...
(1),(('ARB INC                       ')('26000 COMMERCENTRE DR         ')('                              ')('LAKE FOREST         ')('CA')('92630     '))
Disconnecting from database
exiting...

*/
	private void doTest4() {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = getNarpPart1("6003093");
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		System.out.println (dbQuery.reportQuery(millis));
	}
/*
Connecting to database
dbdriver com.ibm.as400.access.AS400JDBCDriver
url jdbc:as400://AS4DEV
username prc4031
10:14:06; Start...
10:14:06; Before connection...
10:14:06; Before query...
10:14:07; Before metadata...
cols 1
queryColumnInfo (select b.CMCRP# from RMHCQDATA.CUSMASFL b, RMHCQDATA.CUSMS2FL a where a.CMCMP2 = b.CMCMP and a.CMCMP2 ='HG' and a.CMCUS#2 = b.CMCUS# and a.CMNATLCON# = 7850695,1),((CMCRP#,3))
10:14:07; Before getting rows...
10:14:07; Done getting rows; rows = 0
10:14:07; Finished...
(0),()
10:14:07; Start...
10:14:07; Before connection...
10:14:07; Before query...
10:14:07; Before metadata...
cols 6
queryColumnInfo (select cmname, cmadr1, cmadr2, cmcity, cmstat, cmzip from RMHCQDATA.CUSMASF4 where CMCMP = 'HG' and CMCRP# = 7850695 and CMCUS# = 7850695,6),((CMNAME,1)(CMADR1,1)(CMADR2,1)(CMCITY,1)(CMSTAT,1)(CMZIP,1))
10:14:07; Before getting rows...
10:14:07; Done getting rows; rows = 1
10:14:07; Finished...
(1),(('WHC INC                       ')('P O BOX 2340                  ')('                              ')('LAFAYETTE           ')('LA')('705022340 '))
Disconnecting from database
exiting...
*/
	@SuppressWarnings("unused")
	private void doTest5() {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = getNarpPart1("7850695");
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		System.out.println (dbQuery.reportQuery(millis));
	}
	@SuppressWarnings("unused")
	private void doTest5a() {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = getNarpPart3("7850695");
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		System.out.println (dbQuery.reportQuery(millis));
	}
/*
Connecting to database
dbdriver com.ibm.as400.access.AS400JDBCDriver
url jdbc:as400://AS4DEV
username prc4031
10:16:02; Start...
10:16:02; Before connection...
10:16:03; Before query...
10:16:03; Before metadata...
cols 1
queryColumnInfo (select b.CMCRP# from RMHCQDATA.CUSMASFL b, RMHCQDATA.CUSMS2FL a where a.CMCMP2 = b.CMCMP and a.CMCMP2 ='HG' and a.CMCUS#2 = b.CMCUS# and a.CMNATLCON# = 3093,1),((CMCRP#,3))
10:16:03; Before getting rows...
10:16:03; Done getting rows; rows = 1
10:16:03; Finished...
(1),(('7850695'))
10:16:03; Start...
10:16:03; Before connection...
10:16:03; Before query...
10:16:03; Before metadata...
cols 6
queryColumnInfo (select cmname, cmadr1, cmadr2, cmcity, cmstat, cmzip from RMHCQDATA.CUSMASF4 where CMCMP = 'HG' and CMCRP# = 3093 and CMCUS# = 3093,6),((CMNAME,1)(CMADR1,1)(CMADR2,1)(CMCITY,1)(CMSTAT,1)(CMZIP,1))
10:16:03; Before getting rows...
10:16:03; Done getting rows; rows = 0
10:16:03; Finished...
(0),()
Disconnecting from database
exiting...
*/
	@SuppressWarnings("unused")
	private void doTest6() {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = getNarpPart1("3093");
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		System.out.println (dbQuery.reportQuery(millis));
	}
	@SuppressWarnings("unused")
	private void doTest6a() {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		DBQuery dbQuery = getNarpPart3("3093");
		stopWatch.stop();
		final long millis = stopWatch.getTime();
		System.out.println (dbQuery.reportQuery(millis));
	}
	private DBQuery getNarpPart1(String narp) {
		String sql = "select distinct(b.CMCRP#) from RMHCQDATA.CUSMASFL b, RMHCQDATA.CUSMS2FL a "+
			"where a.CMCMP2 ='HG' and a.CMNATLCON# = "+narp+" and a.CMCMP2 = b.CMCMP and  a.CMCUS#2 = b.CMCUS#";
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
	private DBQuery getNarpPart2(String narp) {
		String sql = "select a.CMCUS#, a.cmname, a.cmadr1, a.cmadr2, a.cmcity, a.cmstat, a.cmzip "+
			"from RMHCQDATA.CUSMS2F2 b, RMHCQDATA.CUSMASFL a "+
			"where b.CMCUS#2 = a.CMCUS# and b.CMCMP2 = 'HG' and b.CMNATLCON# = "+narp+" order by a.CMCMP";
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
	private DBQuery getNarpPart3(String corplink) {
		String sql = "select cmname, cmadr1, cmadr2, cmcity, cmstat, cmzip "+
			"from RMHCQDATA.CUSMASF4 "+
			"where CMCMP = 'HG' and CMCRP# = "+corplink+" and CMCUS# = "+corplink;			// both of these are the corplink
		DBQuery dbQuery = new DBQuery (m_db, sql);
		dbQuery.executeQuery();
		return dbQuery;
	}
}
