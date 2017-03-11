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
//    Index    User Id     Date          Project                         Desciption
//    ------  ---------  ----------  ----------------  ----------------------------------------------------------
//    x001     DTC9028    11/26/02    SR26609 PCR1     Modification for the new log-in process
//    x002     DTC9028    11/26/02    SR26609 PCR1     Ensure that connection are ended 
//    x003     DTC9028    12/26/02    SR26609 PCR1     Changed the "log out" to "close reports"
//    x004     DTC9028    12/26/02    SR26609 PCR1     Add additional transaction type descriptions
//    x005     DTC9028    12/26/02    SR26609 PCR1     Add text for a transaction type of unknown
//    x006     DTC9028    12/26/02    SR26609 PCR1     Add rate information for the detail section
//    x007     DTC9028    12/26/02    SR26609 PCR1     Changed the page title 
//    x008     DTC9028    12/26/02    SR26609 PCR1     Add invoice totals
//    x009     DTC9028    12/26/02    SR26609 PCR1     Add credit/adjustments and payment amounts (contractdetailEBean.java) 
//    x010     DTC9028    12/26/02    SR26609 PCR1     Changed the copy right text 
//    x011     DTC9028    12/26/02    SR26609 PCR1     Make changes to the transaction header information
//    x012     DTC9028    12/26/02    SR26609 PCR1     Add the account number
//    x013     DTC9028    12/28/02    SR26609 PCR1     Add the labor charges 
//    x014     DTC9028    02/12/03    SR26609 PCR1     Show the equipment number for a work order 
//    x015     DTC9028    03/18/03    SR28586 PCR2     Show the contact information  
//    x016     DTC9028    02/09/04    SR28586 PCR22    Show credit balance for Canada 
//    x017     DTC9028    02/09/04    SR28586 PCR22    Always show the location contact information for Canada
//    x018     DTC2073    04/15/04    SR28586 PCR9     Display the branch address information
//    x019     DTC2073    04/15/04    SR31413 PCR11    Display the billing address from the Job billing file when applicable
//    x020     DTC2073    06/03/04    SR31413 PCR7     Display "validated credit" balances for domestic
//    x021     DTC9028    01/12/05    TT404162         Added the RHOTYP to the getrows method for contractdetailDBean
//    RI022    DTC9028    02/02/05    SR31413 PCR26    Add copyright year dynamically to the page.
//    RI023    DTC9028    08/01/05    SR31413 PCR19    Datasource implementation modification
//    RI024    DTC2073    11/15/05    SR35880          Add comments for re-rent items (catg/class = 975-0001)
//    RI025    DTC2073    01/31/06    SR36721 		   Add shop supply fee to WO page.
//    RI026    DTC2073    02/03/06    SR35873          Abbreviated Equipment Release
// **************************************************************************************************************

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
<jsp:useBean id="contractdetailbean" class="etrieveweb.etrievebean.contractdetailBean" scope="page" />
<jsp:useBean id="contractdetail2bean" class="etrieveweb.etrievebean.contractdetailDBean" scope="page" />
<jsp:useBean id="contractdetail3bean" class="etrieveweb.etrievebean.contractdetailcustomerjobBean" scope="page" />
<jsp:useBean id="contractdetail4bean" class="etrieveweb.etrievebean.contractdetailCBean" scope="page" />
<jsp:useBean id="contractdetail5bean" class="etrieveweb.etrievebean.contractdetailEBean" scope="page" />
<jsp:useBean id="contractdetail6bean" class="etrieveweb.etrievebean.contractdetailFBean" scope="page" />


<%
// *** Retrieve and test the input parameters ***

String str_color = "#cccccc";
String str_contract = request.getParameter("contract");
String str_sequence = request.getParameter("sequence");
String str_transtype = request.getParameter("transtype");
 
if ( str_contract == null )	str_contract = "";
if ( str_sequence == null )	str_sequence = "";
if ( str_transtype == null )	str_transtype = "";


// ***  Define variables  ***

int num_sequence = Integer.valueOf(str_sequence).intValue();
int num_count = 0;
String str_tableheader = "";
DecimalFormat df = new DecimalFormat("###,###,###,###.##");    
String str_getCreditAdj = "N";
String str_getARamounts = "Y";
String str_invoiceError = "Y";
String str_showtotals = "Y";
String str_CSinfo = "Customer Service Department, Monday through Friday 7am to 7pm CST at: (800) 456-6492.";
String str_contactinfo1 = "If you have a question regarding this invoice, please call our";
String str_contactinfo2 = "For information regarding this invoice, please call our ";


// ***  Retrieve session variables  ***

String str_username = (String) session.getValue("username");          
String str_password = (String) session.getValue("password");        
String str_as400url = (String) session.getValue("as400url");          
String str_as400driver = (String) session.getValue("as400driver");
String str_customer = (String) session.getValue("customer");
String str_company = (String) session.getValue("company");
String str_datalib = (String) session.getValue("datalib");
String list_customer = (String) session.getValue("list");
String str_companyname = (String) session.getValue("companyname");


// ***  Define connection variables   ***

Connection contractdetailbeanconnection = null;
Connection contractdetail2Abeanconnection = null;
Connection contractdetail2Bbeanconnection = null;
Connection contractdetail2Cbeanconnection = null;
Connection contractdetail2Dbeanconnection = null;
Connection contractdetail3beanconnection = null;
Connection contractdetail4beanconnection = null;
Connection contractdetail5beanconnection = null;
Connection contractdetail6beanconnection = null;

  //*************************************************************
  // RI022 - Retrieve today's date to be used for copyright statment.
  //*************************************************************
  
  Calendar cpToday = Calendar.getInstance();
  int cpYear = cpToday.get(Calendar.YEAR) ;  

//*************************************************************************
// RI026 - Retrieve eqp release and extend authority flag and 
// Retrieve the session arrays for earlier eqp release and extend requests
//*************************************************************************

String    equipChangeAuth = (String)    session.getAttribute("equipChangeAuth");	// RI026
ArrayList EqpExtContracts = (ArrayList) session.getAttribute("EqpExtContracts");	// RI026
ArrayList EqpRelContracts = (ArrayList) session.getAttribute("EqpRelContracts");	// RI026
ArrayList EqpRelLineNum   = (ArrayList) session.getAttribute("EqpRelLineNum");		// RI026
ArrayList EqpRelQtyRel    = (ArrayList) session.getAttribute("EqpRelQtyRel");		// RI026

boolean prevReleases = true;		// RI026
boolean prevExtend   = true;		// RI026

if (EqpRelContracts == null || EqpRelLineNum == null || EqpRelQtyRel == null)		// RI026
	prevReleases = false;															// RI026

if (EqpExtContracts == null)	// RI026
	prevExtend   = false;		// RI026

if (equipChangeAuth == null)	// RI026 
	equipChangeAuth = "No";		// RI026
	
// *************************************************************************************************
// ***  Make AS/400 connection  #1  - Retrieve Header and Customer information                   ***
// *************************************************************************************************

contractdetailbeanconnection = contractdetailbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);      


