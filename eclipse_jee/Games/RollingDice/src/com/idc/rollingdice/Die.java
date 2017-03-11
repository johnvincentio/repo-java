package com.idc.rollingdice;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class Die extends JComponent {
	private static final long serialVersionUID = 1;
	private static final int SPOT_DIAM = 9; // Diameter of spots

	private int _faceValue; // value that shows on face of die

	public Die() {
		// -- Preferred size is set, but layout may change it.
		setPreferredSize(new Dimension(60, 60));
		roll(); // Set to random initial value
	}

	public int roll() {
		int val = (int) (6 * Math.random() + 1); // Range 1-6
		setValue(val);
		return val;
	}

	public int getValue() {
		return _faceValue;
	}

	public void setValue(int spots) {
		_faceValue = spots;
		repaint(); // Value has changed, must repaint
	}

	@Override
	public void paintComponent(Graphics g) {
		int w = getWidth(); // Get height and width
		int h = getHeight();

		// ... Change to Graphic2D for smoother spots.
		Graphics2D g2 = (Graphics2D) g; // See note below
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// ... Paint background
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, w, h);
		g2.setColor(Color.BLACK);

		g2.drawRect(0, 0, w - 1, h - 1); // Draw border

		switch (_faceValue) {
		case 1:
			drawSpot(g2, w / 2, h / 2);
			break;
		case 3:
			drawSpot(g2, w / 2, h / 2);
			// Fall thru to next case
		case 2:
			drawSpot(g2, w / 4, h / 4);
			drawSpot(g2, 3 * w / 4, 3 * h / 4);
			break;
		case 5:
			drawSpot(g2, w / 2, h / 2);
			// Fall thru to next case
		case 4:
			drawSpot(g2, w / 4, h / 4);
			drawSpot(g2, 3 * w / 4, 3 * h / 4);
			drawSpot(g2, 3 * w / 4, h / 4);
			drawSpot(g2, w / 4, 3 * h / 4);
			break;
		case 6:
			drawSpot(g2, w / 4, h / 4);
			drawSpot(g2, 3 * w / 4, 3 * h / 4);
			drawSpot(g2, 3 * w / 4, h / 4);
			drawSpot(g2, w / 4, 3 * h / 4);
			drawSpot(g2, w / 4, h / 2);
			drawSpot(g2, 3 * w / 4, h / 2);
			break;
		}
	}

	private void drawSpot(Graphics2D g2, int x, int y) {
		g2.fillOval(x - SPOT_DIAM / 2, y - SPOT_DIAM / 2, SPOT_DIAM, SPOT_DIAM);
	}
}
