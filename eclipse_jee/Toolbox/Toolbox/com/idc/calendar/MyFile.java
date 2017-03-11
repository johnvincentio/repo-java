package com.idc.calendar;

import java.io.*;
import java.util.logging.*;

public class MyFile {
	private String m_strFile;
	private Logger m_logger = Logger.getLogger("");
	private PrintStream m_outfile;

	public MyFile (String strFile, Logger logger) {
		m_strFile = strFile;
		m_logger = logger;
	}
	public void open() {
		try {
			m_outfile = new PrintStream (new BufferedOutputStream (
					new FileOutputStream (m_strFile)));
		} catch (IOException ex) {
			m_logger.severe ("Unable to open the output file");
			System.exit (1);
		}
	}
	public void close() {m_outfile.close();}
	public void write (String msg) {m_outfile.println(msg);}
	public void newLine () {m_outfile.println();}
	public void newPage () {m_outfile.print("\\f\\n");}
}
