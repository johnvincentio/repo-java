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

package com.hertz.hercutil.rentalman.reports.equipmentOnRent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class to encapsulate the Equipment On Rent Collection
 * 
 * @author John Vincent
 */

public class EquipmentOnRentInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList m_list = new ArrayList();
	private int countryCode;
	public EquipmentOnRentInfo (int countryCode) {
		this.countryCode = countryCode;
	}
	public boolean isUS() {return countryCode == 1;}

	public Iterator getItems() {return m_list.iterator();}
	public void add (EquipmentOnRentItemInfo item) {m_list.add(item);}
	public void add (EquipmentOnRentInfo equipmentOnRentInfo) {
		for (Iterator iter = equipmentOnRentInfo.getItems(); iter.hasNext(); )
			m_list.add((EquipmentOnRentItemInfo) iter.next());
	}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((EquipmentOnRentItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
