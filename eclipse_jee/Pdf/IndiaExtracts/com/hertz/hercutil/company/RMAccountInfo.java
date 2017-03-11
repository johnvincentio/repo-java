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
import java.math.BigDecimal;
import java.util.HashMap;

import com.hertz.hercutil.presentation.CountryUtil;
import com.hertz.hercutil.presentation.HercDate;
import com.hertz.hercutil.presentation.UtilHelper;

/**
 * never use setters - the RentalMan account type objects must be immutable.
 * 
 * @author John Vincent
 */

public class RMAccountInfo implements RMAccountType, Serializable {
	private String account;
	private int countryCode;
	private String business;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private HercDate lastActivityDate;
	
	public RMAccountInfo (int countryCode, HashMap hashMap) {
		this (((BigDecimal)hashMap.get("CMCUS#")).toString(), countryCode, hashMap);
	}
	public RMAccountInfo (String account, int countryCode, HashMap hashMap) {
		this.account = account;
		this.countryCode = countryCode;
		this.business = UtilHelper.trimString((String) hashMap.get("cmname"));
		this.address1 = UtilHelper.trimString((String) hashMap.get("cmadr1"));
		this.address2 = UtilHelper.trimString((String) hashMap.get("cmadr2"));
		this.city = UtilHelper.trimString((String) hashMap.get("cmcity"));
		this.state = UtilHelper.trimString((String) hashMap.get("cmstat"));
		this.zip = UtilHelper.trimString((String) hashMap.get("cmzip"));
		this.lastActivityDate = new HercDate((BigDecimal)hashMap.get("cmlidt"));
	}

	public String getAccountNumber() {return account;}
	public int getCountryCode() {return countryCode;}
	public String getBusiness() {return business;}
	public String getAddress1() {return address1;}
	public String getAddress2() {return address2;}
	public String getCity() {return city;}
	public String getState() {return state;}
	public String getZip() {return zip;}
	public HercDate getLastActivity() {return lastActivityDate;}

	public String getCountry() {return CountryUtil.getCountryFromCode(getCountryCode());}
	public String getCountryAbbrev() {return CountryUtil.getCountryAbbrevFromCountryCode(getCountryCode());}
	
	public String toString() {
		return "(RMAccountInfo "+getAccountNumber()+","+getCountryCode()+","+
				getBusiness()+","+getAddress1()+","+getAddress2()+","+
				getCity()+","+getState()+","+getZip()+","+getLastActivity()+")";
	}
	public boolean isNarp() {return false;}
	public boolean isAccount() {return true;}
	public boolean isLocalNarp() {return false;}
	public boolean nullData() {return this.account == null;}
}
