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
//    Index     User Id        Date          Project                       Desciption
//    -------  ------------  -----------  -------------------  ----------------------------------------------------------
//    RI001     DTC2073       04/22/05     SR31413 PCR29        Create new overdue contracts report
//    RI002     DTC2073       05/06/05     SR31413 PCR24        Data download feature
//    RI003     DTC9028       07/28/05     SR31413 PCR19        Implement datasource modification
//    RI004     DTC2073       11/15/05     SR35880              Add comments for re-rent items (catg/class = 975-0001)
//	  RI005     DTC2073       01/11/06     SR35879              Add Job Name to the report.
//    RI006     DTC2073       02/03/06     SR35873              Abbreviated Equipment Release
// ***************************************************************************************************************

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

<%@ page language="java" import="java.sql.*,java.util.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="overduecontractsbean" class="etrieveweb.etrievebean.overduecontractsBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />
<jsp:useBean id="sortOptionBean" class="etrieveweb.etrievebean.sortOptionBean" scope="page" />

<%

// *************************
// Get session variables
// *************************

String str_username = (String)session.getValue("username");
String str_customer = (String) session.getValue("customer");
String str_company = (String) session.getValue("company");
String str_datalib = (String) session.getValue("datalib");
String list_customer = (String) session.getValue("list");
String str_password = (String)session.getValue("password");
String str_as400url = (String)session.getValue("as400url");
String str_as400driver = (String)session.getValue("as400driver");
String loginuserid = (String)session.getValue("loginuserid");


String str_userid = "";
String str_allpickup = "";

if ( loginuserid == null )
	loginuserid = "";

String    equipChangeAuth = (String)    session.getAttribute("equipChangeAuth");	// RI006
ArrayList EqpExtContracts = (ArrayList) session.getAttribute("EqpExtContracts");	// RI006
ArrayList EqpRelContracts = (ArrayList) session.getAttribute("EqpRelContracts");	// RI006
ArrayList EqpRelLineNum   = (ArrayList) session.getAttribute("EqpRelLineNum");		// RI006
ArrayList EqpRelQtyRel    = (ArrayList) session.getAttribute("EqpRelQtyRel");		// RI006

boolean prevReleases = true;		// RI006
boolean prevExtend   = true;		// RI006

if (EqpRelContracts == null || EqpRelLineNum == null || EqpRelQtyRel == null)		// RI006
	prevReleases = false;															// RI006

if (EqpExtContracts == null)	// RI006
	prevExtend   = false;		// RI006

if (equipChangeAuth == null)	// RI006
	equipChangeAuth = "No";		// RI006
	
Connection MenuSecurityBeanconnection = null;
Connection overduecontractsbeanconnection = null;

//*************************************************************
// RI012 - Retrieve today's date to be used for copyright statment.
//*************************************************************
  
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  
  
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

MenuSecurityBeanconnection = MenuSecurityBean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U07"))  {
	response.sendRedirect("menu.jsp");
}

// ***********************
// Get input parameters
// ***********************
 
String str_companyname  = (String) session.getValue("companyname");

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

String webPageName = "overdueContracts";

String[] returnArray = sortOptionBean.getSortOptions(webPageName, sortFld0, sortFld1, sortFld2, order0, order1, order2);

String str_OrderBy = returnArray[0];
String sortOptionsTable = returnArray[1];
String hiddenSortFields = returnArray[2];

// ********************************
// Download variables
// ********************************

String displayPageName = "Overdue Contracts Report";

// **********************
// End current connection
//**********************

MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);

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
				<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;overdue rentals&nbsp;&nbsp;&nbsp;</td>
				<td></td>
			</tr>

		</table>

		<br>
		
		<%

		// *************************************************************************
		// Make a new AS400 connection to retrieve account information and pickup ticket status
		// **************************************************************************

		overduecontractsbeanconnection = overduecontractsbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		String str_color    = "#cccccc";

		String str_description = "";
		String str_contract = "";
		String str_startdate = "";
		String str_dseq = "";
		String str_item = "";
		String str_estRetDate = "";
		String str_jobname = "";	// RI005
		
		String str_total = overduecontractsbean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, jobnumber);
		String str_itemcomments = "";		// RI004

		int num_display = 1000;
		int num_count = 0;
		int num_total = Integer.valueOf(str_total).intValue();
		int num_current = Integer.valueOf(startnumber).intValue();
		int num_nextstart = num_current + num_display;
		int num_prevstart = num_current - num_display;

		if ( num_prevstart <= 0 )
			num_prevstart = 0;

		// ***************************************************
		//  RI002 - Setup the data list array used for the download.  
		//  The first line is reserved for column heading
		// ***************************************************
		
		String dataList [][] = new String[num_total+1][12];		// RI005
		
		dataList[0][0] = "Account#";			// RI002
		dataList[0][1] = "Account Name";		// RI002
		dataList[0][2] = "Contract#";			// RI002
		dataList[0][3] = "Start Date";			// RI002
		dataList[0][4] = "Est Return Date";		// RI002
		dataList[0][5] = "Equipment#";			// RI002
		dataList[0][6] = "Quantity";			// RI002
		dataList[0][7] = "Description";			// RI002
		dataList[0][8] = "On Pickup Tkt";		// RI002
		dataList[0][9] = "Ordered By";			// RI002			
		dataList[0][10] = "Purchase Order";		// RI002
		dataList[0][11] = "Job Name";			// RI005
		
		// ****************************************************************
		// RI002 - Attempt to retrieve session objects for this report
		// ****************************************************************

		String sesDataList[][] = (String [][])session.getAttribute("dataList");		// RI002
		String reportName      = (String)session.getAttribute("reportName");		// RI002
		boolean loadDataList   = false;												// RI002

		if ( num_current == 0 || sesDataList == null || reportName == null || ( reportName != null && !reportName.equals(webPageName) ) )	// RI002
		{ 
			session.removeAttribute("dataList");		// RI002
			session.removeAttribute("reportName");		// RI002
			loadDataList = true;						// RI002
		}												// RI002
		else											// RI002
		{												// RI002
			dataList = sesDataList;						// RI002
		}												// RI002

		// *****************************************************************
		// RI014 - Add the table that will position the download button
		// *****************************************************************	

		%>

		<table border="0" width="900">   
			<tr>
				<td width="723" class="tableheader3" valign="bottom">Items on this report include those that are within 2 days of being overdue.</td>
					<%
					if (num_total == 0 )  { 
					%>
						<td width="140" class="footerblack">&nbsp;</td>		
					<% 
					} else { 
					%> 
						<td width="45" align=right" valign="middle">&nbsp;</td>
							<td width="87" class="footerblack" align="right">
								<Form action="servlet/EtrieveDownLoadServlet"  method=POST> 
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

			<table cellpadding="3" cellspacing="1" border="0" width="900">

				<%	
				// *********************************************
				// Output the sort options table
				// *********************************************
	
				if (num_total > 0)  {  
				%>
					<Form action="overdueContracts.jsp"  name="sortFrm" method=POST  onsubmit=" return validate();"> 
						<tr bgcolor="#999999">
							<td colspan="10"  align="center">
								<input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
								<%=sortOptionsTable%>
							</td>
						</tr>
					</form>
				<%	
				} 
				%>
				
				<tr>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="7%" class="whitemid">Contract</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="7%" class="whitemid">Start<br>Date</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="8%" class="whitemid">Equip #</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="5%" class="whitemid">Qty</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="25%" class="whitemid">Description</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="7%" class="whitemid">Est<br>Return<br>Date</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="6%" class="whitemid">On<br>Pickup<br>Ticket</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="10%" class="whitemid">Ordered<br>By</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="13%" class="whitemid">Purchase Order</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="12%" class="whitemid">Job Name</td>
				</tr>
				

			<%
				// ******************************
				// Process the detail records
				// ******************************

				// *************************************************************
				//  RI014 - Determine no of records to be retrieved and processed.
				// *************************************************************

				int num_start = 0;					// RI002
							
				if  ( !loadDataList )				// RI002
						num_start = num_current;	// RI002
						
						
				if ( overduecontractsbean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_start, jobnumber, str_OrderBy ) ) {   //  RI002

					// ******************************************************************************************
					// Query the database to retrieve all the pickup tickets for the customer.  Create one long string containing the 
					// contract, sequence and equipment number.  Each set will begin and end with two asterisks.  This long string will 
					// later be scanned to see if an equipment number is scheduled for pickup
					// ******************************************************************************************

					if ( overduecontractsbean.getPickup(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib ) ) {
						while ( overduecontractsbean.getNext3() ) { 
							str_allpickup  = str_allpickup.trim() + "**"  +  overduecontractsbean.getColumn3("RUCON#").trim() + "-" +  
						        overduecontractsbean.getColumn3("RUSEQ#").trim() + "-" +  
						        overduecontractsbean.getColumn3("RUEQP#").trim() + "**";
						}
					}

					// ********************************************************************************
					// RI002 - Break out of the loop if the data list has already been loaded
					//         and the page record limit has been reached
					// ********************************************************************************
					
					// RI002 -   while ( overduecontractsbean.getNext() && num_count < num_display) {


					while ( overduecontractsbean.getNext() ) {		// RI002
					
						if ( (num_count >= num_display) && !loadDataList )	// RI002
							break;											// RI002
							
						num_count++;

						if ( str_color.equals("#cccccc") )
							str_color = "#999999";
						else 
							str_color = "#cccccc";
    
						str_contract = overduecontractsbean.getColumn("RDCON#");
						str_description = overduecontractsbean.getColumn("ECDESC").replace('"', ' ');
						str_startdate = overduecontractsbean.getColumn("RHDATO");
						str_dseq = overduecontractsbean.getColumn("RDSEQ#");
						str_item = overduecontractsbean.getColumn("RDITEM");
						str_estRetDate = overduecontractsbean.getColumn("RHERDT");
						int int_catg = Integer.valueOf(overduecontractsbean.getColumn("RDCATG")).intValue();	// RI004
						int int_class = Integer.valueOf(overduecontractsbean.getColumn("RDCLAS")).intValue();	// RI004
						str_jobname = overduecontractsbean.getColumn("CJNAME");	// RI005
						
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

						// **************************************************************
						// RI005 - Check job name
						// **************************************************************
						
						if ( str_jobname == null )		// RI005
							str_jobname = "";			// RI005
							
						// ******************************************************
						//  RI004 - Retrieve the description for rerent items
						// ******************************************************
							
						if ( str_company.equals("HG") && int_catg == 975 && int_class == 1 )	{	// RI004
							str_itemcomments = overduecontractsbean.getItemComments(str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_dseq).intValue());  // RI004
						} else {
							str_itemcomments = "";	// RI004
						}

						// ************************************************************************
						//  RI006 - Check if this equipment qualifies for online release or extend.
						//  This section will determine if a release has already been done for
						//  the equipment within this session
						// ************************************************************************
						
						if( prevReleases && str_pickup.equals("No") && equipChangeAuth.equals("Yes") )	// RI006
						{	
							String str_qty = overduecontractsbean.getColumn("RDQTY").trim();			// RI006
						
							int num_qty = 0;								// RI006
							
							if (str_qty == null) str_qty = "0";				// RI006
							
							int i = str_qty.indexOf(".");					// RI006
									
							if (i > 0)	str_qty = str_qty.substring(0,i);	// RI006
									
							num_qty = Integer.valueOf(str_qty).intValue();	// RI006

							int lstAllRelQty = 0;							// RI006
							int size = EqpRelContracts.size();				// RI006
							
							for	(i=0; i < size; i++)						// RI006
							{
							
								String lstCon     = (String) EqpRelContracts.get(i);				// RI006
								String lstLineNum = (String) EqpRelLineNum.get(i);					// RI006
								String lstQty     = (String) EqpRelQtyRel.get(i);					// RI006
										
								if (lstCon.trim().equals(str_contract) && lstLineNum.trim().equals(str_dseq) )		// RI006
									lstAllRelQty = lstAllRelQty + Integer.valueOf(lstQty).intValue() ;				// RI006
							}	
								
							if(lstAllRelQty >= num_qty )	// RI006
								str_pickup = "Yes";			// RI006			
						}

						// ************************************************************************
						//  RI006 - Check if this contract qualifies for online extension.  This 
						//  section will determine if an earlier extension has been done for the
						//  contract in the same session.       
						// ************************************************************************
						
						boolean allowExtension = true;	// RI006
						
						if( prevExtend )				// RI006
						{
						
							int size = EqpExtContracts.size();		// RI006
		
							for	( int i=0; i<size ; i++ )			// RI006
							{					
								String lstCon = (String)EqpExtContracts.get(i);		// RI006
								
								if (lstCon.trim().equals(str_contract.trim()) )		// RI006
								{
										allowExtension = false;	// RI006
										break;					// RI006				
								}
							}
								
						}
						
						// **************************************************************
						// RI002 - Add the detail to the two-dimentional array used later to output to a CSV file
						//         Ensure that null values are changes to blanks
						// **************************************************************
						
						dataList[num_count][0] = str_customer;								// RI002
						dataList[num_count][1] = str_companyname;							// RI002
						dataList[num_count][2] = str_contract;								// RI002
						dataList[num_count][3] = str_startdate;								// RI002
						dataList[num_count][4] = str_estRetDate;							// RI002
						dataList[num_count][5] = overduecontractsbean.getColumn("RDITEM");	// RI002
						dataList[num_count][6] = overduecontractsbean.getColumn("RDQTY");	// RI002
						dataList[num_count][7] = str_description;							// RI002
						dataList[num_count][8] = str_pickup;								// RI002
						dataList[num_count][9] = overduecontractsbean.getColumn("RHORDB");	// RI002
						dataList[num_count][10] = overduecontractsbean.getColumn("RHPO#");	// RI002
						dataList[num_count][11] = str_jobname;								// RI005

						if ( !str_itemcomments.trim().equals("") )	{						// RI004
							dataList[num_count][7] = dataList[num_count][7].trim() + " *** COMMENT: " + str_itemcomments.trim();	// RI004
							str_itemcomments = "<BR>" + str_itemcomments.trim();			// RI004
						}
						
						for(int j = 0; j < dataList[num_count].length; j++)					// RI002
						{																	// RI002
							if ( dataList[num_count][j] == null )							// RI002
								dataList[num_count][j] = "";								// RI002
						}																	// RI002
						
						// *******************************************************
						// RI002 - show only the maximum records per page
						// *******************************************************
						 
						if( (num_count + num_current) > num_current && (num_count + num_current) <= num_nextstart)		// RI002
						{																								// RI002

						%>

							<tr>
								<form action="contractDetail.jsp" method=POST>
									<input type="hidden" name="contract" value=<%=str_contract%>>
									<input type="hidden" name="sequence" value="000">
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata"><a href="contractDetail.jsp?contract=<%=str_contract%>&sequence=000" onClick="submit();"><!--CONTRACT--><%=str_contract%></a></td>
								</form>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--START_DATE--><%=str_startdate%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--ITEM--><%=overduecontractsbean.getColumn("RDITEM")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--QUANTITY--><%=overduecontractsbean.getColumn("RDQTY")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--DESC--><%=str_description%><%=str_itemcomments%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledatared"><!--ESTIMATED RETURN DATE--><%=str_estRetDate%>&nbsp;<br><br>
									<% if ( equipChangeAuth.equals("Yes") && allowExtension && (str_pickup.equals("No") )) {  // RI006 %> 
										<a href="extendEquipment.jsp?contract=<%=str_contract%>"><IMG src="images/extend.gif" height="21" width="50" BORDER=0 align="center" valign="bottom"></a> 
									<% } %>
								</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--ON PICKUP TICKET--><%=str_pickup%>&nbsp;<br><br>
									<% if ( equipChangeAuth.equals("Yes") && (str_pickup.equals("No") )) { // RI006 %> 
										<a href="releaseEquipment.jsp?contract=<%=str_contract%>&lineNum=<%=overduecontractsbean.getColumn("RDSEQ#")%>"><IMG src="images/release.gif" height="21" width="50" BORDER=0 align="center" valign="bottom"></a>
									<% } %>
								</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--ORDERED_BY--><%=overduecontractsbean.getColumn("RHORDB")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--PO#--><%=overduecontractsbean.getColumn("RHPO#")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--JOB NAME--><%=str_jobname%>&nbsp;</td>
							</tr>

						<%
						}	// RI002
					}
				}
				
				// ******************************************************************************************************
				// RI002 - place the two-dimentional array in a session object to be retrieved in the download servlet
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
						<form action="overdueContracts.jsp" method=POST> 
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
						<form action="overdueContracts.jsp" method=POST> 
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
	<a href="menu.jsp"><IMG src="images/menu.gif" height="40" width="62" border="0"></a> 
	<a href="javascript:window.close()"><IMG src="images/closeReports.gif" height="40" width="123" border="0"></a> 
	<a href="javascript:window.print()"><img src="images/print.gif" width="67" height="40" border="0"></a>

	<br>

	<hr width="900">

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

  overduecontractsbean.cleanup();	// RI003
  overduecontractsbean.endcurrentConnection(overduecontractsbeanconnection);

%>

</BODY>
</HTML>
