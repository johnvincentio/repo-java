package com.idc.rda.phase1.items;

import com.idc.rda.Utils;

public class AlphaItemInfo {
	private String[] strArray;
	public AlphaItemInfo (String[] strArray) {
//		System.out.println(">>> AlphaItemInfo(constructor); strArray "+Utils.traceStringArray (strArray));
		this.strArray = strArray;
//		System.out.println("<<< AlphaItemInfo(constructor)");
	}

	public String[] getData() {return strArray;}

	public String toString() {
		return "("+Utils.traceStringArray (getData())+")";
	}
}
