package com.idc.wordgame;

import java.io.*;
import java.util.*;

public class Dictionary {
	private final static String m_strDictname = "Unabr.dict";
	private App m_app;
	private List<String> m_dict = new ArrayList<String>();

	public Dictionary(App app) {m_app = app;}
	private boolean isSearchStopped() {
		return m_app.getAppThread().getStopStatus();
	}
	public boolean setupDictionary() {
		BufferedReader bf;
		String strText;
		try {
			bf = new BufferedReader(new FileReader(m_strDictname));
			while ((strText = bf.readLine()) != null) {
				m_dict.add(strText.toLowerCase());
			}
			bf.close();
		}
		catch (IOException ex) {
			return false;
		}
		return true;
	}
	public void doWordSearch(String strLetters) {
		String strDictWord;
		int nTotal = 0;
		m_app.setStatusMessage("Searching...");
		m_app.initProgressBar(0,m_dict.size());
		m_app.addWord("");
		m_app.addWord("Letters in word "+strLetters);
		m_app.addWord("");
		for (int i=0; i<m_dict.size(); i++) {
			strDictWord = ((String)m_dict.get(i));
			m_app.setProgressBar(i);
			if (checkWord (strLetters, strDictWord)) {
				m_app.addWord(strDictWord);
				nTotal++;
			}
			if (isSearchStopped()) {
				m_app.addWord("----------------------------------");
				m_app.addWord("Search stopped by user");
				m_app.addWord("----------------------------------");
				m_app.addWord(nTotal+" words were found");
				m_app.addWord("----------------------------------");
				m_app.setStatusMessage("Stopped...");
				return;
			}
		}
		m_app.addWord("----------------------------------");
		m_app.addWord(nTotal+" words were found");
		m_app.addWord("----------------------------------");
		m_app.setStatusMessage("Finished...");
	}
	private boolean checkWord (String strLetters, String strDictWord) {
		LetterSlave lsd, lsl;
		boolean bFound = false;

		List<LetterSlave> listLetters = makeLetters (strLetters);
		List<LetterSlave> dictWord = makeLetters (strDictWord);
		for (int i=0; i<dictWord.size(); i++) { //for each letter in dict word
			bFound = false;
			lsd = dictWord.get(i);
			for (int j=0; j<listLetters.size(); j++) {
				lsl = listLetters.get(j);
				if ((lsd.getInt() == lsl.getInt()) && (! lsl.getBool())) {
					lsl.setBool(true);
					bFound = true;
					break;
				}
			}
			if (! bFound) return false;
		}
		return true;
	}
	private List<LetterSlave> makeLetters (String strLetters) {
		List<LetterSlave> list = new ArrayList<LetterSlave>();
		StringBuffer sb = new StringBuffer(strLetters);
		for (int i=0; i<sb.length(); i++) {
			list.add (new LetterSlave(sb.charAt(i)));
		}
		return list;
	}
	private class LetterSlave {
//		private char m_ch;
		private int m_int;
		private boolean m_bool;
		public LetterSlave(char ch) {
//			m_ch = ch;
			m_int = (int) ch;
			m_bool = false;
		}
		public void setBool(boolean bool) {m_bool = bool;}
		public boolean getBool() {return m_bool;}
		public int getInt() {return m_int;}
	}
}

