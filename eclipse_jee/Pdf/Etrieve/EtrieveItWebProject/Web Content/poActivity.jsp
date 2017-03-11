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
//    Index    User Id       Date       Project                       Desciption
//   -------  ----------  -----------  ----------------   ----------------------------------------------------------
//    x001     DTC9028     11/14/03     SR28586 PCR8       Create PO Activity List
//    x002     DTC9028     01/12/05     TT404162           Corrected qty error for bulk items   
//    RI003    DTC2073     02/01/05     SR31413 PCR26      Add copyright year dynamically to the page.
//    RI004    DTC2073     04/22/05     SR31413 PCR29      Add Estimated Return date to Equip on Rent 
//    RI005    DTC9028     08/01/05     SR31413 PCR19      Datasource implementation modification
//    RI006    DTC2073     12/16/05     SR35880            Add comments for re-rent items (catg/class 975-0001)
//    RI007    DTC2073     02/03/06     SR35873            Abbreviated Equipment Release
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
	<jsp:useBean id="poactivitybean" class="etrieveweb.etrievebean.poActivityBean" scope="page" />
	<jsp:useBean id="MenuSecurityBean" class="etrieveweb.etrievebean.MenuSecurityBean" scope="page" />

 
	<!-- get input parameters -->
	<%
	String str_username         = (String)session.getValue("username");
	String str_customer         = (String) session.getValue("customer");
	String str_company         = (String) session.getValue("company");
	String str_datalib             = (String) session.getValue("datalib");
	String list_customer        = (String) session.getValue("list");
	String str_password        = (String)session.getValue("password");
	String str_as400url          = (String)session.getValue("as400url");
	String str_as400driver     = (String)session.getValue("as400driver");
	String loginuserid             =  (String)session.getValue("loginuserid");
	String totalrecords           = request.getParameter("totalrecords");
	String str_userid              = "";
	String str_checkboxname = "";
	String str_checkboxvalue = "";
	String pojobselect = "";
	String selection_list = "";
	String pojob_SQLlist = "";
	String contract_SQLlist = "";
	String str_RDCON  =  "";
	String str_RDISEQ   = "";
	String str_RDSEQ   = "";
	String str_RDITEM  =  "";
	String str_RDQTY    =  "";
	String str_RDLOC     = "";
	String str_RDLBDT  = "";
	String str_RD$BLD  = "";
	String str_RDAMT$  = "";
	String str_RHORDB = "";
	String str_ECDESC   = "";
	String str_RHPO     = "";
	String str_RHJOB   = "";
	String str_RHDATO = "";
	String str_RHLRDT = "";
	String str_RHOTYP   = "";
	String str_color    =  "";
	String str_invamt = "";
	String str_invbal = "";
	String str_estRetDate = "";
	boolean overdueContract = false;

	int pojob_count = 0;

	String  [] str_POarray   = new String[1000];
	String  [] str_JOBarray = new String[1000];
	double [] orgAmtarray  = new double[1000];
	double [] balAmtarray  = new double[1000];

	double num_invamt = 0;
	double num_invbal = 0;

	DecimalFormat df   = new DecimalFormat("###,###,###,###.##");    

	//*************************************************************
	// RI003 - Retrieve today's date to be used for copyright statment.
	//*************************************************************
  
	Calendar cpToday = Calendar.getInstance();
	int cpYear = cpToday.get(Calendar.YEAR) ;  
  
	// ********************************************
	// RI004 - Retrieve the current system date
	//*********************************************

	Calendar Today = Calendar.getInstance();
	int todayDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);
  
  
	if ( loginuserid == null )
		loginuserid = "";

	if ( totalrecords == null )
		totalrecords = "";

	if ( totalrecords.equals("") )
		totalrecords = "0";

	int recordcount = Integer.valueOf(totalrecords).intValue();

	String    equipChangeAuth = (String)    session.getAttribute("equipChangeAuth");	// RI007
	ArrayList EqpExtContracts = (ArrayList) session.getAttribute("EqpExtContracts");	// RI007
	ArrayList EqpRelContracts = (ArrayList) session.getAttribute("EqpRelContracts");	// RI007
	ArrayList EqpRelLineNum   = (ArrayList) session.getAttribute("EqpRelLineNum");		// RI007
	ArrayList EqpRelQtyRel    = (ArrayList) session.getAttribute("EqpRelQtyRel");		// RI007

	boolean prevReleases = true;		// RI007
	boolean prevExtend   = true;		// RI007

	if (EqpRelContracts == null || EqpRelLineNum == null || EqpRelQtyRel == null)		// RI007
		prevReleases = false;															// RI007

	if (EqpExtContracts == null)	// RI007
		prevExtend   = false;		// RI007

	if (equipChangeAuth == null)	// RI007
		equipChangeAuth = "No";		// RI007
	
	Connection MenuSecurityBeanconnection = null;
	Connection poactivitybeanconnection = null;

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

	// **********************
	// End current connection
	//**********************

	MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);


	// ***************************************************************************
	//  Retrieve all the checkbox values that were selected and build the select string for the SQL
	//  NOTE:  The logic will also check for single quotes embedded in the PO or JOB fields and 
	//  parse out the values using the LIKE condition
	// ***************************************************************************

	str_color    = "#cccccc";

	for (int val  = 0; val < recordcount+2; ++val)

	{
		str_checkboxname = "checkbox" +  val;
		str_checkboxvalue   = request.getParameter(str_checkboxname);

		int startpos = 0; 
		int endpos = 0; 

		if ( str_checkboxvalue == null )
			str_checkboxvalue   = "";

		if ( !str_checkboxvalue.equals("") )  {

			if ( str_color.equals("#cccccc") )
				str_color = "#999999";
			else 
				str_color = "#cccccc";

			startpos = str_checkboxvalue.indexOf("postart"); 
			endpos = str_checkboxvalue.indexOf("poend"); 

			String str_PO = str_checkboxvalue.substring(startpos+7, endpos).trim();
			
			if ( str_PO.equals(" ")  ||  str_PO.equals("") )
				str_PO = "";
			else
				str_PO =  str_checkboxvalue.substring(startpos+7, endpos).trim();

			startpos = str_checkboxvalue.indexOf("jobstart"); 
			endpos = str_checkboxvalue.indexOf("jobend"); 

			String str_JOB = str_checkboxvalue.substring(startpos+8, endpos).trim();

			if ( str_JOB.equals(" ") ||  str_JOB.equals("") )
				str_JOB = "";
			else
				str_JOB = str_checkboxvalue.substring(startpos+8, endpos).trim();

			if  ( !pojob_SQLlist.equals("") )  {
				pojob_SQLlist = pojob_SQLlist + " or ";
			}

                  			selection_list = selection_list.trim()  +  "<tr bgcolor=" + str_color + "><td background=images/empty.gif  align=left  valign=top  class=tabledata>"  +  str_PO.trim()  +  "</td><td background=images/empty.gif  align=left  valign=top  class=tabledata>"  +  str_JOB.trim()  +  "</td></tr> ";

			startpos = str_checkboxvalue.indexOf("postart"); 

			pojob_SQLlist = pojob_SQLlist.trim()  + str_checkboxvalue.substring(0, startpos-1);

			// **********************************************
			// Setup arrays to be used later for the summary section
			// **********************************************

			str_POarray[pojob_count] = str_PO;
			str_JOBarray[pojob_count] = str_JOB;
			orgAmtarray[pojob_count] = 0;
			balAmtarray[pojob_count] = 0;
			pojob_count++;

		}

		if ( pojob_count >= 15 )
			break;

	}

	if ( !pojob_SQLlist.equals("") )  
		pojobselect = " and ( "  + pojob_SQLlist +  " ) ";
	else
		  response.sendRedirect("poHistory.jsp");	


