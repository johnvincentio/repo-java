// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// Description:  Login account bean
// Date:	         09-10-2002     
// Developer:    Sima Zaslavsky, Robert Iacobucci	            
// *************************************************************************    
//   MODIFICATION INDEX
//   
//    Index    User Id    	Date      Project                            Desciption
//   -------  ---------  ---------- -----------------  ------------------------------------------------------------------
//    x001     DTC9028               SR26609 pre-load   Created new bean to validate new login parameters from the HERC website.
//    x002     DTC9028    04/10/03   SR28586 PCR7       Added Gold Service authentication 
//    RI003    DTC9028    06/21/05   SR31413 PCR19      Implement datasource
//    RI004    DTC2073    02/15/06   SR35873            Abbreviated Equipment Release
// ***************************************************************************************************************
package etrieveweb.etrievebean;

import java.util.*;
import java.sql.*;
import java.io.*; 
import javax.servlet.*;
import javax.servlet.http.*;

import javax.naming.*; 
import javax.sql.*;

import etrieveweb.utility.sqlBean;

public class loginacctBean extends sqlBean implements java.io.Serializable{

	private String userid = "";
	private String password = "";
	private String submit = "";
	private int count = 0;
	private ResultSet result=null;
	private Statement stmt=null;
	private Connection theconnection = null;

