package com.idc.p6;

public class Speaker {
	private VirtualTime vt;

	public Speaker(VirtualTime vt) {
		this.vt = vt;
	}

	public void say(String s) {
		System.out.println(vt + " " + s);
		vt.nanosleep(50000000 * s.length());
	}
}
