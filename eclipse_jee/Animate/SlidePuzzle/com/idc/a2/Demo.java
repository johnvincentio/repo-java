package com.idc.a2;

import javax.swing.JFrame;

public class Demo extends JFrame {
	private static final long serialVersionUID = 1;

	public Demo(String msg) {
		super (msg);
		setContentPane(new SlidePuzzleGUI());
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		JFrame win = new Demo("Slide Puzzle Demo");
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
