package com.idc.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JV {
	public static void main(String[] args) {
		(new JV()).doTest();
	}
	private void doTest() {
		String[] strcmd = {"cmd.exe", "/c", "set"};
		try {
			Process process = Runtime.getRuntime().exec(strcmd);
			BufferedReader reader = new BufferedReader (new InputStreamReader (process.getInputStream()));
			String lineRead = null;
			while((lineRead = reader.readLine()) != null) {
				System.out.println(lineRead);
			}
			reader.close();
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}
}
