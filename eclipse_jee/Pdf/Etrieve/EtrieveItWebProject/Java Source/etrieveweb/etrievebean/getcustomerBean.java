/// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// **************************************************************************************************************** 
//   MODIFICATION INDEX
//   
//    Index    User Id         	Date      Project                            Desciption
//   -------  -------------  ----------  -------------------  ----------------------------------------------------
//    RI001    DTC9028        06/21/05    SR31413 PCR19        Implement datasource
//
// ***************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import java.util.*; 
import etrieveweb.utility.sqlBean;

public class getcustomerBean extends sqlBean implements java.io.Serializable{

	ResultSet result=null, result2=null;
	Statement stmt=null, stmt2=null;

	public getcustomerBean() {
		super();
	}

 
	public synchronized boolean getRows(String customer_list, int customer_num, String company, String datalib) throws Exception { 

		try { 		// RI001
			
			String SQLstatement = "select CMNAME " +                      
				"from " + datalib + ".CUSMASFL " +  
				" where CMCMP='" + company + "' and CMCUS#=" + customer_num + " and CMCUS# in " + customer_list;
                
			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
			result=stmt.executeQuery(SQLstatement);

			return (result != null);
			
		} catch (SQLException sqle) {				// RI001
			System.err.println(sqle.getMessage());	// RI001
			return false;							// RI001
		} catch (Exception e) {					// RI001
			System.err.println(e.getMessage());		// RI001
			return false;							// RI001
		} finally {								// RI001
		}											// RI001
	}


	public boolean getNext() throws Exception {
		return result.next();
	}


	public String getColumn(String colNum) throws Exception {
		return result.getString(colNum);
	}


	public synchronized String getCustomerVec(int corporate_num, String company, String datalib) throws Exception { 

		try {		// RI001
			   
			String list_customer = "";
		
			String SQLstatement2 = "select CMCUS#, CMNAME " +                      
				"from " + datalib + ".CUSMASF4 " +  
				" where CMCMP='" + company + "' and CMCRP#=" + corporate_num;
         
			stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
			result2=stmt2.executeQuery(SQLstatement2);

			if ( result2 != null ) {
				list_customer = "(";
				while ( result2.next() ) {
					list_customer += result2.getString("CMCUS#") + ",";
				}
				list_customer = list_customer.substring( 0, (list_customer.length()-1) ); 
				list_customer += ")"; 
			}
		
			return list_customer; 
			
		} catch (SQLException sqle) {				// RI001
			System.err.println(sqle.getMessage());	// RI001
			return "";								// RI001
		} catch (Exception e) {					// RI001
			System.err.println(e.getMessage());		// RI001
			return "";								// RI001
		} finally {								// RI001
			endcurrentResultset(result2);			// RI001
			endcurrentStatement(stmt2);				// RI001
		}											// RI001
		
	}


	public void cleanup() throws Exception {
		// RI001	stmt.close();
		endcurrentResultset(result);			// RI001
		endcurrentStatement(stmt);				// RI001
	}

}