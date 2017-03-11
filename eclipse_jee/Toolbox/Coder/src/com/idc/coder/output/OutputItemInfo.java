package com.idc.coder.output;

public class OutputItemInfo {
	private String output;
	public OutputItemInfo (String output) {
		this.output = output;
	}
	public String getOutput() {return output;}
	public String toString() {
		return "("+getOutput()+")";
	}
}
