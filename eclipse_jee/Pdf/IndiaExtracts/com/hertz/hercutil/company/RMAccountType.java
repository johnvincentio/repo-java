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

package com.hertz.hercutil.company;

import java.io.Serializable;

import com.hertz.hercutil.presentation.HercDate;

/**
 * never use setters - the RentalMan account type objects must be immutable.
 * 
 * @author John Vincent
 */

public interface RMAccountType extends Serializable {
	public static final int US_CODE = 1;
	public static final int CA_CODE = 2;

	public String getAccountNumber();
	public int getCountryCode();
	public String getCountry();
	public String getBusiness();
	public String getAddress1();
	public String getAddress2();
	public String getCity();
	public String getState();
	public String getZip();
	public String getCountryAbbrev();
	public HercDate getLastActivity();

	public String toString();

	public boolean isNarp();
	public boolean isAccount();
	public boolean isLocalNarp();
	public boolean nullData();
}
