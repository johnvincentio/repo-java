package com.idc.p5;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

class DigitalNumber {
	private float x = 0;

	private float y = 0;

	private float size = 5;

//	private int number;

//	private Shape s;

	private float space = 0;

	public static final int DOTS = 10;

	private Color on, off;

	DigitalNumber() {
		this(0f, 0f, 5f, Color.cyan, Color.black);
	}

	DigitalNumber(float x, float y, float size, Color on, Color off) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.on = on;
		this.off = off;

	}

	public void drawNumber(int number, Graphics2D g) {

		int flag = 0;

		switch (number) {
		case (0):
			flag = 125;
			break;
		case (1):
			flag = 96;
			break;
		case (2):
			flag = 55;
			break;
		case (3):
			flag = 103;
			break;
		case (4):
			flag = 106;
			break;
		case (5):
			flag = 79;
			break;
		case (6):
			flag = 94;
			break;
		case (7):
			flag = 97;
			break;
		case (8):
			flag = 127;
			break;
		case (9):
			flag = 107;
			break;
		case (DOTS):
			GeneralPath path = new GeneralPath();
			path.moveTo(x + (size / 2), y + (size / 2) - 1);
			path.lineTo(x + (size / 2), y + (size / 2) + 1);
			path.moveTo(x + (size / 2), y + (size / 2) + size - 1);
			path.lineTo(x + (size / 2), y + (size / 2) + size + 1);
			g.setPaint(on);
			g.draw(path);
			return;

		}
		// Top
		if ((flag & 1) == 1) {
			g.setPaint(on);
		} else {
			g.setPaint(off);
		}
		GeneralPath Top = new GeneralPath();
		Top.moveTo(x + space, y);
		Top.lineTo(x + size - space, y);
		g.draw(Top);
		// Middle
		if ((flag & 2) == 2) {
			g.setPaint(on);
		} else {
			g.setPaint(off);
		}
		GeneralPath Middle = new GeneralPath();
		Middle.moveTo(x + space, y + size);
		Middle.lineTo(x + size - space, y + size);
		g.draw(Middle);
		// Bottom
		if ((flag & 4) == 4) {
			g.setPaint(on);
		} else {
			g.setPaint(off);
		}
		GeneralPath Bottom = new GeneralPath();
		Bottom.moveTo(x + space, y + (size * 2));
		Bottom.lineTo(x + size - space, y + (size * 2));
		g.draw(Bottom);
		// TopLeft
		if ((flag & 8) == 8) {
			g.setPaint(on);
		} else {
			g.setPaint(off);
		}
		GeneralPath TopLeft = new GeneralPath();
		TopLeft.moveTo(x, y + space);
		TopLeft.lineTo(x, y + size - space);
		g.draw(TopLeft);
		// BottomLeft
		if ((flag & 16) == 16) {
			g.setPaint(on);
		} else {
			g.setPaint(off);
		}
		GeneralPath BottomLeft = new GeneralPath();
		BottomLeft.moveTo(x, y + size + space);
		BottomLeft.lineTo(x, y + (size * 2) - space);
		g.draw(BottomLeft);
		// TopRight
		if ((flag & 32) == 32) {
			g.setPaint(on);
		} else {
			g.setPaint(off);
		}
		GeneralPath TopRight = new GeneralPath();
		TopRight.moveTo(x + size, y + space);
		TopRight.lineTo(x + size, y + size - space);
		g.draw(TopRight);
		// BottomRight
		if ((flag & 64) == 64) {
			g.setPaint(on);
		} else {
			g.setPaint(off);
		}
		GeneralPath BottomRight = new GeneralPath();
		BottomRight.moveTo(x + size, y + size + space);
		BottomRight.lineTo(x + size, y + (size * 2) - space);
		g.draw(BottomRight);

	}

	public void setSpacing(boolean spacingOn) {
		if (spacingOn == false) {
			space = 0;
		} else {
			this.setSpacing(spacingOn, 5f);
		}
	}

	public void setSpacing(boolean spacingOn, float gap) {
		if (gap < 2) {
			gap = 2;
		}
		if (spacingOn == true) {
			space = size / gap;
		}
	}

	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setSize(float size) {
		this.size = size;
	}

}