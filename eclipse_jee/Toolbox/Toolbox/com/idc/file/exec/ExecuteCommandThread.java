package com.idc.file.exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecuteCommandThread extends Thread {
	private String[] m_strCmd;
	private OutputLine m_cout;
	private File m_cwd = null;
	private String[] m_envp = null;

	public ExecuteCommandThread(String[] strCmd, OutputLine cout) {
		m_strCmd = strCmd;
		m_cout = cout;
	}

	public ExecuteCommandThread(String[] strCmd, String[] envp, File cwd, OutputLine cout) {
		this (strCmd, cout);
		m_cwd = cwd;
		m_envp = envp;
		System.out.println("cwd :" + cwd + ":");
		for (int num = 0; num < envp.length; num++) {
			System.out.println("num " + num + " value :" + envp[num] + ":");
		}
	}

	public void run() {
		System.out.println(">>> ExecuteCommandThread::run");
		BufferedReader reader = null;
		Process process;
		try {
			if (m_cwd == null)
				process = Runtime.getRuntime().exec (m_strCmd);
			else
				process = Runtime.getRuntime().exec (m_strCmd, m_envp, m_cwd);
			reader = new BufferedReader(new InputStreamReader (process.getInputStream()));
			String lineRead = null;
			while ((lineRead = reader.readLine()) != null) {
//				System.out.println("lineRead :" + lineRead + ":");
				m_cout.println(lineRead);
			}
		} catch (IOException ioex) {
			ioex.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException ex) {
			}
		}
		System.out.println("<<< ExecuteCommandThread::run");
	}
}
