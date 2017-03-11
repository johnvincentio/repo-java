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

//*************************************************************
// *** Retrieve and test the input parameters ***
//*************************************************************

String str_contract = request.getParameter("contract");
String str_lineNum = request.getParameter("lineNum");

if ( str_contract == null || str_lineNum == null )	
{
	request.setAttribute("errorMsg","Missing information, cannot release equipment from this application.");
	response.sendRedirect("securityFailure.jsp");			
}	

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
    <jsp:param name="errorMsg" value="Not authorized to release equipments from this application." />	
    <jsp:param name="pageUse" value="Error" />	
    </jsp:forward>
	<%
}

%>

<html>

	<!-- JSP page content header -->

	<%@ page language="java" import="java.sql.*,java.util.*,java.text.*" errorPage="ErrorPage.jsp"%>
	<jsp:useBean id="contractdetailbean" class="etrieveweb.etrievebean.contractdetailBean" scope="page" />
	<jsp:useBean id="contractdetail2bean" class="etrieveweb.etrievebean.contractdetailDBean" scope="page" />
	<jsp:useBean id="contractdetail3bean" class="etrieveweb.etrievebean.contractdetailcustomerjobBean" scope="page" />
	<jsp:useBean id="contractdetail6bean" class="etrieveweb.etrievebean.contractdetailFBean" scope="page" />

	<%
	//*************************************************************
	// ***  Retrieve session variables  ***
	//*************************************************************

	String str_username = (String) session.getValue("username");          
	String str_password = (String) session.getValue("password");        
	String str_as400url = (String) session.getValue("as400url");          
	String str_as400driver = (String) session.getValue("as400driver");
	String str_customer = (String) session.getValue("customer");
	String str_company = (String) session.getValue("company");
	String str_datalib = (String) session.getValue("datalib");
	String list_customer = (String) session.getValue("list");
	String str_companyname = (String) session.getValue("companyname");

	//*************************************************************
	// ***  Define variables and initialise variables ***
	//*************************************************************

	int num_count  = 0;
	int num_count2 = 0;
	String str_transtype = " ";
	String str_color = "#cccccc";
	String str_sequence = "0";
	int num_sequence = Integer.valueOf(str_sequence).intValue();
	 
	Connection contractdetailbeanconnection = null;
	Connection contractdetail2Abeanconnection = null;
	Connection contractdetail6beanconnection = null;
	Connection contractdetail3beanconnection = null;

	String str_account = "";
	String str_billname = "";
	String str_billaddress1 = "";
	String str_billaddress2 = "";
	String str_billcity = "";
	String str_billstate = "";
	String str_billzip  = "";
	String str_billarea = "";
	String str_billphone = "";

	String str_shipname =  "";
	String str_shipaddress1 = "";
	String str_shipaddress2 = "";
	String str_shipcity = "";
	String str_shipstate = "";
	String str_shipzip = "";
	String str_shiparea = "";
	String str_shipphone = "";

	String branchinfo = "";
	String str_startdate = "";
	String str_estdate = "";
	String str_type = "";
	String str_hertzlocation = "";

	//***************************************************************************************
	// Retrieve today's date for the copyright statment and drop down
	//****************************************************************************************
  
	Calendar cpToday = Calendar.getInstance();
	
	int    cpYear  = cpToday.get(Calendar.YEAR) ;  
	String cpMonth = Integer.toString(cpToday.get(Calendar.MONTH)+1);
	String cpDay   = Integer.toString(cpToday.get(Calendar.DAY_OF_MONTH));
	
	if(cpMonth.trim().length() == 1)    
		cpMonth = "0" + cpMonth ;

	if(cpDay.trim().length() == 1)    
		cpDay = "0" + cpDay ;

	String shortYr = Integer.toString(cpYear) ;
	
	shortYr = shortYr.substring(2);

	String today = cpMonth + "/" + cpDay + "/" + shortYr ;

	//**************************************************
	// Calculate tomorrow's date for the drop down
	//**************************************************
	
	Calendar tomCal = Calendar.getInstance();
	
	tomCal.add(Calendar.DATE, 1);

	int cpYear1	 = tomCal.get(Calendar.YEAR) ;  
	cpMonth = Integer.toString(tomCal.get(Calendar.MONTH)+1);
	cpDay = Integer.toString(tomCal.get(Calendar.DAY_OF_MONTH));

	if(cpMonth.trim().length() == 1)    
		cpMonth = "0" + cpMonth ;

	if(cpDay.trim().length() == 1)    
		cpDay = "0" + cpDay ;

	shortYr = Integer.toString(cpYear1) ;
	shortYr = shortYr.substring(2);

	String tomorrow = cpMonth + "/" + cpDay + "/" + shortYr ;

	// **************************************************
	// ***  Retrieve Header and Customer information ***
	// **************************************************

	contractdetailbeanconnection = contractdetailbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);      

	if ( ! (contractdetailbean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), str_transtype)) ) 
	{    
		%>
		<jsp:forward page="securityFailure.jsp">	
		<jsp:param name="errorMsg" value="Contract is not open to release the selected equipment." />	
		</jsp:forward>
		<%
	}

	if ( contractdetailbean.getNext() ) 
	{   

		// ***  Retrieve contract header information  ***
		
		String str_jobnumber = contractdetailbean.getColumn("RHJOB#");
		str_hertzlocation = contractdetailbean.getColumn("RHLOC");
		String str_empnum = contractdetailbean.getColumn("RHEMP#").trim();
		str_type = contractdetailbean.getColumn("RHTYPE");
		str_startdate = contractdetailbean.getColumn("RHDATO");
		str_estdate = contractdetailbean.getColumn("RHERDT");

		if ( !str_startdate.trim().equals("0") && str_startdate.length() == 8)
			str_startdate = str_startdate.substring(4, 6) + "/" + str_startdate.substring(6, 8) + "/" + str_startdate.substring(2, 4);
		else
			str_startdate = "";

		if ( !str_estdate.trim().equals("0") && str_estdate.length() == 8)
			str_estdate = str_estdate.substring(4, 6) + "/" + str_estdate.substring(6, 8) + "/" + str_estdate.substring(2, 4);
		else
			str_estdate = "";

		// ***  Initialize bill-to and ship-to address  ***

		str_account = contractdetailbean.getColumn("CMCUS#");
		str_billname = contractdetailbean.getColumn("CMNAME");
		str_billaddress1 = contractdetailbean.getColumn("CMADR1");
		str_billaddress2 = contractdetailbean.getColumn("CMADR2");
		str_billcity = contractdetailbean.getColumn("CMCITY");
		str_billstate = contractdetailbean.getColumn("CMSTAT");
		str_billzip  = contractdetailbean.getColumn("CMZIP");
		str_billarea = contractdetailbean.getColumn("CMAREA");
		str_billphone = contractdetailbean.getColumn("CMPHON");

		str_shipname =  str_billname;
		str_shipaddress1 = str_billaddress1;
		str_shipaddress2 = str_billaddress2;
		str_shipcity = str_billcity; 
		str_shipstate = str_billstate;  
		str_shipzip = str_billzip;
		str_shiparea = str_billarea;
		str_shipphone = str_billphone;

		contractdetailbean.cleanup();	
		contractdetailbean.endcurrentConnection(contractdetailbeanconnection);

		// **************************************
		// ***   Retrieve ship-to information *** 
		// **************************************

		contractdetail3beanconnection = contractdetail3bean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		if ( contractdetail3bean.getRows( Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_jobnumber) ) 
		{
			if ( contractdetail3bean.getNext() ) 
			{
				str_shipname = contractdetail3bean.getColumn("CJNAME");
				str_shipaddress1 = contractdetail3bean.getColumn("CJADR1");
				str_shipaddress2 = contractdetail3bean.getColumn("CJADR2");
				str_shipcity = contractdetail3bean.getColumn("CJCITY");
				str_shipstate = contractdetail3bean.getColumn("CJST");
				str_shipzip = contractdetail3bean.getColumn("CJZIP");
				str_shiparea = contractdetail3bean.getColumn("CJAREA");
				str_shipphone = contractdetail3bean.getColumn("CJPHON");
			}       
		}
    
		// ********************************************************
		//  Retrieve Billing address from CUSJBLFL if applicable.
		// ********************************************************

		if (contractdetail3bean.getRowsB(Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_jobnumber) ) 
		{
			if ( contractdetail3bean.getNextB() ) 
			{
				str_billname = contractdetail3bean.getColumnB("CINAME");
				str_billaddress1 = contractdetail3bean.getColumnB("CIADR1");
				str_billaddress2 = contractdetail3bean.getColumnB("CIADR2");
				str_billcity = contractdetail3bean.getColumnB("CICITY");
				str_billstate = contractdetail3bean.getColumnB("CIST");
				str_billzip = contractdetail3bean.getColumnB("CIZIP");
				str_billarea = contractdetail3bean.getColumnB("CIAREA");
				str_billphone = contractdetail3bean.getColumnB("CIPHON");
			}
		}

		contractdetail3bean.cleanup();	
		contractdetail3bean.endcurrentConnection(contractdetail3beanconnection);

		// *********************************************
		// Format the telephone numbers
		// *********************************************

		if ( !str_billphone.trim().equals("0") && str_billphone.length() == 7)
			str_billphone = str_billphone.substring(0, 3) + "-" + str_billphone.substring(3, 7);

		if ( !str_shipphone.trim().equals("0") && str_shipphone.length() == 7)
			str_shipphone = str_shipphone.substring(0, 3) + "-" + str_shipphone.substring(3, 7);

		// ***************************************************
		// Create new array to receive location information.  
		// ***************************************************

		String [] locationinfoArray = new String[4]; 
		
		if ( !str_hertzlocation.equals("") )  
		{
			contractdetail6beanconnection = contractdetail6bean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

			locationinfoArray = contractdetail6bean.getLocInfo(  str_company,  str_hertzlocation,  str_datalib );

			branchinfo = locationinfoArray[2].trim() + "<BR>" + locationinfoArray[1].trim() + "<BR><BR>" + locationinfoArray[3].trim() + "<br><br>";		

			contractdetail6bean.cleanup();
			contractdetail6bean.endcurrentConnection(contractdetail6beanconnection);

		}
	}

 	// ******************************************************************
	// ***  Determine if there were previous releases done
	// ******************************************************************

	ArrayList EqpRelContracts = (ArrayList)session.getAttribute("EqpRelContracts");
	ArrayList EqpRelLineNum   = (ArrayList)session.getAttribute("EqpRelLineNum");
	ArrayList EqpRelQtyRel    = (ArrayList)session.getAttribute("EqpRelQtyRel");

	String str_prevRel = "Y" ;
	int all_itemsQty = 0;
	int rel_itemsQty = 0;
	
	if (EqpRelContracts == null || EqpRelLineNum == null || EqpRelLineNum == null)
		str_prevRel = "N";

	if ( str_prevRel.equals("Y") )  
	{
		contractdetail2Abeanconnection = contractdetail2bean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		if (contractdetail2bean.getRows( Integer.valueOf(str_customer).intValue(), str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), str_type, "equipment", str_transtype, "") ) 
		{		
			SkipEquipment:
					
			while ( contractdetail2bean.getNext() ) 
			{
				String str_item  = contractdetail2bean.getColumn("RDITEM");
				String str_catg  = contractdetail2bean.getColumn("RDCATG");
				String str_class = contractdetail2bean.getColumn("RDCLAS");
				String str_stock = contractdetail2bean.getColumn("RDSTKC");
				String str_line  = contractdetail2bean.getColumn("RDSEQ#");
				String str_qty   = contractdetail2bean.getColumn("RDQTY");
				
				// *** Retrieve information about the line items - equipments  ***
						
				String str_description = contractdetail2bean.getDescription( str_company, str_datalib, str_item, Integer.valueOf(str_catg).intValue(), Integer.valueOf(str_class).intValue(), str_stock); 
				String str_bulk = contractdetail2bean.getColumn2("ECBULK");

				int i = str_qty.indexOf(".");	
				
				if (i > 0)
					str_qty = str_qty.substring(0,i);
							
				int num_qty = Integer.valueOf(str_qty).intValue() ;	
						
				if (num_qty == 0)			 
					continue SkipEquipment;
					
				//  *** Check for earlier release requests made from the same session. ***
				//	*** Do not display an equipment if it was released ealier in the same session *** 
						
				int lstAllRelQty = 0;
				int size = EqpRelContracts.size();	

				all_itemsQty = all_itemsQty + num_qty;
				rel_itemsQty = 0;
				
				for	(i=0; i<size ; i++)
				{
					String lstCon     = (String) EqpRelContracts.get(i);
					String lstLineNum = (String) EqpRelLineNum.get(i);
					String lstQty     = (String) EqpRelQtyRel.get(i);

					rel_itemsQty = rel_itemsQty + Integer.valueOf(lstQty).intValue();

					if (lstCon.trim().equals(str_contract.trim()) && lstLineNum.trim().equals(str_line.trim()) && lstLineNum.trim().equals(str_lineNum.trim()))
					{
						if (!str_bulk.trim().equals("Y"))
							continue SkipEquipment ;													
						else
							lstAllRelQty = lstAllRelQty + Integer.valueOf(lstQty).intValue();
					}
				}	

				if(lstAllRelQty >= num_qty )
				{
					num_count2 = 0;
					continue SkipEquipment;
				}
				else
				{
					num_qty	= num_qty - lstAllRelQty;
					str_qty = Integer.toString(num_qty);
					num_count2++;	
				}	
				
			}
		}
		
		contractdetail2bean.cleanup();
		contractdetail2bean.endcurrentConnection(contractdetail2Abeanconnection);

		if ( num_count2 == 0 || (all_itemsQty == rel_itemsQty) )
		{
		%>
			<jsp:forward page="equipRelExtConfirm.jsp">	
    		<jsp:param name="errorMsg" value="The selected contract has no eligible items for release." />	
    		<jsp:param name="pageUse" value="Error" />	
    		</jsp:forward>
    	<%
		}
	}			


	// ***************************************************************************************** 
	// ***   Output page header with customer bill to ship to address and branch information   *    
	// ***************************************************************************************** 
		
	%>
	<head>

		<title>Hertz Equipment Rental Online Reporting</title>

		<meta http-equiv="expires" content="-1">
		<meta http-equiv="pragma" content="no-cache">
		<link href="images/wynnesystems.css" rel="stylesheet" type="text/css">

		<style type="text/css">
			#tabdiv {position:absolute; top:120px; left:0px; z-index:2; visibility:show}
			#maintext {position:absolute; left:20px; width:650px; z-index:0;}
		</style>
   
   
		<script language="JavaScript" src="images/validateEqpRel.js"></script> 
   

	</HEAD>
	
	<BODY BGCOLOR="#ffffff"   onload="document.eqpRelFrm.contactName.focus()">

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
					<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;request for release&nbsp;&nbsp;&nbsp;</td>
					<td></td>
				</tr>

			</table>

			<br>
			
      		<%
			// ***************************************************************************************** 
			// ***   Output the Bill-to address and Ship-to address and Location contact information
			// ***************************************************************************************** 	
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
					<td align="center" valign="top" width="230" bgcolor="#cccccc"><a class="tableheader3"><br>Herc branch location</a><br><%=branchinfo%></td>
				</tr>
		
			</table>

			<br><br>
 	
 			<%
 	
 			//********************************************
 			//	Form entries to be filled in by the User
 			//********************************************

 			%>

			<table cellpadding="10" cellspacing="1" border="1" width="650" bgcolor="#ffffa4">
				<tr>
					<td class="tableheader3">To request a rental pickup, please complete this form.  A notification e-mail will be sent to our offices to schedule a pickup.  Required entries are marked with a <a class="redbold">*</a></td>
				</tr>
			</table>

			<br>
	
			<FORM name="eqpRelFrm" action="releaseEquipVerify.jsp" method =POST onsubmit="return validateData();" >	

				<%
					// *** Customer Bill to, Ship to address and Herc branch location information  ****
				%>

				<Input Type="hidden" name="contract"         value = "<%=str_contract%>" >
				<Input Type="hidden" name="startdate"        value = "<%=str_startdate%>" >
				<Input Type="hidden" name="estdate"          value = "<%=str_estdate%>" >
				<Input Type="hidden" name="str_billname"     value = "<%=str_billname.trim()%>" >
				<Input Type="hidden" name="str_shipname"     value = "<%=str_shipname.trim()%>" >
				<Input Type="hidden" name="str_billaddress1" value = "<%=str_billaddress1.trim()%>" >
				<Input Type="hidden" name="str_billaddress2" value = "<%=str_billaddress2.trim()%>" >
				<Input Type="hidden" name="str_shipaddress1" value = "<%=str_shipaddress1.trim()%>" >
				<Input Type="hidden" name="str_shipaddress2" value = "<%=str_shipaddress2.trim()%>" >
				<Input Type="hidden" name="str_billcity"     value = "<%=str_billcity.trim()%>" >
				<Input Type="hidden" name="str_billstate"    value = "<%=str_billstate.trim()%>" >
				<Input Type="hidden" name="str_billzip"      value = "<%=str_billzip.trim()%>" >
				<Input Type="hidden" name="str_shipcity"     value = "<%=str_shipcity.trim()%>" >
				<Input Type="hidden" name="str_shipstate"    value = "<%=str_shipstate.trim()%>" >
				<Input Type="hidden" name="str_shipzip"      value = "<%=str_shipzip.trim()%>" >
				<Input Type="hidden" name="str_billarea"     value = "<%=str_billarea.trim()%>" >
				<Input Type="hidden" name="str_billphone"    value = "<%=str_billphone.trim()%>" >
				<Input Type="hidden" name="str_shiparea"     value = "<%=str_shiparea.trim()%>" >
				<Input Type="hidden" name="str_shipphone"    value = "<%=str_shipphone.trim()%>" >
				<Input Type="hidden" name="branchinfo"       value = "<%=branchinfo.trim()%>" >
				<Input Type="hidden" name="location"         value = "<%=str_hertzlocation.trim()%>" >
	
				<TABLE cellpadding="3" cellspacing="1" border="0" width="900" >

					<TR>
						<TD width="10%" valign="bottom" align="center">Contract<br>Number</TD>	
						<TD width="10%" valign="bottom" align="center">Start<br>Date</TD>	
						<TD width="10%" valign="bottom" align="center">Estimated<br>Return Date</TD>	
						<TD width="12%" valign="bottom" align="center">Release<br>Date</TD>
						<TD width="8%"  valign="bottom" align="center">Release<br>Time</TD>
						<TD width="50%" valign="bottom" align="center">&nbsp;</TD>
					</TR>
					
					<TR>
						<TD class="data" valign="top" align="center"><%=str_contract%></TD>
						<TD class="data" valign="top" align="center"><%=str_startdate%></TD>
						<TD class="data" valign="top" align="center"><%=str_estdate%></TD>
						<td align="center" valign="top" ><a class="redbold">*</a>
							<select name="relDate">
								<option value="<%=today%>"><%=today%>
								<option value="<%=tomorrow%>"><%=tomorrow%>
								<option value="ASAP">ASAP										
							</select>
						</td>
						<td align="center" valign="top" ><a class="redbold">*</a>
							<select name="relTime">
								<option value="AM">AM
								<option value="PM">PM										
							</select>
						</td>
						<td valign="top" align="left" valign="middle"><a class="redbold">NOTE:</a> A release can only be requested within the next 24 hours. Please allow 2 business days for the on-pickup status to be updated.</td>
					</tr>

				</table>

				<br>
	
				<table cellpadding="3" cellspacing="1" border="0" width="900">

					<caption class="tableheader3" align="left">The following are on-rent items that can be released for this contract.</caption>

					<tr>
						<td background="images/empty.gif" bgcolor="#000000" align="center" valign="bottom" width="3%" class="whitemid">&nbsp;</td>
						<td background="images/empty.gif" bgcolor="#000000" align="center" valign="bottom" width="15%" class="whitemid">Equipment</td>
						<td bgcolor="#000000" background="images/empty.gif" align="center" valign="bottom" width="20%" class="whitemid">Quantity to Release</td>
						<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="bottom" width="62%" class="whitemid">Description</td>
					</tr>			

					<%


					// ******************************
					//   Retrieve equipment detail 
					// ******************************
	
					contractdetail2Abeanconnection = contractdetail2bean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

					if (contractdetail2bean.getRows( Integer.valueOf(str_customer).intValue(), str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), str_type, "equipment", str_transtype, "") ) 
					{		
						NextEquipment:
					
						while ( contractdetail2bean.getNext() ) 
						{   
							if ( str_color.equals("#cccccc") ) 
								str_color = "#999999"; 
							else 
								str_color = "#cccccc";
   
							String str_item = contractdetail2bean.getColumn("RDITEM");
							String str_catg = contractdetail2bean.getColumn("RDCATG");
							String str_class = contractdetail2bean.getColumn("RDCLAS");
							String str_stock = contractdetail2bean.getColumn("RDSTKC");
							String str_line = contractdetail2bean.getColumn("RDSEQ#");
						
							// *** Retrieve information about the line items - equipments  ***
						
							String str_description = contractdetail2bean.getDescription( str_company, str_datalib, str_item, Integer.valueOf(str_catg).intValue(), Integer.valueOf(str_class).intValue(), str_stock); 
							String str_bulk = contractdetail2bean.getColumn2("ECBULK");
							String str_qty = contractdetail2bean.getColumn("RDQTY");
							String str_pickup = contractdetail2bean.getPickupStatus(str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_line).intValue(), str_item  );

							// *** Skip an equipment if a pickup ticket exists ***
  
							if ( str_pickup.trim().equals("OP") )
								continue NextEquipment ;

							// *** Check if there is enough quantity on rent to be release by this request *** 
						
							int i = str_qty.indexOf(".");	
							
							if (i > 0)
								str_qty = str_qty.substring(0,i);
							
							int num_qty = Integer.valueOf(str_qty).intValue() ;	
						
							if (num_qty == 0)			 
								continue NextEquipment ;

							String str_currentQty = str_qty;

							//	*** Determine if there are eligible items to release
						
							if(str_prevRel.equals("Y"))
							{
								int lstAllRelQty = 0;
								int size = EqpRelContracts.size();	
								
								for	(i=0; i<size ; i++)
								{
									String lstCon     = (String) EqpRelContracts.get(i);
									String lstLineNum = (String) EqpRelLineNum.get(i);
									String lstQty     = (String) EqpRelQtyRel.get(i);
										
									if (lstCon.trim().equals(str_contract.trim()) && lstLineNum.trim().equals(str_line.trim()) )
									{
										if (!str_bulk.trim().equals("Y"))
											continue NextEquipment ;													
										else
											lstAllRelQty = lstAllRelQty + Integer.valueOf(lstQty).intValue() ;
									}
								}	
							
								if(lstAllRelQty >= num_qty )
									continue NextEquipment ;
								else
								{
									num_qty	= num_qty - lstAllRelQty;
									str_qty = Integer.toString(num_qty);
								}		
						
							}

							//   *** Create selection list for items with quantity > 1 ***
				
							String str_dummyQty = str_qty ;
						 
							if (num_qty > 1 )
							{
								String str_bulkSelect = "<select name=\"blkQty\"> ";
								
								for (i=num_qty ; i>0 ; i--)
									str_bulkSelect = str_bulkSelect.trim() + "<option value=\"" + i +"\">" + i ;										
																
								str_bulkSelect = str_bulkSelect.trim() + "</select>" ;
								str_qty = str_bulkSelect ;
								
								str_dummyQty = "<select>";
							}

							//  *** If the equipment being processed was the one selected by user in the parent report, ***
							//  *** it would be automatically selected when page is displayed for the first time ***

							String chkStat = "";

							if (str_line.trim().equals(str_lineNum.trim()))
								chkStat = "checked" ;

							num_count++;		
											
							%>
					
							<tr>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" valign="top" align="center" class="tabledata"><input type="checkbox" name="EqpChkd" <%=chkStat%>></td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" valign="top" align="center" class="tabledata"><!--ITEM--><%=str_item%></td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" valign="top" align="center" class="tabledata"><!--QUANTITY--><%=str_qty%></td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" valign="top" align="left" class="tabledata"><!--DESC--><%=str_description%></td>
							</tr>
					
							<Input Type="hidden" name="eqpSel" value = "" >	
							<Input Type="hidden" name="item" value = "<%=str_item%>" >	
							<Input Type="hidden" name="lineNum" value = "<%=str_line%>" >	
							<Input Type="hidden" name="qtyRel" value = "<%=str_dummyQty%>" >	
							<Input Type="hidden" name="description" value = "<%=str_description%>" >
							<Input Type="hidden" name="currentQty" value = "<%=str_currentQty%>" >

							<%

						} 
					
						contractdetail2bean.cleanup();
						contractdetail2bean.endcurrentConnection(contractdetail2Abeanconnection);
					}
			     
					%>

				</table>

				<br>
		
				<%							
		
				// *************************************************************************************
				// ** Display other section if there is at least one equipment available for release
				// *************************************************************************************
			
				if (num_count == 0)
				{		
				%>
					<a class="redbold">There are no eligible releases for this contract. Please select another contract.
					Click </a><a href="javascript:history.go(-1)"><b>here </b></a><a class="redbold"> to go back.</a><br><br>
				<%	
			
				}		
				else
				{
	
				%>

					<TABLE cellpadding="3" cellspacing="1" border="0">

						<TR>
							<TD valign="middle" align="center"> <a class="redbold">*</a>&nbsp;Contact name</TD>
							<td valign="middle" align="center"> <INPUT type="text"  name="contactName" size="40" maxlength="40"></td>
							<td valign="middle" align="center"> <a class="redbold">*</a>&nbsp;Contact phone</td>
							<td valign="middle" align="left"><INPUT type="text"  name="contactPhone1" size="1" maxlength="3"><INPUT type="text"  name="contactPhone2" size="1" maxlength="3"><B>-</B><INPUT type="text" name="contactPhone3" size="2" maxlength="4"></td>
						</tr>
					</table>

					<br>
		
					<table cellpadding="3" cellspacing="1" border="0" width="900" >
						<tr>
							<td><a class="redbold">*</a>&nbsp;Please enter detailed information regarding access...</td>
						</tr>
						<tr>
							<td><TEXTAREA rows="5" cols="110" name="userComments"></TEXTAREA></td>
						</tr>
					</table>
	
					<br>

					<Input Type="hidden" name="num_count" value = "<%=num_count%>" >
					<INPUT type="submit" name="submit" value="Continue to next page..." >

				<%	
			
				}		
	
				%>

			</FORM>
	
			<%
			//*******************************
			//  ***  End of EqpRelFrm   ***  
			//*******************************
			%>

			<br><br>
	
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
