
package com.idc.api;

import java.io.File;
import java.util.Iterator;
import com.idc.trace.JVOutputFile;

public class Control {
	private static final String OUTFILE1 = "c:\\temp\\jv1.txt";
	private static final String OUTFILE2 = "c:\\temp\\jv2.txt";
//	private static final String NEWLINE = "\n";
	DirInfo m_dirInfo = new DirInfo();

	private void setFile (String s) {JVOutputFile.getInstance().setFile (s, false);}
	private void closeFile() {JVOutputFile.getInstance().close();}
	private void output (String s) {JVOutputFile.getInstance().println(s);}

// get directory name to parse
// recursively look for directories
// add directory names to Info
// parse through the list
// create output file
// for each type of directory, write ant code
// close input and output files
// done

	public void doit(String topDir) {
		parseDir (new File(topDir));
		m_dirInfo.sort();
//		System.out.println("dirInfo :"+m_dirInfo.toString());
		setFile (OUTFILE1);
		makeOutput1();
		makeOutput2();
		closeFile();
		setFile (OUTFILE2);
		makeOutput3();
		closeFile();
	}
	private void parseDir (File dir) {
		DirItemInfo dirItemInfo;
		File[] allFiles = dir.listFiles();
		for (int i=0; i<allFiles.length; i++) {
			File file = allFiles[i];
			if (! file.isDirectory()) continue;
//			System.out.println("dir :"+file.getAbsolutePath());
			dirItemInfo = new DirItemInfo(file);
			if (dirItemInfo.isSource()) {
				m_dirInfo.add (dirItemInfo);
				continue;
			}
			if (dirItemInfo.isParser()) parseDir (file);
		}
	}
	private void makeOutput1() {
		StringBuffer buf = new StringBuffer();
		buf.append("\t<target name=\"all\" depends=\"javadocs_clean");
		DirItemInfo dirItemInfo;
		Iterator<DirItemInfo> iter = m_dirInfo.getItems();
		while (iter.hasNext()) {
			dirItemInfo = (DirItemInfo) iter.next();
			if (! dirItemInfo.isSource()) continue;
			buf.append(", ").append(dirItemInfo.getName());
		}
		buf.append("\"/>");
		output (buf.toString());
		output ("");
//		System.out.println(buf.toString());
	}
	private void makeOutput2() {
		StringBuffer buf;
		DirItemInfo dirItemInfo;
		Iterator<DirItemInfo> iter = m_dirInfo.getItems();
		while (iter.hasNext()) {
			dirItemInfo = (DirItemInfo) iter.next();
			if (! dirItemInfo.isSource()) continue;
			buf = new StringBuffer();
			buf.append("\t<target name=\"");
			buf.append(dirItemInfo.getName());
			buf.append("\">");
			output (buf.toString());
			output ("\t\t<antcall target=\"javadocs\">");
			output ("\t\t\t<param name=\"my.javadocs.app\" value=\""+dirItemInfo.getName()+"\"/>");
			output ("\t\t\t<param name=\"my.javadocs.dir\" value=\""+dirItemInfo.getAntPath()+"\"/>");
			output ("\t\t</antcall>");
			output ("\t</target>");
		}
	}
	private void makeOutput3() {
		DirItemInfo dirItemInfo;
		Iterator<DirItemInfo> iter = m_dirInfo.getItems();
		while (iter.hasNext()) {
			dirItemInfo = (DirItemInfo) iter.next();
			if (! dirItemInfo.isSource()) continue;
			output ("\t\t<li><a href=\""+dirItemInfo.getName()+"/index.html\">"+dirItemInfo.getName()+"</a></li>");
		}
	}
}
