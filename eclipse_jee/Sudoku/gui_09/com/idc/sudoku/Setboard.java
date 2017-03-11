
package com.idc.sudoku;

public class Setboard {

	public static Board getUserBoard (int scenario) {
		switch (scenario) {
			case 1: return setBoard9();
			case 2: return setBoard9a();

			case -1: return setBoard3();
			case -2: return setBoard3a();
			case -3: return setBoard4();
			case -4: return setBoard6();
			case -5: return setBoard6a();
		}
		return (new Board());
	}
	private static Board setBoard9() {
		Board board = new Board();
		board.set(1,0,9); board.set(3,0,6); board.set(5,0,2); board.set(8,0,4);
		board.set(1,1,7); board.set(6,1,5);
		board.set(2,2,2); board.set(3,2,3); board.set(7,2,6);
		board.set(2,3,8); board.set(3,3,1); board.set(8,3,9);
		board.set(1,4,4); board.set(7,4,5);
		board.set(0,5,5); board.set(5,5,3); board.set(6,5,4);
		board.set(1,6,6); board.set(5,6,8); board.set(6,6,9);
		board.set(2,7,7); board.set(7,7,4);
		board.set(0,8,1); board.set(3,8,5); board.set(5,8,6); board.set(7,8,7);
		return board;
	}
	private static Board setBoard9a() {
		Board board = new Board();
		board.set(3,0,4); board.set(5,0,5);

		board.set(1,2,6); board.set(2,2,4);
		board.set(6,2,9); board.set(7,2,3);

		board.set(1,3,5);
		board.set(2,3,6);
		board.set(6,3,2);
		board.set(7,3,7);

		board.set(3,4,7);
		board.set(5,4,3);
		board.set(7,4,9);

		board.set(1,5,9);
		board.set(3,5,6);
		board.set(5,5,1);
		board.set(7,5,4);

		board.set(0,6,4);
		board.set(1,6,1);
		board.set(7,6,5);
		board.set(8,6,8);

		board.set(1,7,7);
		board.set(2,7,5);
		board.set(3,7,3);
		board.set(5,7,9);
		board.set(6,7,6);
		board.set(7,7,1);

		board.set(1,8,8);
		board.set(7,8,2);
		return board;
	}
	private static Board setBoard3() {
		Board board = new Board();
		board.set(0,0,3);
		board.set(1,1,2);
		board.set(2,2,1);
		return board;
	}
	private static Board setBoard3a() {
		Board board = new Board();
		board.set(0,0,1);
		board.set(1,1,2);
		board.set(2,2,3);
		return board;
	}
	private static Board setBoard4() {
		Board board = new Board();
		board.set(0,0,2);
		board.set(1,1,3);
		board.set(2,2,4);
		board.set(3,3,1);
		return board;
	}
	private static Board setBoard6() {
		Board board = new Board();
		board.set(0,0,3); board.set(2,0,2); board.set(5,0,5);
		board.set(1,1,5); board.set(3,1,3);
		board.set(2,2,1); board.set(4,2,3);
		board.set(0,3,2); board.set(3,3,4);
		board.set(1,4,4); board.set(4,4,6);
		board.set(2,5,5); board.set(5,5,2);
		return board;
	}
	private static Board setBoard6a() {
		Board board = new Board();
		board.set(0,0,3);
		board.set(1,1,5);
		board.set(2,2,1);
		board.set(3,3,4);
		board.set(4,4,6);
		board.set(5,5,2);
		return board;
	}
}
