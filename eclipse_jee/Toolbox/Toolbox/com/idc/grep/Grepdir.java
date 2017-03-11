package com.idc.grep;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;

public class Grepdir {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: grepdir String Files");
			System.exit(1);
		}
		String strFind = args[0];
		String strFiles = args[1];
		boolean bCaseSensitive = false;
		String strCwd = System.getProperty("user.dir");
//		String strCwd = "c:\\work3\\wrkspc";
//		String strCwd = "C:\\irac\\src\\iRACSrc3.0_INT\\iRACSources";				
		Grepdir grepdir = new Grepdir();
		System.out.println("Searching directories :"+strCwd+":");
		grepdir.doGrepdir (strCwd, strFind, strFiles, bCaseSensitive);
		System.out.println("Search is complete");
	}
	private void doGrepdir (String strDir, String strFind, String strFiles, boolean bCaseSensitive) {
//		System.out.println(">>> doGrepdir");
//		System.out.println("strFind :"+strFind+":");
//		System.out.println("strFiles :"+strFiles+":");
//		System.out.println("strDir :"+strDir+":");
		doDirectory (strDir, strFind, strFiles, bCaseSensitive);
//		System.out.println("<<< doGrepdir");		
	}
	private void doDirectory (String strDir, String strFind, String strFiles, boolean bCaseSensitive) {
		File file = new File(strDir);		
		doDirectory (file, strFind, strFiles, bCaseSensitive);
	}	
	private void doDirectory (File dir, String strFind, String strFiles, boolean bCaseSensitive) {
		File [] allFiles;
		File file;
				
//		System.out.println(">>> doDirectory; "+dir.getPath());
		if (! dir.isDirectory()) return;
		
// handle files
		
		GrepdirFilter filter = new GrepdirFilter();
		filter.setMask(strFiles);		
		allFiles = dir.listFiles(filter);
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isFile()) {
//				System.out.println("(File) i = "+i+" file :"+file.getPath()+":");				
				doFile (file, strFind, bCaseSensitive);
			}
		}

// handle directories

		allFiles = dir.listFiles();
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) {
//				System.out.println("(Directory) i = "+i+" file :"+file.getPath()+":");				
				doDirectory (file, strFind, strFiles, bCaseSensitive);
			}
		}
//		System.out.println("<<< doDirectory");
	}
	private void doFile (File file, String strFind, boolean bCaseSensitive) {
//		System.out.println(">>> doFile; "+file.getPath());
		if (! file.isFile()) return;
		
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader buf = null;
		String line;
		try {
			buf = new BufferedReader(new FileReader(file));
			while ((line = buf.readLine()) != null) {
				if (bCaseSensitive) {
					if (line.indexOf(strFind) > -1) list.add(line.trim());					
				}
				else {
					if (line.toLowerCase().indexOf(strFind.toLowerCase()) > -1)
						list.add(line.trim());
				}
			}
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
		if (list.size() > 0) {
			System.out.println(list.size()+" occurance(s) in file "+
					file.getPath()+"\n");
			Iterator<String> iterator = list.iterator();
			while (iterator.hasNext()) {
				line = (String) iterator.next();
				System.out.println ("\t "+line.trim());				
			}
			System.out.println("");
		}
//		System.out.println("<<< doFile; "+file.getPath());		
		return;
	}						

	public class GrepdirFilter implements FilenameFilter {
		private String mask = "";
		private String first = "";
		private String second = "";
		public boolean accept (File dir, String name) {
//			System.out.println(">>> GrepdirFilter::accept");				
//			System.out.println("(Filter) name :"+name+":");
//			System.out.println("m :"+mask+": 1 :"+first+": 2 :"+second+":");
			if (first.length() > 0) {
				if (! name.startsWith(first)) return false;
			}
			if (second.length() > 0) {
				if (! name.endsWith(second)) return false;
			}
			if (mask.length() > 0) {
				if (! mask.equals(name)) return false;
			}
//			System.out.println("<<< GrepdirFilter::accept - true");						
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
}

