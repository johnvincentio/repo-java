package com.idc.file.dump;

public class OctalDump extends GenericDump {
	public OctalDump (String s, int n) {super(s,n);}
	protected String getValue(byte data) {return Integer.toString(data,8);}
	protected void startMsg(String s) {System.out.println("Octal Dump of file "+s);}
	protected void endMsg(String s) {System.out.println("Octal Dump Complete");}
	protected void write(String s) {System.out.print(":"+s);}
	protected void newline() {System.out.println();}
}

