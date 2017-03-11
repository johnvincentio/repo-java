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
import java.util.ArrayList;
import java.util.Iterator;

import com.hertz.hercutil.rentalman.data.RMContractRentalsEquipmentDataInfo;
import com.hertz.hercutil.rentalman.data.RMContractRentalsEquipmentDataItemInfo;

/**
 * Method "getContractRentalItems" in RentalManCADAO and RentalUSDAO returns an un-ordered ContractRentalsEquipmentInfo object,
 * since we removed orderby clause from the SQL statement, because of the indexing issue from AS400 guys.
 * Now the Object returned will not be in an order.(comments added by prc9203)
 * @author John Vincent
 *
 */
public class ContractRentalsEquipmentInfo implements Serializable {
	private ArrayList m_list = new ArrayList();
/*
 * Constructor to build an empty ArrayList
 */
	public ContractRentalsEquipmentInfo() {}
/*
 * Constructor to build a new list of Contract Rental Equipment
 */
	public ContractRentalsEquipmentInfo (ContractRentalsEquipmentInfo contractRentalsEquipmentInfo) {
		Iterator iter = contractRentalsEquipmentInfo.getItems();
		while (iter.hasNext()) {
			ContractRentalsEquipmentItemInfo info = (ContractRentalsEquipmentItemInfo) iter.next();
			if (info != null)
				add (new ContractRentalsEquipmentItemInfo (info));
		}
	}
/*
 * Constructor to build a new list of Contract Rental Equipment from the RM Contract Rental Equipment 
 */
	public ContractRentalsEquipmentInfo (RMContractRentalsEquipmentDataInfo rmContractRentalsEquipmentDataInfo) {
		Iterator iter = rmContractRentalsEquipmentDataInfo.getItems();
		while (iter.hasNext()) {
			RMContractRentalsEquipmentDataItemInfo info = (RMContractRentalsEquipmentDataItemInfo) iter.next();
			if (info != null)
				add (new ContractRentalsEquipmentItemInfo (info.getCategory(), info.getClassification()));
		}
	}
/*
 * Get the Contract Rentals Equipment Item for a given category and classification
 */
	private ContractRentalsEquipmentItemInfo getItem (int category, int classification) {
		Iterator iter = m_list.iterator();
		ContractRentalsEquipmentItemInfo info;
		while (iter.hasNext()) {
			info = (ContractRentalsEquipmentItemInfo) iter.next();
			if (category == info.getCategory() && classification == info.getClassification()) return info;
		}
		return null;
	}
/*
 * Return true if the given category and classification is a Contract Rentals Equipment Item
 */
	public boolean isExists (int category, int classification) {
		if (getItem (category, classification) == null) return false;
		return true;
	}

	public Iterator getItems() {return m_list.iterator();}
	public void add (ContractRentalsEquipmentItemInfo item) {m_list.add(item);}

	public int getSize() {return m_list.size();}
//	public boolean isNone() {return getSize() < 1;}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((ContractRentalsEquipmentItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
