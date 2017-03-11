<%
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
//    Index    User Id       Date      Project             Desciption
//   ------   ----------  --------    -----------------   ----------------------------------------------------------
//    RI001    DTC2073     01/28/05    SR31413 PCR26       Add copyright year dynamically to the page.
//    RI002    DTC2073     02/07/05    SR31413 PCR19       Datasource implementation
//    RI003    DTC9028     09/14/05    SR31413 PCR19       Initialize the demo account logic
// ***************************************************************************************************************
%>
<!-- JSP page content header -->  
<!-- November 16, 2005 ** (SR35420) -->

<html>
<head>

<title>Hertz Equipment Rental Online Reporting</title>

<link href="images/wynnesystems.css" rel="stylesheet" type="text/css">

<style type="text/css">
#tabdiv {position:absolute; top:120px; left:0px; z-index:2; visibility:show}
#maintext {position:absolute; left:20px; width:650px; z-index:0;}
</style>

<script language="Javascript">
<!--

// validate the text entered in the login form

function validate() {
  if (document.login.repuserid.value.length < 1) {
    alert("Please enter your RentalMan user name.");
    return false;
  }
  else if (document.login.reppassword.value.length < 1) {
    alert("Please enter your RentalMan password.");
    return false;
  }
  else {
    return true;
  }
}
 
function validatecust() {
  if (document.login.corpacct.value.length < 1) {
    alert("Please enter a customer number.");
    return false;
  } 
  else {
    return true;
  }
}
// -->
</script>

</HEAD>

<!-- JSP page content header -->
<%@ page language="java" import="java.sql.*, java.util.*, java.text.*,  java.net.*,  java.io.*,com.ibm.as400.access.* "  errorPage="ErrorPage.jsp"%>
<jsp:useBean id="sysctlflbean"  class="etrieveweb.etrievebean.sysctlflBean" scope="page" />   	<!-- RI002 -->  
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />
<%

String sCodeKey = "CSCode";
String sDatalibKey = "CSDatalib";
String sPathKey = "CSPath";
String srbName  = "EtrieveIt";
String sDriverKey = "CSDriver";       
String sPasswordKey = "CSPassword";    
String sURLKey  = "CSURL";              
String sUserIDKey = "CSUserID";   
String str_company = new String();
String str_datalib = new String();
String str_path = new String();      
String str_userid = new String();
String str_password = new String();
String str_as400driver = new String();
String str_as400url = new String();
String str_as400name = "";
String errormessage = "";
String repcmp  = request.getParameter("cm");
String repuserid  = request.getParameter("repuserid");
String reppassword  = request.getParameter("reppassword");
String submitbutton = request.getParameter("submitbutton");
String loginuserid  = "";
String loginpassword = "";
String screenname = "login";
String str_demoAccount = "N";	// RI003
boolean authenticated = false;
boolean errorexists       = false;
boolean useDatasource = false;		// RI002

Connection MenuSecurityBeanconnection = null;

//*************************************************************
// RI001 - Retrieve today's date to be used for copyright statment.
//*************************************************************
 Calendar cpToday = Calendar.getInstance();
 int cpYear = cpToday.get(Calendar.YEAR) ;  

if ( repcmp == null )
	repcmp = "";

if ( repuserid == null )
	repuserid = "";

if ( reppassword == null )
	reppassword = "";

if ( submitbutton == null )
	submitbutton = "";


// ************************************************************
// Authenticate the user and password after the log-in button was pressed
// ************************************************************

