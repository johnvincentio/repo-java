// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************   
// ****************************************************************************************************************   
//   MODIFICATION INDEX
//   
//  Index     User Id         Date       Project                Desciption
//  -------  -------------  ----------  ----------------------  ----------------------------------------------------
//  RI001     DTC9028        03/02/05    SR31413 PCR19           Implement the datasource connection
// ****************************************************************************************************************

package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import java.util.*; 
import etrieveweb.utility.sqlBean;

public class checkcustomeridBean extends sqlBean implements java.io.Serializable{


	ResultSet result=null, result2=null;
	Statement stmt=null, stmt2=null;

	public checkcustomeridBean() {
		super();
	}

 
	public boolean checkCustomerID(int corporate, int customer_num, String company, String datalib) throws Exception { 

		try {		// RI001
			
			String SQLstatement = "select CMNAME " +                      
				"from " + datalib + ".CUSMASFL " +  
				"where CMCMP='" + company + "' and CMCUS#=" + customer_num + " and CMCRP#=" + corporate;
  
			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
			result=stmt.executeQuery(SQLstatement);
		
			if ( result == null )
				return false;
			else 
				return result.next();

		} catch (SQLException sqle) {				// RI001
			System.err.println(sqle.getMessage());	// RI001
			return false;							// RI001
		} catch (Exception e) {					// RI001
			System.err.println(e.getMessage());		// RI001
			return false;							// RI001
		} finally {								// RI001
			endcurrentResultset(result);			// RI001
			endcurrentStatement(stmt);				// RI001
		}											// RI001
		
	}


	public void cleanup() throws Exception {
		try {
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

 
}