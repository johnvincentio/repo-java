// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
//   MODIFICATION INDEX 
//   
//    Index     User Id      Date     Project            Desciption
//   -------  ----------  ---------- ----------------  -----------------------------------------------------
//    RI001    DTC2073     02/07/05   SR31413 PCR19     Created bean
//    RI002    DTC9028     09/14/05   SR31413 PCR19     Pass the argument that determines a demo account
//    RI003    DTC2073     02/06/06   SR35873           Abbreviated Equipment Release
//*****************************************************************************************************************

package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;

import etrieveweb.utility.sqlBean;

public class sysctlflBean extends sqlBean implements java.io.Serializable {

	ResultSet result = null;
	Statement stmt = null;

	private Connection conn = null;

	public sysctlflBean() {
		super();
	}

	// ****************************************************************************
	//  Retrieve the flag that determines whether to connect with a data source
	// ****************************************************************************
	
	public synchronized boolean getConnectionFlag(String str_demoAccount) throws Exception {

		boolean connectionFlag = false;
			
		try {
			
			conn = makedatasourceConnection(str_demoAccount);

			if ( conn != null ) {
				
				String SQLstatement = "select * from SYSCTLFL where ZXKYFL = 'ETRIV1' ";

				stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                
				result = stmt.executeQuery(SQLstatement);
			
				if ( !result.equals(null) ) {
					result.next();
					if ( result.getString("ZXYON3").equals("Y") )
						connectionFlag = true;
				}
			}
			
        
		} catch (SQLException sqle) {
			System.err.println(sqle.getMessage());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			endcurrentResultset(result);
			endcurrentStatement(stmt);
			endcurrentConnection(conn);
		}

		return connectionFlag;
	
			
	}
    	
	// *********************************************************
	// Retrieve the Etrieve control records from RentalMan
	// *********************************************************
	
	public synchronized String[] getProperties(String str_demoAccount) throws Exception {

		String[] Array = { "", "", "", "", "", "", "",""}; 	// RI003

		try {
					
			conn = makedatasourceConnection(str_demoAccount);
				
			// ****************************************
			//  Retrieve ETRIV1 control record
			// ****************************************
		
			String SQLstatement = "select * from SYSCTLFL where ZXKYFL = 'ETRIV1' or ZXKYFL = 'ETRIV2' order by ZXKYFL";

			stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                
			result = stmt.executeQuery(SQLstatement);
        
			if ( !result.equals(null) ) {
        	
				while ( result.next() ) {
			
					if ( result.getString("ZXKYFL").trim().equals("ETRIV1") ) {
					
						Array[0] = result.getString("ZXCMP").trim();    // Company
						Array[1] = result.getString("ZXYORN").trim();   // Pref Credit
						Array[2] = result.getString("ZXYON2").trim();	// Pref International
						Array[3] = result.getString("ZXCOD3").trim();	// Path
						Array[4] = result.getString("ZXTEXT").trim();	// Data Library
						Array[5] = result.getString("ZXDATA").trim();	// AS400 name

					} else if ( result.getString("ZXKYFL").trim().equals("ETRIV2") ) {
					
						Array[6] = result.getString("ZXDATA").trim();  // Etrieve user
						Array[7] = result.getString("ZXTEXT").trim();  // SMTP Server Name 		// RI003

						if ( Array[7].trim().equals("") )		// RI003
							Array[7] = "hertz.com";				// RI003
					}

				}			
			}
  
		} catch (SQLException sqle) {
			System.err.println(sqle.getMessage());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {								
			endcurrentResultset(result);
			endcurrentStatement(stmt);
			endcurrentConnection(conn);
		}
		
				
		return Array;
          
	}


	// **********************************************************************************************************
	// RI003 - Retrieve the Etrieve control records from RentalMan that contains the e-mail address for sending 
	// release/extend notifications.  Location records will override all other.
	// **********************************************************************************************************
	
	public synchronized String getdefaultEmail(String str_demoAccount,String str_comp,String strLocation) throws Exception {

		String defaultEmail = "";
		String companyEmail = "" ;
		String locationEmail = "" ;
		
		try {

			conn = makedatasourceConnection(str_demoAccount);
							
			String SQLstatement = 
				"select * from SYSCTLFL where ZXKYFL = 'ETRIV3' and ZXCMP = '" + str_comp + "' order by ZXKYFL ";

			stmt = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			result = stmt.executeQuery(SQLstatement);
        
			if ( !result.equals(null) ) 
			{
				while ( result.next() ) 
				{
					if ( result.getString("ZXLOC").trim().equals("") ) 
					{
							companyEmail = result.getString("ZXDATA");
					} 
					else if ( result.getString("ZXLOC").trim().equals(strLocation) ) 
					{
							locationEmail = result.getString("ZXDATA");
					}
				}
				
				if ( companyEmail == null )  companyEmail = "";
				if ( locationEmail == null ) locationEmail = "";
		   }
  
		} catch (SQLException sqle) {
			System.err.println(sqle.getMessage());
			System.err.println(sqle);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println(e);
		} finally {								
			endcurrentResultset(result);
			endcurrentStatement(stmt);
			endcurrentConnection(conn);
		}
		
		// *** Get the location address if availble else take company address ***
		// *** If none of them is available, return blank address ***
		
		if(!locationEmail.equals(""))
			defaultEmail = locationEmail;
		else
			if(!companyEmail.equals(""))	
				defaultEmail = companyEmail;	
			else
				defaultEmail = "";
	
		return defaultEmail;
          
	}



    public void cleanup() throws Exception {

    }


}
