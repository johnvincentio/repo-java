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
//    Index    User Id       Date       Project                       Desciption
//    ------  -----------  ----------  ---------------   ----------------------------------------------------------
//    x001     DTC9028     11/26/02    SR26609 PCR1       Modification for the new log-in process
//    x002     DTC9028     11/26/02    SR26609 PCR1       Ensure that connection are ended 
//    x003     DTC9028     12/26/02    SR26609 PCR1       Changed the "log out" to "close reports"
//    x004     DTC9028     12/26/02    SR26609 PCR1       Changed the page title 
//    x005     DTC9028     12/26/02    SR26609 PCR1       Changed the copy right text 
//    x006     DTC9028     12/26/02    SR26609 PCR1       Add the account number
//    x007     DTC9028     01/26/03    SR26609 PCR1       Remove the hyper-link on the equipment number
//    x008     DTC9028     01/26/03    SR26609 PCR1       Replace daily, weekly and monthly rates with invoice date, start date and return date
//    x009     DTC2073     08/19/04    SR31413 PCR5       Add sort options to the page.  
//    RI010    DTC2073     02/01/05    SR31413 PCR26      Add copyright year dynamically to the page. 
//    RI011    DTC2073     05/06/05    SR31413 PCR24      Data download feature
//    RI012    DTC2073     11/15/05    SR35880            Add comments for re-rent items (catg/class 975-0001) and year filter
//    RI013	   DTC2051	   01/09/07    SR39459            Website re-write support.
//    RI014	   DTC9028	   01/08/08    SR39459            Changed max records per page from 10000 to 100.
//    VB015    DTC2073     09/05/08    SR44922            E-Trieve Websphere Fix Pack - Remove word servlet from URL
//    VB016    DTP0056     10/17/08    TT-IN6261          Passing parameter to menu
//									   TT-IN9252
// *****************************************************************************************************************

// RI013	String[]    names = session.getValueNames(); 
// RI013	if ( names.length <= 0 || names[0] == "")
// RI013	  response.sendRedirect("securityFailure.jsp");	

Enumeration enum = session.getAttributeNames();		// RI013

if (!enum.hasMoreElements())						// RI013
	response.sendRedirect("securityFailure.jsp");	// RI013
	
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

<%@ page language="java" import="java.sql.*, java.util.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="detailhistorybean" class="etrieveweb.etrievebean.eqdetailhistoryBean" scope="page" />
<jsp:useBean id="sortOptionBean" class="etrieveweb.etrievebean.sortOptionBean" scope="page" />
<jsp:useBean id="yearOptionsBean" class="etrieveweb.etrievebean.yearOptionsBean" scope="page" />

<%

// ************************
// Get input parameters
// ************************

String str_companyname  = (String) session.getAttribute("companyname");		// RI013

String startnumber   = request.getParameter("startnumber");
String str_catg      = request.getParameter("catg");
String str_class     = request.getParameter("class");
String str_desc      = request.getParameter("desc");
String str_jobnumber = request.getParameter("jobnumber");

String sortFld0 = (String) request.getParameter("sortFld0");
String sortFld1 = (String) request.getParameter("sortFld1");
String sortFld2 = (String) request.getParameter("sortFld2");

String order0 = (String) request.getParameter("order0");
String order1 = (String) request.getParameter("order1");
String order2 = (String) request.getParameter("order2");

String selectYear = (String) request.getParameter("selectYear"); 	// RI012

//*************************************************************
// RI010 - Retrieve today's date to be used for copyright statment.
//*************************************************************
 
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  


// ********************************
// Validate the input parameters
// ********************************

if ( startnumber == null )	startnumber = "0";
if (str_catg == null) str_catg="";
if (str_class == null) str_class="";
if (str_desc == null) str_desc="";
if (str_jobnumber == null) str_jobnumber="";
if ( selectYear == null )	selectYear = Integer.toString(cpYear); 				// RI012
if ( selectYear.trim().equals("") )	 selectYear = Integer.toString(cpYear); 	// RI012

