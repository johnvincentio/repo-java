// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// **************************************************************************************************************** 

package etrieveweb.etrievebean;

import java.sql.*;
import java.io.*; 
import java.util.*; 
import java.text.*; 
import etrieveweb.utility.sqlBean;

public class rentalreturnsBean extends sqlBean implements java.io.Serializable{

String company= "";

ResultSet result=null, result2=null;
Statement stmt=null, stmt2=null;

public rentalreturnsBean() {
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
    SQLstatement = "select distinct RHCON#, RHISEQ, RHTYPE, RHOTYP, RHDATO, RHLRDT, RHPO#, RHLOC, RHJOBL, RHAMT$, RHMANI " + 
		   "from " + datalib + ".RACHDRFL " +  
		   "where RHCMP='" + company + "' and RHCUS# in " + customer_list + 
                   " and (RHTYPE='E' or RHTYPE='C' or (RHTYPE='O' and RHOTYP<>'C'))" +
		   " order by RHCON# desc, RHISEQ desc";     
  else  
    SQLstatement = "select distinct RHCON#, RHISEQ, RHTYPE, RHOTYP, RHDATO, RHLRDT, RHPO#, RHLOC, RHJOBL, RHAMT$, RHMANI " + 
		   "from " + datalib + ".RACHDRFL " +  
		   "where RHCMP='" + company + "' and RHCUS# in " + customer_list + " and RHJOB#='" + jobnumber + "'" +
                   " and (RHTYPE='E' or RHTYPE='C' or (RHTYPE='O' and RHOTYP<>'C'))" +
		   " order by RHCON# desc, RHISEQ desc";     
 
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



public synchronized String[] getNumRows(String customer_list, int customer_num, String company, String datalib, String jobnumber) throws Exception {
  
  String SQLstatement2 = "";
  double TotalAmount = 0.0;
  String[] Array = {"", ""};
  DecimalFormat df = new DecimalFormat("###,###.##");

   
  if ( jobnumber.equals("") )
    SQLstatement2 = "select distinct RHCON#, RHISEQ, RHTYPE, RHOTYP, RHDATO, RHLRDT, RHPO#, RHLOC, RHJOBL, RHAMT$, RHMANI " + 
		    "from " + datalib + ".RACHDRFL " +  
		    "where RHCMP='" + company + "' and RHCUS# in " + customer_list + 
                    " and (RHTYPE='E' or RHTYPE='C' or (RHTYPE='O' and RHOTYP<>'C'))";
  else 
    SQLstatement2 = "select distinct RHCON#, RHISEQ, RHTYPE, RHOTYP, RHDATO, RHLRDT, RHPO#, RHLOC, RHJOBL, RHAMT$, RHMANI " + 
		    "from " + datalib + ".RACHDRFL " +  
		    "where RHCMP='" + company + "' and RHCUS# in " + customer_list + " and RHJOB#='" + jobnumber + "'" +
                    " and (RHTYPE='E' or RHTYPE='C' or (RHTYPE='O' and RHOTYP<>'C'))";

  stmt2=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
  stmt.close();
}

}