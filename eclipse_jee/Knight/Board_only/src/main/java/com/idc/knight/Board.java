package com.idc.knight;

import org.apache.log4j.Logger;

public class Board {
	private static Logger logger = Logger.getRootLogger();

	private int m_count_countExits = 0;
	private int m_cntr_countExits = 0;

	private int m_cntr_isConnected = 0;

	private int m_one_exit_count_isPossible = 0;
	private int m_count_exits_isPossible = 0;
	private int m_x_isPossible = 0;
	private int m_y_isPossible = 0;

	private int m_cntr_getNextValidMove = 0;

	private Square m_toSquare_moveBackward = null;

	private Square m_fromSquare_moveForward = null;
	private Square m_toSquare_moveForward = null;
	private int m_moveCounter_moveForward = 0;

	protected static final int[] m_incrementX = {1,2,2,1,-1,-2,-2,-1};
	protected static final int[] m_incrementY = {2,1,-1,-2,-2,-1,1,2};
	public static int getMoveTypesCount() {
		return m_incrementX.length;
	}
	public void checkValidMove (int moveAwayType) throws Exception {
		if (moveAwayType < 1 || moveAwayType > getMoveTypesCount()) throw new Exception ("Board.checkValidMove; Invalid moveAwayType value of "+moveAwayType);
	}

	public void getPos (int x, int y, int moveAwayType, Pair toPair) throws Exception {
		checkValidMove (moveAwayType);
		toPair.set (x + m_incrementX[moveAwayType - 1], y + m_incrementY[moveAwayType - 1]);
	}
	public void getPos (Pair fromPair, int moveAwayType, Pair toPair) throws Exception {
		getPos (fromPair.getX(), fromPair.getY(), moveAwayType, toPair);
	}

	private Square m_squares[][];

	public int getMaxMoveCounter() {return (m_squares.length * m_squares[0].length) - 1;}

