
package com.idc.files;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class FilesSizes {
	private PrintWriter m_out;
	private List<Holder> m_list = new ArrayList<Holder>();

	public static void main (String[] args) {
		(new FilesSizes()).app(args);
	}
	private void add (Holder holder) {m_list.add(holder);}

	private void app (String[] args) {
		String strDir = System.getProperty("user.dir");
//                strDir = "C:\\jv6Work";
//                strDir = "C:\\Program Files";
		strDir = "C:\\";
		System.out.println("workdir "+strDir);
		doDirectory (new File(strDir));

		makeFile("c:\\tmp\\BySizes.txt");
		m_out.println("By sizes");
		m_out.println(" ");
		Collections.sort(m_list, new SortHoldersAscBySizes());
		Iterator<Holder> iter = m_list.iterator();
		Holder holder;
		while (iter.hasNext()) {
			holder = (Holder) iter.next();
			holder.show();
		}
		closeFile();

		makeFile("c:\\tmp\\ByFiles.txt");
		m_out.println("By files");
		m_out.println(" ");
		Collections.sort(m_list, new SortHoldersAscByFiles());
		iter = m_list.iterator();
		while (iter.hasNext()) {
			holder = (Holder) iter.next();
			holder.show();
		}
		closeFile();
	}
	private void makeFile(String strFile) {
		try {
			m_out = new PrintWriter (new BufferedWriter(
			new FileWriter(strFile)));
		}
		catch (IOException ex) {
			System.out.println("Unable to create output file: "+ex.getMessage());
			System.exit(1);
		}
	}
	private void closeFile() {m_out.close();}
	private void doDirectory (final File dir) {
		Holder holder = new Holder(dir.getPath());
//                System.out.println(">>> doDirectory "+dir.getPath());
		if (! dir.isDirectory()) return;

		File file;
		File[] allFiles = dir.listFiles();
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (! file.isFile()) continue;
			holder.IncrFiles();
			holder.IncrSizes(file.length());
		}
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) doDirectory(file);
		}
		add (holder);
//                if (holder.isShow())
//                        m_out.println("("+holder.getSizes()+","+holder.getFiles()+") "+dir.getPath());
//                System.out.println("<<< doDirectory");
	}
/*
	private boolean isDirectoryOK(final File dir, String mask) {
		String strDir = dir.getPath();
		int len = mask.length();
		if (strDir.length() != len) return true;
		if (strDir.substring(0,len).equals(mask)) return false;
		return true;
	}
*/
	private class SortHoldersAscBySizes implements Comparator<Holder> {
		public int compare(Holder a, Holder b) {
			if (a.getSizes() < b.getSizes()) return 1;
			return 0;
		}
	}
	private class SortHoldersAscByFiles implements Comparator<Holder> {
		public int compare(Holder a, Holder b) {
			if (a.getFiles() < b.getFiles()) return 1;
			return 0;
		}
	}
	private class Holder {
		private String m_name;
//                private long m_div = 1000000;
		private long m_div = 1000;
		private long m_filter = m_div * 1000;
		private long m_filter_files = 50;
		private long m_files = 0;
		private long m_sizes = 0;
		public Holder(String s){m_name = s;}
		public String getName() {return m_name;}
		public long getFiles() {return m_files;}
		public long getSizes() {return m_sizes;}
		public void IncrFiles() {m_files++;}
		public void IncrSizes (long lnum) {m_sizes += (lnum / m_div);}
/*
		public void Increment (Holder holder) {
			m_files += holder.getFiles();
			m_sizes += holder.getSizes();
		}
*/
		public boolean isShow() {
//                        return true;
			return (getFiles() > m_filter_files) || (getSizes() > m_filter);
		}
		public void show() {
			if (isShow())
			m_out.println("("+getSizes()+","+getFiles()+") "+getName());
		}
	}
}
