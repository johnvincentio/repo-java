package com.idc.knight;

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
import java.net.URL;

public class JVFile {
	private static final int DATA_BLOCK_SIZE=1024;

	public static StringBuffer readFileFromClasspath (Class<?> classRef, String path) {
//		System.out.println (">>> JVFile::readFileFromClasspath; path "+path);
		URL url = classRef.getClass().getClass().getResource(path);
		System.out.println ("url "+url.getPath ());
		try {
			File file = new File(url.toURI());
			return readFile (file.getPath ());
		}
		catch (Exception ex) {
			System.out.println ("Exception; "+ex.getMessage ());
		}
		return null;
	}

	public static StringBuffer readFile (String fileName) {
		File file = new File(fileName);
		StringBuffer sb = new StringBuffer();
		
		try	{
			if (! file.exists()) return null;
			
			BufferedReader br = new BufferedReader (new FileReader (file));
			try	{
				String lineSep = "\n";
				String nextLine;			
				while ((nextLine = br.readLine()) != null) {
					sb.append(nextLine);
					sb.append(lineSep);
				}
			} 
			finally {
				br.close();
			}
		} 
		catch (IOException ioe)	{
			return null;
		}
		return sb;
	}

	public static boolean copyFile (String strFromFile, String strToFile) {
//		System.out.println("Copying file "+strFromFile+" to file "+strToFile);
		int byteCount;
		try {
			FileInputStream fis = new FileInputStream (strFromFile);
			BufferedInputStream bufIn = new BufferedInputStream (fis, DATA_BLOCK_SIZE);

			FileOutputStream fos = new FileOutputStream (strToFile);
			BufferedOutputStream bufOut = new BufferedOutputStream (fos, DATA_BLOCK_SIZE);

			byte[] data = new byte[1024];
			while ((byteCount = bufIn.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
				bufOut.write (data, 0, byteCount);
			}
			bufIn.close();
			bufOut.flush();
			bufOut.close();
		}
		catch (IOException ex) {
			System.out.println("Unable to copy the file; "+ex.getMessage());
			return false;
		}
		return true;
	}
	public static boolean writeFile (String strText, String strFile) {
		return writeFile (strText, new File (strFile));
	}
	public static boolean writeFile (String strText, File file) {
		try {
			PrintWriter pw = new PrintWriter (new BufferedWriter (new FileWriter(file)));
			pw.print(strText);
			pw.flush();
			pw.close();
			return true;
		}
		catch (IOException ex) {
			System.out.println("Unable to write the file; "+ex.getMessage());
			return false;
		}
	}
	public static void removeFile (String strFile) {removeFile(new File(strFile));}
	public static void removeFile (File file) {
		System.out.println("Removing file "+file.getPath());
		if (file.isFile()) file.delete();
	}
	public static long getFileLineCount (String fileName) {
		File file = new File(fileName);
		long count = 0;
		
		try	{
			if (! file.exists()) return count;
			
			BufferedReader br = new BufferedReader (new FileReader (file));
			try	{
				while ((br.readLine()) != null) {
					count++;
				}
			} 
			finally {
				br.close();
			}
		} 
		catch (IOException ioe)	{}
		return count;
	}
	public static String getName (String strName) {
		if (strName == null || strName.length() < 1) return "";
		int pos = strName.lastIndexOf('.');
		if (pos < 0) return strName;
		return strName.substring(0,pos);
	}
	public static String getExtension (String strName) {
		if (strName == null || strName.length() < 1) return "";
		int pos = strName.lastIndexOf('.');
		if (pos < 0) return "";
		return strName.substring(pos+1);
	}

	public static boolean makeDirectories (File file) {
//		System.out.println("--- JVFile::makeDirectories; file :"+file.getPath()+":");
		file = new File (file.getPath());
		return file.getParentFile().mkdirs();
//		System.out.println("<<< makeDirectories; bool "+bool);
	}
	public static boolean makeFullDirectories (File file) {
//		System.out.println("--- JVFile::makeFullDirectories; file :"+file.getPath()+":");
		file = new File (file.getPath());
		return file.mkdirs();
//		System.out.println("<<< makeDirectories; bool "+bool);
	}

	public static File makeWorkingDirectory (File file) {
		String tmpDir = Long.toString (System.currentTimeMillis());
		return new File (file.getPath() + File.separatorChar + tmpDir);
	}

	public static File makeWorkingFile (File baseDirectory) {
		String tmpFile = Long.toString (System.currentTimeMillis());
		return new File (baseDirectory.getPath() + File.separatorChar + tmpFile + ".txt");
	}

	public static String getCwd() {
		return System.getProperty ("user.dir");
	}
	public static File createCwdSubDirectory (String... subdirs) {
		String dir = getCwd();
		for (String s : subdirs) {
			dir += File.separatorChar + s;
		}
		return new File (dir);
	}
	public static File createSolutionsSubDirectory (String... subdirs) {
		String dir = "";
		for (String s : subdirs) {
			dir += File.separatorChar + s;
		}
		return new File (dir);
	}
}
