package com.idc.five.game;

public class Coordinate {
	private int dr;
	private int dc;
	public Coordinate (int dr, int dc) {
		this.dr = dr;
		this.dc = dc;
	}
	public int getRow() {return dr;}
	public int getCol() {return dc;}
	public String toString() {return "(" + getRow() + "," + getCol() + ")";}
}
