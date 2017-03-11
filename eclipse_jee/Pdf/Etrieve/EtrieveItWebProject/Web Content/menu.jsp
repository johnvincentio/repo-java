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
//    Index      User Id       Date            Project          Desciption
//    -------   ------------  ------------  ----------------   ---------------------------------------------------
//    x001        DTC9028      11/26/02      SR26609 PCR1       Modification for the new log-in process
//    x002        DTC9028      11/26/02      SR26609 PCR1       Ensure that connection are ended 
//    x003        DTC9028      12/26/02      SR26609 PCR1       Changed the "log out" to "close reports"
//    x004        DTC9028      12/26/02      SR26609 PCR1       Changed the page title 
//    x005        DTC9028      12/26/02      SR26609 PCR1       Changed the copy right text 
//    x006        DTC9028      12/26/02      SR26609 PCR1       Add the account number
//    x007        DTC9028      04/28/03      SR28586 PCR1       Add logic to check for a customer rep and display hyper-link button
//    x008        DTC9028      04/28/03      SR28586 PCR7       Add logic to check security for a customer rep
//    RI009       DTC2073      02/01/05      SR31413 PCR26      Add copyright year dynamically to the page. 
//    RI010       DTC2073      04/22/05      SR31413 PCR29      Add overdue rentals report
//    RI011       DTC9028      06/10/05      SR31413 PCR24      Add a broadcast message
//    RI012       DTC9028      07/15/05      SR31413 PCR23      Add closed rentals reports
//    RI013       DTC9028      07/27/05      SR31413 PCR19      Implement datasource
// ****************************************************************************************************************

String[]    names = session.getValueNames();
			 
if ( names.length <= 0 || names[0] == "")
  response.sendRedirect("securityFailure.jsp");	
%>



<html>
<head>

<title>Hertz Equipment Rental Online Reporting</title>

<meta http-equiv="expires" content="-1">
<meta http-equiv="pragma" content="no-cache">
<link href="images/wynnesystems.css" rel="stylesheet" type="text/css">

<style type="text/css">
#tabdiv {position:absolute; top:120px; left:0px; z-index:2; visibility:show}
#maintext {position:absolute; left:20px; width:650px; z-index:0;}
</style>

</HEAD>


<!-- JSP page content header -->
<%@ page language="java" import="java.sql.*, java.util.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="getcustomerbean" class="etrieveweb.etrievebean.getcustomerBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />

<%
String str_corporate    = (String) session.getValue("corporate");
String str_customer     = (String) session.getValue("customer");
String str_datalib         = (String) session.getValue("datalib");
String str_company     = (String) session.getValue("company");
String str_nationalyn  = (String) session.getValue("nationalyn");
String str_username    = (String)session.getValue("username");
String str_password    = (String)session.getValue("password");
String str_as400url       = (String)session.getValue("as400url");
String str_as400driver = (String)session.getValue("as400driver");
String loginuserid        = (String)session.getValue("loginuserid");
String loginpassword  = (String)session.getValue("loginpassword");
String str_userid                 = "";

if ( str_corporate == null )
  response.sendRedirect("securityFailure.jsp");

if ( loginuserid == null )
  loginuserid = "";

if ( loginpassword == null )
  loginpassword = "";

Connection MenuSecurityBeanconnection = null;
Connection getcustomerbeanconnection = null;

// *******************************
// Broadcast message
// *******************************

String broadcastmessage = "Important Note: To better serve our customers, key reports include an option to download the information for use in various spreadsheet or database programs.";  //  RI011

//*************************************************************
// RI009 - Retrieve today's date to be used for copyright statment.
//*************************************************************
  
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  

// *******************************
// single account - format Customer List
// *******************************

String list_customer = "(" + Integer.valueOf(str_customer) + ")";


// ***************************************************
// Make a new AS400 connection to retrieve customer information
// ***************************************************

getcustomerbeanconnection = getcustomerbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);


// *************************************
// national account - Get/format Customer List
// *************************************

if ( str_nationalyn.equals("y") )
	list_customer = getcustomerbean.getCustomerVec( Integer.valueOf(str_corporate).intValue(), str_company, str_datalib);

session.putValue("list", list_customer);


// *************************
// get company name selected 
// *************************

