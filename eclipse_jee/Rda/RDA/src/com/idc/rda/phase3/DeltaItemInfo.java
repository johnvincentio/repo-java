package com.idc.rda.phase3;

import java.io.Serializable;

import com.idc.rda.Utils;

public class DeltaItemInfo implements Serializable {
	private static final long serialVersionUID = -3886650107569110453L;

	private int sheetNumber;
	private String[] data;

	public DeltaItemInfo (int sheetNumber, String[] data) {
		this.sheetNumber = sheetNumber;
		this.data = data;
	}

	public int getSheetNumber() {return sheetNumber;}
	public String[] getData() {return data;}

	public String toString() {
		return "("+getSheetNumber()+","+Utils.traceStringArray (getData())+")";
	}
}

