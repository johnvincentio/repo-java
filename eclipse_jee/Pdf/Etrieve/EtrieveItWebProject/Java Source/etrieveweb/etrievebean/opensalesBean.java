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
//    Index     User Id       Date      Project            Desciption
//    -------  ----------  ----------- -----------------  -----------------------------------------------------
//    RI001     DTC9028     07/28/05    SR31413 PCR19      Implement datasource modification
//    RI002     DTC2073     10/19/05    SR35420		       Data Retrieval Limit
// *****************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import java.util.*; 
import java.text.*; 
import etrieveweb.utility.sqlBean;

public class opensalesBean extends sqlBean implements java.io.Serializable{

	String company= "";

	ResultSet result=null, result2=null;
	Statement stmt=null, stmt2=null;

	public opensalesBean() {
		super();
	}


	public String Company() {
		return company;
	}
	
	public void setCompany(String input) {
		company=input;
	}
 
 
	public synchronized boolean getRows(String customer_list, int customer_num, String company, String datalib, int start_num, String jobnumber, String str_orderBy) throws Exception { 
   
		String SQLstatement = "";
  
		// ** Note: The previous hard-coded order by was RHCON# desc, RHISEQ desc

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;
		
		if ( jobnumber.equals("") )
			SQLstatement = 
				"select distinct RHCON#, RHISEQ, RHTYPE, RHOTYP, RHDATO, RHLRDT, RHSYSD, RHPO#, RHLOC, RHJOBL, RHAMT$ " + 
					"from " + datalib + ".RACHDRF4 " +		// RI002  
					"where RHCMP='" + company + "' and RHCUS# in " + customer_list + 
					" and RHTYPE='S' and (RHSTTS='HL' or RHSTTS='OP')" + " " + str_orderBy;
    
		else  
			SQLstatement = 
				"select distinct RHCON#, RHISEQ, RHTYPE, RHOTYP, RHDATO, RHLRDT, RHSYSD, RHPO#, RHLOC, RHJOBL, RHAMT$, RHMANI " + 
					"from " + datalib + ".RACHDRF4 " +		// RI002  
					"where RHCMP='" + company + "' and RHCUS# in " + customer_list + " and RHJOB#='" + jobnumber + "'" +
					" and RHTYPE='S' and (RHSTTS='HL' or RHSTTS='OP')" + " " + str_orderBy;
 
 
		// RI001	stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI001
		
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


	public synchronized String[] getNumRows(String customer_list, int customer_num, String company, String datalib, String jobnumber) throws Exception {
  
		String SQLstatement2 = "";
		double TotalAmount = 0.0;
		String[] Array = {"", ""};
		DecimalFormat df = new DecimalFormat("###,###.##");

   
		if ( jobnumber.equals("") )
			SQLstatement2 = 
				"select distinct RHCON#, RHISEQ, RHTYPE, RHOTYP, RHDATO, RHLRDT, RHSYSD, RHPO#, RHLOC, RHJOBL, RHAMT$, RHMANI " + 
					"from " + datalib + ".RACHDRF4 " +		// RI002  
					"where RHCMP='" + company + "' and RHCUS# in " + customer_list + 
					" and RHTYPE='S' and (RHSTTS='HL' or RHSTTS='OP')";
		else 
			SQLstatement2 = 
				"select distinct RHCON#, RHISEQ, RHTYPE, RHOTYP, RHDATO, RHLRDT, RHSYSD, RHPO#, RHLOC, RHJOBL, RHAMT$, RHMANI " + 
					"from " + datalib + ".RACHDRF4 " +		// RI002  
					"where RHCMP='" + company + "' and RHCUS# in " + customer_list + " and RHJOB#='" + jobnumber + "'" +
					" and RHTYPE='S' and (RHSTTS='HL' or RHSTTS='OP')";

		// RI001	stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI002
		
		result2=stmt2.executeQuery(SQLstatement2);

		int totalrecords = 0;

		while ( result2.next() ) {
			TotalAmount += Double.valueOf(result2.getString("RHAMT$")).doubleValue();
			totalrecords = result2.getRow();
		}
		
		Array[0] = Integer.toString(totalrecords); 
		Array[1] = df.format(TotalAmount);

		return Array;
	}



	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI001
		endcurrentResultset(result2);	// RI001
		endcurrentStatement(stmt);		// RI001
		endcurrentStatement(stmt2);		// RI001
	}

}