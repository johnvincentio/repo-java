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
 * Class to encapsulate the RMDataInfo objects for all countries
 * 
 * @author John Vincent
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class RMComboInfo implements Serializable {
	private ArrayList m_list = new ArrayList();

	public Iterator getItems() {return m_list.iterator();}
	public void add (RMComboItemInfo item) {
		if (item != null) m_list.add(item);
	}

	/**
	 * Get RMComboItemInfo by country code
	 * 
	 * @param countryCode		country code
	 * @return					RMComboItemInfo
	 */
	public RMComboItemInfo getItem (int countryCode) {
		Iterator iter = m_list.iterator();
		RMComboItemInfo info;
		while (iter.hasNext()) {
			info = (RMComboItemInfo) iter.next();
			if (countryCode == info.getCountryCode()) return info;
		}
		return null;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((RMComboItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
