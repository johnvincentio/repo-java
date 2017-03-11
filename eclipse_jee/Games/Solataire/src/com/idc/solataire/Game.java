package com.idc.solataire;

import com.idc.trace.Debug;

public class Game implements BoardInterface {
	private BoardMoves boardMoves;
	public Game(int x, int y) throws GameException {
		Debug.setFile("/tmp/debug.txt",false);
		Debug.setStatus(false);
		boardMoves = new BoardMoves(x,y);
		boardMoves.printBoard("Begin");
	}
	public void start(int x, int y) throws GameException {
		boolean bWork = true;
		PegInfo peginfo;
		int nextMove = 0;
		Peg currentPeg = new Peg(x, y);
		while (bWork) {
			Debug.println("---Main Loop");
			if (boardMoves.MakeValidMove (currentPeg,nextMove)) {
				if (boardMoves.isFinished()) {
					System.out.println("Found a solution");
					Debug.setStatus(true);
					boardMoves.printSolution();
					peginfo = boardMoves.deleteLastMove();
					currentPeg = new Peg(peginfo.getX(),peginfo.getY());
					nextMove = peginfo.getMove() + 1;
					continue; // bWork
//					break; // all done
				}
				else
					currentPeg.setXY(-1,0); // start behind first peg position
			}
			nextMove = 0;
			Peg nextPeg = boardMoves.findNextEmptyPeg(currentPeg);  // find next valid empty peg
			if (nextPeg.getX() == INVALID) { // back up last move and try again
				if (! boardMoves.isOkDeleteLastMove()) break; // cannot delete beyond the first move
				peginfo = boardMoves.deleteLastMove();
				currentPeg = new Peg(peginfo.getX(),peginfo.getY());
				nextMove = peginfo.getMove() + 1;
			}
			else
				currentPeg = new Peg(nextPeg.getX(),nextPeg.getY());
		} // while bwork
	}

	public static void main(String[] args) {
		int x = MAXX/2; int y = MAXY/2;
		try {
			Game game1 = new Game(x, y); // remove the center peg
			game1.start(x, y);
		}
		catch (GameException ge) {
			ge.printStackTrace();
			System.out.println (ge.toString());
			System.out.println ("Failed due to system error");
		}
	}
}
