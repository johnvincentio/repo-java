package com.idc.test.a;

import java.util.HashMap;

public class Ccc {
	public static void main(String[] args) {
		HashMap<CccKey, String> map = new HashMap<CccKey, String>();
		map.put(new CccKey(2), "two");
		map.put(new CccKey(4), "four");
		System.out.println(map);
		System.out.println();

		System.out.println("Iterate the values of the HashMap");
		for (String s : map.values()) {
			System.out.println("String is :"+s+":");
		}
		System.out.println();

		System.out.println("Iterate through the keys of the HashMap");
		for (CccKey cccKey : map.keySet()) {
			System.out.println("Key :"+cccKey+":");
		}
		System.out.println();

		System.out.println("Demonstrate access");
		System.out.println("map.get( 2 ) = " + map.get(new CccKey(2)));
		System.out.println("map.get( 5 ) = " + map.get(new CccKey(5)));
		System.out.println("map = " + map);
		System.out.println();

		System.out.println("map.exists(2) "+map.containsKey(new CccKey(2)));
	}
}
