package com.idc.test;

import java.io.Serializable;

public class JVBuntItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private int value;
	private long lvalue;
	private int hashCode = 0;
	public JVBuntItemInfo (String name, int value, long lvalue) {
		this.name = name;
		this.value = value;
		this.lvalue = lvalue;
		hashCode = (name + ";" + Integer.toString(value) + ";" + Long.toString(lvalue)).hashCode();
	}
	public String getName() {return name;}
	public int getValue() {return value;}
	public long getLvalue() {return lvalue;}

	public void setName (String name) {this.name = name;}
	public void setValue (int value) {this.value = value;}
	public void setLvalue (long lvalue) {this.lvalue = lvalue;}

	public int hashCode() {return hashCode;}
	public boolean equals (Object obj) {
		if (obj == null || ! (obj instanceof JVBuntItemInfo)) return false;
		if (this.hashCode != obj.hashCode()) return false;
		return true;
	}
	public String toString() {
		return "("+getName()+","+getValue()+","+getLvalue()+")";
	}
}
