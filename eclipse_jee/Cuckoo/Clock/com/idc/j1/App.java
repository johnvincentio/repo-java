package com.idc.j1;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class App extends JFrame {
	private static final long serialVersionUID = 1;

	private DateFormatPanel dateFormatPanel;

	public App(String msg, String[] args) {
		super(msg);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				doStopClient();
			}
		});
		dateFormatPanel = new DateFormatPanel();
		setContentPane(dateFormatPanel);
		pack(); // Layout components
		setLocationRelativeTo(null); // Center window.
		setVisible(true);
		dateFormatPanel.start();
	}

	public void doStopClient() {
		dateFormatPanel.stop();
		System.exit(0);
	}

	public static void main(String[] args) {
		new App("Clock App", args);
	}
}
