
String str_companyname  = (String) session.getAttribute("companyname");		// RI013

String startnumber   = request.getParameter("startnumber");
String str_catg      = request.getParameter("catg");
String str_class     = request.getParameter("class");
String str_desc      = request.getParameter("desc");
String str_jobnumber = request.getParameter("jobnumber");

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
if (str_catg == null) str_catg="";
if (str_class == null) str_class="";
if (str_desc == null) str_desc="";
if (str_jobnumber == null) str_jobnumber="";
if ( selectYear == null )	selectYear = Integer.toString(cpYear); 				// RI012
if ( selectYear.trim().equals("") )	 selectYear = Integer.toString(cpYear); 	// RI012

		String str_total = detailhistorybean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, str_datalib, str_catg, str_class, str_jobnumber, selectYear);
		int num_total = Integer.valueOf(str_total).intValue();
		int num_current = Integer.valueOf(startnumber).intValue();

if ( detailhistorybean.getRows(list_customer, Integer.valueOf(str_customer).intValue(), str_company, 
		str_datalib, str_catg, str_class, str_jobnumber, num_start, str_OrderBy, selectYear ) ) {

					while ( detailhistorybean.getNext() ) {		// RI011

						String str_sequence = detailhistorybean.getColumn("RDISEQ");

						while( str_sequence.length() < 3 ) {
							str_sequence = "0" + str_sequence;
						}

						// ***************************************************************
						// for rate used, substitute a word for the single letter returned in the result set
						// ***************************************************************

						str_rateused = detailhistorybean.getColumn("RDRATU");

						if ( str_rateused.trim().equals("H") ) 
							str_rateused = "DAY";
						else if (str_rateused.equals("D")) 
							str_rateused = "DAY";
						else if (str_rateused.equals("W")) 
							str_rateused = "WEEK";
						else if (str_rateused.equals("M")) 
							str_rateused = "MONTH";

						// ******************
						// Invoice date
						// ******************
												
						String str_invdate = detailhistorybean.getColumn("RDSYSD");

						if ( !str_invdate.trim().equals("0") && str_invdate.length() == 8)  {
						
							str_invdate = str_invdate.substring(4, 6) + "/" + str_invdate.substring(6, 8) + "/" + str_invdate.substring(2, 4);

						} else {
						
							str_invdate = "";
							
						}


						// ******************
						// Start date
						// ******************

						String str_dateout = detailhistorybean.getColumn("RDDATO");
          
						if ( !str_dateout.trim().equals("0") && str_dateout.length() == 8)   {

							str_dateout = str_dateout.substring(4, 6) + "/" + str_dateout.substring(6, 8) + "/" + str_dateout.substring(2, 4);

						} else {
						
							str_dateout = "";
							
						}


						// ******************
						// Return date
						// ******************

						String str_datein = detailhistorybean.getColumn("RDDATI");
          
						if ( !str_datein.trim().equals("0") && str_datein.length() == 8)  {

							str_datein = str_datein.substring(4, 6) + "/" + str_datein.substring(6, 8) + "/" + str_datein.substring(2, 4);

						} else  {

							str_datein = "";

						}

						// *** Retrieve the description for rerent items

						int int_catg = Integer.valueOf(str_catg).intValue();		//  RI012
						int int_class = Integer.valueOf(str_class).intValue();		//  RI012
						
						if ( str_company.equals("HG") && int_catg == 975 && int_class == 1 )	{	// RI012
							String str_contract = detailhistorybean.getColumn("RDCON#");			// RI002
							String str_dseq = detailhistorybean.getColumn("RDSEQ#");				// RI002
							str_itemcomments = detailhistorybean.getItemComments(str_company, str_datalib, Integer.valueOf(str_contract).intValue(), Integer.valueOf(str_sequence).intValue(), Integer.valueOf(str_dseq).intValue());  // RI012
						} else {
							str_itemcomments = "";	// RI012
						}
						
						// **************************************************************
						// RI011 - Add the detail to the two-dimentional array used later to output to a CSV file
						//         Ensure that null values are changes to blanks
						// **************************************************************

						dataList[num_count][0] = str_customer;													// RI011
						dataList[num_count][1] = str_companyname;												// RI011
						dataList[num_count][2] = str_catg + "-" + str_class;									// RI011
						dataList[num_count][3] = str_desc;														// RI011
						dataList[num_count][4] = detailhistorybean.getColumn("RDITEM");							// RI011
						dataList[num_count][5] = detailhistorybean.getColumn("RDCON#") + "-" + str_sequence;	// RI011
						dataList[num_count][6] = str_invdate ;													// RI011
						dataList[num_count][7] = str_dateout;													// RI011
						dataList[num_count][8] = str_datein;													// RI011
						dataList[num_count][9] = str_rateused;													// RI011
						dataList[num_count][10] =  "$" + detailhistorybean.getColumn("RDAMT$");					// RI011
						dataList[num_count][11] = selectYear;													// RI012

						if ( !str_itemcomments.trim().equals("") )	{											// RI002
							dataList[num_count][3] = dataList[num_count][3].trim() + " *** COMMENT: " + str_itemcomments.trim();		// RI002
							str_itemcomments = "<BR>" + str_itemcomments.trim();
						}
						


	detailhistorybean.endcurrentConnection(detailhistorybeanconnection);
