package com.idc.five.test;

import com.idc.five.output.Output;
import com.idc.five.output.OutputFile;

public class A {
	public static void main(String[] args) {
		Output output = new OutputFile ("five-in-a-row-dump-file", ".txt", "/tmp");
		output.open();
		output.println("line 1");
		output.println("line 2");
		output.println("line 3");
		output.close();
	}
}
