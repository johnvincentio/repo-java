package com.idc.knight.apps;

import java.io.File;

import com.idc.knight.Board;
import com.idc.knight.JVFile;
import com.idc.knight.Pair;
import com.idc.knight.xml.Scenario;
import com.idc.knight.xml.Solution;
import com.idc.knight.xml.XStreamHelper;

public class CompareFiles {

	private static final String FROM_DIR = "C:\\work\\work401";		// must use backslashes else the replace will not work.
	private static final String TO_DIR = "C:\\work\\new_jv_solutions";

	public static void main (String[] args) {
		(new CompareFiles()).app(args);
	}
	private void app (String[] args) {
		System.out.println("First pass");
		doDirectory (new File (FROM_DIR), true);
		System.out.println("Second pass");
		doDirectory (new File (TO_DIR), false);
		System.out.println("Complete");
	}

	private void doDirectory (final File dir, boolean fromDir) {
//		System.out.println(">>> doDirectory "+dir.getPath());
		if (! dir.isDirectory()) return;

		File file;
		File[] allFiles = dir.listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			file = allFiles[i];
			if (! file.isFile()) continue;
			String strFrom = file.getPath();
			if (! strFrom.endsWith(".xml")) continue;
			
			String strTo;
			if (fromDir)
				strTo = replace (strFrom, FROM_DIR, TO_DIR);
			else
				strTo = replace (strFrom, TO_DIR, FROM_DIR);
			File fileTo = new File (strTo);
			if (! fileTo.isFile() || ! fileTo.exists()) {
				System.out.println("File "+strTo+" not found");
				continue;
			}
//			System.out.println("Comparing "+strFrom+" to "+strTo);
			doCompare (strFrom, strTo);
		}
		for (int i = 0; i < allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) doDirectory (file, fromDir);
		}
//		System.out.println("<<< doDirectory");
	}

	private void doCompare (String fromFile, String toFile) {
		StringBuffer buf1 = JVFile.readFile (fromFile);
		StringBuffer buf2 = JVFile.readFile (toFile);
		if (fromFile.contains("report.xml")) {
			Scenario scenario1 = XStreamHelper.xmlToScenario (buf1.toString());
			Scenario scenario2 = XStreamHelper.xmlToScenario (buf2.toString());
			if (! isSameScenario (scenario1, scenario2))
				System.out.println("*** Different: fromFile "+fromFile+" toFile "+toFile);
		}
		else {
			Solution solution1 = XStreamHelper.xmlToSolution (buf1.toString());
			Solution solution2 = XStreamHelper.xmlToSolution (buf2.toString());
			if (! isSameSolution (solution1, solution2))
				System.out.println("*** Different: fromFile "+fromFile+" toFile "+toFile);
		}
	}

	private boolean isSameScenario (Scenario scenario1, Scenario scenario2) {
		if (scenario1 == null) return false;
		if (scenario2 == null) return false;
		if (scenario1.getSize() == null) return false;
		if (scenario2.getSize() == null) return false;
		if (scenario1.getSize().getX() != scenario2.getSize().getX()) return false;
		if (scenario1.getSize().getY() != scenario2.getSize().getY()) return false;

		if (scenario1.getStart() == null) return false;
		if (scenario2.getStart() == null) return false;
		if (scenario1.getStart().getX() != scenario2.getStart().getX()) return false;
		if (scenario1.getStart().getY() != scenario2.getStart().getY()) return false;

//		if (scenario1.getStartTime() != scenario2.getStartTime()) return false;
//		if (scenario1.getEndTime() != scenario2.getEndTime()) return false;
		if (scenario1.getSolutions() != scenario2.getSolutions()) return false;
		if (scenario1.getTotalMoves() != scenario2.getTotalMoves()) return false;
//		if (scenario1.getElapsedTime() != scenario2.getElapsedTime()) return false;
		return true;
	}

	private boolean isSameSolution (Solution solution1, Solution solution2) {
		if (solution1 == null) return false;
		if (solution2 == null) return false;
		if (solution1.getSolution() != solution2.getSolution()) return false;

		if (solution1.getSize() == null) return false;
		if (solution2.getSize() == null) return false;
		if (solution1.getSize().getX() != solution2.getSize().getX()) return false;
		if (solution1.getSize().getY() != solution2.getSize().getY()) return false;

		if (solution1.getStart() == null) return false;
		if (solution2.getStart() == null) return false;
		if (solution1.getStart().getX() != solution2.getStart().getX()) return false;
		if (solution1.getStart().getY() != solution2.getStart().getY()) return false;

		if (solution1.getCurrentSolutionTotalMoves() != solution2.getCurrentSolutionTotalMoves()) return false;
		if (solution1.getTotalMoves() != solution2.getTotalMoves()) return false;

		Board board1 = solution1.getBoard();
		Board board2 = solution2.getBoard();
		if (board1 == null) return false;
		if (board2 == null) return false;
		try {
			for (int i = 0; i < solution1.getStart().getX(); i++) {
				for (int j = 0; j < solution1.getStart().getY(); j++) {
					Pair pair = new Pair(i, j);
					if (board1.getX (pair) != board2.getX (pair)) return false;
					if (board1.getY (pair) != board2.getY (pair)) return false;
					if (board1.getMoveCounter (pair) != board2.getMoveCounter (pair)) return false;
					if (board1.getMoveAwayType (pair) != board2.getMoveAwayType (pair)) return false;
				}
			}
		}
		catch (Exception ex) {
			return false;
		}

		return true;
	}

	private String replace (String input, String pattern, String replace) {
		int s = 0;
		int e = 0;
		StringBuffer buf = new StringBuffer();
    
		while ((e = input.indexOf(pattern, s)) >= 0) {
			buf.append(input.substring(s, e));
			buf.append(replace);
			s = e + pattern.length();
		}
		buf.append(input.substring(s));
		return buf.toString();
	}
}
