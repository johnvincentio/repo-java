package com.idc.pattern.e;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DddInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<KeyItemInfo, DddItemInfo> m_map;

	public DddInfo (int capacity) {
		m_map = new LinkedHashMap <KeyItemInfo, DddItemInfo> (capacity);
	}

	public Iterator<DddItemInfo> getItems() {return m_map.values().iterator();}
	public void add (DddItemInfo item) {
		if (item != null) m_map.put (getKeyItemInfo (item), item);
	}
	public int getSize() {return m_map.size();}
	public boolean isNone() {return getSize() < 1;}

	public boolean isExists (DddItemInfo item) {
		return m_map.containsKey (getKeyItemInfo (item));
	}
	public boolean isExists (String name, int iValue, long lValue) {
		return m_map.containsKey (getKeyItemInfo (name, iValue, lValue));
	}

	public DddItemInfo getItem (DddItemInfo item) {
		return m_map.get (getKeyItemInfo (item));
	}
	public DddItemInfo getItem (String name, int iValue, long lValue) {
		return m_map.get (getKeyItemInfo (name, iValue, lValue));
	}

	private KeyItemInfo getKeyItemInfo (DddItemInfo dddItemInfo) {
		return new KeyItemInfo (dddItemInfo.getName(), dddItemInfo.getIValue(), dddItemInfo.getLValue());
	}
	private KeyItemInfo getKeyItemInfo (String name, int iValue, long lValue) {
		return new KeyItemInfo (name, iValue, lValue);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_map.size(); i++)
			buf.append(((DddItemInfo) m_map.get(i)).toString());
		return "("+buf.toString()+")";
	}

	private class KeyItemInfo implements Serializable {
		private static final long serialVersionUID = 1L;

		private String name;
		private int iValue;
		private long lValue;

		public KeyItemInfo (String name, int iValue, long lValue) {
			this.name = name;
			this.iValue = iValue;
			this.lValue = lValue;
			System.out.println("name "+name+" iValue "+iValue+" lValue "+lValue);
		}

		public String getName() {return name;}
		public int getIValue() {return iValue;}
		public long getLValue() {return lValue;}

		public String toString() {
			return "("+getName()+","+getIValue()+","+getLValue()+")";
		}
	}
}
