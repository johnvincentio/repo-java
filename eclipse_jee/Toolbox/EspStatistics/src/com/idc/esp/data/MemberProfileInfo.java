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

package com.idc.esp.data;

/**
 * Container object for the Member Profile objects
 * 
 * @author John Vincent
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class MemberProfileInfo implements Serializable {

	private static final long serialVersionUID = 9151422495264994129L;
	private ArrayList<MemberProfileItemInfo> m_list = new ArrayList<MemberProfileItemInfo>();

	public void add (MemberProfileItemInfo item) {if (item != null) m_list.add(item);}
	public Iterator<MemberProfileItemInfo> getItems() {return m_list.iterator();}
	public Iterator<MemberProfileItemInfo> getSortedItems() {
		Collections.sort (m_list, new UsernameAsc());
		return m_list.iterator();
	}
	public int getSize() {return m_list.size();}
	public boolean isEmpty() {return getSize() < 1;}

	public MemberProfileItemInfo getItem (String username) {
		for (MemberProfileItemInfo item : m_list) {
	        if (item.getUsername().equals(username)) return item;
	    }
	    return null;
	}

	/**
	 * Checks if username exists
	 * 
	 * @param username			username
	 * @return					true if username exists
	 */
	public boolean isExists (String username) {
		return getItem (username) != null ? true : false;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (MemberProfileItemInfo item : m_list) buf.append (item);
		return "("+buf.toString()+")";
	}
	
	/**
	 * Check if username exists more than once irrespective of case
	 * @param username		Username
	 * @return				true if username exists more than once, false otherwise
	 */
	public boolean isMoreThanOnce(String username) {
		int counter = 0;
		for (MemberProfileItemInfo item : m_list) {
	        if (item.getUsername().equalsIgnoreCase(username)) counter++;
			if (counter > 1) return true;
	    }
		return false;
	}

	private class UsernameAsc implements Comparator<MemberProfileItemInfo> {
		public int compare (MemberProfileItemInfo a, MemberProfileItemInfo b) {
			return a.getUsername().compareTo (b.getUsername());
		}
	}
}