if ( getcustomerbean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib) ) {

	if ( !getcustomerbean.getNext() ) 
		session.putValue( "companyname", "UNKNOWN" );
	else
		session.putValue( "companyname", getcustomerbean.getColumn("CMNAME") );

	String  str_companyname  = (String) session.getValue("companyname");
  
	getcustomerbean.cleanup();	// RI013
	getcustomerbean.endcurrentConnection(getcustomerbeanconnection);	// RI013

	// ***********************************    
	//open connection for menu security
	// ***********************************

	MenuSecurityBeanconnection = MenuSecurityBean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

	// **********************************************************
	// Determine which user id needs to be checked.  If the user logged in via
	// the variable login page, then use the variable user id.  If not, use the 
	// standard user id.  Note:  The AS400 connection is still made using the
	// standard user id. 
	// **********************************************************

	if  ( !loginuserid.equals("") )  
		str_userid = loginuserid;
	else
		str_userid = str_username;

	str_userid = str_username;

	%>


   <BODY BGCOLOR="#ffffff">

      <div ID=maintext>

         <table border="0" width="650" cellspacing="0" cellpadding="0">
            <tr>
              <td width="300" align="right"><img src="images/goldReports_header.gif" width="300" height="100"></td>
              <td width="100" align="center">&nbsp;</td>
              <td width="250" align="left"><img src="images/HERClogo.gif" width="250" height="100"></td>
            </tr>
         </table>

         <table border="0" width="650" cellspacing="0" cellpadding="0">
            <tr>
              <td background="images/bottom_back.gif" width="25">&nbsp;</td>
              <td background="images/bottom_back.gif" width="595"><!--COMPANY_NAME--><a class="company"><%=str_companyname.trim()%></a>&nbsp;&nbsp;-&nbsp;&nbsp;Account Number: <!--COMPANY_NUMBER--><a class="data"><%=str_customer%></a></td>
              <td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
            </tr>
            <tr>
               <td><img src="images/empty.gif" height="10"></td>
               <td><img src="images/arrow.gif"  height="10" align="right"></td>
               <td></td>
            </tr>
            <tr>
               <td><img src="images/empty.gif"  height="30"></td>
               <td align="right" class="history">main menu&nbsp;&nbsp;&nbsp;</td>
               <td></td>
            </tr>
         </table>

		<%  
		if ( !broadcastmessage.equals("") )  {	// RI011  
		%>
			<table border="0" width="650" cellspacing="0" cellpadding="0">
				<tr>
					<td width="20">&nbsp;</td>
					<td width="40"  align="right"><img src="images/postit.JPG" width="38" height="38"></td>
					<td width="590" align="left" valign="middle" class="redbold"><%=broadcastmessage%></td>
				</tr>
			</table>
		<%
		}
		%>
		
		<br>
		
         <table width="325" border=0 cellspacing=4 cellpadding=5 align="left">

            <%
            // *******************
            // Rental History Option
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U08"))  {

            %>
               <tr>
                  <form action="rentalHistory.jsp" method=POST>
                        <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38"  BORDER=0 align="left" valign="top">Rental History by Equipment Type</td>
                  </form>
               </tr>
  
            <%
            }
            %>


            <%
            // *************************
            // Equipment On Rent option
            // *************************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U07"))    {

            %>
               <tr>
                  <form action="equipOnRent.jsp" method=POST>
                         <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Equipment on Rent by Start Date</td>
                  </form>
               </tr>
            <%
            }
            %>


            <%
            // *************************
            // Overdue Rentals
            // *************************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U07"))    {

            %>
               <tr>
                  <form action="overdueContracts.jsp" method=POST>
                         <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Overdue Rentals</td>
                  </form>
               </tr>
            <%
            }
            %>
            
            <%
            // *************************
            // Closed Rentals
            // *************************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U07"))    {

            %>
               <tr>
                  <form action="closedContracts.jsp" method=POST>
                         <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Closed Rentals</td>
                  </form>
               </tr>
            <%
            }
            %>
            
            <%
            // *********************
            // Reservations and quotes
            // *********************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U73"))  {

            %>
               <tr>
                  <form action="resvQuote.jsp" method=POST>
                        <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Reservations and Quotes</td>
                  </form>
               </tr>
            <%
            }
            %>


            <%
            // *******************
            // Open Contracts Option
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U06"))   {
 
            %>
               <tr>
                  <form action="openContracts.jsp" method=POST>
                        <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Open Rental Contracts</td>
                  </form>
               </tr>
            <%
            }
            %>


            <%
            // *******************
            // Rental Returns
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U70"))   {
 
            %>
               <tr>
                  <form action="rentalReturns.jsp" method=POST>
                        <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Returns, Exchanges, and Credits</td>
                  </form>
               </tr>
            <%
            }
            %>


            <%
            // *******************
            // Open Sales Orders
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U74"))  {

            %>
               <tr>
                  <form action="openSales.jsp" method=POST>
                        <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Open Sales Orders</td>
                  </form>
               </tr>
            <%
            }
            %>

            <%
            // *******************
            // Closed Sales Orders 
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U72"))  {

            %>
               <tr>
                  <form action="salesInvoices.jsp" method=POST>
                       <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Closed Sales Orders</td>
                  </form>
               </tr>
            <%
            }
            %>

         </table>


         <table width="325" border=0 cellspacing=4 cellpadding=5>

            <%
            // *******************
            // Account Summary
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U81"))  {

            %>
               <tr>
                  <form action="acctSummary.jsp" method=POST>
                       <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38"  BORDER=0 align="left" valign="top">Account Summary and Open Invoices</td>
                  </form>
               </tr>
            <%
            }
            %>


            <%
            // *******************
            // Payment History
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U80"))  {

            %>
               <tr>
                  <form action="payCheckTotal.jsp" method=POST>
                        <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Payment History</td>
                  </form>
               </tr>
            <%
            }
            %>


            <%
            // *******************
            // Open Invoices
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U84"))   {

            %>
               <tr>
                  <form action="openInvoice.jsp" method=POST>
                        <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Open Invoices</td>
                  </form>
               </tr>
            <%
            }
            %>


            <%
            // *******************
            // Paid Invoices
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U85"))  {

            %>
               <tr>
                  <form action="paidInvoice.jsp" method=POST>
                        <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Paid Invoices</td>
                  </form>
               </tr>
            <%
            }
            %>

            <%
            // *******************
            // Invoice aging
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U83"))   {

            %>
               <tr>
                  <form action="invoiceAging.jsp" method=POST>
                        <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Invoice Aging</td>
                  </form>
               </tr>
            <%
            }
            %>

            <%
            // *******************
            // Monthly Billings
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U71"))   {

            %>
               <tr>
                  <form action="monthlyBillings.jsp" method=POST>
                       <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Automated Monthly Billings</td>
                  </form>
               </tr>
            <%
            }
            %>

            <%
            // *******************
            // PO Activity Report
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U07"))   {

            %>
               <tr>
                  <form action="poHistory.jsp" method=POST>
                       <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">PO/Job Activity Report</td>
                  </form>
               </tr>
            <%
            }
            %>

			<%
            // *******************
            // Rental Man Reports
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U07"))   {

            %>
               <tr>
                  <form action="SpoolFileList.jsp" method=POST>
                       <td class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="left" valign="top">Rental Man Reports&nbsp;&nbsp;<img src="images/new.JPG" height="16" width="43" BORDER=0 valign="top"></td>
                  </form>
               </tr>
            <%
            }
            %>

         </table>

         <br clear="all">

         <table width="650" border=0 cellspacing=4 cellpadding=5>

            <% 

            // *******************
            // Search
            // *******************

            if(MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U07"))   {

            %>
               <tr>
                  <form action="search.jsp" method=POST>
                       <td align="center" class="menu"><input type=image SRC="images/forward.gif" height="21" width="38" BORDER=0 align="center" valign="top">Search Online Reports</td>
                  </form>
               </tr>
            <%
            }
            %>

        </table>


<center>

         <%   if ( !str_corporate.equals("") ) {   %>
                       <a href="corpLinkAccounts.jsp"><IMG src="images/corpacct.gif" height="40" width="115" border="0"></a> 
         <%   }  %>

<%
}
%>

<a href="javascript:window.close()"><IMG src="images/closeReports.gif" height="40" width="123" border="0"></a> 
<%  if (  !loginuserid.equals("")   &&  !loginpassword.equals("")  )  { %>   
                  <a href="customerservice.jsp"><IMG src="images/viewNewAccount.gif" height="40" width="150" border="0"></a> 
<%  }  %>

</center>

<br>
<hr>

      <table border="0" width="650" cellspacing="0" cellpadding="0">

         <tr>
            <td background="images/empty.gif" class="footerblack"><center>&copy; <%=cpYear%> The Hertz Corporation.  All Rights Reserved.  &reg REG. U.S. PAT. OFF.</center></td>
         </tr>
 
      </table>

<br>
    
</div>

<%
  // **********************
  // End current connections
  //**********************
  
// RI013 getcustomerbean.endcurrentConnection(getcustomerbeanconnection);
MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);
%>

</BODY>
</HTML>
