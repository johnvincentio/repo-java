package com.idc.sql.app;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import com.idc.sql.lib.DB;
import com.idc.sql.lib.IncludeTableInfo;
import com.idc.sql.lib.Scenarios;
import com.idc.trace.LogHelper;

public class AppFK {

	public static void main (String[] args) {
		(new AppFK()).doTestFKHercdb();
	}

	private void handleExportedKeys (DatabaseMetaData metaData, String schema, String table) {
		ResultSet resultSet = null;
		try {
			resultSet = metaData.getExportedKeys (null, schema, table);
			int count = 0;
			while (resultSet.next()) {
				++count;
			}
			if (count > 0) System.out.println("Exported; count "+count+" schema "+schema+" table "+table);
		}
		catch (SQLException sqlex) {
			System.err.println("schema "+schema+" table "+table);
			System.err.println("SQLException; "+sqlex.getErrorCode()+", "+sqlex.getMessage()+", "+sqlex.getSQLState());
		}
		catch (Exception ex) {
			System.err.println("schema "+schema+" table "+table);
			System.err.println("Exception; "+ex.getMessage());
		}
		finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				}
				catch (SQLException e) {}
			}
		}
	}
	private void handleImportedKeys (DatabaseMetaData metaData, String schema, String table) {
		ResultSet resultSet = null;
		try {
			resultSet = metaData.getImportedKeys (null, schema, table);
			int count = 0;
			while (resultSet.next()) {
				++count;
			}
			if (count > 0) System.out.println("Imported; count "+count+" schema "+schema+" table "+table);
		}
		catch (SQLException sqlex) {
			System.err.println("schema "+schema+" table "+table);
			System.err.println("SQLException; "+sqlex.getErrorCode()+", "+sqlex.getMessage()+", "+sqlex.getSQLState());
		}
		catch (Exception ex) {
			System.err.println("schema "+schema+" table "+table);
			System.err.println("Exception; "+ex.getMessage());
		}
		finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				}
				catch (SQLException e) {}
			}
		}
	}
	private void doTestFKHercdb() {
		System.out.println(">>> doTestFKHercdb");
		String scenario = "irac";
		LogHelper.info("Connecting to database using "+scenario);
		DB db = new DB("c:/jv/utils/dbtoolgui.xml");
		if (! db.getConnection (scenario)) {
			LogHelper.info("giving up...");
			System.exit(1);
		}
		db.setDebug(false);

		Scenarios scenarios = db.getScenarios();
		String schema = scenarios.getScenario (scenario).getSchema();
		System.out.println("schema "+schema);

//		String schema = "DB2INST1";
//		String table = "ERROR_TRANSLATION";
		ResultSet resultSet = null;
		ResultSetMetaData rsmd = null;

		ArrayList<String> list = db.getTables (new IncludeTableInfo(), true);

		try {
			DatabaseMetaData metaData = db.getDBConnection().getMetaData();
			for (String table : list) {
				handleExportedKeys (metaData, schema, table);
				handleImportedKeys (metaData, schema, table);
//				System.out.println("schema "+schema+" table "+table);
/*
				rsmd = resultSet.getMetaData();
				int cols = rsmd.getColumnCount();
				LogHelper.info("cols "+cols);
*/
/*
				for (int num=0; num < cols; num++) {
					System.out.println((num + 1) + ", " +
							rsmd.getColumnName(num+1) + ", " +
							rsmd.getColumnType(num+1) + ", " +
							rsmd.getColumnDisplaySize(num+1));
				}
*/
		
/*
				while (resultSet.next()) {
					String tableName = resultSet.getString("FKCOLUMN_NAME");
					String tableType = resultSet.getString("PKCOLUMN_NAME");
					System.out.println("tableName "+tableName);
					System.out.println("tableType "+tableType);
				}
				break;
*/
			}
		}
		catch (SQLException sqlex) {
			System.err.println("SQLException; "+sqlex.getErrorCode()+", "+sqlex.getMessage()+", "+sqlex.getSQLState());
		}
		catch (Exception ex) {
			System.err.println("Exception; "+ex.getMessage());
		}
		finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				}
				catch (SQLException e) {}
			}
			rsmd = null;
			LogHelper.info("Disconnecting from database");
			db.disConnect();
			db = null;
		}
		LogHelper.info("exiting...");
		System.out.println("<<< doTestFKHercdb");
	}
}

