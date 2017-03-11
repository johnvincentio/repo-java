/********************************************************************
*			Copyright (c) 2006 The Hertz Corporation				*
*			  All Rights Reserved.  (Unpublished.)					*
*																	*
*		The information contained herein is confidential and		*
*		proprietary to The Hertz Corporation and may not be			*
*		duplicated, disclosed to third parties, or used for any		*
*		purpose not expressly authorized by it.  Any unauthorized	*
*		use, duplication, or disclosure is prohibited by law.		*
*																	*
*********************************************************************/

package com.hertz.hercutil.rentalman.reports;

import java.util.Iterator;

import com.hertz.hercutil.rentalman.reports.equipmentDetailHistory.EquipmentDetailHistoryInfo;
import com.hertz.hercutil.rentalman.reports.equipmentDetailHistory.EquipmentDetailHistoryItemInfo;
import com.hertz.hercutil.rentalman.reports.equipmentHistory.EquipmentHistoryInfo;
import com.hertz.hercutil.rentalman.reports.equipmentHistory.EquipmentHistoryItemInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.EquipmentOnRentInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.EquipmentOnRentItemInfo;
import com.hertz.hercutil.rentalman.reports.openInvoice.OpenInvoiceInfo;
import com.hertz.hercutil.rentalman.reports.openInvoice.OpenInvoiceItemInfo;

/**
 * Class to encapsulate CSV formatting concerns
 * 
 * @author John Vincent
 */

public class CSVTemplateHelper {

