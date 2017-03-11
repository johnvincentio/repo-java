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
* @author John Vincent
*
*	Encapsulate MemberDataAccountInfo object
 */

public class MemberDataAccountInfo implements Serializable {
	private static final long serialVersionUID = -3271367000871647831L;
	private ArrayList<MemberDataAccountItemInfo> m_list = new ArrayList<MemberDataAccountItemInfo>();
	public Iterator<MemberDataAccountItemInfo> getItems() {return m_list.iterator();}
	public void add (MemberDataAccountItemInfo item) {if (item != null) m_list.add (item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public MemberDataAccountItemInfo getFirst() {return m_list.get(0);}

	/**
	 * Get Number of members who have access to a given accountid
	 *  
	 * @param accountId		account id
	 * @return				Number of members who have access to a given accountid
	 */
	public int getMemberCountForAccount (long accountId) {
	    Iterator<MemberDataAccountItemInfo> accountItemsIterator = getItems();
	    int count = 0; 
	    while (accountItemsIterator.hasNext()) {
	        MemberDataAccountItemInfo accountItemInfo = accountItemsIterator.next();
	        if (accountId == accountItemInfo.getAccountid()) count++;
	    }
	    return count;
	}

	/**
	 * Get member ids of members who have access to a given accountid
	 * 
	 * @param accountId			account id
	 * @return					ids of members who have access to a given accountid
	 */
	public long[] getMemberIdsForAccount (long accountId) {
	    int count = getMemberCountForAccount(accountId);
	    long[] memberIds = new long[count];
	    Iterator<MemberDataAccountItemInfo> accountItemsIterator = getItems();
	    int i = 0; 
	    while (accountItemsIterator.hasNext()) {
	        MemberDataAccountItemInfo accountItemInfo = accountItemsIterator.next();
	        if (accountId == accountItemInfo.getAccountid()) {
	            memberIds[i] = accountItemInfo.getMemberid();
		        i++;
	        }
	    }
	    return memberIds;
	}

	/**
	 * Get common accounts from between this object and memberDataAccountInfo. Used in M1.2.2
	 * 
	 * @param memberDataAccountInfo		Member Accounts
	 * @return							Common Member Accounts
	 */
	public MemberDataAccountInfo isCommonAccount (MemberDataAccountInfo memberDataAccountInfo) {
		MemberDataAccountInfo commonAccounts = new MemberDataAccountInfo();
		Iterator<MemberDataAccountItemInfo> iterator = getItems();
		while (iterator.hasNext()) {
			MemberDataAccountItemInfo memberDataAccountItemInfo = iterator.next();
			if (memberDataAccountInfo.isExists(memberDataAccountItemInfo.getAccountid()))
				commonAccounts.add(memberDataAccountItemInfo);
		}
		return commonAccounts;
	}

	/**
	 * Checks if accountid exists in this MemberDataAccountInfo. AT1.6.3
	 * 
	 * @param accountid			account id
	 * @return					true if accountid exists
	 */
	public boolean isExists (long accountid) {
		Iterator<MemberDataAccountItemInfo> iterator = getItems();
		while (iterator.hasNext()) {
			MemberDataAccountItemInfo memberDataAccountItemInfo = iterator.next();
			if (memberDataAccountItemInfo.getAccountid() == accountid) return true;
		}
		return false;
	}

	/**
	 * Get member accounts by accountid. Used by AT1.6 and others.
	 * 
	 * @param accountid		account id
	 * @return				member accounts
	 */
	public MemberDataAccountInfo getMemberDataByAccount (long accountid) {
		MemberDataAccountInfo memberDataAccountInfo = new MemberDataAccountInfo();
		Iterator<MemberDataAccountItemInfo> iterator = getItems();
		while (iterator.hasNext()) {
			MemberDataAccountItemInfo memberDataAccountItemInfo = iterator.next();
			if (memberDataAccountItemInfo.getAccountid() == accountid)
				memberDataAccountInfo.add(memberDataAccountItemInfo);
		}
		return memberDataAccountInfo;
	}

	/**
	 * Get member accounts for this memberid
	 *  
	 * @param memberid	member id
	 * @return			member accounts
	 */
	public MemberDataAccountInfo getMemberAccountForMember(long memberid) {
		MemberDataAccountInfo memberDataAccountInfo = new MemberDataAccountInfo();
		Iterator<MemberDataAccountItemInfo> iterator = getItems();
		while (iterator.hasNext()) {
			MemberDataAccountItemInfo memberDataAccountItemInfo = iterator.next();
			if (memberDataAccountItemInfo.getMemberid() == memberid)
				memberDataAccountInfo.add(memberDataAccountItemInfo);
		}
		return memberDataAccountInfo;
	}

	/**
	 * Get member account by memberid and accountid
	 *  
	 * @param memberid		member id
	 * @param accountid		account id
	 * @return				member account
	 */
	public MemberDataAccountItemInfo getMemberAccount (long memberid, long accountid) {
		Iterator<MemberDataAccountItemInfo> iterator = getItems();
		while (iterator.hasNext()) {
			MemberDataAccountItemInfo memberDataAccountItemInfo = iterator.next();
			if (memberDataAccountItemInfo.getMemberid() == memberid &&
					memberDataAccountItemInfo.getAccountid() == accountid)
				return memberDataAccountItemInfo;
		}
		return null;
	}

	/**
	 * Checks if memberid, accountid exists
	 * 
	 * @param memberid			member id
	 * @param accountid			account id
	 * @return					true if memberid, accountid exists
	 */
	public boolean isExists (long memberid, long accountid) {
		return getMemberAccount (memberid, accountid) != null ? true : false;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (MemberDataAccountItemInfo item : m_list)
			buf.append("MemberDataAccountItemInfo = "+item);
		return "("+buf.toString()+")";
	}
}
