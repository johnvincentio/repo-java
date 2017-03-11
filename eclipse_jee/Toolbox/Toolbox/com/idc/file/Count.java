package com.idc.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class Count {
	private int m_nTypes;
	private long m_nTotalFilesCount = 0;
	private long m_nTotalLinesCount = 0;
	private long m_nFiles[];
	private long m_nLines[];

	private static final String[] m_astrTypes = {"other", "class", "jar",
			"psf", "java", "sql", "jacl", "tld", "wsdl", "jsp", "js", "xml",
			"xsl", "xsd", "xmi", "dtd", "html", "htm", "css", "log", "txt",
			"ldif", "properties", "mf", "runtime", "websettings", "modulemaps",
			"j2ee", "compatibility", "website-config", "gph", "dnx",
			"webspheredeploy", "classpath", "project", "ftl", "css", "jrxml", "jasper",
			"eisConnections", "bat"};

	public static void main(String[] args) {
		Count count = new Count();
		count.doCount(args[0]);
	}

	private void doCount(String arg) {
		m_nTypes = m_astrTypes.length;
		// System.out.println("m_nTypes "+m_nTypes);
		m_nFiles = new long[m_nTypes];
		m_nLines = new long[m_nTypes];
		for (int i = 0; i < m_nTypes; i++) {
			m_nFiles[i] = 0;
			m_nLines[i] = 0;
		}
		String strDir = null;
		// System.out.println(">>> doCount; arg :"+arg+":");
		if (arg != null && arg.length() > 0)
			strDir = arg;
		else
			strDir = System.getProperty("user.dir");
		strDir = "C:/work101";
		File tmpfile = new File(strDir);
		if (! tmpfile.isDirectory()) {
			System.out.println("Directory " + strDir + " does not exist");
			System.exit(1);
		}
		// System.out.println("Searching directories :"+strDir+":");
		doDirectory(strDir);
		doReport(strDir);
		// System.out.println("Search is complete");
	}

	private void doDirectory(String strDir) {
		File file = new File(strDir);
		doDirectory(file);
	}

	private void doDirectory(File dir) {
		File[] allFiles;
		File file;

		// handle files

		// System.out.println(">>> doDirectory; "+dir.getPath());
		if (! dir.isDirectory())
			return;
		allFiles = dir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			file = allFiles[i];
			if (file.isFile()) {
				// System.out.println("(File) i = "+i+" file :"+file.getPath()+":");
				doFile(file);
			}
		}

		// handle directories

		allFiles = dir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) {
				// System.out.println("(Directory) i = "+i+" file :"+file.getPath()+":");
				doDirectory(file);
			}
		}
		// System.out.println("<<< doDirectory");
	}

	private void doFile(File file) {
		// System.out.println(">>> doFile; "+file.getPath());
		if (!file.isFile())
			return;
		int nType = getFileType(file);
		// System.out.println("Type "+nType+" file "+file.getPath());
		m_nFiles[nType]++;
		m_nTotalFilesCount++;
		if (nType < 1) {
			System.out.println("Other: " + file.getPath());
			return;
		}
		if (nType < 4)
			return;

		long lCntr = 0;
		BufferedReader buf = null;
		try {
			buf = new BufferedReader(new FileReader(file));
			while ((buf.readLine()) != null) {
				m_nTotalLinesCount++;
				lCntr++;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				if (buf != null)
					buf.close();
			} catch (IOException exception2) {
				exception2.printStackTrace();
			}
		}
		m_nLines[nType] += lCntr;
		// System.out.println("<<< doFile; "+file.getPath());
		return;
	}

	private int getFileType(File file) {
		String strName = file.getName();
		// System.out.println("strName :"+strName+":");
		int num = strName.lastIndexOf(".");
		if (num < 0) {
			System.out.println("Unknown type; file is: "+strName);
			return 0; // no extension
		}
		String strExt = strName.substring(num + 1, strName.length());
		// System.out.println("strExt :"+strExt+":");
		m_nTypes = m_astrTypes.length;
		for (int i = 0; i < m_nTypes; i++) {
			if (strExt.equalsIgnoreCase(m_astrTypes[i])) return i;
		}
		System.out.println("Unknown type; file is: "+strName);
		return 0;
	}

	private void doReport(String strDir) {
		System.out.println("Scan Directory " + strDir);
		System.out.println("Total Number of Files " + m_nTotalFilesCount);
		System.out.println("Total Number of Lines " + m_nTotalLinesCount);

		System.out.println("File type statistics: (type, files, lines)");
		for (int i = 0; i < m_nTypes; i++) {
			System.out.println("(" + m_astrTypes[i] + ", " + m_nFiles[i] + ", "	+ m_nLines[i] + ")");
		}
		System.out.println("Done");
	}
}
