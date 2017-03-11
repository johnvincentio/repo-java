package com.idc.five.scoring;

import com.idc.five.game.Board;
import com.idc.five.players.Players;

public class Scores {
	private Board m_board;

	private int[][][] m_scores;

	public Scores (Board board) {
		m_board = board;
		m_scores = new int[Players.PLAYER2][m_board.getRows()][m_board.getColumns()];
	}

	void incrementCount (int player, int row, int col, int count) {
		assert Players.isValidPlayer(player);
		assert m_board.isValidRow(row);
		assert m_board.isValidColumn(col);
		m_scores[player - 1][row][col] += count;
	}

	int getScore (int player, int row, int col) {
		assert Players.isValidPlayer(player);
		assert m_board.isValidRow(row);
		assert m_board.isValidColumn(col);
		return m_scores[player - 1][row][col];
	}
}
