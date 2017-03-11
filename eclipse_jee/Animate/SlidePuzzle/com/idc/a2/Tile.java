package com.idc.a2;

public class Tile {
	private int m_row; // row of final position
	private int m_col; // col of final position
	private String m_face; // string to display

	public Tile(int row, int col, String face) {
		m_row = row;
		m_col = col;
		m_face = face;
	}

	public void setFace(String newFace) {m_face = newFace;}
	public String getFace() {return m_face;}
	public boolean isInFinalPosition(int r, int c) {return r == m_row && c == m_col;}
}