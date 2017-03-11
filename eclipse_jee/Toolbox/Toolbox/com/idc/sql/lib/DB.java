package com.idc.sql.lib;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idc.trace.LogHelper;
import com.idc.utils.UtilHelper;

public class DB {
	private Scenarios m_scenarios;
	private Scenario m_scenario;
	private Connection m_connection;
	private boolean m_bConnected = false;
	private boolean m_bDebug = false;

	public DB (String configurationFile) {
		m_scenarios = (new JVxml()).parse (new File (configurationFile));
	}

	public void setDebug (boolean debug) {m_bDebug = debug;}

	public Scenarios getScenarios() {return m_scenarios;}

	private void showSortedMap (Map<String, String> map) {
		Iterator<Map.Entry<String, String>> keyValuePairs = map.entrySet().iterator();
		for (int i = 0; i < map.size(); i++) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) keyValuePairs.next();
			LogHelper.debug ("Map: "+entry.getKey() + "=" + entry.getValue());
		}
	}

	public Connection getDBConnection() {return m_connection;}
	public boolean getConnection (String name) {
		m_scenario = m_scenarios.getScenario(name);
		String dbdriver = m_scenario.getDriver();
		String url = m_scenario.getUrl();
		String database = m_scenario.getDatabasename();
		String username = m_scenario.getUsername();
		String password = m_scenario.getPassword();
		String schema = m_scenario.getSchema();
		LogHelper.info("dbdriver "+dbdriver);
		LogHelper.info("url "+url);
		LogHelper.info("database "+database);
		LogHelper.info("username "+username);
//		LogHelper.info("password "+password);
		LogHelper.info("schema "+schema);
		String connectionUrl;

		Map<String, String> map = new HashMap<String, String> (System.getenv());
		showSortedMap (map);
		if (m_scenario.isAS400())
			connectionUrl = url + database + "/" + schema + ";naming=sql;errors=full";
		else if (m_scenario.isSQLServer())
			connectionUrl = url + "//" + database;
		else if (m_scenario.isMySQL())
			connectionUrl = url + "//" + database + "/" + schema;
		else 
			connectionUrl = url + database;
		LogHelper.info("connectionUrl "+connectionUrl);
		try {
			DriverManager.registerDriver((Driver)Class.forName(dbdriver).newInstance());
			LogHelper.info("driver registered");
		} 
		catch (Exception e) {
			LogHelper.error(e.getMessage());
			return m_bConnected;
		}
		try {
		 	m_connection = DriverManager.getConnection(connectionUrl, username, password);
		} catch (SQLException ex) {
			System.out.println("Exception; "+ex.getMessage());
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

	public ArrayList<String> getTables (IncludeTableInfo includeTableInfo, boolean includeType) {
		if (m_bDebug) LogHelper.info(">>> getTables; schema :"+m_scenario.getSchema()+": includeType "+includeType);
		ResultSet resultSet = null;
		ArrayList<String> list = new ArrayList<String>();
//		String[] m_strType = {"TABLE"};
		try {
			DatabaseMetaData metaData = m_connection.getMetaData();
			if (m_bDebug) LogHelper.info ("Database version: " + metaData.getDatabaseProductVersion());

			String schema = m_scenario.isSQLServer() ? null : m_scenario.getSchema();
			resultSet = metaData.getTables (null, schema, null, null);
//			resultSet = metaData.getTables (null, schema, null, m_strType);
			if (m_bDebug) LogHelper.info ("Retrieved Tables metadata");

			while (resultSet.next()) {
				String tableName = resultSet.getString("TABLE_NAME");
				String tableType = resultSet.getString("TABLE_TYPE");
				if (tableType != null && tableType.equalsIgnoreCase("TABLE")) {
					if (m_bDebug) LogHelper.info("tableName :"+tableName+": tableType :"+tableType+":");
					if (includeType) {
						if (isIncludeTable (tableName, includeTableInfo)) list.add(tableName);
					}
					else {
						if (! isIncludeTable (tableName, includeTableInfo)) list.add(tableName);
					}
				}
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
		if (m_bDebug) LogHelper.info("<<< getTables; Number of tables "+list.size());
		return list;
	}	

	public ArrayList<String> getViews (IncludeTableInfo includeTableInfo) {
		if (m_bDebug) LogHelper.info(">>> getViews; schema :"+m_scenario.getSchema()+":");
		ResultSet resultSet = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			DatabaseMetaData metaData = m_connection.getMetaData();

			String schema = m_scenario.isSQLServer() ? null : m_scenario.getSchema();
			resultSet = metaData.getTables (null, schema, null, null);

			while (resultSet.next()) {
				String tableName = resultSet.getString("TABLE_NAME");
				String tableType = resultSet.getString("TABLE_TYPE");
				if (tableType != null && tableType.equalsIgnoreCase("VIEW")) {
					if (m_bDebug) LogHelper.info("tableName :"+tableName+": tableType :"+tableType+":");
					if (isIncludeTable (tableName, includeTableInfo)) list.add(tableName);
				}
			}
		}
		catch (SQLException sqlException) {
			LogHelper.error("trouble in getViews; "+sqlException.getMessage());
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
		if (m_bDebug) LogHelper.info("<<< getViews; Number of views "+list.size());
		return list;
	}

	private boolean isIncludeTable (String tableName, IncludeTableInfo includeTableInfo) {
		if (includeTableInfo.getSize() < 1) return true;
		for (Iterator<IncludeTableItemInfo> iter = includeTableInfo.getItems(); iter.hasNext(); ) {
			IncludeTableItemInfo includeTableItemInfo = (IncludeTableItemInfo) iter.next();
			if (tableName.equalsIgnoreCase (includeTableItemInfo.getName())) return true;
		}
		return false;
	}

	public ArrayList<String> getPrimaryKeys (String tableName) {
		ResultSet resultSet = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			DatabaseMetaData metaData = m_connection.getMetaData();
			String schema = m_scenario.isSQLServer() ? null : m_scenario.getSchema();
			resultSet = metaData.getPrimaryKeys (null, schema, tableName);
			if (m_bDebug) LogHelper.info ("Retrieved Primary Keys metadata");
			while (resultSet.next()) {
				String columnName = resultSet.getString("COLUMN_NAME");
				if (m_bDebug) LogHelper.info("primary keys; columnName :"+columnName+":");
				list.add (columnName);
			}
		}
		catch (SQLException sqlException) {
			LogHelper.error("trouble in getPrimaryKeys "+sqlException.getMessage()+" "+sqlException.getErrorCode()+" "+sqlException.getSQLState());
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
		if (m_bDebug) LogHelper.info(">>> DB:getIndexes");
		IndexesInfo indexesInfo = new IndexesInfo();
		ResultSet resultSet = null;
//		System.out.println("DatabaseMetaData.tableIndexStatistic "+DatabaseMetaData.tableIndexStatistic);	// 0
//		System.out.println("DatabaseMetaData.tableIndexClustered "+DatabaseMetaData.tableIndexClustered);	// 1
//		System.out.println("DatabaseMetaData.tableIndexHashed "+DatabaseMetaData.tableIndexHashed);			// 2
//		System.out.println("DatabaseMetaData.tableIndexOther "+DatabaseMetaData.tableIndexOther);			// 3
		try {
			DatabaseMetaData metaData = m_connection.getMetaData();
			String schema = m_scenario.isSQLServer() ? null : m_scenario.getSchema();
			if (m_bDebug) LogHelper.info("schema :"+schema+": tableName :"+tableName+":");
			resultSet = metaData.getIndexInfo (null, schema, tableName, false, true);
			if (m_bDebug) LogHelper.info ("Retrieved Indexes metadata");
			while (resultSet.next()) {
				String index = resultSet.getString("INDEX_NAME");
				if (index == null) continue;
				boolean unique = resultSet.getBoolean("NON_UNIQUE");
				String column = resultSet.getString("COLUMN_NAME");
				if (column == null) continue;
				int position = resultSet.getInt("ORDINAL_POSITION");
				boolean bOrder = true;
				String tmp = resultSet.getString("ASC_OR_DESC");
				if (tmp != null && tmp.equalsIgnoreCase("D")) bOrder = false;

				indexesInfo.add (index, unique, new IndexItemInfo (column, position, bOrder));
			}
		}
		catch (SQLException sqlException) {
			LogHelper.error("trouble in getIndexes "+sqlException.getMessage()+" "+sqlException.getErrorCode()+" "+sqlException.getSQLState());
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
		if (m_bDebug) LogHelper.info("<<< DB:getIndexes");
		return indexesInfo;
	}

	public ArrayList<ColumnInfo> getColumns (String tableName) {
		String name;
		ColumnInfo info;
		ResultSet resultSet = null;
		ArrayList<ColumnInfo> list = new ArrayList<ColumnInfo>();		
		try {
			DatabaseMetaData metaData = m_connection.getMetaData();
			String schema = m_scenario.isSQLServer() ? null : m_scenario.getSchema();
			resultSet = metaData.getColumns(null, schema, tableName, null);
			if (m_bDebug) LogHelper.info ("Retrieved Columns metadata");
			while (resultSet.next()) {
				info = new ColumnInfo();
				name = resultSet.getString("COLUMN_NAME");				
				info.setName(name);
				//DECIMAL_DIGITS
				info.setType(resultSet.getInt("DATA_TYPE"));
				if (info.getType() == Types.CHAR || info.getType() == Types.VARCHAR 
						|| info.getType() == Types.NUMERIC || info.getType() == Types.DECIMAL)
					info.setLen(resultSet.getInt("COLUMN_SIZE"));
				if (info.getType() == Types.DECIMAL)
					info.setScale(resultSet.getInt("DECIMAL_DIGITS"));
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

	public void makeSQLForRows (Output output, String toSchema, TableInfo tableInfo) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = m_connection.prepareStatement (getFetchSQL (tableInfo));
			rs = ps.executeQuery();
			int row_cntr = 0;
			while (rs.next()) {
				row_cntr++;
				if (m_bDebug) LogHelper.info("found a record; row_cntr "+row_cntr);
				RowInfo rowInfo = getRow (tableInfo, rs);
//				if (m_bDebug) LogHelper.info("row:"+rowInfo.toString()+":");
				MakeSQL.makeCreateInsert (output, rowInfo, toSchema, tableInfo);
			}
		}
		catch (SQLException sqlException) {
			LogHelper.error("***** trouble in getRows; "+sqlException.getMessage());
			LogHelper.error("Could not get rows for table "+tableInfo.getName());
		}
		finally {
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
	}

	public RowsInfo getRows (TableInfo tableInfo) {
		RowsInfo rowsInfo = new RowsInfo();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = m_connection.prepareStatement (getFetchSQL (tableInfo));
			rs = ps.executeQuery();
			int row_cntr = 0;
			while (rs.next()) {
				row_cntr++;
				if (m_bDebug) LogHelper.info("found a record; row_cntr "+row_cntr);
				RowInfo rowInfo = getRow (tableInfo, rs);
				if (m_bDebug) LogHelper.info("row:"+rowInfo.toString()+":");	
				rowsInfo.add (rowInfo);
			}
//			LogHelper.info("rows:"+rowsInfo.toString()+":");
		}
		catch (SQLException sqlException) {
			LogHelper.error("***** trouble in getRows; "+sqlException.getMessage());
			LogHelper.error("Could not get rows for table "+tableInfo.getName());
		}
		finally {
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

	private String getFetchSQL (TableInfo tableInfo) {
		StringBuffer buf = new StringBuffer();
		if (tableInfo.isSelect()) {
			buf.append (tableInfo.getSelect());
		}
		else {
			buf.append ("select ");

			ArrayList<ColumnInfo> columns = tableInfo.getCollist();
			int last_col = columns.size() - 1;
			for (int j = 0; j < columns.size(); j++) {
				ColumnInfo columnInfo = (ColumnInfo) columns.get(j);
				buf.append (columnInfo.getName());
				if (j < last_col) buf.append(",");
			}
			buf.append (" from ");
			if (! m_scenario.isSQLServer()) buf.append (m_scenario.getSchema()).append(".");
			buf.append (tableInfo.getName());
		}
		if (tableInfo.isClause()) buf.append (" ").append(tableInfo.getClause());
		
		buf.append (" ").append(tableInfo.getOrderByClause());
		if (m_bDebug) LogHelper.info ("Stmt :"+buf.toString()+":");
		return buf.toString();
	}
/*
	if (tableInfo.isClause()) {
		buf.append (", ");
		if (! m_scenario.isSQLServer()) buf.append (m_scenario.getSchema()).append(".");
		buf.append (tableInfo.getJoin());
	}
*/
	private RowInfo getRow (TableInfo tableInfo, ResultSet rs) throws SQLException {
		RowInfo rowInfo = new RowInfo();
		String value, str;
		ArrayList<ColumnInfo> columns = tableInfo.getCollist();
		for (int j = 0; j < columns.size(); j++) {
			value = "";
			ColumnInfo columnInfo = (ColumnInfo) columns.get(j);
			if (columnInfo.getType() == Types.CHAR) {
				str = rs.getString(j+1);
				if (! (str == null || str.length() < 1))
					value = "'"+UtilHelper.cleanForSql(str)+"'";
				else
					value = "''";
			}
			else if (columnInfo.getType() == Types.VARCHAR) {
				str = rs.getString(j+1);
				if (! (str == null || str.length() < 1))
					value = "'"+UtilHelper.cleanForSql(str)+"'";
				else
					value = "''";
			}
			else if (columnInfo.getType() == Types.CLOB) {
				java.sql.Clob myClob = rs.getClob(j+1);
				if (myClob == null)
					value = "''";
				else {
//					LogHelper.info("clob length "+myClob.length());
					str = myClob.getSubString(1, (int) myClob.length());
//					LogHelper.info("str value :"+str+":");
					if (! (str == null || str.length() < 1))
						value = "'"+UtilHelper.cleanForSql(str)+"'";
					else
						value = "''";
//					LogHelper.info("clob value :"+value+":");
					myClob = null;
				}
			}
			else if (columnInfo.getType() == Types.BLOB) {
				java.sql.Blob myBlob = rs.getBlob(j+1);
				value = "null";
				if (myBlob != null)
					LogHelper.info("Found blob not null value; table "+tableInfo.getName()+" column "+columnInfo.getName());
			}
			else if (columnInfo.getType() == Types.LONGVARBINARY) {
				byte[] bytes = rs.getBytes(j+1);
				if (bytes != null && bytes.length > 0)
					LogHelper.info("Found longvarbinary not null value; table "+tableInfo.getName()+" column "+columnInfo.getName());
			}
			else if (columnInfo.getType() == Types.SMALLINT)
				value = Integer.toString(rs.getInt(j+1));
			else if (columnInfo.getType() == Types.INTEGER)
				value = Integer.toString(rs.getInt(j+1));
			else if (columnInfo.getType() == Types.BIGINT)
				value = Long.toString(rs.getLong(j+1));
			else if (columnInfo.getType() == Types.FLOAT) {
				value = Float.toString(rs.getFloat(j+1));
			}
			else if (columnInfo.getType() == Types.DOUBLE) {
				value = Double.toString(rs.getDouble(j+1));
			}
			else if (columnInfo.getType() == Types.BIT) {
				value = rs.getBoolean(j+1) ? "1" : "0";
			}
			else if (columnInfo.getType() == Types.DECIMAL || columnInfo.getType() == Types.NUMERIC) {
				try {
					BigDecimal bd = rs.getBigDecimal(j+1);
					if (bd == null)
						value = "null";
					else
						value = bd.toString();
					bd = null;
				}
				catch (NumberFormatException nex) {
					LogHelper.info("DECIMAL or NUMERIC NumberFormatException; null value used; table "+tableInfo.getName()+" column "+columnInfo.getName());
					value = "null";
				}
			}
			else if (columnInfo.getType() == Types.DATE) {
				java.util.Date date = rs.getDate(j+1);
				if (date == null)
					value = "null";
				else
					value = "'"+UtilHelper.getDateString(date)+"'";
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
					value = "'"+UtilHelper.getDateString(time)+"'";
				time = null;
			}
			else
				LogHelper.error("unknown type "+columnInfo.getType()+" column "+columnInfo.getName()+
					" in table "+tableInfo.getName());
			rowInfo.add (new DataInfo(value));
		}
		return rowInfo;
	}

	public ArrayList<TableInfo> makeTableInfoWithIncludeList (IncludeTableInfo includeTableInfo) {
		return makeTableInfoWithIncludeList (includeTableInfo, true);
	}
	public ArrayList<TableInfo> makeTableInfoWithIncludeList (IncludeTableInfo includeTableInfo, boolean includeType) {
		LogHelper.info("Getting Table info with include/exclude list");
		ArrayList<TableInfo> allTableInfo = new ArrayList<TableInfo>();
		ArrayList<String> allTables = getTables (includeTableInfo, includeType);
		Iterator<String> iterator = allTables.iterator();
		while (iterator.hasNext()) {
			String tableName = ((String) iterator.next()).toUpperCase();
			IncludeTableItemInfo includeTableItemInfo = includeTableInfo.getTable (tableName);
			if (includeType) {
				if (includeTableItemInfo == null) {
					allTableInfo.add (new TableInfo (tableName, "", "", ""));
				}
				else {
					allTableInfo.add (new TableInfo (tableName, includeTableItemInfo.getSelect(), 
						includeTableItemInfo.getClause(), includeTableItemInfo.getOrder()));
				}
			}
			else {
				allTableInfo.add (new TableInfo (tableName, "", "", ""));
			}
		}
		return makeTableInfoFromTableInfo (allTableInfo);
	}

	public ArrayList<TableInfo> makeTableInfoFromTableInfo (ArrayList<TableInfo> allTableInfo) {
		LogHelper.info("Getting Index info");
		TableInfo tableInfo;
		Iterator<TableInfo> iterator = allTableInfo.iterator();
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

		for (int i=0; i<allTableInfo.size(); i++) {
			tableInfo = (TableInfo) allTableInfo.get(i);
			Iterator<String> iter1 = tableInfo.getPklist().iterator();
			while (iter1.hasNext()) {
				String column = (String) iter1.next();
//				LogHelper.info("Primary key: "+column);
				ColumnInfo columnInfo = tableInfo.getColumn(column);
				columnInfo.setPk(true);
			}
		}

		return allTableInfo;
	}

	public TableInfo getTable(ArrayList<TableInfo> allTableInfo, String name) {
		TableInfo info;
		Iterator<TableInfo> iterator = allTableInfo.iterator();
		while (iterator.hasNext()) {
			info = (TableInfo) iterator.next();
			if ((info.getName()).equals(name)) return info;
		}
		return null;
	}
}
