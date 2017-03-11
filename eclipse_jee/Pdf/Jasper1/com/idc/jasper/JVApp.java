package com.idc.jasper;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.export.FontKey;
import net.sf.jasperreports.engine.export.PdfFont;
import net.sf.jasperreports.engine.util.JRLoader;

public class JVApp {

	public static void main(String[] args) {
		JVApp jvApp = new JVApp();
		try {
			jvApp.doReport();
		}
		catch (JRException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void doReport() throws JRException, Exception {

/*
 * show hyperlink to site
 */
//		doTestRentEquipNonNarp();	// Equipment on Rent, single account

//		doTestRentEquipNarp();		// Equipment on Rent, narp account

		doRentalHistoryByEquipmentType();	// Rental History by Equipment Type 2007

/*
 * account / narp support
 * localization
 * 
 * show report sections.
 * understand section capabilities.
 * discuss hyperlinks/anchors
 * details + summary in one report
 * handles totals
 * page statistics
 * any font
 * any pixel, anywhere, appears any way you like.
 * any filter, any sort is possible.
 * 
 *  can do graphs, pie charts, tables etc.
 */
/*
 * show pablo pdfs;
 * overdue rentals
 * then show spaninh version
 */
		// not implemented
//		doRentalHistoryByEquipmentTypeNarp();	// Rental History by Equipment Type 2007 for a Narp

//		doTestRentEquipInOneGo1c();		// 

//		doTestReservationsAndQuotesNonNarp();		// Reservations and Quotes

//		doTestReservationsAndQuotesCentredColumns();	// Reservations and Quotes, centred columns

//		doTestReservationsAndQuotesCentredColumnsSpanish();	// Reservations and Quotes, centred columns - Spanish
	}

	public void doTestRentEquipNonNarp() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestRentEquipNonNarp; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Reports/99/equipmentOnRent/equipmentOnRent.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Reports/99/equipmentOnRent/non_narp_data.txt";
		String fileName = "RentEquipNonNarp";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Image image = Utils.getImage("logo_HERC.gif");
		Map parameters = new HashMap();
		String accountNumber = "1434171";
		String accountName = "ABC INC";
		parameters.put("ReportTitle", "Equipment on Rent Report");
		parameters.put("TitleAccountNumber", accountNumber);
		parameters.put("TitleAccountName", accountName);
		parameters.put("SummaryImage", image);

		String[] columnNames = new String[] {
				"Account", "Name", "Contract", "StartDate", "EstReturnDate",
				"Equipment", "Quantity", "Description", "OnPickupTicket", "OrderedBy",
				"PurchaseOrder", "Overdue", "JobName", "DailyRate", "WeeklyRate",
				"FourWeekRate", "TotalBilled", "EstCosttoDate", "TotalEstCost"
		};
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
		DateFormat df = new SimpleDateFormat("MM/dd/yy");
		csvDs.setDateFormat(df);
		csvDs.setUseFirstRowAsHeader(false);
		csvDs.setColumnNames(columnNames);

		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		Utils.doView (jprint);
		System.out.println("<<< JVApp; doTestRentEquipNonNarp");
	}

	public void doTestRentEquipNarp() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestRentEquipNarp; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Reports/99/equipmentOnRent/equipmentOnRent.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Reports/99/equipmentOnRent/narp_data.txt";
		String fileName = "RentEquipNarp";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		String accountNumber = "1234";
		String accountName = "Narp Account Name";
		Image image = Utils.getImage("logo_HERC.gif");
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Equipment on Rent Report");
		parameters.put("TitleAccountNumber", accountNumber);
		parameters.put("TitleAccountName", accountName);
		parameters.put("SummaryImage", image);

		String[] columnNames = new String[] {
				"Account", "Name", "Contract", "StartDate", "EstReturnDate",
				"Equipment", "Quantity", "Description", "OnPickupTicket", "OrderedBy",
				"PurchaseOrder", "Overdue", "JobName", "DailyRate", "WeeklyRate",
				"FourWeekRate", "TotalBilled", "EstCosttoDate", "TotalEstCost"
		};
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
		DateFormat df = new SimpleDateFormat("MM/dd/yy");
		csvDs.setDateFormat(df);
		csvDs.setUseFirstRowAsHeader(false);
		csvDs.setColumnNames(columnNames);

		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		Utils.doView (jprint);
		System.out.println("<<< JVApp; doTestRentEquipNarp");
	}

