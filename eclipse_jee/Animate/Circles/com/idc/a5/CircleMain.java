package com.idc.a5;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class CircleMain extends JFrame {
	private static final long serialVersionUID = 1;

	public CircleMain(String msg) {
		super(msg);
		setLayout(new BorderLayout());
		setContentPane(new CirclePanel(200));
		pack();
		setLocationRelativeTo(null); // Center window.
		setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args) {
		JFrame window = new CircleMain("Circles");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