	// Constructor
	public loginacctBean() {
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


	// *************************************
	// Validate the Customer Number
	// *************************************

	public String validateCustomer(String asDriver,  String asURL,  String userID,  String password,  String datalib,  String company,  int cust_num) throws Exception, SQLException {

		synchronized (this) {

			try {

				theconnection = makeautoConnection(asDriver, asURL, userID, password);

				String SQLstatement=
					"select CMCUS# " 
						+ "from " 
						+ datalib + ".CUSMASFL " 
						+ " where CMCMP='" + company + "' and CMCUS#=" + cust_num; 
                
				stmt=conn.createStatement();

				result=stmt.executeQuery(SQLstatement);

				if ( !result.equals(null) ) {
					result.next();
					String testresult = result.getString("CMCUS#").trim();
					return testresult.trim();
				}

			} catch (SQLException sqle) {
				System.err.println(sqle.getMessage());
			} catch (Exception e) {
				System.err.println(e.getMessage());
			} finally {								// RI003
				endcurrentResultset(result);			// RI003
				endcurrentStatement(stmt);				// RI003
				endcurrentConnection(theconnection);	// RI003
			}											// RI003
      
			return ("");
		}

	}


	// ****************************************************
	//  Validate the Narp number  
	// ****************************************************

	public boolean validNarp(String asDriver,  String asURL,  String userID,  String password,  String datalib,  String company,  int cust_num) throws Exception, SQLException {

		synchronized (this) {
			
			try {
				
				theconnection = makeautoConnection(asDriver, asURL, userID, password);

				String SQLstatement=
							"select CMNATLCON# " + 
							"from " + datalib +  ".CUSMS2F2 " +  
      			 			" where CMCMP2 ='" + company +  
							"' and CMNATLCON# =" +  cust_num;   
   
				stmt=conn.createStatement();

				result=stmt.executeQuery(SQLstatement);

				if ( !result.equals(null) ) {
					result.next();
					String testresult = result.getString("CMNATLCON#"); 
					return true;
				}

			} catch (SQLException sqle) {
				System.err.println(sqle.getMessage());
				return false;					
			} catch (Exception e) {
				System.err.println(e.getMessage());
				return false;					
			} finally {								// RI003
				endcurrentResultset(result);			// RI003
				endcurrentStatement(stmt);				// RI003
				endcurrentConnection(theconnection);	// RI003
			}											// RI003
			
			return true;
		}
	}

	// ****************************************************
	//  Validate the Narp number or the Narp/Customer combination
	// ****************************************************

	public boolean validNarpCust(String asDriver,  String asURL,  String userID,  String password,  String datalib,  String company,  int narp_num, int cust_num) throws Exception, SQLException {

		synchronized (this) {

			try {

				theconnection = makeautoConnection(asDriver, asURL, userID, password);

				String SQLstatement=
					"select CMNATLCON# " + 
					"from " + datalib +  ".CUSMS2F2 " +  
					" where CMCMP2 ='" + company +  
					"' and CMNATLCON# =" +  narp_num;   

				if  (cust_num > 0) {
					SQLstatement=
						"select CMNATLCON# " + 
						"from " + datalib +  ".CUSMS2F2 " +  
						" where CMCMP2 ='" + company + "' and CMCUS#2=" + cust_num +
						" and CMNATLCON# =" +  narp_num;
				} 
   
				stmt=conn.createStatement();

				result=stmt.executeQuery(SQLstatement);

				if ( !result.equals(null) ) {
					result.next();
					String testresult = result.getString("CMNATLCON#"); 
					return true;
				}

			} catch (SQLException sqle) {
				System.err.println(sqle.getMessage());
				return false;	
			} catch (Exception e) {
				System.err.println(e.getMessage());
				return false;	
			} finally {								// RI003
				endcurrentResultset(result);			// RI003
				endcurrentStatement(stmt);				// RI003
				endcurrentConnection(theconnection);	// RI003
			}											// RI003
			
			return true;
		}

	}


	// ***********************************************************************
	//  Retrieve the Corplink number for the NARP number that was passed
	// ***********************************************************************

	public String getNarpCorplink(String asDriver,  String asURL,  String userID,  String password,  String datalib,  String company,  int narp_num) throws Exception, SQLException {

		synchronized (this) {

			try {

				theconnection = makeautoConnection(asDriver, asURL, userID, password);

				String SQLstatement=
					"select CMCRP# " + 
					"from " +  datalib + ".CUSMS2FL, " + 
					datalib + ".CUSMASFL " +  
					"where CMCMP2 = CMCMP " +			
					" and    CMCUS#2 = CMCUS#" + 
					" and CMCMP2 ='" + company  +			
					"' and CMNATLCON# =" +  narp_num;		

				stmt=conn.createStatement();

				result=stmt.executeQuery(SQLstatement);

				if ( !result.equals(null) ) {
					result.next();
					String testresult = result.getString("CMCRP#").trim();
					return testresult.trim();
				}

			} catch (SQLException sqle) {
				System.err.println(sqle.getMessage());
			} catch (Exception e) {
				System.err.println(e.getMessage());
			} finally {								// RI003
				endcurrentResultset(result);			// RI003
				endcurrentStatement(stmt);				// RI003
				endcurrentConnection(theconnection);	// RI003
			}											// RI003
      
			return ("");
		}

	}


	// **************************************
	//  Retrieve the customer corporate account 
	// **************************************

	public String getCustCorplink(String asDriver,  String asURL,  String userID,  String password,  String datalib,  String company,  int cust_num) throws Exception, SQLException {

		synchronized (this) {

			try {

				theconnection = makeautoConnection(asDriver, asURL, userID, password);

				String SQLstatement=
					"select CMCRP# " + 
					"from " + datalib + ".CUSMASF4 " +  
					" where CMCMP='" + company + "' and CMCRP#=" + cust_num + " and CMCUS#=" + cust_num; 
                	
				stmt=conn.createStatement();

				result=stmt.executeQuery(SQLstatement);

				if ( !result.equals(null) ) {
					result.next();
					String testresult = result.getString("CMCRP#").trim();
					return testresult.trim();
				}

			} catch (SQLException sqle) {
				System.err.println(sqle.getMessage());
			} catch (Exception e) {
				System.err.println(e.getMessage());
			} finally {								// RI003
				endcurrentResultset(result);			// RI003
				endcurrentStatement(stmt);				// RI003
				endcurrentConnection(theconnection);	// RI003
			}											// RI003
			
			return ("");
			
		}

	}


	// *****************************************
	//  Autheticate with the Herc Gold Service database
	// *****************************************

	public String []validGoldAcct(String  str_GoldCorpAcct,  String  str_GoldCorpPin,  String  str_GoldUserNam,   String  str_GoldUserPin,  String  str_GoldAcctTyp,   String  str_DBUserId,  String  str_DBPassword, String  str_DBUrl,  String  str_DBDriver,  String  str_DBCorporate,  String  str_DBPersonal  ) throws Exception, SQLException {
 
		synchronized (this) {

			String returnValues[] = {"","",""};		// RI004
			String str_Release_Extend = "";			// RI004
			String str_Release_Email  = "";			// RI004
			
			javax.sql.DataSource ds1 = null;		// RI004
			Connection theconnection1 = null;		// RI004
			Statement stmt1=null;					// RI004
			ResultSet result1 = null;				// RI004

			String authenticated         = "No";
			String str_databasename = "";

			try {

				// RI003	Context ctx = new InitialContext();
				// RI003	DataSource ds = null;
				// RI003	Class.forName( "com.microsoft.jdbc.sqlserver.SQLServerDriver" );
				
				javax.naming.InitialContext ctx = new javax.naming.InitialContext();	// RI003
				javax.sql.DataSource ds = null;										// RI003

				String SQLstatement = "";

				if (  str_GoldAcctTyp.equals("P")  )  {

					SQLstatement = 
						"select Account_Number  " + 
						"from Personal_Profiles  " +  
						"where Account_Number = '" +  str_GoldCorpAcct.trim()  +  "' " +		
						" and    User_Name = '" +  str_GoldUserNam.trim() + "' " +
						" and Personal_PIN_Code = '" +  str_GoldUserPin.trim()  +  "' ";

					str_databasename = str_DBPersonal;

					if  ( str_DBUrl.trim().equals("")  )													// RI003	
						ds  = (javax.sql.DataSource) ctx.lookup("java:comp/env/jdbc/HercWebUpdatedGold");	// RI003
					
				} else if ( str_GoldAcctTyp.equals("C")  )   {
						
					SQLstatement = 
						"select Account_Number, Release_Extend, Release_Extend_Email  " + 	// RI004
						"from Corporate_Profiles  " +  
						"where Account_Number = '" +  str_GoldCorpAcct.trim()  +   "' " +		
						" and Admin_User_Name = '" +  str_GoldUserNam.trim() + "' " +
						" and Admin_PIN_Code = '" +  str_GoldUserPin.trim()  +  "' ";

					str_databasename = str_DBCorporate;

					if  ( str_DBUrl.trim().equals("")  )													// RI003
						ds  = (javax.sql.DataSource) ctx.lookup("java:comp/env/jdbc/HercGoldCorporate");	// RI003
		                                                        
				}

				// ***********************************************************************
				//  Determine whether to use a datasource or the properties file values
				//  **********************************************************************

				if  ( str_DBUrl.trim().equals("")  )  {
					theconnection = ds.getConnection();	// RI003
				} else  {
					Class.forName( "com.microsoft.jdbc.sqlserver.SQLServerDriver" );	// RI003
					String str_connectionUrl = str_DBUrl.trim() + ";databasename=" + str_databasename.trim();
					theconnection = DriverManager.getConnection( str_connectionUrl,  str_DBUserId.trim(), str_DBPassword.trim()  );
				}

				// *****************
				// Run the query
				// *****************

				stmt=theconnection.createStatement();
 
				result=stmt.executeQuery(SQLstatement);

				if ( !result.equals(null) ) {
					
					result.next();
					
					String testresult = result.getString("Account_Number").trim();
					
					authenticated =  "Yes";
					
					// ***********************************************
					// RI004 - Retrieve extend/release parameters
					// ***********************************************
					
					if ( str_GoldAcctTyp.equals("C")  )									// RI004
					{																		// RI004
						 str_Release_Extend = result.getString("Release_Extend");			// RI004
						 str_Release_Email  = result.getString("Release_Extend_Email");		// RI004
					}																		// RI004
					else if ( str_GoldAcctTyp.equals("P")  )								// RI004  
					{																		// RI004

						if  ( str_DBUrl.trim().equals("") ) {  												// RI004											
							ds1  = (javax.sql.DataSource) ctx.lookup("java:comp/env/jdbc/HercGoldCorporate");	// RI004
							theconnection1 = ds1.getConnection();												// RI004
						} 																						// RI004
						else  																					// RI004
						{																						// RI004
							Class.forName( "com.microsoft.jdbc.sqlserver.SQLServerDriver" );					// RI004
							String str_connectionUrl = str_DBUrl.trim() + ";databasename=" + str_DBCorporate.trim();							// RI004
							theconnection1 = DriverManager.getConnection( str_connectionUrl,  str_DBUserId.trim(), str_DBPassword.trim() );		// RI004
						}						// RI004

					
						String SQLstatement1 = 														// RI004
							"select Account_Number, Release_Extend, Release_Extend_Email  " + 		// RI004
							"from Corporate_Profiles  " +  											// RI004
							"where Account_Number = '" +  str_GoldCorpAcct.trim()  +   "' " ;		// RI004		

						stmt1=theconnection1.createStatement();			// RI004
						
						result1 = stmt1.executeQuery(SQLstatement1);	// RI004							
						
						if ( result1 != null )							// RI004 
						{		
							result1.next();														// RI004
							str_Release_Extend = result1.getString("Release_Extend");			// RI004 		
							str_Release_Email  = result1.getString("Release_Extend_Email");		// RI004	
						}				// RI004		
						
					}		// RI004

					if (str_Release_Extend == null) str_Release_Extend = "";	// RI004
					if (str_Release_Email == null)  str_Release_Email = "";	// RI004
										
				}

			} catch (SQLException sqle) {
				System.err.println(sqle.getMessage());
				System.err.println(sqle);				// RI004
			} catch (Exception e) {
				System.err.println(e.getMessage());
				System.err.println(e);					// RI004
			} finally {								// RI003
				endcurrentResultset(result);			// RI003
				endcurrentStatement(stmt);				// RI003
				endcurrentConnection(theconnection);	// RI003
				
				if(result1 != null) endcurrentResultset(result1);					// RI004
				if(stmt1 != null) endcurrentStatement(stmt1);						// RI004
				if(theconnection1 != null) endcurrentConnection(theconnection1);	// RI004
				
			}											// RI003

			// RI004 return authenticated;
			
			returnValues[0] = authenticated;		// RI004
			returnValues[1] = str_Release_Extend;	// RI004
			returnValues[2] = str_Release_Email;	// RI004
			
			return returnValues;	// RI004
		}

	}


	// *****************************************
	// Clean up routine
	// *****************************************

	public void cleanup() throws Exception {

		try {
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	
	}


}