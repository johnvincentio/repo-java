package com.idc.p1;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class JVClock extends JFrame {
	private static final long serialVersionUID = 1;

	private Clock clock;
	public JVClock (String msg, String[] args) {
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
        clock.start();
	}
	public void doStopClient() {
		clock.stop();
		System.exit(0);
	} 
	public static void main (String[] args) {
		new JVClock("Clock App", args);
	}
}
