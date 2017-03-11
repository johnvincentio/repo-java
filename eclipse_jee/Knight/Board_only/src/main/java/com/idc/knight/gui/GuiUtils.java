package com.idc.knight.gui;

import java.io.File;

import com.idc.knight.JVFile;
import com.idc.knight.Pair;
import com.idc.knight.xml.Scenario;
import com.idc.knight.xml.Solution;
import com.idc.knight.xml.XStreamHelper;

public class GuiUtils {

	public static long parseString (String str) {
		try {
			long solution = Long.parseLong (str);
			return solution;
		}
		catch (Exception ex) {
			return 0L;
		}
	}
	public static int parseIntString (String str) {
		try {
			int solution = Integer.parseInt (str);
			return solution;
		}
		catch (Exception ex) {
			return 0;
		}
	}

	public static Scenario getScenario (String dirname, int max_x, int max_y, int start_x, int start_y) {
//		System.out.println (">>> GuiUtils::getScenario; dirname "+dirname+" max_x "+max_x+" max_y "+max_y+" start_x "+start_x+" start_y "+start_y);
		Scenario scenario = null;
		File file = JVFile.createSolutionsSubDirectory (dirname, (new Pair(max_x, max_y)).dirname(), (new Pair(start_x - 1, start_y - 1)).dirname(), "report.xml");
//		System.out.println("report pathname :"+file.getPath()+":");
		if (file.exists() && ! file.isDirectory()) {
			StringBuffer buf = JVFile.readFile (file.getPath());
//			System.out.println("Reading file "+file.getPath());
			scenario = XStreamHelper.xmlToScenario (buf.toString());
		}
//		System.out.println ("<<< GuiUtils::getScenario; "+scenario);
		return scenario;
	}

	public static Solution getSolution (String dirname, int max_x, int max_y, int start_x, int start_y, String solution_text) {
//		System.out.println (">>> GuiUtils::getSolution; dirname "+dirname+" max_x "+max_x+" max_y "+max_y+" start_x "+start_x+" start_y "+start_y);
		Solution solution = null;
		File file = JVFile.createSolutionsSubDirectory (dirname, (new Pair(max_x, max_y)).dirname(), (new Pair(start_x - 1, start_y - 1)).dirname(), solution_text + ".xml");
//		System.out.println("solution pathname :"+file.getPath()+":");
		if (file.exists() && ! file.isDirectory()) {
			StringBuffer buf = JVFile.readFile (file.getPath());
//			System.out.println("Reading file "+file.getPath());
			solution = XStreamHelper.xmlToSolution (buf.toString());
		}
//		System.out.println ("<<< GuiUtils::getSolution; "+solution);
		return solution;
	}
}
