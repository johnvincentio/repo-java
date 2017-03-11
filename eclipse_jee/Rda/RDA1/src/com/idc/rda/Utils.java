package com.idc.rda;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator.CellValue;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idc.rda.phase1.items.AlphaInfo;
import com.idc.rda.phase1.items.AlphaItemInfo;

public class Utils {
	private static final String NL = "\n";
	private static final StringBuffer COLUMNS = new StringBuffer ("ABCDEFGHIJKLMNOPQRSTUVWYXZ");

	public static double parseDouble (String string, double defaultValue) {
		try {
			return Double.parseDouble(string);
		}
		catch (Exception e) {
			return defaultValue;
		}
	}
	public static int getIntColumn (char chr) {
//		System.out.println("chr :"+chr+":");
		for (int i = 0; i < COLUMNS.length(); i++) {
//			System.out.println("i "+i+" m_columns.charAt(i) "+COLUMNS.charAt(i));
			char ch = COLUMNS.charAt(i);
			if (ch == chr) return i + 1;
		}
		return -1;
	}
	public static AlphaInfo parseSectionToAlphaInfo (final HSSFWorkbook workbook, String sheetName, int fromRow, char chrFromCol, int toRow, char chrToCol) throws Exception {
		System.out.println(">>> Utils::parseSectionToAlphaInfo; sheetName "+sheetName+" fromRow "+fromRow+" chrFromCol "+chrFromCol+" toRow "+toRow+" chrToCol "+chrToCol);
		int sheetNumber = workbook.getSheetIndex (sheetName);
		if (sheetNumber < 1) throw new Exception ("Cannot find sheet name :"+sheetName+":");

		int fromCol = getIntColumn (chrFromCol);
		int toCol = getIntColumn (chrToCol);
		int maxcols = toCol - fromCol;
//		System.out.println("fromCol "+fromCol+" toCol "+toCol+" maxcols "+maxcols);

		String[] strArray = new String[maxcols + 1];
		for (int colnum = fromCol; colnum <= toCol; colnum++) {
//			System.out.println("colnum "+colnum+" toCol "+toCol);
			strArray[colnum - fromCol] = handleCell (workbook, sheetNumber, fromRow, colnum);
		}
		AlphaItemInfo alphaItemInfo = new AlphaItemInfo (strArray);
		AlphaInfo alphaInfo = new AlphaInfo (sheetName, alphaItemInfo);

		for (int rownum = fromRow + 1; rownum <= toRow; rownum++) {
			strArray = new String[maxcols + 1];
			for (int colnum = fromCol; colnum <= toCol; colnum++) {
//				System.out.println("colnum "+colnum+" toCol "+toCol);
				strArray[colnum - fromCol] = handleCell (workbook, sheetNumber, rownum, colnum);
			}
			alphaInfo.add (new AlphaItemInfo (strArray));
		}
		System.out.println("<<< Utils::parseSectionToAlphaInfo; "+alphaInfo);
		return alphaInfo;
	}

	public static String parseItem (final HSSFWorkbook workbook, int sheetNumber, int row, char chrCol) {
		int col = getIntColumn (chrCol);
		return handleCell (workbook, sheetNumber, row, col);
	}

	public static String getValidatedString (final HSSFWorkbook workbook, String sheetName,  int rowKey, char chrColKey, String key, int row, char chrCol, boolean required) throws Exception {
		System.out.println(">>> Utils::getValidatedString; sheetName "+sheetName+" rowKey "+rowKey+" chrColKey "+chrColKey+" key "+key);
		int sheetNumber = workbook.getSheetIndex (sheetName);
		if (sheetNumber < 1) throw new Exception ("Cannot find sheet name :"+sheetName+":");

		String name = handleCell (workbook, sheetNumber, rowKey, getIntColumn (chrColKey));
		if (name == null || (! name.equals(key))) throw new Exception ("Could not find key "+key+" on sheet "+sheetName);
		System.out.println("name :"+name+":");

		String value = handleCell (workbook, sheetNumber, row, getIntColumn (chrCol));
		if ((required) && value == null || value.length() < 1) throw new Exception ("Required value is empty; key "+key+" on sheet "+sheetName);
		System.out.println("<<< Utils::getValidatedString; value :"+value+":");
		return value;
	}
	public static int getValidatedInt (final HSSFWorkbook workbook, String sheetName,  int rowKey, char chrColKey, String key, int row, char chrCol, boolean required) throws Exception {
		String str = getValidatedString (workbook, sheetName,  rowKey, chrColKey, key, row, chrCol, required);
		int value = (int) parseDouble (str, -1.0);
		if (value < 100) throw new Exception ("Illegal template version "+value);
		return value;
	}

