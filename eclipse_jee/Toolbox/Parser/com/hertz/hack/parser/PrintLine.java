package com.hertz.hack.parser;

public class PrintLine implements OutputLine {
	public void open(String s) {}
	public void println(String msg) {
		System.out.println(msg);
	}
	public void close() {}
}
