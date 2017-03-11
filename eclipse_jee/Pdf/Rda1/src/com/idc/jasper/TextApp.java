
package com.idc.jasper;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

public class TextApp {
	private static final String TASK_COMPILE = "compile";
	private static final String TASK_FILL = "fill";
	private static final String TASK_TEXT = "text";
	private static final String TASK_PDF = "pdf";

	public static void main (String[] args) {
		String fileName = "TextReport.jrxml";
		doTest (TASK_COMPILE,fileName);
		doTest (TASK_FILL, fileName);
		doTest (TASK_TEXT, fileName);
		doTest (TASK_PDF, fileName);
	}
	public static void doTest (String taskName, String fileName) {
		try
		{
			long start = System.currentTimeMillis();
			if (TASK_COMPILE.equals(taskName))
			{
				JasperCompileManager.compileReportToFile(fileName);
				System.err.println("Compile time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_FILL.equals(taskName))
			{
				//Preparing parameters
				Map parameters = new HashMap();
				parameters.put("ReportTitle", "Address Report");
				parameters.put("FilterClause", "'Boston', 'Chicago', 'Oslo'");
				parameters.put("OrderClause", "City");

//				JasperFillManager.fillReportToFile(fileName, parameters, getConnection());
				System.err.println("Filling time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_TEXT.equals(taskName))
			{
				JRTextExporter exporter = new JRTextExporter();
				File sourceFile = new File(fileName);
				JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);
				File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".txt");

				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
				exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(10));
				exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(10));
				exporter.exportReport();

				System.err.println("Text creation time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_PDF.equals(taskName))
			{
				JasperExportManager.exportReportToPdfFile(fileName);
				System.err.println("PDF creation time : " + (System.currentTimeMillis() - start));
			}
		}
		catch (JRException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
