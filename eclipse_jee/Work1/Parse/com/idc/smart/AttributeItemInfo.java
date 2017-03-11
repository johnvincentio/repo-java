package com.idc.smart;

import java.io.Serializable;

public class AttributeItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String value;
	public AttributeItemInfo (String name, String value) {
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
