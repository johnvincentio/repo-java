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
//    Index     User Id        Date          Project                       Desciption
//    -------  ------------  -----------  -------------------  ----------------------------------------------------------
//    x001      DTC9028       11/26/02     SR26609 PCR1         Modification for the new log-in process
//    x002      DTC9028       11/26/02     SR26609 PCR1         Ensure that connection are ended 
//    x003      DTC9028       12/26/02     SR26609 PCR1         Changed the "log out" to "close reports"
//    x004      DTC9028       12/26/02     SR26609 PCR1         Changed the page title 
//    x005      DTC9028       12/26/02     SR26609 PCR1         Changed the copy right text 
//    x006      DTC9028       12/26/02     SR26609 PCR1         Add the account number
//    x007      DTC9028       02/11/03     SR26609 PCR1         Display RHPO# instead of RHJOBL
//    x008      DTC9028       04/28/03     SR28586 PCR1         Add logic to check security for a customer rep
//    x009      DTC2073       03/04/04     SR28586 PCR19        Add a column to the On-Rent Report to show the Ordered by field.
//    x010      DTC2073       04/09/04     SR31413 PCR11        Add a column to the On-Rent Report to show on pickup ticket status
//    x011      DTC2073       08/19/04     SR31413 PCR5         Add sort options
//    RI012     DTC2073       02/01/05     SR31413 PCR26        Add copyright year dynamically to the page
//    RI013     DTC9028       04/29/05     SR31413 PCR29        Add Estimated Return Date
//    RI014     DTC2073       05/06/05     SR31413 PCR24        Data download feature
//    RI015     DTC9028       07/27/05     SR31413 PCR19        Implement datasource modifications
//    RI016     DTC2073       11/15/05     SR35880              Add comments for re-rent items (catg/class = 975-0001)
//    RI017     DTC2073       01/18/06     SR35879              Add Job Name to the report
//    RI018     DTC2073       02/03/06     SR35873              Abbreviated Equipment Release
//    RI019     DTC2051       07/11/06     SR35421              Added Bill/Accrued cost columns
//    RI020	    DTC2051	      01/09/07     SR39459              Website re-write support.
//    RI021	    DTC9028	      01/08/08     SR39459              Changed max records per page from 10000 to 100.
//    RI022     DTC9028       02/14/08     SR40399              Include comments for all re-rent items
//    VB023     DTC2073       09/05/08     SR44922              E-Trieve Websphere Fix Pack - Remove word servlet from URL
//    RI024     DTC9028       03/12/07     SR35419              Etrieve equipment release
//    RI025     DTC9028       03/12/07     SR37176              Etrieve contract extention
//    VB026     DTP0056       10/17/08     TT-IN6261            Passing parameter to menu
//									       TT-IN9252
// ***************************************************************************************************************

// RI020	String[]    names = session.getValueNames();	 
// RI020	if ( names.length <= 0 || names[0] == "")
// RI020	  response.sendRedirect("securityFailure.jsp");	

Enumeration enum = session.getAttributeNames();		// RI020

if (!enum.hasMoreElements())						// RI020
	response.sendRedirect("securityFailure.jsp");	// RI020
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

<script language="JavaScript" src="images/gerericFunctions.js">
</script> 

</HEAD>

<%@ page language="java" import="java.sql.*,java.util.*, java.text.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="equiponrentbean" class="etrieveweb.etrievebean.equiponrentBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />
<jsp:useBean id="sortOptionBean" class="etrieveweb.etrievebean.sortOptionBean" scope="page" />
<jsp:useBean id="pendingtransbean" class="etrieveweb.etrievebean.pendingtransBean" scope="page" />

<%

// *************************
// Get session variables
// *************************

String str_username    = (String) session.getAttribute("username");		// RI020
String str_customer    = (String) session.getAttribute("customer");		// RI020
String str_company     = (String) session.getAttribute("company");		// RI020
String str_datalib     = (String) session.getAttribute("datalib");		// RI020
String list_customer   = (String) session.getAttribute("list");			// RI020
String str_password    = (String) session.getAttribute("password");		// RI020
String str_as400url    = (String) session.getAttribute("as400url");		// RI020
String str_as400driver = (String) session.getAttribute("as400driver");	// RI020
String loginuserid     = (String) session.getAttribute("loginuserid");	// RI020
String str_GoldCountry = (String) session.getAttribute("CountryCode");	// RI020
String    equipChangeAuth = (String) session.getAttribute("equipChangeAuth");	// RI018
String str_allowReleases  = (String) session.getAttribute("AllowReleases");	// RI024
String str_allowExtend    = (String) session.getAttribute("AllowExtend");	// RI025

String str_connType    = "INQ"; 	//RI020
String str_PUTicket    = "";		//RI024

String str_userid = "";
String str_allpickup = "";

if ( loginuserid == null )
	loginuserid = "";

Connection MenuSecurityBeanconnection = null;
Connection equiponrentbeanconnection = null;
Connection pendingtransbeanconnection = null;	// RI024 RI025

// ********************************************************************************
// RI024 RI025 - Check for current transactions
//  (1) US - check for pending transactions
//  (2) Canada - check for transactions that have been done within the session
// *******************************************************************************

if (equipChangeAuth == null)	// RI024 RI025
	equipChangeAuth = "No";		// RI024 RI025

if (str_allowReleases == null)	// RI024
	str_allowReleases   = "N";	// RI024

