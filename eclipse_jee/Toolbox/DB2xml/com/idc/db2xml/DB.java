package com.idc.db2xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Properties;

public class DB {
	private Connection m_connection;
	private boolean m_bConnected = false;
	private static String m_strSchema = "DB2INST1";
	private static final String[] m_strType = { "TABLE" };
	
	public DB() {}

	public boolean getConnection (String propertiesFile) {

		String username, password,  database, url;
		try
		{
			Properties defaultProps = new Properties();
			FileInputStream in = new FileInputStream(propertiesFile);
			defaultProps.load(in);
			in.close();
			username = defaultProps.getProperty("db2v8.username");
			password = defaultProps.getProperty("db2v8.password");
			database = defaultProps.getProperty("db2v8.databasename");
		}
		catch (IOException exception)
		{
			System.out.println ("trouble with properties file");
			return m_bConnected;
		}	
		url = "jdbc:db2:"+database;
		try
		{
			DriverManager.registerDriver((Driver)Class.forName("COM.ibm.db2.jdbc.app.DB2Driver").newInstance());
		} 
		catch (Exception e) 
		{
			System.out.println(e);
			return m_bConnected;
		};
		try {
		 	m_connection = (Connection) DriverManager.getConnection(url, username, password);
		} catch (SQLException ex) {
			System.out.println("could not get connection");
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
			System.out.println("could not get connection");
		}
	}
	
	public ArrayList<String> getTables() {
		ResultSet resultSet = null;
		ArrayList<String> list = new ArrayList<String>();
		String tableName;
		try {
			DatabaseMetaData metaData = m_connection.getMetaData();
			resultSet = metaData.getTables(null, m_strSchema, null, m_strType);
			while (resultSet.next()) 			{
				tableName = resultSet.getString("TABLE_NAME");
				list.add (tableName);
			}
		}
		catch (SQLException sqlException) {
			System.out.println("trouble in getTables");
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

	public ArrayList<String> getPrimaryKeys (String tableName) {
		ResultSet resultSet = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			DatabaseMetaData metaData = m_connection.getMetaData();
			resultSet = metaData.getPrimaryKeys(null, m_strSchema, tableName);
			while (resultSet.next()) {
				list.add (resultSet.getString("COLUMN_NAME"));
			}
		}
		catch (SQLException sqlException) {
			System.out.println("trouble in getPrimaryKeys");
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

	public ArrayList<ColumnInfo> getColumns (String tableName) {
		String name;
		ColumnInfo info;
		ResultSet resultSet = null;
		ArrayList<ColumnInfo> list = new ArrayList<ColumnInfo>();		
		try {
			DatabaseMetaData metaData = m_connection.getMetaData();
			resultSet = metaData.getColumns(null, m_strSchema, tableName, null);
			while (resultSet.next()) {
				info = new ColumnInfo();
				name = resultSet.getString("COLUMN_NAME");				
				info.setName(name);
				info.setType(resultSet.getInt("DATA_TYPE"));
				if (info.getType() == Types.CHAR || info.getType() == Types.VARCHAR)
					info.setLen(resultSet.getInt("COLUMN_SIZE"));
				list.add (info);				
			}
		}
		catch (SQLException sqlException) {
			System.out.println("trouble in getColumns");
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
}


