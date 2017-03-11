package com.idc.j2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.TimerTask;

import javax.swing.JFrame;

public class AppFrame extends JFrame {
	private static final long serialVersionUID = 1;

	private static final int MAX_DIGITS = 5;

	AppFrame(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cpane = getContentPane();
		final DigitalTimer dc = new DigitalTimer(MAX_DIGITS);
		dc.setBackground(Color.black);
		cpane.add(dc, BorderLayout.CENTER);

		setSize(MAX_DIGITS * 30 + 20, 100);
		setVisible(true);

		class Task extends TimerTask {
			public void run() {
				dc.repaint();
			}
		}
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new Task(), 0L, 250L);
	}

	public static void main(String args[]) {
		new AppFrame("Digital Timer");
	}
}