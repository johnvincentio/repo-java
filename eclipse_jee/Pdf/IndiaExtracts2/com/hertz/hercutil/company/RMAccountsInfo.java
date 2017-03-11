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

/**
 * @author John Vincent
 *
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.hertz.hercutil.rentalman.data.RMAccountDataInfo;
import com.hertz.hercutil.rentalman.data.RMAccountDataItemInfo;

public class RMAccountsInfo implements Serializable {
	private ArrayList m_list = new ArrayList();
	public RMAccountsInfo() {}
	public RMAccountsInfo (RMAccountDataInfo rmAccountDataInfo) {
		Iterator iter = rmAccountDataInfo.getItems();
		while (iter.hasNext()) {
			RMAccountDataItemInfo rmAccountDataItemInfo = (RMAccountDataItemInfo) iter.next();
			add (new RMAccountInfo(rmAccountDataItemInfo));
		}
	}
	public static boolean isAccount (RMAccountType info) {return (info == null) ? false: true;}
	public Iterator getItems() {return m_list.iterator();}
	public void add (RMAccountType item) {
		if (item != null) m_list.add(item);
	}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((RMAccountType) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
