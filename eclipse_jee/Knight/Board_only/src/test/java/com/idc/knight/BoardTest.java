package com.idc.knight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class BoardTest {

	private Board m_board;

	@Before
	public void initialize() {
		m_board = new Board();
	}

	@Test
	public void test1() throws Exception {
		System.out.println("--- Board::test1");
		int x_size = 8;
		int y_size = 8;
		Pair size = new Pair (x_size, y_size);
		m_board.setBoard (size);
		Pair test = new Pair();
		for (int i = 0; i < x_size; i++) {
			for (int j = 0; j < y_size; j++) {
				test.set (i, j);
				m_board.checkValidSquare (test);
			}
		}
	}
	@Test
	public void test2() throws Exception {
		System.out.println("--- Board::test2");
		int x_size = 5;
		int y_size = 9;
		Pair size = new Pair (x_size, y_size);
		m_board.setBoard (size);
		Pair test = new Pair();
		for (int i = 0; i < x_size; i++) {
			for (int j = 0; j < y_size; j++) {
				test.set (i, j);
				m_board.checkValidSquare (test);
			}
		}
	}
	@Test (expected = Exception.class)
	public void test3() throws Exception {
		System.out.println("--- Board::test3");
		m_board.setBoard (new Pair (5, 2));
	}
	@Test (expected = Exception.class)
	public void test4() throws Exception {
		System.out.println("--- Board::test4");
		m_board.setBoard (new Pair (1, 6));
	}
	@Test (expected = Exception.class)
	public void test5() throws Exception {
		System.out.println("--- Board::test5");
		m_board.setBoard (new Pair (0, 5));
	}
	@Test (expected = Exception.class)
	public void test6() throws Exception {
		System.out.println("--- Board::test6");
		m_board.setBoard (new Pair (-1, 4));
	}
	@Test (expected = Exception.class)
	public void test7() throws Exception {
		System.out.println("--- Board::test7");
		m_board.setBoard (new Pair (5, -1));
	}
	@Test (expected = Exception.class)
	public void test8() throws Exception {
		System.out.println("--- Board::test8");
		m_board.setBoard (new Pair (6, 0));
	}
	@Test (expected = Exception.class)
	public void test9() throws Exception {
		System.out.println("--- Board::test9");
		m_board.setBoard (new Pair (8, 1));
	}
	@Test (expected = Exception.class)
	public void test10() throws Exception {
		System.out.println("--- Board::test10");
		m_board.setBoard (new Pair (15, 2));
	}

	@Test (expected = Exception.class)
	public void btest1() throws Exception {
		System.out.println("--- Board::btest1");
		m_board.setBoard (new Pair (1, 8));
	}
	@Test (expected = Exception.class)
	public void btest1a() throws Exception {
		System.out.println("--- Board::btest1a");
		m_board.setBoard (new Pair (0, 7));
	}
	@Test (expected = Exception.class)
	public void btest1b() throws Exception {
		System.out.println("--- Board::btest1b");
		m_board.setBoard (new Pair (-1, 6));
	}
	@Test (expected = Exception.class)
	public void btest2() throws Exception {
		System.out.println("--- Board::btest2");
		m_board.setBoard (new Pair (8, 1));
	}
	@Test (expected = Exception.class)
	public void btest2a() throws Exception {
		System.out.println("--- Board::btest2a");
		m_board.setBoard (new Pair (7, 0));
	}
	@Test (expected = Exception.class)
	public void btest2b() throws Exception {
		System.out.println("--- Board::btest2b");
		m_board.setBoard (new Pair (6, -1));
	}
	@Test
	public void btest3() {
		System.out.println("--- Board::btest3");
		for (int x = -5; x < 20; x++) {
			for (int y = -5; y < 20; y++) {
				try {
					m_board.setBoard (new Pair (x, y));
//					assertEquals (data.getCurrentMove(), -1);
//					assertEquals (data.getMaxX(), x);
//					assertEquals (data.getMaxY(), y);
				}
				catch (Exception ex) {
					if (x > 3 && y > 3) fail ("x value of "+x+" y value of "+y+" should have failed");
				}
			}
		}
	}
	@Test
	public void btest3a() throws Exception {
		System.out.println("--- Board::btest3a");
		Pair test = new Pair();
		for (int x = 3; x < 20; x++) {
			for (int y = 3; y < 20; y++) {
				m_board.setBoard (new Pair (x, y));
				for (int i = 0; i < x; i++) {
					for (int j = 0; j < y; j++) {
						test.set (i, j);
						assertFalse (m_board.isOccupied (test));						
					}
				}
			}
		}
	}

	@Test
	public void ptest1a() throws Exception {
		System.out.println("--- Board::ptest1a");
		m_board.setBoard (new Pair (4, 4));
		Pair from = new Pair (0, 0);
		Pair to = new Pair();
		Pair tempPair = new Pair();

// make moveCounter = 0

		m_board.addFirstMove (0, from);			// add the first move

		Square square = m_board.getSquare (from);
		assertEquals (square.getMoveCounter(), 0);
		assertEquals (square.getFrom().getX(), 0);
		assertEquals (square.getFrom().getY(), 0);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 1

		int moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 1);
		System.out.println("from (1) "+from);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		System.out.println("from (2) "+from);
		System.out.println("to (1) "+to);
		m_board.printBoard ("Status at 1");

		assertEquals (square.getMoveCounter(), 0);		// verify from square
		assertEquals (square.getFrom().getX(), 0);
		assertEquals (square.getFrom().getY(), 0);
		assertEquals (square.getMoveAwayType(), 1);
		assertEquals (m_board.getMoveCounter (from), 0);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 1);
		assertEquals (square.getFrom().getX(), 0);
		assertEquals (square.getFrom().getY(), 0);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 2

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 2);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 2");

		assertEquals (square.getMoveCounter(), 1);		// verify from square
		assertEquals (square.getFrom().getX(), 0);
		assertEquals (square.getFrom().getY(), 0);
		assertEquals (square.getMoveAwayType(), 2);
		assertEquals (m_board.getMoveCounter (from), 1);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 2);
		assertEquals (square.getFrom().getX(), 1);
		assertEquals (square.getFrom().getY(), 2);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 3

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 5);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 3");

		assertEquals (square.getMoveCounter(), 2);		// verify from square
		assertEquals (square.getFrom().getX(), 1);
		assertEquals (square.getFrom().getY(), 2);
		assertEquals (square.getMoveAwayType(), 5);
		assertEquals (m_board.getMoveCounter (from), 2);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 3);
		assertEquals (square.getFrom().getX(), 3);
		assertEquals (square.getFrom().getY(), 3);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 4

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 7);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 4");

		assertEquals (square.getMoveCounter(), 3);		// verify from square
		assertEquals (square.getFrom().getX(), 3);
		assertEquals (square.getFrom().getY(), 3);
		assertEquals (square.getMoveAwayType(), 7);
		assertEquals (m_board.getMoveCounter (from), 3);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 4);
		assertEquals (square.getFrom().getX(), 2);
		assertEquals (square.getFrom().getY(), 1);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 5

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 2);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 5");

		assertEquals (square.getMoveCounter(), 4);		// verify from square
		assertEquals (square.getFrom().getX(), 2);
		assertEquals (square.getFrom().getY(), 1);
		assertEquals (square.getMoveAwayType(), 2);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 5);
		assertEquals (square.getFrom().getX(), 0);
		assertEquals (square.getFrom().getY(), 2);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 6

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 4);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 6");

		assertEquals (square.getMoveCounter(), 5);		// verify from square
		assertEquals (square.getFrom().getX(), 0);
		assertEquals (square.getFrom().getY(), 2);
		assertEquals (square.getMoveAwayType(), 4);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 6);
		assertEquals (square.getFrom().getX(), 2);
		assertEquals (square.getFrom().getY(), 3);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 7

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 6);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 7");

		assertEquals (square.getMoveCounter(), 6);		// verify from square
		assertEquals (square.getFrom().getX(), 2);
		assertEquals (square.getFrom().getY(), 3);
		assertEquals (square.getMoveAwayType(), 6);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 7);
		assertEquals (square.getFrom().getX(), 3);
		assertEquals (square.getFrom().getY(), 1);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 8

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 1);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 8");

		assertEquals (square.getMoveCounter(), 7);		// verify from square
		assertEquals (square.getFrom().getX(), 3);
		assertEquals (square.getFrom().getY(), 1);
		assertEquals (square.getMoveAwayType(), 1);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 8);
		assertEquals (square.getFrom().getX(), 1);
		assertEquals (square.getFrom().getY(), 0);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 9

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 4);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 9");

		assertEquals (square.getMoveCounter(), 8);		// verify from square
		assertEquals (square.getFrom().getX(), 1);
		assertEquals (square.getFrom().getY(), 0);
		assertEquals (square.getMoveAwayType(), 4);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 9);
		assertEquals (square.getFrom().getX(), 2);
		assertEquals (square.getFrom().getY(), 2);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 10

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 7);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 10");

		assertEquals (square.getMoveCounter(), 9);		// verify from square
		assertEquals (square.getFrom().getX(), 2);
		assertEquals (square.getFrom().getY(), 2);
		assertEquals (square.getMoveAwayType(), 7);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 10);
		assertEquals (square.getFrom().getX(), 3);
		assertEquals (square.getFrom().getY(), 0);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 11

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 2);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 11");

		assertEquals (square.getMoveCounter(), 10);		// verify from square
		assertEquals (square.getFrom().getX(), 3);
		assertEquals (square.getFrom().getY(), 0);
		assertEquals (square.getMoveAwayType(), 2);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 11);
		assertEquals (square.getFrom().getX(), 1);
		assertEquals (square.getFrom().getY(), 1);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 12

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 5);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 12");

		assertEquals (square.getMoveCounter(), 11);		// verify from square
		assertEquals (square.getFrom().getX(), 1);
		assertEquals (square.getFrom().getY(), 1);
		assertEquals (square.getMoveAwayType(), 5);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 12);
		assertEquals (square.getFrom().getX(), 3);
		assertEquals (square.getFrom().getY(), 2);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 13

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 7);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 13");

		assertEquals (square.getMoveCounter(), 12);		// verify from square
		assertEquals (square.getFrom().getX(), 3);
		assertEquals (square.getFrom().getY(), 2);
		assertEquals (square.getMoveAwayType(), 7);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 13);
		assertEquals (square.getFrom().getX(), 2);
		assertEquals (square.getFrom().getY(), 0);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 14

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 1);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 14");

		assertEquals (square.getMoveCounter(), 13);		// verify from square
		assertEquals (square.getFrom().getX(), 2);
		assertEquals (square.getFrom().getY(), 0);
		assertEquals (square.getMoveAwayType(), 1);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 14);
		assertEquals (square.getFrom().getX(), 0);
		assertEquals (square.getFrom().getY(), 1);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 13

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, -1);			// no valid move
		m_board.moveBackward (from);					// move backward
		m_board.printBoard ("Status at 13");

		assertEquals (square.getMoveCounter(), -1);		// verify from square
		assertEquals (square.getFrom().getX(), -1);
		assertEquals (square.getFrom().getY(), -1);
		assertEquals (square.getMoveAwayType(), 0);
		assertFalse (square.isOccupied());

		from = new Pair (from);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 13);
		assertEquals (square.getFrom().getX(), 2);
		assertEquals (square.getFrom().getY(), 0);
		assertEquals (square.getMoveAwayType(), 1);
		assertTrue (square.isOccupied());

