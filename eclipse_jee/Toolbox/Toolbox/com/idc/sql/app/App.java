package com.idc.sql.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.file.JVFile;
import com.idc.sql.lib.DB;
import com.idc.sql.lib.IncludeTableInfo;
import com.idc.sql.lib.IncludeTableItemInfo;
import com.idc.sql.lib.MakeSQL;
import com.idc.sql.lib.Output;
import com.idc.sql.lib.OutputFile;
import com.idc.sql.lib.TableInfo;
import com.idc.trace.LogHelper;

public class App {

	public static void main (String[] args) {
//		(new App()).doTestDemoDataBob();
//		(new App()).doTestDemoData2();
//		(new App()).doTestDemoDataBob2();
//		(new App()).doTestDemoData();
//		(new App()).doTestAirClick();
//		(new App()).doTestAS400();
//		(new App()).doTestDataHercdb();
//		(new App()).doGetSomeAS400Data();
	}

	@SuppressWarnings("unused")
	private void doTestAirClick() {
		String scenario = "airclickDev";
		String toSchema = "hercdb";
		LogHelper.info("Connecting to database using "+scenario);
		DB db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! db.getConnection (scenario)) {
			LogHelper.info("giving up...");
			System.exit(1);
		}

		IncludeTableInfo includeTableInfo = new IncludeTableInfo();
		ArrayList<TableInfo> allTableInfo = db.makeTableInfoWithIncludeList (includeTableInfo);
		LogHelper.info("number of tables "+allTableInfo.size());

