package com.idc.jasper;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.export.FontKey;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.PdfFont;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import net.sf.jasperreports.view.JasperViewer;

import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Document;

public class Utils {

	public static String getPath (String baseDir, String name, String ext) {
		return baseDir + "/" + name + "." + ext;
	}
	public static String getHtml (String htmlFile) {
		try {
			return FileUtils.readFully (new FileReader(htmlFile));
		}
		catch (Exception ex) {ex.printStackTrace();}
		return null;
	}
	public static Image getImage (String name) throws Exception {
		Image image = Toolkit.getDefaultToolkit().createImage(
				JRLoader.loadBytesFromLocation(name));
		MediaTracker traker = new MediaTracker(new Panel());
		traker.addImage(image, 0);
		try {
			traker.waitForID(0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	public static void doCompile (String jrxml, String jasper) throws Exception {	// .jasper
		long start = System.currentTimeMillis();
		JasperCompileManager.compileReportToFile(jrxml, jasper);
		System.out.println("doCompile "+jrxml+" creation time : " + (System.currentTimeMillis() - start));
	}
	public static void doFill (String jasper, String jprint) throws Exception {		// .jprint
		long start = System.currentTimeMillis();
		JasperFillManager.fillReportToFile(jasper, jprint, null, new JREmptyDataSource());
		System.out.println("doFill "+jasper+" creation time : " + (System.currentTimeMillis() - start));
	}
	public static void doFill (Connection connection, Map parameters, String jasper, String jprint) throws Exception {		// .jprint
		long start = System.currentTimeMillis();
		JasperFillManager.fillReportToFile(jasper, jprint, parameters, connection);
		System.out.println("doFill "+jasper+" creation time : " + (System.currentTimeMillis() - start));
	}

	public static void doFillXML (String xmlFile, String jasper, String jprint) throws Exception {
		long start = System.currentTimeMillis();
		Map params = new HashMap();
		Document document = JRXmlUtils.parse(JRLoader.getLocationInputStream(xmlFile));
		params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
		JasperFillManager.fillReportToFile(jasper, jprint, params);
		System.out.println("doFill "+jasper+" creation time : " + (System.currentTimeMillis() - start));
	}
	public static void doFillCSV (JRCsvDataSource csvDs, Map parameters, String jasper, String jprint) throws Exception {
		long start = System.currentTimeMillis();
		JasperFillManager.fillReportToFile(jasper, jprint, parameters, csvDs);
		System.out.println("doFill "+jasper+" creation time : " + (System.currentTimeMillis() - start));
	}
	public static void doFillHTML (String htmlText, String jasper, String jprint) throws Exception {
		long start = System.currentTimeMillis();
		Map params = new HashMap();
		params.put("HtmlText", htmlText);
		JasperFillManager.fillReportToFile(jasper, jprint, params, new JREmptyDataSource());
		System.out.println("doFill "+jasper+" creation time : " + (System.currentTimeMillis() - start));
	}
	public static void doPdf (String jprint, String pdf) throws Exception {	// .pdf
		long start = System.currentTimeMillis();
		JasperExportManager.exportReportToPdfFile(jprint, pdf);
		System.out.println("doPdf "+pdf+" creation time : " + (System.currentTimeMillis() - start));
	}
	public static void doView (String jprint) throws Exception {
		long start = System.currentTimeMillis();
		JasperViewer.viewReport(jprint, false);
		System.out.println("doView "+jprint+" creation time : " + (System.currentTimeMillis() - start));
	}

	public static void doReportCSV (JRCsvDataSource csvDs, Map parameters, String jasper, 
			String jprint, String pdf) throws Exception {
		long start = System.currentTimeMillis();
		JasperFillManager.fillReportToFile(jasper, jprint, parameters, csvDs);
		JasperExportManager.exportReportToPdfFile(jprint, pdf);
		System.out.println("doReportCSV "+jasper+" creation time : " + (System.currentTimeMillis() - start));
	}
	public static void doReportCSV (String csvName, String accountNumber, String accountName, int reportNumber, int extractNumber,
			String jasper, String jprint, String pdf) throws Exception {
		System.out.println(">>>doReportCSV ");
		long start = System.currentTimeMillis();

		String[] columnNames = new String[] {};
		switch (extractNumber) {
			case 101:
			default:
				columnNames = new String[] {
					"Account", "Name", "Contract", "StartDate", "EstReturnDate",
					"Equipment", "Quantity", "Description", "OnPickupTicket", "OrderedBy",
					"PurchaseOrder", "Overdue", "JobName", "DailyRate", "WeeklyRate",
					"FourWeekRate", "TotalBilled", "EstCosttoDate", "TotalEstCost"
			};
		}
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
		DateFormat df = new SimpleDateFormat("MM/dd/yy");
		csvDs.setDateFormat(df);
		csvDs.setUseFirstRowAsHeader(false);
		csvDs.setColumnNames(columnNames);

		String reportName;
		switch (reportNumber) {
			case 501:
			default:
				reportName = "Equipment on Rent Report";
		}

		Image image = Utils.getImage("logo_HERC.gif");
		Map parameters = new HashMap();
		parameters.put("ReportTitle", reportName);
		parameters.put("TitleAccountNumber", accountNumber);
		parameters.put("TitleAccountName", accountName);
		parameters.put("SummaryImage", image);

		JasperFillManager.fillReportToFile(jasper, jprint, parameters, csvDs);
		JasperExportManager.exportReportToPdfFile(jprint, pdf);
		System.out.println("<<< doReportCSV "+jasper+" creation time : " + (System.currentTimeMillis() - start));
	}

	public static void doReportCSV2 (String csvName, String accountNumber, String accountName, int reportNumber, int extractNumber,
			String jasper, String pdf) throws Exception {
		System.out.println(">>>doReportCSV ");
		long start = System.currentTimeMillis();

		String[] columnNames = new String[] {};
		switch (extractNumber) {
			case 101:
			default:
				columnNames = new String[] {
					"Account", "Name", "Contract", "StartDate", "EstReturnDate",
					"Equipment", "Quantity", "Description", "OnPickupTicket", "OrderedBy",
					"PurchaseOrder", "Overdue", "JobName", "DailyRate", "WeeklyRate",
					"FourWeekRate", "TotalBilled", "EstCosttoDate", "TotalEstCost"
			};
		}
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
		DateFormat df = new SimpleDateFormat("MM/dd/yy");
		csvDs.setDateFormat(df);
		csvDs.setUseFirstRowAsHeader(false);
		csvDs.setColumnNames(columnNames);

		String reportName;
		switch (reportNumber) {
			case 501:
			default:
				reportName = "Equipment on Rent Report";
		}

		Image image = Utils.getImage("logo_HERC.gif");
		Map parameters = new HashMap();
		parameters.put("ReportTitle", reportName);
		parameters.put("TitleAccountNumber", accountNumber);
		parameters.put("TitleAccountName", accountName);
		parameters.put("SummaryImage", image);

		Map fontMap = new HashMap();
		fontMap.put(new FontKey("DejaVu Sans", true, false), new PdfFont("Helvetica-Bold", "Cp1252", false));
		fontMap.put(new FontKey("DejaVu Sans", false, true), new PdfFont("Helvetica-Oblique", "Cp1252", false));
		fontMap.put(new FontKey("DejaVu Sans", true, true), new PdfFont("Helvetica-BoldOblique", "Cp1252", false));

		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, 
				JasperFillManager.fillReport(jasper, parameters));
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, pdf);
		exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);

		System.out.println("Before exportReport");
		exporter.exportReport();
		System.out.println("<<< doReportCSV "+jasper+" creation time : " + (System.currentTimeMillis() - start));
	}

