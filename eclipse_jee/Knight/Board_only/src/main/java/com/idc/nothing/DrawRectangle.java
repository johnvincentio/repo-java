package com.idc.nothing;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

class DrawRectangle extends JFrame {
	private static final long serialVersionUID = 8124815555475901663L;

	public DrawRectangle() {
		// to Set JFrame title
		super("Draw A Rectangle In JFrame");

		// Set default close operation for JFrame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set JFrame size
		setSize(500, 500);

		// Make JFrame visible
		setVisible(true);
	}

	public void paint(Graphics g) {
		super.paint(g);

		// draw rectangle outline
		g.drawRect(50, 50, 300, 100);

		// set color to Green
		g.setColor(Color.GREEN);

		// fill rectangle with GREEN color
		g.fillRect(50, 50, 300, 100);
	}

	public static void main(String[] args) {
		new DrawRectangle();
	}
}
