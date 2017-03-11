package com.idc.sql.lib;

/**
* @author John Vincent
*/

import java.io.Serializable;

public class DataInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String data;
	public DataInfo (String data) {this.data = data;}
	public String getData() {return data;}
	public String toString() {return "("+data+")";}
}
