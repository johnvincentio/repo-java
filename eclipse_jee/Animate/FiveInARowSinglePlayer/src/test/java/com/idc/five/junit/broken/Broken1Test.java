package com.idc.five.junit.broken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.Board;
import com.idc.five.game.Coordinate;
import com.idc.five.game.Game;
import com.idc.five.game.Moves;
import com.idc.five.players.Players;

public class Broken1Test {

	Game m_game;
	Players m_players = new Players();
	Board m_board = new Board();
	Moves m_moves = new Moves(m_players, m_board);

	@Before
	public void initialize() {
		m_game = new Game (m_players, m_board, m_moves);
	}

	@Test
	public void test1() {
		Coordinate coordinate = m_game.suggestMove();	// white
		assertEquals (coordinate.getRow(), 4);
		assertEquals (coordinate.getCol(), 4);
		m_game.makeAutoMove();

		assertTrue(m_game.isMorePlay());
		assertFalse(m_game.isTie());
		assertFalse(m_game.isVictory(Players.PLAYER1));
		assertFalse(m_game.isVictory(Players.PLAYER2));
		assertFalse(m_game.isGameOver());

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 3);
		assertEquals (coordinate.getCol(), 3);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 3);
		assertEquals (coordinate.getCol(), 4);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 4);
		assertEquals (coordinate.getCol(), 3);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 5);
		assertEquals (coordinate.getCol(), 4);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 2);
		assertEquals (coordinate.getCol(), 4);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 6);
		assertEquals (coordinate.getCol(), 4);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 7);
		assertEquals (coordinate.getCol(), 4);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 5);
		assertEquals (coordinate.getCol(), 3);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 4);
		assertEquals (coordinate.getCol(), 2);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 1);
		assertEquals (coordinate.getCol(), 5);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 5);
		assertEquals (coordinate.getCol(), 1);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 6);
		assertEquals (coordinate.getCol(), 0);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 3);
		assertEquals (coordinate.getCol(), 5);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 5);
		assertEquals (coordinate.getCol(), 5);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 5);
		assertEquals (coordinate.getCol(), 6);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 4);
		assertEquals (coordinate.getCol(), 6);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 7);
		assertEquals (coordinate.getCol(), 3);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 3);
		assertEquals (coordinate.getCol(), 7);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 2);
		assertEquals (coordinate.getCol(), 8);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 2);
		assertEquals (coordinate.getCol(), 6);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 6);
		assertEquals (coordinate.getCol(), 5);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 4);
		assertEquals (coordinate.getCol(), 7);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 6);
		assertEquals (coordinate.getCol(), 2);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 4);
		assertEquals (coordinate.getCol(), 5);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 4);
		assertEquals (coordinate.getCol(), 8);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 3);
		assertEquals (coordinate.getCol(), 6);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 2);
		assertEquals (coordinate.getCol(), 7);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// white
		assertEquals (coordinate.getRow(), 1);
		assertEquals (coordinate.getCol(), 6);
		m_game.makeAutoMove();

		coordinate = m_game.suggestMove();				// black
		assertEquals (coordinate.getRow(), 0);
		assertEquals (coordinate.getCol(), 6);
		m_game.makeAutoMove();

// this move is fatal - should play 1, 4
// which is a 3 and a 4 with a gap of 1.
// this is a winning move.

		coordinate = m_game.suggestMove();				// white, not 6, 3
		assertEquals (coordinate.getRow(), 1);
		assertEquals (coordinate.getCol(), 4);
		m_game.makeAutoMove();


	}
}
