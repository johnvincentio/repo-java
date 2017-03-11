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
//    Index      User Id         Date          Project                       Desciption
//    ---------   --------------  ------------   -----------------------    ----------------------------------------------------------
//    x001        DTC9028    11/26/02    SR26609 PCR1          Modification for the new log-in process
//    x002        DTC9028    01/24/02    SR26609 PCR1          Removed the join to RAPKUPF2 file 
//
// ***************************************************************************
package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import etrieveweb.utility.sqlBean;

public class opencontractsBean extends sqlBean implements java.io.Serializable{

String company= "";

ResultSet result=null, result2=null;
Statement stmt=null, stmt2=null;

public opencontractsBean() {
  super();
}


public String Company() {
	return company;
}
public void setCompany(String input) {
	company=input;
}
 
 
public synchronized boolean getRows(String customer_list, int customer_num, String company, String datalib, int start_num, String jobnumber) throws Exception { 
   
  String SQLstatement = "";
  
  if ( jobnumber.equals("") )
    SQLstatement = "select distinct RHCON#, RHDATO, RHERDT, RHPO#, RHLOC, RHJOBL, RHMANI , RHTYPE " + 
		   "from " + datalib + ".RAOHDRFL  " +  
		   " where RHCMP='" + company + "' and RHTYPE='O' and RHCUS# in " + customer_list +
		   " order by RHCON# desc";     
  else  
    SQLstatement = "select distinct RHCON#, RHDATO, RHERDT, RHPO#, RHLOC, RHJOBL, RHMANI, RHTYPE " + 
		   "from " + datalib + ".RAOHDRFL   " +  
		   " where RHCMP='" + company + "' and RHTYPE='O' and RHCUS# in " + customer_list + " and RHJOB#='" + jobnumber + "'" +
		   " order by RHCON# desc";       
 
  stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
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

 

public synchronized String getNumRows(String customer_list, int customer_num, String company, String datalib, String jobnumber) throws Exception {
  
  String SQLstatement2 = "";
   
  if ( jobnumber.equals("") )
    SQLstatement2 = "select distinct RHCON#, RHDATO, RHERDT, RHPO#, RHLOC, RHJOBL, RHMANI, RHTYPE " + 
		   "from " + datalib + ".RAOHDRFL   " +  
		         " where RHCMP='" + company + "' and RHTYPE='O' and RHCUS# in " + customer_list;
  else 
    SQLstatement2 = "select distinct RHCON#, RHDATO, RHERDT, RHPO#, RHLOC, RHJOBL, RHMANI, RHTYPE " + 
		   "from " + datalib + ".RAOHDRFL  " +  
		         "where RHCMP='" + company + "' and RHTYPE='O' and RHCUS# in " + customer_list + " and RHJOB#='" + jobnumber + "'"; 
 
  stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
  result2=stmt2.executeQuery(SQLstatement2);
  int totalrecords = 0;
  while ( result2.next() ) {
       totalrecords = result2.getRow();
  }
  return Integer.toString(totalrecords); 
}

public void cleanup() throws Exception {
  stmt.close();
}

}