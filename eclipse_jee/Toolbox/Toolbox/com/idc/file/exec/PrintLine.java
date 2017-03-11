
package com.idc.file.exec;

public class PrintLine implements OutputLine {
	public void println(String msg) {
		System.out.println(msg);
	}
	public void close() {}
}
