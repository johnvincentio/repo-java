package com.idc.esp.data;

import java.util.ArrayList;
import java.util.Iterator;

public class AccountInfo {

	private ArrayList<AccountItemInfo> m_collection = new ArrayList<AccountItemInfo> ();

	public void add (AccountItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<AccountItemInfo> getItems() {return m_collection.iterator();}
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

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(m_collection.get(i).toString());
		return "("+buf.toString()+")";
	}
}
