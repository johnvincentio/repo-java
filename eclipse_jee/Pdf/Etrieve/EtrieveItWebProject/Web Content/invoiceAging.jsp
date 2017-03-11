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
//    Index     User Id         Date          Project        Desciption
//   -------   ------------  -----------  ----------------  ----------------------------------------------------------
//    x001      DTC9028       11/26/02     SR26609 PCR1      Modification for the new log-in process
//    x002      DTC9028       11/26/02     SR26609 PCR1      Ensure that connection are ended 
//    x003      DTC9028       12/26/02     SR26609 PCR1      Changed the "log out" to "close reports"
//    x004      DTC9028       12/26/02     SR26609 PCR1      Changed the page title 
//    x005      DTC9028       12/26/02     SR26609 PCR1      Changed the copy right text 
//    x006      DTC9028       12/26/02     SR26609 PCR1      Add the account number
//    x007      DTC9028       01/26/03     SR26609 PCR1      Remove date selection fields
//    x008      DTC9028       01/26/03     SR26609 PCR1      Add the 150 days column
//    x009      DTC9028       02/07/03     SR26609 PCR1      Make provisions for Java dates where Jan=0, Dec=11
//    x010      DTC9028       04/28/03     SR28586 PCR1      Add logic to check security for a customer rep
//    RI011     DTC2073       02/01/05     SR31413 PCR26     Add copyright year dynamically to the page.
//    RI012     DTC9028       03/03/05     SR31413 PCR19     Implement datasource modification 
//
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
#maintext {position:absolute; left:20px; width: 650px; z-index:0;}
</style>

<script language="JavaScript1.2">

function loadText() {

	form1.agedate.value = "<!--AGE_DATE-->";
	form1.thrudate.value = "<!--THRU_DATE-->";
}

</script>

</HEAD>


<!-- JSP page content header -->
<%@ page language="java" import="java.sql.*,java.util.*,java.text.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="invoiceagingbean" class="etrieveweb.etrievebean.invoiceagingBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />

 
<!-- get input parameters -->

