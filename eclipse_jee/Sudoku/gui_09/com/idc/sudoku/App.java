package com.idc.sudoku;

import java.util.Iterator;

import com.idc.trace.LogHelper;

public class App {
	private static final boolean m_bPrint = false;
	private Patterns m_patterns;
	private Boards m_boards = new Boards();
	private long m_victories = 0;

	public static void main(String[] args) {(new App()).doApp();}

	private void doApp() {
		LogHelper.info (">>> App::doApp");

		LogHelper.info (" "+LogHelper.timing ("Making the patterns"));
		m_patterns = Utils.makePatterns();
		LogHelper.info (LogHelper.timing ("Patterns have been made"));
		m_patterns.showNumber();

		LogHelper.info (LogHelper.timing ("Set the board"));
		int game_num = 0;
		m_boards.set (0,Setboard.getUserBoard(game_num));
		m_boards.show(0);

		LogHelper.info (LogHelper.timing ("Starting pattern matching"));
		nextMove (0);

		LogHelper.info("Total number of possible combinations "+m_victories);
		LogHelper.info (LogHelper.timing ("End of App"));
	}

	private void nextMove (int row) {
		if (row >= Bucket.BUCKETS) {
			showVictory();
			return;
		}
		Board currentBoard = m_boards.getBoard(row);
		Board nextBoard = m_boards.getBoard(row+1);
		
		Pattern pattern;
		Iterator<Pattern> iter = m_patterns.getPatterns();
		while (iter.hasNext()) {
			pattern = iter.next();
			nextBoard.set(currentBoard);
			nextBoard.addPattern (row, pattern);
			if (Utils.isSituationPossible (row, pattern, currentBoard, nextBoard)) nextMove (row+1);
		}
	}
	private void showVictory() {
		m_victories++;
		if (m_bPrint) {
			JVOutputFile out = JVOutputFile.getInstance();
			out.setFile("/tmp/sudoku/Victory_"+m_victories+".txt", false);
			out.timing("Victory "+m_victories);
			out.println("");
			for (int y=0; y<Bucket.BUCKETS; y++) {
				for (int x=0; x<Bucket.BUCKETS; x++)
					out.print(" "+m_boards.getBoard(Bucket.BUCKETS).get(x,y));
				out.println(" ");
			}
			out.timing("End of Victory");
			out.close();
		}
	}
}
