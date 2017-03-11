package com.idc.nothing;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

public class DrawSquareInJFrame extends JFrame {
	private static final long serialVersionUID = -3194813804497465766L;

	public DrawSquareInJFrame() {
		// Set JFrame title
		super("Draw A Square In JFrame");

		// Set default close operation for JFrame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set JFrame size
		setSize(400, 400);

		// Make JFrame visible
		setVisible(true);
	}

	public void paint(Graphics g) {
		super.paint(g);

		// draw square outline
		g.drawRect(50, 50, 100, 100);

		// set color to RED
		// So after this, if you draw anything, all of it's result will be RED
		g.setColor(Color.RED);

		// fill square with RED
		g.fillRect(50, 50, 100, 100);
		
		g.drawString("hi", 300, 300);
	}

	public static void main(String[] args) {
		new DrawSquareInJFrame();
	}
}
