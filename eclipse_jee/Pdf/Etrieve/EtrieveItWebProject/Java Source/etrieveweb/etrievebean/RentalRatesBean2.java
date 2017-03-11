
package etrieveweb.etrievebean;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.*;
import java.sql.CallableStatement;
import java.sql.SQLException;

/** Encapsulate access to RPG program */
/* 
CREATE PROCEDURE  DTC9028W/RMRATES2( 
  IN  CMP   CHAR(2),                   
  IN  LOC   CHAR(3),  
  IN  STRDT CHAR(6),                      
  IN  ENDDT CHAR(6),                 
  IN  CAT   CHAR(3),                 
  IN  CLASS CHAR(4),                 
  IN  CUST  CHAR(7),   
  IN  TASK  CHAR(7),          
  OUT MNRT  CHAR(8),               
  OUT DLRT  CHAR(8),               
  OUT WKRT  CHAR(8),               
  OUT MORT  CHAR(8),
  OUT RAMT  CHAR(10),                   
  OUT COMPL CHAR(30),
  OUT MSG   CHAR(78))                                            
  LANGUAGE        RPGLE                                 
*/

public class RentalRatesBean2 implements Serializable {

	private static Connection con;
	private static CallableStatement rpg;  

	public synchronized String [] getRentalRates(String inCompany, String inLocation, String inStartDate, String inEstReturnDate, String inCatg, String inClass, String inCustomerNum, String inCalcType) throws SQLException {

		String [] rentalRates = new String [8]; 
		 
		CallableStatement rpgcall = null ;
		Connection conn = null ;
			
		try{
		
			javax.naming.Context ctx = new javax.naming.InitialContext();		

			javax.sql.DataSource rentalmanDS = (javax.sql.DataSource) ctx.lookup("java:comp/env/jdbc/RentalMan");	

			conn = rentalmanDS.getConnection();
		
		} catch (Exception e)  {
        	
			System.err.println("Failed to make a datasource connection.");	
        	
        	conn = null;
		}

		try{
			
			rpgcall = conn.prepareCall("Call DTC9028W.RMRATES2(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Set Input parameters.
			rpgcall.setString(1, inCompany);		// Company flag
			rpgcall.setString(2, inLocation);		// Location
			rpgcall.setString(3, inStartDate);		// Start date (mmddyy)
			rpgcall.setString(4, inEstReturnDate);	// Estimated return date (mmddyy)
			rpgcall.setString(5, inCatg);			// Category
			rpgcall.setString(6, inClass);			// Class
			rpgcall.setString(7, inCustomerNum);	// Customer number
			rpgcall.setString(8, inCalcType);       // Task - RATES or RENTAMT

			// Set output parameters.
	       	rpgcall.registerOutParameter(9,  Types.CHAR);	// Minimum rate
			rpgcall.registerOutParameter(10, Types.CHAR);	// Daily rate
			rpgcall.registerOutParameter(11, Types.CHAR);	// Weekly rate
			rpgcall.registerOutParameter(12, Types.CHAR);	// Monthly rate
			rpgcall.registerOutParameter(13,  Types.CHAR);	// Rental charges
			rpgcall.registerOutParameter(14, Types.CHAR);	// Completion indicator
			rpgcall.registerOutParameter(15, Types.CHAR);	// If ERROR, Display Error Message
			
			//Run the Stored Procedure. If execute routine returns "True" display values.
       	    
       	    rpgcall.execute();
       	    
			rentalRates[0] = rpgcall.getString(9);	// Minimum rate
			rentalRates[1] = rpgcall.getString(10);	// Daily rate
			rentalRates[2] = rpgcall.getString(11);	// Weekly rate
			rentalRates[3] = rpgcall.getString(12);	// Monthly rate
			rentalRates[4] = rpgcall.getString(13);	// Rental Charge 
			rentalRates[5] = rpgcall.getString(14);	// Completion indicator
			rentalRates[6] = rpgcall.getString(15);	// Error message
						
		} catch (SQLException e) {		
			System.err.println(e);		
			System.err.println("General Exception");			
		} 
		
		return rentalRates;
	}  
	
}
