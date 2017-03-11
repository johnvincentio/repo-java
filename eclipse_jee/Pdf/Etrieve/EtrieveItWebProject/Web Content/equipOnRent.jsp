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
<jsp:useBean id="equiponrentbean" class="etrieveweb.etrievebean.equiponrentBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />
<jsp:useBean id="sortOptionBean" class="etrieveweb.etrievebean.sortOptionBean" scope="page" />

<%

// *************************
// Get session variables
// *************************

String str_username    = (String) session.getValue("username");
String str_customer    = (String) session.getValue("customer");
String str_company     = (String) session.getValue("company");
String str_datalib     = (String) session.getValue("datalib");
String list_customer   = (String) session.getValue("list");
String str_password    = (String) session.getValue("password");
String str_as400url    = (String) session.getValue("as400url");
String str_as400driver = (String) session.getValue("as400driver");
String loginuserid     = (String) session.getValue("loginuserid");

String str_userid = "";
String str_allpickup = "";

if ( loginuserid == null )
	loginuserid = "";

String    equipChangeAuth = (String)    session.getAttribute("equipChangeAuth");	// RI018
ArrayList EqpExtContracts = (ArrayList) session.getAttribute("EqpExtContracts");	// RI018
ArrayList EqpRelContracts = (ArrayList) session.getAttribute("EqpRelContracts");	// RI018
ArrayList EqpRelLineNum   = (ArrayList) session.getAttribute("EqpRelLineNum");		// RI018
ArrayList EqpRelQtyRel    = (ArrayList) session.getAttribute("EqpRelQtyRel");		// RI018

boolean prevReleases = true;		// RI018
boolean prevExtend   = true;		// RI018

if (EqpRelContracts == null || EqpRelLineNum == null || EqpRelQtyRel == null)		// RI018
	prevReleases = false;															// RI018

if (EqpExtContracts == null)	// RI018 
	prevExtend   = false;		// RI018

if (equipChangeAuth == null)	// RI018 
	equipChangeAuth = "No";		// RI018

