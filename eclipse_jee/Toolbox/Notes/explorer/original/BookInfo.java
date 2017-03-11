
package com.idc.explorer.original;

import java.io.Serializable;

/**
 *	Describe a BookItemInfo
 *
 * @author John Vincent
 */
 
public class BookInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String first;
	private String last;

	public BookInfo (String first, String last) {
		this.first = first;
		this.last = last;
	}

	public String getFirst() {return first;}
	public String getLast() {return last;}

	public void setFirst (String first) {this.first = first;}
	public void setLast (String last) {this.last = last;}

	public String getValue() {
		return getFirst()+" "+getLast();
	}

	public String toString() {
		return "("+getFirst()+","+getLast()+")";
	}
}