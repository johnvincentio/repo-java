package com.idc.sql.lib;

import java.io.Serializable;

/**
 *	Describe a IndexItemInfo
 *
 * @author John Vincent
 */
 
public class IndexItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String columnName;
	private int position;
	private boolean asc;

	public IndexItemInfo (String columnName, int position, boolean asc) {
		this.columnName = columnName;
		this.position = position;
		this.asc = asc;
	}

	public String getColumnName() {return columnName;}
	public int getPosition() {return position;}
	public boolean isAsc() {return asc;}

	public String toString() {
		return "("+getColumnName()+","+getPosition()+","+isAsc()+")";
	}
}
