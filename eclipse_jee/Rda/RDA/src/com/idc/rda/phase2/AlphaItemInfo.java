package com.idc.rda.phase2;

import java.io.Serializable;
import java.util.HashMap;

import com.idc.rda.Utils;

public class AlphaItemInfo implements Serializable {
	private static final long serialVersionUID = -7832875588790709859L;

	private int sheetNumber;
	private HashMap<String, String[]> data;

	public AlphaItemInfo (int sheetNumber, HashMap<String, String[]> data) {
		this.sheetNumber = sheetNumber;
		this.data = data;
	}

	public int getSheetNumber() {return sheetNumber;}
	public HashMap<String, String[]> getData() {return data;}

	public String toString() {
		return "(AlphaItemInfo "+getSheetNumber()+","+Utils.traceMap (getData())+")\n";
	}
}

