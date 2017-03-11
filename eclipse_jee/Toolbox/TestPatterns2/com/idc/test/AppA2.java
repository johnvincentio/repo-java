package com.idc.test;

import java.util.Iterator;
import java.util.Random;

import com.idc.pattern.patterns.KeyItemInfo;

public class AppA2 {
	public static void main (String[] args) {
		Random random = new Random (System.currentTimeMillis());
//		(new AppA2()).doMercuryTest(random, 5);
//		(new AppA2()).doVenusTest(random, 5);
//		(new AppA2()).doMarsTest(random, 5);
		(new AppA2()).doJupiterTest(random, 5);
	}
	@SuppressWarnings("unused")
	private void doMercuryTest (Random random, int maxCount) {
		TestMercuryInfo info = new TestMercuryInfo(maxCount);
		System.out.println(info);
		for (int num = 0; num < maxCount; num++) {
			info.add (new DddItemInfo (Integer.toString(num), num, num, "aaa" + num, "bbb" + num));
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
			info.add (new DddItemInfo (Integer.toString(num), num, num, "aaa" + num, "bbb" + num));
		}
		System.out.println("size "+info.getSize()+" isNone "+info.isNone());
		System.out.println("info "+info.toString());

		KeyItemInfo key = info.getKeyItemInfo ("2", 2, 2);
		boolean bExists = info.isExists(key);
		System.out.println("bExists "+bExists);
		DddItemInfo item = (DddItemInfo) info.getItem(key);
		System.out.println("item "+item);
	}

	@SuppressWarnings("unused")
	private void doMarsTest (Random random, int maxCount) {
		TestMarsInfo info = new TestMarsInfo (maxCount);
		for (int num = maxCount; num > -1; num--) {
			info.add (new DddItemInfo (Integer.toString(num), num, num, "aaa" + num, "bbb" + num));
		}
		System.out.println("size "+info.getSize()+" isNone "+info.isNone());
		System.out.println("info "+info.toString());

		Iterator iter = info.getItems();
		while (iter.hasNext()) {
			DddItemInfo item = (DddItemInfo) iter.next();
			System.out.println("item :"+item+":");
		}

		KeyItemInfo key = info.getKeyItemInfo ("2", 2, 2);
		boolean bExists = info.isExists(key);
		System.out.println("bExists "+bExists);
		DddItemInfo item = (DddItemInfo) info.getItem(key);
		System.out.println("item "+item);
	}

	@SuppressWarnings("unused")
	private void doJupiterTest (Random random, int maxCount) {
		System.out.println(">>> doJupiterTest");
		TestJupiterInfo info = new TestJupiterInfo (maxCount);
		for (int num = maxCount; num > -1; num--) {
			info.add (new DddHashItemInfo (Integer.toString(num), num, num, "aaa" + num, "bbb" + num));
		}
		System.out.println("size "+info.getSize()+" isNone "+info.isNone());
		System.out.println("info "+info.toString());

		Iterator iter = info.getItems();
		while (iter.hasNext()) {
			DddHashItemInfo item = (DddHashItemInfo) iter.next();
			System.out.println("item :"+item+":");
		}

		DddHashItemInfo key = new DddHashItemInfo (Integer.toString(2), 2, 2, "aaa" + 2, "bbb" + 2);
		boolean bExists = info.isExists(key);
		System.out.println("bExists "+bExists);
		System.out.println("<<< doJupiterTest");
	}

	@SuppressWarnings("unused")
	private DddItemInfo makeDddItemInfo (Random random, int num) {
		return new DddItemInfo (Long.toString (random.nextLong()), 
			random.nextInt(), random.nextLong(), 
			Integer.toString(num) + "aaa", Integer.toString(num) + "bbb");
	}
}
