package com.idc.pdf;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class A1 {
	public static void main (String[] args) {
		(new A1()).doTest1();
	}
	private void doTest1() {
		JasperReport jasperReport;
		JasperPrint jasperPrint;
		JasperDesign jasperDesign;

		Map parameters = new HashMap();
		parameters.put("Report_Title", "Salesman Details");

//		 load JasperDesign from XML and compile it into JasperReport
//		jasperDesign = JRXmlLoader.load("C:/jasperReports/demo.jrxml");
//		jasperReport = JasperCompileManager.compileReport(jasperDesign);
		 
//		 fill JasperPrint using fillReport() method
//		jasperPrint = JasperFillManager.fillReport(jasperReport,parameters,conn);
	}
}
