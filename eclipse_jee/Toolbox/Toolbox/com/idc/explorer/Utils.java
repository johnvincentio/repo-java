package com.idc.explorer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.idc.trace.LogHelper;

public class Utils {
	public static final char OS_SEP = '\\';
	public static final char JAR_SEP = '/';

	public static File makeWorkingDirectory (File file) {
		String tmpDir = Long.toString (System.currentTimeMillis());
		return new File (file.getPath() + File.separatorChar + tmpDir);
	}
	public static File convertToOSName (String strName) {
		return new File (strName.replace (OS_SEP, JAR_SEP));
	}

	public static void browserFile (final String strFile) {
		String[] strCmd = {ExplorerGUI.BROWSER, strFile};		
		LogHelper.info("strCmd :"+strCmd+":");
		try {
			Runtime.getRuntime().exec (strCmd);
		}
		catch (IOException e) {
			LogHelper.info ("cannot run command "+strCmd);
		}
	}

	public static void editFile (final String strFile) {
		String[] strCmd = {ExplorerGUI.EDITOR, strFile};		
		LogHelper.info("strCmd :"+strCmd+":");
		try {
			Runtime.getRuntime().exec (strCmd);
		}
		catch (IOException e) {
			LogHelper.info ("cannot run command "+strCmd);
		}
	}

	public static String getClassName (Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");
		return classString.substring (dotIndex+1);
	}

	public static StringBuffer readFile (File file) throws IOException {
		String lineSep = "\n";
		BufferedReader br = new BufferedReader (new FileReader (file));
		String nextLine;
		StringBuffer sb = new StringBuffer();
		while ((nextLine = br.readLine()) != null) {
			sb.append(nextLine);
			sb.append(lineSep);
		}
		br.close();
		return sb;
	}

	public static void writeFile (String strText, File file) throws IOException {
		PrintWriter pw = new PrintWriter (new BufferedWriter (new FileWriter (file)));
		pw.print (strText);
		pw.flush();
		pw.close();
	}

	private static final int DATA_BLOCK_SIZE=1024;

	public static boolean copyFile (File fromFile, File toFile) {
//		System.out.println("Copying file "+strFromFile+" to file "+strToFile);
		int byteCount;
		try {
			FileInputStream fis = new FileInputStream (fromFile);
			BufferedInputStream bufIn = new BufferedInputStream (fis, DATA_BLOCK_SIZE);

			FileOutputStream fos = new FileOutputStream (toFile);
			BufferedOutputStream bufOut = new BufferedOutputStream (fos, DATA_BLOCK_SIZE);

			byte[] data = new byte[1024];
			while ((byteCount = bufIn.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
				bufOut.write (data, 0, byteCount);
			}
			bufIn.close();
			bufOut.flush();
			bufOut.close();
			fis.close();
			fos.close();
		}
		catch (IOException ex) {
			System.out.println("Unable to copy the file; "+ex.getMessage());
			return false;
		}
		return true;
	}
}
