package com.idc.knight.gui;

import java.awt.GridLayout;
import javax.swing.JPanel;

import com.idc.knight.xml.Solution;

public class BoardGui extends JPanel {
	private static final long serialVersionUID = 3073868392281286304L;

	private SquareGui m_squares[][];
	private String m_solutions_dir;
	private int m_max_x, m_max_y;
	private SquareKeyAdapter squareKeyAdapter = new SquareKeyAdapter (this);

	private Solution m_solution = null;
	private int m_current_move_counter = -1;

	public BoardGui (String solutions_dir, int max_x, int max_y) {
		m_solutions_dir = solutions_dir;
		m_max_x = max_x;
		m_max_y = max_y;
		createBoard();
	}
	public void createBoard (int max_x, int max_y) {
		m_max_x = max_x;
		m_max_y = max_y;
		createBoard();
	}
	public void createBoard() {
//		System.out.println(">>> BoardGui::createBoard; m_max_x "+m_max_x+" m_max_y "+m_max_y);
		m_squares = null;
		m_squares = new SquareGui[m_max_x][m_max_y];

		removeAll();
		setLayout (new GridLayout (m_max_y, m_max_x, 0, 0));
		for (int col = m_max_y - 1; col >= 0; col--) {
			for (int row = 0; row < m_max_x; row++) {
				m_squares[row][col] = new SquareGui (row, col, squareKeyAdapter);
				add (m_squares[row][col]);			// add squares to local squares
			}
		}
//		System.out.println("<<< BoardGui::createBoard");
	}

	public void redraw (boolean up) {
//		System.out.println(">>> BoardGui::redraw; up "+up+" m_current_move_counter "+m_current_move_counter);
		int max_move = m_max_x * m_max_y - 1;
		if (up) m_current_move_counter++;
		if (! up) m_current_move_counter--;
		if (m_current_move_counter > max_move) m_current_move_counter = 0;
		if (m_current_move_counter < 0) m_current_move_counter = max_move;
		drawSolution();
//		System.out.println("<<< BoardGui::redraw; up "+up+" m_current_move_counter "+m_current_move_counter);
	}

	public void handleSolution (int start_x, int start_y, String solution_text) {
//		System.out.println(">>> BoardGui::handleSolution; start_x "+start_x+" start_y "+start_y+" solution_text "+solution_text);
		m_solution = GuiUtils.getSolution (m_solutions_dir, m_max_x, m_max_y, start_x, start_y, solution_text);
		m_current_move_counter = 0;
		drawSolution();
//		System.out.println("<<< BoardGui::handleSolution");
	}

	private void drawSolution() {
		System.out.println(">>> BoardGui::drawSolution; "+(m_solution == null ? "Clear the board" : "Draw the board"));
		if (m_solution == null) {
			for (int col = m_max_y - 1; col >= 0; col--) {
				for (int row = 0; row < m_max_x; row++) {
					SquareGui square = m_squares[row][col];
					square.setMoveCounter();
				}
			}
			m_current_move_counter = -1;
		}
		else {
			for (int col = m_max_y - 1; col >= 0; col--) {
				for (int row = 0; row < m_max_x; row++) {
					SquareGui square = m_squares[row][col];
					int move_counter = m_solution.getMoveCounter (row, col);
					square.setMoveCounter (move_counter, m_current_move_counter);
				}
			}
		}
		System.out.println("<<< BoardGui::drawSolution");
	}
}
