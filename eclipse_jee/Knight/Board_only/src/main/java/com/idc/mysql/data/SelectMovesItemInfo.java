package com.idc.mysql.data;

import java.io.Serializable;

public class SelectMovesItemInfo implements Serializable {
	private static final long serialVersionUID = -6789606421440152807L;

	private long id;
	private long solutionNumber;
	private int moveCounter;
	private int xpos;
	private int ypos;
	private int moveAwaytype;
	private int fromXpos;
	private int fromYpos;

	public SelectMovesItemInfo (long id, long solutionNumber, int moveCounter, int xpos, int ypos, int moveAwaytype, int fromXpos, int fromYpos) {
		this.id = id;
		this.solutionNumber = solutionNumber;
		this.moveCounter = moveCounter;
		this.xpos = xpos;
		this.ypos = ypos;
		this.moveAwaytype = moveAwaytype;
		this.fromXpos = fromXpos;
		this.fromYpos = fromYpos;
	}

	public long getId() {return id;}
	public long getSolutionNumber() {return solutionNumber;}
	public int getMoveCounter() {return moveCounter;}
	public int getXpos() {return xpos;}
	public int getYpos() {return ypos;}
	public int getMoveAwaytype() {return moveAwaytype;}
	public int getFromXpos() {return fromXpos;}
	public int getFromYpos() {return fromYpos;}

	public String toString() {
		return "("+getId()+","+getSolutionNumber()+","+getMoveCounter()+","+getXpos()+","+getYpos()+","+getMoveAwaytype()+","+getFromXpos()+","+getFromYpos()+")";
	}
}
