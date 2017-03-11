package com.idc.five.junit.game;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.Board;
import com.idc.five.game.Game;
import com.idc.five.game.Moves;
import com.idc.five.players.Players;

public class Game3Test {
	Game m_game;
	Players m_players = new Players();
	Board m_board = new Board();
	Moves m_moves = new Moves(m_players, m_board);

	@Before
	public void initialize() {
		m_game = new Game (m_players, m_board, m_moves);
	}

	@Test
	public void testTie() {

		assertTrue(m_game.isMorePlay());
		assertFalse(m_game.isVictory(Players.PLAYER1));
		assertFalse(m_game.isVictory(Players.PLAYER2));
		assertFalse(m_game.isTie());

		for (int row = 0; row < m_board.getRows(); row++) {
			if ((row >= 0 && row <= 3) || row == 8) {
				for (int col = 1; col < m_board.getColumns(); ) {
					m_game.move (Players.PLAYER2, row, col);
					m_game.move (Players.PLAYER1, row, col + 1);
					col = col + 2;
				}
			}
			else {
				for (int col = 1; col < m_board.getColumns(); ) {
					m_game.move (Players.PLAYER1, row, col);
					m_game.move (Players.PLAYER2, row, col + 1);
					col = col + 2;
				}
			}
		}
		assertTrue(m_game.isMorePlay());
		assertFalse(m_game.isVictory(Players.PLAYER1));
		assertFalse(m_game.isVictory(Players.PLAYER2));
		assertFalse(m_game.isTie());

		m_game.move (Players.PLAYER1, 0, 0);
		m_game.move (Players.PLAYER2, 1, 0);
		m_game.move (Players.PLAYER1, 2, 0);
		m_game.move (Players.PLAYER2, 3, 0);
		m_game.move (Players.PLAYER2, 4, 0);
		m_game.move (Players.PLAYER1, 5, 0);
		m_game.move (Players.PLAYER2, 6, 0);
		m_game.move (Players.PLAYER1, 7, 0);
		m_game.move (Players.PLAYER1, 8, 0);

		assertFalse(m_game.isMorePlay());
		assertFalse(m_game.isVictory(Players.PLAYER1));
		assertFalse(m_game.isVictory(Players.PLAYER2));
		assertTrue(m_game.isTie());
	}
}
