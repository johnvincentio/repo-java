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
//    Index     User Id      Date      Project            Desciption
//   -------   ---------- ----------- -----------------  ----------------------------------------------------------
//    RI001     DTC9028    07/28/05    SR31413 PCR19      Implement datasource modification
// ****************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import etrieveweb.utility.sqlBean;

public class joblistingBean extends sqlBean implements java.io.Serializable{


	ResultSet result=null, result2=null;
	Statement stmt=null, stmt2=null;

	public joblistingBean() {
		super();
	}

 
	public synchronized boolean getRows(String customer_list, int customer_num, String company, String datalib, int start_num) throws Exception { 

		String SQLstatement = 
			"select distinct(CJJOB#), CJJLOC, CJCOM, CJADR1, CJCITY, CJAREA, CJPHON " + 
			"from " + datalib + ".CUSJOBFL " +  
			" where CJCMP='" + company + "' and CJCUS# in " + customer_list + 
			" order by CJJOB#";          
                
		// RI001	stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		result=stmt.executeQuery(SQLstatement);

		if ( start_num > 0 )
			result.absolute(start_num);

		return (result != null);
	}


	public boolean getNext() throws Exception {
		return result.next();
	}


	public String getColumn(String colNum) throws Exception {
		return result.getString(colNum);
	}


	public synchronized String getNumRows(String customer_list, int customer_num, String company, String datalib) throws Exception {

		String SQLstatement2 = 
			"select count(distinct CJJOB#) as total_num_rows " + 
			"from " + datalib + ".CUSJOBFL " +  
			" where CJCMP='" + company + "' and CJCUS# in " + customer_list; 
  
		stmt2=conn.createStatement();

		result2=stmt2.executeQuery(SQLstatement2);

		result2.next();
		
		return result2.getString("total_num_rows");
	}


	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI001
		endcurrentResultset(result2);	// RI001
		endcurrentStatement(stmt);		// RI001
		endcurrentStatement(stmt2);		// RI001
	}

}