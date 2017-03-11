package com.idc.five.junit.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.SizingItemInfo;

public class Sizing2Test {

	@Before
	public void initialize() {
	}

	@Test
	public void test_20_20() {
		SizingItemInfo item = new SizingItemInfo(20, 20);
		assertEquals(item.getRows(), 20);
		assertEquals(item.getColumns(), 20);
		assertEquals(item.getCalculatedBoardWidth(), 600);
		assertEquals(item.getCalculatedBoardHeight(), 600);
		assertEquals(item.getCalculatedCellSize(), 30);
		assertEquals(item.getCalculatedOffsetWidth(), 0);
		assertEquals(item.getCalculatedWindowWidth(), 600);
		assertEquals(item.getCalculatedWindowHeight(), 763);
	}

	@Test
	public void test_19_20() {
		SizingItemInfo item = new SizingItemInfo(19, 20);
		assertEquals(item.getRows(), 19);
		assertEquals(item.getColumns(), 20);
		assertEquals(item.getCalculatedBoardWidth(), 600);
		assertEquals(item.getCalculatedBoardHeight(), 570);
		assertEquals(item.getCalculatedCellSize(), 30);
		assertEquals(item.getCalculatedOffsetWidth(), 0);
		assertEquals(item.getCalculatedWindowWidth(), 600);
		assertEquals(item.getCalculatedWindowHeight(), 733);
	}

	@Test
	public void test_18_20() {
		SizingItemInfo item = new SizingItemInfo(18, 20);
		assertEquals(item.getRows(), 18);
		assertEquals(item.getColumns(), 20);
		assertEquals(item.getCalculatedBoardWidth(), 600);
		assertEquals(item.getCalculatedBoardHeight(), 540);
		assertEquals(item.getCalculatedCellSize(), 30);
		assertEquals(item.getCalculatedOffsetWidth(), 0);
		assertEquals(item.getCalculatedWindowWidth(), 600);
		assertEquals(item.getCalculatedWindowHeight(), 703);
	}

	@Test
	public void test_15_20() {
		SizingItemInfo item = new SizingItemInfo(15, 20);
		assertEquals(item.getRows(), 15);
		assertEquals(item.getColumns(), 20);
		assertEquals(item.getCalculatedBoardWidth(), 600);
		assertEquals(item.getCalculatedBoardHeight(), 450);
		assertEquals(item.getCalculatedCellSize(), 30);
		assertEquals(item.getCalculatedOffsetWidth(), 0);
		assertEquals(item.getCalculatedWindowWidth(), 600);
		assertEquals(item.getCalculatedWindowHeight(), 613);
	}

	@Test
	public void test_10_20() {
		SizingItemInfo item = new SizingItemInfo(10, 20);
		assertEquals(item.getRows(), 10);
		assertEquals(item.getColumns(), 20);
		assertEquals(item.getCalculatedBoardWidth(), 600);
		assertEquals(item.getCalculatedBoardHeight(), 300);
		assertEquals(item.getCalculatedCellSize(), 30);
		assertEquals(item.getCalculatedOffsetWidth(), 0);
		assertEquals(item.getCalculatedWindowWidth(), 600);
		assertEquals(item.getCalculatedWindowHeight(), 463);
	}

	@Test
	public void test_5_20() {
		SizingItemInfo item = new SizingItemInfo(5, 20);
		assertEquals(item.getRows(), 5);
		assertEquals(item.getColumns(), 20);
		assertEquals(item.getCalculatedBoardWidth(), 600);
		assertEquals(item.getCalculatedBoardHeight(), 150);
		assertEquals(item.getCalculatedCellSize(), 30);
		assertEquals(item.getCalculatedOffsetWidth(), 0);
		assertEquals(item.getCalculatedWindowWidth(), 600);
		assertEquals(item.getCalculatedWindowHeight(), 313);
	}

	@Test
	public void test_20_19() {
		SizingItemInfo item = new SizingItemInfo(20, 19);
		assertEquals(item.getRows(), 20);
		assertEquals(item.getColumns(), 19);
		assertEquals(item.getCalculatedBoardWidth(), 570);
		assertEquals(item.getCalculatedBoardHeight(), 600);
		assertEquals(item.getCalculatedCellSize(), 30);
		assertEquals(item.getCalculatedOffsetWidth(), 0);
		assertEquals(item.getCalculatedWindowWidth(), 570);
		assertEquals(item.getCalculatedWindowHeight(), 763);
	}

	@Test
	public void test_20_18() {
		SizingItemInfo item = new SizingItemInfo(20, 18);
		assertEquals(item.getRows(), 20);
		assertEquals(item.getColumns(), 18);
		assertEquals(item.getCalculatedBoardWidth(), 540);
		assertEquals(item.getCalculatedBoardHeight(), 600);
		assertEquals(item.getCalculatedCellSize(), 30);
		assertEquals(item.getCalculatedOffsetWidth(), 11);
		assertEquals(item.getCalculatedWindowWidth(), 551);
		assertEquals(item.getCalculatedWindowHeight(), 763);
	}
}
