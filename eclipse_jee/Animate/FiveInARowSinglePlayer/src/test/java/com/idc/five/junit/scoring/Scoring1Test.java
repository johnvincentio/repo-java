package com.idc.five.junit.scoring;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.Board;
import com.idc.five.game.Game;
import com.idc.five.game.Moves;
import com.idc.five.pattern.PatternUtils;
import com.idc.five.players.Players;

public class Scoring1Test {
	Game m_game;
	Players m_players = new Players();
	Board m_board = new Board();
	Moves m_moves = new Moves(m_players, m_board);

	@Before
	public void initialize() {
		m_game = new Game (m_players, m_board, m_moves);
	}

	/*
	 * tests for a 9x9 board
	 */

	@Test
	public void test1() {
		m_game.move (Players.PLAYER1, 3, 2);
		assertEquals (m_board.getPlayerAt(3, 2), Players.PLAYER1);
//		m_game.show ("Test1");

//		assertEquals (m_game.getScoring().calculateCount (true, true, Game.PLAYER1, 3, 2, 1), 1);
//		assertEquals (m_game.getScoring().calculateCount (true, true, Game.PLAYER2, 3, 2, 1), 0);

		for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
//			assertEquals (m_game.getScoring().calculateCount(true, false, Game.PLAYER2, 3, 2, pattern), 0);
		}

//		assertEquals (m_game.getScoring().calculateCount (true, false, Game.PLAYER1, 3, 2, 0), 9);
//		assertEquals (m_game.getScoring().calculateCount (true, false, Game.PLAYER1, 3, 2, 1), 9);
//		assertEquals (m_game.getScoring().calculateCount (true, false, Game.PLAYER1, 3, 2, 2), 6);
//		assertEquals (m_game.getScoring().calculateCount (true, false, Game.PLAYER1, 3, 2, 3), 8);
	}

	@Test
	public void JVtest2() {
		m_game.move (Players.PLAYER1, 3, 2);
		m_game.move (Players.PLAYER1, 2, 2);
		m_game.move (Players.PLAYER1, 0, 2);
		assertEquals (m_board.getPlayerAt(3, 2), Players.PLAYER1);
		assertEquals (m_board.getPlayerAt(2, 2), Players.PLAYER1);
		assertEquals (m_board.getPlayerAt(0, 2), Players.PLAYER1);
//		m_game.show ("Test1");

//		assertEquals (m_game.getScoring().calculateCount(true, true, Game.PLAYER1, 3, 2, 0), 2);
//		assertEquals (m_game.getScoring().calculateCount(true, true, Game.PLAYER1, 2, 2, 0), 2);
//		assertEquals (m_game.getScoring().calculateCount(true, true, Game.PLAYER1, 0, 2, 0), 1);
//		assertEquals (m_game.getScoring().calculateCount(true, true, Game.PLAYER2, 3, 2, 0), 0);
//		assertEquals (m_game.getScoring().calculateCount(true, true, Game.PLAYER2, 2, 2, 0), 0);
	}

	@Test
	public void JVtest3() {
		m_game.move (Players.PLAYER1, 3, 2);
		m_game.move (Players.PLAYER1, 2, 2);
		m_game.move (Players.PLAYER1, 0, 2);
		assertEquals (m_board.getPlayerAt(3, 2), Players.PLAYER1);
		assertEquals (m_board.getPlayerAt(2, 2), Players.PLAYER1);
		assertEquals (m_board.getPlayerAt(0, 2), Players.PLAYER1);
//		m_game.show ("Test1");

//		assertEquals (m_game.getScoring().calculateCount(true, false, Game.PLAYER1, 3, 2, 0), 9);
//		assertEquals (m_game.getScoring().calculateCount(true, false, Game.PLAYER1, 2, 2, 0), 9);
//		assertEquals (m_game.getScoring().calculateCount(true, false, Game.PLAYER1, 0, 2, 0), 9);
	}
}
