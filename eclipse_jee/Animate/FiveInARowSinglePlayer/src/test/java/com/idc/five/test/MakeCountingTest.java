package com.idc.five.test;

import java.io.File;
import java.util.ArrayList;

import com.idc.five.counting.Counting;
import com.idc.five.counting.CountsItemInfo;
import com.idc.five.game.Board;
import com.idc.five.pattern.PatternUtils;
import com.idc.five.players.Players;
import com.idc.five.utils.JVFile;

public class MakeCountingTest {
	private Players m_players = new Players();
	private Board m_board;
	private Counting m_counting;

	public static void main(String[] args) {
		(new MakeCountingTest()).doAllTests();
	}
	private void doAllTests() {
//		doTest ("CountingATest", 1);
//		doTest ("CountingBTest", 2);
//		doTest ("CountingCTest", 3);
//		doTest ("CountingDTest", 4);
//		doTest ("CountingETest", 5);
//		doTest ("CountingFTest", 6);
//		doTest ("CountingGTest", 7);
		doTest ("CountingHTest", 8);
	}
	private void doTest (String name, int test) {
		JVFile out = new JVFile (new File("/tmp/" + name + ".java"));
		out.open();
 
		outTop (out, name);
		switch (test) {
		case 1:
			test1(out, "test");
			break;
		case 2:
			test2(out, "test");
			break;
		case 3:
			test3(out, "test");
			break;
		case 4:
			test4(out, "test");
			break;
		case 5:
			test5(out, "test");
			break;
		case 6:
			test6(out, "test");
			break;
		case 7:
			test7(out, "test");
			break;
		case 8:
			test8(out, "test");
			break;
		}

		out.writeNL("}");
		out.close();
	}

	private void test1 (JVFile out, String testMethodName) {
		ArrayList<String> code = new ArrayList<String>();
		m_board = new Board();
		m_counting = new Counting (m_players, m_board);
		code.add("m_counting = new Counting (m_players, m_board);");
		outTestCase (out, testMethodName, code);
	}

