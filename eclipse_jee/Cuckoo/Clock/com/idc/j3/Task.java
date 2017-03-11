package com.idc.j3;

import java.util.TimerTask;

class Task extends TimerTask {
	private int m_ticks;

	public Task (int ticks) {m_ticks = ticks;}

	public void run() {
		if (m_ticks >= 24 * 4) m_ticks = 0;		// reset every 24 hours
		if (m_ticks % 4 == 0)
			fullHour (m_ticks / 4);
		else if (m_ticks % 2 == 0)
			halfHour();
		else
			quarterHour();
		m_ticks++;
	}
	private void quarterHour() {
		System.out.println("quarterHour");
	}
	private void halfHour() {
		System.out.println("halfHour");
	}
	private void fullHour(int hours) {
		System.out.println("fullHour "+hours);
	}
}
