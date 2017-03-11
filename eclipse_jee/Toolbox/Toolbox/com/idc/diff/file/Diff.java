package com.idc.diff.file;

import java.io.IOException;

import com.idc.file.exec.OutputLine;

public class Diff {

	private static final int UNREAL=Integer.MAX_VALUE;
	private FileInfo m_oldInfo, m_newInfo;
	private PrintOut m_printOut;
	private int m_nBlocklen;

	/** m_blocklen is the info about found blocks. It will be set to 0, except
	 * at the line#s where blocks start in the old file. At these places it
	 * will be set to the # of lines in the block. During printout ,
	 * this # will be reset to -1 if the block is!  printed as a MOVE block
	 * (because the printout phase will encounter the block twice, but
	 * must only print it once.)
	 * The array declarations are to MAXLINECOUNT+2 so that we can have two
	 * extra lines (pseudolines) at line# 0 and line# MAXLINECOUNT+1
	 * (or less).
	 */
	private int m_blocklen[];

	private OutputLine m_printline;
	public void printLine(String msg) {m_printline.println(msg);}
	public Diff(OutputLine printline) {m_printline = printline;}

	public boolean doDiff(String oldFile, String newFile) {
		m_oldInfo = new FileInfo(oldFile);
		m_newInfo = new FileInfo(newFile);
		m_printOut = new PrintOut (this);
		printLine( ">>>> Difference of file \"" + oldFile + 
			"\" \n and file \"" + newFile + "\".\n");
		try {
//			Node.traverseTree("Before first scan");		// debugging
			m_oldInfo.inputScan(true);
//			Node.traverseTree("Before second scan");	// debugging
			m_newInfo.inputScan(false);
//			Node.traverseTree("After second scan");		// debugging
		} catch (IOException e) {
			System.err.println("Read error: " + e);
			return false;
		}
		m_nBlocklen = m_oldInfo.getMaxLine();
		if (m_newInfo.getMaxLine() > m_nBlocklen) m_nBlocklen = m_newInfo.getMaxLine();
		m_nBlocklen += 2;
		m_blocklen = new int[m_nBlocklen];
		m_oldInfo.initAlloc();
		m_newInfo.initAlloc();
		transForm();
//		showBlocklen("after transform()");	// debugging
//		m_oldInfo.showOther("old file");	// debugging
//		m_newInfo.showOther("new file");	// debugging
		return m_printOut.printOut();
	}
	public FileInfo getOldInfo() {return m_oldInfo;}
	public FileInfo getNewInfo() {return m_newInfo;}
	public int getBlocklen(int num) {return m_blocklen[num];}
	public void setBlocklen(int num, int value) {m_blocklen[num] = value;}
	/*
	private void showBlocklen(String msg) {
		System.out.println(">>> showBlocklen(); "+msg);
		for (int i=0; i<m_nBlocklen; i++) {
			System.out.println("(i,value) ("+i+","+m_blocklen[i]+")");
		}
		System.out.println("<<< showBlocklen()");
	}
	*/

	/*
	 * transform	
	 * Analyzes the file differences and leaves its findings in
	 * the global arrays m_oldInfo.other, m_newInfo.other, and m_blocklen.
	 * Expects both files in symtab.
	 * Expects valid "maxLine" and "symbol" in m_oldInfo and m_newInfo.
	 */
	private void transForm() {				  
		int oldline, newline;
		int oldmax = m_oldInfo.getMaxLine() + 2;  /* Count pseudolines at  */
		int newmax = m_newInfo.getMaxLine() + 2;  /* ..front and rear of file */

		for (oldline=0; oldline < oldmax; oldline++)
			m_oldInfo.setOther(oldline ,-1);
		for (newline=0; newline < newmax; newline++)
			m_newInfo.setOther(newline, -1);

		scanUnique();  /* scan for lines used once in both files */
		scanAfter();   /* scan past sure-matches for non-unique blocks */
		scanBefore();  /* scan backwards from sure-matches */
		scanBlocks();  /* find the fronts and lengths of blocks */
	}