if ( contractdetailbean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), str_transtype) ) {    // **** Begin X0100****
     

	if ( contractdetailbean.getNext() ) {     // **** Begin X0200 ****


		// *** Retrieve specific columns from the AS/400 SQL ***

		String str_joblocation = contractdetailbean.getColumn("RHJOBL");
		String str_jobnumber = contractdetailbean.getColumn("RHJOB#");
		String str_ponumber = contractdetailbean.getColumn("RHPO#");
		String str_orderby = contractdetailbean.getColumn("RHORDB");
		String str_signedby = contractdetailbean.getColumn("RHSIG");
		String str_dateout = contractdetailbean.getColumn("RHDATO");
		String str_sysd = contractdetailbean.getColumn("RHSYSD");
		String str_hertzlocation = contractdetailbean.getColumn("RHLOC");
		String str_empnum = contractdetailbean.getColumn("RHEMP#").trim();

		str_invoiceError = "N";

		// *** Format start date  and time   ***

		if ( !str_dateout.trim().equals("0") && str_dateout.length() == 8)
			str_dateout = str_dateout.substring(4, 6) + "/" + str_dateout.substring(6, 8) + "/" + str_dateout.substring(2, 4);
		else
			str_dateout = "";


		String str_timeout = contractdetailbean.getColumn("RHTIMO"); 

		if ( !str_timeout.trim().equals("0")  ) {
			while (str_timeout.length() < 4) {
				str_timeout = "0" + str_timeout;
			}
			str_timeout = str_timeout.substring(0, 2) + ":" + str_timeout.substring(2, 4);
		}  else  {
			str_timeout = "";
		}


		// ***   Format estimated return date  and time   ***

		String str_estreturndate = contractdetailbean.getColumn("RHERDT");

		if ( !str_estreturndate.trim().equals("0") && str_estreturndate.length() == 8)
			str_estreturndate = str_estreturndate.substring(4, 6) + "/" + str_estreturndate.substring(6, 8) + "/" + str_estreturndate.substring(2, 4);
		else
			str_estreturndate = "";

		String str_estreturntime = contractdetailbean.getColumn("RHERTM");

		if ( !str_estreturntime.trim().equals("0") ) {
			while (str_estreturntime.length() < 4) {
				str_estreturntime = "0" + str_estreturntime;
			}
			str_estreturntime = str_estreturntime.substring(0, 2) + ":" + str_estreturntime.substring(2, 4);
		}


		// ***  Format last return date  and time  ***


		String str_lastreturndate = contractdetailbean.getColumn("RHLRDT");

		if ( !str_lastreturndate.trim().equals("0") && str_lastreturndate.length() == 8)
			str_lastreturndate = str_lastreturndate.substring(4, 6) + "/" + str_lastreturndate.substring(6, 8) + "/" + str_lastreturndate.substring(2, 4);
		else
			str_lastreturndate = "";

		String str_lastreturntime = contractdetailbean.getColumn("RHLRTM");

		if ( !str_lastreturntime.trim().equals("0")  ) {

			while (str_lastreturntime.length() < 4) {
				str_lastreturntime = "0" + str_lastreturntime;
			}

			str_lastreturntime = str_lastreturntime.substring(0, 2) + ":" + str_lastreturntime.substring(2, 4);

		} else   {

			str_lastreturntime = "";

		}


		// ***  Format delivery date  ***


		String str_deliverydate = contractdetailbean.getColumn("RHDLVD");

		if ( !str_deliverydate.trim().equals("0") && str_deliverydate.length() == 8)
			str_deliverydate = str_deliverydate.substring(4, 6) + "/" + str_deliverydate.substring(6, 8) + "/" + str_deliverydate.substring(2, 4);
		else
			str_deliverydate = "";


		// ***  Format invoice/return date  ***

		if ( !str_sysd.trim().equals("0") && str_sysd.length() == 8)
			str_sysd = str_sysd.substring(4, 6) + "/" + str_sysd.substring(6, 8) + "/" + str_sysd.substring(2, 4);
		else
			str_sysd = "";


		// ***  Define and initialize variables  ***

		String str_deliveryby = contractdetailbean.getColumn("RHDELB");
		String str_pickupby = contractdetailbean.getColumn("RHPKUB");
		String str_type = contractdetailbean.getColumn("RHTYPE");

		String str_pagetitle = "contract detail";
		String str_transdesc = "Contract";
		String str_rentaldays = "";
		int num_rentaldays = 0;
		String str_rentalhours = "";
		String str_ordertype = "";
		String str_fromcyclebilldate = "";
		String str_tocyclebilldate = "";
		String str_cycbill_num = "";
		String str_woequipment = "";
		double num_rentamt = 0.0;
		double num_dwamt   = 0.0;
		double num_slsamt  = 0.0;
		double num_taxamt  = 0.0;
		double num_fuelamt = 0.0;
		double num_laboramt = 0.0;
		double num_returnamt = 0.0;
		double num_miscamt   = 0.0;
		double num_invamt    = 0.0;
		double num_delvamt   = 0.0;
		double num_pickamt   = 0.0;
		double num_cradjamt  = 0.0;
		double num_payments  = 0.0;
		double num_currentbal = 0.0;
		double num_totShopFees = 0.0 ;	  // RI025
		
		String str_itemcomments = "";		// RI024
							
		if ( str_transtype.equals("sales") )  {
			str_transdesc = "Order";
			str_pagetitle = "order detail";
		}

		// *** Initialize variables specific to INVOICES only  ***

		if (num_sequence != 0) {

			str_pagetitle = "invoice detail";
			str_transdesc = "Invoice";

			str_rentaldays  = contractdetailbean.getColumn("RHRDYS");
			num_rentaldays  = Integer.valueOf(str_rentaldays).intValue();
			str_rentalhours = contractdetailbean.getColumn("RHRHRS");
			str_ordertype   = contractdetailbean.getColumn("RHOTYP");
			str_cycbill_num = contractdetailbean.getColumn("RH#CYB");

			if ( !str_rentalhours.equals("0.00") )           
				num_rentaldays = num_rentaldays + 1;

			// *** Format from cycle bill date ***

			str_fromcyclebilldate = contractdetailbean.getColumn("RHCBDF");

			if ( !str_ordertype.equals("C") && !str_cycbill_num.equals("0")  ) 
				str_fromcyclebilldate = contractdetailbean.getColumn("RHLBDT");

			if ( !str_fromcyclebilldate.trim().equals("0") && str_fromcyclebilldate.length() == 8)
				str_fromcyclebilldate = str_fromcyclebilldate.substring(4, 6) + "/" + str_fromcyclebilldate.substring(6, 8) + "/" + str_fromcyclebilldate.substring(2, 4);
			else
				str_fromcyclebilldate = "";

			if ( ( str_fromcyclebilldate.equals("") && str_ordertype.equals("C") )  ||  ( !str_ordertype.equals("C") && str_cycbill_num.equals("0")  )   )
				str_fromcyclebilldate = str_dateout;


			// *** Format to cycle bill date  ***

			if ( !str_tocyclebilldate.trim().equals("0") && str_tocyclebilldate.length() == 8)
				str_tocyclebilldate = str_tocyclebilldate.substring(4, 6) + "/" + str_tocyclebilldate.substring(6, 8) + "/" + str_tocyclebilldate.substring(2, 4);
			else
				str_tocyclebilldate = "";

			str_tocyclebilldate = str_lastreturndate;

		}


		// Determine whether to display invoice totals

		if ( num_sequence == 0  &&  (  str_type.equals("O")  ||  str_type.equals("R")  ||   ( str_type.equals("X")  &&  !str_transtype.equals("sales") )  )  )
			str_showtotals = "N";

		if ( num_sequence != 0 )
			str_showtotals = "Y";

		if ( str_showtotals.equals("Y") )   {
			num_rentamt = Double.valueOf( contractdetailbean.getColumn("RHRNT$") ).doubleValue();
			num_dwamt   = Double.valueOf( contractdetailbean.getColumn("RHDW$") ).doubleValue();
			num_slsamt  = Double.valueOf( contractdetailbean.getColumn("RHSLS$") ).doubleValue();
			num_taxamt  = Double.valueOf( contractdetailbean.getColumn("RHTAX$") ).doubleValue();
			num_fuelamt = Double.valueOf( contractdetailbean.getColumn("RHFUEL") ).doubleValue();
			num_laboramt = Double.valueOf( contractdetailbean.getColumn("RHLBR$") ).doubleValue();
			num_returnamt = Double.valueOf( contractdetailbean.getColumn("RHRTN$") ).doubleValue();
			num_miscamt = Double.valueOf( contractdetailbean.getColumn("RHMSC$") ).doubleValue();
			num_invamt  = Double.valueOf( contractdetailbean.getColumn("RHAMT$") ).doubleValue();
			num_delvamt = Double.valueOf( contractdetailbean.getColumn("RHDEL$") ).doubleValue();
			num_pickamt = Double.valueOf( contractdetailbean.getColumn("RHPKU$") ).doubleValue();
		}


		// ***  Initialize bill-to and ship-to address  ***

		String str_account = contractdetailbean.getColumn("CMCUS#");
		String str_billname = contractdetailbean.getColumn("CMNAME");
		String str_billaddress1 = contractdetailbean.getColumn("CMADR1");
		String str_billaddress2 = contractdetailbean.getColumn("CMADR2");
		String str_billcity = contractdetailbean.getColumn("CMCITY");
		String str_billstate = contractdetailbean.getColumn("CMSTAT");
		String str_billzip  = contractdetailbean.getColumn("CMZIP");
		String str_billarea = contractdetailbean.getColumn("CMAREA");
		String str_billphone = contractdetailbean.getColumn("CMPHON");

		String str_shipname =  str_billname;
		String str_shipaddress1 = str_billaddress1;
		String str_shipaddress2 = str_billaddress2;
		String str_shipcity = str_billcity; 
		String str_shipstate = str_billstate;  
		String str_shipzip = str_billzip;
		String str_shiparea = str_billarea;
		String str_shipphone = str_billphone;

		// Retrieve work order header information

		if ( str_type.equals("W") )
			str_woequipment = contractdetailbean.getworkorder(str_company, str_datalib, Integer.valueOf(str_contract).intValue() );
     

		// *** End AS/400 connection #1 - Retrieve Header and Customer information  ***

		contractdetailbean.cleanup();	// RI023
		contractdetailbean.endcurrentConnection(contractdetailbeanconnection);



		// **************************************************************************************
		// ***  Make AS/400 connection  #2  - Retrieve Credits, Adjustments, Payments and Current Balance          ***
		// **************************************************************************************

		int validCredit = 0;
			
		if  ( str_showtotals.equals("Y")  && str_getCreditAdj.equals("Y") && str_getARamounts.equals("N")  )  {

			contractdetail4beanconnection =  contractdetail4bean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

			num_cradjamt  =  Double.valueOf( contractdetail4bean.get_creditadj( str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue() ) ).doubleValue();       

			contractdetail4bean.cleanup();	// RI023
			contractdetail4bean.endcurrentConnection(contractdetail4beanconnection);
		}


		if  (  str_showtotals.equals("Y")  && str_getCreditAdj.equals("N") && str_getARamounts.equals("Y")  )  {

			contractdetail5beanconnection =  contractdetail5bean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

			if ( contractdetail5bean.getRows( Integer.valueOf(str_customer).intValue(), str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue()  ) )  {

				if ( contractdetail5bean.getNext() ) {
					num_payments  = Double.valueOf( contractdetail5bean.getColumn("AHTPAY") ).doubleValue();
					num_cradjamt    = Double.valueOf( contractdetail5bean.getColumn("AHADJ$") ).doubleValue();
					num_currentbal = Double.valueOf( contractdetail5bean.getColumn("AHCBAL") ).doubleValue();  
					
		
					//**************************************************************
					//  x020- Validate credit balance based on collection status code	
					//  Show credit balance for Validated Credit of a Open Invoices
					//  Show credit balance for Non Validated Credit of a Paid Invoice.
					//**************************************************************

					if (str_company.trim().equals("HG") && num_currentbal < 0)
					{											
						String str_ahcols = contractdetail5bean.getColumn("AHCOLS") ;
						String str_ahstts = contractdetail5bean.getColumn("AHSTTS") ;

						if ( str_ahcols != null )
						{
							int len = str_ahcols.length() ;

							if(len > 0)
							{
								String colCode = str_ahcols.substring(0,1); 
								int statNum = Integer.valueOf(str_ahcols.substring(1,4)).intValue();

								if (colCode.equals("B") && ( statNum > 0 && statNum <499 ))
								{			
									if ( !str_ahstts.equals("PD"))
										validCredit = 1;
									}		
										else
									{			
										if (str_ahstts.equals("PD"))
											validCredit = 1;	
									}				
								}
						    }
							else
							{
								if (str_ahstts.equals("PD"))
									validCredit = 1;	
							}		
					}
							
					                             
				}

			}

			contractdetail5bean.cleanup();	// RI023
			contractdetail5bean.endcurrentConnection(contractdetail5beanconnection);

		}


		// ******************************************************************** 
		// ***           Make AS/400 connection  #3  -  retrieve ship-to information                             *** 
		// ******************************************************************** 

		contractdetail3beanconnection = contractdetail3bean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		if ( contractdetail3bean.getRows( Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_jobnumber) ) {

			if ( contractdetail3bean.getNext() ) {
				str_shipname = contractdetail3bean.getColumn("CJNAME");
				str_shipaddress1 = contractdetail3bean.getColumn("CJADR1");
				str_shipaddress2 = contractdetail3bean.getColumn("CJADR2");
				str_shipcity = contractdetail3bean.getColumn("CJCITY");
				str_shipstate = contractdetail3bean.getColumn("CJST");
				str_shipzip = contractdetail3bean.getColumn("CJZIP");
				str_shiparea = contractdetail3bean.getColumn("CJAREA");
				str_shipphone = contractdetail3bean.getColumn("CJPHON");
			}
               
		}
    
		// ********************************************************
		//  X019 - Retrieve Billing address from CUSJBLFL if applicable.
		// ********************************************************

		if (contractdetail3bean.getRowsB(Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_jobnumber) ) {

			if ( contractdetail3bean.getNextB() ) {
				str_billname = contractdetail3bean.getColumnB("CINAME");
				str_billaddress1 = contractdetail3bean.getColumnB("CIADR1");
				str_billaddress2 = contractdetail3bean.getColumnB("CIADR2");
				str_billcity = contractdetail3bean.getColumnB("CICITY");
				str_billstate = contractdetail3bean.getColumnB("CIST");
				str_billzip = contractdetail3bean.getColumnB("CIZIP");
				str_billarea = contractdetail3bean.getColumnB("CIAREA");
				str_billphone = contractdetail3bean.getColumnB("CIPHON");
			}
		}


     
		// *** End AS/400 connection # 3 - Retrieve ship-to information                   ***

		contractdetail3bean.cleanup();	// RI023
		contractdetail3bean.endcurrentConnection(contractdetail3beanconnection);


		// ******************************************************************** 
		// ***           Make AS/400 connection  #6  -  retrieve location contact information             *** 
		// ***           Show the location contact info EVERY time for Canada 
		// ******************************************************************** 

		// ***********************************************************************************************************
		// x018 - Create new array to receive location information.  Modifified this section to retieve the information when there is a location.
		// ***********************************************************************************************************

		String [] locationinfoArray = new String[4];  // RI026

		if ( !str_hertzlocation.equals("") )  {

			contractdetail6beanconnection = contractdetail6bean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

			locationinfoArray = contractdetail6bean.getLocInfo(  str_company,  str_hertzlocation,  str_datalib );

			String str_locationinfo = locationinfoArray[0].trim();

			if  ( num_sequence == 0  ||  str_company.trim().equals("CR")  )   {

				str_contactinfo1 = "If you have a question regarding this transaction, please call our ";
				str_contactinfo2 = "For information regarding this transaction, please call our ";

				if (  str_locationinfo.equals("")  ) {

					str_contactinfo1 = str_contactinfo1.trim() + " " + str_CSinfo;
						str_contactinfo2 = str_contactinfo2.trim() + " " + str_CSinfo;

	        		if ( str_company.trim().equals("CR")  )   {
						str_contactinfo1 = "";
						str_contactinfo2 = "";
	        		}

				} else  {

					str_contactinfo1 = str_contactinfo1.trim() + " location in " + str_locationinfo.trim() + ".";
					str_contactinfo2 = str_contactinfo2.trim() + " location in " + str_locationinfo.trim() + ".";

                 }        

			}  else  {

				str_contactinfo1 = str_contactinfo1.trim() + " " + str_CSinfo;
				str_contactinfo2 = str_contactinfo1.trim();

			}

		contractdetail6bean.cleanup();	// RI023
		contractdetail6bean.endcurrentConnection(contractdetail6beanconnection);
	}


	if ( str_hertzlocation.trim().equals("")  &&   str_company.trim().equals("CR") )   {
		str_contactinfo1 = "";
		str_contactinfo2 = "";
	}

	// *********************************************
	// Format the telephone numbers
	// *********************************************

	if ( !str_billphone.trim().equals("0") && str_billphone.length() == 7)
		str_billphone = str_billphone.substring(0, 3) + "-" + str_billphone.substring(3, 7);

	if ( !str_shipphone.trim().equals("0") && str_shipphone.length() == 7)
		str_shipphone = str_shipphone.substring(0, 3) + "-" + str_shipphone.substring(3, 7);


	// *********************************
	// Determine the contract/invoice type
	// *********************************

	String str_contracttype = "UNKNOWN";
     
	// moved to the bottom                if ( str_transtype.equals("sales") )
	// moved to the bottom                       str_contracttype = "SALES";

	if ( str_type.equals("R") ) 
		str_contracttype = "RESERVATION";
	else if ( str_type.equals("X") ) 
		str_contracttype = "QUOTE";
	else if ( str_type.equals("C") ) 
		str_contracttype = "CREDIT MEMO";
	else if ( str_type.equals("E") ) 
		str_contracttype = "EXCHANGE";
	else if ( str_type.equals("I") ) 
		str_contracttype = "INTER-COMPANY EXPENSE";
	else if ( str_type.equals("M") ) 
		str_contracttype = "MANUAL INVOICE E&D";
	else if ( str_type.equals("Y") ) 
		str_contracttype = "EQUIP SALE QUOTE";
	else if ( str_type.equals("T") ) 
		str_contracttype = "EQUIP SALE RESRV";
	else if ( str_type.equals("G") ) 
		str_contracttype = "RENTAL PURCHASE QUOTE";
	else if ( str_type.equals("F") ) 
		str_contracttype = "RENTAL PURCHASE";
	else if ( str_type.equals("Q") ) 
		str_contracttype = "EQUIP SALE";
	else if ( str_type.equals("S") ) 
		str_contracttype = "SALES";
	else if ( str_type.equals("W") ) 
		str_contracttype = "WORK ORDER";
	else if ( str_sequence.equals("") || str_sequence.equals("0") || str_sequence.equals("000")) 
		str_contracttype = "OPEN RENTAL";
	else {
		if ( num_sequence != 0 ) {
			if ( str_ordertype.equals("C") ) 
				str_contracttype = "PROGRESS BILLING";
			else if ( str_ordertype.equals("A") )
				str_contracttype = "MANUAL INVOICE";
			else if ( str_ordertype.equals("E") )
				str_contracttype = "EXCHANGE";
			else if ( str_ordertype.equals("F") )
				str_contracttype = "CLOSED RENTAL";
			else if ( str_ordertype.equals("H") )
				str_contracttype = "RENTAL PURCHASE CONTRACT";
			else if ( str_ordertype.equals("I") )
				str_contracttype = "INTER-COMPANY EXPENSE";
			else if ( str_ordertype.equals("M") )
				str_contracttype = "CREDIT MEMO";
			else if ( str_ordertype.equals("P") )
				str_contracttype = "PARTIAL RETURN";
			else if ( str_ordertype.equals("Q") )
				str_contracttype = "EQUIPMENT SALE";
			else if ( str_ordertype.equals("R") )
				str_contracttype = "RESERVATION";
			else if ( str_ordertype.equals("S") )
				str_contracttype = "SALES QUOTE";
			else if ( str_ordertype.equals("Y") )
				str_contracttype = "EQUIPMENT SALES QUOTE";
			else 
				str_contracttype = "UNKNOWN";	
		}
	}

	// If an aquisition, force the UNKNOWN condition

	if (  str_empnum.equals("")  )
		str_contracttype = "UNKNOWN";



	// If the transaction is a work order, simulate as if the menu option came from a sales report

	if ( str_type.equals("W") || str_type.equals("S") || str_type.equals("I") )
		str_transtype = "sales";


	// ************************************************************************
	//  RI026 - Check if this contract qualifies for online extension.  This 
	//  section will determine if an earlier extension has been done for the
	//  contract in the same session.       
	// ************************************************************************
						
		boolean allowExtension = true;	// RI026
				
		if( prevExtend )				// RI026
		{
					
			int size = EqpExtContracts.size();		// RI026
	
			for	( int i=0; i<size ; i++ )			// RI026
			{					
				String lstCon = (String)EqpExtContracts.get(i);		// RI026
							
				if (lstCon.trim().equals(str_contract.trim()) )		// RI026
				{
						allowExtension = false;	// RI026
						break;					// RI026				
				}
			}
						
		}
		
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
					<td background="images/bottom_back.gif" width="595"><!--COMPANY_NAME--><a class="company"><%=str_companyname.trim()%></a></td>
					<td width="30" align="left"><img src="images/bottom_back.gif" width="30" height="30"></td>
				</tr>

				<tr>
					<td><img src="images/empty.gif" height="10"></td>
					<td><img src="images/arrow.gif" height="10" align="right"></td>
					<td></td>
				</tr>

				<tr>
					<td><img src="images/empty.gif" height="30"></td>
					<td align="right" class="history"><a href='menu.jsp'>main menu</a>&nbsp;&nbsp;&nbsp;<%=str_pagetitle%>&nbsp;&nbsp;&nbsp;</td>
					<td></td>
					</tr>

			</table>

			<br>

			<% 

			// *** Output the contract/invoice type description  ***


			String str_contracttype_class = "tableheader3";

			if (str_contracttype == "UNKNOWN" ) {
				str_contracttype_class = "redbold";
				str_contracttype =  "Please note, this rental was set up with a Hertz acquisition company.  Detail is not available at this time. " + str_contactinfo2.trim() + "   We apologize for any inconvenience.";
			}
			
			%>
      
			<table border="0" width="650" cellspacing="0" cellpadding="0">
				<tr>
					<td width="20">&nbsp;</td>
					<td width="630"><a class="<%=str_contracttype_class%>"><!--BANNER--><%=str_contracttype%></a></td>
				</tr>
			</table>

			<br>

			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Customer Number: <a class="data"><!--CUSTOMER NUMBER--><%=str_account%></a>

			<br>
 
			<%
			// #######################################################################################################################
			// #      Output the Bill-to address
			// #######################################################################################################################
			%>
         
			<TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0" WIDTH="275" align="left">

				<tr>
 					<td background="images/tlcrnr.gif" align="right" width="25" height="30">&nbsp;</td>
					<td background="images/top_back.gif" width="225">&nbsp;</td>
					<td background="images/trcrnr.gif" align="left" width="25" height="30">&nbsp;</td>
				</tr>

				<tr>
					<td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
					<td align="left">Bill To</td>
					<td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
				</tr>

				<tr>
					<td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
					<td align="left"><a class="data"><!--BILL_NAME--><%=str_billname%></a></td>
					<td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
				</tr>

				<tr>
					<td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
					<td align="left"><a class="data"><!--BILL_ADD1--><%=str_billaddress1%><br><!--BILL_ADD2--><%=str_billaddress2%></a></td>
					<td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
				</tr>

				<tr>
					<td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
					<td align="left"><a class="data"><!--BILL_CITY--><%=str_billcity%>,&nbsp;<!--BILL_STATE--><%=str_billstate%>&nbsp;<!--BILL_ZIP--><%=str_billzip%></a></td>
					<td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
				</tr>

				<tr>
					<td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
					<td align="left"><a class="data">(<!--BILL_AREA--><%=str_billarea%>)&nbsp;<!--BILL_PHONE--><%=str_billphone%></a></td>
					<td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
				</tr>

				<tr>
					<td background="images/blcrnr.gif" align="right" width="25" height="30">&nbsp;</td>
					<td background="images/bottom_back.gif" width="225">&nbsp;</td>
					<td background="images/brcrnr.gif" align="left" width="25" height="30">&nbsp;</td>
				</tr>

			</TABLE>

			<%
			// #######################################################################################################################
			// #      Output the Ship-to address
			// #######################################################################################################################
			%>

			<TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0" WIDTH="275">

				<tr>
					<td background="images/tlcrnr.gif" align="right" width="25" height="30">&nbsp;</td>
					<td background="images/top_back.gif" width="225">&nbsp;</td>
					<td background="images/trcrnr.gif" align="left" width="25" height="30">&nbsp;</td>
				</tr>

				<tr>
					<td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
					<td align="left">Ship To</td>
					<td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
				</tr>

				<tr>
					<td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
					<td align="left"><a class="data"><!--SHIP_NAME--><%=str_shipname%></a></td>
					<td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
				</tr>

				<tr>
					<td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
					<td align="left"><a class="data"><!--SHIP_ADD1--><%=str_shipaddress1%><br><!--SHIP_ADD2--><%=str_shipaddress2%></a></td>
					<td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
				</tr>

				<tr>
					<td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
					<td align="left"><a class="data"><!--SHIP_CITY--><%=str_shipcity%>,&nbsp;<!--SHIP_STATE--><%=str_shipstate%>&nbsp;<!--SHIP_ZIP--><%=str_shipzip%></a></td>
					<td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
				</tr>

				<tr>
					<td align="left" WIDTH="25" background="images/left.gif">&nbsp;</td>
					<td align="left"><a class="data">(<!--SHIP_AREA--><%=str_shiparea%>)&nbsp;<!--SHIP_PHONE--><%=str_shipphone%></a></td>
					<td align="left" WIDTH="25" background="images/right.gif">&nbsp;</td>
				</tr>

				<tr>
					<td background="images/blcrnr.gif" align="right" width="25" height="30">&nbsp;</td>
					<td background="images/bottom_back.gif" width="225">&nbsp;</td>
					<td background="images/brcrnr.gif" align="left" width="25" height="30">&nbsp;</td>
				</tr>

			</TABLE>


			<br clear="all">

			<%
			// #######################################################################################################################
			// #      Output the contract/invoice header information
			// #######################################################################################################################
			%>

			<table cellpadding="3" cellspacing="0" border="0" width="675">

				<tr>
					<td width="325" align="left"><%=str_transdesc%> Date: <a class="data"><!--DATE--><%=str_sysd%></a></td>
					<td width="200" align="left"><%=str_transdesc%> Number: <a class="data"><!--CONTRACT--><%=str_contract%>-<!--SEQUENCE--><%=str_sequence%></a></td>
					<td width="150"  align="left">&nbsp;</td>
				</tr>


				<%  if ( num_sequence == 0  && !str_transtype.equals("sales")  )   {  %>
					<tr>
						<td align="left">Start Date: <a class="data"><!--START_DATE--><%=str_dateout%></a></td>
						<td align="left">Estimated Return Date: <a class="data"><!--RETURN_DATE--><%=str_estreturndate%></a></td>
						<td align="left">
						<% if ( equipChangeAuth.equals("Yes") && allowExtension && str_contracttype.trim().equals("OPEN RENTAL")) {  // RI026 %> 
							<a href="extendEquipment.jsp?contract=<%=str_contract%>">
							<IMG src="images/extend.gif" height="21" width="50" BORDER=0 align="center" valign="bottom"></a> 
						<% } %>
						</td>
					</tr>
				<%  }  %>


				<%  if ( num_sequence != 0 && str_type.equals("O") )  {  %>

						<%  
						if ( str_ordertype.equals("C") ) {  
							str_timeout = ""; 
							str_lastreturntime = "";
						}
						%>

						<tr>
							<td align="left">Invoice From Date: <a class="data"><!--INVOICE_FROM_DATE--><%=str_fromcyclebilldate%> <%=str_timeout%></a></td>
							<td align="left">Invoice To Date: <a class="data"><!--INVOICE_TO_DATE--><%=str_tocyclebilldate%> <%=str_lastreturntime%></a></td>
							<td>&nbsp;</td>
						</tr>

						<%  if (str_ordertype.equals("F") )  {   %>
							<tr>
								<td align="left"><!--RENTAL DAYS-->Rental Days: <a class="data"><%=num_rentaldays%></a></td>
								<td align="left"><!--ACUTAL RETURN DATE-->Actual Return Date: <a class="data"><%=str_lastreturndate%></a></td>
								<td>&nbsp;</td>
							</tr>
						<%  }  %>


				<%  }  %>

				<tr>
					<td  align="left">Job Number: <a class="data"><!--JOB_NUM--><%=str_jobnumber%></a></td>
					<td  align="left" colspan="2">P.O. Number: <a class="data"><!--PO_NUM--><%=str_ponumber%></a></td>
				</tr>

				<tr>
					<td  align="left">Ordered By: <a class="data"><!--ORDERED--><%=str_orderby%></a></td>
					<td  align="left" colspan="2">Signed By: <a class="data"><!--SIGNED BY--><%=str_signedby%></a></td>
				</tr>

				<tr>
					<td  align="left">
						Hertz Location: <a class="data"><!--RTN_LOCATION--><%=str_hertzlocation%></a>
						<% if ( !locationinfoArray[1].trim().equals("") )  {%>
							<BR>
							<table cellpadding="0" cellspacing="0" border="0" width="325">
								<tr>
									<td width="82" >&nbsp;</td>
									<td width = "234"><a class="data"><%=locationinfoArray[1]%></a></td>
								</tr>
							</table>			
						<%  }  %>
					</td>
					<td  align="left"><% if ( str_type.equals("W") ) %>Equipment Number:  <a class="data"><!--WORK ORDER EQP#--><%=str_woequipment%>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
			  	 
				<tr>
					<td align="left">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td>&nbsp;</td>
				</tr>

			</table>

			<%

			// #######################################################################################################################
			// #      Output the equipment section
			// #######################################################################################################################

			if ( num_sequence == 0 && !str_transtype.equals("sales") ) {        // **** Begin X0300 ****

			%>

 				<a class="tableheader1">Equipment</a><br>

				<table cellpadding="3" cellspacing="1" border="0" width="650">

					<tr>
						<td background="images/empty.gif" bgcolor="#000000" align="left" valign="bottom" width="15%" class="whitemid">Equipment</td>
						<td bgcolor="#000000" background="images/empty.gif" align="center" valign="bottom" width="10%" class="whitemid">Qty</td>
						<td bgcolor="#000000" background="images/empty.gif" align="center" valign="bottom" width="10%" class="whitemid">On Pickup Tkt</td>
						<td bgcolor="#000000" background="images/empty.gif" align="left" valign="bottom" width="65%" class="whitemid">Description</td>
					</tr>

					<%

					// ********************************************************
					//  *** Make a AS400 connection #4 - Retrieve item detail information    ***
					// ******************************************************** 

					contractdetail2Abeanconnection = contractdetail2bean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

					if ( contractdetail2bean.getRows( Integer.valueOf(str_customer).intValue(), str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), str_type, "equipment", str_transtype, str_ordertype) ) {     //  **** Begin X0400 ****


   
						// *** Loop through SQL results and load table      ***

						while ( contractdetail2bean.getNext() ) {     // **** Begin X0500 ****

							num_count++;

							if ( str_color.equals("#cccccc") )
								str_color = "#999999";
							else 
								str_color = "#cccccc";
   
							String str_item = contractdetail2bean.getColumn("RDITEM");
							String str_dseq = contractdetail2bean.getColumn("RDSEQ#");
							String str_catg = contractdetail2bean.getColumn("RDCATG");
							String str_class = contractdetail2bean.getColumn("RDCLAS");
							int int_catg = Integer.valueOf(str_catg).intValue();		// RI024
							int int_class = Integer.valueOf(str_class).intValue();		// RI024
							String str_stock = contractdetail2bean.getColumn("RDSTKC");
							String str_dayrate = contractdetail2bean.getColumn("RDDYRT");
							String str_weekrate = contractdetail2bean.getColumn("RDWKRT");
							String str_monthrate = contractdetail2bean.getColumn("RDMORT");
							String str_ratecode = contractdetail2bean.getColumn("RDRATU");
							String str_mhcode = contractdetail2bean.getColumn("RDMHCD").trim();
							String str_mhout = contractdetail2bean.getColumn("RDMHO");
							String str_mhchg = contractdetail2bean.getColumn("RDMICG");

							double  num_mhout = Double.valueOf(str_mhout).doubleValue();

							String str_pickup = "No";
							String str_equipmake = "";
							String str_equipmodel = ""; 
							String str_equipserialno = "";
							String str_mhtext= "";
							String str_mhtext2 = "";
							String str_dyfreemiles = "";
							String str_wkfreemiles = "";
							String str_mofreemiles = ""; 
							double temp_double = 0.00;

							if (str_mhcode.equals("H") )  {
								str_mhtext = "HR ";
								str_mhtext2 = "Hrs free: ";
							}
							else if (str_mhcode.equals("M") )  {
								str_mhtext = "MI ";
								str_mhtext2 = "Miles free: ";
							}

							// *** Retrieve the description for the detail item *** 

							String str_description = contractdetail2bean.getDescription( str_company, str_datalib, str_item, Integer.valueOf(str_catg).intValue(), Integer.valueOf(str_class).intValue(), str_stock);
                            

							// *** Retrieve the description for rerent items
							
							if ( str_company.equals("HG") && int_catg == 975 && int_class == 1 )	{	// RI024
								str_itemcomments = contractdetail2bean.getItemComments(str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), Integer.valueOf(str_dseq).intValue());  // RI024 
								if ( !str_itemcomments.trim().equals("") )		// RI024
									str_description = str_description.trim() + "<BR>" + str_itemcomments.trim() + "<BR>";	// RI024
							}
 
							// *** Retrieve the make, model and serial number for the detail item *** 

							if ( !str_catg.equals("") && !str_class.equals("") )   {
								String[] str_array = contractdetail2bean.getEquipmentInfo(str_company, str_datalib, str_item, Integer.valueOf(str_catg).intValue(), Integer.valueOf(str_class).intValue() );
								str_equipmake  = str_array[0];
								str_equipmodel  = str_array[1];
								str_equipserialno = str_array[2];
								str_array = contractdetail2bean.getFreeMiles(str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_dseq).intValue(), Integer.valueOf(str_catg).intValue(), Integer.valueOf(str_class).intValue() );
								str_dyfreemiles  = str_array[0];
								str_wkfreemiles  = str_array[1];
								str_mofreemiles  = str_array[2]; 
								if ( str_dyfreemiles.equals("0") )
									str_dyfreemiles = "";
								if ( str_wkfreemiles.equals("0") )
									str_wkfreemiles = "";
								if ( str_mofreemiles.equals("0") )
									str_mofreemiles = "";
							}


							// *** Remove the decimal portion of the rates  ***

							temp_double = Double.parseDouble( str_dayrate );
							int num_dayrate = (int) temp_double;

							temp_double = Double.parseDouble( str_weekrate );
							int num_weekrate = (int) temp_double;

							temp_double = Double.parseDouble( str_monthrate );
							int num_monthrate = (int) temp_double;


							str_pickup = contractdetail2bean.getPickupStatus(str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_dseq).intValue(), str_item  );
  
							if ( str_pickup.equals("OP") )
								str_pickup = "Yes";
							else
								str_pickup = "No";
 
							
							// ************************************************************************
							//  RI026 - Check if this equipment qualifies for online release or extend.
							//  This section will determine if a release has already been done for
							//  the equipment within this session
							// ************************************************************************

						
							if( prevReleases && str_pickup.equals("No") && equipChangeAuth.equals("Yes") )	// RI026
							{	
								String str_qty = contractdetail2bean.getColumn("RDQTY").trim();					// RI026
						
								int num_qty = 0;								// RI026
								if (str_qty == null) str_qty = "0";				// RI026
							
								int i = str_qty.indexOf(".");					// RI026
									
								if (i > 0)	str_qty = str_qty.substring(0,i);	// RI026
									
								num_qty = Integer.valueOf(str_qty).intValue();	// RI026

								int lstAllRelQty = 0;							// RI026
								int size = EqpRelContracts.size();				// RI026
							
								for	(i=0; i < size; i++)						// RI026
								{
							
									String lstCon     = (String) EqpRelContracts.get(i);				// RI026
									String lstLineNum = (String) EqpRelLineNum.get(i);					// RI026
									String lstQty     = (String) EqpRelQtyRel.get(i);					// RI026
										
									if (lstCon.trim().equals(str_contract) && lstLineNum.trim().equals(str_dseq) )		// RI026
										lstAllRelQty = lstAllRelQty + Integer.valueOf(lstQty).intValue() ;	// RI026
								}	
								
								if(lstAllRelQty >= num_qty )	// RI026
									str_pickup = "Yes";			// RI026
							}
						
					%>

					<tr>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" valign="top" align="left" class="tabledata"><!--ITEM--><%=str_item%></td>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" valign="top" align="center" class="tabledata"><!--QUANTITY--><%=contractdetail2bean.getColumn("RDQTY")%></td>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" valign="top" align="center" class="tabledata"><!--ON PICKUP--><%=str_pickup%><br><br>
							<% if ( equipChangeAuth.equals("Yes") && str_pickup.equals("No") && str_contracttype.trim().equals("OPEN RENTAL") ) { // RI026 %> 
								<a href="releaseEquipment.jsp?contract=<%=str_contract%>&lineNum=<%=str_dseq%>"><IMG src="images/release.gif" height="21" width="50" BORDER=0 align="center" valign="bottom"></a>
							<% } %>
						</td>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" valign="top" align="left" class="tabledata"><!--DESC--><%=str_description%>


							<%  if ( !str_type.equals("S") && !str_type.equals("I") && !str_type.equals("W")  ) {   %>

								<% if ( ( num_dayrate != 0   ||   num_weekrate != 0   ||   num_monthrate != 0)   &&  !str_type.equals("Q")   )  {   %>
									<br>Rates: Day&nbsp;/&nbsp;Week&nbsp;/&nbsp;4 Week:&nbsp;$<%=num_dayrate%>&nbsp;/&nbsp;$<%=num_weekrate%>&nbsp;/&nbsp;$<%=num_monthrate%>
								<%  }  %>


								<% if (!str_equipmake.equals("") || !str_equipmodel.equals("") || !str_equipserialno.equals("")  ) { %>
									<br> 
									<% if (!str_equipmake.equals("") )  %>
										Make:&nbsp;<%=str_equipmake%>&nbsp;&nbsp;

									<% if (!str_equipmodel.equals("") )  %>
										Model:&nbsp;<%=str_equipmodel%>&nbsp;&nbsp;

									<% if (!str_equipserialno.equals("") )  %>
										Ser #:&nbsp;<%=str_equipserialno%>&nbsp;&nbsp;
							<% }  %>


							<%  if ( !str_catg.equals("") && !str_class.equals("") && !str_mhtext.equals("")  &&  !str_type.equals("Q")  )   {  %>
								<br><%=str_mhtext2%> 
								<%  if ( !str_dyfreemiles.equals("")  ||   !str_wkfreemiles.equals("")  ||  !str_mofreemiles.equals("") )   {  %>
										Day <%=str_dyfreemiles%>&nbsp;&nbsp;Week <%=str_wkfreemiles%>&nbsp;&nbsp;4 Week <%=str_mofreemiles%>
								<%  }  
								}   
							%>


							<% if ( !str_mhout.equals("0.000") && !str_mhtext.equals("")  &&  !str_type.equals("Q")  )  {    %>

								<%
									str_mhout = str_mhout.substring(0,  str_mhout.length()-1 );

									if (str_mhchg.equals("0.00") )
										str_mhchg = "";
								%>

									<br>
									<%=str_mhtext%>out: <%=str_mhout%>&nbsp; 
									<%=str_mhtext%>chg: <%=str_mhchg%>

							<%
								}
							%>

						<%  }  %>

						</td>
					</tr>


					<%
   
						}      // **** End X0500 ****

					}    // **** End X0400 ****


					//  *** End AS400 connection #4 - Retrieve item detail information    ***

					contractdetail2bean.cleanup();	// RI023
					contractdetail2bean.endcurrentConnection(contractdetail2Abeanconnection);


                     %>

				</table>

			<%

			}     // **** End X0300 ****


				// ********************************************************
				//  *** Make a AS400 connection #5 - Retrieve item detail information    ***
				// ******************************************************** 

		else if ( !str_transtype.equals("sales")  || str_type.equals("T") ||  str_type.equals("Y") ) {     // **** Begin X0600 ****

			%>

			<a class="tableheader1">Equipment</a><br>

			<!--Equipment Closed Header-->

			<table cellpadding="3" cellspacing="1" border="0" width="650">

				<tr>
					<td background="images/empty.gif" bgcolor="#000000" align="left" width="12%" class="whitemid">Equipment#</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left" width="6%" class="whitemid">Qty</td>
					<td bgcolor="#000000" background="images/empty.gif" align="left" width="70%" class="whitemid">Description</td>
					<td bgcolor="#000000" background="images/empty.gif" align="right" width="12%" class="whitemid">Amount</td>
				</tr>

				<%

				contractdetail2Bbeanconnection = contractdetail2bean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

				if ( contractdetail2bean.getRows( Integer.valueOf(str_customer).intValue(), str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), str_type, "equipment", str_transtype, str_ordertype) ) {     // **** Begin X0700 ****


					// *** Loop through SQL results and load table  ***

					while ( contractdetail2bean.getNext() ) {     // **** Begin X0800 ****

						num_count++;

						if ( str_color.equals("cccccc") )
							str_color = "999999";
						else 
							str_color = "cccccc";

						String str_item = contractdetail2bean.getColumn("RDITEM");
						String str_dseq = contractdetail2bean.getColumn("RDSEQ#");
						String str_stock = contractdetail2bean.getColumn("RDSTKC");
						String str_catg = contractdetail2bean.getColumn("RDCATG");
						String str_class = contractdetail2bean.getColumn("RDCLAS");
						int int_catg = Integer.valueOf(str_catg).intValue();		// RI024
						int int_class = Integer.valueOf(str_class).intValue();		// RI024
						String str_amount = contractdetail2bean.getColumn("RDAMT$");
						String str_dayrate = contractdetail2bean.getColumn("RDDYRT");
						String str_weekrate = contractdetail2bean.getColumn("RDWKRT");
						String str_monthrate = contractdetail2bean.getColumn("RDMORT");
						String str_ratecode = contractdetail2bean.getColumn("RDRATU");
						String str_mhcode = contractdetail2bean.getColumn("RDMHCD");
						String str_mhout = contractdetail2bean.getColumn("RDMHO");
						String str_mhin  = contractdetail2bean.getColumn("RDMHI");
						String str_mhchg = contractdetail2bean.getColumn("RDMICG");
						String str_mhamt$ = contractdetail2bean.getColumn("RDEXM$");
						String str_mhtot  = contractdetail2bean.getColumn("MHTOTAL");
						String str_rateused  = "";
						String str_equipmake = "";
						String str_equipmodel = ""; 
						String str_equipserialno = "";
						String str_mhtext = "";
						String str_mhtext2 = "";
						String str_dyfreemiles = "";
						String str_wkfreemiles = "";
						String str_mofreemiles = ""; 
						double temp_double = 0.00;

						if (str_mhcode.equals("H") )  {
							str_mhtext             = "HR ";
							str_mhtext2           = "Hrs free: ";
						}
						else if (str_mhcode.equals("M") )  {
							str_mhtext             = "MI ";
							str_mhtext2           = "Miles free: ";
						}

						// *** Retrieve the description for the detail item *** 

						String str_description = contractdetail2bean.getDescription( str_company, str_datalib, str_item, Integer.valueOf(str_catg).intValue(), Integer.valueOf(str_class).intValue(), str_stock);

						// *** Retrieve the description for rerent items
							
						if ( str_company.equals("HG") && int_catg == 975 && int_class == 1)	{	// RI024
							str_itemcomments = contractdetail2bean.getItemComments(str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), Integer.valueOf(str_dseq).intValue());  // RI024 
							if ( !str_itemcomments.trim().equals("") )		// RI024
								str_description = str_description.trim() + "<BR>" + str_itemcomments.trim() + "<BR>";	// RI024
						}
									
						// *** Retrieve the make, model and serial number for the detail item *** 

						if ( !str_catg.equals("") && !str_class.equals("") )   {
							String[] str_array = contractdetail2bean.getEquipmentInfo(str_company, str_datalib, str_item, Integer.valueOf(str_catg).intValue(), Integer.valueOf(str_class).intValue() );
							str_equipmake = str_array[0];
							str_equipmodel = str_array[1];
							str_equipserialno = str_array[2];
							str_array = contractdetail2bean.getFreeMiles(str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_dseq).intValue(), Integer.valueOf(str_catg).intValue(), Integer.valueOf(str_class).intValue() );
							str_dyfreemiles = str_array[0];
							str_wkfreemiles = str_array[1];
							str_mofreemiles = str_array[2]; 
							if ( str_dyfreemiles.equals("0") )
								str_dyfreemiles = "";
							if ( str_wkfreemiles.equals("0") )
								str_wkfreemiles = "";
							if ( str_mofreemiles.equals("0") )
							str_mofreemiles = "";
						}


						// *** Remove the decimal portion of the rates  ***

						temp_double = Double.parseDouble( str_dayrate );
						int num_dayrate = (int) temp_double;

						temp_double = Double.parseDouble( str_weekrate );
						int num_weekrate = (int) temp_double;

						temp_double = Double.parseDouble( str_monthrate );
						int num_monthrate = (int) temp_double;


						// *** Indicate which rate was used  ****

						if ( str_ratecode.equals("D") )
							str_rateused = "<br>Rate Type Used: Day";
						else if ( str_ratecode.equals("W") )
							str_rateused = "<br>Rate Type Used: Week";
						else if ( str_ratecode.equals("M") )
							str_rateused = "<br>Rate Type Used: 4 Week";


				%>

				<tr>
					<td bgcolor="<%=str_color%>" background="images/empty.gif" valign="top" align="left" class="tabledata"><!--ITEM--><%=str_item%></td>
					<td bgcolor="<%=str_color%>" background="images/empty.gif" valign="top" align="left" class="tabledata"><!--QUANTITY--><%=contractdetail2bean.getColumn("RDQTY")%></td>
					<td bgcolor="<%=str_color%>" background="images/empty.gif" valign="top" align="left" class="tabledata"><!--DESC--><%=str_description%>

						<%  if ( !str_type.equals("S") && !str_type.equals("I") && !str_type.equals("W")  ) {   %>

							<% if ( !str_ratecode.equals("")  &&  !str_type.equals("Q")  && !str_type.equals("T")  && !str_type.equals("Y") )  {   %>
								<br>Rates: Day&nbsp;/&nbsp;Week&nbsp;/&nbsp;4 Week:&nbsp;$<%=num_dayrate%>&nbsp;/&nbsp;$<%=num_weekrate%>&nbsp;/&nbsp;$<%=num_monthrate%>
							<% } %>

							<% if (!str_equipmake.equals("") || !str_equipmodel.equals("") || !str_equipserialno.equals("")  ) { %>
									<br>
									<% if (!str_equipmake.equals("") )  %>
										Make:&nbsp;<%=str_equipmake%>&nbsp;&nbsp;

									<% if (!str_equipmodel.equals("") )  %>
										Model:&nbsp;<%=str_equipmodel%>&nbsp;&nbsp;

									<% if (!str_equipserialno.equals("") )  %>
										Ser #:&nbsp;<%=str_equipserialno%>&nbsp;&nbsp;
							<% }  %>

							<%  if ( !str_catg.equals("") && !str_class.equals("") &&  !str_type.equals("Q") && !str_mhtext.equals("") )   {  %>
									<br><%=str_mhtext2%> 
									<%  if ( !str_dyfreemiles.equals("")  ||   !str_wkfreemiles.equals("")  ||  !str_mofreemiles.equals("") )   {  %>
											Day <%=str_dyfreemiles%>&nbsp;&nbsp;Week <%=str_wkfreemiles%>&nbsp;&nbsp;4 Week <%=str_mofreemiles%>
								<%  }  
								}   
							%>



							<%  if ( !str_mhout.equals("0.000") && !str_mhtext.equals("") &&  !str_type.equals("Q")  )  {    %>

								<% 

									if (str_mhin.equals("0.000") )  {
										str_mhin = "";
										str_mhtot = str_mhout;
									}  else {
										str_mhin = str_mhin.substring(0,  str_mhin.length()-1 );
									}

									str_mhout = str_mhout.substring(0,  str_mhout.length()-1 );
									str_mhtot  = str_mhtot.substring(0,  str_mhtot.length()-1 );

									if (str_mhamt$.equals("0.00") )
										str_mhamt$ = "";

									if (str_mhchg.equals("0.00") )
										str_mhchg = "";


									%>

									<br>
									<%=str_mhtext%>out: <%=str_mhout%>  
									<%=str_mhtext%>in: <%=str_mhin%>  
									Total:&nbsp;<%=str_mhtot%> 
									<%=str_mhtext%> chg: <%=str_mhchg%>  
									Tot chg: <%=str_mhamt$%>&nbsp; 

								<%
								}
								%>

						<%  }  %>


					</td>
					<td bgcolor="<%=str_color%>" background="images/empty.gif" valign="top" align="right" width="20%" class="tabledata"><!--AMOUNT--><%=str_amount%></td>
				</tr>

			<%

					}     // **** End X0800 ****

				}     // **** End X0700 ****


				//  *** End currect connection #5  ***

				contractdetail2bean.cleanup();	// RI0023
				contractdetail2bean.endcurrentConnection(contractdetail2Bbeanconnection);


			%>

			</table>


          <%

		}    // **** End X0600 ****

		%>


		<%

          // #######################################################################################################################
          // #      Create the Sales/Misc section table
          // #######################################################################################################################

		if ( !str_contracttype.equals("Q") && !str_transtype.equals("sales") ) {     // **** Begin X0900 ****


			// *****************************
			// Make a AS400 connection # 6
			// *****************************

			contractdetail2Cbeanconnection = contractdetail2bean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

			if ( contractdetail2bean.getRows( Integer.valueOf(str_customer).intValue(), str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), str_type, "sales", str_transtype, str_ordertype) ) {    // **** Begin X01000 ****

				str_tableheader = "";

				//  *** Loop through SQL results and load table ***

				while ( contractdetail2bean.getNext() ) {    // **** Begin X01100 ****

					num_count++;

					if ( str_color.equals("cccccc") )
						str_color = "999999";
					else 
						str_color = "cccccc";

					String str_item = contractdetail2bean.getColumn("RDITEM");
					String str_stock = contractdetail2bean.getColumn("RDSTKC");
					String str_catg = "0";
					String str_class = "0";
					String str_amount = "";

					if ( !str_transtype.equals("sales") ) {
						str_catg = contractdetail2bean.getColumn("RDCATG");
						str_class = contractdetail2bean.getColumn("RDCLAS");
						if ( num_sequence != 0 )
							str_amount = contractdetail2bean.getColumn("RDAMT$");
					}   

					double num_soldprice = Double.valueOf( contractdetail2bean.getColumn("SOLDPRICE")  ).doubleValue();

					str_amount = df.format(num_soldprice);

					int decpos = str_amount.indexOf("."); 

					if (decpos == -1 )
						str_amount = str_amount + ".00";

					if (  (   (str_amount.length()-1)  - decpos ==  1 )  )
						str_amount = str_amount + "0";


					String str_description = contractdetail2bean.getDescription( str_company, str_datalib, str_item, Integer.valueOf(str_catg).intValue(), Integer.valueOf(str_class).intValue(), str_stock);

		%>

					<!--Sales Table for Equipment Open and Closed-->

					<%  if (str_tableheader.equals("")  )  {  %>
 
							<a class="tableheader1">Sales/Misc.</a><br>

							<table cellpadding="3" cellspacing="1" border="0" width="650">

								<tr>
									<td background="images/empty.gif" bgcolor="#000000" align="left" width="20%" class="whitemid">Item #</td>
									<td bgcolor="#000000" background="images/empty.gif" align="left" width="10%" class="whitemid">Qty</td>
									<td bgcolor="#000000" background="images/empty.gif" align="left" width="10%" class="whitemid">UM</td>
									<td bgcolor="#000000" background="images/empty.gif" align="left" width="40%" class="whitemid">Description</td>
									<td bgcolor="#000000" background="images/empty.gif" align="right" width="10%" class="whitemid">Price</td>
									<td bgcolor="#000000" background="images/empty.gif" align="right" width="10%" class="whitemid">Amount</td>
								</tr>

								<% 
								str_tableheader = "X";

						}   %>

						<tr>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" width="20%" class="tabledata"><!--ITEM--><%=str_item%></td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" width="10%" class="tabledata"><!--QUANTITY--><%=contractdetail2bean.getColumn("RDQTY")%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" width="10%" class="tabledata"><!--UNIT--><%=contractdetail2bean.getColumn("RDUNIT")%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left" width="40%" class="tabledata"><!--DESC--><%=str_description%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" width="10%" class="tabledata"><!--REG_AMOUNT--><%=contractdetail2bean.getColumn("RDPRCE")%>&nbsp;</td>
							<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" width="10%" class="tabledata"><!--SOLD_AMOUNT-->
								<%
									// ignore  if (num_sequence != 0) {
                            	%>
								<%=str_amount%>
								<%
									// ignore                                     }
								%>&nbsp;
							</td>
						</tr>

				<%
				}     // **** End X01100 ****

			}    // **** End X01000 ****


			//  *** End currect connection # 6   ***
			
			contractdetail2bean.cleanup();	// RI0023
			contractdetail2bean.endcurrentConnection(contractdetail2Cbeanconnection);


			%>


			</table>

			<%

		}    // **** End X0900 ****

		else {   // **** Begin X01200 ****

          %>

		<%

		// ****************************
		// Make a AS400 connection #7
		// ****************************

		contractdetail2Dbeanconnection = contractdetail2bean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password);

		if ( contractdetail2bean.getRows( Integer.valueOf(str_customer).intValue(), str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), str_type, "sales", str_transtype, str_ordertype) ) {    // **** Begin X01300 ****

			str_tableheader = "";

			// *** Loop through SQL results and load table ***

			while ( contractdetail2bean.getNext() ) {    // **** Begin X01400 ****

				num_count++;

				if ( str_color.equals("cccccc") )
					str_color = "999999";
				else 
					str_color = "cccccc";

				String str_item = contractdetail2bean.getColumn("RDITEM");
				String str_stock = contractdetail2bean.getColumn("RDSTKC");
				String str_qty = contractdetail2bean.getColumn("RDQTY");
				String str_uom = contractdetail2bean.getColumn("RDUNIT").trim();
				String str_uprice = contractdetail2bean.getColumn("RDPRCE");
				String str_catg = "0";
				String str_class = "0";
				String str_amount = "";

				if ( !str_transtype.equals("sales") ) {
					str_catg = contractdetail2bean.getColumn("RDCATG");
					str_class = contractdetail2bean.getColumn("RDCLAS");
					if ( num_sequence != 0 )   {
						str_amount = contractdetail2bean.getColumn("RDAMT$");
					}
				}   

				// ignore  if ( str_type.equals("S")  ||  str_type.equals("I")  ||  str_type.equals("W")  ||  str_type.equals("Y")  ||  str_type.equals("T")  ||  str_type.equals("G")   ||  str_type.equals("X")  )   {

				double num_soldprice = Double.valueOf( contractdetail2bean.getColumn("SOLDPRICE")  ).doubleValue();

				str_amount = df.format(num_soldprice);

				int decpos = str_amount.indexOf("."); 

				if (decpos == -1 )
					str_amount = str_amount + ".00";

				if (  (   (str_amount.length()-1)  - decpos ==  1 )  )
					str_amount = str_amount + "0";
 
				// ignore                                 }


				String str_description = "";

				if ( str_type.equals("W") )   {
					if ( contractdetail2bean.getColumn("RDTYPE").equals("LO")  )
						str_description = "Outside labor";
					else if ( contractdetail2bean.getColumn("RDTYPE").equals("LI")  )
						str_description = "Inside labor";
					else if ( contractdetail2bean.getColumn("RDTYPE").equals("MC")  )
						str_description = "Mileage charge";
				}

				if ( str_description.equals("") )
					str_description = contractdetail2bean.getDescription( str_company, str_datalib, str_item, Integer.valueOf(str_catg).intValue(), Integer.valueOf(str_class).intValue(), str_stock);


				//*******************************************************
				// RI025 - Calculate shop supply fees for Domestic only
				//******************************************************

				
				if ( str_company.trim().equals("HG") && str_type.equals("W") )		// RI025
				{ 
					String str_empCls = contractdetail2bean.getColumn("RSEMPC");	// RI025
				
					if ( str_empCls.trim().equalsIgnoreCase("SHPSP-SYS") ||			// RI025
						 str_empCls.trim().equalsIgnoreCase("SHPSP-MAN") ||	
				 		str_empCls.trim().equalsIgnoreCase("SHPSP-LEG") )	 
						{	 
							num_totShopFees = num_totShopFees + Double.valueOf(str_amount).doubleValue();	// RI025

						}
				}		          
				%>

				<%  
				if (str_tableheader.equals("")  )  {  %>

					<!--Only Table for Equipment Sales-->

					<a class="tableheader1">Sales/Misc.</a><br>

					<table cellpadding="3" cellspacing="1" border="0" width="650">

						<tr>
							<td bgcolor="#000000" background="images/empty.gif" align="left"   width="20%" class="whitemid">Item</td>
							<td bgcolor="#000000" background="images/empty.gif" align="left"   width="10%" class="whitemid">Qty</td>
							<td bgcolor="#000000" background="images/empty.gif" align="left"   width="10%" class="whitemid">Unit</td>
							<td bgcolor="#000000" background="images/empty.gif" align="left"   width="40%" class="whitemid">Description</td>
							<td bgcolor="#000000" background="images/empty.gif" align="right" width="10%" class="whitemid">Price</td>
							<td bgcolor="#000000" background="images/empty.gif" align="right" width="10%" class="whitemid">Amount</td>
						</tr>

						<% 
						str_tableheader = "X";

				}   %>

				<%  
				String str_showline = "Y";

				if ( ( str_qty.equals("0.00")  ||  str_uom.equals("") ) &&  str_uprice.equals("0.000") &&  str_amount.equals("0.00")  && str_uom.equals("")  )    
					str_showline = "N";

				if ( str_showline.equals("Y") )  {   %>
					<tr>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"    class="tabledata"><!--ITEM--><%=str_item%></td>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"    class="tabledata"><!--QUANTITY--><%=str_qty%>&nbsp;</td>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"    class="tabledata"><!--UNIT--><%=str_uom%>&nbsp;</td>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" align="left"    class="tabledata"><!--DESC--><%=str_description%>&nbsp;</td>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" class="tabledata"><!--REG_AMOUNT--><%=str_uprice%>&nbsp;</td>
						<td bgcolor="<%=str_color%>" background="images/empty.gif" align="right" class="tabledata"><!--SOLD_AMOUNT--><%=str_amount%>&nbsp;</td>
					</tr>
				<%
				}  
				%>


				<%

			}    // **** End X01400 ****


		}    // **** End X01300 ****


		//  End currect connection #7

		contractdetail2bean.cleanup();	// RI0023
		contractdetail2bean.endcurrentConnection(contractdetail2Dbeanconnection);


		%>

		</table>

          <%

		}     // **** End X01200 ****

		%>

          
		<% 

		// ##################################################################################################
		// #  Output the contract totals
		// ##################################################################################################

		if ( str_showtotals.equals("Y")  )  {    // **** Begin X01250 ****

		%>

		<br><br>

		<TABLE BORDER="0" CELLSPACING="0" CELLPADDING="3" WIDTH="650">

			<tr>
				<td align="left"    width="270"  height="30">&nbsp;</td>
				<td align="right"  width="25"   background="images/tlcrnr.gif">&nbsp;</td>
				<td align="right"  width="25"   background="images/top_back.gif">&nbsp;</td>
				<td align="left"     width="180" background="images/top_back.gif">&nbsp;</td>
				<td align="right"  width="100" background="images/top_back.gif">&nbsp;</td>
				<td align="right"  width="25"   background="images/top_back.gif">&nbsp;</td>
				<td align="left"     width="25"   background="images/trcrnr.gif">&nbsp;</td>
			</tr>

			<tr>
				<td align="left">&nbsp;</td>
				<td align="left" background="images/left.gif">&nbsp;</td>
				<td align="left">&nbsp;</td>
				<td align="left" class="tableheader1">Invoice Totals</td>
				<td align="right">&nbsp;</td>
				<td align="left">&nbsp;</td>
				<td align="left" background="images/right.gif">&nbsp;</td>
			</tr>

			<% 
			// ***********************
			// Output the rental amount
			// ***********************
               
			int decpos = 0;

			if ( num_rentamt != 0.0 ) {  

				String str_rentamt = df.format(num_rentamt);

				decpos = str_rentamt.indexOf("."); 

				if (decpos == -1 )
					str_rentamt = str_rentamt + ".00";

				if (  (   (str_rentamt.length()-1)  - decpos ==  1 )  )
					str_rentamt = str_rentamt + "0";
   
				%>

				<tr>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Rental Amount</td>
					<td align="right" class="data"><%=str_rentamt%></td>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/right.gif">&nbsp;</td>
				</tr>
			<%  
			}  
			%>


				<%  
			// ******************************
			// Output the damage waiver amount
			// ******************************

			if ( num_dwamt != 0.0  ) {  

				String str_dwamt = df.format(num_dwamt);

				decpos = str_dwamt.indexOf(".");    

				if (decpos == -1 )
					str_dwamt = str_dwamt + ".00";

				if (  (   (str_dwamt.length()-1)  - decpos ==  1 )  )
					str_dwamt = str_dwamt + "0";

				%>
				<tr>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Damage Waiver</td>
					<td align="right" class="data"><%=str_dwamt.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left"  background="images/right.gif">&nbsp;</td>
				</tr>

				<%  
			}  
			%>


			<% 
			// *************************************************
			// Output the sales amount or Parts and Materials
			// *************************************************

			if ( num_slsamt != 0.0  ) {  

				String str_slsamt = df.format(num_slsamt);

				decpos = str_slsamt.indexOf(".");    

				if (decpos == -1 )
					str_slsamt = str_slsamt + ".00";

				if (  (   (str_slsamt.length()-1)  - decpos ==  1 )  )
					str_slsamt = str_slsamt + "0";

				String str_amountlabel = "Sales Amount";
				
				if ( str_type.equals("W") )
					str_amountlabel = "Parts & Materials";
					
				%>
				<tr>
					<td align="left">&nbsp;</td>
					<td align="left"  background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left"><%=str_amountlabel%></td>
					<td align="right" class="data"><%=str_slsamt.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left"  background="images/right.gif">&nbsp;</td>
				</tr>

			<%
			}
			%>


			<%  
			// ***********************
			// Output the fuel amount
			// ***********************

			if ( num_fuelamt != 0.0 ) {  

				String str_fuelamt = df.format(num_fuelamt);

				decpos = str_fuelamt.indexOf(".");    

				if (decpos == -1 )
					str_fuelamt = str_fuelamt + ".00";

				if (  (   (str_fuelamt.length()-1)  - decpos ==  1 )  )
					str_fuelamt = str_fuelamt + "0";

				%>
				<tr>
					<td align="left">&nbsp;</td>
					<td align="left"  background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Fuel Charge</td>
					<td align="right" class="data"><%=str_fuelamt.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left"  background="images/right.gif">&nbsp;</td>
				</tr>

			<%
			}
			%>


			<%  
			// **************************
			// Output the labor charge amount
			// **************************

			if ( num_laboramt != 0.0 ) {  

				String str_laboramt = df.format(num_laboramt);

				decpos = str_laboramt.indexOf(".");    

				if (decpos == -1 )
					str_laboramt = str_laboramt + ".00";

				if (  (   (str_laboramt.length()-1)  - decpos ==  1 )  )
					str_laboramt = str_laboramt + "0";

				%>
				<tr>
					<td align="left">&nbsp;</td>
					<td align="left"  background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Labor Charge</td>
					<td align="right" class="data"><%=str_laboramt.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left"  background="images/right.gif">&nbsp;</td>
				</tr>

			<%
			}
			%>


			<%  
			// *********************************************************************
			// RI025 - Output the shop supply fees for work orders for domestic only
			// *********************************************************************


			if ( num_totShopFees != 0.0 && str_company.trim().equals("HG") && str_type.equals("W") ) 
			{

				String str_totShopFees = df.format(num_totShopFees);
	
				decpos = str_totShopFees.indexOf(".");    
	
				if (decpos == -1 )
					str_totShopFees = str_totShopFees + ".00";

				if (  (   (str_totShopFees.length()-1)  - decpos ==  1 )  )
					str_totShopFees = str_totShopFees + "0";

				if ( num_miscamt != 0.0 )
					num_miscamt    = num_miscamt - num_totShopFees;
					
				%>
				<tr>
					<td align="left">&nbsp;</td>
					<td align="left"  background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Shop Supply Fee</td>
					<td align="right" class="data"><%=str_totShopFees.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left"  background="images/right.gif">&nbsp;</td>
				</tr>

			<%
			  }	
			%>
			

			<% 
			// *************************************************
			// Add returns to the misc amount.  
			// *************************************************

			if ( num_returnamt != 0.0  )
				num_miscamt    = num_miscamt + num_returnamt;
    
			%>


			<%  
			// ***************************
			// Output the misc amount
			// ***************************

			if ( num_miscamt != 0.0 ) {  

				String str_miscamt = df.format(num_miscamt);

				decpos = str_miscamt.indexOf(".");    

				if (decpos == -1 )
					str_miscamt = str_miscamt + ".00";

				if (  (   (str_miscamt.length()-1)  - decpos ==  1 )  )
					str_miscamt = str_miscamt + "0";
                           
				%>
				<tr>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Miscellaneous Charges</td>
					<td align="right" class="data"><%=str_miscamt.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/right.gif">&nbsp;</td>
				</tr>

			<%
			}
			%>


			<%  
			// ***************************
			// Output the delivery amount
			// **************************

			if ( num_delvamt != 0.0 ) {  

				String str_delvamt = df.format(num_delvamt);

				decpos = str_delvamt.indexOf(".");    

				if (decpos == -1 )
					str_delvamt = str_delvamt + ".00";

				if (  (   (str_delvamt.length()-1)  - decpos ==  1 )  )
					str_delvamt = str_delvamt + "0";

				%>
				<tr>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Delivery Charge</td>
					<td align="right" class="data"><%=str_delvamt.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/right.gif">&nbsp;</td>
				</tr>

			<%
			}
			%>


			<%  
			// ***************************
			// Output the pick-up amount
			// **************************

			if ( num_pickamt != 0.0 ) { 

				String str_pickamt = df.format(num_pickamt);

				decpos = str_pickamt.indexOf(".");    

				if (decpos == -1 )
					str_pickamt = str_pickamt + ".00";

				if (  (   (str_pickamt.length()-1)  - decpos ==  1 )  )
					str_pickamt = str_pickamt + "0";

				%>
				<tr>
					<td align="left">&nbsp;</td>
					<td align="left"  background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Pickup Charge</td>
					<td align="right" class="data"><%=str_pickamt.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/right.gif">&nbsp;</td>
				</tr>

			<%
			}
			%>


			<%  
			// ***************************
			// Output the tax amount
			// **************************

			if ( num_taxamt != 0.0 ) {  

				String str_taxamt = df.format(num_taxamt);

				decpos = str_taxamt.indexOf(".");    

				if (decpos == -1 )
					str_taxamt = str_taxamt + ".00";

				if (  (   (str_taxamt.length()-1)  - decpos ==  1 )  )
					str_taxamt = str_taxamt + "0";

			%>
				<tr>
					<td align="left">&nbsp;</td>
					<td align="left"  background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Sales Tax</td>
					<td align="right" class="data"><%=str_taxamt.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/right.gif">&nbsp;</td>
				</tr>

			<%
			}
			%>


			<%
			// ***************************
			// Output the invoice amount
			// **************************

			String str_invamt = df.format(num_invamt);

			decpos = str_invamt.indexOf(".");    

			if (decpos == -1 )
				str_invamt = str_invamt + ".00";

			if (  (   (str_invamt.length()-1)  - decpos ==  1 )  )
				str_invamt = str_invamt + "0";

			%>

			<tr>
				<td align="left">&nbsp;</td>
				<td align="left" background="images/left.gif">&nbsp;</td>
				<td align="left">&nbsp;</td>
				<td align="left">Total Invoice Amount</td>
				<td align="right" class="data">$&nbsp;<%=str_invamt.trim()%></td>
				<td align="left">&nbsp;</td>
				<td align="left" background="images/right.gif">&nbsp;</td>
			</tr>

			<%
			// ***************************************************************************
			// If the current balance if a credit, then add the amount to the
			// adjustment total and then clear the current balance.  
			// This modification does not apply to Canada ("CR")
			// *************************************************************************** 
			// x020 - Only 'Validated Credit' balances will be displayed as 
			// credit balances for Domestic. Others will be processed and then 
			// displayed. A valid credit is having the collection status code range 
			//	from B001 to B498. 
			// ***************************************************************************
 
			if ( num_currentbal < 0 &&  str_company.trim().equals("HG") )  {
				if ( validCredit == 0) {
					num_cradjamt = num_cradjamt + Math.abs(num_currentbal);
					num_currentbal = 0.0;
				}
			}

			%>


			<% 
			// ***********************************
			// Output the credit/adjustment amount and
			// the adjusted total
			// ***********************************

			if ( num_cradjamt != 0.0 ) {  

				double num_adjtotal  = num_invamt +  num_cradjamt;
				String str_cradjamt     = df.format(num_cradjamt);
				String str_adjtotal       = df.format(num_adjtotal);
                                             
				decpos = str_cradjamt.indexOf(".");    

				if (decpos == -1 )
					str_cradjamt = str_cradjamt + ".00";

				if (  (   (str_cradjamt.length()-1)  - decpos ==  1 )  )
					str_cradjamt = str_cradjamt + "0";
            
				decpos = str_adjtotal.lastIndexOf(".");    

				if (decpos == -1 )
					str_adjtotal = str_adjtotal + ".00";

				if (  (   (str_adjtotal.length()-1)  - decpos ==  1 )  )
					str_adjtotal = str_adjtotal + "0";
    
			%>

				<tr>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Total Adjustments</td>
					<td align="right" class="data"><%=str_cradjamt.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/right.gif">&nbsp;</td>
				</tr>

				<tr>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Adjusted Total</td>
					<td align="right" class="data">$&nbsp;<%=str_adjtotal.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/right.gif">&nbsp;</td>
				</tr>

			<%
			}
			%>

			<%
			// ****************************
			// Output the total payments amount
			// ****************************

			if ( str_getARamounts.equals("Y") && num_payments != 0.0 ) {  

				String str_payments = df.format(num_payments);

				decpos = str_payments.indexOf(".");    

				if (decpos == -1 )
					str_payments = str_payments + ".00";

				if (  (   (str_payments.length()-1)  - decpos ==  1 )  )
					str_payments = str_payments + "0";

			%>

				<tr>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Payments Applied</td>
					<td align="right" class="data">$&nbsp;<%=str_payments.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/right.gif">&nbsp;</td>
				</tr>

			<%
			}
			%>


			<%
			// ****************************
			// Output the current balance
			// ****************************

			if ( str_getARamounts.equals("Y") && num_sequence != 0  ) {  

				String str_currentbal = df.format(num_currentbal);

				decpos = str_currentbal.indexOf(".");    

				if (decpos == -1 )
					str_currentbal = str_currentbal + ".00";

				if (  (   (str_currentbal.length()-1)  - decpos ==  1 )  )
					str_currentbal = str_currentbal + "0";

			%>

				<tr>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/left.gif">&nbsp;</td>
					<td align="left">&nbsp;</td>
					<td align="left">Net Due</td>
					<td align="right" class="data">$&nbsp;<%=str_currentbal.trim()%></td>
					<td align="left">&nbsp;</td>
					<td align="left" background="images/right.gif">&nbsp;</td>
				</tr>

			<%
			}
			%>

			<tr>
				<td align="left">&nbsp;</td>
				<td background="images/blcrnr.gif" align="right" height="30">&nbsp;</td>
				<td background="images/bottom_back.gif" colspan = "4" >&nbsp;</td>
				<td background="images/brcrnr.gif" align="left" height="30">&nbsp;</td>
			</tr>


		</TABLE>
 
		<br>

	<%
	}    // **** End  X01250 ****    
	%>

	<br>


         <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="3" WIDTH="650">
             <tr>
                <td><%=str_contactinfo1%></td>
             </tr>
         </table>
           
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

<%

    }    // **** End X0200 ****

}  // **** End X0100 ****

// Determine if there was invoice/contract detail displayed.  If not show error page.

if ( str_invoiceError.equals("Y") )
  response.sendRedirect("invoiceError.jsp?contract=" + str_contract + "&sequence=" + str_sequence + "&transtype=" + str_transtype);	

%>

</HTML>
