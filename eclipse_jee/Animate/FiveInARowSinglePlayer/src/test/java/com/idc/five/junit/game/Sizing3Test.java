package com.idc.five.junit.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.SizingItemInfo;

public class Sizing3Test {

	@Before
	public void initialize() {
	}

	@Test
	public void test_13_5() {
		SizingItemInfo item = new SizingItemInfo(13, 5);
		assertEquals(item.getRows(), 13);
		assertEquals(item.getColumns(), 5);
		assertEquals(item.getCalculatedBoardWidth(), 210);
		assertEquals(item.getCalculatedBoardHeight(), 546);
		assertEquals(item.getCalculatedCellSize(), 42);
		assertEquals(item.getCalculatedOffsetWidth(), 341);
		assertEquals(item.getCalculatedWindowWidth(), 551);
		assertEquals(item.getCalculatedWindowHeight(), 709);
	}

	@Test
	public void test_15_9() {
		SizingItemInfo item = new SizingItemInfo(15, 9);
		assertEquals(item.getRows(), 15);
		assertEquals(item.getColumns(), 9);
		assertEquals(item.getCalculatedBoardWidth(), 324);
		assertEquals(item.getCalculatedBoardHeight(), 540);
		assertEquals(item.getCalculatedCellSize(), 36);
		assertEquals(item.getCalculatedOffsetWidth(), 227);
		assertEquals(item.getCalculatedWindowWidth(), 551);
		assertEquals(item.getCalculatedWindowHeight(), 703);
	}
}
