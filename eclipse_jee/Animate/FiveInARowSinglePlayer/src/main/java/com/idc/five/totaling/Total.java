package com.idc.five.totaling;

public class Total {
	private int total = 0;
	private int[] counts = new int[5];
	
	void incrementTotal (int count) {total += count;}
	void increment (int level) {
		assert level > 0 && level <= 5;
		counts[level - 1]++;
	}
	int getTotal() {return total;}
	String getTotals() {
		StringBuffer buf = new StringBuffer();
		buf.append ("[");
		for (int i = 0; i < 5; i++) {
			if (i > 0) buf.append (",");
			buf.append (counts[i]);
		}
		buf.append ("]");
		return buf.toString();
	}
}
