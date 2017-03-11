package com.idc.pattern.patterns;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.idc.test.DddItemInfo;

public class VenusInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<DddKeyItemInfo, DddItemInfo> m_map;

	public VenusInfo () {m_map = new HashMap <DddKeyItemInfo, DddItemInfo> ();}
	public VenusInfo (int capacity) {m_map = new HashMap <DddKeyItemInfo, DddItemInfo> (capacity);}

	public void add (DddKeyItemInfo key, DddItemInfo item) {if (item != null) m_map.put (key, item);}
	public int getSize() {return m_map.size();}
	public boolean isNone() {return getSize() < 1;}

	public DddKeyItemInfo getKeyItemInfo (DddItemInfo dddItemInfo) {
		return new DddKeyItemInfo (dddItemInfo.getName(), dddItemInfo.getIValue(), dddItemInfo.getLValue());
	}
	public DddKeyItemInfo getKeyItemInfo (String name, int iValue, long lValue) {
		return new DddKeyItemInfo (name, iValue, lValue);
	}

	public boolean isExists (DddKeyItemInfo key) {return m_map.containsKey (key);}
	public DddItemInfo getItem (DddKeyItemInfo key) {return m_map.get (key);}

	public String toString() {return "("+m_map+")";}

	public class DddKeyItemInfo {

		private String name;
		private int iValue;
		private long lValue;
		private int hashCode = 0;
		public DddKeyItemInfo (String name, int iValue, long lValue) {
			this.name = name;
			this.iValue = iValue;
			this.lValue = lValue;
			hashCode = (name + ";" + Integer.toString(iValue) + ";" + Long.toString(lValue)).hashCode();
			System.out.println("DddKeyItemInfo constructor:: name "+name+" iValue "+iValue+" lValue "+lValue);
		}
		public String getName() {return name;}
		public int getIValue() {return iValue;}
		public long getLValue() {return lValue;}

		public int hashCode() {return hashCode;}
		public boolean equals (Object obj) {
			if (obj == null || ! (obj instanceof DddKeyItemInfo)) return false;
			if (this.hashCode != ((DddKeyItemInfo) obj).hashCode()) return false;
			return true;
		}

		public String toString() {
			return "("+getName()+","+getIValue()+","+getLValue()+")";
		}
	}
}
