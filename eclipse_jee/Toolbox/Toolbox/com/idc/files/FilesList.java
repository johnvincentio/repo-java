package com.idc.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FilesList {
	private static final String REPORT_FILE = "c:\\tmp\\files_list.txt";
	private static final String FILES_DIR = "C:\\work\\work202\\herc-14.5-dev";

	private PrintWriter m_out;
	public static void main (String[] args) {
		(new FilesList()).app (FILES_DIR, REPORT_FILE);
	}
	private void app (String filesDir, String reportFile) {
		makeFile (reportFile);
		doDirectory (new File(filesDir));
		closeFile();
		System.out.println("Complete");
	}

	private void makeFile (String reportFile) {
		try {
			m_out = new PrintWriter (new BufferedWriter(new FileWriter(reportFile)));
		}
		catch (IOException ex) {
			System.out.println("Unable to create output file: "+ex.getMessage());
			System.exit(1);
		}
	}
	private void closeFile() {m_out.close();}

	private void doDirectory (final File dir) {
//		System.out.println(">>> doDirectory "+dir.getPath());
		if (! dir.isDirectory()) return;

		File file;
		File[] allFiles = dir.listFiles();
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (! file.isFile()) continue;
			m_out.println(file.getPath());
		}
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) doDirectory(file);
		}
//		System.out.println("<<< doDirectory");
	}
}
