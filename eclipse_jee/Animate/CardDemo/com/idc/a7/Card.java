package com.idc.a7;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.ImageIcon;

class Card {
	private ImageIcon _image;
	private int _x;
	private int _y;

	public Card(ImageIcon image) {
		_image = image;
	}

	public void moveTo(int x, int y) {
		_x = x;
		_y = y;
	}

	public boolean contains(int x, int y) {
		return (x > _x && x < (_x + getWidth()) && y > _y && y < (_y + getHeight()));
	}

	public int getWidth() {
		return _image.getIconWidth();
	}

	public int getHeight() {
		return _image.getIconHeight();
	}

	public int getX() {
		return _x;
	}

	public int getY() {
		return _x;
	}

	public void draw(Graphics g, Component c) {
		_image.paintIcon(c, g, _x, _y);
	}
}
