package com.idc.test;

import java.io.Serializable;
import java.util.Comparator;

import com.idc.pattern.patterns.KeyItemInfo;
import com.idc.pattern.patterns.MarsInfo;

public class TestMarsInfo extends MarsInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public TestMarsInfo (int capacity) {
		super (capacity, new Comparator<KeyItemInfo> () {
			public int compare(KeyItemInfo o1, KeyItemInfo o2) {
				DddKeyItemInfo d1 = (DddKeyItemInfo) o1;
				DddKeyItemInfo d2 = (DddKeyItemInfo) o2;
				return d1.getName().compareTo(d2.getName());
			}
		});
	}

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
/*
	@SuppressWarnings("unused") 
	private class ItemComparator2 implements Comparator {
		public int compare(Object o1, Object o2) {
			KeyItemInfo key1 = (KeyItemInfo) o1;
			KeyItemInfo key2 = (KeyItemInfo) o2;
			if (key1.getIValue() == key2.getIValue()) return 0;
			return key1.getIValue() > key2.getIValue() ? 1 :-1;
		}
	}
*/
