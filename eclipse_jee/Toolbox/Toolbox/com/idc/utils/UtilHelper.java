
package com.idc.utils;

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
import java.net.URLEncoder;
import java.text.CharacterIterator;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Various helper methods
 */

public class UtilHelper { 

	public static String getDateString(Date date) { //date('2005-06-01')
		if (date == null) return null;
	    return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public static StringBuffer makeSpace(int count) {
		StringBuffer buf = new StringBuffer();
		for (int num = 0; num < count; num++) buf.append(" ");
		return buf;
	}
	public static String makeString (boolean bool) {
		if (bool) return "Y";
		return "N";
	}
	public static String cleanString (String str) {
//		LogHelper.info("str :"+str+":");
		if (str == null || str.length() < 1) return "";
		StringBuffer buf = new StringBuffer (str.trim());
		if (buf.charAt(0) == '"') buf.deleteCharAt(0);
		if (buf.charAt(buf.length()-1) == '"') buf.deleteCharAt(buf.length()-1);
//		LogHelper.info("buf :"+buf.toString()+":");
		return buf.toString();
	}

	/**
	 * If there is a single quote escape it with another single quote
	 * @param str
	 * @return String
	 */
	public static String cleanForSql(String str) {
	    if (str != null) {
		    StringBuffer strBuf = new StringBuffer();
		    for (int i=0; i < str.length(); i++) {
		        char ch = str.charAt(i);
		        if (ch == '\'') { // if a single quote, we need to escape for SQL
		            strBuf.append(new Character(ch).toString()); // a single quote needs to be escaped by another single quote
		            strBuf.append(new Character(ch).toString());
		        }
		        else strBuf.append(new Character(ch).toString());
		    }
		    return strBuf.toString();
	    }
	    return null;
	}

	/**
	 * Masks a credit card number (eg. ***********1234)
	 * 
	 * @param cardNumber
	 * @return
	 */
	public static String maskPaymentCard(String cardNumber) {
		if(cardNumber != null && cardNumber.length() > 4)		
			cardNumber = cardNumber.substring(0, cardNumber.length() - 4).replaceAll(".", "*") + cardNumber.substring(cardNumber.length() - 4);

		return cardNumber;
	}
	
	/**
	 * Generates junk text for testing
	 * 
	 * @return		Random String
	 */
	public static String r() {
		int num = (int) (Math.random() * 10 + 2);
		String s = "";
		for (int i = 0; i < num; i++) {
			if ((((int)(Math.random() * 10)) % 2) != 0)
				s += new Character((char)(Math.random() * 100 % 26 + 65)).toString();
			else
				s += new Character((char)(Math.random() * 100 % 26 + 97)).toString();
		}
		return s;
	}
	
	// convert throwable to string for debugging
    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }
    
