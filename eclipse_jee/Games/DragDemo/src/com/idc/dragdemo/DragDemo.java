package com.idc.dragdemo;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class DragDemo extends JApplet {
	private static final long serialVersionUID = 1;

	public static void main(String[] args) {
		JFrame window = new JFrame();
		window.setTitle("Drag Demo");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(new DragBallPanel());
		window.pack();
		window.setVisible(true);
	}

	public DragDemo() {
		this.setContentPane(new DragBallPanel());
	}
}
