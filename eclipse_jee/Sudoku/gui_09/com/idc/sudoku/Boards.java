
package com.idc.sudoku;

public class Boards {
	private Board[] m_boards;
	public Boards() {
		m_boards = new Board[Bucket.BUCKETS+1];	// need one extra to simplify the algorithm
		for (int i=0; i<Bucket.BUCKETS+1; i++) m_boards[i] = new Board();
	}
	public void set (int pos, Board board) {m_boards[pos].set(board);}
	public Board getBoard (int pos) {return m_boards[pos];}
	public void show (int pos) {m_boards[pos].show();}
}
