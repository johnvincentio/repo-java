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
//    Index     User Id      Date         Project             Desciption
//   --------  ----------  ----------   ------------------   ----------------------------------------------------------
//    x001      DTC9028     11/14/03     SR28586 PCR8         Create PO history list
//    RI002     DTC2073     02/01/05     SR31413 PCR26        Add copyright year dynamically to the page
//    RI003     DTC9028     08/01/05     SR31413 PCR19        Datasource implementation modification
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
<%@ page language="java" import="java.sql.*,java.util.*,java.text.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="pohistorybean" class="etrieveweb.etrievebean.poHistoryBean" scope="page" />
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
  String str_gotrecords   = "";
  String str_ponumber = "";
  String str_jobnumber = "";
  String str_checkboxname = "";
  String str_checkboxvalue = "";
  String str_total                = "";
  String str_12monthsagodate  = "";
  String str_sortby      = request.getParameter("sortby");
  String str_POchecked = "";
  String str_JOBchecked = "";
  String str_searchby   = request.getParameter("searchby");
  String str_searchtext1   = "";
  String str_searchtext2   = "";
  String startstring = "";	
  int quotepos = 0;
  int invalidpos1 = 0;
  int invalidpos2 = 0;
  int invalidpos3 = 0;

  //*************************************************************
  // Retrieve today's date to be used for copyright statment.
  //*************************************************************
  
  Calendar cpToday = Calendar.getInstance();
  int cpYear = cpToday.get(Calendar.YEAR) ;  
  
  
  
  if ( str_sortby == null )
           str_sortby   = "PO";

  if ( str_sortby.equals("PO") )  {
           str_sortby   = " order by RHPO#, RHJOB# ";
           str_POchecked = " checked ";
  }  else  {
           str_sortby   = " order by RHJOB#, RHPO# ";
           str_JOBchecked = " checked ";
  }


  if ( str_searchby == null )
           str_searchby   = "";


  if ( loginuserid == null )
            loginuserid = "";

  Connection MenuSecurityBeanconnection = null;
  Connection pohistorybeanconnection = null;

  // **********************************************************
  // Determine if a search string was entered.  If so, remove any single quote
  // that might have been entered
  // **********************************************************

  str_searchby = str_searchby.toUpperCase();

  invalidpos1 = str_searchby.indexOf("DELETE"); 
  invalidpos2 = str_searchby.indexOf("SELECT"); 
  invalidpos3 = str_searchby.indexOf("UPDATE"); 

  if ( invalidpos1 >= 0 ||  invalidpos2 >= 0   ||  invalidpos3 >= 0  ) 
  	str_searchby = "";

  if ( !str_searchby.equals("")   )  {

	quotepos = str_searchby.indexOf("'"); 

	if (quotepos == -1 ) {

		str_searchtext1 =  "  ( RHPO#    like  '%" + str_searchby.trim() + "%')  "; 
		str_searchtext2 =  "  ( RHJOB#  like  '%" + str_searchby.trim() + "%')  "; 

	} else {

		str_searchtext1 =  "  ( RHPO#   like '%";
		str_searchtext2 =  "  ( RHJOB# like '%";

		for (int val3  = 0; val3 < str_searchby.length(); ++val3)   {

			if  ( startstring.equals("Y") )  {
				str_searchtext1 = str_searchtext1 + "  and  RHPO# like '%";
				str_searchtext2 = str_searchtext2 + "  and  RHJOB# like '%";
				startstring = "N";
			}

			if  ( str_searchby.trim().substring(val3, val3+1).equals("'") ) {
				str_searchtext1 = str_searchtext1.trim() + "%' ";
				str_searchtext2 = str_searchtext2.trim() + "%' ";
				startstring = "Y";
			}

			if  ( !str_searchby.trim().substring(val3, val3+1).equals("'") )  {
				str_searchtext1 = str_searchtext1 + str_searchby.trim().substring(val3, val3+1);
				str_searchtext2 = str_searchtext2 + str_searchby.trim().substring(val3, val3+1);
			}
		
			if  (  val3+1 == str_searchby.trim().length()  ) {
				str_searchtext1 =str_searchtext1.trim() + "%') ";
				str_searchtext2 =str_searchtext2.trim() + "%') ";
				startstring = "";
			}
		}
    	}

	str_searchtext1 = " and ( "  +   str_searchtext1.toUpperCase() + " or  " + str_searchtext2.toUpperCase()  +  " )  ";
   }


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

  if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U07"))
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

  // *****************************************************
  //  Calculate the date for twelve months back from today's date
  // Account for the Java date handling where January = 0 and December = 11
  // *****************************************************
  
      Calendar Today = Calendar.getInstance();

      int temptoday = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

      str_12monthsagodate = Integer.toString(temptoday);

      int ageyear=0, agemonth=0, ageday=0;

      Calendar BackDate365  = Calendar.getInstance();

      ageyear    = Integer.valueOf( str_12monthsagodate.substring(0, 4) ).intValue();
      agemonth = Integer.valueOf( str_12monthsagodate.substring(4, 6) ).intValue();
      ageday     = Integer.valueOf( str_12monthsagodate.substring(6, 8) ).intValue();

      agemonth = agemonth - 1;

      BackDate365.set(ageyear, agemonth, ageday);

      BackDate365.add(Calendar.DATE, -365);

      temptoday = BackDate365.get(Calendar.YEAR)*10000 + (BackDate365.get(Calendar.MONTH)+1)*100 + BackDate365.get(Calendar.DAY_OF_MONTH);

      str_12monthsagodate = Integer.toString(temptoday);

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
        <td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;purchase order history&nbsp;&nbsp;&nbsp;</td>
        <td></td>
      </tr>

   </table>

   <br>

   <center>

   <form action="poActivity.jsp" method=POST>

   <table cellpadding="3" cellspacing="1" border="0" width="650">

      <tr>
         <td bgcolor="#ffffff" align="left"  class="tableheader3" colspan="3">&nbsp;Note: The list below reflects PO's/Job's with activity in the last 12 months.  To view detail, click the checkbox<br>&nbsp;to the left of each and select the "View Activity" button located at the bottom of the page.</td>
      </tr>

      <tr>
         <td bgcolor="#000000" background="images/empty.gif" align="left" valign="top" width="5%" class="whitemid">&nbsp;</td>
         <td bgcolor="#000000" background="images/empty.gif" align="left" valign="top" width="35%" class="whitemid">Purchase Order</td> 
         <td bgcolor="#000000" background="images/empty.gif" align="left" valign="top" width="60%" class="whitemid">Job Number</td>
     </tr>

      <%

      // ***************************************************
      // Make a new AS400 connection to retrieve account information
      // ***************************************************

      pohistorybeanconnection = pohistorybean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

      String str_color    = "#cccccc";
      String str_description = "";
      String str_contract = "";
      String str_startdate = "";

      str_total = pohistorybean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_12monthsagodate, str_searchtext1 );
  
      int num_display = 500000;
      int num_count = 0;
      int num_total = Integer.valueOf(str_total).intValue();
      int num_current = Integer.valueOf(startnumber).intValue();
      int num_nextstart = num_current + num_display;
      int num_prevstart = num_current - num_display;

      if ( num_prevstart <= 0 )
           num_prevstart = 0;

	if ( pohistorybean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_current, str_12monthsagodate, str_sortby, str_searchtext1  ) ) {

		while ( pohistorybean.getNext() && num_count < num_display) {

			num_count++;
			str_gotrecords   = "Y";
  			startstring = "";	
			quotepos = 0;

			if ( str_color.equals("#cccccc") )
				str_color = "#999999";
			else 
				str_color = "#cccccc";

			str_ponumber = pohistorybean.getColumn("RHPO#");
			str_jobnumber = pohistorybean.getColumn("RHJOB#");

			str_checkboxname = "checkbox" + num_count; 

			quotepos = str_ponumber.indexOf("'"); 

			if (quotepos == -1 ) {

				str_checkboxvalue =  "  ( RHPO# = '" + str_ponumber.trim() + "')  "; 

			} else {

				str_checkboxvalue =  "  ( RHPO# like '%";

				for (int val3  = 0; val3 < str_ponumber.length(); ++val3)   {

					if  ( startstring.equals("Y") )  {
						str_checkboxvalue = str_checkboxvalue + "  and  RHPO# like '%";
						startstring = "N";
					}

					if  ( str_ponumber.substring(val3, val3+1).equals("'") ) {
						str_checkboxvalue = str_checkboxvalue.trim() + "%' ";
						startstring = "Y";
					}

					if  ( !str_ponumber.substring(val3, val3+1).equals("'") ) 
						str_checkboxvalue = str_checkboxvalue + str_ponumber.substring(val3, val3+1);
		
					if  (  val3+1 == str_ponumber.length()  ) {
						str_checkboxvalue = str_checkboxvalue.trim() + "%') ";
						startstring = "";
					}
				}
			}

  			startstring = "";	

			quotepos = str_jobnumber.indexOf("'"); 

			if (quotepos == -1 )  {

				str_checkboxvalue = str_checkboxvalue.trim() + " and ( RHJOB# = '" +  str_jobnumber.trim() + "') ";

			} else {

				str_checkboxvalue =  str_checkboxvalue.trim() +  " and  ( RHJOB# like '%";

				for (int val3  = 0; val3 < str_jobnumber.length(); ++val3)   {

					if  ( startstring.equals("Y") )  {
						str_checkboxvalue = str_checkboxvalue + "  and  RHJOB# like '%";
						startstring = "N";
					}

					if  ( str_jobnumber.substring(val3, val3+1).equals("'") ) {
						str_checkboxvalue = str_checkboxvalue.trim() + "%' ";
						startstring = "Y";
					}

					if  ( !str_jobnumber.substring(val3, val3+1).equals("'") ) 
						str_checkboxvalue = str_checkboxvalue + str_jobnumber.substring(val3, val3+1);
		
					if  (  val3+1 == str_jobnumber.length()  ) {
						str_checkboxvalue = str_checkboxvalue.trim() + "%') ";
						startstring = "";
					}
				}
			}

			str_checkboxvalue = " ( " + str_checkboxvalue.trim() + " )  postart" + str_ponumber + "poend jobstart" + str_jobnumber + "jobend";

      %>

            <tr>
                <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata">&nbsp;<input type="checkbox" name="<%=str_checkboxname%>" value="<%=str_checkboxvalue%>"></td>
                <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"      valign="top" class="tabledata">&nbsp;&nbsp;<%=str_ponumber%></td>
                <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata">&nbsp;&nbsp;<%=str_jobnumber%></td>
            </tr>

     <%
         }

      }
     %>

       <tr>
           <%  if ( !str_total.equals("0") )  {  %>
		<td bgcolor="#ffffff" align="center" colspan="3" class="tableheader3"><br><input type="submit" value="View Activity for selected item(s)" name="SearchButton"></td>  
              <% }  else {  %>
                     <td bgcolor="#CCCCCC" align="left" colspan="3" class="tableheader3"><br>No records were found<br></td>
              <% }  %>
       </tr>

  </table>

   <input type="hidden"  name="totalrecords"  value="<%=str_total.trim()%>">

  </form>

