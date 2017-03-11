package com.idc.jasper;

import net.sf.jasperreports.engine.JRException;

public class HyperlinkApp {

	public static void main(String[] args) {
		(new HyperlinkApp()).doTest();
	}
	public void doTest() {
		System.out.println("HyperlinkApp; doReport 104");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/OpenInvoices/HyperlinkReport.jrxml";
		String fileName = "HyperlinkReport";
		String baseDir = "c:/tmp101/4";
		try {
			Utils.doCompile (reportName, Utils.getPath (baseDir, fileName, "jasper"));
			Utils.doFill (Utils.getPath (baseDir, fileName, "jasper"), Utils.getPath (baseDir, fileName, "jprint"));
			Utils.doPdf (Utils.getPath (baseDir, fileName, "jprint"), Utils.getPath (baseDir, fileName, "pdf"));
		}
		catch (JRException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
