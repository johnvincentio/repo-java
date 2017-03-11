package com.idc.sudoku;

public class Test2 {
	public static void main(String[] args) {
		(new Test2()).doTest();
	}
	private void doTest() {
		System.out.println("doTest");
//		doTest1();
		doTest4();
//		doTest3();
		System.out.println("End of test");
	}
	@SuppressWarnings("unused")
	private void doTest1() {
		int row;
		Pattern pattern;
		Board lBoard1, lBoard2;
		boolean bPoss;

		System.out.println("Test 1");
		row = 0;
		Bucket bt = new Bucket();
		bt.set (1,2,3,4,5,6,7,8,9);
		pattern = new Pattern (bt);
		lBoard1 = new Board();
		lBoard1.set(0,0,1);
//		lBoard1.show();
		lBoard2 = new Board();
		lBoard2.set(lBoard1);
		lBoard2.addPattern (row, pattern);
//		lBoard2.show();
		bPoss = Utils.isSituationPossible (row, pattern, lBoard1, lBoard2);
		System.out.println("bPoss "+bPoss);
	}
	@SuppressWarnings("unused")
	private void doTest2() {
		int row;
		Pattern pattern;
		Board lBoard1, lBoard2;
		boolean bPoss;

		System.out.println("Test 2");
		row = 0;
		Bucket bt = new Bucket();
		bt.set (1,2,3,4,5,6,7,8,9);
		pattern = new Pattern (bt);
		lBoard1 = new Board();
		lBoard1.set(1,1,1);
		lBoard1.show();
		lBoard2 = new Board();
		lBoard2.set(lBoard1);
		lBoard2.addPattern (row, pattern);
		lBoard2.show();
		bPoss = Utils.isSituationPossible (row, pattern, lBoard1, lBoard2);
		System.out.println("bPoss "+bPoss);
	}
	@SuppressWarnings("unused")
	private void doTest3() {
		int row;
		Pattern pattern;
		Board lBoard1, lBoard2;
		boolean bPoss;

		System.out.println("Test 3");
		row = 3;
		Bucket bt = new Bucket();
		bt.set (1,2,3,4,5,6,7,8,9);
		pattern = new Pattern (bt);
		lBoard1 = new Board();
		lBoard1.set(6,5,9);
		lBoard2 = new Board();
		lBoard2.set(lBoard1);
		lBoard2.addPattern (row, pattern);
		bPoss = Utils.isSituationPossible (row, pattern, lBoard1, lBoard2);
		System.out.println("bPoss "+bPoss);
	}
	private void doTest4() {
		int row;
		Pattern pattern;
		Board lBoard1, lBoard2;
		boolean bPoss;

		System.out.println("Test 4");
		row = 0;
		Bucket bt = new Bucket();
		bt.set (1,2,3,4,5,6,7,8,9);
		pattern = new Pattern (bt);
		lBoard1 = new Board();
		lBoard1.set(1,1,9);
		lBoard1.show();
		lBoard2 = new Board();
		lBoard2.set(lBoard1);
		lBoard2.addPattern (row, pattern);
		lBoard2.show();
		bPoss = Utils.isSituationPossible (row, pattern, lBoard1, lBoard2);
		System.out.println("bPoss "+bPoss);
	}
}