	public static String handleCell (final HSSFWorkbook workbook, final int sheetNumber, final int pRow, final int pCol) {
		HSSFSheet sheet = workbook.getSheetAt (sheetNumber);
		int row = pRow - 1;
		int col = pCol - 1;
		if (sheet == null) return null;
		HSSFRow hssfRow = sheet.getRow (row);
		if (hssfRow == null) return null;
		HSSFCell hssfCell = hssfRow.getCell (col);
		if (hssfCell == null) return null;
		int type = hssfCell.getCellType();
//		System.out.println("row "+row+" col "+col+" type " + type);
		String value = null;
		try {
			if (type == 2) {
				HSSFFormulaEvaluator hSSFFormulaEvaluator = new HSSFFormulaEvaluator (workbook);
				CellValue cellValue = hSSFFormulaEvaluator.evaluate (hssfCell);
				type = cellValue.getCellType();
			}
			switch (type) {
			case 0:
				double dbl = hssfCell.getNumericCellValue();
				value = String.valueOf (dbl);
				break;
			case 1:				// String
				HSSFRichTextString abc = hssfCell.getRichStringCellValue();
				value = abc.getString();
				break;
			case 3:				// Blank
				break;
			case 4:				// Boolean
				boolean bool = hssfCell.getBooleanCellValue();
				value = String.valueOf (bool);
				break;
			default:
				System.err.println("Unknown type "+type);
			}
			return value;
		}
		catch (Exception ex) {
			System.out.println("Exception in handleCell; "+ex.getMessage());
			System.out.println("workbook.getSheetName(sheetNumber)"+workbook.getSheetName(sheetNumber)+" pRow "+pRow+" pCol "+pCol+" type " + type);
			ex.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public static void showList (ArrayList<String[]> list) {
		for (String[] strArray : list) {
			StringBuffer buf = new StringBuffer();
			for (String s : strArray) buf.append(s).append(",");
			System.out.println(buf.toString());
		}
	}
	public static StringBuffer traceMap (HashMap<String, String[]> map) {
		StringBuffer buf = new StringBuffer();
		Iterator<Map.Entry<String, String[]>> parameters = map.entrySet().iterator();
		while (parameters.hasNext()) {
			Map.Entry<String, String[]> entry = parameters.next();
			String key = entry.getKey();
			String[] values = entry.getValue();
			buf.append ("key[" + key + "] values[");
			for (int i = 0; i < values.length; i++) {
				buf.append (values[i]);
				if (i + 1 < values.length)
					buf.append (", ");
				else
					buf.append ( "]");
			}
		}
		return buf;
	}
	public static StringBuffer traceMap2 (HashMap<String, HashMap<String, String[]>> map) {
		StringBuffer buf = new StringBuffer();
		Iterator<Map.Entry<String, HashMap<String, String[]>>> parameters = map.entrySet().iterator();
		while (parameters.hasNext()) {
			Map.Entry<String, HashMap<String, String[]>> entry = parameters.next();
			String section = entry.getKey();
			buf.append (NL).append (section).append (NL);
			buf.append (traceMap (entry.getValue()));
			buf.append (NL);
		}
		return buf;
	}
	public static StringBuffer traceStringArray (String[] strArray) {
		StringBuffer buf = new StringBuffer();
		buf.append ("values[");
		for (int i = 0; i < strArray.length; i++) {
			buf.append (strArray[i]);
			if (i + 1 < strArray.length)
				buf.append (", ");
			else
				buf.append ( "]");
		}
		return buf;
	}
	public static boolean writeFile (String strText, String strFile) {
		return writeFile (strText, new File (strFile));
	}
	public static boolean writeFile (String strText, File file) {
		try {
			PrintWriter pw = new PrintWriter (new BufferedWriter (new FileWriter(file)));
			pw.print(strText);
			pw.flush();
			pw.close();
			return true;
		}
		catch (IOException ex) {
			System.out.println("Unable to write the file; "+ex.getMessage());
			return false;
		}
	}
	public static String replace (String input, String pattern, String replace) {
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
/*
	public static void showSheet (HSSFSheet sheet) {
		Iterator rows = sheet.rowIterator();
		while (rows.hasNext()) {
			HSSFRow row = (HSSFRow) rows.next();
			System.out.println("Row #" + row.getRowNum());

			// Iterate over each cell in the row and print out the cell's
			// content
			Iterator cells = row.cellIterator();
			while (cells.hasNext()) {
				HSSFCell cell = (HSSFCell) cells.next();
				System.out.println("Cell #" + cell.getCellNum());
				switch (cell.getCellType()) {
				case HSSFCell.CELL_TYPE_NUMERIC:
					System.out.println(cell.getNumericCellValue());
					break;
				case HSSFCell.CELL_TYPE_STRING:
					System.out.println(cell.getStringCellValue());
					break;
				default:
					System.out.println("unsupported cell type");
					break;
				}
			}
		}
	}
*/
/*
	public static HashMap<String, String[]> parseSectionToMap (final HSSFWorkbook workbook, String sheetName, int fromRow, char chrFromCol, int toRow, char chrToCol) throws Exception {
		int sheetNumber = workbook.getSheetIndex (sheetName);
		if (sheetNumber < 1) throw new Exception ("Cannot find sheet name :"+sheetName+":");
		int fromCol = getIntColumn (chrFromCol);
		int toCol = getIntColumn (chrToCol);
		System.out.println("fromCol "+fromCol+" toCol "+toCol);
		String section = handleCell (workbook, sheetNumber, fromRow, fromCol);
		System.out.println("section :"+section+":");

		HashMap<String, String[]> map = new HashMap<String, String[]>();
		int maxcols = toCol - fromCol;
		for (int rownum = fromRow + 1; rownum <= toRow; rownum++) {
			String[] strArray = new String[maxcols];
			String type = handleCell (workbook, sheetNumber, rownum, fromCol);
			for (int colnum = fromCol + 1; colnum <= toCol; colnum++) {
				strArray[colnum - fromCol - 1] = handleCell (workbook, sheetNumber, rownum, colnum);
			}
			map.put(type, strArray);
		}
		return map;
	}
*/
