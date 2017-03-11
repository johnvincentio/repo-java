// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// Description:  Retrieve Credit and Adjustment Amount for an invoice
// Date:	         11-22-2002     
// Developer:    Robert Iacobucci	            
// *************************************************************************    
//   MODIFICATION INDEX
//   
//    Index     User Id      Date       Project           Desciption
//   --------  ----------- ---------- -----------------  ----------------------------------------------------
//    x001      DTC9028                SR26609 PCR1       Retrieve the credit and adjustment amount.                                                                                                                
//    RI002     DTC9028     08/01/05   SR31413 PCR19      Datasource implementation modification
// ***************************************************************************


package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import etrieveweb.utility.sqlBean;

public class contractdetailCBean extends sqlBean implements java.io.Serializable{

	ResultSet result=null, result2=null;
	Statement stmt=null, stmt2=null;


	public contractdetailCBean() {
		super();
	}
 

	public String get_creditadj(String company, String datalib, int contract_num, int sequence_num) throws Exception { 

		String SQLstatement = "";

		SQLstatement = 
		"select  sum(HCINV$) as CRADJAMT " + 
		"from " + datalib + ".CRAHDRFL  " +  
		" where HCCMP='" + company + "' and HCINV#=" + contract_num +
		" and HCOISQ = " + sequence_num  +
		" and HCAORD = 'A' ";
 
		try {
       
			// RI002	stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI002
			
			result=stmt.executeQuery(SQLstatement);

			result.next();

			String testresult = result.getString("CRADJAMT"); 
			
			return testresult.trim();

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return "0.00";
		} catch (Exception e) {
			return "0.00";
		}

	}


	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI002
		endcurrentStatement(stmt);		// RI002
	}

}