	/*
	 * scanunique
	 * Scans for lines which are used exactly once in each file.
	 * Expects both files in symtab, and m_oldInfo and m_newInfo valid.
	 * The appropriate "other" array entries are set to the line# in
	 * the other file.
	 * Claims pseudo-lines at 0 and XXXinfo.maxLine+1 are unique.
	 */
	private void scanUnique() {
		int oldline, newline;
		Node lNode;

		for (newline = 1; newline <= m_newInfo.getMaxLine(); newline++) {
			lNode = m_newInfo.getSymbol(newline);
			if (lNode.symbolIsUnique()) {	// 1 use in each file
				oldline = lNode.getLinenum();
				m_newInfo.setOther(newline, oldline); // record 1-1 map
				m_oldInfo.setOther(oldline, newline);
			}
		}
		m_newInfo.setOther(0, 0);
		m_oldInfo.setOther(0, 0);
		m_newInfo.setOther(m_newInfo.getMaxLine() + 1, m_oldInfo.getMaxLine() + 1);
		m_oldInfo.setOther(m_oldInfo.getMaxLine() + 1, m_newInfo.getMaxLine() + 1);
	}

	/*
	 * scanafter
	 * Expects both files in symtab, and m_oldInfo and m_newInfo valid.
	 * Expects the "other" arrays contain positive #s to indicate
	 * lines that are unique in both files.
	 * For each such pair of places, scans past in each file.
	 * Contiguous groups of lines that match non-uniquely are
	 * taken to be good-enough matches, and so marked in "other".
	 * Assumes each other[0] is 0.
	 */
	private void scanAfter() {
		int oldline, newline;
		for (newline = 0; newline <= m_newInfo.getMaxLine(); newline++) {
			oldline = m_newInfo.getOther(newline);
			if (oldline >= 0) {			/* is unique in old &amp; new */
				for ( ; ;) {			/* scan after there in both files */
					if (++oldline > m_oldInfo.getMaxLine()) break; 
					if (m_oldInfo.getOther(oldline) >= 0) break;
					if (++newline > m_newInfo.getMaxLine()   ) break; 
					if (m_newInfo.getOther(newline) >= 0) break;
					if (m_newInfo.getSymbol(newline) !=
						m_oldInfo.getSymbol(oldline)) break;  // not same
					m_newInfo.setOther(newline, oldline); // record a match
					m_oldInfo.setOther(oldline, newline);
				}
			}
		}
	}

	/**
	 * scanbefore
	 * As scanafter, except scans towards file fronts.
	 * Assumes the off-end lines have been marked as a match.
	 */
	private void scanBefore() {
		int oldline, newline;
		for (newline = m_newInfo.getMaxLine() + 1; newline > 0; newline--) {
			oldline = m_newInfo.getOther(newline);
			if (oldline >= 0) {		   /* unique in each */
				for( ; ;) {
					if (--oldline <= 0) break;
					if (m_oldInfo.getOther(oldline) >= 0) break;
					if (--newline <= 0) break;
					if (m_newInfo.getOther(newline) >= 0) break;
					if (m_newInfo.getSymbol(newline) !=
						m_oldInfo.getSymbol(oldline)) break;  // not same
					m_newInfo.setOther(newline, oldline); // record a match
					m_oldInfo.setOther(oldline, newline);
				}
			}
		}
	}

	/**
	 * scanblocks - Finds the beginnings and lengths of blocks of matches.
	 * Sets the m_blocklen array (see definition).
	 * Expects m_oldInfo valid.
	 */
	private void scanBlocks() {
		int oldline, newline;
		int oldfront = 0;	  // line# of front of a block in old, or 0 
		int newlast = -1;	  // newline's value during prev. iteration

		for (oldline = 1; oldline <= m_oldInfo.getMaxLine(); oldline++)
			m_blocklen[oldline] = 0;
		m_blocklen[m_oldInfo.getMaxLine() + 1] = UNREAL; // starts a mythical blk
		for (oldline = 1; oldline <= m_oldInfo.getMaxLine(); oldline++) {
			newline = m_oldInfo.getOther(oldline);
			if (newline < 0)
				oldfront = 0;  /* no match: not in block */
			else {				   /* match. */
				if (oldfront == 0) oldfront = oldline;
				if (newline != (newlast+1)) oldfront = oldline;
				++m_blocklen[oldfront];		
			}
			newlast = newline;
		}
	}
}
