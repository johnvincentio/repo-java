package com.idc.rm;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;

public abstract class SQLBean implements java.io.Serializable {

	private String sDriver,
		sDriverKey = "CSDriver",
		sPassword,
		sPasswordKey = "CSPassword",
		srbName = "etrieveweb.utility.Connection",
		sURL,
		sURLKey = "CSURL",
		sUserID,
		sUserIDKey = "CSUserID";

	protected Connection conn;

	public SQLBean() {

		try {    // get the PropertyResourceBundle
        	
			Properties settings = new Properties();
			URL path = this.getClass().getClassLoader().getResource("conf//Connection.properties"); 
			FileInputStream sf = new FileInputStream(path.getPath());
			settings.load(sf);
			sDriver = settings.getProperty(sDriverKey);
			sPassword = settings.getProperty(sPasswordKey);
			sURL = settings.getProperty(sURLKey);
			sUserID = settings.getProperty(sUserIDKey);
			
		} catch (Exception e) {    // error
			System.err.println("Failed to load properties.");
			return;
		} // end catch
	}

	// **************************************************************
	// Make a non-datasource connection
	// **************************************************************
	
	public Connection  makeConnection() throws Exception {
    	
		Class.forName(sDriver);
        
		conn = DriverManager.getConnection(sURL, sUserID, sPassword);
        
		return conn;
        
	}

	// **************************************************************
	// Determine how to make a connection
	// **************************************************************
	
	public Connection makeautoConnection(String asDriver, String asURL, String asUserID, String asPassword) throws Exception {

		String str_demoAccount = "";		// RI002
		
		if ( asUserID.trim().equals("PXPAS409") )	// RI002
			str_demoAccount = "Y";					// RI002
			
		Connection theconnection = null;   // RI001
		
		if ( !asPassword.trim().equals("") ) {

			sPassword = asPassword.trim();

			if ( !asDriver.trim().equals("") )
				sDriver = asDriver.trim();

			if ( !asURL.trim().equals("") )
				sURL = asURL.trim();

			if ( !asUserID.trim().equals("")  ) 
				sUserID = asUserID.trim();

			theconnection = makeConnection();  // RI001
		
		} else {   // RI001
			
			theconnection = makedatasourceConnection(str_demoAccount);  // RI001, RI002
		}

		return theconnection;

	}



	// *********************************************************
	// Make a datasource connection  - RI001
	// *********************************************************
	

	public Connection makedatasourceConnection(String str_demoAccount) throws Exception {	// RI001, RI002
        
		Connection dsconnection = null;   // RI001
        
		try {
        	
			// Locate the naming initial context
			javax.naming.Context ctx = new javax.naming.InitialContext();		// RI001

			// Perform a JNDI lookup for the DataSource. 
			// The JNDI name in the code is a resource reference and is not necessary the same as the JNDI name you defined for the DataSource. 
			// You need to bind this resource reference to the real JNDI name in the deployment descriptor. 

			javax.sql.DataSource rentalmanDS = (javax.sql.DataSource) ctx.lookup("java:comp/env/jdbc/RentalMan");	// RI001

			if ( str_demoAccount.trim().equals("Y") ) {	// RI002
				rentalmanDS = (javax.sql.DataSource) ctx.lookup("java:comp/env/jdbc/RentalManTraining");	// RI002
			}	// RI002
			
			// create a connection from the DataSource
			conn = rentalmanDS.getConnection();		// RI001
		
		} catch (Exception e)  {	// RI001
        	
			System.err.println("Failed to make a datasource connection.");		// RI001
        	
        	conn = null;		// RI001
		
		}
        
		dsconnection = conn;	// RI001
		
		return dsconnection;	// RI001
		
	}		// RI001


    public abstract void cleanup() throws Exception;

	// ****************************
	// End the connection
	// ****************************
	public void endConnection() throws Exception {
		cleanup();
		conn.close();
	}


	// **********************************
	// RI001 - End the current resultset
	// **********************************
	public void endcurrentResultset(ResultSet currResultSet) throws Exception {
        
        if (currResultSet != null) {
			try {
				currResultSet.close();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	// **********************************
	// RI001 - End the current statement
	// **********************************
	public void endcurrentStatement(Statement currStatement) throws Exception {
        
        if (currStatement != null) {
			try {
				currStatement.close();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	// **********************************
	// RI001 - End the current connection
	// **********************************
	public void endcurrentConnection(Connection currConnection) throws Exception {

        if (currConnection != null) {		// RI001
			try {
				currConnection.close();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}		// RI001
	}


}