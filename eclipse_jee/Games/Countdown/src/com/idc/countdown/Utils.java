
package com.idc.countdown;

public class Utils {
	private Utils() {}
	public static Answers doCalculate(Numbers numbers) {
		Answers all = new Answers();
		Answer answer = new Answer();
		Answer runner = new Answer();
		boolean bFirst = true;
		boolean bExact = false;

		for (int d1=0; d1<Constants.MAX_NUMBERS; d1++) {
			runner.set(0, numbers.get(d1));
			for (int o1=0; o1<4; o1++) {
				runner.set(1, o1);
				for (int d2=0; d2<Constants.MAX_NUMBERS; d2++) {
					runner.set(2, numbers.get(d2));
					if (d2 == d1) continue;
					for (int o2=0; o2<4; o2++) {
						runner.set(3, o2);
						for (int d3=0; d3<Constants.MAX_NUMBERS; d3++) {
							runner.set(4, numbers.get(d3));
							if (d3 == d2 || d3 == d1) continue;
							for (int o3=0; o3<4; o3++) {
								runner.set(5, o3);
								for (int d4=0; d4<Constants.MAX_NUMBERS; d4++) {
									runner.set(6, numbers.get(d4));
									if (d4 == d3 || d4 == d2 || d4 == d1) continue;
									for (int o4=0; o4<4; o4++) {
										runner.set(7, o4);
										for (int d5=0; d5<Constants.MAX_NUMBERS; d5++) {
											runner.set(8, numbers.get(d5));
											if (d5 == d4 || d5 == d3 || d5 == d2 || d5 == d1) continue;
											for (int o5=0; o5<4; o5++) {
												runner.set(9, o5);
												for (int d6=0; d6<Constants.MAX_NUMBERS; d6++) {
													runner.set(10, numbers.get(d6));
													if (d6 == d5 || d6 == d4 || d6 == d3 || d6 == d2 || d6 == d1) continue;
														runner.setTotal(Utils.calculate (runner));
//														show (runner);
														if (numbers.getTarget() == runner.getTotal()) {
															bExact = true;
															answer = new Answer();
															answer.set(runner);
															all.add(answer);
														}
														if (! bExact) {
															if (bFirst || isCloser (answer, numbers.getTarget(), runner)) {
																bFirst = false;
																answer.set(runner);
//																System.out.println("closer"+show (answer));
															}
														}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if (! bExact) all.add (answer);
		return all;
	}
	public static String show (Answer runner) {return runner.show();}
	public static boolean isCloser (Answer answer, int nTarget, Answer runner) {
//		System.out.println(">>> isCloser; target "+nTarget+" total "+runner.getTotal()+" answer:total "+answer.getTotal());
		int n1 = nTarget - runner.getTotal();
		int n2 = nTarget - answer.getTotal();
//		System.out.println("(1) n1 "+n1+" n2 "+n2);
		if (n1 < 0) n1 = -n1;
		if (n2 < 0) n2 = -n2;
//		System.out.println("(2) n1 "+n1+" n2 "+n2);
		if (n1 < n2) return true;
		return false;
	}
	public static int calculate (Answer runner) {
		int nTotal = 0;
		nTotal = useCalculator (runner.get(0), runner.get(1), runner.get(2));
		nTotal = useCalculator (nTotal, runner.get(3), runner.get(4));
		nTotal = useCalculator (nTotal, runner.get(5), runner.get(6));
		nTotal = useCalculator (nTotal, runner.get(7), runner.get(8));
		nTotal = useCalculator (nTotal, runner.get(9), runner.get(10));
		return nTotal;
	}
	private static int useCalculator (int n1, int oper, int n2) {
		switch(oper) {
		case 3:
			return n1 / n2;
		case 2:
			return n1 * n2;
		case 1:
			return n1 - n2;
		case 0:
		default:
			return n1 + n2;
		}
	}
	public static String getOperator(int num) {
		switch(num) {
		case 3:
			return "/";
		case 2:
			return "*";
		case 1:
			return "-";
		case 0:
		default:
			return "+";
		}
	}
	public static int makeInt (String str) {return Integer.parseInt(str);}
	public static int makePositive (int num) {if (num < 0) return -num; return num;}
}
