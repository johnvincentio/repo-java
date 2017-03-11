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
 * Describe RentalMan narp accounts.
 * 
 * @author John Vincent
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.hertz.hercutil.company.RMNarpAccountInfo;

public class RMNarpDataInfo implements Serializable {
	private ArrayList m_list = new ArrayList();
	public Iterator getItems() {return m_list.iterator();}
	public int getSize() {return m_list.size();}

	/**
	 * Add Narp account
	 * 
	 * @param demo					true if demo account
	 * @param rmNarpAccountInfo		RM Narp account
	 */
	public void add (boolean demo, RMNarpAccountInfo rmNarpAccountInfo) {
		if (rmNarpAccountInfo == null) return;
		if (! isExists (rmNarpAccountInfo))
			m_list.add (new RMNarpDataItemInfo (demo, rmNarpAccountInfo));
	}

	/**
	 * Add Narp account
	 * 
	 * @param rmNarpDataItemInfo
	 */
	public void add (RMNarpDataItemInfo rmNarpDataItemInfo) {
		if (rmNarpDataItemInfo != null) m_list.add (rmNarpDataItemInfo);
	}
		
	/**
	 * Determine whether this narp account already exists
	 * 
	 * @param rmNarpAccountInfo		RM Narp account
	 * @return						true if exists
	 */
	public boolean isExists (RMNarpAccountInfo rmNarpAccountInfo) {
		Iterator iter = getItems();
		while (iter.hasNext()) {
			RMNarpDataItemInfo rmNarpDataItemInfo = (RMNarpDataItemInfo) iter.next();
			if (rmNarpDataItemInfo.getNarp().equals(rmNarpAccountInfo.getAccountNumber())
					&& rmNarpDataItemInfo.getCountryCode() == rmNarpAccountInfo.getCountryCode())
				return true;
		}
		return false;
	}

	/**
	 * Determine whether this narp account already exists
	 * 
	 * @param account				Account number
	 * @param countryCode			Country code
	 * @return						true if exists
	 */
	public boolean isExists (String account, int countryCode) {
		Iterator iter = getItems();
		while (iter.hasNext()) {
			RMNarpDataItemInfo rmNarpDataItemInfo = (RMNarpDataItemInfo) iter.next();
			if (rmNarpDataItemInfo.getNarp().equals(account)
					&& rmNarpDataItemInfo.getCountryCode() == countryCode)
				return true;
		}
		return false;
	}

	/**
	 * Get narp account object for a given account number and country code
	 * 
	 * @param account		Account number
	 * @param countryCode	Country code
	 * @return				Narp account object
	 */
	public RMNarpDataItemInfo getRMNarpDataItemInfo (String account, int countryCode) {
		Iterator iter = getItems();
		while (iter.hasNext()) {
			RMNarpDataItemInfo rmNarpDataItemInfo = (RMNarpDataItemInfo) iter.next();
			if (rmNarpDataItemInfo.getNarp().equals(account)
					&& rmNarpDataItemInfo.getCountryCode() == countryCode)
				return rmNarpDataItemInfo;
		}
		return null;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++){
			buf.append("RMNarpDataItemInfo = ");
			buf.append(((RMNarpDataItemInfo) m_list.get(i)).toString());
		}
		return "("+buf.toString()+")";
	}
}
