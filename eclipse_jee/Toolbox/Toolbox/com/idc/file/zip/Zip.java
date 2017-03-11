package com.idc.file.zip;

import java.io.*;
import java.util.zip.*;
import java.util.Enumeration;

public class Zip {
	private String m_strZipfile;
	private static final int DATA_BLOCK_SIZE=1024;
	public Zip(String[] args) {
		if (args.length < 1) {
			myMsg("Usage: Zip zipfile");
			System.exit(0);
		}
		m_strZipfile = args[0];
		myMsg("Passed Zip File :"+m_strZipfile);
	}
	public static void main (String[] args) {
		Zip zip = new Zip (args);
		zip.doList();
		zip.doGet();
		zip.doCreate();
	}
	private void doList() {
		try {
			ZipFile zf = new ZipFile(m_strZipfile);
			for (Enumeration<? extends ZipEntry> entries = zf.entries(); entries.hasMoreElements();) {
            	String zipEntryName = ((ZipEntry) entries.nextElement()).getName();
				myMsg("Zipfile entry "+zipEntryName);
			}
			zf.close();
		}
		catch (IOException e) {
			myMsg("Trouble "+e.getMessage());
		}
	}
	private void doGet() {
		BufferedOutputStream bufOut;
		FileOutputStream fos;
		ZipInputStream zipInput;
		ZipEntry zipEntry;
		byte[] data;
		int byteCount;
		try {
			zipInput = new ZipInputStream (new FileInputStream(m_strZipfile));
			while ((zipEntry = zipInput.getNextEntry()) != null) {
				fos = new FileOutputStream("tmp/"+zipEntry.getName());
				myMsg("File "+zipEntry.getName());
				bufOut = new BufferedOutputStream(fos, DATA_BLOCK_SIZE);
				data = new byte[DATA_BLOCK_SIZE];
        		while ((byteCount = zipInput.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
					bufOut.write(data, 0, byteCount);
				}
				bufOut.flush();
				bufOut.close();
			}
			zipInput.close();
		}
		catch (IOException e) {
			myMsg("Trouble; "+e.getMessage());
		}
	}
	private void doCreate() {
		try {
			FileOutputStream fos = new FileOutputStream("/tmp/test.zip");
			ZipOutputStream zipOutput = new ZipOutputStream(fos);
			zipOutput.setMethod(ZipOutputStream.DEFLATED);
			doCreateDirectory(zipOutput, new File(System.getProperty("user.dir")));
			zipOutput.close();
		}
		catch (IOException e) {
			myMsg("Trouble; "+e.getMessage());
		}
	}
	private void doCreateDirectory(ZipOutputStream zipOutput, File dir) {
		File [] allFiles = dir.listFiles();
		File file;
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isFile())
				doCreateFile (zipOutput, file);
			else
				doCreateDirectory (zipOutput, file);
		}
	}
	private void doCreateFile(ZipOutputStream zipOutput, File file) {
		int byteCount;
		try {
			String dataFile = file.getPath();
			myMsg("Adding file "+dataFile);
			FileInputStream fis = new FileInputStream(dataFile);
			BufferedInputStream bufIn = new BufferedInputStream(fis);
			ZipEntry zipEntry = new ZipEntry(dataFile);
			zipOutput.putNextEntry(zipEntry);
			byte [] data = new byte[DATA_BLOCK_SIZE];
			while ((byteCount = bufIn.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
			    zipOutput.write(data, 0, byteCount);
			}
			zipOutput.flush();
			zipOutput.closeEntry();
			bufIn.close();
		}
		catch (IOException e) {
			myMsg("Trouble; "+e.getMessage());
		}
	}
	private void myMsg (String msg) {System.out.println(msg);}
}

