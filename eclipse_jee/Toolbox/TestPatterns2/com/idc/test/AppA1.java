package com.idc.test;

import java.util.Iterator;
import java.util.Random;

import com.idc.pattern.patterns.KeyItemInfo;

public class AppA1 {
	public static void main (String[] args) {
		Random random = new Random (System.currentTimeMillis());
		(new AppA1()).doMercuryTest(random, 5);
	}
	private void doMercuryTest (Random random, int maxCount) {
		TestMercuryInfo info = new TestMercuryInfo(maxCount);
		for (int num = 0; num < maxCount; num++) {
			info.add (makeDddItemInfo (random, num));
		}
		System.out.println("size "+info.getSize()+" isNone "+info.isNone());
		
		Iterator<?> iter = info.getItems();
		while (iter.hasNext()) {
			DddItemInfo item = (DddItemInfo) iter.next();
			System.out.println("item :"+item+":");
		}
	}
	@SuppressWarnings("unused")
	private void doVenusTest (Random random, int maxCount) {
		TestVenusInfo info = new TestVenusInfo (maxCount);
		for (int num = 0; num < maxCount; num++) {
			info.add (makeDddItemInfo (random, num));
		}
		System.out.println("size "+info.getSize()+" isNone "+info.isNone());

		KeyItemInfo key = info.getKeyItemInfo ("a", 1, 2L);
		boolean bExists = info.isExists(key);
		System.out.println("bExists "+bExists);
		DddItemInfo item = (DddItemInfo) info.getItem(key);
		System.out.println("item "+item);
	}

	private DddItemInfo makeDddItemInfo (Random random, int num) {
		return new DddItemInfo (Long.toString (random.nextLong()), 
			random.nextInt(), random.nextLong(), 
			Integer.toString(num) + "aaa", Integer.toString(num) + "bbb");
	}
}
