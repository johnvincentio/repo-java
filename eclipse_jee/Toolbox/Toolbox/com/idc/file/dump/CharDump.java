package com.idc.file.dump;

public class CharDump extends GenericDump {
	public CharDump (String s, int n) {super(s,n);}
	protected String getValue(byte data) {return String.valueOf((char) data);}
	protected void startMsg(String s) {System.out.println("Char Dump of file "+s);}
	protected void endMsg(String s) {System.out.println("Char Dump Complete");}
	protected void write(String s) {System.out.print(":"+s);}
	protected void newline() {System.out.println();}
}