	/**
	 * Get CSV data for the Equipment History report
	 * 
	 * @param csvTemplateInfo			Csv templating instructions
	 * @param equipmentHistoryInfo		Equipment History data
	 * @return							CSV data for the report
	 */
	public static StringBuffer getEquipmentHistory (CSVTemplateInfo csvTemplateInfo, EquipmentHistoryInfo equipmentHistoryInfo) {
		StringBuffer buf = new StringBuffer();
		if (csvTemplateInfo.isHeader()) {
			int i = 0;
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Account#, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Account Name, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Address, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("City, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("State, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Country, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Cat-Class, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Description, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Rental Days, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("# of Trans, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Rental Amount, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Rental Year, ");
			buf.append("\n");
		}

		boolean bFirst = true;
		for (Iterator iter = equipmentHistoryInfo.getItems(); iter.hasNext(); ) {
			EquipmentHistoryItemInfo item = (EquipmentHistoryItemInfo) iter.next();
			if (! bFirst) buf.append("\n");
			bFirst = false;
			int i = 0;
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getAccountNumber());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getBusiness());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getAddress1());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getCity());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getState());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getCountry());
			csvTemplateInfo.applyColumn (buf, i++, item.getCategory() + "-" + item.getClassification());
			csvTemplateInfo.applyColumn (buf, i++, item.getDescription());
			csvTemplateInfo.applyColumn (buf, i++, item.getRentalDays());
			csvTemplateInfo.applyColumn (buf, i++, item.getTransactions());
			csvTemplateInfo.applyColumn (buf, i++, item.getRentalAmount());
			csvTemplateInfo.applyColumn (buf, i++, item.getRentalYear(), true);
		}
		return buf;
	}

	/**
	 * Get CSV data for the Detailed Equipment History report
	 * 
	 * @param csvTemplateInfo				Csv templating instructions
	 * @param equipmentDetailHistoryInfo	Detailed Equipment History data
	 * @return								CSV data for the report
	 */
	public static StringBuffer getEquipmentDetailHistory (CSVTemplateInfo csvTemplateInfo, EquipmentDetailHistoryInfo equipmentDetailHistoryInfo) {
		StringBuffer buf = new StringBuffer();
		if (csvTemplateInfo.isHeader()) {
			int i = 0;
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Account#,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Account Name,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Address,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("City,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("State,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Country,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Cat-Class,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Description,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Equip#,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Invoice,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Invoice Date,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Start Date,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Return Date,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Rate Used,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Rental Amount,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Rental Year");
			buf.append("\n");
		}

		boolean bFirst = true;
		for (Iterator iter = equipmentDetailHistoryInfo.getItems(); iter.hasNext(); ) {
			EquipmentDetailHistoryItemInfo item = (EquipmentDetailHistoryItemInfo) iter.next();
			if (! bFirst) buf.append("\n");
			bFirst = false;
			int i = 0;
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getAccountNumber());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getBusiness());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getAddress1());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getCity());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getState());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getCountry());
			csvTemplateInfo.applyColumn (buf, i++, item.getCategory() + "-" + item.getClassification());
			csvTemplateInfo.applyColumn (buf, i++, item.getDescription());
			csvTemplateInfo.applyColumn (buf, i++, item.getEquipmentNumber());
			csvTemplateInfo.applyColumn (buf, i++, item.getContractNumber() + "-" +item.getSequence());
			csvTemplateInfo.applyColumn (buf, i++, item.getInvoiceDate());
			csvTemplateInfo.applyColumn (buf, i++, item.getStartDate());
			csvTemplateInfo.applyColumn (buf, i++, item.getReturnDate());
			csvTemplateInfo.applyColumn (buf, i++, item.getRateUsed());
			csvTemplateInfo.applyColumn (buf, i++, item.getRentalAmount());
			csvTemplateInfo.applyColumn (buf, i++, item.getRentalYear(), true);
		}
		return buf; 
	}

	/**
	 * Get CSV data for the Equipment On Rent report
	 * 
	 * @param csvTemplateInfo			Csv templating instructions
	 * @param equipmentOnRentInfo		Equipment on Rent data
	 * @return							CSV data for the report
	 */
	public static StringBuffer getEquipmentOnRent (CSVTemplateInfo csvTemplateInfo, EquipmentOnRentInfo equipmentOnRentInfo) {
		StringBuffer buf = new StringBuffer();
		if (csvTemplateInfo.isHeader()) {
			int i = 0;
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Account#, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Account Name, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Address, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("City, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("State, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Country, ");

			if (csvTemplateInfo.isInclude(i++)) buf.append ("Contract#, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Start Date, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Est Return Date, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Equipment#, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Quantity, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Description, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("On Pickup Tkt, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Ordered By, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Purchase Order, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Overdue, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Job Name, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Daily rate, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Weekly rate, ");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("4 Week rate ");

			if (equipmentOnRentInfo.isUS()) {
				if (csvTemplateInfo.isInclude(i++)) buf.append (", Total Billed, ");
				if (csvTemplateInfo.isInclude(i++)) buf.append ("Est. Cost to Date, ");
				if (csvTemplateInfo.isInclude(i++)) buf.append ("Total Est. Cost");
			}
			buf.append("\n");
		}

		boolean bFirst = true;
		for (Iterator iter = equipmentOnRentInfo.getItems(); iter.hasNext(); ) {
			EquipmentOnRentItemInfo item = (EquipmentOnRentItemInfo) iter.next();
			if (! bFirst) buf.append("\n");
			bFirst = false;
			int i = 0;
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getAccountNumber());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getBusiness());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getAddress1());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getCity());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getState());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getCountry());

			csvTemplateInfo.applyColumn (buf, i++, item.getContract());
			csvTemplateInfo.applyColumn (buf, i++, item.getStartDate());
			csvTemplateInfo.applyColumn (buf, i++, item.getEstimatedReturnDate());
			csvTemplateInfo.applyColumn (buf, i++, item.getItem());
			csvTemplateInfo.applyColumn (buf, i++, item.getQuantity());
			csvTemplateInfo.applyColumn (buf, i++, item.getDescription());

			csvTemplateInfo.applyColumn (buf, i++, item.getPickupTicket());
			csvTemplateInfo.applyColumn (buf, i++, item.getOrderedBy());
			csvTemplateInfo.applyColumn (buf, i++, item.getPurchaseOrder());

			if (item.isOverdueContract())
				csvTemplateInfo.applyColumn (buf, i++, "Yes");
			else
				csvTemplateInfo.applyColumn (buf, i++, "No");
				
			csvTemplateInfo.applyColumn (buf, i++, item.getJobName());
			csvTemplateInfo.applyColumn (buf, i++, item.getDayRate());
			csvTemplateInfo.applyColumn (buf, i++, item.getWeekRate());
			if (equipmentOnRentInfo.isUS()) {
				csvTemplateInfo.applyColumn (buf, i++, item.getMonthRate());
				csvTemplateInfo.applyColumn (buf, i++, item.getTotalBilled());
				csvTemplateInfo.applyColumn (buf, i++, item.getTotalAccrued());
				csvTemplateInfo.applyColumn (buf, i++, item.getTotalEstimatedCost(), true);
			}
			else {
				csvTemplateInfo.applyColumn (buf, i++, item.getMonthRate(), true);
			}
		}
		return buf;
	}

	/**
	 * Get CSV data for the Open Invoices report
	 * 
	 * @param csvTemplateInfo			Csv templating instructions
	 * @param openInvoiceInfo			Open Invoice data
	 * @return							CSV data for the report
	 */
	public static StringBuffer getOpenInvoice (CSVTemplateInfo csvTemplateInfo, OpenInvoiceInfo openInvoiceInfo) {
		StringBuffer buf = new StringBuffer();
		if (csvTemplateInfo.isHeader()) {
			int i = 0;
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Account#,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Account Name,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Address,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("City,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("State,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Country,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Invoice #,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Inv Date,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Loc,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Status,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Original,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Balance,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Purchase Order,");
			if (csvTemplateInfo.isInclude(i++)) buf.append ("Banner");
			buf.append("\n");
		}

		boolean bFirst = true;
		for (Iterator iter = openInvoiceInfo.getItems(); iter.hasNext(); ) {
			OpenInvoiceItemInfo item = (OpenInvoiceItemInfo) iter.next();
			if (! bFirst) buf.append("\n");
			bFirst = false;
			int i = 0;
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getAccountNumber());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getBusiness());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getAddress1());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getCity());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getState());
			csvTemplateInfo.applyColumn (buf, i++, item.getRmAccountType().getCountry());
			csvTemplateInfo.applyColumn (buf, i++, item.getContract() + "-" + item.getSequence());
			csvTemplateInfo.applyColumn (buf, i++, item.getDuedate());
			csvTemplateInfo.applyColumn (buf, i++, item.getLocation());
			csvTemplateInfo.applyColumn (buf, i++, item.getStatus());
			csvTemplateInfo.applyColumn (buf, i++, item.getOriginal());
			csvTemplateInfo.applyColumn (buf, i++, item.getBalance());
			csvTemplateInfo.applyColumn (buf, i++, item.getPurchaseOrder());
			csvTemplateInfo.applyColumn (buf, i++, item.getBanner(), true);
		}
		return buf; 
	}
}
