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
import etrieveweb.utility.sqlBean;

public class eqhistorybyjobBean extends sqlBean implements java.io.Serializable{

 
ResultSet result=null, result2=null;
Statement stmt=null, stmt2=null;

public eqhistorybyjobBean() {
  super();
}
 
 
 
public synchronized boolean getRows(int customer_num, String company, String datalib, int start_num) throws Exception { 

  String SQLstatement = "select RDCATG, RDCLAS, ECDESC, sum(RDRDYS) as totalrentdays, sum(RDRHRS) as totalrenthours,count(rdcon#) as totalTransactions, sum(RDAMT$) as totalrentamount " + 
			"from " + datalib + ".RACDETF3 left outer join " + datalib + ".EQPCCMF1 " + 
			"on ECCMP= RDCMP and ECCATG=RDCATG and ECCLAS=RDCLAS" + 
			" where ECCMP='" + company + "' and RDTYPE='RI' and RDCUS#=" + customer_num +
			" group by RDCATG, RDCLAS, ECDESC order by RDCATG, RDCLAS";     

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


public synchronized String getNumRowsAll(int customer_num, String company, String datalib) throws Exception {
  String SQLstatement2 = "select count(*) as total_num_rows " + 
			 "from " + datalib + ".RACDETF3 left outer join " + datalib + ".EQPCCMF1 " + 
			 "on ECCMP= RDCMP and ECCATG=RDCATG and ECCLAS=RDCLAS" + 
			 " where ECCMP='" + company + "' and RDTYPE='RI' and RDCUS#=" + customer_num;
  
  stmt2=conn.createStatement();
  result2=stmt2.executeQuery(SQLstatement2);
  result2.next();
  return result2.getString("total_num_rows");
}

public synchronized String getNumRows(int customer_num, String company, String datalib) throws Exception {
  String SQLstatement2 = "select count(*) as total_num_rows " + 
			 "from " + datalib + ".RACDETF3 " + 
			 "where exists (select distinct RDCATG, RDCLAS, ECDESC " + 
			 "from " + datalib + ".EQPCCMF1 " + 
			 "on ECCMP= RDCMP and ECCATG=RDCATG and ECCLAS=RDCLAS" + 
			 " where ECCMP='" + company + "' and RDTYPE='RI' and RDCUS#=" + customer_num + ")";     

  stmt2=conn.createStatement();
  result2=stmt2.executeQuery(SQLstatement2);
  result2.next();
  return result2.getString("total_num_rows");
}

public void cleanup() throws Exception {
  stmt.close();
}

}