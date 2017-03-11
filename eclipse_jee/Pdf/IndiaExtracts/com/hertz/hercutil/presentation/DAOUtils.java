/********************************************************************
*			Copyright (c) 2006 The Hertz Corporation				*
*			  All Rights Reserved.  (Unpublished.)					*
*																	*
*		The information contained herein is confidential and		*
*		proprietary to The Hertz Corporation and may not be			*
*		duplicated, disclosed to third parties, or used for any		*
*		purpose not expressly authorized by it.  Any unauthorized	*
*		use, duplication, or disclosure is prohibited by law.		*
*																	*
*********************************************************************/

package com.hertz.hercutil.presentation;

/**
* @author John Vincent
*
* handle DAO methods
*/

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hertz.irac.framework.HertzSystemException;
import com.hertz.irac.util.logging.LogBroker;

public class DAOUtils {

	private DAOUtils() { }

	/*
	 * Connect to a named data source
	 */
	public static Connection getDSConnection (String source) throws Exception {
		String dbdriver = "com.ibm.as400.access.AS400JDBCDriver";
		String url = "jdbc:as400://";
		String database = "AS4DEV";
		String username = "prc4031";
		String password = "xxxxxx";
		String schema = "RMHCQDATA";
		System.out.println("dbdriver "+dbdriver);
		System.out.println("url "+url);
		System.out.println("database "+database);
		System.out.println("username "+username);

//		String connectionUrl = url + database + ";naming=sql;errors=full";
		String connectionUrl = url + database + "/" + schema + ";naming=sql;errors=full";
		try {
			DriverManager.registerDriver((Driver)Class.forName(dbdriver).newInstance());
		} 
		catch (Exception e) {
			throw new HertzSystemException("SQL Exception (getDSConnection) while  "
					+ "DriverManager.registerDriver : \n" + e);
		}
		try {
			Connection connection = DriverManager.getConnection(connectionUrl, username, password);
			System.out.println("catalog :"+connection.getCatalog()+":");
//			connection.setCatalog("RMHCQDATA");
			return connection;
		} catch (SQLException se) {
			throw new HertzSystemException("SQL Exception (getDSConnection) while getting "
					+ "DB connection : \n" + se);
		}
	}

/*
 * Close database connection
 */
	public static void closeConnection(Connection dbConnection) throws HertzSystemException {
		try {
			if (dbConnection != null && (! dbConnection.isClosed())) {
				dbConnection.close();
			}
		} catch (SQLException se) {
			throw new HertzSystemException("SQL Exception while closing "
					+ "DB connection : \n" + se);
		}
	}
/*
 * Close result set
 */
	public static void closeResultSet(ResultSet result) throws HertzSystemException {
		try {
			if (result != null) {
				result.close();
				result = null;
			}
		} catch (SQLException se) {
			throw new HertzSystemException("SQL Exception while closing "
					+ "Result Set : \n" + se);
		}
	}
/*
 * Close statement
 */
	public static void closeStatement(PreparedStatement stmt) throws HertzSystemException {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException se) {
			throw new HertzSystemException("SQL Exception while closing "
					+ "Statement : \n" + se);
		}
	}

/*
 * Commit transaction
 */
	public static void commit(Connection dbConnection) {
		try {
			if (dbConnection != null && (! dbConnection.isClosed()))
				dbConnection.commit();
		}
		catch (SQLException ex) {
			rollback(dbConnection);
		}
	}
/*
 * Rollback transaction
 */
	public static void rollback(Connection dbConnection) {
		try {
			if (dbConnection != null && (! dbConnection.isClosed()))
				dbConnection.rollback();
		}
		catch (SQLException ex) {
		}
	}
/*
 * Make a standard SQL error message from a SQLException
 */
	public static void showSQLError(String msg, SQLException ex) {
		LogBroker.debug(DAOUtils.class, msg);
		LogBroker.debug(DAOUtils.class, "--- DAOUtils::SQLException ---:");
		if (ex != null) {
			LogBroker.debug(DAOUtils.class, "Message: "+ex.getMessage());
			LogBroker.debug(DAOUtils.class, "SQL State: "+ex.getSQLState());
			LogBroker.debug(DAOUtils.class, "Error Code: "+ex.getErrorCode());
		}
	}
/*
 * Get current datetime
 */
	public static java.sql.Date getCurrentDate() {
		return new java.sql.Date(new java.util.Date().getTime());	
	}
/*
 * Get current datatime as a Date object from a HercDate object
 */
	public static java.sql.Date getHercDate(HercDate hercDate) {
		if (hercDate == null || hercDate.isNull()) return null;
		return new java.sql.Date(hercDate.getDate().getTime());
	}

	public static String cleanString (String str) {
		return cleanString (str, "");
	}

	public static String cleanString (String str, String def) {
		if (str == null) return def;
		return str.trim();
	}
}
