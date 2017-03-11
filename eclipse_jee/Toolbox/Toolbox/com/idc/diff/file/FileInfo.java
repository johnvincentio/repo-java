package com.idc.diff.file;

import java.io.*;

public class FileInfo {                        /** This is the info kept per-file.     */
	private static final int MAXLINECOUNT = 20000;
	private String m_strFile;
	private BufferedReader m_file;        /* File handle that is open for read.  */
	private int m_maxLine;        /* After input done, # lines in file.  */
	private Node m_symbol[]; /* The symtab handle of each line. */
	private int m_other[]; /* Map of line# to line# in other file */
	/* ( -1 means don't-know ).            */

	public FileInfo(String filename) {
//		debug.info(">>> FileInfo::FileInfo; filename "+filename);
		m_strFile = filename;
		m_symbol = new Node [MAXLINECOUNT+2];
		m_other  = null;
		m_maxLine = 0;
//	debug.info("<<< FileInfo::FileInfo");
	}

	public void inputScan(boolean bOld) throws IOException {
//	debug.info(">>> FileInfo:inputScan()");
		String sbuf;
		try {
			m_file = new BufferedReader(
					new FileReader (new File(m_strFile)));
			while ((sbuf = m_file.readLine()) != null) {
				int linenum = ++m_maxLine;
				if (linenum > MAXLINECOUNT) {
					System.err.println("MAXLINECOUNT exceeded, must stop.");
					System.exit(1);
				}
				m_symbol[linenum] = Node.addSymbol(sbuf, bOld, linenum);
			}
			m_file.close();
		} catch (IOException e) {
			System.err.println("Diff can't read file " + m_strFile);
			System.err.println("Error Exception was:" + e);
			System.exit(1);
		}
//		debug.info("<<< FileInfo:inputScan()");
	}
	public void initAlloc() {m_other  = new int[m_symbol.length + 2];}
	public int getMaxLine() {return m_maxLine;}
	public Node getSymbol (int num) {return m_symbol[num];}
	public int getOther (int num) {return m_other[num];}
	public void setOther (int num, int value) {m_other[num] = value;}
	public void showOther(String msg) {
		System.out.println(">>> showOther(): "+msg);
		for (int i=0; i<m_maxLine + 2; i++) {
			System.out.println("(i,value) ("+i+","+m_other[i]+")");
		}
		System.out.println("<<< showOther()");
	}
}
