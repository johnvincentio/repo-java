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
//    Index     User Id        Date          Project          Desciption
//   --------  -----------  -----------  ------------------  ----------------------------------------------------------
//    x001      DTC9028      11/26/02     SR26609 pre-load    Modification for the new log-in process
//    x002      DTC9028      11/26/02     SR26609 PCR1        Ensure that connection are ended 
//    x003      DTC9028      12/26/02     SR26609 PCR1        Changed the "log out" to "close reports"
//    x004      DTC9028      12/26/02     SR26609 PCR1        Changed the page title 
//    x005      DTC9028      12/26/02     SR26609 PCR1        Changed the copy right text 
//    x006      DTC9028      12/26/02     SR26609 PCR1        Add the account number
//    x007      DTC9028      02/11/03     SR26609 PCR1        Remove the current balance from the page
//    x008      DTC9028      02/11/03     SR26609 PCR1        Replace RHJOBL with RHPO#
//    x009      DTC9028      04/28/03     SR28586 PCR1        Add logic to check security for a customer rep
//    x010      DTC9028      03/02/04     SR28586 PCR27       Include credit balances for Canadia Etrieve (company = CR)
//    x011      DTC2073      08/19/04     SR31413 PCR5        Add sort options to the page.  
//    RI012     DTC2073      02/01/05     SR31413 PCR26       Add copyright year dynamically to the page
//    RI013     DTC2073      05/06/05     SR31413 PCR24       Data download feature
//    RI014     DTC9028      07/28/05     SR31413 PCR19       Implement datasource modification
// *************************************************************************************************************
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

<%@ page language="java" import="java.sql.*, java.util.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="paidinvoicebean" class="etrieveweb.etrievebean.paidinvoiceBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />
<jsp:useBean id="sortOptionBean" class="etrieveweb.etrievebean.sortOptionBean" scope="page" />


 
<%
String str_username = (String)session.getValue("username");
String str_customer = (String) session.getValue("customer");
String str_company  = (String) session.getValue("company");
String str_datalib  = (String) session.getValue("datalib");
String list_customer = (String) session.getValue("list");
String str_password = (String)session.getValue("password");
String str_as400url = (String)session.getValue("as400url");
String str_as400driver = (String)session.getValue("as400driver");
String loginuserid = (String)session.getValue("loginuserid");
String str_userid = "";

if ( loginuserid == null )
	loginuserid = "";

Connection MenuSecurityBeanconnection = null;
Connection paidinvoicebeanconnection = null;

//*************************************************************
// RI012 - Retrieve today's date to be used for copyright statment.
//*************************************************************
  
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  

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

MenuSecurityBeanconnection  = MenuSecurityBean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U85"))
{
	response.sendRedirect("menu.jsp");
}

// ************************
// Get input parameters
// ************************

String str_companyname  = (String) session.getValue("companyname");
String startnumber = request.getParameter("startnumber");
String jobnumber = request.getParameter("jobnumber");

String sortFld0 = (String) request.getParameter("sortFld0");
String sortFld1 = (String) request.getParameter("sortFld1");
String sortFld2 = (String) request.getParameter("sortFld2");

String order0 = (String) request.getParameter("order0");
String order1 = (String) request.getParameter("order1");
String order2 = (String) request.getParameter("order2");

// ********************************
// Validate the input parameters
// ********************************

if ( startnumber == null )	startnumber = "0";
if ( jobnumber == null )	jobnumber = "";

// ***********************************
// Format the sort options table
// ***********************************

String webPageName = "paidInvoice";

String[] returnArray = sortOptionBean.getSortOptions(webPageName, sortFld0, sortFld1, sortFld2, order0, order1, order2);

String str_OrderBy = returnArray[0];
String sortOptionsTable = returnArray[1];
String hiddenSortFields = returnArray[2];

// ********************************
// Download variables
// ********************************

String displayPageName = "Paid Invoice Report";		// RI013

// **********************
// End current connection
//**********************

MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);

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
					<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;paid invoices&nbsp;&nbsp;&nbsp;</td>
					<td></td>
				</tr>
			</table>

		</center>

		<br>


		<%

		// ***************************************************
		// Make a new AS400 connection to retrieve information
		// ***************************************************

		paidinvoicebeanconnection = paidinvoicebean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		String str_color    = "#cccccc";

		// Don't calculate     String str_balsum = paidinvoicebean.getTotalCurr(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib);
   
		String str_balsum = "";

		%>

		<%  if ( !str_balsum.equals("") )  { %>
				&nbsp;&nbsp;Current Balance: <a class="data"><!--BALANCE_SUM-->$<%=str_balsum%></a><br>
				<br>
		<%	}   %>

		<%  
		String str_total    = paidinvoicebean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib);

		int num_display = 500;
		int num_count = 0;
		int num_total = Integer.valueOf(str_total).intValue();
		int num_current = Integer.valueOf(startnumber).intValue();
		int num_nextstart = num_current + num_display;
		int num_prevstart = num_current - num_display;

		if ( num_prevstart <= 0 )
			num_prevstart = 0;

		// ***************************************************
		//  RI013 - Setup the data list array used for the download.  
		//  The first line is reserved for column heading
		// ***************************************************
		
		String dataList [][] = new String[num_total+1][9];		// RI013
		
		dataList[0][0] = "Account#";		// RI013
		dataList[0][1] = "Account Name";	// RI013
		dataList[0][2] = "Invoice #";		// RI013
		dataList[0][3] = "Inv Date";		// RI013
		dataList[0][4] = "Loc";				// RI013
		dataList[0][5] = "Status";			// RI013
		dataList[0][6] = "Original";		// RI013
		dataList[0][7] = "Balance";			// RI013
		dataList[0][8] = "Purchase Order";	// RI013	
		
		// ****************************************************************
		// RI013 - Attempt to retrieve session objects for this report
		// ****************************************************************

		String sesDataList[][] = (String [][])session.getAttribute("dataList");		// RI013
		String reportName      = (String)session.getAttribute("reportName");		// RI013
		boolean loadDataList   = false;												// RI013

		if ( num_current == 0 || sesDataList == null || reportName == null || ( reportName != null && !reportName.equals(webPageName) ) )	// RI013
		{ 
			session.removeAttribute("dataList");		// RI013
			session.removeAttribute("reportName");		// RI013
			loadDataList = true;						// RI013
		}												// RI013
		else											// RI013
		{												// RI013
			dataList = sesDataList;						// RI013
		}												// RI013

		// *****************************************************************
		// RI013 - Add the table that will position the download button
		// *****************************************************************	

		if (num_total > 0 ) { 		// RI013

		%>

			<table border="0" width="650">   
				<tr>
					<td width="473" class="tableheader3" align=right" valign="top">&nbsp;</td>
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
				</tr>
			</table>
		<%
		}    // RI013
		%>
		
		
		<table cellpadding="3" cellspacing="1" border="0" width="650">

			<%	
			// *********************************************
			// Output the sort options table
			// *********************************************
	
			if (num_total > 0)  {  
			%>
				<Form action="paidInvoice.jsp"  name="sortFrm" method=POST  onsubmit=" return validate();">
					<tr bgcolor="#999999">
						<td colspan="7" align="center">			 
							<input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
							<%=sortOptionsTable%>
						</td>
					</tr>
				</Form>
			<%	
			} 
			%>

			<tr>
				<td background="images/empty.gif" bgcolor="#000000" align="right"  width="12%" valign="top" class="whitemid">Invoice #</td>
				<td background="images/empty.gif" bgcolor="#000000" align="center" width="10%" valign="top" class="whitemid">Inv Date</td>
				<td background="images/empty.gif" bgcolor="#000000" align="center" width="5%" valign="top" class="whitemid">Loc</td>
				<td background="images/empty.gif" bgcolor="#000000" align="center" width="8%" valign="top" class="whitemid">ST</td>
				<td background="images/empty.gif" bgcolor="#000000" align="right"  width="12%" valign="top" class="whitemid">Original</td>
				<td background="images/empty.gif" bgcolor="#000000" align="right"  width="10%" valign="top" class="whitemid">Balance</td>
				<td background="images/empty.gif" bgcolor="#000000" align="left"   width="43%" valign="top" class="whitemid">Purchase Order</td>
			</tr>


			<%
			
			// *************************************************************
			//  RI013 - Determine no of records to be retrieved and processed.
			// *************************************************************

			int num_start = 0;					// RI013
							
			if  ( !loadDataList )				// RI013
					num_start = num_current;	// RI013
					
			if ( paidinvoicebean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_start, str_OrderBy) ) {		// RI013

				// ********************************************************************************
				// RI013 - Break out of the loop if the data list has already been loaded
				//         and the page record limit has been reached
				// ********************************************************************************
				
				// RI013 -  while ( paidinvoicebean.getNext() && num_count < num_display) {


				while ( paidinvoicebean.getNext() ) {		// RI013
					
					if ( (num_count >= num_display) && !loadDataList )	// RI013
						break;											// RI013
							
					num_count++;

					if ( str_color.equals("#cccccc") )
						str_color = "#999999";
					else 
						str_color = "#cccccc";
    
					String str_contract = paidinvoicebean.getColumn("AHINV#");
					String str_sequence = paidinvoicebean.getColumn("AHISEQ");
					String str_balance  = paidinvoicebean.getColumn("AHCBAL");

					// *************************************
					//  Only display credit balances for Canada
					// *************************************

					if ( !str_balance.equals("0.00") &&  !str_company.trim().equals("CR")  )
						str_balance = "0.00";

						while( str_sequence.length() < 3 ) {
							str_sequence = "0" + str_sequence;
						}


						String str_duedate = paidinvoicebean.getColumn("AHDUED");

						if ( !str_duedate.trim().equals("0") && str_duedate.length() == 8)  {
							str_duedate = str_duedate.substring(4, 6) + "/" + str_duedate.substring(6, 8) + "/" + str_duedate.substring(2, 4);
						} else {
							str_duedate = "";
						}
						
						
						String str_status = "OPEN";

						if ( paidinvoicebean.getColumn("AHSTTS").equals("PD") ) 
							str_status = "PAID";
						else if ( paidinvoicebean.getColumn("AHSTTS").equals("OP") )
							str_status = "PARTIAL";
						else
							str_status = "ERR";

						String str_recordsource = paidinvoicebean.getColumn("AHSRC");
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
						// RI013 - Add the detail to the two-dimentional array used later to output to a CSV file
						//         Ensure that null values are changes to blanks
						// **************************************************************

						dataList[num_count][0] = str_customer;									// RI013
						dataList[num_count][1] = str_companyname;								// RI013
						dataList[num_count][2] = str_contract +"-"+ str_sequence;				// RI013
						dataList[num_count][3] = str_duedate;									// RI013
						dataList[num_count][4] = paidinvoicebean.getColumn("AHLOC");			// RI013
						dataList[num_count][5] = str_status;									// RI013
						dataList[num_count][6] = "$" + paidinvoicebean.getColumn("AHAMT$");		// RI013
						dataList[num_count][7] = "$" + str_balance;								// RI013
						dataList[num_count][8] = paidinvoicebean.getColumn("RHPO#");			// RI013

						for(int j = 0; j < dataList[num_count].length; j++)					// RI013
						{																	// RI013
							if ( dataList[num_count][j] == null )							// RI013
								dataList[num_count][j] = "";								// RI013
						}																	// RI013
						
						// *******************************************************
						// RI013 - show only the maximum records per page
						// *******************************************************
						 
						if( (num_count + num_current) > num_current && (num_count + num_current) <= num_nextstart)		// RI013
						{																								// RI013

						%>

							<tr>
								<form action="contractDetail.jsp" method=POST>
									<input type="hidden" name="contract" value="<!--CONTRACT--><%=str_contract%>">
									<input type="hidden" name="sequence" value="<!--SEQUENCE--><%=str_sequence%>">
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top" class="tabledata"><a href="contractDetail.jsp?contract=<%=str_contract%>&sequence=<%=str_sequence%>" onClick="submit();"><!--INVOICE--><%=str_contract%>-<%=str_sequence%></a></td>
								</form>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--INV_DATE--><%=str_duedate%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--LOCATION--><%=paidinvoicebean.getColumn("AHLOC")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--STATUS--><%=str_status%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top"   class="tabledata"><!--ORIGINAL-->$<%=paidinvoicebean.getColumn("AHAMT$")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top"   class="tabledata"><!--BALANCE-->$<%=str_balance%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top"      class="tabledata"><!--PO#--><%=paidinvoicebean.getColumn("RHPO#")%>&nbsp;</td>
							</tr>

					<%
						}   // RI013
					}
				}
				
				// ******************************************************************************************************
				// RI013 - place the two-dimentional array in a session object to be retrieved in the download servlet
				// ******************************************************************************************************
				
				if  ( loadDataList )										// RI013
				{															// RI013
					session.setAttribute("dataList", dataList );			// RI013
					session.setAttribute("reportName", webPageName );		// RI013
				}															// RI013
				
				%>


			</table>

			<table border="0" width="650" cellspacing="0" cellpadding="0">

				<tr>
					<td width="515" align="left" class="tableheader3"><!--NUM_RECORDS-->&nbsp;

            			<%
            			if ( num_total > 0 ) {    %>

							<%=num_current+1%> - 

                      		<%
							if ( num_nextstart < num_total ) {    %>
								<%=num_nextstart%> of 
                     		<%     
							} else {    
							%>
								<%=num_total%> of 
             				<%    
							}
						}
              			%>
						<%=str_total%> record(s).
					</td>

					<td align="right" width="90">
						<%   
						if ( num_prevstart >= 0 && num_current != 0 ) {   
						%>
							<form action="paidInvoice.jsp" method=POST> 
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
						if ( num_nextstart < num_total ) {    
						%>
							<form action="paidInvoice.jsp" method=POST> 
								<input type="hidden" name="startnumber" value="<%=num_nextstart%>">
								<%=hiddenSortFields%>
                                <input type=image src="images/next.gif" height="40" width="62" border=0> 
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

  paidinvoicebean.cleanup();	// RI014
  paidinvoicebean.endcurrentConnection(paidinvoicebeanconnection);

%>

</BODY>
</HTML>