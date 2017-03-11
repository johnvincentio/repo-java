package com.idc.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;

public class Test extends JFrame {

	public static void main(String[] args) {
		Test t = new Test();
	}

	public Test() {
		setTitle("Frame");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 50, 100, 100);
		setSize(400,300);
		setVisible(true);
		Graphics g = getGraphics();
		paint(g);
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.setFont(new Font(null, Font.PLAIN, 12));
		g.drawString("Hello World!", 50, 50);
	}
}