if (str_allowExtend == null)	// RI025
	str_allowExtend   = "N";	// RI025
	
// RI024 RI025   ArrayList EqpExtContracts = (ArrayList) session.getAttribute("EqpExtContracts");	// RI018
// RI024 RI025   ArrayList EqpRelContracts = (ArrayList) session.getAttribute("EqpRelContracts");	// RI018
// RI024 RI025   ArrayList EqpRelLineNum   = (ArrayList) session.getAttribute("EqpRelLineNum");		// RI018
// RI024 RI025   ArrayList EqpRelQtyRel    = (ArrayList) session.getAttribute("EqpRelQtyRel");		// RI018

ArrayList EqpExtContracts = new ArrayList();	// RI025
ArrayList EqpRelContracts = new ArrayList();	// RI024
ArrayList EqpRelLineNum = new ArrayList();		// RI024
ArrayList EqpRelQtyRel  = new ArrayList();		// RI024
ArrayList EqpExtConEstDate = new ArrayList();		// Vrush
ArrayList EqpExtConStatus = new ArrayList();		// Vrush
int rel = 0;	// RI024
int ext = 0;	// RI025

if ( equipChangeAuth.trim().equals("Yes") && ( str_allowReleases.equals("Y") || str_allowExtend.equals("Y") ) ) {	// RI024 RI025
	
	if ( str_company.equals("HG") ) {		// RI024 RI025
	
		pendingtransbeanconnection = pendingtransbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password, str_GoldCountry, str_connType);	// RI024 RI025

		if ( pendingtransbean.getPendingTrans(Integer.valueOf(str_customer).intValue(), str_company, str_datalib ) ) {    // RI024 RI025
			
			while ( pendingtransbean.getNext() ) {	// RI024 RI025

				if ( pendingtransbean.getColumn("THTYPE").trim().equals("REL") ) {		// RI024 RI025

					String str_openqty = pendingtransbean.getColumn("TDQTY").trim();	// RI024 RI025

					int i = str_openqty.indexOf(".");				// RI024 RI025
					if (i > 0)										// RI024 RI025
						str_openqty = str_openqty.substring(0,i);	// RI024 RI025
					
					EqpRelContracts.add(rel, pendingtransbean.getColumn("THCON#").trim() );		// RI024 RI025
					EqpRelLineNum.add(rel, pendingtransbean.getColumn("TDSEQ#").trim() );		// RI024 RI025
					EqpRelQtyRel.add(rel,  str_openqty );		// RI024 RI025
					rel++;		// RI024 RI025
				} else if ( pendingtransbean.getColumn("THTYPE").trim().equals("EXT") ) {		// RI024 RI025
					EqpExtContracts.add(ext, pendingtransbean.getColumn("THCON#").trim() );		// RI024 RI025
					EqpExtConEstDate.add(ext, pendingtransbean.getColumn("THERDT").trim() );		// RI025
					EqpExtConStatus.add(ext, pendingtransbean.getColumn("THSTAT").trim() );		// RI025
					ext++;		// RI024 RI025
				}		// RI024 RI025
			}			// RI024 RI025
		}				// RI024 RI025
		
		pendingtransbean.endcurrentConnection(pendingtransbeanconnection);	// RI024 RI025
		
	} else if ( str_company.equals("CR") ) {	// RI024 RI025
	
		EqpExtContracts = (ArrayList) session.getAttribute("EqpExtContracts");	// RI024 RI025
		EqpRelContracts = (ArrayList) session.getAttribute("EqpRelContracts");	// RI024 RI025
		EqpRelLineNum   = (ArrayList) session.getAttribute("EqpRelLineNum");	// RI024 RI025
		EqpRelQtyRel    = (ArrayList) session.getAttribute("EqpRelQtyRel");		// RI024 RI025
		
	}	// RI024 RI025
}

boolean prevReleases = true;		// RI018
boolean prevExtend   = true;		// RI018

if (EqpRelContracts == null || EqpRelLineNum == null || EqpRelQtyRel == null)		// RI018
	prevReleases = false;															// RI018

if (EqpExtContracts == null)	// RI018 
	prevExtend   = false;		// RI018

if (equipChangeAuth == null)	// RI018 
	equipChangeAuth = "No";		// RI018



//*************************************************************
// RI012 - Retrieve today's date to be used for copyright statment.
//*************************************************************
  
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  

// ********************************************
// RI013 - Retrieve the current system date
//*********************************************

Calendar Today = Calendar.getInstance();
int todayDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);
  
// ****************************************************************************
// Determine which user id needs to be checked.  If the user logged in via
// the variable login page, then use the variable user id.  If not, use the 
// standard user id.  Note:  The AS400 connection is still made using the
// standard user id. 
// ****************************************************************************

if  ( !loginuserid.equals("") )  
	str_userid = loginuserid;
else
	str_userid = str_username;

str_userid = str_username;

// ****************************************************************
// Make a new AS400 Connection.  Check if user is authorized for this option.  
//  If user is not allowed - redirect to menu
// ****************************************************************

MenuSecurityBeanconnection = MenuSecurityBean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password, str_GoldCountry, str_connType);		// RI020

if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U07"))  {
	// VB026 response.sendRedirect("menu.jsp");
	response.sendRedirect("menu.jsp?showReports=1");   // VB026
}

// ***********************
// Get input parameters
// ***********************
 
String str_companyname  = (String) session.getAttribute("companyname");		// RI020

