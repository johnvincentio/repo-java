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
//    Index     User Id       Date          Project                       Desciption
//    -------  ----------  -----------  ------------------  ----------------------------------------------------------
//    x001      DTC9028     11/26/02     SR26609 PCR1        Modification for the new log-in process
//    x002      DTC9028     11/26/02     SR26609 PCR1        Ensure that connection are ended 
//    x003      DTC9028     12/26/02     SR26609 PCR1        Changed the "log out" to "close reports"
//    x004      DTC9028     12/26/02     SR26609 PCR1        Changed the page title 
//    x005      DTC9028     12/26/02     SR26609 PCR1        Changed the copy right text 
//    x006      DTC9028     12/26/02     SR26609 PCR1        Add the account number
//    x007      DTC9028     02/11/03     SR26609 PCR1        Replace RHJOBL with RHPO#
//    x008      DTC9028     02/11/03     SR26609 PCR1        Added functionality to only display requested aging buckets
//    x009      DTC9028     04/28/03     SR28586 PCR1        Add logic to check security for a customer rep
//    x010      DTC2073     08/13/04     SR31413 PCR5        Add sort options
//    RI011     DTC2073     02/01/05     SR31413 PCR26       Add copyright year dynamically to the page
//    RI012     DTC2073     05/06/05     SR31413 PCR24       Data download feature
//    RI013     DTC9028     07/22/05     SR31413 PCR19       Implement datasource
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

<script language="JavaScript" src="images/validateSort.js">
</script> 

</HEAD>

<%@ page language="java" import="java.sql.*,java.util.*,java.text.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="openinvoicebean" class="etrieveweb.etrievebean.openinvoiceBean" scope="page" />
<jsp:useBean id="invoiceagingbean" class="etrieveweb.etrievebean.invoiceagingBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />
<jsp:useBean id="sortOptionBean" class="etrieveweb.etrievebean.sortOptionBean" scope="page" />


<%
String str_username = (String)session.getValue("username");
String str_customer = (String) session.getValue("customer");
String str_company = (String) session.getValue("company");
String str_companyname  = (String) session.getValue("companyname");
String str_datalib = (String) session.getValue("datalib");
String list_customer = (String) session.getValue("list");
String str_password  = (String)session.getValue("password");
String str_as400url  = (String)session.getValue("as400url");
String str_as400driver = (String)session.getValue("as400driver");
String loginuserid = (String)session.getValue("loginuserid");
String str_isUsingInternational = (String)session.getValue("isUsingInternational");  // RI013

String str_userid = "";

if ( loginuserid == null )
	loginuserid = "";

Connection MenuSecurityBeanconnection = null;
Connection openinvoicebeanconnection = null;
Connection invoiceagingbeanconnection = null;

//*************************************************************
// RI011 - Retrieve today's date to be used for copyright statment.
//*************************************************************
  
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  

// ************************
// Get input parameters
// ************************

String startnumber = request.getParameter("startnumber");
String jobnumber   = request.getParameter("jobnumber");
String str_agebucket = request.getParameter("aging");
String str_agingdate = request.getParameter("agedate");

String org_agebucket = str_agebucket;

String sortFld0 = (String) request.getParameter("sortFld0");
String sortFld1 = (String) request.getParameter("sortFld1");
String sortFld2 = (String) request.getParameter("sortFld2");

String order0 = (String) request.getParameter("order0");
String order1 = (String) request.getParameter("order1");
String order2 = (String) request.getParameter("order2");

String str_dateselect = "";
String str_agetotal = "";
String str_agetotaldesc = "";

if ( startnumber == null )	startnumber = "0";
if ( jobnumber == null )	jobnumber = ""; 
if ( str_agebucket == null )	str_agebucket = ""; 
if ( str_agingdate == null )	str_agingdate = "";
 
int dayspos = str_agebucket.indexOf("days"); 

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

if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U84") && str_agebucket.equals("") )
{
	response.sendRedirect("menu.jsp");
}
 

MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);


