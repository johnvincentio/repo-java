package com.idc.pattern.patterns;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class EarthInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<KeyItemInfo, Object> m_map;

	public EarthInfo () {m_map = new LinkedHashMap <KeyItemInfo, Object> ();}
	public EarthInfo (int capacity) {m_map = new LinkedHashMap <KeyItemInfo, Object> (capacity);}

	public void add (KeyItemInfo key, Object item) {if (item != null) m_map.put (key, item);}
	public Iterator<Object> getItems() {return m_map.values().iterator();}
	public int getSize() {return m_map.size();}
	public boolean isNone() {return getSize() < 1;}

	public boolean isExists (KeyItemInfo key) {return m_map.containsKey (key);}
	public Object getItem (KeyItemInfo key) {return m_map.get (key);}

	public String toString() {return "("+m_map+")";}
}
