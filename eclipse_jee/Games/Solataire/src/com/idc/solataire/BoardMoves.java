package com.idc.solataire;

import com.idc.trace.Debug;

public class BoardMoves implements BoardInterface {
	private int[] adjacentX = {1, 2, 1, -1, -2, -1}; // moves 0-5
	private int[] adjacentY = {1, 0, -1, -1, 0, 1};
	private int[] jumperX = {2, 4, 2, -2, -4, -2};
	private int[] jumperY = {2, 0, -2, -2, 0, 2};
	private MovesTable mt;
	private Board board;

	public BoardMoves (int x, int y) throws GameException {
		board = new Board(x,y);
		mt = new MovesTable();
	}

	public boolean MakeValidMove(Peg peg, int next_move) throws GameException {
		int x1, y1, x2, y2, x3, y3;
		boolean bMoved = false;

		x1 = peg.getX(); //to peg
		y1 = peg.getY();
		Debug.println(">>> MakeValidMove; x,y,move (" + x1 + "," + y1 + "," + next_move + ")");
		for (int move=next_move; move<MAXMOVES; move++) {
			bMoved = false;
			if (! board.isBoard(x1,y1,EMPTY)) continue;
			x2 = x1 + adjacentX[move]; //adjacent peg
			y2 = y1 + adjacentY[move];
			x3 = x1 + jumperX[move]; // from peg
			y3 = y1 + jumperY[move];
			if (x2 < 0 || x2 >= MAXX) continue;
			if (y2 < 0 || y2 >= MAXY) continue;
			if (x3 < 0 || x3 >= MAXX) continue;
			if (y3 < 0 || y3 >= MAXY) continue;
			if (! board.isBoard(x2,y2,OCCUPIED)) continue;
			if (! board.isBoard(x3,y3,OCCUPIED)) continue;

			if (! mt.addMove(x1, y1, x2, y2, x3, y3, move)) continue;
			board.setBoard(x1,y1,OCCUPIED);
			board.setBoard(x2,y2,EMPTY);
			board.setBoard(x3,y3,EMPTY);
			mt.printMovesTable("Move");
			printBoard("Move");
			bMoved = true;
			break;
		}
		Debug.println("<<< MakeValidMove; Moved " + bMoved);
		return bMoved;
	}

	public Peg findNextEmptyPeg (Peg currentPeg) throws GameException {
		int x = currentPeg.getX() + 1; // start with the next peg position
		int y = currentPeg.getY();
		if (x >= MAXX) {x=0; y+=1;}
		Debug.println(">>> findNextEmptyPeg; x,y ("+x+","+y+")");
		Peg peg = new Peg(INVALID,INVALID);
		for (int j=y; j<MAXY; j++) {
			for (int i=x; i < MAXX; i++) {
//				Debug.println("x,y,status ("+i+","+j+"),"+board.getBoard(i,j));
				if (board.isBoard(i,j,EMPTY)) {
					peg.setXY(i,j);
					Debug.println("<<< findNextEmptyPeg; (Valid) x,y ("+peg.getX()+","+peg.getY()+")");
					return peg;
				}
				x = 0;
			}
		}
		Debug.println("<<< findNextEmptyPeg; (Bad) x,y ("+peg.getX()+","+peg.getY()+")");
		return peg;
	}

	public boolean isOkDeleteLastMove() {return mt.isOkDeleteLastMove();}
	public PegInfo deleteLastMove() throws GameException {
		mt.printMovesTable("Before Delete");
		printBoard("Before Delete");
		Peg pegFrom = mt.getFromPeg();
		Peg pegAdj = mt.getAdjPeg();
		Peg pegTo = mt.getToPeg();
		int currentMove = mt.getCurrentMoveNumber();
		board.setBoard(pegFrom,OCCUPIED);
		board.setBoard(pegAdj,OCCUPIED);
		board.setBoard(pegTo,EMPTY);
		mt.deleteLastMove();
		mt.printMovesTable("After Delete");
		printBoard("After Delete");
		return new PegInfo(pegTo.getX(),pegTo.getY(),currentMove);
	}
	public void printSolution() throws GameException {
		if (board.isCenterPegOccupied())
			Debug.setFile("/tmp/solution.txt", true);
		else
			Debug.setFile("/tmp/done.txt", true);
		printBoard("Done");
		printMovesTable("Done");
		Debug.setStatus(false);
	}

	public boolean isFinished() {return mt.isFinished();}
	public void printMovesTable (String msg) {mt.printMovesTable(msg);}
	public void printBoard(String msg) throws GameException {board.printBoard(msg);}
}
