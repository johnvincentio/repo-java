package com.idc.rda.phase1;

import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idc.rda.Utils;

public class JiraTemplates {
	public static HashMap<String, HashMap<String, String[]>> getSections_100 (final HSSFWorkbook workbook, int sheetNumber) {
		HashMap<String, HashMap<String, String[]>> map = new HashMap<String, HashMap<String, String[]>>();
		map.put ("Work Request Details", Utils.parseSectionToMap (workbook, sheetNumber, 1, 'B', 3, 'E'));
		map.put ("Development Estimates", Utils.parseSectionToMap (workbook, sheetNumber, 5, 'B', 6, 'D'));
		map.put ("Vendor Change", Utils.parseSectionToMap (workbook, sheetNumber, 8, 'B', 17, 'D'));
		map.put ("Irac Framework Change", Utils.parseSectionToMap (workbook, sheetNumber, 20, 'B', 27, 'D'));
		map.put ("Etrieve Change", Utils.parseSectionToMap (workbook, sheetNumber, 30, 'B', 33, 'D'));
		map.put ("Herc Required Server Change", Utils.parseSectionToMap (workbook, sheetNumber, 36, 'B', 40, 'D'));
		map.put ("Herc Required LDAP Change", Utils.parseSectionToMap (workbook, sheetNumber, 43, 'B', 47, 'D'));
		map.put ("Herc Required Database Change", Utils.parseSectionToMap (workbook, sheetNumber, 50, 'B', 61, 'D'));
		map.put ("Herc Change to Vendor", Utils.parseSectionToMap (workbook, sheetNumber, 65, 'B', 74, 'D'));
		map.put ("Type of Herc change", Utils.parseSectionToMap (workbook, sheetNumber, 77, 'B', 86, 'D'));
		map.put ("Functionality", Utils.parseSectionToMap (workbook, sheetNumber, 89, 'B', 99, 'C'));
		map.put ("Component", Utils.parseSectionToMap (workbook, sheetNumber, 102, 'B', 117, 'C'));
		map.put ("Factors", Utils.parseSectionToMap (workbook, sheetNumber, 119, 'B', 124, 'D'));
		map.put ("Project Factors", Utils.parseSectionToMap (workbook, sheetNumber, 126, 'B', 137, 'E'));

		map.put ("Pre-Development", Utils.parseSectionToMap (workbook, sheetNumber, 141, 'B', 144, 'E'));
		map.put ("Development", Utils.parseSectionToMap (workbook, sheetNumber, 146, 'B', 165, 'E'));
		map.put ("Release", Utils.parseSectionToMap (workbook, sheetNumber, 167, 'B', 172, 'E'));

		map.put ("Totals", Utils.parseSectionToMap (workbook, sheetNumber, 175, 'B', 178, 'E'));

		map.put ("High Level Estimates", Utils.parseSectionToMap (workbook, sheetNumber, 181, 'B', 185, 'D'));
		map.put ("Detailed Level Estimates", Utils.parseSectionToMap (workbook, sheetNumber, 188, 'B', 192, 'D'));
		map.put ("Architect", Utils.parseSectionToMap (workbook, sheetNumber, 195, 'B', 198, 'D'));
		return map;
	}
	public static HashMap<String, HashMap<String, String[]>> getSections_100_GOOD (final HSSFWorkbook workbook, int sheetNumber) {
		HashMap<String, HashMap<String, String[]>> map = new HashMap<String, HashMap<String, String[]>>();
		map.put ("Development Estimates", Utils.parseSectionToMap (workbook, sheetNumber, 5, 'B', 6, 'D'));
		map.put ("Vendor Change", Utils.parseSectionToMap (workbook, sheetNumber, 8, 'B', 17, 'D'));
		map.put ("Irac Framework Change", Utils.parseSectionToMap (workbook, sheetNumber, 20, 'B', 27, 'D'));
		map.put ("Etrieve Change", Utils.parseSectionToMap (workbook, sheetNumber, 30, 'B', 33, 'D'));
		map.put ("Herc Required Server Change", Utils.parseSectionToMap (workbook, sheetNumber, 36, 'B', 40, 'D'));
		map.put ("Herc Required LDAP Change", Utils.parseSectionToMap (workbook, sheetNumber, 43, 'B', 47, 'D'));
		map.put ("Herc Required Database Change", Utils.parseSectionToMap (workbook, sheetNumber, 50, 'B', 61, 'D'));
		map.put ("Herc Change to Vendor", Utils.parseSectionToMap (workbook, sheetNumber, 65, 'B', 74, 'D'));
		map.put ("Type of Herc change", Utils.parseSectionToMap (workbook, sheetNumber, 77, 'B', 86, 'D'));
		map.put ("Functionality", Utils.parseSectionToMap (workbook, sheetNumber, 89, 'B', 99, 'C'));
		map.put ("Component", Utils.parseSectionToMap (workbook, sheetNumber, 102, 'B', 117, 'C'));
		map.put ("Factors", Utils.parseSectionToMap (workbook, sheetNumber, 119, 'B', 124, 'D'));
		map.put ("Project Factors", Utils.parseSectionToMap (workbook, sheetNumber, 126, 'B', 137, 'E'));

		map.put ("Pre-Development", Utils.parseSectionToMap (workbook, sheetNumber, 141, 'B', 144, 'E'));
		map.put ("Development", Utils.parseSectionToMap (workbook, sheetNumber, 146, 'B', 165, 'E'));
		map.put ("Release", Utils.parseSectionToMap (workbook, sheetNumber, 167, 'B', 172, 'E'));

		map.put ("Totals", Utils.parseSectionToMap (workbook, sheetNumber, 175, 'B', 178, 'E'));

		map.put ("High Level Estimates", Utils.parseSectionToMap (workbook, sheetNumber, 181, 'B', 185, 'D'));
		map.put ("Detailed Level Estimates", Utils.parseSectionToMap (workbook, sheetNumber, 188, 'B', 192, 'D'));
		map.put ("Architect", Utils.parseSectionToMap (workbook, sheetNumber, 195, 'B', 198, 'D'));
		return map;
	}
}
