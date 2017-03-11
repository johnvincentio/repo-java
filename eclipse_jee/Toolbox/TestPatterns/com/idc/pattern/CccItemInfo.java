package com.idc.pattern;

import java.io.Serializable;

public class CccItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String aaa;
	private String bbb;

	public CccItemInfo (String id, String aaa, String bbb) {
		this.id = id;
		this.aaa = aaa;
		this.bbb = bbb;
	}

	public String getId() {return id;}
	public String getAaa() {return aaa;}
	public String getBbb() {return bbb;}

	public void setId (String id) {this.id = id;}
	public void setAaa (String aaa) {this.aaa = aaa;}
	public void setBbb (String bbb) {this.bbb = bbb;}

	public String toString() {
		return "("+getId()+","+getAaa()+","+getBbb()+")";
	}
}
