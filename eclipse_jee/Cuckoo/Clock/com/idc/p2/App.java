package com.idc.p2;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class App extends JFrame {
	private static final long serialVersionUID = 1;

	private Clock clock;
	public App (String msg, String[] args) {
		super (msg);
		this.addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				doStopClient();
			}
		});
		clock = new Clock();
        setContentPane(clock);
        pack();                          // Layout components
        setLocationRelativeTo(null);     // Center window.
        setVisible(true);
        validate();
		repaint();

	}
	public void doStopClient() {
		System.exit(0);
	} 
	public static void main (String[] args) {
		new App("Clock App", args);
	}
}