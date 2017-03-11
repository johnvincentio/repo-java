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

import com.hertz.hercutil.rentalman.data.RMContractLocationsDataInfo;
import com.hertz.hercutil.rentalman.data.RMContractLocationsDataItemInfo;

/**
 * @author John Vincent 
 *
 */
public class ContractLocationsInfo implements Serializable {
	private ArrayList m_collection = new ArrayList();
	public Iterator getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public ContractLocationsInfo() {}

	public ContractLocationsInfo (ContractLocationsInfo contractLocationsInfo) {
		add (contractLocationsInfo);
	}

	public ContractLocationsInfo (RMContractLocationsDataInfo rmContractLocationsDataInfo) {
		Iterator iter = rmContractLocationsDataInfo.getItems();
		while (iter.hasNext()) {
			RMContractLocationsDataItemInfo info = (RMContractLocationsDataItemInfo) iter.next();
			if (info != null)
				add (new ContractLocationsItemInfo (info.getBranch()));
		}
	}

	public void add (ContractLocationsItemInfo item) {
		if (item != null) m_collection.add(item);
	}

	protected ArrayList getCollection() {return m_collection;}
	public void add (ContractLocationsInfo info) {
		if (info != null) m_collection.addAll (info.getCollection());
	}

	public boolean isExists (ContractLocationsItemInfo item) {
		return m_collection.contains(item);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_collection.size(); i++)
			buf.append(((ContractLocationsItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
