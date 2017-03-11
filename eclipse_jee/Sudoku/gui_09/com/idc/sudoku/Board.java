package com.idc.sudoku;

public class Board {
	private int[][] m_squares = new int[Bucket.BUCKETS][Bucket.BUCKETS];
	public Board() {}
	public Board (Board board) {set (board);}
	public void set (Board board) {
		for (int x=0; x<Bucket.BUCKETS; x++) {
			for (int y=0; y<Bucket.BUCKETS; y++) {
				m_squares[x][y] = board.get(x,y);
			}
		}
	}
	public int get(int x, int y) {return m_squares[x][y];}
	public void set(int x, int y, int value) {m_squares[x][y] = value;}
	public void addPattern (int y, Pattern pattern) {
		for (int x=0; x<Bucket.BUCKETS; x++) m_squares[x][y] = pattern.get(x);
	}
	public void show() {
		System.out.println("");
		for (int y=0; y<Bucket.BUCKETS; y++) {
			for (int x=0; x<Bucket.BUCKETS; x++)
				System.out.print(" "+m_squares[x][y]);
			System.out.println(" ");
		}
	}
}

