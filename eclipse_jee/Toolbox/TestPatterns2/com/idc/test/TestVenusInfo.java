package com.idc.test;

import java.io.Serializable;

import com.idc.pattern.patterns.KeyItemInfo;
import com.idc.pattern.patterns.VenusInfo;

public class TestVenusInfo extends VenusInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public TestVenusInfo () {super();}
	public TestVenusInfo (int capacity) {super (capacity);}

	public void add (DddItemInfo item) {super.add (getKeyItemInfo (item), item);}

	public KeyItemInfo getKeyItemInfo (DddItemInfo dddItemInfo) {
		return new DddKeyItemInfo (dddItemInfo.getName(), dddItemInfo.getIValue(), dddItemInfo.getLValue());
	}
	public KeyItemInfo getKeyItemInfo (String name, int iValue, long lValue) {
		return new DddKeyItemInfo (name, iValue, lValue);
	}

	public class DddKeyItemInfo implements KeyItemInfo {
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
			if (obj == null || ! (obj instanceof KeyItemInfo)) return false;
			if (this.hashCode != ((KeyItemInfo) obj).hashCode()) return false;
			return true;
		}

		public String toString() {
			return "("+getName()+","+getIValue()+","+getLValue()+")";
		}
	}
}
