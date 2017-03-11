package com.idc.a8;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

class Bullseye extends JPanel {
	private static final long serialVersionUID = 1;
	static final int SIZE = 300; // initial window size
	int rings = 5; // Number of rings to draw in bullseye

	public Bullseye() {
		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(SIZE, SIZE));
	}

	public void setRings(int r) {
		rings = r;
		this.repaint(); // new value of rings - better repaint
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int x = SIZE / 2; // x,y of top left corner for drawing circles
		int y = SIZE / 2;

		for (int i = rings; i > 0; i--) {
			if (i % 2 == 0) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.blue);
			}
			int radius = i * 100 / rings; // compute radius of this ring
			g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
		}
	}
}
