package com.idc.jasper;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.export.FontKey;
import net.sf.jasperreports.engine.export.PdfFont;
import net.sf.jasperreports.engine.util.JRLoader;

public class JVAppTests {

	public static void main(String[] args) {
		JVAppTests jvApp = new JVAppTests();
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
 * Equipment on Rent, single account
 */
//		doTestRentEquipNonNarp();
/*
 * Equipment on Rent, narp account
 */
//		doTestRentEquipNarp();
/*
 * Rental History by Equipment Type 2007
 */
		doTestRentEquipInOneGo1c();
/*
 * Reservations and Quotes
 */

/*
 * Reservations and Quotes, centred columns
 */

/*
 * Reservations and Quotes - spanish
 */

	}
	/*
	 * Prashant; finished version
	 */
	public void doRentalHistoryByEquipmentType() throws JRException, Exception {
		  System.out.println(">>> JVApp; doRentalHistoryByEquipmentType; doReport 111");
		  String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/rentalHistoryByEquipmentType_2007.jrxml";
		  String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/rentalHistoryByEquipmentType_2007.txt";
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

	public void doTestRentEquipNonNarp() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestRentEquipNonNarp; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/equip.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/non_narp_data.txt";
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
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/equip.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/99/narp_data.txt";
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

	public void doTestRentEquip1c() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestRentEquip1c; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/4/equip.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/4/data.txt";
		String fileName = "EquipRentCsv4";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Image image = Utils.getImage("logo_HERC.gif");
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Proof of Concept; Equipment on Rent Report");
		parameters.put("SummaryImage", image);

		String[] columnNames = new String[] {
				"Account", "Name", "Contract", "StartDate", "EstReturnDate",
				"Equipment", "Quantity", "Description", "OnPickupTicket", "OrderedBy",
				"PurchaseOrder", "Overdue", "JobName", "DailyRate", "WeeklyRate",
				"FourWeekRate", "TotalBilled", "EstCosttoDate", "TotalEstCost"
		};
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
		DateFormat df = new SimpleDateFormat("MM/dd/yy");	//12/28/07
		csvDs.setDateFormat(df);
//		ds.setUseFirstRowAsHeader(true);
		csvDs.setColumnNames(columnNames);

		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		Utils.doView (jprint);
		System.out.println("<<< JVApp; doTestRentEquip1c");
	}
	public void doTestRentEquip1b() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestRentEquip1b; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/2/equip.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/2/data.txt";
		String fileName = "EquipRentCsv2";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Image image = Utils.getImage("logo_HERC.gif");
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Proof of Concept; Equipment on Rent Report");
		parameters.put("SummaryImage", image);

		String[] columnNames = new String[] {
				"AccountNumber", "AccountName", "ContractNumber", "StartDate", "EstReturnDate",
				"EquipmentNumber", "Quantity", "Description", "OnPickupTicket", "OrderedBy",
				"PurchaseOrder", "Overdue", "JobName", "DailyRate", "WeeklyRate",
				"FourWeekRate", "TotalBilled", "EstCosttoDate", "TotalEstCost"
		};
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
		DateFormat df = new SimpleDateFormat("MM/dd/yy");	//12/28/07
		csvDs.setDateFormat(df);
//		ds.setUseFirstRowAsHeader(true);
		csvDs.setColumnNames(columnNames);

		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTestRentEquip1b");
	}

	public void doTestRentEquip1a() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestRentEquip1a; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/1/equip.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Equip on Rent/1/data.txt";
		String fileName = "EquipRentCsv1";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Image image = Utils.getImage("logo_HERC.gif");
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Proof of Concept; Equipment on Rent Report");
		parameters.put("SummaryImage", image);

		String[] columnNames = new String[] {
				"AccountNumber", "AccountName", "ContractNumber", "StartDate", "EstReturnDate",
				"EquipmentNumber", "Quantity", "Description", "OnPickupTicket", "OrderedBy",
				"PurchaseOrder", "Overdue", "JobName", "DailyRate", "WeeklyRate",
				"FourWeekRate", "TotalBilled", "EstCosttoDate", "TotalEstCost"
		};
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
		DateFormat df = new SimpleDateFormat("MM/dd/yy");	//12/28/07
		csvDs.setDateFormat(df);
//		ds.setUseFirstRowAsHeader(true);
		csvDs.setColumnNames(columnNames);

		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTestRentEquip1a");
	}

