package com.idc.slider;

//SlidePuzzleGUI.java - GUI for SlidePuzzle
//Fred Swartz, 2003-May-10, 2004-May-3
//
//The SlidePuzzleGUI class creates a panel which 
//  contains two subpanels.
//  1. In the north is a subpanel for controls (just a button now).
//  2. In the center a graphics
//This needs a few improvements.  
//Both the GUI and Model define the number or rows and columns.
//       How would you set both from one place? 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class SlidePuzzleGUI extends JPanel {
	private static final long serialVersionUID = 1;
	private GraphicsPanel _puzzleGraphics;

	private SlidePuzzleModel _puzzleModel = new SlidePuzzleModel();

	public SlidePuzzleGUI() {
		// --- Create a button. Add a listener to it.
		JButton newGameButton = new JButton("New Game");
		newGameButton.addActionListener(new NewGameAction());

		// --- Create control panel
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		controlPanel.add(newGameButton);

		// --- Create graphics panel
		_puzzleGraphics = new GraphicsPanel();

		// --- Set the layout and add the components
		this.setLayout(new BorderLayout());
		this.add(controlPanel, BorderLayout.NORTH);
		this.add(_puzzleGraphics, BorderLayout.CENTER);
	}// end constructor

	// ////////////////////////////////////////////// class GraphicsPanel
	// This is defined inside the outer class so that
	// it can use the outer class instance variables.
	class GraphicsPanel extends JPanel implements MouseListener {
		private static final long serialVersionUID = 1;
		private static final int ROWS = 3;

		private static final int COLS = 3;

		private static final int CELL_SIZE = 80; // Pixels

		private Font _biggerFont;

		// ================================================== constructor
		public GraphicsPanel() {
			_biggerFont = new Font("SansSerif", Font.BOLD, CELL_SIZE / 2);
			this.setPreferredSize(new Dimension(CELL_SIZE * COLS, CELL_SIZE
					* ROWS));
			this.setBackground(Color.black);
			this.addMouseListener(this); // Listen own mouse events.
		}// end constructor

		// =======================================x method paintComponent
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			for (int r = 0; r < ROWS; r++) {
				for (int c = 0; c < COLS; c++) {
					int x = c * CELL_SIZE;
					int y = r * CELL_SIZE;
					String text = _puzzleModel.getFace(r, c);
					if (text != null) {
						g.setColor(Color.gray);
						g.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
						g.setColor(Color.black);
						g.setFont(_biggerFont);
						g.drawString(text, x + 20, y + (3 * CELL_SIZE) / 4);
					}
				}
			}
		}// end paintComponent

		// ======================================== listener mousePressed
		public void mousePressed(MouseEvent e) {
			// --- map x,y coordinates into a row and col.
			int col = e.getX() / CELL_SIZE;
			int row = e.getY() / CELL_SIZE;

			if (!_puzzleModel.moveTile(row, col)) {
				// moveTile moves tile if legal, else returns false.
				Toolkit.getDefaultToolkit().beep();
			}

			this.repaint(); // Show any updates to model.
		}// end mousePressed

		// ========================================== ignore these events
		public void mouseClicked(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}// end class GraphicsPanel

	// //////////////////////////////////////// inner class NewGameAction
	public class NewGameAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_puzzleModel.reset();
			_puzzleGraphics.repaint();
		}
	}// end inner class NewGameAction

}// end class SlidePuzzleGUI
