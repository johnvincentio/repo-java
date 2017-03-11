package com.idc.c2;

import java.util.Calendar;

import junit.framework.TestCase;

public class Test extends TestCase {
	public long getTime (int hours, int minutes, int seconds, int millis) {
		Calendar m_calendar = Calendar.getInstance();
		m_calendar.set (Calendar.HOUR_OF_DAY, hours);
		m_calendar.set (Calendar.MINUTE, minutes);
		m_calendar.set (Calendar.SECOND, seconds);
		m_calendar.set (Calendar.MILLISECOND, millis);
		return m_calendar.getTimeInMillis();
	}

	public void test1() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 20, 13, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
	}

	public void test1a() {		// test quarter hour
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 14, 59, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
	}
	public void test1b() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 15, 1, 0));
		assertNotNull (result);
		assertTrue (result.isQuarterHour());
	}
	public void test1c() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 15, 2, 0));
		assertNotNull (result);
		assertTrue (result.isQuarterHour());
	}
	public void test1d() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 15, 3, 0));
		assertNotNull (result);
		assertFalse (result.isQuarterHour());
		assertTrue (result.isNothing());
	}

	public void test2a() {			// test 3/4 hour
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 44, 59, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
	}
	public void test2b() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 45, 1, 0));
		assertNotNull (result);
		assertTrue (result.isQuarterHour());
	}
	public void test2c() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 45, 2, 0));
		assertNotNull (result);
		assertTrue (result.isQuarterHour());
	}
	public void test2d() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 45, 3, 0));
		assertNotNull (result);
		assertFalse (result.isQuarterHour());
		assertTrue (result.isNothing());
	}

	public void test3a() {		// test hour
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (1, 25, 59, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}
	public void test3b() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (1, 25, 0, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}
	public void test3c() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (1, 25, 1, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}
	public void test3d() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (1, 25, 2, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}
	public void test3e() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (1, 25, 3, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}
	public void test3f() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (1, 59, 59, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}
	public void test3g() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (1, 59, 0, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}
	public void test3h() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (1, 59, 59, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}
	public void test3j() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (1, 0, 0, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 1);
	}
	public void test3k() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (1, 0, 1, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 1);
	}
	public void test3m() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (1, 0, 2, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 1);
	}
	public void test3n() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (1, 0, 3, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}

	public void test4a() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (10, 59, 59, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}
	public void test4b() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (10, 0, 0, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 10);
	}
	public void test4c() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (10, 0, 1, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 10);
	}
	public void test4d() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (10, 0, 2, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 10);
	}
	public void test4e() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (10, 0, 3, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}

	public void test5a() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (12, 59, 59, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}
	public void test5b() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (12, 0, 0, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 12);
	}
	public void test5c() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (12, 0, 1, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 12);
	}
	public void test5d() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (12, 0, 2, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 12);
	}
	public void test5e() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (12, 0, 3, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}

	public void test6a() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (12, 59, 59, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}
	public void test6b() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (13, 0, 0, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 13);
	}
	public void test6c() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (13, 0, 1, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 13);
	}
	public void test6d() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (13, 0, 2, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 13);
	}
	public void test6e() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (13, 0, 3, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}

	public void test7a() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (23, 59, 59, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}
	public void test7b() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (24, 0, 0, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 24);
	}
	public void test7c() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (24, 0, 1, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 24);
	}
	public void test7d() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (24, 0, 2, 0));
		assertNotNull (result);
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertTrue(result.isHour());
		assertEquals (result.getHours(), 24);
	}
	public void test7e() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (24, 0, 3, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isHour());
	}

	public void test8a() {		// test half hour
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 29, 59, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
	}
	public void test8b() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 30, 0, 0));
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertTrue (result.isHalfHour());
		assertFalse(result.isHour());
	}
	public void test8c() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 30, 1, 0));
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertTrue (result.isHalfHour());
		assertFalse(result.isHour());
	}
	public void test8d() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 30, 2, 0));
		assertFalse (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertTrue (result.isHalfHour());
		assertFalse(result.isHour());
	}
	public void test8e() {
		Calculate calculate = new Calculate(2);
		Result result = calculate.handle (getTime (4, 30, 3, 0));
		assertNotNull (result);
		assertTrue (result.isNothing());
		assertFalse (result.isQuarterHour());
		assertFalse (result.isHalfHour());
		assertFalse(result.isHour());
	}
}
