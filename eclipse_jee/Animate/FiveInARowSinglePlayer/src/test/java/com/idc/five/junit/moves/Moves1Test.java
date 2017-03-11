package com.idc.five.junit.moves;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.Board;
import com.idc.five.game.Moves;
import com.idc.five.players.Players;

public class Moves1Test {

	Players m_players;
	Board m_board;
	Moves m_moves;

	@Before
	public void initialize() {
		m_players = new Players();
		m_board = new Board();
		m_moves = new Moves (m_players, m_board);
	}

	@Test
	public void test1() {
		assertEquals (m_moves.getCurrentPlayer(), Players.PLAYER1);
		assertEquals (m_moves.getCurrentMove(), 0);
		assertTrue (m_moves.isMoveRemaining());

		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				m_moves.move (r, c);
			}
		}
		assertEquals (m_moves.getCurrentPlayer(), Players.PLAYER2);
		assertEquals (m_moves.getCurrentMove(), m_board.getRows() * m_board.getColumns());
		assertFalse (m_moves.isMoveRemaining());
	}

	@Test
	public void test2() {
		m_moves.move (7, 6);
		assertEquals (m_moves.getCurrentPlayer(), Players.PLAYER2);
		assertEquals (m_moves.getCurrentMove(), 1);
		assertTrue (m_moves.isMoveRemaining());

		m_moves.move (5, 4);
		assertEquals (m_moves.getCurrentPlayer(), Players.PLAYER1);
		assertEquals (m_moves.getCurrentMove(), 2);
		assertTrue (m_moves.isMoveRemaining());

		m_moves.move (3, 2);
		assertEquals (m_moves.getCurrentPlayer(), Players.PLAYER2);
		assertEquals (m_moves.getCurrentMove(), 3);
		assertTrue (m_moves.isMoveRemaining());
	}

	@Test
	public void test3() {
		m_moves.move (Players.PLAYER2, 7, 6);
		assertEquals (m_moves.getCurrentPlayer(), Players.PLAYER1);
		assertEquals (m_moves.getCurrentMove(), 1);
		assertTrue (m_moves.isMoveRemaining());

		m_moves.move (Players.PLAYER2, 5, 4);
		assertEquals (m_moves.getCurrentPlayer(), Players.PLAYER1);
		assertEquals (m_moves.getCurrentMove(), 2);
		assertTrue (m_moves.isMoveRemaining());

		m_moves.move (Players.PLAYER2, 3, 2);
		assertEquals (m_moves.getCurrentPlayer(), Players.PLAYER1);
		assertEquals (m_moves.getCurrentMove(), 3);
		assertTrue (m_moves.isMoveRemaining());
	}

	@Test
	public void test4() {
		m_moves.move (7, 6);
		assertEquals (m_moves.getCurrentPlayer(), Players.PLAYER2);
		assertEquals (m_moves.getCurrentMove(), 1);
		assertTrue (m_moves.isMoveRemaining());

		m_moves.move (5, 4);
		assertEquals (m_moves.getCurrentPlayer(), Players.PLAYER1);
		assertEquals (m_moves.getCurrentMove(), 2);
		assertTrue (m_moves.isMoveRemaining());

		m_moves.undo ();
		assertEquals (m_moves.getCurrentPlayer(), Players.PLAYER2);
		assertEquals (m_moves.getCurrentMove(), 1);
		assertTrue (m_moves.isMoveRemaining());

		m_moves.undo ();
		assertEquals (m_moves.getCurrentPlayer(), Players.PLAYER1);
		assertEquals (m_moves.getCurrentMove(), 0);
		assertTrue (m_moves.isMoveRemaining());
	}
}
