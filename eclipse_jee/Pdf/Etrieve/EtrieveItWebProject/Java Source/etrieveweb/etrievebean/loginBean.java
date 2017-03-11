// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************

package etrieveweb.etrievebean;

import java.util.*;
import java.sql.*;
import java.io.*; 
import etrieveweb.utility.sqlBean;

public class loginBean extends sqlBean implements java.io.Serializable{

  private String userid = "";
  private String password = "";
  private String submit = "";
  private int count = 0;
  private ResultSet result=null;
  private Statement stmt=null;

  // Constructor
  public loginBean() {
    super();
  }

  // get methods
  public String getUserid() {
    return userid;
  }
  public String getPassword() {
    return password;
  }
  public String getSubmit() {
    return submit;
  }
 
  // set methods 
  public void setUserid(String input) {
    userid=input;
  }
  public void setPassword(String input) {
    password=input;
  }
  public void setSubmit(String input) {
    submit=input;
  }


  // check the user if existed in file 
  public String authenticate(String userid, String password, String company, String datalib) throws Exception, SQLException {
    synchronized (this) {
      try {
        makeConnection();
        String SQLstatement="select ZSCUS# from " + datalib + ".SYSSECF2 where ZSCMP='" + company + "' and ZSUSER='" + userid.trim().toUpperCase() + "' and ZSEMP='" + password.trim().toUpperCase() + "'";                
        stmt=conn.createStatement();
        result=stmt.executeQuery(SQLstatement);
        if ( !result.equals(null) ) {
          result.next();
          return result.getString("ZSCUS#").trim();
        }
      }
      catch (SQLException sqle) {
        System.err.println(sqle.getMessage());
        System.out.println("SQL statement failed");
      }
      catch (Exception e) {
        System.err.println(e.getMessage());
        System.out.println("The record is not existed!");
      }
      return ("");
    }

  }



  // check if the user is corporate account 
  public String getCorpAccount(int customer_num, String company, String datalib) throws Exception, SQLException {
    synchronized (this) {
      try {
        makeConnection();
        String SQLstatement="select * " + 
			"from " + datalib + ".CUSMASF4 " +  
			" where CMCMP='" + company + "' and CMCRP#=" + customer_num + " and CMCUS#=" + customer_num; 
                
        stmt=conn.createStatement();
        result=stmt.executeQuery(SQLstatement);
        if ( result != null && result.next() )  
          return result.getString("CMCRP#");
        else 
          return "";   
      }
      catch (SQLException sqle) {
        System.err.println(sqle.getMessage());
        System.out.println("SQL statement failed");
      }
      catch (Exception e) {
        System.err.println(e.getMessage());
        System.out.println("The record is not existed!");
      }
      return ("");
    }

  }



  public void cleanup() throws Exception {
    try {
      stmt.close();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

}