	private void test2 (JVFile out, String testMethodName) {
		ArrayList<String> code = new ArrayList<String>();
		m_board = new Board();
		m_board.setPlayer(Players.PLAYER1, 4, 6);
		m_counting = new Counting (m_players, m_board);
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 6);");
		code.add("m_counting = new Counting (m_players, m_board);");
		outTestCase (out, testMethodName, code);
	}

	private void test3 (JVFile out, String testMethodName) {
		ArrayList<String> code = new ArrayList<String>();
		m_board = new Board();
		m_board.setPlayer(Players.PLAYER1, 4, 6);
		m_board.setPlayer(Players.PLAYER2, 8, 3);
		m_counting = new Counting (m_players, m_board);
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 6);");
		code.add("m_board.setPlayer(Players.PLAYER2, 8, 3);");
		code.add("m_counting = new Counting (m_players, m_board);");
		outTestCase (out, testMethodName, code);
	}

	private void test4 (JVFile out, String testMethodName) {
		ArrayList<String> code = new ArrayList<String>();
		m_board = new Board();
		m_board.setPlayer(Players.PLAYER1, 4, 3);
		m_board.setPlayer(Players.PLAYER2, 5, 4);
		m_counting = new Counting (m_players, m_board);
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 3);");
		code.add("m_board.setPlayer(Players.PLAYER2, 5, 4);");
		code.add("m_counting = new Counting (m_players, m_board);");
		outTestCase (out, testMethodName, code);
	}

	private void test5 (JVFile out, String testMethodName) {
		ArrayList<String> code = new ArrayList<String>();
		m_board = new Board();
		m_board.setPlayer(Players.PLAYER1, 4, 3);
		m_board.setPlayer(Players.PLAYER1, 4, 4);
		m_board.setPlayer(Players.PLAYER2, 5, 4);
		m_board.setPlayer(Players.PLAYER2, 6, 3);
		m_counting = new Counting (m_players, m_board);
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 3);");
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 4);");
		code.add("m_board.setPlayer(Players.PLAYER2, 5, 4);");
		code.add("m_board.setPlayer(Players.PLAYER2, 6, 3);");
		code.add("m_counting = new Counting (m_players, m_board);");
		outTestCase (out, testMethodName, code);
	}

	private void test6 (JVFile out, String testMethodName) {
		ArrayList<String> code = new ArrayList<String>();
		m_board = new Board();
		m_board.setPlayer(Players.PLAYER1, 4, 2);
		m_board.setPlayer(Players.PLAYER1, 4, 3);
		m_board.setPlayer(Players.PLAYER1, 4, 4);
		m_board.setPlayer(Players.PLAYER2, 5, 4);
		m_board.setPlayer(Players.PLAYER2, 6, 3);
		m_board.setPlayer(Players.PLAYER2, 7, 2);
		m_counting = new Counting (m_players, m_board);
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 2);");
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 3);");
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 4);");
		code.add("m_board.setPlayer(Players.PLAYER2, 5, 4);");
		code.add("m_board.setPlayer(Players.PLAYER2, 6, 3);");
		code.add("m_board.setPlayer(Players.PLAYER2, 7, 2);");
		code.add("m_counting = new Counting (m_players, m_board);");
		outTestCase (out, testMethodName, code);
	}

	private void test7 (JVFile out, String testMethodName) {
		ArrayList<String> code = new ArrayList<String>();
		m_board = new Board();
		m_board.setPlayer(Players.PLAYER1, 4, 1);
		m_board.setPlayer(Players.PLAYER1, 4, 2);
		m_board.setPlayer(Players.PLAYER1, 4, 3);
		m_board.setPlayer(Players.PLAYER1, 4, 4);
		m_board.setPlayer(Players.PLAYER2, 5, 4);
		m_board.setPlayer(Players.PLAYER2, 6, 3);
		m_board.setPlayer(Players.PLAYER2, 7, 2);
		m_board.setPlayer(Players.PLAYER2, 8, 1);
		m_counting = new Counting (m_players, m_board);
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 1);");
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 2);");
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 3);");
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 4);");
		code.add("m_board.setPlayer(Players.PLAYER2, 5, 4);");
		code.add("m_board.setPlayer(Players.PLAYER2, 6, 3);");
		code.add("m_board.setPlayer(Players.PLAYER2, 7, 2);");
		code.add("m_board.setPlayer(Players.PLAYER2, 8, 1);");
		code.add("m_counting = new Counting (m_players, m_board);");
		outTestCase (out, testMethodName, code);
	}

	private void test8 (JVFile out, String testMethodName) {
		ArrayList<String> code = new ArrayList<String>();
		m_board = new Board();
		m_board.setPlayer(Players.PLAYER1, 2, 2);
		m_board.setPlayer(Players.PLAYER1, 3, 3);
		m_board.setPlayer(Players.PLAYER1, 4, 3);
		m_board.setPlayer(Players.PLAYER1, 5, 3);

		m_board.setPlayer(Players.PLAYER1, 6, 5);
		m_board.setPlayer(Players.PLAYER1, 5, 6);
		m_board.setPlayer(Players.PLAYER1, 4, 7);
		m_board.setPlayer(Players.PLAYER1, 7, 8);

		m_board.setPlayer(Players.PLAYER2, 0, 0);
		m_board.setPlayer(Players.PLAYER2, 1, 0);
		m_board.setPlayer(Players.PLAYER2, 2, 0);
		
		m_board.setPlayer(Players.PLAYER2, 7, 1);
		m_board.setPlayer(Players.PLAYER2, 7, 2);
		m_board.setPlayer(Players.PLAYER2, 7, 3);

		m_board.setPlayer(Players.PLAYER2, 1, 3);
		m_board.setPlayer(Players.PLAYER2, 2, 4);
		m_board.setPlayer(Players.PLAYER2, 3, 5);

		m_board.setPlayer(Players.PLAYER2, 0, 7);
		m_board.setPlayer(Players.PLAYER2, 0, 8);

		m_counting = new Counting (m_players, m_board);
		code.add("m_board.setPlayer(Players.PLAYER1, 2, 2);");
		code.add("m_board.setPlayer(Players.PLAYER1, 3, 3);");
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 3);");
		code.add("m_board.setPlayer(Players.PLAYER1, 5, 3);");
		code.add("m_board.setPlayer(Players.PLAYER1, 6, 5);");
		code.add("m_board.setPlayer(Players.PLAYER1, 5, 6);");
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 7);");
		code.add("m_board.setPlayer(Players.PLAYER1, 7, 8);");

		code.add("m_board.setPlayer(Players.PLAYER2, 0, 0);");
		code.add("m_board.setPlayer(Players.PLAYER2, 1, 0);");
		code.add("m_board.setPlayer(Players.PLAYER2, 2, 0);");
		
		code.add("m_board.setPlayer(Players.PLAYER2, 7, 1);");
		code.add("m_board.setPlayer(Players.PLAYER2, 7, 2);");
		code.add("m_board.setPlayer(Players.PLAYER2, 7, 3);");

		code.add("m_board.setPlayer(Players.PLAYER2, 1, 3);");
		code.add("m_board.setPlayer(Players.PLAYER2, 2, 4);");
		code.add("m_board.setPlayer(Players.PLAYER2, 3, 5);");

		code.add("m_board.setPlayer(Players.PLAYER2, 0, 7);");
		code.add("m_board.setPlayer(Players.PLAYER2, 0, 8);");

		code.add("m_counting = new Counting (m_players, m_board);");
		outTestCase (out, testMethodName, code);
	}
	/*
	 * make class level code
	 */
	private void outTop (JVFile out, String clazzName) {
		out.writeNL("package com.idc.five.junit.counting;");
		out.writeNL("");
		out.writeNL("import static org.junit.Assert.assertEquals;");
		out.writeNL("import static org.junit.Assert.assertFalse;");
		out.writeNL("import static org.junit.Assert.assertNotNull;");
		out.writeNL("import static org.junit.Assert.assertNull;");
		out.writeNL("import static org.junit.Assert.assertTrue;");
		out.writeNL("");
		out.writeNL("import org.junit.Before;");
		out.writeNL("import org.junit.Test;");
		out.writeNL("");

		out.writeNL("");
		out.writeNL("import com.idc.five.game.Board;");
		out.writeNL("import com.idc.five.players.Players;");
		out.writeNL("import com.idc.five.counting.Counting;");
		out.writeNL("import com.idc.five.counting.CountsItemInfo;");
		out.writeNL("");
		out.writeNL("public class "+clazzName+" {");
		out.writeNL("");
		out.writeNL("private Players m_players;");
		out.writeNL("private Board m_board;");
		out.writeNL("private Counting m_counting;");
		out.writeNL("");
		out.writeNL("@Before");
		out.writeNL("public void initialize() {");
		out.writeNL("m_players = new Players();");
		out.writeNL("m_board = new Board();");
		out.writeNL("}");
		out.writeNL("");
	}

	/*
	 * make code for the individual test case
	 */
	private void outTestCase (JVFile out, String testMethodName, ArrayList<String> code) {
		out.writeNL("@Test");
		out.writeNL("public void "+testMethodName+"() {");
		out.writeNL("CountsItemInfo counts;");
		if (code != null) {
			for (String str : code) {
				out.writeNL(str);
			}
		}

		for (int player = Players.PLAYER1; player <= Players.PLAYER2; player++) {
			for (int r = 0; r < m_board.getRows(); r++) {
				for (int c = 0; c < m_board.getColumns(); c++) {
					for (int pattern = 0; pattern < PatternUtils.getNumberOfVictoryPatterns(); pattern++) {
						CountsItemInfo counts = m_counting.getCounts (player, r, c, pattern);
						out.writeNL("");
						out.writeNL("counts = m_counting.getCounts ("+player+", "+r+", "+c+", "+pattern+");");

						if (m_board.isEmpty(r, c)) {
							out.writeNL("assertTrue (m_board.isEmpty ("+r+", "+c+"));");
							out.writeNL("assertNotNull (counts);");
							out.writeNL("assertEquals (counts.getActual(), "+counts.getActual()+");");
							out.writeNL("assertEquals (counts.getPossible(), "+counts.getPossible()+");");
							out.writeNL("assertEquals (counts.isBorder(), "+counts.isBorder()+");");
							out.writeNL("assertEquals (counts.isBounded(), "+counts.isBounded()+");");
						}
						else {
							out.writeNL("assertTrue (m_board.isNotEmpty ("+r+", "+c+"));");
							out.writeNL("assertNull (counts);");
						}
					}
				}
			}
		}
		out.writeNL("}");
	}
}
