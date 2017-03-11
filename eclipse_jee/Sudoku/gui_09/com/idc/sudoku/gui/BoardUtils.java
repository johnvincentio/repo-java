package com.idc.sudoku.gui;

import com.idc.trace.LogHelper;

public class BoardUtils {

	public static void calculateOptions(Square[][] squares) {
		LogHelper.debug (">>> BoardUtils::calculateOptions");
		recalculateOptions(squares);		// general buckets calculator
		boolean bChangedOptions = true;
		int nMatching = 2;
		while (bChangedOptions) {
			bChangedOptions = recalculateSwitches_1 (squares, nMatching);
			if (! bChangedOptions) {
				bChangedOptions = recalculateSwitches_2 (squares);
			}
		}
		LogHelper.debug ("<<< BoardUtils::calculateOptions");
	}

	private static void recalculateOptions(Square[][] squares) {
		LogHelper.debug (">>> BoardUtils::recalculateOptions");
		Switches switches = new Switches();		// calculate options for every square
		for (int row = Boardgui.getMinSquare(); row <= Boardgui.getMaxSquare(); row++) {
			for (int col = Boardgui.getMinSquare(); col <= Boardgui.getMaxSquare(); col++) {
				switches.setOn();
				switches = getRowSwitches(squares, row);
				switches.addOff (getColumnSwitches(squares, col));
				switches.addOff (getLocalSquareSwitches(squares, row, col));
//				switches.show("CalculateOptions before");
				squares[row][col].setSwitches(switches);
//				switches.show("CalculateOptions after");
			}
		}
		LogHelper.debug ("<<< BoardUtils::recalculateOptions");
	}

	private static boolean recalculateSwitches_2(Square[][] squares) {
		LogHelper.debug (">>> BoardUtils::recalculateSwitches_2");
		Square sq;
		for (int row = Boardgui.getMinSquare(); row <= Boardgui.getMaxSquare(); row++) {
			for (int col = Boardgui.getMinSquare(); col <= Boardgui.getMaxSquare(); col++) {
				sq = squares[row][col];
				if (sq.isOccupied()) continue;
				Switches switches = sq.getSwitches();
				for (int pos=Switches.MIN_SWITCH; pos<=Switches.MAX_SWITCH; pos++) {
					if (! switches.get(pos)) continue;
					if (getRowCountForBucket (squares, row, pos) == 1) {
//LogHelper.debug("(type 101);  pos "+pos+" row "+row+" col "+col+" :"+sq.toString()+":");
						sq.getSwitches().setOff();
						sq.getSwitches().setOn(pos);
					}
					if (getColumnCountForBucket (squares, col, pos) == 1) {
//LogHelper.debug("(type 102);  pos "+pos+" row "+row+" col "+col+" :"+sq.toString()+":");
						sq.getSwitches().setOff();
						sq.getSwitches().setOn(pos);
					}
					if (getLocalSquareCountForBucket (squares, row, col, pos) == 1) {
//LogHelper.debug("(type 103);  pos "+pos+" row "+row+" col "+col+" :"+sq.toString()+":");
						sq.getSwitches().setOff();
						sq.getSwitches().setOn(pos);
					}
				}
			}
		}
		LogHelper.debug ("<<< BoardUtils::recalculateSwitches_2");
		return false;
	}

	private static boolean recalculateSwitches_1(Square[][] squares, int nMatching) {
		LogHelper.debug (">>> BoardUtils::recalculateSwitches_1");
		String pattern;
		Square sq;
		for (int row = Boardgui.getMinSquare(); row <= Boardgui.getMaxSquare(); row++) {
			for (int col = Boardgui.getMinSquare(); col <= Boardgui.getMaxSquare(); col++) {
				sq = squares[row][col];
				if (sq.isOccupied()) continue;
				if (sq.getSwitches().getCountSwitchesSet() == nMatching) {
//LogHelper.debug("Current Match "+nMatching+" ; row "+row+" col "+col+" :"+sq.toString()+":");				
					pattern = sq.getStringSetSwitches();		// set pattern
					if (getRowCountForBucket (squares, row, pattern) == nMatching) {
						if (removePatternFromBucketForRow(squares, row, pattern)) return true;
					}
					if (getColumnCountForBucket (squares, col, pattern) == nMatching) {
						if (removePatternFromBucketForColumn(squares, col, pattern)) return true;
					}
					if (getLocalSquareCountForBucket (squares, row, col, pattern) == nMatching) {
						if (removePatternFromBucketForLocalSquare(squares, row, col, pattern)) return true;
					}
				}
			}
		}
		LogHelper.debug ("<<< BoardUtils::recalculateSwitches_1");
		return false;
	}

