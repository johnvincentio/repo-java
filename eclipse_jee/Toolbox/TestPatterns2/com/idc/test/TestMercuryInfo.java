package com.idc.test;

import java.io.Serializable;

import com.idc.pattern.patterns.MercuryInfo;

public class TestMercuryInfo extends MercuryInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public TestMercuryInfo () {super();}

	public TestMercuryInfo (int capacity) {super (capacity);}

	public void add (DddItemInfo item) {super.add (item);}
}
