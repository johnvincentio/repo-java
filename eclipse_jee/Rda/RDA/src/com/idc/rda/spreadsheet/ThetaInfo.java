package com.idc.rda.spreadsheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idc.rda.phase3.BetaInfo;
import com.idc.rda.phase3.DeltaItemInfo;

public class ThetaInfo {
	private String m_outputFilename;
	private BetaInfo m_betaInfo;
	private HSSFWorkbook m_workbook;
	private HSSFSheet m_listSheet;
	private HSSFSheet m_summarySheet;

	public ThetaInfo (String outputFilename, BetaInfo betaInfo) {
		m_outputFilename = outputFilename;
		m_betaInfo = betaInfo;
	}

	public void createSpreadSheet() {
		m_workbook = new HSSFWorkbook();
		m_listSheet = createListSheet();
		m_summarySheet = createSummarySheet();
		saveWorkbook();
	}
	private HSSFSheet createListSheet() {
		HSSFSheet sheet = m_workbook.createSheet("List");
		int maxCol = -1;
		maxCol = handleData (sheet, "Work Request Details", "Jira Number", 0 , maxCol + 1);
//		maxCol = handleData (sheet, "Work Request Details", "Description Name", 0 , maxCol + 1);
//		maxCol = handleData (sheet, "Development Estimates", "Total", 0 , maxCol + 1);
		maxCol = handleData (sheet, "Vendor Change", "RentalMan", 0 , maxCol + 1);
		maxCol = handleData (sheet, "Vendor Change", "AS400", 0 , maxCol + 1);
		maxCol = handleData (sheet, "Vendor Change", "Etrieve", 0 , maxCol + 1);
		maxCol = handleData (sheet, "Vendor Change", "Rouse", 0 , maxCol + 1);
		maxCol = handleData (sheet, "Vendor Change", "Mindshare", 0 , maxCol + 1);
		maxCol = handleData (sheet, "Vendor Change", "AirClick", 0 , maxCol + 1);
		maxCol = handleData (sheet, "Vendor Change", "SQLServer", 0 , maxCol + 1);
		maxCol = handleData (sheet, "Vendor Change", "DB2", 0 , maxCol + 1);
		maxCol = handleData (sheet, "Vendor Change", "Fleet", 0 , maxCol + 1);
		return sheet;
	}
	private HSSFSheet createSummarySheet() {
		HSSFSheet sheet = m_workbook.createSheet("Summary");
        HSSFRow rowA = sheet.createRow(0);
        HSSFCell cellA = rowA.createCell(0);
        cellA.setCellValue(new HSSFRichTextString("summary SHEET"));
        sheet.autoSizeColumn((short) 0);
		return sheet;
	}
	private int handleData (HSSFSheet sheet, String key, String subkey, int row, int col) {
		int maxColumnCount = col;
		HSSFCell hssfCell;

		hssfCell = createCellIfNotFound (sheet, row, col);
		hssfCell.setCellValue (new HSSFRichTextString (key));

		hssfCell = createCellIfNotFound (sheet, row+1, col);
		hssfCell.setCellValue (new HSSFRichTextString (subkey));

		sheet.autoSizeColumn ((short) col);

		int rowcnt = row + 2;
		Iterator<DeltaItemInfo> iter = m_betaInfo.getData (key, subkey);
		while (iter.hasNext()) {
			int colcnt = col;
			DeltaItemInfo deltaItemInfo = iter.next();
			if (deltaItemInfo == null) continue;
			System.out.println("deltaItemInfo "+deltaItemInfo);
			String[] arrData = deltaItemInfo.getData();
			for (String data : arrData) {
				System.out.println("rowcnt "+rowcnt+" colcnt "+colcnt+" data:"+data+":");
				hssfCell = createCellIfNotFound (sheet, rowcnt, colcnt);
				hssfCell.setCellValue (new HSSFRichTextString (data));
				if (data != null) hssfCell.setCellType (HSSFCell.CELL_TYPE_STRING);
				sheet.autoSizeColumn ((short) colcnt);
				if (colcnt > maxColumnCount) maxColumnCount = colcnt;
				colcnt++;
			}
			rowcnt++;
		}

		return maxColumnCount;
	}

	private HSSFCell createCellIfNotFound (HSSFSheet sheet, int row, int col) {
		HSSFRow hssfRow = sheet.getRow (row);
		if (hssfRow == null) hssfRow = sheet.createRow (row);
		HSSFCell hssfCell = hssfRow.getCell (col);
		if (hssfCell == null) hssfCell = hssfRow.createCell (col);
		hssfCell.setCellType (HSSFCell.CELL_TYPE_STRING);
		return hssfCell;
	}

	private void saveWorkbook() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream (new File (m_outputFilename));
            m_workbook.write (fos);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
}
/*
		map.put ("Jira", Utils.parseSectionToMap (workbook, sheetNumber, 2, 'C', 3, 'E'));
*/
/*
 * Iterator<DeltaItemInfo> iter = betaInfo.getData ("Vendor Change", "RentalMan"); 
 * while (iter.hasNext()) { DeltaItemInfo deltaItemInfo = iter.next(); if (deltaItemInfo == null) continue;
 * System.out.println("deltaItemInfo "+deltaItemInfo); }
 */
/*
 * HSSFWorkbook workbook = new HSSFWorkbook(); CreationHelper createHelper =
 * workbook.getCreationHelper(); Sheet sheet =
 * workbook.createSheet("new sheet");
 * 
 * // Create a row and put some cells in it. Rows are 0 based. Row row =
 * sheet.createRow((short) 0); // Create a cell and put a value in it. Cell cell
 * = row.createCell(0); cell.setCellValue(1);
 * 
 * // Or do it on one line. row.createCell(1).setCellValue(1.2);
 * row.createCell(2).setCellValue(
 * createHelper.createRichTextString("This is a string"));
 * row.createCell(3).setCellValue(true);
 * 
 * // Write the output to a file FileOutputStream fileOut = new
 * FileOutputStream("workbook.xls"); workbook.write(fileOut); fileOut.close();
 */
/*
	private HSSFRow createRowIfNotFound (HSSFSheet sheet, int row) {
		HSSFRow hssfRow = sheet.getRow (row);
		if (hssfRow != null) return hssfRow;
		return sheet.createRow (row);
	}
	private HSSFCell createCellIfNotFound (HSSFRow hssfRow, int col) {
		HSSFCell hssfCell = hssfRow.getCell (col);
		if (hssfCell != null) return hssfCell;
		return hssfRow.createCell (col);
	}
*/
