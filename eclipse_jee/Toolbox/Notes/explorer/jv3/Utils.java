package com.idc.explorer.jv3;

import java.io.File;

public class Utils {
	public static final char OS_SEP = '\\';
	public static final char JAR_SEP = '/';

//	private File getBaseWorkingDirectory() {return baseWorkingDirectory;}
	public static File makeWorkingDirectory() {
		return makeWorkingDirectory (makeWorkingDirectory (new File ("c:/jvExplorer")));
	}
	public static File makeWorkingDirectory (File file) {
		String tmpDir = Long.toString (System.currentTimeMillis());
		return new File (file.getPath() + File.separatorChar + tmpDir);
	}
	public static File convertToOSName (String strName) {
		return new File (strName.replace (OS_SEP, JAR_SEP));
	}
}
