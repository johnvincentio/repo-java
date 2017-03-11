package com.idc.test3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;

import com.idc.minesweeper.App;
import com.idc.minesweeper.Board;

public class Square extends JButton {	// models a square in Minesweeper.
	private static final long serialVersionUID = 649523981238974972L;

	final static int SQUARE_SIZE = 40;		// default = 20

	private App m_app;
	private int m_xCoord;
	private int m_yCoord;
	private boolean m_bVisible = false;				// true if visible to user
	private boolean m_bMined = false;				// true if has a mine
	private int m_nNumberOfAdjacentMines = 0;		// # of adjacent mines
//	private boolean m_bPromulgated = false;			// true if already promulgated to adjacent squares
	private boolean m_bFlagged = false;				// true if flagged by user

	private Font m_font = new Font("TimesRoman", Font.PLAIN, 16);

	public Square (App app, int i, int j) {
		m_app = app;
		m_xCoord = i;
		m_yCoord = j;
		setBorder(BorderFactory.createLineBorder(Color.black));
	}
	public int getXCoord() {return m_xCoord;}
	public int getYCoord() {return m_yCoord;}

	public void setMined() {m_bMined = true;}
	public boolean isMined() {return m_bMined;}

	public int getNumberOfAdjacentMines() {
		return m_nNumberOfAdjacentMines;
	}
	public void setNumberOfAdjacentMines (int nMines) {
		m_nNumberOfAdjacentMines = nMines;
	}
	public boolean isNeedPromulgation() {
		if (m_bVisible) return false;
		if (m_nNumberOfAdjacentMines > 0) return false;
//		if (m_bPromulgated) return false;
		if (m_bMined) return false;
		if (m_bFlagged) return false;
		return true;
	}

	public void selected() {
		System.out.println("--- Square::selected");
		m_bVisible = true;
	}
	public void flagged() {
		if (m_bFlagged) {
			m_bFlagged = false;
			m_bVisible = false;
			return;
		}
		if (m_bVisible && m_bMined) return;

		m_bFlagged = true;
		m_bVisible = true;
	}

	public Dimension getPreferredSize() {return new Dimension(SQUARE_SIZE, SQUARE_SIZE);}
	public Dimension getMinimumSize() {return getPreferredSize();}

	@Override
	public void paintComponent (Graphics g) {
		super.paintComponent(g);

//		System.out.println(">>> Square::paintComponent; "+toString());
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Dimension d = this.getPreferredSize();
		System.out.println("d; w = "+d.width+" h "+d.height);
/*
		if (m_bVisible) {
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, SQUARE_SIZE, SQUARE_SIZE);

			g2.setColor(Color.BLACK);		// paint grid
			g2.drawLine (0, 0, SQUARE_SIZE, 0);
			g2.drawLine (0, 0, 0, SQUARE_SIZE);
		}
*/

		if (m_bVisible) {
			if (m_bFlagged) {
				// m_imageFlag
				System.out.println("drawing a flag");
				g2.setColor(Color.LIGHT_GRAY);
				g2.fill3DRect(0, 0, SQUARE_SIZE, SQUARE_SIZE, true);

				Image image = Board.m_imageFlag;
				int h = image.getHeight(this);
				int w = image.getWidth(this);
				System.out.println("h "+h+" w "+w);
//				g2.drawImage(image, 1, 1, 850, 750, this);
			}
			else if (m_bMined) {
				System.out.println("drawing a mine");
				Image image = Board.m_imageMine;
				int h = image.getHeight(this);
				int w = image.getWidth(this);
//				System.out.println("h "+h+" w "+w);
				g2.drawImage(image, 1, 1, 850, 750, this);
			}
			else {
				g2.setFont(m_font);
				g2.setColor (Color.BLUE);
				if (m_nNumberOfAdjacentMines > 0) {
					String strTmp = Integer.toString(m_nNumberOfAdjacentMines, 10);
					g2.drawString (strTmp, 5, 16);
				}
				else
					g2.drawString ("", 0, 0);
			}
		}
		else {
			g2.setColor(Color.LIGHT_GRAY);

//			g2.fill3DRect(0, 0, SQUARE_SIZE, SQUARE_SIZE, true);
//			g2.draw3DRect(0, 0, SQUARE_SIZE, SQUARE_SIZE, true);
		}

		g2.dispose();
//		System.out.println("<<< Square::paintComponent");
	}

	public String showSquare() {
		StringBuffer buf = new StringBuffer();
		buf.append("(").append(m_nNumberOfAdjacentMines).append(",").append(reportBoolean(m_bVisible));
		buf.append(",").append(reportBoolean(m_bMined)).append(",").append(reportBoolean(m_bFlagged)).append(")");
		return buf.toString();
	}
	private String reportBoolean(boolean bool) {
		return bool ? "T" : "F";
	}
	@Override
	public String toString() {
		return "Square [m_xCoord=" + m_xCoord + ", m_yCoord=" + m_yCoord
				+ ", m_bVisible=" + m_bVisible + ", m_bMined=" + m_bMined
				+ ", m_nNumberOfAdjacentMines=" + m_nNumberOfAdjacentMines
				+ ", m_bFlagged=" + m_bFlagged + "]";
	}
}

/*


	public void setVisible() {m_bVisible = true;}
	public boolean isVisible() {return m_bVisible;}

	public void JVinitMineNumber(int nMines) {
		m_nNumberOfAdjacentMines = nMines;
		m_image = null;
		if (m_nNumberOfAdjacentMines < 0)
			m_image = m_app.getToolkit().getImage("gifs/mine.gif");
	}
	public void setMineNumber(int nMines) {
		initMineNumber(nMines);
		
	}

	public int getMineNumber() {return m_nNumberOfAdjacentMines;}

	public void JVinitMineNumber(int nMines) {
		m_nNumberOfAdjacentMines = nMines;
		m_image = null;
		if (m_nNumberOfAdjacentMines < 0)
			m_image = m_app.getToolkit().getImage("gifs/mine.gif");
	}

*/
