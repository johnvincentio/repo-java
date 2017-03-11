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
<%@ page language="java" import="java.sql.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="detailinvoicebean" class="etrieveweb.etrievebean.eqdetailinvoiceBean" scope="page" />

<!-- get input parameters -->

<%
String str_item = request.getParameter("item");
String str_contract = request.getParameter("contract");
String str_sequence = request.getParameter("sequence");
String str_daily = request.getParameter("daily");
String str_weekly = request.getParameter("weekly");
String str_monthly = request.getParameter("monthly");
String str_catg = request.getParameter("catg");
String str_class = request.getParameter("class");
String str_desc = request.getParameter("desc");

if (str_item==null) str_item="";
if (str_contract==null) str_contract="";
if (str_sequence==null) str_sequence="";
if (str_daily==null) str_daily="";
if (str_weekly==null) str_weekly="";
if (str_monthly==null) str_monthly="";
if (str_catg==null) str_catg="";
if (str_class==null) str_class="";
if (str_desc==null) str_desc="";

String str_companyname  = (String) session.getValue("companyname");
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
<td background="images/bottom_back.gif" width="595" class="company"><!--COMPANY_NAME--><%=str_companyname%></td>
<td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
</tr>

<tr>
<td><img src="images/empty.gif" height="10"></td>
<td><img src="images/arrow.gif" height="10" align="right"></td>
<td></td>
</tr>

<tr>
<td><img src="images/empty.gif" height="30"></td>
<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;invoice detail&nbsp;&nbsp;&nbsp;</td>
<td></td>
</tr>

</table>

<br>

<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0 WIDTH="200" align="right">

<!--DETAIL_INV_TABLE-->

<%
String str_username = (String)session.getValue("username");
String str_password = (String)session.getValue("password");
String str_as400url = (String)session.getValue("as400url");
String str_as400driver = (String)session.getValue("as400driver");
Connection detailinvoicebeanconnection = null;

detailinvoicebeanconnection = detailinvoicebean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

String str_color = "#cccccc";
String str_customer = (String) session.getValue("customer");
String str_company  = (String) session.getValue("company");
String str_datalib  = (String) session.getValue("datalib");
String list_customer = (String) session.getValue("list");

String str_total = detailinvoicebean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_catg, str_class, str_contract, str_sequence);
String str_dateout = "";
String str_billfrom = "";
String str_billto = "";

int num_display = 20;
int num_count = 0;
 
if ( detailinvoicebean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_catg, str_class, str_contract, str_sequence ) ) {

  if ( detailinvoicebean.getNext() ) {

    num_count++;

    if ( str_color.equals("#cccccc") )
      str_color = "#999999";
    else 
      str_color = "#cccccc";


    //format date fields
    str_dateout = detailinvoicebean.getColumn("RDDATO");

    if ( !str_dateout.trim().equals("0") )
      str_dateout = str_dateout.substring(4, 6) + "/" + str_dateout.substring(6, 8) + "/" + str_dateout.substring(2, 4);
    else
      str_dateout = "";


    str_billfrom = detailinvoicebean.getColumn("RDLBDF");

    if ( !str_billfrom.trim().equals("0") )
      str_billfrom = str_billfrom.substring(4, 6) + "/" + str_billfrom.substring(6, 8) + "/" + str_billfrom.substring(2, 4);
    else
      str_billfrom = "";


    str_billto = detailinvoicebean.getColumn("RDLBDT");

    if ( !str_billto.trim().equals("0") )
      str_billto = str_billto.substring(4, 6) + "/" + str_billto.substring(6, 8) + "/" + str_billto.substring(2, 4);
    else
      str_billto = "";
   
%>

<tr>
<td background="images/tlcrnr.gif">&nbsp;</td>
<td background="images/top_back.gif"><img src="images/empty.gif" width="150" height="30"></td>
<td background="images/trcrnr.gif">&nbsp;</td>
</tr>

<tr>
<td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
<td align="left"><a class="tableheader3">Rental Rates</a><br>
Daily: &nbsp;&nbsp;&nbsp;&nbsp;<a class="data">$<!--DAY_RATE--><%=str_daily%></a><br>
Weekly: <a class="data">$<!--WEEK_RATE--><%=str_weekly%></a><br>
Monthly: <a class="data">$<!--MONTH_RATE--><%=str_monthly%></a><br>
</td>
<td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
</tr>

<tr>
<td background="images/blcrnr.gif">&nbsp;</td>
<td background="images/bottom_back.gif"><img src="images/empty.gif" width="150" height="30"></td>
<td background="images/brcrnr.gif">&nbsp;</td>
</tr>

</TABLE>


DESCRIPTION: <a class="data"><!--DESC--><%=str_desc%></a><br>
<br>
EQUIPMENT #: <a class="data"><!--ITEM--><%=str_item%></a><br>
<br>
CATEGORY-CLASS: <a class="data"><!--CATG_CLASS--><%=str_catg%>-<%=str_class%></a><br>
<br>
INVOICE #: <a class="data"><!--CONTRACT--><%=str_contract%></a><br>
<br clear="all">
<br>

<center>

<table cellpadding="3" cellspacing="1" border="0" width="100%">

<tr>
<td background="images/empty.gif" bgcolor="#000000" align="center" width="5%" class="whitemid">Seq#</td>
<td background="images/empty.gif" bgcolor="#000000" align="center" width="10%" class="whitemid">Date Out</td>
<td background="images/empty.gif" bgcolor="#000000" align="right" width="20%" class="whitemid">Bill From</td>
<td bgcolor="#000000" background="images/empty.gif" align="right" width="20%" class="whitemid">Bill To</td>
<td bgcolor="#000000" background="images/empty.gif" align="right" width="10%" class="whitemid">Rent Days</td>
<td bgcolor="#000000" background="images/empty.gif" align="right" width="10%" class="whitemid">Rent Hours</td>
<td bgcolor="#000000" background="images/empty.gif" align="right" width="25%" class="whitemid">Rental Amount</td>
</tr>

<tr>
<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" width="5%" class="tabledata"><!--INVOICE--><%=str_sequence%></td>
<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" width="10%" class="tabledata"><!--DATE_OUT--><%=str_dateout%></td>
<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" width="20%" class="tabledata"><!--BILL_FROM--><%=str_billfrom%></td>
<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" width="20%" class="tabledata"><!--BILL_TO--><%=str_billto%></td>
<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" width="10%" class="tabledata"><!--RENT_DAYS--><%=detailinvoicebean.getColumn("RDRDYS")%></td>
<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" width="10%" class="tabledata"><!--RENT_HOURS--><%=detailinvoicebean.getColumn("RDRHRS")%></td>
<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" width="25%" class="tabledata"><!--RENT_COST-->$<%=detailinvoicebean.getColumn("RDAMT$")%></td>
</tr>

<%
   }
}
%>

<!--DETAIL_INV_TABLE-->

</table>

<table border="0" width="100%" cellspacing="0" cellpadding="0">

<tr>
<td align="left" class="tableheader3"><!--NUM_RECORDS--><%=str_total%> record(s) found.</td>
<td align="right" class="tableheader3">Total: $<!--TOTAL_AMT--><%=detailinvoicebean.getTotalAmount(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_catg, str_class, str_contract, str_sequence)%>
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
            <td background="images/empty.gif" class="footerblack"><center>&copy; 2004 The Hertz Corporation.  All Rights Reserved.  &reg REG. U.S. PAT. OFF.</center></td>
         </tr>
 
      </table>

<br>

</div>

</BODY>
</HTML>

<%
  detailinvoicebean.endcurrentConnection(detailinvoicebeanconnection);
%>