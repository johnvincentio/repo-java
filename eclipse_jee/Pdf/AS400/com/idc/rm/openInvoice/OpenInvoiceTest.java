package com.idc.rm.openInvoice;

import java.util.Calendar;

/*
 * do openInvoice.jsp; use InvoiceAgingBean and OpenInvoiceBean.
 */

public class OpenInvoiceTest {
	public static void main (String[] args) {
		try {
			long start = System.currentTimeMillis();
			(new OpenInvoiceTest()).doTest1();
			System.out.println("test total time : " + (System.currentTimeMillis() - start));
		}
		catch (Exception ex) {
			System.out.println("Exception; "+ex.getMessage());
		}
	}
	private void doTest1() throws Exception {
		System.out.println(">>> doTest1 - 1");
		OpenInvoiceInfo openInvoiceInfo = new OpenInvoiceInfo();

		//ageDate can be set, used as the date for aging.
		// not currently in use.
		// set to today
//		yyyymmdd

		String agingDate = "20071201";

		String list_customer = "2821732";
		String accountNumber = "2821732";
		String accountName = "Padme";	// account name from RM
		openInvoiceInfo = doAccount (openInvoiceInfo, list_customer, accountNumber, 
				accountName, agingDate);

		list_customer = "8513422";
		accountNumber = "8513422";
		accountName = "SHADY GRADY CONSTRUCTION CO";	// account name from RM
		openInvoiceInfo = doAccount (openInvoiceInfo, list_customer, accountNumber, 
				accountName, agingDate);

		StringBuffer buf = openInvoiceInfo.getCSV (true);
		System.out.println("CSV: \n"+buf.toString());
		System.out.println("<<< doTest1 - 1");
	}

	private OpenInvoiceInfo doAccount (OpenInvoiceInfo openInvoiceInfo,
			String list_customer, String accountNumber, String accountName, String str_agingdate) throws Exception {
		System.out.println(">>> doAccount");
		OpenInvoiceBean openInvoiceBean = new OpenInvoiceBean();
		System.out.println("Connection to DB");
		openInvoiceBean.makeConnection();

		String str_company = "HG";
		String str_dateselect = "";
		String str_datalib = "RMHCQDATA";
		String str_OrderBy = "";

		/*
		 * Aging options;
		 * 
		 * open
		 * curr
		 * 30days
		 * 60days
		 * 90days
		 * 120days
		 * 150days
		 */

		String str_agebucket = "open";
		if (str_agebucket == null) str_agebucket = "";
		int dayspos = str_agebucket.indexOf("days"); 
		if (str_agebucket.equals("open") || (dayspos == -1 && (! str_agebucket.equals("curr")))) str_agebucket = "";

/*
 * This could use some refactoring as not dependent on the account itself. Just do once.
 */
		if (! str_agebucket.equals("")) {

			Calendar Today = Calendar.getInstance();

			int temptoday = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

			if (str_agingdate.length() != 8)
				str_agingdate = Integer.toString(temptoday);

			Calendar Age30 = Calendar.getInstance();
			Calendar Age60 = Calendar.getInstance();
			Calendar Age90 = Calendar.getInstance(); 
			Calendar Age120 = Calendar.getInstance();
			Calendar Age150 = Calendar.getInstance();

			int ageyear = Integer.valueOf(str_agingdate.substring(0, 4)).intValue();
			int agemonth = Integer.valueOf(str_agingdate.substring(4, 6)).intValue();
			int ageday  = Integer.valueOf(str_agingdate.substring(6, 8)).intValue();

			// *************************************************************
			// Account for the Java date handling where January = 0 and December = 11
			// *************************************************************
		  
			agemonth = agemonth - 1;

			Age30.set(ageyear,  agemonth, ageday);
			Age60.set(ageyear,  agemonth, ageday);
			Age90.set(ageyear,  agemonth, ageday);
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

			String str_agetotaldesc;
			if (str_agebucket.equals("curr") )  {
				str_dateselect = str_current;
				str_agetotaldesc = "Current invoices:";
			} else if (str_agebucket.equals("30days")) {
				str_dateselect = str_age30;
				str_agetotaldesc = "30 days total:";
			} else if (str_agebucket.equals("60days")) {
				str_dateselect = str_age60;
				str_agetotaldesc = "60 days total:";
			} else if (str_agebucket.equals("90days")) {
				str_dateselect = str_age90;
				str_agetotaldesc = "90 days total:";
			} else if (str_agebucket.equals("120days")) {
				str_dateselect = str_age120;
				str_agetotaldesc = "120 days total:";
			} else if (str_agebucket.equals("150days")) {
				str_dateselect = str_age150;
				str_agetotaldesc = "Over 150 days total:";
			}
		}

		if (openInvoiceBean.getRows (list_customer, Integer.valueOf(accountNumber).intValue(), 
				str_company, str_datalib, str_dateselect, str_OrderBy)) {
			while (openInvoiceBean.getNext()) {
				openInvoiceInfo.add (new OpenInvoiceItemInfo (openInvoiceBean, accountNumber, accountName));
			}
		}

		openInvoiceBean.cleanup();
		openInvoiceBean.endConnection();
		System.out.println("<<< doAccount");
		return openInvoiceInfo;
	}
}