	/*
	 * Prashant; finished version
	 */
	public void doRentalHistoryByEquipmentType() throws JRException, Exception {
		  System.out.println(">>> JVApp; doRentalHistoryByEquipmentType; doReport 111");
		  String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Reports/99/rentalHistoryByEquipmentType_2007/rentalHistoryByEquipmentType_2007.jrxml";
		  String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Reports/99/rentalHistoryByEquipmentType_2007/rentalHistoryByEquipmentType_2007.txt";
		  String fileName = "rentalHistoryByEquipmentType_2007";
		  String baseDir = "c:/tmp101/4";

		  String jasper = Utils.getPath (baseDir, fileName, "jasper");
		  String jprint = Utils.getPath (baseDir, fileName, "jprint");
		  String pdf = Utils.getPath (baseDir, fileName, "pdf");

		  Image image = Utils.getImage("logo_HERC.gif");
		  Map parameters = new HashMap();
		  parameters.put("ReportTitle", "Rental History By Equipment Type");
		  parameters.put("SummaryImage", image);

		  String[] columnNames = new String[] { "Account", "Name", "CatClass",
		    "Description", "RentalDays", "NumberOfTrans", "RentalAmount",
		    "RentalYear" };
		  JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
//		  csvDs.setRecordDelimiter("\r\n");
//		  DateFormat df = new SimpleDateFormat("MM/dd/yy"); //12/28/07
//		  csvDs.setDateFormat(df);
//		  ds.setUseFirstRowAsHeader(true);
		  csvDs.setColumnNames(columnNames);

		  Utils.doCompile (reportName, jasper);
		  Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		  Utils.doPdf (jprint, pdf);
		  Utils.doView (jprint);
		  System.out.println("<<< JVApp; doRentalHistoryByEquipmentType");
	}

	public void reservationsAndQuotes() throws JRException, Exception {
		System.out.println(">>> JVApp; reservationsAndQuotes; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/reservationsAndQuotes.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/1434171_reservationsAndQuotes.csv";
		//String csvName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/overdueRentals.txt";
		String fileName = "reservationsAndQuotesCSV";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Image logo_HERC = Utils.getImage("logo_HERC.gif");
		Image herc_logo_gray = Utils.getImage("herc_logo_gray3.gif");
		//Image image = Utils.getImage("C:/irac7/wrkspc/Pdf/Jasper1/google.gif");
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Reporte de Reservaciones y Quotas");
		parameters.put("logoHerc", logo_HERC);
		parameters.put("logoHercGray", herc_logo_gray);

		String[] columnNames = new String[] {
				"Account", "AcctName", "Contract", "Type" ,"StartDate", 
				  "ReturnDate", "PONum", "Location", "OrderedBy",
		};

		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		//csvDs.setRecordDelimiter("\r\n");
		csvDs.setRecordDelimiter("\n");
		csvDs.setUseFirstRowAsHeader(true);
		DateFormat df = new SimpleDateFormat("MM/dd/yy");	//12/28/07
		csvDs.setDateFormat(df);
		csvDs.setColumnNames(columnNames);

		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		Utils.doView (jprint);

		System.out.println("<<< JVApp; reservationsAndQuotes");
	}
	
	public void overDueRentals() throws JRException, Exception {
		System.out.println(">>> JVApp; overDueRentals; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/overdueRentals.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/1434171_overdueRentals.csv";
		//String csvName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/overdueRentals.txt";
		String fileName = "overdueRentalCSV";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Image logo_HERC = Utils.getImage("logo_HERC.gif");
		Image herc_logo_gray = Utils.getImage("herc_logo_gray3.gif");
		//Image image = Utils.getImage("C:/irac7/wrkspc/Pdf/Jasper1/google.gif");
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Overdue Rentals Report");
		parameters.put("logoHerc", logo_HERC);
		parameters.put("logoHercGray", herc_logo_gray);

		String[] columnNames = new String[] {
				"Account", "AcctName", "Contract", "StartDate", "Equipment",
				 "Quantity", "Description", "EstReturnDate", "OnPickupTicket", "OrderedBy",
				"PurchaseOrder", "JobName", "TotalBilled",  "EstCosttoDate", "TotalEstCost"
		};

		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		//csvDs.setRecordDelimiter("\r\n");
		csvDs.setRecordDelimiter("\n");
		csvDs.setUseFirstRowAsHeader(true);
		
		DateFormat df = new SimpleDateFormat("MM/dd/yy");	//12/28/07
		csvDs.setDateFormat(df);
//		ds.setUseFirstRowAsHeader(true);
		csvDs.setColumnNames(columnNames);
		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		Utils.doView (jprint);
		System.out.println("<<< JVApp; overDueRentals");
	}

	public void doTestRentEquipInOneGo1d() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestRentEquipInOneGo1d; doReport 103");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/equip.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/narp_data.txt";
		String fileName = "RentEquipNarp";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");

		Utils.doCompile (reportName, jasper);

		int reportNumber = 501;
		int extractNumber = 101;
		String accountNumber = "1234";
		String accountName = "Narp Account Name";

		ByteArrayOutputStream byteStream = 
			Utils.doReportCSV (csvName, accountNumber, accountName, reportNumber, extractNumber,
				jasper);
		System.out.println("len "+byteStream.size());
		
//		Utils.doView (jprint);
		System.out.println("<<< JVApp; doTestRentEquipInOneGo1d");
	}

	public void doTestRentEquipInOneGo1c() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestRentEquipInOneGo1c; doReport 103");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/equip.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/narp_data.txt";
		String fileName = "RentEquipNarp";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Utils.doCompile (reportName, jasper);

		int reportNumber = 501;
		int extractNumber = 101;
		String accountNumber = "1234";
		String accountName = "Narp Account Name";

		Utils.doReportCSV (csvName, accountNumber, accountName, reportNumber, extractNumber,
				jasper, jprint, pdf);

		Utils.doView (jprint);
		System.out.println("<<< JVApp; doTestRentEquipInOneGo1c");
	}

