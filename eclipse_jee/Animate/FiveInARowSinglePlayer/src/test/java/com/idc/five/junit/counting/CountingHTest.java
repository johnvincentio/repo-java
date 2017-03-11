package com.idc.five.junit.counting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.counting.Counting;
import com.idc.five.counting.CountsItemInfo;
import com.idc.five.game.Board;
import com.idc.five.players.Players;

public class CountingHTest {

	private Players m_players;
	private Board m_board;
	private Counting m_counting;

	@Before
	public void initialize() {
		m_players = new Players();
		m_board = new Board();
	}

	@Test
	public void test() {
		CountsItemInfo counts;
		m_board.setPlayer(Players.PLAYER1, 2, 2);
		m_board.setPlayer(Players.PLAYER1, 3, 3);
		m_board.setPlayer(Players.PLAYER1, 4, 3);
		m_board.setPlayer(Players.PLAYER1, 5, 3);
		m_board.setPlayer(Players.PLAYER1, 6, 5);
		m_board.setPlayer(Players.PLAYER1, 5, 6);
		m_board.setPlayer(Players.PLAYER1, 4, 7);
		m_board.setPlayer(Players.PLAYER1, 7, 8);
		m_board.setPlayer(Players.PLAYER2, 0, 0);
		m_board.setPlayer(Players.PLAYER2, 1, 0);
		m_board.setPlayer(Players.PLAYER2, 2, 0);
		m_board.setPlayer(Players.PLAYER2, 7, 1);
		m_board.setPlayer(Players.PLAYER2, 7, 2);
		m_board.setPlayer(Players.PLAYER2, 7, 3);
		m_board.setPlayer(Players.PLAYER2, 1, 3);
		m_board.setPlayer(Players.PLAYER2, 2, 4);
		m_board.setPlayer(Players.PLAYER2, 3, 5);
		m_board.setPlayer(Players.PLAYER2, 0, 7);
		m_board.setPlayer(Players.PLAYER2, 0, 8);
		m_counting = new Counting(m_players, m_board);

		counts = m_counting.getCounts(1, 0, 0, 0);
		assertTrue(m_board.isNotEmpty(0, 0));
		assertNull(counts);

		counts = m_counting.getCounts(1, 0, 0, 1);
		assertTrue(m_board.isNotEmpty(0, 0));
		assertNull(counts);

		counts = m_counting.getCounts(1, 0, 0, 2);
		assertTrue(m_board.isNotEmpty(0, 0));
		assertNull(counts);

		counts = m_counting.getCounts(1, 0, 0, 3);
		assertTrue(m_board.isNotEmpty(0, 0));
		assertNull(counts);

		counts = m_counting.getCounts(1, 0, 1, 0);
		assertTrue(m_board.isEmpty(0, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 1, 1);
		assertTrue(m_board.isEmpty(0, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 1, 2);
		assertTrue(m_board.isEmpty(0, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 1, 3);
		assertTrue(m_board.isEmpty(0, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 2, 0);
		assertTrue(m_board.isEmpty(0, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 2, 1);
		assertTrue(m_board.isEmpty(0, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 0, 2, 2);
		assertTrue(m_board.isEmpty(0, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 2, 3);
		assertTrue(m_board.isEmpty(0, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 3, 0);
		assertTrue(m_board.isEmpty(0, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 3, 1);
		assertTrue(m_board.isEmpty(0, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 0, 3, 2);
		assertTrue(m_board.isEmpty(0, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 3, 3);
		assertTrue(m_board.isEmpty(0, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 4, 0);
		assertTrue(m_board.isEmpty(0, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 4, 1);
		assertTrue(m_board.isEmpty(0, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 0, 4, 2);
		assertTrue(m_board.isEmpty(0, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 4, 3);
		assertTrue(m_board.isEmpty(0, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 5, 0);
		assertTrue(m_board.isEmpty(0, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 5, 1);
		assertTrue(m_board.isEmpty(0, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 0, 5, 2);
		assertTrue(m_board.isEmpty(0, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 5, 3);
		assertTrue(m_board.isEmpty(0, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 6, 0);
		assertTrue(m_board.isEmpty(0, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 6, 1);
		assertTrue(m_board.isEmpty(0, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 6, 2);
		assertTrue(m_board.isEmpty(0, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 6, 3);
		assertTrue(m_board.isEmpty(0, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 0, 7, 0);
		assertTrue(m_board.isNotEmpty(0, 7));
		assertNull(counts);

		counts = m_counting.getCounts(1, 0, 7, 1);
		assertTrue(m_board.isNotEmpty(0, 7));
		assertNull(counts);

		counts = m_counting.getCounts(1, 0, 7, 2);
		assertTrue(m_board.isNotEmpty(0, 7));
		assertNull(counts);

		counts = m_counting.getCounts(1, 0, 7, 3);
		assertTrue(m_board.isNotEmpty(0, 7));
		assertNull(counts);

		counts = m_counting.getCounts(1, 0, 8, 0);
		assertTrue(m_board.isNotEmpty(0, 8));
		assertNull(counts);

		counts = m_counting.getCounts(1, 0, 8, 1);
		assertTrue(m_board.isNotEmpty(0, 8));
		assertNull(counts);

		counts = m_counting.getCounts(1, 0, 8, 2);
		assertTrue(m_board.isNotEmpty(0, 8));
		assertNull(counts);

		counts = m_counting.getCounts(1, 0, 8, 3);
		assertTrue(m_board.isNotEmpty(0, 8));
		assertNull(counts);

		counts = m_counting.getCounts(1, 1, 0, 0);
		assertTrue(m_board.isNotEmpty(1, 0));
		assertNull(counts);

		counts = m_counting.getCounts(1, 1, 0, 1);
		assertTrue(m_board.isNotEmpty(1, 0));
		assertNull(counts);

		counts = m_counting.getCounts(1, 1, 0, 2);
		assertTrue(m_board.isNotEmpty(1, 0));
		assertNull(counts);

		counts = m_counting.getCounts(1, 1, 0, 3);
		assertTrue(m_board.isNotEmpty(1, 0));
		assertNull(counts);

		counts = m_counting.getCounts(1, 1, 1, 0);
		assertTrue(m_board.isEmpty(1, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 1, 1);
		assertTrue(m_board.isEmpty(1, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 1, 2);
		assertTrue(m_board.isEmpty(1, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 1, 3);
		assertTrue(m_board.isEmpty(1, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 3);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 2, 0);
		assertTrue(m_board.isEmpty(1, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 2, 1);
		assertTrue(m_board.isEmpty(1, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 2, 2);
		assertTrue(m_board.isEmpty(1, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 2, 3);
		assertTrue(m_board.isEmpty(1, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 3, 0);
		assertTrue(m_board.isNotEmpty(1, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 1, 3, 1);
		assertTrue(m_board.isNotEmpty(1, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 1, 3, 2);
		assertTrue(m_board.isNotEmpty(1, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 1, 3, 3);
		assertTrue(m_board.isNotEmpty(1, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 1, 4, 0);
		assertTrue(m_board.isEmpty(1, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 4, 1);
		assertTrue(m_board.isEmpty(1, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 4, 2);
		assertTrue(m_board.isEmpty(1, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 4, 3);
		assertTrue(m_board.isEmpty(1, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 5, 0);
		assertTrue(m_board.isEmpty(1, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 5, 1);
		assertTrue(m_board.isEmpty(1, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 5, 2);
		assertTrue(m_board.isEmpty(1, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 5, 3);
		assertTrue(m_board.isEmpty(1, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 6, 0);
		assertTrue(m_board.isEmpty(1, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 6, 1);
		assertTrue(m_board.isEmpty(1, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 6, 2);
		assertTrue(m_board.isEmpty(1, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 6, 3);
		assertTrue(m_board.isEmpty(1, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 7, 0);
		assertTrue(m_board.isEmpty(1, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 7, 1);
		assertTrue(m_board.isEmpty(1, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 7, 2);
		assertTrue(m_board.isEmpty(1, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 7, 3);
		assertTrue(m_board.isEmpty(1, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 1, 8, 0);
		assertTrue(m_board.isEmpty(1, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 8, 1);
		assertTrue(m_board.isEmpty(1, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 8, 2);
		assertTrue(m_board.isEmpty(1, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 1, 8, 3);
		assertTrue(m_board.isEmpty(1, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 2, 0, 0);
		assertTrue(m_board.isNotEmpty(2, 0));
		assertNull(counts);

		counts = m_counting.getCounts(1, 2, 0, 1);
		assertTrue(m_board.isNotEmpty(2, 0));
		assertNull(counts);

		counts = m_counting.getCounts(1, 2, 0, 2);
		assertTrue(m_board.isNotEmpty(2, 0));
		assertNull(counts);

		counts = m_counting.getCounts(1, 2, 0, 3);
		assertTrue(m_board.isNotEmpty(2, 0));
		assertNull(counts);

		counts = m_counting.getCounts(1, 2, 1, 0);
		assertTrue(m_board.isEmpty(2, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 1, 1);
		assertTrue(m_board.isEmpty(2, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 2, 1, 2);
		assertTrue(m_board.isEmpty(2, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 1, 3);
		assertTrue(m_board.isEmpty(2, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 2, 2, 0);
		assertTrue(m_board.isNotEmpty(2, 2));
		assertNull(counts);

		counts = m_counting.getCounts(1, 2, 2, 1);
		assertTrue(m_board.isNotEmpty(2, 2));
		assertNull(counts);

		counts = m_counting.getCounts(1, 2, 2, 2);
		assertTrue(m_board.isNotEmpty(2, 2));
		assertNull(counts);

		counts = m_counting.getCounts(1, 2, 2, 3);
		assertTrue(m_board.isNotEmpty(2, 2));
		assertNull(counts);

		counts = m_counting.getCounts(1, 2, 3, 0);
		assertTrue(m_board.isEmpty(2, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 4);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 2, 3, 1);
		assertTrue(m_board.isEmpty(2, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 2, 3, 2);
		assertTrue(m_board.isEmpty(2, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 3, 3);
		assertTrue(m_board.isEmpty(2, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 4, 0);
		assertTrue(m_board.isNotEmpty(2, 4));
		assertNull(counts);

		counts = m_counting.getCounts(1, 2, 4, 1);
		assertTrue(m_board.isNotEmpty(2, 4));
		assertNull(counts);

		counts = m_counting.getCounts(1, 2, 4, 2);
		assertTrue(m_board.isNotEmpty(2, 4));
		assertNull(counts);

		counts = m_counting.getCounts(1, 2, 4, 3);
		assertTrue(m_board.isNotEmpty(2, 4));
		assertNull(counts);

		counts = m_counting.getCounts(1, 2, 5, 0);
		assertTrue(m_board.isEmpty(2, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 2, 5, 1);
		assertTrue(m_board.isEmpty(2, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 2, 5, 2);
		assertTrue(m_board.isEmpty(2, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 5, 3);
		assertTrue(m_board.isEmpty(2, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 6, 0);
		assertTrue(m_board.isEmpty(2, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 6, 1);
		assertTrue(m_board.isEmpty(2, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 6, 2);
		assertTrue(m_board.isEmpty(2, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 2, 6, 3);
		assertTrue(m_board.isEmpty(2, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 7, 0);
		assertTrue(m_board.isEmpty(2, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 7, 1);
		assertTrue(m_board.isEmpty(2, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 7, 2);
		assertTrue(m_board.isEmpty(2, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 7, 3);
		assertTrue(m_board.isEmpty(2, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 8, 0);
		assertTrue(m_board.isEmpty(2, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 2, 8, 1);
		assertTrue(m_board.isEmpty(2, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 2, 8, 2);
		assertTrue(m_board.isEmpty(2, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 2, 8, 3);
		assertTrue(m_board.isEmpty(2, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 3, 0, 0);
		assertTrue(m_board.isEmpty(3, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 3, 0, 1);
		assertTrue(m_board.isEmpty(3, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 3, 0, 2);
		assertTrue(m_board.isEmpty(3, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 3, 0, 3);
		assertTrue(m_board.isEmpty(3, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 3, 1, 0);
		assertTrue(m_board.isEmpty(3, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 1, 1);
		assertTrue(m_board.isEmpty(3, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 1, 2);
		assertTrue(m_board.isEmpty(3, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 3, 1, 3);
		assertTrue(m_board.isEmpty(3, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 3, 2, 0);
		assertTrue(m_board.isEmpty(3, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 2, 1);
		assertTrue(m_board.isEmpty(3, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 2, 2);
		assertTrue(m_board.isEmpty(3, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 2, 3);
		assertTrue(m_board.isEmpty(3, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 3, 0);
		assertTrue(m_board.isNotEmpty(3, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 3, 3, 1);
		assertTrue(m_board.isNotEmpty(3, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 3, 3, 2);
		assertTrue(m_board.isNotEmpty(3, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 3, 3, 3);
		assertTrue(m_board.isNotEmpty(3, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 3, 4, 0);
		assertTrue(m_board.isEmpty(3, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 3, 4, 1);
		assertTrue(m_board.isEmpty(3, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 3, 4, 2);
		assertTrue(m_board.isEmpty(3, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 4, 3);
		assertTrue(m_board.isEmpty(3, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 5, 0);
		assertTrue(m_board.isNotEmpty(3, 5));
		assertNull(counts);

		counts = m_counting.getCounts(1, 3, 5, 1);
		assertTrue(m_board.isNotEmpty(3, 5));
		assertNull(counts);

		counts = m_counting.getCounts(1, 3, 5, 2);
		assertTrue(m_board.isNotEmpty(3, 5));
		assertNull(counts);

		counts = m_counting.getCounts(1, 3, 5, 3);
		assertTrue(m_board.isNotEmpty(3, 5));
		assertNull(counts);

		counts = m_counting.getCounts(1, 3, 6, 0);
		assertTrue(m_board.isEmpty(3, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 6, 1);
		assertTrue(m_board.isEmpty(3, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 3, 6, 2);
		assertTrue(m_board.isEmpty(3, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 6, 3);
		assertTrue(m_board.isEmpty(3, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 7, 0);
		assertTrue(m_board.isEmpty(3, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 7, 1);
		assertTrue(m_board.isEmpty(3, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 7, 2);
		assertTrue(m_board.isEmpty(3, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 7, 3);
		assertTrue(m_board.isEmpty(3, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 8, 0);
		assertTrue(m_board.isEmpty(3, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 3, 8, 1);
		assertTrue(m_board.isEmpty(3, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 3, 8, 2);
		assertTrue(m_board.isEmpty(3, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 4);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 3, 8, 3);
		assertTrue(m_board.isEmpty(3, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 4, 0, 0);
		assertTrue(m_board.isEmpty(4, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 0, 1);
		assertTrue(m_board.isEmpty(4, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 4, 0, 2);
		assertTrue(m_board.isEmpty(4, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 4, 0, 3);
		assertTrue(m_board.isEmpty(4, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 4, 1, 0);
		assertTrue(m_board.isEmpty(4, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 1, 1);
		assertTrue(m_board.isEmpty(4, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 1, 2);
		assertTrue(m_board.isEmpty(4, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 1, 3);
		assertTrue(m_board.isEmpty(4, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 2, 0);
		assertTrue(m_board.isEmpty(4, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 2, 1);
		assertTrue(m_board.isEmpty(4, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 2, 2);
		assertTrue(m_board.isEmpty(4, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 4, 2, 3);
		assertTrue(m_board.isEmpty(4, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 3, 0);
		assertTrue(m_board.isNotEmpty(4, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 4, 3, 1);
		assertTrue(m_board.isNotEmpty(4, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 4, 3, 2);
		assertTrue(m_board.isNotEmpty(4, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 4, 3, 3);
		assertTrue(m_board.isNotEmpty(4, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 4, 4, 0);
		assertTrue(m_board.isEmpty(4, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 4, 1);
		assertTrue(m_board.isEmpty(4, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 4, 2);
		assertTrue(m_board.isEmpty(4, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 4, 4, 3);
		assertTrue(m_board.isEmpty(4, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 3);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 5, 0);
		assertTrue(m_board.isEmpty(4, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 4, 5, 1);
		assertTrue(m_board.isEmpty(4, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 5, 2);
		assertTrue(m_board.isEmpty(4, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 5, 3);
		assertTrue(m_board.isEmpty(4, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 6, 0);
		assertTrue(m_board.isEmpty(4, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 6, 1);
		assertTrue(m_board.isEmpty(4, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 6, 2);
		assertTrue(m_board.isEmpty(4, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 6, 3);
		assertTrue(m_board.isEmpty(4, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 4, 7, 0);
		assertTrue(m_board.isNotEmpty(4, 7));
		assertNull(counts);

		counts = m_counting.getCounts(1, 4, 7, 1);
		assertTrue(m_board.isNotEmpty(4, 7));
		assertNull(counts);

		counts = m_counting.getCounts(1, 4, 7, 2);
		assertTrue(m_board.isNotEmpty(4, 7));
		assertNull(counts);

		counts = m_counting.getCounts(1, 4, 7, 3);
		assertTrue(m_board.isNotEmpty(4, 7));
		assertNull(counts);

		counts = m_counting.getCounts(1, 4, 8, 0);
		assertTrue(m_board.isEmpty(4, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 4, 8, 1);
		assertTrue(m_board.isEmpty(4, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 4, 8, 2);
		assertTrue(m_board.isEmpty(4, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 4, 8, 3);
		assertTrue(m_board.isEmpty(4, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 5, 0, 0);
		assertTrue(m_board.isEmpty(5, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 0, 1);
		assertTrue(m_board.isEmpty(5, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 5, 0, 2);
		assertTrue(m_board.isEmpty(5, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 5, 0, 3);
		assertTrue(m_board.isEmpty(5, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 5, 1, 0);
		assertTrue(m_board.isEmpty(5, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 1, 1);
		assertTrue(m_board.isEmpty(5, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 1, 2);
		assertTrue(m_board.isEmpty(5, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 1, 3);
		assertTrue(m_board.isEmpty(5, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 2, 0);
		assertTrue(m_board.isEmpty(5, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 2, 1);
		assertTrue(m_board.isEmpty(5, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 2, 2);
		assertTrue(m_board.isEmpty(5, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 2, 3);
		assertTrue(m_board.isEmpty(5, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 3, 0);
		assertTrue(m_board.isNotEmpty(5, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 5, 3, 1);
		assertTrue(m_board.isNotEmpty(5, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 5, 3, 2);
		assertTrue(m_board.isNotEmpty(5, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 5, 3, 3);
		assertTrue(m_board.isNotEmpty(5, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 5, 4, 0);
		assertTrue(m_board.isEmpty(5, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 4, 1);
		assertTrue(m_board.isEmpty(5, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 4, 2);
		assertTrue(m_board.isEmpty(5, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 4, 3);
		assertTrue(m_board.isEmpty(5, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 3);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 5, 0);
		assertTrue(m_board.isEmpty(5, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 5, 1);
		assertTrue(m_board.isEmpty(5, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 5, 2);
		assertTrue(m_board.isEmpty(5, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 5, 3);
		assertTrue(m_board.isEmpty(5, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 6, 0);
		assertTrue(m_board.isNotEmpty(5, 6));
		assertNull(counts);

		counts = m_counting.getCounts(1, 5, 6, 1);
		assertTrue(m_board.isNotEmpty(5, 6));
		assertNull(counts);

		counts = m_counting.getCounts(1, 5, 6, 2);
		assertTrue(m_board.isNotEmpty(5, 6));
		assertNull(counts);

		counts = m_counting.getCounts(1, 5, 6, 3);
		assertTrue(m_board.isNotEmpty(5, 6));
		assertNull(counts);

		counts = m_counting.getCounts(1, 5, 7, 0);
		assertTrue(m_board.isEmpty(5, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 7, 1);
		assertTrue(m_board.isEmpty(5, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 7, 2);
		assertTrue(m_board.isEmpty(5, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 7, 3);
		assertTrue(m_board.isEmpty(5, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 8, 0);
		assertTrue(m_board.isEmpty(5, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 5, 8, 1);
		assertTrue(m_board.isEmpty(5, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 5, 8, 2);
		assertTrue(m_board.isEmpty(5, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 5, 8, 3);
		assertTrue(m_board.isEmpty(5, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 0, 0);
		assertTrue(m_board.isEmpty(6, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 0, 1);
		assertTrue(m_board.isEmpty(6, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 0, 2);
		assertTrue(m_board.isEmpty(6, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 0, 3);
		assertTrue(m_board.isEmpty(6, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 1, 0);
		assertTrue(m_board.isEmpty(6, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 1, 1);
		assertTrue(m_board.isEmpty(6, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 1, 2);
		assertTrue(m_board.isEmpty(6, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 1, 3);
		assertTrue(m_board.isEmpty(6, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 2, 0);
		assertTrue(m_board.isEmpty(6, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 2, 1);
		assertTrue(m_board.isEmpty(6, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 2, 2);
		assertTrue(m_board.isEmpty(6, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 2, 3);
		assertTrue(m_board.isEmpty(6, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 3, 0);
		assertTrue(m_board.isEmpty(6, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 4);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 3, 1);
		assertTrue(m_board.isEmpty(6, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 3, 2);
		assertTrue(m_board.isEmpty(6, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 3, 3);
		assertTrue(m_board.isEmpty(6, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 4, 0);
		assertTrue(m_board.isEmpty(6, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 4, 1);
		assertTrue(m_board.isEmpty(6, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 4, 2);
		assertTrue(m_board.isEmpty(6, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 4, 3);
		assertTrue(m_board.isEmpty(6, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 5, 0);
		assertTrue(m_board.isNotEmpty(6, 5));
		assertNull(counts);

		counts = m_counting.getCounts(1, 6, 5, 1);
		assertTrue(m_board.isNotEmpty(6, 5));
		assertNull(counts);

		counts = m_counting.getCounts(1, 6, 5, 2);
		assertTrue(m_board.isNotEmpty(6, 5));
		assertNull(counts);

		counts = m_counting.getCounts(1, 6, 5, 3);
		assertTrue(m_board.isNotEmpty(6, 5));
		assertNull(counts);

		counts = m_counting.getCounts(1, 6, 6, 0);
		assertTrue(m_board.isEmpty(6, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 6, 1);
		assertTrue(m_board.isEmpty(6, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 6, 2);
		assertTrue(m_board.isEmpty(6, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 6, 3);
		assertTrue(m_board.isEmpty(6, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 7, 0);
		assertTrue(m_board.isEmpty(6, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 7, 1);
		assertTrue(m_board.isEmpty(6, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 7, 2);
		assertTrue(m_board.isEmpty(6, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 7, 3);
		assertTrue(m_board.isEmpty(6, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 3);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 8, 0);
		assertTrue(m_board.isEmpty(6, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 6, 8, 1);
		assertTrue(m_board.isEmpty(6, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 8, 2);
		assertTrue(m_board.isEmpty(6, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 6, 8, 3);
		assertTrue(m_board.isEmpty(6, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 7, 0, 0);
		assertTrue(m_board.isEmpty(7, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 0, 1);
		assertTrue(m_board.isEmpty(7, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 7, 0, 2);
		assertTrue(m_board.isEmpty(7, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 7, 0, 3);
		assertTrue(m_board.isEmpty(7, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 7, 1, 0);
		assertTrue(m_board.isNotEmpty(7, 1));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 1, 1);
		assertTrue(m_board.isNotEmpty(7, 1));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 1, 2);
		assertTrue(m_board.isNotEmpty(7, 1));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 1, 3);
		assertTrue(m_board.isNotEmpty(7, 1));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 2, 0);
		assertTrue(m_board.isNotEmpty(7, 2));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 2, 1);
		assertTrue(m_board.isNotEmpty(7, 2));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 2, 2);
		assertTrue(m_board.isNotEmpty(7, 2));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 2, 3);
		assertTrue(m_board.isNotEmpty(7, 2));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 3, 0);
		assertTrue(m_board.isNotEmpty(7, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 3, 1);
		assertTrue(m_board.isNotEmpty(7, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 3, 2);
		assertTrue(m_board.isNotEmpty(7, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 3, 3);
		assertTrue(m_board.isNotEmpty(7, 3));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 4, 0);
		assertTrue(m_board.isEmpty(7, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 4, 1);
		assertTrue(m_board.isEmpty(7, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 7, 4, 2);
		assertTrue(m_board.isEmpty(7, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 4);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 4, 3);
		assertTrue(m_board.isEmpty(7, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 5, 0);
		assertTrue(m_board.isEmpty(7, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 5, 1);
		assertTrue(m_board.isEmpty(7, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 5, 2);
		assertTrue(m_board.isEmpty(7, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 5, 3);
		assertTrue(m_board.isEmpty(7, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 6, 0);
		assertTrue(m_board.isEmpty(7, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 6, 1);
		assertTrue(m_board.isEmpty(7, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 6, 2);
		assertTrue(m_board.isEmpty(7, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 6, 3);
		assertTrue(m_board.isEmpty(7, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 7, 0);
		assertTrue(m_board.isEmpty(7, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 7, 1);
		assertTrue(m_board.isEmpty(7, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 7, 7, 2);
		assertTrue(m_board.isEmpty(7, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 7, 3);
		assertTrue(m_board.isEmpty(7, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 7, 8, 0);
		assertTrue(m_board.isNotEmpty(7, 8));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 8, 1);
		assertTrue(m_board.isNotEmpty(7, 8));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 8, 2);
		assertTrue(m_board.isNotEmpty(7, 8));
		assertNull(counts);

		counts = m_counting.getCounts(1, 7, 8, 3);
		assertTrue(m_board.isNotEmpty(7, 8));
		assertNull(counts);

		counts = m_counting.getCounts(1, 8, 0, 0);
		assertTrue(m_board.isEmpty(8, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 0, 1);
		assertTrue(m_board.isEmpty(8, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 0, 2);
		assertTrue(m_board.isEmpty(8, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 0, 3);
		assertTrue(m_board.isEmpty(8, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 1, 0);
		assertTrue(m_board.isEmpty(8, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 1, 1);
		assertTrue(m_board.isEmpty(8, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 8, 1, 2);
		assertTrue(m_board.isEmpty(8, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 1, 3);
		assertTrue(m_board.isEmpty(8, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 2, 0);
		assertTrue(m_board.isEmpty(8, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 2, 1);
		assertTrue(m_board.isEmpty(8, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 8, 2, 2);
		assertTrue(m_board.isEmpty(8, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 2, 3);
		assertTrue(m_board.isEmpty(8, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 3, 0);
		assertTrue(m_board.isEmpty(8, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 3, 1);
		assertTrue(m_board.isEmpty(8, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 8, 3, 2);
		assertTrue(m_board.isEmpty(8, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 3, 3);
		assertTrue(m_board.isEmpty(8, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 4, 0);
		assertTrue(m_board.isEmpty(8, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 4, 1);
		assertTrue(m_board.isEmpty(8, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 8, 4, 2);
		assertTrue(m_board.isEmpty(8, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 4, 3);
		assertTrue(m_board.isEmpty(8, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 5, 0);
		assertTrue(m_board.isEmpty(8, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 5, 1);
		assertTrue(m_board.isEmpty(8, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 8, 5, 2);
		assertTrue(m_board.isEmpty(8, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 5, 3);
		assertTrue(m_board.isEmpty(8, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 6, 0);
		assertTrue(m_board.isEmpty(8, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 6, 1);
		assertTrue(m_board.isEmpty(8, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 8, 6, 2);
		assertTrue(m_board.isEmpty(8, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 6, 3);
		assertTrue(m_board.isEmpty(8, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 7, 0);
		assertTrue(m_board.isEmpty(8, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 7, 1);
		assertTrue(m_board.isEmpty(8, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(1, 8, 7, 2);
		assertTrue(m_board.isEmpty(8, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 7, 3);
		assertTrue(m_board.isEmpty(8, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 8, 0);
		assertTrue(m_board.isEmpty(8, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 8, 1);
		assertTrue(m_board.isEmpty(8, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 8, 2);
		assertTrue(m_board.isEmpty(8, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(1, 8, 8, 3);
		assertTrue(m_board.isEmpty(8, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 0, 0);
		assertTrue(m_board.isNotEmpty(0, 0));
		assertNull(counts);

		counts = m_counting.getCounts(2, 0, 0, 1);
		assertTrue(m_board.isNotEmpty(0, 0));
		assertNull(counts);

		counts = m_counting.getCounts(2, 0, 0, 2);
		assertTrue(m_board.isNotEmpty(0, 0));
		assertNull(counts);

		counts = m_counting.getCounts(2, 0, 0, 3);
		assertTrue(m_board.isNotEmpty(0, 0));
		assertNull(counts);

		counts = m_counting.getCounts(2, 0, 1, 0);
		assertTrue(m_board.isEmpty(0, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 1, 1);
		assertTrue(m_board.isEmpty(0, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 1, 2);
		assertTrue(m_board.isEmpty(0, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 1, 3);
		assertTrue(m_board.isEmpty(0, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 2, 0);
		assertTrue(m_board.isEmpty(0, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 2, 1);
		assertTrue(m_board.isEmpty(0, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 0, 2, 2);
		assertTrue(m_board.isEmpty(0, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 2, 3);
		assertTrue(m_board.isEmpty(0, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 4);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 3, 0);
		assertTrue(m_board.isEmpty(0, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 3, 1);
		assertTrue(m_board.isEmpty(0, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 0, 3, 2);
		assertTrue(m_board.isEmpty(0, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 3, 3);
		assertTrue(m_board.isEmpty(0, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 4, 0);
		assertTrue(m_board.isEmpty(0, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 4, 1);
		assertTrue(m_board.isEmpty(0, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 0, 4, 2);
		assertTrue(m_board.isEmpty(0, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 4, 3);
		assertTrue(m_board.isEmpty(0, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 5, 0);
		assertTrue(m_board.isEmpty(0, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 5, 1);
		assertTrue(m_board.isEmpty(0, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 0, 5, 2);
		assertTrue(m_board.isEmpty(0, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 5, 3);
		assertTrue(m_board.isEmpty(0, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 6, 0);
		assertTrue(m_board.isEmpty(0, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 6, 1);
		assertTrue(m_board.isEmpty(0, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 3);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 6, 2);
		assertTrue(m_board.isEmpty(0, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 6, 3);
		assertTrue(m_board.isEmpty(0, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 0, 7, 0);
		assertTrue(m_board.isNotEmpty(0, 7));
		assertNull(counts);

		counts = m_counting.getCounts(2, 0, 7, 1);
		assertTrue(m_board.isNotEmpty(0, 7));
		assertNull(counts);

		counts = m_counting.getCounts(2, 0, 7, 2);
		assertTrue(m_board.isNotEmpty(0, 7));
		assertNull(counts);

		counts = m_counting.getCounts(2, 0, 7, 3);
		assertTrue(m_board.isNotEmpty(0, 7));
		assertNull(counts);

		counts = m_counting.getCounts(2, 0, 8, 0);
		assertTrue(m_board.isNotEmpty(0, 8));
		assertNull(counts);

		counts = m_counting.getCounts(2, 0, 8, 1);
		assertTrue(m_board.isNotEmpty(0, 8));
		assertNull(counts);

		counts = m_counting.getCounts(2, 0, 8, 2);
		assertTrue(m_board.isNotEmpty(0, 8));
		assertNull(counts);

		counts = m_counting.getCounts(2, 0, 8, 3);
		assertTrue(m_board.isNotEmpty(0, 8));
		assertNull(counts);

		counts = m_counting.getCounts(2, 1, 0, 0);
		assertTrue(m_board.isNotEmpty(1, 0));
		assertNull(counts);

		counts = m_counting.getCounts(2, 1, 0, 1);
		assertTrue(m_board.isNotEmpty(1, 0));
		assertNull(counts);

		counts = m_counting.getCounts(2, 1, 0, 2);
		assertTrue(m_board.isNotEmpty(1, 0));
		assertNull(counts);

		counts = m_counting.getCounts(2, 1, 0, 3);
		assertTrue(m_board.isNotEmpty(1, 0));
		assertNull(counts);

		counts = m_counting.getCounts(2, 1, 1, 0);
		assertTrue(m_board.isEmpty(1, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 1, 1);
		assertTrue(m_board.isEmpty(1, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 1, 1, 2);
		assertTrue(m_board.isEmpty(1, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 1, 1, 3);
		assertTrue(m_board.isEmpty(1, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 1, 2, 0);
		assertTrue(m_board.isEmpty(1, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 1, 2, 1);
		assertTrue(m_board.isEmpty(1, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 2, 2);
		assertTrue(m_board.isEmpty(1, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 2, 3);
		assertTrue(m_board.isEmpty(1, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 3, 0);
		assertTrue(m_board.isNotEmpty(1, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 1, 3, 1);
		assertTrue(m_board.isNotEmpty(1, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 1, 3, 2);
		assertTrue(m_board.isNotEmpty(1, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 1, 3, 3);
		assertTrue(m_board.isNotEmpty(1, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 1, 4, 0);
		assertTrue(m_board.isEmpty(1, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 4, 1);
		assertTrue(m_board.isEmpty(1, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 4, 2);
		assertTrue(m_board.isEmpty(1, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 4, 3);
		assertTrue(m_board.isEmpty(1, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 5, 0);
		assertTrue(m_board.isEmpty(1, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 5, 1);
		assertTrue(m_board.isEmpty(1, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 5, 2);
		assertTrue(m_board.isEmpty(1, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 1, 5, 3);
		assertTrue(m_board.isEmpty(1, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 6, 0);
		assertTrue(m_board.isEmpty(1, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 6, 1);
		assertTrue(m_board.isEmpty(1, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 6, 2);
		assertTrue(m_board.isEmpty(1, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 1, 6, 3);
		assertTrue(m_board.isEmpty(1, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 7, 0);
		assertTrue(m_board.isEmpty(1, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 1, 7, 1);
		assertTrue(m_board.isEmpty(1, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 7, 2);
		assertTrue(m_board.isEmpty(1, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 1, 7, 3);
		assertTrue(m_board.isEmpty(1, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 1, 8, 0);
		assertTrue(m_board.isEmpty(1, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 1, 8, 1);
		assertTrue(m_board.isEmpty(1, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 1, 8, 2);
		assertTrue(m_board.isEmpty(1, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 1, 8, 3);
		assertTrue(m_board.isEmpty(1, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 2, 0, 0);
		assertTrue(m_board.isNotEmpty(2, 0));
		assertNull(counts);

		counts = m_counting.getCounts(2, 2, 0, 1);
		assertTrue(m_board.isNotEmpty(2, 0));
		assertNull(counts);

		counts = m_counting.getCounts(2, 2, 0, 2);
		assertTrue(m_board.isNotEmpty(2, 0));
		assertNull(counts);

		counts = m_counting.getCounts(2, 2, 0, 3);
		assertTrue(m_board.isNotEmpty(2, 0));
		assertNull(counts);

		counts = m_counting.getCounts(2, 2, 1, 0);
		assertTrue(m_board.isEmpty(2, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 1, 1);
		assertTrue(m_board.isEmpty(2, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 2, 1, 2);
		assertTrue(m_board.isEmpty(2, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 1, 3);
		assertTrue(m_board.isEmpty(2, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 2, 2, 0);
		assertTrue(m_board.isNotEmpty(2, 2));
		assertNull(counts);

		counts = m_counting.getCounts(2, 2, 2, 1);
		assertTrue(m_board.isNotEmpty(2, 2));
		assertNull(counts);

		counts = m_counting.getCounts(2, 2, 2, 2);
		assertTrue(m_board.isNotEmpty(2, 2));
		assertNull(counts);

		counts = m_counting.getCounts(2, 2, 2, 3);
		assertTrue(m_board.isNotEmpty(2, 2));
		assertNull(counts);

		counts = m_counting.getCounts(2, 2, 3, 0);
		assertTrue(m_board.isEmpty(2, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 2, 3, 1);
		assertTrue(m_board.isEmpty(2, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 2, 3, 2);
		assertTrue(m_board.isEmpty(2, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 3, 3);
		assertTrue(m_board.isEmpty(2, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 4, 0);
		assertTrue(m_board.isNotEmpty(2, 4));
		assertNull(counts);

		counts = m_counting.getCounts(2, 2, 4, 1);
		assertTrue(m_board.isNotEmpty(2, 4));
		assertNull(counts);

		counts = m_counting.getCounts(2, 2, 4, 2);
		assertTrue(m_board.isNotEmpty(2, 4));
		assertNull(counts);

		counts = m_counting.getCounts(2, 2, 4, 3);
		assertTrue(m_board.isNotEmpty(2, 4));
		assertNull(counts);

		counts = m_counting.getCounts(2, 2, 5, 0);
		assertTrue(m_board.isEmpty(2, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 5, 1);
		assertTrue(m_board.isEmpty(2, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 5, 2);
		assertTrue(m_board.isEmpty(2, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 5, 3);
		assertTrue(m_board.isEmpty(2, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 6, 0);
		assertTrue(m_board.isEmpty(2, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 6, 1);
		assertTrue(m_board.isEmpty(2, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 6, 2);
		assertTrue(m_board.isEmpty(2, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 6, 3);
		assertTrue(m_board.isEmpty(2, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 7, 0);
		assertTrue(m_board.isEmpty(2, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 7, 1);
		assertTrue(m_board.isEmpty(2, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 7, 2);
		assertTrue(m_board.isEmpty(2, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 7, 3);
		assertTrue(m_board.isEmpty(2, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 8, 0);
		assertTrue(m_board.isEmpty(2, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 2, 8, 1);
		assertTrue(m_board.isEmpty(2, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 2, 8, 2);
		assertTrue(m_board.isEmpty(2, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 2, 8, 3);
		assertTrue(m_board.isEmpty(2, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 0, 0);
		assertTrue(m_board.isEmpty(3, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 4);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 0, 1);
		assertTrue(m_board.isEmpty(3, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 0, 2);
		assertTrue(m_board.isEmpty(3, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 0, 3);
		assertTrue(m_board.isEmpty(3, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 1, 0);
		assertTrue(m_board.isEmpty(3, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 3, 1, 1);
		assertTrue(m_board.isEmpty(3, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 3, 1, 2);
		assertTrue(m_board.isEmpty(3, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 1, 3);
		assertTrue(m_board.isEmpty(3, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 2, 0);
		assertTrue(m_board.isEmpty(3, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 2, 1);
		assertTrue(m_board.isEmpty(3, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 2, 2);
		assertTrue(m_board.isEmpty(3, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 3, 2, 3);
		assertTrue(m_board.isEmpty(3, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 3, 0);
		assertTrue(m_board.isNotEmpty(3, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 3, 3, 1);
		assertTrue(m_board.isNotEmpty(3, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 3, 3, 2);
		assertTrue(m_board.isNotEmpty(3, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 3, 3, 3);
		assertTrue(m_board.isNotEmpty(3, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 3, 4, 0);
		assertTrue(m_board.isEmpty(3, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 3, 4, 1);
		assertTrue(m_board.isEmpty(3, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 4, 2);
		assertTrue(m_board.isEmpty(3, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 4, 3);
		assertTrue(m_board.isEmpty(3, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 3, 5, 0);
		assertTrue(m_board.isNotEmpty(3, 5));
		assertNull(counts);

		counts = m_counting.getCounts(2, 3, 5, 1);
		assertTrue(m_board.isNotEmpty(3, 5));
		assertNull(counts);

		counts = m_counting.getCounts(2, 3, 5, 2);
		assertTrue(m_board.isNotEmpty(3, 5));
		assertNull(counts);

		counts = m_counting.getCounts(2, 3, 5, 3);
		assertTrue(m_board.isNotEmpty(3, 5));
		assertNull(counts);

		counts = m_counting.getCounts(2, 3, 6, 0);
		assertTrue(m_board.isEmpty(3, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 3, 6, 1);
		assertTrue(m_board.isEmpty(3, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 3, 6, 2);
		assertTrue(m_board.isEmpty(3, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 3, 6, 3);
		assertTrue(m_board.isEmpty(3, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 7, 0);
		assertTrue(m_board.isEmpty(3, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 7, 1);
		assertTrue(m_board.isEmpty(3, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 3, 7, 2);
		assertTrue(m_board.isEmpty(3, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 3, 7, 3);
		assertTrue(m_board.isEmpty(3, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 3, 8, 0);
		assertTrue(m_board.isEmpty(3, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 3, 8, 1);
		assertTrue(m_board.isEmpty(3, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 8, 2);
		assertTrue(m_board.isEmpty(3, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 3, 8, 3);
		assertTrue(m_board.isEmpty(3, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 0, 0);
		assertTrue(m_board.isEmpty(4, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 0, 1);
		assertTrue(m_board.isEmpty(4, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 0, 2);
		assertTrue(m_board.isEmpty(4, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 0, 3);
		assertTrue(m_board.isEmpty(4, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 1, 0);
		assertTrue(m_board.isEmpty(4, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 1, 1);
		assertTrue(m_board.isEmpty(4, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 1, 2);
		assertTrue(m_board.isEmpty(4, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 1, 3);
		assertTrue(m_board.isEmpty(4, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 2, 0);
		assertTrue(m_board.isEmpty(4, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 2, 1);
		assertTrue(m_board.isEmpty(4, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 2, 2);
		assertTrue(m_board.isEmpty(4, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 2, 3);
		assertTrue(m_board.isEmpty(4, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 3, 0);
		assertTrue(m_board.isNotEmpty(4, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 4, 3, 1);
		assertTrue(m_board.isNotEmpty(4, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 4, 3, 2);
		assertTrue(m_board.isNotEmpty(4, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 4, 3, 3);
		assertTrue(m_board.isNotEmpty(4, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 4, 4, 0);
		assertTrue(m_board.isEmpty(4, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 4, 1);
		assertTrue(m_board.isEmpty(4, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 4, 2);
		assertTrue(m_board.isEmpty(4, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 4, 3);
		assertTrue(m_board.isEmpty(4, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 5, 0);
		assertTrue(m_board.isEmpty(4, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 5, 1);
		assertTrue(m_board.isEmpty(4, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 5, 2);
		assertTrue(m_board.isEmpty(4, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 5, 3);
		assertTrue(m_board.isEmpty(4, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 6, 0);
		assertTrue(m_board.isEmpty(4, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 6, 1);
		assertTrue(m_board.isEmpty(4, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 6, 2);
		assertTrue(m_board.isEmpty(4, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 6, 3);
		assertTrue(m_board.isEmpty(4, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 4);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 7, 0);
		assertTrue(m_board.isNotEmpty(4, 7));
		assertNull(counts);

		counts = m_counting.getCounts(2, 4, 7, 1);
		assertTrue(m_board.isNotEmpty(4, 7));
		assertNull(counts);

		counts = m_counting.getCounts(2, 4, 7, 2);
		assertTrue(m_board.isNotEmpty(4, 7));
		assertNull(counts);

		counts = m_counting.getCounts(2, 4, 7, 3);
		assertTrue(m_board.isNotEmpty(4, 7));
		assertNull(counts);

		counts = m_counting.getCounts(2, 4, 8, 0);
		assertTrue(m_board.isEmpty(4, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 4, 8, 1);
		assertTrue(m_board.isEmpty(4, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 8, 2);
		assertTrue(m_board.isEmpty(4, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 4, 8, 3);
		assertTrue(m_board.isEmpty(4, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 0, 0);
		assertTrue(m_board.isEmpty(5, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 0, 1);
		assertTrue(m_board.isEmpty(5, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 0, 2);
		assertTrue(m_board.isEmpty(5, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 0, 3);
		assertTrue(m_board.isEmpty(5, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 1, 0);
		assertTrue(m_board.isEmpty(5, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 1, 1);
		assertTrue(m_board.isEmpty(5, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 1, 2);
		assertTrue(m_board.isEmpty(5, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 1, 3);
		assertTrue(m_board.isEmpty(5, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 2, 0);
		assertTrue(m_board.isEmpty(5, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 2, 1);
		assertTrue(m_board.isEmpty(5, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 2, 2);
		assertTrue(m_board.isEmpty(5, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 2, 3);
		assertTrue(m_board.isEmpty(5, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 3, 0);
		assertTrue(m_board.isNotEmpty(5, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 5, 3, 1);
		assertTrue(m_board.isNotEmpty(5, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 5, 3, 2);
		assertTrue(m_board.isNotEmpty(5, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 5, 3, 3);
		assertTrue(m_board.isNotEmpty(5, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 5, 4, 0);
		assertTrue(m_board.isEmpty(5, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 4, 1);
		assertTrue(m_board.isEmpty(5, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 4, 2);
		assertTrue(m_board.isEmpty(5, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 4, 3);
		assertTrue(m_board.isEmpty(5, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 5, 0);
		assertTrue(m_board.isEmpty(5, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 5, 1);
		assertTrue(m_board.isEmpty(5, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 5, 2);
		assertTrue(m_board.isEmpty(5, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 5, 3);
		assertTrue(m_board.isEmpty(5, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 6, 0);
		assertTrue(m_board.isNotEmpty(5, 6));
		assertNull(counts);

		counts = m_counting.getCounts(2, 5, 6, 1);
		assertTrue(m_board.isNotEmpty(5, 6));
		assertNull(counts);

		counts = m_counting.getCounts(2, 5, 6, 2);
		assertTrue(m_board.isNotEmpty(5, 6));
		assertNull(counts);

		counts = m_counting.getCounts(2, 5, 6, 3);
		assertTrue(m_board.isNotEmpty(5, 6));
		assertNull(counts);

		counts = m_counting.getCounts(2, 5, 7, 0);
		assertTrue(m_board.isEmpty(5, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 7, 1);
		assertTrue(m_board.isEmpty(5, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 7, 2);
		assertTrue(m_board.isEmpty(5, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 7, 3);
		assertTrue(m_board.isEmpty(5, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 8, 0);
		assertTrue(m_board.isEmpty(5, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 5, 8, 1);
		assertTrue(m_board.isEmpty(5, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 8, 2);
		assertTrue(m_board.isEmpty(5, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 5, 8, 3);
		assertTrue(m_board.isEmpty(5, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 0, 0);
		assertTrue(m_board.isEmpty(6, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 0, 1);
		assertTrue(m_board.isEmpty(6, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 0, 2);
		assertTrue(m_board.isEmpty(6, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 0, 3);
		assertTrue(m_board.isEmpty(6, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 1, 0);
		assertTrue(m_board.isEmpty(6, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 1, 1);
		assertTrue(m_board.isEmpty(6, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 1, 2);
		assertTrue(m_board.isEmpty(6, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 1, 3);
		assertTrue(m_board.isEmpty(6, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 2, 0);
		assertTrue(m_board.isEmpty(6, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 2, 1);
		assertTrue(m_board.isEmpty(6, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 2, 2);
		assertTrue(m_board.isEmpty(6, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 2, 3);
		assertTrue(m_board.isEmpty(6, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 3, 0);
		assertTrue(m_board.isEmpty(6, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 3, 1);
		assertTrue(m_board.isEmpty(6, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 3, 2);
		assertTrue(m_board.isEmpty(6, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 3, 3);
		assertTrue(m_board.isEmpty(6, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 4, 0);
		assertTrue(m_board.isEmpty(6, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 4, 1);
		assertTrue(m_board.isEmpty(6, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 4, 2);
		assertTrue(m_board.isEmpty(6, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 4, 3);
		assertTrue(m_board.isEmpty(6, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 5, 0);
		assertTrue(m_board.isNotEmpty(6, 5));
		assertNull(counts);

		counts = m_counting.getCounts(2, 6, 5, 1);
		assertTrue(m_board.isNotEmpty(6, 5));
		assertNull(counts);

		counts = m_counting.getCounts(2, 6, 5, 2);
		assertTrue(m_board.isNotEmpty(6, 5));
		assertNull(counts);

		counts = m_counting.getCounts(2, 6, 5, 3);
		assertTrue(m_board.isNotEmpty(6, 5));
		assertNull(counts);

		counts = m_counting.getCounts(2, 6, 6, 0);
		assertTrue(m_board.isEmpty(6, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 6, 1);
		assertTrue(m_board.isEmpty(6, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 6, 2);
		assertTrue(m_board.isEmpty(6, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 6, 3);
		assertTrue(m_board.isEmpty(6, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 7, 0);
		assertTrue(m_board.isEmpty(6, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 7, 1);
		assertTrue(m_board.isEmpty(6, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 7, 2);
		assertTrue(m_board.isEmpty(6, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 6, 7, 3);
		assertTrue(m_board.isEmpty(6, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 8, 0);
		assertTrue(m_board.isEmpty(6, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 8, 1);
		assertTrue(m_board.isEmpty(6, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 8, 2);
		assertTrue(m_board.isEmpty(6, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 6, 8, 3);
		assertTrue(m_board.isEmpty(6, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 7, 0, 0);
		assertTrue(m_board.isEmpty(7, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 0, 1);
		assertTrue(m_board.isEmpty(7, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 4);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 7, 0, 2);
		assertTrue(m_board.isEmpty(7, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 7, 0, 3);
		assertTrue(m_board.isEmpty(7, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 7, 1, 0);
		assertTrue(m_board.isNotEmpty(7, 1));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 1, 1);
		assertTrue(m_board.isNotEmpty(7, 1));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 1, 2);
		assertTrue(m_board.isNotEmpty(7, 1));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 1, 3);
		assertTrue(m_board.isNotEmpty(7, 1));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 2, 0);
		assertTrue(m_board.isNotEmpty(7, 2));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 2, 1);
		assertTrue(m_board.isNotEmpty(7, 2));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 2, 2);
		assertTrue(m_board.isNotEmpty(7, 2));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 2, 3);
		assertTrue(m_board.isNotEmpty(7, 2));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 3, 0);
		assertTrue(m_board.isNotEmpty(7, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 3, 1);
		assertTrue(m_board.isNotEmpty(7, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 3, 2);
		assertTrue(m_board.isNotEmpty(7, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 3, 3);
		assertTrue(m_board.isNotEmpty(7, 3));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 4, 0);
		assertTrue(m_board.isEmpty(7, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 4, 1);
		assertTrue(m_board.isEmpty(7, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 4);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 4, 2);
		assertTrue(m_board.isEmpty(7, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 7, 4, 3);
		assertTrue(m_board.isEmpty(7, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 5, 0);
		assertTrue(m_board.isEmpty(7, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 7, 5, 1);
		assertTrue(m_board.isEmpty(7, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 5, 2);
		assertTrue(m_board.isEmpty(7, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 5, 3);
		assertTrue(m_board.isEmpty(7, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 6, 0);
		assertTrue(m_board.isEmpty(7, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 6, 1);
		assertTrue(m_board.isEmpty(7, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 6, 2);
		assertTrue(m_board.isEmpty(7, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 6, 3);
		assertTrue(m_board.isEmpty(7, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 7, 7, 0);
		assertTrue(m_board.isEmpty(7, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 7, 1);
		assertTrue(m_board.isEmpty(7, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 7, 7, 2);
		assertTrue(m_board.isEmpty(7, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 7, 3);
		assertTrue(m_board.isEmpty(7, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 7, 8, 0);
		assertTrue(m_board.isNotEmpty(7, 8));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 8, 1);
		assertTrue(m_board.isNotEmpty(7, 8));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 8, 2);
		assertTrue(m_board.isNotEmpty(7, 8));
		assertNull(counts);

		counts = m_counting.getCounts(2, 7, 8, 3);
		assertTrue(m_board.isNotEmpty(7, 8));
		assertNull(counts);

		counts = m_counting.getCounts(2, 8, 0, 0);
		assertTrue(m_board.isEmpty(8, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 0, 1);
		assertTrue(m_board.isEmpty(8, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 0, 2);
		assertTrue(m_board.isEmpty(8, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 0, 3);
		assertTrue(m_board.isEmpty(8, 0));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 1, 0);
		assertTrue(m_board.isEmpty(8, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 1, 1);
		assertTrue(m_board.isEmpty(8, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 8, 1, 2);
		assertTrue(m_board.isEmpty(8, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 8);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 1, 3);
		assertTrue(m_board.isEmpty(8, 1));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 2, 0);
		assertTrue(m_board.isEmpty(8, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 2, 1);
		assertTrue(m_board.isEmpty(8, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 8, 2, 2);
		assertTrue(m_board.isEmpty(8, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 7);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 2, 3);
		assertTrue(m_board.isEmpty(8, 2));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 3, 0);
		assertTrue(m_board.isEmpty(8, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 3, 1);
		assertTrue(m_board.isEmpty(8, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 8, 3, 2);
		assertTrue(m_board.isEmpty(8, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 3, 3);
		assertTrue(m_board.isEmpty(8, 3));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 4, 0);
		assertTrue(m_board.isEmpty(8, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 4, 1);
		assertTrue(m_board.isEmpty(8, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 8, 4, 2);
		assertTrue(m_board.isEmpty(8, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 4, 3);
		assertTrue(m_board.isEmpty(8, 4));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 2);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 5, 0);
		assertTrue(m_board.isEmpty(8, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 5, 1);
		assertTrue(m_board.isEmpty(8, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 8, 5, 2);
		assertTrue(m_board.isEmpty(8, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 5, 3);
		assertTrue(m_board.isEmpty(8, 5));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 6);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 6, 0);
		assertTrue(m_board.isEmpty(8, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 6, 1);
		assertTrue(m_board.isEmpty(8, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 8, 6, 2);
		assertTrue(m_board.isEmpty(8, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 6, 3);
		assertTrue(m_board.isEmpty(8, 6));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 3);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 7, 0);
		assertTrue(m_board.isEmpty(8, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 4);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 7, 1);
		assertTrue(m_board.isEmpty(8, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), false);
		assertEquals(counts.isBounded(), false);

		counts = m_counting.getCounts(2, 8, 7, 2);
		assertTrue(m_board.isEmpty(8, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 7, 3);
		assertTrue(m_board.isEmpty(8, 7));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 2);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 8, 0);
		assertTrue(m_board.isEmpty(8, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 8, 1);
		assertTrue(m_board.isEmpty(8, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 9);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 8, 2);
		assertTrue(m_board.isEmpty(8, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 1);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);

		counts = m_counting.getCounts(2, 8, 8, 3);
		assertTrue(m_board.isEmpty(8, 8));
		assertNotNull(counts);
		assertEquals(counts.getActual(), 1);
		assertEquals(counts.getPossible(), 5);
		assertEquals(counts.isBorder(), true);
		assertEquals(counts.isBounded(), true);
	}
}