	private static boolean removePatternFromBucketForRow (Square[][] squares, int row, String pattern) {
		LogHelper.debug (">>> BoardUtils::removePatternFromBucketForRow");
//		LogHelper.debug ("Removing pattern :"+pattern+" from row "+row);
		Square sq;
		boolean bChange = false;
		for (int col=Boardgui.getMinSquare(); col<=Boardgui.getMaxSquare(); col++) {
			sq = squares[row][col];
			if (sq.isOccupied()) continue;
			if (pattern.equals(sq.getStringSetSwitches())) continue;		// can't change oneself.
//			LogHelper.debug(" (before remove) row "+row+" col "+col+" :"+sq.toString()+":");
			if (sq.setSwitchesOff(pattern)) bChange = true;
//			LogHelper.debug(" (after remove) row "+row+" col "+col+" :"+sq.toString()+":");
		}
		LogHelper.debug ("<<< BoardUtils::removePatternFromBucketForRow");
		return bChange;
	}

	private static boolean removePatternFromBucketForColumn (Square[][] squares, int col, String pattern) {
		LogHelper.debug (">>> BoardUtils::removePatternFromBucketForColumn");
//		LogHelper.debug("Removing pattern :"+pattern+" from column "+col);
		Square sq;
		boolean bChange = false;
		for (int row=Boardgui.getMinSquare(); row<=Boardgui.getMaxSquare(); row++) {
			sq = squares[row][col];
			if (sq.isOccupied()) continue;
			if (pattern.equals(sq.getStringSetSwitches())) continue;		// can't change oneself.
//			LogHelper.debug(" (before remove) row "+row+" col "+col+" :"+sq.toString()+":");
			if (sq.setSwitchesOff(pattern)) bChange = true;
//			LogHelper.debug(" (after remove) row "+row+" col "+col+" :"+sq.toString()+":");
		}
		LogHelper.debug ("<<< BoardUtils::removePatternFromBucketForColumn");
		return bChange;
	}