	public static void doReportCSV (String csvName, String accountNumber, String accountName, int reportNumber, int extractNumber,
			String jasper, String pdf) throws Exception {
		System.out.println(">>>doReportCSV - 101");
		long start = System.currentTimeMillis();

		String[] columnNames = new String[] {};
		switch (extractNumber) {
			case 101:
			default:
				columnNames = new String[] {
					"Account", "Name", "Contract", "StartDate", "EstReturnDate",
					"Equipment", "Quantity", "Description", "OnPickupTicket", "OrderedBy",
					"PurchaseOrder", "Overdue", "JobName", "DailyRate", "WeeklyRate",
					"FourWeekRate", "TotalBilled", "EstCosttoDate", "TotalEstCost"
			};
		}
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
		DateFormat df = new SimpleDateFormat("MM/dd/yy");
		csvDs.setDateFormat(df);
		csvDs.setUseFirstRowAsHeader(false);
		csvDs.setColumnNames(columnNames);

		String reportName;
		switch (reportNumber) {
			case 501:
			default:
				reportName = "Equipment on Rent Report";
		}

		Image image = Utils.getImage("logo_HERC.gif");
		Map parameters = new HashMap();
		parameters.put("ReportTitle", reportName);
		parameters.put("TitleAccountNumber", accountNumber);
		parameters.put("TitleAccountName", accountName);
		parameters.put("SummaryImage", image);

		Map fontMap = new HashMap();
		fontMap.put(new FontKey("DejaVu Sans", true, false), new PdfFont("Helvetica-Bold", "Cp1252", false));
		fontMap.put(new FontKey("DejaVu Sans", false, true), new PdfFont("Helvetica-Oblique", "Cp1252", false));
		fontMap.put(new FontKey("DejaVu Sans", true, true), new PdfFont("Helvetica-BoldOblique", "Cp1252", false));

		FileOutputStream fos = new FileOutputStream (pdf);

		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, JasperFillManager.fillReport(jasper, parameters));
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
		exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);

		System.out.println("Before exportReport");
		exporter.exportReport();
		
		fos.flush();
		fos.close();

		System.out.println("<<< doReportCSV "+jasper+" creation time : " + (System.currentTimeMillis() - start));
	}

	public static ByteArrayOutputStream doReportCSV (String csvName, 
			String accountNumber, String accountName, int reportNumber, int extractNumber,
			String jasper) throws Exception {
		System.out.println(">>>doReportCSV - 201");
		long start = System.currentTimeMillis();

		String[] columnNames = new String[] {};
		switch (extractNumber) {
			case 101:
			default:
				columnNames = new String[] {
					"Account", "Name", "Contract", "StartDate", "EstReturnDate",
					"Equipment", "Quantity", "Description", "OnPickupTicket", "OrderedBy",
					"PurchaseOrder", "Overdue", "JobName", "DailyRate", "WeeklyRate",
					"FourWeekRate", "TotalBilled", "EstCosttoDate", "TotalEstCost"
			};
		}
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
		DateFormat df = new SimpleDateFormat("MM/dd/yy");
		csvDs.setDateFormat(df);
		csvDs.setUseFirstRowAsHeader(false);
		csvDs.setColumnNames(columnNames);

		String reportName;
		switch (reportNumber) {
			case 501:
			default:
				reportName = "Equipment on Rent Report";
		}

		Image image = Utils.getImage("logo_HERC.gif");
		Map parameters = new HashMap();
		parameters.put("ReportTitle", reportName);
		parameters.put("TitleAccountNumber", accountNumber);
		parameters.put("TitleAccountName", accountName);
		parameters.put("SummaryImage", image);

		Map fontMap = new HashMap();
		fontMap.put(new FontKey("DejaVu Sans", true, false), new PdfFont("Helvetica-Bold", "Cp1252", false));
		fontMap.put(new FontKey("DejaVu Sans", false, true), new PdfFont("Helvetica-Oblique", "Cp1252", false));
		fontMap.put(new FontKey("DejaVu Sans", true, true), new PdfFont("Helvetica-BoldOblique", "Cp1252", false));

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, JasperFillManager.fillReport(jasper, parameters));
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteStream);
		exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);

		System.out.println("Before exportReport");
		exporter.exportReport();
		
		byteStream.flush();
		byteStream.close();

		System.out.println("<<< doReportCSV "+jasper+" creation time : " + (System.currentTimeMillis() - start));
		return byteStream;
	}

