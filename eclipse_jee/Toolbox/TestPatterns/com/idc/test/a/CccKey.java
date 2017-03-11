package com.idc.test.a;

public class CccKey {
	private int number;
	private int hashCode = 0;
	public CccKey (int number) {
		this.number = number;
		hashCode = Integer.toString(number).hashCode();
	}
	public int getNumber() {return number;}
	public int hashCode() {return hashCode;}
	public boolean equals (Object obj) {
		if (obj == null || ! (obj instanceof CccKey)) return false;
		if (this.hashCode != ((CccKey) obj).hashCode()) return false;
		return true;
	}
	public String toString() {
		return "("+getNumber()+")";
	}
}
