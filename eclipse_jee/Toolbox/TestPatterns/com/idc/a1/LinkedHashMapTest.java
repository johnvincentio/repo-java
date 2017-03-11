package com.idc.a1;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.idc.a1.Player.Key;

public class LinkedHashMapTest {
	private static void fill (Map<Key, Player> players, SportDatabase sd) {
		Player[] p = sd.getPlayers();
		for (int i = 0; i < p.length; i++) {
			players.put(p[i].getKey(), p[i]);
		}
	}

	private static void test(Map<Key, Player> players, SportDatabase sd) {
		System.out.println("Testing " + players.getClass().getName());
		fill (players, sd);
		for (Iterator<Player> it = players.values().iterator(); it.hasNext();) {
			System.out.println(it.next());
		}
		System.out.println();
	}

	public static void main(String[] args) {
		SportDatabase sd = new CricketDatabase();
		test (new HashMap<Key, Player>(), sd);
		test (new LinkedHashMap<Key, Player>(), sd);
		test (new IdentityHashMap<Key, Player>(), sd);
	}
}
