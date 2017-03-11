// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************
//   MODIFICATION INDEX 
//   
//    Index    User Id      Date       Project         Desciption
//    ------- ---------- ----------  ---------------- ------------------------------------------------
//    x001     DTC9028    02/11/03    SR26609 PCR1     Added RHORDB to the SQL
//    x002     DTC9028    02/11/03    SR26609 PCR1     Changed the ordered by to be RHDATO
//	  x003     DTC2073    08/19/04    SR31413 PCR5     Add the order by variable to the logic. 
//    RI004    DTC9028    07/28/05    SR31413 PCR19    Implement datasource modification
//    RI005    DTC2073    10/20/05    SR35420          Data Retrieval Limit  
// ****************************************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import java.util.*; 
import java.text.*; 
import etrieveweb.utility.sqlBean;

public class resvquoteBean extends sqlBean implements java.io.Serializable{

	String company= "";

	ResultSet result=null, result2=null;
	Statement stmt=null, stmt2=null;

	int cutOffDtVal = 0 ;  // RI005
	
	public resvquoteBean() {
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
  
		// ** Note: The previous hard-coded order by was RHDATO desc, RHCON# desc

		if ( !str_orderBy.trim().equals("") )
			str_orderBy = " order by " + str_orderBy;
			
		if ( jobnumber.equals("") )
			SQLstatement = 
				"select 'O', RHCON#, RHTYPE, 0, RHMNT3, RHDATO, RHERDT, RHPO#, RHJOB#, RHLOC, RHJOBL, RHORDB, ZLCITY, ZLST " 
				+ " from " 
				+ datalib 
				+ ".RAOHDRF2, " 
				+ datalib 
				+ ".SYSLOCFL " 
				+ " where (RHCMP='" 
				+ company 
				+ "' and RHCUS# in " 
				+ customer_list + ") and RHTYPE in ('R','X') " 
				+ " and (RHCMP=ZLCMP and RHLOC=ZLLOC) " 
				+ " and RHERDT  >= " + cutOffDtVal	+ " "	// RI005
				+ " union all " 
				+ " select 'C', RHCON#, RHTYPE, RHISEQ, RHSTTS, RHDATO, RHERDT, RHPO#, RHJOB#, RHLOC, RHJOBL, RHORDB, ZLCITY, ZLST " 
				+ " from " 
				+ datalib 
				+ ".RACHDRF4, " 
				+ datalib + ".SYSLOCFL " 
				+ " where (RHCMP='" 
				+ company 
				+ "' and RHCUS# in " 
				+ customer_list 
				+ ") and (RHTYPE='R' or (RHTYPE='X' and RHOTYP<>'S')) and RHSTTS NOT IN ('CN', 'FL')" 
				+ " and (RHCMP=ZLCMP and RHLOC=ZLLOC) " 
				+ " and RHERDT  >= " + cutOffDtVal	+ " "	// RI005
				+ str_orderBy;		   
		else  
			SQLstatement = 
				"select 'O', RHCON#, RHTYPE, 0, RHMNT3, RHDATO, RHERDT, RHPO#, RHJOB#, RHLOC, RHJOBL, RHORDB, ZLCITY, ZLST " 
				+ " from " 
				+ datalib 
				+ ".RAOHDRF2 " 
				+ datalib 
				+ ".SYSLOCFL " 
				+ " where (RHCMP='" 
				+ company 
				+ "' and RHCUS# in " 
				+ customer_list 
				+ ") and RHTYPE in ('R','X') and RHJOB#='" + jobnumber + "'" 
				+ " and (RHCMP=ZLCMP and RHLOC=ZLLOC) " 
				+ " and RHERDT  >= " + cutOffDtVal	+ " "	// RI005
				+ " union all " 
				+ " select 'C', RHCON#, RHTYPE, RHISEQ, RHSTTS, RHDATO, RHERDT, RHPO#, RHJOB#, RHLOC, RHJOBL, RHORDB, ZLCITY, ZLST " 
				+ " from " 
				+ datalib 
				+ ".RACHDRF4 " 
				+ datalib 
				+ ".SYSLOCFL " 
				+ " where (RHCMP='" 
				+ company 
				+ "' and RHCUS# in " 
				+ customer_list 
				+ ") and (RHTYPE='R' or (RHTYPE='X' and RHOTYP<>'S'))  and RHSTTS NOT IN ('CN', 'FL') and RHJOB#='" 
				+ jobnumber 
				+ "'" 
				+ " and (RHCMP=ZLCMP and RHLOC=ZLLOC) " 
				+ " and RHERDT  >= " + cutOffDtVal	+ " "	// RI005
				+ str_orderBy;
  
 
		// RI004	stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI004
		
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


	public String getColumnNum(int colNum) throws Exception {
		return result.getString(colNum);
	}


	public synchronized String getNumRows(String customer_list, int customer_num, String company, String datalib, String jobnumber) throws Exception {
  
		String SQLstatement2 = "";

		// **********************************************************************************
		// Calcluate the cust-off date to be current year plus two complete prior years
		// *********************************************************************************
		
		Calendar cutOffDt = Calendar.getInstance();		// RI005
		cutOffDt.add(Calendar.YEAR, -2 );				// RI005
		cutOffDtVal = (cutOffDt.get(Calendar.YEAR)*10000) + 100 + 01;	// RI005
		
		if ( jobnumber.equals("") )
			SQLstatement2 = 
				" select 'O', RHCON#, RHTYPE, 0, RHMNT3, RHDATO, RHERDT, RHPO#, RHJOB#, RHLOC, RHJOBL, RHORDB, ZLCITY, ZLST " 
				+ " from " 
				+ datalib 
				+ ".RAOHDRF2, " 
				+ datalib + ".SYSLOCFL " 
				+ " where (RHCMP='" 
				+ company 
				+ "' and RHCUS# in " 
				+ customer_list 
				+ ") and RHTYPE in ('R','X') " 
				+ " and (RHCMP=ZLCMP and RHLOC=ZLLOC) " 
				+ " and RHERDT  >= " + cutOffDtVal	+ " "	// RI005
				+ " union all " 
				+ " select 'C', RHCON#, RHTYPE, RHISEQ, RHSTTS, RHDATO, RHERDT, RHPO#, RHJOB#, RHLOC, RHJOBL, RHORDB, ZLCITY, ZLST " 
				+ " from " 
				+ datalib 
				+ ".RACHDRF4, " 
				+ datalib + ".SYSLOCFL " 
				+ " where (RHCMP='" 
				+ company 
				+ "' and RHCUS# in " 
				+ customer_list 
				+ ") and (RHTYPE='R' or (RHTYPE='X' and RHOTYP<>'S')) and RHSTTS NOT IN ('CN', 'FL')" 
				+ " and (RHCMP=ZLCMP and RHLOC=ZLLOC) "
				+ " and RHERDT  >= " + cutOffDtVal;		// RI005
		else 
			SQLstatement2 = 
				"select 'O', RHCON#, RHTYPE, 0, RHMNT3, RHDATO, RHERDT, RHPO#, RHJOB#, RHLOC, RHJOBL, RHORDB, ZLCITY, ZLST " 
				+ " from " 
				+ datalib 
				+ ".RAOHDRF2 " 
				+ datalib + ".SYSLOCFL " 
				+ " where (RHCMP='" 
				+ company 
				+ "' and RHCUS# in " 
				+ customer_list 
				+ ") and RHTYPE in ('R','X') and RHJOB#='" 	+ jobnumber + "'" 
				+ " and (RHCMP=ZLCMP and RHLOC=ZLLOC) " 
				+ " and RHERDT  >= " + cutOffDtVal	+ " "	// RI005
				+ " union all " 
				+ " select 'C', RHCON#, RHTYPE, RHISEQ, RHSTTS, RHDATO, RHERDT, RHPO#, RHJOB#, RHLOC, RHJOBL, RHORDB, ZLCITY, ZLST " 
				+ " from " 
				+ datalib 
				+ ".RACHDRF4 " 
				+ datalib 
				+ ".SYSLOCFL " 
				+ " where (RHCMP='" 
				+ company 
				+ "' and RHCUS# in " 
				+ customer_list 
				+ ") and (RHTYPE='R' or (RHTYPE='X' and RHOTYP<>'S'))  and RHSTTS NOT IN ('CN', 'FL') and RHJOB#='" + jobnumber + "'" 
				+ " and (RHCMP=ZLCMP and RHLOC=ZLLOC) "
				+ " and RHERDT  >= " + cutOffDtVal;		// RI005

		// RI004	stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	// RI004
		
		result2=stmt2.executeQuery(SQLstatement2);

		int totalrecords = 0;
  
		while ( result2.next() ) {
			totalrecords = result2.getRow();
		}

		return Integer.toString(totalrecords); 
	}


	public void cleanup() throws Exception {
		endcurrentResultset(result);	// RI004
		endcurrentResultset(result2);	// RI004
		endcurrentStatement(stmt);		// RI004
		endcurrentStatement(stmt2);		// RI004
	}

}