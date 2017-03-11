package com.idc.trry.jpg;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idc.rda.Utils;

public class App {
	private static final String XML = "/home/jv/vc/projects/Rda/Notes/jv/jv.xls";

	public static void main(String[] args) {
		(new App()).doTest();
	}
	private void doTest() {
		HSSFWorkbook workbook = null;
		try {
			InputStream myxls = new FileInputStream (XML);
			workbook = new HSSFWorkbook (myxls);
		} catch (Exception ex) {
			System.err.println("Exception; " + ex.getMessage());
		}
		if (workbook == null) return;
		HSSFFormulaEvaluator.evaluateAllFormulaCells (workbook);

		int sheetNumber = workbook.getSheetIndex ("RDA");
		Utils.parseItem (workbook, sheetNumber, 7, 'D');
	}
}