if ( !repuserid.equals("")   &&  !reppassword.equals("")  &&  submitbutton.equals("Log In")  )   {

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
		str_datalib = settings.getProperty(sDatalibKey).trim();   
		str_path = settings.getProperty(sPathKey);

	} catch (Exception e)  {
		System.err.println("Failed to load resource properties----.");
		response.sendRedirect("securityFailure.jsp");
		return;

	} 


	// ************************************************************
    //  Get the PropertyResourceBundle for the connection to the AS/400
    //      1. Retrieve the driver   
    //      2. Retrieve user id and password
    //      3. Retrieve server url
    // ************************************************************

	try {

		srbName = "etrieveweb.utility.Connection";

		Properties settings = new Properties();

		URL path = this.getClass().getClassLoader().getResource("conf//Connection.properties"); 

		FileInputStream sf  = new FileInputStream(path.getPath());

		settings.load(sf);

		str_userid = settings.getProperty(sUserIDKey).trim();       
	    str_password = settings.getProperty(sPasswordKey).trim();   
	    str_as400driver = settings.getProperty(sDriverKey).trim();        
		str_as400url = settings.getProperty(sURLKey).trim();            

	} catch (Exception e)  {
		System.err.println("Failed to load connection properties.");
		response.sendRedirect("securityFailure.jsp");
		return;
	}  

	// *********************************************************************************
	// RI002 - Retrieve the flag from the SYSCTLFL that determines whether to make a
	// connection using the datasource or use the properties file parameters.                                        
	// *********************************************************************************

	try {		// RI002

		useDatasource = sysctlflbean.getConnectionFlag(str_demoAccount);	// RI002, RI003

		if ( useDatasource ) {		// RI002
			
			String[] sysctlfl_array = sysctlflbean.getProperties(str_demoAccount);		// RI002, RI003

			str_company = sysctlfl_array[0];				// RI002
			str_path = sysctlfl_array[3];					// RI002
			str_datalib = sysctlfl_array[4];				// RI002
			str_as400name = sysctlfl_array[5];				// RI002
			str_userid = sysctlfl_array[6];					// RI002

			str_password = "";					// RI002
			str_as400driver = "";				// RI002
			str_as400url = "";					// RI002
							
			if ( str_path.equals("") )			// RI002
				str_path = "../";				// RI002	
				
		}										// RI002
						
	} catch (Exception e) {											// RI002
		str_path = "../";											// RI002
		response.sendRedirect(str_path + "securityFailure.jsp");	// RI002
		System.out.println("Failed to load RentalMan properties parameters");	// RI002
		return;																	// RI002
	}					// RI002

		
	// ********************************************************
	//  Determine the domain name of the AS400 from the url
	// ********************************************************
	
	if ( str_as400name.trim().equals("") ) {		// RI002

		int found_dev       = str_as400url.toUpperCase().indexOf("AS4DEV"); 
		int found_prod     = str_as400url.toUpperCase().indexOf("AS4HIRG"); 
		int found_candev  = str_as400url.toUpperCase().indexOf("AS4HRCCD"); 
		int found_canprod = str_as400url.toUpperCase().indexOf("AS4HRCCP"); 

		if ( found_dev >=  0 )  
			str_as400name  = "AS4DEV"; 
		else if ( found_prod >=  0 )  
			str_as400name  = "AS4HIRG"; 
		else if ( found_candev >=  0 )  
			str_as400name  = "AS4HRCCD"; 
		else if ( found_canprod >=  0 )  
			str_as400name  = "AS4HRCCP"; 
		else  {
			System.err.println("Could not determine AS400 domain from properties.");
			response.sendRedirect("securityFailure.jsp");
			return;		
		}  

	} else if ( !str_as400name.trim().equals("AS4DEV")            //  RI002
				&& !str_as400name.trim().equals("AS4HIRG")        //  RI002
				&&  !str_as400name.trim().equals("AS4HRCCD")      //  RI002
				&& !str_as400name.trim().equals("AS4HRCCP") ) {   //  RI002
			System.err.println("Could not determine AS400 domain from properties.");	//  RI002		
			response.sendRedirect("securityFailure.jsp");   							//  RI002
			return;																		//  RI002		
	}  																					//  RI002
	
	// **********************************************************
	// Authenticate user and if so check if authorized to the Etrieve application
	// **********************************************************
	AS400 system = new AS400(str_as400name);
	try {
	
		if ( system.authenticate( repuserid.trim(), reppassword.trim() ) )   {
		
			authenticated = true;    
            session.invalidate();
            session = request.getSession(true);
            String theid = (String) session.getId();
            session.putValue("System" , system);
            session.putValue("AS400name" , str_as400name);
            
		    if ( theid.equals( "" )  ||  theid.equals( "null" ) )
            	response.sendRedirect("securityFailure.jsp");
				session.putValue("loginuserid", repuserid.toUpperCase()  ); 
				session.putValue("loginpassword", reppassword ); 
			}

			system.disconnectAllServices();

  			MenuSecurityBeanconnection = MenuSecurityBean.makeautoConnection(str_as400driver, str_as400url, str_userid, str_password);

  			if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, repuserid.toUpperCase(), "UUU"))
  			{
				errormessage = "User ID specified does not have required authority.";
      			errorexists = true;
				session.putValue("loginuserid", ""  ); 
				session.putValue("loginpassword", "" ); 
  			}
  
			MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);

	}  catch(Exception error) {

		system.disconnectAllServices();
		String errortext = error.getMessage();
		session.putValue("loginuserid", "" ); 
		session.putValue("loginpassword", "" ); 

		if (  errortext.indexOf("User ID is not known")  >= 0 )
			errormessage = "User ID is not known.  Please try again.";

		if ( errortext.indexOf("Signon was canceled")  >=  0 ) 
			errormessage = "Signon was canceled";

		if ( errortext.indexOf("Password is incorrect")  >= 0 ) 
			errormessage = "Password is incorrect";
			
		if (errortext.indexOf("User ID will be disabled after next incorrect sign-on")  >= 0 )
			errormessage = "Password is incorrect.  User ID will be disabled after next incorrect sign-on.";
			
		if (errortext.indexOf("User ID is disabled")  >= 0 ) 
			errormessage = "User ID is disabled.";

		if ( errormessage.equals("")  )
			errormessage = errortext.trim();

		errorexists = true;

	}
	
}

