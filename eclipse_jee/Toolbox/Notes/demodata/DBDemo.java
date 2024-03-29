package com.idc.sql.app.old;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.sql.lib.ColumnInfo;
import com.idc.sql.lib.DataInfo;
import com.idc.sql.lib.IndexItemInfo;
import com.idc.sql.lib.IndexesInfo;
import com.idc.sql.lib.JVxml;
import com.idc.sql.lib.RowInfo;
import com.idc.sql.lib.RowsInfo;
import com.idc.sql.lib.Scenario;
import com.idc.sql.lib.Scenarios;
import com.idc.sql.lib.TableInfo;
import com.idc.sql.lib.Utils;
import com.idc.trace.LogHelper;

public class DBDemo {
	private String m_strConfigurationFile = "c:/jv/utils/dbtoolgui.xml";
	private Scenarios scenarios;
	private Scenario scenario;
	private Connection m_connection;
	private boolean m_bConnected = false;
	private static final String[] m_strType = {"TABLE"};
	private boolean m_bDebug = true;

	public DBDemo() {
		scenarios = (new JVxml()).parse (new File (m_strConfigurationFile));
	}
	public DBDemo (String configurationFile) {
		scenarios = (new JVxml()).parse (new File (configurationFile));
	}

	public Scenarios getScenarios() {return scenarios;}

	public Connection getDBConnection() {return m_connection;}
	public boolean getConnection (String name) {
		scenario = scenarios.getScenario(name);
		String dbdriver = scenario.getDriver();
		String url = scenario.getUrl();
		String database = scenario.getDatabasename();
		String username = scenario.getUsername();
		String password = scenario.getPassword();
		String schema = scenario.getSchema();
		LogHelper.info("dbdriver "+dbdriver);
		LogHelper.info("url "+url);
		LogHelper.info("database "+database);
		LogHelper.info("username "+username);
//		LogHelper.info("password "+password);
		LogHelper.info("schema "+schema);
		String connectionUrl;
		if (scenario.isAS400())
			connectionUrl = url + database + "/" + schema + ";naming=sql;errors=full";
		else 
			connectionUrl = url + database;
		LogHelper.info("connectionUrl "+connectionUrl);
		try {
			DriverManager.registerDriver((Driver)Class.forName(dbdriver).newInstance());
		} 
		catch (Exception e) {
			LogHelper.error(e.getMessage());
			return m_bConnected;
		}
		try {
		 	m_connection = DriverManager.getConnection(connectionUrl, username, password);
		} catch (SQLException ex) {
			LogHelper.error("could not get connection");
			return false;
		}
	 	m_bConnected = true;
	 	return true;	 	
	}
	public void disConnect() {
		try {
			if (m_bConnected) {
				m_connection.close();
			}
			return;
		} catch (SQLException ex) {
			LogHelper.error("could not get connection");
		}
	}

