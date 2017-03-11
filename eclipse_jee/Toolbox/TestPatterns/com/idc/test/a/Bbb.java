package com.idc.test.a;

import java.util.HashMap;

public class Bbb {
	public static void main(String[] args) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put (new Integer(2), "two");
		map.put (new Integer(4), "four");
		System.out.println(map);
		System.out.println();

		System.out.println("Iterate the values of the HashMap");
		for (String s : map.values()) {
			System.out.println("String is :"+s+":");
		}
		System.out.println();

		System.out.println("Iterate through the keys of the HashMap");
		for (Integer num : map.keySet()) {
			System.out.println("Key :"+num+":");
		}
		System.out.println();

		System.out.println("Demonstrate access");
		System.out.println("map.get( 2 ) = " + map.get(new Integer(2)));
		System.out.println("map.get( 5 ) = " + map.get(new Integer(5)));
		System.out.println("map = " + map);
		System.out.println();

		System.out.println("map.exists(2) "+map.containsKey(new Integer(2)));
	}
}
