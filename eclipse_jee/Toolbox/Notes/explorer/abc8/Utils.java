package com.idc.explorer.abc8;

import java.io.File;
import java.io.IOException;

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
}
