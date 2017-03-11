package com.idc.five.scoring;

import java.util.Iterator;

import com.idc.five.counting.Counting;
import com.idc.five.counting.CountsInfo;
import com.idc.five.counting.CountsItemInfo;
import com.idc.five.game.Board;
import com.idc.five.game.Coordinate;
import com.idc.five.output.Output;
import com.idc.five.players.Players;
import com.idc.five.utils.Utilities;

public class Scoring {

	private Players m_players;
	private Board m_board;
	private Counting m_counting;
	private Scores m_scores;
	private int m_currentPlayer;

	public Scoring (Players players, Board board, final int currentPlayer) {
		m_players = players;
		m_board = board;
		m_currentPlayer = currentPlayer;
		m_counting = new Counting (m_players, m_board, true);
		m_scores = calculateScores();
	}

	private Scores calculateScores() {
		Scores scores = new Scores (m_board);
		for (int row = 0; row < m_board.getRows(); row++) {
			for (int col = 0; col < m_board.getColumns(); col++) {
				if (m_board.isNotEmpty (row, col)) continue;
				for (int player = Players.PLAYER1; player <= Players.PLAYER2; player++) {
					int computeScore = computePossibleScore (m_currentPlayer, player, row, col);
					scores.incrementCount (player, row, col, computeScore);
				}
			}
		}
		return scores;
	}

	public void showCountsInfo (String msg, Output output) {
		m_counting.showCountsInfo (msg, output);
	}

	public Coordinate getHighestPossibleScoringMove() {
//		System.out.println(">>> Scoring::getHighestPossibleScoringMove; currentPlayer "+currentPlayer);
		assert Players.isValidPlayer(m_currentPlayer);

//		Output output = new OutputTTY();
//		m_counting.showCountsInfo ("Scoring::getHighestPossibleScoring", output);
//		showScores ("Scoring::getHighestPossibleScoring", m_board, scores);

		int highest = 0;
		int row_select = 0, col_select = 0;
		for (int row = 0; row < m_board.getRows(); row++) {
			for (int col = 0; col < m_board.getColumns(); col++) {
				int scoreMe = m_scores.getScore (m_currentPlayer, row, col);
				int scoreOther = m_scores.getScore (Players.whoIsOtherPlayer (m_currentPlayer), row, col);
//				System.out.println(" currentPlayer "+currentPlayer+" row "+row+" col "+col+
//						" scoreME "+scoreMe+" scoreOther "+scoreOther+" scoreTotal "+(scoreOther + scoreMe)+" highest "+highest);
				if (scoreMe >= getScoreWin()) return new Coordinate (row, col);		// I win
//				int choose = scoreOther > scoreMe ? scoreOther : scoreMe;
				int choose = scoreOther + scoreMe;
				if (choose > highest) {
					highest = choose;
					row_select = row;
					col_select = col;
				}
			}
		}
//		System.out.println("<<< Scoring::getHighestPossibleScoringMove; currentPlayer "+currentPlayer);
		return new Coordinate (row_select, col_select);
	}

	private int computePossibleScore (final int currentPlayer, final int player, final int row, final int col) {
//		System.out.println(">>> Scoring::computePossibleScore; player "+player+"("+row+","+col+")");
		int total = 0;
		boolean bDebug = false;
//		if (row == 0 && col == 0) bDebug = true;
//		if (row == 4 && col == 0) bDebug = true;
//		if (row == 0 && col == 4) bDebug = true;
//		if (row == 4 && col == 3) bDebug = true;
		boolean bCurrentPlayer = (player == currentPlayer);
		for (int actual = 0; actual <= m_board.getMaxSize(); actual++) {
			CountsInfo countsInfo = m_counting.getCountsInfo (player, row, col, actual);
			int how_many = countsInfo.getSize();
			if (how_many < 1) continue;
			if (how_many > 0) {
				if (bDebug)
					System.out.println("player "+player+"("+row+","+col+") actual "+actual+" how_many "+how_many+" countsInfo "+countsInfo);
			}
			Iterator<CountsItemInfo> iter = countsInfo.getItems();
			while (iter.hasNext()) {
				CountsItemInfo item = iter.next();
				int subTotal = computePossibleScoreInner (bCurrentPlayer, item, how_many);
				total += subTotal;
				if (bDebug)
					System.out.println("CountsItemInfo "+item+" subTotal "+subTotal+" total "+total);
			}
		}
//		System.out.println("<<< Scoring::computePossibleScore; total "+total);
		return total;
	}

	private int getScoringFactor() {
		return (m_board.getRows() - 4) + (m_board.getColumns() - 4);
	}
	private int getScoreWin() {
		return 100000 * getScoringFactor();
	}