/*
INSERT INTO ORDERS VALUES(
10263,'ERNSH',9,'1996-07-23 00:00:00.0','1996-08-20 00:00:00.0','1996-07-31 00:00:00.0',
3,146.06,'Ernst Handel','Kirchgasse 6','Graz',NULL,'8010','Austria')

CREATE TABLE ORDERS(ORDERID INTEGER,CUSTOMERID VARCHAR,EMPLOYEEID INTEGER,
ORDERDATE TIMESTAMP,REQUIREDDATE TIMESTAMP,SHIPPEDDATE TIMESTAMP,
SHIPVIA INTEGER,FREIGHT NUMERIC,SHIPNAME VARCHAR,
SHIPADDRESS VARCHAR,SHIPCITY VARCHAR,SHIPREGION VARCHAR,
SHIPPOSTALCODE VARCHAR,SHIPCOUNTRY VARCHAR)


*/
	public void doTestFirstJasper1d() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestFirstJasper1d; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/FirstJasper/3/JVFirstJasper.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/FirstJasper/3/orders.txt";
		String fileName = "JVFirstJasperCsv";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Image image = Utils.getImage("logo_HERC.gif");
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "The First Jasper Report Ever");
		parameters.put("MaxOrderID", new Integer(10500));
		parameters.put("SummaryImage", image);

		String[] columnNames = new String[] {
				"OrderID", "CustomerID", "EmployeeID",
				"OrderDate", "RequiredDate", "ShippedDate",
				"ShipVia", "Freight", "ShipName",
				"ShipAddress", "ShipCity", "ShipRegion",
				"ShipPostalCode", "ShipCountry"
		};
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");	//1996-07-01
		csvDs.setDateFormat(df);
