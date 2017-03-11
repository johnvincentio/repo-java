package com.idc.pattern;

public class App {
	public static void main (String[] args) {
		(new App()).doTest();
	}
	private void doTest() {
		makeAbcInfo (1);
	}
	private AbcInfo makeAbcInfo (int maxcount) {
		AbcInfo abcInfo = new AbcInfo();
		for (int num = 0; num < maxcount; num++) {
			long millis = System.currentTimeMillis();
			int mills = (int) (millis / 10000);
			abcInfo.add (new AbcItemInfo (Long.toString(millis), mills, millis));
		}
		return abcInfo;
	}
}
