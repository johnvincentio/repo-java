package com.idc.sql.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.sql.lib.DB;
import com.idc.sql.lib.IncludeTableInfo;
import com.idc.sql.lib.IncludeTableItemInfo;
import com.idc.sql.lib.MakeSQLforMySQL;
import com.idc.sql.lib.Output;
import com.idc.sql.lib.OutputFile;
import com.idc.sql.lib.TableInfo;
import com.idc.trace.LogHelper;

public class MakeAllSQL_DB2_to_MySQL {
	private static final String CONFIG_FILE = "C:/jvDevelopment/repo_four/windows/dbtoolgui.xml";
	private static final String SQL_DIR = "c:/tmp/data/demodata2";
	private static final String SQL_INSERTS_DIR = "c:/tmp/data/demodata2/inserts";

	private static final String[] INCLUDE_LIST_1 = {
		"ADMINPROFILES", "BRANCHFRANCHISE", "BRANCHINFO", "BRANCHLOCATIONS", "BRANCHONSITEACCOUNTS", "BRANCHRATINGS",
		"CINELEASE_LOCATIONS", "CINELEASE_PACKAGES", "CINELEASE_PACKAGE_CATEGORIES", "CINELEASE_PACKAGE_ITEMS", 
		"COMPANIES", "COMPANYACCOUNTS",  
		"CRES_BRANCHES", "CRES_CUSTOMERS", "CRES_JOB_LOCATIONS", "CRES_RENTAL_ITEMS", "CRES_REQUESTS", 
		"DAOTEST", "EMAIL_ADDRESS", "EMAIL_CATEGORY", "EMAIL_DEFINITION", 
		"EMAIL_FIXED_ATTACHMENT", "FMADEALS", 
		"GRIP_PACKAGES", "GRIP_PACKAGE_CATEGORIES", "GRIP_PACKAGE_ITEMS", "HERCQUARTZJOBS", 
		"HES_CATEGORIES", "HES_CATEGORY_ITEMS", "HES_EQUIPMENT_CATEGORIES",	"HES_EQUIPMENT_CATEGORY_ITEMS", 
		"HES_GRIP_CATEGORIES", "HES_GRIP_CATEGORY_PACKAGES", "HES_GRIP_CATEGORY_PACKAGE_ITEMS", 
		"HES_LIGHTING_CATEGORIES", "HES_LIGHTING_CATEGORY_PACKAGES", "HES_LIGHTING_CATEGORY_PACKAGE_ITEMS", 
		"HES_LOCATIONS", "HES_REQUESTS", "HES_TRUCK_CATEGORIES", 
		"HES_TRUCK_CATEGORY_PACKAGES", "HES_TRUCK_CATEGORY_PACKAGE_ITEMS", "HES_VEHICLE_CATEGORIES", "HES_VEHICLE_CATEGORY_ITEMS", 
		"HOMEPAGECATEGORIES", "HOTDEALS", "ITEMDETAILS", "LDAPMEMBERS", 
		"LIGHTING_PACKAGES", "LIGHTING_PACKAGE_CATEGORIES", "LIGHTING_PACKAGE_ITEMS", 
		"MAKESMODELS", "MEMBERACCOUNTS", "MEMBERHISTORY", "MEMBERLOCATIONS", "MEMBERPREFERENCES", 
		"MEMBERREQUESTS", "MEMBERREQUESTSITEMS", "MEMBERS", 
		"MIGRATION", "MIGRATIONLOGS", "MINDSHARE", 
		"QRTZ_DM_BLOB_TRIGGERS", "QRTZ_DM_CRON_TRIGGERS", "QRTZ_DM_FIRED_TRIGGERS", 
		"QRTZ_DM_JOB_DETAILS", "QRTZ_DM_JOB_LISTENERS", "QRTZ_DM_LOCKS", "QRTZ_DM_PAUSED_TRIGGER_GRPS", 
		"QRTZ_DM_SCHEDULER_STATE", "QRTZ_DM_SIMPLE_TRIGGERS", "QRTZ_DM_TRIGGERS", "QRTZ_DM_TRIGGER_LISTENERS", 
		"RENTALEQUIPMENT", "RENTALRATES", 
		"RMACCOUNTSAF", "RMACCOUNTSCA", "RMACCOUNTSSA", "RMACCOUNTSUS", 
		"RMNARPACCOUNTSAF", "RMNARPACCOUNTSCA", "RMNARPACCOUNTSSA", "RMNARPACCOUNTSUS", 
		"SALESQUOTETOOLS", "TASKPROFILE", "TASKPROFILEACCOUNTS", "TASKPROFILEREPORTS"
	};
	private static final String[] INCLUDE_LIST_2 = {
		"BRANCHEQUIPMENTRATINGS", "SALESITEMS"
	};
	private static final String[] INCLUDE_LIST_3 = {
		"RMCONTRACTLOCATIONSUS", "RMCONTRACTLOCATIONSCA", "RMCONTRACTLOCATIONSAF", "RMCONTRACTLOCATIONSSA",
		"RMCONTRACTRENTALEQUIPMENTUS", "RMCONTRACTRENTALEQUIPMENTCA", "RMCONTRACTRENTALEQUIPMENTAF", "RMCONTRACTRENTALEQUIPMENTSA"
	};

