package com.idc.a2;

public class SlidePuzzleModel {
	private int m_rows;
	private int m_cols;
	private Tile[][] m_contents; // All tiles.
	private Tile m_emptyTile; // The empty space.

	public SlidePuzzleModel(int rows, int cols) {
		m_rows = rows;
		m_cols = cols;
		m_contents = new Tile[m_rows][m_cols];
		reset(); // Initialize and shuffle tiles.
	}

	public int getRows() {return m_rows;}
	public int getCols() {return m_cols;}

	String getFace(int row, int col) {
		return m_contents[row][col].getFace();
	}

	public void reset() {
		for (int r = 0; r < m_rows; r++) {
			for (int c = 0; c < m_cols; c++) {
				m_contents[r][c] = new Tile(r, c, "" + (r * m_cols + c + 1));
			}
		}
		m_emptyTile = m_contents[m_rows - 1][m_cols - 1];
		m_emptyTile.setFace(null);

		for (int r = 0; r < m_rows; r++) {
			for (int c = 0; c < m_cols; c++) {
				exchangeTiles(r, c, (int) (Math.random() * m_rows), (int) (Math.random() * m_cols));
			}
		}
	}

	public boolean moveTile(int r, int c) {
		return checkEmpty(r, c, -1, 0) || checkEmpty(r, c, 1, 0) || checkEmpty(r, c, 0, -1) || checkEmpty(r, c, 0, 1);
	}

	private boolean checkEmpty(int r, int c, int rdelta, int cdelta) {
		int rNeighbor = r + rdelta;
		int cNeighbor = c + cdelta;
		if (isLegalRowCol(rNeighbor, cNeighbor) && m_contents[rNeighbor][cNeighbor] == m_emptyTile) {
			exchangeTiles(r, c, rNeighbor, cNeighbor);
			return true;
		}
		return false;
	}

	public boolean isLegalRowCol(int r, int c) {
		return r >= 0 && r < m_rows && c >= 0 && c < m_cols;
	}

	private void exchangeTiles(int r1, int c1, int r2, int c2) {
		Tile temp = m_contents[r1][c1];
		m_contents[r1][c1] = m_contents[r2][c2];
		m_contents[r2][c2] = temp;
	}

	public boolean isGameOver() {
		for (int r = 0; r < m_rows; r++) {
			for (int c = 0; c < m_cols; c++) {
				Tile trc = m_contents[r][c];
				return trc.isInFinalPosition(r, c);
			}
		}
		return true;
	}
}
