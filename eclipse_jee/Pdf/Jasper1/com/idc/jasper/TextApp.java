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
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

public class TextApp {

	private static final String TASK_COMPILE = "compile";
	private static final String TASK_FILL = "fill";
	private static final String TASK_PRINT = "print";
	private static final String TASK_PDF = "pdf";
	private static final String TASK_RTF = "rtf";
	private static final String TASK_TEXT = "text";

	public static void main(String[] args) {
/*
		if(args.length == 0) {
			usage();
			return;
		}
		String taskName = args[0];
		String fileName = args[1];
*/
		String taskName = TASK_COMPILE;
		String fileName = "C:/irac7/wrkspc/Pdf/Jasper1/TextReport.jrxml";

		/*
		 * Phase 1
		 */
		taskName = TASK_COMPILE;
		fileName = "C:/irac7/wrkspc/Pdf/Jasper1/TextReport.jrxml";

		/*
		 * Phase 2
		 */
		taskName = TASK_FILL;
		fileName = "C:/irac7/wrkspc/Pdf/Jasper1/TextReport.jasper";
		try
		{
			long start = System.currentTimeMillis();
			if (TASK_COMPILE.equals(taskName))
			{
				System.out.println("Before compileReportToFile");
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

				System.out.println("Before fillReportToFile");
				JasperFillManager.fillReportToFile(fileName, parameters, getConnection());
				System.err.println("Filling time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_PRINT.equals(taskName))
			{
				System.out.println("Before printReport");
				JasperPrintManager.printReport(fileName, true);
				System.err.println("Printing time : " + (System.currentTimeMillis() - start));
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
			else if (TASK_RTF.equals(taskName))
			{
				File sourceFile = new File(fileName);
		
				JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);
		
				File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".rtf");
				
				JRRtfExporter exporter = new JRRtfExporter();
				
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
				
				exporter.exportReport();

				System.err.println("RTF creation time : " + (System.currentTimeMillis() - start));
			}
			else
			{
				usage();
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


	/**
	 *
	 */
	private static void usage()
	{
		System.out.println( "TextApp usage:" );
		System.out.println( "\tjava TextApp task file" );
		System.out.println( "\tTasks : compile | fill | print | pdf | text" );
	}


	/**
	 *
	 */
	private static Connection getConnection() throws ClassNotFoundException, SQLException
	{
		//Change these settings according to your local configuration
		String driver = "org.hsqldb.jdbcDriver";
		String connectString = "jdbc:hsqldb:hsql://localhost";
		String user = "sa";
		String password = "";


		Class.forName(driver);
		Connection conn = DriverManager.getConnection(connectString, user, password);
		return conn;
	}
}
