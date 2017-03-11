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
//    Index    User Id        Date       Project          Desciption
//    ------  -----------  -----------  ---------------  ----------------------------------------------------------
//    x001     DTC9028      04/10/03     SR28586 PCR3     Create a search page
//    x002     DTC9028      04/28/03     SR28586 PCR1     Add logic to check security for a customer rep
//    x003     DTC9028      01/12/05     TT404162         Added additional RHTYPE and RHOTYP logic for the "transtype=sales"
//    RI004    DTC2073      02/01/05     SR31413 PCR26    Add copyright year dynamically to the page.
//    RI005    DTC9028      08/01/05     SR31413 PCR19    Implement datasource modification.
// ***************************************************************************

String[]    names = session.getValueNames();
 
if ( names.length <= 0 || names[0] == "")
  response.sendRedirect("securityFailure.jsp");	
%>

<SCRIPT LANGUAGE='JavaScript'> 
	function SetInitialFocus(){ document.searchform['contractsearch'].focus(); }window.onload = SetInitialFocus; 
</SCRIPT>

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
<%@ page language="java" import="java.sql.*, java.util.* " errorPage="ErrorPage.jsp"%>
<jsp:useBean id="searchbean" class="etrieveweb.etrievebean.searchBean" scope="page" />
<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />

 
<!-- get input parameters -->
<%

  String str_username         = (String)session.getValue("username");
  String str_customer          = (String) session.getValue("customer");
  String str_company          = (String) session.getValue("company");
  String str_datalib              = (String) session.getValue("datalib");
  String list_customer         = (String) session.getValue("list");
  String str_password         = (String)session.getValue("password");
  String str_as400url            = (String)session.getValue("as400url");
  String str_as400driver      = (String)session.getValue("as400driver");
  String str_companyname  = (String) session.getValue("companyname");
  String loginuserid             = (String)session.getValue("loginuserid");
  String str_status               = "";
  String startnumber             = request.getParameter("startnumber");
  String jobnumber               = request.getParameter("jobnumber");
  String contractsearch        = request.getParameter("contractsearch");
  String searchbutton          = request.getParameter("SearchButton");
  String str_total                   =  "";
   String str_userid                  = "";

  if ( loginuserid == null )
            loginuserid = "";
  
 if ( startnumber == null )
	startnumber = "0";

 if ( jobnumber == null )
	jobnumber = "";

 if ( contractsearch == null )
	contractsearch = "";

 if ( searchbutton == null )
	searchbutton = "";

 if ( contractsearch.trim().equals("")  )
	contractsearch = "";

 Connection MenuSecurityBeanconnection = null;
 Connection searchbeanconnection = null;

  //*************************************************************
  // RI004 - Retrieve today's date to be used for copyright statment.
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

  if(!MenuSecurityBean.isUserAuthorized(str_company, str_datalib, str_userid, "U07"))
  {
    response.sendRedirect("menu.jsp");
  }
    
  MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);
	
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
        <td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;search&nbsp;&nbsp;&nbsp;</td>
        <td></td>
      </tr>

   </table>

   <br>

   <center>
      <form name="searchform" action="search.jsp" method=POST>
           <table cellpadding="0" cellspacing="1" border="0" width="650">
              <tr>
                  <td width="25">&nbsp;</td>
                  <td width="625" class="menu" align="left">Search by contract or invoice&nbsp;<input type="text"  maxLength="8" size="13" name="contractsearch">&nbsp;&nbsp;<input type="submit" value="Search" name="SearchButton"></td>
              </tr>
          </table>
      </form>
   </center>

   <br>

   <%   if  (  searchbutton.equals("Search")  &&  !contractsearch.equals("")  )   {   

	// ***************************************************
	// Make a new AS400 connection to retrieve account information
	// ***************************************************

	searchbeanconnection = searchbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

	String str_color    = "#cccccc";
	String str_contract = "";
	String str_startdate = "";
	String str_trandate = "";
	String str_sequence = "";
	String str_PO = "";
	String str_ordtype  = "";
	String str_type        = "";
	String str_typetext = "";

	str_total = searchbean.getNumRows( Integer.valueOf(str_customer).intValue(), Integer.valueOf(contractsearch).intValue(),  str_company, str_datalib );
  
	int num_display = 5000;
	int num_count = 0;
	int num_total = Integer.valueOf(str_total).intValue();
	int num_current = Integer.valueOf(startnumber).intValue();
	int num_nextstart = num_current + num_display;
	int num_prevstart = num_current - num_display;

	if ( num_prevstart <= 0 )
	        num_prevstart = 0;

	%>

	<center>


	<table cellpadding="3" cellspacing="1" border="0" width="650">

		<tr>
		   <td colspan="4" bgcolor="white" background="images/empty.gif" align="center"  valign="top" class="redbold">Search found <%=str_total%> record(s) for <%=contractsearch%>.</td>
		</tr>

	   <%  if (  !str_total.equals("0")  )  {  %>
		<tr>
		   <td bgcolor="#000000" background="images/empty.gif" align="left" valign="top" width="25%" class="whitemid">Contract / Invoice</td>
		   <td bgcolor="#000000" background="images/empty.gif" align="left" valign="top" width="15%" class="whitemid">Date</td>
		   <td bgcolor="#000000" background="images/empty.gif" align="left" valign="top" width="40%" class="whitemid">Purchase Order</td>
		   <td bgcolor="#000000" background="images/empty.gif" align="left" valign="top" width="20%" class="whitemid">Type</td>
		</tr>
	   <%  }  %>


	<%
	if ( searchbean.getRows(Integer.valueOf(str_customer).intValue(), Integer.valueOf(contractsearch).intValue(),  str_company, str_datalib,  num_current ) ) {

		while ( searchbean.getNext() && num_count < num_display) {

			num_count++;

			if ( str_color.equals("#cccccc") )
			        str_color = "#999999";
			else 
			        str_color = "#cccccc";
    
			str_contract   = searchbean.getColumn("RHCON#");
			str_sequence = searchbean.getColumn("RHISEQ");
			str_startdate  = searchbean.getColumn("RHDATO");
			str_trandate   = searchbean.getColumn("RHSYSD");
			str_PO            = searchbean.getColumn("RHPO#");
			str_ordtype   = searchbean.getColumn("RHOTYP");
			str_type         = searchbean.getColumn("RHTYPE");

			
			if ( str_type.equals("R") )
				str_typetext = "Reservation";
			else if ( str_type.equals("X") )
				str_typetext = "Quote";
			else if ( str_type.equals("W") )
				str_typetext = "Work Order";
			else if ( str_type.equals("S") ||  str_ordtype.equals("Y")   )
				str_typetext = "Sales Order";
			else if ( !str_sequence.equals("0") )
				str_typetext = "Invoice";
			else if ( str_type.equals("O") )
				str_typetext = "Rental Contract";
			else 
				str_typetext = "";
                  
			// **************************
			// Format the sequence number 
			// **************************

			while( str_sequence.length() < 3 ) {
			         str_sequence = "0" + str_sequence;
			}

			// **************
			// Start Date
			// **************

			if ( !str_startdate.trim().equals("0") && str_startdate.length() == 8)
			        str_startdate = str_startdate.substring(4, 6) + "/" + str_startdate.substring(6, 8) + "/" + str_startdate.substring(2, 4);
			else
			        str_startdate = "";


			// **************
			// Transaction Date
			// **************

			if ( !str_trandate.trim().equals("0") && str_trandate.length() == 8)
			        str_trandate = str_trandate.substring(4, 6) + "/" + str_trandate.substring(6, 8) + "/" + str_trandate.substring(2, 4);
			else
			        str_trandate = "";


			// ********************************
			// Build the url based on the transaction
			// ********************************

			String url = "contractDetail.jsp?contract=" + str_contract + "&sequence=" + str_sequence;;
			
           if ( str_type.equals("F") ) 
				url = url.trim() + "&transtype=rp con";
           else if ( str_type.equals("Q") ) 
				url = url.trim() + "&transtype=eqp sale";
           else if ( str_type.equals("S") || str_type.equals("I") || str_type.equals("W") || ( str_type.equals("X") && str_ordtype.equals("S") ) ) 
                url = url.trim() + "&transtype=sales";

			if (  !str_sequence.equals("000")  )  
				str_contract = str_contract.trim() + "-" + str_sequence;
			
				
      %>

			<tr>
			    <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata">&nbsp;&nbsp;<a href="<%=url%>"><%=str_contract%></a></td>
			    <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata"><%=str_trandate%></td>
			    <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata"><%=str_PO%>&nbsp;</td>
			    <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" valign="top" class="tabledata"><%=str_typetext%>&nbsp;</td>
			</tr>

                   <%
		}
	}
                   %>

	</table>

	<%  if (  !str_total.equals("0")  )  {  %>

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

		 <%=str_total%> record(s).</td>
	      </td>
	      <td align="right" width="90">
                           <%   if ( num_prevstart >= 0 && num_current != 0 ) {    %>
                                       <form action="search.jsp" method=POST> 
                                           <input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
                                           <input type="hidden" name="jobnumber" value= "<%=jobnumber%>">
                                           <input type=image src="images/prev.gif" height="40" width="87" border="0">
                                     </form>
                           <%  }   %>
	      </td>

	      <td width="65">
                           <%   if ( num_nextstart < num_total ) {%>
                                    <form action="search.jsp" method=POST> 
                                         <input type="hidden" name="startnumber" value="<%=num_nextstart%>">
                                         <input type="hidden" name="jobnumber" value= "<%=jobnumber%>"> 
                                         <input type=image src="images/next.gif" height="40" width="62" border=0> 
                                    </form>
                           <%   }    %>
	      </td>

	   </tr>

	</table>

	<%  }  %>

	<br><br>

</center>

<%  }  %>


<a href="javascript:history.go(-1)"><IMG src="images/back.gif" height="40" width="62" border="0"></a> 
<a href="menu.jsp"><IMG src="images/menu.gif" height="40" width="62" border="0"></a> 
<a href="javascript:window.close()"><IMG src="images/closeReports.gif" height="40" width="123" border="0"></a> 
<%   if  (  searchbutton.equals("Search")  &&  !contractsearch.equals("")  &&  !str_total.equals("0")  )   {   %>
                <a href="javascript:window.print()"><img src="images/print.gif" width="67" height="40" border="0"></a>
<%  }  %>
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

  searchbean.cleanup();	// RI005
  searchbean.endcurrentConnection(searchbeanconnection);

%>

</BODY>
</HTML>