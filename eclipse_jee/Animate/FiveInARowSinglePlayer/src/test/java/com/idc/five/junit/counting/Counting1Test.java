package com.idc.five.junit.counting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.counting.Counting;
import com.idc.five.counting.CountsItemInfo;
import com.idc.five.game.Board;
import com.idc.five.output.Output;
import com.idc.five.output.OutputTTY;
import com.idc.five.players.Players;

/*
Pattern 0;
	I
	I
	I

Pattern 1;
   -----

Pattern 2;
      /
     /
    /
Pattern 3;
    \
     \
      \
*/

public class Counting1Test {

	private Players m_players;
	private Board m_board;

	@Before
	public void initialize() {
		m_players = new Players();
		m_board = new Board();
	}

	@Test
	public void test0a() {		// empty
		Counting counting = new Counting (m_players, m_board);
		assertNotNull (counting);
//		Output output = new OutputTTY();
//		counting.showCountsInfo ("Counting1Test::test0a", output);
	}

	@Test
	public void test0b() {
		int player = Players.PLAYER1;
		m_board.setPlayer(player, 4, 4);
		Counting counting = new Counting (m_players, m_board);
		assertNotNull (counting);
		Output output = new OutputTTY();
		counting.showCountsInfo ("Counting1Test::test0b", output);
	}

