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

/**
 * never use setters - the RentalMan account type objects must be immutable.
 * 
 * @author John Vincent
 */

import java.io.Serializable;
import java.util.Iterator;

import com.hertz.hercutil.presentation.CountryUtil;
import com.hertz.hercutil.presentation.HercDate;

public class RMNarpAccountInfo implements RMAccountType, Serializable {
	private RMAccountInfo rmNarpAccountInfo;
	private RMAccountsInfo rmAccountsInfo = new RMAccountsInfo();
	private RMAccountInfo rmCorpLinkAccountInfo;

	public RMNarpAccountInfo (RMAccountInfo rmAccountInfo, RMAccountInfo rmCorpLinkAccountInfo, 
			RMAccountsInfo rmAccountsInfo) {
		this.rmNarpAccountInfo = rmAccountInfo;
		this.rmCorpLinkAccountInfo = rmCorpLinkAccountInfo;
		this.rmAccountsInfo = rmAccountsInfo;
	}

	public Iterator getItems() {return rmAccountsInfo.getItems();}
	public int getSize() {return rmAccountsInfo.getSize();}
	public boolean isNone() {return getSize() < 1;}
	public boolean isNarp() {return true;}
	public boolean isAccount() {return false;}
	public boolean isLocalNarp() {return false;}
	public boolean nullData() {
		if (this.rmNarpAccountInfo != null && this.rmAccountsInfo != null) return false;
		return true;
	}
	public RMAccountInfo getNarpAccount() {return rmNarpAccountInfo;}

	public String getAccountNumber() {return rmNarpAccountInfo.getAccountNumber();}
	public int getCountryCode() {return rmNarpAccountInfo.getCountryCode();}
	public String getBusiness() {return rmNarpAccountInfo.getBusiness();}
	public String getAddress1() {return rmNarpAccountInfo.getAddress1();}
	public String getAddress2() {return rmNarpAccountInfo.getAddress2();}
	public String getCity() {return rmNarpAccountInfo.getCity();}
	public String getState() {return rmNarpAccountInfo.getState();}
	public String getZip() {return rmNarpAccountInfo.getZip();}
	public HercDate getLastActivity() {return rmNarpAccountInfo.getLastActivity();}
	
	public String getCountry() {return CountryUtil.getCountryFromCode(getCountryCode());}
	public String getCountryAbbrev() {return CountryUtil.getCountryAbbrevFromCountryCode(getCountryCode());}
	public RMAccountInfo getCorpLinkAccount() {return rmCorpLinkAccountInfo;}

	/**
	 * Determine whether account exists for an account and country
	 * 
	 * @param accountNumber		Account number
	 * @param countryCode		Country code
	 * @return					true if account exists
	 */
	public boolean isExists (String accountNumber, int countryCode) {
		Iterator iterator = getItems();
		while (iterator.hasNext()) {
			RMAccountInfo accountInfo = (RMAccountInfo)iterator.next();
			if (accountInfo.getAccountNumber().equals(accountNumber) && accountInfo.getCountryCode() == countryCode)
				return true;
		}
		return false;
	}

	public String toString() {
		return "(RMNarpAccountInfo "+rmNarpAccountInfo+", rmCorpLinkAccountInfo "+rmCorpLinkAccountInfo+", rmAccountsInfo "+rmAccountsInfo+")";
	}
}
