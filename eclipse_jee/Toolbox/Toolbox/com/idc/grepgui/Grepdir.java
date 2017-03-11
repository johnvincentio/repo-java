package com.idc.grepgui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.trace.LogHelper;

public class Grepdir {
	private GrepdirGui m_app;
	public Grepdir (GrepdirGui app) {m_app = app;}
	public boolean isSearchStopped() {
//		System.out.println("is search stopped; "+m_app.getAppThread().getStopStatus());
		return m_app.getAppThread().getStopStatus();
	}
	private void handleProgressIndicator() {m_app.handleProgressIndicator();}
	private void addMessage (String msg) {m_app.setMessagesArea(msg);}
	public void doGrepdir (String strDir, ArrayList<String> list, String strFiles, boolean bCaseSensitive) {
		doDirectory (new File(strDir), list, strFiles, bCaseSensitive);
//		System.out.println("<<< leaving doGrepdir");
	}
	private void doDirectory (File dir, ArrayList<String> findList, String strFiles, boolean bCaseSensitive) {
		File [] allFiles;
		File file;
/*
		try {
			Thread.sleep(10L);
		}
		catch (InterruptedException ex) {
			System.out.println("thread exception; "+ex.getMessage());
		}
*/
				if (isSearchStopped()) return;	// user stopped the search
							
//		LogHelper.info(">>> doDirectory; "+dir.getPath());
		if (! dir.isDirectory()) return;
		handleProgressIndicator();
		
// handle files
		
		GrepdirMultipleMasksFilter filter = new GrepdirMultipleMasksFilter();
		filter.setMask(strFiles);		
		allFiles = dir.listFiles(filter);
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isFile()) {
//				LogHelper.info("(File) i = "+i+" file :"+file.getPath()+":");
				if (isSearchStopped()) return;	// user stopped the search
				doFile (file, findList, bCaseSensitive);
			}
		}

// handle directories

		allFiles = dir.listFiles();
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) {
//				LogHelper.info("(Directory) i = "+i+" file :"+file.getPath()+":");
				if (isSearchStopped()) return;	// user stopped the search
				doDirectory (file, findList, strFiles, bCaseSensitive);
			}
		}
