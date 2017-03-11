package com.idc.knight;

public class Square {

	private Pair from = new Pair();
	private int moveCounter = -1;
	private int moveAwayType = 0;

	public String toString() {
		return moveCounter + "(" + moveAwayType + ")" + "(" + from.toString() + ")";
	}
	public void init() {
		this.from.set();
		this.moveCounter = -1;
		this.moveAwayType = 0;		
	}
	public int getMoveCounter() {return moveCounter;}
	public void setMoveCounter (int moveCounter) {
		this.moveCounter = moveCounter;
	}

	public Pair getFrom() {return from;}
	public void setFrom (Pair from) {
		this.from.set (from);
	}

	public int getMoveAwayType() {return moveAwayType;}
	public void setMoveAwayType (int moveAwayType) {
		this.moveAwayType = moveAwayType;
	}

	public boolean isOccupied() {
		return moveCounter > -1;
	}
	public void checkValid() throws Exception {
		if (from.getX() < -1 || from.getY() < -1) throw new Exception ("Square.checkValid; from "+from.toString()+" is not valid");
		if (moveCounter < -1) throw new Exception ("Square.checkValid; from "+from.toString()+" moveCounter "+moveCounter+" is not valid");
		if (moveAwayType < 0 || moveAwayType > Board.getMoveTypesCount())
			throw new Exception ("Square.checkValid; from "+from.toString()+" moveAwayType "+moveAwayType+" is not valid");

		if (from.getX() < 0) {
			if (from.getY() > -1) throw new Exception ("Square.checkValid; from "+from.toString()+" is not valid");
			if (moveCounter > -1) throw new Exception ("Square.checkValid; from "+from.toString()+" moveCounter "+moveCounter+" is not valid");
			if (moveAwayType > 0) throw new Exception ("Square.checkValid; from "+from.toString()+" moveAwayType "+moveAwayType+" is not valid");
		}
		else {	// fromX >= 0
			if (from.getY() < 0) throw new Exception ("Square.checkValid; from "+from.toString()+" is not valid");
			if (moveCounter < 0) throw new Exception ("Square.checkValid; from "+from.toString()+" moveCounter "+moveCounter+" is not valid");
			if (moveAwayType < 0) throw new Exception ("Square.checkValid; from "+from.toString()+" moveAwayType "+moveAwayType+" is not valid");
		}
	}
}
