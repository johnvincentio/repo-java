package com.idc.test1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import javax.swing.JPanel;

public class FlagPanel extends JPanel {

	private static final long serialVersionUID = -6175383558234071138L;
	public static Image m_imageMine;

	final static int SQUARE_SIZE = 40;

	MakeFlag m_app;
	private int m_type;
	public FlagPanel (MakeFlag app, int type) {
		System.out.println(">>> FlagPanel::constructor; type "+type);
		m_app = app;
		m_type = type;
		m_imageMine = getToolkit().getImage("gifs/mine.gif");
		System.out.println("<<< FlagPanel::constructor");
	}

	public Dimension getPreferredSize() {return new Dimension(SQUARE_SIZE, SQUARE_SIZE);}
	public Dimension getMinimumSize() {return getPreferredSize();}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int xPoints[] = {16, 21, 33, 22, 24, 16, 8, 11, 1, 13};
		int yPoints[] = {0, 11, 11, 16, 29, 22, 29, 16, 11, 11};
		GeneralPath star = new GeneralPath();
		star.moveTo (xPoints[0], yPoints[0]);
		for (int k = 1; k < xPoints.length; k++)
			star.lineTo (xPoints[k], yPoints[k]);
		star.closePath();

		switch (m_type) {
		case 1:
		default:
			g2.setColor(Color.BLACK);
			g2.translate(0, 0);
			g2.fill(star);
			break;
		case 2:
			g2.setColor(Color.YELLOW);
			g2.translate(0, 0);
			g2.fill(star);
			break;
		case 3:
			g2.setColor(Color.LIGHT_GRAY);
			g2.fill3DRect(0, 0, SQUARE_SIZE, SQUARE_SIZE, true);
			break;
		case 4:
			g2.setColor(Color.LIGHT_GRAY);
			g2.fill3DRect(0, 0, SQUARE_SIZE, SQUARE_SIZE, false);
			break;
		case 5:
			g2.drawImage(m_imageMine, 5, 5, 850, 750, this);
//			g2.setColor(Color.LIGHT_GRAY);
//			g2.fill3DRect(0, 0, SQUARE_SIZE, SQUARE_SIZE, true);
			break;
		case 6:
			int h = m_imageMine.getHeight(this);
			int w = m_imageMine.getWidth(this);
			System.out.println("case 6; h "+h+" w "+w);
//			Rectangle bounds = m_imageMine.getBounds();
			g2.setColor(Color.LIGHT_GRAY);
			g2.fill3DRect(0, 0, SQUARE_SIZE, SQUARE_SIZE, true);
			g2.translate(10, 10);
			g2.drawImage(m_imageMine, 0, 0, w, h, this);
			break;
		case 7:
			g2.setColor(Color.LIGHT_GRAY);
			g2.fillRect(0, 0, SQUARE_SIZE, SQUARE_SIZE);
			break;
		case 8:
			g2.setColor(Color.LIGHT_GRAY);
			g2.draw3DRect(0, 0, SQUARE_SIZE, SQUARE_SIZE, true);
			g2.drawImage(m_imageMine, 5, 5, 850, 750, this);
			break;
		case 9:
			g2.setColor(Color.LIGHT_GRAY);
			g2.draw3DRect(0, 0, SQUARE_SIZE, SQUARE_SIZE, false);
			g2.drawImage(m_imageMine, 5, 5, 850, 750, this);
			break;
		}
		g2.dispose();
	}
}
