package com.idc.five.junit.coordinate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.idc.five.game.Coordinate;

public class CoordinateTest {
	@Test
	public void test_one() {
		Coordinate coordinate = new Coordinate (6, 2);
		assertTrue (coordinate != null);
		assertEquals (coordinate.getRow(), 6);
		assertEquals (coordinate.getCol(), 2);
	}

	@Test
	public void test_two() {
		Coordinate coordinate = new Coordinate (3, 8);
		assertTrue (coordinate != null);
		assertEquals (coordinate.getRow(), 3);
		assertEquals (coordinate.getCol(), 8);
	}
}
