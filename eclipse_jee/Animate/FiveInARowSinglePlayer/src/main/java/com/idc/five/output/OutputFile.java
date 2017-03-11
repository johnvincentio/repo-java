package com.idc.five.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OutputFile implements Output {
	private File m_file;
	private BufferedWriter m_buffer;

	public OutputFile(File file) {
		m_file = file;
	}

	public OutputFile(String name, String extension, String directory) {
		try {
			m_file = File.createTempFile(name, extension, new File(directory));
			System.out.println("Temp file : " + m_file.getAbsolutePath());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean open() {
		try {
			m_buffer = new BufferedWriter(new FileWriter(m_file));
		}
		catch (IOException e) {
			System.out.println("Unable to open file " + m_file.getPath() + ". Aborting");
			return false;
		}
		return true;
	}

	public void close() {
		try {
			m_buffer.close();
			m_buffer = null;
			m_file = null;
		}
		catch (IOException e) {
			System.out.println("Could not close file " + m_file.getPath() + ":");
		}
	}

	public void print(String msg) {
		try {
			m_buffer.write(msg);
			m_buffer.flush();
		}
		catch (IOException e) {
			System.out.println("Could not write to file " + m_file.getPath() + ": Aborting...");
			System.exit(1);
		}
	}

	public void println(String msg) {
		print(msg + "\n");
	}

	public void println() {
		print("\n");
	}

	public void print(int num) {
		print(Integer.toString(num));
	}
}
