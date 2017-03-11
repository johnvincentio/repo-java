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
//    Index    User Id     Date       Project            Desciption
//    ------- ---------- -----------  ----------------  ----------------------------------------------------------
//    x001     DTC9028    11/14/02    SR26609 PCR1       Made changes related to new log-in logic
//    x002     DTC9028    11/14/02    SR26609 PCR1       Made changes end the connection
//    x003     DTC9028    12/14/02    SR26609 PCR1       Made changes to close browser window
//    x004     DTC9028    11/26/02    SR26609 PCR1       Added 150 day bucket
//    x005     DTC9028    12/23/02    SR26609 PCR1       Added account number to the header
//    x006     DTC9028    11/26/02    SR26609 PCR1       Incorporate Hertz images
//    x007     DTC9028    11/26/02    SR26609 PCR1       Change the title bar description
//    x008     DTC9028    01/21/03    SR26609 PCR1       Removed the available credit amount 
//    x009     DTC9028    02/07/03    SR26609 PCR1       Changed aging buckets to be retrieved from detail file 
//    x010     DTC9028    02/11/03    SR26609 PCR1       Remove the yearly comparison box at bottom of page.
//    x011     DTC9028    02/11/03    SR26609 PCR1       Add the curr yr and prev yr billed amount to the header  
//    x012     DTC9028    02/11/03    SR26609 PCR1       Add hyper link to aging buckets to display the detail
//    x013     DTC9028    04/28/03    SR28586 PCR1       Add logic to check security for a customer rep
//    x014     DTC2073    03/07/04    SR28586 PCR30      Remove 'Credit Limit' field from this screen.
//    x015     DTC2073    03/11/04    SR28586 PCR31      Add comma to all amount fields.
//    RI016    DTC2073    02/01/05    SR31413 PCR26      Add copyright year dynamically to the page. 
//    RI017    DTC9028    06/27/05    SR31413 PCR19      Implement datasource modifications
// **************************************************************************************************************

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

</HEAD>

<!-- JSP page content header -->
<%@ page language="java" import="java.sql.*, java.util.*, java.text.*" errorPage="ErrorPage.jsp" %>
<jsp:useBean id="acctsummarybean" class="etrieveweb.etrievebean.acctsummaryBean" scope="page" />
<jsp:useBean id="invoiceagingbean" class="etrieveweb.etrievebean.invoiceagingBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />

 
<!-- get input parameters -->
<%

String str_username  = (String)session.getValue("username");
String str_customer  = (String) session.getValue("customer");
String str_company   = (String) session.getValue("company");
String str_datalib   = (String) session.getValue("datalib");
String list_customer = (String) session.getValue("list");
String str_password  = (String)session.getValue("password");
String str_as400url  = (String)session.getValue("as400url");
String str_as400driver = (String)session.getValue("as400driver");
String str_companyname = (String) session.getValue("companyname");
String loginuserid     = (String)session.getValue("loginuserid");
String str_isUsingInternational = (String)session.getValue("isUsingInternational");  // RI017

String str_showyearlyBox = "N";
String str_userid = "";

String str_agingdate  = request.getParameter("agedate");
String str_thrudate    = request.getParameter("thrudate");

if ( loginuserid == null )
	loginuserid = "";

Connection MenuSecurityBeanconnection = null;
Connection acctsummarybeanconnection = null;
Connection invoiceagingbeanconnection = null;

//*************************************************************
// RI016 - Retrieve today's date to be used for copyright statment.
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

if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U81"))  {
	response.sendRedirect("menu.jsp");
}
  
// **********************
// End current connection
//**********************

MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);


// ****************************
// Get aging date and thru date 
// Calculate aging buckets 
// ****************************

if ( str_agingdate == null )	
	str_agingdate = "";

if ( str_thrudate == null )	
	str_thrudate = "";

Calendar Today = Calendar.getInstance();

int temptoday = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

if ( str_agingdate.length() != 8 )
	str_agingdate = Integer.toString(temptoday);

if ( str_thrudate.length() != 8 )
	str_thrudate =  Integer.toString(temptoday);


invoiceagingbeanconnection = invoiceagingbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

String[] str_array = invoiceagingbean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company.trim(), str_datalib, str_agingdate, str_thrudate, str_isUsingInternational);	// RI017
                    
