package com.idc.teehee;

import java.io.File;

import junit.framework.TestCase;

public class Test2 extends TestCase {

	public void test1a() {
		String file = "C:/jv/utils/Sounds/cuckoo.au";
		Sound.doSound (new File (file));
	}
	public void test1b() {
		String file = "C:/jv/utils/Sounds/cowbell.au";
		Sound.doSound (new File (file));
	}
	public void JVtest1c() {
		String file = "C:/jvDownloads/Developer/Sounds/cuckoo_clock1_x.wav";
		Sound.doSound (new File (file));
	}

	public void test2a() {
		String file = "C:/jvDownloads/Developer/Sounds/cuckoo_clock1_x.wav";
		Sound.doAudioPlayerTest (file);
	}

	public void testm() {
		double count = 1;
		for (int i = 1; i <=80; i++) {
			count = count * 0.5;
		}
		System.out.println("count "+count);
	}
}
