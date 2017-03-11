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
//  Index    User Id      Date     Project           Desciption
//  ------  ---------- ---------- ----------------  -----------------------------------------
//  RI001    DTC9028    03/02/05   SR31413 PCR19     Implement the datasource connection
//  RI002    DTC9028    09/14/05   SR31413 PCR19     Initialize the demo account logic
// ****************************************************************************************************************
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import etrieveweb.etrievebean.checkcustomeridBean;
import etrieveweb.etrievebean.sysctlflBean;    // RI001

public class EtrieveWebCheckCustomerIdServlet extends HttpServlet {
	
    /**
    * Handle the HTTP GET method
    */
    
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		String sCodeKey = "CSCode";
		String sDatalibKey = "CSDatalib";
		String srbName = "EtrieveIt";
		String sPathKey = "CSPath";
		String str_path = new String();
		String str_demoAccount = new String();		// RI002
		boolean useDatasource = false;			// RI001
		
		checkcustomeridBean checkbean = new checkcustomeridBean();
		sysctlflBean sysctlflbean = new sysctlflBean();   			// RI001

		HttpSession session = request.getSession(true);

		String corporate = (String) session.getValue("corporate");
		String str_company = (String) session.getValue("company");
		String str_datalib = (String) session.getValue("datalib");
		String str_username = (String)session.getValue("username");
		String str_password = (String)session.getValue("password");
		String str_as400url = (String)session.getValue("as400url");
		String str_as400driver = (String)session.getValue("as400driver");


		// ********************************************************************************
		// RI002 - Determine if the DEMO functionality is being triggered (Domestic only)
		// ********************************************************************************
		
		if (str_company.equals("HG") && str_username.trim().equals("PXPAS409") )	// RI002
			str_demoAccount = "Y";		// RI002
		
		Connection theconnection = null;

		String req_nationalyn = request.getParameter("typeyn").trim();
		String req_id = request.getParameter("id").trim();

		// ****************************************
		//  Get the PropertyResourceBundle
		// ****************************************
		
		try {
			
			String str_root = this.getServletContext().getRealPath("");
			File path = new File( str_root, "WEB-INF//classes//conf//EtrieveIt.properties"); 
			Properties settings = new Properties();
			FileInputStream sf = new FileInputStream(path);
			settings.load(sf);
                     
			if (str_company == "")
				str_company = settings.getProperty(sCodeKey);

			if (str_datalib == "")
				str_datalib = settings.getProperty(sDatalibKey);

			str_path = settings.getProperty(sPathKey);

		} catch (Exception e) {
			System.err.println("Failed to load properties.");
			return;
		} 


		// *********************************************************************************
		// RI001 - If the properties file parameters are blank then retrieve the 
		// parameter values from the RentalMan system control file.  
		// *********************************************************************************

		try {		// RI001
			
			useDatasource = sysctlflbean.getConnectionFlag(str_demoAccount);	// RI001, RI002

			if ( useDatasource ) {		// RI001
			
				String[] sysctlfl_array = sysctlflbean.getProperties(str_demoAccount);		// RI001, RI002

			if (str_company == "")					// RI001
				str_company = sysctlfl_array[0];	// RI001

			if (str_datalib == "")					// RI001
				str_datalib = sysctlfl_array[4];	// RI001
				
				str_path =  sysctlfl_array[3];		// RI001

			}
			
		} catch (Exception e) {					// RI001
			System.out.println("Failed to load RentalMan properties parameters");	// RI001
			return;	// RI001	
		}		// RI001
		
		
		// ****************************
		// Check for corporate
		// ****************************
		
		if ( corporate == null)
			response.sendRedirect(str_path + "ErrorPage.jsp");

		try {
        	
			if (req_nationalyn.equals("y"))
				session.putValue("nationalyn", "y");
			else
				session.putValue("nationalyn", "n");

			theconnection = checkbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

			if (checkbean.checkCustomerID( 
						Integer.valueOf(corporate).intValue(),
						Integer.valueOf(req_id).intValue(),
						str_company,
						str_datalib)) {
								
				session.putValue("customer", req_id);
				
				checkbean.endcurrentConnection(theconnection);
				
				response.sendRedirect(str_path + "menu.jsp");
				
			} else {
                
				checkbean.endcurrentConnection(theconnection);
				response.sendRedirect(str_path + "securityFailure.jsp");
			}

		} catch (Exception e) {
			System.out.println(" Error Exception: CustomerID is not valid.");
		}
	}
}