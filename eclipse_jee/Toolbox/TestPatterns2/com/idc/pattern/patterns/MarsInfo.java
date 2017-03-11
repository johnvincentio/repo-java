package com.idc.pattern.patterns;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class MarsInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<KeyItemInfo, Object> m_map;
	private TreeMap<KeyItemInfo, Object> m_treeMap;

	public MarsInfo (int capacity, Comparator<KeyItemInfo> comparator) {
		m_map = new LinkedHashMap <KeyItemInfo, Object> (capacity);
		m_treeMap = new TreeMap <KeyItemInfo, Object> (comparator);
	}

	public Iterator<Object> getItems() {return m_treeMap.values().iterator();}

	public void add (KeyItemInfo key, Object item) {
		if (item == null) return;
		m_map.put (key, item);
		m_treeMap.put (key, item);
	}
	public int getSize() {return m_map.size();}
	public boolean isNone() {return getSize() < 1;}

	public boolean isExists (KeyItemInfo key) {return m_map.containsKey (key);}
	public Object getItem (KeyItemInfo key) {return m_map.get (key);}

	public String toString() {return "("+m_map+")";}
}
