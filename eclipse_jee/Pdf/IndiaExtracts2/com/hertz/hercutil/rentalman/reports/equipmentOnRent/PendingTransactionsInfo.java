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
 * Class to encapsulate the Pending Transactions Collection
 * 
 * @author John Vincent
 */

public class PendingTransactionsInfo implements Serializable {
	private ArrayList m_list_release = new ArrayList();
	private ArrayList m_list_extend = new ArrayList();
	public void add (PendingReleaseItemInfo pendingReleaseItemInfo) {m_list_release.add (pendingReleaseItemInfo);}
	public void add (PendingExtendItemInfo pendingExtendItemInfo) {m_list_extend.add (pendingExtendItemInfo);}

	public boolean isRelease (String contract, String detailSequence) {
		for (Iterator iter = m_list_release.iterator(); iter.hasNext(); ) {
			PendingReleaseItemInfo item = (PendingReleaseItemInfo) iter.next();
			if (item == null) continue;
			if (contract.equals (item.getContract()) && detailSequence.equals (item.getSequence())) return true;
		}
		return false;
	}

	public PendingExtendItemInfo getExtend (String contract) {
		for (Iterator iter = m_list_extend.iterator(); iter.hasNext(); ) {
			PendingExtendItemInfo item = (PendingExtendItemInfo) iter.next();
			if (item == null) continue;
			if (contract.equals (item.getContract())) return item;
		}
		return null;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append ("Release (");
		for (int i = 0; i<m_list_release.size(); i++)
			buf.append(((PendingReleaseItemInfo) m_list_release.get(i)).toString());
		buf.append (") Extend (");
		for (int i=0; i<m_list_extend.size(); i++)
			buf.append(((PendingExtendItemInfo) m_list_extend.get(i)).toString());
		buf.append (")");
		return buf.toString();
	}
}
