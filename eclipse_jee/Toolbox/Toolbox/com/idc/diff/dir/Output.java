
package com.idc.diff.dir;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Time;

public class Output {
	private PrintWriter m_writer;
	public Output (String name) {
		try {
		m_writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream (name, false)), true);
		} catch (Exception ex) {
			System.out.println("exception in setFile");
			System.exit(1);
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
