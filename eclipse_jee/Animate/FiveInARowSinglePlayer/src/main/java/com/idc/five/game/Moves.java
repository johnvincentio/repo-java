package com.idc.five.game;

import com.idc.five.output.Output;
import com.idc.five.players.Players;
import com.idc.five.utils.Utilities;

public class Moves {
	private static final int NO_MOVE = -1;

	private Players m_players;
	private Board m_board;

	private int m_currentPlayer = Players.PLAYER1; 	// The player who moves next.
	private int m_currentMove = 0; 					// Next move number.
	private int[][] m_moves;						// move number

	public Moves (Players players, Board board) {
		m_players = players;
		m_board = board;
		m_moves = new int[m_board.getRows()][m_board.getColumns()];
		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				m_moves[r][c] = NO_MOVE;
			}
		}
	}
	public int getCurrentPlayer() {return m_currentPlayer;}
	public int getCurrentMove() {return m_currentMove;}
	public boolean isMoveRemaining() {return m_currentMove < m_board.getRows() * m_board.getColumns();}

	public boolean isNoMove (int r, int c) {return m_moves[r][c] == NO_MOVE;}
	public int getMove (int r, int c) {return m_moves[r][c];}

	public void move (int r, int c) {
		assert m_board.isValidRow(r);
		assert m_board.isValidColumn(c);
		assert isMoveRemaining();
		m_moves[r][c] = m_currentMove;
		m_currentPlayer = Players.whoIsOtherPlayer (m_currentPlayer); // Flip players
		m_currentMove++; // Increment number of moves.
	}

	public void move (int player, int r, int c) {
		assert Players.isValidPlayer(player);
		assert m_board.isValidRow(r);
		assert m_board.isValidColumn(c);
		assert isMoveRemaining();
		m_moves[r][c] = m_currentMove;
		m_currentPlayer = Players.whoIsOtherPlayer (player); // Flip players
		m_currentMove++; // Increment number of moves.
	}

	public Coordinate undo() {
		Coordinate coordinate = null;
		if (m_currentMove > 0) {
			m_currentMove--;
			for (int r = 0; r < m_board.getRows(); r++) {
				for (int c = 0; c < m_board.getColumns(); c++) {
					if (m_moves[r][c] == m_currentMove) {
						m_moves[r][c] = NO_MOVE;
						m_currentPlayer = Players.whoIsOtherPlayer(m_currentPlayer);
						coordinate = new Coordinate (r, c);
					}
				}
			}
		}
		assert coordinate != null;
		return coordinate;
	}

	public Coordinate getLastMove() {
		Coordinate coordinate = null;
		if (m_currentMove > 0) {
			for (int r = 0; r < m_board.getRows(); r++) {
				for (int c = 0; c < m_board.getColumns(); c++) {
					if (m_moves[r][c] == m_currentMove - 1) {
						coordinate = new Coordinate (r, c);
					}
				}
			}
		}
		assert coordinate != null;
		return coordinate;
	}

	public Coordinate getMoveByMoveNumber (int num) {
		assert num >= 0;
		if (m_currentMove > 0) {
			for (int r = 0; r < m_board.getRows(); r++) {
				for (int c = 0; c < m_board.getColumns(); c++) {
					if (m_moves[r][c] == num) {
						return new Coordinate (r, c);
					}
				}
			}
		}
		return null;
	}

	public void showMoves (String msg, Output output) {
		output.println(">>> Show Moves; "+msg);
		output.println("Current Move "+getCurrentMove());
		output.println(m_players.getPlayerName(getCurrentPlayer())+" to play");
		for (int r = 0; r < m_board.getRows(); r++) {
			boolean first = true;
			for (int c = 0; c < m_board.getColumns(); c++) {
				if (! first) output.print(",");
				first = false;
				if (isNoMove(r, c))
					output.print("  ");
				else
					output.print(Utilities.leadingSpacesPad (getMove(r, c), 2));
			}
			output.print("\n");
		}
		output.print("\n");
		for (int move = 0; move < getCurrentMove(); move++) {
			for (int r = 0; r < m_board.getRows(); r++) {
				for (int c = 0; c < m_board.getColumns(); c++) {
					if (getMove(r, c) == move)
						output.println("Move "+move+" row "+r+" col "+c);
				}
			}
		}
		output.println("<<< Show Moves; "+msg);
	}
}
