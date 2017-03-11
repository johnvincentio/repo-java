package com.idc.c2;

import java.util.Timer;
import java.util.TimerTask;

public class App {
	private static final int UPDATE_INTERVAL = 2;  // seconds
	private Timer m_timer = new Timer();

	public static void main(String args[]) {(new App()).doApp();}
	private void doApp() {
		m_timer.schedule (new Task(), 1000, UPDATE_INTERVAL * 1000);
	}

	private class Task extends TimerTask {
		private Calculate m_calculate = new Calculate (UPDATE_INTERVAL);
		private boolean bProcessed = false;
		public void run() {
			long currentTime = System.currentTimeMillis();
			Result result = m_calculate.handle (currentTime);
			if (result.isNothing())
				bProcessed = false;
			else if (! bProcessed) {
				bProcessed = true;
				if (result.isHour()) showHours (currentTime, result.getHours());
				else if (result.isQuarterHour()) showQuarterHour (currentTime);
				else if (result.isHalfHour()) showHalfHour (currentTime);
			}
		}
	}
	private void showQuarterHour (long currentTime) {System.out.println(currentTime + " Quarter Hour");}
	private void showHalfHour (long currentTime) {System.out.println(currentTime + " Half Hour");}
	private void showHours (long currentTime, int hours) {System.out.println(currentTime + " " + hours + " hours");}
}
