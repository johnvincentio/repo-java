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
//   --------  ----------  -----------  ----------------  ----------------------------------------------------------
//    x001      DTC9028     11/26/02     SR26609 PCR1      Modification for the new log-in process
//    x002      DTC9028     11/26/02     SR26609 PCR1      Ensure that connection are ended 
//    x003      DTC9028     12/26/02     SR26609 PCR1      Changed the "log out" to "close reports"
//    x004      DTC9028     12/26/02     SR26609 PCR1      Changed the page title 
//    x005      DTC9028     12/26/02     SR26609 PCR1      Changed the copy right text 
//    x006      DTC9028     12/26/02     SR26609 PCR1      Add the account number
//    x007      DTC9028     04/28/03     SR28586 PCR1      Add logic to check security for a customer rep
//    RI008     DTC2073     02/01/05     SR31413 PCR26     Add copyright year dynamically to the page
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

</HEAD>

<!-- JSP page content header -->
<%@ page language="java" import="java.sql.*, java.util.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="opencontractsbean" class="etrieveweb.etrievebean.opencontractsBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />

 
<!-- get input parameters -->
<%
   String str_username    = (String)session.getValue("username");
   String str_customer     = (String) session.getValue("customer");
   String str_company     = (String) session.getValue("company");
   String str_datalib         = (String) session.getValue("datalib");
   String list_customer    = (String) session.getValue("list");
   String str_password    = (String)session.getValue("password");
   String str_as400url       = (String)session.getValue("as400url");
   String str_as400driver = (String)session.getValue("as400driver");
   String loginuserid        = (String)session.getValue("loginuserid");
   String str_userid          = "";

  if ( loginuserid == null )
            loginuserid = "";

   Connection MenuSecurityBeanconnection = null;
   Connection opencontractsbeanconnection = null;

  //*************************************************************
  // RI008 - Retrieve today's date to be used for copyright statment.
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

   if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U06"))
   {
     response.sendRedirect("menu.jsp");
   }

  String str_companyname  = (String) session.getValue("companyname");
  String startnumber = request.getParameter("startnumber");
  String jobnumber = request.getParameter("jobnumber");

  if ( startnumber == null )	startnumber = "0";
  if ( jobnumber == null )	jobnumber = "";

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
            <td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;open rental contracts&nbsp;&nbsp;&nbsp;</td>
            <td></td>
         </tr>
      </table>

      <br>

      <center>

         <table cellpadding="3" cellspacing="1" border="0" width="650">

            <tr>
               <td bgcolor="#000000" background="images/empty.gif" align="center" width="10%" class="whitemid">Contract</td>
               <td bgcolor="#000000" background="images/empty.gif" align="left"      width="8%" class="whitemid">Status</td>
               <td bgcolor="#000000" background="images/empty.gif" align="center" width="10%" class="whitemid">Start Date</td>
               <td bgcolor="#000000" background="images/empty.gif" align="center" width="10%" class="whitemid">Est. Return Date</td>
               <td bgcolor="#000000" background="images/empty.gif" align="left"      width="21%" class="whitemid">P.O.#</td>
               <td bgcolor="#000000" background="images/empty.gif" align="center" width="6%" class="whitemid">Loc</td>
               <td bgcolor="#000000" background="images/empty.gif" align="left"      width="35%" class="whitemid">Job Location</td>
            </tr>

            <!--OPEN_KTS_TABLE-->

            <%

            // ***************************************************
            // Make a new AS400 connection to retrieve information
            // ***************************************************

            opencontractsbeanconnection = opencontractsbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

            String str_color    = "#cccccc";
            /*String str_customer = (String) session.getValue("customer");
            String str_company  = (String) session.getValue("company");
            String str_datalib  = (String) session.getValue("datalib");
            String list_customer = (String) session.getValue("list");*/
            String str_contract = "";

            String str_total = opencontractsbean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, jobnumber);
  
            int num_display = 1000;
            int num_count = 0;
            int num_total = Integer.valueOf(str_total).intValue();
            int num_current = Integer.valueOf(startnumber).intValue();
            int num_nextstart = num_current + num_display;
            int num_prevstart = num_current - num_display;

            if ( num_prevstart <= 0 )
                    num_prevstart = 0;

            if ( opencontractsbean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_current, jobnumber) ) {

               while ( opencontractsbean.getNext() && num_count < num_display) {

                     num_count++;

                     if ( str_color.equals("#cccccc") )
                            str_color = "#999999";
                     else 
                            str_color = "#cccccc";
    
                     str_contract = opencontractsbean.getColumn("RHCON#");

                     String str_startdate = opencontractsbean.getColumn("RHDATO");

                     if ( !str_startdate.trim().equals("0") && str_startdate.length() == 8)
                            str_startdate = str_startdate.substring(4, 6) + "/" + str_startdate.substring(6, 8) + "/" + str_startdate.substring(2, 4);
                     else
                            str_startdate = "";

                     String str_returndate = opencontractsbean.getColumn("RHERDT");

                     if ( !str_returndate.trim().equals("0") && str_returndate.length() == 8)
                             str_returndate = str_returndate.substring(4, 6) + "/" + str_returndate.substring(6, 8) + "/" + str_returndate.substring(2, 4);
                     else
                             str_returndate = "";

                     String str_type = opencontractsbean.getColumn("RHTYPE");

                     if ( str_type == null )
                             str_type = "OPEN";
                     else if ( str_type.trim().equals("O"))
                            str_type = "OPEN";

            %>

                     <tr>
                        <form action="contractDetail.jsp" method=POST>
                              <input type="hidden" name="contract" value="<!--CONTRACT--><%=str_contract%>">
                              <input type="hidden" name="sequence" value="000">
                              <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" class="tabledata"><a href="contractDetail.jsp?contract=<%=str_contract%>&sequence=000" onClick="submit();"><!--CONTRACT--><%=str_contract%></a></td>
                        </form>
                        <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"      class="tabledata"><!--TYPE--><%=str_type%>&nbsp;</td>
                        <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" class="tabledata"><!--START_DATE--><%=str_startdate%>&nbsp;</td>
                        <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" class="tabledata"><!--RTRN_DATE--><%=str_returndate%>&nbsp;</td>
                        <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"      class="tabledata"><!--PO_NUM--><%=opencontractsbean.getColumn("RHPO#")%>&nbsp;</td>
                        <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" class="tabledata"><!--LOCATION--><%=opencontractsbean.getColumn("RHLOC")%>&nbsp;</td>
                        <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"       class="tabledata"><!--JOB_LOC--><%=opencontractsbean.getColumn("RHJOBL")%>&nbsp;</td>
                    </tr>

       <%
               }
            }
      %>

       </table>

      <table cellpadding="0" cellspacing="0" border="0" width="650">

      <tr>
         <td bgcolor="#ffffff" background="images/empty.gif" width="515" align="left" class="tableheader3"><!--NUM_RECORDS-->&nbsp;

             <%  if ( num_total > 0 ) {   %>
                      <%=num_current+1%>- 
                      <%    if ( num_nextstart < num_total ) {   %>
                                     <%=num_nextstart%> of 
                      <%    }else {   %>
                                    <%=num_total%> of 
                      <%
                                }
                      }
                  %>

              <%=str_total%> record(s).
         </td>

         <td align="right" width="90">
             <%   if ( num_prevstart >= 0 && num_current != 0 ) {    %>
                          <form action="openContracts.jsp" method=POST> 
                                 <input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
                                 <input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
                                 <input type=image src="images/prev.gif" height="40" width="87" border="0">
                         </form>
              <%  }   %>
         </td>

         <td width="65"> 
              <%   if ( num_nextstart < num_total ) {    %>
                          <form action="openContracts.jsp" method=POST> 
                               <input type="hidden" name="startnumber" value="<%=num_nextstart%>">
                               <input type="hidden" name="jobnumber" value= "<%=jobnumber%>"> 
                              <input type=image src="images/next.gif" height="40" width="62" border=0> 
                         </form>
               <%  }   %>
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

opencontractsbean.endcurrentConnection(opencontractsbeanconnection);

%>

</BODY>
</HTML>
