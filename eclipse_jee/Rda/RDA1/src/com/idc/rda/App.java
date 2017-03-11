package com.idc.rda;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idc.rda.phase1.JiraItemInfo;
import com.idc.rda.phase1.xml.JiraXML;

public class App {
//	private static final String INPUT_XML = "/home/jv/vc/projects/Rda/RDA1/input/1078.xls";
	private static final String INPUT_XML = "C:/irac7/wrkspc/Rda/RDA1/input/1078.xls";
	private static final String OUTPUT_XML = "C:/irac7/wrkspc/Rda/RDA1/output/jv01.xml";

	public static void main(String[] args) {
		App m_app = new App();
		try {
			m_app.doTest();
		}
		catch (Exception ex) {
			System.out.println("Exception; "+ex.getMessage());
		}
	}

	private void doTest() throws Exception  {
		HSSFWorkbook workbook = null;
		try {
			InputStream myxls = new FileInputStream (INPUT_XML);
			workbook = new HSSFWorkbook (myxls);
		} catch (Exception ex) {
			System.err.println("Exception; " + ex.getMessage());
		}
		if (workbook == null) return;
		HSSFFormulaEvaluator.evaluateAllFormulaCells (workbook);

		JiraItemInfo jiraItemInfo = new JiraItemInfo (INPUT_XML, workbook);
		System.out.println("jiraItemInfo is built");

		JiraXML jiraXML = new JiraXML (OUTPUT_XML, jiraItemInfo);
		jiraXML.createXML();

		System.out.println("doTest is complete");
	}
}