//		LogHelper.info("<<< doDirectory");
	}
	private void doFile (final File file, final ArrayList<String> findList, boolean bCaseSensitive) {
//		LogHelper.info(">>> doFile; "+file.getPath());
		if (! file.isFile()) return;
		
		ArrayList<String> codeList = new ArrayList<String>();
		BufferedReader buf = null;
		String line;
		try {
			buf = new BufferedReader(new FileReader(file));
			while ((line = buf.readLine()) != null) {codeList.add(line);}
			buf.close();
			buf = null;
		}
		catch (IOException exception) {
			System.out.println("Exception "+exception.getMessage());
			System.out.println("Trouble reading file "+file.getPath());
//			exception.printStackTrace();
		}
		finally {
			try {
				if (buf != null) buf.close();
			}
			catch (IOException exception2) {
				System.out.println("Exception "+exception2.getMessage());
				System.out.println("Trouble closing file "+file.getPath());
				exception2.printStackTrace();
			}
		}
		buf = null;

// first pass; look for all strings, they all must be found

		boolean[] abFound = new boolean[findList.size()];
		for (int i=0; i<abFound.length; i++) abFound[i] = false;

		Iterator<String> iter = codeList.iterator();	// parse through the code line by line
		while(iter.hasNext()) {
			String strCode = (String) iter.next();
			for (int i=0; i<findList.size(); i++) {	// find next string
				if (isStringFound(bCaseSensitive, strCode, (String) findList.get(i)))
					abFound[i] = true;		// string found
			}
		}
		for (int i=0; i<abFound.length; i++) {
			if (! abFound[i]) return;	// a string not found in file
		}

// second pass; list all occurrences of any of the strings

		ArrayList<String> foundList = new ArrayList<String>();
		iter = codeList.iterator();
		while(iter.hasNext()) {	// parse through the code
			String strCode = (String) iter.next();
			if (isAnyStringFound(bCaseSensitive, strCode, findList))
			foundList.add(strCode.trim());
		}

// list occurrences

		if (foundList.size() > 0) {
			LogHelper.info(foundList.size()+" occurance(s) in file "+
					file.getPath()+"\n");
			addMessage(foundList.size()+" occurance(s) in file "+
					file.getPath()+"\n");
			Iterator<String> iterator = foundList.iterator();
			while (iterator.hasNext()) {
				line = (String) iterator.next();
				addMessage ("\t "+line.trim());				
			}
			addMessage("");
		}
//		LogHelper.info("<<< doFile; "+file.getPath());		
		return;
	}
	private boolean isStringFound(final boolean bCaseSensitive,
						final String strCode, final String strFind) {
		if (bCaseSensitive) {
			if (strCode.indexOf(strFind) > -1) return true;
		}
		else {
			if (strCode.toLowerCase().indexOf(strFind.toLowerCase()) > -1)
				return true;
		}
		return false;
	}
	private boolean isAnyStringFound (final boolean bCaseSensitive,
						final String strCode, final ArrayList<String> findList) {
		Iterator<String> iter = findList.iterator();
		while(iter.hasNext()) {
			String strFind = (String) iter.next();
			if (isStringFound(bCaseSensitive, strCode, strFind)) return true;
		}
		return false;
	}
	public class GrepdirFilter implements FilenameFilter {
		private String mask = "";
		private String first = "";
		private String second = "";
		public boolean accept (File dir, String name) {
//			LogHelper.info(">>> GrepdirFilter::accept");				
//			LogHelper.info("(Filter) name :"+name+":");
//			LogHelper.info("m :"+mask+": 1 :"+first+": 2 :"+second+":");
			if (first.length() > 0) {
				if (! name.startsWith(first)) return false;
			}
			if (second.length() > 0) {
				if (! name.endsWith(second)) return false;
			}
			if (mask.length() > 0) {
				if (! mask.equals(name)) return false;
			}
//			LogHelper.info("<<< GrepdirFilter::accept - true");						
			return true;
		}
		public void setMask (String mask) {
			int star = mask.indexOf("*");
			if (star < 0) {
				this.mask = mask;				
				return;
			}
			first = mask.substring(0,star);
			second = mask.substring(star+1,mask.length());
		}
	}
	public class GrepdirMultipleMasksFilter implements FilenameFilter {
		private ArrayList<String> masks = new ArrayList<String>();
		private ArrayList<String> first = new ArrayList<String>();
		private ArrayList<String> second = new ArrayList<String>();

		public boolean accept (File dir, String name) {
//			System.out.println(">>> GrepdirMultipleMasksFilter::accept");
//			System.out.println("(Filter) dir.getPath() :"+dir.getPath()+":");
//			System.out.println("(Filter) name :"+name+":");
/*
			for (String s1 : masks) {
				System.out.println("s1 :"+s1+":");
			}
			for (String s2 : first) {
				System.out.println("s2 :"+s2+":");
			}
			for (String s3 : second) {
				System.out.println("s3 :"+s3+":");
			}
*/

			for (String s1 : masks) {
//				System.out.println("s1 :"+s1+":");
				if (s1.equals(name)) return true;
			}
			for (int i = 0; i < first.size(); i++) {
//				System.out.println("i "+i+" first.get(i) "+first.get(i)+" second.get(i) "+second.get(i));
				if (name.startsWith(first.get(i)) && name.endsWith(second.get(i))) return true;
			}
//			System.out.println("<<< GrepdirMultipleMasksFilter::accept - false");						
			return false;
		}
		public void setMask (String str) {
//			System.out.println(">>> GrepdirMultipleMasksFilter::setMask; str :"+str+":");
			String[] parts = str.split(",");
//			System.out.println("parts.length "+parts.length);
			for (String s : parts) {
				String item = s.trim();
//				System.out.println("item :"+item+":");
				int star = item.indexOf("*");		// if no *, then use the whole string
				if (star < 0) {
					masks.add(item);
				}
				else {
//					System.out.println("star "+star+" item :"+item+":");
					first.add(item.substring(0,star));	// if * found, split on first * only
					second.add(item.substring(star+1,item.length()));
				}
			}
//			System.out.println("<<< GrepdirMultipleMasksFilter::setMask");
		}
	}
}
