package com.idc.five.game;

import com.idc.five.counting.Counting;
import com.idc.five.counting.CountsItemInfo;
import com.idc.five.pattern.PatternUtils;
import com.idc.five.players.Players;
import com.idc.five.scoring.Scoring;

public class Game {

	private Players m_players;
	private Board m_board;
	private Moves m_moves;

	private static final boolean MAKE_SILLY_MOVE = false;

	public static final int MORE_PLAY = 0;
	public static final int TIE = -1;

	private int m_status = MORE_PLAY;

	public Game() {
		m_players = new Players();
		m_board = new Board();
		m_moves = new Moves (m_players, m_board);
	}

	public Game (Players players, Board board, Moves moves) {
		m_players = players;
		m_board = board;
		m_moves = moves;
	}

	public int getCurrentPlayer() {return m_moves.getCurrentPlayer();}
	public Coordinate getLastMove() {return m_moves.getLastMove();}

	public void undo() {
		Coordinate coordinate = m_moves.undo();
		if (coordinate != null) m_board.setEmpty(coordinate);
		calculateStatus();
	}

	public void move (int player, Coordinate coordinate) {
		move (player, coordinate.getRow(), coordinate.getCol());
	}
	public void move (int player, int r, int c) {
		m_board.setPlayer (player, r, c); // Record this move.
		m_moves.move (player, r, c);
		calculateStatus();
	}

	public Coordinate suggestMove() {
//		System.out.println(">>> Game::suggestMove; current player is "+m_players.getPlayerName (getCurrentPlayer()));
		Scoring scoring = new Scoring (m_players, m_board, getCurrentPlayer());
		Coordinate coordinate = scoring.getHighestPossibleScoringMove();
//		System.out.println("<<< Game::suggestMove; Row "+coordinate.getRow()+" column "+coordinate.getCol());
		return coordinate;
	}

	public void makeAutoMove() {
		if (MAKE_SILLY_MOVE)
			makeAutoMoveSilly();
		else
			move (getCurrentPlayer(), suggestMove());
	}

	public void makeAutoMoveSilly() {
		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				if (m_board.isEmpty(r, c)) {
					move (getCurrentPlayer(), r, c);
					return;
				}
			}
		}
	}

	public boolean isMorePlay() {return m_status == MORE_PLAY;}
	public boolean isVictory (int player) {return m_status == player;}
	public boolean isTie() {return m_status == TIE;}

	public boolean isGameOver() {
		if (isVictory (Players.PLAYER1)) return true;
		if (isVictory (Players.PLAYER2)) return true;
		if (isTie()) return true;
		return false;
	}

	/**
	 * Calculate status of the game.
	 * 
	 * @return			status; MORE_PLAY = continue playing
	 * 							TIE = game is tied
	 * 							player number if victory
	 */
	private void calculateStatus() {
//		System.out.println(">>> Game::calculateStatus");
		if (! m_moves.isMoveRemaining()) {
//			System.out.println("found a tie game");
			m_status = Game.TIE;
			return;
		}

		Counting counting = new Counting (m_players, m_board, false);

		for (int row = 0; row < m_board.getRows(); row++) {
			assert m_board.isValidRow(row);
			for (int col = 0; col < m_board.getColumns(); col++) {
				assert m_board.isValidColumn(col);
				if (m_board.isEmpty (row, col)) continue;
				int player = m_board.getPlayerAt (row, col);
				assert Players.isValidPlayer(player);
				for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
					assert pattern >= 0 && pattern < PatternUtils.getNumberOfVictoryPatterns();
					CountsItemInfo counts = counting.getCounts (player, row, col, pattern);
					assert (counts != null);

					int num = counts.getActual();
					if (num >= 5) {
//						System.out.println("<<< Game::calculateStatus; Victory; player "+player+" row "+row+" col "+col+" pattern "+pattern);
						m_status = player;
						return;
					}
				}
			}
		}
		m_status = Game.MORE_PLAY;
//		System.out.println("<<< Game::calculateStatus; more play");
	}
}
