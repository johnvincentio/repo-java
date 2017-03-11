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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javax.sql.DataSource;

import com.hertz.hercutil.framework.HercErrorHelper;
import com.hertz.irac.framework.HertzSystemException;
import com.hertz.irac.util.logging.LogBroker;
import com.ibm.websphere.ce.cm.StaleConnectionException;

public class DAOUtils {
	public static final String SCHEMA = "HERCDB.";

	private DAOUtils() { }
/*
 * Get the data source object, keep this private
 */
	private static DataSource getEnvDataSource(String dsName) throws HertzSystemException {
		try {
			String dataSourceName = ServiceLocatorHelper.getInstance().getString(dsName);
			return ServiceLocatorHelper.getInstance().getDataSource(dataSourceName);
		}
		catch (ServiceLocatorHelperException ex) {
			throw HercErrorHelper.handleServiceLocatorHelperException (DAOUtils.class, ex);
		}
	}
/*
 * Get the data source object, keep this private
 */
	private static DataSource getDSDataSource(String dsName) throws HertzSystemException {
		try {
			return ServiceLocatorHelper.getInstance().getDataSource(dsName);
		}
		catch (ServiceLocatorHelperException ex) {
			throw HercErrorHelper.handleServiceLocatorHelperException (DAOUtils.class, ex);
		}
	}
/*
 * Connect to a named data source
 */
	public static Connection getDSConnection(String source) throws HertzSystemException {
		try {
			return getEnvDataSource(source).getConnection();
		} catch (StaleConnectionException staleException) {
			return getDSConnectionFoundStale(source);
		} catch (SQLException se) {
			throw new HertzSystemException("SQL Exception (getDSConnection) while getting "
					+ "DB connection : \n" + se);
		}
	}

/*
 * Connect to a named data source, to be used only when a previous attempt to connect resulted in a 
 * stale connection. Keep this private.
 */
	private static Connection getDSConnectionFoundStale(String source) throws HertzSystemException {
		try {
			return getEnvDataSource(source).getConnection();
		} catch (SQLException se) {
			throw new HertzSystemException("SQL Exception (getDSConnectionFoundStale) while getting "
					+ "DB connection : \n" + se);
		}
	}

/*
 * Connect to a named data source
 */
	public static Connection getDBConnection(DataSource dataSource) throws HertzSystemException {
		try {
			return dataSource.getConnection();
		} catch (StaleConnectionException staleException) {
			return getDSConnectionFoundStale(dataSource);
		} catch (SQLException se) {
			throw new HertzSystemException("SQL Exception (getDBConnection) while getting "
					+ "DB connection : \n" + se);
		}
	}

/*
 * Connect to a named data source, to be used only when a previous attempt to connect resulted in a 
 * stale connection. Keep this private.
 */
	private static Connection getDSConnectionFoundStale(DataSource dataSource) throws HertzSystemException {
		try {
			return dataSource.getConnection();
		} catch (SQLException se) {
			throw new HertzSystemException("SQL Exception (getDSConnectionFoundStale) while getting "
					+ "DB connection : \n" + se);
		}
	}

/*
 * Connect to a named data source
 */
	public static Connection getDBConnection(String source) throws HertzSystemException {
		try {
			return getDSDataSource(source).getConnection();
		} catch (StaleConnectionException staleException) {
			return getDSConnectionFoundStale(source);
		} catch (SQLException se) {
			throw new HertzSystemException("SQL Exception (getDBConnection) while getting "
					+ "DB connection : \n" + se);
		}
	}

/*
 * Connect to a named data source, to be used only when a previous attempt to connect resulted in a 
 * stale connection. Keep this private.
 */
	private static Connection getDBConnectionFoundStale(String source) throws HertzSystemException {
		try {
			return getDSDataSource(source).getConnection();
		} catch (SQLException se) {
			throw new HertzSystemException("SQL Exception (getDBConnectionFoundStale) while getting "
					+ "DB connection : \n" + se);
		}
	}

