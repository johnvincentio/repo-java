package com.idc.five.junit.scoring;

import junit.framework.TestCase;

import com.idc.five.pattern.PatternUtils;

public class Scoring2Test extends TestCase {
	public void test1() {
//		Board board = new Board();
//		Scoring scoring = new Scoring(board);
		assertEquals (PatternUtils.getNumberOfVictoryPatterns(), 4);
	}
}
