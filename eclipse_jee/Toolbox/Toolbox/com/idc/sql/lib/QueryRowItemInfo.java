package com.idc.sql.lib;

import java.io.Serializable;

/**
* @author John Vincent
*/

public class QueryRowItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String value;
	public QueryRowItemInfo (String value) {this.value = value;}
	public String getValue() {return value;}
	public String toString() {
		return "("+getValue()+")";
	}
}