	@Test
	public void test1() {		// occupied by same player
		int player = Players.PLAYER1;
		int pattern = 0;
		m_board.setPlayer(player, 4, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo item = counting.getCounts (player, 4, 4, pattern);
		assertNull (item);
	}

	@Test
	public void test1a() {		// occupied by other player
		int player = Players.PLAYER2;
		int pattern = 0;
		m_board.setPlayer(player, 4, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 4, 4, pattern);
		assertNull (counts);
	}

	@Test
	public void test2() {		// empty board
		int player = Players.PLAYER1;
		int pattern = 0;
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 4, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}

	@Test
	public void test2a() {		// ----w*---
		int player = Players.PLAYER1;
		int pattern = 0;
		m_board.setPlayer(player, 4, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 5, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 2);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}

	@Test
	public void test2b() {		// ---ww*---
		int player = Players.PLAYER1;
		int pattern = 0;
		m_board.setPlayer(player, 3, 4);
		m_board.setPlayer(player, 4, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 5, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 3);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}

	@Test
	public void test2c() {		// --www*---
		int player = Players.PLAYER1;
		int pattern = 0;
		m_board.setPlayer(player, 2, 4);
		m_board.setPlayer(player, 3, 4);
		m_board.setPlayer(player, 4, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 5, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 4);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test2d() {		// -wwww*---
		int player = Players.PLAYER1;
		int pattern = 0;
		m_board.setPlayer(player, 1, 4);
		m_board.setPlayer(player, 2, 4);
		m_board.setPlayer(player, 3, 4);
		m_board.setPlayer(player, 4, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 5, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 5);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test2e() {		// wwwww*---
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(player, 0, 4);
		m_board.setPlayer(player, 1, 4);
		m_board.setPlayer(player, 2, 4);
		m_board.setPlayer(player, 3, 4);
		m_board.setPlayer(player, 4, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 5, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 6);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test2f() {		// wwwww*w--
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(player, 0, 4);
		m_board.setPlayer(player, 1, 4);
		m_board.setPlayer(player, 2, 4);
		m_board.setPlayer(player, 3, 4);
		m_board.setPlayer(player, 4, 4);
		m_board.setPlayer(player, 6, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 5, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 7);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test2g() {		// wwwww*ww-
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(player, 0, 4);
		m_board.setPlayer(player, 1, 4);
		m_board.setPlayer(player, 2, 4);
		m_board.setPlayer(player, 3, 4);
		m_board.setPlayer(player, 4, 4);
		m_board.setPlayer(player, 6, 4);
		m_board.setPlayer(player, 7, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 5, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 8);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test2h() {		// wwwww*www
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(player, 0, 4);
		m_board.setPlayer(player, 1, 4);
		m_board.setPlayer(player, 2, 4);
		m_board.setPlayer(player, 3, 4);
		m_board.setPlayer(player, 4, 4);
		m_board.setPlayer(player, 6, 4);
		m_board.setPlayer(player, 7, 4);
		m_board.setPlayer(player, 8, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 5, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 9);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}

	public void test3a() {		// wwwww*www
		int player = Players.PLAYER1;
		int pattern = 1;

		m_board.setPlayer(player, 0, 4);
		m_board.setPlayer(player, 1, 4);
		m_board.setPlayer(player, 2, 4);
		m_board.setPlayer(player, 3, 4);
		m_board.setPlayer(player, 4, 4);
		m_board.setPlayer(player, 6, 4);
		m_board.setPlayer(player, 7, 4);
		m_board.setPlayer(player, 8, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 5, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}

	public void test3b() {		// wwwww*www
		int player = Players.PLAYER1;
		int pattern = 2;

		m_board.setPlayer(player, 0, 4);
		m_board.setPlayer(player, 1, 4);
		m_board.setPlayer(player, 2, 4);
		m_board.setPlayer(player, 3, 4);
		m_board.setPlayer(player, 4, 4);
		m_board.setPlayer(player, 6, 4);
		m_board.setPlayer(player, 7, 4);
		m_board.setPlayer(player, 8, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 5, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 8);
		assertFalse (counts.isBorder());
	}

	public void test3c() {		// wwwww*www
		int player = Players.PLAYER1;
		int pattern = 3;

		m_board.setPlayer(player, 0, 4);
		m_board.setPlayer(player, 1, 4);
		m_board.setPlayer(player, 2, 4);
		m_board.setPlayer(player, 3, 4);
		m_board.setPlayer(player, 4, 4);
		m_board.setPlayer(player, 6, 4);
		m_board.setPlayer(player, 7, 4);
		m_board.setPlayer(player, 8, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 5, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 8);
		assertFalse (counts.isBorder());
	}

	public void test4a() {		// *--------
		int player = Players.PLAYER1;
		int pattern = 0;

		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 0, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertTrue (counts.isBorder());
	}
	public void test4b() {		// --------*
		int player = Players.PLAYER1;
		int pattern = 0;

		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 8, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertTrue (counts.isBorder());
	}
	public void test4c() {		// *--------
		int player = Players.PLAYER1;
		int pattern = 0;

		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 0, 0, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertTrue (counts.isBorder());
	}
	public void test4d() {		// --------*
		int player = Players.PLAYER1;
		int pattern = 0;

		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 0, 8, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertTrue (counts.isBorder());
	}
	public void test4e() {		// --------*
		int player = Players.PLAYER1;
		int pattern = 0;

		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 8, 8, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertTrue (counts.isBorder());
	}
	public void test4f() {		// -*-------
		int player = Players.PLAYER1;
		int pattern = 0;

		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 1, 0, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test4g() {		// -*-------
		int player = Players.PLAYER1;
		int pattern = 0;

		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 1, 8, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test4h() {		// -*-------
		int player = Players.PLAYER1;
		int pattern = 0;

		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 7, 0, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test4j() {		// -*-------
		int player = Players.PLAYER1;
		int pattern = 0;

		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 7, 8, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}

	public void test5a() {		// --b*b----
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 2, 4);
		m_board.setPlayer(Players.PLAYER2, 4, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 3, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 1);
		assertTrue (counts.isBorder());
	}
	public void test5b() {		// --b*-b---
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 2, 4);
		m_board.setPlayer(Players.PLAYER2, 5, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 3, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 2);
		assertTrue (counts.isBorder());
	}
	public void test5b1() {		// --b-*b---
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 2, 4);
		m_board.setPlayer(Players.PLAYER2, 5, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 4, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 2);
		assertTrue (counts.isBorder());
	}
	public void test5c() {		// --b-*-b--
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 2, 4);
		m_board.setPlayer(Players.PLAYER2, 6, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 4, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 3);
		assertFalse (counts.isBorder());
	}
	public void test5d() {		// --b*--b--
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 2, 4);
		m_board.setPlayer(Players.PLAYER2, 6, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 3, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 3);
		assertTrue (counts.isBorder());
	}
	public void test5e() {		// --b*---b-
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 2, 4);
		m_board.setPlayer(Players.PLAYER2, 7, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 3, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 4);
		assertTrue (counts.isBorder());
	}
	public void test5f() {		// --b*----b
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 2, 4);
		m_board.setPlayer(Players.PLAYER2, 8, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 3, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 5);
		assertTrue (counts.isBorder());
	}
	public void test5g() {		// --b*-----
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 2, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 3, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 6);
		assertTrue (counts.isBorder());
	}
	public void test5h() {		// b*------b
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 0, 4);
		m_board.setPlayer(Players.PLAYER2, 8, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 1, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 7);
		assertTrue (counts.isBorder());
	}
	public void test5j() {		// b---*---b
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 0, 4);
		m_board.setPlayer(Players.PLAYER2, 8, 4);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 4, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 7);
		assertFalse (counts.isBorder());
	}
	public void test6a() {		// borders should not be relevant
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 4, 3);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 4, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test6b() {		// borders should not be relevant
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 4, 5);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 4, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test6c() {		// borders should not be relevant
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 3, 3);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 4, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test6d() {		// borders should not be relevant
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 3, 5);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 4, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test6e() {		// borders should not be relevant
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 5, 3);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 4, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
	public void test6f() {		// borders should not be relevant
		int player = Players.PLAYER1;
		int pattern = 0;

		m_board.setPlayer(Players.PLAYER2, 5, 5);
		Counting counting = new Counting (m_players, m_board);
		CountsItemInfo counts = counting.getCounts (player, 4, 4, pattern);
		assertNotNull (counts);
		assertEquals (counts.getActual(), 1);
		assertEquals (counts.getPossible(), 9);
		assertFalse (counts.isBorder());
	}
}