    // returns HTML attribute string, ie. attribute="value"
    public static String setAttribute(String attribute, String value) {
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

     /**
     * Converts submitted array of form values to a hashmap 
     * 
     * @param optionSelected	Array of values
     * @return					Map of values
     */  
    public static Map<String, Boolean> getSelectedMapFromArray (String [] optionSelected) {
    	Map<String, Boolean> selected = new HashMap<String, Boolean>();
		if (optionSelected != null) {
			for (int i = 0; i < optionSelected.length; i++)
				if (UtilHelper.isSet(optionSelected[i]))
					selected.put(optionSelected[i], new Boolean(true));
		}
    	return selected;
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
    public static int parseInt (String string) {
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
	
	// tests for parameter existence (not for empty string)
	public static boolean isParameter(HttpServletRequest request, String parameter) {
		return request.getParameter(parameter) != null;
	}

	/**
	 * Get referer from the request
	 * 
	 * @param request		HttpServletRequest
	 * @return				referer
	 */
    public static String getReferer (HttpServletRequest request) {
    	return request.getHeader("Referer");
    }

    public static void setNoCache(HttpServletResponse response) {
    	response.setHeader("Pragma", "no-cache");
    	response.setHeader("Cache-Control", "no-store");
    	response.setHeader("Expires", "0"); 
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

    /**
     * Get named parameter from the request
     * 
     * @param request		HttpServletRequest
     * @param key			Parameter value to get
     * @param def			Default to use if paramter not found
     * @return				Parameter value
     */
    public static String getStringRequestValue (HttpServletRequest request, String key, String def) {
    	String value = request.getParameter(key);
    	value = (value == null ? def : value.trim());
    	return value;
    }

    /**
     * Get named parameter from the request, default is ""
     * 
     * @param request		HttpServletRequest
     * @param key			Parameter value to get
     * @return				Parameter value
     */
    public static String getStringRequestValue (HttpServletRequest request, String key) {
    	return getStringRequestValue (request, key, "");
    }

    /**
     * Get named parameter values from the request
     * 
     * @param request		HttpServletRequest
     * @param key			Parameter values to get
     * @param def			Default to use if paramter not found
     * @return				Parameter values
     */
    public static String [] getStringRequestValues (HttpServletRequest request, String key, String [] def) {
    	String [] values = request.getParameterValues(key);
    	values = (values == null || values.length == 0 ? def : values);
    	return values;
    }
    
    /**
     * Get named parameter values from the request, default is ""
     * 
     * @param request		HttpServletRequest
     * @param key			Parameter values to get
     * @return				Parameter values
     */
    public static String [] getStringRequestValues (HttpServletRequest request, String key) {
    	return getStringRequestValues (request, key, new String[]{});
    }

    /**
     * Get named parameter from the request
     * 
     * @param request		HttpServletRequest
     * @param key			Parameter value to get
     * @param def			Default to use if paramter not found
     * @return				Parameter value
     */
    public static int getIntRequestValue (HttpServletRequest request, String key, int def) {
    	String value = request.getParameter(key);
    	int num = (value == null ? def : parseInt(value.trim()));
    	return num;
    }

    /**
     * Get named parameter from the request, default is 0
     * 
     * @param request		HttpServletRequest
     * @param key			Parameter value to get
     * @return				Parameter value
     */
    public static int getIntRequestValue (HttpServletRequest request, String key) {
    	return getIntRequestValue (request, key, 0);
    }

    /**
     * Get named parameter from the request, default is 0L
     * 
     * @param request		HttpServletRequest
     * @param key			Parameter value to get
     * @return				Parameter value
     */
    public static long getLongRequestValue (HttpServletRequest request, String key) {
    	return getLongRequestValue (request, key, 0L);
    }

    /**
     * Get named parameter from the request
     * 
     * @param request		HttpServletRequest
     * @param key			Parameter value to get
     * @param def			Default to use if paramter not found
     * @return				Parameter value
     */
    public static long getLongRequestValue (HttpServletRequest request, String key, long def) {
    	String value = request.getParameter(key);
    	long num = (value == null ? def : parseLong(value.trim()));
    	return num;
    }

    /**
     * Check for existence of a parameter in the request
     * 
     * @param request		HttpServletRequest
     * @param key			Parameter value to get
     * @return				true if parameter exists, else false
     */
    public static boolean isExistsStringRequest (HttpServletRequest request, String key) {
    	return (getStringRequestValue(request, key).length() > 0);
    }
    
    // these methods handle image submit buttons (parameter.x format)
    public static boolean isExistsStringImageRequest(HttpServletRequest request, String key) {
        return isSet(request.getParameter(key)) || isSet(request.getParameter(key + ".x"));
    }
    
    public static boolean isExistsCharImageRequest(HttpServletRequest request, char key) {
        return isSet(request.getParameter(key + "")) || isSet(request.getParameter(key + ".x"));
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
	 * Mangle a String to make it unrecognizable
	 * 
	 * @param srcData		String to mangle
	 * @return				Mangled String
	 */
	public static String mangleString (String srcData){
		String mangledString = "";
		int [] roll = {1, 27, 34, 7, 12, 64, 2, 17, 4, 7, 23, 87, 10, 2, 3, 15, 77, 43, 59, 40};
		int rLen = roll.length;
		int t_int;
		char c;
		Random rand = new Random();
		
		for (int i = 0; i < srcData.length(); i++) {
			c = srcData.charAt(i);
			if (c >= 97 && c <= 122)		//ASCII lowercase character, mutate to another lowercase character.
				mangledString += (char)(((srcData.charAt(i)+rand.nextInt(rLen)) % 26) + 97);
			else if (c >= 65 && c <= 90)	//ASCII uppercase character, mutate to another uppercase character.
				mangledString += (char)(((srcData.charAt(i)+rand.nextInt(rLen)) % 26) + 65);
			else if (c >= 48 && c <= 57) {	//ASCII Digit, mutate to another digit
				t_int = c;
				while (t_int == c){
					t_int = rand.nextInt(10);
				}
				mangledString += t_int;
			}
			else
				mangledString += c;
		}
		return mangledString;		
	}
	
	/**
	 * Get boolean value for a string
	 * 
	 * @param str		String representation of a boolean
	 * @return			true if yes, y or true
	 */
	public static boolean getBoolean (String str) {
	    if (str == null) return false;
        if (str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("y") ||
        		str.equalsIgnoreCase("true")) return true;
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
		return date + 86400000l;//  next day in milli seconds
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
		if(etrieveDate > today) 
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
		if (etrieveDate > today) return "yes";
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
		if(i < maxHour){
			calculatedDate.set(Calendar.HOUR_OF_DAY, defaultHour);
		}
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

	/*
	 * Get time in millis before the number of days passed
	 */
	public static Long getTimeInMillisBefore(int days) {
		Calendar cal = Calendar.getInstance();
		cal.add (Calendar.DATE, -days);
		return new Long(cal.getTime().getTime());
	}
}
