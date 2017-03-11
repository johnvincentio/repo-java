package com.idc.jasper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRXmlUtils;

import org.w3c.dom.Document;

public class A1 {
	public static void main(String[] args) {
		A1 a1 = new A1();
		try {
			a1.doTest1();
		}
		catch (JRException e) {e.printStackTrace();}
		catch (Exception e) {e.printStackTrace();}
	}

	private void doTest() throws JRException {
		String dir = "C:/irac7/wrkspc/Pdf/Rda1/data/a1/";
		String[] reports = {"r1", "r1s1"};
		doCompile (dir, reports);
		doFill (dir, reports[0], "northwind.xml");
		doPdf (dir, reports[0]);
	}

	private void doTest1() throws JRException {
		String dir = "C:/irac7/wrkspc/Pdf/Rda1/data/a1/";
		String[] reports = {"CustomersReport", "OrdersReport"};
		doCompile (dir, reports);
		doFill (dir, reports[0], "northwind.xml");
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
	private void doFill (String dir, String fileName, String dataName) throws JRException {
		System.out.println("Before doFill; "+dir+fileName+", "+dir+dataName);
		long start = System.currentTimeMillis();
		Map params = new HashMap();
		Document document = JRXmlUtils.parse(JRLoader.getLocationInputStream (dir+dataName));
		params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
		params.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd");
		params.put(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#,##0.##");
		params.put(JRXPathQueryExecuterFactory.XML_LOCALE, Locale.ENGLISH);
		params.put(JRParameter.REPORT_LOCALE, Locale.US);
		JasperFillManager.fillReportToFile (dir+fileName+".jasper", params);
		System.out.println("Filling time : " + (System.currentTimeMillis() - start));
	}
	private void doPdf (String dir, String fileName) throws JRException {
		System.out.println("Before doPdf; "+dir+fileName);
		long start = System.currentTimeMillis();
		JasperExportManager.exportReportToPdfFile (dir+fileName+".jrprint");
		System.out.println("PDF creation time : " + (System.currentTimeMillis() - start));
	}
}
