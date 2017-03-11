package com.idc.appender;

import javax.swing.*;

public class AppCleaner extends JFrame {
	private static final long serialVersionUID = 1;
	public AppCleaner(String msg) {
		super(msg);
		setContentPane((new AppCleanerPanel(this)).makeContentPane());
		setSize(700,730);                // width, height
		setVisible(true);
	}
	public static void main (String args[]) {
		JFrame frame = new AppCleaner ("Text Cleaner Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
