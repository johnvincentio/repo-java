package com.idc.b1;

import com.idc.b.Clock;

public class SubClock extends Clock {
	public SubClock() {}

	public long readClock() {
		return time;			// legal as time is protected.
	}
	
}
