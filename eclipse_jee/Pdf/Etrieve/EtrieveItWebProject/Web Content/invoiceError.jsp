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
//   Index     User Id      Date          Project           Desciption
//  --------  ----------  -----------   ---------------   --------------------------------------------------------
//    x001     DTC9028     01/26/03      SR26609 PCR1       Changed the "log out" to "close reports"
//    x002     DTC9028     01/26/03      SR26609 PCR1       Changed the page title 
//    x003     DTC9028     01/26/03      SR26609 PCR1       Changed the copy right text 
//    x004     DTC9028     01/26/03      SR26609 PCR1       Add the account number
//    x005     DTC9028     02/24/04      SR28586 PCR22      Added contact information for Canada
//    RI006    DTC2073     02/01/05      SR31413 PCR26      Add copyright year dynamically to the page. 
//    RI007    DTC9028     08/01/05      SR31413 PCR19      Datasource implementation modification 
// **************************************************************************************************************

String[]    names = session.getValueNames();
		 
if ( names.length <= 0 || names[0] == "")
  response.sendRedirect("securityFailure.jsp");	

String str_contract   = request.getParameter("contract");
String str_sequence = request.getParameter("sequence");
String str_transtype = request.getParameter("transtype");

if ( str_contract == null )	str_contract = "";
if ( str_sequence == null )	str_sequence = "";
if ( str_transtype == null )	str_transtype = "";

String str_transaction  = "";
String str_transaction2 = "";
String str_locationinfo = "";

if ( str_sequence.equals("000") || str_sequence.equals("") )   {
     str_transaction   = "contract";
     str_transaction2 = "Contract";
} else {
    str_transaction   = "invoice";
    str_transaction2 = "Invoice";
}

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
<%@ page language="java" import="java.sql.*, java.util.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="invoiceerrorbean" class="etrieveweb.etrievebean.invoiceErrorBean" scope="page" />

 
<!-- get input parameters -->
<%

String str_username  = (String) session.getValue("username");          
String str_password  = (String) session.getValue("password");        
String str_as400url  = (String) session.getValue("as400url");          
String str_as400driver = (String) session.getValue("as400driver");
String str_customer = (String) session.getValue("customer");
String str_company  = (String) session.getValue("company");
String str_companyname = (String) session.getValue("companyname");
String str_datalib = (String) session.getValue("datalib");

//*************************************************************
// Retrieve today's date to be used for copyright statment.
//*************************************************************
  
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  


// ***  Define connection variables if Etrieve Canada  ***

if (  str_company.trim().equals("CR") )  {

	Connection invoiceerrorbeanconnection  = null;
	
	invoiceerrorbeanconnection = invoiceerrorbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);
	
	str_locationinfo = invoiceerrorbean.getLocInfo( str_company.trim(),  str_datalib, Integer.valueOf(str_customer).intValue(), Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), str_transtype);
	
	invoiceerrorbean.cleanup();	// RI007
	
	invoiceerrorbean.endcurrentConnection(invoiceerrorbeanconnection);
	
	if ( !str_locationinfo.trim().equals("") )
		str_locationinfo = "our location in " + str_locationinfo.trim() + ".";
	else
		str_locationinfo = "the Hertz branch identified on your original rental document.";
		
}  else if (  str_company.trim().equals("HG") )
	str_locationinfo = "our billing office at 1-800-456-6492 Monday thru Friday, between the hours of 7-7 CST.";


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
         <td background="images/bottom_back.gif" width="595"><a class="company"><%=str_companyname.trim()%></a>&nbsp;&nbsp;-&nbsp;&nbsp;Account Number: <a class="data"><%=str_customer%></a></td>
         <td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
      </tr>

      <tr>
         <td><img src="images/empty.gif" height="10"></td>
         <td><img src="images/arrow.gif" height="10" align="right"></td>
         <td></td>
      </tr>

      <tr>
        <td><img src="images/empty.gif" height="30"></td>
        <td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;invoice error&nbsp;&nbsp;&nbsp;</td>
        <td></td>
      </tr>

   </table>

   <br>

   <center>

   <table cellpadding="3" cellspacing="1" border="0" width="650">

      <tr>
         <td colspan="2" align="left" valign="top" >We apologize but the detail associated with this <%=str_transaction%>  number is not currently available.  <br><br>If you require more information, please contact <%=str_locationinfo%>  Please have your account and this <%=str_transaction%> number  available.  <br><br></td>
      </tr>

     <tr>
        <td width="20%">Account number: </td>
        <td><%=str_customer%></td>
    </tr>

     <tr>
        <td><%=str_transaction2%> number: </td>
        <td><%=str_contract%>-<%=str_sequence%></td>
    </tr>

  </table>

</center>
<br>
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

</BODY>
</HTML>