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

import com.hertz.hercutil.company.ContractRentalsEquipmentInfo;
import com.hertz.hercutil.company.ContractRentalsEquipmentItemInfo;

/**
 * Describe RentalMan Contract Rental Items
 * 
 * @author John Vincent
 */

public class RMContractRentalsEquipmentDataInfo implements Serializable {
	private Set m_collection = new HashSet();
	public Iterator getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public void add (int countryCode, String account, ContractRentalsEquipmentInfo contractRentalsEquipmentInfo) {
		if (contractRentalsEquipmentInfo == null) return;
		Iterator iter = contractRentalsEquipmentInfo.getItems();
		while (iter.hasNext()) {
			ContractRentalsEquipmentItemInfo contractRentalsEquipmentItemInfo = (ContractRentalsEquipmentItemInfo) iter.next();
			if (contractRentalsEquipmentItemInfo == null) continue;
			add (new RMContractRentalsEquipmentDataItemInfo (countryCode, account, 
					contractRentalsEquipmentItemInfo.getCategory(), contractRentalsEquipmentItemInfo.getClassification()));
		}
	}

	public void add (RMContractRentalsEquipmentDataItemInfo info) {
		if (info != null) m_collection.add (info);
	}

	protected Set getCollection() {return m_collection;}
	public void add (RMContractRentalsEquipmentDataInfo info) {
		if (info != null) m_collection.addAll (info.getCollection());
	}

	public boolean isExists (RMContractRentalsEquipmentDataItemInfo item) {
		if (item == null) return false;
		return m_collection.contains(item);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append ("(");
		Iterator iter = m_collection.iterator();
		while (iter.hasNext()) {
			RMContractRentalsEquipmentDataItemInfo item = (RMContractRentalsEquipmentDataItemInfo) iter.next();
			if (item != null) buf.append (item);
		}
		buf.append (")");
		return buf.toString();
	}
}
