package com.idc.knight.apps;

import java.io.File;

import com.idc.knight.JVFile;

public class RenameFiles {

	private static final String FROM_DIR = "C:\\work\\Knight_solutions";
	private static final String TO_DIR = "C:/work/jv_solutions";

	public static void main (String[] args) {
		(new RenameFiles()).app(args);
	}
	private void app (String[] args) {
		doDirectory (new File (FROM_DIR));
		System.out.println("Complete");
	}

	private void doDirectory (final File dir) {
//		System.out.println(">>> doDirectory "+dir.getPath());
		if (! dir.isDirectory()) return;

		File file;
		File[] allFiles = dir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			file = allFiles[i];
			if (! file.isFile()) continue;
			String strFrom = file.getPath();
			String strTo = replace (strFrom, FROM_DIR, TO_DIR) + ".xml";
			JVFile.makeDirectories (new File (strTo));
			System.out.println("Copying "+strFrom+" to "+strTo);
			JVFile.copyFile (strFrom, strTo);
		}
		for (int i = 0; i < allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) doDirectory (file);
		}
//		System.out.println("<<< doDirectory");
	}

	private String replace (String input, String pattern, String replace) {
		int s = 0;
		int e = 0;
		StringBuffer buf = new StringBuffer();
    
		while ((e = input.indexOf(pattern, s)) >= 0) {
			buf.append(input.substring(s, e));
			buf.append(replace);
			s = e + pattern.length();
		}
		buf.append(input.substring(s));
		return buf.toString();
	}
}