	public void doTestRentEquipInOneGo1b() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestRentEquipInOneGo1b; doReport 102");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/equip.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/narp_data.txt";
		String fileName = "RentEquipNarp";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Utils.doCompile (reportName, jasper);

		int reportNumber = 501;
		int extractNumber = 101;
		String accountNumber = "1234";
		String accountName = "Narp Account Name";

		Utils.doReportCSV (csvName, accountNumber, accountName, reportNumber, extractNumber,
				jasper, jprint, pdf);

//		Utils.doReportCSV (csvDs, parameters, jasper, jprint, pdf);
		Utils.doView (jprint);
/*
 * original code
 *
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		Utils.doView (jprint);
*/
		System.out.println("<<< JVApp; doTestRentEquipInOneGo1b");
	}

	public void doTestRentEquipInOneGo1a() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestRentEquipInOneGo1a; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/equip.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/narp_data.txt";
		String fileName = "RentEquipNarp";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Utils.doCompile (reportName, jasper);

		String accountNumber = "1234";
		String accountName = "Narp Account Name";
		Image image = Utils.getImage("logo_HERC.gif");
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Equipment on Rent Report");
		parameters.put("TitleAccountNumber", accountNumber);
		parameters.put("TitleAccountName", accountName);
		parameters.put("SummaryImage", image);

		String[] columnNames = new String[] {
				"Account", "Name", "Contract", "StartDate", "EstReturnDate",
				"Equipment", "Quantity", "Description", "OnPickupTicket", "OrderedBy",
				"PurchaseOrder", "Overdue", "JobName", "DailyRate", "WeeklyRate",
				"FourWeekRate", "TotalBilled", "EstCosttoDate", "TotalEstCost"
		};
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
		DateFormat df = new SimpleDateFormat("MM/dd/yy");
		csvDs.setDateFormat(df);
		csvDs.setUseFirstRowAsHeader(false);
		csvDs.setColumnNames(columnNames);

		Map fontMap = new HashMap();
		fontMap.put(new FontKey("DejaVu Sans", true, false), new PdfFont("Helvetica-Bold", "Cp1252", false));
		fontMap.put(new FontKey("DejaVu Sans", false, true), new PdfFont("Helvetica-Oblique", "Cp1252", false));
		fontMap.put(new FontKey("DejaVu Sans", true, true), new PdfFont("Helvetica-BoldOblique", "Cp1252", false));

		Utils.doReportCSV (csvDs, parameters, jasper, jprint, pdf);
		Utils.doView (jprint);
/*
 * original code
 *
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		Utils.doView (jprint);
*/
		System.out.println("<<< JVApp; doTestRentEquipInOneGo1a");
	}


}