// ************************************************************
// Determine if the user was already authenticated from a previous request.
// If so, display the customer number screen.
// ************************************************************

loginuserid         = (String) session.getValue("loginuserid");
loginpassword   = (String) session.getValue("loginpassword");
System.out.println("String :" + str_as400name);
System.out.println("String :" + loginuserid);

if ( loginuserid == null )
	loginuserid = "";

if ( loginpassword == null )
	loginpassword = "";

if ( !loginuserid.equals("")   &&  !loginpassword.equals("")  )   {
	authenticated = true;    
	screenname    = "customer service";
} 


%>

<BODY BGCOLOR="#ffffff">
 
	<table border="0" width="100%" align="center" cellspacing="0" cellpadding="0">
	      <tr>
	         <td width="300" align="right" valign="bottom"><img src="images/goldReports_header.gif" width="300" height="100"></td>
	         <td width="100%" align="center">&nbsp;</td>
	         <td width="250" align="left"><img src="images/HERClogo.gif" width="250" height="100"></td>
	      </tr>
	      <tr>
	         <td colspan="3" align="right" valign="top" class="history"><hr><a href="javascript:window.close()">close window</a>&nbsp;&nbsp;&nbsp;<%=screenname%>&nbsp;&nbsp;&nbsp;</td>
	      </tr>
	</table>

	<center>

	<table border="0" cellspacing="0" cellpadding="5" width="450">
		<%   if (  repcmp.trim().equals("D") )  {  %>
			<tr>
		   	   <td align="center"><IMG src="images/cust_reports_us.gif" height="36" width="300" border="0"></td>
			</tr>
		<%  }  %>
		<%   if (  repcmp.trim().equals("C") )  {  %>
			<tr>
			   <td align="center"><IMG src="images/cust_reports_ca.gif" height="36" width="250" border="0"></td>
			</tr>
		<%  }  %>
	</table>

	<table border="1" cellspacing="0" cellpadding="5" width="450" bgcolor="#FED70C" bordercolor="black">

	      <caption class="menu">Please enter the following information to access Etrieve.</caption>            

	      <tr>
	         <td align="center"><br>

		<table border="0" cellspacing="0" cellpadding="5">

 		<%  if ( screenname.equals("customer service")  )  { %> 

			<form name="login" action="servlet/EtrieveWebValidationServlet" method=POST onSubmit="return validatecust();">

			      <tr>
			         <td colspan="2" align="center"><br><br>Enter a valid customer number to display.&nbsp;</td>
			      </tr>

			      <tr>
			         <td  align="right" class="menu">Customer #:&nbsp;</td>
			         <td align="left"><input type="text" name="corpacct" size="10"  maxLength="7" ></td>
			      </tr>

			     <tr>
			         <td colspan="2" align="center"><br><input type="submit" value="Retrieve Customer" name="submitbutton"><br><br><br></td>
			     </tr>

			     <input type="hidden" name="custserv" size="10"  value="a2b4c6d8e0">

			</form>

		<%  }  else  {  %>

			<form name="login" action="customerservice.jsp" method=POST onSubmit="return validate();">

			      <tr>
			         <td colspan="2" align="center">Enter your production RentalMan  user name and password</td>
			      </tr>

			      <tr>
			         <td align="right" class="menu">User Name:&nbsp;</td>
			         <td align="left"><input type="text" name="repuserid" size="15" maxLength="10" ></td>
			      </tr>

			      <tr>
			         <td align="right" class="menu">Password:&nbsp;</td>
			         <td align="left"><input type="password" name="reppassword" size="15" maxLength="10" ></td>
			     </tr>

			     <tr>
			         <td colspan="2" align="center"><br><input type="submit" value="Log In" name="submitbutton"><br><br></td>
			     </tr>

			</form>

                        	<%  }  %>


		<%  if  ( errorexists ) {  %>
			<tr bgcolor="red" >
			   <td colspan="2" align="center" class="whitebold"><%=errormessage.trim()%></td>
			</tr>
		<%  }   %>

		</table>


	         </td>
	      </tr>

	</table>

  
   <a href="javascript:window.close()"><IMG src="images/closeit.gif" height="40" width="66" border="0"></a> 


<br>


     <br>
     <hr>

     <table border="0" width="650" cellspacing="0" cellpadding="0">

       <tr>
           <td background="images/empty.gif" class="footerblack"><center>&copy; <%=cpYear%> The Hertz Corporation.  All Rights Reserved.  &reg REG. U.S. PAT. OFF.</center></td>
       </tr>
 
    </table>

</center>

<br>

</BODY>
</HTML>
