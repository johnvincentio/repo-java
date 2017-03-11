package com.idc.rm;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public abstract class SQLBean implements java.io.Serializable {
	private Connection m_connection;
	public Connection getConnection() {return m_connection;}

	public String getCompanyCode (int countryCode) {
		if (countryCode == 2) return "CR";
		return "HG";
	}
	public String getDataLibrary (int countryCode) {
		if (countryCode == 2) return "WSDATA";
		return "RMHCQDATA";
	}

/*
			Properties conProperties = new Properties();
			conProperties.put("url", "jdbc:as400://");
			conProperties.put("user", "prc4031");
			conProperties.put("password", "tub$59ak");
			conProperties.put("portNumber", "50000");
			conProperties.put("databaseName", "AS4DEV");
			conProperties.put("deferPrepares", "false");	

			conProperties.put("currentSchema", "RMHCQDATA");
			conProperties.put("catalogSchema", "RMHCQDATA");
			conProperties.put("traceFile","C:/tmp101/jdbc.trace");
			conProperties.put("traceFileAppend","true");
			conProperties.put("TraceLevel","TRACE_ALL");

//			conProperties.put("retrieveMessagesFromServerOnGetMessage", "true");
//			conProperties.put("emulateParameterMetaDataForZCalls", "1");
//			conProperties.put("clientApplicationInformation", "T4Driver");
//			conProperties.put("clientWorkstation", "192.168.10.125");
			conProperties.put("defaultIsolationLevel", java.sql.Connection.TRANSACTION_READ_COMMITTED);
			conProperties.put("jdbcCollection", "NULLIDR1");
			conProperties.put("driverType", "4");
			Connection connection = DriverManager.getConnection(connectionUrl, conProperties);
*/
	public boolean makeConnection() throws Exception {
		String dbdriver = "com.ibm.as400.access.AS400JDBCDriver";
		String url = "jdbc:as400://";
		String database = "AS4DEV";
		String username = "prc4031";
		String password = "daw$21ny";
		System.out.println("dbdriver "+dbdriver);
		System.out.println("url "+url);
		System.out.println("database "+database);
		System.out.println("username "+username);

		String connectionUrl = url + database + ";naming=sql;errors=full";
		try {
			DriverManager.registerDriver((Driver)Class.forName(dbdriver).newInstance());
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		try {
		 	m_connection = DriverManager.getConnection(connectionUrl, username, password);
		} catch (SQLException ex) {
			System.out.println("could not get connection");
			return false;
		}
		return true;
	}


    public abstract void cleanup() throws Exception;

	public void endConnection() throws Exception {
		cleanup();
		m_connection.close();
		m_connection = null;
	}

	public void endcurrentResultset(ResultSet currResultSet) throws Exception {
        if (currResultSet != null) {
			try {
				currResultSet.close();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public void endcurrentStatement(Statement currStatement) throws Exception {
         if (currStatement != null) {
			try {
				currStatement.close();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
