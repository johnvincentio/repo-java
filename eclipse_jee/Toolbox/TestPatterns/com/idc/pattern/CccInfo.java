package com.idc.pattern;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CccInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<String, CccItemInfo> m_collection = new HashMap<String, CccItemInfo>();

	public Iterator<CccItemInfo> getItems() {return m_collection.values().iterator();}		// not ordered
	// want ordering, use LinkedHashMap

	public void add (CccItemInfo item) {
		if (item != null && (! isExists (item.getId())))
			m_collection.put (item.getId(), item);
	}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public boolean isExists (String id) {return m_collection.containsKey (id);}
	public CccItemInfo getItem (String id) {return m_collection.get (id);}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((CccItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