	private static final String[] EXCLUDE_LIST = {
		"ACRDATAFL", "ARIDETFL", "ARIHD2FL", "ARIHDRFL", "CRAHDRFL", "CRAHDRFL", "CUSJBLFL", "CUSJOBFL", "CUSMASFL", "CUSMS2FL", 
		"EQPCCMFL", "EQPCCMFL", "EQPMASFL", "ETWDETFL", "ETWHDRFL", "ITMMASFL", "MARACDFL", "ORDCOMFL", "RAAPKUFL", "RACDETFL",
		"RACHDRFL", "RAOADTFL", "RAOAMTFL", "RAODETFL", "RAOHDRFL", "RAPKUPFL", "RASDETFL", "SYSCTLFL", "SYSLOCFL", "SYSPGMFL",
		"SYSSE2FL", "SYSSECFL", "WOHEADFL", "BRANCHEQUIPMENTRATINGS_TEMP", "BRANCHFRANCHISE_TEMP", "BRANCHINFO_TEMP",
		"BRANCHLOCATIONS_JV", "BRANCHLOCATIONS_TEMP", "BRANCHONSITEACCOUNTS_TEMP", "BRANCHRATINGS_TEMP",
		"CINELEASE_LOCATIONS_TEMP", "CINELEASE_PACKAGES_TEMP", "CINELEASE_PACKAGE_CATEGORIES_TEMP", "CINELEASE_PACKAGE_ITEMS_TEMP",
		"COMPANIES_TEMP", "COMPANYACCOUNTS_JV", "COMPANYACCOUNTS_TEMP", "COMPANYACCOUNTS_TEST", "CREDITAPPLICATIONSV2_TEMP",
		"CREDITAPPLICATIONS_TEMP", "CRES_BRANCHES_ML", "CRES_CUSTOMERS_ML", "CRES_JOB_LOCATIONS_ML", "CRES_RENTAL_ITEMS_ML",
		"CRES_REQUESTS_ML", "DC2_TASKPROFILE", "DC2_TASKPROFILEACCOUNTS", "DC2_TASKPROFILEREPORTS", "EMAIL_DEFINITION_ENUS_IDEV",
		"EMAIL_DEFINITION_ENUS_TEMP", "EMAIL_QUEUE_DC", "EMAIL_QUEUE_DC2", "EMAIL_QUEUE_RC", "FMADEALS_TEMP",
		"GRIP_PACKAGES_TEMP", "GRIP_PACKAGE_CATEGORIES_TEMP", "GRIP_PACKAGE_ITEMS_TEMP", "HES_CATEGORIES_TEMP", "HES_CATEGORY_ITEMS_TEMP",
		"HES_EQUIPMENT_CATEGORIES_TEMP", "HES_EQUIPMENT_CATEGORY_ITEMS_TEMP", "HES_GRIP_CATEGORIES_TEMP",
		"HES_GRIP_CATEGORY_PACKAGES_TEMP", "HES_GRIP_CATEGORY_PACKAGE_ITEMS_TEMP", "HES_LIGHTING_CATEGORIES_TEMP", 
		"HES_LIGHTING_CATEGORY_PACKAGES_TEMP", "HES_LIGHTING_CATEGORY_PACKAGE_ITEMS_TEMP", "HES_LOCATIONS_TEMP", "HES_REQUEST_DETAILS",
		"HES_TRUCK_CATEGORIES_TEMP", "HES_TRUCK_CATEGORY_PACKAGES_TEMP", "HES_TRUCK_CATEGORY_PACKAGE_ITEMS_TEMP",
		"HES_VEHICLE_CATEGORIES_TEMP", "HES_VEHICLE_CATEGORY_ITEMS_TEMP",
		"HOMEPAGECATEGORIES_TEMP", "HOTDEALS_TEMP", "ITEMDETAILS_TEMP",
		"JV", "JV1", "JV_ABC", "JV_DEF", "JV2", "JVTEST1", "LDAPMEMBERS_TEMP",
		"LIGHTING_PACKAGES_TEMP", "LIGHTING_PACKAGE_CATEGORIES_TEMP", "LIGHTING_PACKAGE_ITEMS_TEMP", "MAKESMODELS_TEMP",
		"MEMBERACCOUNTS_TEMP", "MEMBERHISTORY_TEMP", "MEMBERLOCATIONS_TEMP", "MEMBERPREFERENCES_TEMP", "MEMBERREQUESTSITEMS_TEMP",
		"MEMBERREQUESTS_TEMP", "MEMBERS_TEMP", "MIGRATIONLOGS_DC", "MIGRATIONLOGS_DC2", "MIGRATIONLOGS_DC_0429", "MIGRATIONLOGS_RC",
		"MINDSHARE_OLD", "PTFTYPHF", "RCSALESITEMS", "RENTALEQUIPMENT_TEMP", "RENTALRATES_TEMP", "RESTRICTED_TABLES", 
		"RMACCOUNTSCA_DC", "RMACCOUNTSCA_DC2", "RMACCOUNTSCA_RC", "RMACCOUNTSCA_TEMP", "RMACCOUNTSUS_DC", "RMACCOUNTSUS_DC2",
		"RMACCOUNTSUS_JV", "RMACCOUNTSUS_RC", "RMACCOUNTSUS_TEMP", "RMCONTRACTLOCATIONSCA_TEMP", "RMCONTRACTLOCATIONSUS_JV",
		"RMCONTRACTLOCATIONSUS_TEMP", "RMCONTRACTLOCATIONSUS_TEST", "RMCONTRACTRENTALEQUIPMENTCA_TEMP",
		"RMCONTRACTRENTALEQUIPMENTUS_JV", "RMCONTRACTRENTALEQUIPMENTUS_TEMP", "RMNARPACCOUNTSCA_TEMP",
		"RMNARPACCOUNTSUS_JV", "RMNARPACCOUNTSUS_TEMP", "SALESITEMS_TEMP", "SALESQUOTETOOLS_TEMP"
	};

