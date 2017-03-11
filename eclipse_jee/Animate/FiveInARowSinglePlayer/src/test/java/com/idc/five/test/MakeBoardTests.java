package com.idc.five.test;

import java.io.File;
import java.util.ArrayList;

import com.idc.five.game.Board;
import com.idc.five.players.Players;
import com.idc.five.utils.JVFile;

public class MakeBoardTests {
//	private static final String NL = "\n";
//	private static final String TAB = "\t";

	private Board m_board;

	private JVFile m_out = new JVFile (new File("/tmp/boardTests.java"));

	public static void main(String[] args) {
		(new MakeBoardTests()).doTests();
	}
	private void doTests() {
		m_out.open();
		outTop("BoardATest");

		test1("test_one");
		test2("test_two");
		test3("test_three");
		test4("test_four");
		testAll("test_all");

		m_out.writeNL("}");
		m_out.close();
	}

	private void test1 (String testMethodName) {
		m_board = new Board();
//		m_board.showBoard("test1");
		outTestCase (testMethodName, null);
	}

	private void test2 (String testMethodName) {
		ArrayList<String> code = new ArrayList<String>();
		m_board = new Board();
		m_board.setPlayer(Players.PLAYER1, 4, 4);
		code.add("m_board.setPlayer(Players.PLAYER1, 4, 4);");
		outTestCase (testMethodName, code);
	}

	private void test3 (String testMethodName) {
		ArrayList<String> code = new ArrayList<String>();
		m_board = new Board();
		m_board.setPlayer(Players.PLAYER2, 0, 0);
		code.add("m_board.setPlayer(Players.PLAYER2, 0, 0);");
		outTestCase (testMethodName, code);
	}

	private void test4 (String testMethodName) {
		ArrayList<String> code = new ArrayList<String>();
		m_board = new Board();
		m_board.setPlayer(Players.PLAYER1, 3, 6);
		m_board.setPlayer(Players.PLAYER2, 7, 2);
		code.add("m_board.setPlayer(Players.PLAYER1, 3, 6);");
		code.add("m_board.setPlayer(Players.PLAYER2, 7, 2);");
		outTestCase (testMethodName, code);
	}

	private void testAll (String testMethodName) {
		ArrayList<String> code = new ArrayList<String>();
		m_board = new Board();
		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				m_board.setPlayer(Players.PLAYER1, r, c);
				code.add("m_board.setPlayer("+Players.PLAYER1+", "+r+", "+c+");");
			}
		}
		outTestCase (testMethodName, code);
	}

	/*
	 * make class level code
	 */
	private void outTop (String clazzName) {
		m_out.writeNL("package com.idc.five.junit.board;");
		m_out.writeNL("");
		m_out.writeNL("import static org.junit.Assert.assertEquals;");
		m_out.writeNL("import static org.junit.Assert.assertFalse;");
		m_out.writeNL("import static org.junit.Assert.assertNotNull;");
		m_out.writeNL("import static org.junit.Assert.assertNull;");
		m_out.writeNL("import static org.junit.Assert.assertTrue;");
		m_out.writeNL("");
		m_out.writeNL("import org.junit.Before;");
		m_out.writeNL("import org.junit.Test;");
		m_out.writeNL("");

		m_out.writeNL("");
		m_out.writeNL("import com.idc.five.game.Board;");
		m_out.writeNL("import com.idc.five.players.Players;");
		m_out.writeNL("");
		m_out.writeNL("public class "+clazzName+" {");
		m_out.writeNL("");
		m_out.writeNL("private Board m_board;");
		m_out.writeNL("");
		m_out.writeNL("@Before");
		m_out.writeNL("public void initialize() {");
		m_out.writeNL("m_board = new Board();");
		m_out.writeNL("}");
		m_out.writeNL("");
	}

	/*
	 * make code for the individual test case
	 */
	private void outTestCase (String testMethodName, ArrayList<String> code) {
		m_out.writeNL("@Test");
		m_out.writeNL("public void "+testMethodName+"() {");
		if (code != null) {
			for (String str : code) {
				m_out.writeNL(str);
			}
			m_out.writeNL("");
		}

		for (int r = 0; r < m_board.getRows(); r++) {
			for (int c = 0; c < m_board.getColumns(); c++) {
				if (m_board.isEmpty(r, c)) {
					m_out.writeNL("assertTrue (m_board.isEmpty ("+r+", "+c+"));");
				}
				else {
					m_out.writeNL("assertTrue (m_board.isNotEmpty ("+r+", "+c+"));");
					int num = m_board.getPlayerAt(r, c);
					m_out.writeNL("assertEquals(m_board.getPlayerAt("+r+", "+c+"), "+num+");");
				}
			}
		}
		m_out.writeNL("}");
	}
}
