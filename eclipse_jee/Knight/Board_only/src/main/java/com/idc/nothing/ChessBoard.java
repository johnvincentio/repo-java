package com.idc.nothing;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class ChessBoard extends javax.swing.JPanel implements MouseListener {
	private static final long serialVersionUID = -2546018037689610861L;
	private int circlex;
	private int circley;

	ChessBoard() {
		setPreferredSize(new java.awt.Dimension(470, 470));
		// Draw a visible border around the panel:
		setBorder(new javax.swing.border.LineBorder(java.awt.Color.LIGHT_GRAY,
				10));
		addMouseListener(this);
	}

	// Paint the component
	// Swing separates "paint" calls into paintBorder, paintComponent, and
	// paintChildren.
	// "Extensions of Swing components which wish to implement their own paint
	// code should place this code
	// within the scope of the paintComponent() method (not within paint())."
	// ( Fowler, A. Painting in AWT and Swing. 1994-2005 Sun Microsystems, Inc.
	// Retrieved from http://java.sun.com/products/jfc/tsc/articles/painting/. )
	public void paintComponent(java.awt.Graphics g) {
		java.awt.Insets insets = getInsets();

		int width = getWidth();
		int height = getHeight();

		drawBoard((Graphics2D) g, width, height, insets.left, insets.right,
				insets.top, insets.bottom); // !< We draw the board.
	}

	/**
	 * This method draws the board and the labels for rows and columns.
	 * 
	 * @param g
	 *            The graphics surface to draw on.
	 * @param width
	 *            Width of the board.
	 * @param heigth
	 *            Height of the board.
	 * @param left_margin
	 *            The width of the left inset.
	 * @param top_margin
	 *            The width of the top inset.
	 */
	protected void drawBoard(Graphics2D g, int width, int height,
			int left_margin, int right_margin, int top_margin, int bottom_margin) {

		int squareWidth = (int) ((float) (width - left_margin - right_margin) / 9.0f);
		int squareHeight = (int) ((float) (height - top_margin - bottom_margin) / 9.0f);
		int squareSize = Math.min(squareWidth, squareHeight);

		// 1. Draw board
		for (int row = 0; row < 8; row++) {
			for (int column = 1; column < 9; column++) {
				if (row % 2 == column % 2) {
					g.setColor(java.awt.Color.BLACK);
				} else {
					g.setColor(java.awt.Color.WHITE);
				}
				g.fillRect(column * squareSize + left_margin, row * squareSize
						+ top_margin, squareSize, squareSize);
			}
		}

		// 2. Prepare to draw labels
		g.setColor(java.awt.Color.BLACK);
		g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN,
				squareSize / 2));
		java.awt.FontMetrics fm = g.getFontMetrics();

		// 3. draw row labels
		for (int row = 8; row > 0; row--) {
			g.drawString(String.valueOf(9 - row), squareSize / 2 + left_margin,
					row * squareSize - fm.getHeight() / 2 + top_margin);
		}

		// 4. draw column labels
		char[] labels = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };
		for (int column = 0; column < 8; column++) {
			g.drawString(
					String.valueOf(labels[column]),
					((float) column + 1.5f) * squareSize - 0.5f
							* fm.charWidth(labels[column]) + left_margin, 8
							* squareSize + fm.getAscent() + top_margin);
		}

		// 5. draw circle

		if (this.circlex != 0) {
			g.setColor(java.awt.Color.RED);
			g.fillOval(this.circlex * squareSize + left_margin, this.circley
					* squareSize + top_margin, squareSize, squareSize);
		}
	}

	public void mouseClicked(MouseEvent e) {
		java.awt.Insets insets = getInsets();

		int squareWidth = (int) ((float) (getWidth() - insets.left - insets.right) / 9.0f);
		int squareHeight = (int) ((float) (getHeight() - insets.top - insets.bottom) / 9.0f);
		int squareSize = Math.min(squareWidth, squareHeight);

		this.circlex = ((e.getX() - insets.left) / squareSize);
		this.circley = ((e.getY() - insets.top) / squareSize);

		repaint();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Chessboard");
		ChessBoard board = new ChessBoard();
		// frame.getContentPane().add(board) can be shortened to:
		frame.add(board);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true); // frame.show() is deprecated
	}
}