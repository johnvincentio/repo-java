package com.idc.five.totaling;

import com.idc.five.output.Output;
import com.idc.five.players.Players;

public class Totals {
	private Total[] m_total = new Total[Players.PLAYER2];

	public Totals() {
		for (int i = 0; i < Players.PLAYER2; i++) m_total[i] = new Total();
	}
	void increment (int player, int level) {
		assert Players.isValidPlayer(player);
		assert level >= 0 && level <= 5;
		Total total = getTotal (player);
		total.increment (level);
	}
	Total getTotal (int player) {
		assert Players.isValidPlayer(player);
		return m_total[player - 1];
	}

	public void showTotals (String msg, Players players, Output output) {
		output.println(">>> Totaling::ShowTotals; "+msg);
		for (int player = Players.PLAYER1; player <= Players.PLAYER2; player++) {
			Total total = getTotal(player);
			output.println ("Totals for "+players.getPlayerName(player)+ " are: " + total.getTotal() + total.getTotals());
		}
		output.println("<<< Totaling::ShowTotals; "+msg);
	}
}
