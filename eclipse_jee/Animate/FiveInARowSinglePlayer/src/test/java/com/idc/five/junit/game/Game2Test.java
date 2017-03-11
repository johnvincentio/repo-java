package com.idc.five.junit.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.Board;
import com.idc.five.game.Coordinate;
import com.idc.five.game.Game;
import com.idc.five.game.Moves;
import com.idc.five.players.Players;

public class Game2Test {
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
		m_game.move (1, 5, 5);
		m_game.move (1, 5, 6);
		m_game.move (1, 6, 6);
		m_game.move (1, 8, 7);

		m_game.move (2, 4, 3);
		m_game.move (2, 5, 4);
		m_game.move (2, 6, 5);
		m_game.move (2, 7, 6);
		Coordinate coordinate = m_game.suggestMove();
		assertEquals (coordinate.getRow(), 3);
		assertEquals (coordinate.getCol(), 2);
	}

	@Test
	public void test2() {
		m_game.move (2, 0, 1);
		m_game.move (2, 0, 2);
		m_game.move (2, 1, 0);
		m_game.move (2, 2, 0);
		Coordinate coordinate = m_game.suggestMove();
		assertEquals (coordinate.getRow(), 0);
		assertEquals (coordinate.getCol(), 3);
	}

	@Test
	public void test3() {
		m_game.move (2, 0, 1);
		m_game.move (2, 0, 2);
		m_game.move (2, 0, 3);
		m_game.move (2, 1, 0);
		m_game.move (2, 2, 0);
		m_game.move (2, 3, 0);
		Coordinate coordinate = m_game.suggestMove();
		assertEquals (coordinate.getRow(), 0);
		assertEquals (coordinate.getCol(), 4);
	}
}
