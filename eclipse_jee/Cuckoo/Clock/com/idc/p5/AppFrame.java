package com.idc.p5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.TimerTask;

import javax.swing.JFrame;

public class AppFrame extends JFrame {
	private static final long serialVersionUID = 1;

	AppFrame() {
		this("Demo Frame");
	}

	AppFrame(String title) {
		super(title);
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String args[]) {
		AppFrame aframe = new AppFrame("Digital Clock");
		Container cpane = aframe.getContentPane();
		final DigitalClock dc = new DigitalClock();
		dc.setBackground(Color.black);
		cpane.add(dc, BorderLayout.CENTER);
		aframe.setSize(310, 120);
		aframe.setVisible(true);

		class Task extends TimerTask {
			public void run() {
				dc.repaint();
			}
		}
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new Task(), 0L, 250L);
	}
}
