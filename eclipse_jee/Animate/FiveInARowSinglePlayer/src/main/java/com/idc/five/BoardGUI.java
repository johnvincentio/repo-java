package com.idc.five;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;

import com.idc.five.game.Board;
import com.idc.five.game.Coordinate;
import com.idc.five.game.Moves;
import com.idc.five.game.SizingItemInfo;
import com.idc.five.players.Players;

public class BoardGUI extends JComponent {
	private static final long serialVersionUID = 1330671199043047797L;

	private SizingItemInfo m_sizingItemInfo;
	private Players m_players;
	private Board m_board;
	private Moves m_moves;

	private Coordinate m_currentMouseCoordinate = null;

	public BoardGUI (SizingItemInfo sizingItemInfo, Players players, Board board, Moves moves) {
		m_sizingItemInfo = sizingItemInfo;
		m_players = players;
		m_board = board;
		m_moves = moves;
	}

	public void setCurrentMouseCoordinate (Coordinate coordinate) {
		this.m_currentMouseCoordinate = coordinate;
	}

	public void setCurrentMousePosition (int x, int y) {
//		System.out.println("--- BoardGUI::setCurrentMousePosition; x " + x + " y " + y);
		Coordinate coord = m_sizingItemInfo.calculateCoordinate (x, y);
		if (! m_board.isValidRow(coord.getRow()) || ! m_board.isValidColumn(coord.getCol()))
			m_currentMouseCoordinate = null;
		else
			m_currentMouseCoordinate = coord;	
//		System.out.println("--- BoardGUI::setCurrentMousePosition; m_currentMouseCoordinate "+m_currentMouseCoordinate);
	}

	private int getMyCellSize() {
		return m_sizingItemInfo.getCalculatedCellSize();
	}

	public Coordinate getCoordinate (int x, int y) {
		Coordinate coord = m_sizingItemInfo.calculateCoordinate (x, y);
		if (! m_board.isValidRow(coord.getRow()) || ! m_board.isValidColumn(coord.getCol())) return null;
		return coord;
	}

	@Override
	public void paintComponent(Graphics g) {
//		System.out.println(">>> BoardGUI::paintComponent; m_currentMouseCoordinate "+m_currentMouseCoordinate);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor (new Color (200, 200, 200));		// paint background
		g2.fillRect (0, 0, m_sizingItemInfo.getCalculatedWindowWidth(), m_sizingItemInfo.getCalculatedBoardHeight());

		g2.translate (m_sizingItemInfo.getCalculatedOffsetWidth() / 2, 0);

		g2.setColor (Color.LIGHT_GRAY);		// paint board
		g2.fillRect (0, 0, m_sizingItemInfo.getCalculatedBoardWidth(), m_sizingItemInfo.getCalculatedBoardHeight());

		g2.setColor(Color.BLACK);		// paint grid
		for (int r = 1; r < m_board.getRows(); r++) {		// Horizontal lines
			g2.drawLine (0, r * getMyCellSize(), m_board.getColumns() * getMyCellSize(), r * getMyCellSize());
		}
		for (int c = 0; c <= m_board.getColumns(); c++) {	// Vertical lines
			g2.drawLine (c * getMyCellSize(), 0, c * getMyCellSize(), m_board.getRows() * getMyCellSize());
		}

		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				if (m_board.isEmpty(r, c)) continue;
				g2.setColor (m_players.getPlayerColor (m_board.getPlayerAt (r, c)));
				g2.fillOval (c * getMyCellSize() + 2, r * getMyCellSize() + 2, getMyCellSize() - 4, getMyCellSize() - 4);
			}
		}

//		int xPoints[] = { 55, 67, 109, 73, 83, 55, 27, 37, 1, 43 };
//		int yPoints[] = { 0, 36, 36, 54, 96, 72, 96, 54, 36, 36 };
//		int xPoints[] = { 18, 23, 36, 24, 27, 18, 9, 12, 1, 14 };
//		int yPoints[] = { 0, 12, 12, 18, 32, 24, 32, 18, 12, 12 };
//		int xPoints[] = {12, 16, 24, 16, 18, 12, 6, 8, 1, 10};
//		int yPoints[] = {0, 8, 8, 12, 22, 16, 22, 12, 8, 8};
		int xPoints[] = {16, 21, 33, 22, 24, 16, 8, 11, 1, 13};
		int yPoints[] = {0, 11, 11, 16, 29, 22, 29, 16, 11, 11};
		GeneralPath star = new GeneralPath();
		star.moveTo (xPoints[0], yPoints[0]);
		for (int k = 1; k < xPoints.length; k++)
			star.lineTo (xPoints[k], yPoints[k]);
		star.closePath();

		if (m_currentMouseCoordinate != null && m_board.isEmpty (m_currentMouseCoordinate)) {
//			System.out.println("m_currentMouseCoordinate "+m_currentMouseCoordinate);
//			System.out.println("m_moves.getCurrentPlayer() "+m_moves.getCurrentPlayer());
//			System.out.println("Game.getPlayerColor (m_game.getCurrentPlayer()) "+m_players.getPlayerColor (m_moves.getCurrentPlayer()));

			g2.setColor (m_players.getPlayerColor (m_moves.getCurrentPlayer()));
			int offset = (m_sizingItemInfo.getCalculatedCellSize() - 30) / 2;
//			System.out.println("offset "+offset);
			int starX = offset + m_currentMouseCoordinate.getRow() * getMyCellSize();
			int starY = offset + m_currentMouseCoordinate.getCol() * getMyCellSize();
//			System.out.println("starX "+starX+" starY "+starY);
			g2.translate (starY, starX);
			g2.fill (star);
		}
//		System.out.println("<<< BoardGUI::paintComponent");
	}
}
