package com.idc.five.junit.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.SizingItemInfo;

public class Sizing1Test {

	@Before
	public void initialize() {
	}

	@Test
	public void test_5_5() {
		SizingItemInfo item = new SizingItemInfo(5, 5);
		assertEquals(item.getRows(), 5);
		assertEquals(item.getColumns(), 5);
		assertEquals(item.getCalculatedBoardWidth(), 550);
		assertEquals(item.getCalculatedBoardHeight(), 550);
		assertEquals(item.getCalculatedCellSize(), 110);
		assertEquals(item.getCalculatedOffsetWidth(), 0);
		assertEquals(item.getCalculatedWindowWidth(), 550);
		assertEquals(item.getCalculatedWindowHeight(), 713);
	}

	@Test
	public void test_6_5() {
		SizingItemInfo item = new SizingItemInfo(6, 5);
		assertEquals(item.getRows(), 6);
		assertEquals(item.getColumns(), 5);
		assertEquals(item.getCalculatedBoardWidth(), 455);
		assertEquals(item.getCalculatedBoardHeight(), 546);
		assertEquals(item.getCalculatedCellSize(), 91);
		assertEquals(item.getCalculatedOffsetWidth(), 96);
		assertEquals(item.getCalculatedWindowWidth(), 551);
		assertEquals(item.getCalculatedWindowHeight(), 709);
	}

	@Test
	public void test_5_6() {
		SizingItemInfo item = new SizingItemInfo(5, 6);
		assertEquals(item.getRows(), 5);
		assertEquals(item.getColumns(), 6);
		assertEquals(item.getCalculatedBoardWidth(), 546);
		assertEquals(item.getCalculatedBoardHeight(), 455);
		assertEquals(item.getCalculatedCellSize(), 91);
		assertEquals(item.getCalculatedOffsetWidth(), 5);
		assertEquals(item.getCalculatedWindowWidth(), 551);
		assertEquals(item.getCalculatedWindowHeight(), 618);
	}

	@Test
	public void test_6_6() {
		SizingItemInfo item = new SizingItemInfo(6, 6);
		assertEquals(item.getRows(), 6);
		assertEquals(item.getColumns(), 6);
		assertEquals(item.getCalculatedBoardWidth(), 546);
		assertEquals(item.getCalculatedBoardHeight(), 546);
		assertEquals(item.getCalculatedCellSize(), 91);
		assertEquals(item.getCalculatedOffsetWidth(), 5);
		assertEquals(item.getCalculatedWindowWidth(), 551);
		assertEquals(item.getCalculatedWindowHeight(), 709);
	}
}
