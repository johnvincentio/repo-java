package com.idc.sudoku;

public class Pattern {
	private Bucket m_bucket;
	public Pattern (Bucket bucket) {
		m_bucket = new Bucket(bucket);
	}
	public int get (int x) {return m_bucket.get(x);}
	public void show() {m_bucket.show();}
	public String toString() {return m_bucket.getStringAllBuckets();}
}

