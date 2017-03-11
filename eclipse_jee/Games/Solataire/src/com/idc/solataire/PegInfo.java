package com.idc.solataire;

public class PegInfo extends Peg {
	private int move;
	public PegInfo(int x, int y, int move) {
		super (x,y);
		this.move = move;
	}
	public int getMove() {return move;}
}
