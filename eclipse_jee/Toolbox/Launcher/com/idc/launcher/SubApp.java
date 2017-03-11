package com.idc.launcher;

import java.util.Iterator;
import java.util.Map;

public class SubApp {
	public static void main (String[] args) {
		(new SubApp()).doTest();
	}
	private void doTest() {
		System.out.println(">>> SubApp::doTest");
		Map<String, String> map = System.getenv();
		Iterator<Map.Entry<String, String>> keyValuePairs1 = map.entrySet().iterator();
		System.out.println("***********************************");
		for (int i = 0; i < map.size(); i++) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) keyValuePairs1.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			System.out.println("key :" + key + ": value :" + value + ":");
		}
		System.out.println("<<< SubApp::doTest");
	}
}
