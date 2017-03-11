
package com.idc.knight;

import java.io.*;
import java.sql.Time;

public class Debug {
	private static boolean bConsole = false;
	private static boolean bStatus = false;

	private static PrintWriter out;

	public Debug() {}

	public static void setFile () {bStatus = bConsole = true;}
	public static void setFile (String strFile, boolean bValue) {
		try {
			out = new PrintWriter (new BufferedWriter (
							new FileWriter (strFile, bValue)));
		} catch (IOException e) {
			System.out.println ("Unable to open file :" + strFile + ":");
			return;
		}
		bStatus = true;
	}
	public static synchronized void print (String msg) {
		if (! bStatus) return;
		if (bConsole) {System.out.print (msg); return;}
		out.write (msg);
	}
	public static void flush() {out.flush();}
	private static String newLine() {return "\r\n";}
	public static synchronized void println () {print(newLine());}
	public static synchronized void println (String msg) {print(msg + newLine());}
	public static synchronized void timing (String msg) {
		Time time = new Time(System.currentTimeMillis());
		println (time.toString() + "; " + msg);
	}
}

/*
		try {
			out = new PrintWriter (new BufferedWriter (
							new FileWriter (strFile, bValue)));
System.out.println("setFile 2");
		} catch (IOException e) {
			System.out.println ("Unable to open file :" + strFile + ":");
			return;
		}
*/
