package com.idc.pattern.patterns;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class VenusInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<KeyItemInfo, Object> m_map;

	public VenusInfo () {m_map = new HashMap <KeyItemInfo, Object> ();}
	public VenusInfo (int capacity) {m_map = new HashMap <KeyItemInfo, Object> (capacity);}

	public void add (KeyItemInfo key, Object item) {if (item != null) m_map.put (key, item);}
	public int getSize() {return m_map.size();}
	public boolean isNone() {return getSize() < 1;}

	public boolean isExists (KeyItemInfo key) {return m_map.containsKey (key);}
	public Object getItem (KeyItemInfo key) {return m_map.get (key);}

	public String toString() {return "("+m_map+")";}
}
