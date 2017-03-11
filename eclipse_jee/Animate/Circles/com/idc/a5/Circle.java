package com.idc.a5;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

class Circle {
	int _x; // x coord of bounding rect upper left corner.

	int _y; // y coord of bounding rect upper left corner.

	int _diameter; // Height and width of bounding rectangle.

	Color _color;

	Circle(Point center, int radius, Color color) {
		// ... Change user oriented parameters into more useful values.
		_x = center.x - radius;
		_y = center.y - radius;
		_diameter = 2 * radius;
		_color = color;
	}

	void draw(Graphics g) {
		// ... Should we save and restore the previous color?
		g.setColor(_color);
		g.fillOval(_x, _y, _diameter, _diameter);
	}
}
