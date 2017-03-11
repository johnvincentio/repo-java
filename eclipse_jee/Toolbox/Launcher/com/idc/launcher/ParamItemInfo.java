package com.idc.launcher;

import java.io.Serializable;

public class ParamItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	public ParamItemInfo (String name) {
		this.name = name;
	}
	public String getName() {return name;}

	public void setName (String name) {this.name = name;}

	public String toString() {
		return "("+getName()+")";
	}
}
