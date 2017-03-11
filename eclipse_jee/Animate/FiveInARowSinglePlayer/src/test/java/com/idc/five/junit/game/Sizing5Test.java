package com.idc.five.junit.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.SizingItemInfo;

public class Sizing5Test {

	@Before
	public void initialize() {
	}

	@Test
	public void test_9_9() {
		SizingItemInfo item = new SizingItemInfo(9, 9);
		assertEquals(item.getRows(), 9);
		assertEquals(item.getColumns(), 9);
		assertEquals(item.getCalculatedBoardWidth(), 549);
		assertEquals(item.getCalculatedBoardHeight(), 549);
		assertEquals(item.getCalculatedCellSize(), 61);
		assertEquals(item.getCalculatedOffsetWidth(), 2);
		assertEquals(item.getCalculatedWindowWidth(), 551);
		assertEquals(item.getCalculatedWindowHeight(), 712);
	}
}
