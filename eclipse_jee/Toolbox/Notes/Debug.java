
package com.idc.loadtest;

import java.io.*;
import java.sql.Time;

public class Debug {
	private static String m_strFile = "";
	private static boolean m_bAppend = false;
	private static boolean m_bConsole = true;

	private Debug() {}

	public static void setFile (String name, boolean bValue) {
		m_strFile = name;
		m_bAppend = bValue;
		m_bConsole = false;
		timing("Initializing");
	}

	private static String newLine() {return "\r\n";}
	public static synchronized void println () {print(newLine());}
	public static synchronized void println (String msg) {print(msg + newLine());}
	public static synchronized void print (String msg) {
		if (m_bConsole) {
			System.out.print(msg);
			return;
		}
		try {
			FileWriter out = new FileWriter (m_strFile,m_bAppend);
			out.write (msg);
			out.close();
			m_bAppend = true;
		}
		catch (IOException e) {
			println ("Unable to write to File :" + m_strFile + ":");
			println (msg);
		}
	}
	public static synchronized void timing (String msg) {
		Time time = new Time(System.currentTimeMillis());
		println (time.toString() + "; " + msg);
	}
}
