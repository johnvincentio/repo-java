package com.idc.slider;

import javax.swing.JFrame;

public class SlidePuzzle {
	public static void main(String[] args) {
		JFrame window = new JFrame("Slide Puzzle");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(new SlidePuzzleGUI());
		window.pack(); // finalize layout
		window.setVisible(true);
		window.setResizable(false);
	}
}
