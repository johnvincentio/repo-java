package com.idc.five.junit.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.Board;
import com.idc.five.game.Coordinate;
import com.idc.five.players.Players;

public class Board1Test {

	private Board m_board;

	@Before
	public void initialize() {
		m_board = new Board();
	}

	@Test
	public void test1() {
		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				assertTrue (m_board.isEmpty(r, c));
				assertFalse (m_board.isNotEmpty(r, c));
			}
		}
	}

	@Test
	public void test2() {
		m_board.setPlayer (Players.PLAYER1, 4, 3);
		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				if (r == 4 && c == 3) {
					assertFalse (m_board.isEmpty(r, c));
					assertTrue (m_board.isNotEmpty(r, c));
				}
				else {
					assertTrue (m_board.isEmpty(r, c));
					assertFalse (m_board.isNotEmpty(r, c));
				}
			}
		}
		assertEquals (m_board.getPlayerAt(4, 3), Players.PLAYER1);
	}

	@Test
	public void test3() {
		m_board.setPlayer (Players.PLAYER1, 4, 3);
		m_board.setPlayer (Players.PLAYER2, 6, 2);
		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				if ((r == 4 && c == 3) || (r == 6 && c == 2))
					assertFalse (m_board.isEmpty(r, c));
				else
					assertTrue (m_board.isEmpty(r, c));
			}
		}
		assertEquals (m_board.getPlayerAt(4, 3), Players.PLAYER1);
		assertEquals (m_board.getPlayerAt(6, 2), Players.PLAYER2);

		assertTrue (m_board.isPlayerAt (Players.PLAYER1, 4, 3));
		assertTrue (m_board.isPlayerAt (Players.PLAYER2, 6, 2));

		assertTrue (m_board.isOtherPlayerAt (Players.PLAYER2, 4, 3));
		assertTrue (m_board.isOtherPlayerAt (Players.PLAYER1, 6, 2));

		m_board.setEmpty(4, 3);
		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				if (r == 6 && c == 2)
					assertFalse (m_board.isEmpty(r, c));
				else
					assertTrue (m_board.isEmpty(r, c));
			}
		}

		Coordinate coordinate = new Coordinate (6, 2);
		m_board.setEmpty (coordinate);
		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				assertTrue (m_board.isEmpty(r, c));
			}
		}
	}
}
