package com.idc.grepgui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/*
 * 
/Users/jv/Desktop/MyDevelopment/repo_hes/brackets/hes/src

*.js, *.html

wrap

 */

public class Noddy {
	public static void main(String[] args) {
		(new Noddy()).dotest();
	}
	private void dotest() {
		File dir = null;
		dir = new File("/Users/jv/Desktop/MyDevelopment/repo_hes/brackets/hes/src/js/views");
		dir = new File("/Users/jv/Desktop/MyDevelopment/repo_hes/brackets/hes/src/templates/templates");
		ArrayList<String> findList = new ArrayList<String>();
		findList.add("wrap");
		String strFiles = "";
		strFiles = "*.js, *.html";
		strFiles = "contactUs*.js, *.html";
		strFiles = "contactUsView.js, *.html";
		doDirectory(dir, findList, strFiles, false);
	}
	private void doDirectory (File dir, ArrayList<String> findList, String strFiles, boolean bCaseSensitive) {
		File [] allFiles;
		File file;
							
		System.out.println(">>> doDirectory; "+dir.getPath());
		if (! dir.isDirectory()) return;
		
// handle files
		
		GrepdirMultipleMasksFilter filter = new GrepdirMultipleMasksFilter();
		filter.setMask(strFiles);
		allFiles = dir.listFiles(filter);
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isFile()) {
				System.out.println("(File) i = "+i+" file :"+file.getPath()+":");
			}
		}
		System.out.println("<<< doDirectory");
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