/*			
1, PKTABLE_CAT, 12, 128
2, PKTABLE_SCHEM, 12, 128
3, PKTABLE_NAME, 12, 128
4, PKCOLUMN_NAME, 12, 128
5, FKTABLE_CAT, 12, 128
6, FKTABLE_SCHEM, 12, 128
7, FKTABLE_NAME, 12, 128
8, FKCOLUMN_NAME, 12, 128
9, KEY_SEQ, 5, 6
10, UPDATE_RULE, 5, 6
11, DELETE_RULE, 5, 6
12, FK_NAME, 12, 128
13, PK_NAME, 12, 128
14, DEFERRABILITY, 5, 6
*/

/*
exportedKeys
count 1 schema DB2INST1 table EMAIL_QUEUE
count 3 schema DB2INST1 table IWOD_DEPLOYMENTS
count 3 schema DB2INST1 table IWOD_LEGS
count 2 schema DB2INST1 table IWOD_RCVR_DEPLOYMENTS
count 1 schema DB2INST1 table IWOD_REPORTS
count 2 schema DB2INST1 table LOCAMENITIES
count 2 schema DB2INST1 table LOCCITIES
count 4 schema DB2INST1 table LOCCOUNTRIES
count 2 schema DB2INST1 table LOCGENRES
count 7 schema DB2INST1 table LOCLOCATIONS
count 2 schema DB2INST1 table LOCRESCENTERS
count 1 schema DB2INST1 table LOCRESTRICTIONS
count 2 schema DB2INST1 table LOCSTATES
count 1 schema DB2INST1 table VIRTUAL_URL
*/

/*
package my_package;

import java.sql.*;
import java.util.*;

public class Main_SearchAllTablesForFieldname {

  String colNameToSearchFor = "part_no";
  
  String catalog = null;
  String schema = null;
  List listOfTables = new ArrayList();

  public static void main(String[] args)
  {
    new Main_SearchAllTablesForFieldname();
  }

  public Main_SearchAllTablesForFieldname()
  {
    try
    {
      // connect with the jdbc odbc bridge driver
      Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

      // the jdbc odbc connection string
      Connection con = DriverManager.getConnection("jdbc:odbc:DB_NAME", "", "");

      // get the database metadata
      DatabaseMetaData dmd = con.getMetaData();

      // get a list of all tables
      getListOfAllTables(listOfTables, dmd);

      // see if you can find the column name in any tables
      searchForColumnNameInTables(dmd);
    }
    catch (Exception e)
    {
      System.err.println("exception: " + e.getMessage());
    }
  }

  private void searchForColumnNameInTables(DatabaseMetaData dmd) 
  throws SQLException {
    Iterator iter = listOfTables.iterator();
    while (iter.hasNext()) {
      String tableName = (String) iter.next();
      java.sql.ResultSet rs = dmd.getColumns(catalog, schema, tableName, "%");
      while (rs.next()) {
        String colName = rs.getString(4);
        if (colName.trim().toLowerCase().equals(colNameToSearchFor)){
          System.out.println("found '" + colNameToSearchFor + "' in " + tableName );
        }
      }
    }
  }

  private void getListOfAllTables(List listOfTables, DatabaseMetaData dmd) 
  throws SQLException {
    String[] tableTypes = {
        "TABLE",
        "VIEW",
        "ALIAS",
        "SYNONYM",
        "GLOBAL TEMPORARY",
        "LOCAL TEMPORARY",
        "SYSTEM TABLE"};
    ResultSet rs = dmd.getTables(catalog, schema, "%", tableTypes);

    while (rs.next()) {
      String tableName = rs.getString(3);
      listOfTables.add(tableName);
    }
    rs.close();
  }

}
1, PKTABLE_CAT, 12, 128
2, PKTABLE_SCHEM, 12, 128
3, PKTABLE_NAME, 12, 128
4, PKCOLUMN_NAME, 12, 128
5, FKTABLE_CAT, 12, 128
6, FKTABLE_SCHEM, 12, 128
7, FKTABLE_NAME, 12, 128
8, FKCOLUMN_NAME, 12, 128
9, KEY_SEQ, 5, 6
10, UPDATE_RULE, 5, 6
11, DELETE_RULE, 5, 6
12, FK_NAME, 12, 128
13, PK_NAME, 12, 128
14, DEFERRABILITY, 5, 6

*/
