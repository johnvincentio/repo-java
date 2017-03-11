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
//    Index    User Id        Date          Project                       Desciption
//    ------  -----------  -----------  ------------------  ----------------------------------------------------------
//    x001     DTC9028      11/14/02     SR26609 PCR1        Made changes related to new log-in logic
//    x002     DTC9028      11/14/02     SR26609 PCR1        Made changes end the connection
//    x003     DTC9028      12/14/02     SR26609 PCR1        Made changes to close browser window
//    x004     DTC9028      12/23/02     SR26609 PCR1        Added account number to the table
//    x005     DTC9028      11/26/02     SR26609 PCR1        Incorporate Hertz images
//    x006     DTC9028      11/26/02     SR26609 PCR1        Changed browser title bar
//    x007     DTC9028      01/24/03     SR26609 PCR1        Add the contract number to the table
//    x008     DTC9028      02/11/03     SR26609 PCR1        Remove the RHJOBL column
//    x009     DTC9028      02/11/03     SR26609 PCR1        Remove the grand total at the bottom of the page
//    x010     DTC9028      04/28/03     SR28586 PCR1        Add logic to check security for a customer rep
//    x011     DTC2073      08/19/04     SR31514 PCR5        Add sort options
//    RI012    DTC2073      02/01/05     SR31413 PCR26       Add copyright year dynamically to the page
//    RI013    DTC2073      05/06/05     SR31413 PCR24       Data download feature
//    RI014    DTC9028      07/27/05     SR31413 PCR19       Implement datasource modifications
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
<jsp:useBean id="monthlybillingsbean" class="etrieveweb.etrievebean.monthlybillingsBean" scope="page" />
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
Connection monthlybillingsbeanconnection = null;

//*************************************************************
// Retrieve today's date to be used for copyright statment.
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


if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U71"))
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

if ( startnumber == null )	startnumber = "0";
if ( jobnumber == null )	jobnumber = "";

// **********************
// End current connection
//**********************

MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);

// ***********************************
// Format the sort options table
// ***********************************

String webPageName = "monthlyBillings";

String[] returnArray = sortOptionBean.getSortOptions(webPageName, sortFld0, sortFld1, sortFld2, order0, order1, order2);

String str_OrderBy = returnArray[0];
String sortOptionsTable = returnArray[1];
String hiddenSortFields = returnArray[2];

// ********************************
// Download variables
// ********************************

