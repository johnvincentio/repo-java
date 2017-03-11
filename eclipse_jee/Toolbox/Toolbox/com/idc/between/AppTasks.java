
package com.idc.between;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.idc.trace.LogHelper;

public class AppTasks {

	static public boolean isSearchDirFieldValid (String strDir) {
		if (strDir == null || strDir.length() < 1) return false;
		File file = new File (strDir);
		if (! file.isDirectory()) return false;
		return true;
	}
	static public boolean isOutputDirFieldValid (String strDir) {
		if (strDir == null || strDir.length() < 1) return false;
		File file = new File (strDir);
		if (file.isDirectory()) return false;
		return true;
	}

	static public boolean areDateFieldsValid (String strStart, String strEnd) {
//		LogHelper.info(">>> isDateFieldValid");
//		LogHelper.info("start date field :"+strStart+":");
//		LogHelper.info("end date field :"+strEnd+":");
		if (getDateFieldValue(strStart) < 1) return false;
		if (getDateFieldValue(strEnd) < 1) return false;
		return true;
	}
	static public long getDateFieldValue(String strDate) {
		long lSince = 0;
		if (strDate == null || strDate.length() < 1)
		return lSince;

		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy kk:mm:ss");
		try {
			Date myDate = format.parse(strDate);
			lSince = myDate.getTime();
		}
		catch (ParseException e) {
			LogHelper.error("Unable to parse "+strDate);
			return 0L;
		}
		return lSince;
	}

	static public boolean isDirectoryOK(final File dir, String mask) {
		String strDir = dir.getPath();
		int len = mask.length();
		if (strDir.length() != len) return true;
		if (strDir.substring(0,len).equals(mask)) return false;
		return true;
	}
}
