package com.idc.a5;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;

public class CirclePanel extends JComponent implements MouseListener {
	private static final long serialVersionUID = 1;

	private int _size; // Height/width of component.

	private ArrayList<Circle> _circles = new ArrayList<Circle>();

	public CirclePanel(int size) {
		_size = size;
		setPreferredSize(new Dimension(_size, _size));
		addMouseListener(this); // This component listens to mouse events.
	}

	@Override
	public void paintComponent(Graphics g) {
		// ... Draw background.
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, _size, _size);

		// ... Draw each of the circles.
		for (Circle c : _circles) {
			c.draw(g);
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		// ... Generate random color and size circle at mouse click point.
		Color randColor = new Color((int) (256 * Math.random()),
				(int) (256 * Math.random()), (int) (256 * Math.random()));
		int randRadius = (int) (_size / 4 * Math.random());
		Circle c = new Circle(e.getPoint(), randRadius, randColor);

		// ... Save this circle for painting later.
		_circles.add(c);

		// ... Cause screen to be repainted.
		repaint();
	}
}
