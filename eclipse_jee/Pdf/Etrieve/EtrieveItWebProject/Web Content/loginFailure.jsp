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
//    Index    User Id       Date      Project             Desciption
//   ------   ----------  --------    -----------------   ----------------------------------------------------------
//    RI001    DTC2073     01/28/05    SR31413 PCR26       Add copyright year dynamically to the page.
// *************************************************************************
%>

<%@ page language="java" import="java.sql.*, java.util.*" errorPage="ErrorPage.jsp"%>

<%
  //*************************************************************
  // RI001 - Retrieve today's date to be used for copyright statment.
  //*************************************************************
  
  Calendar cpToday = Calendar.getInstance();
  int cpYear = cpToday.get(Calendar.YEAR) ;  
  
%>
<html>
<head>
<title>Hertz Equipment Rental Online Reporting</title>
<meta http-equiv="expires" content="-1">
<meta http-equiv="pragma" content="no-cache">
<link href="images/wynnesystems.css" rel="stylesheet" type="text/css">

<style type="text/css">
#maintext {position:absolute; left:20px; width:650px; z-index:0;}
</style>

</HEAD>

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
<td background="images/bottom_back.gif" width="595" class="company">&nbsp;</td>
<td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
</tr>
<tr>
<td><img src="images/empty.gif" width="50" height="10"></td>
<td><img src="images/arrow.gif" height="10" align="right"></td>
<td>&nbsp;</td>
</tr>
<tr>
<td><img src="images/empty.gif" width="50" height="30"></td>
<td align="right" class="history">
<a href="javascript:window.close()">close window</a>&nbsp;&nbsp;&nbsp;
login failure&nbsp;&nbsp;&nbsp;
</td>
<td>&nbsp;</td>
</tr>
</table>
<br>

<center>
<table border="0" cellspacing="0" cellpadding="0" width="650">
<tr><td>
<p>The userid/password combination you submitted cannot be authenticated by the server. Please use the back button on your browser to return to the log in page and enter your User Name and Password again. If this problem persists, please contact the <a href="mailto:hercsales@hertz.com">Etrieve-it&#153; Manager</a>.</p>
</td></tr>
</table>
</center>

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
</HTML>