<%
  String str_username     = (String) session.getValue("username");
  String str_customer     = (String)  session.getValue("customer");
  String str_company     = (String)  session.getValue("company");
  String str_datalib         = (String)  session.getValue("datalib");
  String list_customer    = (String)  session.getValue("list");
  String str_password    = (String)  session.getValue("password");
  String str_as400url       = (String) session.getValue("as400url");
  String str_as400driver = (String)  session.getValue("as400driver");
  String loginuserid        = (String)session.getValue("loginuserid");
  String str_isUsingInternational = (String)session.getValue("isUsingInternational");  // RI012
  String str_userid          = "";

  if ( loginuserid == null )
            loginuserid = "";

  Connection MenuSecurityBeanconnection = null;
  Connection invoiceagingbeanconnection = null;

  //*************************************************************
  // RI011 - Retrieve today's date to be used for copyright statment.
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

  // **********************************************************
  // Make a new AS400 Connection.  Check if user is authorized for this option.  
  //  If user is not allowed - redirect to menu
  // **********************************************************
   
  MenuSecurityBeanconnection = MenuSecurityBean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

  if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U83"))
  {
    response.sendRedirect("menu.jsp");
  }

  // **********************
  // End current connection
  //**********************

 MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);

 String str_companyname  = (String) session.getValue("companyname"); 

 String startnumber        = request.getParameter("startnumber");
 String str_agingdate     = request.getParameter("agedate");
 String str_thrudate        = request.getParameter("thrudate");
 String str_age_month   = request.getParameter("age_month");
 String str_age_date       = request.getParameter("age_date");
 String str_age_year       = request.getParameter("age_year");
 String str_thru_month  = request.getParameter("thru_month");
 String str_thru_date     = request.getParameter("thru_date");
 String str_thru_year     = request.getParameter("thru_year");

 if ( startnumber == null )	
         startnumber = "0";

 if ( str_agingdate == null )	
         str_agingdate = "";

 if ( str_thrudate == null )	
          str_thrudate = "";

 if ( str_age_month == null )	
          str_age_month = "";

 if ( str_age_date == null )	
          str_age_date = "";

 if ( str_age_year == null )	
          str_age_year = "";

 if ( str_thru_month == null )	
         str_thru_month = "";

 if ( str_thru_date == null )	
         str_thru_date = "";

 if ( str_thru_year == null )	
         str_thru_year = "";

 // ****************************
 // Get aging date and thru date
 // ****************************

 if ( !str_age_month.equals("") && !str_age_date.equals("") && !str_thru_year.equals("") )
          str_agingdate=str_age_year+str_age_month+str_age_date;

 if ( !str_thru_month.equals("") && !str_thru_date.equals("") && !str_thru_year.equals("") )
          str_thrudate=str_thru_year+str_thru_month+str_thru_date;

 Calendar Today = Calendar.getInstance();

 int temptoday = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

 if ( str_agingdate.length() != 8 )
         str_agingdate = Integer.toString(temptoday);

 if ( str_thrudate.length() != 8 )
        str_thrudate =  Integer.toString(temptoday);


 String mma     = str_agingdate.substring(4, 6); 
 String dda      = str_agingdate.substring(6, 8);
 String yyyya = str_agingdate.substring(0, 4);

 String mmt     = str_thrudate.substring(4, 6);
 String ddt      = str_thrudate.substring(6, 8);
 String yyyyt = str_thrudate.substring(0, 4);

 /*String str_customer = (String) session.getValue("customer");

 String str_corporate = (String) session.getValue("corporate");
 String str_company  = (String) session.getValue("company");
 String str_datalib      = (String) session.getValue("datalib");
 String list_customer = (String) session.getValue("list");*/

 // **********************************************************
 // Make a new AS400 Connection to retreive invoice aging
 // **********************************************************

 invoiceagingbeanconnection = invoiceagingbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);


 String[] str_array2 = invoiceagingbean.getCustomerInfo(Integer.valueOf(str_customer).intValue(), str_company, str_datalib);

 String str_recordopendate = str_array2[0];
 String str_lastpaydate        = str_array2[1];
 String str_lastpayment       = str_array2[2];

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
              <td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;invoice aging&nbsp;&nbsp;&nbsp;</td>
              <td></td>
           </tr>

        </table>

       

        <table cellpadding="2" cellspacing="2" border="0" width="225" align="left">

           <tr>
              <td valign="bottom" align="left">&nbsp;&nbsp;&nbsp;Date Account Opened:</td>
              <td valign="bottom" align="left" class="data"><!--DATE_OPENED--><%=str_recordopendate%></td>
          </tr>

          <tr>
             <td valign="bottom" align="left">&nbsp;&nbsp;&nbsp;Date of Last Payment:</td>
             <td valign="bottom" align="left" class="data"><!--DATE_PAID--><%=str_lastpaydate%></td>
          </tr>

          <tr>
             <td valign="bottom" align="left">&nbsp;&nbsp;&nbsp;Last Payment Amount:</td>
             <td valign="bottom" align="left" class="data"><!--AMOUNT--><%=str_lastpayment%></td>
          </tr>

        </table>

         <br clear="all">

         <table cellpadding="3" cellspacing="1" border="0" width="665">

              <tr>
                 <td bgcolor="#000000" background="images/empty.gif" align="left"   width="12%" class="whitemid">Invoice #</td>
                 <td bgcolor="#000000" background="images/empty.gif" align="right" width="10%" class="whitemid">Inv Date</td>
                 <td bgcolor="#000000" background="images/empty.gif" align="right" width="13%" class="whitemid">Current</td>
                 <td bgcolor="#000000" background="images/empty.gif" align="right" width="13%" class="whitemid">30 Days</td>
                 <td bgcolor="#000000" background="images/empty.gif" align="right" width="13%" class="whitemid">60 Days</td>
                 <td bgcolor="#000000" background="images/empty.gif" align="right" width="13%" class="whitemid">90 Days</td>
                 <td bgcolor="#000000" background="images/empty.gif" align="right" width="13%" class="whitemid">120 Days</td>
                 <td bgcolor="#000000" background="images/empty.gif" align="right" width="13%" class="whitemid">150 Days</td>
             </tr>

              <!--INV_TABLE-->

              <%

              String str_color    = "#cccccc";
              String str_contract = "";

              String[] str_array = invoiceagingbean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_agingdate, str_thrudate, str_isUsingInternational);	// RI012

              String str_total                = str_array[0];
              String str_totalcurrent   = str_array[1];
              String str_totalday30     = str_array[2];
              String str_totalday60     = str_array[3];
              String str_totalday90     = str_array[4];
              String str_totalday120   = str_array[5];
              String str_totalamount  = str_array[6];  
              String str_totalday150   = str_array[7];

              int year=0, month=0, day=0;
     
              double num_daycurrent = 0.0;
              double num_day30         = 0.0;
              double num_day60         = 0.0;
              double num_day90         = 0.0;
              double num_day120       = 0.0;
              double num_day150       = 0.0;

              String str_daycurrent = "";
              String str_day30   = "";
              String str_day60   = "";
              String str_day90   = "";
              String str_day120  = "";
              String str_day150  = "";

              DecimalFormat df = new DecimalFormat("###,###,###,###.##");

              Calendar AgeDueDate = Calendar.getInstance();
              Calendar Age30             = Calendar.getInstance();
              Calendar Age60             = Calendar.getInstance();
              Calendar Age90             = Calendar.getInstance(); 
              Calendar Age120           = Calendar.getInstance();
              Calendar Age150           = Calendar.getInstance();

               int num_display = 20;
               int num_count = 0;
               int num_total = Integer.valueOf(str_total).intValue();
               int num_current = Integer.valueOf(startnumber).intValue(); 
               int num_nextstart = num_current + num_display;
               int num_prevstart = num_current - num_display;

               if ( num_prevstart <= 0 )
                       num_prevstart = 0;

               if ( str_agingdate.length() == 8  ) {

                    year    = Integer.valueOf( str_agingdate.substring(0, 4) ).intValue();
                    month = Integer.valueOf( str_agingdate.substring(4, 6) ).intValue();
                    day     = Integer.valueOf( str_agingdate.substring(6, 8) ).intValue();

                    // *************************************************************
                    // Account for the Java date handling where January = 0 and December = 11
                    // *************************************************************
  
                   month = month - 1;

                    Age30.set(year, month, day);
                    Age60.set(year, month, day);
                    Age90.set(year, month, day);
                    Age120.set(year, month, day);
                    Age150.set(year, month, day);

                    Age30.add(Calendar.DATE, -29);
                    Age60.add(Calendar.DATE, -59);
                    Age90.add(Calendar.DATE, -89);
                    Age120.add(Calendar.DATE, -119);
                    Age150.add(Calendar.DATE, -149);

               }

               if ( invoiceagingbean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_current, str_agingdate, str_thrudate, str_isUsingInternational) ) {   // **** Begin X0100 ****

                    while ( invoiceagingbean.getNext() && num_count < num_display) {   // **** Begin X0200 ****

                         num_count++;

                         if ( str_color.equals("#cccccc") )
                               str_color = "#999999";
                         else 
                               str_color = "#cccccc";
    
                         str_contract               = invoiceagingbean.getColumn("AHINV#");
                         String str_sequence = invoiceagingbean.getColumn("AHISEQ");

                         while( str_sequence.length() < 3 ) {
                                      str_sequence = "0" + str_sequence;
                          }

                          // ***********************
                          // Format the invoice date
                          // ***********************

                          String str_invdate = invoiceagingbean.getColumn("AHDUED");

                          if ( !str_invdate.trim().equals("0") && str_invdate.length() == 8)
                                     str_invdate = str_invdate.substring(4, 6) + "/" + str_invdate.substring(6, 8) + "/" + str_invdate.substring(2, 4);
                          else
                                     str_invdate = "";
 
                          // ***********************
                          // Format the due date
                          // ***********************

                          if ( invoiceagingbean.getColumn("AHDUED").length() == 8 && str_agingdate.length() == 8 ) {    // **** Begin X0300 ****

                                 year    = Integer.valueOf( invoiceagingbean.getColumn("AHDUED").substring(0, 4) ).intValue();
                                month = Integer.valueOf( invoiceagingbean.getColumn("AHDUED").substring(4, 6) ).intValue();
                                day      = Integer.valueOf( invoiceagingbean.getColumn("AHDUED").substring(6, 8) ).intValue();

                               // *************************************************************
                               // Account for the Java date handling where January = 0 and December = 11
                               // *************************************************************
  
                               month = month - 1;

                                AgeDueDate.set(year, month, day);
      
                                num_daycurrent = 0.0;
                                num_day30   = 0.0;
                                num_day60   = 0.0;
                                num_day90   = 0.0;
                                num_day120  = 0.0;
                                num_day150  = 0.0;

                                str_daycurrent = "";
                                str_day30   = "";
                                str_day60   = "";
                                str_day90   = "";
                                str_day120  = "";
                                str_day150  = "";

                                if (AgeDueDate.before(Age150))
                                         num_day150 = Double.valueOf(invoiceagingbean.getColumn("AHCBAL")).doubleValue();
                                else if (AgeDueDate.before(Age120))
                                         num_day120 = Double.valueOf(invoiceagingbean.getColumn("AHCBAL")).doubleValue();
                                else if (AgeDueDate.before(Age90))
                                        num_day90 = Double.valueOf(invoiceagingbean.getColumn("AHCBAL")).doubleValue();
                                else if (AgeDueDate.before(Age60))
                                       num_day60 = Double.valueOf(invoiceagingbean.getColumn("AHCBAL")).doubleValue();
                                else if (AgeDueDate.before(Age30))
                                      num_day30= Double.valueOf(invoiceagingbean.getColumn("AHCBAL")).doubleValue();
                                else
                                      num_daycurrent= Double.valueOf(invoiceagingbean.getColumn("AHCBAL")).doubleValue();

                                if ( num_day150 != 0.0 )
                                      str_day150 = "$" + df.format(num_day150);
                                else if ( num_day120 != 0.0 )
                                      str_day120 = "$" + df.format(num_day120);
                               else if ( num_day90 != 0.0 )
                                     str_day90 = "$" + df.format(num_day90);
                               else if ( num_day60 != 0.0 )
                                     str_day60 = "$" + df.format(num_day60);
                               else if ( num_day30 != 0.0 )
                                     str_day30 = "$" + df.format(num_day30);
                               else if ( num_daycurrent != 0.0 )
                                     str_daycurrent = "$" + df.format(num_daycurrent);

                          }   // **** End X0300 ****
    
     
              %>

                         <tr>
                            <form action="contractDetail.jsp" method=POST>
                                    <input type="hidden" name="contract" value="<!--CONTRACT--><%=str_contract%>">
                                    <input type="hidden" name="sequence" value="<!--SEQUENCE--><%=str_sequence%>">
                                    <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" class="tabledata">
                                    <a href="contractDetail.jsp?contract=<%=str_contract%>&sequence=<%=str_sequence%>" onClick="submit();"><!--CONTRACT--><%=str_contract%>-<%=str_sequence%></a></td>
                            </form>
                            <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" class="tabledata"><%=str_invdate%>&nbsp;</td>
                            <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" class="tabledata"><%=str_daycurrent%>&nbsp;</td>
                            <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" class="tabledata"><%=str_day30%>&nbsp;</td>
                            <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" class="tabledata"><%=str_day60%>&nbsp;</td>
                            <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" class="tabledata"><%=str_day90%>&nbsp;</td>
                            <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" class="tabledata"><%=str_day120%>&nbsp;</td>
                            <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" class="tabledata"><%=str_day150%>&nbsp;</td>
                         </tr>

              <%
                    }   // **** End X0200 ****

               }   // **** End X0100 ****
               %>

              <tr>
                 <td background="images/empty.gif" align="left"    class="tabledata">Subtotal:</td>
                 <td background="images/empty.gif" align="right" class="tabledata">&nbsp;</td>
                 <td background="images/empty.gif" align="right" class="tabledata">$<%=str_totalcurrent%>&nbsp;</td>
                 <td background="images/empty.gif" align="right" class="tabledata">$<%=str_totalday30%>&nbsp;</td>
                 <td background="images/empty.gif" align="right" class="tabledata">$<%=str_totalday60%>&nbsp;</td>
                 <td background="images/empty.gif" align="right" class="tabledata">$<%=str_totalday90%>&nbsp;</td>
                 <td background="images/empty.gif" align="right" class="tabledata">$<%=str_totalday120%>&nbsp;</td>
                 <td background="images/empty.gif" align="right" class="tabledata">$<%=str_totalday150%>&nbsp;</td>
              </tr>

         </table>

         <table cellpadding="2" cellspacing="0" border="0" width="665">

             <tr>
                <td align="left" class="tableheader3"></td>
                <td align="right" class="tableheader3">Total: <!--TOTAL-->$<%=str_totalamount%></td>
             </tr>

         </table>

         <table cellpadding="0" cellspacing="0" border="0" width="665">

              <tr>

                 <td  width="510" background="images/empty.gif" bgcolor="#ffffff" align="left" valign="top" class="tableheader3"><!--NUM_RECORDS-->

                 <%  if ( num_total > 0 ) {  %>
                              <%=num_current+1%> - 

                 <%  if ( num_nextstart < num_total ) {  %>
                              <%=num_nextstart%> of 
                 <%  }else {  %>
                              <%=num_total%> of 
                 <%  }
                         } 
                  %>

                  <%=str_total%> record(s).
                  </td>

                  <td align="right" width="90">
                       <% if ( num_prevstart >= 0 && num_current != 0 ) {  %>
                                    <form action="invoiceAging.jsp" method=POST> 
                                           <input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
                                           <input type="hidden" name="agedate" value="<%=str_agingdate%>"> 
                                           <input type="hidden" name="thrudate" value="<%=str_thrudate%>"> 
                                           <input type=image src="images/prev.gif" height="40" width="87" border="0">
                                   </form>
                        <% }  %>
                  </td>

                  <td width="65"> 
                        <%   if ( num_nextstart < num_total ) {  %>
                                 <form action="invoiceAging.jsp" method=POST> 
                                        <input type="hidden" name="startnumber" value="<%=num_nextstart%>">
                                        <input type="hidden" name="agedate" value="<%=str_agingdate%>"> 
                                        <input type="hidden" name="thrudate" value="<%=str_thrudate%>"> 
                                        <input type=image src="images/next.gif" height="40" width="62" border=0> 
                                 </form>
                         <%   }   %>
                  </td>

              </tr>

         </table>

         
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
invoiceagingbean.cleanup();		// RI012
invoiceagingbean.endcurrentConnection(invoiceagingbeanconnection);
%>

</BODY>

</HTML>