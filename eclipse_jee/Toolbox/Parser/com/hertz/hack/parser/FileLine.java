
package com.hertz.hack.parser;

import java.io.*;

public class FileLine implements OutputLine {
	private PrintWriter m_out;
	public void open(String strFile) {
		try {
			m_out = new PrintWriter (new BufferedWriter(
				new FileWriter(strFile)));
		}
		catch (IOException ex) {
			System.out.println("Unable to create output file "+strFile+": "+ex.getMessage());
			System.exit(1);
		}

	}
	public void println(String msg) {
		m_out.println(msg);
	}
	public void close() {
		m_out.close();
	}
}

