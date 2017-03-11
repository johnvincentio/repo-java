package com.idc.test;

import java.io.Serializable;

public class DddHashItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private int iValue;
	private long lValue;
	private String junk;
	private String boat;
	private int hashCode = 0;
	public DddHashItemInfo (String name, int iValue, long lValue, String junk, String boat) {
		this.name = name;
		this.iValue = iValue;
		this.lValue = lValue;
		this.junk = junk;
		this.boat = boat;
		hashCode = (name + ";" + Integer.toString(iValue) + ";" + Long.toString(lValue)).hashCode();
	}
	public String getName() {return name;}
	public int getIValue() {return iValue;}
	public long getLValue() {return lValue;}
	public String getJunk() {return junk;}
	public String getBoat() {return boat;}

	public void setName (String name) {this.name = name;}
	public void setIValue (int iValue) {this.iValue = iValue;}
	public void setLValue (long lValue) {this.lValue = lValue;}
	public void setJunk (String junk) {this.junk = junk;}
	public void setBoat (String boat) {this.boat = boat;}

	public int hashCode() {return hashCode;}
	public boolean equals (Object obj) {
		if (obj == null || ! (obj instanceof DddHashItemInfo)) return false;
		if (this.hashCode != ((DddHashItemInfo) obj).hashCode()) return false;
		return true;
	}

	public String toString() {
		return "("+getName()+","+getIValue()+","+getLValue()+","+getJunk()+","+getBoat()+")";
	}
}
