package com.idc.pattern.patterns;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.idc.test.DddItemInfo;
import com.idc.test.DddKeyItemInfo;

public class EarthInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<DddKeyItemInfo, DddItemInfo> m_map;

	public EarthInfo () {m_map = new LinkedHashMap <DddKeyItemInfo, DddItemInfo> ();}
	public EarthInfo (int capacity) {m_map = new LinkedHashMap <DddKeyItemInfo, DddItemInfo> (capacity);}

	public void add (DddKeyItemInfo key, DddItemInfo item) {if (item != null) m_map.put (key, item);}
	public Iterator<DddItemInfo> getItems() {return m_map.values().iterator();}
	public int getSize() {return m_map.size();}
	public boolean isNone() {return getSize() < 1;}

	public boolean isExists (DddKeyItemInfo key) {return m_map.containsKey (key);}
	public DddItemInfo getItem (DddKeyItemInfo key) {return m_map.get (key);}

	public String toString() {return "("+m_map+")";}
}
