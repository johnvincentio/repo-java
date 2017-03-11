package com.idc.file.dump;

import java.io.*;

public abstract class GenericDump {
	private String m_strFile;
	private int m_max;
	public GenericDump (String s, int n) {m_strFile = s; m_max = n;}

	public boolean dump() {
		startMsg(m_strFile);
		try {
			int num = 1;
			File file = new File (m_strFile);
			RandomAccessFile raf = new RandomAccessFile (file,"r");
			for (long pos=0; pos<file.length(); pos++) {
				raf.seek(pos);
				write(getValue(raf.readByte()));
				if (++num > m_max) {
					newline();
					num = 1;
				}
			}
			raf.close();
		}
		catch (IOException ex) {return false;}
		newline();
		endMsg(m_strFile);
		return true;
	}
	protected abstract void write(String s);
	protected abstract String getValue(byte data);
	protected abstract void startMsg(String s);
	protected abstract void endMsg(String s);
	protected abstract void newline();
}