String displayPageName = "Automated Monthly Billings Report";	// RI013

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
				<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;monthly billings&nbsp;&nbsp;&nbsp;</td>
				<td></td>
			</tr>

		</table>

		<br>

		<%

		// ***************************************************
		// Make a new AS400 connection to retrieve information
		// ***************************************************

		monthlybillingsbeanconnection = monthlybillingsbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		String str_color    = "#cccccc";
		/*String str_customer = (String) session.getValue("customer");
		String str_company  = (String) session.getValue("company");
		String str_datalib  = (String) session.getValue("datalib");
		String list_customer = (String) session.getValue("list");*/
      
		String str_contract = "";
			
		String[] str_array = monthlybillingsbean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, jobnumber);

		String str_total = str_array[0];
		String str_totalamount = str_array[1];  

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
		
		String dataList [][] = new String[num_total+1][10];		// RI013

		dataList[0][0] = "Account#";		// RI013
		dataList[0][1] = "Account Name";	// RI013
		dataList[0][2] = "Invoice";			// RI013
		dataList[0][3] = "Contract#";		// RI013
		dataList[0][4] = "Type";			// RI013
		dataList[0][5] = "Start Date";		// RI013
		dataList[0][6] = "Return Date";		// RI013
		dataList[0][7] = "P.O. #";			// RI013
		dataList[0][8] = "Loc";				// RI013
		dataList[0][9] = "Amount";			// RI013
		
		// ****************************************************************
		// RI013 - Attempt to retrieve session objects for this report
		// ****************************************************************

		String sesDataList[][] = (String [][])session.getAttribute("dataList");		// RI013
		String reportName      = (String)session.getAttribute("reportName");		// RI013
		boolean loadDataList   = false;												// RI013

		if ( num_current == 0 || sesDataList == null || reportName == null || ( reportName != null && !reportName.equals(webPageName) ) )	// RI010
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
		
		<table cellpadding="3" cellspacing="1" border="0" width="665">

			<%	
			// *********************************************
			// Output the sort options table
			// *********************************************
	
			if (num_total > 0)  {  
			%>
				<Form action="monthlyBillings.jsp"  name="sortFrm" method=POST  onsubmit=" return validate();">
					<tr bgcolor="#999999">
						<td colspan="8"  align="center"> 
							<input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
							<%=sortOptionsTable%>
						</td>
					</tr>
				</Form>
			<%	
			} 
			%>
		
			<tr>
				<td background="images/empty.gif" bgcolor="#000000" align="center" width="13%" class="whitemid">Invoice</td>
				<td background="images/empty.gif" bgcolor="#000000" align="center" width="10%" class="whitemid">Contract#</td>
				<td background="images/empty.gif" bgcolor="#000000" align="center" width="8%"  class="whitemid">Type</td>
				<td background="images/empty.gif" bgcolor="#000000" align="right"  width="9%"  class="whitemid">Start<br>Date</td>
				<td background="images/empty.gif" bgcolor="#000000" align="right"  width="9%"  class="whitemid">Return<br> Date</td>
				<td background="images/empty.gif" bgcolor="#000000" align="left"   width="34%" class="whitemid">P.O. #</td>
				<td background="images/empty.gif" bgcolor="#000000" align="center" width="5%"  class="whitemid">Loc</td>
				<td background="images/empty.gif" bgcolor="#000000" align="right"  width="12%" class="whitemid">Amount</td>
			</tr>


		<%

			// *************************************************************
			//  RI013 - Determine no of records to be retrieved and processed.
			// *************************************************************

			int num_start = 0;					// RI013
							
			if  ( !loadDataList )				// RI013
					num_start = num_current;	// RI013
						
			if ( monthlybillingsbean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_start, jobnumber, str_OrderBy) ) {    // RI013

				// ********************************************************************************
				// RI013 - Break out of the loop if the data list has already been loaded
				//         and the page record limit has been reached
				// ********************************************************************************
					
				// RI013 -   while ( monthlybillingsbean.getNext() && num_count < num_display) {


				while ( monthlybillingsbean.getNext() ) {		// RI013
					
					if ( (num_count >= num_display) && !loadDataList )	// RI013
						break;											// RI013
			
			
					num_count++;

					if ( str_color.equals("#cccccc") )
						str_color = "#999999";
					else 
						str_color = "#cccccc";
    
					str_contract = monthlybillingsbean.getColumn("RHCON#");
					String str_sequence = monthlybillingsbean.getColumn("RHISEQ");

					while( str_sequence.length() < 3 ) {
						str_sequence = "0" + str_sequence;
					}

					// **************
					// Start Date
					// **************

					String str_startdate = monthlybillingsbean.getColumn("RHDATO");

					if ( !str_startdate.trim().equals("0") && str_startdate.length() == 8)  {
					
						str_startdate = str_startdate.substring(4, 6) + "/" + str_startdate.substring(6, 8) + "/" + str_startdate.substring(2, 4);

					} else {
					
						str_startdate = "";

					}
					
					// **************
					// Return Date
					// **************
					
					String str_returndate = monthlybillingsbean.getColumn("RHLRDT");

					if ( !str_returndate.trim().equals("0") && str_returndate.length() == 8)  {
					
						str_returndate = str_returndate.substring(4, 6) + "/" + str_returndate.substring(6, 8) + "/" + str_returndate.substring(2, 4);

					} else  {
						str_returndate = "";
					}


					String str_type = "UNKNOWN";
					float num_amount = Float.valueOf(monthlybillingsbean.getColumn("RHAMT$")).floatValue();
 

					if ( monthlybillingsbean.getColumn("RHTYPE").equals("O") )
						str_type = "RNTL BILLED";
					else if ( monthlybillingsbean.getColumn("RHTYPE").equals("F") ) 
						str_type = "RNT PUR BILL";
					else 
						str_type = "UNKNOWN";


					// **************************************************************
					// RI013 - Add the detail to the two-dimentional array used later to output to a CSV file
					//         Ensure that null values are changes to blanks
					// **************************************************************

					dataList[num_count][0] = str_customer;									// RI013
					dataList[num_count][1] = str_companyname;								// RI013
					dataList[num_count][2] = str_contract +"-"+str_sequence;				// RI013
					dataList[num_count][3] = str_contract;									// RI013
					dataList[num_count][4] = str_type;										// RI013
					dataList[num_count][5] = str_startdate;									// RI013
					dataList[num_count][6] = str_returndate;								// RI013
					dataList[num_count][7] = monthlybillingsbean.getColumn("RHPO#");		// RI013
					dataList[num_count][8] = monthlybillingsbean.getColumn("RHLOC");		// RI013
					dataList[num_count][9] = "$" + monthlybillingsbean.getColumn("RHAMT$");	// RI013

					for(int j = 0; j < dataList[num_count].length; j++)					// RI013
					{																	// RI013
						if ( dataList[num_count][j] == null )							// RI013
							dataList[num_count][j] = "";								// RI013
					}																	// RI013
						
					// *******************************************************
					// RI013 - show only the maximum records per page
					// *******************************************************
						 
					if( (num_count + num_current) > num_current && (num_count + num_current) <= num_nextstart)		// RI010
					{		
						
			%>

						<tr>
							<form action="contractDetail.jsp" method=POST>
								<input type="hidden" name="contract" value="<!--CONTRACT--><%=str_contract%>">
								<input type="hidden" name="sequence" value="<!--SEQUENCE--><%=str_sequence%>">
								<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><a href="contractDetail.jsp?contract=<%=str_contract%>&sequence=<%=str_sequence%>" onClick="submit();"><!--INVOICE--><%=str_contract%>-<%=str_sequence%></a></td>
							</form>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--CONTRACT#--><%=str_contract%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!--TYPE--><%=str_type%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top" class="tabledata"><!--START_DATE--><%=str_startdate%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top" class="tabledata"><!--RTRN_DATE--><%=str_returndate%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata"><!--PO_NUM--><%=monthlybillingsbean.getColumn("RHPO#")%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top" class="tabledata"><!--LOCATION--><%=monthlybillingsbean.getColumn("RHLOC")%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top" class="tabledata"><!--AMOUNT-->$<%=monthlybillingsbean.getColumn("RHAMT$")%>&nbsp;</td>
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

   <table cellpadding="0" cellspacing="0" border="0" width="650">

       <% 
           // Do not show the grand total at the bottom of the page
           str_totalamount = ""; 
           if  (!str_totalamount.equals("") )   {
        %>
               <tr>
                  <td colspan="3" align="right" class="tableheader3">Total: <!--TOTAL_AMT-->$<%=str_totalamount%>&nbsp;</td>
               </tr>
        <%  }  %>

      <tr>
         <td width="515" align="left" valign="top" class="tableheader3">
         <!--NUM_RECORDS-->&nbsp;
         <%   if ( num_total > 0 ) {  %>
                        <%=num_current+1%> - 
                        <%   if ( num_nextstart < num_total ) {    %>
                                      <%=num_nextstart%> of 
                        <%   }else {   %>
                                      <%=num_total%> of 
                        <%   }
                   }
                   %>
           <%=str_total%> record(s).
         </td>

         <td align="right" width="90">
              <%  if ( num_prevstart >= 0 && num_current != 0 ) {     %>
                            <form action="monthlyBillings.jsp" method=POST> 
                                   <input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
                                   <input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
                                   	<%=hiddenSortFields%>
                                   <input type=image src="images/prev.gif" height="40" width="87" border="0">
                            </form>
              <%   }   %>
          </td>

          <td width="65"> 
              <%    if ( num_nextstart < num_total ) {     %>
                         <form action="monthlyBillings.jsp" method=POST> 
                              <input type="hidden" name="startnumber" value="<%=num_nextstart%>">
                              <input type="hidden" name="jobnumber" value= "<%=jobnumber%>"> 
                              <%=hiddenSortFields%>
                              <input type=image src="images/next.gif" height="40" width="62" border=0> 
                         </form>
               <%  }    %>
           </td>
       </tr>

   </table>
   
  

   <form action="jobList.jsp" method=POST>
        <input type="hidden" name="reporttype" value="monthlybillings">
        <a href="javascript:history.go(-1)"><IMG src="images/back.gif" height="40" width="62" border="0"></a> 
        <a href="menu.jsp"><IMG src="images/menu.gif" height="40" width="62" border="0"></a> 
        <a href="javascript:window.close()"><IMG src="images/closeReports.gif" height="40" width="123" border="0"></a> 
        <a href="javascript:window.print()"><img src="images/print.gif" width="67" height="40" border="0"></a>
        <input type=image src="images/viewByJob.gif" height="40" width="100" border=0>
   </form>


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
  monthlybillingsbean.cleanup();	// RI014
  monthlybillingsbean.endcurrentConnection(monthlybillingsbeanconnection);

%>

</BODY>
</HTML>
