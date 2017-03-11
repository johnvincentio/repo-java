package com.idc.test;

import java.io.Serializable;

public class DddKeyItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

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
