package com.idc.file.dump;

public class ByteDump extends GenericDump {
	public ByteDump (String s, int n) {super(s,n);}
	protected String getValue(byte data) {return Integer.toString(data);}
	protected void startMsg(String s) {System.out.println("Byte Dump of file "+s);}
	protected void endMsg(String s) {System.out.println("Byte Dump Complete");}
	protected void write(String s) {System.out.print(":"+s);}
	protected void newline() {System.out.println();}
}

