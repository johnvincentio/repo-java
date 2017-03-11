package com.idc.jasper;

import java.text.DateFormat;
import java.util.Date;

public class Abc {
	public static void main (String[] args) {
		(new Abc()).doTest();
	}
	public void doTest() {
		Date jdate = new Date();
		String s1 = DateFormat.getDateInstance().format(jdate);
		System.out.println("s1 "+s1);
	}
}
//Jan 19, 2009