		Iterator<TableInfo> iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			TableInfo info = (TableInfo) iterator.next();
			info.doShowInfo();
		}

		boolean bDropTables = true;
		boolean bCreateTables = true;
		boolean bCreateIndexes = true;
		boolean bCreateGrants = true;
		boolean bInserts = true;

		new File ("c:\\tmp\\data\\airclick\\inserts").mkdirs();
		if (bDropTables) {
			Output output = startOutput ("c:\\tmp\\data\\airclick\\dropTables.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.dropTable (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateTables) {
			Output output = startOutput ("c:\\tmp\\data\\airclick\\createTables.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeCreateTable (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateIndexes) {
			Output output = startOutput ("c:\\tmp\\data\\airclick\\createIndexes.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeIndexes (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateGrants) {
			Output output = startOutput ("c:\\tmp\\data\\airclick\\createGrants.sql");
			for (int i = 0; bCreateGrants && i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeGrants (output, toSchema, tableInfo.getName());
			}
			endOutput (output);
			output = null;
		}

		for (int i = 0; bInserts && i < allTableInfo.size(); i++) {
			TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
			Output output = startOutput ("c:\\tmp\\data\\airclick\\inserts\\"+tableInfo.getName().toLowerCase()+".sql");
			MakeSQL.makeCreateInserts (db, output, toSchema, tableInfo);
			endOutput (output);
			output = null;
		}

		LogHelper.info("Disconnecting from database");
		db.disConnect();

		LogHelper.info("exiting...");
	}

	private void doGetSomeAS400Data() {
		String scenario = "as400CA";
		String toSchema = "hercdb";
		LogHelper.info("Connecting to database using "+scenario);
		DB db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! db.getConnection (scenario)) {
			LogHelper.info("giving up...");
			System.exit(1);
		}

		IncludeTableInfo includeTableInfo = new IncludeTableInfo();
		includeTableInfo.add (new IncludeTableItemInfo ("SYSCTLFL"));
		LogHelper.info("number of tables "+includeTableInfo.getSize());

		ArrayList<TableInfo> allTableInfo = db.makeTableInfoWithIncludeList (includeTableInfo);
		LogHelper.info("number of tables "+allTableInfo.size());

		boolean bDropTables = true;
		boolean bCreateTables = true;
		boolean bCreateIndexes = true;
		boolean bCreateGrants = true;
		boolean bInserts = true;

		new File ("c:\\tmp\\data\\as400\\inserts").mkdirs();
		if (bDropTables) {
			Output output = startOutput ("c:\\tmp\\data\\as400\\dropTables.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.dropTable (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateTables) {
			Output output = startOutput ("c:\\tmp\\data\\as400\\createTables.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeCreateTable (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateIndexes) {
			Output output = startOutput ("c:\\tmp\\data\\as400\\createIndexes.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeIndexes (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateGrants) {
			Output output = startOutput ("c:\\tmp\\data\\as400\\createGrants.sql");
			for (int i = 0; bCreateGrants && i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeGrants (output, toSchema, tableInfo.getName());
			}
			endOutput (output);
			output = null;
		}

		for (int i = 0; bInserts && i < allTableInfo.size(); i++) {
			TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
			Output output = startOutput ("c:\\tmp\\data\\as400\\inserts\\"+tableInfo.getName().toLowerCase()+".sql");
			MakeSQL.makeCreateInserts (db, output, toSchema, tableInfo);
			endOutput (output);
			output = null;
		}

		LogHelper.info("Disconnecting from database");
		db.disConnect();

		LogHelper.info("exiting...");
	}

	@SuppressWarnings("unused")
	private void doTestDemoData() {
		String scenario = "as400US";
		String toSchema = "hercdb";
		LogHelper.info("Connecting to database using "+scenario);
		DB db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! db.getConnection (scenario)) {
			LogHelper.info("giving up...");
			System.exit(1);
		}

		IncludeTableInfo includeTableInfo = new IncludeTableInfo();
		includeTableInfo.add (new IncludeTableItemInfo ("RAOADTFL",
				"SELECT B.* FROM RAOHDRFL as A, RAOADTFL as B",
				"WHERE RHCMP = ADCMP and RHCON# = ADCON# and RHCMP = 'HG' and RHCUS# = '140276'",
				"ORDER BY ADCMP, ADCON#, ADTYPE, ADSEQ#"));

		includeTableInfo.add (new IncludeTableItemInfo ("ARIDETFL", "", "WHERE ADCMP = 'HG' and ADCUS# = '140276'"));		// needed by a view
		includeTableInfo.add (new IncludeTableItemInfo ("ARIHD2FL", "", "WHERE AHCMP = 'HG' and AHCUS# = '140276'"));
		includeTableInfo.add (new IncludeTableItemInfo ("ARIHDRFL", "", "WHERE AHCMP = 'HG' and AHCUS# = '140276'"));
		includeTableInfo.add (new IncludeTableItemInfo ("CRAHDRFL", "", "WHERE HCCMP = 'HG' and HCCUS# = '140276'"));
		includeTableInfo.add (new IncludeTableItemInfo ("CUSJBLFL", "", "WHERE CICMP = 'HG' and CICUS# = '140276'"));
		includeTableInfo.add (new IncludeTableItemInfo ("CUSJOBFL", "", "WHERE CJCMP = 'HG' and CJCUS# = '140276'"));
		includeTableInfo.add (new IncludeTableItemInfo ("CUSMASFL", "", "WHERE CMCMP = 'HG' and CMCUS# = '140276'"));

		includeTableInfo.add (new IncludeTableItemInfo ("CUSMS2FL", "", "where CMCMP2 = 'HG' and CMCUS#2 = '140276'"));

		includeTableInfo.add (new IncludeTableItemInfo ("MARACDFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RACDETFL", "", "WHERE RDCMP = 'HG' and RDCUS# = '140276'"));
		includeTableInfo.add (new IncludeTableItemInfo ("RACHDRFL", "", "WHERE RHCMP = 'HG' and RHCUS# = '140276'"));

		includeTableInfo.add (new IncludeTableItemInfo ("RAODETFL", "", "WHERE RDCMP = 'HG' and RDCUS# = '140276'"));
		includeTableInfo.add (new IncludeTableItemInfo ("RAOHDRFL", "", "WHERE RHCMP = 'HG' and RHCUS# = '140276'"));

		includeTableInfo.add (new IncludeTableItemInfo ("SYSCTLFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("SYSLOCFL"));

		includeTableInfo.add (new IncludeTableItemInfo ("SYSPGMFL"));	// do not need all of them

		includeTableInfo.add (new IncludeTableItemInfo ("SYSSECFL"));		// needed by a view
		includeTableInfo.add (new IncludeTableItemInfo ("WOHEADFL", "", "WHERE VHCMP = 'HG' and VHCUS# = '140276'"));
		includeTableInfo.add (new IncludeTableItemInfo ("PTFTYPHF"));
		LogHelper.info("number of tables "+includeTableInfo.getSize());

		ArrayList<TableInfo> allTableInfo = db.makeTableInfoWithIncludeList (includeTableInfo);
		LogHelper.info("number of tables "+allTableInfo.size());

		boolean bDropTables = true;
		boolean bCreateTables = true;
		boolean bCreateIndexes = true;
		boolean bCreateGrants = true;
		boolean bInserts = true;

		new File ("c:\\tmp\\data\\demodata\\inserts").mkdirs();
		if (bDropTables) {
			Output output = startOutput ("c:\\tmp\\data\\demodata\\dropTables.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.dropTable (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateTables) {
			Output output = startOutput ("c:\\tmp\\data\\demodata\\createTables.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeCreateTable (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateIndexes) {
			Output output = startOutput ("c:\\tmp\\data\\demodata\\createIndexes.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeIndexes (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateGrants) {
			Output output = startOutput ("c:\\tmp\\data\\demodata\\createGrants.sql");
			for (int i = 0; bCreateGrants && i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeGrants (output, toSchema, tableInfo.getName());
			}
			endOutput (output);
			output = null;
		}

		for (int i = 0; bInserts && i < allTableInfo.size(); i++) {
			TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
			Output output = startOutput ("c:\\tmp\\data\\demodata\\inserts\\"+tableInfo.getName().toLowerCase()+".sql");
			MakeSQL.makeCreateInserts (db, output, toSchema, tableInfo);
			endOutput (output);
			output = null;
		}

		LogHelper.info("Disconnecting from database");
		db.disConnect();

		LogHelper.info("exiting...");
	}

	@SuppressWarnings("unused")
	private void doTestDemoDataBob() {
		String scenario = "as400USBob";
		String toSchema = "hercdb";
		LogHelper.info("Connecting to database using "+scenario);
		DB db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! db.getConnection (scenario)) {
			LogHelper.info("giving up...");
			System.exit(1);
		}

		IncludeTableInfo includeTableInfo = new IncludeTableInfo();
		includeTableInfo.add (new IncludeTableItemInfo ("RAOADTFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RAOAMTFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RASDETFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RAPKUPFL"));

		includeTableInfo.add (new IncludeTableItemInfo ("EQPMASFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("EQPCCMFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("ITMMASFL"));

		includeTableInfo.add (new IncludeTableItemInfo ("ORDCOMFL"));
		LogHelper.info("number of tables "+includeTableInfo.getSize());

		ArrayList<TableInfo> allTableInfo = db.makeTableInfoWithIncludeList (includeTableInfo);
		LogHelper.info("number of tables "+allTableInfo.size());

		boolean bDropTables = true;
		boolean bCreateTables = true;
		boolean bCreateIndexes = true;
		boolean bCreateGrants = true;
		boolean bInserts = true;

		new File ("c:\\tmp\\data\\demodata_bob\\inserts").mkdirs();
		if (bDropTables) {
			Output output = startOutput ("c:\\tmp\\data\\demodata_bob\\dropTables.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.dropTable (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateTables) {
			Output output = startOutput ("c:\\tmp\\data\\demodata_bob\\createTables.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeCreateTable (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateIndexes) {
			Output output = startOutput ("c:\\tmp\\data\\demodata_bob\\createIndexes.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeIndexes (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateGrants) {
			Output output = startOutput ("c:\\tmp\\data\\demodata_bob\\createGrants.sql");
			for (int i = 0; bCreateGrants && i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeGrants (output, toSchema, tableInfo.getName());
			}
			endOutput (output);
			output = null;
		}

		for (int i = 0; bInserts && i < allTableInfo.size(); i++) {
			TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
			Output output = startOutput ("c:\\tmp\\data\\demodata_bob\\inserts\\"+tableInfo.getName().toLowerCase()+".sql");
			MakeSQL.makeCreateInserts (db, output, toSchema, tableInfo);
			endOutput (output);
			output = null;
		}

		LogHelper.info("Disconnecting from database");
		db.disConnect();

		LogHelper.info("exiting...");
	}

	@SuppressWarnings("unused")
	private void doTestDemoDataBob2() {
		String scenario = "as400USBob";
		String toSchema = "hercdb";
		LogHelper.info("Connecting to database using "+scenario);
		DB db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! db.getConnection (scenario)) {
			LogHelper.info("giving up...");
			System.exit(1);
		}

		IncludeTableInfo includeTableInfo = new IncludeTableInfo();
		includeTableInfo.add (new IncludeTableItemInfo ("ARIDETFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("ARIHD2FL"));
		includeTableInfo.add (new IncludeTableItemInfo ("ARIHDRFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("CRAHDRFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("CUSJBLFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("CUSJOBFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("ETWHDRFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("ETWDETFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("ORDCOMFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RAAPKUFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RACDETFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RACHDRFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RAOADTFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RAOAMTFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RAODETFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RAOHDRFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RAPKUPFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("RASDETFL"));
		includeTableInfo.add (new IncludeTableItemInfo ("WOHEADFL"));
		LogHelper.info("number of tables "+includeTableInfo.getSize());

		ArrayList<TableInfo> allTableInfo = db.makeTableInfoWithIncludeList (includeTableInfo);
		LogHelper.info("number of tables "+allTableInfo.size());

		boolean bDropTables = true;
		boolean bCreateTables = true;
		boolean bCreateIndexes = true;
		boolean bCreateGrants = true;
		boolean bInserts = true;

		new File ("c:\\tmp\\data\\demodata_bob2\\inserts").mkdirs();
		if (bDropTables) {
			Output output = startOutput ("c:\\tmp\\data\\demodata_bob\\dropTables.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.dropTable (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateTables) {
			Output output = startOutput ("c:\\tmp\\data\\demodata_bob2\\createTables.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeCreateTable (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateIndexes) {
			Output output = startOutput ("c:\\tmp\\data\\demodata_bob2\\createIndexes.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeIndexes (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateGrants) {
			Output output = startOutput ("c:\\tmp\\data\\demodata_bob2\\createGrants.sql");
			for (int i = 0; bCreateGrants && i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeGrants (output, toSchema, tableInfo.getName());
			}
			endOutput (output);
			output = null;
		}

		for (int i = 0; bInserts && i < allTableInfo.size(); i++) {
			TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
			Output output = startOutput ("c:\\tmp\\data\\demodata_bob2\\inserts\\"+tableInfo.getName().toLowerCase()+".sql");
			MakeSQL.makeCreateInserts (db, output, toSchema, tableInfo);
			endOutput (output);
			output = null;
		}

		LogHelper.info("Disconnecting from database");
		db.disConnect();

		LogHelper.info("exiting...");
	}

	@SuppressWarnings("unused")
	private void doTestAS400() {
		File outputFile = new File ("c:\\tmp\\data\\as400\\OK.sql");
		LogHelper.info ("Opening file");		
		Output output = new OutputFile(outputFile);
		if (! output.open()) {
			LogHelper.info("giving up...");
			System.exit(1);
		}

		String scenario = "as400US";
		String toSchema = "hercdb";
		LogHelper.info("Connecting to database using "+scenario);
		DB db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! db.getConnection (scenario)) {
			LogHelper.info("giving up...");
			System.exit(1);
		}
		db.setDebug(true);

		IncludeTableInfo includeTableInfo = new IncludeTableInfo();
		ArrayList<TableInfo> allTableInfo = db.makeTableInfoWithIncludeList (includeTableInfo);
		LogHelper.info("number of tables "+allTableInfo.size());

		Iterator<TableInfo> iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			TableInfo info = (TableInfo) iterator.next();
			info.doShowInfo();
		}

		LogHelper.info("Disconnecting from database");
		db.disConnect();

		LogHelper.info("exiting...");
	}

	@SuppressWarnings("unused")
	private void doTestHercdb() {
		File outputFile = new File ("c:\\tmp\\all.sql");
		LogHelper.info ("Opening file");		
		Output output = new OutputFile(outputFile);
		if (! output.open()) {
			LogHelper.info("giving up...");
			System.exit(1);
		}

		String scenario = "edevdb";
		String toSchema = "hercdb";
		LogHelper.info("Connecting to database using "+scenario);
		DB db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! db.getConnection (scenario)) {
			LogHelper.info("giving up...");
			System.exit(1);
		}
		db.setDebug(true);

		IncludeTableInfo includeTableInfo = new IncludeTableInfo();
		ArrayList<TableInfo> allTableInfo = db.makeTableInfoWithIncludeList (includeTableInfo);
		LogHelper.info("number of tables "+allTableInfo.size());

		Iterator<TableInfo> iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			TableInfo info = (TableInfo) iterator.next();
			info.doShowInfo();
		}

		ArrayList<String> list = db.getViews (includeTableInfo);
		for (String item : list) {
			MakeSQL.makeGrants (output, toSchema, item);
		}

		LogHelper.info("Writing the sql file");
		output.writeNL();
		output.writeNL("-- generated SQL file");
		output.writeNL();
		for (int i = 0; i < allTableInfo.size(); i++) {
			TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
//			MakeSQL.dropTable (output, toSchema, tableInfo);
//			MakeSQL.makeCreateTable (output, toSchema, tableInfo);
//			MakeSQL.makeIndexes (output, toSchema, tableInfo);
			MakeSQL.makeGrants (output, toSchema, tableInfo.getName());
		}
		output.writeNL();
		output.writeNL("-- end of generated SQL \n");
		output.close();
		output = null;

		LogHelper.info("Disconnecting from database");
		db.disConnect();

		LogHelper.info("exiting...");
	}

	@SuppressWarnings("unused")
	private void doTestDataHercdb() {
		String scenario = "edevdb";
		String toSchema = "hercdb";
		LogHelper.info("Connecting to database using "+scenario);
		DB db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! db.getConnection (scenario)) {
			LogHelper.info("giving up...");
			System.exit(1);
		}
		db.setDebug(true);

		IncludeTableInfo includeTableInfo = new IncludeTableInfo();
//		includeTableInfo.add (new IncludeTableItemInfo ("HOMEPAGECATEGORIES"));
//		includeTableInfo.add (new IncludeTableItemInfo ("HOTDEALS"));
		includeTableInfo.add (new IncludeTableItemInfo ("BRANCHINFO"));
		includeTableInfo.add (new IncludeTableItemInfo ("BranchLocations"));
//		includeTableInfo.add (new IncludeTableItemInfo ("RENTALRATES"));
//		includeTableInfo.add (new IncludeTableItemInfo ("ITEMDETAILS"));
//		includeTableInfo.add (new IncludeTableItemInfo ("RENTALEQUIPMENT"));
//		includeTableInfo.add (new IncludeTableItemInfo ("SALESITEMS"));

		ArrayList<TableInfo> allTableInfo = db.makeTableInfoWithIncludeList (includeTableInfo);
		LogHelper.info("number of tables "+allTableInfo.size());

		Iterator<TableInfo> iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			TableInfo info = (TableInfo) iterator.next();
			info.doShowInfo();
		}

		LogHelper.info("Writing the sql file");
		for (int i = 0; i < allTableInfo.size(); i++) {
			TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
			File outputFile = new File ("c:\\tmp\\data\\testDataHercDB\\inserts\\"+tableInfo.getName().toLowerCase()+".sql");
			JVFile.makeDirectories (outputFile);
			Output output = startOutput (outputFile.getPath());
			MakeSQL.makeCreateInserts (db, output, toSchema, tableInfo);
			endOutput (output);
			output = null;
		}

		LogHelper.info("Disconnecting from database");
		db.disConnect();

		LogHelper.info("exiting...");
	}

	@SuppressWarnings("unused")
	private void doTestDemoData2() {
		String scenario = "as400US";
		String toSchema = "hercdb";
		LogHelper.info("Connecting to database using "+scenario);
		DB db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! db.getConnection (scenario)) {
			LogHelper.info("giving up...");
			System.exit(1);
		}

		IncludeTableInfo includeTableInfo = new IncludeTableInfo();
		includeTableInfo.add (new IncludeTableItemInfo ("ACRDATAFL"));
		LogHelper.info("number of tables "+includeTableInfo.getSize());

		ArrayList<TableInfo> allTableInfo = db.makeTableInfoWithIncludeList (includeTableInfo);
		LogHelper.info("number of tables "+allTableInfo.size());

		boolean bDropTables = true;
		boolean bCreateTables = true;
		boolean bCreateIndexes = true;
		boolean bCreateGrants = true;
		boolean bInserts = false;

		new File ("c:\\tmp\\data\\demodata2\\inserts").mkdirs();
		if (bDropTables) {
			Output output = startOutput ("c:\\tmp\\data\\demodata2\\dropTables.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.dropTable (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateTables) {
			Output output = startOutput ("c:\\tmp\\data\\demodata2\\createTables.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeCreateTable (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateIndexes) {
			Output output = startOutput ("c:\\tmp\\data\\demodata2\\createIndexes.sql");
			for (int i = 0; i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeIndexes (output, toSchema, tableInfo);
			}
			endOutput (output);
			output = null;
		}

		if (bCreateGrants) {
			Output output = startOutput ("c:\\tmp\\data\\demodata2\\createGrants.sql");
			for (int i = 0; bCreateGrants && i < allTableInfo.size(); i++) {
				TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
				MakeSQL.makeGrants (output, toSchema, tableInfo.getName());
			}
			endOutput (output);
			output = null;
		}

		for (int i = 0; bInserts && i < allTableInfo.size(); i++) {
			TableInfo tableInfo = (TableInfo) allTableInfo.get(i);
			Output output = startOutput ("c:\\tmp\\data\\demodata2\\inserts\\"+tableInfo.getName().toLowerCase()+".sql");
			MakeSQL.makeCreateInserts (db, output, toSchema, tableInfo);
			endOutput (output);
			output = null;
		}

		LogHelper.info("Disconnecting from database");
		db.disConnect();

		LogHelper.info("exiting...");
	}

	private Output startOutput (String file) {
		Output output = new OutputFile (new File (file));
		if (! output.open ()) {
			LogHelper.info ("giving up...");
			System.exit(1);
		}
		output.writeNL();
		output.writeNL("-- generated SQL file");
		output.writeNL();
		return output;
	}

	private void endOutput (Output output) {
		output.writeNL();
		output.writeNL("commit;");
		output.writeNL();
		output.writeNL("-- end of generated SQL \n");
		output.close();
	}
}
