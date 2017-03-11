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
//   Index     User Id      Date          Project           Desciption
//  --------  ----------  -----------   ---------------   --------------------------------------------------------
//    x001     DTC9028     06/14/05      SR31413 PCR24      Implement data download
// ***************************************************************************

String[]    names = session.getValueNames();
		 
if ( names.length <= 0 || names[0] == "")
  response.sendRedirect("securityFailure.jsp");	

  String str_customer          = (String) session.getValue("customer");
  String str_company          = (String) session.getValue("company");
  String str_companyname  = (String) session.getValue("companyname");
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

 
<%


//*************************************************************
// Retrieve today's date to be used for copyright statment.
//*************************************************************
  
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  


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
			<td background="images/bottom_back.gif" width="595"><a class="company"><%=str_companyname.trim()%></a>&nbsp;&nbsp;-&nbsp;&nbsp;Account Number: <a class="data"><%=str_customer%></a></td>
			<td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
		</tr>

		<tr>
			<td><img src="images/empty.gif" height="10"></td>
			<td><img src="images/arrow.gif" height="10" align="right"></td>
			<td></td>
		</tr>

		<tr>
			<td><img src="images/empty.gif" height="30"></td>
			<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;download details&nbsp;&nbsp;&nbsp;</td>
			<td></td>
		</tr>

	</table>

	<br>

	<center>

		<table cellpadding="3" cellspacing="1" border="0" width="650">

			<tr>
				<td align="left" valign="top" class="tableheader3">The download feature allows for the data contained in the report to be used in various spreadsheet or database programs.</td>
			</tr>

			<tr>
				<td>
					<table border="0">
						<tr>
							<td colspan="4" align="left" valign="top" class="redbold">Important note: The following steps may vary based on the browser being used.<br><br></td>
						</tr>
						<tr>
							<td align="right" valign="middle" class="tableheader3">1.</td>
							<td align="left" valign="middle">Simply access the desired report and click</td>
							<td><IMG SRC="images/download4.JPG" height="22" width="86" BORDER=1 align="left" valign="middle"></td>
							<td align="left" valign="middle">to display the <a class="tableheader3">File Download</a> window.</td>						
						</tr>
						<tr>
							<td align="right" valign="bottom" class="tableheader3">2.</td>
							<td colspan="3" align="left" valign="middle"><br>Press the <a class="tableheader3">Save</a> button to display the <a class="tableheader3">Save As</a> window.</td>
						</tr>
						<tr>
							<td align="right" valign="bottom" class="tableheader3">3.</td>
							<td colspan="3" align="left" valign="bottom"><br>Select your destination folder (take note of folder name and drive).</td>
						</tr>
						<tr>
							<td align="right" valign="bottom" class="tableheader3">4.</td>
							<td colspan="3" align="left" valign="bottom"><br>Accept the default file name or change it at this time.</td>
						</tr>
						<tr>
							<td align="right" valign="bottom" class="tableheader3">5.</td>
							<td colspan="3" align="left" valign="bottom"><br>Be sure the <a class="tableheader3">Save as type</a> is set to <a class="tableheader3">Microsoft Excel Comma Separated Values file</a> and press <a class="tableheader3">Save</a>.</td>
						</tr>

						<tr>
							<td align="right" valign="bottom" class="tableheader3">6.</td>
							<td colspan="3" align="left" valign="bottom"><br>Once the download is complete, the file can be opened by an application that supports CSV files.</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td>&nbsp;</td>
			</tr>

			<tr>
				<td align="left" valign="top" class="tableheader3">Sample</td>
			</tr>
			
			<tr>
				<td align="left" valign="top" class="tabledata"><IMG src="images/spreadsheet.JPG" height="136" width="510" border="1"></td>
			</tr>
			
		</table>

	</center>

	<br>
	<br>
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

</BODY>
</HTML>
