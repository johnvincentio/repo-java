package com.idc.five.pattern;

public class Pattern {
	private int dr;
	private int dc;
	public Pattern (int dr, int dc) {
		this.dr = dr;
		this.dc = dc;
	}
	public int getRow() {return dr;}
	public int getCol() {return dc;}
	public String toString() {return "(" + getRow() + "," + getCol() + ")";}
}
