package com.idc.nothing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ChessBoard2 extends JFrame {
	private static final long serialVersionUID = 4968938934999322745L;

	public static void main(String[] args) {
		new ChessBoard2();
	}

	public ChessBoard2() {
		System.out.println(">>> ChessBoard2");
		setLayout(new GridLayout(8, 8, 0, 0));
		// there are 64 squares
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				boolean white = (col % 2 == 0) == (row % 2 == 0);
				add (new DrawRect(white ? "WHITE" : "BLACK"));
			}
		}
		setSize (400, 400);
		setVisible (true);
	}
}

class DrawRect extends JPanel {
	private static final long serialVersionUID = 6704530670172775610L;
	private String ngjyra = "BLACK";

	public DrawRect (String b) {
		ngjyra = b;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		System.out.println("--- paintComponent");
		if (ngjyra.equals("BLACK"))
			g.setColor(Color.BLACK);
		else
			g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		// add the square with the specified color
	}
}
