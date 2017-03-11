
package com.idc.appender;

import javax.swing.*;

public class App extends JFrame {
	private static final long serialVersionUID = 1;
	public App(String msg) {
		super(msg);
		setContentPane((new Appfile(this)).makeContentPane());
		setSize(700,730);                // width, height
		setVisible(true);
	}
	public static void main (String args[]) {
		JFrame frame = new App ("Appender Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
