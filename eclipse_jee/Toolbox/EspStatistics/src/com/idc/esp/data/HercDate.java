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

package com.idc.esp.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Herc Date object
 * 
 * @author John Vincent
 */

public class HercDate implements Serializable, Cloneable {
	private static final long serialVersionUID = -9183198368182600579L;
	private Date date;

	/**
	 * Constructor will set to current date/time
	 *
	 */
	public HercDate() {
		Calendar cal = Calendar.getInstance();
		date = cal.getTime();
	}

	/**
	 * Constructor sets to passed date/time
	 * 
	 * @param d			Date to set to
	 */
	public HercDate (Date d) {
	    date = d;
	}

	/**
	 * Constructor will set to date/time from RentalMan last activity date
	 * 
	 * @param b			Date from RentalMan table via the DAO
	 */
	public HercDate (BigDecimal b) {
		date = null;
		try {
			if (b != null && b.intValue() != 0) {
				date = new SimpleDateFormat("yyyyMMdd").parse(b.toString());
			}
		}
		catch (Exception e) {
		}
	}

	/**
	 * Clone the Object
	 */
	public Object clone() {
		try {
			HercDate cloned = (HercDate) super.clone();
			cloned.date = (Date) date.clone();
			return cloned;
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}

	/**
	 * Check this Object for equality with another
	 * 
	 * @param obj	Object to check
	 * @return		true if objects are equal
	 */
	public boolean equals (Object obj) {
	    if (this == obj) return true;
	    if (! (obj instanceof HercDate)) return false;
	    HercDate d = (HercDate) obj;
	    return date.equals(d.getDate());
	}

	/**
	 * Get the Date
	 * 
	 * @return		Date as a Date object
	 */
	public Date getDate() {return date;}

	/**
	 * Set the date to Date
	 * 
	 * @param date		Date object
	 */
	public void setDate (Date date) {this.date = date;}

	/**
	 * Get the date in MM/dd/yyyy format
	 * 
	 * @return	date in MM/dd/yyyy format
	 */
	public String getDateString() {
		if (date == null) return null;
	    return new SimpleDateFormat("MM/dd/yyyy").format(date);
	}
	/**
	 * Get the date in yyyyMMddHHmmss format, used for LDAP
	 * 
	 * @return	date in yyyyMMddHHmmss format
	 */
	public String getDateStringForLdap() { 
		if (date == null) return "";
	    return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
	}

	/**
	 * Get the date in yyyy-MM-dd HH:mm:ss format, used for DB2
	 * 
	 * @return	date in yyyy-MM-dd HH:mm:ss format
	 */
	public String getDateStringForDB2() {
		if (date == null) return "";
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}				

	/**
	 * Get the date in MM/dd/yyyy HH:mm:ss format, used for C-res page In AdminTool
	 * 
	 * @return	date in MM/dd/yyyy HH:mm:ss format
	 */
	public String getDateStringForCRes() {
		if (date == null) return "";
		return new SimpleDateFormat("MM/dd/yyyy h:mm a").format(date);
	}		
	/**
	 * Get the date in yyyy-MM-dd, used for DB2 Date columns
	 * 
	 * @return	date in yyyy-MM-dd format
	 */
	public String getSimpleDateStringForDB2() {
		if (date == null) return "";
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}		
	
	/**
	 * Get the time in h:mm a format, used for DB2
	 * 
	 * @return	time in h:mm a format
	 */
	public String getFormattedTimeString() {
		try {
			return new SimpleDateFormat("h:mm a").format(date);
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * Get time in milliseconds - used to reverse sort the requests lists
	 * 
	 * @return		time in milliseconds
	 */
	public long getTime() {return date.getTime();}

	/**
	 * Get the date in MM/dd/yyyy format, used for AdminTool History format
	 * 
	 * @return	date in yyyy-MM-dd HH:mm:ss format
	 */
	public String getHistoryDateString() {
		return new SimpleDateFormat("MM/dd/yyyy").format(date);
	}

	/**
	 * Get the date in MMddyy format, used for RentalMan.
	 * Do not change this, or use for any other purpose
	 * 
	 * @return	date in MMddyy format
	 */
	public String getRentalManDateFormat() {
		return new SimpleDateFormat("MMddyy").format(date);
	}

	/**
	 * Get the date in yyyyMMdd format, used for RentalMan.
	 * Do not change this, or use for any other purpose
	 * 
	 * @return	date in yyyyMMdd format
	 */
	public String getRentalManTodayDateFormat() {
		return new SimpleDateFormat("yyyyMMdd").format(date);
	}

	/**
	 * Get the date in user specified format
	 * 
	 * @return	date in user specified format
	 */
	public String getDateString (String format) {
		if (date == null) return null;
		return new SimpleDateFormat(format).format(date);
	}

	/**
	* Get a Date for the Date string, used only by LDAP. Note that if the Date is not set,
	* the current Date will be returned.
	* Example date string is 20070502174431.
	*
	* Do not change this, or use for any other purpose.
	* 
	 * @param dateString		Date as a LDAP String
	 * @return					String as a Date
	 */
	public static Date parseLDAPDate (String dateString) {
		try {
			if (dateString == null) return new Date();
			return new SimpleDateFormat("yyyyMMddHHmmss").parse(dateString);
		}
		catch (ParseException e) {
			return new Date();
		}
	}

	/**
	* Get a Date for the Date string. Note that if the Date is not set,
	* the current Date will be returned.
	* Note that the date string must be in the format MMMyyyydd, else current Date will be returned.
	*
	* Do not change this.
	* 
	 * @param dateString		Date as a String
	 * @return					String as a Date
	 */
	public static Date parseDate (String dateString) {
		try {
			if (dateString == null) return new Date();
			return new SimpleDateFormat("MMMyyyydd").parse(dateString);
		}
		catch (ParseException e) {
			return new Date();
		}
	}

	/**
	* Get a Date for the Date string. Used to handle dates from RentalMan.
	* Note that if the Date is not set, the current Date will be returned.
	* Note that the date string must be in the format MM/dd/yyyy, else current Date will be returned.
	*
	* Do not change this.
	* 
	 * @param dateString		Date as a String
	 * @return					String as a Date
	 */
	public static Date parseRentalManDate (String dateString) {
		try {
			if (dateString == null) return new Date();
			return new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
		}
		catch (ParseException e) {
			return new Date();
		}
	}

	/**
	* Get a Date for the Date string that is formatted as format.
	* Note that if the Date is not set, the current Date will be returned.
	* Note that the date string must be in the format format, else current Date will be returned.
	*
	* Do not change this.
	* 
	 * @param dateString		Date as a String
	 * @param format			The format of DateString
	 * @return					String as a Date
	 */
	public static Date parseDate (String dateString, String format) {
		try {
			if (dateString == null) return new Date();
			return new SimpleDateFormat(format).parse(dateString);
		}
		catch (ParseException e) {
			return new Date();
		}
	}

	/**
	 * Determine whether the date is null
	 * 
	 * @return		true if the date is null
	 */
	public boolean isNull() {
		return (date == null) ? true : false;
	}

	/**
	 * Get the date in yyyy-MM-dd HH:mm:ss format
	 * 
	 * @return	date in yyyy-MM-dd HH:mm:ss format
	 */
	public String toString() {
		if (date == null) return "";
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
	
	/**
	 * Used in Cres application in AT. Pass String format to get HercDate
	 * @param date				String date
	 * @return
	 */
	public static HercDate parseHercDateWithTime(String dateString) {
		try {
			if (dateString == null) return null;
			return new HercDate(new SimpleDateFormat("MM/dd/yyyy h:mm a").parse(dateString));
		}
		catch (ParseException e) {
			return null;
		}	
	}
	
	/**
	 * Used in Cres application in AT. Pass String format to get HercDate
	 * @param date				String date
	 * @return
	 */
	public static HercDate parseHercDate(String dateString) {
		try {
			if (dateString == null) return null;
			return new HercDate(new SimpleDateFormat("MM/dd/yyyy").parse(dateString));
		}
		catch (ParseException e) {
			return null;
		}	
	}

}
