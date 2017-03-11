package com.idc.knight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SquareTest {

	private Square m_square;

	@Before
	public void initialize() {
		m_square = new Square();
	}
	@Test
	public void test3a() throws Exception {
		m_square.checkValid();
	}

	@Test (expected = Exception.class)
	public void test3a1() throws Exception {
		m_square.setFrom (new Pair (3, 5));
		m_square.setMoveAwayType(-4);
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3a2() throws Exception {
		m_square.setFrom (new Pair (3, 5));
		m_square.setMoveAwayType(9);
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3a3() throws Exception {
		m_square.setFrom (new Pair (3, 5));
		m_square.setMoveCounter (-2);
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3a4() throws Exception {
		m_square.setFrom (new Pair (3, 5));
		m_square.setMoveCounter (-7);
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3b() throws Exception {
		m_square.setFrom (new Pair (-3, 5));
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3c() throws Exception {
		m_square.setFrom (new Pair (3, -5));
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3d() throws Exception {
		m_square.setFrom (new Pair (-2, -5));
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3e() throws Exception {
		m_square.setFrom (new Pair (-1, 3));
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3f() throws Exception {
		m_square.setFrom (new Pair (4, -1));
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3g() throws Exception {
		m_square.setMoveAwayType (5);
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3h() throws Exception {
		m_square.setMoveCounter (3);
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3i() throws Exception {
		m_square.setMoveAwayType (5);
		m_square.setMoveCounter (3);
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3j() throws Exception {
		m_square.setFrom (new Pair (5, 2));
		m_square.setMoveCounter (3);
		m_square.setMoveAwayType (-3);
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3k() throws Exception {
		m_square.setFrom (new Pair (5, 2));
		m_square.setMoveCounter (3);
		m_square.setMoveAwayType (9);
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3m() throws Exception {
		m_square.setFrom (new Pair (5, 2));
		m_square.setMoveAwayType (7);
		m_square.checkValid();
	}
	@Test (expected = Exception.class)
	public void test3n() throws Exception {
		m_square.setFrom (new Pair (5, 2));
		m_square.setMoveAwayType (7);
		m_square.checkValid();
	}

	@Test
	public void test4() throws Exception {
		m_square.setFrom (new Pair (5, 2));
		m_square.setMoveAwayType (7);
		m_square.setMoveCounter (3);
		m_square.checkValid();
	}

	@Test
	public void test1a() {
		assertEquals (m_square.getMoveCounter(), -1);
		assertEquals (m_square.getFrom().getX(), -1);
		assertEquals (m_square.getFrom().getY(), -1);
		assertEquals (m_square.getMoveAwayType(), 0);
		assertFalse (m_square.isOccupied());
	}
	@Test
	public void test1b() {
		m_square.setFrom (new Pair (-1, 4));
		assertEquals (m_square.getMoveCounter(), -1);
		assertEquals (m_square.getFrom().getX(), -1);
		assertEquals (m_square.getFrom().getY(), 4);
		assertEquals (m_square.getMoveAwayType(), 0);
		assertFalse (m_square.isOccupied());
}
	@Test
	public void test1c() {
		m_square.setFrom (new Pair (3, -1));
		assertEquals (m_square.getMoveCounter(), -1);
		assertEquals (m_square.getFrom().getX(), 3);
		assertEquals (m_square.getFrom().getY(), -1);
		assertEquals (m_square.getMoveAwayType(), 0);
		assertFalse (m_square.isOccupied());
	}
	@Test
	public void test1d() {
		m_square.setFrom (new Pair (2, 5));
		assertEquals (m_square.getMoveCounter(), -1);
		assertEquals (m_square.getFrom().getX(), 2);
		assertEquals (m_square.getFrom().getY(), 5);
		assertEquals (m_square.getMoveAwayType(), 0);
		assertFalse (m_square.isOccupied());
	}
	@Test
	public void test1e() {
		m_square.setMoveAwayType (7);
		assertEquals (m_square.getMoveCounter(), -1);
		assertEquals (m_square.getFrom().getX(), -1);
		assertEquals (m_square.getFrom().getY(), -1);
		assertEquals (m_square.getMoveAwayType(), 7);
		assertFalse (m_square.isOccupied());
	}
	@Test
	public void test1f() {
		m_square.setMoveCounter (3);
		assertEquals (m_square.getMoveCounter(), 3);
		assertEquals (m_square.getFrom().getX(), -1);
		assertEquals (m_square.getFrom().getY(), -1);
		assertEquals (m_square.getMoveAwayType(), 0);
		assertTrue (m_square.isOccupied());
	}
	@Test
	public void test2() {
		m_square.setMoveCounter (4);
		m_square.setMoveAwayType (8);
		m_square.setFrom (new Pair (3, 5));
		assertEquals (m_square.getMoveCounter(), 4);
		assertEquals (m_square.getFrom().getX(), 3);
		assertEquals (m_square.getFrom().getY(), 5);
		assertEquals (m_square.getMoveAwayType(), 8);
		assertTrue (m_square.isOccupied());

		m_square.init();
		assertEquals (m_square.getMoveCounter(), -1);
		assertEquals (m_square.getFrom().getX(), -1);
		assertEquals (m_square.getFrom().getY(), -1);
		assertEquals (m_square.getMoveAwayType(), 0);
		assertFalse (m_square.isOccupied());
	}
}
