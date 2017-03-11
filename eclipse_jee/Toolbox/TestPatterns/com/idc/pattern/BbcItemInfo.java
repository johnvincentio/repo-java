package com.idc.pattern;

import java.io.Serializable;

public class BbcItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private BbcKeyItemInfo bbcKeyItemInfo;
	private String junk;
	private String boat;

	public BbcItemInfo (BbcKeyItemInfo bbcKeyItemInfo, String junk, String boat) {
		this.bbcKeyItemInfo = bbcKeyItemInfo;
		this.junk = junk;
		this.boat = boat;
	}
	public int hashCode() {return bbcKeyItemInfo.hashCode();}

	public BbcKeyItemInfo getBbcKeyItemInfo() {return bbcKeyItemInfo;}
	public String getJunk() {return junk;}
	public String getBoat() {return boat;}

	public void setBbcKeyItemInfo (BbcKeyItemInfo bbcKeyItemInfo) {this.bbcKeyItemInfo = bbcKeyItemInfo;}
	public void setJunk (String junk) {this.junk = junk;}
	public void setBoat (String boat) {this.boat = boat;}

	public String toString() {
		return "("+getBbcKeyItemInfo()+","+getJunk()+","+getBoat()+")";
	}
}