// make moveCounter = 12

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, -1);			// no valid move
		m_board.moveBackward (from);					// move backward
		m_board.printBoard ("Status at 12");

		assertEquals (square.getMoveCounter(), -1);		// verify from square
		assertEquals (square.getFrom().getX(), -1);
		assertEquals (square.getFrom().getY(), -1);
		assertEquals (square.getMoveAwayType(), 0);
		assertFalse (square.isOccupied());

		from = new Pair (from);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 12);
		assertEquals (square.getFrom().getX(), 3);
		assertEquals (square.getFrom().getY(), 2);
		assertEquals (square.getMoveAwayType(), 7);
		assertTrue (square.isOccupied());

// make moveCounter = 11

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, -1);			// no valid move
		m_board.moveBackward (from);					// move backward
		m_board.printBoard ("Status at 11");

		assertEquals (square.getMoveCounter(), -1);		// verify from square
		assertEquals (square.getFrom().getX(), -1);
		assertEquals (square.getFrom().getY(), -1);
		assertEquals (square.getMoveAwayType(), 0);
		assertFalse (square.isOccupied());

		from = new Pair (from);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 11);
		assertEquals (square.getFrom().getX(), 1);
		assertEquals (square.getFrom().getY(), 1);
		assertEquals (square.getMoveAwayType(), 5);
		assertTrue (square.isOccupied());

