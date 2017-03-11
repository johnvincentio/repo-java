package etrieveweb.etrievebean;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.*;
import java.sql.CallableStatement;
import java.sql.SQLException;

/** Encapsulate access to RPG program RPG001 */
/* 
CREATE PROCEDURE  DTC9028W/RMRATES1( 
  IN  CMP CHAR(2),                   
  IN  LOC CHAR(3),                   
  IN  ENDDT CHAR(6),                 
  IN  CAT   char(3),                 
  IN  CLASS char(4),                 
  IN  CUST  char(7),                 
  OUT MNRT CHAR(8   ),               
  OUT DLRT CHAR(8   ),               
  OUT WKRT CHAR(8   ),               
  OUT WORT CHAR(8   ),               
  OUT MSG  CHAR(30))                                            
  LANGUAGE        RPGLE                                 
*/
public class RentalRatesBean implements Serializable {

	private static Connection con;
	private static CallableStatement rpg;  

	public synchronized String [] getRentalRates(String inCompany, String inLocation, String inEstReturnDate, String inCatg, String inClass, String inCustomerNum) throws SQLException {

		String [] rentalRates = new String [5]; 
		 
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
			
			rpgcall = conn.prepareCall("Call DTC9028W.RMRATES1(?,?,?,?,?,?,?,?,?,?,?)");

			// Set Input parameters.
			rpgcall.setString(1, inCompany);		// Company flag
			rpgcall.setString(2, inLocation);		// Location
			rpgcall.setString(3, inEstReturnDate);	// Estimated return date (mmddyy)
			rpgcall.setString(4, inCatg);			// Category
			rpgcall.setString(5, inClass);			// Class
			rpgcall.setString(6, inCustomerNum);	// Customer number

			// Set output parameters.
	       	rpgcall.registerOutParameter(7,  Types.CHAR);	// Minimum rate
			rpgcall.registerOutParameter(8,  Types.CHAR);	// Daily rate
			rpgcall.registerOutParameter(9,  Types.CHAR);	// Weekly rate
			rpgcall.registerOutParameter(10, Types.CHAR);	// Monthly rate
			rpgcall.registerOutParameter(11, Types.CHAR);	// Error message
			
			//Run the Stored Procedure. If execute routine returns "True" display values.
       	    rpgcall.execute();
       	    
			rentalRates[0] = rpgcall.getString(7);	// Minimum rate
			rentalRates[1] = rpgcall.getString(8);	// Daily rate
			rentalRates[2] = rpgcall.getString(9);	// Weekly rate
			rentalRates[3] = rpgcall.getString(10);	// Monthly rate
			rentalRates[4] = rpgcall.getString(11);	// Error message
			
		} catch (SQLException e) {		
			System.err.println(e);		
			System.err.println("General Exception");			
		} 
		
		return rentalRates;
	}  
	
}
