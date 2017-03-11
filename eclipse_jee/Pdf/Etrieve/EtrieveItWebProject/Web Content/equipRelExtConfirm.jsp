<%
// *****************************************************************************************************************
// Copyright (C) 2006 The Hertz Corporation, All Rights Reserved.  Unpublished.       
//                                                                                              
// The information contained herein is confidential and proprietary to the Hertz Corporation
// and may not be duplicated, disclosed to third parties, or used for any purpose not       
// expressly authorized by it.                                                              
// Any unauthorized use, duplication, or disclosure is prohibited by law.                   
// ****************************************************************************************************************
//   MODIFICATION INDEX 
//   
//    Index    User Id     Date        Project              Desciption
//    ------  ---------  ----------  ----------------  ----------------------------------------------------------
//    RI001    DTC2073    02/22/06     SR35873              Abbr. Equipment Release
// **************************************************************************************************************

%>
<!-- JSP page content header --> 

<%@ page language="java" import="java.util.*" contentType="text/html" %> 

<html>
	<head>

		<title>Hertz Equipment Rental Online Reporting</title>

		<meta http-equiv="expires" content="-1">
		<meta http-equiv="pragma" content="no-cache">
		<link href="images/wynnesystems.css" rel="stylesheet" type="text/css">

		<style type="text/css">
			#maintext {position:absolute; left:20px; width:650px; z-index:0;}
		</style>

	</HEAD>
	<%

	String str_companyname  = (String) session.getValue("companyname");
	String str_customer    = (String) session.getValue("customer");

	//*************************************************************
	// Retrieve today's date to be used for copyright statment.
	//*************************************************************
  
	Calendar cpToday = Calendar.getInstance();
	int cpYear = cpToday.get(Calendar.YEAR) ;  

	String str_contract = (String) request.getParameter("contract");
	
	if (str_contract == null) 
		str_contract = "";

	String str_supportEmail = "mailto:hercsales@hertz.com";

	String pageUse = (String) request.getParameter("pageUse");

	if (pageUse == null) 
		pageUse = "";

	String pageHeader = "";

	if (pageUse == null)
		pageHeader = "request confirmation";
	else
		if(pageUse.equalsIgnoreCase("Error"))
			pageHeader = "processing error";
		else
		pageHeader = "request confirmation";		
		
	String errMsg = (String)request.getParameter("errorMsg");

	if (errMsg == null) 
		errMsg = "Processing failure during completion of Equipment Release or Extend Request.";
	%>

	<BODY BGCOLOR="#ffffff">

		<div ID=maintext>

			<table border="0" width="900" cellspacing="0" cellpadding="0">
				<tr>
					<td width="300" align="right"><img src="images/goldReports_header.gif" width="300" height="100"></td>
					<td width="350" align="center">&nbsp;</td>
					<td width="250" align="left"><img src="images/HERClogo.gif" width="250" height="100"></td>
				</tr>
			</table>

			<table border="0" width="900" cellspacing="0" cellpadding="0">

				<tr>
					<td background="images/bottom_back.gif" width="25">&nbsp;</td>
					<td background="images/bottom_back.gif" width="845"><!--COMPANY_NAME--><a class="company"><%=str_companyname.trim()%></a>&nbsp;&nbsp;-&nbsp;&nbsp;Account Number: <!--COMPANY_NUMBER--><a class="data"><%=str_customer%></a></td>
					<td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
				</tr>
				<tr>
					<td><img src="images/empty.gif" width="50" height="10"></td>
					<td><img src="images/arrow.gif" height="10" align="right"></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><img src="images/empty.gif" width="50" height="30"></td>
					<td align="right" class="history"><a href="javascript:window.close()">close reports</a>&nbsp;&nbsp;&nbsp;<%=pageHeader%>&nbsp;&nbsp;&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
			</table>
		
			<br>

			<center>
			
				<table border="0" cellspacing="5" cellpadding="5" width="700">
				
					<%
					if(!pageUse.equalsIgnoreCase("Error"))
					{
					%>
						<tr bgcolor="#ffffa4">
							<td align="center" class="redbold" >
								Your request has been submitted to our offices.  Please allow two business days for the transaction to take affect.
							</td>
						</tr>
					<%
					}
					else
					{
					%>
						<tr>
   		 	 				<td>
								We're sorry but your request could not be processed at this time.  Please see the message below for more detail.<br><br>  
								To protect the security of your data, this session is set to timeout after 30 minutes of inactivity.  If this problem persists, please <a href=<%=str_supportEmail%>>contact us</a> and provide the error message below.
								<br><br><br>
								<a class="company">Error message:   <%=errMsg%></a>
								<br><br>
							</td>
  						</tr>
  					<%
  					}
  					%>
              		<tr>
                       	<td class="tableheader3" valign="bottom">Return to:</td>
               		</tr>
               		<%
					if(!pageUse.equalsIgnoreCase("Error"))
					{
					%>
               		<tr>
                  		<form action="contractDetail.jsp" method=POST>
                  			<Input Type="hidden" name="contract" value = "<%=str_contract%>" >	
                  			<Input Type="hidden" name="sequence" value = "000" >	
                        	<td class="tableheader3" valign="bottom"><input type=image SRC="images/forward.gif" height="21" width="38"  BORDER=0 align="left" valign="top">Rental Contract</td>
                  		</form>
               		</tr>
               		<%
               		}
               		%>
               		<tr>
                  		<form action="equipOnRent.jsp" method=POST>
                       		<td class="tableheader3" valign="bottom"><input type=image SRC="images/forward.gif" height="21" width="38"  BORDER=0 align="left" valign="top">Equipment on Rent by Start Date</td>
                  		</form>
               		</tr>
               		<tr>
                  		<form action="overdueContracts.jsp" method=POST>
                       		<td class="tableheader3" valign="bottom"><input type=image SRC="images/forward.gif" height="21" width="38"  BORDER=0 align="left" valign="top">Overdue Rentals</td>
                  		</form>
               		</tr>
             		<tr>
                  		<form action="poHistory.jsp" method=POST>
                       		<td class="tableheader3" valign="bottom"><input type=image SRC="images/forward.gif" height="21" width="38"  BORDER=0 align="left" valign="top">PO/Job Activity Report</td>
                  		</form>
               		</tr>
             		<tr>
                  		<form action="menu.jsp" method=POST>
                       		<td class="tableheader3" valign="bottom"><input type=image SRC="images/forward.gif" height="21" width="38"  BORDER=0 align="left" valign="top">Menu</td>
                  		</form>
               		</tr>
               			
				</table>
				
			</center>
			
			<br><br>
		
			<hr width="900">

			<table border="0" width="900" cellspacing="0" cellpadding="0">
				<tr>
					<td background="images/empty.gif" class="footerblack"><center>&copy; <%=cpYear%> The Hertz Corporation.  All Rights Reserved.  &reg REG. U.S. PAT. OFF.</center></td>
				</tr>
			</table>

			<br>

		</div>

	</BODY>

</HTML>