// make moveCounter = 12

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 7);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 12");

		assertEquals (square.getMoveCounter(), 11);		// verify from square
		assertEquals (square.getFrom().getX(), 1);
		assertEquals (square.getFrom().getY(), 1);
		assertEquals (square.getMoveAwayType(), 7);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 12);
		assertEquals (square.getFrom().getX(), 3);
		assertEquals (square.getFrom().getY(), 2);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());

// make moveCounter = 13

		moveAwayType = m_board.getNextValidMove (from, tempPair);		// calculate next move away
		assertEquals (moveAwayType, 5);
		m_board.moveForward (from, moveAwayType, to);		// make the move
		m_board.printBoard ("Status at 13");

		assertEquals (square.getMoveCounter(), 12);		// verify from square
		assertEquals (square.getFrom().getX(), 3);
		assertEquals (square.getFrom().getY(), 2);
		assertEquals (square.getMoveAwayType(), 5);
		assertTrue (square.isOccupied());

		from = new Pair (to);
		square = m_board.getSquare (from);						// verify to square
		assertEquals (square.getMoveCounter(), 13);
		assertEquals (square.getFrom().getX(), 1);
		assertEquals (square.getFrom().getY(), 3);
		assertEquals (square.getMoveAwayType(), 0);
		assertTrue (square.isOccupied());
	}

	@Test
	public void moves_test3() throws Exception {
		for (int i = 1; i <= Board.getMoveTypesCount(); i++)
			m_board.getPos (new Pair(), i, new Pair());
	}
	@Test (expected = Exception.class)
	public void moves_test3a() throws Exception {
		m_board.getPos (new Pair(), 0, new Pair());
	}
	@Test (expected = Exception.class)
	public void moves_test3b() throws Exception {
		m_board.getPos (new Pair(), -1, new Pair());
	}
	@Test (expected = Exception.class)
	public void moves_test3c() throws Exception {
		m_board.getPos (new Pair(), Board.getMoveTypesCount() + 1, new Pair());
	}

	@Test
	public void moves_test4a() throws Exception {
		Pair start = new Pair (3, 5);
		Pair end = new Pair();
		m_board.getPos (start, 1, end);
		assertEquals (start.getX(), 3);
		assertEquals (start.getY(), 5);
		assertEquals (end.getX(), 4);
		assertEquals (end.getY(), 7);
	}
	@Test
	public void moves_test4b() throws Exception {
		Pair start = new Pair (3, 5);
		Pair end = new Pair();
		m_board.getPos (start, 2, end);
		assertEquals (start.getX(), 3);
		assertEquals (start.getY(), 5);
		assertEquals (end.getX(), 5);
		assertEquals (end.getY(), 6);
	}
	@Test
	public void moves_test4c() throws Exception {
		Pair start = new Pair (3, 5);
		Pair end = new Pair();
		m_board.getPos (start, 3, end);
		assertEquals (start.getX(), 3);
		assertEquals (start.getY(), 5);
		assertEquals (end.getX(), 5);
		assertEquals (end.getY(), 4);
	}
	@Test
	public void moves_test4d() throws Exception {
		Pair start = new Pair (3, 5);
		Pair end = new Pair();
		m_board.getPos (start, 4, end);
		assertEquals (start.getX(), 3);
		assertEquals (start.getY(), 5);
		assertEquals (end.getX(), 4);
		assertEquals (end.getY(), 3);
	}
	@Test
	public void moves_test4e() throws Exception {
		Pair start = new Pair (3, 5);
		Pair end = new Pair();
		m_board.getPos (start, 5, end);
		assertEquals (start.getX(), 3);
		assertEquals (start.getY(), 5);
		assertEquals (end.getX(), 2);
		assertEquals (end.getY(), 3);
	}
	@Test
	public void moves_test4f() throws Exception {
		Pair start = new Pair (3, 5);
		Pair end = new Pair();
		m_board.getPos (start, 6, end);
		assertEquals (start.getX(), 3);
		assertEquals (start.getY(), 5);
		assertEquals (end.getX(), 1);
		assertEquals (end.getY(), 4);
	}
	@Test
	public void moves_test4g() throws Exception {
		Pair start = new Pair (3, 5);
		Pair end = new Pair();
		m_board.getPos (start, 7, end);
		assertEquals (start.getX(), 3);
		assertEquals (start.getY(), 5);
		assertEquals (end.getX(), 1);
		assertEquals (end.getY(), 6);
	}
	@Test
	public void moves_test4h() throws Exception {
		Pair start = new Pair (3, 5);
		Pair end = new Pair();
		m_board.getPos (start, 8, end);
		assertEquals (start.getX(), 3);
		assertEquals (start.getY(), 5);
		assertEquals (end.getX(), 2);
		assertEquals (end.getY(), 7);
	}
	
	@Test
	public void moves_test2() throws Exception {
		assertEquals (Board.m_incrementX[0], 1);
		assertEquals (Board.m_incrementX[1], 2);
		assertEquals (Board.m_incrementX[2], 2);
		assertEquals (Board.m_incrementX[3], 1);
		assertEquals (Board.m_incrementX[4], -1);
		assertEquals (Board.m_incrementX[5], -2);
		assertEquals (Board.m_incrementX[6], -2);
		assertEquals (Board.m_incrementX[7], -1);
	}
	@Test
	public void moves_test2a() throws Exception {
		assertEquals (Board.m_incrementY[0], 2);
		assertEquals (Board.m_incrementY[1], 1);
		assertEquals (Board.m_incrementY[2], -1);
		assertEquals (Board.m_incrementY[3], -2);
		assertEquals (Board.m_incrementY[4], -2);
		assertEquals (Board.m_incrementY[5], -1);
		assertEquals (Board.m_incrementY[6], 1);
		assertEquals (Board.m_incrementY[7], 2);
	}
	@Test
	public void moves_test2b() throws Exception {
		assertEquals (Board.m_incrementX.length, 8);
		assertEquals (Board.m_incrementY.length, 8);
		assertEquals (Board.getMoveTypesCount(), 8);
	}


	@Test
	public void moves_test1() throws Exception {
		for (int i = 1; i <= Board.getMoveTypesCount(); i++)
			m_board.checkValidMove (i);
	}
	@Test (expected = Exception.class)
	public void moves_test1a() throws Exception {
		m_board.checkValidMove (0);
	}
	@Test (expected = Exception.class)
	public void moves_test1b() throws Exception {
		m_board.checkValidMove (-1);
	}
	@Test (expected = Exception.class)
	public void moves_test1c() throws Exception {
		m_board.checkValidMove (Board.getMoveTypesCount() + 1);
	}
}
