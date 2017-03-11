package com.idc.file.exec;

public class App {
	public static void main(String[] arg) throws Exception {
		ExecuteCommand process = new ExecuteCommand();
		String[] cmd1 = {"pwd"};
		String[] cmd2 = {"ls"};
		PrintLine out = new PrintLine();
		process.executeCommand(cmd1, out);
		process.executeCommand(cmd2, out);
	}
}
