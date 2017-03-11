package com.idc.a;

public class ClockReader {
	Clock clock = new Clock();
	public long readClock() {
		return clock.time;
	}
}
