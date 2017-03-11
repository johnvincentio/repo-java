package com.idc.jasper;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.HashMap;

import org.apache.tools.ant.util.FileUtils;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.FontKey;
import net.sf.jasperreports.engine.export.PdfFont;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.util.JRLoader;

/*
 * This test shows how to take html and turn into a pdf.
 */
public class JVMarkupApp {
	private static final String TASK_COMPILE = "compile";
	private static final String TASK_FILL = "fill";
	private static final String TASK_PRINT = "print";
	private static final String TASK_PDF = "pdf";
	private static final String TASK_RTF = "rtf";
	private static final String TASK_XML = "xml";
	private static final String TASK_XML_EMBED = "xmlEmbed";
	private static final String TASK_HTML = "html";
	private static final String TASK_XLS = "xls";
	private static final String TASK_JXL = "jxl";
	private static final String TASK_CSV = "csv";
	private static final String TASK_ODT = "odt";
	private static final String TASK_RUN = "run";

	public static void main (String[] args) {
		/*
		if(args.length == 0) {
			usage();
			return;
		}
		String taskName = args[0];
		String fileName = args[1];
*/
		String taskName;
		String fileName;

		//Compile time : 1547
		taskName = TASK_COMPILE;
		fileName = "C:/irac7/wrkspc/Pdf/Jasper1/JVMarkupReport.jrxml";

		//Filling time : 1453
		taskName = TASK_FILL;
		fileName = "JVMarkupReport.jasper";

		//PDF creation time : 1531
		taskName = TASK_PDF;
		fileName = "JVMarkupReport.jrprint";

		try {
			long start = System.currentTimeMillis();
			if (TASK_COMPILE.equals(taskName))
			{
				System.out.println("Before compileReportToFile");
				JasperCompileManager.compileReportToFile(fileName);
				System.err.println("Compile time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_FILL.equals(taskName)) {
				File parentFolder = new File(fileName).getParentFile();
				String htmlText = FileUtils.readFully(new FileReader(new File(parentFolder, "html.txt")));
				
				Map parameters = new HashMap();
				parameters.put("HtmlText", htmlText);

				System.out.println("Before fillReportToFile");
				JasperFillManager.fillReportToFile(fileName, parameters, (JRDataSource)null);
				System.err.println("Filling time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_PRINT.equals(taskName)) {
				System.out.println("Before printReport");
				JasperPrintManager.printReport(fileName, true);
				System.err.println("Printing time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_PDF.equals(taskName)) {
				File sourceFile = new File(fileName);
				
				JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);
				
				File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".pdf");

				JRPdfExporter exporter = new JRPdfExporter();

				Map fontMap = new HashMap();
				fontMap.put(new FontKey("DejaVu Sans", true, false), new PdfFont("Helvetica-Bold", "Cp1252", false));
				fontMap.put(new FontKey("DejaVu Sans", false, true), new PdfFont("Helvetica-Oblique", "Cp1252", false));
				fontMap.put(new FontKey("DejaVu Sans", true, true), new PdfFont("Helvetica-BoldOblique", "Cp1252", false));

				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
				exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);

				System.out.println("Before exportReport");
				exporter.exportReport();

				System.err.println("PDF creation time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_RTF.equals(taskName)) {
				System.out.println("TASK_RTF");
				File sourceFile = new File(fileName);
				
				JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

				File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".rtf");
				
				JRRtfExporter exporter = new JRRtfExporter();
				
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
				
				exporter.exportReport();

				System.err.println("RTF creation time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_XML.equals(taskName)) {
				System.out.println("TASK_XML");
				JasperExportManager.exportReportToXmlFile(fileName, false);
				System.err.println("XML creation time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_XML_EMBED.equals(taskName)) {
				System.out.println("TASK_XML_EMBED");
				JasperExportManager.exportReportToXmlFile(fileName, true);
				System.err.println("XML creation time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_HTML.equals(taskName)) {
				System.out.println("TASK_HTML");
				JasperExportManager.exportReportToHtmlFile(fileName);
				System.err.println("HTML creation time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_XLS.equals(taskName)) {
				File sourceFile = new File(fileName);
		
				JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);
		
				File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xls");
				
				JRXlsExporter exporter = new JRXlsExporter();
				
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
				exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
				
				exporter.exportReport();

				System.err.println("XLS creation time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_JXL.equals(taskName)) {
				File sourceFile = new File(fileName);

				JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

				File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".jxl.xls");

				JExcelApiExporter exporter = new JExcelApiExporter();

				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
				exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);

				exporter.exportReport();

				System.err.println("XLS creation time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_CSV.equals(taskName)) {
				File sourceFile = new File(fileName);
		
				JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);
		
				File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".csv");
				
				JRCsvExporter exporter = new JRCsvExporter();
				
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
				
				exporter.exportReport();

				System.err.println("CSV creation time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_ODT.equals(taskName)) {
				File sourceFile = new File(fileName);
		
				JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);
		
				File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".odt");
				
				JROdtExporter exporter = new JROdtExporter();
				
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
				
				exporter.exportReport();

				System.err.println("ODT creation time : " + (System.currentTimeMillis() - start));
			}
			else if (TASK_RUN.equals(taskName)) {
				JasperRunManager.runReportToPdfFile(fileName, null, new JREmptyDataSource());
				System.err.println("PDF running time : " + (System.currentTimeMillis() - start));
			}
			else {
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

	private static void usage() {
		System.out.println( "MarkupApp usage:" );
		System.out.println( "\tjava MarkupApp task file" );
		System.out.println( "\tTasks : fill | print | pdf | xml | xmlEmbed | html | rtf | xls | jxl | csv | odt | run" );
	}
}
