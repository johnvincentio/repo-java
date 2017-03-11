package com.idc.esp.data;

import java.util.ArrayList;
import java.util.Iterator;

public class NarpInfo {

	private ArrayList<NarpItemInfo> m_collection = new ArrayList<NarpItemInfo> ();

	public void add (NarpItemInfo item) {
		if (item != null && ! isExists (item.getAccount())) m_collection.add (item);
	}
	public Iterator<NarpItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String getAccounts() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++) {
			if (i > 0) buf.append(" ");
			buf.append (m_collection.get(i).getAccount());
		};
		return buf.toString();
	}

	public boolean isExists (String narp) {
		Iterator<NarpItemInfo> iter = getItems();
		while (iter.hasNext()) {
			NarpItemInfo narpItemInfo = iter.next();
			if (narpItemInfo == null) continue;
			if (narpItemInfo.getAccount().equals (narp)) return true;
		}
		return false;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(m_collection.get(i).toString());
		return "("+buf.toString()+")";
	}
}
