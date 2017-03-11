package com.idc.teehee;

import java.util.Calendar;
import junit.framework.TestCase;

public class Test1 extends TestCase {
	public Calendar getCalendar(long currentTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentTime);
		System.out.println(calendar.get(Calendar.HOUR_OF_DAY) + ","
				+ calendar.get(Calendar.MINUTE) + ","
				+ calendar.get(Calendar.SECOND) + ", "
				+ calendar.get(Calendar.MILLISECOND));
		return calendar;
	}

	public void testl() {
		Calendar calendar = getCalendar(1262022301001L);
		assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 12);
		assertEquals(calendar.get(Calendar.MINUTE), 45);
		assertEquals(calendar.get(Calendar.SECOND), 1);
		assertEquals(calendar.get(Calendar.MILLISECOND), 1);
	}

	public void testla() {
		Calendar calendar = getCalendar(1262023200645L);
		assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 13);
		assertEquals(calendar.get(Calendar.MINUTE), 0);
		assertEquals(calendar.get(Calendar.SECOND), 0);
		assertEquals(calendar.get(Calendar.MILLISECOND), 645);
	}

	public void testlb() {
		Calendar calendar = getCalendar(1262023202646L);
		assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 13);
		assertEquals(calendar.get(Calendar.MINUTE), 0);
		assertEquals(calendar.get(Calendar.SECOND), 2);
		assertEquals(calendar.get(Calendar.MILLISECOND), 646);
	}

	public void testlc() {
		Calendar calendar = getCalendar(1262024101593L);
		assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 13);
		assertEquals(calendar.get(Calendar.MINUTE), 15);
		assertEquals(calendar.get(Calendar.SECOND), 1);
		assertEquals(calendar.get(Calendar.MILLISECOND), 593);
	}

	public void testld() {
		Calendar calendar = getCalendar(1262025000068L);
		assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 13);
		assertEquals(calendar.get(Calendar.MINUTE), 30);
		assertEquals(calendar.get(Calendar.SECOND), 0);
		assertEquals(calendar.get(Calendar.MILLISECOND), 68);
	}

	public void testle() {
		Calendar calendar = getCalendar(1262025002068L);
		assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 13);
		assertEquals(calendar.get(Calendar.MINUTE), 30);
		assertEquals(calendar.get(Calendar.SECOND), 2);
		assertEquals(calendar.get(Calendar.MILLISECOND), 68);
	}

	public void testlf() {
		Calendar calendar = getCalendar(1262025900724L);
		assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 13);
		assertEquals(calendar.get(Calendar.MINUTE), 45);
		assertEquals(calendar.get(Calendar.SECOND), 0);
		assertEquals(calendar.get(Calendar.MILLISECOND), 724);
	}

	public void testlg() {
		Calendar calendar = getCalendar(1262025902724L);
		assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 13);
		assertEquals(calendar.get(Calendar.MINUTE), 45);
		assertEquals(calendar.get(Calendar.SECOND), 2);
		assertEquals(calendar.get(Calendar.MILLISECOND), 724);
	}

	public void testlh() {
		Calendar calendar = getCalendar(1262026801380L);
		assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 14);
		assertEquals(calendar.get(Calendar.MINUTE), 0);
		assertEquals(calendar.get(Calendar.SECOND), 1);
		assertEquals(calendar.get(Calendar.MILLISECOND), 380);
	}

	public void testlj() {
		Calendar calendar = getCalendar(1262026800309L);
		assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 14);
		assertEquals(calendar.get(Calendar.MINUTE), 0);
		assertEquals(calendar.get(Calendar.SECOND), 0);
		assertEquals(calendar.get(Calendar.MILLISECOND), 309);
	}
}
// 1262026801989 14 hours
