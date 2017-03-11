package com.idc.test.a;

import java.util.HashMap;

public class Aaa {
	public static void main(String[] args) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(new Integer(2), "two");
		map.put(new Integer(4), "four");
		System.out.println(map);
		System.out.println();

		System.out.println("Enumerate the HashMap");
		for (String s : map.values()) {
			System.out.println("String is :"+s+":");
		}
		System.out.println();

		System.out.println("Iterate through the HashMap");
		for (Integer num : map.keySet()) {
			System.out.println("Key :"+num+":");
		}
		System.out.println();

		System.out.println("Demonstrate access");
		System.out.println("map.get( 2 ) = " + map.get(new Integer(2)));
		System.out.println("map.get( 5 ) = " + map.get(new Integer(5)));
		System.out.println("map = " + map);
		System.out.println();

		System.out.println("Show that duplicates cannot be added.");
		Object value = map.put (new Integer(8), "eight");
		if (value != null)
			System.out.println("Could not add 8.");
		else
			System.out.println("Added 8.");
		System.out.println("map = " + map);

		value = map.put(new Integer(4), "FOUR");
		if (value != null)
			System.out.println("Could not add 4.");
		else
			System.out.println("Added 4.");
		System.out.println("map = " + map);
		System.out.println();

		System.out.println("Demonstrate modification");
		map.put(new Integer(4), "FOUR");
		System.out.println("map = " + map);
	}
}
