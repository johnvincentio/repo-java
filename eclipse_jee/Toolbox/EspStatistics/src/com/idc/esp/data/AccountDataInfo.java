package com.idc.esp.data;

import java.util.ArrayList;
import java.util.Iterator;

public class AccountDataInfo {
	private ArrayList<AccountDataItemInfo> m_collection = new ArrayList<AccountDataItemInfo> ();

	public void add (AccountDataItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<AccountDataItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public AccountDataItemInfo getAccount (long accountid) {
		Iterator<AccountDataItemInfo> iter = getItems();
		while (iter.hasNext()) {
			AccountDataItemInfo accountDataItemInfo = iter.next();
			if (accountDataItemInfo == null) continue;
			if (accountid == accountDataItemInfo.getAccountid()) return accountDataItemInfo;
		}
		return null;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(m_collection.get(i).toString());
		return "("+buf.toString()+")";
	}
}
