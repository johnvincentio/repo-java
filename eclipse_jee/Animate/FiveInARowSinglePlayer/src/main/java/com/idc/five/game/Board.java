package com.idc.five.game;

import com.idc.five.output.Output;
import com.idc.five.players.Players;

public class Board {
	public static final int EMPTY = 0;

	private Players m_players;
	private int rows;
	private int columns;

	private int[][] boardValues; 	// The board values.

	public Board() {
		this (new Players(), 9, 9);			// set default board size
	}
	public Board (Players players, int rows, int columns) {
		assert rows >= getMinRows() && rows <= getMaxRows();
		assert columns >= getMinColumns() && columns <= getMaxColumns();
		m_players = players;
		this.rows = rows;
		this.columns = columns;

		this.boardValues = new int[this.rows][this.columns];
		for (int r = 0; r < this.rows; r++) {
			for (int c = 0; c < this.columns; c++) {
				this.boardValues[r][c] = EMPTY;
			}
		}
	}

	public int getMaxSize() {
		return columns > rows ? columns : rows;
	}

	public int getRows() {return rows;}
	public void setRows (int row) {this.rows = row;}

	public int getColumns() {return columns;}
	public void setColumns (int column) {this.columns = column;}

	public int getMinRows() {return 5;}
	public int getMaxRows() {return 20;}
	public int getCurrentRow() {return rows - getMinRows();}
	public boolean isValidRow (int row) {
		return row >= 0 && row < getRows();
	}

	public int getMinColumns() {return 5;}
	public int getMaxColumns() {return 20;}
	public int getCurrentColumn() {return columns - getMinColumns();}
	public boolean isValidColumn (int columns) {
		return columns >= 0 && columns < getColumns();
	}

	@Override
	public String toString() {
		return "Board [rows=" + rows + ", columns=" + columns + "]";
	}




	public boolean isOtherPlayerAt (int player, int r, int c) {
		assert Players.isValidPlayer(player);
		assert isValidRow(r);
		assert isValidColumn(c);
		return boardValues[r][c] == Players.whoIsOtherPlayer (player);
	}

	public boolean isPlayerAt (int player, int r, int c) {
		assert Players.isValidPlayer(player);
		assert isValidRow(r);
		assert isValidColumn(c);
		return boardValues[r][c] == player;
	}

	public int getPlayerAt (int r, int c) {
		assert ! isEmpty(r, c);
		return boardValues[r][c];
	}

	public boolean isEmpty (Coordinate coordinate) {
		return isEmpty (coordinate.getRow(), coordinate.getCol());
	}

	public boolean isEmpty (int r, int c) {
		assert isValidRow(r);
		assert isValidColumn(c);
		return boardValues[r][c] == EMPTY;
	}
	public boolean isNotEmpty (int r, int c) {return ! isEmpty (r, c);}

	public void setEmpty (int r, int c) {
		assert ! isEmpty(r, c);
		assert isValidRow(r);
		assert isValidColumn(c);
		boardValues[r][c] = EMPTY;
	}
	public void setEmpty (Coordinate coordinate) {
		setEmpty (coordinate.getRow(), coordinate.getCol());
	}

	public void setPlayer (int player, int r, int c) {
		assert Players.isValidPlayer(player);
		assert isEmpty(r, c);
		boardValues[r][c] = player;
	}

	public void showBoard (String msg, Output output) {
		output.println(">>> Show Board; "+msg);
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getColumns(); c++) {
				if (isEmpty(r, c))
					output.print(".");
				else
					output.print(m_players.getPlayerNameCharacter (getPlayerAt(r, c)).substring(0, 1));
			}
			output.print("\n");
		}
		output.println("<<< Show Board; "+msg);
	}
}
