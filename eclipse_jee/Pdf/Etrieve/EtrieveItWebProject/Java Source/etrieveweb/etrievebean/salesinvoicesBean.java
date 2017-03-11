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
//    Index    User Id       Date      Project           Desciption
//    ------  ----------- ----------- ----------------  ----------------------------------------------------------
//    x001     DTC9028     02/11/03    SR26609 PCR1      Added RHSYSD to the SQL
//    x002     DTC9028     02/11/03    SR26609 PCR1      Changed the ordered by to be RHSYSD
//    x002     DTC9028     02/12/03    SR26609 PCR1      Added RHSYSD to the SQL
//    x003     DTC9028     03/22/03    SR28586 PCR24     Added the RHSTTS <> 'CN' in the SQL
//	  x004     DTC2073     08/19/04    SR31413 PCR5      Add the order by variable to the logic. 
//    RI005    DTC9028     07/28/05    SR31413 PCR19     Implement datasource modification
//    RI006    DTC2073     10/21/05    SR35420    		 Data Retrieval Limit
// ***************************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import java.util.*; 
import java.text.*; 
import etrieveweb.utility.sqlBean;

public class salesinvoicesBean extends sqlBean implements java.io.Serializable{

	String company= "";

	ResultSet result=null, result2=null;
	Statement stmt=null, stmt2=null;

	int cutOffDtVal = 0 ;  // RI006

	public salesinvoicesBean() {
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
  
		// ** Note: The previous hard-coded order by was RHSYSD desc, RHCON# desc, RHISEQ desc

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;
			
		if ( jobnumber.equals("") )
			SQLstatement = 
				"select distinct RHCON#, RHISEQ, RHTYPE, RHOTYP, RHDATO, RHLRDT, RHSYSD, RHPO#, RHLOC, RHJOBL, RHAMT$ " 
				+ " from " 
				+ datalib 
				+ ".RACHDRH4 " 			// RI006
				+ " where RHCMP='" 
				+ company 
				+ "' and RHCUS# in " 
				+ customer_list 
				+ " and RHSYSD  >= "	// RI006 
				+ cutOffDtVal +   		// RI006 
				" and (RHTYPE='Q' or " 
				+ " (RHTYPE='F' and RHOTYP<>'C') or " 
				+ " (RHTYPE='S' and RHSTTS<>'HL' and RHSTTS<>'OP' and RHSTTS<>'CN'  )) " 
				+ str_orderBy;     
		else  
			SQLstatement = 
				"select distinct RHCON#, RHISEQ, RHTYPE, RHOTYP, RHDATO, RHLRDT, RHSYSD, RHPO#, RHLOC, RHJOBL, RHAMT$, RHMANI " 
				+ " from " 
				+ datalib 
				+ ".RACHDRH4 " 			// RI006
				+ " where RHCMP='" 
				+ company 
				+ "' and RHCUS# in " 
				+ customer_list 
				+ " and RHSYSD  >= "	// RI006 
				+ cutOffDtVal 			// RI006
				+ " and RHJOB#='" 
				+ jobnumber 
				+ "'" 
				+  " and (RHTYPE='Q' or " 
				+ " (RHTYPE='F' and RHOTYP<>'C') or " 
				+ " (RHTYPE='S' and RHSTTS<>'HL' and RHSTTS<>'OP'  and RHSTTS<>'CN' )) " 
				+ str_orderBy;
 
		// RI005	stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI005
		
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
		DecimalFormat df = new DecimalFormat("###,###,###,###.##");

		// **********************************************************************************
		// Calcluate the cust-off date to be current year plus two complete prior years
		// **********************************************************************************
		
		Calendar cutOffDt = Calendar.getInstance();		// RI006
		cutOffDt.add(Calendar.YEAR, -2 );				// RI006
		cutOffDtVal = (cutOffDt.get(Calendar.YEAR)*10000) + 100 + 01;	// RI006
		  
		if ( jobnumber.equals("") )
			SQLstatement2 = 
				"select distinct RHCON#, RHISEQ, RHTYPE, RHOTYP, RHDATO, RHLRDT, RHSYSD, RHPO#, RHLOC, RHJOBL, RHAMT$, RHMANI " 
				+ " from " 
				+ datalib 
				+ ".RACHDRH4 " 
				+ " where RHCMP='" 		// RI006
				+ company 
				+ "' and RHCUS# in " 
				+ customer_list 
				+ " and RHSYSD  >= "	// RI006 
				+ cutOffDtVal			// RI006 
				+ " and (RHTYPE='Q' or " 
				+ " (RHTYPE='F' and RHOTYP<>'C') or " 
				+ " (RHTYPE='S' and RHSTTS<>'HL' and RHSTTS<>'OP'  and RHSTTS<>'CN' ))";
		else 
			SQLstatement2 = 
				"select distinct RHCON#, RHISEQ, RHTYPE, RHOTYP, RHDATO, RHLRDT, RHSYSD, RHPO#, RHLOC, RHJOBL, RHAMT$, RHMANI " 
				+ " from " 
				+ datalib 
				+ ".RACHDRH4 " 			// RI006
				+ " where RHCMP='" 
				+ company 
				+ "' and RHCUS# in " 
				+ customer_list 
				+" and RHSYSD  >= "		// RI006 
				+ cutOffDtVal			// RI006 
				+ " and RHJOB#='" 
				+ jobnumber 
				+ "'" 
				+ " and (RHTYPE='Q' or " 
				+ "(RHTYPE='F' and RHOTYP<>'C') or " 
				+ " (RHTYPE='S' and RHSTTS<>'HL' and RHSTTS<>'OP'  and RHSTTS<>'CN' ))" ;

		// RI005	stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI005

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
		endcurrentResultset(result);	// RI005
		endcurrentResultset(result2);	// RI005
		endcurrentStatement(stmt);		// RI005
		endcurrentStatement(stmt2);		// RI005
	}

}