package com.idc.a1;

import javax.swing.JFrame;

public class BBDemo extends JFrame {
	private static final long serialVersionUID = 1;

	public BBDemo(String msg) {
		super (msg);
		setContentPane(new BBPanel());
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		JFrame win = new BBDemo("Bouncing Ball Demo");
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
