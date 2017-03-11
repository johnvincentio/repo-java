package com.idc.ant.task;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.idc.file.exec.ExecuteCommand;
import com.idc.file.exec.OutputLine;
import com.idc.file.exec.PrintFile;

public class CompareDirectory extends Task {
	private static boolean bIgnoreWhiteSpace = false;

	private File m_outputFile;
	private File m_fromDir;
	private File m_toDir;

	public void setOutputFile (File file) {
		System.out.println(">>> setOutputFile; file :" + file.getPath());
		m_outputFile = file;
		if (file.exists())
			throw new BuildException("CompareDirectory; setOutputFile " + file + " already exists");
		System.out.println("<<< setOutputFile");
	}

	public void setFromDir (File file) {
		System.out.println(">>> setFromDir; file :" + file.getPath());
		m_fromDir = file;
		if (! file.exists())
			throw new BuildException("CompareDirectory; setFromDir " + file + " doesn\'t exist");
		System.out.println("<<< setFromDir");
	}

	public void setToDir (File file) {
		System.out.println(">>> setToDir; file :" + file.getPath());
		m_toDir = file;
		if (! file.exists())
			throw new BuildException("CompareDirectory; setToDir " + file + " doesn\'t exist");
		System.out.println("<<< setToDir");
	}

	public void execute() throws BuildException {
		System.out.println(">>> execute");
		System.out.println ("m_fromDir "+m_fromDir);
		System.out.println ("m_toDir "+m_toDir);
		PrintFile printFile = new PrintFile (m_outputFile);

		try {
			doDirectory (m_fromDir, printFile);
//			doTest();
		}
		catch (Exception ex) {
			throw new BuildException (ex);
		}
		System.out.println("<<< execute");
	}

	private void doDirectory (File currentDir, PrintFile printFile) throws Exception {
		File[] allFiles = currentDir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			File file = allFiles[i];
			if (file.isDirectory())
				doDirectory (file, printFile);
			else if (file.isFile()) {
				File compareFile = makeWriteFile (file);
//				printFile.println ("Comparing:");
//				printFile.println (file.getPath());
//				printFile.println (compareFile.getPath());
				if (compareFile.exists()) 
					doCompare (file, compareFile, bIgnoreWhiteSpace, printFile);
				else {
					printFile.println ("File "+compareFile.getPath()+" not found");
				}
			}
		}
	}

	private File makeWriteFile (File currentFile) {
		String s1 = currentFile.getPath();
		int f1 = s1.indexOf(m_fromDir.getPath());
		if (f1 < 0) return null;
		String s2 = m_toDir + "/" + s1.substring(f1 + m_fromDir.getPath().length());
//		System.out.println("s2 :"+s2+":");
		return new File (s2);
	}

	private void doCompare (File baseFile, File newFile, boolean bIgnoreWhiteSpace, OutputLine outputLine) {
//		System.out.println(">>> doCompare; baseFile :"+baseFile+": newFile :"+newFile+":");		
		ExecuteCommand exec = new ExecuteCommand();
		if (bIgnoreWhiteSpace) {
			String[] strCmd = { "fc", "/W", "/N", baseFile.getPath(), newFile.getPath() };
			exec.executeCommand(strCmd, outputLine);
		} else {
			String[] strCmd = { "fc", "/N", baseFile.getPath(), newFile.getPath() };
			exec.executeCommand(strCmd, outputLine);
		}
		exec = null;
//		System.out.println("<<< doCompare");
	}
}
