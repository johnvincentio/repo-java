package com.idc.sudoku;

public class Test1 {
	public static void main(String[] args) {
		(new Test1()).doTest();
	}
	private void doTest() {
		System.out.println("Test");
		doTest1();
		doTest2();
		System.out.println("End of test");
	}
	private void doTest1() {
		System.out.println("Test 1");
		Bucket bt = new Bucket();
		bt.set (1,2,3,4,5,6,7,8,9);
		if (Utils.isPattern(bt))
			System.out.println("value "+bt.getStringAllBuckets()+" is valid");
		else
			System.out.println("value "+bt.getStringAllBuckets()+" is not valid");
	}
	private void doTest2() {
		System.out.println("Test 2");
		Bucket bt = new Bucket();
		bt.set (1,2,3,4,5,6,7,8,8);
		if (Utils.isPattern(bt))
			System.out.println("value "+bt.getStringAllBuckets()+" is valid");
		else
			System.out.println("value "+bt.getStringAllBuckets()+" is not valid");
	}
}
