package com.idc.five.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JVFile {

	private File file;
	private BufferedWriter buffer;

	public JVFile (File file) {
		this.file = file;
	}

	public boolean open() {
		try {
			buffer = new BufferedWriter (new FileWriter (file));
		}
		catch (IOException e) {
			System.out.println ("Unable to open file " + file.getPath () + ". Aborting");
			return false;
		}
		return true;
	}

	public void close() {
		try {
			buffer.close ();
		}
		catch (IOException e) {
			System.out.println ("Could not close file " + file.getPath () + ":");
		}
	}

	public void write (String msg) {
		try {
			buffer.write (msg);
			buffer.flush ();
		}
		catch (IOException e) {
			System.out.println ("Could not write to file " + file.getPath () + ": Aborting...");
			System.exit (1);
		}
	}

	public void writeNL (String msg) {
		write (msg + "\n");
	}

	public void writeNL() {
		write ("\n");
	}

	public void write (int num) {
		write (Integer.toString (num));
	}

	public static String getCwd() {
		return System.getProperty ("user.dir");
	}

	public static String getExtension (String strName) {
		if (strName == null || strName.length () < 1) return "";
		int pos = strName.lastIndexOf ('.');
		if (pos < 0) return "";
		return strName.substring (pos + 1);
	}
}
