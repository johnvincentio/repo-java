package com.idc.file.dump;

public class HexDump extends GenericDump {
	public HexDump (String s, int n) {super(s,n);}
	protected String getValue(byte data) {return Integer.toString(data,16);}
	protected void startMsg(String s) {System.out.println("Hex Dump of file "+s);}
	protected void endMsg(String s) {System.out.println("Hex Dump Complete");}
	protected void write(String s) {System.out.print(":"+s);}
	protected void newline() {System.out.println();}
}

