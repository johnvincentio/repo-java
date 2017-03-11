package com.idc.solataire;

public class Peg {
	private int x;
	private int y;
	public Peg() {setXY(0,0);}
	public Peg(int x, int y) {setXY(x,y);}
	public Peg(Peg tPeg) {setXY(tPeg.getX(),tPeg.getY());}
	public void setXY(int x, int y) {setX(x); setY(y);}
	public void setX(int x) {this.x = x;}
	public void setY(int y) {this.y = y;}
	public int getX() {return x;}
	public int getY() {return y;}
}
