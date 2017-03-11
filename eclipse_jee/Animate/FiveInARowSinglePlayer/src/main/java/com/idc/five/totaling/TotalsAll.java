package com.idc.five.totaling;

import com.idc.five.game.Board;
import com.idc.five.output.Output;
import com.idc.five.pattern.PatternUtils;
import com.idc.five.players.Players;
import com.idc.five.utils.Utilities;

public class TotalsAll {

	private int[][][][] m_totalsAll;

	private Players m_players;
	private Board m_board;

	public TotalsAll (Players players, Board board) {
		m_players = players;
		m_board = board;
		m_totalsAll = new int[Players.PLAYER2][m_board.getRows()][m_board.getColumns()][PatternUtils.getNumberOfVictoryPatterns()];
	}

	public void setValue (int player, int row, int col, int pattern, int actual) {
//		System.out.println(">>> TotalsAll::setValue; player "+player+" pattern "+pattern);
		assert Players.isValidPlayer(player);
		assert pattern >= 0 && pattern < PatternUtils.getNumberOfVictoryPatterns();
		assert m_board.isValidRow(row);
		assert m_board.isValidColumn(col);
		assert actual >= 0;
		m_totalsAll[player - 1][row][col][pattern] = actual;
//		System.out.println("<<< TotalsAll::setValue");
	}

	public int getValue (int player, int row, int col, int pattern) {
//		System.out.println(">>> TotalsAll::getValue; player "+player+" pattern "+pattern);
		assert Players.isValidPlayer(player);
		assert pattern >= 0 && pattern < PatternUtils.getNumberOfVictoryPatterns();
		assert m_board.isValidRow(row);
		assert m_board.isValidColumn(col);
//		System.out.println("<<< TotalsAll::getValue");
		return m_totalsAll[player - 1][row][col][pattern];
	}

	public void showTotalsAll (String msg, Output output) {
		output.println(">>> TotalsAll::ShowTotalsAll; "+msg);
		for (int player = Players.PLAYER1; player <= Players.PLAYER2; player++) {
			output.println ("Counts for "+m_players.getPlayerName(player));
			for (int r = 0; r < m_board.getRows(); r++) {
				boolean first = true;
				for (int c = 0; c < m_board.getColumns(); c++) {
					if (! first) output.print (",  ");
					first = false;
					for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
						int actual = getValue (player, r, c, pattern);
						output.print (Utilities.leadingSpacesPad ("("+actual+")", 6));
					}
				}
				output.print("\n");
			}
		}
		output.println("<<< TotalsAll::ShowTotalsAll; "+msg);
	}
}
