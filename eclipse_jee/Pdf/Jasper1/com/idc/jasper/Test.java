
package com.idc.jasper;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

public class Test {

/*
 * this works, except seems to stop the server
 */
	public void doRentalHistoryByEquipmentType() throws JRException, Exception {
		  System.out.println(">>> Test; doRentalHistoryByEquipmentType; doReport 111");
		  String reportName = "C:/irac7/wrkspc/Pdf/JVReports/Reports/99/rentalHistoryByEquipmentType_2007/rentalHistoryByEquipmentType_2007.jrxml";
		  String csvName = "C:/irac7/wrkspc/Pdf/JVReports/Reports/99/rentalHistoryByEquipmentType_2007/rentalHistoryByEquipmentType_2007.txt";
		  String fileName = "rentalHistoryByEquipmentType_2007";
		  String baseDir = "c:/tmp";

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
		  csvDs.setColumnNames(columnNames);

		  Utils.doCompile (reportName, jasper);
		  Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		  Utils.doPdf (jprint, pdf);
//		  Utils.doView (jprint);		// this can take down the server - when exit the viewer, the server dies

		  System.out.println("<<< Test; doRentalHistoryByEquipmentType");
	}
/*
 * this works
 */
	public void doRentalHistoryByEquipmentType_98() throws JRException, Exception {
		  System.out.println(">>> Test; doRentalHistoryByEquipmentType; doReport 111");
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
		  csvDs.setColumnNames(columnNames);
/*
		  Utils.doCompile (reportName, jasper);
		  Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		  Utils.doPdf (jprint, pdf);
		  Utils.doView (jprint);
*/
		  System.out.println("<<< Test; doRentalHistoryByEquipmentType");
	}
	public void doRentalHistoryByEquipmentType_99() throws JRException, Exception {
		  System.out.println(">>> Test; doRentalHistoryByEquipmentType; doReport 111");
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
/*
		  String[] columnNames = new String[] { "Account", "Name", "CatClass",
		    "Description", "RentalDays", "NumberOfTrans", "RentalAmount",
		    "RentalYear" };
		  JRCsvDataSource csvDs = new JRCsvDataSource(JRLoader.getLocationInputStream(csvName));
		  csvDs.setColumnNames(columnNames);

		  Utils.doCompile (reportName, jasper);
		  Utils.doFillCSV (csvDs, parameters, jasper, jprint);
		  Utils.doPdf (jprint, pdf);
		  Utils.doView (jprint);
*/
		  System.out.println("<<< Test; doRentalHistoryByEquipmentType");
	}
}
