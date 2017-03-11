package com.idc.p6;

public class VirtualTime {
	public static final int SPEEDUP_FACTOR = 1;

	long start = System.currentTimeMillis();

	public void reset() {
		start = System.currentTimeMillis();
	}

	public void nanosleep(long nanos) {
		long millis = nanos / 1000000 / SPEEDUP_FACTOR;
		nanos = nanos / SPEEDUP_FACTOR - millis * 1000000;
		try {
			Thread.sleep(millis, (int) nanos);
		} catch (InterruptedException e) {
			System.out.println("-- sleep() interrupted!");
		}
	}

	public long curvtime() {
		return (System.currentTimeMillis() - start) * SPEEDUP_FACTOR;
	}

	public String toString() {
		return String.valueOf(curvtime());
	}
}
