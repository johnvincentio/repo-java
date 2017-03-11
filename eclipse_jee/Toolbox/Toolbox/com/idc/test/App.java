package com.idc.test;

public class App {

	public static void main (String[] args) {
		App.doit1();
		App.doit2();
	}
	private static void doit1() {
		JVxml jvxml = new JVxml();
		String strFile =
		"C:\\irac\\wrkspc\\HERC_INT\\Servers\\IracTestServer.wsc\\cells\\localhost\\namebindings.xml";
		jvxml.parse (strFile);
	}
	private static void doit2() {
		JVxml2 jvxml = new JVxml2();
		String strFile =
		"C:\\irac\\wrkspc\\HERC_INT\\Servers\\IracTestServer.wsc\\cells\\localhost\\namebindings.xml";
		jvxml.parse (strFile);
	}
}
