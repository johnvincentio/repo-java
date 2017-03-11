
package com.idc.sudoku;

import com.idc.sudoku.gui.Boardgui;
import java.util.Iterator;

public class Brute {
	private Boardgui m_boardgui;
	private Patterns m_patterns;
	private Boards m_boards = new Boards();
	
	public Brute (Boardgui boardgui) {
		System.out.println(">>> Brute::constructor");
		m_boardgui = boardgui;
		m_boards.set (0,makeBoard());
		m_boards.show(0);
		m_patterns = Utils.makePatterns();
		System.out.println("<<< Brute::constructor");
	}
	private Board makeBoard() {
//		System.out.println(">>> makeBoard");
		Board board = new Board();
		int num;
		for (int row = Boardgui.getMinSquare(); row <= Boardgui.getMaxSquare(); row++) {
			for (int col = Boardgui.getMinSquare(); col <= Boardgui.getMaxSquare(); col++) {
				num = m_boardgui.getSquare(row,col).getIntSquareValue();
				board.set(col-1, row-1, num);
			}
		}
//		System.out.println("<<< makeBoard");
		return board;
	}

	public Board doBrute() {
		System.out.println(">>> Brute::doBrute");
		nextMove (0);
		System.out.println("Solution follows:");
		m_boards.show(Bucket.BUCKETS);
		System.out.println("<<< Brute::doBrute");
		return m_boards.getBoard(Bucket.BUCKETS);
	}
	private void nextMove (int row) {
		boolean bDebug = false;
//		if (row > 6) bDebug = true;
		if (bDebug) System.out.println(">>> Brute::nextMove; row "+row);

		Board currentBoard = m_boards.getBoard(row);
		Board nextBoard = m_boards.getBoard(row+1);
		if (bDebug) {
			System.out.println("Current Board:");
			currentBoard.show();
		}
		
		Pattern pattern;
		Iterator<Pattern> iter = m_patterns.getPatterns();
		while (iter.hasNext()) {
			pattern = iter.next();
			if (Utils.isPatternPossible (row, pattern, currentBoard)) {
//				if (bDebug) System.out.println("Possible pattern: "+pattern.toString());
				nextBoard.set(currentBoard);
				nextBoard.addPattern (row, pattern);
				if (Utils.isSituationPossible (row, pattern, currentBoard, nextBoard)) {
					if (bDebug) {
						System.out.println("New Next Board:");
						nextBoard.show();
					}
					if (row+1 >= Bucket.BUCKETS) break;
					nextMove (row+1);
				}
			}
		}
		if (bDebug) System.out.println("<<< Brute::nextMove; row "+row);
	}
}
