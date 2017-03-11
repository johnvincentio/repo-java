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
//    Index     User Id        Date        Project           Desciption
//   --------  -----------  -----------  -----------------  ----------------------------------------------------------
//    x001      DTC9028      11/14/02     SR26609 PCR1       Made changes related to new log-in logic
//    x002      DTC9028      11/14/02     SR26609 PCR1       Made changes end the connection
//    x003      DTC9028      12/14/02     SR26609 PCR1       Made changes to close browser window
//    x004      DTC9028      12/23/02     SR26609 PCR1       Added account number to the table
//    x005      DTC9028      11/26/02     SR26609 PCR1       Incorporate Hertz images
//    x006      DTC9028      11/26/02     SR26609 PCR1       Changed browser title bar
//    x007      DTC9028      01/24/03     SR26609 PCR1       Fix the align ment of the next/prev images
//    x008      DTC9028      12/26/02     SR26609 PCR1       Change the copyright statement 
//    x009      DTC9028      04/28/03     SR28586 PCR1       Add logic to check for a customer rep and display hyper-link button
//    x010      DTC2073      04/16/04     SR28586 PCR23      Added last activity date
//    RI011     DTC2073      02/02/05     SR31413 PCR26      Add copyright year dynamically to the page
//    RI012     DTC2073      05/09/05     SR31413 PCR27      Add the full address for each customer
//    RI013     DTC9028      06/23/05     SR31413 PCR19      Implement the datasource connection
// *************************************************************************

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

<%@ page language="java" import="java.sql.*,java.util.*" errorPage="ErrorPage.jsp"%>
<jsp:useBean id="corplinkaccountbean" class="etrieveweb.etrievebean.corplinkaccountBean" scope="page" />

<%
String startnumber = request.getParameter("startnumber");
if ( startnumber == null )  
  startnumber = "0";

String str_corporate    = (String) session.getValue("corporate");
String str_customer     = (String) session.getValue("customer");
String str_company     = (String) session.getValue("company");
String str_datalib         = (String) session.getValue("datalib");
String str_username    = (String)session.getValue("username");
String str_password    = (String)session.getValue("password");
String str_as400url       = (String)session.getValue("as400url");
String str_as400driver = (String)session.getValue("as400driver");
String loginuserid        = (String)session.getValue("loginuserid");
String loginpassword  = (String)session.getValue("loginpassword");

if ( loginuserid == null )
  loginuserid = "";

if ( loginpassword == null )
  loginpassword = "";

Connection corplinkaccountbeanconnection = null;