	public ArrayList getTables() {
		if (m_bDebug) LogHelper.info(">>> getTables; schema :"+scenario.getSchema()+":");
		ResultSet resultSet = null;
		ArrayList<String> list = new ArrayList<String>();
		String tableName;
		try {
			DatabaseMetaData metaData = m_connection.getMetaData();
			LogHelper.info ("DB2 version: " + metaData.getDatabaseProductVersion());

			resultSet = metaData.getTables (null, scenario.getSchema(), null, m_strType);
			while (resultSet.next()) {
				tableName = resultSet.getString("TABLE_NAME");
				if (m_bDebug) LogHelper.info("tableName :"+tableName+":");
				if (isIncludeTable (tableName)) list.add(tableName);
			}
		}
		catch (SQLException sqlException) {
			LogHelper.error("trouble in getTables; "+sqlException.getMessage());
			System.exit(1);
		}
		finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				}
				catch (SQLException e) {}
			}
		}
		if (m_bDebug) LogHelper.info("<<< getTables");
		return list;
	}	

	public ArrayList getPrimaryKeys (String tableName) {
		ResultSet resultSet = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			DatabaseMetaData metaData = m_connection.getMetaData();
			resultSet = metaData.getPrimaryKeys (null, scenario.getSchema(), tableName);
			while (resultSet.next()) {
				String columnName = resultSet.getString("COLUMN_NAME");
				if (m_bDebug) LogHelper.info("columnName :"+columnName+":");
				list.add (columnName);
			}
		}
		catch (SQLException sqlException) {
			LogHelper.error("trouble in getPrimaryKeys "+sqlException.getMessage()+" "+
					sqlException.getErrorCode()+" "+sqlException.getSQLState());
			System.exit(1);
		}
		finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				}
				catch (SQLException e) {}
			}
		}
		return list;
	}

	public IndexesInfo getIndexes (String tableName) {
		IndexesInfo indexesInfo = new IndexesInfo();
		ResultSet resultSet = null;
		try {
			DatabaseMetaData metaData = m_connection.getMetaData();
			resultSet = metaData.getIndexInfo (null, scenario.getSchema(), tableName, false, true);
			while (resultSet.next()) {
				indexesInfo.add (resultSet.getString("INDEX_NAME"),
						resultSet.getBoolean("NON_UNIQUE"),
						new IndexItemInfo (
								resultSet.getString("COLUMN_NAME"),
								resultSet.getInt("ORDINAL_POSITION"),
								resultSet.getString("ASC_OR_DESC").equals ("D") ? false : true));
			}
		}
		catch (SQLException sqlException) {
			LogHelper.error("trouble in getIndexes "+sqlException.getMessage()+" "+
					sqlException.getErrorCode()+" "+sqlException.getSQLState());
			System.exit(1);
		}
		finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				}
				catch (SQLException e) {}
			}
		}
		return indexesInfo;
	}

	public ArrayList getColumns (String tableName) {
		String name;
		ColumnInfo info;
		ResultSet resultSet = null;
		ArrayList<ColumnInfo> list = new ArrayList<ColumnInfo>();		
		try {
			DatabaseMetaData metaData = m_connection.getMetaData();
			resultSet = metaData.getColumns(null, scenario.getSchema(), tableName, null);
			while (resultSet.next()) {
				info = new ColumnInfo();
				name = resultSet.getString("COLUMN_NAME");				
				info.setName(name);
				info.setType(resultSet.getInt("DATA_TYPE"));
				if (info.getType() == Types.CHAR || info.getType() == Types.VARCHAR)
					info.setLen(resultSet.getInt("COLUMN_SIZE"));
				info.setNullable(resultSet.getInt("NULLABLE"));
				list.add(info);				
			}
		}
		catch (SQLException sqlException) {
			LogHelper.error("trouble in getColumns "+sqlException.getMessage()+" "+
					sqlException.getErrorCode()+" "+sqlException.getSQLState());
			System.exit(1);
		}
		finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				}
				catch (SQLException e) {}
			}
		}
		return list;
	}
	public RowsInfo getRows (TableInfo tableInfo, ArrayList columns) {
		RowsInfo rowsInfo = new RowsInfo();
		StringBuffer buf = new StringBuffer();
		buf.append ("select ");

		int last_col = columns.size() - 1;
		for (int j = 0; j < columns.size(); j++) {
			ColumnInfo columnInfo = (ColumnInfo) columns.get(j);
			buf.append (columnInfo.getName());
			if (j < last_col) buf.append(",");
		}
		buf.append (" from ").append (scenario.getSchema()).append(".").append (tableInfo.getName());
		buf.append (" ").append(tableInfo.getOrderByClause());
		if (m_bDebug) LogHelper.info ("Stmt :"+buf.toString()+":");

		PreparedStatement ps = null;
		ResultSet rs = null;
		String value, str;
		try {
			ps = m_connection.prepareStatement(buf.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
//				if (m_bDebug) LogHelper.info("found a record");
				RowInfo rowInfo = new RowInfo();
				for (int j = 0; j < columns.size(); j++) {
					value = "";
					ColumnInfo columnInfo = (ColumnInfo) columns.get(j);
					if (columnInfo.getType() == Types.CHAR) {
						value = "'"+rs.getString(j+1)+"'";
					}
					else if (columnInfo.getType() == Types.VARCHAR) {
						str = rs.getString(j+1);
						if (! (str == null || str.length() < 1))
							value = "'"+Utils.cleanForSql(str)+"'";
						else
							value = "''";
					}
					else if (columnInfo.getType() == Types.CLOB) {
						java.sql.Clob myClob = rs.getClob(j+1);
						if (myClob == null)
							value = "''";
						else {
//							LogHelper.info("clob length "+myClob.length());
							str = myClob.getSubString(1, (int) myClob.length());
//							LogHelper.info("str value :"+str+":");
							if (! (str == null || str.length() < 1))
								value = "'"+Utils.cleanForSql(str)+"'";
							else
								value = "''";
//							LogHelper.info("clob value :"+value+":");
							myClob = null;
						}
					}
					else if (columnInfo.getType() == Types.BLOB) {
						java.sql.Blob myBlob = rs.getBlob(j+1);
						value = "null";
						if (myBlob != null)
							LogHelper.info("Found blob not null value; table "+tableInfo.getName()+" column "+columnInfo.getName());
					}
					else if (columnInfo.getType() == Types.SMALLINT)
						value = Integer.toString(rs.getInt(j+1));
					else if (columnInfo.getType() == Types.INTEGER)
						value = Integer.toString(rs.getInt(j+1));
					else if (columnInfo.getType() == Types.BIGINT)
						value = Long.toString(rs.getLong(j+1));
					else if (columnInfo.getType() == Types.FLOAT) {
						LogHelper.info("Found float, table "+tableInfo.getName()+" column "+columnInfo.getName());
						System.exit(1);
					}
					else if (columnInfo.getType() == Types.DOUBLE) {
						LogHelper.info("Found double, table "+tableInfo.getName()+" column "+columnInfo.getName());
						System.exit(1);
					}
					else if (columnInfo.getType() == Types.DECIMAL) {
						value = rs.getBigDecimal(j+1).toString();
					}
					else if (columnInfo.getType() == Types.NUMERIC) {
						value = rs.getBigDecimal(j+1).toString();
					}
					else if (columnInfo.getType() == Types.DATE) {
						java.util.Date date = rs.getDate(j+1);
						if (date == null)
							value = "null";
						else
							value = "'"+Utils.getDateString(date)+"'";
						date = null;
					}
					else if (columnInfo.getType() == Types.TIMESTAMP) {
						java.sql.Timestamp timestamp = rs.getTimestamp(j+1);
						if (timestamp == null)
							value = "null";
						else
							value = "'"+timestamp+"'";
						timestamp = null;
					}
					else if (columnInfo.getType() == Types.TIME) {
						java.sql.Time time = rs.getTime(j+1);
						if (time == null)
							value = "null";
						else
							value = "'"+Utils.getDateString(time)+"'";
						time = null;
					}
					else
						LogHelper.error("unknown type "+columnInfo.getType()+" column "+columnInfo.getName()+
							" in table "+tableInfo.getName());
					rowInfo.add (new DataInfo(value));
				}
//				if (m_bDebug) LogHelper.info("row:"+rowInfo.toString()+":");	
				rowsInfo.add (rowInfo);
			}
//			LogHelper.info("rows:"+rowsInfo.toString()+":");
		}
		catch (SQLException sqlException) {
			LogHelper.error("***** trouble in getRows; "+sqlException.getMessage());
			LogHelper.error("Could not get rows for table "+tableInfo.getName());
		}
		finally {
			buf = null;
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				}
				catch (SQLException e) {}
			}
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				}
				catch (SQLException e) {}
			}
		}
		return rowsInfo;
	}

	public ArrayList makeTableInfo (ArrayList<TableInfo> allTableInfo) {
		LogHelper.info("Getting Index info");
		TableInfo tableInfo;
		Iterator iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			tableInfo = (TableInfo) iterator.next();
			tableInfo = getTable (allTableInfo, tableInfo.getName());
			tableInfo.addIndexlist (getIndexes (tableInfo.getName()));	// get index info
		}

		LogHelper.info("Getting Primary Key and Column info");
		iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			tableInfo = (TableInfo) iterator.next();
			tableInfo = getTable (allTableInfo, tableInfo.getName());
			tableInfo.addPklist (getPrimaryKeys (tableInfo.getName()));	// get primary key info
			tableInfo.addColumnlist (getColumns (tableInfo.getName()));	// get column info
		}

		LogHelper.info("Rationalizing primary key info");
		Iterator iter1;
		String column;

		for (int i=0; i<allTableInfo.size(); i++) {
			tableInfo = (TableInfo) allTableInfo.get(i);
			iter1 = tableInfo.getPklist().iterator();
			while (iter1.hasNext()) {
				column = (String) iter1.next();
//				LogHelper.info("Primary key: "+column);
				ColumnInfo columnInfo = tableInfo.getColumn(column);
				columnInfo.setPk(true);
			}
		}

		return allTableInfo;
	}
	public TableInfo getTable(ArrayList<TableInfo> allTableInfo, String name) {
		TableInfo info;
		Iterator iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			info = (TableInfo) iterator.next();
			if ((info.getName()).equals(name)) return info;
		}
		return null;
	}

	public boolean isIncludeTable (String str) {
		return isIncludeSystemData (str);
//		return isIncludeRMData (str);
//		return isIncludeTesting (str);
	}

	@SuppressWarnings("unused")
	private boolean isIncludeSystemData (String str) {
		if (str.equalsIgnoreCase("EMAIL_ADDRESS")) return true;
		if (str.equalsIgnoreCase("EMAIL_ATTACHMENT")) return true;
		if (str.equalsIgnoreCase("EMAIL_CATEGORY")) return true;
		if (str.equalsIgnoreCase("EMAIL_DEFINITION")) return true;
		if (str.equalsIgnoreCase("EMAIL_DEFINITION_ENUS")) return true;
		if (str.equalsIgnoreCase("EMAIL_FIXED_ATTACHMENT")) return true;
		if (str.equalsIgnoreCase("EMAIL_QUEUE")) return true;
		if (str.equalsIgnoreCase("BRANCHLOCATIONS")) return true;
		if (str.equalsIgnoreCase("BRANCHINFO")) return true;
		if (str.equalsIgnoreCase("RENTALEQUIPMENT")) return true;
		if (str.equalsIgnoreCase("ITEMDETAILS")) return true;
		if (str.equalsIgnoreCase("HOMEPAGECATEGORIES")) return true;
		if (str.equalsIgnoreCase("MAKESMODELS")) return true;
		if (str.equalsIgnoreCase("RENTALRATES")) return true;
		if (str.equalsIgnoreCase("SALESITEMS")) return true;
		if (str.equalsIgnoreCase("HOTDEALS")) return true;
		if (str.equalsIgnoreCase("FMADEALS")) return true;
		return false;
	}
	@SuppressWarnings("unused")
	private boolean isIncludeMemberData (String str) {
		if (str.equalsIgnoreCase("LDAPMembers")) return true;
		if (str.equalsIgnoreCase("Companies")) return true;
		if (str.equalsIgnoreCase("CompanyAccounts")) return true;
		if (str.equalsIgnoreCase("Members")) return true;
		if (str.equalsIgnoreCase("MemberAccounts")) return true;
		if (str.equalsIgnoreCase("MemberLocations")) return true;
		if (str.equalsIgnoreCase("MemberPreferences")) return true;
		if (str.equalsIgnoreCase("MemberRequests")) return true;
		if (str.equalsIgnoreCase("MemberRequestsItems")) return true;
		if (str.equalsIgnoreCase("MemberHistory")) return true;
		return false;
	}
	@SuppressWarnings("unused")
	private boolean isIncludeRMData (String str) {
		if (str.equalsIgnoreCase("RMNARPAccountsUS")) return true;
		if (str.equalsIgnoreCase("RMNARPAccountsCA")) return true;
		if (str.equalsIgnoreCase("RMAccountsUS")) return true;
		if (str.equalsIgnoreCase("RMAccountsCA")) return true;
		if (str.equalsIgnoreCase("RMContractLocationsUS")) return true;
		if (str.equalsIgnoreCase("RMContractLocationsCA")) return true;
		if (str.equalsIgnoreCase("RMContractRentalEquipmentUS")) return true;
		if (str.equalsIgnoreCase("RMContractRentalEquipmentCA")) return true;
		return false;
	}
	@SuppressWarnings("unused")
	private boolean isIncludeTesting (String str) {
		if (str.equalsIgnoreCase("JVC")) return true;
		return false;
	}
}
