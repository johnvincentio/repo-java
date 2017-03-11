package com.idc.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Count2 {
	ArrayList<String> unknownList = new ArrayList<String>();
	private int m_nTypes1;
	private long m_nTotalFilesCount1 = 0;
	private long m_nTotalLinesCount1 = 0;
	private long m_nFiles1[];
	private long m_nLines1[];

	private int m_nTypes2;
	private long m_nTotalFilesCount2 = 0;
	private long m_nFiles2[];

	private static final String[] m_astrTypes1 = {"bat", "classpath", "compatibility", "component", "container",
			"css", "dat", "dnx", "dtd", "eisConnections", "ftl", "gph",  
			"html", "htm", "j2ee", "jacl", "java", "jrxml", "js", "jsp", "jsdtscope",
			"ldif", "log", "mf", "modulemaps", "name",
			"prefs", "project", "properties", "psf", "runtime", "scss", "sql", "tld", "txt", "values", 
			"websettings", "website-config", "webspheredeploy", 
			"wsdd", "wsdl", "xml", "xmi", "xsd", "xsl"};

	private static final String[] m_astrTypes2 = {"class", "ear", "ico", "gif", "jar", "jasper", "pdf", "png", "zip"};

	public static void main(String[] args) {
		Count2 count = new Count2();
		count.doCount();
	}

	private void doCount() {
		m_nTypes1 = m_astrTypes1.length;
//		System.out.println("m_nTypes "+m_nTypes1);
		m_nFiles1 = new long[m_nTypes1];
		m_nLines1 = new long[m_nTypes1];
		for (int i = 0; i < m_nTypes1; i++) {
			m_nFiles1[i] = 0;
			m_nLines1[i] = 0;
		}

		m_nTypes2 = m_astrTypes2.length;
//		System.out.println("m_nTypes "+m_nTypes2);
		m_nFiles2 = new long[m_nTypes2];
		for (int i = 0; i < m_nTypes2; i++) {
			m_nFiles2[i] = 0;
		}

		String strDir = "C:/work104/HercJars/access";
		strDir = "c:/work104/HercJacl/development/ears";
		strDir = "c:/work104/HercJacl/herc_server";
		strDir = "c:/work104/HercJacl";
		strDir = "c:/work104/HercDataWeb";
		strDir = "c:/work104";
//		strDir = "C:/development/herc-11.6-dev/HercTestingWeb/JavaSource";
		File tmpfile = new File(strDir);
		if (! tmpfile.isDirectory()) {
			System.out.println("Directory " + strDir + " does not exist");
			System.exit(1);
		}
		System.out.println("Searching directories :"+strDir+":");
		doDirectory(strDir);
		doReport(strDir);
		System.out.println("Search is complete");
	}

	private void doDirectory(String strDir) {
		File file = new File(strDir);
		doDirectory(file);
	}

	private void doDirectory(File dir) {
//		System.out.println(">>> doDirectory; "+dir.getPath());
		if (! dir.isDirectory()) return;			// handle files
		File[] allFiles = dir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			File file = allFiles[i];
			if (file.isFile()) {
//				System.out.println("(File) i = "+i+" file :"+file.getPath()+":");
				doFile(file);
			}
		}

		allFiles = dir.listFiles();					// handle directories
		for (int i = 0; i < allFiles.length; i++) {
			File file = allFiles[i];
			if (file.isDirectory()) {
//				System.out.println("(Directory) i = "+i+" file :"+file.getPath()+":");
				doDirectory(file);
			}
		}
//		System.out.println("<<< doDirectory");
	}

	private void doFile(File file) {
//		System.out.println(">>> doFile; "+file.getPath());
		if (! file.isFile()) return;

		int nType1 = getFileType1 (file);
		int nType2 = getFileType2 (file);
//		System.out.println("Type1 "+nType1+" Type2 "+nType2);

		if (nType1 > -1) {
			m_nFiles1[nType1]++;
			m_nTotalFilesCount1++;

			long lCntr = 0;
			BufferedReader buf = null;
			try {
				buf = new BufferedReader(new FileReader(file));
				while ((buf.readLine()) != null) {
					m_nTotalLinesCount1++;
					lCntr++;
				}
				m_nLines1[nType1] += lCntr;
			}
			catch (Exception exception) {
				exception.printStackTrace();
			}
			finally {
				try {
					if (buf != null) buf.close();
				}
				catch (IOException exception2) {
					exception2.printStackTrace();
				}
			}
		}
		else if (nType2 > -1) {
			m_nFiles2[nType2]++;
			m_nTotalFilesCount2++;
		}
		else
			unknownList.add (file.getPath());
//		System.out.println("<<< doFile; "+file.getPath());
		return;
	}

	private int getFileType1 (File file) {
		String strName = file.getName();
//		System.out.println("strName :"+strName+":");
		int num = strName.lastIndexOf(".");
		if (num < 0) {
//			System.out.println("Unknown type; file is: "+strName);
			return -1; // no extension
		}
		String strExt = strName.substring(num + 1, strName.length());
//		System.out.println("strExt :"+strExt+":");
		m_nTypes1 = m_astrTypes1.length;
		for (int i = 0; i < m_nTypes1; i++) {
			if (strExt.equalsIgnoreCase (m_astrTypes1[i])) return i;
		}
//		System.out.println("Unknown type; file is: "+strName);
		return -1;
	}

	private int getFileType2 (File file) {
		String strName = file.getName();
//		System.out.println("strName :"+strName+":");
		int num = strName.lastIndexOf(".");
		if (num < 0) {
//			System.out.println("Unknown type; file is: "+strName);
			return -1; // no extension
		}
		String strExt = strName.substring(num + 1, strName.length());
//		System.out.println("strExt :"+strExt+":");
		m_nTypes2 = m_astrTypes2.length;
		for (int i = 0; i < m_nTypes2; i++) {
			if (strExt.equalsIgnoreCase (m_astrTypes2[i])) return i;
		}
//		System.out.println("Unknown type; file is: "+strName);
		return -1;
	}

	private void doReport(String strDir) {
		System.out.println("Scan Directory " + strDir);
		System.out.println("File type statistics: (type, files, lines)");
		for (int i = 0; i < m_nTypes1; i++) {
			System.out.println("(" + m_astrTypes1[i] + ", " + m_nFiles1[i] + ", "	+ m_nLines1[i] + ")");
		}

		System.out.println("File type statistics: (type, files)");
		for (int i = 0; i < m_nTypes2; i++) {
			System.out.println("(" + m_astrTypes2[i] + ", " + m_nFiles2[i] + ")");
		}

		System.out.println("Total Number of Unknown Files "+unknownList.size());
		for (int i = 0; i < unknownList.size(); i++) {
			System.out.println (unknownList.get(i));
		}
		System.out.println("Total Number of Files " + (m_nTotalFilesCount1 + m_nTotalFilesCount2 + unknownList.size()));
		System.out.println("Total Number of Lines " + m_nTotalLinesCount1);
	}
}
