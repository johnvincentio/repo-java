package com.idc.five.counting;

import com.idc.five.game.Board;
import com.idc.five.output.Output;
import com.idc.five.pattern.PatternUtils;
import com.idc.five.players.Players;
import com.idc.five.utils.Utilities;

public class Counting {
	private CountsItemInfo[][][][] m_countsAll;		// CountsItemInfo for each Player, Row, Column, Pattern

	private Players m_players;
	private Board m_board;

	public Counting (Players players, Board board) {
		this (players, board, true);
	}
	public Counting (Players players, Board board, boolean ignoreOccupied) {
		m_players = players;
		m_board = board;
		m_countsAll = new CountsItemInfo[Players.PLAYER2][m_board.getRows()][m_board.getColumns()][PatternUtils.getNumberOfVictoryPatterns()];
		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				if (ignoreOccupied && m_board.isNotEmpty(r, c)) continue;
				for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
					setCounts (Players.PLAYER1, r, c, pattern, calculateCounts (Players.PLAYER1, r, c, pattern));
					setCounts (Players.PLAYER2, r, c, pattern, calculateCounts (Players.PLAYER2, r, c, pattern));
				}
			}
		}
//		Output output = new OutputTTY();
//		showCountsInfo("Counting", output);
	}

	private void setCounts (int player, int row, int col, int pattern, CountsItemInfo countsItemInfo) {
//		System.out.println(">>> Counting::setCounts; player "+player+" pattern "+pattern);
		assert Players.isValidPlayer(player);
		assert m_board.isValidRow(row);
		assert m_board.isValidRow(col);
		assert PatternUtils.isValidPattern(pattern);
		m_countsAll[player - 1][row][col][pattern] = countsItemInfo;
//		System.out.println("<<< Counting::setCounts");
	}

	public CountsInfo getCountsInfo (int player, int row, int col, int actual) {
		assert Players.isValidPlayer(player);
		assert m_board.isValidRow(row);
		assert m_board.isValidRow(col);
		assert actual >= 0;
		CountsInfo countsInfo = new CountsInfo();
		for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
			CountsItemInfo countsItemInfo = getCounts (player, row, col, pattern);
			if (countsItemInfo == null) continue;
			if (countsItemInfo.getActual() == actual) countsInfo.add (countsItemInfo);
		}
		return countsInfo;
	}

	public CountsItemInfo getCounts (int player, int row, int col, int pattern) {
//		System.out.println(">>> Counting::getCounts; player "+player+" pattern "+pattern);
		assert Players.isValidPlayer(player);
		assert m_board.isValidRow(row);
		assert m_board.isValidRow(col);
		assert PatternUtils.isValidPattern(pattern);
//		System.out.println("<<< Counting::getCounts");
		return m_countsAll[player - 1][row][col][pattern];
	}


	/*
	 * Create the CountsItemInfo object; various counts
	 */
	private CountsItemInfo calculateCounts (int player, int r, int c, int pattern) {
//		System.out.println(">>> calculateCounts; player "+player+" r "+r+" c "+c+" pattern "+pattern);
		assert Players.isValidPlayer(player);
		assert m_board.isValidRow(r);
		assert m_board.isValidRow(c);
		assert PatternUtils.isValidPattern(pattern);
		
//		System.out.println("<<< calculateCounts");
		return new CountsItemInfo (
				calculateActualCount (player, r, c, pattern),
				calculatePossibleCount (player, r, c, pattern),
				isBorder (player, r, c, pattern),
				isActualBounded (player, r, c, pattern));
	}

	private int calculateActualCount (int player, int r, int c, int pattern) {
//		System.out.println(">>> calculateActualCount; player "+player+" r "+r+" c "+c+" pattern "+pattern);
		int count = 1;
		for (int dir = -1; dir < 2; dir++, dir++) {
			for (int i = 1; i < m_board.getMaxSize(); i++) {
				int row = r + dir * PatternUtils.getVictoryPatternRow (pattern) * i;
				int col = c + dir * PatternUtils.getVictoryPatternCol (pattern) * i;
//				System.out.println("dir "+dir+" row "+row+" col "+col);
				if (row < 0 || row >= m_board.getRows()) break;
				if (col < 0 || col >= m_board.getColumns()) break;
				if (! m_board.isPlayerAt (player, row, col)) break;
				count++;
			}
//			System.out.println("calculateActualCount; count "+count);
		}
//		System.out.println("<<< calculateActualCount; count "+count);
		return count;
	}

	private int calculatePossibleCount (int player, int r, int c, int pattern) {
//		System.out.println(">>> calculatePossibleCount; player "+player+" r "+r+" c "+c+" pattern "+pattern);
		int count = 1;
		for (int dir = -1; dir < 2; dir++, dir++) {
			for (int i = 1; i < m_board.getMaxSize(); i++) {
				int row = r + dir * PatternUtils.getVictoryPatternRow (pattern) * i;
				int col = c + dir * PatternUtils.getVictoryPatternCol (pattern) * i;
//				System.out.println("dir "+dir+" row "+row+" col "+col);
				if (row < 0 || row >= m_board.getRows()) break;
				if (col < 0 || col >= m_board.getColumns()) break;
				if (m_board.isOtherPlayerAt (player, row, col)) break;
				count++;
			}
//			System.out.println("calculatePossibleCount; count "+count);
		}
//		System.out.println("<<< calculatePossibleCount; count "+count);
		return count;
	}

	private boolean isBorder (int player, int r, int c, int pattern) {
		for (int dir = -1; dir < 2; dir++, dir++) {
			int row = r + dir * PatternUtils.getVictoryPatternRow (pattern);
			int col = c + dir * PatternUtils.getVictoryPatternCol (pattern);
			if (row < 0 || row >= m_board.getRows()) return true;
			if (col < 0 || col >= m_board.getColumns()) return true;
			if (m_board.isOtherPlayerAt (player, row, col)) return true;
		}
		return false;
	}

	private boolean isActualBounded (int player, int r, int c, int pattern) {
//		System.out.println(">>> isActualBounded; player "+player+" r "+r+" c "+c+" pattern "+pattern);
		for (int dir = -1; dir < 2; dir++, dir++) {
			for (int i = 1; i < m_board.getMaxSize(); i++) {
				int row = r + dir * PatternUtils.getVictoryPatternRow (pattern) * i;
				int col = c + dir * PatternUtils.getVictoryPatternCol (pattern) * i;
//				System.out.println("dir "+dir+" row "+row+" col "+col);
				if (row < 0 || row >= m_board.getRows()) return true;
				if (col < 0 || col >= m_board.getColumns()) return true;
				if (m_board.isOtherPlayerAt (player, row, col)) return true;
				if (m_board.isEmpty (row, col)) break;
			}
//			System.out.println("isActualBounded; count "+count);
		}
//		System.out.println("<<< isActualBounded; count "+count);
		return false;
	}

	/**
	 * Display the CountsInfo object.
	 * 
	 * @param msg	Message for the display
	 */
	public void showCountsInfo (String msg, Output output) {
		output.println(">>> Counting::showCountsInfo; "+msg);
		for (int player = Players.PLAYER1; player <= Players.PLAYER2; player++) {
			output.println ("Counts for " + m_players.getPlayerName(player));
			for (int r = 0; r < m_board.getRows(); r++) {
				for (int line = 1; line < 3; line++) {
					if (line == 2) output.print ("  ");
					boolean first = true;
					for (int c = 0; c < m_board.getColumns(); c++) {
						if (! first) output.print (",  ");
						first = false;
						for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
							CountsItemInfo counts = getCounts (player, r, c, pattern);
							if (counts == null) {
								if (! m_board.isEmpty(r, c)) {
									String name = Utilities.repeatString (m_players.getPlayerName (m_board.getPlayerAt(r, c)).substring(0, 1), 5);
									output.print(name);
								}
								else
									output.print("xxxxxxxxx");
							}
							else {
								if (line == 1)
									output.print (Utilities.leadingSpacesPad ("("+counts.getActual()+","+counts.getPossible()+")", 3));
								else
									output.print (Utilities.leadingSpacesPad ("("+counts.getBorder()+","+counts.getBounded()+")", 4));
							}
						}
					}
					output.print("\n");
				}
			}
		}
		output.println("<<< Counting::showCountsInfo; "+msg);
	}
}
