
package com.idc.file.exec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class PrintFile implements OutputLine {
	private PrintWriter m_writer;
	public PrintFile (File file) {
		try {
			m_writer = new PrintWriter (new OutputStreamWriter (new FileOutputStream (file, true)), true);
		} catch (Exception ex) {
			System.out.println("exception in setFile");
		}
	}
	public void println(String msg) {m_writer.println (msg);}
	public void print(String msg) {m_writer.print (msg);}
	public void println() {m_writer.println();}
	public void close() {m_writer.close();}
}
