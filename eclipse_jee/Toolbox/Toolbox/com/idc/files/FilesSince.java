package com.idc.files;

import java.util.*;
import java.text.*;
import java.io.*;

public class FilesSince {
	private PrintWriter m_out;
	public static void main (String[] args) {
		(new FilesSince()).app(args);
	}
	private void app (String[] args) {
		if (args.length < 1) doUsage();
		long lSince = 0;
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date myDate = format.parse(args[0]);
			lSince = myDate.getTime();
		}
		catch (ParseException e) {
			System.out.println("Unable to parse "+args[0]);
			doUsage();
		}
		String strDir = System.getProperty("user.dir");
		if (args.length > 1) strDir = args[1];
		strDir = "c:\\development\\herc_09_06";
		System.out.println("workdir "+strDir+" Since "+lSince);

		makeFile();
		doDirectory (new File(strDir), lSince);
		closeFile();
		System.out.println("Complete");
	}
	private void doUsage() {
		System.out.println("Usage: com.idc.files.FilesSince dd/mm/yyyy");
		System.exit(1);
	}
	private void makeFile() {
		try {
			m_out = new PrintWriter (new BufferedWriter(new FileWriter("c:\\tmp\\since.txt")));
		}
		catch (IOException ex) {
			System.out.println("Unable to create output file: "+ex.getMessage());
			System.exit(1);
		}
	}
	private void closeFile() {m_out.close();}
	private void doDirectory (final File dir, long lSince) {
//		System.out.println(">>> doDirectory "+dir.getPath());
		if (! dir.isDirectory()) return;

		File file;
		File[] allFiles = dir.listFiles();
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (! file.isFile()) continue;
			if (! isFileOK (file)) continue;
//			System.out.println("File "+file.getPath()+" modified "+	
//					file.lastModified());
			if (lSince < file.lastModified()) {
				m_out.println(file.getPath());
			}
		}
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) doDirectory(file,lSince);
		}
//		System.out.println("<<< doDirectory");
	}
	private boolean isFileOK (File file) {
		String name = file.getPath().trim().toLowerCase();
		if (name.endsWith(".class")) return false;
		return true;
	}
	/*
	private boolean isDirectoryOK (final File dir, String mask) {
		String strDir = dir.getPath();
		int len = mask.length();
		if (strDir.length() != len) return true;
		if (strDir.substring(0,len).equals(mask)) return false;
		return true;
	}
	*/
}
