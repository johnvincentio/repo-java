package com.idc.pattern;

import java.io.Serializable;

public class BbcKeyItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private int iValue;
	private long lValue;
	private int hashCode = 0;

	public BbcKeyItemInfo (String name, int iValue, long lValue) {
		this.name = name;
		this.iValue = iValue;
		this.lValue = lValue;
		hashCode = (name + ";" + Integer.toString(iValue) + ";" + Long.toString(lValue)).hashCode();
	}

	public String getName() {return name;}
	public int getIValue() {return iValue;}
	public long getLValue() {return lValue;}

	public void setName (String name) {this.name = name;}
	public void setIValue (int iValue) {this.iValue = iValue;}
	public void setLValue (long lValue) {this.lValue = lValue;}

	public int hashCode() {return hashCode;}
	public boolean equals (Object obj) {
		if (obj == null || ! (obj instanceof BbcKeyItemInfo)) return false;
		if (this.hashCode != ((BbcKeyItemInfo) obj).hashCode()) return false;
		return true;
	}

	public String toString() {
		return "("+getName()+","+getIValue()+","+getLValue()+")";
	}
}
