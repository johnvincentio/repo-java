package com.idc.app;

import com.idc.http.HttpMessage;

public class App {
	public static void main (String[] args) {
		(new App()).doTest();
	}
	private void doTest() {
		System.out.println(">>> doTest");
		try {
			HttpMessage.doSetupHttps();
		}
		catch (Exception ex) {
			System.err.println ("Unable to setupHttps; "+ex.getMessage());
		}
		for (int counter = 1; ; counter++) {
			if (counter > 1) mySleep (5000);
			System.out.println("thread #"+counter+" starting");
			MyThread myThread = new MyThread();
			myThread.start();
			for (int waiting = 1; ; waiting++) {
				mySleep (5000);
				if (! myThread.isAlive()) {
					myThread = null;
					break;
				}
				if (waiting > 4) {
					System.err.println("Thread #"+counter+" may be hanging");
					mySound();
				}
				else
					System.out.println("thread #"+counter+" is alive");

			}
			System.out.println("thread #"+counter+" completed");
		}
	}
	public static void mySound() {
		for (int i = 0; i < 5; i++) {
			java.awt.Toolkit.getDefaultToolkit().beep();
			mySleep (200);
		}
	}
	public static void mySleep (int millis) {
		try {
			Thread.sleep (millis);
		}
		catch (Exception ex) {
			System.err.println("sleep exception in mySleep; "+ex.getMessage());
		}
	}
}
