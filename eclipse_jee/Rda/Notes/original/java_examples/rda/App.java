package com.idc.rda;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idc.rda.phase1.JiraInfo;
import com.idc.rda.phase2.OuterInfo;
import com.idc.rda.phase3.BetaInfo;
import com.idc.rda.phase3.DeltaItemInfo;

//http://poi.apache.org/spreadsheet/examples.html
/*
http://www.vogella.de/articles/JavaPDF/article.html
 
http://pdfbox.apache.org/
 
http://www.prasannatech.net/2009/01/convert-pdf-text-parser-java-api-pdfbox.html
http://thottingal.in/blog/2009/06/24/pdfbox-extract-text-from-pdf/
http://www.printmyfolders.com/Home/PDFBox-Tutorial
http://pdfbox.apache.org/userguide/cookbook.html
http://pdfbox.apache.org/userguide/index.html
*/

public class App {
	private static final String XML = "c:/work112/herc_11_5.xls";

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

		JiraInfo jiraInfo = new JiraInfo (XML, workbook);
		System.out.println("jiraInfo "+jiraInfo);
		OuterInfo outerInfo = new OuterInfo (jiraInfo);
		System.out.println("outerInfo "+outerInfo);

		BetaInfo betaInfo = new BetaInfo (outerInfo);
		System.out.println("betaInfo "+betaInfo);

		Iterator<DeltaItemInfo> iter = betaInfo.getData ("Vendor Change", "RentalMan");
		while (iter.hasNext()) {
			DeltaItemInfo deltaItemInfo = iter.next();
			if (deltaItemInfo == null) continue;
			System.out.println("deltaItemInfo "+deltaItemInfo);
		}
	}
}

//HashMap<String, String[]> map = parseSectionToMap (sheet, 8, 'B', 17, 'D');
//showMap (map);

//showSheet (workbook.getSheet("SUMMARY"));

//String summary = handleSummarySheet(workbook.getSheet("SUMMARY"));
//if (summary == null)
//	System.err.println("Summary sheet is invalid");

/*
	private String handleSummarySheet(HSSFSheet sheet) {
		if (sheet == null) return null;
		System.out.println (handleCell (sheet, 7, 0));
		System.out.println (handleCell (sheet, 7, 1));
		System.out.println (handleCell (sheet, 7, 2));
		System.out.println (handleCell (sheet, 7, 3));
		System.out.println (handleCell (sheet, 7, 4));
		System.out.println (handleCell (sheet, 7, 5));
		System.out.println (handleCell (sheet, 7, 6));
		System.out.println (handleCell (sheet, 8, 1));
		System.out.println (handleCell (sheet, 9, 0));
		System.out.println (handleCell (sheet, 9, 1));
		System.out.println (handleCell (sheet, 9, 2));
		System.out.println (handleCell (sheet, 9, 3));
		System.out.println (handleCell (sheet, 9, 4));
		System.out.println (handleCell (sheet, 9, 5));
		System.out.println (handleCell (sheet, 9, 6));
		return "abc";
	}
*/

