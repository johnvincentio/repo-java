package com.idc.cuckoo;

import java.io.File;
import java.util.TimerTask;

public class Task extends TimerTask {
	private int m_ticks;
	private String strdir = "C:/jv/utils/Sounds/";

	public Task(int ticks) {m_ticks = ticks;}

	public void run() {
		if (m_ticks >= 24 * 4) 
			m_ticks = 0; // reset every 24 hours
		if (m_ticks % 4 == 0) 
			fullHour(m_ticks / 4);
		else if (m_ticks % 2 == 0)
			halfHour();
		else
			quarterHour();
		m_ticks++;
	}

	private void quarterHour() {
//		System.out.println("quarterHour");
		Sound.doSoundTest (new File(strdir + "cowbell.au"));
	}

	private void halfHour() {
//		System.out.println("halfHour");
		Sound.doSoundTest (new File(strdir + "cuckoo.au"));
	}

	private void fullHour(int hours) {
//		System.out.println("fullHour " + hours);
		for (int num = 0; num < hours; num++)
			Sound.doSoundTest (new File(strdir + "cuckoo.au"));
	}
}
