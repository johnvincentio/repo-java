package com.idc.pattern.a;

import java.io.Serializable;

public class DddItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private KeyItemInfo keyItemInfo;
	private String junk;
	private String boat;

	public DddItemInfo (String name, int iValue, long lValue, String junk, String boat) {
		this.keyItemInfo = new KeyItemInfo (name, iValue, lValue);
		this.junk = junk;
		this.boat = boat;
	}
	public int hashCode() {return keyItemInfo.hashCode();}

	public KeyItemInfo getKeyItemInfo() {return keyItemInfo;}
	public String getJunk() {return junk;}
	public String getBoat() {return boat;}

	public void setKeyItemInfo (KeyItemInfo keyItemInfo) {this.keyItemInfo = keyItemInfo;}
	public void setJunk (String junk) {this.junk = junk;}
	public void setBoat (String boat) {this.boat = boat;}

	public String toString() {
		return "("+getKeyItemInfo()+","+getJunk()+","+getBoat()+")";
	}

	public class KeyItemInfo {

		private String name;
		private int iValue;
		private long lValue;
		private int hashCode = 0;

		public KeyItemInfo (String name, int iValue, long lValue) {
			this.name = name;
			this.iValue = iValue;
			this.lValue = lValue;
			hashCode = (name + ";" + Integer.toString(iValue) + ";" + Long.toString(lValue)).hashCode();
			System.out.println("name "+name+" iValue "+iValue+" lValue "+lValue);
			System.out.println("hashCode "+hashCode);
		}

		public String getName() {return name;}
		public int getIValue() {return iValue;}
		public long getLValue() {return lValue;}

		public void setName (String name) {this.name = name;}
		public void setIValue (int iValue) {this.iValue = iValue;}
		public void setLValue (long lValue) {this.lValue = lValue;}

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
