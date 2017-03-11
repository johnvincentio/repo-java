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

package com.hertz.hercutil.member.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Encapsulate Member Data
 * 
 * @author John Vincent
 */

public class MemberDataInfo implements Serializable {
	private static final long serialVersionUID = -8421780265727871582L;
	private ArrayList<MemberDataItemInfo> m_list = new ArrayList<MemberDataItemInfo>();
	public Iterator<MemberDataItemInfo> getItems() {return m_list.iterator(); }
	public void add (MemberDataItemInfo item) {if (item != null) m_list.add (item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	/**
	 * Get the first member object
	 * 
	 * @return	first member object or null if none found
	 */
	public MemberDataItemInfo getFirst() {
		if (getSize() < 1) return null;
		return m_list.get(0);
	}

	/**
	 * Get username of a given member id
	 * 
	 * @param	id		memberid
	 * @return			username
	 */
	public String getUserName (long id) {
		for (MemberDataItemInfo item : m_list) {
			if (item == null) continue;
			if (id == item.getMemberid()) return item.getUsername();
		}
		return null;
	}

	/**
	 * Get member record of a given username
	 * 
	 * @param userName		username
	 * @return				member record
	 */
	public MemberDataItemInfo getMemberDataItemInfo (String userName) {
		for (MemberDataItemInfo item : m_list) {
			if (item == null) continue;
			if (userName.equals(item.getUsername())) return item;
		}
		return null;
	}

	/**
	 * Get member record of a given memberid
	 * 
	 * @param memberid		memberid
	 * @return				member record
	 */
	public MemberDataItemInfo getMemberDataItemInfo (long memberid) {
		for (MemberDataItemInfo item : m_list) {
			if (item == null) continue;
			if (memberid == item.getMemberid()) return item;
		}
		return null;
	}

	/**
	 * Get memberid for a given username
	 * 
	 * @param userName		username
	 * @return				member id, or zero if username not found
	 */
	public long getMemberId (String userName) {
		for (MemberDataItemInfo item : m_list) {
			if (item == null) continue;
			if (userName.equals(item.getUsername())) return item.getMemberid();
		}
		return 0L;
	}

	/**
	 * Checks if memberid exists
	 * 
	 * @param memberid			member id
	 * @return					true if memberid exists
	 */
	public boolean isExists (long memberid) {
		return getMemberDataItemInfo (memberid) != null ? true : false;
	}

	/**
	 * Checks if companyid exists
	 * 
	 * @param companyid			company id
	 * @return					true if companyid exists
	 */
	public boolean isCompanyExists (long companyid) {
		for (MemberDataItemInfo item : m_list) {
			if (item == null) continue;
			if (companyid == item.getCompanyid()) return true;
		}
		return false;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (MemberDataItemInfo item : m_list) buf.append(item);
		return "("+buf.toString()+")";
	}
}
