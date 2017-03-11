package com.idc.test;

import java.io.Serializable;

import com.idc.pattern.patterns.JupiterInfo;

public class TestJupiterInfo extends JupiterInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public TestJupiterInfo (int capacity) {super (capacity);}

	public void add (Object item) {super.add (item);}
}