String startnumber = request.getParameter("startnumber");
String jobnumber = request.getParameter("jobnumber");

String sortFld0 = (String) request.getParameter("sortFld0");
String sortFld1 = (String) request.getParameter("sortFld1");
String sortFld2 = (String) request.getParameter("sortFld2");

String order0 = (String) request.getParameter("order0");
String order1 = (String) request.getParameter("order1");
String order2 = (String) request.getParameter("order2");
 
if ( startnumber == null )	startnumber = "0";
if ( jobnumber == null )	jobnumber = "";

// ***********************************
// Format the sort options table
// ***********************************

String webPageName = "equipOnRent";

String[] returnArray = sortOptionBean.getSortOptions(webPageName, sortFld0, sortFld1, sortFld2, order0, order1, order2);

String str_OrderBy = returnArray[0];
String sortOptionsTable = returnArray[1];
String hiddenSortFields = returnArray[2];

// ********************************
// Download variables
// ********************************

String displayPageName = "Equipment On Rent by Start Date Report";	// RI014

// **********************
// End current connection
//**********************

MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);

%>


<BODY BGCOLOR="#ffffff">

	<div ID=maintext>

		<table border="0" width="940" cellspacing="0" cellpadding="0">
			<tr>
				<td width="300" align="right"><img src="images/goldReports_header.gif" width="300" height="100"></td>
				<td width="390" align="center">&nbsp;</td>
				<td width="250" align="left"><img src="images/HERClogo.gif" width="250" height="100"></td>
			</tr>
		</table>


		<table border="0" width="940" cellspacing="0" cellpadding="0">

			<tr>
				<td background="images/bottom_back.gif" width="25">&nbsp;</td>
				<td background="images/bottom_back.gif" width="885"><!--COMPANY_NAME--><a class="company"><%=str_companyname.trim()%></a>&nbsp;&nbsp;-&nbsp;&nbsp;Account Number: <!--COMPANY_NUMBER--><a class="data"><%=str_customer%></a></td>
				<td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
			</tr>

			<tr>
				<td><img src="images/empty.gif" height="10"></td>
				<td><img src="images/arrow.gif" height="10" align="right"></td>
				<td></td>
			</tr>

			<tr>
				<td><img src="images/empty.gif" height="30"></td>
				<!--VB026-->  <!--  <td align="right" class="history"><a href='menu.jsp'>report menu</a>&nbsp;&nbsp;&nbsp;equipment on  rent by start date&nbsp;&nbsp;&nbsp;</td>  -->

	            <td align="right" class="history"><a href='menu.jsp?showReports=1'>report menu</a>&nbsp;&nbsp;&nbsp;equipment on  rent by start date&nbsp;&nbsp;&nbsp;</td>    <!--VB026-->
				<td></td>
			</tr>

		</table>

		<br>

		<%

		// *************************************************************************
		// Make a new AS400 connection to retrieve account information and pickup ticket status
		// **************************************************************************

		equiponrentbeanconnection = equiponrentbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password, str_GoldCountry, str_connType);	// RI020

		String str_color    = "#cccccc";

		String str_description = "";
		String str_contract = "";
		String str_startdate = "";
		String str_dseq = "";
		String str_item = "";
		String str_estRetDate = "";
		String str_jobname = "";		// RI017
		String str_dayrate = "";		// RI019
		String str_weekrate = "";		// RI019
		String str_monthrate = "";		// RI019
		String str_totalbilled = "";	// RI019
		String str_totalaccrued = "";	// RI019
		String str_totalestcost = "";	// RI019
		double num_totalbilled  = 0.0;	// RI019
		double num_totalaccrued = 0.0;	// RI019
		double num_totalestcost = 0.0;	// RI019
		double temp_double = 0.00;		// RI019
		
		int decpos2 = 0;				// RI019
		
		DecimalFormat df = new DecimalFormat("###,###,###,###.##");		// RI019
		
		boolean overdueContract = false;
		
		String str_total = equiponrentbean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, jobnumber);
		String str_itemcomments = "";		// RI016
		
		int num_display = 100;		// RI021
		int num_count = 0;
		int num_total = Integer.valueOf(str_total).intValue();
		int num_current = Integer.valueOf(startnumber).intValue();
		int num_nextstart = num_current + num_display;
		int num_prevstart = num_current - num_display;

		if ( num_prevstart <= 0 )
			num_prevstart = 0;

		// ***************************************************
		//  RI014 - Setup the data list array used for the download.  
		//  The first line is reserved for column heading
		// ***************************************************
		
		String dataList [][] = new String[num_total+1][19];		// RI019

		dataList[0][0] = "Account#";			// RI014
		dataList[0][1] = "Account Name";		// RI014
		dataList[0][2] = "Contract#";			// RI014
		dataList[0][3] = "Start Date";			// RI014
		dataList[0][4] = "Est Return Date";		// RI014
		dataList[0][5] = "Equipment#";			// RI014
		dataList[0][6] = "Quantity";			// RI014
		dataList[0][7] = "Description";			// RI014
		dataList[0][8] = "On Pickup Tkt";		// RI014
		dataList[0][9] = "Ordered By";			// RI014			
		dataList[0][10] = "Purchase Order";		// RI014
		dataList[0][11] = "Overdue";			// RI014
		dataList[0][12] = "Job Name";			// RI017
		dataList[0][13] = "Daily rate";			// RI019
		dataList[0][14] = "Weekly rate";		// RI019
		dataList[0][15] = "4 Week rate";		// RI019
		dataList[0][16] = "Total Billed";		// RI019
		dataList[0][17] = "Est. Cost to Date";	// RI019
		dataList[0][18] = "Total Est. Cost";	// RI019
						
		if ( !str_company.equals("HG") ) {		// RI019
			dataList[0][16] = "";				// RI019
			dataList[0][17] = "";				// RI019
			dataList[0][18] = "";				// RI019
		}		// RI019
		
		// ****************************************************************
		// RI014 - Attempt to retrieve session objects for this report
		// ****************************************************************

		String sesDataList[][] = (String [][])session.getAttribute("dataList");		// RI014
		String reportName      = (String)session.getAttribute("reportName");		// RI014
		boolean loadDataList   = false;												// RI014

		if ( num_current == 0 || sesDataList == null || reportName == null || ( reportName != null && !reportName.equals(webPageName) ) )	// RI014
		{ 
			session.removeAttribute("dataList");		// RI014
			session.removeAttribute("reportName");		// RI014
			loadDataList = true;						// RI014
		}												// RI014
		else											// RI014
		{												// RI014
			dataList = sesDataList;						// RI014
		}												// RI014

		// *****************************************************************
		// RI014 - Add the table that will position the download button
		// *****************************************************************	

		%>

		<table border="0" width="940">   
			<tr>
				<td width="763" class="tableheader3" valign="bottom">Items that have an Estimated Return Date in red are overdue.</td>
					<%
					if (num_total == 0 )  { 
					%>
						<td width="177" class="footerblack">&nbsp;</td>		
					<% 
					} else { 
					%> 
						<td width="45" align=right" valign="middle">&nbsp;</td>
							<td width="87" class="footerblack" align="right">
								<Form action="EtrieveDownLoadServlet"  method=POST> 
								<!-- VB023 -Removed word 'servlet' from URL -->
									<input type=image SRC="images/download4.JPG" height="22" width="86" BORDER=0 align="right" valign="bottom">
									<Input TYPE="hidden" name="filename" value = "<%=displayPageName%>" >
								</form>
							</td>
							<td width="45" class="footerblack" align="left">
								<class="history"><a href='downloaddetails.jsp'>[details]</a>
							</td>
					<% 
					} 
					%>
			</tr>
		</table>
		
		
		
		<center>

			<table cellpadding="3" cellspacing="1" border="0" width="940">

				<%	
				// *********************************************
				// Output the sort options table
				// *********************************************
	
				if (num_total > 0)  {  
				%>
					<Form action="equipOnRent.jsp"  name="sortFrm" method=POST  onsubmit=" return validate();"> 
						<tr bgcolor="#999999">
							<td colspan="13"  align="center">	
								<input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
								<%=sortOptionsTable%>
							</td>
						</tr>
					</form>
				<%	
				} 
				%>
				
				<tr>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="7%"  class="whitemid">Contract</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="5%"  class="whitemid">Start<br>Date</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="7%"  class="whitemid">Equip #</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="4%"  class="whitemid">Qty</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="17%" class="whitemid">Description</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="6%"  class="whitemid">Est.<br>Return<br>Date</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="6%"  class="whitemid">On<br>Pickup<br>Ticket</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="8%"  class="whitemid">Ordered<br>By</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="11%" class="whitemid">Purchase Order</td>
					<% if ( !str_company.equals("HG") ) { 	// RI019 %>
						<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="29%" class="whitemid">Job Name</td>
					<% } else {		// RI019%>
						<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="10%" class="whitemid">Job Name</td>
						<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="7%"  class="whitemid">Total<br>Rental Charges<br>Billed<br><a href="javascript:open_win('TB')"><IMG src="images/moreinfo.GIF" height="15" width="51" border="0"></a></td>
						<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="6%"  class="whitemid">Est.<br>Rental<br>Charges<br>To Date<br><a href="javascript:open_win('EC')"><IMG src="images/moreinfo.GIF" height="15" width="51" border="0"></a></td>
						<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="6%"  class="whitemid">Total<br>Est.<br>Rental Charges<br><a href="javascript:open_win('TC')"><IMG src="images/moreinfo.GIF" height="15" width="51" border="0"></a></td>
					<% } // RI019%>
				</tr>
				
			<%
				// ******************************
				// Process the detail records
				// ******************************

				// *************************************************************
				//  RI014 - Determine no of records to be retrieved and processed.
				// *************************************************************

				int num_start = 0;					// RI014
							
				if  ( !loadDataList )				// RI014
						num_start = num_current;	// RI014

						
				if ( equiponrentbean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_start, jobnumber, str_OrderBy ) ) {    // RI014

					// ******************************************************************************************
					// Query the database to retrieve all the pickup tickets for the customer.  Create one long string containing the 
					// contract, sequence and equipment number.  Each set will begin and end with two asterisks.  This long string will 
					// later be scanned to see if an equipment number is scheduled for pickup
					// ******************************************************************************************

					String [] RAreleases = new String[num_total+1];		// RI024
					String [] RAticket   = new String[num_total+1];		// RI024
					int release_num = 0;		// RI024
					
					if ( equiponrentbean.getPickup(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib ) ) {
					
						while ( equiponrentbean.getNext3() ) { 
						
							str_allpickup  = 
								str_allpickup.trim() 
								+ "**"  
								+  equiponrentbean.getColumn3("RUCON#").trim() 
								+ "-" 
								+  equiponrentbean.getColumn3("RUSEQ#").trim() 
						        + "-" 
						        + equiponrentbean.getColumn3("RUEQP#").trim() 
						        + "**";
						        
						        RAreleases[release_num] = "**"  				// RI024
								+  equiponrentbean.getColumn3("RUCON#").trim() 
								+ "-" 
								+ equiponrentbean.getColumn3("RUSEQ#").trim() 
								+ "-" 
								+ equiponrentbean.getColumn3("RUEQP#").trim() 
								+ "**";
								
								RAticket[release_num] = equiponrentbean.getColumn3("RUPKU#").trim();	// RI024
							
								release_num++;	// RI024
						}
					}


					// ********************************************************************************
					// RI014 - Break out of the loop if the data list has already been loaded
					//         and the page record limit has been reached
					// ********************************************************************************

					//*** RI014 -     while ( equiponrentbean.getNext() && num_count < num_display) {

					while ( equiponrentbean.getNext() ) {		// RI014 
					
						if ( (num_count >= num_display) && !loadDataList )	// RI014
							break;											// RI014
							
						num_count++;

						if ( str_color.equals("#cccccc") )
							str_color = "#999999";
						else 
							str_color = "#cccccc";
    
						str_contract = equiponrentbean.getColumn("RDCON#").trim();
						str_description = equiponrentbean.getColumn("ECDESC").replace('"', ' ');
						str_startdate = equiponrentbean.getColumn("RHDATO");
						str_dseq = equiponrentbean.getColumn("RDSEQ#").trim();
						str_item = equiponrentbean.getColumn("RDITEM");
						str_estRetDate = equiponrentbean.getColumn("RHERDT");
						int int_catg = Integer.valueOf(equiponrentbean.getColumn("RDCATG")).intValue();		// RI016
						int int_class = Integer.valueOf(equiponrentbean.getColumn("RDCLAS")).intValue();	// RI016
						str_jobname = equiponrentbean.getColumn("CJNAME");									// RI017
						str_dayrate = equiponrentbean.getColumn("RDDYRT");				// RI019
						str_weekrate = equiponrentbean.getColumn("RDWKRT");				// RI019
						str_monthrate = equiponrentbean.getColumn("RDMORT");			// RI019
						str_totalbilled = equiponrentbean.getColumn("RNBILL");			// RI019
						str_totalaccrued = equiponrentbean.getColumn("RNACCR");			// RI019
						str_totalestcost = equiponrentbean.getColumn("RNRCST");			// RI019
						overdueContract = false;

						// ***********************
						// Cost amount
						// ***********************
						
						if ( str_totalbilled == null )		// RI019
							str_totalbilled = "0.00";		// RI019
							
						if ( str_totalaccrued == null )		// RI019
							str_totalaccrued = "0.00";		// RI019

						if ( str_totalestcost == null )		// RI019
							str_totalestcost = "0.00";		// RI019

						// **************************************************
						// RI019 Remove the decimal portion of the rates  ***
						// **************************************************

						temp_double = Double.parseDouble( str_dayrate );	// RI019
						int num_dayrate = (int) temp_double;				// RI019

						temp_double = Double.parseDouble( str_weekrate );	// RI019
						int num_weekrate = (int) temp_double;				// RI019

						temp_double = Double.parseDouble( str_monthrate );	// RI019
						int num_monthrate = (int) temp_double;				// RI019
											
						// ***********************
						// Start Date
						// ***********************
						
						if ( !str_startdate.trim().equals("0") && str_startdate.length() == 8) {
						
							str_startdate = str_startdate.substring(4, 6) + "/" + str_startdate.substring(6, 8) + "/" + str_startdate.substring(2, 4);

						} else {

							str_startdate = "";

						}
						
						// ***********************
						// Estimated Return Date
						// ***********************
				
						if ( !str_estRetDate.trim().equals("0") && str_estRetDate.length() == 8) {
				
							int estimatedReturn = Integer.parseInt(str_estRetDate);

							if ( estimatedReturn < todayDate )
								overdueContract = true;

							str_estRetDate = str_estRetDate.substring(4, 6) + "/" + str_estRetDate.substring(6, 8) + "/" + str_estRetDate.substring(2, 4);
							
						} else {
							str_estRetDate = "";
						}
					
						// ****************************************************************************
						// Get Pickup ticket status.  Create the scan string and then scan the long string that was created
						// earlier to see if the equipment is scheduled for pickup
						// ****************************************************************************

						String str_checkpickup  = "**"  +  str_contract.trim() + "-" +  str_dseq.trim() + "-" + str_item.trim() + "**";
	
		  				String str_pickup = "";

						int decpos = str_allpickup.indexOf( str_checkpickup ); 

						if (decpos == -1 )
							str_pickup = "No";
						else
							str_pickup = "Yes";

						
						// ***************************************************
						// RI024 - If on-pickup, then get the pickup ticket
						// ***************************************************
						
						str_PUTicket = "";		// RI024
						
						if ( str_pickup.equals("Yes") ) {	// RI024
						
								for	(int r=0; r < num_total+1; r++)		// RI024
								{
									if ( str_checkpickup.equals(RAreleases[r]) && str_PUTicket.trim().equals("") ) 	// RI024
										str_PUTicket = RAticket[r];	// RI024
								}	// RI024
						}	// RI024
						
						// ***************************************************************
						//  Format the description is longer than 25 bytes and has a slash in the text
						// ***************************************************************

						int descpos = str_description.indexOf(" "); 

						if (descpos == -1   ||   descpos > 1  )  {

							String str_newECDESC = "";
						
							for (int val4  = 0; val4 < str_description.length(); ++val4)   {

								if  ( str_description.substring(val4, val4+1).equals("/") )
									str_newECDESC = str_newECDESC.trim() + " " +  str_description.substring(val4, val4+1) + " ";
								else
									str_newECDESC = str_newECDESC + str_description.substring(val4, val4+1);
							}

							str_description = str_newECDESC;
						}
						
						// ******************************************************
						//  RI016 - Retrieve the description for rerent items
						// ******************************************************
												 
						if ( str_company.equals("HG") && ( (int_catg == 975 && int_class == 1) || str_item.substring(0, 2).equals("RR") ) )	{	// RI016  RI022
							str_itemcomments = equiponrentbean.getItemComments(str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_dseq).intValue());  // RI016
						} else {
							str_itemcomments = "";	// RI016
						}

						// **************************************************************
						// RI017 - Check job name
						// **************************************************************
						
						if ( str_jobname == null )		// RI017
							str_jobname = "";			// RI017


						// ************************************************************************
						//  RI018 - Check if this equipment qualifies for online release or extend.
						//  (1) USA - Determine if releases are pending
						//  (2) Canada - Determine if a release has already been done for equip in this session  
						// ************************************************************************
						
						boolean allowReleases = true;
						
						if( prevReleases && str_pickup.equals("No") && equipChangeAuth.equals("Yes") )	// RI018
						{	
							String str_qty    = equiponrentbean.getColumn("RDQTY").trim();				// RI018
												
							int num_qty = 0;								// RI018
							
							if (str_qty == null) str_qty = "0";				// RI018
							
							int i = str_qty.indexOf(".");					// RI018
									
							if (i > 0)	str_qty = str_qty.substring(0,i);	// RI018
									
							num_qty = Integer.valueOf(str_qty).intValue();	// RI018

							int lstAllRelQty = 0;							// RI018
							int size = EqpRelContracts.size();				// RI018
							
							for	(i=0; i < size; i++)						// RI018
							{
								String lstCon     = (String) EqpRelContracts.get(i);				// RI018
								String lstLineNum = (String) EqpRelLineNum.get(i);					// RI018
								String lstQty     = (String) EqpRelQtyRel.get(i);					// RI018
										
								if (lstCon.trim().equals(str_contract) && lstLineNum.trim().equals(str_dseq) )		// RI018
									lstAllRelQty = lstAllRelQty + Integer.valueOf(lstQty).intValue() ;	// RI018
							}	
								
							if(lstAllRelQty >= num_qty ) {	// RI018 RI024
								str_pickup = "Yes";			// RI018			
								if ( str_company.equals("HG") )		// RI024
									str_pickup = "Pending";			// RI024
							} // RI024
						}

						if ( str_allowReleases.equals("N") || str_pickup.equals("Yes") )	// RI024
							allowReleases = false;			// RI024
							
						// ************************************************************************
						//  RI018 - Check if this contract qualifies for online extension.  This 
						//  section will determine if an earlier extension has been done for the
						//  contract in the same session.       
						// ************************************************************************
						
						boolean allowExtension = true;	// RI018
						int svInd = -1 ; //RI025
						
						if( prevExtend )				// RI018
						{
						
							int size = EqpExtContracts.size();		// RI018
		
							for	( int i=0; i<size ; i++ )			// RI018
							{					
								String lstCon = (String)EqpExtContracts.get(i);		// RI018
								
								if (lstCon.trim().equals(str_contract.trim()) )		// RI018
								{
										svInd = i ;				// RI025
										allowExtension = false;	// RI018
										break;					// RI018				
								}
							}
								
						}

						if ( str_allowExtend.equals("N") )	// RI025
							allowExtension = false;			// RI025
							
						//*****************************************************************
						// RI025 Start
						// If a contract has a Pending Extension Request, display new
						// estimated return date.
						//*****************************************************************

						String str_pendingExt = "";	// bob1
						
						if (svInd != -1)
						{
						String conStatus = (String)EqpExtConStatus.get(svInd);

							if (conStatus.equals("P") || conStatus.equals("p"))
							{	
								String Save_estRetDate = str_estRetDate;
								
								str_estRetDate = (String)EqpExtConEstDate.get(svInd);

								allowReleases = false;
								allowExtension = false;
								
								if ( !str_estRetDate.trim().equals("0") && str_estRetDate.length() == 8) 
								{
									int estimatedReturn = Integer.parseInt(str_estRetDate);

									if ( estimatedReturn < todayDate )
										overdueContract = true;

									str_estRetDate = str_estRetDate.substring(4, 6) + "/" + str_estRetDate.substring(6, 8) + "/" + str_estRetDate.substring(2, 4);
								} 
								
								str_pendingExt = "Pending";		// bob1
								str_estRetDate = Save_estRetDate;
							}
						}
						// RI025 End
						
						// **************************************************************
						// RI014 - Add the detail to the two-dimentional array used later to output to a CSV file
						//         Ensure that null values are changes to blanks
						// **************************************************************
						
						dataList[num_count][0] = str_customer;								// RI014
						dataList[num_count][1] = str_companyname;							// RI014
						dataList[num_count][2] = str_contract;								// RI014
						dataList[num_count][3] = str_startdate;								// RI014
						dataList[num_count][4] = str_estRetDate;							// RI014
						dataList[num_count][5] = equiponrentbean.getColumn("RDITEM");		// RI014
						dataList[num_count][6] = equiponrentbean.getColumn("RDQTY");		// RI014
						dataList[num_count][7] = str_description;							// RI014
						dataList[num_count][8] = str_pickup;								// RI014
						if ( str_pickup.equals("Yes") )										// RI024
						dataList[num_count][8] = "Tkt: " + str_PUTicket;			        // RI024
						dataList[num_count][9] = equiponrentbean.getColumn("RHORDB");		// RI014
						dataList[num_count][10] = equiponrentbean.getColumn("RHPO#");		// RI014
						dataList[num_count][12] = str_jobname;								// RI017
						dataList[num_count][13] = str_dayrate;								// RI019
						dataList[num_count][14] = str_weekrate;								// RI019
						dataList[num_count][15] = str_monthrate;							// RI019
						dataList[num_count][16] = str_totalbilled;							// RI019
						dataList[num_count][17] = str_totalaccrued;							// RI019
						dataList[num_count][18] = str_totalestcost;							// RI019
						
						if ( !str_company.equals("HG") ) { 		// RI019
							dataList[num_count][16] = "";		// RI019
							dataList[num_count][17] = "";		// RI019
							dataList[num_count][18] = "";		// RI019
						}										// RI019
						
						if ( overdueContract )
							dataList[num_count][11] = "Yes";								// RI014
						else
							dataList[num_count][11] = "No";									// RI014

						if ( !str_itemcomments.trim().equals("") )	{						// RI016
							dataList[num_count][7] = dataList[num_count][7].trim() + " *** COMMENT: " + str_itemcomments.trim();	// RI016
							str_itemcomments = "<BR>" + str_itemcomments.trim();			// RI016
						}
						
						for(int j = 0; j < dataList[num_count].length; j++)					// RI014
						{																	// RI014
							if ( dataList[num_count][j] == null )							// RI014
								dataList[num_count][j] = "";								// RI014
						}																	// RI014
						
						// ******************************************************
						// RI019 - format the total billed amount for output
						// ******************************************************
						
						num_totalbilled = Double.valueOf(str_totalbilled).doubleValue();	// RI019
						
						str_totalbilled    = df.format(num_totalbilled);					// RI019
						
						decpos2 = str_totalbilled.indexOf(".");		// RI019

						if (decpos2 == -1 )								// RI019
							str_totalbilled = str_totalbilled + ".00";	// RI019

						if ( ( ( str_totalbilled.length()-1)  - decpos2 ==  1 )  )	// RI019
							str_totalbilled = str_totalbilled + "0";				// RI019

						if ( !str_company.trim().equals("HG") )		// RI019
							str_totalbilled = ".00";				// RI019
							
						// ******************************************************
						// RI019 - format the total accrued amount for output
						// ******************************************************
					
						num_totalaccrued = Double.valueOf(str_totalaccrued).doubleValue();	// RI019
						
						str_totalaccrued   = df.format(num_totalaccrued);					// RI019
						
						decpos2 = str_totalaccrued.indexOf(".");		// RI019 

						if (decpos2 == -1 )									// RI019
							str_totalaccrued = str_totalaccrued + ".00";	// RI019

						if ( ( ( str_totalaccrued.length()-1)  - decpos2 ==  1 )  )	// RI019
							str_totalaccrued = str_totalaccrued + "0";				// RI019

						if ( !str_company.trim().equals("HG") )		// RI019
							str_totalaccrued = ".00";				// RI019
							
						// **************************************************************
						// RI019 - format the total estimated rental amount for output
						// *************************************************************
						
						num_totalestcost = Double.valueOf(str_totalestcost).doubleValue();	// RI019
						
						str_totalestcost   = df.format(num_totalestcost);		// RI019
							
						decpos2 = str_totalestcost.indexOf(".");		// RI019 

						if (decpos2 == -1 )									// RI019
							str_totalestcost = str_totalestcost + ".00";	// RI019

						if ( ( ( str_totalestcost.length()-1)  - decpos2 ==  1 )  )	// RI019
							str_totalestcost = str_totalestcost + "0";				// RI019

						if ( !str_company.trim().equals("HG") )		// RI019
							str_totalestcost = ".00";				// RI019
							
						// *******************************************************
						// RI014 - show only the maximum records per page
						// *******************************************************
						 
						if( (num_count + num_current) > num_current && (num_count + num_current) <= num_nextstart)		// RI014
						{																								// RI014
								
						%>

							<tr>
								<form action="contractDetail.jsp" method=POST>
									<input type="hidden" name="contract" value=<%=str_contract%>>
									<input type="hidden" name="sequence" value="000">
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata"><a href="contractDetail.jsp?contract=<%=str_contract%>&sequence=000" onClick="submit();"><%=str_contract%></a></td>
								</form>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--START_DATE--><%=str_startdate%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--ITEM--><%=equiponrentbean.getColumn("RDITEM")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--QUANTITY--><%=equiponrentbean.getColumn("RDQTY")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata">
									<%=str_description%><%=str_itemcomments%>&nbsp;
									<% if ( ( num_dayrate != 0   ||   num_weekrate != 0   ||   num_monthrate != 0) )  {   // RI019 %>
										<br><br>Rates: Day&nbsp;/&nbsp;Week&nbsp;/&nbsp;4 Week:&nbsp;<br>$<%=num_dayrate%>&nbsp;/&nbsp;$<%=num_weekrate%>&nbsp;/&nbsp;$<%=num_monthrate%>
									<%  }  %>
								</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata<% if (overdueContract) {%>red<%}%>"><!--ESTIMATED RETURN DATE-->
									<%=str_estRetDate%>&nbsp;<br><br><%=str_pendingExt.trim()%>
									<% if ( equipChangeAuth.equals("Yes") && allowExtension && (str_pickup.equals("No")) ) {  // RI018 %> 
										<a href="extendEquipment.jsp?contract=<%=str_contract%>&orgpage=<%=webPageName%>"><IMG src="images/extend.gif" height="21" width="50" BORDER=0 align="center" valign="bottom"></a> 
									<% } %>
								</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--ON PICKUP TICKET-->
									<%=str_pickup%>&nbsp;<br><br>
									<% if ( !str_PUTicket.trim().equals("") ) { // RI024 %>
										Ticket#: <a href="javascript:pickupticketinfo('<%=str_PUTicket%>', '', '')"><%=str_PUTicket%></a>
									<% } // RI024 %>		
									<% if ( str_pickup.trim().equals("Pending") ) { // RI024 %>
										<a href="javascript:pickupticketinfo('', '<%=str_contract%>', '<%=str_dseq %>')">Detail</a>
									<% } // RI024 %>												
									<% if ( equipChangeAuth.equals("Yes") && str_pickup.equals("No") && allowReleases && str_allowReleases.equals("Y") ) { // RI024 %>  
										<a href="releaseEquipment.jsp?contract=<%=str_contract%>&lineNum=<%=equiponrentbean.getColumn("RDSEQ#")%>&orgpage=<%=webPageName%>"><IMG src="images/release.gif" height="21" width="50" BORDER=0 align="center" valign="bottom"></a>
									<% } %>
								</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--ORDERED_BY--><%=equiponrentbean.getColumn("RHORDB")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--PO#--><%=equiponrentbean.getColumn("RHPO#")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--JOB NAME--><%=str_jobname%>&nbsp;</td>
								<% if ( str_company.equals("HG") ) { 	// RI019 %>
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata"><!--Total billed--><%=str_totalbilled%>&nbsp;</td>
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata"><!--Est.Cost to date--><%=str_totalaccrued%>&nbsp;</td>
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata"><!--Total Est cost--><%=str_totalestcost%>&nbsp;</td>
								<% } %>
							</tr>

						<%
						}		// RI014
					}
				}
				
				// ******************************************************************************************************
				// RI014 - place the two-dimentional array in a session object to be retrieved in the download servlet
				// ******************************************************************************************************
				
				if  ( loadDataList )										// RI014
				{															// RI014
					session.setAttribute("dataList", dataList );			// RI014
					session.setAttribute("reportName", webPageName );		// RI014
				}															// RI014

     		%>

		</table>

		<table cellpadding="3" cellspacing="0" border="0" width="900">

			<tr>
				<td bgcolor="#ffffff" align="left" width="745" class="tableheader3"><!--NUM_RECORDS-->&nbsp;
					<%  if ( num_total > 0 ) {   %>
						<%=num_current+1%> - 
                        <%  if ( num_nextstart < num_total ) {    %>
								<%=num_nextstart%> of 
                        <%  }else {  %>
                                <%=num_total%> of 
                        <%   }
                       }
                        %>

               		<%=str_total%> record(s).</td>
				</td>
				<td align="right" width="90">
               		<%   
               		if ( num_prevstart >= 0 && num_current != 0 ) {    
               		%>
						<form action="equipOnRent.jsp" method=POST> 
							<input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
							<input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
							<%=hiddenSortFields%>
							<input type=image src="images/prev.gif" height="40" width="87" border="0">
						</form>
					<%
					}   
					%>
				</td>

				<td width="65">
                	<%
                	if ( num_nextstart < num_total ) {
                	%>
						<form action="equipOnRent.jsp" method=POST> 
							<input type="hidden" name="startnumber" value="<%=num_nextstart%>">
							<input type="hidden" name="jobnumber" value= "<%=jobnumber%>"> 
							<input type=image src="images/next.gif" height="40" width="62" border=0> 
							<%=hiddenSortFields%>
						</form>
                  	<%   
                  	}    
                  	%>
				</td>

			</tr>

		</table>

	</center>

	<a href="javascript:history.go(-1)"><IMG src="images/back.gif" height="40" width="62" border="0"></a> 
	<!--VB026-->   <!-- <a href="menu.jsp"><IMG src="images/menu.gif" height="40" width="62" border="0"></a> -->
    <a href="menu.jsp?showReports=1"><IMG src="images/menu.gif" height="40" width="62" border="0"></a>  <!--VB026--> 
	<a href="javascript:window.close()"><IMG src="images/closeReports.gif" height="40" width="123" border="0"></a> 
	<a href="javascript:window.print()"><img src="images/print.gif" width="67" height="40" border="0"></a>

	<br>

	<hr width="940">

	<table border="0" width="900" cellspacing="0" cellpadding="0">
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

  equiponrentbean.cleanup();	// RI015
  equiponrentbean.endcurrentConnection(equiponrentbeanconnection);

%>

</BODY>
</HTML>