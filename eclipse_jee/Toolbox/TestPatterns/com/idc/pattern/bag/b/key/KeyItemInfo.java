package com.idc.pattern.bag.b.key;

import java.io.Serializable;

public class KeyItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private int iValue;
	private long lValue;

	public KeyItemInfo (String name, int iValue, long lValue) {
		this.name = name;
		this.iValue = iValue;
		this.lValue = lValue;
		System.out.println("name "+name+" iValue "+iValue+" lValue "+lValue);
	}

	public String getName() {return name;}
	public int getIValue() {return iValue;}
	public long getLValue() {return lValue;}

	public String toString() {
		return "("+getName()+","+getIValue()+","+getLValue()+")";
	}
}
