package com.idc.sudoku;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Time;

public class JVOutputFile {
	private static JVOutputFile m_log = new JVOutputFile();
	public static JVOutputFile getInstance() {return m_log;}
	private PrintWriter m_writer;
	private JVOutputFile() {m_writer = new PrintWriter(System.out,true);}
	public PrintWriter getWriter() {return m_writer;}

	public void setFile () {m_writer = new PrintWriter(System.out, true);}
	public void setFile (String name, boolean bValue) {
		try {
			m_writer = new PrintWriter(new OutputStreamWriter(
							new FileOutputStream(name, bValue)), true);
		} catch (Exception ex) {
			System.err.println("exception in setFile");
			setFile();
		}
	}
	public void println () {m_writer.println ();}
	public void println (String msg) {m_writer.println (msg);}
	public void print (String msg) {m_writer.print (msg);}
	public void timing (String msg) {
		println ((new Time(System.currentTimeMillis())).toString() + "; " + msg);
	}
	public void close() {m_writer.close();}
}
