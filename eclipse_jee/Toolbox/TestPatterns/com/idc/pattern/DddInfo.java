package com.idc.pattern;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DddInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<DddKeyItemInfo, DddItemInfo> m_collection = new LinkedHashMap<DddKeyItemInfo, DddItemInfo>();

	public Iterator<DddItemInfo> getItems() {return m_collection.values().iterator();}
	public void add (DddItemInfo item) {
		if (item == null) return;
		m_collection.put (item.getDddKeyItemInfo(), item);
	}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public boolean isExists (DddItemInfo item) {
		return m_collection.containsKey (item.getDddKeyItemInfo());
	}
	public DddItemInfo getItem (DddKeyItemInfo item) {
		return m_collection.get (item);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((DddItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
