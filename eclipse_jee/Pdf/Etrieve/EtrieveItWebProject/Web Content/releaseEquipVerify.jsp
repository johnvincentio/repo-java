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
//    RI001    DTC2073    02/06/06     SR35873              Abbr. Equipment Release
// **************************************************************************************************************

String[]    names = session.getValueNames();
		 
if ( names.length <= 0 || names[0] == "")
  response.sendRedirect("securityFailure.jsp");	
    
//*********************************************************************
//*** Verify that the user has required authority for equipment release
//*********************************************************************
 
String equipChangeAuth = (String)session.getAttribute("equipChangeAuth");

if  (equipChangeAuth == null) equipChangeAuth = "";

if  (equipChangeAuth.equals("") || !equipChangeAuth.equals("Yes") )
{
	equipChangeAuth = "";
	%>
	<jsp:forward page="equipRelExtConfirm.jsp">	
    <jsp:param name="errorMsg" value="Not authorized to release a contract from this application." />	
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
	String str_relDate = request.getParameter("relDate");
	String str_relTime = request.getParameter("relTime");
	String str_contactName = request.getParameter("contactName");
	String str_contactPhone1 = request.getParameter("contactPhone1");
	String str_contactPhone2 = request.getParameter("contactPhone2");
	String str_contactPhone3 = request.getParameter("contactPhone3");
	String str_userComments = request.getParameter("userComments");
	String str_sequence   = request.getParameter("sequence");

	if ( str_contract == null )	str_contract = "";
	if ( str_startdate == null ) str_startdate = "";
	if ( str_estdate == null )	str_estdate = "";
	if ( str_relDate == null )	str_relDate = "";
	if ( str_relTime == null )	str_relTime = "";
	if ( str_contactName == null )	str_contactName = "";
	if ( str_contactPhone1 == null ) str_contactPhone1 = "000";
	if ( str_contactPhone2 == null ) str_contactPhone2 = "000";
	if ( str_contactPhone3 == null ) str_contactPhone3 = "000";
	if ( str_userComments == null )	str_userComments = "";
	if ( str_sequence == null )	    str_sequence = "0";

	if ( str_contract.trim().equals(""))	
	{
		request.setAttribute("errorMsg","Missing information, cannot release equipment from this application.");
		response.sendRedirect("securityFailure.jsp");			
	}	
	
	//*************************************************************
	// ***  Retrieve session variables  ***
	//*************************************************************

	String str_customer    = (String) session.getValue("customer");
	String str_company     = (String) session.getValue("company");
	String str_datalib     = (String) session.getValue("datalib");
	String list_customer   = (String) session.getValue("list");
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
					<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;request release verification&nbsp;&nbsp;&nbsp;</td>
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
					<td align="center" valign="top" width="230" bgcolor="#cccccc"><a class="tableheader3"><br>Herc branch location</a><br><%=branchinfo%><br></td>
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

			<br>

			<FORM name="eqpRelVerifyFrm" action="servlet/EtrieveMailServlet" method =POST >
		
				<Input Type="hidden" name="reqPage"       value = "releaseEquipment" >	
				<Input Type="hidden" name="contract"      value = "<%=str_contract%>" >	
				<Input Type="hidden" name="relDate"       value = "<%=str_relDate%>" >	
				<Input Type="hidden" name="relTime"       value = "<%=str_relTime%>" >
				<Input Type="hidden" name="contactName"   value = "<%=str_contactName %>" >
				<Input Type="hidden" name="contactPhone1" value = "<%=str_contactPhone1%>" >
				<Input Type="hidden" name="contactPhone2" value = "<%=str_contactPhone2%>" >
				<Input Type="hidden" name="contactPhone3" value = "<%=str_contactPhone3%>" >
				<Input Type="hidden" name="userComments"  value = "<%=str_userComments %>" >
				<Input Type="hidden" name="location"      value = "<%=str_location %>" >			
		
				<TABLE cellpadding="3" cellspacing="1" border="0" width="900" >

					<TR>
						<TD width="10%" valign="bottom" align="center">Contract<br>Number</TD>	
						<TD width="10%" valign="bottom" align="center">Start<br>Date</TD>	
						<TD width="10%" valign="bottom" align="center">Estimated<br>Return Date</TD>	
						<TD width="12%" valign="bottom" align="center">Release<br>Date</TD>
						<TD width="8%" valign="bottom" align="center">Release<br>Time</TD>
						<TD width="50%" valign="bottom" align="center">&nbsp;</TD>
					</tr>
				
					<tr>
						<TD class="data" valign="top" align="center"><%=str_contract%></TD>
						<TD class="data" valign="top" align="center"><%=str_startdate%></TD>
						<TD class="data" valign="top" align="center"><%=str_estdate%></TD>
						<td class="redbold" align="center" valign="top" ><%=str_relDate%></td>
						<td class="redbold" align="center" valign="top" ><%=str_relTime%></td>
						<td valign="top" align="left" valign="middle">&nbsp;</td>
					</tr>

				</table>

				<br>
	
				<%
	
				// ******************************
				//   Display equipment detail 
				// ******************************
	
				%>	
	
				<table cellpadding="3" cellspacing="1" border="0" width="900">

					<tr>
						<td background="images/empty.gif" bgcolor="#000000" align="center" valign="bottom" width="15%" class="whitemid">Equipment</td>
						<td bgcolor="#000000" background="images/empty.gif" align="center" valign="bottom" width="15%" class="whitemid">Quantity to Release</td>
						<td bgcolor="#000000" background="images/empty.gif" align="left" valign="bottom" width="70%" class="whitemid">Description</td>
					</tr>			

				<%
			
					// ******************************
					//   Retrieve variable arrays
					// ******************************
	
					int i=0;

					String EqpChkd[]     = request.getParameterValues("EqpChkd");
					String eqpSel[]      = request.getParameterValues("eqpSel");
					String item[]        = request.getParameterValues("item");
					String line[]        = request.getParameterValues("lineNum");
					String description[] = request.getParameterValues("description");
					String qtyRel[]      = request.getParameterValues("qtyRel");			
					String blkQty[]      = request.getParameterValues("blkQty");
					String currQty[]     = request.getParameterValues("currentQty");

					int blkCount = 0 ;

					// **** Process all line items displayed on the release page ******

					for ( i=0; i < eqpSel.length; i++)
  					{

						// *** Verify if the quantity value ***
						// *** For bulk items having  quantity > 1, the str_qty will contain an indicator value only
						// ***     Retrive actual selected value from the drop down quantity array    ***
				  			
						String str_qty = qtyRel[i];
						int ind = str_qty.indexOf("select");

						if (ind >= 0)
						{	
							if (blkQty != null)	
							{
								str_qty = blkQty[blkCount] ;	
								blkCount = blkCount + 1;
							}	
							else
								str_qty = "0.0"	;
						} 

						// *** If this line item was selected by user display on the confirm page *** 
			
						if  (eqpSel[i].equals("Y"))
						{

							String str_item = item[i];
							String str_line = line[i];					
							String str_des = description[i];
							String str_currentQty = currQty[i];
							
							num_count++;
					
			 			%>
							<tr>
								<td bgcolor="#999999" background="images/empty.gif" valign="top" align="center" class="redbold"><!--ITEM--><%=str_item%></td>
								<td bgcolor="#999999" background="images/empty.gif" valign="top" align="center" class="redbold"><!--QUANTITY--><%=str_qty%></td>
								<td bgcolor="#999999" background="images/empty.gif" valign="top" align="left" class="tabledata"><!--DESC--><%=str_des%></td>
							</tr>

							<Input Type="hidden" name="item" value = "<%=str_item%>" >	
							<Input Type="hidden" name="qtyRel" value = "<%=str_qty%>" >	
							<Input Type="hidden" name="line" value = "<%=str_line%>" >	
							<Input Type="hidden" name="currentQty" value = "<%=str_currentQty%>" >	

						<%
						}
					}
					%>

				</table>

				<br>

				<table cellpadding="3" cellspacing="1" border="0">

					<TR>
						<td valign="top" align="left">&nbsp;</td>
						<TD valign="middle" align="left">Contact name:</TD>
						<td valign="middle" align="left" class="redbold"><%=str_contactName%></td>
					</tr>
				
					<tr>
						<td valign="top" align="left">&nbsp;</td>
						<td valign="middle" align="left">Contact phone:</td>
						<td valign="middle" align="left" class="redbold">1-<%=str_contactPhone1%>-<%=str_contactPhone2%>-<%=str_contactPhone3%></td>
					</tr>
				
					<tr>
						<td valign="top" align="left">&nbsp;</td>
						<td valign="top" align="left">Comments:</td>
						<td valign="top" align="left" class="redbold"><%=str_userComments%></td>
					</tr>
				
				</table>

				<Input Type="hidden" name="num_count" value = "<%=num_count%>" >

				<br><br>
				
				<INPUT type="submit" name="submit" value="Submit Release Request">

	
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
