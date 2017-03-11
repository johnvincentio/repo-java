package com.idc.sql.app;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AppMySQL {
	public static void main (String[] args) {
		(new AppMySQL()).doTest();
	}

	private boolean doTest() {
		System.out.println(">>> doTest");
		String dbdriver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql:";
		String host = "localhost";
		String port = "3306";
		String schema = "jv_schema_1";
		String username = "jv";
		String password = "yeti";
		String connectionUrl = url + "//" + host + ":" + port + "/" + schema;
		System.out.println("connectionUrl "+connectionUrl);
		Connection m_connection = null;
		try {
			DriverManager.registerDriver ((Driver)Class.forName(dbdriver).newInstance());
			System.out.println("driver registered");
		} 
		catch (Exception e) {
			System.out.println("Error; unable to register the driver "+e.getMessage());
			return false;
		}
		try {
			m_connection = DriverManager.getConnection (connectionUrl, username, password);
		}
		catch (SQLException ex) {
			System.out.println("Exception; "+ex.getMessage());
			System.out.println("could not get connection");
			return false;
		}
		try {
			String selectTableSQL = "select id, name from city";
			Statement statement = m_connection.createStatement();
			ResultSet rs = statement.executeQuery(selectTableSQL);
			while (rs.next()) {
				String id = rs.getString("ID");
				String name = rs.getString("NAME");
				System.out.println("id "+id+" name "+name);
			}
		}
		catch (SQLException ex) {
			System.out.println("Exception; "+ex.getMessage());
			System.out.println("could not get connection");
			try {
				if (m_connection != null) m_connection.close();
			}
			catch (SQLException ex2) {
				System.out.println("Exception; "+ex2.getMessage());
				System.out.println("could not close connection");
				return true;
			}
			return false;
		}

		System.out.println("<<< doTest");
		return true;
	}
}