%>

<BODY BGCOLOR="#ffffff">

<div ID=maintext>

	<table border="0" width="900" cellspacing="0" cellpadding="0">
		<tr>
		   <td width="300" align="right"><img src="images/goldReports_header.gif" width="300" height="100"></td>
		   <td width="350" align="center">&nbsp;</td>
		   <td width="250" align="left"><img src="images/HERClogo.gif" width="250" height="100"></td>
		</tr>
	</table>


	<table border="0" width="900" cellspacing="0" cellpadding="0">

		<tr>
		   <td background="images/bottom_back.gif" width="25">&nbsp;</td>
		   <td background="images/bottom_back.gif" width="845"><!--COMPANY_NAME--><a class="company"><%=str_companyname.trim()%></a>&nbsp;&nbsp;-&nbsp;&nbsp;Account Number: <!--COMPANY_NUMBER--><a class="data"><%=str_customer%></a></td>
		   <td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
		</tr>

		<tr>
		   <td><img src="images/empty.gif" height="10"></td>
		   <td><img src="images/arrow.gif" height="10" align="right"></td>
		   <td></td>
		</tr>

		<tr>
		   <td><img src="images/empty.gif" height="30"></td>
		   <td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;purchase order activity&nbsp;&nbsp;&nbsp;</td>
		   <td></td>
		</tr>

	</table>

	<br>

	<table cellpadding="4" cellspacing="1" border="0" width="750">

		<tr>
	   	   <td bgcolor="#ffffff" align="left"  class="tableheader3" colspan="2">&nbsp;Selection(s) made</td>
		</tr>

		<tr>
	   	   <td bgcolor="#000000"  background="images/empty.gif" align="left"  width="50%" valign="top"  class="whitemid">&nbsp;Purchase Order</td>
	   	   <td bgcolor="#000000"  background="images/empty.gif" align="left"  width="50%" valign="top"  class="whitemid">&nbsp;Job Number</td>
		</tr>

		<%=selection_list.trim()%>

	</table>

	<br>

	<center>

    <%
	// ***************************************
	// Equipment on rent 
	// ***************************************
	%>

	<table cellpadding="3" cellspacing="1" border="0" width="900">

		<tr>
	   	   <td bgcolor="#ffffff" align="left"  class="tableheader3" colspan="11">&nbsp;Equipment On Rent<br>&nbsp;Note: Equipment scheduled to be on rent less than one month will have a blank last bill date.<br>&nbsp;Items that have an Estimated Return Date in red are overdue.</td>
		</tr>

		<tr>
	   	   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="7%"  class="whitemid">Contract</td>
	   	   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="7%"  class="whitemid">Start<br>Date</td>
	   	   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="8%"  class="whitemid">Equip#</td>
	   	   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="5%"  class="whitemid">Qty</td>
	   	   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="18%" class="whitemid">Description</td>
	   	   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="12%" class="whitemid">Ordered<br>by</td>
           <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="7%"  class="whitemid">Est<Br>Return<br>Date</td>
	   	   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="6%"  class="whitemid">On<br>Pickup<br>Ticket</td>
	   	   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="7%"  class="whitemid">Billed to Date</td>
	   	   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="7%"  class="whitemid">Last Bill Date</td> 
	   	   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="16%" class="whitemid">Purchase Order /<br>Job Number</td>
		</tr>

		<%

		// ***************************************************
		// Make a new AS400 connection to retrieve account information
		// ***************************************************

		int num_count = 0;
		int num_current = 0;

		if  (  !pojob_SQLlist.equals("")  )   {

			poactivitybeanconnection = poactivitybean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

			str_color    = "#cccccc";

			if ( poactivitybean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_current, pojobselect ) ) {

				while ( poactivitybean.getNext() ) {

					num_count++;

					if ( str_color.equals("#cccccc") )
						str_color = "#999999";
					else 
						str_color = "#cccccc";

					str_RDCON    =  poactivitybean.getColumn("RDCON#").trim();
					str_RDSEQ     = poactivitybean.getColumn("RDSEQ#");
					str_RDITEM  =  poactivitybean.getColumn("RDITEM").trim();
					str_RDQTY    =  poactivitybean.getColumn("RDQTY").trim();
					str_RDLOC     = poactivitybean.getColumn("RDLOC");
					str_RDLBDT  = poactivitybean.getColumn("RDLBDT");
					str_RD$BLD  = poactivitybean.getColumn("RD$BLD");
					str_RHORDB = poactivitybean.getColumn("RHORDB").trim();
					str_ECDESC   = poactivitybean.getColumn("ECDESC");
					str_RHPO       = poactivitybean.getColumn("RHPO#").trim();
					str_RHJOB     = poactivitybean.getColumn("RHJOB#").trim();
					str_RHDATO = poactivitybean.getColumn("RHDATO");
					str_estRetDate = poactivitybean.getColumn("RHERDT");
					overdueContract = false;
						
					// ********************
					//  Get pick-up status
					// ********************

                    String   str_pickup = poactivitybean.getPickupStatus(str_company, str_datalib, Integer.valueOf(str_RDCON).intValue(), Integer.valueOf(str_RDSEQ).intValue(), str_RDITEM  );
  
                    if ( str_pickup.equals("OP") )
                    	str_pickup = "Yes";
                    else
                        str_pickup = "No";
 
					// **************
					// Start Date
					// **************

               
					if ( !str_RHDATO.trim().equals("0") && str_RHDATO.length() == 8)
						str_RHDATO = str_RHDATO.substring(4, 6) + "/" + str_RHDATO.substring(6, 8) + "/" + str_RHDATO.substring(2, 4);
					else
						str_RHDATO = "";

					// **************
					// Next Bill Date
					// **************

					if ( !str_RDLBDT.trim().equals("0") && str_RDLBDT.length() == 8)
						str_RDLBDT = str_RDLBDT.substring(4, 6) + "/" + str_RDLBDT.substring(6, 8) + "/" + str_RDLBDT.substring(2, 4);
					else
						str_RDLBDT  = "";

					// ***********************
					// Estimated Return Date
					// ***********************

					if ( !str_estRetDate.trim().equals("0") && str_estRetDate.length() == 8) {

						int estimatedReturn = Integer.parseInt(str_estRetDate);

						if ( estimatedReturn < todayDate )
							overdueContract = true;

						str_estRetDate = str_estRetDate.substring(4, 6) + "/" + str_estRetDate.substring(6, 8) + "/" + str_estRetDate.substring(2, 4);
							
					} else {
						str_estRetDate = "";
					}

					// **********************************
					//  Format the right decimal numbers
					// **********************************

					int decpos = str_RD$BLD.indexOf("."); 

					if (decpos == -1 )
						str_RD$BLD = str_RD$BLD.trim() + ".00";

					if (  (   (str_RD$BLD.length()-1)  - decpos ==  1 )  )
						str_RD$BLD = str_RD$BLD.trim() + "0";

					// *************************************************
					//  Format the description is longer than 25 bytes and has 
					//  a slash in the text
					// *************************************************

					int descpos = str_ECDESC.indexOf(" "); 

					if (descpos == -1  ||   descpos > 1)  {

						String str_newECDESC = "";
						
						for (int val4  = 0; val4 < str_ECDESC.length(); ++val4)   {

							if  ( str_ECDESC.substring(val4, val4+1).equals("/")  &&  val4 > 1)
								str_newECDESC = str_newECDESC.trim() + " " +  str_ECDESC.substring(val4, val4+1) + " ";
							else
								str_newECDESC = str_newECDESC + str_ECDESC.substring(val4, val4+1);
						}

						str_ECDESC = str_newECDESC;
					}

					// ************************************************************************
					//  RI007 - Check if this equipment qualifies for online release or extend.
					//  This section will determine if a release has already been done for
					//  the equipment within this session
					// ************************************************************************
						
					if( prevReleases && str_pickup.equals("No") && equipChangeAuth.equals("Yes") )	// RI007
					{	
						String str_qty = poactivitybean.getColumn("RDQTY").trim();					// RI007
						
						int num_qty = 0;								// RI007
							
						if (str_qty == null) str_qty = "0";				// RI007
							
						int i = str_qty.indexOf(".");					// RI007
									
						if (i > 0)	str_qty = str_qty.substring(0,i);	// RI007
									
						num_qty = Integer.valueOf(str_qty).intValue();	// RI007

						int lstAllRelQty = 0;							// RI007
						int size = EqpRelContracts.size();				// RI007
							
						for	(i=0; i < size; i++)						// RI007
						{
							
							String lstCon     = (String) EqpRelContracts.get(i);				// RI007
							String lstLineNum = (String) EqpRelLineNum.get(i);					// RI007
							String lstQty     = (String) EqpRelQtyRel.get(i);					// RI007
										
							if (lstCon.trim().equals(str_RDCON) && lstLineNum.trim().equals(str_RDSEQ) )		// RI007
								lstAllRelQty = lstAllRelQty + Integer.valueOf(lstQty).intValue() ;				// RI007
						}	
								
						if(lstAllRelQty >= num_qty )	// RI007
							str_pickup = "Yes";			// RI007			
					}

					// ************************************************************************
					//  RI007 - Check if this contract qualifies for online extension.  This 
					//  section will determine if an earlier extension has been done for the
					//  contract in the same session.       
					// ************************************************************************
						
					boolean allowExtension = true;	// RI007
						
					if( prevExtend )				// RI007
					{
						
						int size = EqpExtContracts.size();		// RI007
		
						for	( int i=0; i<size ; i++ )			// RI007
						{					
							String lstCon = (String)EqpExtContracts.get(i);		// RI007
							
							if (lstCon.trim().equals(str_RDCON.trim()) )		// RI007
							{
									allowExtension = false;	// RI007
									break;					// RI007
							}
						}
							
					}
					// *****************************************************
					//  RI006 - Retrieve the description for rerent items
					//  Note: the RDISEQ number is defaulted to 0
					// *****************************************************

					String str_itemcomments = "";		// RI006
					int int_seq = 0;					// RI006 
					int int_catg = Integer.valueOf(poactivitybean.getColumn("RDCATG")).intValue();		//  RI006
					int int_class = Integer.valueOf(poactivitybean.getColumn("RDCLAS")).intValue();		//  RI006
							
					if ( str_company.equals("HG") && int_catg == 975 && int_class == 1 )	{		//  RI006
						str_itemcomments = "<br>" + poactivitybean.getItemComments(str_company, str_datalib, Integer.valueOf(str_RDCON).intValue(), int_seq, Integer.valueOf(str_RDSEQ).intValue()); 	//  RI006
					} else {		//  RI006
						str_itemcomments = "";	//  RI006		
					}		// RI006	
      			%>

					<tr>
					   <td bgcolor="<%=str_color%>"  background="images/empty.gif" align="center" valign="top"  class="tabledata"><a href="contractDetail.jsp?contract=<%=str_RDCON%>&sequence=000"><%=str_RDCON%></a><br>&nbsp;<br><a href="contractHistory.jsp?contract=<%=str_RDCON%>&sequence=000&ret=PO">View<br>Related<br>Documents</a></td>
					   <td bgcolor="<%=str_color%>"  background="images/empty.gif" align="right"  valign="top"  class="tabledata"><%=str_RHDATO%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>"  background="images/empty.gif" align="left"   valign="top"  class="tabledata"><%=str_RDITEM%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>"  background="images/empty.gif" align="right"  valign="top"  class="tabledata"><%=str_RDQTY%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>"  background="images/empty.gif" align="left"   valign="top"  class="tabledata"><%=str_ECDESC%>&nbsp;<%=str_itemcomments.trim()%></td>
					   <td bgcolor="<%=str_color%>"  background="images/empty.gif" align="left"   valign="top"  class="tabledata"><%=str_RHORDB%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>"  background="images/empty.gif" align="right"  valign="top"  class="tabledata<% if (overdueContract) {%>red<%}%>"><%=str_estRetDate%>&nbsp;<br><br>
							<% if ( equipChangeAuth.equals("Yes") && allowExtension && (str_pickup.equals("No") )) {  // RI007 %> 
								<a href="extendEquipment.jsp?contract=<%=str_RDCON%>"><IMG src="images/extend.gif" height="21" width="50" BORDER=0 align="center" valign="bottom"></a> 
							<% } %>
					   </td>
					   <td bgcolor="<%=str_color%>"  background="images/empty.gif" align="center" valign="top"  class="tabledata"><%=str_pickup%>&nbsp;<br><br>
							<% if ( equipChangeAuth.equals("Yes") && (str_pickup.equals("No") )) { // RI007 %> 
								<a href="releaseEquipment.jsp?contract=<%=str_RDCON%>&lineNum=<%=str_RDSEQ%>"><IMG src="images/release.gif" height="21" width="50" BORDER=0 align="center" valign="bottom"></a>
							<% } %>
					   </td>
					   <td bgcolor="<%=str_color%>"  background="images/empty.gif" align="right"  valign="top"  class="tabledata"><%=str_RD$BLD%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>"  background="images/empty.gif" align="right"  valign="top"  class="tabledata"><%=str_RDLBDT%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>"  background="images/empty.gif" align="left"   valign="top"  class="tabledata"><%=str_RHPO%>&nbsp;<br><br><%=str_RHJOB%></td>
					</tr>

 			<%
				}
			}
		}

		%>

		<%     if  ( num_count == 0 )  {  %>
			<tr>
			   <td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="left"  valign="top"  class="tabledata" colspan="11">&nbsp;No equipment currently on rent for specified selection</td>
			</tr>
		<%     }     %>

   	</table>

	<%
	// *************************************
	// Display the Equipment History data
	// *************************************
	%>

	<br>

	<table cellpadding="3" cellspacing="1" border="0" width="900">

		<tr>
		   <td bgcolor="#ffffff" align="left"  class="tableheader3" colspan="10">&nbsp;Equipment History</td>
		</tr>

		<tr>
		   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="7%"  class="whitemid">&nbsp;Contract</td>
		   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="7%"  class="whitemid">&nbsp;Start Date</td>
		   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="8%" class="whitemid">&nbsp;Equip#</td>
		   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="5%"  class="whitemid">&nbsp;Qty</td>
		   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="25%" class="whitemid">&nbsp;Description</td>
		   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="5%"  class="whitemid">&nbsp;Loc</td>
		   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="12%" class="whitemid">&nbsp;Ordered<br>by</td>
		   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="7%"  class="whitemid">&nbsp;End Date</td>
		   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="8%" class="whitemid">&nbsp;Total Billed</td>
		   <td bgcolor="#000000"  background="images/empty.gif" align="center" valign="top"  width="16%" class="whitemid">&nbsp;Purchase Order /<br>Job Number</td>
		</tr>


		<%

		num_count = 0;
		num_current = 0;

		contract_SQLlist = " ";

		if  (  !pojob_SQLlist.equals("")  )   {

			if ( poactivitybean.getReturnedRA( list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_current, pojobselect ) ) {

 				while ( poactivitybean.getNext2() ) {

					num_count++;

					if  ( num_count != 1 ) 
						contract_SQLlist = contract_SQLlist.trim() + ", ";
	
					contract_SQLlist = contract_SQLlist.trim() + " " + poactivitybean.getColumn2("RHCON#")  + " ";
				}
			}
		}

     	 	%>

		<%             if  ( num_count == 0 )  {  %>
				<tr>
				   <td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="left"  valign="top"  class="tabledata" colspan="10">&nbsp;No equipment history for specified selection</td>
				</tr>

		<%

			} else  { 

				num_count = 0;
				num_current = 0;

	      			str_color    = "#cccccc";

	      			if ( poactivitybean.getReturnedEquip(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_current, contract_SQLlist,  pojobselect ) ) {

						while ( poactivitybean.getNext3() ) {
 
							num_count++;

							if ( str_color.equals("#cccccc") )
								str_color = "#999999";
							else 
								str_color = "#cccccc";

							str_RDCON     =  poactivitybean.getColumn3("RDCON#");
							str_RDITEM   =  poactivitybean.getColumn3("RDITEM");
							str_RDLOC     = poactivitybean.getColumn3("RDLOC");
							str_ECDESC    = poactivitybean.getColumn3("ECDESC");
							str_RHORDB  = poactivitybean.getColumn3("RHORDB");
							str_RHPO        = poactivitybean.getColumn3("RHPO#").trim();
							str_RHJOB      = poactivitybean.getColumn3("RHJOB#").trim();
							str_RHDATO  = poactivitybean.getColumn3("RHDATO");
							str_RDAMT$  = poactivitybean.getColumn3("RDAMT$");
							str_RHLRDT   = "";
							str_RDQTY =  poactivitybean.getColumn3("RDQTYR");

							if ( poactivitybean.getHistoryEndDate(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_current, str_RDCON, str_RDITEM  ) ) {
								while ( poactivitybean.getNext8() ) {
									if  (  str_RHLRDT.equals("")  )
										str_RHLRDT  = poactivitybean.getColumn8("RHLRDT");
								}
							}

							// **************
							// Start Date
							// **************
	
							if ( !str_RHDATO.trim().equals("0") && str_RHDATO.length() == 8)
								str_RHDATO = str_RHDATO.substring(4, 6) + "/" + str_RHDATO.substring(6, 8) + "/" + str_RHDATO.substring(2, 4);
							else
								str_RHDATO = "";


							// **************
							// End Date
							// **************
	
							if ( !str_RHLRDT.trim().equals("0") && str_RHLRDT.length() == 8)
								str_RHLRDT = str_RHLRDT.substring(4, 6) + "/" + str_RHLRDT.substring(6, 8) + "/" + str_RHLRDT.substring(2, 4);
							else
								str_RHLRDT = "";


							// **********************************
							//  Format the right decimal numbers
							// **********************************

							int decpos = str_RDAMT$.indexOf("."); 

							if (decpos == -1 )
								str_RDAMT$ = str_RDAMT$.trim() + ".00";

							if (  (   (str_RDAMT$.length()-1)  - decpos ==  1 )  )
								str_RDAMT$ = str_RDAMT$.trim() + "0";

							// *************************************************
							//  Format the description is longer than 25 bytes and has 
							//  a slash in the text
							// *************************************************

							int descpos = str_ECDESC.indexOf(" "); 

							if (descpos == -1   ||   descpos > 25  )  {

								String str_newECDESC = "";
						
								for (int val4  = 0; val4 < str_ECDESC.length(); ++val4)   {

									if  ( str_ECDESC.substring(val4, val4+1).equals("/")  &&  val4 > 10)
										str_newECDESC = str_newECDESC.trim() + " " +  str_ECDESC.substring(val4, val4+1) + " ";
									else
										str_newECDESC = str_newECDESC + str_ECDESC.substring(val4, val4+1);
								}

								str_ECDESC = str_newECDESC;
							}


					// *****************************************************
					//  RI006 - Retrieve the description for rerent items
					// *****************************************************

					String str_itemcomments = "";		// RI006
					int int_iseq = Integer.valueOf(poactivitybean.getColumn3("RDISEQ")).intValue();		// RI006
					int int_catg = Integer.valueOf(poactivitybean.getColumn3("RDCATG")).intValue();		//  RI006
					int int_class = Integer.valueOf(poactivitybean.getColumn3("RDCLAS")).intValue();	//  RI006
							
					if ( str_company.equals("HG") && int_catg == 975 && int_class == 1 )	{		//  RI006
						str_RDSEQ = poactivitybean.getItemSeq(str_company, str_datalib, Integer.valueOf(str_RDCON).intValue(), int_iseq, str_RDITEM); 	//  RI006
						str_itemcomments = "<br>" + poactivitybean.getItemComments(str_company, str_datalib, Integer.valueOf(str_RDCON).intValue(), int_iseq, Integer.valueOf(str_RDSEQ).intValue()); 	//  RI006
					} else {		//  RI006
						str_itemcomments = "";	//  RI006		
					}		// RI006	
					
      			%>

							<tr>
								<td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="center"  valign="top"  class="tabledata"><%=str_RDCON%>&nbsp;<br><br><a href="contractHistory.jsp?contract=<%=str_RDCON%>&sequence=000&ret=PO">View<br>Related<br>Documents</a></td>
								<td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="right" valign="top"  class="tabledata"><%=str_RHDATO%>&nbsp;</td>
								<td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="left"  valign="top"  class="tabledata"><%=str_RDITEM%>&nbsp;</td>
								<td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="right" valign="top"  class="tabledata"><%=str_RDQTY%>&nbsp;</td>
								<td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="left"  valign="top"  class="tabledata"><%=str_ECDESC%>&nbsp;<%=str_itemcomments%></td>
								<td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="right" valign="top"  class="tabledata"><%=str_RDLOC%>&nbsp;</td>
								<td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="left"  valign="top"  class="tabledata"><%=str_RHORDB%>&nbsp;</td>
								<td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="left"  valign="top"  class="tabledata"><%=str_RHLRDT%>&nbsp;</td>
								<td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="right" valign="top"  class="tabledata"><%=str_RDAMT$%>&nbsp;</td>
								<td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="left"  valign="top"  class="tabledata"><%=str_RHPO%>&nbsp;<br><br><%=str_RHJOB%></td>
							</tr>

				<%
						}
					}
				}

				%>

	</table>

	<br>

	<%  
	// *************************************
	// Display the Open Invoice data
	// *************************************
	%>

	<table cellpadding="3" cellspacing="1" border="0" width="900">

		<tr>
		   <td bgcolor="#ffffff" align="left"  class="tableheader3" colspan="9">&nbsp;Open Invoices</td>
		</tr>

		<tr>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="11%" valign="top" class="whitemid">Invoice #</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="8%"  valign="top" class="whitemid">Invoice<br>Date</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="8%"  valign="top" class="whitemid">Invoice<BR>From</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="8%"  valign="top" class="whitemid">Invoice<br>To</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="10%" valign="top" class="whitemid">Status</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="5%"  valign="top" class="whitemid">Loc</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="right"   width="15%" valign="top" class="whitemid">Original</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="right"   width="15%" valign="top" class="whitemid">Balance</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="20%" valign="top" class="whitemid">Purchase Order /<br>Job Number</td>
		</tr>

		<%

		num_count = 0;
		num_current = 0;
		num_invamt = 0;
		num_invbal = 0;


		if  (  !pojob_SQLlist.equals("")  )   {

			if ( poactivitybean.getOpenInvoices(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_current, pojobselect ) ) {

 				while ( poactivitybean.getNext4() ) {

					num_count++;

					if ( str_color.equals("#cccccc") )
						str_color = "#999999";
					else 
						str_color = "#cccccc";
    
					String str_contract  = poactivitybean.getColumn4("AHINV#");
					String str_sequence = poactivitybean.getColumn4("AHISEQ");
					String str_ahloc         = poactivitybean.getColumn4("AHLOC");
					String str_ahamt       = poactivitybean.getColumn4("AHAMT$").trim();
					String str_ahcbal       = poactivitybean.getColumn4("AHCBAL").trim();
					str_RHPO                = poactivitybean.getColumn4("RHPO#").trim();
					str_RHJOB              = poactivitybean.getColumn4("RHJOB#").trim();

					double  num_ahamt       =  Double.valueOf( str_ahamt ).doubleValue();
					num_invamt = num_invamt + num_ahamt;

					double  num_ahcbal       =  Double.valueOf( str_ahcbal ).doubleValue();
					num_invbal = num_invbal + num_ahcbal;

					for (int val  = 0;  val < pojob_count;  ++val) {
						if ( str_RHPO.equals(  str_POarray[val] )  &&  str_RHJOB.equals(  str_JOBarray[val] ) )  {
							 orgAmtarray[val]  =  orgAmtarray[val]  + num_ahamt;
							balAmtarray[val]   =  balAmtarray[val]  + num_ahcbal;
						}
					}


					while( str_sequence.length() < 3 ) {
						str_sequence = "0" + str_sequence;
					}

					String str_duedate = poactivitybean.getColumn4("AHDUED");
	
					if ( !str_duedate.trim().equals("0") && str_duedate.length() == 8)
						str_duedate = str_duedate.substring(4, 6) + "/" + str_duedate.substring(6, 8) + "/" + str_duedate.substring(2, 4);
					else
						str_duedate = "";


					String str_ordertype          = poactivitybean.getColumn4("RHOTYP");
					String str_cycbill_num      = poactivitybean.getColumn4("RH#CYB");

					String str_fromcyclebilldate = poactivitybean.getColumn4("RHCBDF");

					if ( !str_ordertype.equals("C") && !str_cycbill_num.equals("0")  ) 
						str_fromcyclebilldate = poactivitybean.getColumn4("RHLBDT");

					if ( ( str_fromcyclebilldate.equals("") && str_ordertype.equals("C") )  ||  ( !str_ordertype.equals("C") && str_cycbill_num.equals("0")  )   )
						str_fromcyclebilldate = poactivitybean.getColumn4("RHDATO");

					if ( !str_fromcyclebilldate.trim().equals("0") && str_fromcyclebilldate.length() == 8)
						str_fromcyclebilldate = str_fromcyclebilldate.substring(4, 6) + "/" + str_fromcyclebilldate.substring(6, 8) + "/" + str_fromcyclebilldate.substring(2, 4);
					else
						str_fromcyclebilldate = "";


					String str_tocyclebilldate = poactivitybean.getColumn4("RHLRDT");

					if ( !str_tocyclebilldate.trim().equals("0") && str_tocyclebilldate.length() == 8)
						str_tocyclebilldate = str_tocyclebilldate.substring(4, 6) + "/" + str_tocyclebilldate.substring(6, 8) + "/" + str_tocyclebilldate.substring(2, 4);
					else
						str_tocyclebilldate = "";


					String str_status = "OPEN";

					if ( poactivitybean.getColumn4("AHSTTS").equals("OP") ) {

						if ( Double.valueOf( poactivitybean.getColumn4("CurrBalance")).doubleValue() > 0)
							str_status = "PARTIAL";
						else
                           						str_status = "OPEN";

					}   else {

						str_status = "ERR";

						if ( poactivitybean.getColumn4("AHSTTS").equals("PP") )
							str_status = "PARTIAL";
 					}

					String str_recordsource = poactivitybean.getColumn4("AHSRC");

					String str_banner ="";

					if ( str_recordsource.equals("R1") )
						str_banner = "FULL RETURN";
					else if ( str_recordsource.equals("R2") )
						str_banner = "PARTIAL RETURN";
					else if ( str_recordsource.equals("R4") )
						str_banner = "EXCHANGE";
					else if ( str_recordsource.equals("R5") )
						str_banner = "CREDIT MEMO";
					else if ( str_recordsource.equals("R6") )
						str_banner = "CYCLE BILL";
					else if ( str_recordsource.equals("R7") )
						str_banner = "RENTL PURCHS";
					else if ( str_recordsource.equals("S1") )
						str_banner = "EQUIP SALE";
					else if ( str_recordsource.equals("S2") )
						str_banner = "PARTS/MERCHD SALE";
					else 
						str_banner = "";

	 	%>

					<tr>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><a href="contractDetail.jsp?contract=<%=str_contract%>&sequence=<%=str_sequence%>"><%=str_contract%>-<%=str_sequence%></a><br>&nbsp;<br><a href="contractHistory.jsp?contract=<%=str_contract%>&sequence=<%=str_sequence%>&ret=PO" >View<br>Related<br>Documents</a></td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><%=str_duedate%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><%=str_fromcyclebilldate%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><%=str_tocyclebilldate%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><%=str_status%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><%=str_ahloc%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata">$<%=str_ahamt%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata">$<%=str_ahcbal%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><%=str_RHPO.trim()%>&nbsp;<br><br><%=str_RHJOB.trim()%></td>
					</tr>

		<%
				}
      			}

		}
		%>

		<%     if  ( num_count == 0 )  {  %>
			<tr>
			   <td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="left"  valign="top"  class="tabledata" colspan="9">&nbsp;No invoices currently open for specified selection</td>
			</tr>
		<%     }   else  {  

			str_invamt = df.format(num_invamt);

			int decpos = str_invamt.indexOf(".");    

			if (decpos == -1 )
				str_invamt = str_invamt + ".00";

			if (  (   (str_invamt.length()-1)  - decpos ==  1 )  )
				str_invamt = str_invamt + "0";

			str_invbal = df.format(num_invbal);

			decpos = str_invbal.indexOf(".");    

			if (decpos == -1 )
				str_invbal = str_invbal + ".00";

			if (  (   (str_invbal.length()-1)  - decpos ==  1 )  )
				str_invbal = str_invbal + "0";
		%>
			<tr>
			   <td bgcolor="white"  background="images/empty.gif"  align="right"  valign="top"  class="tableheader3"  colspan="6">&nbsp;Summary Total</td>
			   <td bgcolor="white"  background="images/empty.gif"  align="right"    valign="top"  class="tableheader3">$<%=str_invamt%>&nbsp;</td>
			   <td bgcolor="white"  background="images/empty.gif"  align="right"    valign="top"  class="tableheader3">$<%=str_invbal%>&nbsp;</td>
			   <td bgcolor="white"  background="images/empty.gif"  align="right"    valign="top"  class="tableheader3">&nbsp;</td>
			</tr>
		<%     }   %>
	</table>


	<br>

	<%  
	// *************************************
	// Display the Paid Invoice data
	// *************************************
	%>

	<table cellpadding="3" cellspacing="1" border="0" width="900">

		<tr>
		   <td bgcolor="#ffffff" align="left"  class="tableheader3" colspan="9">&nbsp;Paid Invoices</td>
		</tr>

		<tr>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="11%"  valign="top" class="whitemid">Invoice #</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="8%"   valign="top" class="whitemid">Invoice<br>Date</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="8%"   valign="top" class="whitemid">Invoice<br>From</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="8%"   valign="top" class="whitemid">Invoice<br>To</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="10%"  valign="top" class="whitemid">Status</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="5%"   valign="top" class="whitemid">Loc</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="right"   width="15%"  valign="top" class="whitemid">Original</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="right"   width="15%"  valign="top" class="whitemid">Balance</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="center"  width="20%"  valign="top" class="whitemid">Purchase Order /<br>Job Number</td>
		</tr>

		<%

		num_count = 0;
		num_current = 0;
		num_invamt = 0;


		if  (  !pojob_SQLlist.equals("")  )   {

			if ( poactivitybean.getPaidInvoices(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_current, pojobselect ) ) {

 				while ( poactivitybean.getNext5() ) {

					num_count++;

					if ( str_color.equals("#cccccc") )
						str_color = "#999999";
					else 
						str_color = "#cccccc";
    
					String str_contract    = poactivitybean.getColumn5("AHINV#");
					String str_sequence   = poactivitybean.getColumn5("AHISEQ");
					String str_ahloc         = poactivitybean.getColumn5("AHLOC");
					String str_ahamt       = poactivitybean.getColumn5("AHAMT$").trim();
					String str_ahcbal       = poactivitybean.getColumn5("AHCBAL").trim();
					str_RHPO                = poactivitybean.getColumn5("RHPO#").trim();
					str_RHJOB              = poactivitybean.getColumn5("RHJOB#").trim();

					double  num_ahamt       =  Double.valueOf( str_ahamt ).doubleValue();
					num_invamt = num_invamt + num_ahamt;

					double  num_ahcbal       =  Double.valueOf( str_ahcbal ).doubleValue();

					if (  num_ahcbal  <  0  &&   str_company.trim().equals("HG") ) {
						num_ahcbal = 0.00;
						str_ahcbal = "0.00";
					}

					num_invbal = num_invbal + num_ahcbal;

					for (int val  = 0;  val < pojob_count;  ++val) {
						if ( str_RHPO.equals(  str_POarray[val] )  &&  str_RHJOB.equals(  str_JOBarray[val] ) )  {
							orgAmtarray[val]  =  orgAmtarray[val]  + num_ahamt;
							balAmtarray[val]   =  balAmtarray[val]  + num_ahcbal;
						}
					}


					while( str_sequence.length() < 3 ) {
						str_sequence = "0" + str_sequence;
					}

					String str_duedate = poactivitybean.getColumn5("AHDUED");
	
					if ( !str_duedate.trim().equals("0") && str_duedate.length() == 8)
						str_duedate = str_duedate.substring(4, 6) + "/" + str_duedate.substring(6, 8) + "/" + str_duedate.substring(2, 4);
					else
						str_duedate = "";


					String str_ordertype          = poactivitybean.getColumn5("RHOTYP");
					String str_cycbill_num      = poactivitybean.getColumn5("RH#CYB");

					String str_fromcyclebilldate = poactivitybean.getColumn5("RHCBDF");

					if ( !str_ordertype.equals("C") && !str_cycbill_num.equals("0")  ) 
						str_fromcyclebilldate = poactivitybean.getColumn5("RHLBDT");

					if ( ( str_fromcyclebilldate.equals("") && str_ordertype.equals("C") )  ||  ( !str_ordertype.equals("C") && str_cycbill_num.equals("0")  )   )
						str_fromcyclebilldate = poactivitybean.getColumn5("RHDATO");

					if ( !str_fromcyclebilldate.trim().equals("0") && str_fromcyclebilldate.length() == 8)
						str_fromcyclebilldate = str_fromcyclebilldate.substring(4, 6) + "/" + str_fromcyclebilldate.substring(6, 8) + "/" + str_fromcyclebilldate.substring(2, 4);
					else
						str_fromcyclebilldate = "";


					String str_tocyclebilldate = poactivitybean.getColumn5("RHLRDT");

					if ( !str_tocyclebilldate.trim().equals("0") && str_tocyclebilldate.length() == 8)
						str_tocyclebilldate = str_tocyclebilldate.substring(4, 6) + "/" + str_tocyclebilldate.substring(6, 8) + "/" + str_tocyclebilldate.substring(2, 4);
					else
						str_tocyclebilldate = "";


					String str_status = "CLOSED";

	 	%>

					<tr>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><a href="contractDetail.jsp?contract=<%=str_contract%>&sequence=<%=str_sequence%>"><%=str_contract%>-<%=str_sequence%></a><br>&nbsp;<br><a href="contractHistory.jsp?contract=<%=str_contract%>&sequence=<%=str_sequence%>&ret=PO" >View<br>Related<br>Documents</a></td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><%=str_duedate%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><%=str_fromcyclebilldate%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><%=str_tocyclebilldate%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><%=str_status%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="center" valign="top" class="tabledata"><%=str_ahloc%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata">$<%=str_ahamt%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata">$<%=str_ahcbal%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"   valign="top" class="tabledata"><%=str_RHPO.trim()%><br>&nbsp;<br><br><%=str_RHJOB.trim()%></td>
					</tr>

		<%
				}
      			}

		}
		%>

		<%     if  ( num_count == 0 )  {  %>
			<tr>
			   <td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="left"  valign="top"  class="tabledata" colspan="9">&nbsp;No invoices currently paid for specified selection</td>
			</tr>
		<%     }   else  {  

			str_invamt = df.format(num_invamt);

			int decpos = str_invamt.indexOf(".");    

			if (decpos == -1 )
				str_invamt = str_invamt + ".00";

			if (  (   (str_invamt.length()-1)  - decpos ==  1 )  )
				str_invamt = str_invamt + "0";
		%>
			<tr>
			   <td bgcolor="white"  background="images/empty.gif"  align="right"  valign="top"  class="tableheader3"  colspan="6">&nbsp;Summary Total</td>
			   <td bgcolor="white"  background="images/empty.gif"  align="right"    valign="top"  class="tableheader3">$<%=str_invamt%>&nbsp;</td>
			   <td bgcolor="white"  background="images/empty.gif"  align="right"    valign="top"  class="tableheader3">$0.00&nbsp;</td>
			   <td bgcolor="white"  background="images/empty.gif"  align="right"    valign="top"  class="tableheader3">&nbsp;</td>
			</tr>
		<%     }   %>
	</table>


	<br>

	</center>

	<%  
	// *************************************
	// Display the Invoice Summary data
	// *************************************
	%>

	<table cellpadding="3" cellspacing="1" border="0" width="595">

		<tr>
		   <td bgcolor="#ffffff" align="left"  class="tableheader3" colspan="4">&nbsp;Summary for selected PO/Job's</td>
		</tr>

		<tr>
		   <td background="images/empty.gif" bgcolor="#000000" align="left" width="30%"   valign="top" class="whitemid">Purchase Order</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="left" width="30%"   valign="top" class="whitemid">Job Number</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="right" width="20%"   valign="top" class="whitemid">Invoiced Amount</td>
		   <td background="images/empty.gif" bgcolor="#000000" align="right" width="20%"   valign="top" class="whitemid">Balance</td>
		</tr>

		<%

		num_count = 0;
		num_current = 0;
		num_invamt = 0;
		num_invbal = 0;


		if  (  !pojob_SQLlist.equals("")  )   {

			for (int val  = 0;  val < pojob_count;  ++val) {


					num_count++;

					if ( str_color.equals("#cccccc") )
						str_color = "#999999";
					else 
						str_color = "#cccccc";

					String str_ahamt = df.format(orgAmtarray[val]);
					String str_hacbal = df.format(balAmtarray[val]);

					int decpos = str_ahamt.indexOf(".");    

					if (decpos == -1 )
						str_ahamt = str_ahamt + ".00";

					if (  (   ( str_ahamt.length()-1)  - decpos ==  1 )  )
						str_ahamt = str_ahamt + "0";


					decpos = str_hacbal.indexOf(".");    

					if (decpos == -1 )
						str_hacbal = str_hacbal + ".00";

					if (  (   ( str_hacbal.length()-1)  - decpos ==  1 )  )
						str_hacbal = str_hacbal  + "0";


					num_invamt = num_invamt + orgAmtarray[val];
					num_invbal = num_invbal + balAmtarray[val];

	 	%>

					<tr>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"  valign="top" class="tabledata"><%=str_POarray[val].trim()%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"  valign="top" class="tabledata"><%=str_JOBarray[val].trim()%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata">$<%=str_ahamt%>&nbsp;</td>
					   <td bgcolor="<%=str_color%>" background="images/empty.gif" align="right"  valign="top" class="tabledata">$<%=str_hacbal%>&nbsp;</td>
					</tr>

		<%
      			}

		}
		%>

		<%     if  ( num_count == 0 )  {  %>
			<tr>
			   <td bgcolor="<%=str_color%>"  background="images/empty.gif"  align="left"  valign="top"  class="tabledata" colspan="4">&nbsp;No invoice summary is available for specified selection(s)</td>
			</tr>
		<%     }   else  {  

			str_invamt = df.format(num_invamt);

			int decpos = str_invamt.indexOf(".");    

			if (decpos == -1 )
				str_invamt = str_invamt + ".00";

			if (  (   (str_invamt.length()-1)  - decpos ==  1 )  )
				str_invamt = str_invamt + "0";



			str_invbal = df.format(num_invbal);

			decpos = str_invbal.indexOf(".");    

			if (decpos == -1 )
				str_invbal = str_invbal + ".00";

			if (  (   (str_invbal.length()-1)  - decpos ==  1 )  )
				str_invbal = str_invbal + "0";
		%>
			<tr>
			   <td bgcolor="white"  background="images/empty.gif"  align="right"  valign="top"  class="tableheader3"  colspan="2">&nbsp;Summary Total</td>
			   <td bgcolor="white"  background="images/empty.gif"  align="right"    valign="top"  class="tableheader3">$<%=str_invamt%>&nbsp;</td>
			   <td bgcolor="white"  background="images/empty.gif"  align="right"    valign="top"  class="tableheader3">$<%=str_invbal%>&nbsp;</td>
			</tr>
		<%     }   %>

	</table>


	<br><br>

	<a href="javascript:history.go(-1)"><IMG src="images/back.gif" height="40" width="62" border="0"></a> 
	<a href="menu.jsp"><IMG src="images/menu.gif" height="40" width="62" border="0"></a> 
	<a href="javascript:window.close()"><IMG src="images/closeReports.gif" height="40" width="123" border="0"></a> 
	<a href="javascript:window.print()"><img src="images/print.gif" width="67" height="40" border="0"></a>
	<br>

<hr width="900">

<table border="0" width="900" cellspacing="0" cellpadding="0">
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
  
  poactivitybean.cleanup();	// RI005
  poactivitybean.endcurrentConnection(poactivitybeanconnection);

%>

</BODY>
</HTML>