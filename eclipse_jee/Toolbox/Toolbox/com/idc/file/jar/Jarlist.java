package com.idc.file.jar;

import java.io.File;
import java.io.FilenameFilter;

public class Jarlist {

	public static void main(String[] args) {
		String strFiles = "*.jar";
		String strCwd = System.getProperty("user.dir");
//		String strCwd = "C:\\Program Files\\IBM\\WebSphere Studio\\runtimes\\base_v5\\lib";			
		Jarlist app = new Jarlist();
//		System.out.println("Searching directories :"+strCwd+":");
		app.doGrepdir (strCwd, strFiles);
//		System.out.println("Search is complete");
	}
	private void doGrepdir (String strDir, String strFiles) {
//		System.out.println(">>> doGrepdir");
//		System.out.println("strFiles :"+strFiles+":");
//		System.out.println("strDir :"+strDir+":");
		doDirectory (strDir, strFiles);
//		System.out.println("<<< doGrepdir");		
	}
	private void doDirectory (String strDir, String strFiles) {
		File file = new File(strDir);		
		doDirectory (file, strFiles);
	}	
	private void doDirectory (File dir, String strFiles) {
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
				doFile (file);
			}
		}

// handle directories

		allFiles = dir.listFiles();
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) {
//				System.out.println("(Directory) i = "+i+" file :"+file.getPath()+":");				
				doDirectory (file, strFiles);
			}
		}
//		System.out.println("<<< doDirectory");
	}
	private void doFile (File file) {
//		System.out.println(">>> doFile; "+file.getPath());
		if (! file.isFile()) return;
		System.out.println();
		System.out.println(">>> Library "+file.getPath());	
		Jar jar = new Jar();
		jar.doList(file.getPath());
		System.out.println("<<< Library "+file.getPath());	

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
