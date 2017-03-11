package com.idc.jasper;

import net.sf.jasperreports.engine.JRException;

public class JVOpenInvoicesApp {

	public static void main(String[] args) {
		(new JVOpenInvoicesApp()).doTest();
	}

	public void doTest() {
		System.out.println("JVOpenInvoicesApp; doReport 103");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/OpenInvoices/report2.jrxml";
		String xmlName = "C:/irac7/wrkspc/Pdf/JVReports/OpenInvoices/data1.xml";
		String fileName = "report2";
		String baseDir = "c:/tmp101/4";
		try {
			Utils.doCompile (reportName, Utils.getPath (baseDir, fileName, "jasper"));
			Utils.doFillXML (xmlName, Utils.getPath (baseDir, fileName, "jasper"), Utils.getPath (baseDir, fileName, "jprint"));
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

