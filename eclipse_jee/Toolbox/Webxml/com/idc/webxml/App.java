package com.idc.webxml;

import javax.swing.JFrame;

public class App extends JFrame {
	private static final long serialVersionUID = 1;
	public App(String msg) {
		super(msg);
		setContentPane((new Appfile(this)).makeContentPane());
		setSize(700,730);                // width, height
		setVisible(true);
	}
	public static void main (String args[]) {
		JFrame frame = new App ("Web.xml Converter Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
