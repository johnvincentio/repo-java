/// *****************************************************************************************************************
// Copyright (C) 2005 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
// Description:  Account validation of a POST from the HERC web server 
// Date:	         09-11-2002
// Developer:    Sima Zaslavsky, Robert Iacobucci	            
// **************************************************************************         
//   MODIFICATION INDEX
//   
//    Index     User Id        Date       Project              Desciption
//    -------  -----------  -----------  -------------------  ----------------------------------------------------
//    x001      DTC9028      11/26/02    SR26609 pre-load     New login procedure  
//    x002      DTC9028      01/13/03    SR26609 pre-load     Retrieve connection properties   
//    x003      DTC9028      01/13/03    SR26609 pre-load     Added decryption methods  
//    x004      DTC9028      04/10/03    SR28586 PCR7         Added Gold Service authentication 
//    x005      DTC9028      04/28/03    SR28586 PCR1         Added the variable login logic
//    x006      DTC9028      07/15/03    SR28586 PCR1         Accounted for submit button not pressed
//    x007      DTC9028      09/19/03    SR31413 PCR22        Added logic to override to the training lib (USA only)  
//    x008      DTC2073      12/09/04    SR31413 PCR25        Send custom message to securityFailure page  
//    RI009     DTC2073      02/09/05    SR31413 PCR19        Implement the datasource connection
//    RI010     DTC9028      09/14/05    SR31413 PCR19        Initialize the demo account logic
//    RI011     DTC2073      02/15/06    SR35873              Abbreviated Equipment Release
// ******************************************************************************************************************
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;

import etrieveweb.etrievebean.loginBean;
import etrieveweb.etrievebean.loginacctBean;
import etrieveweb.etrievebean.sysctlflBean;    // RI009

public class EtrieveWebValidationServlet extends HttpServlet {

    /*****************************************************
    * Implement the HTTP do method by building a simple web page.
	*****************************************************/