for (int val  = 0; val < 8; ++val)  {

	int decpos = 0;

	decpos = str_array[val].indexOf("."); 

	if (decpos == -1 )
		str_array[val] = str_array[val] + ".00";

	if (  (   (str_array[val].length()-1)  - decpos ==  1 )  )
		str_array[val] = str_array[val] + "0";

}
       

String str_total        = str_array[0];
String str_totalcurrent = str_array[1];
String str_totalday30   = str_array[2];
String str_totalday60   = str_array[3];
String str_totalday90   = str_array[4];
String str_totalday120  = str_array[5];
String str_totalamount  = str_array[6];  
String str_totalday150  = str_array[7];

invoiceagingbean.endcurrentConnection(invoiceagingbeanconnection);

// ***************************************************
// Make a new AS400 connection to retrieve account information
// ***************************************************

acctsummarybeanconnection = acctsummarybean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

if ( acctsummarybean.getCustomerInfo( Integer.valueOf(str_customer).intValue(), str_company, str_datalib) ) {       // **** Begin X0100  ****

	if ( acctsummarybean.getNext() ) {      // **** Begin X0200 ****
    
		String str_recordadddate = acctsummarybean.getColumn("CMRCDT");


		if ( !str_recordadddate.trim().equals("0") && str_recordadddate.length() == 8)
			str_recordadddate = str_recordadddate.substring(4, 6) + "/" + str_recordadddate.substring(6, 8) + "/" + str_recordadddate.substring(2, 4);
		else
			str_recordadddate = "";
			
		// **************************
		// Format last transaction date
		// **************************

		String str_lasttransdate = acctsummarybean.getColumn("CMLIDT");

		if ( !str_lasttransdate.trim().equals("0") && str_lasttransdate.length() == 8)
			str_lasttransdate = str_lasttransdate.substring(4, 6) + "/" + str_lasttransdate.substring(6, 8) + "/" + str_lasttransdate.substring(2, 4);
		else
			str_lasttransdate = "";

		// ***************************
		// Format last payment date
		// ***************************

		String str_lastpmtdate = acctsummarybean.getColumn("CMLPDT");

		if ( !str_lastpmtdate.trim().equals("0") && str_lastpmtdate.length() == 8)
			str_lastpmtdate = str_lastpmtdate.substring(4, 6) + "/" + str_lastpmtdate.substring(6, 8) + "/" + str_lastpmtdate.substring(2, 4);
		else
			str_lastpmtdate = "";
 

		// ****************************** 
		// Retrieve other account information
		// ******************************

		String str_cmadr1  = acctsummarybean.getColumn("CMADR1");
		String str_cmadr2  = acctsummarybean.getColumn("CMADR2");
		String str_cmcity  = acctsummarybean.getColumn("CMCITY");
		String str_cmstat  = acctsummarybean.getColumn("CMSTAT");
		String str_cmzip   = acctsummarybean.getColumn("CMZIP");
		String str_cmarea  = acctsummarybean.getColumn("CMAREA");
		String str_cmphon  = acctsummarybean.getColumn("CMPHON");
		String str_cmfaxa  = acctsummarybean.getColumn("CMFAXA");
		String str_cmfax   = acctsummarybean.getColumn("CMFAX#");
		String str_cmcont  = acctsummarybean.getColumn("CMCONT");
		String str_cmcrl   = acctsummarybean.getColumn("CMCRL");
		String str_cmopnamt = acctsummarybean.getColumn("CMOPN$");
		String str_cmhicr   = acctsummarybean.getColumn("CMHICR");
		String str_cmlamt   = acctsummarybean.getColumn("CMLAMT");

		if ( acctsummarybean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company.trim(), str_datalib) ) {      // **** Begin X0300

			if ( acctsummarybean.getNext() ) {      // **** Begin X0400 *****

				// Get the available credit amount
				
				double fl_creditLimit = Double.parseDouble(str_cmcrl);
				double fl_openAmount = Double.parseDouble(acctsummarybean.getColumn("RentAmt"));
				double fl_rentAmount = Double.parseDouble(str_cmopnamt);
				String str_avcredit  = String.valueOf(fl_creditLimit - fl_openAmount - fl_rentAmount);

				if ( str_avcredit.length() > (str_avcredit.indexOf('.') + 3 ) )
					str_avcredit = str_avcredit.substring(0, str_avcredit.indexOf('.') + 3);

				// *************************************************************
				// x015  - Create the decimal format variable to output formated dollars
				// ************************************************************* 

				DecimalFormat df = new DecimalFormat("###,###,###,###,###,###.00"); 
	
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
								<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;account summary and open invoices&nbsp;&nbsp;&nbsp;</td>
								<td></td>
							</tr>

						</table>

						<br>

						<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0 WIDTH="325">

							<tr>
								<td background="images/tlcrnr.gif">&nbsp;</td>
								<td colspan="2" background="images/top_back.gif"><img src="images/empty.gif" width="250" height="30"></td>
								<td background="images/trcrnr.gif">&nbsp;</td>
							</tr>

							<tr>
								<td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
								<td align="right">Bill To Address&nbsp;&nbsp;&nbsp;</td>
								<td align="left" class="data"><!--ADDRESS1--><%=str_cmadr1%><br><!--ADDRESS2--><%=str_cmadr2%></td>
								<td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td> 
							</tr>
 
                                             <tr>
                                                <td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
                                                <td align="left"></td>
                                                <td align="left" class="data"><!--CITY--><%=str_cmcity%>,&nbsp;<!--STATE--><%=str_cmstat%>&nbsp;<!--ZIP--><%=str_cmzip%></td>
                                                <td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
                                             </tr>

                                             <tr>
                                                <td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
                                                <td align="right">Phone&nbsp;&nbsp;&nbsp;</td>
                                                <td align="left" class="data">(<!--AREA--><%=str_cmarea%>)&nbsp;<!--PHONE--><%=str_cmphon%></td>
                                                <td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
                                             </tr>

                                             <tr>
                                                <td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
                                                <td align="right">Fax&nbsp;&nbsp;&nbsp;</td>
                                                <td align="left" class="data">(<!--FAX--><%=str_cmfaxa%>)&nbsp;<%=str_cmfax%></td>
                                                <td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
                                             </tr>

                                             <tr>
                                                <td background="images/blcrnr.gif">&nbsp;</td>
                                                <td colspan="2" background="images/bottom_back.gif"><img src="images/empty.gif" width="250" height="30"></td>
                                                <td background="images/brcrnr.gif">&nbsp;</td>
                                             </tr>

                                          </TABLE>

                                          <br>

                                          <table cellpadding="3" cellspacing="0" border="0" width="650">

                                                <tr>
                                                   <td align="right">Contact:</td>
                                                   <td align="left" class="data"><!--CONTACT--><%=str_cmcont%></td>
                                                   <td align="right"><!-- x014 remove Credit Limit: -->&nbsp;</td>
                                                   <td align="left" class="data"><!-- x014 remove CREDIT_LIMIT $<%=str_cmcrl%>-->&nbsp;</td>
                                                   <td colspan="2" align="left">&nbsp;</td>
                                               </tr>

                                               <tr>
                                                  <td align="right">Date account opened:</td>
                                                  <td align="left" class="data"><!--ACCT_OPEN_DATE--><%=str_recordadddate%></td>
                                                  <td align="right">Open A/R amount:</td>
                                                  <td align="right" class="data"><!--OPEN_AMT-->$<%=str_totalamount%></td>
                                                  <td colspan="2">&nbsp;</td>
                                               </tr>

                                               <tr>
                                                  <td align="right">Last order date:</td>
                                                  <td align="left" class="data"><!--LST_ORDER_DATE--><%=str_lasttransdate%></td>
                                                  <td align="right">On rent amount:</td>
                                                  <td align="right" class="data"><!--RENT_AMT-->$<%=df.format(Double.parseDouble( str_cmopnamt ))%></td>
                                                  <td colspan="2">&nbsp;</td>
                                              </tr>

                                              <tr>
                                                 <td align="right">Last payment date:</td>
                                                 <td align="left" class="data"><!--LST_PMT_DATE--><%=str_lastpmtdate%></td>
                                                 <td align="right">YTD Billed:</td>
                                                 <td align="right" class="data"><!--TOTAL_THIS-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("totalthis") ))%></td>
                                                 <td colspan="2" align="left">&nbsp;</td>
                                              </tr>

                                              <tr>
                                                 <td align="right">Last payment amount:</td>
                                                 <td align="left" class="data"><!--LST_PMT_AMT-->$<%=df.format(Double.parseDouble( str_cmlamt ))%></td>
                                                 <td align="right">Prior YR Billed:</td>
                                                 <td align="right" class="data"><!--TOTAL_LAST-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("totallast") ))%></td>
                                                 <td colspan="2" align="left">&nbsp;</td>
                                              </tr>

                                          </table>

                                          <br>

                                          <!-- Aging Buckets Table -->

                                          <table cellpadding="0" cellspacing="0" border="0" width="650">

                                              <tr>
                                                 <td background="images/tlcrnr.gif" width="25" height="30" align="right">&nbsp;</td>
                                                 <td colspan="6" background="images/top_back.gif" width="600">&nbsp;</td>
                                                 <td background="images/trcrnr.gif" width="25" height="30" align="left">&nbsp;</td>
                                              </tr>

                                              <tr>
                                                 <td align="left" width="25" background="images/left.gif" align="right">&nbsp;</td>
                                                 <td colspan="4" align="left">Quick Account Aging</td>
                                                 <td align="right"><b>Grand Total</b></td>
                                                 <td align="right" class="data"><!--RENT_AMT-->$<%=str_totalamount%></td>
                                                 <td align="left" width="25" background="images/right.gif" align="left">&nbsp;</td>
                                              </tr>

                                   <tr>
                                                 <td align="left" width="25" background="images/left.gif" align="right">&nbsp;</td>
                                                 <td colspan="4" align="left">&nbsp;</td>
                                                 <td align="right">&nbsp;</td>
                                                 <td align="right" class="footerblack"><a href="openInvoice.jsp?aging=open">View Detail</a> </td>
                                                 <td align="left" width="25" background="images/right.gif" align="left">&nbsp;</td>
                                              </tr>


                                              <tr>
                                                 <td align="left" width="25" background="images/left.gif" align="right">&nbsp;</td>
                                                 <td colspan="6" align="left">&nbsp;</td>
                                                 <td align="left" width="25" background="images/right.gif" align="left">&nbsp;</td>
                                              </tr>

                                              <tr>
                                                 <td align="left" width="25" background="images/left.gif" align="right">&nbsp;</td>
                                                 <td align="right">Current </td>
                                                 <td align="right">30 Days</td>
                                                 <td align="right">60 Days</td>
                                                 <td align="right">90 Days</td>
                                                 <td align="right">120 Days</td>
                                                 <td align="right">150 Days</td>
                                                 <td align="left" width="25" background="images/right.gif" align="left">&nbsp;</td>
                                              </tr>

                                              <tr>
                                                 <td align="left" width="25" background="images/left.gif" align="right">&nbsp;</td>
                                                 <td align="right" class="data"><!--CURRENT-->$<%=str_totalcurrent%></td>
                                                 <td align="right" class="data"><!--30_DAYS-->$<%=str_totalday30%></td>
                                                 <td align="right" class="data"><!--60_DAYS-->$<%=str_totalday60%></td>
                                                 <td align="right" class="data"><!--90_DAYS-->$<%=str_totalday90%></td>
                                                 <td align="right" class="data"><!--120_DAYS-->$<%=str_totalday120%></td>
                                                 <td align="right" class="data"><!--150_DAYS-->$<%=str_totalday150%></td>
                                                 <td align="left" width="25" background="images/right.gif" align="left">&nbsp;</td> 
                                              </tr>

                                              <tr>
                                                 <td align="left" width="25" background="images/left.gif" align="right">&nbsp;</td>
                                                 <td align="right" class="footerblack"><!--CURRENT--><a href="openInvoice.jsp?aging=curr">View Detail</a></td>
                                                 <td align="right" class="footerblack"><!--30_DAYS--><a href="openInvoice.jsp?aging=30days">View Detail</a></td>
                                                 <td align="right" class="footerblack"><!--60_DAYS--><a href="openInvoice.jsp?aging=60days">View Detail</a></td>
                                                 <td align="right" class="footerblack"><!--90_DAYS--><a href="openInvoice.jsp?aging=90days">View Detail</a></td>
                                                 <td align="right" class="footerblack"><!--120_DAYS--><a href="openInvoice.jsp?aging=120days">View Detail</a></td>
                                                 <td align="right" class="footerblack"><!--150_DAYS--><a href="openInvoice.jsp?aging=150days">View Detail</a></td>
                                                 <td align="left" width="25" background="images/right.gif" align="left">&nbsp;</td> 
                                              </tr>
                                              <tr>
                                                 <td background="images/blcrnr.gif" align="right" width="25" height="30">&nbsp;</td>
                                                 <td colspan="6" background="images/bottom_back.gif">&nbsp;</td>
                                                 <td background="images/brcrnr.gif" align="left" width="25" height="30">&nbsp;</td>
                                              </tr>

                                          </table>

                                          <br>

                                          <%  if (str_showyearlyBox.equals("Y") )  {  %>

                                             <table cellpadding="0" cellspacing="0" border="0" width="650">
 
                                                 <tr>
                                                    <td background="images/tlcrnr.gif" width="25" height="30" align="right">&nbsp;</td>
                                                    <td colspan="4" background="images/top_back.gif" width="600">&nbsp;</td>
                                                    <td background="images/trcrnr.gif" width="25" height="30" align="left">&nbsp;</td>
                                                 </tr>

                                                 <tr>
                                                    <td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
                                                    <td align="left">&nbsp;</td>
                                                    <td align="right">This year</td>
                                                    <td align="right">Last year</td>
                                                    <td align="right">Life to date</td>
                                                    <td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
                                                </tr>

                                                <tr>
                                                   <td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
                                                   <td align="left">Rental</td>
                                                   <td align="right" class="data"><!--RENTAL_THIS-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("cal_cmrytd") ))%></td>
                                                   <td align="right" class="data"><!--RENTAL_LAST-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("cal_cmrly") ))%></td>
                                                   <td align="right" class="data"><!--RENTAL_LIFE-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("cal_cmrltd") ))%></td>
                                                   <td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
                                                </tr>

                                                <tr>
                                                   <td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
                                                   <td align="left">Sales</td>
                                                   <td align="right" class="data"><!--SALES_THIS-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("cal_cmsytd") ))%></td>
                                                   <td align="right" class="data"><!--SALES_LAST-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("cal_cmsly") ))%></td>
                                                   <td align="right" class="data"><!--SALES_LIFE-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("cal_cmsltd") ))%></td>
                                                   <td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
                                                </tr>

                                                <tr>
                                                   <td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
                                                   <td align="left">Equipment sales</td>
                                                   <td align="right" class="data"><!--EQUIP_THIS-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("cal_cmeytd") ))%></td>
                                                   <td align="right" class="data"><!--EQUIP_LAST-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("cal_cmely") ))%></td>
                                                   <td align="right" class="data"><!--EQUIP_LIFE-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("cal_cmeltd") ))%></td>
                                                   <td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
                                                </tr>

                                                <tr>
                                                   <td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
                                                   <td align="left">Other</td>
                                                   <td align="right" class="data"><!--OTHER_THIS-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("cal_cmoytd") ))%></td>
                                                   <td align="right" class="data"><!--OTHER_LAST-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("cal_cmoly") ))%></td>
                                                   <td align="right" class="data"><!--OTHER_LIFE-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("cal_cmoltd") ))%></td>
                                                   <td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
                                                </tr>

                                                <tr>
                                                   <td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
                                                   <td align="left">Totals</td>
                                                   <td align="right" class="data"><!--TOTAL_THIS-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("totalthis") ))%></td>
                                                   <td align="right" class="data"><!--TOTAL_LAST-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("totallast") ))%></td>
                                                   <td align="right" class="data"><!--TOTAL_LIFE-->$<%=df.format(Double.parseDouble( acctsummarybean.getColumn("totallife") ))%></td>
                                                   <td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
                                                </tr>

                                                <tr>
                                                   <td background="images/blcrnr.gif" align="right" width="25" height="30">&nbsp;</td>
                                                   <td colspan="4" background="images/bottom_back.gif">&nbsp;</td>
                                                   <td background="images/brcrnr.gif" align="left" width="25" height="30">&nbsp;</td>
                                                </tr>

                                             </table>

                                             <br>

                                          <%   }   %>

 
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

                                  </BODY>

<%
                             }      // **** End X0400 ****

                       }  // *** End X0300 ***

  }   // *** End X0200 ***

}    // *** End X0100 ***

  // **********************
  // End current connection
  //**********************

acctsummarybean.cleanup();		// RI017
acctsummarybean.endcurrentConnection(acctsummarybeanconnection);

%>

</HTML>
