package com.idc.app;

public class MyThread extends Thread {
	public void run() {
		System.out.println(">>> run");
//		App.mySleep (60*1000);				// for testing purposes only
		ReloadDataHelper.doLogin();
		System.out.println("<<< run");
	}
}
