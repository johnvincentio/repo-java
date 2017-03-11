package com.idc.trace;

import java.io.*;
import java.sql.Time;

public class JVLog {
	private static JVLog m_log = new JVLog();
	public static JVLog getInstance() {return m_log;}
	private PrintWriter m_writer;
	private JVLog() {m_writer = new PrintWriter(System.out,true);}
	public PrintWriter getWriter() {return m_writer;}

	public void setFile () {m_writer = new PrintWriter(System.out, true);}
	public void setFile (String name, boolean bValue) {
		try {
			m_writer = new PrintWriter(new OutputStreamWriter(
							new FileOutputStream(name, bValue)), true);
		} catch (Exception ex) {
			System.out.println("exception in setFile");
			setFile();
		}
		timing("Initializing");
	}
	public void println () {m_writer.println ();}
	public void println (String msg) {m_writer.println (msg);}
	public void print (String msg) {m_writer.print (msg);}
	public void timing (String msg) {
		println ((new Time(System.currentTimeMillis())).toString() + "; " + msg);
	}
	public void close() {m_writer.close();}
}
