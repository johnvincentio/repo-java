package com.idc.five.junit.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.idc.five.game.Coordinate;
import com.idc.five.game.SizingItemInfo;

public class Sizing4Test {

	private Coordinate coord;
	private SizingItemInfo item;

	@Before
	public void initialize() {
		item = new SizingItemInfo(13, 5);
	}

	@Test
	public void start_13_5_a() {
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
	public void test_165_28() {
		coord = item.calculateCoordinate (165, 28);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), -1);
	}

	@Test
	public void test_21_25() {
		coord = item.calculateCoordinate (21, 25);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), -1);
	}

	@Test
	public void test_156_66() {
		coord = item.calculateCoordinate (156, 66);
		assertEquals(coord.getRow(), 1);
		assertEquals(coord.getCol(), -1);
	}

	@Test
	public void test_171_28() {
		coord = item.calculateCoordinate (171, 28);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), 0);
	}

	@Test
	public void test_220_30() {
		coord = item.calculateCoordinate (220, 30);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), 1);
	}

	@Test
	public void test_270_30() {
		coord = item.calculateCoordinate (270, 30);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), 2);
	}

	@Test
	public void test_320_30() {
		coord = item.calculateCoordinate (320, 30);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), 3);
	}

	@Test
	public void test_360_30() {
		coord = item.calculateCoordinate (360, 30);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), 4);
	}

	@Test
	public void test_378_30() {
		coord = item.calculateCoordinate (378, 30);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), 4);
	}

	@Test
	public void test_381_30() {
		coord = item.calculateCoordinate (381, 30);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), -1);
	}

	@Test
	public void test_500_30() {
		coord = item.calculateCoordinate (500, 30);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), -1);
	}

	@Test
	public void test_550_30() {
		coord = item.calculateCoordinate (550, 30);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), -1);
	}

	@Test
	public void test_560_30() {
		coord = item.calculateCoordinate (560, 30);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), -1);
	}

	@Test
	public void test_600_30() {
		coord = item.calculateCoordinate (600, 30);
		assertEquals(coord.getRow(), 0);
		assertEquals(coord.getCol(), -1);
	}

	@Test
	public void test_200_525() {
		coord = item.calculateCoordinate (200, 525);
		assertEquals(coord.getRow(), 12);
		assertEquals(coord.getCol(), 0);
	}

	@Test
	public void test_230_525() {
		coord = item.calculateCoordinate (230, 525);
		assertEquals(coord.getRow(), 12);
		assertEquals(coord.getCol(), 1);
	}

	@Test
	public void test_260_525() {
		coord = item.calculateCoordinate (260, 525);
		assertEquals(coord.getRow(), 12);
		assertEquals(coord.getCol(), 2);
	}

	@Test
	public void test_360_525() {
		coord = item.calculateCoordinate (360, 525);
		assertEquals(coord.getRow(), 12);
		assertEquals(coord.getCol(), 4);
	}

	@Test
	public void test_360_560() {
		coord = item.calculateCoordinate (360, 560);
		assertEquals(coord.getRow(), -1);
		assertEquals(coord.getCol(), 4);
	}
}
