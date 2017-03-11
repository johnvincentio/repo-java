/********************************************************************
*			Copyright (c) 2006 The Hertz Corporation				*
*			  All Rights Reserved.  (Unpublished.)					*
*																	*
*		The information contained herein is confidential and		*
*		proprietary to The Hertz Corporation and may not be			*
*		duplicated, disclosed to third parties, or used for any		*
*		purpose not expressly authorized by it.  Any unauthorized	*
*		use, duplication, or disclosure is prohibited by law.		*
*																	*
*********************************************************************/

/*
 * Created on Apr 10, 2007
 */
package com.hertz.hercutil.framework;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Michael Tardif
 */
public class DateHelper {

	/* 
	 * these mthods validate date strings 
	 */
	
	public static boolean isValidFormMonthYear(String monthYear) {
		return (parseFormMonthYear(monthYear) != null) ? true : false;
	}
	
	public static boolean isValidFormMonthYearDay(String day, String monthYear) {
		return (parseFormMonthYearDay(day, monthYear) != null) ? true : false;
	}
	
	/* 
	 * these methods convert date strings to Dates 
	 */
	
	// parses date string of the form "MMyyyy"
	public static Date parseFormMonthYear(String monthYear) {
		return parseDateString(monthYear, new SimpleDateFormat("MMyyyy"));
	}
	
	// parses monthYear and day
	public static Date parseFormMonthYearDay(String day, String monthYear) {
		return parseDateString(day + "/" + monthYear, new SimpleDateFormat("dd/MMyyyy"));
	}
	
	// parses date string of the form "MM/dd/yyyy"
	public static Date parseFormFullDateString(String date) {
		return parseDateString(date, new SimpleDateFormat("MM/dd/yyyy"), false);
	}

	
	
	
	/* 
	 * these methods convert Dates to date strings 
	 */
	
	// generates date string of the form "MMyyyy" for use in form values
	public static String formatFormMonthYear(Date d) {
		return new SimpleDateFormat("MMyyyy").format(d);
	}
	
	// generates date string of the form "MMyyyy" for use in form values
	public static String formatFormMonthYear(Calendar c) {
		return formatFormMonthYear(c.getTime());
	}	
	
	// generates presentation date string of the form "MMM yyyy"
	// TODO: phase2: dialect specific
	public static String formatDisplayMonthYear(Date d) {
		return new SimpleDateFormat("MMM yyyy").format(d);
	}
	
	// generates presentation date string of the form "MMM"
	// TODO: phase2: dialect specific
	public static String formatDisplayShortMonth(Calendar c) {
		return new SimpleDateFormat("MMM").format(c.getTime());
	}
	
	// generates presentation date string of the form "MMM yyyy"
	// TODO: phase2: dialect specific
	public static String formatDisplayMonthYear(Calendar c) {
		return formatDisplayMonthYear(c.getTime());
	}	
	
	// generates presentation date string of the form dd/MM/yyyy
	// TODO: phase2: dialect specific
	public static String formatDisplayDate(Date d) {
		return new SimpleDateFormat("MM/dd/yyyy").format(d);
	}
	
	// generates presentation date string of the form dd/MM/yyyy
	public static String formatDisplayTime(Date d) {
		return new SimpleDateFormat("h:mm a").format(d);
	}
	
	// generates date string to be used in Cart hash code
	public static String formatCartDateHash(Date d) {
		return new SimpleDateFormat("MMMddyyyy").format(d);
	}
	
	// generates the day of month 
	public static int formatDayOfMonth(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c.get(Calendar.DAY_OF_MONTH);
	}
	
	
	// get today's date in form presentation format
	public static String getFormTodayDate() {
		return formatDisplayDate(new Date());
	}
	
	// get tomorrow's date in form presentation format
	public static String getFormTomorrowDate() {
	    Calendar c = Calendar.getInstance();
	    c.add(Calendar.DAY_OF_MONTH, 1);
	    return formatDisplayDate(c.getTime());
	}
	
	
	// helper method to trap parse exception and nullpointers
	public static Date parseDateString(String dateString, SimpleDateFormat dateFormat) {
		return parseDateString(dateString, dateFormat, true);
	}
	
	// helper method to trap parse exception and nullpointers
	public static Date parseDateString(String dateString, SimpleDateFormat dateFormat, boolean lenient) {
		try {
			dateFormat.setLenient(lenient);
			return dateFormat.parse(dateString);
		}
		
		catch(Exception e) {
			return null;
		}
	}
	
	// convert Date to SQL Date
	public static java.sql.Date getSQLDate(Date d) {
		return new java.sql.Date(d.getTime());
	}
	
	public static int getCurrentDayOfMonth() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getNextDayOfMonth() {
	    Calendar c = Calendar.getInstance();
	    c.add(Calendar.DAY_OF_MONTH, 1);
	    return c.get(Calendar.DAY_OF_MONTH);
	}
	
	public static String getMonthYearOfNextDay() {
	    Calendar c = Calendar.getInstance();
	    c.add(Calendar.DAY_OF_MONTH, 1);	    
	    return formatFormMonthYear(c);
	}
	
	public static int compareByDay(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance();
		
		c1.setTime(d1);
		c2.setTime(d2);
		
		return compareByDate(c1, c2);
	}

	public static Date getLastYear() {
		Calendar cal = Calendar.getInstance();
		cal.add (Calendar.YEAR, -1);
		return cal.getTime();
	}
	
	// true if start date within 12 months of end date
	public static boolean within12Months(Date startDate, Date endDate) {
		Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance();
		
		c1.setTime(startDate);
		c1.add(Calendar.MONTH, 13);
		c2.setTime(endDate);		
		
		// clear date fields less than day
		c1 = new GregorianCalendar(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), 1);
		c2 = new GregorianCalendar(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), 1);
		
		return c2.before(c1);
	}

	// compares two dates, based on day
	public static int compareByDate(Calendar c1, Calendar c2) {	
		// clear date fields less than day
		c1 = new GregorianCalendar(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH));
		c2 = new GregorianCalendar(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH));
		
		return c1.getTime().compareTo(c2.getTime());
	}
	
	public static String getMilliseconds() {
		return getMilliseconds(new Date());
	}
	
	public static String getMilliseconds(Date d) {
		return String.valueOf(d.getTime());
	}
	/**
	 * converts etrievedate into mm/dd/yyyy format. 
	 * @param etrieveDate	etrieve date
	 * @return
	 */
	public static String formatEtrieveDate(String etrieveDate) {
		if (! etrieveDate.trim().equals("0") && etrieveDate.length() == 8)  {
			etrieveDate = etrieveDate.substring(4, 6) + "/" + etrieveDate.substring(6, 8) + "/" + etrieveDate.substring(0, 4);
		} else {
			etrieveDate = "";
		}
		return etrieveDate;
	}
	/**
	* Get the week day from date object
	 *
	 * @param date		    Date
	 * @param isInShortForm The week day name is in short form or in full form
	 * @return				String day of the week
	 * @author              Kamal Maji 
	 */
	public static String getWeekDayFromDate (Date date, boolean isInShortForm) {
		if(isInShortForm){
			return new SimpleDateFormat("E").format(date);
		}else{
			return new SimpleDateFormat("EEEE").format(date);
		}
	}
}
