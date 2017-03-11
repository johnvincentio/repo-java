package com.hertz.hack.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
//import java.sql.Blob;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DB {
	private Connection m_connection;
	private boolean m_bConnected = false;
	private static String m_strSchema = "DB2INST1";
	
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

	public Data getData () {
		Data data = new Data();
		StringBuffer buf = new StringBuffer();
		buf.append("select log_time, remote_ip_address, logdata ");
		buf.append(" from ");
		buf.append(m_strSchema);
		buf.append(".hacklog order by log_time");

		SimpleDateFormat sdf =
				new SimpleDateFormat("dd MMM yyyy hh:mm:ss z");
		Date myDate = null;	
		ResultSet resultSet = null;
		String date;
		String ipaddr;
		String line;
//		Blob blob;
		try {
			Statement stmt = m_connection.createStatement();
			resultSet = stmt.executeQuery(buf.toString());
			while (resultSet.next()) {
				myDate = new Date(((Timestamp) resultSet.getTimestamp(1)).getTime());
				date = sdf.format(myDate);
				ipaddr = resultSet.getString(2);
				line = resultSet.getString(3);
//				System.out.println("ip "+ipaddr);
/*
				blob = resultSet.getBlob(3);
				System.out.println("stage 1");
				line = "";
				if (blob != null && blob.length() > 0) {
					raw_buffer = new byte[(int)blob.length()];
	         		line = new String( raw_buffer );
				}
*/
				data.add (new Item (date, ipaddr, line));
			}
		}
		catch (SQLException sqlx) {
			System.out.println("trouble in getData; "+sqlx.getMessage());
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
		return data;
	}

}
