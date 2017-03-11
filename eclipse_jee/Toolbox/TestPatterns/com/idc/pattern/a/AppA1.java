package com.idc.pattern.a;

import java.util.Iterator;
import java.util.Random;

public class AppA1 {
	public static void main (String[] args) {
		(new AppA1()).doTest();
	}
	private void doTest() {
		DddInfo dddInfo = makeDddInfo (5);
		System.out.println("size "+dddInfo.getSize());
		Iterator<DddItemInfo> iter = dddInfo.getItemsUnsorted();
		while (iter.hasNext()) {
			DddItemInfo item = (DddItemInfo) iter.next();
			System.out.println("item :"+item+":");
		}
		System.out.println("\n\nsorted");
		iter = dddInfo.getItems();
		while (iter.hasNext()) {
			DddItemInfo item = (DddItemInfo) iter.next();
			System.out.println("item :"+item+":");
		}
	}
	private DddInfo makeDddInfo (int maxcount) {
		Random random = new Random (System.currentTimeMillis());
		DddInfo dddInfo = new DddInfo();
		for (int num = 0; num < maxcount; num++) {
			dddInfo.add (new DddItemInfo (Long.toString (random.nextLong()), 
					random.nextInt(), random.nextLong(), Integer.toString(num) + "aaa", Integer.toString(num) + "bbb"));
		}
		return dddInfo;
	}
}
