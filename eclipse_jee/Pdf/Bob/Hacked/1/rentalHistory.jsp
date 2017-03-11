
String str_username    = (String) session.getAttribute("username");		// RI013
String str_customer    = (String) session.getAttribute("customer");		// RI013
String str_company     = (String) session.getAttribute("company");		// RI013
String str_datalib     = (String) session.getAttribute("datalib");		// RI013
String list_customer   = (String) session.getAttribute("list");			// RI013
String str_password    = (String) session.getAttribute("password");		// RI013
String str_as400url    = (String) session.getAttribute("as400url");		// RI013
String str_as400driver = (String) session.getAttribute("as400driver");	// RI013
String loginuserid     = (String) session.getAttribute("loginuserid");	// RI013
String str_GoldCountry = (String) session.getAttribute("CountryCode");	// RI013
String str_connType    = "INQ"; 	//RI013
String str_userid = "";

if ( loginuserid == null )
	loginuserid = "";

// ************************
// Get input parameters
// ************************

String str_companyname  = (String) session.getAttribute("companyname");		// RI003

String startnumber = request.getParameter("startnumber");
String jobnumber = request.getParameter("jobnumber");

String sortFld0 = (String) request.getParameter("sortFld0");
String sortFld1 = (String) request.getParameter("sortFld1");
String sortFld2 = (String) request.getParameter("sortFld2");

String order0 = (String) request.getParameter("order0");
String order1 = (String) request.getParameter("order1");
String order2 = (String) request.getParameter("order2");

String selectYear = (String) request.getParameter("selectYear"); 	// RI012

// ********************************
// Validate the input parameters
// ********************************

if ( startnumber == null )	startnumber = "0";
if ( jobnumber == null )	jobnumber = ""; 
if ( selectYear == null )	selectYear = Integer.toString(cpYear); 	// RI012

// ***********************************
// RI012 - Format year filter options
// ***********************************

String strYearFilter = yearOptionsBean.getFilterOptions(selectYear);	// RI012


// ********************************
// Download variables
// ********************************

String displayPageName = "Rental History by Equip Type for " + selectYear;		// RI010

// **********************
// End current connection
//**********************

MenuSecurityBean.endcurrentConnection(MenuSecurityBeanconnection);

		historybeanconnection = historybean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password, str_GoldCountry, str_connType);	// RI013

		String str_total    = historybean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, jobnumber, selectYear);

		String str_totalall = historybean.getNumRowsAll(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, jobnumber, selectYear);

		int num_display = 100;	// RI014
		int num_count = 0;
		int num_total = Integer.valueOf(str_total).intValue();
		int num_current = Integer.valueOf(startnumber).intValue();
		int num_nextstart = num_current + num_display;
		int num_prevstart = num_current - num_display;

		if ( num_prevstart <= 0 )
			num_prevstart = 0;

		// ***************************************************
		//  RI010 - Setup the data list array used for the download.  
		//  The first line is reserved for column heading
		// ***************************************************
		
		String dataList [][] = new String[num_total+1][8];		// RI010
		
		dataList[0][0] = "Account#";		// RI010
		dataList[0][1] = "Account Name";	// RI010
		dataList[0][2] = "Cat-Class";		// RI010
		dataList[0][3] = "Description";		// RI010
		dataList[0][4] = "Rental Days";		// RI010
		dataList[0][5] = "# of Trans";		// RI010
		dataList[0][6] = "Rental Amount";	// RI010
		dataList[0][7] = "Rental Year";	// RI012

		// ****************************************************************
		// RI010 - Attempt to retrieve session objects for this report
		// ****************************************************************

		String sesDataList[][] = (String [][])session.getAttribute("dataList");		// RI010
		String reportName      = (String)session.getAttribute("reportName");		// RI010
		boolean loadDataList   = false;												// RI010

				int num_start = 0;					// RI010
							
				if  ( !loadDataList )				// RI010
						num_start = num_current;	// RI010


				if ( historybean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, num_start, jobnumber, str_OrderBy, selectYear) ) {

					// ********************************************************************************
					// RI010 - Break out of the loop if the data list has already been loaded
					//         and the page record limit has been reached
					// ********************************************************************************

					// RI010 -    while ( historybean.getNext() && num_count < num_display) {

					while ( historybean.getNext() ) {		// RI010
					
						if ( (num_count >= num_display) && !loadDataList )	// RI010
							break;											// RI010
							
						num_count++;

						// **********************************
						// Format the category/class fields
						// **********************************
   
						str_catg = historybean.getColumn("RDCATG");

						while ( str_catg.length() < 3 ) {
							str_catg = "0" + str_catg;
						}

						str_class = historybean.getColumn("RDCLAS");

						while ( str_class.length() < 4 ) {
							str_class = "0" + str_class;
						}


						str_description = historybean.getColumn("ECDESC").replace('"', ' ');

						// **************************************************************
						// RI010 - Add the detail to the two-dimentional array used later to output to a CSV file
						//         Ensure that null values are changes to blanks
						// **************************************************************
						
						dataList[num_count][0] = str_customer;										// RI010
						dataList[num_count][1] = str_companyname;									// RI010
						dataList[num_count][2] = str_catg + "-" + str_class ;						// RI010
				   		dataList[num_count][3] = historybean.getColumn("ECDESC");					// RI010
				   		dataList[num_count][4] = historybean.getColumn("totalrentdays");			// RI010
				   		dataList[num_count][5] = historybean.getColumn("totalTransactions");		// RI010
				   		dataList[num_count][6] = "$" + historybean.getColumn("totalrentamount");	// RI010
				   		dataList[num_count][7] = selectYear;										// RI012

						for(int j = 0; j < dataList[num_count].length; j++)					// RI010
						{																	// RI010
							if ( dataList[num_count][j] == null )							// RI010
								dataList[num_count][j] = "";								// RI010
						}																	// RI010
						
					}
				}

  // **********************
  // End current connection
  //**********************

 historybean.cleanup();		// RI011
 historybean.endcurrentConnection(historybeanconnection);

