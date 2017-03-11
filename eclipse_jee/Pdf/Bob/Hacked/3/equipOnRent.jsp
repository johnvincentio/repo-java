
<jsp:useBean id="pendingtransbean" class="etrieveweb.etrievebean.pendingtransBean" scope="page" />

String    equipChangeAuth = (String) session.getAttribute("equipChangeAuth");	// RI018
String str_allowReleases  = (String) session.getAttribute("AllowReleases");	// RI024
String str_allowExtend    = (String) session.getAttribute("AllowExtend");	// RI025

String str_connType    = "INQ"; 	//RI020
String str_PUTicket    = "";		//RI024

String str_allpickup = "";

if (equipChangeAuth == null)	// RI024 RI025
	equipChangeAuth = "No";		// RI024 RI025

if (str_allowReleases == null)	// RI024
	str_allowReleases   = "N";	// RI024

if (str_allowExtend == null)	// RI025
	str_allowExtend   = "N";	// RI025
	
ArrayList EqpExtContracts = new ArrayList();	// RI025
ArrayList EqpRelContracts = new ArrayList();	// RI024
ArrayList EqpRelLineNum = new ArrayList();		// RI024
ArrayList EqpRelQtyRel  = new ArrayList();		// RI024
ArrayList EqpExtConEstDate = new ArrayList();		// Vrush
ArrayList EqpExtConStatus = new ArrayList();		// Vrush
int rel = 0;	// RI024
int ext = 0;	// RI025

if ( equipChangeAuth.trim().equals("Yes") && ( str_allowReleases.equals("Y") || str_allowExtend.equals("Y") ) ) {	// RI024 RI025
	
	if ( str_company.equals("HG") ) {		// RI024 RI025

		if ( pendingtransbean.getPendingTrans(Integer.valueOf(str_customer).intValue(), str_company, str_datalib ) ) {    // RI024 RI025
			
			while ( pendingtransbean.getNext() ) {	// RI024 RI025

				if ( pendingtransbean.getColumn("THTYPE").trim().equals("REL") ) {		// RI024 RI025

					String str_openqty = pendingtransbean.getColumn("TDQTY").trim();	// RI024 RI025

					int i = str_openqty.indexOf(".");				// RI024 RI025
					if (i > 0)										// RI024 RI025
						str_openqty = str_openqty.substring(0,i);	// RI024 RI025
					
					EqpRelContracts.add(rel, pendingtransbean.getColumn("THCON#").trim() );		// RI024 RI025
					EqpRelLineNum.add(rel, pendingtransbean.getColumn("TDSEQ#").trim() );		// RI024 RI025
					EqpRelQtyRel.add(rel,  str_openqty );		// RI024 RI025
					rel++;		// RI024 RI025
				} else if ( pendingtransbean.getColumn("THTYPE").trim().equals("EXT") ) {		// RI024 RI025
					EqpExtContracts.add(ext, pendingtransbean.getColumn("THCON#").trim() );		// RI024 RI025
					EqpExtConEstDate.add(ext, pendingtransbean.getColumn("THERDT").trim() );		// RI025
					EqpExtConStatus.add(ext, pendingtransbean.getColumn("THSTAT").trim() );		// RI025
					ext++;		// RI024 RI025
				}		// RI024 RI025
			}			// RI024 RI025
		}				// RI024 RI025
		
		pendingtransbean.endcurrentConnection(pendingtransbeanconnection);	// RI024 RI025
		
	} else if ( str_company.equals("CR") ) {	// RI024 RI025
	
		EqpExtContracts = (ArrayList) session.getAttribute("EqpExtContracts");	// RI024 RI025
		EqpRelContracts = (ArrayList) session.getAttribute("EqpRelContracts");	// RI024 RI025
		EqpRelLineNum   = (ArrayList) session.getAttribute("EqpRelLineNum");	// RI024 RI025
		EqpRelQtyRel    = (ArrayList) session.getAttribute("EqpRelQtyRel");		// RI024 RI025
		
	}	// RI024 RI025
}

boolean prevReleases = true;		// RI018
boolean prevExtend   = true;		// RI018

if (EqpRelContracts == null || EqpRelLineNum == null || EqpRelQtyRel == null)		// RI018
	prevReleases = false;															// RI018

if (EqpExtContracts == null)	// RI018 
	prevExtend   = false;		// RI018

if (equipChangeAuth == null)	// RI018 
	equipChangeAuth = "No";		// RI018



//*************************************************************
// RI012 - Retrieve today's date to be used for copyright statment.
//*************************************************************
  
Calendar cpToday = Calendar.getInstance();
int cpYear = cpToday.get(Calendar.YEAR) ;  

// ********************************************
// RI013 - Retrieve the current system date
//*********************************************

Calendar Today = Calendar.getInstance();
int todayDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

// ***********************
// Get input parameters
// ***********************
 
String str_companyname  = (String) session.getAttribute("companyname");		// RI020