<br>

   <form action="poHistory.jsp" method=POST>
	<table cellpadding="8" cellspacing="0" border="0"  width="500" >
		<tr bgcolor="ffffff" >
		   <td align="center" valign="middle" class="tableheader3">To reset your selections and display the complete list, select the Reset button below<BR><BR><input type="submit" value="Reset selection(s)"</td>
		</tr>
	</table>
	<input type="hidden" name="sortby" value="PO">
	<input type="hidden" name="searchby" value="">
   </form>


   <form action="poHistory.jsp" method=POST>
	<table cellpadding="8" cellspacing="0" border="0"  width="400" >
		<tr bgcolor="#999999" >
		   <td align="center" valign="middle" class="tableheader3">&nbsp;Sort list by:&nbsp;<input type="radio" name="sortby" value="PO"  <%=str_POchecked%>>PO  number&nbsp;&nbsp;<input type="radio" name="sortby" value="JOB"  <%=str_JOBchecked%>> Job  number</td>
		</tr>
		<tr bgcolor="#999999" >
		   <td align="center" valign="middle" class="tableheader3">&nbsp;Search PO's and/or Job's that contain:&nbsp;<input type="text" name="searchby" size="20" maxlength="40" value="<%=str_searchby.trim()%>">&nbsp;</td>
		<tr bgcolor="#999999" >
		   <td align="center" valign="middle" class="tableheader3">&nbsp;<input type="submit" value="Submit" name="Submit"></td>
		</tr>
	</table>
   </form>


   <table cellpadding="3" cellspacing="0" border="0" width="650">

      <tr>
         <td bgcolor="#ffffff" align="left" width="515" class="tableheader3"><!--NUM_RECORDS-->&nbsp;
                <%  if ( num_total > 0 ) {   %>
                          <%=num_current+1%> - 
                         <%  if ( num_nextstart < num_total ) {    %>
                                           <%=num_nextstart%> of 
                         <%  }else {  %>
                                           <%=num_total%> of 
                        <%   }
                       }
                        %>
               <%  if ( !str_total.trim().equals("0")  )  %> 
               		<%=str_total%> record(s).

         </td>
         </td>
         <td align="right" width="90">
               <%   if ( num_prevstart >= 0 && num_current != 0 ) {    %>
                                    <form action="equipOnRent.jsp" method=POST> 
                                          <input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
                                          <input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
                                          <input type=image src="images/prev.gif" height="40" width="87" border="0">
                                    </form>
                <%  }   %>
         </td>

         <td width="65">
                <%   if ( num_nextstart < num_total ) {%>
                                    <form action="equipOnRent.jsp" method=POST> 
                                         <input type="hidden" name="startnumber" value="<%=num_nextstart%>">
                                         <input type="hidden" name="jobnumber" value= "<%=jobnumber%>"> 
                                         <input type=image src="images/next.gif" height="40" width="62" border=0> 
                                    </form>
                  <%   }    %>
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

  pohistorybean.cleanup();	// RI003
  pohistorybean.endcurrentConnection(pohistorybeanconnection);

%>

</BODY>
</HTML>