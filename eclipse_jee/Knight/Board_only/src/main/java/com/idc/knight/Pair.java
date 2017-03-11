package com.idc.knight;

public class Pair {
	private int x = -1;
	private int y = -1;

	public Pair() {}
	public Pair (int x, int y) {
		this.x = x;
		this.y = y;
	}
	public Pair (Pair pair) {
		this.x = pair.getX();
		this.y = pair.getY();
	}
	
	public int getX() {return x;}
	public int getY() {return y;}

	public void set (int x, int y) {
		this.x = x;
		this.y = y;
	}
	public void set (Pair pair) {
		this.x = pair.getX();
		this.y = pair.getY();
	}
	public void set() {
		this.x = -1;
		this.y = -1;
	}

	public String dirname() {return x + "-" + y;}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
