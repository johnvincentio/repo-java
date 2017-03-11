package com.idc.rda;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idc.rda.phase1.JiraInfo;
import com.idc.rda.phase2.OuterInfo;
import com.idc.rda.phase3.BetaInfo;
import com.idc.rda.spreadsheet.ThetaInfo;

//http://poi.apache.org/spreadsheet/examples.html

public class App {
	private static final String INPUT_XML = "/home/jv/vc/projects/Rda/RDA/herc_11_5_unix.xls";
//	private static final String XML = "/home/jv/vc/projects/Rda/RDA/test.xls";
	private static final String OUTPUT_XML = "/home/jv/vc/projects/Rda/Notes/output/jv99.xls";

	public static void main(String[] args) {
		(new App()).doTest();
	}

	private void doTest() {
		HSSFWorkbook workbook = null;
		try {
			InputStream myxls = new FileInputStream (INPUT_XML);
			workbook = new HSSFWorkbook (myxls);
		} catch (Exception ex) {
			System.err.println("Exception; " + ex.getMessage());
		}
		if (workbook == null) return;
		HSSFFormulaEvaluator.evaluateAllFormulaCells (workbook);

		JiraInfo jiraInfo = new JiraInfo (INPUT_XML, workbook);
		System.out.println("jiraInfo "+jiraInfo);
		OuterInfo outerInfo = new OuterInfo (jiraInfo);
		System.out.println("outerInfo "+outerInfo);

		BetaInfo betaInfo = new BetaInfo (outerInfo);
		System.out.println("betaInfo "+betaInfo);

		ThetaInfo thetaInfo = new ThetaInfo (OUTPUT_XML, betaInfo);
		thetaInfo.createSpreadSheet();
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

