package com.idc.file.jar;

import java.io.*;
import java.util.jar.*;

public class Jar {
	private static final int DATA_BLOCK_SIZE=1024;
	private static final char OS_SEP = '\\';
	private static final char JAR_SEP = '/';
	
	public Jar () {}
	public static void main(String[] args) {
		Jar jar = new Jar();
		jar.doWork(args);
	}
	private void doWork(String[] args) {
		String strOutputDir;
		if (args.length < 1) {
			myMsg("Usage: jar filename");
			myExit(0);
		}		
		doList(args[0]);
		if (args.length < 2 || args[1].length() < 1)
			strOutputDir = "c:\\temp\\jartest";		// default to something
		else	
			strOutputDir = args[1];				
		doHandleJar(args[0], strOutputDir);
	}
	private void doHandleJar(String strJarFile, String strOutputDir) {
		String strExt = getFileExtension (strJarFile);	// look for a JAR
		if (strExt == null) return;		//no extension		
		if ((! strExt.equalsIgnoreCase("jar")) && (! strExt.equalsIgnoreCase("war")) &&
					(! strExt.equalsIgnoreCase("ear"))) return;

		myMsg("Handle jar file "+strJarFile);							
		String strDir = strOutputDir + OS_SEP + strExt.toUpperCase() + "_" + 
				getFileName(strJarFile);
		doMakeOSDirectories(true, strDir, OS_SEP);	// create new output directory for this jar

		String strToFile = strDir + OS_SEP + getFileName(strJarFile);		
		doCopyFile (strJarFile, strToFile);	// copy jar file to new directory
				
		doUnjar(strToFile, strDir);
	}	
	private void doUnjar(String strJarFile, String strOutputDir) {	
		JarEntry jarEntry;
		String newFile;				
		try {
			myMsg("Opening JAR file "+strJarFile);
			myMsg("Output Directory "+strOutputDir);
			JarInputStream jarInput = new JarInputStream(
					new FileInputStream(strJarFile));
			while ((jarEntry = jarInput.getNextJarEntry()) != null) {
				myMsg("\nJar Entry; "+jarEntry.getName());
				newFile = strOutputDir + OS_SEP + convertJarToOSName(jarEntry.getName());
				doMakeOSDirectories(false, newFile, OS_SEP);
				if (jarEntry.isDirectory()) continue;	// ignore directory entries								
				doCreateFile (jarInput, newFile);		// create the file
				doHandleJar(newFile, strOutputDir);		// handle another jar	
			}
			myMsg("Closing JAR file "+strJarFile);					
			jarInput.close();
		}
		catch (IOException ex) {
			myMsg("Unable to Unjar the jar file; "+ex.getMessage());
			myExit(1);
		}
	}
	private void doCreateFile (JarInputStream jarInput, String strFile) {
		myMsg("Creating file "+strFile);
		int byteCount;		
		try {
			FileOutputStream fos = new FileOutputStream (strFile);
			BufferedOutputStream bufOut = new BufferedOutputStream (fos, DATA_BLOCK_SIZE);
			byte[] data = new byte[1024];
			while ((byteCount = jarInput.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
				bufOut.write(data, 0, byteCount);
			}
			bufOut.flush();
			bufOut.close();
		}
		catch (IOException ex) {
			myMsg("Unable to create the file; "+ex.getMessage());
			myExit(1);
		}		
	}
	private void doMakeOSDirectories (boolean bAllDirs, String strDir, char chSep) {
//		myMsg("Make Directories for path "+strDir);
		if (bAllDirs) {		
			if (strDir.trim().charAt(strDir.length()-1) != chSep)
				strDir += chSep;
		}
		String strSubdir = "";		
		int spos = 0;
		int epos = 0;
		while ((epos = strDir.indexOf(chSep, spos)) > -1) {
			if (strSubdir.length() > 0)
				strSubdir = strSubdir + chSep + strDir.substring(spos,epos);
			else
				strSubdir = strDir.substring(spos,epos);
			spos = epos + 1;		
			doMakeDirectory (strSubdir);
		}
	}
	private void doMakeDirectory (String strDir) {
		if (strDir != null && strDir.length() > 0) {
			File file = new File(strDir);
			if (! file.isDirectory()) {
				myMsg("Creating Directory "+strDir);
				file.mkdir();
			}
		}
	}		
	public void doList(String strJarFile) {
		JarEntry jarEntry;
		try {
			myMsg("Opening JAR file "+strJarFile);
			JarInputStream jarInput = new JarInputStream(
					new FileInputStream(strJarFile));
			while ((jarEntry = jarInput.getNextJarEntry()) != null) {
				myMsg("File "+jarEntry.getName());
			}
			myMsg("Closing JAR file "+strJarFile);					
			jarInput.close();
		}
		catch (IOException ex) {
			myMsg("Unable to read from the jar file; "+ex.getMessage());
			myExit(1);
		}		
	}
	private void doCopyFile (String strFromFile, String strToFile) {
		myMsg("Copying file "+strFromFile+" to file "+strToFile);
		int byteCount;		
		try {
			FileInputStream fis = new FileInputStream (strFromFile);
			BufferedInputStream bufIn = new BufferedInputStream (fis, DATA_BLOCK_SIZE);
						
			FileOutputStream fos = new FileOutputStream (strToFile);
			BufferedOutputStream bufOut = new BufferedOutputStream (fos, DATA_BLOCK_SIZE);
			byte[] data = new byte[1024];
			while ((byteCount = bufIn.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
				bufOut.write(data, 0, byteCount);
			}
			bufIn.close();
			bufOut.flush();
			bufOut.close();
		}
		catch (IOException ex) {
			myMsg("Unable to copy the file; "+ex.getMessage());
			myExit(1);
		}		
	}
	private String convertJarToOSName (String strName) {return strName.replace(JAR_SEP, OS_SEP);}	
	private String getFileName (String strName) {
		String strReturn;
		int num;
		if (strName == null || strName.length() < 1) return "";
		if ((num = strName.lastIndexOf(OS_SEP)) < 0)
			strReturn = strName;
		else
			strReturn = strName.substring(num+1, strName.length());
		return strReturn;
	}
	private String getFileExtension (String strName) {
		int num = strName.lastIndexOf(".");
		if (num < 0) return null;		//no extension
		return strName.substring(num+1,strName.length());
	}
	private void myMsg(String msg) {System.out.println(msg);}
	private void myExit(int num) {System.exit(num);}	
}
