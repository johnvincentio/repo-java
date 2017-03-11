package com.idc.pattern.a;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class DddInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<DddItemInfo.KeyItemInfo, DddItemInfo> m_collection = new LinkedHashMap<DddItemInfo.KeyItemInfo, DddItemInfo>();
	private TreeMap<DddItemInfo.KeyItemInfo, DddItemInfo> m_treeMap1 = 
		new TreeMap<DddItemInfo.KeyItemInfo, DddItemInfo>(new ItemComparator1());
	private TreeMap<DddItemInfo.KeyItemInfo, DddItemInfo> m_treeMap2 = 
		new TreeMap<DddItemInfo.KeyItemInfo, DddItemInfo>(new ItemComparator2());
	private TreeMap<DddItemInfo.KeyItemInfo, DddItemInfo> m_treeMap3 = 
		new TreeMap<DddItemInfo.KeyItemInfo, DddItemInfo>(new ItemComparator3());

	public Iterator<DddItemInfo> getItems() {return m_treeMap1.values().iterator();}
	public Iterator<DddItemInfo> getItemsUnsorted() {return m_collection.values().iterator();}
	public void add (DddItemInfo item) {
		if (item == null) return;
		m_collection.put (item.getKeyItemInfo(), item);
		m_treeMap1.put (item.getKeyItemInfo(), item);
		m_treeMap2.put (item.getKeyItemInfo(), item);
		m_treeMap3.put (item.getKeyItemInfo(), item);
	}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public boolean isExists (DddItemInfo item) {
		return m_collection.containsKey (item.getKeyItemInfo());
	}
	public DddItemInfo getItem (DddItemInfo.KeyItemInfo item) {
		return m_collection.get (item);
	}

	public class ItemComparator1 implements Comparator<DddItemInfo.KeyItemInfo> {
		public int compare(DddItemInfo.KeyItemInfo o1, DddItemInfo.KeyItemInfo o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}
	public class ItemComparator2 implements Comparator<DddItemInfo.KeyItemInfo> {
		public int compare (DddItemInfo.KeyItemInfo o1, DddItemInfo.KeyItemInfo o2) {
			if (o1.getIValue() == o2.getIValue()) return 0;
			return o1.getIValue() > o2.getIValue() ? 1 :-1;
		}
	}
	public class ItemComparator3 implements Comparator<DddItemInfo.KeyItemInfo> {
		public int compare (DddItemInfo.KeyItemInfo o1, DddItemInfo.KeyItemInfo o2) {
			if (o1.getLValue() == o2.getLValue()) return 0;
			return o1.getLValue() > o2.getLValue() ? 1 :-1;
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((DddItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