//		ds.setUseFirstRowAsHeader(true);
		csvDs.setColumnNames(columnNames);

		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTestFirstJasper1d");
	}

	public void doTestFirstJasper1c() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestFirstJasper1c; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/FirstJasper/2/JVFirstJasper.jrxml";
		String fileName = "JVFirstJasper";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Image image = Utils.getImage("logo_HERC.gif");
		
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "The First Jasper Report Ever");
		parameters.put("MaxOrderID", new Integer(10500));
		parameters.put("SummaryImage", image);

		Utils.doCompile (reportName, jasper);
		Utils.doFill (getConnection(), parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTestFirstJasper1c");
	}

	public void doTestFirstJasper1b() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestFirstJasper1b; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/FirstJasper/1/FirstJasper.jrxml";
		String fileName = "FirstJasper";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Image image = Toolkit.getDefaultToolkit().createImage(
				JRLoader.loadBytesFromLocation("hertz_logo.gif"));
		MediaTracker traker = new MediaTracker(new Panel());
		traker.addImage(image, 0);
		try
		{
			traker.waitForID(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "The First Jasper Report Ever");
		parameters.put("MaxOrderID", new Integer(10500));
		parameters.put("SummaryImage", image);

		Utils.doCompile (reportName, jasper);
		Utils.doFill (getConnection(), parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTestFirstJasper1b");
	}

	public void doTestFirstJasper1a() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestFirstJasper1a; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/FirstJasper/1/FirstJasper.jrxml";
		String fileName = "FirstJasper";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Image image = Toolkit.getDefaultToolkit().createImage(
				JRLoader.loadBytesFromLocation("dukesign.jpg"));
		MediaTracker traker = new MediaTracker(new Panel());
		traker.addImage(image, 0);
		try
		{
			traker.waitForID(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "The First Jasper Report Ever");
		parameters.put("MaxOrderID", new Integer(10500));
		parameters.put("SummaryImage", image);

		Utils.doCompile (reportName, jasper);
		Utils.doFill (getConnection(), parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTestFirstJasper1a");
	}
	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		//Change these settings according to your local configuration
		String driver = "org.hsqldb.jdbcDriver";
		String connectString = "jdbc:hsqldb:hsql://localhost";
		String user = "sa";
		String password = "";

		Class.forName(driver);
		Connection conn = DriverManager.getConnection(connectString, user, password);
		return conn;
	}

	public void doTestCSV1d() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestCSV1d; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/4/jv.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/4/jv.txt";
		String fileName = "jv4Csv";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		String[] columnNames = new String[]{"city", "name"};
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
//		ds.setUseFirstRowAsHeader(true);
		csvDs.setColumnNames(columnNames);

		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Address Report");
		parameters.put("DataFile", "CsvDataSource.txt - CSV data source");

		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTestCSV1d");
	}

	public void doTestCSV1c() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestCSV1c; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/3/jv.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/3/jv.txt";
		String fileName = "jvCsv";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		String[] columnNames = new String[]{"city", "name"};
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
//		ds.setUseFirstRowAsHeader(true);
		csvDs.setColumnNames(columnNames);

		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Address Report");
		parameters.put("DataFile", "CsvDataSource.txt - CSV data source");

		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTestCSV1c");
	}
	
	public void doTestCSV1b() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestCSV1b; doReport 103");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/2/CsvDataSourceReport.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/2/CsvDataSource.txt";
		String fileName = "CsvDataSource";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		String[] columnNames = new String[]{"city", "id", "name", "address", "state"};
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
//		ds.setUseFirstRowAsHeader(true);
		csvDs.setColumnNames(columnNames);

		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Address Report");
		parameters.put("DataFile", "CsvDataSource.txt - CSV data source");
		Set states = new HashSet();
		states.add("Active");
		states.add("Trial");
		parameters.put("IncludedStates", states);

		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTestCSV1b");
	}


	public void doTest2a() throws JRException, Exception {
		System.out.println(">>> JVApp; doTest2a; doReport 101");
		String htmlFile = "C:/irac7/wrkspc/Pdf/Jasper1/1434171_Account_Summary.html";
		String reportName = "C:/irac7/wrkspc/Pdf/Jasper1/JVAllMarkupReport.jrxml";
		String fileName = "AccountSummary";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Utils.doCompile (reportName, jasper);
		Utils.doFillHTML (Utils.getHtml (htmlFile), jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTest2a");
	}
	public void doTest2b() throws JRException, Exception {
		System.out.println(">>> JVApp; doTest2b; doReport 102");
		String htmlFile = "C:/irac7/wrkspc/Pdf/JVReports/html.txt";
		htmlFile = "C:/irac7/wrkspc/Pdf/Kevin/1434171_Account_Summary.html";
		htmlFile = "C:/irac7/wrkspc/Pdf/Kevin/JVAccount_Summary.html";
//		htmlFile = "C:/irac7/wrkspc/Pdf/JVReports/sun.html";
//		htmlFile = "C:/irac7/wrkspc/Pdf/JVReports/html2.txt";
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/JVMarkupReport.jrxml";
		String fileName = "JVMarkupReport";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

//		String jv = Utils.getHtml (htmlFile);
//		System.out.println(jv);

		Utils.doCompile (reportName, jasper);
		Utils.doFillHTML (Utils.getHtml (htmlFile), jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTest2b");
	}
	public void doTest2() throws JRException, Exception {
		System.out.println(">>> JVApp; doTest2; doReport 101");
		String htmlFile = "C:/irac7/wrkspc/Pdf/Jasper1/html.txt";
		String reportName = "C:/irac7/wrkspc/Pdf/Jasper1/JVAllMarkupReport.jrxml";
		String fileName = "JVAllMarkupReport";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Utils.doCompile (reportName, jasper);
		Utils.doFillHTML (Utils.getHtml (htmlFile), jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTest2");
	}

	public void doTestCSV1a() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestCSV1a; doReport 102");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/1/CsvDataSourceReport.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/CSV/1/CsvDataSource.txt";
		String fileName = "CsvDataSource";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		String[] columnNames = new String[]{"city", "id", "name", "address", "state"};
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\r\n");
//		ds.setUseFirstRowAsHeader(true);
		csvDs.setColumnNames(columnNames);

		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Address Report");
		parameters.put("DataFile", "CsvDataSource.txt - CSV data source");
		Set states = new HashSet();
		states.add("Active");
		states.add("Trial");
		parameters.put("IncludedStates", states);

		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTestCSV1a");
	}

	public void doReportXML(String jrxmlName, String xmlName, 
			String outputName, String outputDir) throws JRException, Exception {
		String jasper = Utils.getPath (outputDir, outputName, "jasper");
		String jprint = Utils.getPath (outputDir, outputName, "jprint");
		String pdf = Utils.getPath (outputDir, outputName, "pdf");

		Utils.doCompile (jrxmlName, jasper);
		Utils.doFillXML (xmlName, jasper, jprint);
		Utils.doPdf (jprint, pdf);
	}
	public void doTestXML1() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestXML1; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/OpenInvoices/report2.jrxml";
		String xmlName = "C:/irac7/wrkspc/Pdf/JVReports/OpenInvoices/data1.xml";
		String fileName = "report2";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Utils.doCompile (reportName, jasper);
		Utils.doFillXML (xmlName, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTestXML1");
	}
	public void doTestXML1a() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestXML1a; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/OpenInvoices/report4.jrxml";
		String xmlName = "C:/irac7/wrkspc/Pdf/JVReports/OpenInvoices/data1.xml";
		String fileName = "report4";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Utils.doCompile (reportName, jasper);
		Utils.doFillXML (xmlName, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTestXML1a");
	}
	public void doTestXML2a() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestXML2a; doReport 101");
		doReportXML ("C:/irac7/wrkspc/Pdf/JVReports/XML/1/report.jrxml",
				"C:/irac7/wrkspc/Pdf/JVReports/XML/1/data.xml",
				"xml_1_report", 
				"c:/tmp101/4");
		System.out.println("<<< JVApp; doTestXML2a");
	}
	public void doTestXML2b() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestXML2b; doReport 101");
		doReportXML ("C:/irac7/wrkspc/Pdf/JVReports/XML/2/report.jrxml",
				"C:/irac7/wrkspc/Pdf/JVReports/XML/2/data.xml",
				"xml_2_report", 
				"c:/tmp101/4");
		System.out.println("<<< JVApp; doTestXML2b");
	}
	public void doTestXML2c() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestXML2c; doReport 101");
		doReportXML ("C:/irac7/wrkspc/Pdf/JVReports/XML/3/report.jrxml",
				"C:/irac7/wrkspc/Pdf/JVReports/XML/3/data.xml",
				"xml_3_report", 
				"c:/tmp101/4");
		System.out.println("<<< JVApp; doTestXML2c");
	}
	public void doTestXML2d() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestXML2d; doReport 101");
		doReportXML ("C:/irac7/wrkspc/Pdf/JVReports/XML/4/report.jrxml",
				"C:/irac7/wrkspc/Pdf/JVReports/XML/4/data.xml",
				"xml_4_report", 
				"c:/tmp101/4");
		System.out.println("<<< JVApp; doTestXML2d");
	}
	public void doTestXML2e() throws JRException, Exception {
		System.out.println(">>> JVApp; doTestXML2e; doReport 101");
		doReportXML ("C:/irac7/wrkspc/Pdf/JVReports/XML/4/subreport.jrxml",
				"C:/irac7/wrkspc/Pdf/JVReports/XML/4/data.xml",
				"xml_4_subreport", 
				"c:/tmp101/4");
		System.out.println("<<< JVApp; doTestXML2e");
	}

	public void doTest3a() throws JRException, Exception {		// BeanShell
		System.out.println(">>> JVApp; doTest3a; doReport 101");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/BeanShellReport.jrxml";
		String fileName = "BeanShellReport";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Utils.doCompile (reportName, jasper);
		Utils.doFill (jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTest3a");
	}
	public void doTest3b() throws JRException, Exception {		// BeanShell
		System.out.println(">>> JVApp; doTest3b; doReport 101");
		//C:\irac7\wrkspc\Pdf\JVReports\iReport\1\1.jrxml
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/iReport/1/1.jrxml";
		String fileName = "iReport1";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Utils.doCompile (reportName, jasper);
		Utils.doFill (jasper, jprint);
		Utils.doPdf (jprint, pdf);
		System.out.println("<<< JVApp; doTest3b");
	}
}

/*
	public void doRentalHistoryByEquipmentType1a() throws JRException, Exception {
		System.out.println(">>> JVApp; doRentalHistoryByEquipmentType1a; doReport 103");
		String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Prashant/99/equip.jrxml";
		String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Prashant/99/equip.txt";
		String fileName = "abc";
		String baseDir = "c:/tmp101/4";

		String jasper = Utils.getPath (baseDir, fileName, "jasper");
		String jprint = Utils.getPath (baseDir, fileName, "jprint");
		String pdf = Utils.getPath (baseDir, fileName, "pdf");

		Map parameters = new HashMap();
		String[] columnNames = new String[] { "Account", "Name", "CatClass",
				"Description", "RentalDays", "NumberOfTrans", "RentalAmount",
				"RentalYear" };
		JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		csvDs.setRecordDelimiter("\n");
		csvDs.setColumnNames(columnNames);

		Utils.doCompile (reportName, jasper);
		Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		Utils.doPdf (jprint, pdf);
		Utils.doView (jprint);
		System.out.println("<<< JVApp; doRentalHistoryByEquipmentType1a");
	}
*/
