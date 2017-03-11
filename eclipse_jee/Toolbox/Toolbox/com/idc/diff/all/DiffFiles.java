package com.idc.diff.all;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DiffFiles {
	private static final int DATA_BLOCK_SIZE = 1024;
	private byte[] newData = new byte[DATA_BLOCK_SIZE];
	private byte[] oldData = new byte[DATA_BLOCK_SIZE];

	public boolean compareFiles(String newFile, String oldFile) {	//true => files are the same
//		System.out.println(">>> DiffFiles.compareFiles; "+newFile+", "+oldFile);
		boolean bSame = false;		// assume files are different
		File file = new File (newFile);
		if (! file.isFile()) return bSame;	// ensure files exist
		file = new File (oldFile);
		if (! file.isFile()) return bSame;
		file = null;

		try {
//			System.out.println("Opening file "+newFile);
			FileInputStream newFis = new FileInputStream (newFile);
			BufferedInputStream newBuf = new BufferedInputStream (newFis, DATA_BLOCK_SIZE);
//			System.out.println("Opening file "+oldFile);
			FileInputStream oldFis = new FileInputStream (oldFile);
			BufferedInputStream oldBuf = new BufferedInputStream (oldFis, DATA_BLOCK_SIZE);

			while (true) {
				int newByteCount = newBuf.read (newData, 0, DATA_BLOCK_SIZE);
				int oldByteCount = oldBuf.read (oldData, 0, DATA_BLOCK_SIZE);
				if (newByteCount != oldByteCount) break;	// different
				if (newByteCount < 0) {	// no differences found and EOF
					bSame = true;
					break;
				}
				if (! compareByteArrays (newByteCount, newData, oldData))
					break;		// different data
			}
			newBuf.close(); oldBuf.close();
			newFis.close(); oldFis.close();
			newBuf = null;
			newFis = null;
			oldBuf = null;
			oldFis = null;
		}
		catch (IOException ex) {
			System.out.println("Unable to compare the files; "+ex.getMessage());
			bSame = false;
		}
//		System.out.println("<<< DiffFiles.compareFiles; bSame "+bSame);
		return bSame;
	}
	private boolean compareByteArrays (int count, byte[] arr1, byte[] arr2) {
		boolean bSame = true;
		for (int i=0; i<count; i++) {
			if (arr1[i] != arr2[i]) {
				bSame = false;
				break;
			}
		}
		return bSame;
	}
}
