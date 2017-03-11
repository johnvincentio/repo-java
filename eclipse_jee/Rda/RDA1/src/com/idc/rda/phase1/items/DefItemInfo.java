package com.idc.rda.phase1.items;

import java.io.Serializable;

public class DefItemInfo implements Serializable {
	private static final long serialVersionUID = 7370314445978926803L;

	private String name;
	private int fromRow;
	private char fromCol;
	private int toRow;
	private char toCol;

	public DefItemInfo (String name, int fromRow, char fromCol, int toRow, char toCol) {
		this.name = name;
		this.fromRow = fromRow;
		this.fromCol = fromCol;
		this.toRow = toRow;
		this.toCol = toCol;
	}

	public String getName() {return name;}
	public int getFromRow() {return fromRow;}
	public char getFromCol() {return fromCol;}
	public int getToRow() {return toRow;}
	public char getToCol() {return toCol;}

	public String toString() {
		return "("+getName()+","+getFromRow()+","+getFromCol()+","+getToRow()+","+getToCol()+")";
	}
}
