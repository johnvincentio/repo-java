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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hertz.hercutil.company.ContractLocationsInfo;
import com.hertz.hercutil.company.ContractLocationsItemInfo;

/**
 *  Describe RentalMan Contract Locations
 * 
 * @author John Vincent 
 */

public class RMContractLocationsDataInfo implements Serializable {
	private Set m_collection = new HashSet();
	public Iterator getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public void add (int countryCode, String account, ContractLocationsInfo contractLocationsInfo) {
		if (contractLocationsInfo == null) return;
		Iterator iter = contractLocationsInfo.getItems();
		while (iter.hasNext()) {
			ContractLocationsItemInfo contractLocationsItemInfo = (ContractLocationsItemInfo) iter.next();
			if (contractLocationsItemInfo == null) continue;
			add (new RMContractLocationsDataItemInfo (countryCode, account, contractLocationsItemInfo.getBranch()));
		}
	}

	public void add (RMContractLocationsDataItemInfo info) {
		if (info != null) m_collection.add(info);
	}

	protected Set getCollection() {return m_collection;}
	public void add (RMContractLocationsDataInfo info) {
		if (info != null) m_collection.addAll (info.getCollection());
	}

	public boolean isExists (RMContractLocationsDataItemInfo item) {
		return m_collection.contains(item);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append ("(");
		Iterator iter = m_collection.iterator();
		while (iter.hasNext()) {
			RMContractLocationsDataItemInfo item = (RMContractLocationsDataItemInfo) iter.next();
			if (item != null) buf.append (item);
		}
		buf.append (")");
		return buf.toString();
	}
}
