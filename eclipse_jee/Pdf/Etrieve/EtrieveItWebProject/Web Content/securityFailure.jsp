<!-- JSP page content header --> 
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
//    Index    User Id     Date        Project               Desciption
//    ------  ----------  ----------  --------------------  ----------------------------------------------------------
//    x001     DTC9028     12/26/02    SR26609 pre-load      Changed the "customer login" to "close reports"
//    x002     DTC9028     12/26/02    SR26609 pre-load      Changed the page title 
//    x003     DTC9028     12/26/02    SR26609 pre-load      Changed the copy right text 
//    x004     DTC9028     12/26/02    SR26609 pre-load      Incorporate Hertz images
//    x005     DTC9028     12/26/02    SR26609 pre-load      Modify the e-mail address  
//    x006     DTC2073     12/09/04    SR31413 PCR 25        Retrieve custom authority message to display.
//    RI007    DTC2073     02/01/05    SR31413 PCR26         Add copyright year dynamically to the page.
// ***************************************************************************

session = request.getSession(); 
session.invalidate();

//*************************************************************
// Retrieve today's date to be used for copyright statment.
//*************************************************************
  
  Calendar cpToday = Calendar.getInstance();
  int cpYear = cpToday.get(Calendar.YEAR) ;  

// ******************************************
// Retrieve the custom message variable 
// from the HTTP request object
// ******************************************

String str_errorMsg = (String)request.getAttribute("errorMsg");	 
if( str_errorMsg == null) 	str_errorMsg = "";


%>

<html>
<head>

<title>Hertz Equipment Rental Online Reporting</title>

<meta http-equiv="expires" content="-1">
<meta http-equiv="pragma" content="no-cache">
<link href="/EtrieveIt/images/wynnesystems.css" rel="stylesheet" type="text/css">

<style type="text/css">
#maintext {position:absolute; left:20px; width:650px; z-index:0;}
</style>

<%@ page language="java" import="java.sql.*, java.util.*" %>

</HEAD>

<%
String str_supportEmail = "mailto:hercsales@hertz.com";
%>

<BODY BGCOLOR="#ffffff" onLoad="setTimeout(window.close, 30000)">

	<div ID=maintext>

		<table border="0" width="650" cellspacing="0" cellpadding="0">
			<tr>
				<td width="300" align="right"><img src="/EtrieveIt/images/goldReports_header.gif" width="300" height="100"></td>
				<td width="100" align="center">&nbsp;</td>
				<td width="250" align="left"><img src="/EtrieveIt/images/HERClogo.gif" width="250" height="100"></td>
			</tr>
		</table>

		<table border="0" width="650" cellspacing="0" cellpadding="0">
			<tr>
				<td background="/EtrieveIt/images/bottom_back.gif" width="25">&nbsp;</td>
				<td background="/EtrieveIt/images/bottom_back.gif" width="595" class="company">&nbsp;</td>
				<td width="30" align="left"><img src="/EtrieveIt/images/bottom_back.gif" width="30" height="30"></td>
			</tr>
			<tr>
				<td><img src="/EtrieveIt/images/empty.gif" width="50" height="10"></td>
				<td><img src="/EtrieveIt/images/arrow.gif" height="10" align="right"></td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td><img src="/EtrieveIt/images/empty.gif" width="50" height="30"></td>
				<td align="right" class="history"><a href="javascript:window.close()">close reports</a>&nbsp;&nbsp;&nbsp;security failure&nbsp;&nbsp;&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
		</table>
		
		<br>

		<center>
		
			<table border="0" cellspacing="0" cellpadding="0" width="650">
				<% if(!str_errorMsg.equals("")) { %>
					<tr>
						<td><br><a class="company"><%=str_errorMsg%></a><br><br></td>
					</tr>
				<% } %>
				<tr>
					<td>
						<p><% if(str_errorMsg.equals("")) { %>An error occured while processing your request.  Please close this window and try the request again. <% } %> This window will automatically close in thirty seconds if no action is taken. If this problem persists, please  <a href=<%=str_supportEmail%>> contact us</a>.</p>
					</td>
				</tr>
			</table>
			
		</center>


		<br>
		
		<hr>

		<table border="0" width="650" cellspacing="0" cellpadding="0">

			<tr>
				<td background="/EtrieveIt/images/empty.gif" class="footerblack"><center>&copy; <%=cpYear%> The Hertz Corporation.  All Rights Reserved.  &reg REG. U.S. PAT. OFF.</center></td>
			</tr>
 
		</table>

		<br>

	</div>

</BODY>

</HTML>
