package com.idc.five.totaling;

import com.idc.five.game.Board;
import com.idc.five.output.Output;
import com.idc.five.pattern.PatternUtils;
import com.idc.five.players.Players;

public class Totaling {

	private Players m_players;
	private Board m_board;

	private TotalsAll m_totalsAll;
	private TotalsAll m_scoresAll;
	private Totals m_totals;

	public Totaling (Players players, Board board) {
		m_players = players;
		m_board = board;
		
		m_totalsAll = createTotalsAllForActualCounts();
		m_scoresAll = createTotalsAllForCurrentScores (m_totalsAll);

		m_totals = createTotals (m_totalsAll, m_scoresAll);
	}

	public void showTotalsAll (String msg, Output output) {
		m_totalsAll.showTotalsAll (msg, output);
	}
	public void showScoresAll (String msg, Output output) {
		m_scoresAll.showTotalsAll (msg, output);
	}
	public void showTotals (String msg, Output output) {
		m_totals.showTotals (msg, m_players, output);
	}

	public StringBuffer getTotalsForScoring() {
//		System.out.println(">>> Totaling::getTotalsForScoring");
		StringBuffer buf = new StringBuffer();
		for (int player = 1; player <= Players.PLAYER2; player++) {
			Total total = m_totals.getTotal (player);
			if (player > 1) buf.append (" ");
			buf.append (m_players.getPlayerName(player)).append (" ");
			buf.append (total.getTotal());
			buf.append (total.getTotals());
		}
//		System.out.println("<<< Totaling::getTotalsForScoring");
		return buf;
	}

	private TotalsAll createTotalsAllForActualCounts() {
//		System.out.println(">>> Totaling::createTotalsAllForActualCounts");
		TotalsAll totalsAll = new TotalsAll (m_players, m_board);
		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				if (m_board.isEmpty(r, c)) continue;
				for (int player = Players.PLAYER1; player <= Players.PLAYER2; player++) {
					for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
						totalsAll.setValue (player, r, c, pattern, calculateActualCountForTotals (player, r, c, pattern));
					}
				}
			}
		}
//		totalsAll.showTotalsAll ("Totaling::createTotalsAllForActualCounts");
//		System.out.println("<<< Totaling::createTotalsAllForActualCounts");
		return totalsAll;
	}

	private TotalsAll createTotalsAllForCurrentScores (TotalsAll totalsAll) {
//		System.out.println(">>> Totaling::createTotalsAllForCurrentScores");
		TotalsAll scoresAll = new TotalsAll (m_players, m_board);
		for (int player = Players.PLAYER1; player <= Players.PLAYER2; player++) {
			for (int row = 0; row < m_board.getRows(); row++) {
				for (int col = 0; col < m_board.getColumns(); col++) {
					for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
						scoresAll.setValue (player, row, col, pattern, computeCurrentScoreForTotals (totalsAll.getValue (player, row, col, pattern)));
					}
				}
			}
		}
//		scoresAll.showTotalsAll ("Totaling::createTotalsAllForCurrentScores");
//		System.out.println("<<< Totaling::createTotalsAllForCurrentScores");
		return scoresAll;
	}

	private Totals createTotals (TotalsAll totalsAll, TotalsAll scoresAll) {
//		System.out.println(">>> Totaling::createTotals");
		Totals totals = new Totals();
		for (int player = Players.PLAYER1; player <= Players.PLAYER2; player++) {
			Total total = totals.getTotal (player);
			for (int row = 0; row < m_board.getRows(); row++) {
				for (int col = 0; col < m_board.getColumns(); col++) {
					for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
						total.incrementTotal (scoresAll.getValue (player, row, col, pattern));
						int actual = totalsAll.getValue (player, row, col, pattern);
						if (actual > 0) total.increment (actual);
					}
				}
			}
		}
//		totals.showTotals ("Totaling::createTotals", m_players);
//		System.out.println("<<< Totaling::createTotals");
		return totals;
	}

	private int calculateActualCountForTotals (int player, int r, int c, int pattern) {
//		System.out.println(">>> calculateActualCountForTotals;  player "+player+" r "+r+" c "+c+" pattern "+pattern);
		assert Players.isValidPlayer(player);
		assert m_board.isValidRow(r);
		assert m_board.isValidColumn(c);
		assert PatternUtils.isValidPattern(pattern);
		if (m_board.isEmpty(r, c)) return 0;
		if (m_board.isOtherPlayerAt (player, r, c)) return 0;
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
//			System.out.println("calculateActualCountForTotals; count "+count);
		}
//		System.out.println("<<< calculateActualCountForTotals; count "+count);
		return count;
	}

	private int computeCurrentScoreForTotals (int actual) {
//		System.out.println(">>> Totaling::computeCurrentScoreForTotals; "+actual);
		final int SCORE_WIN_CURRENT = 10000;
		final int SCORE_FOUR_CURRENT = 500;
		final int SCORE_THREE_CURRENT = 100;
		final int SCORE_TWO_CURRENT = 40;
		final int SCORE_ONE_CURRENT = 10;

		int total = 0;
		switch (actual) {
		case 5:
			total += SCORE_WIN_CURRENT;
			break;
		case 4:
			total += SCORE_FOUR_CURRENT;
			break;
		case 3:
			total += SCORE_THREE_CURRENT;
			break;
		case 2:
			total += SCORE_TWO_CURRENT;
			break;
		case 1:
		default:
			total += SCORE_ONE_CURRENT;
			break;
		case 0:
			break;
		}
//		System.out.println("<<< Totaling::computeCurrentScoreForTotals; total "+total);
		return total;
	}
}
