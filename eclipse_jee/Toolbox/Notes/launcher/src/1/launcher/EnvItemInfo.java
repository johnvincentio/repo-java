package com.idc.launcher;

import java.io.Serializable;

public class EnvItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String value;
	public EnvItemInfo (String name, String value) {
		this.name = name;
		this.value = value;
	}
	public String getName() {return name;}
	public String getValue() {return value;}

	public void setName (String name) {this.name = name;}
	public void setValue (String value) {this.value = value;}

	public String toString() {
		return "("+getName()+","+getValue()+")";
	}
}