String startnumber = request.getParameter("startnumber");
String jobnumber = request.getParameter("jobnumber");

		String str_description = "";
		String str_contract = "";
		String str_startdate = "";
		String str_dseq = "";
		String str_item = "";
		String str_estRetDate = "";
		String str_jobname = "";		// RI017
		String str_dayrate = "";		// RI019
		String str_weekrate = "";		// RI019
		String str_monthrate = "";		// RI019
		String str_totalbilled = "";	// RI019
		String str_totalaccrued = "";	// RI019
		String str_totalestcost = "";	// RI019
		double num_totalbilled  = 0.0;	// RI019
		double num_totalaccrued = 0.0;	// RI019
		double num_totalestcost = 0.0;	// RI019
		double temp_double = 0.00;		// RI019
		
		int decpos2 = 0;				// RI019
		
		DecimalFormat df = new DecimalFormat("###,###,###,###.##");		// RI019
		
		boolean overdueContract = false;
		
		int num_total = Integer.valueOf(str_total).intValue();
		int num_current = Integer.valueOf(startnumber).intValue();

		dataList[0][0] = "Account#";			// RI014
		dataList[0][1] = "Account Name";		// RI014
		dataList[0][2] = "Contract#";			// RI014
		dataList[0][3] = "Start Date";			// RI014
		dataList[0][4] = "Est Return Date";		// RI014
		dataList[0][5] = "Equipment#";			// RI014
		dataList[0][6] = "Quantity";			// RI014
		dataList[0][7] = "Description";			// RI014
		dataList[0][8] = "On Pickup Tkt";		// RI014
		dataList[0][9] = "Ordered By";			// RI014			
		dataList[0][10] = "Purchase Order";		// RI014
		dataList[0][11] = "Overdue";			// RI014
		dataList[0][12] = "Job Name";			// RI017
		dataList[0][13] = "Daily rate";			// RI019
		dataList[0][14] = "Weekly rate";		// RI019
		dataList[0][15] = "4 Week rate";		// RI019
		dataList[0][16] = "Total Billed";		// RI019
		dataList[0][17] = "Est. Cost to Date";	// RI019
		dataList[0][18] = "Total Est. Cost";	// RI019
						
		if ( !str_company.equals("HG") ) {		// RI019
			dataList[0][16] = "";				// RI019
			dataList[0][17] = "";				// RI019
			dataList[0][18] = "";				// RI019
		}		// RI019

				
if ( equiponrentbean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_start, jobnumber, str_OrderBy ) ) {    // RI014

					// ******************************************************************************************
					// Query the database to retrieve all the pickup tickets for the customer.  Create one long string containing the 
					// contract, sequence and equipment number.  Each set will begin and end with two asterisks.  This long string will 
					// later be scanned to see if an equipment number is scheduled for pickup
					// ******************************************************************************************

					while ( equiponrentbean.getNext() ) {		// RI014 
					
						// ******************************************************
						// RI019 - format the total billed amount for output
						// ******************************************************
						
						num_totalbilled = Double.valueOf(str_totalbilled).doubleValue();	// RI019
						
						str_totalbilled    = df.format(num_totalbilled);					// RI019
						
						decpos2 = str_totalbilled.indexOf(".");		// RI019

						if (decpos2 == -1 )								// RI019
							str_totalbilled = str_totalbilled + ".00";	// RI019

						if ( ( ( str_totalbilled.length()-1)  - decpos2 ==  1 )  )	// RI019
							str_totalbilled = str_totalbilled + "0";				// RI019

						if ( !str_company.trim().equals("HG") )		// RI019
							str_totalbilled = ".00";				// RI019
							
						// ******************************************************
						// RI019 - format the total accrued amount for output
						// ******************************************************
					
						num_totalaccrued = Double.valueOf(str_totalaccrued).doubleValue();	// RI019
						
						str_totalaccrued   = df.format(num_totalaccrued);					// RI019
						
						decpos2 = str_totalaccrued.indexOf(".");		// RI019 

						if (decpos2 == -1 )									// RI019
							str_totalaccrued = str_totalaccrued + ".00";	// RI019

						if ( ( ( str_totalaccrued.length()-1)  - decpos2 ==  1 )  )	// RI019
							str_totalaccrued = str_totalaccrued + "0";				// RI019

						if ( !str_company.trim().equals("HG") )		// RI019
							str_totalaccrued = ".00";				// RI019
							
						// **************************************************************
						// RI019 - format the total estimated rental amount for output
						// *************************************************************
						
						num_totalestcost = Double.valueOf(str_totalestcost).doubleValue();	// RI019
						
						str_totalestcost   = df.format(num_totalestcost);		// RI019
							
						decpos2 = str_totalestcost.indexOf(".");		// RI019 

						if (decpos2 == -1 )									// RI019
							str_totalestcost = str_totalestcost + ".00";	// RI019

						if ( ( ( str_totalestcost.length()-1)  - decpos2 ==  1 )  )	// RI019
							str_totalestcost = str_totalestcost + "0";				// RI019

						if ( !str_company.trim().equals("HG") )		// RI019
							str_totalestcost = ".00";				// RI019
							
