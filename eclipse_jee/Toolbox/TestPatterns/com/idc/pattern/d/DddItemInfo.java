package com.idc.pattern.d;

import java.io.Serializable;

public class DddItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private int iValue;
	private long lValue;
	private String junk;
	private String boat;
	public DddItemInfo (String name, int iValue, long lValue, String junk, String boat) {
		this.name = name;
		this.iValue = iValue;
		this.lValue = lValue;
		this.junk = junk;
		this.boat = boat;
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

	public String toString() {
		return "("+getName()+","+getIValue()+","+getLValue()+","+getJunk()+","+getBoat()+")";
	}
}
