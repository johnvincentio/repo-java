package com.idc.nothing;

import javax.swing.JFrame;

public class GameFrame extends JFrame {
	private static final long serialVersionUID = 3324087073013409783L;

	public GameFrame() {
		super("Game Frame");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Squares squares = new Squares();
		getContentPane().add(squares);
		for (int i = 0; i < 15; i++) {
			squares.addSquare(i * 10, i * 10, 100, 100);
		}

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

	}

	public static void main(String[] args) {
		new GameFrame();
	}
}
