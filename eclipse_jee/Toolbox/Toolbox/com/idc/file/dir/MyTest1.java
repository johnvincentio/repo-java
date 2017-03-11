package com.idc.file.dir;

import java.io.*;
import java.util.*;

import com.idc.trace.JVOutputFile;

public class MyTest1 {
//	private static int indentLevel = -1;
	private JVOutputFile m_out;
	private String m_strDir;
//	private boolean m_bIndent;
//	private boolean m_bNewline;
	private LineInfo m_lineInfo = new LineInfo();

	public MyTest1 (String strDir, String outputFile, boolean bool1, boolean bool2) {
		m_strDir = strDir;
		m_out = JVOutputFile.getInstance();
		m_out.setFile (outputFile, false);
		m_out.println(strDir);
//		m_bIndent = bool1;
//		m_bNewline = bool2;
	}

	public void doit () {
		listPath (new File (m_strDir));
		m_lineInfo.sort();
		Iterator<LineItemInfo> iter = m_lineInfo.getItems();
		while (iter.hasNext()) {
			LineItemInfo lineItemInfo = (LineItemInfo) iter.next();
			m_out.println(lineItemInfo.getLine());
		}
		m_out.close();
	}
	private void listPath (File path) {
		File files[];		// list of files in a directory
//		indentLevel++;
		files = path.listFiles();
		Arrays.sort (files);
		for (int i=0, n=files.length; i<n; i++) {
			if (files[i].isDirectory())
				listPath (files[i]);
			else
				m_lineInfo.add(files[i].toString());
		}
//		indentLevel--;
	}
}
