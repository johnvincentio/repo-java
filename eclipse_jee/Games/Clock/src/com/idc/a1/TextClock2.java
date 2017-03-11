package com.idc.a1;

import javax.swing.JFrame;

public class TextClock2 {

	public static void main(String[] args) {
		JFrame window = new JFrame("Time of Day");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		window.setContentPane(new Clock());

		window.pack();
		window.setVisible(true);
	}
}
