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
//    Index     User Id       Date       Project                       Desciption
//   --------  ----------  -----------  ---------------  ----------------------------------------------------------
//    x001      DTC9028     11/14/02     SR26609 PCR1     Made changes related to new log-in logic
//    x002      DTC9028     11/14/02     SR26609 PCR1     Made changes end the connection
//    x003      DTC9028     12/14/02     SR26609 PCR1     Made changes to close browser window
//    x004      DTC9028     12/23/02     SR26609 PCR1     Added account number to the header
//    x005      DTC9028     11/26/02     SR26609 PCR1     Incorporate Hertz images
//    x006      DTC9028     11/26/02     SR26609 PCR1     Change the title bar description
//    x007      DTC9028     04/28/03     SR28586 PCR1     Add logic to check security for a customer rep
//    x008      DTC2073     08/19/04     SR31413 PCR5     Add sort options to the page.  
//    x009      DTC2073     02/01/05     SR31413 PCR26    Add copyright year dynamically to the page
//    RI010     DTC2073     05/06/05     SR31413 PCR24    Data download feature
//    RI011     DTC9028     08/01/05     SR31413 PCR19    Datasource implementation
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


<%@ page language="java" import="java.sql.*, java.util.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="paymenthistorybean" class="etrieveweb.etrievebean.paymenthistoryBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />
<jsp:useBean id="sortOptionBean" class="etrieveweb.etrievebean.sortOptionBean" scope="page" />


<%
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

if ( loginuserid == null )
	loginuserid = "";

Connection MenuSecurityBeanconnection = null;
Connection paymenthistorybeanconnection = null;

//*************************************************************
// RI009 - Retrieve today's date to be used for copyright statment.
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

MenuSecurityBeanconnection = MenuSecurityBean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U80"))
{
	response.sendRedirect("menu.jsp");
}

// ************************
// Get input parameters
// ************************

String str_companyname = (String) session.getValue("companyname");

String startnumber = request.getParameter("startnumber");
String str_checknumber = request.getParameter("checknumber");
String str_paydatein = request.getParameter("paydate");
String str_subtotal = request.getParameter("subtotal");

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
if ( str_checknumber == null )	str_checknumber = "";
if ( str_paydatein == null )	str_paydatein = "0";
if ( str_subtotal == null )	str_subtotal = "0";

// ***********************************
// Format the sort options table
// ***********************************

String webPageName = "paymentHist";

String[] returnArray = sortOptionBean.getSortOptions(webPageName, sortFld0, sortFld1, sortFld2, order0, order1, order2);

String str_OrderBy = returnArray[0];
String sortOptionsTable = returnArray[1];
String hiddenSortFields = returnArray[2];

// ********************************
// Download variables
// ********************************

String displayPageName = "Payment History Report";		// RI010


// **********************
// End current connection
//**********************

MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);

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
		// Make a new AS400 connection to retrieve information
		// ***************************************************

		paymenthistorybeanconnection = paymenthistorybean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		String str_color    = "#cccccc";
            
        /*String str_customer = (String) session.getValue("customer");
        String str_company  = (String) session.getValue("company");
        String str_datalib  = (String) session.getValue("datalib");
        String list_customer = (String) session.getValue("list");*/

		String str_total = paymenthistorybean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, Integer.valueOf(str_paydatein).intValue(), str_checknumber);

		int num_display = 1000;
		int num_count = 0;
		int num_total = Integer.valueOf(str_total).intValue();
		int num_current = Integer.valueOf(startnumber).intValue();
		int num_nextstart = num_current + num_display;
		int num_prevstart = num_current - num_display;

		if ( num_prevstart <= 0 )
			num_prevstart = 0;

		// ***************************************************
		//  RI010 - Setup the data list array used for the download.  
		//  The first line is reserved for column heading
		// ***************************************************
		
		String dataList [][] = new String[num_total+1][9];		// RI010
		
		dataList[0][0] = "Account#";		// RI010
		dataList[0][1] = "Account Name";	// RI010
		dataList[0][2] = "Pay Date";		// RI010
		dataList[0][3] = "Check#";			// RI010
		dataList[0][4] = "Amount";			// RI010
		dataList[0][5] = "Invoice#";		// RI010
		dataList[0][6] = "Inv Date";		// RI010
		dataList[0][7] = "Loc";				// RI010
		dataList[0][8] = "Adj Type";		// RI010
		
		// ****************************************************************
		// RI010 - Attempt to retrieve session objects for this report
		// ****************************************************************

		String sesDataList[][] = (String [][])session.getAttribute("dataList");		// RI010
		String reportName      = (String)session.getAttribute("reportName");		// RI010
		boolean loadDataList   = false;												// RI010

		if ( num_current == 0 || sesDataList == null || reportName == null || ( reportName != null && !reportName.equals(webPageName) ) )	// RI010
		{ 
			session.removeAttribute("dataList");		// RI010
			session.removeAttribute("reportName");		// RI010
			loadDataList = true;						// RI010
		}												// RI010
		else											// RI010
		{												// RI010
			dataList = sesDataList;						// RI010
		}												// RI010

		// *****************************************************************
		// RI010 - Add the table that will position the download button
		// *****************************************************************	

		if (num_total > 0 ) { 		// RI010
		

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
		}    // RI010
		%>
		
		
		<center>
		
			<table cellpadding="3" cellspacing="1" border="0" width="650">

				<%	
				// *********************************************
				// Output the sort options table
				// *********************************************
	
				if (num_total > 0)  {  
				%>
					<Form action="paymentHist.jsp"  name="sortFrm" method=POST  onsubmit=" return validate();"> 
						<tr bgcolor="#999999">
							<td colspan="7" align="center">
								<input type="hidden" name="checknumber"  value="<%=str_checknumber%>"> 
								<input type="hidden" name="paydate" value="<%=str_paydatein%>"> 
								<input type="hidden" name="subtotal" value="<%=str_subtotal%>"> 
								<%=sortOptionsTable%>
							</td>
						</tr>
					</Form>
				<%	
				} 
				%>

				<tr>
					<td background="images/empty.gif" bgcolor="#000000" align="left"  width="10%" class="whitemid">Pay Date</td>
					<td background="images/empty.gif" bgcolor="#000000" align="left"  width="10%" class="whitemid">Check#</td>
					<td background="images/empty.gif" bgcolor="#000000" align="right" width="10%" class="whitemid">Amount</td>
					<td bgcolor="#000000" background="images/empty.gif" align="right" width="15%" class="whitemid">Invoice#</td>
					<td bgcolor="#000000" background="images/empty.gif" align="right" width="10%" class="whitemid">Inv Date</td>
					<td bgcolor="#000000" background="images/empty.gif" align="right" width="10%" class="whitemid">Loc</td>
					<td bgcolor="#000000" background="images/empty.gif" align="right" width="35%" class="whitemid">Adj Type</td>
				</tr>


		<%
		
				// *************************************************************
				//  RI010 - Determine no of records to be retrieved and processed.
				// *************************************************************

				int num_start = 0;					// RI010
							
				if  ( !loadDataList )				// RI010
						num_start = num_current;	// RI010
						
				if ( paymenthistorybean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_start, Integer.valueOf(str_paydatein).intValue(), str_checknumber, str_OrderBy) ) {    // RI010

					// ********************************************************************************
					// RI010 - Break out of the loop if the data list has already been loaded
					//         and the page record limit has been reached
					// ********************************************************************************
					
					// RI010 -  while ( paymenthistorybean.getNext() && num_count < num_display) {

					while ( paymenthistorybean.getNext() ) {		// RI010
					
						if ( (num_count >= num_display) && !loadDataList )	// RI010
							break;											// RI010
							
						num_count++;

						if ( str_color.equals("#cccccc") )
							str_color = "#999999";
						else 
							str_color = "#cccccc";
 
						String str_contract = paymenthistorybean.getColumn("ADINV#"); 
						String str_sequence = paymenthistorybean.getColumn("ADISEQ");

						while( str_sequence.length() < 3 ) {
							str_sequence = "0" + str_sequence;
						}

						// ********************
						// Pay date
						// ********************
						
						String str_paydate = paymenthistorybean.getColumn("ADSYSD");

						if ( !str_paydate.trim().equals("0") && str_paydate.length() == 8)  {
						
							str_paydate = str_paydate.substring(4, 6) + "/" + str_paydate.substring(6, 8) + "/" + str_paydate.substring(2, 4);

						} else {
						
							str_paydate = "";
							
						}

						// *******************
						// Invoice date
						// *******************

						String str_invdate = paymenthistorybean.getColumn("AHINVD");

						if ( !str_invdate.trim().equals("0") && str_invdate.length() == 8)  {
		
							str_invdate = str_invdate.substring(4, 6) + "/" + str_invdate.substring(6, 8) + "/" + str_invdate.substring(2, 4);

						} else {
						
							str_invdate = "";
							
						}

						String str_adjdesc = "";
						String str_prefix = "";
						String str_adjtype = paymenthistorybean.getColumn("ADTYPE");
						boolean userdefineyn = false;

						if (paymenthistorybean.getColumn("ADRCDC").equals("A") && paymenthistorybean.getColumn("ADSRC").equals("A") ) {
							str_prefix = "RVRS-";
						}

						if ( str_adjtype.equals("WO") && paymenthistorybean.getColumn("ADRCDC").equals("A") ) {
							str_adjdesc="WRITE OFF";
							str_prefix="";
						}
						else if ( str_adjtype.equals("NS") )   
							str_adjdesc="NSF REVERSE";
						else if ( str_adjtype.equals("MA") )   
							str_adjdesc="MIS-APPLIED";
						else if ( str_adjtype.equals("RV") )   
							str_adjdesc="REVERSAL";
						else if ( str_adjtype.equals("AT") )   
							str_adjdesc="ACT TRANSFER";
						else if ( str_adjtype.equals("CP") )   
							str_adjdesc="CSH TRANS";
						else if ( str_adjtype.length() > 1 )
							userdefineyn = true;

						if ( userdefineyn && paymenthistorybean.getColumn("ADRCDC").equals("A") ) 
							str_adjdesc=paymenthistorybean.getDescription(Integer.valueOf(str_customer).intValue(), str_company, str_datalib, paymenthistorybean.getColumn("ADLOC"), str_adjtype);
 
						str_adjdesc = str_prefix + str_adjdesc;

						// **************************************************************
						// RI010 - Add the detail to the two-dimentional array used later to output to a CSV file
						//         Ensure that null values are changes to blanks
						// **************************************************************
						
						dataList[num_count][0] = str_customer;									// RI010
						dataList[num_count][1] = str_companyname;								// RI010
					    dataList[num_count][2] = str_paydate;									// RI010
					    dataList[num_count][3] = str_checknumber;								// RI010
					    dataList[num_count][4] = "$" + paymenthistorybean.getColumn("Amount");	// RI010
					    dataList[num_count][5] = str_contract+"-"+str_sequence ;				// RI010
					    dataList[num_count][6] = str_invdate;									// RI010
					    dataList[num_count][7] = paymenthistorybean.getColumn("ADLOC");			// RI010
					    dataList[num_count][8] = str_adjdesc;									// RI010

						for(int j = 0; j < dataList[num_count].length; j++)					// RI010
						{																	// RI010
							if ( dataList[num_count][j] == null )							// RI010
								dataList[num_count][j] = "";								// RI010
						}																	// RI010
						
						// *******************************************************
						// RI010 - show only the maximum records per page
						// *******************************************************
						 
						if( (num_count + num_current) > num_current && (num_count + num_current) <= num_nextstart)		// RI010
						{		
						%>

							<tr>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top"  class="tabledata"><!--PAY_DATE--><%=str_paydate%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top"  class="tabledata"><!--CHECK_NUM--><%=str_checknumber%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top"  class="tabledata"><!--AMOUNT-->$<%=paymenthistorybean.getColumn("Amount")%>&nbsp;</td>
								<form action="contractDetail.jsp" method=POST>
									<input type="hidden" name="contract" value="<!--CONTRACT--><%=str_contract%>">
									<input type="hidden" name="sequence" value="<!--SEQUENCE--><%=str_sequence%>">
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top"  class="tabledata"><a href="contractDetail.jsp?contract=<%=str_contract%>&sequence=<%=str_sequence%>" onClick="submit();"><!--INVOICE--><%=str_contract%>-<%=str_sequence%></a>&nbsp;</td>
								</form>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top"  class="tabledata"><!--INV_DATE--><%=str_invdate%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top"  class="tabledata"><!--LOCATION--><%=paymenthistorybean.getColumn("ADLOC")%>&nbsp;</td>
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top"  class="tabledata"><!--ADJ_TYPE--><%=str_adjdesc%>&nbsp;</td>
							</tr>

				<%
						}
					}
				}
				
				// ******************************************************************************************************
				// RI010 - place the two-dimentional array in a session object to be retrieved in the download servlet
				// ******************************************************************************************************
				
				if  ( loadDataList )										// RI010
				{															// RI010
					session.setAttribute("dataList", dataList );			// RI010
					session.setAttribute("reportName", webPageName );		// RI010
				}															// RI010
				
			%>

			</table>

            <table border="0" width="650" cellspacing="0" cellpadding="0">

               <tr>
                   <td colspan="3" align="right" class="tableheader3">Grand Total: <!--GRAND_TOTAL-->$<%=str_subtotal%>&nbsp;</td>
               </tr>

               <tr>
                   <td width="515" background="images/empty.gif" bgcolor="#ffffff" align="left" valign="top" class="tableheader3"><!--NUM_RECORDS-->&nbsp;

                          <%  if ( num_total > 0 ) {  %>
                                         <%=num_current+1%> - 
                                         <%    if ( num_nextstart < num_total ) {    %>
                                                         <%=num_nextstart%> of 
                                         <%    }else {   %>
                                                         <%=num_total%> of 
                                        <%    }
                                   }
                                %>

                            <%=str_total%> record(s).
                   </td>

                   <td align="right" width="90">
                            <%   if ( num_prevstart >= 0 && num_current != 0 ) {    %>
                                              <form action="paymentHist.jsp" method=POST> 
                                                    <input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
                                                    <input type="hidden" name="checknumber"  value="<%=str_checknumber%>"> 
                                                    <input type="hidden" name="paydate"  value="<%=str_paydatein%>"> 
                                                    <input type="hidden" name="subtotal" value="<%=str_subtotal%>"> 
                                                    <%=hiddenSortFields%>
                                                    <input type=image src="images/prev.gif" height="40" width="87" border="0">
                                              </form>
                              <%  }    %>
                      </td>

                       <td width="65"> 
                              <%   if ( num_nextstart < num_total ) {    %>
                                             <form action="paymentHist.jsp" method=POST> 
                                                   <input type="hidden" name="startnumber" value="<%=num_nextstart%>">
                                                   <input type="hidden" name="checknumber" value="<%=str_checknumber%>"> 
                                                   <input type="hidden" name="paydate"     value="<%=str_paydatein%>"> 
                                                   <input type="hidden" name="subtotal"    value="<%=str_subtotal%>"> 
                                                   <%=hiddenSortFields%>
                                                   <input type=image src="images/next.gif" height="40" width="62" border=0> 
                                              </form>
                                   <%  }    %>
                         </td>
               </tr>

            </table>

      </center>

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

paymenthistorybean.cleanup();	// RI011
paymenthistorybean.endcurrentConnection(paymenthistorybeanconnection);


%>

</BODY>
</HTML>

