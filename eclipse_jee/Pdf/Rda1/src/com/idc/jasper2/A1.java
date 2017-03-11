package com.idc.jasper2;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class A1 {
	public static void main(String[] args) {
		A1 a1 = new A1();
		try {
			a1.doTest();
		}
		catch (JRException e) {e.printStackTrace();}
		catch (Exception e) {e.printStackTrace();}
	}

	private void doTest() throws JRException {
		String dir = "C:/irac7/wrkspc/Pdf/Rda1/src/com/idc/jasper2/";
		String[] reports = {"DataSourceReport"};
		doCompile (dir, reports);
		doFill (dir, reports[0]);
		doPdf (dir, reports[0]);
	}


	private void doCompile (String dir, String[] reports) throws JRException {
		long start = System.currentTimeMillis();
		for (String filename : reports) {
			System.out.println("Compile; "+dir+filename);
			JasperCompileManager.compileReportToFile (dir+filename+".jrxml");
		}
		System.out.println("Compile time : " + (System.currentTimeMillis() - start));
	}
	private void doFill (String dir, String fileName) throws JRException {
		System.out.println("Before doFill; "+dir+fileName);
		long start = System.currentTimeMillis();
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Address Report");
		parameters.put("DataFile", "CustomBeanFactory.java - Bean Collection");
		JasperFillManager.fillReportToFile (dir+fileName+".jasper", parameters, new JRBeanCollectionDataSource(CustomBeanFactory.getBeanCollection()));
		System.err.println("Filling time : " + (System.currentTimeMillis() - start));
	}
	private void doPdf (String dir, String fileName) throws JRException {
		System.out.println("Before doPdf; "+dir+fileName);
		long start = System.currentTimeMillis();
		JasperExportManager.exportReportToPdfFile (dir+fileName+".jrprint");
		System.out.println("PDF creation time : " + (System.currentTimeMillis() - start));
	}
}
