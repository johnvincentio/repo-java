package com.idc.rollingdice;

import javax.swing.JFrame;

public class RollDice {

	public static void main(String[] args) {
		JFrame window = new JFrame("Dice Demo");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(new RollDicePanel());
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
}
