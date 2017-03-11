package com.idc.five;

class FiveModel {

	// ================================================================
	// constants
	public static final int EMPTY = 0; // The cell is empty.

	private static final int PLAYER1 = 1;

	public static final int TIE = -1; // Game is a tie (draw).

	// ===================================================================
	// fields
	private int _maxRows; // Number of rows. Set in constructor.

	private int _maxCols; // Number of columns. Set in constructor.

	private int[][] _board; // The board values.

	private int _nextPlayer; // The player who moves next.

	private int _moves = 0; // Number of moves in the game.

	// ==============================================================
	// constructor
	public FiveModel(int rows, int cols) {
		_maxRows = rows;
		_maxCols = cols;
		_board = new int[_maxRows][_maxCols];
		reset();
	}

	// ============================================================
	// getNextPlayer
	/** Returns the next player. */
	public int getNextPlayer() {
		return _nextPlayer;
	}

	// ==============================================================
	// getPlayerAt
	/** Returns player who has played at particular row and column. */
	public int getPlayerAt(int r, int c) {
		return _board[r][c];
	}

	// ====================================================================
	// reset
	/** Clears board to initial state. Makes first move in center. */
	public void reset() {
		for (int r = 0; r < _maxRows; r++) {
			for (int c = 0; c < _maxCols; c++) {
				_board[r][c] = EMPTY;
			}
		}
		_moves = 0; // No moves so far.
		_nextPlayer = PLAYER1;
		// -- Make first move in center.
		move(_maxCols / 2, _maxRows / 2); // First player moves to center
	}

	// =====================================================================
	// move
	/** Play a marker on the board, record it, flip players. */
	public void move(int r, int c) {
		assert _board[r][c] == EMPTY;
		_board[r][c] = _nextPlayer; // Record this move.
		_nextPlayer = 3 - _nextPlayer; // Flip players
		_moves++; // Increment number of moves.
	}

	// =================================================== utility method
	// _count5
	/**
	 * The _count5 utility function returns true if there are five in a row
	 * starting at the specified r,c position and continuing in the dr direction
	 * (+1, -1) and similarly for the column c.
	 */
	private boolean _count5(int r, int dr, int c, int dc) {
		int player = _board[r][c]; // remember the player.
		for (int i = 1; i < 5; i++) {
			if (_board[r + dr * i][c + dc * i] != player)
				return false;
		}
		return true; // There were 5 in a row!
	}

	// ============================================================
	// getGameStatus
	/**
	 * -1 = game is tie, 0 = more to play, 1 = player1 wins, 2 = player2 wins
	 * What I don't like about this is mixing a couple of logical types: player
	 * number, empty board, and game status.
	 */
	public int getGameStatus() {
		int row;
		int col;
//		int n_up, n_right, n_up_right, n_up_left;

//		boolean at_least_one_move; // true if game isn't a tie

		for (row = 0; row < _maxRows; row++) {
			for (col = 0; col < _maxCols; col++) {
				int p = _board[row][col];
				if (p != EMPTY) {
					// look at 4 kinds of rows of 5
					// 1. a column going up
					// 2. a row going to the right
					// 3. a diagonal up and to the right
					// 4. a diagonal up and to the left

					if (row < _maxRows - 4) // Look up
						if (_count5(row, 1, col, 0))
							return p;

					if (col < _maxCols - 4) { // row to right
						if (_count5(row, 0, col, 1))
							return p;

						if (row < _maxRows - 4) { // diagonal up to right
							if (_count5(row, 1, col, 1))
								return p;
						}
					}

					if (col > 3 && row < _maxRows - 4) { // diagonal up left
						if (_count5(row, 1, col, -1))
							return p;
					}
				}// endif position wasn't empty
			}// endfor row
		}// endfor col

		// ... Neither player has won, it's tie if there are empty positions.
		// Game is finished if total moves equals number of positions.
		if (_moves == _maxRows * _maxCols) {
			return TIE; // Game tied. No more possible moves.
		} else {
			return 0; // More to play.
		}
	}

}