	private int computePossibleScoreInner (final boolean bCurrentPlayer, final CountsItemInfo countsItemInfo, final int how_many) {

		final int SCORE_WIN_CURRENT = getScoreWin();
		final int SCORE_FOUR_CURRENT = 10000 * getScoringFactor();
		final int SCORE_THREE_CURRENT = 1000 * getScoringFactor();
		final int SCORE_TWO_CURRENT = 100 * getScoringFactor();

		final int SCORE_FOUR_CURRENT_BOUNDED = 1000 * getScoringFactor();
		final int SCORE_THREE_CURRENT_BOUNDED = 75 * getScoringFactor();
		final int SCORE_TWO_CURRENT_BOUNDED = 50 * getScoringFactor();

		final int SCORE_WIN_OTHER = 75000 * getScoringFactor();
		final int SCORE_FOUR_OTHER = 7500 * getScoringFactor();
		final int SCORE_THREE_OTHER = 125 * getScoringFactor();
		final int SCORE_TWO_OTHER = 75 * getScoringFactor();

		final int SCORE_FOUR_OTHER_BOUNDED = 750 * getScoringFactor();
		final int SCORE_THREE_OTHER_BOUNDED = 50 * getScoringFactor();
		final int SCORE_TWO_OTHER_BOUNDED = 25 * getScoringFactor();

		int scoring_multiplier = 1;
		/*
		switch (how_many) {
		case 4:
			scoring_multiplier = 10;
			break;
		case 3:
			scoring_multiplier = 8;
			break;
		case 2:
			scoring_multiplier = 3;
			break;
		case 1:
		default:
			break;
		}
		*/

		int total = 0;
		int possible = countsItemInfo.getPossible();
		if (possible >= 5) {
			int actual = countsItemInfo.getActual();
			if (bCurrentPlayer) {
				switch (actual) {
				case 4:
					if (countsItemInfo.isBounded())
						total += SCORE_FOUR_CURRENT_BOUNDED;
					else
						total += SCORE_FOUR_CURRENT;
					break;
				case 3:
					if (countsItemInfo.isBounded())
						total += SCORE_THREE_CURRENT_BOUNDED;
					else
						total += SCORE_THREE_CURRENT;
					break;
				case 2:
					if (countsItemInfo.isBounded())
						total += SCORE_TWO_CURRENT_BOUNDED;
					else
						total += SCORE_TWO_CURRENT;
					break;
				case 1:
					break;
				default:
					if (actual >= 5) total += SCORE_WIN_CURRENT;
					break;
				}
			}
			else {
				switch (actual) {
				case 4:
					if (countsItemInfo.isBounded())
						total += SCORE_FOUR_OTHER_BOUNDED;
					else
						total += SCORE_FOUR_OTHER;
					break;
				case 3:
					if (countsItemInfo.isBounded())
						total += SCORE_THREE_OTHER_BOUNDED;
					else
						total += SCORE_THREE_OTHER;
					break;
				case 2:
					if (countsItemInfo.isBounded())
						total += SCORE_TWO_OTHER_BOUNDED;
					else
						total += SCORE_TWO_OTHER;
					break;
				case 1:
					break;
				default:
					if (actual >= 5) total += SCORE_WIN_OTHER;
					break;
				}
			}
		}
		total *= scoring_multiplier;

		final int SCORE_POSSIBLE_ONE = 1;
		final int SCORE_POSSIBLE_TWO = 5;
		final int SCORE_POSSIBLE_THREE = 7;
		final int SCORE_POSSIBLE_FOUR = 10;
		final int SCORE_POSSIBLE_FIVE = 14;
		final int SCORE_POSSIBLE_SIX = 19;
		final int SCORE_POSSIBLE_SEVEN = 25;
		final int SCORE_POSSIBLE_EIGHT = 32;
		final int SCORE_POSSIBLE_NINE = 40;

		switch (possible) {
		case 9:
			total += SCORE_POSSIBLE_NINE;
			break;
		case 8:
			total += SCORE_POSSIBLE_EIGHT;
			break;
		case 7:
			total += SCORE_POSSIBLE_SEVEN;
			break;
		case 6:
			total += SCORE_POSSIBLE_SIX;
			break;
		case 5:
			total += SCORE_POSSIBLE_FIVE;
			break;
		case 4:
			total += SCORE_POSSIBLE_FOUR;
			break;
		case 3:
			total += SCORE_POSSIBLE_THREE;
			break;
		case 2:
			total += SCORE_POSSIBLE_TWO;
			break;
		case 1:
			total += SCORE_POSSIBLE_ONE;
			break;
		default:
			if (possible > 9) total += (possible - 9) * SCORE_POSSIBLE_NINE;
			break;
		}
		return total;
	}

	public void showScores (String msg, Output output) {
		output.println(">>> Scoring::showScores; "+msg);
		for (int player = Players.PLAYER1; player <= Players.PLAYER2; player++) {
			output.println ("Scores for "+m_players.getPlayerName(player));
			for (int r = 0; r < m_board.getRows(); r++) {
				boolean first = true;
				for (int c = 0; c < m_board.getColumns(); c++) {
					if (! first) output.print (",");
					first = false;
					if (m_board.isEmpty(r, c))
						output.print (Utilities.leadingSpacesPad (m_scores.getScore(player, r, c), 8));
					else {
						output.print (Utilities.leadingSpacesPad (m_players.getPlayerName (m_board.getPlayerAt(r, c)).substring(0, 1), 8));
//						output.print (leadingSpacesPad (Game.getPlayerName (board.getPlayerAt(r, c)).substring(0, 1) + " " + scores.getScore(player, r, c), 8));
					}
				}
				output.print("\n");
			}
		}
		output.println("<<< Scoring::showScores; "+msg);
	}

}
