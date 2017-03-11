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

public class eqdetailinvoiceBean extends sqlBean implements java.io.Serializable{

ResultSet result=null, result2=null;
Statement stmt=null, stmt2=null;


public eqdetailinvoiceBean() {
  super();
}
 

 
public synchronized boolean getRows(String customer_list, int customer_num, String company, String datalib, String str_category, String str_class, String str_contract, String str_sequence) throws Exception { 

  String SQLstatement = "select RDDATO, RDLBDF, RDLBDT, RDRDYS, RDRHRS, RDAMT$ " + 
			"from " + datalib + ".RACDETF3 " +  
			" where RDCMP='" + company + "' and RDCATG=" + str_category + " and RDCLAS =" + 
					str_class + " and RDTYPE='RI' and RDCUS# in " + customer_list +
					" and RDCON#=" + str_contract + " and RDISEQ=" + str_sequence +
			" order by RDDATO desc";  
   
  stmt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
  result=stmt.executeQuery(SQLstatement);
  return (result != null);
}


public boolean getNext() throws Exception {
  return result.next();
}


public String getColumn(String colNum) throws Exception {
  return result.getString(colNum);
}


public synchronized String getNumRows(String customer_list, int customer_num, String company, String datalib, String str_category, String str_class, String str_contract, String str_sequence) throws Exception {
  String SQLstatement2 = "select count(*) as total_num_rows " + 
			"from " + datalib + ".RACDETF3 " +  
			" where RDCMP='" + company + "' and RDCATG=" + str_category + " and RDCLAS =" + 
					str_class + " and RDTYPE='RI' and RDCUS# in " + customer_list +
					" and RDCON#=" + str_contract + " and RDISEQ=" + str_sequence; 
  			  
  stmt2=conn.createStatement();
  result2=stmt2.executeQuery(SQLstatement2);
  result2.next();
  return result2.getString("total_num_rows");
}

public synchronized String getTotalAmount(String customer_list, int customer_num, String company, String datalib, String str_category, String str_class, String str_contract, String str_sequence) throws Exception {
  String SQLstatement2 = "select sum(RDAMT$) as total_amount " + 
			"from " + datalib + ".RACDETF3 " +  
			" where RDCMP='" + company + "' and RDCATG=" + str_category + " and RDCLAS =" + 
					str_class + " and RDTYPE='RI' and RDCUS# in" + customer_list +
					" and RDCON#=" + str_contract + " and RDISEQ=" + str_sequence;

  stmt2=conn.createStatement();
  result2=stmt2.executeQuery(SQLstatement2);
  result2.next();
  return result2.getString("total_amount");
}

public void cleanup() throws Exception {
  stmt.close();
}

}