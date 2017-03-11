<!-- authentication -->
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
//    Index    User Id        Date       Project             Desciption
//   -------  -----------  -----------  -----------------   ----------------------------------------------------------
//    x001     DTC9028       11/14/03    SR28586 PCR8        Create PO history list
//    RI002    DTC9028       02/02/05    SR31413 PCR26       Add copyright year dynamically to the page
//    RI003    DTC9028       07/15/05    SR31413 PCR23       Closed rental report
//    RI004    DTC9028       08/01/05    SR31413 PCR19       Datasource implementation modification
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
<%@ page language="java" import="java.sql.*,java.util.*,java.text.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="contracthistorybean" class="etrieveweb.etrievebean.contractHistoryBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />

<%

// *********************************
// Retrieve session variables
// *********************************

String str_username = (String) session.getValue("username");
String str_customer = (String) session.getValue("customer");
String str_company  = (String) session.getValue("company");
String str_datalib  = (String) session.getValue("datalib");
String list_customer = (String) session.getValue("list");
String str_password  = (String)session.getValue("password");
String str_as400url  = (String)session.getValue("as400url");
String str_as400driver = (String)session.getValue("as400driver");
String loginuserid  = (String)session.getValue("loginuserid");


// *********************************
// Retrieve parameter variables
// *********************************

String str_contract  = request.getParameter("contract");
String str_sequence  = request.getParameter("sequence");
String str_returnpage  = request.getParameter("ret");

// *********************************
// Declare variables
// *********************************

String str_RHCON   = "";
String str_RHISEQ  = "";
String str_type    = "";
String str_trandate  = "";
String str_ordtype   = "";
String str_typetext = "";
String str_userid   = "";
String str_ponumber = "";
String str_jobnumber = "";
String str_total  = "";
String startstring = "";	
String returnText = "Return to previous report";
						
int quotepos = 0;

if ( loginuserid == null )
	loginuserid = "";

Connection MenuSecurityBeanconnection = null;
Connection contracthistorybeanconnection = null;

//*************************************************************
// RI002 - Retrieve today's date to be used for copyright statment.
//*************************************************************
  
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  
    
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

// ****************************************************************
// Make a new AS400 Connection.  Check if user is authorized for this option.  
//  If user is not allowed - redirect to menu
// ****************************************************************

MenuSecurityBeanconnection = MenuSecurityBean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U07"))  {
	response.sendRedirect("menu.jsp");
}
    
String str_companyname  = (String) session.getValue("companyname");
String startnumber = request.getParameter("startnumber");
String jobnumber = request.getParameter("jobnumber");

if ( jobnumber == null )	jobnumber = "";
if ( startnumber == null )	startnumber = "0";
if ( str_returnpage == null )	str_returnpage = "";


// **********************
// End current connection
//**********************

MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);

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
				<td><img src="images/arrow.gif" height="10" align="right"></td>
				<td></td>
			</tr>

			<tr>
				<td><img src="images/empty.gif" height="30"></td>
				<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;contract history&nbsp;&nbsp;&nbsp;</td>
				<td></td>
			</tr>

		</table>

		<br>

		<center>

			<table cellpadding="3" cellspacing="1" border="0" width="650">

				<tr>
					<td bgcolor="#ffffff" align="left"  class="tableheader3" colspan="5">&nbsp;History for contract number <%=str_contract%></td>
				</tr>

				<tr>
					<td bgcolor="#000000" background="images/empty.gif" align="left" valign="top" width="15%" class="whitemid">Contract / Invoice</td> 
					<td bgcolor="#000000" background="images/empty.gif" align="left" valign="top" width="10%" class="whitemid">Date</td> 
					<td bgcolor="#000000" background="images/empty.gif" align="left" valign="top" width="30%" class="whitemid">Purchase Order</td> 
					<td bgcolor="#000000" background="images/empty.gif" align="left" valign="top" width="30%" class="whitemid">Job Number</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left" valign="top" width="15%" class="whitemid">Type</td>
				</tr>

      <%

				// ***************************************************
				// Make a new AS400 connection to retrieve account information
				// ***************************************************

				contracthistorybeanconnection = contracthistorybean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

				String str_color    = "#cccccc";
				String str_description = "";
				String str_startdate = "";

				str_total = contracthistorybean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_contract, str_sequence );
  
				int num_display = 10000;
				int num_count = 0;
				int num_total = Integer.valueOf(str_total).intValue();
				int num_current = Integer.valueOf(startnumber).intValue();
				int num_nextstart = num_current + num_display;
				int num_prevstart = num_current - num_display;

				if ( num_prevstart <= 0 )
					num_prevstart = 0;

				if ( contracthistorybean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_current, str_contract, str_sequence  ) ) {

					while ( contracthistorybean.getNext() && num_count < num_display) {

						num_count++;
						startstring = "";	
						quotepos = 0;

						if ( str_color.equals("#cccccc") )
							str_color = "#999999";
						else 
							str_color = "#cccccc";

						str_RHCON = contracthistorybean.getColumn("RHCON#");
						str_RHISEQ = contracthistorybean.getColumn("RHISEQ");
						str_trandate = contracthistorybean.getColumn("RHSYSD");
						str_type  = contracthistorybean.getColumn("RHTYPE");
						str_ordtype  = contracthistorybean.getColumn("RHOTYP");
						str_ponumber = contracthistorybean.getColumn("RHPO#");
						str_jobnumber = contracthistorybean.getColumn("RHJOB#");


						while( str_RHISEQ.length() < 3 ) {
							str_RHISEQ = "0" +  str_RHISEQ;
						}

						// **************
						// Transaction Date
						// **************

						if ( !str_trandate.trim().equals("0") && str_trandate.length() == 8)
							str_trandate = str_trandate.substring(4, 6) + "/" + str_trandate.substring(6, 8) + "/" + str_trandate.substring(2, 4);
						else
							str_trandate = "";


						if ( str_type.equals("R") )
							str_typetext = "Reservation";
						else if ( str_type.equals("X") )
							str_typetext = "Quote";
						else if ( str_type.equals("W") )
							str_typetext = "Work Order";
						else if ( str_type.equals("S") ||  str_ordtype.equals("Y")   )
							str_typetext = "Sales Order";
						else if ( str_type.equals("O")  &&  str_RHISEQ.equals("000")  )
							str_typetext = "Rental Contract";
						else if ( !str_RHISEQ.equals("0") )
							str_typetext = "Invoice";
						else 
							str_typetext = "";
      %>
						<tr>						
							<%
							if (  str_RHISEQ.equals("000")  )  {   
							%>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata">&nbsp;<a href="contractDetail.jsp?contract=<%=str_RHCON%>&sequence=<%=str_RHISEQ%>" ><%=str_RHCON%></a></td>
							<%  
							}  else  {  
							%>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata">&nbsp;<a href="contractDetail.jsp?contract=<%=str_RHCON%>&sequence=<%=str_RHISEQ%>" ><%=str_RHCON%>-<%=str_RHISEQ%></a></td>
							<%
							}  
							%>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata"><%=str_trandate%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata">&nbsp;<%=str_ponumber%></td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata">&nbsp;<%=str_jobnumber%></td>
							<td bgcolor="<%=str_color%>"  background="images/empty.gif" align="left" valign="top" class="tabledata"><%=str_typetext%>&nbsp;</td>
						</tr>
     <%
					}

				}
     %>

				<tr>
					<%  
					if ( !str_total.equals("0") )  {  
					
						if ( str_returnpage.equals("PO") )
							returnText = "Return to PO/Job Activity Report";

						if ( str_returnpage.equals("CR") )
							returnText = "Return to Closed Rentals Report";
							
					%>
						<td bgcolor="#ffffff" align="center" colspan="5" class="tableheader4"><br><form><input type="button" value="<%=returnText%>" onClick="javascript:history.go(-1);"></form></td>  
					<% 
					}  else {  
					%>
						<td bgcolor="#CCCCCC" align="left" colspan="5" class="tableheader3"><br>No records were found<br></td>
					<% 
					}  
					%>
				</tr>

			</table>

			<br>

			<table cellpadding="3" cellspacing="0" border="0" width="650">

				<tr>
					<td bgcolor="#ffffff" align="left" width="515" class="tableheader3"><!--NUM_RECORDS-->&nbsp;
						<%  if ( num_total > 0 ) {   %>
								<%=num_current+1%> - 
								<%  
								if ( num_nextstart < num_total ) {    
								%>
									<%=num_nextstart%> of 
								<%  
								} else {  
								%>
									<%=num_total%> of 
								<%   
								}
							}
                        %>
 						<%  
 						if ( !str_total.trim().equals("0")  )  
 						%> 
							<%=str_total%> record(s).

					</td>

					<td align="right" width="90">
						<%   
						if ( num_prevstart >= 0 && num_current != 0 ) {    
						%>
							<form action="contractHistory.jsp" method=POST> 
								<input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
								<input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
								<input type=image src="images/prev.gif" height="40" width="87" border="0">
							</form>
						<%  
						}   
						%>
					</td>

					<td width="65">
						<%   
						if ( num_nextstart < num_total ) {%>
							<form action="contractHistory.jsp" method=POST> 
								<input type="hidden" name="startnumber" value="<%=num_nextstart%>">
								<input type="hidden" name="jobnumber" value= "<%=jobnumber%>"> 
								<input type=image src="images/next.gif" height="40" width="62" border=0> 
							</form>
						<%   
						}    
						%>
					</td>

				</tr>

			</table>

		</center>

		<a href="javascript:history.go(-1)"><IMG src="images/back.gif" height="40" width="62" border="0"></a> 
		<a href="menu.jsp"><IMG src="images/menu.gif" height="40" width="62" border="0"></a> 
		<a href="javascript:window.close()"><IMG src="images/closeReports.gif" height="40" width="123" border="0"></a> 
		<a href="javascript:window.print()"><img src="images/print.gif" width="67" height="40" border="0"></a>

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
	// End current connection
	//**********************

	contracthistorybean.cleanup();	// RI004
	contracthistorybean.endcurrentConnection(contracthistorybeanconnection);

	%>

</BODY>
</HTML>