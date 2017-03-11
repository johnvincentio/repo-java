package com.idc.five.junit.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.Board;

public class Board2Test {

	private Board m_board;

	@Before
	public void initialize() {
		m_board = new Board();
	}

	@Test
	public void test1() {
		assertEquals(m_board.getMinRows(), 5);
		assertEquals(m_board.getMinColumns(), 5);
		assertEquals(m_board.getMaxRows(), 20);
		assertEquals(m_board.getMaxColumns(), 20);

		assertEquals(m_board.getMaxSize(), 9);

		assertEquals(m_board.getRows(), 9);
		assertEquals(m_board.getColumns(), 9);
		assertEquals(m_board.getCurrentRow(), 4);
		assertEquals(m_board.getCurrentColumn(), 4);

		for (int r = 0; r < m_board.getRows(); r++) {
			assertTrue(m_board.isValidRow(r));
		}
		for (int c = 0; c < m_board.getColumns(); c++) {
			assertTrue(m_board.isValidColumn(c));
		}
		for (int r = -20; r < 0; r++) {
			assertFalse(m_board.isValidRow(r));
		}
		for (int r = m_board.getRows(); r < 30; r++) {
			assertFalse(m_board.isValidRow(r));
		}

		for (int c = -20; c < 0; c++) {
			assertFalse(m_board.isValidColumn(c));
		}
		for (int c = m_board.getColumns(); c < 30; c++) {
			assertFalse(m_board.isValidColumn(c));
		}
	}

	@Test
	public void test2() {
		m_board.setRows(12);
		m_board.setColumns(15);

		assertEquals(m_board.getMaxSize(), 15);

		assertEquals(m_board.getRows(), 12);
		assertEquals(m_board.getColumns(), 15);
		assertEquals(m_board.getCurrentRow(), 7);
		assertEquals(m_board.getCurrentColumn(), 10);

		for (int r = 0; r < m_board.getRows(); r++) {
			assertTrue(m_board.isValidRow(r));
		}
		for (int c = 0; c < m_board.getColumns(); c++) {
			assertTrue(m_board.isValidColumn(c));
		}
		for (int r = -20; r < 0; r++) {
			assertFalse(m_board.isValidRow(r));
		}
		for (int r = m_board.getRows(); r < 30; r++) {
			assertFalse(m_board.isValidRow(r));
		}

		for (int c = -20; c < 0; c++) {
			assertFalse(m_board.isValidColumn(c));
		}
		for (int c = m_board.getColumns(); c < 30; c++) {
			assertFalse(m_board.isValidColumn(c));
		}
	}

	@Test
	public void test3() {
		m_board.setRows(18);
		m_board.setColumns(14);
		assertEquals(m_board.getRows(), 18);
		assertEquals(m_board.getColumns(), 14);

		assertEquals(m_board.getMaxSize(), 18);

		assertEquals(m_board.getCurrentRow(), 13);
		assertEquals(m_board.getCurrentColumn(), 9);

		for (int r = 0; r < m_board.getRows(); r++) {
			assertTrue(m_board.isValidRow(r));
		}
		for (int c = 0; c < m_board.getColumns(); c++) {
			assertTrue(m_board.isValidColumn(c));
		}
		for (int r = -20; r < 0; r++) {
			assertFalse(m_board.isValidRow(r));
		}
		for (int r = m_board.getRows(); r < 30; r++) {
			assertFalse(m_board.isValidRow(r));
		}

		for (int c = -20; c < 0; c++) {
			assertFalse(m_board.isValidColumn(c));
		}
		for (int c = m_board.getColumns(); c < 30; c++) {
			assertFalse(m_board.isValidColumn(c));
		}
	}
}
