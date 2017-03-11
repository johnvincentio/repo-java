<jsp:useBean id="openinvoicebean" class="etrieveweb.etrievebean.openinvoiceBean" scope="page" />
<jsp:useBean id="invoiceagingbean" class="etrieveweb.etrievebean.invoiceagingBean" scope="page" />
String str_isUsingInternational = (String) session.getAttribute("isUsingInternational");  // RI013, RI014
String str_GoldCountry = (String) session.getAttribute("CountryCode");		// RI014
String str_connType    = "INQ"; 	//RI014

// ************************
// Get input parameters
// ************************

String str_agebucket = request.getParameter("aging");
String str_agingdate = request.getParameter("agedate");

String str_dateselect = "";
String str_agetotal = "";
String str_agetotaldesc = "";

if ( str_agebucket == null )	str_agebucket = ""; 
if ( str_agingdate == null )	str_agingdate = "";
 
int dayspos = str_agebucket.indexOf("days"); 

// ****************************************
// Get aging date and format date selection criteria
// ****************************************

if ( str_agebucket.equals("open")  ||  ( dayspos == -1 && !str_agebucket.equals("curr") )  )
	str_agebucket = "";

if ( !str_agebucket.equals("") )  {

	Calendar Today = Calendar.getInstance();

	int temptoday = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

	if ( str_agingdate.length() != 8 )
		str_agingdate = Integer.toString(temptoday);

	int ageyear=0, agemonth=0, ageday=0;

	Calendar Age30 = Calendar.getInstance();
	Calendar Age60 = Calendar.getInstance();
	Calendar Age90 = Calendar.getInstance(); 
	Calendar Age120 = Calendar.getInstance();
	Calendar Age150 = Calendar.getInstance();

	ageyear = Integer.valueOf( str_agingdate.substring(0, 4) ).intValue();
	agemonth = Integer.valueOf( str_agingdate.substring(4, 6) ).intValue();
	ageday  = Integer.valueOf( str_agingdate.substring(6, 8) ).intValue();

	// *************************************************************
	// Account for the Java date handling where January = 0 and December = 11
	// *************************************************************
  
	agemonth = agemonth - 1;

	Age30.set(ageyear,   agemonth, ageday);
	Age60.set(ageyear,   agemonth, ageday);
	Age90.set(ageyear,   agemonth, ageday);
	Age120.set(ageyear, agemonth, ageday);
	Age150.set(ageyear, agemonth, ageday);

	Age30.add(Calendar.DATE, -29);
	Age60.add(Calendar.DATE, -59);
	Age90.add(Calendar.DATE, -89);
	Age120.add(Calendar.DATE, -119);
	Age150.add(Calendar.DATE, -149);

	int fromdate = 0;
	int todate = 0;

	todate = Age30.get(Calendar.YEAR)*10000 + (Age30.get(Calendar.MONTH)+1)*100 + Age30.get(Calendar.DAY_OF_MONTH);
	String str_current = " and AHDUED >= " + Integer.toString(todate);

	fromdate = Age60.get(Calendar.YEAR)*10000 + (Age60.get(Calendar.MONTH)+1)*100 + Age60.get(Calendar.DAY_OF_MONTH);
	todate = Age30.get(Calendar.YEAR)*10000 + (Age30.get(Calendar.MONTH)+1)*100 + Age30.get(Calendar.DAY_OF_MONTH);
	String str_age30 = " and AHDUED >= " + Integer.toString(fromdate) + " and AHDUED < " + Integer.toString(todate);

	fromdate = Age90.get(Calendar.YEAR)*10000 + (Age90.get(Calendar.MONTH)+1)*100 + Age90.get(Calendar.DAY_OF_MONTH);
	todate = Age60.get(Calendar.YEAR)*10000 + (Age60.get(Calendar.MONTH)+1)*100 + Age60.get(Calendar.DAY_OF_MONTH);
	String str_age60 = " and AHDUED >= " + Integer.toString(fromdate) + " and AHDUED < " + Integer.toString(todate);

	fromdate = Age120.get(Calendar.YEAR)*10000 + (Age120.get(Calendar.MONTH)+1)*100 + Age120.get(Calendar.DAY_OF_MONTH);
	todate = Age90.get(Calendar.YEAR)*10000 + (Age90.get(Calendar.MONTH)+1)*100 + Age90.get(Calendar.DAY_OF_MONTH);
	String str_age90 = " and AHDUED >= " + Integer.toString(fromdate) + " and AHDUED < " + Integer.toString(todate);

	fromdate = Age150.get(Calendar.YEAR)*10000 + (Age150.get(Calendar.MONTH)+1)*100 + Age150.get(Calendar.DAY_OF_MONTH);
	todate = Age120.get(Calendar.YEAR)*10000 + (Age120.get(Calendar.MONTH)+1)*100 + Age120.get(Calendar.DAY_OF_MONTH);
	String str_age120 = " and AHDUED >= " + Integer.toString(fromdate) + " and AHDUED < " + Integer.toString(todate);

	todate = Age150.get(Calendar.YEAR)*10000 + (Age150.get(Calendar.MONTH)+1)*100 + Age150.get(Calendar.DAY_OF_MONTH);
	String str_age150 = " and AHDUED < " +  Integer.toString(todate);

	invoiceagingbeanconnection = invoiceagingbean.makeautoConnection(str_as400driver, str_as400url, str_username, str_password, str_GoldCountry, str_connType);		// RI014

	String[] str_array = invoiceagingbean.getNumRows(list_customer, Integer.valueOf(str_customer).intValue(), 
				str_company, str_datalib, str_agingdate, str_agingdate, str_isUsingInternational);	// RI013

	if ( str_agebucket.equals("curr") )  {
		str_dateselect = str_current;
		str_agetotal   = str_array[1];
		str_agetotaldesc = "Current invoices:";
	} else if ( str_agebucket.equals("30days") )  {
		str_dateselect = str_age30;
		str_agetotal   = str_array[2];
		str_agetotaldesc = "30 days total:";
	} else if ( str_agebucket.equals("60days") )  {
		str_dateselect = str_age60;
		str_agetotal   = str_array[3];
		str_agetotaldesc = "60 days total:";
	} else if ( str_agebucket.equals("90days") )  {
		str_dateselect = str_age90;
		str_agetotal   = str_array[4];
		str_agetotaldesc = "90 days total:";
	} else if ( str_agebucket.equals("120days") ) {
		str_dateselect = str_age120;
		str_agetotal   = str_array[5];
		str_agetotaldesc = "120 days total:";
	} else if ( str_agebucket.equals("150days") )  {
		str_dateselect = str_age150;
		str_agetotal   = str_array[7];
		str_agetotaldesc = "Over 150 days total:";
	}
    
	int decpos = 0;

	decpos = str_agetotal.indexOf("."); 

	if (decpos == -1 )
		str_agetotal = str_agetotal + ".00";

	if (  (   (str_agetotal.length()-1)  - decpos ==  1 )  )
		str_agetotal = str_agetotal + "0";

	invoiceagingbean.endcurrentConnection(invoiceagingbeanconnection);


}


