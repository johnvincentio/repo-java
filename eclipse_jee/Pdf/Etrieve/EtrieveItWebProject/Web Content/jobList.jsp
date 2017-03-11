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
//    Index     User Id       Date          Project                       Desciption
//   --------  -----------  -----------   -----------------  ----------------------------------------------------------
//    x001      DTC9028      11/26/02      SR26609 PCR1       Modification for the new log-in process
//    x002      DTC9028      11/26/02      SR26609 PCR1       Ensure that connection are ended 
//    x003      DTC9028      12/26/02      SR26609 PCR1       Changed the "log out" to "close reports"
//    x004      DTC9028      12/26/02      SR26609 PCR1       Changed the page title 
//    x005      DTC9028      12/26/02      SR26609 PCR1       Changed the copy right text 
//    RI006  	DTC2073      02/01/05      SR31413 PCR26      Add copyright year dynamically to the page. 
//    RI007     DTC9028      07/28/05      SR31413 PCR19      Implement datasource modification
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
<%@ page language="java" import="java.sql.*, java.util.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="joblistingbean" class="etrieveweb.etrievebean.joblistingBean" scope="page" />

<%
String startnumber = request.getParameter("startnumber");
String reporttype    = request.getParameter("reporttype");

String str_companyname  = (String) session.getValue("companyname");

String str_customer     = (String) session.getValue("customer");
String str_company     = (String) session.getValue("company");
String str_datalib         = (String) session.getValue("datalib");
String list_customer    = (String) session.getValue("list");
String str_username    = (String)session.getValue("username");
String str_password    = (String)session.getValue("password");
String str_as400url       = (String)session.getValue("as400url");
String str_as400driver = (String)session.getValue("as400driver");

Connection joblistingbeanconnection = null;

if ( startnumber==null )  startnumber="0";

if ( reporttype==null || reporttype.equals("") ) 
  reporttype="rentalHistory";

 //*************************************************************
 // Retrieve today's date to be used for copyright statment.
 //*************************************************************
  
  Calendar cpToday = Calendar.getInstance();
  int cpYear = cpToday.get(Calendar.YEAR) ;   
  
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
              <td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;search by job list&nbsp;&nbsp;&nbsp;</td>
              <td></td>
            </tr>
         </table>

        <br>

        <center>

     <table cellpadding="3" cellspacing="1" border="0" width="650">

        <tr>
           <td background="images/empty.gif" bgcolor="#000000" align="left" width="50%" class="whitemid">Job/Job Location</td>
           <td background="images/empty.gif" bgcolor="#000000" align="left" width="35%" class="whitemid">Contact/Job Number</td>
           <td background="images/empty.gif" bgcolor="#000000" align="left" width="15%" class="whitemid">Phone</td>
       </tr>
 

       <%

        joblistingbeanconnection = joblistingbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

        String str_color = "#cccccc";

        String str_total = joblistingbean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib);

        int num_display = 1000;
        int num_count = 0;
        int num_total = Integer.valueOf(str_total).intValue();
        int num_current = Integer.valueOf(startnumber).intValue();
        int num_nextstart = num_current + num_display;
        int num_prevstart = num_current - num_display;

        if ( num_prevstart <= 0 )
              num_prevstart = 0;

       if ( joblistingbean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_current) ) {

             while ( joblistingbean.getNext() && num_count < num_display ) {

                 num_count++;

                 if ( str_color.equals("#cccccc") )
                        str_color = "#999999";
                 else 
                        str_color = "#cccccc";

                 String str_page = "";

                 if ( reporttype.equals("rentalReturns") )
                        str_page = "rentalReturns.jsp";
                 else if ( reporttype.equals("monthlybillings") )
                        str_page = "monthlyBillings.jsp";
                 else if ( reporttype.equals("opensales") )
                        str_page = "openSales.jsp";
                 else if ( reporttype.equals("salesinvoices") )
                       str_page = "salesInvoices.jsp";
                 else 
                       str_page = "rentalHistory.jsp";

                 String str_phone = joblistingbean.getColumn("CJPHON");

                 if ( !str_phone.trim().equals("0") && str_phone.length() == 7)
                            str_phone = str_phone.substring(0, 3) + "-" + str_phone.substring(3, 7);

                %>

               <tr>
                  <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" width="50%" class="tabledata">
                         <a href="<%=str_page%>?jobnumber=<%=joblistingbean.getColumn("CJJOB#")%>">
                            <!--JOB_LOCATION--><%=joblistingbean.getColumn("CJJLOC")%><BR><!--ADDRESS--><%=joblistingbean.getColumn("CJADR1")%>
                        </a>
                 </td>
                 <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" width="35%" class="tabledata"><!--CONTACT--><%=joblistingbean.getColumn("CJCOM")%>/ <%=joblistingbean.getColumn("CJJOB#")%><BR><!--CITY--><%=joblistingbean.getColumn("CJCITY")%></td>
                 <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" valign="top" width="15%" class="tabledata"><!--AREA--><!--PHONE--><%=joblistingbean.getColumn("CJAREA")%>-<%=str_phone%></td>
              </tr>

<%
   }
}
%>

</table>

<table cellpadding="0" cellspacing="0" border="0" width="650">
 
   <tr>
      <td background="images/empty.gif" bgcolor="#ffffff" width="515" align="left" class="tableheader3"><!--NUM_RECORDS-->

         <%   if ( num_total > 0 ) {   %>

                   <%=num_current+1%> - 

                 <% if ( num_nextstart < num_total ) {    %>
                            <%=num_nextstart%> of 
                 <%  }else {    %>
                            <%=num_total%> of 
                 <%
                        }
                 }
                %>

               <%=str_total%> record(s).</td>

      <td align="right" width="90">
         <%   if ( num_prevstart >= 0 && num_current != 0 ) {   %>
                    <form action="jobList.jsp" method=POST> 
                           <input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
                           <input type="hidden" name="reporttype" value="<%=reporttype%>">
                           <input type=image src="images/prev.gif" height="40" width="87" border="0">
                   </form>

        <%  }   %>
      </td>

     <td width="65"> 
         <% if ( num_nextstart < num_total ) {    %>
                    <form action="jobList.jsp" method=get> 
                         <input type="hidden" name="startnumber" value="<%=num_nextstart%>">
                         <input type="hidden" name="reporttype" value="<%=reporttype%>">
                         <input type=image src="images/next.gif" height="40" width="62" border=0> 
                    </form>
         <%  }   %>
     </td>

   </tr>

</table>

</center>
<br><br>

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
joblistingbean.cleanup();	// RI007
joblistingbean.endcurrentConnection(joblistingbeanconnection);
%>

</BODY>
</HTML>

