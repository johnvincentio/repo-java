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

package com.hertz.hercutil.rentalman.data;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;

import com.hertz.hercutil.company.RMNarpAccountInfo;
import com.hertz.hercutil.presentation.HercDate;
import com.hertz.hercutil.presentation.UtilHelper;

/**
 * never use setters - the RentalMan account type objects must be immutable.
 * 
 * @author John Vincent
 */

public class RMNarpDataItemInfo implements Serializable {
	private boolean demo;
	private int countryCode;
	private String narp;
	private String corplink;
	private String business;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private HercDate lastActivityDate;
	
	public RMNarpDataItemInfo (boolean demo, RMNarpAccountInfo rmNarpAccountInfo) {
		this.demo = demo;
		countryCode = rmNarpAccountInfo.getCountryCode();
		narp = rmNarpAccountInfo.getAccountNumber();
		corplink = rmNarpAccountInfo.getCorpLinkAccount().getAccountNumber();
		business = rmNarpAccountInfo.getBusiness();
		address1 = rmNarpAccountInfo.getAddress1();
		address2 = rmNarpAccountInfo.getAddress2();
		city = rmNarpAccountInfo.getCity();
		state = rmNarpAccountInfo.getState();
		zip = rmNarpAccountInfo.getZip();
		lastActivityDate = rmNarpAccountInfo.getLastActivity();
	}

	public RMNarpDataItemInfo(HashMap hashMap, int countryCode) {
		this.corplink = UtilHelper.trimString((String) hashMap.get("corplink"));
		this.countryCode = countryCode;
		this.narp = UtilHelper.trimString((String) hashMap.get("narp"));
		this.business = UtilHelper.trimString((String) hashMap.get("business"));
		this.address1 = UtilHelper.trimString((String) hashMap.get("address1"));
		this.address2 = UtilHelper.trimString((String) hashMap.get("address2"));
		this.city = UtilHelper.trimString((String) hashMap.get("city"));
		this.state = UtilHelper.trimString((String) hashMap.get("state"));
		this.zip = UtilHelper.trimString((String) hashMap.get("zip"));
		this.demo = UtilHelper.getBoolean((String) hashMap.get("demo"));
		this.lastActivityDate = new HercDate((Date)hashMap.get("lastactivitydate"));
	}

	public boolean isDemo() {return demo;}
	public int getCountryCode() {return countryCode;}
	public String getNarp() {return narp;}
	public String getCorplink() {return corplink;}
	public String getBusiness() {return business;}
	public String getAddress1() {return address1;}
	public String getAddress2() {return address2;}
	public String getCity() {return city;}
	public String getState() {return state;}
	public String getZip() {return zip;}
	public HercDate getLastActivity() {return lastActivityDate;}

	public boolean isSame (RMNarpDataItemInfo item) {
		if (isDemo() != item.isDemo()) return false;
		if (getCountryCode() != item.getCountryCode()) return false;
		if (! UtilHelper.isEquals (getNarp(), item.getNarp())) return false;
		if (! UtilHelper.isEquals (getCorplink(), item.getCorplink())) return false;
		if (! UtilHelper.isEquals (getBusiness(), item.getBusiness())) return false;
		if (! UtilHelper.isEquals (getAddress1(), item.getAddress1())) return false;
		if (! UtilHelper.isEquals (getAddress2(), item.getAddress2())) return false;
		if (! UtilHelper.isEquals (getCity(), item.getCity())) return false;
		if (! UtilHelper.isEquals (getState(), item.getState())) return false;
		if (! UtilHelper.isEquals (getZip(), item.getZip())) return false;
		if (! UtilHelper.isEquals (getLastActivity(), item.getLastActivity())) return false;
		return true;
	}

	public String toString() {
		return "(RMNarpDataItemInfo "+isDemo()+","+getCountryCode()+","+getNarp()+","+getCorplink()+","+
				getBusiness()+","+getAddress1()+","+getAddress2()+","+
				getCity()+","+getState()+","+getZip()+","+getLastActivity().getDate()+")";
	}
}
