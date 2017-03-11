
package com.idc.countdown;

import java.util.Iterator;

public class App {
	public static void main (String[] args) {
		(new App()).doApp();
	}
	private void doApp() {
		System.out.println("Test");
		Numbers numbers = new Numbers();
		numbers.set(75); numbers.set(100); numbers.set(6);
		numbers.set(3); numbers.set(4); numbers.set(5);
		numbers.setTarget(917);
		Answers all = Utils.doCalculate (numbers);
		System.out.println("Numbers: "+numbers.show());
		Answer answer;
		Iterator iter = all.getAnswers();
		while (iter.hasNext()) {
			answer = (Answer) iter.next();
			System.out.println("Closest is: "+Utils.show (answer));
		}
		System.out.println("End of test");
	}

}

