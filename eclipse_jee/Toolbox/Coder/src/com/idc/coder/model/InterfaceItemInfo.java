package com.idc.coder.model;

public class InterfaceItemInfo {
	private Class<?> interfaze;
	public InterfaceItemInfo (Class<?> interfaze) {this.interfaze = interfaze;}
	public Class<?> getInterfaze() {return interfaze;}

	public String toString() {
		return "("+getInterfaze()+")";
	}
}
