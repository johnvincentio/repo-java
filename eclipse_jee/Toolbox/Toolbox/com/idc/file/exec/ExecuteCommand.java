package com.idc.file.exec;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExecuteCommand {
	private StringBuffer m_sb;

	public void executeCommand (String[] strCmd) {
//		System.out.println (strCmd);
//		System.out.println ("OS :"+getOSName()+":");
		m_sb = new StringBuffer();
		try {
			Process process = Runtime.getRuntime().exec(strCmd);
			InputStream in = new BufferedInputStream (process.getInputStream());
			for (;;) {
				int c = in.read();
				if (c == -1) break;
				m_sb.append ((char) c);
			}
			in.close();
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
//		System.out.println (m_sb.toString());
	}
	public String getOutput() {return m_sb.toString();}
	public static String getOSName() {return System.getProperty ("os.name");}
	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.indexOf("win") >= 0;
	}

	public void executeCommand (String[] strCmd, OutputLine cout) {
//		System.out.println(">>> executeCommand");
		try {
			Process process = Runtime.getRuntime().exec (strCmd);
			BufferedReader reader = new BufferedReader (new InputStreamReader (process.getInputStream()));
			String lineRead = null;
			while((lineRead = reader.readLine()) != null) {
				cout.println(lineRead);
			}
			reader.close();
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
//		System.out.println("<<< executeCommand");
	}

	public void executeCommand (String[] strCmd, String[] envp, File cwd, OutputLine cout) {
		try {
			Process process = Runtime.getRuntime().exec (strCmd, envp, cwd);
			BufferedReader reader = new BufferedReader (new InputStreamReader (process.getInputStream()));
			String lineRead = null;
			while((lineRead = reader.readLine()) != null) {
				cout.println(lineRead);
			}
			reader.close();
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	public void executeCommandDetach (String[] strCmd, String[] envp, File cwd, OutputLine cout) {
		try {
			Runtime.getRuntime().exec (strCmd, envp, cwd);
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	public int executeCommandGetExitCode (String[] strCmd, String[] envp, File cwd, OutputLine cout) {
		try {
			Process process = Runtime.getRuntime().exec (strCmd, envp, cwd);
			process.waitFor();
			return process.exitValue();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}
}
