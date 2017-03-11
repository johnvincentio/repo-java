package com.idc.five.junit.counting;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.counting.Counting;
import com.idc.five.counting.CountsItemInfo;
import com.idc.five.game.Board;
import com.idc.five.pattern.PatternUtils;
import com.idc.five.players.Players;

public class Counting2Test {
	private Players m_players;
	private Board m_board;
	private Counting m_counting;

	@Before
	public void initialize() {
		m_players = new Players();
		m_board = new Board();
	}

	@Test
	public void test1() {		// empty board
		m_counting = new Counting (m_players, m_board);
		for (int player = Players.PLAYER1; player <= Players.PLAYER2; player++) {
			for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
				for (int r = 0; r < m_board.getRows(); r++) {
					for (int c = 0; c < m_board.getColumns(); c++) {
						CountsItemInfo item = m_counting.getCounts (player, r, c, pattern);
						assertNotNull (item);
					}
				}
			}
		}
	}

	@Test
	public void test2() {				// occupy one square
		m_board.setPlayer(Players.PLAYER1, 3, 5);
		m_counting = new Counting (m_players, m_board);
		for (int player = Players.PLAYER1; player <= Players.PLAYER2; player++) {
			for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
				for (int r = 0; r < m_board.getRows(); r++) {
					for (int c = 0; c < m_board.getColumns(); c++) {
						CountsItemInfo item = m_counting.getCounts (player, r, c, pattern);
						if (r == 3 && c == 5)
							assertNull (item);
						else
							assertNotNull (item);
					}
				}
			}
		}
	}

	@Test
	public void test3() {				// occupy two squares
		m_board.setPlayer(Players.PLAYER1, 3, 5);
		m_board.setPlayer(Players.PLAYER2, 6, 4);

		m_counting = new Counting (m_players, m_board);
		for (int player = Players.PLAYER1; player <= Players.PLAYER2; player++) {
			for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
				for (int r = 0; r < m_board.getRows(); r++) {
					for (int c = 0; c < m_board.getColumns(); c++) {
						CountsItemInfo item = m_counting.getCounts (player, r, c, pattern);
						if ((r == 3 && c == 5) || (r == 6 && c == 4))
							assertNull (item);
						else
							assertNotNull (item);
					}
				}
			}
		}
	}
}
