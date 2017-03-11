package com.idc.esp.data;

import java.util.ArrayList;
import java.util.Iterator;

public class MemberDataInfo {

	private ArrayList<MemberDataItemInfo> m_collection = new ArrayList<MemberDataItemInfo>();

	public void add (MemberDataItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<MemberDataItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public MemberDataInfo getItems (String username) {
		MemberDataInfo memberDataInfo = new MemberDataInfo();
		Iterator<MemberDataItemInfo> iter = getItems();
		while (iter.hasNext()) {
			MemberDataItemInfo item = iter.next();
			if (item == null) continue;
			if (item.getUsername().equals (username)) memberDataInfo.add (item);
		}
		return memberDataInfo;
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(m_collection.get(i).toString());
		return "("+buf.toString()+")";
	}
}
