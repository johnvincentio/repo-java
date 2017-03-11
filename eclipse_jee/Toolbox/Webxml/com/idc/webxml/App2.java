package com.idc.webxml;

import com.idc.file.exec.OutputLine;

public class App2 implements OutputLine {

	public static void main(String[] args) {
		(new App2()).doTest();
	}
	private void doTest() {
		Control control = new Control (this);
		control.convertWebXML("/tmp/web.xml");
	}
	public void println(String msg) {
		System.out.println(msg);
	}
	public void close() {}
}
