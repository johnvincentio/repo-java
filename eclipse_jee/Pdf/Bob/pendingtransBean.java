// *****************************************************************************************************************
// Copyright (C) 2008 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
//   MODIFICATION INDEX 
//   
//    Index     User Id       Date      Project            Desciption
//    -------  ----------  ----------- -----------------  -----------------------------------------------------
//	  RI001     DTC9028     03/12/08    SR35419            Etrieve equipment release
// ****************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*;
import etrieveweb.utility.sqlBean;

public class pendingtransBean extends sqlBean implements java.io.Serializable {

	String company = "";

	ResultSet result = null, result2 = null;
	Statement stmt = null, stmt2 = null;

    public pendingtransBean() {
        super();
    }

	//***************************************************
	// Check for transactions are pending by customer
	//***************************************************
			
	public synchronized boolean getPendingTrans( int customer_num, String company, String datalib) throws Exception { 
				
		try 
		{		
			String SQLstatement = "";
				
			SQLstatement = 
				"select * "
				+ " From " + datalib + ".ETWHDRFL2 left outer join " + datalib + ".ETWDETFL on "
				+ " THCMP = TDCMP "
				+ " AND THTRN# = TDTRN# "
				+ " where THCMP='" + company + "' "
				+ " and THCUS# = " +  customer_num;
			
			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	

			result=stmt.executeQuery(SQLstatement);
					 
		} catch (SQLException e) {
			
			System.err.println(e.getMessage());
			return false;
		}
        
        return (result != null);

	}

	public boolean getNext() throws Exception {
        return result.next();		
    }	

    public String getColumn(String colNum) throws Exception {
        return result.getString(colNum);
    }
    
	//***************************************************
	// Check for transactions are pending by contract
	//***************************************************
			
	public synchronized boolean getPendingConTrans( String str_contract, String company, String datalib) throws Exception { 
				
		try 
		{		
			String SQLstatement2 = 
				"select * "
				+ " From " + datalib + ".ETWHDRFL3 left outer join " + datalib + ".ETWDETFL on "
				+ " THCMP = TDCMP "
				+ " AND THTRN# = TDTRN# "
				+ " where THCMP='" + company + "' "
				+ " and THCON# = " +  str_contract;
						
			stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	

			result2=stmt2.executeQuery(SQLstatement2);
					 
		} catch (SQLException e) {
			
			System.err.println(e.getMessage());
			return false;
		}
        
        return (result2 != null);

	}

	public boolean getNext2() throws Exception {
        return result2.next();		
    }	

    public String getColumn2(String colNum) throws Exception {
        return result2.getString(colNum);
    }
    
    public void cleanup() throws Exception {
		endcurrentResultset(result);
		endcurrentResultset(result2);
		endcurrentStatement(stmt);
		endcurrentStatement(stmt2);
    }

}