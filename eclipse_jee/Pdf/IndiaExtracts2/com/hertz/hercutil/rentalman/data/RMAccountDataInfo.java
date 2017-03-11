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

/**
 * Describe RentalMan Accounts
 * 
 * @author John Vincent
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.hertz.hercutil.company.RMAccountInfo;
import com.hertz.hercutil.company.RMAccountType;
import com.hertz.hercutil.company.RMNarpAccountInfo;

public class RMAccountDataInfo implements Serializable {
	private ArrayList m_list = new ArrayList();
	public Iterator getItems() {return m_list.iterator();}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	/**
	 * Add RM account
	 * 
	 * @param rmAccountDataItemInfo		Account to be added
	 */
	public void add (RMAccountDataItemInfo rmAccountDataItemInfo) {
		if (rmAccountDataItemInfo == null) return;
		if (! isExists (rmAccountDataItemInfo.getAccount(), rmAccountDataItemInfo.getCountryCode()))
			m_list.add (rmAccountDataItemInfo);
	}

	/**
	 * Add RM accounts
	 * 
	 * @param demo				true if demo account
	 * @param rmAccountInfo		RM account
	 */
	public void add (boolean demo, RMAccountInfo rmAccountInfo) {
		if (rmAccountInfo == null) return;
		if (! isExists (rmAccountInfo)) add (new RMAccountDataItemInfo (demo, rmAccountInfo));
	}

	/**
	 * Add RM Narp accounts, but not the narp account itself.
	 * 
	 * @param demo					true if demo account
	 * @param rmNarpAccountInfo		RM Narp account
	 */
	public void add (boolean demo, RMNarpAccountInfo rmNarpAccountInfo) {
		if (rmNarpAccountInfo == null) return;
		Iterator iter = rmNarpAccountInfo.getItems();
		while (iter.hasNext()) {
			RMAccountType rmAccountType = (RMAccountType) iter.next();
			if (rmAccountType == null) continue;
			RMAccountInfo rmAccountInfo = (RMAccountInfo) rmAccountType;
			if (! isExists (rmAccountInfo))
				add (new RMAccountDataItemInfo (
								demo, 
								rmNarpAccountInfo.getNarpAccount().getAccountNumber(),
								rmAccountInfo));
		}
	}

	/**
	 * Determine whether this account exists
	 * 
	 * @param rmAccountInfo		RM account
	 * @return					true if this account exists
	 */
	public boolean isExists (RMAccountInfo rmAccountInfo) {
		Iterator iter = getItems();
		while (iter.hasNext()) {
			RMAccountDataItemInfo rmAccountDataItemInfo = (RMAccountDataItemInfo) iter.next();
			if (rmAccountDataItemInfo.getAccount().equals(rmAccountInfo.getAccountNumber())
					&& rmAccountDataItemInfo.getCountryCode() == rmAccountInfo.getCountryCode())
				return true;
		}
		return false;
	}

	/**
	 * Detemine whether this account exists
	 * 
	 * @param account		RM account
	 * @param countryCode	Country code
	 * @return				true if this account exists
	 */
	public boolean isExists (String account, int countryCode) {
		Iterator iter = getItems();
		while (iter.hasNext()) {
			RMAccountDataItemInfo rmAccountDataItemInfo = (RMAccountDataItemInfo) iter.next();
			if (rmAccountDataItemInfo == null) continue;
			if (rmAccountDataItemInfo.getAccount().equals(account)
					&& rmAccountDataItemInfo.getCountryCode() == countryCode) return true;
		}
		return false;
	}

	/**
	 * Get accounts in a Narp
	 * 
	 * @param narp		Narp account
	 * @return			Accounts in the Narp
	 */
	public RMAccountDataInfo getAccountsInNarp (String narp) {
		RMAccountDataInfo rmAccountDataInfo = new RMAccountDataInfo();
		Iterator iter = getItems();
		while (iter.hasNext()) {
			RMAccountDataItemInfo rmAccountDataItemInfo = (RMAccountDataItemInfo) iter.next();
			if (rmAccountDataItemInfo == null) continue;
			if (rmAccountDataItemInfo.getNarp().equals(narp))
				rmAccountDataInfo.add (rmAccountDataItemInfo);
		}
		return rmAccountDataInfo;
	}

	/**
	 * Get account object for a given account number and country code
	 * 
	 * @param account		Account number
	 * @param countryCode	Country code
	 * @return				Account object
	 */
	public RMAccountDataItemInfo getRMAccountDataItemInfo (String account, int countryCode) {
		Iterator iter = getItems();
		while (iter.hasNext()) {
			RMAccountDataItemInfo rmAccountDataItemInfo = (RMAccountDataItemInfo) iter.next();
			if (rmAccountDataItemInfo.getAccount().equals(account)
					&& rmAccountDataItemInfo.getCountryCode() == countryCode)
				return rmAccountDataItemInfo;
		}
		return null;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((RMAccountDataItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
