package com.idc.sql.app;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AppDb2 {
	public static void main (String[] args) {
		(new AppDb2()).doTest();
	}
	private boolean doTest() {
		System.out.println(">>> doTest");
		String dbdriver = "COM.ibm.db2.jdbc.app.DB2Driver";
//		String dbdriver = "COM.ibm.db2.jcc.DB2Driver";
		String url = "jdbc:db2:";
		String database = "ECOMDEV";
		String username = "prc4031";
		String password = "love257$";
		String schema = "HERCDB";
		System.out.println("dbdriver "+dbdriver);
		System.out.println("url "+url);
		System.out.println("database "+database);
		System.out.println("username "+username);
//		System.out.println("password "+password);
		System.out.println("schema "+schema);
		String connectionUrl = url + database;
		System.out.println("connectionUrl "+connectionUrl);
		try {
			DriverManager.registerDriver((Driver)Class.forName(dbdriver).newInstance());
			System.out.println("driver registered");
		} 
		catch (Exception e) {
			System.out.println("Error; unable to register the driver "+e.getMessage());
			return false;
		}
		try {
			Connection m_connection = DriverManager.getConnection(connectionUrl, username, password);
		} catch (SQLException ex) {
			System.out.println("Exception; "+ex.getMessage());
			System.out.println("could not get connection");
			return false;
		}
		System.out.println("<<< doTest");
		return true;
	}
}