Connection MenuSecurityBeanconnection = null;
Connection equiponrentbeanconnection = null;

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
				<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;equipment on rent by start date&nbsp;&nbsp;&nbsp;</td>
				<td></td>
			</tr>

		</table>

		<br>

		<%

		// *************************************************************************
		// Make a new AS400 connection to retrieve account information and pickup ticket status
		// **************************************************************************

		equiponrentbeanconnection = equiponrentbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		String str_color    = "#cccccc";

		String str_description = "";
		String str_contract = "";
		String str_startdate = "";
		String str_dseq = "";
		String str_item = "";
		String str_estRetDate = "";
		String str_jobname = "";	// RI017
		boolean overdueContract = false;
		
		String str_total = equiponrentbean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, jobnumber);
		String str_itemcomments = "";		// RI016
		
		int num_display = 1000;
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
		
		String dataList [][] = new String[num_total+1][13];		// RI017

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

		<table border="0" width="900">   
			<tr>
				<td width="723" class="tableheader3" valign="bottom">Items that have an Estimated Return Date in red are overdue.</td>
					<%
					if (num_total == 0 )  { 
					%>
						<td width="177" class="footerblack">&nbsp;</td>		
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
					<Form action="equipOnRent.jsp"  name="sortFrm" method=POST  onsubmit=" return validate();"> 
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
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="7%"  class="whitemid">Contract</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="7%"  class="whitemid">Start<br>Date</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="8%"  class="whitemid">Equip #</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="5%"  class="whitemid">Qty</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="25%" class="whitemid">Description</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="7%"  class="whitemid">Est<br>Return<br>Date</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="6%"  class="whitemid">On<br>Pickup<br>Ticket</td>
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

				int num_start = 0;					// RI014
							
				if  ( !loadDataList )				// RI014
						num_start = num_current;	// RI014

						
				if ( equiponrentbean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_start, jobnumber, str_OrderBy ) ) {    // RI014

					// ******************************************************************************************
					// Query the database to retrieve all the pickup tickets for the customer.  Create one long string containing the 
					// contract, sequence and equipment number.  Each set will begin and end with two asterisks.  This long string will 
					// later be scanned to see if an equipment number is scheduled for pickup
					// ******************************************************************************************

					if ( equiponrentbean.getPickup(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib ) ) {
						while ( equiponrentbean.getNext3() ) { 
							str_allpickup  = str_allpickup.trim() + "**"  +  equiponrentbean.getColumn3("RUCON#").trim() + "-" +  
						        equiponrentbean.getColumn3("RUSEQ#").trim() + "-" +  
						        equiponrentbean.getColumn3("RUEQP#").trim() + "**";
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
						str_jobname = equiponrentbean.getColumn("CJNAME");	// RI017
						overdueContract = false;

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
						
						if ( str_company.equals("HG") && int_catg == 975 && int_class == 1 )	{	// RI016
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
						//  This section will determine if a release has already been done for
						//  the equipment within this session
						// ************************************************************************
						
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
								
							if(lstAllRelQty >= num_qty )	// RI018
								str_pickup = "Yes";			// RI018			
						}

						// ************************************************************************
						//  RI018 - Check if this contract qualifies for online extension.  This 
						//  section will determine if an earlier extension has been done for the
						//  contract in the same session.       
						// ************************************************************************
						
						boolean allowExtension = true;	// RI018
						
						if( prevExtend )				// RI018
						{
						
							int size = EqpExtContracts.size();		// RI018
		
							for	( int i=0; i<size ; i++ )			// RI018
							{					
								String lstCon = (String)EqpExtContracts.get(i);		// RI018
								
								if (lstCon.trim().equals(str_contract.trim()) )		// RI018
								{
										allowExtension = false;	// RI018
										break;					// RI018				
								}
							}
								
						}
						
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
						dataList[num_count][9] = equiponrentbean.getColumn("RHORDB");		// RI014
						dataList[num_count][10] = equiponrentbean.getColumn("RHPO#");		// RI014
						dataList[num_count][12] = str_jobname;								// RI017
						
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
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata"><a href="contractDetail.jsp?contract=<%=str_contract%>&sequence=000" onClick="submit();"><!--CONTRACT--><%=str_contract%></a></td>
								</form>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--START_DATE--><%=str_startdate%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--ITEM--><%=equiponrentbean.getColumn("RDITEM")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--QUANTITY--><%=equiponrentbean.getColumn("RDQTY")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--DESC--><%=str_description%><%=str_itemcomments%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata<% if (overdueContract) {%>red<%}%>"><!--ESTIMATED RETURN DATE-->
									<%=str_estRetDate%>&nbsp;<br><br>
									<% if ( equipChangeAuth.equals("Yes") && allowExtension && (str_pickup.equals("No") )) {  // RI018 %> 
										<a href="extendEquipment.jsp?contract=<%=str_contract%>"><IMG src="images/extend.gif" height="21" width="50" BORDER=0 align="center" valign="bottom"></a> 
									<% } %>
								</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--ON PICKUP TICKET-->
									<%=str_pickup%>&nbsp;<br><br>
									<% if ( equipChangeAuth.equals("Yes") && (str_pickup.equals("No") )) { // RI018 %> 
										<a href="releaseEquipment.jsp?contract=<%=str_contract%>&lineNum=<%=equiponrentbean.getColumn("RDSEQ#")%>"><IMG src="images/release.gif" height="21" width="50" BORDER=0 align="center" valign="bottom"></a>
									<% } %>
								</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--ORDERED_BY--><%=equiponrentbean.getColumn("RHORDB")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--PO#--><%=equiponrentbean.getColumn("RHPO#")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--JOB NAME--><%=str_jobname%>&nbsp;</td>
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

  equiponrentbean.cleanup();	// RI015
  equiponrentbean.endcurrentConnection(equiponrentbeanconnection);

%>

</BODY>
</HTML>