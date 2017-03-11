package com.idc.jasper;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.FontKey;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.PdfFont;

import org.apache.tools.ant.util.FileUtils;

public class JVAllMarkupApp {
	public static void main (String[] args) {
		(new JVAllMarkupApp()).doTest();
	}
	public void doTest() {
		String htmlFile = "C:/irac7/wrkspc/Pdf/Jasper1/html.txt";
		String fileName = "C:/irac7/wrkspc/Pdf/Jasper1/JVAllMarkupReport.jrxml";

		Map fontMap = new HashMap();
		fontMap.put(new FontKey("DejaVu Sans", true, false), new PdfFont("Helvetica-Bold", "Cp1252", false));
		fontMap.put(new FontKey("DejaVu Sans", false, true), new PdfFont("Helvetica-Oblique", "Cp1252", false));
		fontMap.put(new FontKey("DejaVu Sans", true, true), new PdfFont("Helvetica-BoldOblique", "Cp1252", false));
		String destFile = "c:/tmp101/4/JVAllMarkupReport.pdf";
		doTest2(getHtml (htmlFile), fileName, destFile, fontMap);
	}
	public String getHtml (String htmlFile) {
		try {
			return FileUtils.readFully (new FileReader(htmlFile));
		}
		catch (Exception ex) {ex.printStackTrace();}
		return null;
	}
	public void doTest2(String htmlText, String fileName, String destFile, Map fontMap) {
		try {
			long start = System.currentTimeMillis();

			Map reportParams = new HashMap();
			reportParams.put("HtmlText", htmlText);

//			exportReportToStream(java.io.OutputStream);
			System.out.println("Before exportReport");
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, 
					JasperFillManager.fillReport(JasperCompileManager.compileReport(fileName), reportParams));
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
			exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);
			exporter.exportReport();
			System.err.println("PDF creation time : " + (System.currentTimeMillis() - start));
		}
		catch (JRException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doTest1() {
		String fileName = "C:/irac7/wrkspc/Pdf/Jasper1/JVAllMarkupReport.jrxml";

		try {
			long start = System.currentTimeMillis();

			/*
			 * Compile - needs .jrxml
			 */
			System.out.println("Before compileReportToFile");
			JasperReport jasperReport = JasperCompileManager.compileReport(fileName);
			System.err.println("Compile time : " + (System.currentTimeMillis() - start));

			/*
			 * Fill - needs .jasper
			 */
			File parentFolder = new File(fileName).getParentFile();
			String htmlText = FileUtils.readFully (new FileReader(new File(parentFolder, "html.txt")));
			
			Map parameters = new HashMap();
			parameters.put("HtmlText", htmlText);

			System.out.println("Before fillReportToFile");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters);
			//java.io.InputStream
			System.err.println("Filling time : " + (System.currentTimeMillis() - start));

			/*
			 * Pdf - needs .rprint
			 */
			File sourceFile = new File(fileName);
			System.out.println("sourceFile "+sourceFile.getPath());
			
			File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".pdf");
			System.out.println("destFile "+destFile.getPath());

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
//exportReportToStream(java.io.OutputStream);
			System.err.println("PDF creation time : " + (System.currentTimeMillis() - start));
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

/*
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
*/
