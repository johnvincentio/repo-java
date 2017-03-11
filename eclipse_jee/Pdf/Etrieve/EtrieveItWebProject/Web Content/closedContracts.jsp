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
//   Index     User Id        Date       Project            Desciption
//  --------  -----------  -----------  ----------------   ----------------------------------------
//   RI001     DTC2073      07/15/05     SR31413 PCR23      Added closed contracts report.
//   RI002     DTC9028      07/28/05     SR31413 PCR19      Implement datasource modification
//	 RI003     DTC2073      01/11/06     SR35879            Add Job Name to the report.
// *****************************************************************************************************************

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
<jsp:useBean id="closedContractsBean" class="etrieveweb.etrievebean.closedContractsBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />
<jsp:useBean id="sortOptionBean" class="etrieveweb.etrievebean.sortOptionBean" scope="page" />
<jsp:useBean id="yearOptionsBean" class="etrieveweb.etrievebean.yearOptionsBean" scope="page" />

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

String selectYear = (String) request.getParameter("selectYear"); 	// RI003

String str_userid = "";

if ( loginuserid == null )
	loginuserid = "";

Connection MenuSecurityBeanconnection = null;
Connection closedContractsBeanconnection = null;

//*************************************************************
// Retrieve today's date to be used for copyright statment.
//*************************************************************
  
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  

if ( selectYear == null )	selectYear = "Last 6 Months"; 			// RI003
if ( selectYear.trim().equals("") )	 selectYear = "Last 6 Months";	// RI003

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

String webPageName = "closedContracts";

String[] returnArray = sortOptionBean.getSortOptions(webPageName, sortFld0, sortFld1, sortFld2, order0, order1, order2);

String str_OrderBy = returnArray[0];
String sortOptionsTable = returnArray[1];
String hiddenSortFields = returnArray[2];

// ***********************************
// RI003 - Format year filter options
// ***********************************

String strYearFilter = yearOptionsBean.getFilterOptions(selectYear,webPageName);	// RI003

// ********************************
// Download variables
// ********************************