	private static boolean removePatternFromBucketForLocalSquare (Square[][] squares, int row, int col, String pattern) {
		LogHelper.debug (">>> BoardUtils::removePatternFromBucketForLocalSquare");
//		LogHelper.debug("Removing pattern :"+pattern+" from row "+row+" column "+col);
		int xMin = Boardgui.getMinPos(row);
		int xMax = Boardgui.getMaxPos(row);
		int yMin = Boardgui.getMinPos(col);
		int yMax = Boardgui.getMaxPos(col);
//		LogHelper.debug("x ("+xMin+","+xMax+") y ("+yMin+","+yMax+")");
		Square sq;
		boolean bChange = false;
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				sq = squares[x][y];
				if (sq.isOccupied()) continue;
				if (pattern.equals(sq.getStringSetSwitches())) continue;		// can't change oneself.
//				LogHelper.debug(" (before remove) row "+row+" col "+col+" :"+sq.toString()+":");
				if (sq.setSwitchesOff(pattern)) bChange = true;
//				LogHelper.debug(" (after remove) row "+row+" col "+col+" :"+sq.toString()+":");
			}
		}
		LogHelper.debug ("<<< BoardUtils::removePatternFromBucketForLocalSquare");
		return bChange;
	}

	private static int getRowCountForBucket (Square[][] squares, int row, String pattern) {
		int cntr = 0;
		for (int col=Boardgui.getMinSquare(); col<=Boardgui.getMaxSquare(); col++) {
			if (squares[row][col].isOccupied()) continue;
			if (pattern.equals(squares[row][col].getStringSetSwitches())) cntr++;
		}
		return cntr;
	}

	private static int getColumnCountForBucket (Square[][] squares, int col, String pattern) {
		int cntr = 0;
		for (int row=Boardgui.getMinSquare(); row<=Boardgui.getMaxSquare(); row++) {
			if (squares[row][col].isOccupied()) continue;
			if (pattern.equals(squares[row][col].getStringSetSwitches())) cntr++;
		}
		return cntr;
	}

	private static int getLocalSquareCountForBucket (Square[][] squares, int row, int col, String pattern) {
		int xMin = Boardgui.getMinPos(row);
		int xMax = Boardgui.getMaxPos(row);
		int yMin = Boardgui.getMinPos(col);
		int yMax = Boardgui.getMaxPos(col);
//		LogHelper.debug("x ("+xMin+","+xMax+") y ("+yMin+","+yMax+")");
		int cntr = 0;
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				if (squares[x][y].isOccupied()) continue;
				if (pattern.equals(squares[x][y].getStringSetSwitches())) cntr++;
			}
		}
		return cntr;
	}

	private static int getRowCountForBucket (Square[][] squares, int row, int num) {
		int cntr = 0;
		for (int col=Boardgui.getMinSquare(); col<=Boardgui.getMaxSquare(); col++) {
			if (squares[row][col].isOccupied()) continue;
			if (squares[row][col].getSwitches().get(num)) cntr++;
		}
		return cntr;
	}

	private static int getColumnCountForBucket (Square[][] squares, int col, int num) {
		int cntr = 0;
		for (int row=Boardgui.getMinSquare(); row<=Boardgui.getMaxSquare(); row++) {
			if (squares[row][col].isOccupied()) continue;
			if (squares[row][col].getSwitches().get(num)) cntr++;
		}
		return cntr;
	}

	private static int getLocalSquareCountForBucket (Square[][] squares, int row, int col, int num) {
		int xMin = Boardgui.getMinPos(row);
		int xMax = Boardgui.getMaxPos(row);
		int yMin = Boardgui.getMinPos(col);
		int yMax = Boardgui.getMaxPos(col);
//		LogHelper.debug("x ("+xMin+","+xMax+") y ("+yMin+","+yMax+")");
		int cntr = 0;
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				if (squares[x][y].isOccupied()) continue;
				if (squares[x][y].getSwitches().get(num)) cntr++;
			}
		}
		return cntr;
	}

	private static Switches getRowSwitches (Square[][] squares, int row) {
		int num;
		Switches switches = new Switches();
		for (int col=Boardgui.getMinSquare(); col<=Boardgui.getMaxSquare(); col++) {
			num = squares[row][col].getIntSquareValue();
//			LogHelper.debug("row "+row+" col "+col+" num "+num);
			if (num > 0) switches.setOff (num);
		}
//		switches.show("getRowSwitches; row "+row);
		return switches;
	}

	private static Switches getColumnSwitches (Square[][] squares, int col) {
		int num;
		Switches switches = new Switches();
		for (int row=Boardgui.getMinSquare(); row<=Boardgui.getMaxSquare(); row++) {
			num = squares[row][col].getIntSquareValue();
//			LogHelper.debug("col "+col+" row "+row+" num "+num);
			if (num > 0) switches.setOff (num);
		}
//		switches.show("getColumnSwitches; column "+col);
		return switches;
	}

	private static Switches getLocalSquareSwitches (Square[][] squares, int row, int col) {
		int xMin = Boardgui.getMinPos(row);
		int xMax = Boardgui.getMaxPos(row);
		int yMin = Boardgui.getMinPos(col);
		int yMax = Boardgui.getMaxPos(col);
//		LogHelper.debug("x ("+xMin+","+xMax+") y ("+yMin+","+yMax+")");
		int num;
		Switches switches = new Switches();
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				num = squares[x][y].getIntSquareValue();
				if (num > 0) switches.setOff (num);
			}
		}
//		switches.show("getLocalSquareSwitches; row "+row+" col "+col);
		return switches;
	}
}
