package com.idc.sudoku.gui;

import java.io.File;

import javax.swing.SwingUtilities;

import com.idc.sudoku.Board;
import com.idc.sudoku.Brute;
import com.idc.sudoku.Utils;
import com.idc.trace.LogHelper;

public class Boardgui {
	public static final int MIN_SQUARE = 1;
	public static final int MAX_SQUARE = 9;
	private static final int NUM_SQUARES = MAX_SQUARE - MIN_SQUARE + 2;

	private Appgui m_appgui;
	private Square m_squares[][];   		// sudoku grid.
	public Boardgui (Appgui appgui) {
		m_appgui = appgui;
		m_squares = new Square[NUM_SQUARES][NUM_SQUARES];
		for (int row = getMinSquare(); row <= getMaxSquare(); row++) {
			for (int col = getMinSquare(); col <= getMaxSquare(); col++) {
				m_squares[row][col] = new Square(row, col);
			}
		}
	}
	public static int getMinSquare() {return MIN_SQUARE;}
	public static int getMaxSquare() {return MAX_SQUARE;}
	private static int getLocalSquareNumber (int num) {return ((num-1)/3)+1;}
	public static int getMinPos(int pos) {return (getLocalSquareNumber(pos)*3)-2;}
	public static int getMaxPos(int pos) {return (getLocalSquareNumber(pos)*3);}

	public static int getMinLocal() {return 1;}
	public static int getMaxLocal() {return MAX_SQUARE / 3;}
	public static int getMinFromLocal(int pos) {return (pos*3)-2;}
	public static int getMaxFromLocal(int pos) {return pos*3;}

	private Square[][] getSquares() {return m_squares;}
	public Square getSquare (int row, int col) {return m_squares[row][col];}
	public void loadBoard(File file) {SavedBoards.load (file, getSquares());}
	public void saveBoard(File file) {SavedBoards.save (file, getSquares());}

	private void boardRedraw() {
		LogHelper.debug(">>> Boardgui::boardRedraw");
		for (int row = getMinSquare(); row <= getMaxSquare(); row++) {		// redraw the board
			for (int col = getMinSquare(); col <= getMaxSquare(); col++) {
				squareRedraw(m_squares[row][col]);		
			}
		}
		LogHelper.debug("<<< Boardgui::boardRedraw");
	}

	private void squareRedraw (final Square sq) {
		SwingUtilities.invokeLater (
			new Runnable() { 
				public void run() {
					sq.redraw();
				}
			}
		);
	}

	private void calculateOptions() {BoardUtils.calculateOptions(getSquares());}

	public boolean doStart() {
		LogHelper.debug(">>> Boardgui::doStart");
		if (! Utils.isThisSituationPossible(makeBoard()))
			return false;
		LogHelper.debug("isThisSituationPossible - true");
		Square sq;
		for (int row = getMinSquare(); row <= getMaxSquare(); row++) {
			for (int col = getMinSquare(); col <= getMaxSquare(); col++) {
				sq = m_squares[row][col];
				sq.setStartIntSquareValue(sq.getIntSquareUser());
			}
		}
		calculateOptions();
		boardRedraw();
//		boardOutput();
		LogHelper.debug("<<< Boardgui::doStart");
		return true;
	}

	public void doMove() {
//		LogHelper.debug(">>> Appgui::doMove");
		Square sq;
		int num1, num2;
		m_appgui.setStatusMessage("");
		for (int row = getMinSquare(); row <= getMaxSquare(); row++) {
			for (int col = getMinSquare(); col <= getMaxSquare(); col++) {
				sq = m_squares[row][col];
				num1 = sq.getIntSquareValue();
				num2 = sq.getIntSquareUser();
//				LogHelper.debug("row "+row+" col "+col+" num1 "+num1+" num2 "+num2);
				if (sq.isFixed()) {
//					LogHelper.debug("reset to fixed value");
					sq.setIntSquareValue(num1);				
				}
				else {
					if (num1 != num2) {	// user entered a value
						if (num2 < Switches.MIN_SWITCH || num2 > Switches.MAX_SWITCH ||	// is it in range
							(! sq.getSwitches().get(num2))) { 			// is it a valid option
//							LogHelper.debug("invalid option");
							sq.setIntSquareUser(num1);// change is not valid, reset to old value
							sq.setIntSquareValue(num1);
							m_appgui.setStatusMessage ("("+row+","+col+") value "+num2+" is invalid. Reset to "+num1);
						}
						else {					
//							LogHelper.debug("valid option");
							sq.setIntSquareValue(num2);		// make the change
							m_appgui.makeStatusMessage (row, col, num2);
						}
					}					
				}
//				LogHelper.debug("before calculateOptions; row "+row+" col "+col);
				calculateOptions();	// user may have made multiple moves
			}
		}
		boardRedraw();
//		LogHelper.debug("<<< Appgui::doMove");
	}

	public boolean doComputer() {	// set first square that has only one possible value
		Square sq;
		boolean bFound = false;
//LogHelper.debug("doComputer");
		for (int row = getMinSquare(); row <= getMaxSquare(); row++) {
			for (int col = getMinSquare(); col <= getMaxSquare(); col++) {
				sq = m_squares[row][col];
				if (sq.isOccupied()) continue;
//LogHelper.debug(" row "+row+" col "+col+" :"+sq.toString()+":");
				if (sq.getSwitches().getCountSwitchesSet() == 1) {
					for (int pos=Switches.MIN_SWITCH; pos<=Switches.MAX_SWITCH; pos++) {
						if (sq.getSwitches().get(pos)) { // must be the one
							m_appgui.makeStatusMessage (row, col, pos);
//LogHelper.debug("Found single; row "+row+" col "+col+" value "+pos);
							sq.setIntSquareValue(pos);
							bFound = true;
							break;
						}
					}
					if (bFound) break;
				}
				if (bFound) break;
			}
			if (bFound) break;
		}
		calculateOptions();
		boardRedraw();
		if (! bFound) m_appgui.setStatusMessage ("No Move Available");
		return bFound;
	}

	public void doMaxMoves() {	// make all possible moves for squares with only one possible value
		boolean bChanged = true;
		while (bChanged) {
			bChanged = doComputer();
		}	
	}

	public void doBrute() {
		Brute brute = new Brute(this);
		Board board = brute.doBrute();
		Square sq;
		int num;
		for (int row = getMinSquare(); row <= getMaxSquare(); row++) {
			for (int col = getMinSquare(); col <= getMaxSquare(); col++) {
				sq = m_squares[row][col];
				if (sq.isOccupied()) continue;
				num = board.get(col-1, row-1);
				sq.setIntSquareValue(num);
//LogHelper.debug(" row "+row+" col "+col+" :"+sq.toString()+":");
			}
		}
		boardRedraw();
	}

	private Board makeBoard() {
		Board board = new Board();
		int num;
		for (int row = getMinSquare(); row <= getMaxSquare(); row++) {
			for (int col = getMinSquare(); col <= getMaxSquare(); col++) {
				num = m_squares[row][col].getIntSquareUser();
				board.set(row-1,col-1,num);
			}
		}
//		board.show();
		return board;
	}
}
