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

package com.hertz.hercutil.presentation;

public class UtilHelper { 

	public static String trimString(String str) {
		return (str != null) ? str.trim() : "";		
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
}
