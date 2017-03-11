package com.idc.five;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Five extends JFrame {
	private static final long serialVersionUID = 1;

	private static final int ROWS = 9;
	private static final int COLS = 9;
	private static final Color[] PLAYER_COLOR = { null, Color.BLACK, Color.WHITE };
	private static final String[] PLAYER_NAME = { null, "BLACK", "WHITE" };

	private GameBoard _boardDisplay;
	private JTextField _statusField = new JTextField();
	private FiveModel _gameLogic = new FiveModel(ROWS, COLS);

	public static void main(String[] args) {
		JFrame window = new Five("Five in a Row");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Five(String msg) {
		super (msg);
		JButton newGameButton = new JButton("New Game");
		JButton undoButton = new JButton("Undo");

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		controlPanel.add(newGameButton);
		controlPanel.add(undoButton);

		_boardDisplay = new GameBoard();

		this.setLayout(new BorderLayout());
		this.add(controlPanel, BorderLayout.NORTH);
		this.add(_boardDisplay, BorderLayout.CENTER);
		this.add(_statusField, BorderLayout.SOUTH);

		newGameButton.addActionListener(new NewGameAction());

		pack(); // Do layout
		setLocationRelativeTo(null); // Center window.
		setResizable(false);
		setVisible(true); // Make window visible
	}

	// //////////////////////////////////////////////// inner class
	// NewGameAction
	private class NewGameAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_gameLogic.reset();
			_boardDisplay.repaint();
		}
	}

	// //////////////////////////////////////////////////// inner class
	// GameBoard
	// This is defined inside outer class to use things from the outer class:
	// * The logic (could be passed to the constructor).
	// * The number of rows and cols (could be passed to constructor).
	// * The status field - shouldn't really be managed here.
	// See note on using Observer pattern in the model.
	class GameBoard extends JComponent implements MouseListener {
		private static final long serialVersionUID = 1;
		// ============================================================
		// constants
		private static final int CELL_SIZE = 30; // Pixels

		private static final int WIDTH = COLS * CELL_SIZE;

		private static final int HEIGHT = ROWS * CELL_SIZE;

		// ==========================================================
		// constructor
		public GameBoard() {
			this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
			this.addMouseListener(this); // Listen to own mouse events.
		}

		// =======================================================
		// paintComponent
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// ... Paint background
			g2.setColor(Color.LIGHT_GRAY);
			g2.fillRect(0, 0, WIDTH, HEIGHT);

			// ... Paint grid (could be done once and saved).
			g2.setColor(Color.BLACK);
			for (int r = 1; r < ROWS; r++) { // Horizontal lines
				g2.drawLine(0, r * CELL_SIZE, WIDTH, r * CELL_SIZE);
			}
			for (int c = 1; c < COLS; c++) {
				g2.drawLine(c * CELL_SIZE, 0, c * CELL_SIZE, HEIGHT);
			}

			// ... Draw player pieces.
			for (int r = 0; r < ROWS; r++) {
				for (int c = 0; c < COLS; c++) {
					int x = c * CELL_SIZE;
					int y = r * CELL_SIZE;
					int who = _gameLogic.getPlayerAt(r, c);
					if (who != FiveModel.EMPTY) {
						g2.setColor(PLAYER_COLOR[who]);
						g2.fillOval(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
					}
				}
			}
		}

		// ================================================ listener
		// mousePressed
		// When the mouse is pressed (would released be better?),
		// the coordinates are translanted into a row and column.
		// Setting the status field in here isn't really the right idea.
		// Instead the model should notify those who have registered.
		public void mousePressed(MouseEvent e) {
			int col = e.getX() / CELL_SIZE;
			int row = e.getY() / CELL_SIZE;

			boolean gameOver = _gameLogic.getGameStatus() != 0;
			int currentOccupant = _gameLogic.getPlayerAt(row, col);
			if (!gameOver && currentOccupant == FiveModel.EMPTY) {
				// ... Make a move.
				_gameLogic.move(row, col);

				// ... Report what happened in status field.
				switch (_gameLogic.getGameStatus()) {
				case 1:
					// ... Player one wins. Game over.
					_statusField.setText("BLACK WINS");
					break;
				case 2:
					// ... Player two wins. Game over.
					_statusField.setText("WHITE WINS");
					break;

				case FiveModel.TIE: // Tie game. Game over.
					_statusField.setText("TIE GAME");
					break;

				default:
					_statusField.setText(PLAYER_NAME[_gameLogic.getNextPlayer()] + " to play");
				}
			}
			else { // Not legal - clicked non-empty location or game over.
				Toolkit.getDefaultToolkit().beep();
			}

			this.repaint(); // Show updated board
		}

		// ================================================== ignore these
		// events
		public void mouseClicked(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}
}