String str_customer    = (String) session.getAttribute("customer");		// RI013
String str_company     = (String) session.getAttribute("company");		// RI013
String str_datalib     = (String) session.getAttribute("datalib");		// RI013
String list_customer   = (String) session.getAttribute("list");			// RI013
String str_username    = (String) session.getAttribute("username");		// RI013
String str_password    = (String) session.getAttribute("password");		// RI013
String str_as400url    = (String) session.getAttribute("as400url");		// RI013
String str_as400driver = (String) session.getAttribute("as400driver");	// RI013
String str_GoldCountry = (String) session.getAttribute("CountryCode");	// RI013
String str_connType    = "INQ"; 	//RI013

// ***********************************
// Format the sort options table
// ***********************************

String webPageName = "detailHistory";

String[] returnArray = sortOptionBean.getSortOptions(webPageName, sortFld0, sortFld1, sortFld2, order0, order1, order2);

String str_OrderBy = returnArray[0];
String sortOptionsTable = returnArray[1];
String hiddenSortFields = returnArray[2];


// ***********************************
// RI012 - Format year filter options
// ***********************************

String strYearFilter = yearOptionsBean.getFilterOptions(selectYear);	// RI012


// ********************************
// Download variables
// ********************************

String displayPageName = "Detail History Report for " + selectYear; // RI011

// ********************************
// Create connection variable
// ********************************
	
Connection detailhistorybeanconnection = null;

%>

