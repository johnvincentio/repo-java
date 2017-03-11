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
//    Index      User Id        Date         Project            Desciption
//   --------   -----------  ------------  -----------------   ----------------------------------------------------------
//    x001       DTC9028      11/26/02      SR26609 PCR1        Modification for the new log-in process
//    x002       DTC9028      11/26/02      SR26609 PCR1        Ensure that connection are ended 
//    x003       DTC9028      12/26/02      SR26609 PCR1        Changed the "log out" to "close reports"
//    x004       DTC9028      12/26/02      SR26609 PCR1        Changed the page title 
//    x005       DTC9028      12/26/02      SR26609 PCR1        Changed the copy right text 
//    x006       DTC9028      01/30/03      SR26609 PCR1        Made the date the hyper-link instead of check#
//    x007       DTC2073      08/19/04      SR31413 PCR5        Add sort options to the page.  
//    RI008      DTC2073      02/01/05      SR31413 PCR26       Add copyright year dynamically to the page
//    RI009      DTC2073      05/06/05      SR31413 PCR24       Data download feature
//    RI010      DTC9028      06/27/05      SR31413 PCR19       Implement datasource modifications
// ***************************************************************************
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
<jsp:useBean id="paychecktotalbean" class="etrieveweb.etrievebean.paychecktotalBean" scope="page" />
<jsp:useBean id="sortOptionBean" class="etrieveweb.etrievebean.sortOptionBean" scope="page" />


<%
// ************************
// Get input parameters
// ************************

String str_companyname  = (String) session.getValue("companyname");

String startnumber = request.getParameter("startnumber");

String sortFld0 = (String) request.getParameter("sortFld0");
String sortFld1 = (String) request.getParameter("sortFld1");
String sortFld2 = (String) request.getParameter("sortFld2");

String order0 = (String) request.getParameter("order0");
String order1 = (String) request.getParameter("order1");
String order2 = (String) request.getParameter("order2");

 //*************************************************************
 // Retrieve today's date to be used for copyright statment.
 //*************************************************************
  
 Calendar cpToday = Calendar.getInstance();
 int cpYear = cpToday.get(Calendar.YEAR) ;  

// ********************************
// Validate the input parameters
// ********************************

if ( startnumber == null )	startnumber = "0";

String str_customer = (String) session.getValue("customer");
String str_company  = (String) session.getValue("company");
String str_datalib  = (String) session.getValue("datalib");
String list_customer = (String) session.getValue("list");
String str_username = (String)session.getValue("username");
String str_password = (String)session.getValue("password");
String str_as400url = (String)session.getValue("as400url");
String str_as400driver = (String)session.getValue("as400driver");
String str_sIncludeCredits = (String)session.getValue("IncludeCredits");  // RI010

// ***********************************
// Format the sort options table
// ***********************************

String webPageName = "payCheckTotal";

String[] returnArray = sortOptionBean.getSortOptions(webPageName, sortFld0, sortFld1, sortFld2, order0, order1, order2);

String str_OrderBy = returnArray[0];
String sortOptionsTable = returnArray[1];
String hiddenSortFields = returnArray[2];

// ********************************
// Download variables
// ********************************

String displayPageName = "Pay Check Total Report";	// RI009

Connection paychecktotalbeanconnection = null;


%>


