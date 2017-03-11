package com.idc.explorer.abc5;

import java.io.File;

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
}