	public static void main(String[] args) {
		MakeAllSQL_DB2_to_MySQL app = new MakeAllSQL_DB2_to_MySQL();
//		app.doCreate();
		app.doInserts (INCLUDE_LIST_1);
		app.doInserts (INCLUDE_LIST_2);
		app.doInserts (INCLUDE_LIST_3);
		app.doMakeLoadFile (INCLUDE_LIST_1, INCLUDE_LIST_2, INCLUDE_LIST_3);
	}
	private void doMakeLoadFile (String[]... args) {
		new File (SQL_DIR).mkdirs();
		Output output = startOutput (SQL_DIR + "/loadFiles.sql");
		for (int i = 0; i <args.length; i++) {
			String[] arr = args[i];
			for (int j = 0; j < arr.length; j++) {
				String str = arr[j];
				output.writeNL();
				output.writeNL ("loadfile " + SQL_INSERTS_DIR + "/" + str + ".sql;");
			}
		}
		endOutput (output);
		output = null;		
	}
	@SuppressWarnings("unused")
	private void doCreate() {
		new File (SQL_DIR).mkdirs();
		DB db = getDB ("ecomdb");
		String toSchema = "hercdb";
		IncludeTableInfo includeTableInfo = makeIncludeTables (EXCLUDE_LIST);
		ArrayList<TableInfo> allTableInfo = db.makeTableInfoWithIncludeList (includeTableInfo, false);	// exclude list
		LogHelper.info("number of tables "+allTableInfo.size());
		dropTables (db, toSchema, allTableInfo, SQL_DIR + "/dropTables.sql");
		createTables (db, toSchema, allTableInfo, SQL_DIR + "/createTables.sql");
		createIndexes (db, toSchema, allTableInfo, SQL_DIR + "/createIndexes.sql");
		closeDB (db);
	}
	private void doInserts (String[] includeList) {
		new File (SQL_INSERTS_DIR).mkdirs();
		DB db = getDB ("ecomdb");
		String toSchema = "hercdb";
		IncludeTableInfo includeTableInfo = makeIncludeTables (includeList);
		ArrayList<TableInfo> allTableInfo = db.makeTableInfoWithIncludeList (includeTableInfo, true);	// include list
		LogHelper.info("number of tables "+allTableInfo.size());
		for (int i = 0; i < allTableInfo.size(); i++) {
			TableInfo tableInfo = allTableInfo.get(i);
			createTableInserts (db, toSchema, tableInfo, SQL_INSERTS_DIR + "/" + tableInfo.getName().toLowerCase()+".sql");
		}
		closeDB (db);
	}
	private void dropTables (DB db, String toSchema, ArrayList<TableInfo> allTableInfo, String sqlFile) {
		Output output = startOutput (sqlFile);
		for (int i = 0; i < allTableInfo.size(); i++) {
			MakeSQLforMySQL.dropTable (output, toSchema, allTableInfo.get(i));
		}
		endOutput (output);
		output = null;
	}
	private void createTables (DB db, String toSchema, ArrayList<TableInfo> allTableInfo, String sqlFile) {
		Output output = startOutput (sqlFile);
		for (int i = 0; i < allTableInfo.size(); i++) {
			MakeSQLforMySQL.makeCreateTable (output, toSchema, allTableInfo.get(i));
		}
		endOutput (output);
		output = null;
	}
	private void createIndexes (DB db, String toSchema, ArrayList<TableInfo> allTableInfo, String sqlFile) {
		Output output = startOutput (sqlFile);
		for (int i = 0; i < allTableInfo.size(); i++) {
			MakeSQLforMySQL.makeIndexes (output, toSchema, allTableInfo.get(i));
		}
		endOutput (output);
		output = null;
	}
	private void createTableInserts (DB db, String toSchema, TableInfo tableInfo, String sqlFile) {
		Output output = startOutput (sqlFile);
		MakeSQLforMySQL.makeCreateInserts (db, output, toSchema, tableInfo);
		endOutput (output);
		output = null;
	}

	private IncludeTableInfo makeIncludeTables (String[] items) {
		IncludeTableInfo includeTableInfo = new IncludeTableInfo();
		for (int i = 0; i < items.length; i++) {
			includeTableInfo.add (new IncludeTableItemInfo (items[i]));			
		}
		LogHelper.info("number of included/excluded tables "+includeTableInfo.getSize());
		return includeTableInfo;
	}

	private DB getDB (String scenario) {
		LogHelper.info ("Connecting to database using "+scenario);
		DB db = new DB (CONFIG_FILE);
		if (! db.getConnection (scenario)) {
			LogHelper.info ("giving up...");
			System.exit(1);
		}
//		db.setDebug(true);
		return db;
	}
	private void closeDB (DB db) {
		LogHelper.info ("Disconnecting from database");
		db.disConnect();
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
	@SuppressWarnings("unused")
	private void showTableInfo (ArrayList<TableInfo> tableInfo) {
		Iterator<TableInfo> iterator = tableInfo.iterator();
		while (iterator.hasNext()) {
			TableInfo info = (TableInfo) iterator.next();
			info.doShowInfo();
		}
	}
}
