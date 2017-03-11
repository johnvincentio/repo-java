package com.idc.pattern.patterns;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.idc.test.DddItemInfo;
import com.idc.test.DddKeyItemInfo;

public class MarsInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<DddKeyItemInfo, DddItemInfo> m_map;
	private TreeMap<DddKeyItemInfo, DddItemInfo> m_treeMap;

	public MarsInfo (int capacity) {
		m_map = new LinkedHashMap <DddKeyItemInfo, DddItemInfo> (capacity);
		m_treeMap = new TreeMap <DddKeyItemInfo, DddItemInfo> (new Comparator<DddKeyItemInfo> () {
			public int compare(DddKeyItemInfo o1, DddKeyItemInfo o2) {
				DddKeyItemInfo d1 = (DddKeyItemInfo) o1;
				DddKeyItemInfo d2 = (DddKeyItemInfo) o2;
				return d1.getName().compareTo(d2.getName());
			}
		});
	}

	public Iterator<DddItemInfo> getItems() {return m_treeMap.values().iterator();}

	public void add (DddKeyItemInfo key, DddItemInfo item) {
		if (item == null) return;
		m_map.put (key, item);
		m_treeMap.put (key, item);
	}
	public int getSize() {return m_map.size();}
	public boolean isNone() {return getSize() < 1;}

	public boolean isExists (DddKeyItemInfo key) {return m_map.containsKey (key);}
	public DddItemInfo getItem (DddKeyItemInfo key) {return m_map.get (key);}

	public DddKeyItemInfo getKeyItemInfo (DddItemInfo dddItemInfo) {
		return new DddKeyItemInfo (dddItemInfo.getName(), dddItemInfo.getIValue(), dddItemInfo.getLValue());
	}
	public DddKeyItemInfo getKeyItemInfo (String name, int iValue, long lValue) {
		return new DddKeyItemInfo (name, iValue, lValue);
	}

	public String toString() {return "("+m_map+")";}
}