	public void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {

		loginBean loginbean = new loginBean();
		loginacctBean loginacctbean = new loginacctBean();
		sysctlflBean sysctlflbean = new sysctlflBean();   // RI009
		
		String sCodeKey    = "CSCode";
		String sDatalibKey = "CSDatalib";
		String srbName  = "EtrieveIt";
		String sPathKey = "CSPath";
		String sDriverKey  = "CSDriver";            // x002
		String sPasswordKey = "CSPassword";     // x002
		String sURLKey = "CSURL";               // x002
		String sUserIDKey  = "CSUserID";          // x002
		String sDBUserId   = "DBUserId";          // x004
		String sDBPassword = "DBPassword";    // x004
		String sDBUrl  = "DBUrl";                // x004
		String sDBDriver  = "DBDriver";           // x004
		String sDBCorporate = "DBCorporate";    // x004
		String sDBPersonal  = "DBPersonal";     // x004
		String str_company = new String();
		String str_datalib = new String();
		String str_path = new String();
		String str_userid = new String();
		String str_password = new String();
		String str_as400driver = new String();
		String str_as400url = new String();
		String str_customer = new String();
		String str_corporate = new String();
		String str_AS400name = new String();       // RI009
		String parm_value = new String();
		String str_GoldCorpAcct= new String();
		String str_GoldCorpPin = new String();
		String str_GoldUserNam = new String();
		String str_GoldUserPin = new String();
		String str_GoldAcctTyp = new String();
		String str_GoldUserValid = new String();
		String str_GoldUSAAcct = new String();
		String str_GoldCANAcct = new String();
		String str_GoldCountry = new String();
		String str_DBUserId  = new String();
		String str_DBPassword = new String();
		String str_DBUrl  = new String();
		String str_DBDriver  = new String();
		String str_DBCorporate = new String();
		String str_DBPersonal = new String();
		String str_IncludeCredits = new String();			// RI009
		String str_isUsingInternational = new String();	// RI009
		String str_demoAccount = new String();				// RI010
		boolean useDatasource = false;					// RI009

		String loginuserid = "";
		String loginpassword = "";
		String submitbutton = "";
		String custserv = "";
		
		int  custAcct = 0;
		int  narpAcct = 0;

		// *************************************************************************************************
		// RI011 - The goldServiceInfo will contain three elements.
		// Element 1 = whether the account is a valid gold service account (Y/N)
		// Element 2 = whether the corporate account has authority to request releases/extentions
		// Element 3 = the address in the Gold Service database to send e-mail (if applicable)
		// *************************************************************************************************
		
		String [] goldServiceInfo = {"","",""}	;		// RI011
		String str_smtpServer = "";						// RI011
		
		// ************************************************************
		//  Get the PropertyResourceBundle
		//      1. Builds the path to the properties file   
		//      2. Creates an empty property list with no default.  
		//      3. Create a connection to the actual file and then loads the file
		// ************************************************************

		try   {

			String str_root = this.getServletContext().getRealPath("");

			File path = new File( str_root, "WEB-INF//classes//conf//EtrieveIt.properties"); 

			Properties settings = new Properties();

			FileInputStream sf = new FileInputStream(path);

			settings.load(sf);

			str_company = settings.getProperty(sCodeKey);
			str_datalib = settings.getProperty(sDatalibKey).trim();   // x003
			str_path = settings.getProperty(sPathKey);

		} catch (Exception e) // error

		{
			System.err.println("Failed to load resource properties.");
			return;

		} // end catch

		// ************************************************************
		//  Get the PropertyResourceBundle for the connection to the AS/400
		//      1. Retrieve the driver   
		//      2. Retrieve user id and password
		//      3. Retrieve server url
		// ************************************************************

		try   {    // x002

			srbName = "etrieveweb.utility.Connection";
			Properties settings = new Properties();

			URL path = this.getClass().getClassLoader().getResource("conf//Connection.properties"); 

			FileInputStream sf  = new FileInputStream(path.getPath());
			settings.load(sf);

			str_userid  = settings.getProperty(sUserIDKey).trim();			// x003
			str_password  = settings.getProperty(sPasswordKey).trim();		// x003
			str_as400driver = settings.getProperty(sDriverKey).trim();		// x003
			str_as400url  = settings.getProperty(sURLKey).trim();			// x003

		} catch (Exception e) // error   
			{
				response.sendRedirect(str_path + "securityFailure.jsp");
				System.err.println("Failed to load connection properties.");
				return;
			} // end catch

		// ****************************************************************
		// Determine if the request came from the customer service variable login application.  
		// If so, it will be assumed that an internal customer service rep has already logged in
		// and is trying to access a customer number.  The variable login application will 
		// pass only the customer number.  The standard customer authentication step will 
		// be ignored.
		// A change was made to account for the Enter key being pressed instaed of the submit 
		// button while in the Retrieve Customer page. 
		// ****************************************************************

		submitbutton = request.getParameter("submitbutton");
		
		custserv  = request.getParameter("custserv");

		if ( submitbutton == null )
			submitbutton = "";

		if ( custserv == null )
			custserv = "";

		if ( custserv.equals("a2b4c6d8e0")  && submitbutton.equals("")  ) 
			submitbutton = "Retrieve Customer";

		if  (  submitbutton.equals("Retrieve Customer")  )  {
			HttpSession sessionA = request.getSession(true);
			loginuserid  = (String) sessionA.getValue("loginuserid");
			loginpassword = (String) sessionA.getValue("loginpassword");
			if ( loginuserid == null )
				loginuserid = "";
			if ( loginpassword == null )
				loginpassword = "";
		}


		// ************************************************************
		//  Retrieve the parameters sent from the Gold Service Login.  Once received, 
		//  authenticate with the Gold Service database with the parameters that were 
		//  passed.  This step is to ensure security.
		// ************************************************************

		try {
			str_GoldCorpAcct  = request.getParameter("corpacct").trim();
		} catch (Exception e)
			{
				str_GoldCorpAcct = "";
		}


		try {
			str_GoldCorpPin  = request.getParameter("corppin").trim();
		} catch (Exception e)  {
			str_GoldCorpPin = "";
		}


		try {
			str_GoldUserNam  = request.getParameter("usernam").trim();
		} catch (Exception e)  {
			str_GoldUserNam = "";
		}


		try {
			str_GoldUserPin   = request.getParameter("userpin").trim();
		} catch (Exception e)  {
			str_GoldUserPin = "";
		}


		try {
			str_GoldAcctTyp  = request.getParameter("accttype").trim();
		} catch (Exception e)  {
			str_GoldAcctTyp = "";
		}

		try {
			str_GoldUSAAcct  = request.getParameter("us_acc_num").trim();
		} catch (Exception e)  {
			str_GoldUSAAcct = "";
		}

		try {
			str_GoldCANAcct  = request.getParameter("ca_acc_num").trim();
		} catch (Exception e)  {
			str_GoldCANAcct = "";
		}

		try {
			str_GoldCountry  = request.getParameter("country_code").trim();
		} catch (Exception e)  {
			str_GoldCountry = "";
		}


		// *************************************************************************
		// RI010 - Determine if the DEMO functionality is being triggered (Domestic only)
		// *************************************************************************
		
		try {	// RI010

			if ( str_GoldCANAcct.trim().equals("DEMO") )	// RI010
				str_demoAccount = "Y";						// RI010

		} catch (Exception e)  {							// RI010
			System.err.println("Could not determine if a demo account");	// RI010
		}	// RI010

		// *********************************************************************************
		// RI009 - Retrieve the flag from the SYSCTLFL that determines whether to make a
		// connection using the datasource or use the properties file parameters.                                          
		// *********************************************************************************
		
		try {		// RI009

			useDatasource = sysctlflbean.getConnectionFlag(str_demoAccount);	// RI009, RI010

			if ( useDatasource ) {	// RI009
		
				String[] sysctlfl_array = sysctlflbean.getProperties(str_demoAccount);	// RI009, RI010
			
				str_company = sysctlfl_array[0];				// RI009
				str_IncludeCredits 	= sysctlfl_array[1];		// RI009
				str_isUsingInternational = sysctlfl_array[2];	// RI009
				str_path = sysctlfl_array[3];					// RI009
				str_datalib = sysctlfl_array[4];				// RI009
				str_AS400name = sysctlfl_array[5];				// RI009
				str_userid 	= sysctlfl_array[6];				// RI009
				str_smtpServer = sysctlfl_array[7];				// RI011
	
				str_password = "";				// RI009
				str_as400driver = "";			// RI009
				str_as400url = "";				// RI009

				if ( str_path.equals("") )		// RI009
					str_path = "../";			// RI009
					
			}		// RI009
									
		} catch (Exception e) {										// RI009
			str_path = "../";											// RI009
			response.sendRedirect(str_path + "securityFailure.jsp");	// RI009
			System.out.println("Failed to load RentalMan properties parameters");		// RI009
			return;																	// RI009
		}				// RI009
		

		// ********************************************************************************
		// Check if the customer is authorized to access the Etrieve-It reports.
		// If there are any alphabetic values in the "str_GoldUSAAcct" or "str_GoldCANAcct" 
		// variables (except "DEMO" and "DEMOM") then re-direct the security failure page
		// ********************************************************************************
		
		try {

			int cusno = 0;
						
			if ( (!str_GoldUSAAcct.trim().equals("")) )
				cusno = Integer.parseInt(str_GoldUSAAcct);

			if ( 
				(!str_GoldCANAcct.trim().equals("")) &&  
				(!str_GoldCANAcct.trim().equals("DEMO")) && 
				(!str_GoldCANAcct.trim().equals("DEMOM")) &&
				(!str_GoldCANAcct.trim().equals("EMAIL")) )		// RI011
					cusno = Integer.parseInt(str_GoldCANAcct) ;
						
		}  catch (NumberFormatException e)  {
				String errorMsg = "You do not have access to this function.  Please contact your administrator.";	
				request.setAttribute("errorMsg",errorMsg);
			 	getServletContext().getRequestDispatcher("securityFailure.jsp").forward(request,response);
				return ;
		}
		

		// ************************************************************
		//  Get the PropertyResourceBundle for the connection to the SQL Server 
		//      1. Retrieve the driver   
		//      2. Retrieve user id and password
		//      3. Retrieve server url
		// ************************************************************

		if  ( loginuserid.equals("")  &&  loginpassword.equals("")  &&  !submitbutton.equals("Retrieve Customer")  )   {

			try   {     

				srbName = "etrieveweb.utility.Connection";
				Properties settings = new Properties();

				URL path  = this.getClass().getClassLoader().getResource("conf//Connection.properties"); 

				FileInputStream sf  = new FileInputStream(path.getPath());
				settings.load(sf);

				if ( !useDatasource ) {		// RI009
					str_DBUserId  = settings.getProperty(sDBUserId).trim();        // x004
					str_DBPassword  = settings.getProperty(sDBPassword).trim();  // x004
					str_DBUrl = settings.getProperty(sDBUrl).trim();              // x004
					str_DBDriver = settings.getProperty(sDBDriver).trim();         // x004
					str_DBCorporate = settings.getProperty(sDBCorporate).trim();   // x004
					str_DBPersonal = settings.getProperty(sDBPersonal).trim() ;      // x004
				}	//RI009
				
			} catch (Exception e) {
				System.err.println("Failed to load connection properties for Gold database.");
			}  
		}


		// *************************************************************
		// Authenticate the user who is logging in.  Note:  If the request came from the 
		// customer service variable login application, the actual authentication step 
		// will be ignored.
		// *************************************************************

		try {

			if ( str_GoldCorpAcct.equals("")  )  
				str_GoldUserValid = "No";
			else if  ( !loginuserid.equals("")  &&  !loginpassword.equals("")  &&  submitbutton.equals("Retrieve Customer")  )
				str_GoldUserValid = "Yes";
			else if ( str_GoldAcctTyp.equals("")   )  
				str_GoldUserValid = "No";
			else	{	
				goldServiceInfo = loginacctbean.validGoldAcct( str_GoldCorpAcct,  str_GoldCorpPin,  str_GoldUserNam,  str_GoldUserPin,  str_GoldAcctTyp,  str_DBUserId, str_DBPassword, str_DBUrl, str_DBDriver, str_DBCorporate, str_DBPersonal );
				str_GoldUserValid = goldServiceInfo[0];		// RI011
			}
	
		} catch (Exception e)  {
				str_GoldUserValid = "No";
		}

	 
		if (  !str_GoldUserValid.equals("Yes")  )  {
			response.sendRedirect(str_path + "securityFailure.jsp");
			System.err.println("Gold Service user was not authenticated.");
			return;
		}
                 

		// *************************************************************
		// Check if the OPTIONAL napr# parameter was passed.  If so convert to an 
		// integer type and store the value in the customer number field.
		// *************************************************************


		try {

			parm_value = request.getParameter("narpnumber").trim();

			if (!parm_value.equals("")) 
				narpAcct = Integer.valueOf(parm_value).intValue();
                 
		} catch (Exception e)  {
			narpAcct  = 0;
		}



		// *************************************************************
		// Determine which account to use...Domestic or Canada...if coming from the 
		// HERC Gold Service page
		// *************************************************************

		try {

			if (!str_GoldCorpAcct.trim().equals("")  &&   ( !str_GoldUSAAcct.equals("")  ||  !str_GoldCANAcct.equals("") )  )  { 

				if  ( str_company.trim().equals("HG") ) {
					str_GoldCorpAcct = str_GoldUSAAcct;
				}

				if  ( str_company.trim().equals("CR") )  {
					str_GoldCorpAcct = str_GoldCANAcct;
				}

			}

		} catch (Exception e)  {
			response.sendRedirect(str_path + "securityFailure.jsp");
			System.err.println("Invalid Domestic/Canadian Gold Service Corporate Account.");
			return;
		}


		// *************************************************************
		// Convert the Gold Account number parameter into an integer type.  Convert 
		// to an integer type and store the value in the customer number field.
		// *************************************************************

		try {

			if (!str_GoldCorpAcct.equals("")) 
				custAcct = Integer.valueOf(str_GoldCorpAcct).intValue();
                 
		} catch (Exception e)  {
			response.sendRedirect(str_path + "securityFailure.jsp");
			System.err.println("Invalid Gold Service Corporate Account.");
			return;
		}


		// *************************************************************
		// If  the customer number is blank, display the error page   
		// *************************************************************

		if (custAcct == 0 && narpAcct == 0)  {
			response.sendRedirect(str_path + "securityFailure.jsp");
			System.err.println("No account number given.");
			return;
		}


		// ******************************************************************
		//  Retrieve the session Id and invalidate the current session, if applicable.  Create a new
		//   session.  If an error occurs, display a error page
		// ******************************************************************

		try {

			HttpSession session = request.getSession(true);
			session.invalidate();
			session = request.getSession(true);
			String theid = (String) session.getId();


			if ( theid.equals( "" )  ||  theid.equals( "null" ) )
				response.sendRedirect(str_path + "securityFailure.jsp");
			else {

				// ***************************************************
				// Determine if important login variables have been passed  
				// ***************************************************

				// RI009	if ( str_userid.trim().equals("")       ||   
				// RI009	str_password.trim().equals("")      ||   
				// RI009	str_as400driver.trim().equals("")   ||    
				// RI009	str_as400url.trim().equals("")      ||   
				// RI009	str_datalib.trim().equals("")       ||    
				// RI009	str_company.trim().equals("")   )  {
				// RI009		response.sendRedirect(str_path + "securityFailure.jsp");
				// RI009		System.err.println("No login parameters were supplied.");
				// RI009		return;
				// RI009	}

				// ****************************************************************
				// RI009 - Determine if important login variables have been passed.
				// Check only if using the connection parameters from the
				// properties files.  However, if a datasource connection
				// is being done, then do not perform the check.
				// ****************************************************************

				String parmError = "";						// RI009
				
				if ( str_userid.trim().equals("") )		// RI009
					parmError = "Y";						// RI009
					
				else if (str_datalib.trim().equals("") )	// RI009
					parmError = "Y";						// RI009
					
				else if ( str_company.trim().equals("") )	// RI009
					parmError = "Y";						// RI009
									
				else if ( str_AS400name.trim().equals("") && (str_password.trim().equals("") || str_as400driver.trim().equals("") || str_as400url.trim().equals("") ) ) 	// RI009
						parmError = "Y";					// RI009
				
				if ( parmError.equals("Y") ) {									// RI009
					response.sendRedirect(str_path + "securityFailure.jsp");	// RI009
					System.err.println("No login parameters were supplied.");	// RI009
					return;													// RI009
				}																// RI009

				// *********************************************************************************
				// RI011 - Determine if the e-mail functionality should be turned on while testing
				// *********************************************************************************
		
				try {	// RI011
					if ( str_company.trim().equals("HG") && str_GoldCANAcct.trim().equals("EMAIL") )		// RI011
						goldServiceInfo[1] = "Yes";															// RI011
					else if ( str_company.trim().equals("CR") && str_GoldUSAAcct.trim().equals("EMAIL") )	// RI011
						goldServiceInfo[1] = "Yes";															// RI011
				} catch (Exception e)  {											// RI011
					System.err.println("Error occured when overriding e-mail authority flag");	// RI011
				}	
		
				// ***************************************************
				// Retrieve form variables and place them in session variables
				// ***************************************************

				//*****x002   str_userid            =  request.getParameter("userid").toUpperCase();
				//*****x002   str_password      = request.getParameter("password").toUpperCase();
				//*****x002   str_as400driver   =  request.getParameter("as400driver");
				//*****x002   str_as400url        =  request.getParameter("as400url");
				//*****x002   str_datalib           = request.getParameter("as400library").toUpperCase();
				//*****x002   str_company      = request.getParameter("companycode").toUpperCase();


				String[] names = session.getValueNames();

				session.putValue("username", str_userid);
				session.putValue("password", str_password);
				session.putValue("as400driver", str_as400driver);
				session.putValue("as400url", str_as400url);
				session.putValue("datalib", str_datalib);
				session.putValue("company", str_company);
				session.putValue("nationalyn", "");
				session.putValue("loginuserid", "" ); 
				session.putValue("loginpassword", "" ); 
				session.putValue("IncludeCredits", str_IncludeCredits);					// RI009
				session.putValue("isUsingInternational", str_isUsingInternational);		// RI009
				session.putValue("demoAccount", str_demoAccount);						// RI011
				session.setAttribute("smtpServer", str_smtpServer);   					// RI011 
				session.setAttribute("AS400name", str_AS400name);						// RI011

				if ( str_smtpServer.trim().equals(""))									// RI011
					goldServiceInfo[1] = "No";											// RI011
				
				if (!goldServiceInfo[1].trim().equals(""))								// RI011
						session.setAttribute("equipChangeAuth", goldServiceInfo[1]);	// RI011
						
				if (!goldServiceInfo[2].trim().equals(""))								// RI011
						session.setAttribute("equipChangeEmail", goldServiceInfo[2]);	// RI011

				// *****************************************************************
				//  If the request came from the customer service variable login application, then
				// stote userid and password in session variables.  These will be used later to 
				// determine the user's appropriate security functions.  
				// *****************************************************************

				if  ( !loginuserid.equals("")  &&  !loginpassword.equals("")  &&  submitbutton.equals("Retrieve Customer")  )  {
					session.putValue("loginuserid", loginuserid ); 
					session.putValue("loginpassword", loginpassword ); 
				}

				//*****************************************************************
				// Determine if the customer number being processed is a NARP number.   If so, 
				// populate the NARP number field and clear the customer number.
				//*****************************************************************

                           
				if (narpAcct == 0 && custAcct > 0) {

					if (loginacctbean.validNarp(
								str_as400driver, 
								str_as400url, 
								str_userid, 
								str_password, 
								str_datalib, 
								str_company, 
								custAcct )  )  {
									narpAcct = custAcct;
									custAcct = 0;
					} 
				}


				// ************************************************************
				// Authenticate the Narp number and Customer number, determine which one to
				// use.  If not valid then force an error by clearing the narp/customer variables
				// ************************************************************       
 
				if ( narpAcct > 0 ) {

					if (loginacctbean.validNarpCust(
								str_as400driver, 
								str_as400url, 
								str_userid, 
								str_password, 
								str_datalib, 
								str_company, 
								narpAcct,  
								custAcct )  )   {

						if (custAcct == 0) {
							str_corporate =  loginacctbean.getNarpCorplink( 
													str_as400driver, 
													str_as400url, 
													str_userid, 
													str_password, 
													str_datalib, 
													str_company, 
													narpAcct  );     

							if (!str_corporate.equals(""))  {
								custAcct = Integer.valueOf(str_corporate).intValue();
							} else {
								custAcct = 0;
								narpAcct = 0;
								System.out.println("Error:  no corp link found for narp");
							}

						} 

					} else {
						narpAcct = 0;
						custAcct = 0;
						System.out.println("Error:  narp/customer is NOT valid");
					}
				}
                 

				// ***********************************************************
				// Validate the customer number  and determine if processing can continue 
				// ***********************************************************
 
				if (custAcct > 0) {

					str_customer = loginacctbean.validateCustomer( 
											str_as400driver, 
											str_as400url, 
											str_userid, 
											str_password, 
											str_datalib, 
											str_company, 
											custAcct  );
				}


				// ***********************************************************
				//  Process login results 
				// ***********************************************************
 
				if (!str_customer.equals("")) {

					str_corporate =  loginacctbean.getCustCorplink( 
											str_as400driver, 
											str_as400url, 
											str_userid, 
											str_password, 
											str_datalib, 
											str_company, 
											custAcct  );     

					session.putValue("customer", str_customer);
					session.putValue("corporate", str_corporate);


					if (!str_corporate.equals(""))
						response.sendRedirect(str_path + "corpLinkAccounts.jsp");
					else
						response.sendRedirect(str_path + "menu.jsp");

				} else {
					
					response.sendRedirect(str_path + "securityFailure.jsp");
					System.err.println("Invalid customer number was detected.");
					loginbean.cleanup();
				}

			}

        } catch (Exception e) {
            System.out.println(" Error Exception....");
		}

	}    // End DoGet

}    // End of class