package com.idc.pattern.b;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

public class App1 {
	public static void main (String[] args) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		
		treeMap.put ("2", "two");			// sorts to natural order
		treeMap.put ("3", "three");
		treeMap.put ("1", "one");

		Collection<String> c = treeMap.values();
		
		Iterator<String> iter = c.iterator();
		while (iter.hasNext())
			System.out.println(iter.next());

		String value = treeMap.get("2");
		System.out.println("value :"+value+":");

		Iterator<String> iter2 = treeMap.values().iterator();
		while (iter2.hasNext())
			System.out.println(iter2.next());

		boolean b = treeMap.containsKey("3");
		System.out.println("b "+b);
		b = treeMap.containsKey("35");
		System.out.println("b "+b);
	}
}
