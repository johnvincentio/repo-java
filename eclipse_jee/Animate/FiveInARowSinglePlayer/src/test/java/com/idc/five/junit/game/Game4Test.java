package com.idc.five.junit.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.Board;
import com.idc.five.game.Coordinate;
import com.idc.five.game.Game;
import com.idc.five.game.Moves;
import com.idc.five.players.Players;

public class Game4Test {
	Game m_game;
	Players m_players = new Players();
	Board m_board = new Board();
	Moves m_moves = new Moves(m_players, m_board);

	@Before
	public void initialize() {
		m_game = new Game (m_players, m_board, m_moves);
	}

	@Test
	public void testExport() {

		for (int row = 0; row < m_board.getRows(); row++) {
			if ((row >= 0 && row <= 3) || row == 8) {
				for (int col = 1; col < m_board.getColumns(); ) {
					m_game.move (Players.PLAYER2, row, col);
					m_game.move (Players.PLAYER1, row, col + 1);
					col = col + 2;
				}
			}
			else {
				for (int col = 1; col < m_board.getColumns(); ) {
					m_game.move (Players.PLAYER1, row, col);
					m_game.move (Players.PLAYER2, row, col + 1);
					col = col + 2;
				}
			}
		}

		m_game.move (Players.PLAYER1, 0, 0);
		m_game.move (Players.PLAYER2, 1, 0);
		m_game.move (Players.PLAYER1, 2, 0);
		m_game.move (Players.PLAYER2, 3, 0);
		m_game.move (Players.PLAYER2, 4, 0);
		m_game.move (Players.PLAYER1, 5, 0);
		m_game.move (Players.PLAYER2, 6, 0);
		m_game.move (Players.PLAYER1, 7, 0);
		m_game.move (Players.PLAYER1, 8, 0);

//		Output output = new OutputTTY();
//		m_moves.showMoves("save", output);
//		m_board.showBoard("save", output);

		int cnt = 0;
		for (int num = 0; num < m_board.getRows() * m_board.getColumns(); num++) {
			Coordinate coord = m_moves.getMoveByMoveNumber (num);
			if (coord == null) break;
			cnt++;
		}
		System.out.println("cnt "+cnt);
		assertEquals(cnt, m_moves.getCurrentMove());
	}
}
