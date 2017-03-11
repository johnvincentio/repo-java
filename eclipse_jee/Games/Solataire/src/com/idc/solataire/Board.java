package com.idc.solataire;

import com.idc.trace.Debug;

public class Board implements BoardInterface {
	private int[][] board = new int[MAXX][MAXY];

	public Board (int x, int y) throws GameException {
		for (int i = 0; i < MAXX; i++) {		 // default to illegal peg
			for (int j = 0; j < MAXY; j++)
				setBoard(i,j,ILLEGAL);
		}
		for (int j = 0; j < MAXY; j++) {
			for (int i = j; i < MAXX - j; i++, i++ ) {
				setBoard(i,j,OCCUPIED);
			}
		}
		setBoard(x,y,EMPTY);
	}
	public void setBoard(Peg peg, int Value) throws GameException {setBoard(peg.getX(),peg.getY(),Value);}
	public void setBoard (int x, int y, int Value) throws GameException {
		if (x<0 || x>=MAXX || y<0 || y>=MAXY) throw new GameException ("setBoard; bad x,y: ("+x+","+y+")");
		board[x][y] = Value;
	}
	public int getBoard(Peg peg) throws GameException {return getBoard(peg.getX(),peg.getY());}
	public int getBoard(int x, int y) throws GameException {
		if (x<0 || x>=MAXX || y<0 || y>=MAXY) throw new GameException ("getBoard; bad x,y: ("+x+","+y+")");
		return board[x][y];
	}
	public boolean isBoard(int x, int y, int Value) throws GameException {return (getBoard(x,y) == Value);}
	public boolean isCenterPegOccupied() throws GameException {return (getBoard(MAXX/2,MAXY/2) == OCCUPIED);}

	public void printBoard(String msg) throws GameException {
		Debug.println(">>> printBoard "+msg);
		for (int j=MAXY-1; j>-1; j--) {
			Debug.println();
			for (int i=0; i<MAXX; i++) {
				if (isBoard(i,j,ILLEGAL))
					Debug.print(". ");
				else
					Debug.print(getBoard(i,j) + " ");
			}
		}
		Debug.println();
		Debug.println("<<< printBoard");
	}
}
