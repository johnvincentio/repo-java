
package com.idc.appender;

public class Row {
	private String row;
	public Row (String s) {row = s;}
	public void setRow(String s) {row = s;}
	public String getRow() {return row;}
	public String toString() {return "("+row+")";}
}
