package com.idc.file.shred;

import java.io.*;

public class Shred {
//	private static final int DATA_BLOCK_SIZE=1024;

	public static void main (String[] args) {
		Shred app = new Shred();
		app.makeZeroes (args[0]);
	}
	private void makeZeroes (String strFile) {
		try {
			File file = new File (strFile);
			long fileLen = file.length();
			System.out.println("length "+fileLen);
			RandomAccessFile raf = new RandomAccessFile (file, "rw");
			for (long pos = 0; pos < fileLen; pos++) {
				raf.seek(pos);
				raf.writeByte(0);
			}
			raf.close();
		}
		catch (IOException ex) {
			System.out.println("Unable to shred file "+strFile);
		}
	}
/*
	private void doMyDump (String s) {
		(new CharDump (s, 30)).dump();
		(new ByteDump (s, 30)).dump();
		(new OctalDump (s, 30)).dump();
		(new HexDump (s, 30)).dump();
	}
*/
}

