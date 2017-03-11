package com.idc.knight.apps;

import java.io.File;

import com.idc.knight.JVFile;
import com.idc.knight.xml.Scenario;
import com.idc.knight.xml.XStreamHelper;

public class Statistics1 {

//	private static final String RESULTS_DIR = "C:/jvDevelopment/repo_four/eclipse_jee/Knight_solutions";
	private static final String RESULTS_DIR = "C:/work/new_jv_solutions";
//	private static final String RESULTS_DIR = "C:/work/work401";

	private long totalSolutions = 0;
	private long totalMoves = 0;
	private long totalElapsedTime = 0;

	public static void main (String[] args) {
		(new Statistics1()).app (args);
	}
	private void app (String[] args) {
		doDirectory (new File (RESULTS_DIR));
		System.out.println("Totals; solutions "+totalSolutions+" totalMoves "+totalMoves+" elapsedTime "+totalElapsedTime);
		System.out.println("Complete");
	}

	private void doDirectory (final File dir) {
//		System.out.println(">>> doDirectory "+dir.getPath());
		if (! dir.isDirectory()) return;

		File file;
		File[] allFiles = dir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			file = allFiles[i];
			if (! file.isFile()) continue;
			String strFrom = file.getPath();
//			System.out.println("strFrom :"+strFrom+":");
			if (! strFrom.endsWith(".xml")) continue;
			StringBuffer buf = JVFile.readFile (strFrom);
			if (file.getName().startsWith("report.")) {
				Scenario scenario = XStreamHelper.xmlToScenario (buf.toString());
				System.out.println("scenario; size "+scenario.getSize()+" start "+scenario.getStart()+" solutions "+scenario.getSolutions()+
						" totalMoves "+scenario.getTotalMoves()+" elapsedTime "+scenario.getElapsedTime()+", ["+getSmartTime (scenario.getElapsedTime()) + "]");
				totalSolutions += scenario.getSolutions();
				totalMoves += scenario.getTotalMoves();
				totalElapsedTime += scenario.getElapsedTime();
			}
/*
			else {
				Solution solution = XStreamHelper.xmlToSolution (buf.toString());
				System.out.println("solution "+solution);
			}
*/
		}
		for (int i = 0; i < allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) doDirectory (file);
		}
//		System.out.println("<<< doDirectory");
	}

/*
ref:
http://www.epochconverter.com/
*/
	private String getSmartTime (long elapsedTime) {
//		System.out.println(">>> getSmartTime; elapsedTime "+elapsedTime);
		if (elapsedTime < 0) {
			return elapsedTime + " is an invalid elapsed time";
		}
		String strDate = "";
		long rem1 = elapsedTime / 1000;					// seconds
		long millis = elapsedTime - rem1 * 1000;
//		System.out.println("millis "+millis);

		long rem2 = rem1 / 60;							// minutes
		long seconds = rem1 - (rem2 * 60);					// seconds
//		System.out.println("rem1 "+rem1+" rem2 "+rem2+" seconds "+seconds);

		long hours = rem2 / 60;							// hours
		long minutes = rem2 - (hours * 60);					// minutes
//		System.out.println("hours "+hours+" minutes "+minutes);

		if (hours > 0) {
			if (hours == 1)
				strDate += "1 hour, ";
			else if (hours > 1)
				strDate += hours + " hours, ";
			strDate += minutes + " minutes, ";
		}
		else {
			if (minutes > 0) strDate += minutes + " minutes, ";
		}
		strDate += seconds + " seconds, " + millis + " millis";
//		System.out.println("<<< getSmartTime; strDate :"+strDate+":");
		return strDate;
	}

	@SuppressWarnings("unused")
	private void app1 (String[] args) {
		System.out.println("getSmartTime "+getSmartTime(-1));
		System.out.println("getSmartTime "+getSmartTime(0));
		System.out.println("getSmartTime "+getSmartTime(1));					// 0 minutes, 0 seconds, 1 millis

		System.out.println("getSmartTime "+getSmartTime(1 * 1000));				// 0 minutes, 1 seconds, 0 millis
		System.out.println("getSmartTime "+getSmartTime(60 * 1000));			// 1 minute, 0 seconds, 0 millis
		System.out.println("getSmartTime "+getSmartTime(60 * 60 * 1000));		// 1 hour, 0 minutes, 0 seconds, 0 millis
		System.out.println("getSmartTime "+getSmartTime(5 * 60 * 60 * 1000));	// 5 hours, 0 minutes, 0 seconds, 0 millis
		System.out.println("getSmartTime "+getSmartTime(80 * 60 * 60 * 1000));	// 80 hours, 0 minutes, 0 seconds, 0 millis

		System.out.println("getSmartTime "+getSmartTime(797379));		// 13 minutes, 17 seconds, 379 millis
		System.out.println("getSmartTime "+getSmartTime(6797379));		// 1 hour, 53 minutes, 17 seconds, 379 millis
		System.out.println("getSmartTime "+getSmartTime(73498562));		// 20 hours, 24 minutes, 58 seconds, 379 millis
	}
}
