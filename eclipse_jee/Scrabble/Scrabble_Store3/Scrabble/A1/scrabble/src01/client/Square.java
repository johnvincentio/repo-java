
import javax.swing.*;
import java.awt.*;

public class Square extends JPanel {	// models a square in Scrabble.
	final static int SQUARE_SIZE = 30;
	final static int INNER_SQUARE_SIZE = 20;
	final static int SQUARE_OFFSET = (SQUARE_SIZE - INNER_SQUARE_SIZE) / 2;

	private ScrabbleImages m_images;
	private boolean m_bRack;	// true for rack, false for board
	private int m_xCoord;
	private int m_yCoord;
	private int m_letterNumber;	// number of the letter
	private Image m_imgTile;	// image for the tile is square
	private Image m_imgLetter;	// image for the letter on this square

	public Square (ScrabbleImages images, boolean b, int i, int j) {
		m_images = images;
		m_bRack = b;		// true if in the rack
		m_xCoord = i;
		m_yCoord = j;
		setLetterNumber(-1);	// default to no letter
		setTileImage();
		setBorder(BorderFactory.createLoweredBevelBorder());
		setBackground(new Color(255,255,150)); // dirty yellow
	}
	public void setLetterNumber(int letter) {
		m_letterNumber = letter;
		setLetterImage();
	}
	public int getLetterNumber() {return m_letterNumber;}
	private void setLetterImage() {
		m_imgLetter = m_images.getLetterImage(m_letterNumber);
		repaint();
	}
	private void setTileImage() {
		m_imgTile = null;
		if (! isRack())
			m_imgTile = m_images.getTileImage(m_xCoord, m_yCoord);
		repaint();
	}

	public Dimension getPreferredSize() {return new Dimension(SQUARE_SIZE, SQUARE_SIZE);}
	public Dimension getMinimumSize() {return getPreferredSize();}

	public int getXCoord() {return this.m_xCoord;}
	public int getYCoord() {return this.m_yCoord;}
	public boolean isEmpty(){if (m_letterNumber < 0) return true;return false;}
	public boolean isRack() {return this.m_bRack;}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (m_imgTile != null)
			g.drawImage(m_imgTile, 0, 0, this);
		if (m_imgLetter != null)
			g.drawImage(m_imgLetter, SQUARE_OFFSET, SQUARE_OFFSET, 
				INNER_SQUARE_SIZE, INNER_SQUARE_SIZE, this);
	}
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Rack ").append(m_bRack);
		buffer.append(", x ").append(m_xCoord);
		buffer.append(", y ").append(m_yCoord);
		buffer.append(", letter ").append(m_letterNumber);
		return buffer.toString();
	}
}

