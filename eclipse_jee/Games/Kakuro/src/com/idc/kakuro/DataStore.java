package com.idc.kakuro;

import java.util.HashMap;

public class DataStore {
	private HashMap<MyKey, Answers> data = new HashMap<MyKey, Answers>();

	public Answers getAnswers (int squares, int total) {
//		System.out.println(">>> DataStore::getAnswers; total "+total+" squares "+squares);
		Answers answers = data.get (new MyKey (squares, total));
//		System.out.println("<<< DataStore::getAnswers; answers :"+answers);
		return answers;
	}

	public void addAnswers (int squares, int total, Answers answers) {
//		System.out.println(">>> DataStore::addAnswers; total "+total+" squares "+squares);
		MyKey key = new MyKey (squares, total);
		if (data.containsKey(key)) return;
		data.put(key, answers);
//		System.out.println("count is "+data.size());
//		System.out.println("<<< DataStore::addAnswers");
	}
}
