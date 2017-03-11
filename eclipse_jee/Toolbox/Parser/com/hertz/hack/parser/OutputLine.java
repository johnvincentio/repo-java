package com.hertz.hack.parser;

public interface OutputLine {
	public void open(String msg);
	public void println(String msg);
	public void close();
}
