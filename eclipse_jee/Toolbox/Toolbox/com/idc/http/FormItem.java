
package com.idc.http;

import java.io.Serializable;

/**
 * @author John Vincent
 */

public class FormItem implements Serializable {
	private static final long serialVersionUID = 1;
	private String key;
	private String value;
	public FormItem(String key, String value) {
		this.key = key;
		this.value = value;
	}
	public String getKey() {return key;}
	public String getValue() {return value;}
	public void setKey (String key) {this.key = key;}
	public void setValue (String value) {this.value = value;}
	public String toString() {
		return "("+getKey()+","+getValue()+")";
	}
}