//*************************************************************
// RI011 - Retrieve today's date to be used for copyright statment.
//*************************************************************
  
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  
 
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

		<br>
 
		<table>
			<tr>
				<td class="menu"><a href="servlet/EtrieveWebCheckCustomerIdServlet?typeyn=y&id=<%=str_corporate%>">National Accounts</a> </td>
			</tr>
			<tr>
				<td class="redbold">Accounts with no date have had no activity in the past twelve months.</td>
			</tr>
		</table>

		<center>

			<table cellpadding="3" cellspacing="1" border="0" width="650">

				<tr>
					<td background="images/empty.gif" bgcolor="#000000" align="left" valign="bottom" width="40%" class="whitemid">Account Name</td>
					<td background="images/empty.gif" bgcolor="#000000" align="left" valign="bottom" width="35%" class="whitemid">Address</td>
					<td background="images/empty.gif" bgcolor="#000000" align="left" valign="bottom" width="13%" class="whitemid">Account<br> Number</td>
					<td background="images/empty.gif" bgcolor="#000000" align="left" valign="bottom" width="12%" class="whitemid">Last Activity</td>
				</tr>

				<%

				// ***************************************************
				// Make a new AS400 connection to retrieve accounts information
				// ***************************************************

				corplinkaccountbeanconnection = corplinkaccountbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

				String str_color    = "#cccccc";
				String str_total = corplinkaccountbean.getNumRows(Integer.valueOf(str_corporate).intValue(), str_company, str_datalib);

				int num_display = 1000;
				int num_count = 0;
				int num_total = Integer.valueOf(str_total).intValue();
				int num_current = Integer.valueOf(startnumber).intValue();
				int num_nextstart = num_current + num_display;
				int num_prevstart = num_current - num_display;

				if ( num_prevstart <= 0 ) {
					num_prevstart = 0;
				}

				if ( corplinkaccountbean.getRows( Integer.valueOf(str_corporate).intValue(), str_company, str_datalib, num_current) ) {

					while ( corplinkaccountbean.getNext() && num_count < num_display) {

						num_count++;

						if ( str_color.equals("#cccccc") )
							str_color = "#999999";
						else 
							str_color = "#cccccc";
 
						String str_name = corplinkaccountbean.getColumn("CMNAME");
						String str_id = corplinkaccountbean.getColumn("CMCUS#");
						String str_address1 = corplinkaccountbean.getColumn("CMADR1").trim();
						String str_address2 = corplinkaccountbean.getColumn("CMADR2").trim();
						String str_zip = corplinkaccountbean.getColumn("CMZIP").trim();
						
						if ( str_zip.length() == 9 )
							str_zip = str_zip.substring(0, 5) + "-" + str_zip.substring(5, 9);

			
						// *************************************
						// RI012 - Format the customer address 
						// *************************************
						
						String customeraddress = "";
						
						if ( !str_address1.equals("") ) {
							
							int decpos1 = str_address1.indexOf("ATTN"); 
							int decpos2 = str_address1.indexOf("ATTEN"); 
							int decpos3 = str_address1.indexOf("DBA"); 
											
							if (decpos1 == -1 && decpos2 == -1 && decpos3 == -1)
								customeraddress = str_address1.trim();
								
						}
						
						if ( !str_address2.equals("") ) {
							
							int decpos1 = str_address2.indexOf("ATTN"); 
							int decpos2 = str_address2.indexOf("ATTEN"); 
							int decpos3 = str_address2.indexOf("DBA"); 
														
							if (decpos1 == -1 && decpos2 == -1 && decpos3 == -1) {
							
								if ( !customeraddress.equals("") )
									customeraddress = customeraddress + "<BR>";
									
								customeraddress = customeraddress.trim() + str_address2.trim();
								
							}
								
						}
						
						if ( !corplinkaccountbean.getColumn("CMCITY").trim().equals("") )  {
						
							if ( !customeraddress.equals("") )
									customeraddress = customeraddress + "<BR>";
									
							customeraddress = customeraddress.trim() + corplinkaccountbean.getColumn("CMCITY").trim() + ", ";
						}
						
						customeraddress = customeraddress.trim() + " " + corplinkaccountbean.getColumn("CMSTAT").trim() + " " + str_zip.trim() + "<BR><BR>";
							
						// **************************************
						//  Retrieve and format the last activity date
						// **************************************

						String str_lastdate = "";

						str_lastdate = corplinkaccountbean.getDate();

						if ( !str_lastdate.trim().equals("0") && str_lastdate.length() == 8)
							str_lastdate = str_lastdate.substring(4, 6) + "/" + str_lastdate.substring(6, 8) + "/" + str_lastdate.substring(2, 4);
						else
							str_lastdate = "";
					%>

					<tr>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><a href="servlet/EtrieveWebCheckCustomerIdServlet?typeyn=n&id=<%=str_id%>"><!--CUSTOMER--><%=str_name%></a></td>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><%=customeraddress%></td>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><!--CUSTOMER NUMBER--><%=str_id%></td>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><!-- : Last Activity Date --><%=str_lastdate%></td>
					</tr>

			<%
				}
			}

			%>

		</table>

		<br>

		<table border="0" width="650" cellspacing="0" cellpadding="0">

            <tr>
               <td width="515" background="images/empty.gif" bgcolor="#ffffff" align="left" class="tableheader3"><!--NUM_RECORDS-->

                      <%  if ( num_total > 0 ) {     %>
                               &nbsp;&nbsp;<%=num_current+1%> - 

                              <%   if ( num_nextstart < num_total ) {   %>
                                           <%=num_nextstart%> of 
                              <%  }else {   %>
                                           <%=num_total%> of 
                             <%
                                      }
                              }
                             %>

                      <%=str_total%> record(s).
                   </td>

                   <td align="right" width="90">
                      <%  if ( num_prevstart >= 0 && num_current != 0 ) {    %>
                              <form action="corpLinkAccounts.jsp" method=POST> 
                                     <input type="hidden" name="startnumber" value="<%=num_prevstart%>"> 
                                     <input type=image src="images/prev.gif" height="40" width="87" border="0">
                              </form>
                     <% 
                            }
                      %>
                  </td>

                  <td width="65"> 
                      <%   if ( num_nextstart < num_total ) {   %>
                                <form action="corpLinkAccounts.jsp" method=GET> 
                                        <input type="hidden" name="startnumber" value="<%=num_nextstart%>">
                                        <input type=image src="images/next.gif" height="40" width="62" border=0> 
                                </form>
                       <% 
                               }
                       %>
                 </td>
             </tr>

         </table>

      </center>
  
      <br>

      <a href="javascript:window.close()"><IMG src="images/closeReports.gif" height="40" width="123" border="0"></a> 
      <a href="javascript:window.print()"><img src="images/print.gif" width="67" height="40" border="0"></a>
      <%  if (  !loginuserid.equals("")   &&  !loginpassword.equals("")  )  { %>   
                        <a href="customerservice.jsp"><IMG src="images/viewNewAccount.gif" height="40" width="150" border="0"></a> 
      <%  }  %>

      <br>

      <hr>

      <table border="0" width="650" cellspacing="0" cellpadding="0">
         <tr>
            <td background="images/empty.gif" class="footerblack"><center>&copy; <%=cpYear%> The Hertz Corporation.  All Rights Reserved.  &reg REG. U.S. PAT. OFF.</center><br><br></td>
         </tr>
      </table>

   </div>



<%
  // **********************
  // End current connection
  //**********************
corplinkaccountbean.cleanup();	// RI013
corplinkaccountbean.endcurrentConnection(corplinkaccountbeanconnection);

%>

</BODY>

</HTML>
