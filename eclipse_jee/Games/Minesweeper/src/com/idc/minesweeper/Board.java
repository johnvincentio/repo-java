package com.idc.minesweeper;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Board extends JPanel {
	private static final long serialVersionUID = 8581597966918775735L;

	final static int BOARD_SIZEX = 8;		// default 16
	final static int BOARD_SIZEY = 12;		// default 30
	final static int MAX_MINES = 10;		// default 99

	public static Image m_imageBlast;
	public static Image m_imageMine;
	public static Image m_imageFlag;
/*
1 blue 
2 green 
3 red 
4 dark blue or purple = new Color(72, 61, 77)
5 brown or maroon = new Color(176, 48, 96)
6 Cyan or turquoise = new Color(116, 179, 171)
7 Black 
8 Grey = new Color(105, 105, 105)
9 Deep Pink = new Color(225, 20, 147)
*/
	public static Color[] m_textColors = {Color.BLUE, Color.GREEN, Color.RED, 
						new Color(72, 61, 77), new Color(176, 48, 96), new Color(116, 179, 171),
						Color.BLACK, new Color(105, 105, 105), new Color(225, 20, 147)};

	private App m_app;

	private Square m_squares[][];   // MineSweeper grid.

	public Board (App app) {
		m_app = app;

		m_imageBlast = getToolkit().getImage("gifs/blast.gif");
		m_imageMine = getToolkit().getImage("gifs/mine.gif");
		m_imageFlag = getToolkit().getImage("gifs/flag.gif");

		setLayout (new GridLayout (BOARD_SIZEX, BOARD_SIZEY, 0, 0));
		m_squares = new Square[BOARD_SIZEX][BOARD_SIZEY];
		java.util.List<Square> listMineField = new java.util.ArrayList<Square>();
		for (int row = 0; row < BOARD_SIZEX; row++) {	// add squares to board
			for (int col = 0; col < BOARD_SIZEY; col++) {
				m_squares[row][col] = new Square(row, col);
//				m_squares[row][col].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
				m_squares[row][col].addMouseListener (new MineMouseListener());
				add(m_squares[row][col]);
				listMineField.add(m_squares[row][col]);
			}
		}
		java.util.Collections.shuffle(listMineField);
		Square sq;
		for (int i = 0; i < MAX_MINES; i++) {		// add the mines to the mine field
			sq = listMineField.get(i);
			sq.setMined();
		}
		listMineField = null;

		int nTotal;
		for (int row = 0; row < BOARD_SIZEX; row++) {
			for (int col = 0; col < BOARD_SIZEY; col++) {
				nTotal = countAdjacentMines(row,col);
				m_squares[row][col].setNumberOfAdjacentMines(nTotal);
			}
		}
	}

	private int countAdjacentMines (int x, int y) {
//		System.out.println(">>> countAdjacentMines; x,y "+x+","+y);
		int nTotal = 0;
		if (isMined(x-1,y-1)) nTotal += 1;
		if (isMined(x,y-1)) nTotal += 1;
		if (isMined(x+1,y-1)) nTotal += 1;
		if (isMined(x-1,y)) nTotal += 1;
		if (isMined(x+1,y)) nTotal += 1;
		if (isMined(x-1,y+1)) nTotal += 1;
		if (isMined(x,y+1)) nTotal += 1;
		if (isMined(x+1,y+1)) nTotal += 1;
//		System.out.println("<<< countAdjacentMines; total "+nTotal);
		return nTotal;
	}
	private boolean isMined (int x, int y) {
		if (! isValid (x, y)) return false;
		return m_squares[x][y].isMined();
	}
	private boolean isValid (int x, int y) {
		if ( (x < 0) || (x >= BOARD_SIZEX) || (y < 0) || (y >=BOARD_SIZEY)) return false;
		return true;
	}

	public class MineMouseListener implements MouseListener {
		public void mousePressed(MouseEvent me) {
			Square sq = ((Square) me.getComponent());
			System.out.println("mouse released; square :"+sq);

			if (SwingUtilities.isLeftMouseButton(me)) {
				System.out.println("detected left button");
				handleSelectedSquare (sq.getXCoord(), sq.getYCoord());
				updateBoard();
				showBoard ("isLeftMouseButton");
			}
			else if (SwingUtilities.isRightMouseButton(me)) {
				System.out.println("detected right button");
				sq.flagged();
				updateBoard();
				showBoard ("isRightMouseButton");
			}
		}
		public void mouseReleased(MouseEvent me) {}
		public void mouseEntered(MouseEvent me) {}
		public void mouseClicked(MouseEvent me) {}
		public void mouseExited(MouseEvent me) {}		
	}

	private void handleSelectedSquare (int x, int y) {
		if (! isValid (x, y)) return;

		System.out.println(">>> handleSelectedSquare; x "+x+" y "+y);

		Square sq = m_squares[x][y];
		if (sq.isNeedPromulgation()) {
			System.out.println("needs Promulgation");
			sq.selected();
			handleSelectedSquare(x-1,y-1);		// move 1
			handleSelectedSquare(x,y-1);		// move 2
			handleSelectedSquare(x+1,y-1);		// move 3
			handleSelectedSquare(x-1,y);		// move 4
			handleSelectedSquare(x+1,y);		// move 5
			handleSelectedSquare(x-1,y+1);		// move 6
			handleSelectedSquare(x,y+1);		// move 7
			handleSelectedSquare(x+1,y+1);		// move 8
		}
		else {
			sq.selected();
		}
		System.out.println("<<< handleSelectedSquare");
	}

	public void updateBoard() {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					repaint();
				}
			}
		);
	}

	@SuppressWarnings("unused")
	private void sayWhat(String evDs, MouseEvent e) {	// useful for mouse
		System.out.println(evDs + " detected on " + e.getComponent().getClass().getName()+" .\n");
	}

	private void showBoard (String msg) {
		System.out.println(">>> showBoard; "+msg);
		for (int row = 0; row < BOARD_SIZEX; row++) {
			boolean first = true;
			for (int col = 0; col < BOARD_SIZEY; col++) {
				if (first) 
					first = false;
				else
					System.out.print(",   ");
				Square sq = m_squares[row][col];
				System.out.print(sq.showSquare());
			}
			System.out.println();
		}		
		System.out.println("<<< showBoard; "+msg);
	}
}

/*
//		m_imageFlag = getToolkit().getImage("other_gifs/flag.gif");
		try {
			m_imageFlag = ImageIO.read(new File("more_gifs/flag.ico"));
		}
		catch (Exception ex) {
			System.out.println("Exception; "+ex.getMessage());
			System.exit(0);
		}
*/
