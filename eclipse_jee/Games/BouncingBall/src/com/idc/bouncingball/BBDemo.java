package com.idc.bouncingball;

import javax.swing.JFrame;

public class BBDemo extends JFrame {
	private static final long serialVersionUID = 1;

	public BBDemo() {
		super ("Bouncing Ball Demo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(new BBPanel());
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new BBDemo();
	}
}