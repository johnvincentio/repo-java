package com.idc.sudoku;

public class Bucket {
	public static final int BUCKETS = 9;
	private int m_buckets[] = new int[BUCKETS];
	public Bucket () {}
	public Bucket (Bucket bt) {set(bt);}
	public int get (int row) {return m_buckets[row];}
	public void set (int row, int value) {m_buckets[row] = value;}
	public void set (Bucket bt) {for (int pos=0; pos<BUCKETS; pos++) m_buckets[pos] = bt.get(pos);}
	public void set (int i1, int i2, int i3, int i4, int i5, int i6,
			int i7, int i8, int i9) {
		m_buckets[0] = i1; m_buckets[1] = i2; m_buckets[2] = i3;
		m_buckets[3] = i4; m_buckets[4] = i5; m_buckets[5] = i6;
		m_buckets[6] = i7; m_buckets[7] = i8; m_buckets[8] = i9;
	}
	public void reset() {for (int pos=0; pos<BUCKETS; pos++) m_buckets[pos] = 0;}

	public String getStringAllBuckets() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<BUCKETS; i++) {
			buf.append(Integer.toString(m_buckets[i]));
		}
		return buf.toString();
	}
	public void show() {
		System.out.println("show bucket");
		for (int i=0; i<BUCKETS; i++) {
			System.out.print(m_buckets[i]+" ");
		}
		System.out.println("");
		System.out.println("end of show bucket");
	}

	public void add (int row, int value) {m_buckets[row] = value;}
	public boolean set (long lValue) {
		long ldiv, ltmp;
		int row;
		for (int pos=BUCKETS-1; pos >= 0; pos--) {
//			System.out.println("lValue "+lValue);
			ldiv = getDiv(pos+1);
			ltmp = lValue / ldiv;
			row = BUCKETS - pos - 1;	
//			System.out.println("row "+row+" ltmp "+ltmp);
			if (ltmp < 1 || ltmp > BUCKETS) return false;
			if (row < 0 || row >= BUCKETS) return false;
			m_buckets[row] = (int) ltmp;
			lValue -= ltmp * ldiv;
//System.out.println("row "+row+" pos "+pos+" ldiv "+ldiv+" ltmp "+ltmp+" lValue "+lValue);
		}
//		System.out.println("bucket is OK");
		return true;
	}
	public long getLongValue() {
		long lvalue = 0;
		long mult = 1;
		for (int i=0; i<BUCKETS; i++) {
			lvalue += m_buckets[i] * mult;
			mult *= 10;
		}
		return lvalue;
	}

	public static long getMin() {return getSomeValue (1);}
	public static long getMax() {return getSomeValue (BUCKETS);}
	private static long getSomeValue(int value) {
		long lvalue = 0;
		long mult = 1;
		for (int row = 0; row < BUCKETS; row++) {
			lvalue += value * mult;
			mult *= 10;
		}
		return lvalue;
	}
	private static long getDiv(int row) {
		long mult = 1;
		for (int i=0; i<row-1; i++) {
			mult *= 10;
		}
		return mult;
	}
}

