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
<%@ page language="java" import="java.util.*" contentType="text/html" %> 
<%@ page isErrorPage="true" %>
 
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

<%
 //*************************************************************
 // RI001 - Retrieve today's date to be used for copyright statment.
 //*************************************************************
 
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  

 
String str_supportEmail = "mailto:hercsales@hertz.com";
String str_errortext = "Possible session timeout";

try {
     str_errortext = exception.getMessage();
} catch(NullPointerException e) {
         str_errortext = "Possible session timeout";
}

if ( str_errortext == null )
         str_errortext = "Possible session timeout";

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
<td align="right" class="history"><a href="javascript:window.close()">close reports</a>&nbsp;&nbsp;&nbsp;processing failure&nbsp;&nbsp;&nbsp;</td>
<td>&nbsp;</td>
</tr>

</table>

<br>

<center>

<table border="0" cellspacing="0" cellpadding="0" width="650">
   <tr>
     <td>
We're sorry but your request could not be processed at this time.  This may be the result of session inactivity for an extended time.  To protect the security of your data, this session is set to timeout after 30 minutes of inactivity.  
<br><br>
Please click on <a href="javascript:window.close()">close reports</a> and return to the Gold Service Login screen if you wish to continue.
<br><br>
If this problem persists after re-activating your session, please <a href=<%=str_supportEmail%>>contact us</a> and provide the error message below.
<br><br><br>
<a class="company">Error message:   <%=str_errortext%></a>
<br><br>
<%
try {
  application.log((String) request.getAttribute("sourcePage"), exception);
} catch(NullPointerException e) {
         str_errortext = "Possible session timeout";
}
%>
<br>

</td>
</tr>
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
