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

package com.hertz.hercutil.rentalman.reports.equipmentHistory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class to encapsulate the Equipment History Collection
 * 
 * @author John Vincent
 */

public class EquipmentHistoryInfo implements Serializable {
	private ArrayList m_list = new ArrayList();

	public Iterator getItems() {return m_list.iterator();}
	public void add (EquipmentHistoryItemInfo item) {m_list.add(item);}
	public void add (EquipmentHistoryInfo equipmentHistoryInfo) {
		for (Iterator iter = equipmentHistoryInfo.getItems(); iter.hasNext(); )
			m_list.add((EquipmentHistoryItemInfo) iter.next());
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((EquipmentHistoryItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