<BODY BGCOLOR="#ffffff" TEXT="#000000" LINK="#cccccc" VLINK="#666666">

	<div ID=maintext>

		<table border="0" width="650" cellspacing="0" cellpadding="0">
			<tr>
				<td width="300" align="right"><img src="images/goldReports_header.gif" width="300" height="100"></td>
				<td width="100" align="center">&nbsp;</td>
				<td width="250" align="left"><img src="images/HERClogo.gif" width="250" height="100"></td>
			</tr>
		</table>

		<table border="0" width="650" cellspacing="0" cellpadding="0">
			<tr>
				<td background="images/bottom_back.gif" width="25">&nbsp;</td>
				<td background="images/bottom_back.gif" width="595"><!--COMPANY_NAME--><a class="company"><%=str_companyname.trim()%></a>&nbsp;&nbsp;-&nbsp;&nbsp;Account Number: <!--COMPANY_NUMBER--><a class="data"><%=str_customer%></a></td>
				<td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
			</tr>
			<tr>
				<td><img src="images/empty.gif" height="10"></td>
				<td><img src="images/arrow.gif" height="10" align="right"></td>
				<td></td>
			</tr>
			<tr>
				<td><img src="images/empty.gif" height="30"></td>
				<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;payment history&nbsp;&nbsp;&nbsp;</td>
				<td></td>
			</tr>
		</table>

		<br>


		<%

		// ***************************************************
		// Make a new AS400 connection to retrieve  information
		// ***************************************************

		paychecktotalbeanconnection = paychecktotalbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		String str_color    = "#cccccc";

		String[] str_array = paychecktotalbean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_sIncludeCredits);		// RI010
				
		String str_total = str_array[0];
		String str_totalamount = str_array[1];  

		int num_display = 1000;
		int num_count = 0;
		int num_total = Integer.valueOf(str_total).intValue();
		int num_current = Integer.valueOf(startnumber).intValue();
		int num_nextstart = num_current + num_display;
		int num_prevstart = num_current - num_display;

		if ( num_prevstart <= 0 )
			num_prevstart = 0;

		// ***************************************************
		//  RI009 - Setup the data list array used for the download.  
		//  The first line is reserved for column heading
		// ***************************************************
		
		String dataList [][] = new String[num_total+1][5];		// RI009

		dataList[0][0] = "Account#";		// RI009
		dataList[0][1] = "Account Name";	// RI009	
		dataList[0][2] = "Pay Date";		// RI009
		dataList[0][3] = "Check#";			// RI009
		dataList[0][4] = "Total";			// RI009
		
		// ****************************************************************
		// RI009 - Attempt to retrieve session objects for this report
		// ****************************************************************

		String sesDataList[][] = (String [][])session.getAttribute("dataList");		// RI009
		String reportName      = (String)session.getAttribute("reportName");		// RI009
		boolean loadDataList   = false;												// RI009

		if ( num_current == 0 || sesDataList == null || reportName == null || ( reportName != null && !reportName.equals(webPageName) ) )	// RI009
		{ 
			session.removeAttribute("dataList");		// RI009
			session.removeAttribute("reportName");		// RI009
			loadDataList = true;						// RI009
		}												// RI009
		else											// RI009
		{												// RI009
			dataList = sesDataList;						// RI009
		}												// RI009

		// *****************************************************************
		// RI009 - Add the table that will position the download button
		// *****************************************************************	

		if (num_total > 0 ) { 		// RI009

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
		}    // RI009
		%>
		
		<center>
		
			<table cellpadding="3" cellspacing="1" border="0" width="650">

				<%	
				// *********************************************
				// Output the sort options table
				// *********************************************
	
				if (num_total > 0)  {  
				%>
					<Form action="payCheckTotal.jsp"  name="sortFrm" method=POST  onsubmit=" return validate();">
						<tr bgcolor="#999999">
							<td colspan="3" align="center">
								<%=sortOptionsTable%>
							</td>
						</tr>
					</Form>
				<%	
				} 
				%>
				<tr>
					<td background="images/empty.gif" bgcolor="#000000" align="left" width="30%" class="whitemid">Pay Date</td>
					<td background="images/empty.gif" bgcolor="#000000" align="left" width="35%" class="whitemid">Check#</td>
					<td bgcolor="#000000" background="images/empty.gif" align="right" width="35%" class="whitemid">Total</td>
				</tr>

				<%

				// *************************************************************
				//  RI009 - Determine no of records to be retrieved and processed.
				// *************************************************************	

				int num_start = 0;					// RI009
							
				if  ( !loadDataList )				// RI009
						num_start = num_current;	// RI009
						
					
				if ( paychecktotalbean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_start, str_OrderBy, str_sIncludeCredits) ) {		// RI010

					// ********************************************************************************
					// RI009 - Break out of the loop if the data list has already been loaded
					//         and the page record limit has been reached
					// ********************************************************************************
					
					// RI009 -   while ( paychecktotalbean.getNext() && num_count < num_display) {
					

					while ( paychecktotalbean.getNext() ) {		// RI009
					
						if ( (num_count >= num_display) && !loadDataList )	// RI009
							break;											// RI009
						
						num_count++;

						if ( str_color.equals("#cccccc") )
							str_color = "#999999";
						else 
							str_color = "#cccccc";
 
						String str_checkNumber = paychecktotalbean.getColumn("ADPID#").trim();
						String str_checkno = "%20";
 
						if  ( !str_checkNumber.equals("") )
							str_checkno = str_checkNumber;

						String str_paydate = paychecktotalbean.getColumn("ADSYSD");
 
						if ( !str_paydate.trim().equals("0") && str_paydate.length() == 8)  {
						
							str_paydate = str_paydate.substring(4, 6) + "/" + str_paydate.substring(6, 8) + "/" + str_paydate.substring(2, 4);

						} else {
						
							str_paydate = "";
     					}
     					
						// **************************************************************
						// RI009 - Add the detail to the two-dimentional array used later to output to a CSV file
						//         Ensure that null values are changes to blanks
						// **************************************************************
						
						dataList[num_count][0] = str_customer;										// RI009
						dataList[num_count][1] = str_companyname;									// RI009
						dataList[num_count][2] = str_paydate;										// RI009
						dataList[num_count][3] = str_checkNumber;									// RI009
						dataList[num_count][4] = "$" + paychecktotalbean.getColumn("SubTotal");		// RI009

						for(int j = 0; j < dataList[num_count].length; j++)					// RI009
						{																	// RI009
							if ( dataList[num_count][j] == null )							// RI009
								dataList[num_count][j] = "";								// RI009
						}																	// RI009
						
						// *******************************************************
						// RI009 - show only the maximum records per page
						// *******************************************************
						 
						if( (num_count + num_current) > num_current && (num_count + num_current) <= num_nextstart)		// RI009
						{				
					
					%>

							<tr>
								<form action="paymentHist.jsp" method=POST>
									<input type="hidden" name="paydate" value="<!--PAY_DATE--><%=paychecktotalbean.getColumn("ADSYSD")%>">
									<input type="hidden" name="checknum" value="<!--CHECK_NUM--><%=str_checkNumber%>">
									<input type="hidden" name="subtotal" value="<!--SUB_TOTAL--><%=paychecktotalbean.getColumn("SubTotal")%>">
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" width="30%" class="tabledata"><a href="paymentHist.jsp?checknumber=<%=str_checkno.trim()%>&paydate=<%=paychecktotalbean.getColumn("ADSYSD")%>&subtotal=<%=paychecktotalbean.getColumn("SubTotal")%>" onClick="submit();"><!--PAY_DATE--><%=str_paydate%></a>&nbsp;</td>
								</form>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" width="35%" class="tabledata"><!--CHECK_NUM--><%=str_checkNumber%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top" width="35%" class="tabledata"><!--TOTAL-->$<%=paychecktotalbean.getColumn("SubTotal")%>&nbsp;</td>
							</tr>

				<%
						}  // RI009
					}
				}
			
				// ******************************************************************************************************
				// RI009 - place the two-dimentional array in a session object to be retrieved in the download servlet
				// ******************************************************************************************************
			
				if  ( loadDataList )										// RI009
				{															// RI009
					session.setAttribute("dataList", dataList );			// RI009
					session.setAttribute("reportName", webPageName );		// RI009
				}															// RI009
				
				%>

			</table>


         <table border="0" width="650" cellspacing="0" cellpadding="0">

           <tr>
                   <td align="right" colspan="3" class="tableheader3">Grand Total: <!--GRAND_TOTAL-->$<%=str_totalamount%>&nbsp;</td>
            </tr>

             <tr>
                <td background="images/empty.gif" bgcolor="#ffffff" width="515" align="left" valign="top" class="tableheader3"><!--NUM_RECORDS-->&nbsp;

                    <%   if ( num_total > 0 ) {    %>

                                <%=num_current+1%> - 

                                <%   if ( num_nextstart < num_total ) {    %>
                                                 <%=num_nextstart%> of 
                                <%   } else {   %>
                                                <%=num_total%> of 
                                <%   }
                              }
                               %>

                    <%=str_total%> record(s).</td>

                <td align="right" width="90">
                   <%   if ( num_prevstart >= 0 && num_current != 0 ) {    %>
                              <form action="payCheckTotal.jsp" method=POST> 
                              		<input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
                              		<%=hiddenSortFields%>
									<input type=image src="images/prev.gif" height="40" width="87" border="0">
                              </form>
                   <%  }    %>
                </td>

                <td width="65"> 
                   <%  if ( num_nextstart < num_total ) {    %>
                                 <form action="payCheckTotal.jsp" method=POST> 
                                 	<input type="hidden" name="startnumber" value="<%=num_nextstart%>">
                                 	<%=hiddenSortFields%>
                                    <input type=image src="images/next.gif" height="40" width="62" border=0> 
                                 </form>
                    <% }  %>
                </td>
           </tr>

         </table>

      </center>


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

paychecktotalbean.cleanup();	// RI010
paychecktotalbean.endcurrentConnection(paychecktotalbeanconnection);
%>

</BODY>
</HTML>
