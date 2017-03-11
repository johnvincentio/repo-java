package com.idc.j4;

import java.io.File;
import java.util.TimerTask;

class Task extends TimerTask {
	private int m_ticks;

	public Task (int ticks) {m_ticks = ticks;}

	public void run() {
		if (m_ticks >= 24 * 4) m_ticks = 0;		// reset every 24 hours
		if (m_ticks > 64 && m_ticks < 68)
			homeTime();
		else if (m_ticks % 4 == 0) {
			int hour = m_ticks / 4;
			if (hour > 12) hour -= 12;
			fullHour (hour);
		}
		else if (m_ticks % 2 == 0)
			halfHour();
		else
			quarterHour();
		m_ticks++;
	}
	private void quarterHour() {
//		System.out.println("quarterHour");
		Sound.doSoundTest (new File("com/idc/j4/bird.au"));
	}
	private void halfHour() {
//		System.out.println("halfHour");
		Sound.doSoundTest (new File("com/idc/j4/rooster.au"));
	}
	private void fullHour(int hours) {
//		System.out.println("fullHour "+hours);
		for (int num = 0; num < hours; num++)
			Sound.doSoundTest (new File("com/idc/j4/cuckoo.au"));
	}
	private void homeTime() {
		Sound.doSoundTest (new File("com/idc/j4/cheering.au"));
	}
}