String displayPageName = "Closed Rentals Report" + " " + selectYear; // RI003

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
				<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;closed rentals&nbsp;&nbsp;&nbsp;</td>
				<td></td>
			</tr>

		</table>

		<br>

		<%

		// *************************************************************************
		// Make a new AS400 connection to retrieve account information and pickup ticket status
		// **************************************************************************

		closedContractsBeanconnection = closedContractsBean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		String str_color    = "#cccccc";

		String str_description = "";
		String str_contract = "";
		String str_startdate = "";
		String str_item = "";
		String str_closedate = "";
		String str_jobname = "";	// RI003
  
		String str_total = closedContractsBean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, jobnumber, selectYear);
  
		int num_display = 500;
		int num_count = 0;
		int num_total = Integer.valueOf(str_total).intValue();
		int num_current = Integer.valueOf(startnumber).intValue();
		int num_nextstart = num_current + num_display;
		int num_prevstart = num_current - num_display;

		if ( num_prevstart <= 0 )
			num_prevstart = 0;

		// ***************************************************
		//  Setup the data list array used for the download.  
		//  The first line is reserved for column heading
		// ***************************************************
		
		String dataList [][] = new String[num_total+1][12];		// RI003

		dataList[0][0] = "Account#";
		dataList[0][1] = "Account Name";
		dataList[0][2] = "Contract#";
		dataList[0][3] = "Start Date";
		dataList[0][4] = "Closed Date";
		dataList[0][5] = "Equipment#";
		dataList[0][6] = "Quantity";
		dataList[0][7] = "Description";
		dataList[0][8] = "Ordered By";		
		dataList[0][9] = "Purchase Order";
		dataList[0][10] = "Job Name"; 		// RI003
		dataList[0][11] = "Year Returned";	// RI003
		
		// ****************************************************************
		// Attempt to retrieve session objects for this report
		// ****************************************************************

		String sesDataList[][] = (String [][])session.getAttribute("dataList");
		String reportName      = (String)session.getAttribute("reportName");
		boolean loadDataList   = false;

		if ( num_current == 0 || sesDataList == null || reportName == null || ( reportName != null && !reportName.equals(webPageName) ) )
		{ 
			session.removeAttribute("dataList");
			session.removeAttribute("reportName");
			loadDataList = true;
		}
		else
		{
			dataList = sesDataList;
		}

		// *****************************************************************
		// Add the table that will position the download button
		// *****************************************************************	
						
		%>
		<table border="0" width="900">   
			<tr>
				<Form action="closedContracts.jsp"  name="yearFilterFrm" method=POST  onsubmit=" return validate();">
				<td width="723" class="tableheader3" valign="bottom">
					Show rental history for:&nbsp;<%=strYearFilter%>				
						<input type="hidden" name="jobnumber" value= "<%=jobnumber%>">	
				</td>
				</Form>
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
						<td width="45" class="footerblack" align="left"><class="history"><a href='downloaddetails.jsp'>[details]</a></td>
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
					<Form action="closedContracts.jsp"  name="sortFrm" method=POST  onsubmit=" return validate();"> 
						<tr bgcolor="#999999">
							<td colspan="9"  align="center">
								<input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
								<input type="hidden" name="selectYear" value= "<%=selectYear%>">	
								<%=sortOptionsTable%>
							</td>
						</tr>
					</form>
				<%	
				} 
				%>
				
				<tr>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="7%" class="whitemid">Contract</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="7%"  class="whitemid">Start<br>Date</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="7%"  class="whitemid">Closed<br>Date</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="8%" class="whitemid">Equip #</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="5%"  class="whitemid">Qty</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="29%" class="whitemid">Description</td>
					<td bgcolor="#000000" background="images/empty.gif" align="center" valign="top" width="12%" class="whitemid">Ordered<br>By</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="13%" class="whitemid">Purchase Order</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left"   valign="top" width="12%" class="whitemid">Job Name</td>
				</tr>
				

			<%
				// ******************************
				// Process the detail records
				// ******************************

				int num_start = 0;
							
				if  ( !loadDataList )
						num_start = num_current;
						
				if ( closedContractsBean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_start, jobnumber, str_OrderBy, selectYear ) ) {

					while ( closedContractsBean.getNext() ) {
					
						if ( (num_count >= num_display) && !loadDataList )
							break;	

						num_count++;

						if ( str_color.equals("#cccccc") )
							str_color = "#999999";
						else 
							str_color = "#cccccc";
    
						str_contract = closedContractsBean.getColumn("RDCON#");
						str_description = closedContractsBean.getColumn("ECDESC").replace('"', ' ');
						str_startdate = closedContractsBean.getColumn("RHDATO");
						str_item = closedContractsBean.getColumn("RDITEM");
						str_jobname = closedContractsBean.getColumn("CJNAME");	// RI003

						// **************
						// Start Date
						// **************

						if ( !str_startdate.trim().equals("0") && str_startdate.length() == 8)
							str_startdate = str_startdate.substring(4, 6) + "/" + str_startdate.substring(6, 8) + "/" + str_startdate.substring(2, 4);
						else
							str_startdate = "";
							
						// **************
						// Closed Date
						// **************
						
						str_closedate = closedContractsBean.getColumn("RHLRDT");

						if ( !str_closedate.trim().equals("0") && str_closedate.length() == 8)
							str_closedate = str_closedate.substring(4, 6) + "/" + str_closedate.substring(6, 8) + "/" + str_closedate.substring(2, 4);
						else
							str_closedate = "";
					
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
						// RI003 - Check job name
						// **************************************************************
						
						if ( str_jobname == null )		// RI003
							str_jobname = "";			// RI003
							
						// **************************************************************
						// Add the detail to the two-dimentional array used later to output to a CSV file
						// Ensure that null values are changes to blanks
						// **************************************************************
						
						dataList[num_count][0] = str_customer;
						dataList[num_count][1] = str_companyname;
						dataList[num_count][2] = str_contract;
						dataList[num_count][3] = str_startdate;
						dataList[num_count][4] = str_closedate;
						dataList[num_count][5] = closedContractsBean.getColumn("RDITEM");
						dataList[num_count][6] = closedContractsBean.getColumn("RDQTY");
						dataList[num_count][7] = str_description;
						dataList[num_count][8] = closedContractsBean.getColumn("RHORDB");
						dataList[num_count][9] = closedContractsBean.getColumn("RHPO#");
						dataList[num_count][10] = str_jobname;			// RI003
						dataList[num_count][11] = selectYear;			// RI003
						
						for(int j = 0; j < dataList[num_count].length; j++)	
						{
							if ( dataList[num_count][j] == null )
								dataList[num_count][j] = "";
						}
						
						// *******************************************************
						// Show only the maximum records per page
						// *******************************************************
						 
						if( (num_count + num_current) > num_current && (num_count + num_current) <= num_nextstart)		// RI014
						{	
								
						%>

							<tr>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><%=str_contract%><Br><br><a href="contractHistory.jsp?contract=<%=str_contract%>&sequence=000&ret=CR">View<br>Related<br>Documents</a></td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--START_DATE--><%=str_startdate%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--CLOSED DATE--><%=str_closedate%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--ITEM--><%=closedContractsBean.getColumn("RDITEM")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--QUANTITY--><%=closedContractsBean.getColumn("RDQTY")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--DESC--><%=str_description%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--ORDERED_BY--><%=closedContractsBean.getColumn("RHORDB")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--PO#--><%=closedContractsBean.getColumn("RHPO#")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--JOB NAME--><%=str_jobname%>&nbsp;</td>
							</tr>

						<%
						}
					}
				}
				
				// ******************************************************************************************************
				// Place the two-dimentional array in a session object to be retrieved in the download servlet
				// ******************************************************************************************************
				
				if  ( loadDataList )
				{
					session.setAttribute("dataList", dataList );
					session.setAttribute("reportName", webPageName );
				}
				
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
						<form action="closedContracts.jsp" method=POST> 
							<input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
							<input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
							<input type="hidden" name="selectYear" value= "<%=selectYear%>">
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
						<form action="closedContracts.jsp" method=POST> 
							<input type="hidden" name="startnumber" value="<%=num_nextstart%>">
							<input type="hidden" name="jobnumber" value= "<%=jobnumber%>"> 
							<input type="hidden" name="selectYear" value= "<%=selectYear%>">
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

closedContractsBean.cleanup();	// RI002
closedContractsBean.endcurrentConnection(closedContractsBeanconnection);

%>

</BODY>
</HTML>