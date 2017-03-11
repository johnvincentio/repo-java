package com.idc.five.counting;

public class CountsItemInfo {
	private int actual;
	private int possible;
	private boolean border = false;
	private boolean bounded = false;
	CountsItemInfo (int actual, int possible, boolean border, boolean bounded) {
		this.actual = actual;
		this.possible = possible;
		this.border = border;
		this.bounded = bounded;
	}
	public int getActual() {return actual;}
	public int getPossible() {return possible;}
	public boolean isBorder() {return border;}
	public boolean isBounded() {return bounded;}
	public String getBorder() {return border ? "T" : "F";}
	public String getBounded() {return bounded ? "T" : "F";}

	public String toString() {
		return "(" + actual + "," + possible + "," + border + "," + bounded +")";
	}
}
