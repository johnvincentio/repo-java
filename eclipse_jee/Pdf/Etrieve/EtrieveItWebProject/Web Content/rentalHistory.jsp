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
//    Index     User Id       Date        Project             Desciption
//   --------  ----------  -----------  -----------------   ---------------------------------------------------
//    x001      DTC9028     11/26/02     SR26609 PCR1        Modification for the new log-in process
//    x002      DTC9028     11/26/02     SR26609 PCR1        Ensure that connection are ended 
//    x003      DTC9028     12/26/02     SR26609 PCR1        Changed the "log out" to "close reports"
//    x004      DTC9028     12/26/02     SR26609 PCR1        Changed the page title 
//    x005      DTC9028     12/26/02     SR26609 PCR1        Changed the copy right text 
//    x006      DTC9028     12/26/02     SR26609 PCR1        Add the account number
//    x007      DTC9028     04/28/03     SR28586 PCR1        Add logic to check security for a customer rep
//    x008      DTC2073     08/19/04     SR31413 PCR5        Add sort options to the page.  
//    RI009     DTC2073     02/01/05     SR31413 PCR26       Add copyright year dynamically to the page
//    RI010     DTC2073     05/06/05     SR31413 PCR24       Data download feature
//    RI011     DTC9028     08/01/05     SR31413 PCR19       Datasource implementation modification
//    RI012     DTC2073     11/15/05     SR35880             Add year filter
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

<!-- JSP page content header -->
<%@ page language="java" import="java.sql.*, java.util.*"  errorPage="ErrorPage.jsp"%>
 
<jsp:useBean id="historybean" class="etrieveweb.etrievebean.eqhistoryBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />
<jsp:useBean id="sortOptionBean" class="etrieveweb.etrievebean.sortOptionBean" scope="page" />
<jsp:useBean id="yearOptionsBean" class="etrieveweb.etrievebean.yearOptionsBean" scope="page" />

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
Connection historybeanconnection = null;

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

if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U08"))
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

String selectYear = (String) request.getParameter("selectYear"); 	// RI012

// ********************************
// Validate the input parameters
// ********************************

if ( startnumber == null )	startnumber = "0";
if ( jobnumber == null )	jobnumber = ""; 
if ( selectYear == null )	selectYear = Integer.toString(cpYear); 	// RI012

// ***********************************
// Format the sort options table
// ***********************************

String webPageName = "rentalHistory";

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

String displayPageName = "Rental History by Equip Type for " + selectYear;		// RI010

// **********************
// End current connection
//**********************

MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);

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
				<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;rental history by equipment type&nbsp;&nbsp;&nbsp;</td>
				<td></td>
			</tr>
		</table>

		<br>
		
		<%

		// ***************************************************
		// Make a new AS400 connection to retrieve information
		// ***************************************************

		historybeanconnection = historybean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		String str_color    = "#cccccc";
		String str_catg = "";
		String str_class = "";
		String str_description = "";

		String str_total    = historybean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, jobnumber, selectYear);

		String str_totalall = historybean.getNumRowsAll(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, jobnumber, selectYear);

		String str_prevpage = "<a href='rentalHistory.jsp?jobnumber=" + jobnumber + "&startnumber=" + startnumber + "'>Rental History   </a>";

		str_prevpage = java.net.URLEncoder.encode(str_prevpage);

		int num_display = 5000;
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
		
		String dataList [][] = new String[num_total+1][8];		// RI010
		
		dataList[0][0] = "Account#";		// RI010
		dataList[0][1] = "Account Name";	// RI010
		dataList[0][2] = "Cat-Class";		// RI010
		dataList[0][3] = "Description";		// RI010
		dataList[0][4] = "Rental Days";		// RI010
		dataList[0][5] = "# of Trans";		// RI010
		dataList[0][6] = "Rental Amount";	// RI010
		dataList[0][7] = "Rental Year";	// RI012

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

		
		
		%>
			<table border="0" width="900">   
				<tr>
					<Form action="rentalHistory.jsp"  name="yearFilterFrm" method=POST  onsubmit=" return validate();">
					<td width="723" class="tableheader3" align=right" valign="middle">
						<input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
						Show rental history for year:&nbsp;&nbsp;<%=strYearFilter%>	
					</td>
					</form>
					<td width="45" align=right" valign="middle">&nbsp;</td>
					<td width="87" class="footerblack" align="right">
					<% if (num_total > 0 ) { 		// RI010  %>
						<Form action="servlet/EtrieveDownLoadServlet"  method=POST> 
							<input type=image SRC="images/download4.JPG" height="22" width="86" BORDER=0 align="right" valign="bottom">
							<Input TYPE="hidden" name="filename" value = "<%=displayPageName%>" >
						</form>
					<% }  // RI010 %>
					</td>
					<td width="45" class="footerblack" align="left">
						<% if (num_total > 0 ) { 		// RI010  %>
						<class="history"><a href='downloaddetails.jsp'>[details]</a>
						<% }  // RI010 %>
					</td>
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
					<Form action="rentalHistory.jsp"  name="sortFrm" method=POST  onsubmit=" return validate();">
						<tr bgcolor="#999999">
							<td colspan="5"  align="center"> 
								<input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
								<input type="hidden" name="selectYear" value= "<%=selectYear%>">	
								<%=sortOptionsTable%>
							</td>
						</tr>
					</Form>
				<%	
				} 
				%>


				<tr>
					<td background="images/empty.gif" bgcolor="#000000" align="center" width="16%" class="whitemid">Cat-Class</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left" width="40%" class="whitemid">Description</td>
					<td bgcolor="#000000" background="images/empty.gif" align="right" width="12%" class="whitemid">Rental Days</td>
					<td bgcolor="#000000" background="images/empty.gif" align="right" width="12%" class="whitemid"># of Trans</td>
					<td bgcolor="#000000" background="images/empty.gif" align="right" width="20%" class="whitemid">Rental Amount</td>
				</tr>

		
		<%
				// *************************************************************
				//  RI010 - Determine no of records to be retrieved and processed.
				// *************************************************************

				int num_start = 0;					// RI010
							
				if  ( !loadDataList )				// RI010
						num_start = num_current;	// RI010


				if ( historybean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_start, jobnumber, str_OrderBy, selectYear) ) {

					// ********************************************************************************
					// RI010 - Break out of the loop if the data list has already been loaded
					//         and the page record limit has been reached
					// ********************************************************************************

					// RI010 -    while ( historybean.getNext() && num_count < num_display) {

					while ( historybean.getNext() ) {		// RI010
					
						if ( (num_count >= num_display) && !loadDataList )	// RI010
							break;											// RI010
							
						num_count++;

						if ( str_color.equals("#cccccc") )
							str_color = "#999999";
						else 
							str_color = "#cccccc";
 
						// **********************************
						// Format the category/class fields
						// **********************************
   
						str_catg = historybean.getColumn("RDCATG");

						while ( str_catg.length() < 3 ) {
							str_catg = "0" + str_catg;
						}

						str_class = historybean.getColumn("RDCLAS");

						while ( str_class.length() < 4 ) {
							str_class = "0" + str_class;
						}


						str_description = historybean.getColumn("ECDESC").replace('"', ' ');

						// **************************************************************
						// RI010 - Add the detail to the two-dimentional array used later to output to a CSV file
						//         Ensure that null values are changes to blanks
						// **************************************************************
						
						dataList[num_count][0] = str_customer;										// RI010
						dataList[num_count][1] = str_companyname;									// RI010
						dataList[num_count][2] = str_catg + "-" + str_class ;						// RI010
				   		dataList[num_count][3] = historybean.getColumn("ECDESC");					// RI010
				   		dataList[num_count][4] = historybean.getColumn("totalrentdays");			// RI010
				   		dataList[num_count][5] = historybean.getColumn("totalTransactions");		// RI010
				   		dataList[num_count][6] = "$" + historybean.getColumn("totalrentamount");	// RI010
				   		dataList[num_count][7] = selectYear;										// RI012

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

							<form action="detailHistory.jsp" method=POST>

								<tr>
									<input type="hidden" name="catg" value="<%=str_catg%>">
									<input type="hidden" name="class" value="<%=str_class%>">
									<input type="hidden" name="desc" value="<%=str_description%>">
									<input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
									<input type="hidden" name="prevpage" value= "<%=str_prevpage%>">
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" width="16%" class="tabledata"><a href="detailHistory.jsp?selectYear=<%=selectYear%>&catg=<%=str_catg%>&class=<%=str_class%>&desc=<%=str_description.trim()%>&jobnumber=<%=jobnumber%>&prevpage=<%=str_prevpage%>" onClick="submit();"><!--CATG_CLAS--><%=str_catg%>-<%=str_class%></a></td>
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" width="40%" class="tabledata"><!--DESC--><%=historybean.getColumn("ECDESC")%></td>
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top" width="12%" class="tabledata"><!--RENT_DAYS--><%=historybean.getColumn("totalrentdays")%></td>
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top" width="12%" class="tabledata"><!--RENT_NUM--><%=historybean.getColumn("totalTransactions")%></td>
									<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top" width="20%" class="tabledata"><!--RENT_COST-->$<%=historybean.getColumn("totalrentamount")%></td>
								</tr>

							</form>

						<%
						}    // RI010
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


      <table border="0" width="900" cellspacing="0" cellpadding="0">

         <tr>
            <td align="left" width="745" class="tableheader3"><!--NUM_RECORDS--> &nbsp;

               <%   if ( num_total > 0 && num_nextstart < num_total ) {   %>
                             <%=num_current+1%> - 
                             <%   if ( num_nextstart < num_total ) {     %>
                                              <%=num_nextstart%> of 
                             <%  } else {     %>
                                         <%=num_total%> of 
                            <%
                                     }
                         }

                %>

                <%=str_totalall%> record(s) found in <!--NUM_ROWS--> <%=str_total%> category-class(es).
            </td>
            <td align="right" width="90">
                <%    if ( num_prevstart >= 0 && num_current != 0 ) {     %>                           
                                      <form action="rentalHistory.jsp" method=POST> 
                                            <input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
                                            <input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
                                            <%=hiddenSortFields%>
                                            <input type="hidden" name="selectYear" value="<%=selectYear%>">
                                            <input type=image src="images/prev.gif" height="40" width="87" border="0">
                                     </form>                                 
                <%    }    %>
            </td>
            <td width="65"> 
                <%    if ( num_nextstart < num_total ) {    %>
                                    <form action="rentalHistory.jsp" method=POST> 
                                          <input type="hidden" name="startnumber" value="<%=num_nextstart%>">
                                          <input type="hidden" name="jobnumber" value= "<%=jobnumber%>"> 
                                          <%=hiddenSortFields%>
                                          <input type="hidden" name="selectYear" value="<%=selectYear%>">
                                          <input type=image src="images/next.gif" height=40 width=62 border=0> 
                                    </form>
                 <%  }   %>
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

 historybean.cleanup();		// RI011
 historybean.endcurrentConnection(historybeanconnection);

%>

</BODY>
</HTML>