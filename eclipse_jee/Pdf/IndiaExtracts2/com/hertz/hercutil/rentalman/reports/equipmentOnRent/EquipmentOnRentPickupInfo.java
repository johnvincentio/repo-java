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
 * Class to encapsulate the Equipment On Rent Pickup Collection
 * 
 * @author John Vincent
 */

public class EquipmentOnRentPickupInfo implements Serializable {
	private ArrayList m_list = new ArrayList();

	private Iterator getItems() {return m_list.iterator();}
	public void add (EquipmentOnRentPickupItemInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}
/*
	public String getAllPickup() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((EquipmentOnRentPickupItemInfo) m_list.get(i)).getRAReleases());
		return buf.toString();
	}
*/
	public String getTicket (String contract, String sequence, String equipment) {
		Iterator iter = getItems();
		while (iter.hasNext()) {
			EquipmentOnRentPickupItemInfo item = (EquipmentOnRentPickupItemInfo) iter.next();
			if (item == null) continue;
			if (! contract.equals(item.getContract())) continue;
			if (! sequence.equals(item.getSequence())) continue;
			if (! equipment.equals(item.getEquipment())) continue;
			return item.getTicket();
		}
		return null;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((EquipmentOnRentPickupItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}

//str_contract.trim() + "-" +  str_dseq.trim() + "-" + str_item.trim() + "**";

/*
for	(int r=0; r < num_total+1; r++) {
	if (str_checkpickup.equals(RAreleases[r]) && str_PUTicket.trim().equals(""))
		str_PUTicket = RAticket[r];
}
*/
