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

import com.hertz.hercutil.company.RMAccountType;

/**
 * @author Sandeep M. Mahale
 *
 * This is a Utility class to handle inconsistencies between database & presentation.
 * For example, country exists as USA in database, but should appear as United States in presentation.
 * Also, country doesn't have a 2 char code(like US or CA) in database, but this class could generate one.
 * Add methods to this class as necessary
 * 
 */
public class CountryUtil {
	
	/*
	 * get String CountryAbbrev from an int countryCode
	 * for example get "USA" from 1, & "CA" from 2
	 */
	public static String getCountryAbbrevFromCountryCode(int countryCode) {
		switch (countryCode) {
		case RMAccountType.US_CODE:
			return "USA";
		case RMAccountType.CA_CODE:
			return "CA";
		default:
			return "";
		}	
	}

	public static String getCountryFromCode (int countryCode) {
		switch (countryCode) {
			case RMAccountType.US_CODE:
				return "United States";
			case RMAccountType.CA_CODE:
				return "Canada";
			default:
				return "";
		}
	}

}