// ***********************************
// Format the sort options table
// ***********************************

String webPageName = "openInvoice";

String[] returnArray = sortOptionBean.getSortOptions(webPageName, sortFld0, sortFld1, sortFld2, order0, order1, order2);

String str_OrderBy = returnArray[0];
String sortOptionsTable = returnArray[1];
String hiddenSortFields = returnArray[2];

// ********************************
// Download variables
// ********************************

String displayPageName = "Open Invoice Report ";	// RI012

// ****************************************
// Get aging date and format date selection criteria
// ****************************************

if ( str_agebucket.equals("open")  ||  ( dayspos == -1 && !str_agebucket.equals("curr") )  )
	str_agebucket = "";

if ( !str_agebucket.equals("") )  {

	Calendar Today = Calendar.getInstance();

	int temptoday = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

	if ( str_agingdate.length() != 8 )
		str_agingdate = Integer.toString(temptoday);

	int ageyear=0, agemonth=0, ageday=0;

	Calendar Age30 = Calendar.getInstance();
	Calendar Age60 = Calendar.getInstance();
	Calendar Age90 = Calendar.getInstance(); 
	Calendar Age120 = Calendar.getInstance();
	Calendar Age150 = Calendar.getInstance();

	ageyear = Integer.valueOf( str_agingdate.substring(0, 4) ).intValue();
	agemonth = Integer.valueOf( str_agingdate.substring(4, 6) ).intValue();
	ageday  = Integer.valueOf( str_agingdate.substring(6, 8) ).intValue();

	// *************************************************************
	// Account for the Java date handling where January = 0 and December = 11
	// *************************************************************
  
	agemonth = agemonth - 1;

	Age30.set(ageyear,   agemonth, ageday);
	Age60.set(ageyear,   agemonth, ageday);
	Age90.set(ageyear,   agemonth, ageday);
	Age120.set(ageyear, agemonth, ageday);
	Age150.set(ageyear, agemonth, ageday);

	Age30.add(Calendar.DATE, -29);
	Age60.add(Calendar.DATE, -59);
	Age90.add(Calendar.DATE, -89);
	Age120.add(Calendar.DATE, -119);
	Age150.add(Calendar.DATE, -149);

	int fromdate = 0;
	int todate = 0;

	todate = Age30.get(Calendar.YEAR)*10000 + (Age30.get(Calendar.MONTH)+1)*100 + Age30.get(Calendar.DAY_OF_MONTH);
	String str_current = " and AHDUED >= " + Integer.toString(todate);

	fromdate = Age60.get(Calendar.YEAR)*10000 + (Age60.get(Calendar.MONTH)+1)*100 + Age60.get(Calendar.DAY_OF_MONTH);
	todate = Age30.get(Calendar.YEAR)*10000 + (Age30.get(Calendar.MONTH)+1)*100 + Age30.get(Calendar.DAY_OF_MONTH);
	String str_age30 = " and AHDUED >= " + Integer.toString(fromdate) + " and AHDUED < " + Integer.toString(todate);

	fromdate = Age90.get(Calendar.YEAR)*10000 + (Age90.get(Calendar.MONTH)+1)*100 + Age90.get(Calendar.DAY_OF_MONTH);
	todate = Age60.get(Calendar.YEAR)*10000 + (Age60.get(Calendar.MONTH)+1)*100 + Age60.get(Calendar.DAY_OF_MONTH);
	String str_age60 = " and AHDUED >= " + Integer.toString(fromdate) + " and AHDUED < " + Integer.toString(todate);

	fromdate = Age120.get(Calendar.YEAR)*10000 + (Age120.get(Calendar.MONTH)+1)*100 + Age120.get(Calendar.DAY_OF_MONTH);
	todate = Age90.get(Calendar.YEAR)*10000 + (Age90.get(Calendar.MONTH)+1)*100 + Age90.get(Calendar.DAY_OF_MONTH);
	String str_age90 = " and AHDUED >= " + Integer.toString(fromdate) + " and AHDUED < " + Integer.toString(todate);

	fromdate = Age150.get(Calendar.YEAR)*10000 + (Age150.get(Calendar.MONTH)+1)*100 + Age150.get(Calendar.DAY_OF_MONTH);
	todate = Age120.get(Calendar.YEAR)*10000 + (Age120.get(Calendar.MONTH)+1)*100 + Age120.get(Calendar.DAY_OF_MONTH);
	String str_age120 = " and AHDUED >= " + Integer.toString(fromdate) + " and AHDUED < " + Integer.toString(todate);

	todate = Age150.get(Calendar.YEAR)*10000 + (Age150.get(Calendar.MONTH)+1)*100 + Age150.get(Calendar.DAY_OF_MONTH);
	String str_age150 = " and AHDUED < " +  Integer.toString(todate);


	invoiceagingbeanconnection = invoiceagingbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

	String[] str_array = invoiceagingbean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_agingdate, str_agingdate, str_isUsingInternational);	// RI013

	if ( str_agebucket.equals("curr") )  {
		str_dateselect = str_current;
		str_agetotal   = str_array[1];
		str_agetotaldesc = "Current invoices:";
	} else if ( str_agebucket.equals("30days") )  {
		str_dateselect = str_age30;
		str_agetotal   = str_array[2];
		str_agetotaldesc = "30 days total:";
	} else if ( str_agebucket.equals("60days") )  {
		str_dateselect = str_age60;
		str_agetotal   = str_array[3];
		str_agetotaldesc = "60 days total:";
	} else if ( str_agebucket.equals("90days") )  {
		str_dateselect = str_age90;
		str_agetotal   = str_array[4];
		str_agetotaldesc = "90 days total:";
	} else if ( str_agebucket.equals("120days") ) {
		str_dateselect = str_age120;
		str_agetotal   = str_array[5];
		str_agetotaldesc = "120 days total:";
	} else if ( str_agebucket.equals("150days") )  {
		str_dateselect = str_age150;
		str_agetotal   = str_array[7];
		str_agetotaldesc = "Over 150 days total:";
	}

    
	int decpos = 0;

	decpos = str_agetotal.indexOf("."); 

	if (decpos == -1 )
		str_agetotal = str_agetotal + ".00";

	if (  (   (str_agetotal.length()-1)  - decpos ==  1 )  )
		str_agetotal = str_agetotal + "0";

	invoiceagingbean.endcurrentConnection(invoiceagingbeanconnection);


}

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

		<center>

			<table border="0" width="650" cellspacing="0" cellpadding="0">
				<tr>
					<td background="images/bottom_back.gif" width="25">&nbsp;</td>
					<td background="images/bottom_back.gif" width="595"><!--COMPANY_NAME--><a class="company"><%=str_companyname.trim()%></a>&nbsp;&nbsp;-&nbsp;&nbsp;Account Number: <!--COMPANY_NUMBER--><a class="data"><%=str_customer%></a></td>
					<td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
				</tr>
				<tr>
					<td><img src="images/empty.gif" height="10"></td>
					<td><img src="images/arrow.gif" height="10" align="right"></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><img src="images/empty.gif" height="30"></td>
					<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;open invoices&nbsp;&nbsp;&nbsp;</td>
					<td></td>
				</tr>
			</table>

			<br>
			
		</center>

		<%

		// ***************************************************
		// Make a new AS400 connection to retrieve information
		// ***************************************************

		openinvoicebeanconnection = openinvoicebean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		String str_color    = "#cccccc";
  
		String str_total    = openinvoicebean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_dateselect);

		int num_display = 5000;
		int num_count = 0;
		int num_total = Integer.valueOf(str_total).intValue();
		int num_current = Integer.valueOf(startnumber).intValue();
		int num_nextstart = num_current + num_display;
		int num_prevstart = num_current - num_display;

		if ( num_prevstart <= 0 )
			num_prevstart = 0;

		// ***************************************************
		//  RI012 - Setup the data list array used for the download.  
		//  The first line is reserved for column heading
		// ***************************************************
		
		String dataList [][] = new String[num_total+1][9];		// RI012

		dataList[0][0] = "Account#";		// RI012
		dataList[0][1] = "Account Name";	// RI012	
		dataList[0][2] = "Invoice #";		// RI012
		dataList[0][3] = "Inv Date";		// RI012
		dataList[0][4] = "Loc";				// RI012
		dataList[0][5] = "Status";			// RI012
		dataList[0][6] = "Original";		// RI012
		dataList[0][7] = "Balance";			// RI012
		dataList[0][8] = "Purchase Order";	// RI012
		
		// ****************************************************************
		// RI012 - Attempt to retrieve session objects for this report
		// ****************************************************************

		String sesDataList[][] = (String [][])session.getAttribute("dataList");		// RI012
		String reportName      = (String)session.getAttribute("reportName");		// RI012
		boolean loadDataList   = false;												// RI012

		if ( num_current == 0 || sesDataList == null || reportName == null || ( reportName != null && !reportName.equals(webPageName) ) )	// RI012
		{ 
			session.removeAttribute("dataList");		// RI012
			session.removeAttribute("reportName");		// RI012
			loadDataList = true;						// RI012
		}												// RI012
		else											// RI012
		{												// RI012
			dataList = sesDataList;						// RI012
		}												// RI012

		// *****************************************************************
		// RI012 - Add the table that will position the download button
		// *****************************************************************	

		if (num_total > 0 ) { 		// RI012
		
		%>

			<table border="0" width="650">   
				<tr>
					<td width="473" class="tableheader3" align=right" valign="top">&nbsp;</td>
					<td width="45" align=right" valign="middle">&nbsp;</td>
					<td width="87" class="footerblack" align="right">
						<Form action="servlet/EtrieveDownLoadServlet"  method=POST> 
							<input type=image SRC="images/download4.JPG" height="22" width="86" BORDER=0 align="right" valign="bottom">
							<Input TYPE="hidden" name="filename" value = "<%=displayPageName%><%=str_agebucket%>" >
						</form>
					</td>
					<td width="45" class="footerblack" align="left">
						<class="history"><a href='downloaddetails.jsp'>[details]</a>
					</td>
				</tr>
			</table>
		<%
		}    // RI012
		%>
		
		
		<table cellpadding="3" cellspacing="1" border="0" width="650">

			<%	
			// *********************************************
			// Output the sort options table
			// *********************************************
	
			if (num_total > 0)  {  
			%>
				<Form action="openInvoice.jsp"  name="sortFrm" method=POST  onsubmit=" return validate();">
					<tr bgcolor="#999999">
						<td colspan="7" align="center"> 
							<input type="hidden" name="jobnumber" value="<%=jobnumber%>">
							<input type="hidden" name="aging" value="<%=org_agebucket%>"> 
							<input type="hidden" name="agedate" value="<%=str_agingdate%>">  
							<%=sortOptionsTable%>
						</td>
					</tr>
				</Form>
			<%	
			} 
			%>
		
			<tr>
				<td background="images/empty.gif" bgcolor="#000000" align="right"  width="13%" valign="top" class="whitemid">Invoice #</td>
				<td background="images/empty.gif" bgcolor="#000000" align="center" width="9%"  valign="top" class="whitemid">Inv Date</td>
				<td background="images/empty.gif" bgcolor="#000000" align="center" width="5%"  valign="top" class="whitemid">Loc</td>
				<td background="images/empty.gif" bgcolor="#000000" align="center" width="10%" valign="top" class="whitemid">ST</td>
				<td background="images/empty.gif" bgcolor="#000000" align="right"  width="12%" valign="top" class="whitemid">Original</td>
				<td background="images/empty.gif" bgcolor="#000000" align="right"  width="12%" valign="top" class="whitemid">Balance</td>
				<td background="images/empty.gif" bgcolor="#000000" align="left"   width="39%" valign="top" class="whitemid">Purchase Order</td>
			</tr>



			<%
			
			// *************************************************************
			//  RI012 - Determine no of records to be retrieved and processed.
			// *************************************************************

			int num_start = 0;					// RI012
							
			if  ( !loadDataList )				// RI012
					num_start = num_current;	// RI012
						
			if ( openinvoicebean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_dateselect, num_start, str_OrderBy) ) {     // RI012

				// ********************************************************************************
				// RI012 - Break out of the loop if the data list has already been loaded
				//         and the page record limit has been reached
				// ********************************************************************************
					
				// RI012 -    while ( openinvoicebean.getNext() && num_count < num_display) {


				while ( openinvoicebean.getNext() ) {		// RI012
					
					if ( (num_count >= num_display) && !loadDataList )	// RI012
						break;											// RI012
							
							
					num_count++;

					if ( str_color.equals("#cccccc") )
						str_color = "#999999";
					else 
						str_color = "#cccccc";
    
					String str_contract = openinvoicebean.getColumn("AHINV#");

					String str_sequence = openinvoicebean.getColumn("AHISEQ");

					while( str_sequence.length() < 3 ) {
						str_sequence = "0" + str_sequence;
					}


					String str_duedate = openinvoicebean.getColumn("AHDUED");

					if ( !str_duedate.trim().equals("0") && str_duedate.length() == 8)  {
					
						str_duedate = str_duedate.substring(4, 6) + "/" + str_duedate.substring(6, 8) + "/" + str_duedate.substring(2, 4);

					} else {

						str_duedate = "";

					}
					
					
					String str_status = "OPEN";

					if ( openinvoicebean.getColumn("AHSTTS").equals("OP") ) {

						if ( Double.valueOf(openinvoicebean.getColumn("CurrBalance")).doubleValue() > 0)
							str_status = "PARTIAL";
						else
							str_status = "OPEN";

					}   else {

						str_status = "ERR";

						if ( openinvoicebean.getColumn("AHSTTS").equals("PP") )
							str_status = "PARTIAL";
					}

					String str_recordsource = openinvoicebean.getColumn("AHSRC");

					String str_banner ="";

					if ( str_recordsource.equals("R1") )
						str_banner = "FULL RETURN";
					else if ( str_recordsource.equals("R2") )
						str_banner = "PARTIAL RETURN";
					else if ( str_recordsource.equals("R4") )
						str_banner = "EXCHANGE";
					else if ( str_recordsource.equals("R5") )
						str_banner = "CREDIT MEMO";
					else if ( str_recordsource.equals("R6") )
						str_banner = "CYCLE BILL";
					else if ( str_recordsource.equals("R7") )
						str_banner = "RENTL PURCHS";
					else if ( str_recordsource.equals("S1") )
						str_banner = "EQUIP SALE";
					else if ( str_recordsource.equals("S2") )
						str_banner = "PARTS/MERCHD SALE";
					else 
						str_banner = "";

					// **************************************************************
					// RI012 - Add the detail to the two-dimentional array used later to output to a CSV file
					//         Ensure that null values are changes to blanks
					// **************************************************************
					
					dataList[num_count][0] = str_customer;								// RI012
					dataList[num_count][1] = str_companyname;							// RI012	
					dataList[num_count][2] = str_contract +"-"+ str_sequence ;			// RI012
					dataList[num_count][3] = str_duedate;								// RI012
					dataList[num_count][4] = openinvoicebean.getColumn("AHLOC");		// RI012
					dataList[num_count][5] = str_status;								// RI012
					dataList[num_count][6] = "$" + openinvoicebean.getColumn("AHAMT$");	// RI012
					dataList[num_count][7] = "$" + openinvoicebean.getColumn("AHCBAL");	// RI012
					dataList[num_count][8] = openinvoicebean.getColumn("RHPO#");		// RI012

					for(int j = 0; j < dataList[num_count].length; j++)					// RI012
					{																	// RI012
						if ( dataList[num_count][j] == null )							// RI012
							dataList[num_count][j] = "";								// RI012
					}																	// RI012
						
					// *******************************************************
					// RI012 - show only the maximum records per page
					// *******************************************************
						 
					if( (num_count + num_current) > num_current && (num_count + num_current) <= num_nextstart)		// RI012
					{																								// RI012

			%>

						<tr>
							<form action="contractDetail.jsp" method=POST>
								<input type="hidden" name="contract" value="<!--INVOICE--><%=str_contract%>">
								<input type="hidden" name="sequence" value="<!--SEQUENCE--><%=str_sequence%>">
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top" class="tabledata"><a href="contractDetail.jsp?contract=<%=str_contract%>&sequence=<%=str_sequence%>" onClick="submit();"><!--INVOICE--><%=str_contract%>-<%=str_sequence%></a></td>
							</form>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--INV_DATE--><%=str_duedate%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--LOCATION--><%=openinvoicebean.getColumn("AHLOC")%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--STATUS--><%=str_status%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata"><!--ORIGINAL-->$<%=openinvoicebean.getColumn("AHAMT$")%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata"><!--BALANCE-->$<%=openinvoicebean.getColumn("AHCBAL")%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--PO#--><%=openinvoicebean.getColumn("RHPO#")%>&nbsp;</td>
						</tr>

			<%
					}   // RI012
				}
			}
			
				// ******************************************************************************************************
				// RI012 - place the two-dimentional array in a session object to be retrieved in the download servlet
				// ******************************************************************************************************
				
				if  ( loadDataList )										// RI012
				{															// RI012
					session.setAttribute("dataList", dataList );			// RI012
					session.setAttribute("reportName", webPageName );		// RI012
				}															// RI012
				
			%>


			<tr>
				<td colspan="5" background="images/empty.gif" bgcolor="#ffffff" align="left" valign="top" class="data">
					<% if ( !str_agebucket.equals("") )  {  %>
						<%=str_agetotaldesc%>&nbsp; $<%=str_agetotal%>
					<% }  %>
					&nbsp;
				</td>
				<td colspan="2" background="images/empty.gif" bgcolor="#ffffff" align="right" valign="top" class="data">Current Balance: <!--BALANCE_SUM-->$<%=openinvoicebean.getTotalCurr(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib)%>&nbsp;</td>
			</tr>

		</table>

      <table border="0" width="650" cellspacing="0" cellpadding="0">
          <tr>
             <td width="515" align="left" class="tableheader3"><!--NUM_RECORDS-->&nbsp;

               <%  if ( num_total > 0 ) {    %>
                           <%=num_current+1%> - 
                           <%    if ( num_nextstart < num_total ) {   %>
                                           <%=num_nextstart%> of 
                          <%     }  else {    %>
                                           <%=num_total%> of 
                          <%     }
                      }
                         %>

                     <%=str_total%> record(s).
                 </td>

                 <td align="right" width="90">
                     <%   
                     if ( num_prevstart >= 0 && num_current != 0 ) {   %>
						<form action="openInvoice.jsp" method=POST> 
							<input type="hidden" name="aging" value="<%=org_agebucket%>"> 
							<input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
							<%=hiddenSortFields%>
							<input type=image src="images/prev.gif" height="40" width="87" border="0">
						</form>
                      <%  
                      }   
                      %>
                  </td>

                  <td width="65"> 
                     <%   
                     if ( num_nextstart < num_total ) {    %>
						<form action="openInvoice.jsp" method=POST> 
							<input type="hidden" name="aging" value="<%=org_agebucket%>">  
							<input type="hidden" name="startnumber" value="<%=num_nextstart%>">
							<input type=image src="images/next.gif" height="40" width="62" border=0> 
							<%=hiddenSortFields%>
						</form>
                      <%  
                      }   
                      %>
                   </td>
           </tr>

      </table>

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



<%
  // **********************
  // End current connection
  //**********************

 openinvoicebean.cleanup();		// RI013
 openinvoicebean.endcurrentConnection(openinvoicebeanconnection);

%>

</BODY>
</HTML>