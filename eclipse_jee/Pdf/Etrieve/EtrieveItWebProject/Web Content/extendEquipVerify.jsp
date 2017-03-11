<!-- authentication --> 
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

String[]    names = session.getValueNames();
		 
if ( names.length <= 0 || names[0] == "")
  response.sendRedirect("securityFailure.jsp");	
    
//*********************************************************************
//*** Verify that the user has required authority for equipment extend
//*********************************************************************
 
 
String equipChangeAuth = (String)session.getAttribute("equipChangeAuth");

if  (equipChangeAuth == null) equipChangeAuth = "";

if  (equipChangeAuth.equals(""))
{
	equipChangeAuth = "";
	%>
	<jsp:forward page="equipRelExtConfirm.jsp">	
    <jsp:param name="errorMsg" value="Not authorized to extend equipments from this application." />	
    <jsp:param name="pageUse" value="Error" />	
    </jsp:forward>
	<%
}

if (!equipChangeAuth.equals("Yes"))
{
	%>
	<jsp:forward page="equipRelExtConfirm.jsp">	
    <jsp:param name="errorMsg" value="Not authorized to extend equipments from this application." />	
    <jsp:param name="pageUse" value="Error" />	
    </jsp:forward>
	<%
}	
		 

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

	<%

	//*************************************************************
	// *** Retrieve and test the input parameters ***
	//*************************************************************

	String str_contract = request.getParameter("contract");
	String str_startdate = request.getParameter("startdate");
	String str_estdate = request.getParameter("estdate");
	String str_revEstDate = request.getParameter("revEstDate");
	String str_contactName = request.getParameter("contactName");
	String str_contactPhone1 = request.getParameter("contactPhone1");
	String str_contactPhone2 = request.getParameter("contactPhone2");
	String str_contactPhone3 = request.getParameter("contactPhone3");
	String str_userComments = request.getParameter("userComments");
	String str_sequence = request.getParameter("sequence");

 
	if ( str_contract == null )	str_contract = "";
	if ( str_startdate == null ) str_startdate = "";
	if ( str_estdate == null )	str_estdate = "";
	if ( str_revEstDate == null )	str_revEstDate = "";
	if ( str_contactName == null )	str_contactName = "";
	if ( str_contactPhone1 == null )	str_contactPhone1 = "000";
	if ( str_contactPhone2 == null )	str_contactPhone2 = "000";
	if ( str_contactPhone3 == null )	str_contactPhone3 = "000";
	if ( str_userComments == null )	str_userComments = "";
	if ( str_sequence == null )	str_sequence = "0";


	// ** Format revised estimated return date ***

	if(!str_revEstDate.equals(""))
	{
		str_revEstDate = str_revEstDate.replace('-','/');
	
		int i = str_revEstDate.lastIndexOf('/');
		String strMonthDay = str_revEstDate.substring(0,i+1);
		String strYr = str_revEstDate.substring(i+3);
		str_revEstDate = strMonthDay +strYr ;
	}	

	//*************************************************************
	// ***  Retrieve session variables  ***
	//*************************************************************

	String str_customer = (String) session.getValue("customer");
	String str_company = (String) session.getValue("company");
	String str_datalib = (String) session.getValue("datalib");
	String list_customer = (String) session.getValue("list");
	String str_companyname = (String) session.getValue("companyname");

	//*************************************************************
	// ***  Define and initialise variables ***
	//*************************************************************

	int num_sequence = Integer.valueOf(str_sequence).intValue();
	int num_count = 0;
	String str_transtype = " ";
	String str_color = "#cccccc";

	//*************************************************
	// Retrieve date to be used for copyright statment 
	//*************************************************
  
	Calendar cpToday = Calendar.getInstance();
	int cpYear = cpToday.get(Calendar.YEAR) ;  


	// *****************************************************************************************
	// ***  Retrieve Header, Customer address and branch information from request parameters ***
	// *****************************************************************************************

	String str_billname = request.getParameter("str_billname");
	String str_shipname = request.getParameter("str_shipname");
	String str_billaddress1 = request.getParameter("str_billaddress1");
	String str_billaddress2 = request.getParameter("str_billaddress2");
	String str_shipaddress1 = request.getParameter("str_shipaddress1");
	String str_shipaddress2 = request.getParameter("str_shipaddress2");
	String str_billcity = request.getParameter("str_billcity");
	String str_billstate = request.getParameter("str_billstate");
	String str_billzip = request.getParameter("str_billzip");
	String str_shipcity = request.getParameter("str_shipcity");
	String str_shipstate = request.getParameter("str_shipstate");
	String str_shipzip = request.getParameter("str_shipzip");
	String str_billarea = request.getParameter("str_billarea");
	String str_billphone = request.getParameter("str_billphone");
	String str_shiparea = request.getParameter("str_shiparea");
	String str_shipphone = request.getParameter("str_shipphone");
	String branchinfo = request.getParameter("branchinfo");
	String str_location = request.getParameter("location");

	if (str_billname == null) str_billname = "";
	if (str_shipname == null) str_shipname = "";
	if (str_billaddress1 == null) str_billaddress1 = "";
	if (str_billaddress2 == null) str_billaddress2 = "";
	if (str_shipaddress1 == null) str_shipaddress1 = "";
	if (str_shipaddress2 == null) str_shipaddress2 = "";
	if (str_billcity == null) str_billcity = "";
	if (str_billstate == null) str_billstate = "";
	if (str_billzip == null) str_billzip = "";
	if (str_shipcity == null) str_shipcity = "";
	if (str_shipstate == null) str_shipstate = "";
	if (str_shipzip == null) str_shipzip = "";
	if (str_billarea == null) str_billarea = "";
	if (str_billphone == null) str_billphone = "";
	if (str_shiparea == null) str_shiparea = "";
	if (str_shipphone == null) str_shipphone = "";
	if (branchinfo == null) branchinfo = "";
	if (str_location == null) str_location = "";


	// ***************************************************************************************** 
	// ***   Output page header with customer bill to ship to address and branch information   *    
	// ***************************************************************************************** 
	
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
					<td><img src="images/empty.gif" height="10"></td>
					<td><img src="images/arrow.gif" height="10" align="right"></td>
					<td></td>
				</tr>

				<tr>
					<td><img src="images/empty.gif" height="30"></td>
					<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;extension request verification&nbsp;&nbsp;&nbsp;</td>
					<td></td>
					</tr>

			</table>

			<br>
			
      		<%
			// *******************************************************************************************
			// ***   Output the Bill-to address and Ship-to address and Location contact information
			// *******************************************************************************************
			%>
			

			<TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0" WIDTH="900">

				<tr>
			
				<td valign="top" width="560">
		
					<TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0" WIDTH="560">

						<tr>
 							<td background="images/tlcrnr.gif" align="right" width="25" height="30">&nbsp;</td>
							<td background="images/top_back.gif" width="225">&nbsp;</td>
							<td background="images/trcrnr.gif" align="left" width="25" height="30">&nbsp;</td>
							<td width="30">&nbsp;</td>
							<td background="images/tlcrnr.gif" align="right" width="25" height="30">&nbsp;</td>
							<td background="images/top_back.gif" width="225">&nbsp;</td>
							<td background="images/trcrnr.gif" align="left" width="25" height="30">&nbsp;</td>
						</tr>

						<tr>
							<td align="left" background="images/left.gif">&nbsp;</td>
							<td align="left">Bill To</td>
							<td align="left" background="images/right.gif">&nbsp;</td>
							<td>&nbsp;</td>
							<td align="left" background="images/left.gif">&nbsp;</td>
							<td align="left">Ship To</td>
							<td align="left" background="images/right.gif">&nbsp;</td>
						</tr>

						<tr>
							<td align="left" background="images/left.gif">&nbsp;</td>
							<td align="left"><a class="data"><!--BILL_NAME--><%=str_billname%></a></td>
							<td align="left" background="images/right.gif">&nbsp;</td>
							<td>&nbsp;</td>
							<td align="left" background="images/left.gif">&nbsp;</td>
							<td align="left"><a class="data"><!--SHIP_NAME--><%=str_shipname%></a></td>
							<td align="left" background="images/right.gif">&nbsp;</td>
						</tr>

						<tr>
							<td align="left" background="images/left.gif">&nbsp;</td>
							<td align="left"><a class="data"><!--BILL_ADD1--><%=str_billaddress1%><br><!--BILL_ADD2--><%=str_billaddress2%></a></td>
							<td align="left" background="images/right.gif">&nbsp;</td>
							<td>&nbsp;</td>
							<td align="left" background="images/left.gif">&nbsp;</td>
							<td align="left"><a class="data"><!--SHIP_ADD1--><%=str_shipaddress1%><br><!--SHIP_ADD2--><%=str_shipaddress2%></a></td>
							<td align="left" background="images/right.gif">&nbsp;</td>
						</tr>

						<tr>
							<td align="left" background="images/left.gif">&nbsp;</td>
							<td align="left"><a class="data"><!--BILL_CITY--><%=str_billcity%>,&nbsp;<!--BILL_STATE--><%=str_billstate%>&nbsp;<!--BILL_ZIP--><%=str_billzip%></a></td>
							<td align="left" background="images/right.gif">&nbsp;</td>
							<td>&nbsp;</td>
							<td align="left" background="images/left.gif">&nbsp;</td>
							<td align="left"><a class="data"><!--SHIP_CITY--><%=str_shipcity%>,&nbsp;<!--SHIP_STATE--><%=str_shipstate%>&nbsp;<!--SHIP_ZIP--><%=str_shipzip%></a></td>
							<td align="left" background="images/right.gif">&nbsp;</td>
						</tr>

						<tr>
							<td align="left" background="images/left.gif">&nbsp;</td>
							<td align="left"><a class="data">(<!--BILL_AREA--><%=str_billarea%>)&nbsp;<!--BILL_PHONE--><%=str_billphone%></a></td>
							<td align="left" background="images/right.gif">&nbsp;</td>
							<td>&nbsp;</td>
							<td align="left" background="images/left.gif">&nbsp;</td>
							<td align="left"><a class="data">(<!--SHIP_AREA--><%=str_shiparea%>)&nbsp;<!--SHIP_PHONE--><%=str_shipphone%></a></td>
							<td align="left" background="images/right.gif">&nbsp;</td>
						</tr>

						<tr>
							<td background="images/blcrnr.gif" align="right" height="30">&nbsp;</td>
							<td background="images/bottom_back.gif" width="225">&nbsp;</td>
							<td background="images/brcrnr.gif" align="left" height="30">&nbsp;</td>
							<td>&nbsp;</td>
							<td background="images/blcrnr.gif" align="right" height="30">&nbsp;</td>
							<td background="images/bottom_back.gif" width="225">&nbsp;</td>
							<td background="images/brcrnr.gif" align="left" height="30">&nbsp;</td>
						</tr>

					</TABLE>
			
				</td>
				<td width="110">&nbsp;</td>
				<td align="center" width="230" bgcolor="#cccccc"><a class="tableheader3"><br>Herc branch location</a><br><%=branchinfo%></td>
			</tr>
		</table>

		<br><br>


		<table cellpadding="10" cellspacing="1" border="1" width="650" bgcolor="#ffffa4">
			<tr>
				<td class="tableheader3">Please review the your entries in <a class="redbold">red</a>.  Upon submitting the request, a notification e-mail will be sent to our offices.  Please allow 2 business days for the change to take affect.</td>
			</tr>
		</table>

 		<%
 	
 		//********************************************
 		//	Form entries displayed for verification
 		//********************************************
 	
 		%>

		<FORM name="eqpExtVerifyFrm" action="servlet/EtrieveMailServlet" method =POST >
		
			<Input Type="hidden" name="reqPage"       value = "extendEquipment" >	
			<Input Type="hidden" name="contract"      value = "<%=str_contract%>" >	
			<Input Type="hidden" name="revEstDate"    value = "<%=str_revEstDate%>" >	
			<Input Type="hidden" name="contactName"   value = "<%=str_contactName %>" >
			<Input Type="hidden" name="contactPhone1" value = "<%=str_contactPhone1%>" >
			<Input Type="hidden" name="contactPhone2" value = "<%=str_contactPhone2%>" >
			<Input Type="hidden" name="contactPhone3" value = "<%=str_contactPhone3%>" >
			<Input Type="hidden" name="userComments"  value = "<%=str_userComments %>" >
			<Input Type="hidden" name="location"      value = "<%=str_location %>" >
	

			<TABLE cellpadding="3" cellspacing="1" border="0">

				<TR>
					<td valign="top" align="left" height="0">&nbsp;</td>
					<TD valign="bottom" align="left" height="0">Contract Number</TD>	
					<TD class="data" valign="top" align="left" height="0"><%=str_contract%></TD>
					<td valign="top" align="left" height="0">&nbsp;</td>
				</tr>

				<tr>
					<td valign="top" align="left">&nbsp;</td>
					<TD valign="bottom" align="left">Start Date</TD>
					<TD class="data" valign="top" align="left"><%=str_startdate%></TD>
					<td valign="top" align="left">&nbsp;</td>
				</tr>

				<tr>
					<td valign="top" align="left">&nbsp;</td>
					<TD valign="bottom" align="left">Estimated Return Date</TD>
					<TD class="data" valign="top" align="left"><%=str_estdate%></TD>
					<td valign="top" align="left">&nbsp;</td>
				</tr>

				<tr>
					<td valign="top" align="left">&nbsp;</td>
					<TD valign="bottom" align="left">Revised Estimated Return Date</TD>
					<TD class="redbold" valign="top" align="left"><%=str_revEstDate%></TD>
					<td valign="top" align="left">&nbsp;</td>
				</tr>
	
				<TR>
					<td valign="top" align="left">&nbsp;</td>
					<TD valign="middle" align="left">Contact name:</TD>
					<td valign="middle" align="left" class="redbold"><%=str_contactName%></td>
					<td valign="top" align="left">&nbsp;</td>
				</tr>
		
				<tr>
					<td valign="top" align="left">&nbsp;</td>
					<td valign="middle" align="left">Contact phone:</td>
					<td valign="middle" align="left" class="redbold">
					1-<%=str_contactPhone1%>-<%=str_contactPhone2%>-<%=str_contactPhone3%>
					</td>
					<td valign="top" align="left">&nbsp;</td>
				</tr>

				<tr>
					<td valign="top" align="left">&nbsp;</td>
					<td valign="middle" align="left">Comments:</td>
					<td valign="middle" align="left" class="redbold"><%=str_userComments%></td>
					<td valign="top" align="left">&nbsp;</td>
				</tr>
				
			</table>

			<br><br>		
		
			<INPUT type="submit" name="submit" value="Submit Extension Request">
	
		</FORM>
	
		<%
		//************************************
		//  ***  End of eqpRelVerifyFrm   ***  
		//***********************************
		%>

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
