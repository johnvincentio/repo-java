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

package com.idc.esp.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.idc.esp.data.HercDate;

/**
 * Various helper methods
 * 
 * @author Michael Tardif
 * @author John Vincent
 * @author Manu Xavier
 */

public class UtilHelper { 
	
	/**
	 * Masks a credit card number (eg. ***********1234)
	 * 
	 * @param cardNumber
	 * @return
	 */
	public static String maskPaymentCard (String cardNumber) {
		if (cardNumber != null && cardNumber.length() > 4)		
			cardNumber = cardNumber.substring(0, cardNumber.length() - 4).replaceAll(".", "*") + cardNumber.substring(cardNumber.length() - 4);
		return cardNumber;
	}
	
	// convert throwable to string for debugging
    public static String getStackTrace (Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }
    
    // returns HTML attribute string, ie. attribute="value"
    public static String setAttribute (String attribute, String value) {
    	return (isSet(attribute) && isSet(value)) ? " " + attribute + "=\"" + value + "\"" : "";
    }

    /**
     * translates a string into application/x-www-form-urlencoded format
     * 
     * @param str		String to encode
     * @return			encoded String
     */
    public static String encodeURI (String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return str;
        }
    }
    public static String decodeURI (String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    /**
     * Return 1 if true, false otherwise
     * @param bool	true or false
     * @return		1 or 0
     */
    public static int parseBoolean(boolean bool){
    	if (bool) return 1;
    	return 0;
    }

    /**
     * Pad a number with zeros (ie. 5 -> 005)
     * 
     * @param number	number to pad
     * @param width		total width of number including zeros (if any)
     * @return 			padded string
     */
	public static String leadingZeroPad(int number, int width) {
		String str = String.valueOf(number);
		int length = str.length();
		for (int i = 0; i < width - length; i++)
			str = "0" + str;
		return str;
	}

	/**
	 * Verify string has a valid digit
	 * 
	 * @param str		String to verify
	 * @return			true if string has a valid digit
	 */
	public static boolean hasDigit (String str) {
		if (str != null) {
			for (int i = 0; i < str.length(); i++)
				if (Character.isDigit(str.charAt(i))) return true;
		}
		return false;
	}

	/**
     * Get an int from an Object
     * 
     * @param obj	Object
     * @return		int
     */
    public static int parseInt (Object obj) {
        try {
            return Integer.parseInt((String) obj);
        }
        catch (Exception e) {
            return 0;
        }
    }

    /**
     * convert string to int for easy form validation
     * 
     * @param string	String
     * @return			int
     */
    public static int parseInt(String string) {
    	try {
    		return Integer.parseInt(string);
    	}
    	catch (Exception e) {
    		return 0;
    	}
    }

    /**
     * Convert string to int for easy form validation, provide default validation if not an int
     * 
     * @param string		String to convert
     * @param defaultValue	Default value
     * @return				int value of the String
     */
    public static int parseInt (String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        }
        catch (Exception e) {
            return defaultValue;
        }    	
    }

    /**
     * Convert a String to a double
     * 
     * @param string	String
     * @return			double value of the String
     */
    public static double parseDouble (String string) {
    	return parseDouble(string, 0);
    }

    /**
     * Convert string to double for easy form validation, provide default validation if not a double
     * 
     * @param string		String to convert
     * @param defaultValue	Default value
     * @return				double value of the String
     */
    public static double parseDouble(String string, double defaultValue) {
    	try {
    		return Double.parseDouble(string);
    	}
    	catch(Exception e) {
    		return defaultValue;
    	}
    }

    /**
     * Convert string to int
     * 
     * @param string		String to convert
     * @param defaultValue	Default value
     * @return				int value of the String
     */
    public static int parseDouble(String string, int defaultValue) {
    	try {
    		return Double.valueOf(string).intValue();
    	}
    	catch(Exception e) {
    		return defaultValue;
    	}
    }

    /**
     * Convert string to long for easy form validation
     * 
     * @param string		String to convert
     * @return				long value of the String
     */
    public static long parseLong (String string) {
    	return parseLong (string, 0L);
    }

    /**
     * Convert string to long for easy form validation
     * 
     * @param string		String to convert
     * @param defaultValue	Default value
     * @return				long value of the String
     */
    public static long parseLong (String string, long defaultValue) {
        try {
            return Long.parseLong(string);
        }
        catch (Exception e) {
        	return defaultValue;
        }
    }

    /**
     * Convert string to boolean
     * 
     * @param string		String to convert
     * @param defaultValue	Default value
     * @return				boolean value of the String
     */
	public static boolean parseBoolean (String string, boolean defaultValue) {
	    if (string == null) return defaultValue;
        if (string.trim().equalsIgnoreCase("yes") || string.trim().equalsIgnoreCase("true")) return true;
	    return false;
	}

	/**
	 * Converts "Y" to "Yes" and "N" to "No".
	 * 
	 * @param ynString
	 * @return
	 */
	public static String getYesNoString (String ynString){
		if (ynString == null) return "";
		String str = "";
		if (! ynString .equals("") && ynString.equalsIgnoreCase("y"))
			str = "Yes";
		if (! ynString .equals("") && ynString.equalsIgnoreCase("n"))
			str = "No";
		return str;		
	}

	/**
	 * Converts "Yes" to "Y" and "No" to "N".
	 * 
	 * @param yesNoString 
	 * @return
	 */
	public static String getYNString(String yesNoString){
		String str = "";
		if (! yesNoString .equals("") && yesNoString.equalsIgnoreCase("yes"))
			str = "Y";
		if (! yesNoString .equals("") && yesNoString.equalsIgnoreCase("no"))
			str = "N";
		return str;		
	}

    // tests for empty String from a Collection
    public static boolean isSet(Object obj) {
        return (obj != null && obj instanceof String && !"".equals(obj));
    }

    // tests for a null/empty string
	public static boolean isSet(String str) {
		return (str == null || "".equals(str)) ? false : true;
	}

	// tests for a null/empty string in array
	public static boolean isSet(String [] array, int index) {
	    try {
	        return isSet(array[index]);
	    }
	    catch(ArrayIndexOutOfBoundsException e) {
	        return false;
	    }
	}

	/**
	 * Trims a string
	 * 
	 * @param str		String to trim
	 * @return			Trimmed string
	 */
	public static String trimString(String str) {
		return (str != null) ? str.trim() : "";		
	}

	/**
	 * Returns true for String equals on, false if not.
	 * For use with a radio button.
	 * 
	 * @param str		String representation of a button state
	 * @return			true if on, else false
	 */
	public static boolean isOn (String str) {
		if (str == null || "".equals(str)) return false;
		if (str.trim().toLowerCase().equals("on")) return true;
		if (str.trim().equals("1")) return true;
		return false;
	}

	/**
	 * Return 1 or 2 for checkbox
	 * @param str			passed "on"
	 * @return
	 */
	public static int isOnInt(String str) {
		boolean isOn = isOn(str);
		if (isOn) return 2;
		return 1;
	}
	
	/**
	 * Returns Y/N for checkbox 
	 * @param str		passed "on"
	 * @return
	 */
	public static String isOnString (String str) {
		boolean isOn = isOn(str);
		if (isOn) return "Y";
		return "N";
	}
	
    // html attribute helpers, ie: <%= setDisabled(quantity != 0) %>
    public static String setDisabled(boolean test) {
        return test ? " disabled=\"disabled\"" : "";
    }
    
    public static String setDisabled(String test) {
    	return setDisabled(isSet(test));
    }
    
    public static String setDisabled(String one, String two) {
    	return (isSet(one) && isSet(two)) ? setDisabled(one.equals(two)) : "";
    }
    
    public static String setSelected(boolean test) {
        return test ? " selected=\"selected\"" : "";
    }
    
    public static String setSelected(String test) {
    	return setSelected(isSet(test));
    }
    
    public static String setSelected(String one, String two) {
    	return (isSet(one) && isSet(two)) ? setSelected(one.equals(two)) : "";
    }    
    
    public static String setChecked(boolean test) {
        return test ? " checked=\"checked\"" : "";
    }
    
    public static String setChecked(String test) {
    	return setChecked(isSet(test));
    }
    
    public static String setChecked(String one, String two) {
    	return (isSet(one) && isSet(two)) ? setChecked(one.equals(two)) : "";
    }     
    
    public static String setReadonly(boolean test) {
        return test ? " readonly=\"readonly\"" : "";
    }
    
    public static String setReadonly(String test) {
    	return setReadonly(isSet(test));
    }
    
    public static String setReadonly(String one, String two) {
    	return (isSet(one) && isSet(two)) ? setReadonly(one.equals(two)) : "";
    }     
    
    public static String setName(String name) {
    	return isSet(name) ? " name=\"" + name + "\"" : "";
    }

    public static String getCheckBoxBoolean(boolean bool) {
    	return bool ? "on" : "";
    }
	public static boolean setCheckBoxBoolean(String str) {
		return "on".equals(str);
	}
	public static String getRadioButtonBoolean(boolean bool) {
		return bool ? "1" : "0";
	}
	public static boolean setRadioButtonBoolean (String str) {
		if (str != null && str.trim().equalsIgnoreCase("1")) return true;
		return false;
	}
	public static boolean setBoolean (String str, String compare) {
		if (str != null && str.trim().equalsIgnoreCase(compare)) return true;
		return false;
	}

	/**
	 * LDAP cannot have special characters like ',','\',=,<,>,#,;,\," in the 
	 * Distingushed Name. This method is used to escape these special characters.
	 * 
     * @param username		User name
     * @return				User name escaped.
     */
	public static String changeSpecialCharacters(String username) {
      final StringBuffer result = new StringBuffer();

      final StringCharacterIterator iterator = new StringCharacterIterator(username);
      char character = iterator.current();
      while (character != CharacterIterator.DONE){
          if (character == ',')
              result.append("_");
          else if (character == '=')
              result.append("_");
          else if (character == '+')
              result.append("_");
          else if (character == '<')
              result.append("_");
          else if (character == '>')
              result.append("_");
          else if (character == '#')
              result.append("_");
          else if (character == ';')
              result.append("_");
          else if (character == '\\')
              result.append("_");
          else if (character == '\"')
              result.append("_");
          else {
              //the char is not a special one
              //add it to the result as is
              result.append(character);
            }
            character = iterator.next();
      }
      return result.toString();
    }

	/**
	 * Get boolean value for a string
	 * 
	 * @param str		String representation of a boolean
	 * @return			true if yes, y or true
	 */
	public static boolean getBoolean (String str) {
	    if (str == null) return false;
        if (str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("y") || str.equalsIgnoreCase("true")) return true;
	    return false;
	}	    

	/**
	 * Convert String to boolean
	 * 
	 * @param str		Y or y will be true
	 * @return			true if Y or y, else false
	 */
	public static boolean setDAOBoolean (String str) {
		if (str == null) return false;
		if (str.trim().equalsIgnoreCase("Y")) return true;
		return false;
	}

	/**
	 * Convert boolean to database string (varchar(1)).
	 * 
	 * @param bool		true or false
	 * @return			Y for true, N for false
	 */
	public static String setDAOString (boolean bool) {
		return bool ? "Y" : "N";
	}

	/**
	 * Get current datetime
	 * 
	 * @return		Current datetime as a database date object
	 */
	public static java.sql.Date getDAOCurrentDate() {
		return new java.sql.Date(new java.util.Date().getTime());	
	}

	/**
	 * Get current datetime as a Date object from a HercDate object
	 * 
	 * @param hercDate		HercDate object
	 * @return				Database date object
	 */
	public static java.sql.Date getDAOHercDate(HercDate hercDate) {
		if (hercDate == null || hercDate.isNull()) return null;
		return new java.sql.Date(hercDate.getDate().getTime());
	}

	/**
	 * Calculates Nextday date in milli seconds.
	 * 
	 * @param date			from date
	 * @return				next date
	 */
	public static long getNextDayDateInMillis(long date) {
		return date + 86400000l;	//  next day in milli seconds
	}

	/**
	 * Calculates the Date as per the business logic
	 * If etrieve date is > today's date return etrievedate+1day else today's date.
	 * @param etrieveDateString		etrieve date in string format
	 * @return	String
	 */
	public static String getNextEtrieveDate(String etrieveDateString) {
		String[] dateString = etrieveDateString.split("/");
		int year = new Integer(dateString[2].length() == 4 ? dateString[2] : ("20" + dateString[2])).intValue();
		int month = new Integer(dateString[0]).intValue();
		int date = new Integer(dateString[1]).intValue();
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month-1, date);
		long etrieveDate = calendar.getTimeInMillis();
		long today = Calendar.getInstance().getTimeInMillis();
		if (etrieveDate > today) 
			return new HercDate(new Date(getNextDayDateInMillis(etrieveDate))).getDateString("MM/dd/yyyy");
		return new HercDate(new Date(getNextDayDateInMillis(today))).getDateString("MM/dd/yyyy");
	}

	/**
	 * If EtrieveDate is greater than today's date return true else fale.
	 * @param etrieveDateString		date string
	 * @return	boolean
	 */
	public static String isShow(String etrieveDateString) {
		String[] dateString = etrieveDateString.split("/");
		int year = new Integer(dateString[2].trim()).intValue();
		int month = new Integer(dateString[0].trim()).intValue();
		int date = new Integer(dateString[1].trim()).intValue();
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month-1, date);
		long etrieveDate = calendar.getTimeInMillis();
		long today = Calendar.getInstance().getTimeInMillis();
		if (etrieveDate > today) 
			return "yes";
		return "no";
	}

	/**
	 * Calculates NextWeek date in milli seconds.
	 * 
	 * @param date			from date
	 * @return				next date
	 */
	public static long getNextWeekDateInMillis(long date) {
		return getNextWeekDateInMillis(date, 1);
	}

	/**
	 * Calculates NextWeek date in milli seconds based on subtype.
	 * 
	 * @param date			from date
	 * @return				next date
	 */
	public static long getNextWeekDateInMillis(long date, long subtype) {
		return date + (subtype * 604800000l);
	}

	/**
	 * Calculates NextMonth date in milli seconds.
	 * 
	 * @param date			from date
	 * @return				next date
	 */
	public static long getNextMonthDateInMillis(long date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.add(Calendar.MONTH, 1);
		int ldom = cal.getActualMaximum(Calendar.DATE);
		int next_day = day;
		if (day > ldom)
			next_day = ldom;
		cal.set(Calendar.DATE, next_day);
		return cal.getTimeInMillis();
	}

	/**
	 * Calculate the next hour time dynamically. For example if the Hour is less than 9pm, 
	 * then set default to 8pm else keep it as it is.
	 * 
	 * @param date			date in milli seconds
	 * @param maxHour		max hour 
	 * @param defaultHour	default hour
	 * @return 				time in milliseconds
	 */
	public static long getNewHourRunTime (long date, int maxHour, int defaultHour) {
		Calendar calculatedDate = Calendar.getInstance();
		calculatedDate.setTimeInMillis(date);
		int i = calculatedDate.get(Calendar.HOUR_OF_DAY);
		if (i < maxHour)
			calculatedDate.set(Calendar.HOUR_OF_DAY, defaultHour);
		return calculatedDate.getTimeInMillis();
	}

	/**
	 * Get current Time in millis
	 * 
	 * @return				current Time in millis
	 */
	public static long getCurrentTimeInMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * Get current Year in millis
	 * 
	 * @return			current Year
	 */
	public static int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * Get the past or future date for a given date. 
	 * 
	 * @param date				Date
	 * @param days				number of days to be added or removed.
	 * @return
	 */
	 public static Date getPastOrFutureDate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
	 }

	 /**
	  * Returns java.util.Date,  String format 'MM/dd/yyyy'.
	  * 
	  * @param dateString
	  * @return
	  */
	 public static java.util.Date getDateFromDateString(String dateString){
		 java.util.Date date = null;
		 DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		 try {
			 if (! dateString.equals(""))
				 date = df.parse(dateString);
		 }
		 catch (ParseException e1) {
			 e1.printStackTrace();
		 }
		 return date;
	 }

	 /**
	  * Returns Start Date
	  * @param dateString		Date String
	  * @return
	  */
	 public static Date getStartDateForCres(String dateString){
		Date date = UtilHelper.getDateFromDateString(dateString);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 01);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	 }

	 /**
	  * Get End date for Cres
	  * @param dateString			End date 
	  * @return						
	  */
	 public static Date getEndDateForCres(String dateString){
		Date date = UtilHelper.getDateFromDateString(dateString);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	 }

	/**
	 * Get a String value from a hashmap for a given attribute
	 * 
	 * @param hashMap		Hash map
	 * @param attribute		Attribute
	 * @return				String value of the attribute
	 */
	public static String getString (Map<String, Object> hashMap, String attribute) {
		String value = (String) hashMap.get(attribute);
	    if (value == null) return "";
	    return value;
	}

	/**
	 * Get an int value from a hashmap for a given attribute
	 * 
	 * @param hashMap		Hash map
	 * @param attribute		Attribute
	 * @return				int value of the attribute
	 */
	public static int getInteger (Map<String, Object> hashMap, String attribute) {
	    Integer value = (Integer) hashMap.get(attribute);
	    if (value == null) return 0;
	    return value.intValue();
	}

	/**
	 * Get a long value from a hashmap for a given attribute
	 * 
	 * @param hashMap		Hash map
	 * @param attribute		Attribute
	 * @return				long value of the attribute
	 */
	public static long getLong (Map<String, Object> hashMap, String attribute) {
		Long longObject = (Long) hashMap.get(attribute);
		if (longObject != null) return longObject.longValue();
		return 0L;
	}
	
	/**
	 * Get the long from String if NumberFormatException then returns 0.
	 * 
	 * @param 			str
	 * @return 			return long value from string
	 */
	public static long getLong(String str){
		long l = 0;
		try {
			l = Long.parseLong(str);	
		}
		catch (NumberFormatException nfe){
			return 0;
		}
		return l;
	}
	
	/**
	 * Get an float value from a hashmap for a given attribute
	 * 
	 * @param hashMap		Hash map
	 * @param attribute		Attribute
	 * @return				float value of the attribute
	 */
	public static float getFloat (Map<String, Object> hashMap, String attribute) {
	    Float value = (Float) hashMap.get(attribute);
	    if (value == null) return 0;
	    return value.floatValue();
	}
	
	/**
	 * Get an BigDecimal value from a hashmap for a given attribute
	 * 
	 * @param hashMap		Hash map
	 * @param attribute		Attribute
	 * @return				BigDecimal  of the attribute
	 */
	public static BigDecimal getBigDecimal (Map<String, Object> hashMap, String attribute) {
		BigDecimal value = (BigDecimal) hashMap.get(attribute);
	    if (value == null) return new BigDecimal(0.0);	
		return value;
	}

	/**
	 * Parse string 0 to N and string 1 to Y
	 * 
	 * @param str	String to parse
	 * @return		Y or N
	 */
	public static String parseDAOBoolean (String str) {
		if (str == null) return "N";
		if (str.equals("1")) return "Y";
		return "N";
	}

	/**
	 * Parse string, converting a null to an empty string
	 * 
	 * @param str	String to parse
	 * @return		Passed string or, if null, an empty string 
	 */
	public static String parseDAOString (String str) {
		if (str == null) return "";
		return str;
	}
	
	/**
	 * Converts a string to currency for display in the web
	 * eg. "2353.3352" -> "$2,353"
	 * 
	 * @param s		String to convert
	 * @return		String as a currency
	 */
	public static String getPriceInCurrency(String s) {	
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);  
		n.setMaximumFractionDigits(0);
		n.setGroupingUsed(true);
		return n.format(parseDouble(s));
	}

	/**
	 * Limit the decimal digits in the price value to 2 digits.
	 * @param price, example $1234.567898
	 * @return price = $1234.56
	 * @author Shubha Thirthahalli
	 */
	public static String getFormattedPrice(String price){
	    if (price.indexOf(".") != -1 && price.split("\\.")[1].length() > 2)
            price = price.substring (0,price.split("\\.")[0].length() + 3);	    
	    return price;
	}

	/**
	 * Determine whether two strings are identical
	 * 
	 * @param s1		first string
	 * @param s2		second string
	 * @return			true if two strings are identical
	 */
	public static boolean isEquals (String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 == null) return false;
		if (s1.equals(s2)) return true;
		return false;
	}

	/**
	 * Determine whether two strings arrays are identical
	 * 
	 * @param s1		first string[]
	 * @param s2		second string[]
	 * @return			true if two strings arrays are identical
	 */
	public static boolean isEquals (String[] s1, String[] s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 == null) return false;
		if (s1.length != s2.length) return false;
		for (int i=0; i< s1.length; i++)	{
			if (! s1[i].equals(s2[i])) return false;
		}
		return true;
	}
	/**
	 * Determine whether two strings are identical
	 * 
	 * @param s1		first string
	 * @param s2		second string
	 * @return			true if two strings are identical
	 */
	public static boolean isEqualsIgnoreCase (String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 == null) return false;
		if (s1.equalsIgnoreCase(s2)) return true;
		return false;
	}

	/**
	 * Determine whether two HercDate objects are identical
	 * 
	 * @param s1		first HercDate
	 * @param s2		second HercDate
	 * @return			true if two HercDate objects are identical
	 */
	public static boolean isEquals (HercDate d1, HercDate d2) {
		if ((d1 == null || d1.isNull()) && (d2 == null || d2.isNull())) return true;
		if (d1 == null || d1.isNull()) return false;
		if (d1.equals(d2)) return true;
		return false;
	}

	/**
	 * Returns Y for true, N for false
	 * @param b		true or false
	 * @return		Y or N
	 */
	public static String parseDAOBoolean(boolean b) {
		if (b) return "Y";
		return "N";
	}

	/**
	 * Parse the string, prepending with "0" to obtain a string of length size.
	 * 
	 * @param input			string to manipulate
	 * @param size			required string length
	 * @return				String representation of the name
	 */
	public static String parseString (String input, int size) {
		if (input == null) return "";
		while (input.length() < size) {
			input = "0" + input;
		}
		return input;
	}

	/**
	 * Get contents of a file
	 * 
	 * @param fileName	Name of the file to read
	 * @return			Contents of the file
	 * @throws IOException
	 */
	public static StringBuffer readFile (String fileName)	{
		File file = new File(fileName);
		StringBuffer sb = new StringBuffer();
		
		try	{
			if (! file.exists())
				return null;
			
			BufferedReader br = new BufferedReader (new FileReader (file));
			try	{
				String lineSep = "\n";
				
				String nextLine;			
				while ((nextLine = br.readLine()) != null) {
					sb.append(nextLine);
					sb.append(lineSep);
				}
			} finally {
				br.close();
			}
		} catch(IOException ioe)	{
			return null;
		}
		
		return sb;
	}

	/**
	 * Write StringBuffer to the named file
	 * 
	 * @param file		File to write to
	 * @param buf		StringBuffer to write to the file
	 * @return			true if successful
	 */
	public static boolean writeFile (StringBuffer buf, File file) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter (new BufferedWriter (new FileWriter (file)));
			pw.print(buf);
			pw.flush();
			pw.close();
			return true;
		}
		catch (IOException ex) {
			return false;
		}
		finally {
			if (pw != null) pw.close();
		} 
	}

	/**
	 * Write bytes to the named file
	 * 
	 * @param file		File to write to
	 * @param bytes		Byte data to write to the file
	 * @return			true if successful
	 */
	public static boolean writeFile (File file, byte[] bytes) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream (file);
			out.write (bytes);
			out.flush();
			out.flush();
			out.flush();
			return true;
		}
		catch (IOException ex) {
			return false;
		}
		finally {
			try {
				if (out != null) out.close();
			}
			catch (IOException ex) {}
		}  
	}

	/**
	 * Write bytes to the named file
	 * 
	 * @param file		File to write to
	 * @param bytes		Byte data to write to the file
	 * @return			true if successful
	 */
	public static boolean writeFile (String filename, byte[] bytes) {
		return writeFile (new File (filename), bytes);
	}

	/**
	 * get the next prime number from number pPrime
	 * 
	 * @param pPrime		number
	 * @return				next prime number from number pPrime
	 */
	public int getNextPrimeNumber (int pPrime) {
		double primeRoot;
		int p;
		primeRoot = Math.floor(Math.sqrt(pPrime));
		for (p = 2; p <= primeRoot; ++p) {
			if ((pPrime) % p == 0) {
				pPrime++;	// not prime;
				primeRoot = Math.floor(Math.sqrt(pPrime));
				p = 1;
			}
		}
		return pPrime;
	}

	/**
	 * Get the phone number in it's constituent parts
	 * 
	 * @param phone		phone number
	 * @return			3 parts
	 */
	public static String[] getPhoneParts (String phone) {
		if (phone == null) return null;
		StringBuffer buf = new StringBuffer ();
		for (int pos = 0; pos < phone.length(); pos++) {
			char chr = phone.charAt(pos);
			if (Character.isDigit(chr)) buf.append (chr);
		}
		int clen = buf.length();
		for (int i = 0; i < 10 - clen; i++)
			buf.insert(0, '0');

		String[] parts = new String[3];
		parts[0] = buf.substring(0, 3);
		parts[1] = buf.substring(3, 6);
		parts[2] = buf.substring(6, 10);
		return parts;
	}

	/**
	 * Convenience method to get the existing String pre or postpend the character by specifying the maximum limit.
	 * Refer: PrePostPendTest.java under HercDataWeb project
	 * 
	 * @param str			Actual value to which prepend or postpend takes place.
	 * @param whatToAdd		Character to add to the existing String
	 * @param prePostPend	if prepend true, else false
	 * @param totalLength	total length determines after pre or post pend to the existing String.
	 * @return				String with the new value
	 */
	public static String preOrPostPendCharToAnExistingString (String str, char whatToAdd, boolean prePostPend, int totalLength) {
		String newValue = str == null ? Character.toString(whatToAdd) : str;
		if (newValue.length() < totalLength) {
			for (int i = newValue.length(); i < totalLength; i++) {
				newValue = prePostPend ? whatToAdd + newValue : newValue + whatToAdd;
			}
		}
		return newValue;
	}
	
	/**
	 * Check if the three parts of phone are not blank 
	 * @param area		area	
	 * @param part1		part1
	 * @param part2		part2
	 * @return
	 */
	public static boolean isValidPhone(String area, String part1, String part2) {
		if (("".equals(area)) & ("".equals(part1)) & ("".equals(part2)))
			return false;
		return true;
	}
	
	public static long getNextEtrieveStringDate(String etrieveDateString) {
		String[] dateString = etrieveDateString.split("/");
		String year1 = (dateString[2].length() == 4 ? dateString[2] : ("20" + dateString[2]));
		String month1 = (dateString[0]);
		String date1 = (dateString[1]);
		int year = new Integer(year1).intValue();
		int month = new Integer(month1).intValue();
		int date = new Integer(date1).intValue();
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month-1, date);		
		long today = Calendar.getInstance().getTimeInMillis();
		long future = calendar.getTimeInMillis();
		return today > future ? getNextDayDateInMillis(today) : getNextDayDateInMillis(future);
	}

	public static long getEtrieveNextYearDate(String etrieveDateString) {
		String[] dateString = etrieveDateString.split("/");
		String year1 = (dateString[2].length() == 4 ? dateString[2] : ("20" + dateString[2]));
		String month1 = (dateString[0]);
		String date1 = (dateString[1]);
		int year = new Integer(year1).intValue();
		int month = new Integer(month1).intValue();
		int date = new Integer(date1).intValue();
		Calendar calendar = Calendar.getInstance();
		calendar.set(year+1, month-1, date-1);
		long futureYear = calendar.getTimeInMillis();
		return futureYear;
	} 

	public static boolean getIsValidEtrieveNextYearDate(String estimatedDate) {
		String[] dateStringEst = estimatedDate.split("/");
		int yearEst = new Integer(dateStringEst[2].length() == 4 ? dateStringEst[2] : ("20" + dateStringEst[2])).intValue();
		int monthEst = new Integer(dateStringEst[0]).intValue();
		int dateEst = new Integer(dateStringEst[1]).intValue();
		Calendar calendarEst = Calendar.getInstance();
		calendarEst.set(yearEst, monthEst-1, dateEst);
		
		Calendar calendar = Calendar.getInstance();	
		calendar.roll(Calendar.YEAR, true);
					
		long futureYear = calendar.getTimeInMillis();
		long futureYearEst = calendarEst.getTimeInMillis();
		
		return futureYearEst <= futureYear ? true : false;
	}

	/**
	 * Returns a formatted string of the float number to append passed number of decimals.
	 * Ex: fValue = 12345.111222333 and numberOfDecimals = 3 result: 12345.111
	 * 
	 * @param fValue						float number to be formatted
	 * @param numberOfDecimals              number of decimals expected
	 * @return 								formatted String
	 */
	public static String formatFloatWithDecimal(float fValue, int numberOfDecimals) {		
		return String.format("%."+numberOfDecimals+"f", fValue);		
	}
	
	/*
	 * Get time in millis before the number of days passed
	 */
	public static Long getTimeInMillisBefore(int days) {
		Calendar cal = Calendar.getInstance();
		cal.add (Calendar.DATE, -days);
		return new Long(cal.getTime().getTime());
	}

	/**
	 * Returns a modified String with old String replaced by new string.
	 *  
	 * @param str					 String to be modified 
	 * @param oldStr        		 Old string to be replaced
	 * @param newStr      		     New String to be replaced with
	 * @return
	 */
	public static String replaceWithString (String str, String oldStr, String newStr) {
		String cleanedString = "";
		if (str != null && !str.equals(""))		
			cleanedString = str.replace(oldStr, newStr);
		return cleanedString;		
	}

	/**
	 * Get part of a String from a String.
	 * 
	 * @param from			Source String
	 * @param separator		Separator
	 * @param part			The part of the separated String you want where 0 is the first part.
	 * @return				Part of the source String that was requested.
	 */
	public static String getStringPart (String from, String separator, int part) {
		if (part < 0) return "";
		if (! UtilHelper.isSet(from)) return "";
		if (! UtilHelper.isSet(separator)) return "";
		
		String[] arr = from.split (separator);
		if (arr.length > part) return arr[part];
		return "";
	}

	/**
	 * Convert content system LinkedHashMap with keys of abc.def to a HashMap with keys of def.
	 * 
	 * @param linkedHashMap		Convert this HashMap.
	 * @return					Converted HashMap.
	 */
	public static HashMap<String, Object> makeMap (LinkedHashMap<String, Object> linkedHashMap) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		Iterator<Map.Entry<String, Object>> iter = linkedHashMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Object> me = iter.next();
			hashMap.put (getStringPart (me.getKey(), "\\.", 1), me.getValue());
		}
		return hashMap;
	}

	public static String getTransformedUrl (String url) {return url.toLowerCase().replace('/', '.').replace(' ', '-');}
	public static String getUnTransformedUrl (String url) {return url.replace('.', '/').replace('-', ' ');}
	
	/**
	 * Get subString from String
	 * @param value			String
	 * @param upto			length
	 * @return
	 */
	public static String getSubString (String value, int upto) {
		if (value == null) return "";
		if (value.length() < upto) return value;
		return value.substring(0, upto);
	}

	/**
	 * Converts collection of primitive integers to int Array.
	 * 
	 * @param integers				List<Integer>
	 * @return						int[]
	 */
	public static int[] convertIntegers (List<Integer> integers){
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}

	/**
	 * Determine whether a string has any non-blank characters
	 * 
	 * @param str		string to check
	 * @return			true if null or blanks, otherwise return false
	 */
	public static boolean isEmpty (String str) {
		if (str == null) return true;
		if (str.trim().length() < 1) return true;
		return false;
	}

	/**
	 * Replace instance of a String at the start of a String with another String replace.
	 *  
	 * @param input			string to change
	 * @param pattern		pattern to change at the start of input.
	 * @param replace		replacement string
	 * @return				transformed string
	 */
	public static String replaceStartsWith (String input, String pattern, String replace) {
		if (UtilHelper.isEmpty (input)) return input;
		if (UtilHelper.isEmpty (pattern)) return input;
		if (replace == null) return input;
		if (! input.startsWith (pattern)) return input;
		return replace + input.substring (pattern.length());
	}

	/**
	 * Replace instance of a String at the end of a String with another String replace.
	 *  
	 * @param input			string to change
	 * @param pattern		pattern to change at the start of input.
	 * @param replace		replacement string
	 * @return				transformed string
	 */
	public static String replaceEndsWith (String input, String pattern, String replace) {
		if (UtilHelper.isEmpty (input)) return input;
		if (UtilHelper.isEmpty (pattern)) return input;
		if (replace == null) return input;
		if (! input.endsWith (pattern)) return input;
		return input.substring (0, input.length() - pattern.length()) + replace;
	}

	/**
	 * Set url string to the protocol of the parameter https
	 * @param url			url to be manipulated
	 * @param https			true => use https, false => use http
	 * @return				url with the appropriate protocol.
	 */
	public static String handleURL (String url, boolean https) {
		if (https) {
			if (url.startsWith("https://")) return url;
			return UtilHelper.replaceStartsWith (url, "http://", "https://");
		}
		if (url.startsWith("http://")) return url;
		return UtilHelper.replaceStartsWith (url, "https://", "http://");
	}
}