	public static Connection getNotStaleDBConnection(String source) throws HertzSystemException {
		LogBroker.debug(DAOUtils.class,">>> DAOUtils::getNotStaleDBConnection");
		for (int count = 0; count < 100; count++) {
			try {
				LogBroker.debug(DAOUtils.class,"getConnection; count "+count);
				Connection connection = getDSDataSource(source).getConnection();
				LogBroker.debug(DAOUtils.class,"getTestConnection; count "+count);
				getTestConnection (connection);
				LogBroker.debug(DAOUtils.class,"<<< DAOUtils::getNotStaleDBConnection");
				return connection;
			} catch (StaleConnectionException staleException) {
				LogBroker.debug(DAOUtils.class,"DAOUtils::getNotStaleDBConnection; StaleConnectionException; count "+count);
				continue;
			} catch (SQLException se) {
				throw new HertzSystemException("SQL Exception (getDBConnection) while getting "
						+ "DB connection : \n" + se);
			}
		}
		LogBroker.debug(DAOUtils.class,"<<< DAOUtils::getNotStaleDBConnection; no connection found");
		throw new HertzSystemException("Unable to get a not stale connection");
	}
	public static Connection getNotStaleDBConnection(DataSource dataSource) throws HertzSystemException {
		LogBroker.debug(DAOUtils.class,">>> DAOUtils::getNotStaleDBConnection");
		for (int count = 0; count < 100; count++) {
			try {
				LogBroker.debug(DAOUtils.class,"getConnection; count "+count);
				Connection connection = dataSource.getConnection();
				LogBroker.debug(DAOUtils.class,"getTestConnection; count "+count);
				getTestConnection (connection);
				LogBroker.debug(DAOUtils.class,"<<< DAOUtils::getNotStaleDBConnection");
				return connection;
			} catch (StaleConnectionException staleException) {
				LogBroker.debug(DAOUtils.class,"DAOUtils::getNotStaleDBConnection; StaleConnectionException; count "+count);
				continue;
			} catch (SQLException se) {
				throw new HertzSystemException("SQL Exception (getDBConnection) while getting "
						+ "DB connection : \n" + se);
			}
		}
		LogBroker.debug(DAOUtils.class,"<<< DAOUtils::getNotStaleDBConnection; no connection found");
		throw new HertzSystemException("Unable to get a not stale connection");
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
 * Close callable statement
 */
	public static void closeCallableStatement(CallableStatement stmt) throws HertzSystemException {
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
 * Convert String to boolean
 */
	public static boolean setBoolean (String str) {
		if (str == null) return false;
		if (str.trim().equalsIgnoreCase("Y")) return true;
		return false;
	}
/*
 * Convert boolean to database string (varchar(1)).
 */
	public static String setString (boolean bool) {
		return bool ? "Y" : "N";
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

/*
 * Get current datatime as a Date object from a HercDate object
 */
	public static java.sql.Date getLastMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add (Calendar.MONTH, -1);
		return new java.sql.Date (cal.getTime().getTime());
	}
	
	/**
	 * Transform a Java Object into a bytes array
	 * 
	 * @param object		Java object
	 * @return				bytes array
	 * @throws Exception
	 */
	public static byte[] getBytes (Object object) throws Exception {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
		objStream.writeObject(object);
		objStream.flush();
		return (byte[])(byteStream.toByteArray());
	}

	/**
	 * Transforms a bytes array into a Java Object
	 * 
	 * @param bytes			bytes array
	 * @return				Java object
	 * @throws Exception
	 */
	public static Object getObject (byte[] bytes) throws Exception {
		Object result = null;
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		byteOutputStream.write (bytes);
		if (byteOutputStream.size() > 0) {
			ByteArrayInputStream byteInStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
			ObjectInputStream inputStream = new ObjectInputStream(byteInStream);
			result = inputStream.readObject();
		}
		return result;
	}

	/**
	 * Determine whether count is modulo interval
	 * 
	 * @param count			Count
	 * @param interval		Interval
	 * @return				true if count is modulo interval
	 */
	public static boolean isMod (int count, int interval) {
		int n1 = count / interval;
		int n2 = n1 * interval;
		if (n2 == count) return true;
		return false;
	}

	/**
	 * Test the connection
	 * 
	 * @param conn			Connection
	 * @throws Exception
	 */
	public static void getTestConnection (Connection conn) throws StaleConnectionException, SQLException {
		String sql = "select dummy from "+DAOUtils.SCHEMA+"migration";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			LogBroker.debug(DAOUtils.class, "Getting next record for test connection");
			String dummy = rs.getString("dummy");
		}
		if (rs != null) rs.close();
		rs = null;
		if (pstmt != null) pstmt.close();
		pstmt = null;
	}
}
