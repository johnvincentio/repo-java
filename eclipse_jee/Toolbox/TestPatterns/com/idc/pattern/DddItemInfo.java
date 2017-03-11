package com.idc.pattern;

import java.io.Serializable;

public class DddItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private DddKeyItemInfo dddKeyItemInfo;
	private String junk;
	private String boat;

	public DddItemInfo (DddKeyItemInfo dddKeyItemInfo, String junk, String boat) {
		this.dddKeyItemInfo = dddKeyItemInfo;
		this.junk = junk;
		this.boat = boat;
	}
	public int hashCode() {return dddKeyItemInfo.hashCode();}

	public DddKeyItemInfo getDddKeyItemInfo() {return dddKeyItemInfo;}
	public String getJunk() {return junk;}
	public String getBoat() {return boat;}

	public void setDddKeyItemInfo (DddKeyItemInfo dddKeyItemInfo) {this.dddKeyItemInfo = dddKeyItemInfo;}
	public void setJunk (String junk) {this.junk = junk;}
	public void setBoat (String boat) {this.boat = boat;}

	public String toString() {
		return "("+getDddKeyItemInfo()+","+getJunk()+","+getBoat()+")";
	}
}