<BODY BGCOLOR="#ffffff" TEXT="#000000" LINK="#cccccc" VLINK="#666666">

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
				<!-- VB016 -->	<!-- <td align="right" class="history"><a href='menu.jsp'>report menu</a>&nbsp;&nbsp;&nbsp;detail history&nbsp;&nbsp;&nbsp;</td>  -->
                <td align="right" class="history"><a href='menu.jsp?showReports=1'>report menu</a>&nbsp;&nbsp;&nbsp;detail history&nbsp;&nbsp;&nbsp;</td>           <!-- VB016 -->
				<td></td>
			</tr>

		</table>

		<%

		// ***************************************************
		// Make a new AS400 connection to retrieve account information
		// ***************************************************

		detailhistorybeanconnection = detailhistorybean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password, str_GoldCountry, str_connType);	// RI013

		String str_color = "#cccccc";
		String str_total = detailhistorybean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_catg, str_class, str_jobnumber, selectYear);
		String str_rateused  = "";
		String str_itemcomments = "";		// RI012
		
		int num_display = 100;	// RI014
		int num_count = 0;
		int num_total = Integer.valueOf(str_total).intValue();
		int num_current = Integer.valueOf(startnumber).intValue();
		int num_nextstart = num_current + num_display;
		int num_prevstart = num_current - num_display;

		if ( num_prevstart <= 0 )
			num_prevstart = 0; 


		// ***************************************************
		//  RI011 - Setup the data list array used for the download.  
		//  The first line is reserved for column heading
		// ***************************************************
		
		String dataList [][] = new String[num_total+1][12];		// RI011
		
		dataList[0][0] = "Account#";		// RI011
		dataList[0][1] = "Account Name";	// RI011
		dataList[0][2] = "Cat-Class";		// RI011
		dataList[0][3] = "Description";		// RI011
		dataList[0][4] = "Equip#";			// RI011
	  	dataList[0][5] = "Invoice";			// RI011
		dataList[0][6] = "Invoice Date";	// RI011
		dataList[0][7] = "Start Date";		// RI011
		dataList[0][8] = "Return Date";		// RI011
		dataList[0][9] = "Rate Used";		// RI011
		dataList[0][10] = "Rental Amount";	// RI011
		dataList[0][11] = "Rental Year";	// RI012
		
		// ****************************************************************
		// RI011 - Attempt to retrieve session objects for this report
		// ****************************************************************

		String sesDataList[][] = (String [][])session.getAttribute("dataList");		// RI011
		String reportName      = (String)session.getAttribute("reportName");		// RI011
		boolean loadDataList   = false;												// RI011

		if ( num_current == 0 || sesDataList == null || reportName == null || ( reportName != null && !reportName.equals(webPageName) ) )	// RI011
		{ 
			session.removeAttribute("dataList");		// RI011
			session.removeAttribute("reportName");		// RI011
			loadDataList = true;						// RI011
		}												// RI011
		else											// RI011
		{												// RI011
			dataList = sesDataList;						// RI011
		}												// RI011

		// *****************************************************************
		// RI011 - Add the table that will position the download button
		// *****************************************************************	

		%>


		<table border="0" width="900">   

			<tr>
				<td valign="bottom">
					DESCRIPTION: <a class="data"><%=str_desc%></a>
					<br>
					CATEGORY-CLASS: <a class="data"><%=str_catg%>-<%=str_class%></a>
				</td>
			</tr>	
			<tr>
				<td>&nbsp;</td>
			</tr>		
		</table>

						
		<table border="0" width="900">   
			<tr>
				<Form action="detailHistory.jsp"  name="yearFilterFrm" method=POST  onsubmit=" return validate();">
					<td width="723" valign="middle" class="tableheader3" >
						<input type="hidden" name="catg" value= "<%=str_catg%>">
						<input type="hidden" name="class" value= "<%=str_class%>">
						<input type="hidden" name="desc" value= "<%=str_desc%>">
						<input type="hidden" name="jobnumber" value= "<%=str_jobnumber%>">	
						Show detail history for year:&nbsp;&nbsp;<%=strYearFilter%>	
					</td>
				</form>
				<% 		
				if (num_total == 0 ) {		// RI011  
				%>
					<td width="177" class="footerblack">&nbsp;</td>								
				<% 
				} else { 
				%> 
					<td width="45" align=right" valign="middle">&nbsp;</td>
					<td width="87" class="footerblack" align="right">
						<Form action="EtrieveDownLoadServlet"  method=POST> 
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
					<Form action="detailHistory.jsp"  name="sortFrm" method=POST  onsubmit=" return validate();"> 
						<tr bgcolor="#999999">
							<td colspan="7"  align="center">
								<input type="hidden" name="catg" value= "<%=str_catg%>">
								<input type="hidden" name="class" value= "<%=str_class%>">
								<input type="hidden" name="desc" value= "<%=str_desc%>">
								<input type="hidden" name="jobnumber" value= "<%=str_jobnumber%>">	
								<input type="hidden" name="selectYear" value="<%=selectYear%>">
								<%=sortOptionsTable%>
							</td>
						</tr>
					</Form>
				<%	
				} 
				%>

				<tr>
					<td background="images/empty.gif" bgcolor="#000000" align="left"   width="24%" class="whitemid">Equipment Number</td>
					<td background="images/empty.gif" bgcolor="#000000" align="left"   width="16%" class="whitemid">Invoice</td>
					<td background="images/empty.gif" bgcolor="#000000" align="center" width="10%" class="whitemid">Invoice<br>Date</td>
					<td background="images/empty.gif" bgcolor="#000000" align="center" width="10%" class="whitemid">Start<br>Date</td>
					<td background="images/empty.gif" bgcolor="#000000" align="center" width="10%" class="whitemid">Return<br>Date</td>
					<td background="images/empty.gif" bgcolor="#000000" align="right"  width="15%" class="whitemid">Rate Used</td>
					<td background="images/empty.gif" bgcolor="#000000" align="right"  width="15%" class="whitemid">Rental Amount</td>
				</tr>

				
				<%
				// **************************************
				// Process the detail history records
				// **************************************

				// *************************************************************
				//  RI010 - Determine no of records to be retrieved and processed.
				// *************************************************************

				int num_start = 0;					// RI011
							
				if  ( !loadDataList )				// RI011
						num_start = num_current;	// RI011
						
						
				if ( detailhistorybean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_catg, str_class, str_jobnumber, num_start, str_OrderBy, selectYear ) ) {		// RI011

					// ********************************************************************************
					// RI011 - Break out of the loop if the data list has already been loaded
					//         and the page record limit has been reached
					// ********************************************************************************
					
					// RI011 -    while ( detailhistorybean.getNext()  && num_count < num_display) {


					while ( detailhistorybean.getNext() ) {		// RI011
					
						if ( (num_count >= num_display) && !loadDataList )	// RI011
							break;											// RI011
							
						num_count++;

						if ( str_color.equals("#cccccc") )
							str_color = "#999999";
						else 
							str_color = "#cccccc";
    

						String str_sequence = detailhistorybean.getColumn("RDISEQ");

						while( str_sequence.length() < 3 ) {
							str_sequence = "0" + str_sequence;
						}

						// ***************************************************************
						// for rate used, substitute a word for the single letter returned in the result set
						// ***************************************************************

						str_rateused = detailhistorybean.getColumn("RDRATU");

						if ( str_rateused.trim().equals("H") ) 
							str_rateused = "DAY";
						else if (str_rateused.equals("D")) 
							str_rateused = "DAY";
						else if (str_rateused.equals("W")) 
							str_rateused = "WEEK";
						else if (str_rateused.equals("M")) 
							str_rateused = "MONTH";

						// ******************
						// Invoice date
						// ******************
												
						String str_invdate = detailhistorybean.getColumn("RDSYSD");

						if ( !str_invdate.trim().equals("0") && str_invdate.length() == 8)  {
						
							str_invdate = str_invdate.substring(4, 6) + "/" + str_invdate.substring(6, 8) + "/" + str_invdate.substring(2, 4);

						} else {
						
							str_invdate = "";
							
						}


						// ******************
						// Start date
						// ******************

						String str_dateout = detailhistorybean.getColumn("RDDATO");
          
						if ( !str_dateout.trim().equals("0") && str_dateout.length() == 8)   {

							str_dateout = str_dateout.substring(4, 6) + "/" + str_dateout.substring(6, 8) + "/" + str_dateout.substring(2, 4);

						} else {
						
							str_dateout = "";
							
						}


						// ******************
						// Return date
						// ******************

						String str_datein = detailhistorybean.getColumn("RDDATI");
          
						if ( !str_datein.trim().equals("0") && str_datein.length() == 8)  {

							str_datein = str_datein.substring(4, 6) + "/" + str_datein.substring(6, 8) + "/" + str_datein.substring(2, 4);

						} else  {

							str_datein = "";

						}

						// *** Retrieve the description for rerent items

						int int_catg = Integer.valueOf(str_catg).intValue();		//  RI012
						int int_class = Integer.valueOf(str_class).intValue();		//  RI012
						
						if ( str_company.equals("HG") && int_catg == 975 && int_class == 1 )	{	// RI012
							String str_contract = detailhistorybean.getColumn("RDCON#");			// RI002
							String str_dseq = detailhistorybean.getColumn("RDSEQ#");				// RI002
							str_itemcomments = detailhistorybean.getItemComments(str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), Integer.valueOf(str_dseq).intValue());  // RI012
						} else {
							str_itemcomments = "";	// RI012
						}
						
						// **************************************************************
						// RI011 - Add the detail to the two-dimentional array used later to output to a CSV file
						//         Ensure that null values are changes to blanks
						// **************************************************************

						dataList[num_count][0] = str_customer;													// RI011
						dataList[num_count][1] = str_companyname;												// RI011
						dataList[num_count][2] = str_catg + "-" + str_class;									// RI011
						dataList[num_count][3] = str_desc;														// RI011
						dataList[num_count][4] = detailhistorybean.getColumn("RDITEM");							// RI011
						dataList[num_count][5] = detailhistorybean.getColumn("RDCON#") + "-" + str_sequence;	// RI011
						dataList[num_count][6] = str_invdate ;													// RI011
						dataList[num_count][7] = str_dateout;													// RI011
						dataList[num_count][8] = str_datein;													// RI011
						dataList[num_count][9] = str_rateused;													// RI011
						dataList[num_count][10] =  "$" + detailhistorybean.getColumn("RDAMT$");					// RI011
						dataList[num_count][11] = selectYear;													// RI012

						if ( !str_itemcomments.trim().equals("") )	{											// RI002
							dataList[num_count][3] = dataList[num_count][3].trim() + " *** COMMENT: " + str_itemcomments.trim();		// RI002
							str_itemcomments = "<BR>" + str_itemcomments.trim();
						}
						
						for(int j = 0; j < dataList[num_count].length; j++)					// RI011
						{																	// RI011
							if ( dataList[num_count][j] == null )							// RI011
								dataList[num_count][j] = "";								// RI011
						}																	// RI011
						
						// *******************************************************
						// RI011 - show only the maximum records per page
						// *******************************************************
						 
						if( (num_count + num_current) > num_current && (num_count + num_current) <= num_nextstart)		// RI011
						{		
						
						%>

							<tr>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--EQUIP_NUM--><%=detailhistorybean.getColumn("RDITEM")%><%=str_itemcomments%></td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--INVOICE--> <a href="contractDetail.jsp?contract=<%=detailhistorybean.getColumn("RDCON#")%>&sequence=<%=str_sequence%>"><%=detailhistorybean.getColumn("RDCON#")%>-<%=str_sequence%></a></td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--INVOICE DATE--><%=str_invdate%></td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--START DATE--><%=str_dateout%></td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--RETURN DATE--><%=str_datein%></td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata"><!--RATE_USED--><%=str_rateused%></td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata"><!--RENT_COST-->$<%=detailhistorybean.getColumn("RDAMT$")%></td>
							</tr>

				<%
						}   //  RI011
					}
				}
				
				// ******************************************************************************************************
				// RI011 - place the two-dimentional array in a session object to be retrieved in the download servlet
				// ******************************************************************************************************
				
				if  ( loadDataList )										// RI011
				{															// RI011
					session.setAttribute("dataList", dataList );			// RI011
					session.setAttribute("reportName", webPageName );		// RI011
				}															// RI011
				
				
				%>

			</table>

			<table border="0" width="900" cellspacing="0" cellpadding="0">

				<tr>
					<td align="right" colspan="3" class="tableheader3">Total: <!--TOTAL_AMT-->$<%=detailhistorybean.getTotalAmount(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_catg, str_class, str_jobnumber, selectYear)%>&nbsp;</td>
				</tr>

				<tr>
					<td align="left" width="745" valign="top" class="tableheader3"><!--NUM_RECORDS-->
						<%  if ( num_total > 0 ) {  %>
								&nbsp;<%=num_current+1%> - 

							<%	if ( num_nextstart < num_total ) {   %>
									<%=num_nextstart%> of 
							<%	} else {    %>
									<%=num_total%> of 
						<%
								}
							}
                        %>

						<%=str_total%> record(s).
					</td>

					<td align="right" width="90">
						<%	if ( num_prevstart >= 0 && num_current != 0 ) {   %>
								<form action="detailHistory.jsp" method=GET> 
									<input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
									<input type="hidden" name="catg" value= "<%=str_catg%>">
									<input type="hidden" name="class" value= "<%=str_class%>">
									<input type="hidden" name="desc" value= "<%=str_desc%>">
									<input type="hidden" name="jobnumber" value= "<%=str_jobnumber%>">
									<%=hiddenSortFields%>
									<input type="hidden" name="selectYear" value="<%=selectYear%>">
									<input type=image src="images/prev.gif" height="40" width="87" border="0">
								</form>
						<%	}  %>
					</td>

					<td width="65"> 
						<%	if ( num_nextstart < num_total ) {    %>
								<form action="detailHistory.jsp" method=GET> 
									<input type="hidden" name="startnumber" value="<%=num_nextstart%>">
									<input type="hidden" name="catg" value= "<%=str_catg%>">
									<input type="hidden" name="class" value= "<%=str_class%>">
									<input type="hidden" name="desc" value= "<%=str_desc%>">
									<input type="hidden" name="jobnumber" value= "<%=str_jobnumber%>"> 
									<%=hiddenSortFields%>
									<input type="hidden" name="selectYear" value="<%=selectYear%>">
									<input type=image src="images/next.gif" height="40" width="62" border=0> 
								</form>
						<%	}    %>
					</td>

				</tr>

			</table>

		</center>

		<br>

		<a href="javascript:history.go(-1)"><IMG src="images/back.gif" height="40" width="62" border="0"></a> 
		<!-- VB016 -->   <!-- <a href="menu.jsp"><IMG src="images/menu.gif" height="40" width="62" border="0"></a> -->
        <a href="menu.jsp?showReports=1"><IMG src="images/menu.gif" height="40" width="62" border="0"></a>    <!-- VB016 --> 
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

	detailhistorybean.endcurrentConnection(detailhistorybeanconnection);
	%>

</BODY>
</HTML>