	public void setBoard (Pair size) throws Exception {
		int x = size.getX();
		int y = size.getY();
		if (x < 3) throw new Exception ("Board (constructor); Invalid x value of "+x);
		if (y < 3) throw new Exception ("Board (constructor); Invalid y value of "+y);
		m_squares = new Square[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				m_squares[i][j] = new Square();
			}
		}
	}
	protected Square getSquare (Pair pair) throws Exception {
		checkValidSquare (pair);
		return m_squares[pair.getX()][pair.getY()];
	}
	public void checkValidSquare (Pair pair) throws Exception {
		int x = pair.getX();
		int y = pair.getY();
		if (x < 0 || x >= m_squares.length) throw new Exception ("Board.checkValidSquare; Invalid x value of "+x);
		if (y < 0 || y >= m_squares[0].length) throw new Exception ("Board.checkValidSquare; Invalid y value of "+y);
	}
	public boolean isValidSquare (Pair pair) {
		int x = pair.getX();
		int y = pair.getY();
		if (x < 0 || x >= m_squares.length) return false;
		if (y < 0 || y >= m_squares[0].length) return false;
		return true;
	}
	public void checkMoveCounter (int moveCounter) throws Exception {
		if (moveCounter < 0 || moveCounter > m_squares.length * m_squares[0].length - 1) throw new Exception ("Board.checkMoveCounter; Invalid moveCounter value of "+moveCounter);
	}

	public boolean isOccupied (Pair pair) throws Exception {
		return getSquare (pair).isOccupied();
	}

	public void addFirstMove (int moveCounter, Pair start) throws Exception {
		checkMoveCounter (moveCounter);
		Square square = getSquare (start);
		square.setFrom (start);
		square.setMoveCounter (moveCounter);
	}

	public int getNextValidMove (Pair fromPair, Pair tempPair) throws Exception {
		if (! isOccupied (fromPair)) throw new Exception ("Board.getNextValidMove; from "+fromPair.toString()+" is not already occupied");
		for (m_cntr_getNextValidMove = (getSquare (fromPair).getMoveAwayType() + 1); m_cntr_getNextValidMove <= getMoveTypesCount(); m_cntr_getNextValidMove++) {
			getPos (fromPair, m_cntr_getNextValidMove, tempPair);
			if (! isValidSquare (tempPair)) continue;
			if (isOccupied (tempPair)) continue;
			return m_cntr_getNextValidMove;
		}
		return -1;
	}

	public int getX (Pair pair) throws Exception {
		return getSquare (pair).getFrom().getX();
	}
	public int getY (Pair pair) throws Exception {
		return getSquare (pair).getFrom().getY();
	}
	public int getMoveCounter (Pair pair) throws Exception {
		return getSquare (pair).getMoveCounter();
	}
	public int getMoveAwayType (Pair pair) throws Exception {
		return getSquare (pair).getMoveAwayType();
	}

	public void moveForward (Pair fromPair, int moveAwayType, Pair toPair) throws Exception {
//		System.out.println(">>> moveForward; fromPair "+fromPair+" moveAwayType "+moveAwayType);
		m_fromSquare_moveForward = getSquare (fromPair);
		if (! isOccupied (fromPair)) throw new Exception ("Board.moveForward; from "+fromPair.toString()+" is not already occupied");
		checkValidMove (moveAwayType);
		m_moveCounter_moveForward = m_fromSquare_moveForward.getMoveCounter();
		checkMoveCounter (m_moveCounter_moveForward);
		m_fromSquare_moveForward.setMoveAwayType (moveAwayType);
		m_fromSquare_moveForward.checkValid();

		getPos (fromPair, moveAwayType, toPair);
		m_toSquare_moveForward = getSquare (toPair);
		if (isOccupied (toPair)) throw new Exception ("Board.moveForward; to "+toPair.toString()+" is already occupied");
		checkMoveCounter (++m_moveCounter_moveForward);
		m_toSquare_moveForward.setMoveCounter (m_moveCounter_moveForward);
		m_toSquare_moveForward.setFrom (fromPair);
		m_toSquare_moveForward.checkValid();
//		System.out.println("<<< moveForward; toPair "+toPair);
		return;
	}
	public void moveBackward (Pair pair) throws Exception {
//		System.out.println(">>> moveBackward; "+pair.toString());
		m_toSquare_moveBackward = getSquare (pair);
//		System.out.println("(1) toSquare "+toSquare);
		pair.set (m_toSquare_moveBackward.getFrom());
//		Square fromSquare = getSquare (pair);
//		System.out.println("(1) fromSquare "+fromSquare);
		m_toSquare_moveBackward.init();
//		System.out.println("(2) toSquare "+toSquare);
//		System.out.println("(2) fromSquare "+fromSquare);
//		System.out.println("<<< moveBackward; "+pair.toString());
		return; 
	}
	public boolean isAnyMoreMoves (Pair fromPair) throws Exception {
		return getSquare (fromPair).getMoveCounter() > 0;
	}
	public boolean isMovesComplete (int moveCounter) {
		if (moveCounter > (m_squares.length * m_squares[0].length) - 2) return true;
		return false;
	}

	public boolean isPossible (Pair from, int moveCounter, Pair tempPair) throws Exception {
//		logger.debug (">>> isPossible; from "+from+" moveCounter "+moveCounter);
//		printBoard (">>> isPossible; from "+from+" moveCounter "+moveCounter);
		if (moveCounter < 2) return true;
		if (moveCounter > getMaxMoveCounter() - 5) return true;
		m_one_exit_count_isPossible = 0;
		for (m_y_isPossible = m_squares[0].length - 1; m_y_isPossible >= 0; m_y_isPossible--) {
			for (m_x_isPossible = 0; m_x_isPossible < m_squares.length; m_x_isPossible++) {
				if (m_squares[m_x_isPossible][m_y_isPossible].getMoveCounter() > -1) continue;		// if occupied, ignore
				m_count_exits_isPossible = countExits (m_x_isPossible, m_y_isPossible, tempPair);
				if (m_count_exits_isPossible < 1) {
//					logger.debug ("*** ("+x+","+y+") is not possible");
					return false;
				}
				if (m_count_exits_isPossible < 2) {
//					logger.debug ("--- ("+x+","+y+") has only one exit");
					if (! isConnected (from, m_x_isPossible, m_y_isPossible)) m_one_exit_count_isPossible++;
					if (m_one_exit_count_isPossible > 1) {
//						logger.debug ("*** more than one square has only one exit");
						return false;
					}
				}
			}
		}
		return true;
	}
	public boolean isConnected (Pair fromPair, int x, int y) throws Exception {
//		logger.debug (">>> isConnected; fromPair "+fromPair+" x "+x+" y "+y);
		for (m_cntr_isConnected = 1; m_cntr_isConnected <= getMoveTypesCount(); m_cntr_isConnected++) {
			checkValidMove (m_cntr_isConnected);
			if (x == (fromPair.getX() + m_incrementX[m_cntr_isConnected - 1]) && y == (fromPair.getY() + m_incrementY[m_cntr_isConnected - 1])) return true;
		}
//		logger.debug ("<<< isConnected; fromPair "+fromPair+" x "+x+" y "+y);
		return false;
	}
	private int countExits (int x, int y, Pair tempPair) throws Exception {
//		logger.debug (">>> countExits; ("+x+","+y+")");
		m_count_countExits = 0;
		for (m_cntr_countExits = 1; m_cntr_countExits <= getMoveTypesCount(); m_cntr_countExits++) {
			getPos (x, y, m_cntr_countExits, tempPair);
			if (! isValidSquare (tempPair)) continue;
			if (isOccupied (tempPair)) continue;
			m_count_countExits++;
		}
//		logger.debug ("<<< countExits; "+count);
		return m_count_countExits;
	}
	public void printBoard (String message) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(">>> ").append(message).append("\n");
		for (int y = m_squares[0].length - 1; y >= 0; y--) {
			for (int x = 0; x < m_squares.length; x++) {
				Square square = m_squares[x][y];
				int moveCounter = square.getMoveCounter();
				if (moveCounter > -1 && moveCounter < 10) sb.append(' ');
				sb.append (moveCounter);
				sb.append ("(").append(square.getMoveAwayType()).append(")");
				sb.append ("(");
				Pair from = square.getFrom();
				if (from.getX() > -1 && from.getX() < 10) sb.append(' ');
				sb.append (from.getX());
				sb.append (",");
				if (from.getY() > -1 && from.getY() < 10) sb.append(' ');
				sb.append (from.getY());
				sb.append ("),");
			}
			sb.append ("\n");
		}
		sb.append("<<< ").append(message).append("\n");
		logger.debug (sb.toString());
	}
}
