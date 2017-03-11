package com.idc.pattern;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class AbcInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<String, AbcItemInfo> m_collection = new LinkedHashMap<String, AbcItemInfo>();

	public Iterator<AbcItemInfo> getItems() {return m_collection.values().iterator();}
	public void add (AbcItemInfo item) {
		if (item == null) return;
		int hashcode = item.hashCode();
		m_collection.put (Integer.toString(hashcode), item);		// need hashcode for the key....
	}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public boolean isExists (AbcItemInfo item) {
		int hashcode = item.hashCode();
		return m_collection.containsKey (Integer.toString(hashcode));
	}
	public AbcItemInfo getItem (AbcItemInfo item) {			// obviously not much use...
		int hashcode = item.hashCode();
		return m_collection.get (Integer.toString(hashcode));
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((AbcItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