/*
 * straight out of DAOUtils
 */
	public static byte[] getBytes (Object object) throws Exception {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
		objStream.writeObject(object);
		objStream.flush();
		return (byte[])(byteStream.toByteArray());
	}

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
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
/*
import datasource.WebappDataSource;

		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Address Report");
		parameters.put("BaseDir", reportFile.getParentFile());
					
		JasperPrint jasperPrint = null;

		try
		{
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(reportFile.getPath());
			
			jasperPrint = 
				JasperFillManager.fillReport(
					jasperReport,
					parameters, 
					new WebappDataSource()
					);
		}
		catch (JRException e)
		{
		
		
		if (jasperPrint != null)
		{
			response.setContentType("application/octet-stream");
			ServletOutputStream ouputStream = response.getOutputStream();
			
			ObjectOutputStream oos = new ObjectOutputStream(ouputStream);
			oos.writeObject(jasperPrint);
			oos.flush();
			oos.close();

			ouputStream.flush();
			ouputStream.close();
		}
*/
/*
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
 */

/*
	public void doReport(String reportName, String xmlName, String destName) {
		System.out.println("doReport 103");
		try {
			long start = System.currentTimeMillis();

			Map reportParams = new HashMap();
			Document document = JRXmlUtils.parse(JRLoader.getLocationInputStream(xmlName));
			reportParams.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
			reportParams.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd");
			reportParams.put(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#,##0.##");
			reportParams.put(JRXPathQueryExecuterFactory.XML_LOCALE, Locale.ENGLISH);
			reportParams.put(JRParameter.REPORT_LOCALE, Locale.US);

			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT,
					JasperFillManager.fillReport(JasperCompileManager.compileReport(reportName), reportParams));
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destName);
			// exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);
			exporter.exportReport();
			System.err.println("PDF creation time : " + (System.currentTimeMillis() - start));
		} catch (JRException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
/*
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
*/

