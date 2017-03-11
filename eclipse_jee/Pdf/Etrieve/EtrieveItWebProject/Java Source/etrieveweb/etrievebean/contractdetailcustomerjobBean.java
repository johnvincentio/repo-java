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
//    Index    User Id       Date       Project           Desciption
//    ------  ----------- -----------  ----------------  --------------------------------------------
//    x001     DTC2073     04/15/04     SR31413 PCR11     Added new methods to retrieve billing address from customer job file if applicable
//    RI002    DTC9028     08/01/05     SR31413 PCR19     Datasource implementation modification
// **************************************************************************************************************


package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import etrieveweb.utility.sqlBean;

public class contractdetailcustomerjobBean extends sqlBean implements java.io.Serializable{

	ResultSet result=null, result2=null, resultB=null;
	Statement stmt=null, stmt2=null, stmtB=null;


	public contractdetailcustomerjobBean() {
		super();
	}
 

	// ****************************************************************
	//     Retrieve ship to address from the CUSJOBFL file
	// ****************************************************************

	public synchronized boolean getRows(int customer_num, String company, String datalib, String jobnumber) throws Exception { 

		String SQLstatement = "";

		SQLstatement = 
			"select CJNAME, CJADR1, CJADR2, CJCITY, CJST, CJZIP, CJAREA, CJPHON " + 
			"from " + datalib + ".CUSJOBFL" +  
			" where CJCMP='" + company + "' and CJCUS#=" + customer_num + " and CJJOB#='" + jobnumber +"'";  
   
		try {
			
			// RI002	stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI002
			
			result=stmt.executeQuery(SQLstatement);
		
			return (result != null);

		} catch (SQLException e)   {
			System.err.println(e.getMessage());
			return false;
		}
	}


	public boolean getNext() throws Exception {
		return result.next();
	}


	public String getColumn(String colNum) throws Exception {
		return result.getString(colNum);
	}


	// ************************************************** 
	//    Retrieve bill to address from the CUSJBLFL file
	// **************************************************

	public synchronized boolean getRowsB(int customer_num, String company, String datalib, String jobnumber) throws Exception { 

		String SQLstatementB = "";

		SQLstatementB = 
			"select CINAME, CIADR1, CIADR2, CICITY, CIST, CIZIP, CIAREA, CIPHON " + 
			"from " + datalib + ".CUSJBLFL" +  
			" where CICMP='" + company + "' and CICUS#=" + customer_num + " and CIJOB#='" + jobnumber +"'";  
   
		try {
		
			// RI002	stmtB=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			stmtB=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI002
					
			resultB=stmtB.executeQuery(SQLstatementB);
			
			return (resultB != null);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

	public boolean getNextB() throws Exception {
		return resultB.next();
	}

	public String getColumnB(String colNum) throws Exception {
		return resultB.getString(colNum);
	}


	public void cleanup() throws Exception {
 		endcurrentResultset(result);	// RI002
		endcurrentResultset(resultB);	// RI002
		endcurrentStatement(stmt);		// RI002
		endcurrentStatement(stmtB);		// RI002
	}

}