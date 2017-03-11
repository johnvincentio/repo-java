
package com.idc.sudoku;

public class Utils {
	private static int[] m_iaWork = new int[Bucket.BUCKETS];
	private static void iaReset() {
		for (int pos=0; pos<Bucket.BUCKETS; pos++) m_iaWork[pos] = 0;
	}
	private static Bucket m_workBucket = new Bucket();

	public static Patterns makePatterns() {
		Patterns patterns = new Patterns();
		Bucket bucket = new Bucket();
		for (int i1=1; i1<10; i1++) {
			for (int i2=1; i2<10; i2++) {
				for (int i3=1; i3<10; i3++) {
					for (int i4=1; i4<10; i4++) {
						for (int i5=1; i5<10; i5++) {
							for (int i6=1; i6<10; i6++) {
								for (int i7=1; i7<10; i7++) {
									for (int i8=1; i8<10; i8++) {
										for (int i9=1; i9<10; i9++) {
				bucket.reset();
				bucket.set (i1,i2,i3,i4,i5,i6,i7,i8,i9);
				if (isPattern(bucket)) {
//					bucket.show();
					patterns.add(new Pattern(bucket));
				}
		} } } } } } } } }
		return patterns;
	}

	public static boolean isPattern (Bucket bucket) {
//		System.out.println(">>> isPattern");
		iaReset();
		boolean bResult = true;
		for (int i=0; i<Bucket.BUCKETS; i++) {
			m_iaWork[bucket.get(i) - 1]++;
		}
		for (int pos=0; pos<Bucket.BUCKETS; pos++) {
			if (m_iaWork[pos] != 1) {
				bResult = false;
				break;
			}
		}
//		System.out.println("<<< isPattern; result "+bResult);
		return bResult;
	}

	public static boolean isPossible (Bucket bucket) {
//		System.out.println(">>> isPossible");
		int value;
		iaReset();
		boolean bResult = true;
		for (int i=0; i<Bucket.BUCKETS; i++) {
			value = bucket.get(i);
			if (value > 0) m_iaWork[value - 1]++;
		}
		for (int pos=0; pos<Bucket.BUCKETS; pos++) {
			if (m_iaWork[pos] > 1) {
				bResult = false;
				break;
			}
		}
//		System.out.println("<<< isPossible; result "+bResult);
		return bResult;
	}
//
//	 check if violates the row itself
//
	public static boolean isPatternPossible (int row, Pattern pattern, Board currentBoard) {
		int bInt, pInt;
		for (int x=0; x<Bucket.BUCKETS; x++) {
			bInt = currentBoard.get(x,row);
			pInt = pattern.get(x);
			if (bInt > 0 && bInt != pInt) {
//				System.out.println("<<< fails at check for the row");
				return false;
			}
		}
		return true;
	}

	public static boolean isSituationPossible (int row, Pattern pattern, Board currentBoard, 
			Board nextBoard) {
//		System.out.println(">>> isSituationPossible");
//		pattern.show();
//		currentBoard.show(); nextBoard.show();
//
// check if violates the row itself
//
//		System.out.println("starting check for the row");
		int bInt, pInt;
		for (int x=0; x<Bucket.BUCKETS; x++) {
			bInt = currentBoard.get(x,row);
			pInt = pattern.get(x);
			if (bInt > 0 && bInt != pInt) {
//				System.out.println("<<< fails at check for the row");
				return false;
			}
		}
//
// check if violates any column
//
//		System.out.println("starting check for the column");
		for (int x=0; x<Bucket.BUCKETS; x++) {
			m_workBucket.reset();
			for (int y=0; y<Bucket.BUCKETS; y++) {
				bInt = nextBoard.get(x,y);
//				System.out.println("bInt "+bInt);
				m_workBucket.set (y, bInt);
			}
			if (! isPossible(m_workBucket)) {
//				System.out.println("<<< fails at check for the column");
				return false;
			}
		}
//
// check if violates local square rule
//
//		System.out.println("starting check local square rule");
		int cntr = 0;
		int nChecks = Bucket.BUCKETS / 3;
		for (int iNum=0; iNum<nChecks; iNum++) {
			for (int jNum=0; jNum<nChecks; jNum++) {
				m_workBucket.reset();
				cntr = 0;
				for (int x=iNum*3; x<(iNum+1)*3; x++) {
					for (int y=jNum*3; y<(jNum+1)*3; y++) {
						bInt = nextBoard.get(x,y);
//						System.out.println("x,y "+x+","+y+" bInt "+bInt);
						m_workBucket.set (cntr++, bInt);
					}
				}
//				m_workBucket.show();
				if (! isPossible(m_workBucket)) {
//					System.out.println("<<< fails at check local square rule");
					return false;
				}
			}
		}
//		System.out.println("<<< isSituationPossible; OK");
		return true;
	}
	public static boolean isThisSituationPossible (Board currentBoard) {
//		System.out.println(">>> isThisSituationPossible");
//		pattern.show();
//		currentBoard.show();
//
// check if violates any row
//
//		System.out.println("starting check for the row");
		int bInt;
		for (int y=0; y<Bucket.BUCKETS; y++) {
			m_workBucket.reset();
			for (int x=0; x<Bucket.BUCKETS; x++) {
				bInt = currentBoard.get(x,y);
				m_workBucket.set (x, bInt);
			}
			if (! isPossible(m_workBucket)) {
//				System.out.println("<<< fails at check for the row");
				return false;
			}
		}
//
// check if violates any column
//
//		System.out.println("starting check for the column");
		for (int x=0; x<Bucket.BUCKETS; x++) {
			m_workBucket.reset();
			for (int y=0; y<Bucket.BUCKETS; y++) {
				bInt = currentBoard.get(x,y);
//				System.out.println("bInt "+bInt);
				m_workBucket.set (y, bInt);
			}
			if (! isPossible(m_workBucket)) {
//				System.out.println("<<< fails at check for the column");
				return false;
			}
		}
//
// check if violates local square rule
//
//		System.out.println("starting check local square rule");
		int cntr = 0;
		int nChecks = Bucket.BUCKETS / 3;
		for (int iNum=0; iNum<nChecks; iNum++) {
			for (int jNum=0; jNum<nChecks; jNum++) {
				m_workBucket.reset();
				cntr = 0;
				for (int x=iNum*3; x<(iNum+1)*3; x++) {
					for (int y=jNum*3; y<(jNum+1)*3; y++) {
						bInt = currentBoard.get(x,y);
//						System.out.println("x,y "+x+","+y+" bInt "+bInt);
						m_workBucket.set (cntr++, bInt);
					}
				}
//				m_workBucket.show();
				if (! isPossible(m_workBucket)) {
//					System.out.println("<<< fails at check local square rule");
					return false;
				}
			}
		}
//		System.out.println("<<< isThisSituationPossible; OK");
		return true;
	}
	public static void doExit() {
		System.out.println("You told me to exit....");
		System.exit(1);
	}
}

