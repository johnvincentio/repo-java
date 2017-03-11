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
	 * @param equipmentHistoryInfo		Equipment History data
	 * @return							CSV data for the report
	 */
	public static StringBuffer getEquipmentHistory (EquipmentHistoryInfo equipmentHistoryInfo) {
		StringBuffer buf = new StringBuffer();
		applyColumn (buf, "Account#");
		applyColumn (buf, "Account Name");
		applyColumn (buf, "Address");
		applyColumn (buf, "City");
		applyColumn (buf, "State");
		applyColumn (buf, "Country");
		applyColumn (buf, "Cat-Class");
		applyColumn (buf, "Description");
		applyColumn (buf, "Rental Days");
		applyColumn (buf, "# of Trans");
		applyColumn (buf, "Rental Amount");
		applyColumn (buf, "Rental Year");
		applyColumn (buf, " ", true);
		buf.append("\n");

		boolean bFirst = true;
		for (Iterator iter = equipmentHistoryInfo.getItems(); iter.hasNext(); ) {
			EquipmentHistoryItemInfo item = (EquipmentHistoryItemInfo) iter.next();
			if (! bFirst) buf.append("\n");
			bFirst = false;

			applyColumn (buf, item.getRmAccountType().getAccountNumber());
			applyColumn (buf, item.getRmAccountType().getBusiness());
			applyColumn (buf, item.getRmAccountType().getAddress1());
			applyColumn (buf, item.getRmAccountType().getCity());
			applyColumn (buf, item.getRmAccountType().getState());
			applyColumn (buf, item.getRmAccountType().getCountry());
			applyColumn (buf, item.getCategory() + "-" + item.getClassification());
			applyColumn (buf, item.getDescription());
			applyColumn (buf, item.getRentalDays());
			applyColumn (buf, item.getTransactions());
			applyColumn (buf, item.getRentalAmount());
			applyColumn (buf, item.getRentalYear());
			applyColumn (buf, " ", true);
		}
		return buf;
	}

	/**
	 * Get CSV data for the Equipment Detail History report
	 * 
	 * @param equipmentDetailHistoryInfo	Equipment DetailHistory data
	 * @return								CSV data for the report
	 */
	public static StringBuffer getEquipmentDetailHistory (EquipmentDetailHistoryInfo equipmentDetailHistoryInfo) {
		StringBuffer buf = new StringBuffer();
		applyColumn (buf, "Account#");
		applyColumn (buf, "Account Name");
		applyColumn (buf, "Address");
		applyColumn (buf, "City");
		applyColumn (buf, "State");
		applyColumn (buf, "Country");
		applyColumn (buf, "Cat-Class");
		applyColumn (buf, "Description");
		applyColumn (buf, "Equip#");
		applyColumn (buf, "Invoice");
		applyColumn (buf, "Invoice Date");
		applyColumn (buf, "Start Date");
		applyColumn (buf, "Return Date");
		applyColumn (buf, "Rate Used");
		applyColumn (buf, "Rental Amount");
		applyColumn (buf, "Rental Year");
		applyColumn (buf, " ", true);
		buf.append("\n");

		boolean bFirst = true;
		for (Iterator iter = equipmentDetailHistoryInfo.getItems(); iter.hasNext(); ) {
			EquipmentDetailHistoryItemInfo item = (EquipmentDetailHistoryItemInfo) iter.next();
			if (! bFirst) buf.append("\n");
			bFirst = false;

			applyColumn (buf, item.getRmAccountType().getAccountNumber());
			applyColumn (buf, item.getRmAccountType().getBusiness());
			applyColumn (buf, item.getRmAccountType().getAddress1());
			applyColumn (buf, item.getRmAccountType().getCity());
			applyColumn (buf, item.getRmAccountType().getState());
			applyColumn (buf, item.getRmAccountType().getCountry());
			applyColumn (buf, item.getCategory() + "-" + item.getClassification());
			applyColumn (buf, item.getDescription());
			applyColumn (buf, item.getEquipmentNumber());
			applyColumn (buf, item.getContractNumber() + "-" +item.getSequence());
			applyColumn (buf, item.getInvoiceDate());
			applyColumn (buf, item.getStartDate());
			applyColumn (buf, item.getReturnDate());
			applyColumn (buf, item.getRateUsed());
			applyColumn (buf, item.getRentalAmount());
			applyColumn (buf, item.getRentalYear());
			applyColumn (buf, " ", true);
		}
		return buf; 
	}

	/**
	 * Get CSV data for the Equipment On Rent report
	 * 
	 * @param equipmentOnRentInfo		Equipment on Rent data
	 * @return							CSV data for the report
	 */
	public static StringBuffer getEquipmentOnRent (EquipmentOnRentInfo equipmentOnRentInfo) {
		StringBuffer buf = new StringBuffer();
		applyColumn (buf, "Account#");
		applyColumn (buf, "Account Name");
		applyColumn (buf, "Address");
		applyColumn (buf, "City");
		applyColumn (buf, "State");
		applyColumn (buf, "Country");
		applyColumn (buf, "Contract#");
		applyColumn (buf, "Start Date");
		applyColumn (buf, "Est Return Date");
		applyColumn (buf, "Equipment#");
		applyColumn (buf, "Quantity");
		applyColumn (buf, "Description");
		applyColumn (buf, "On Pickup Tkt");
		applyColumn (buf, "Ordered By");
		applyColumn (buf, "Purchase Order");
		applyColumn (buf, "Overdue");
		applyColumn (buf, "Job Name");
		applyColumn (buf, "Daily Rate");
		applyColumn (buf, "Weekly Rate");
		applyColumn (buf, "4 Week Rate");
		if (equipmentOnRentInfo.isUS()) {
			applyColumn (buf, "Total Rental Charges Billed");
			applyColumn (buf, "Est. Rental Charges to Date");
			applyColumn (buf, "Total Est. Rental Charges");
		}
		applyColumn (buf, " ", true);
		buf.append("\n");

		boolean bFirst = true;
		for (Iterator iter = equipmentOnRentInfo.getItems(); iter.hasNext(); ) {
			EquipmentOnRentItemInfo item = (EquipmentOnRentItemInfo) iter.next();
			if (! bFirst) buf.append("\n");
			bFirst = false;

			applyColumn (buf, item.getRmAccountType().getAccountNumber());
			applyColumn (buf, item.getRmAccountType().getBusiness());
			applyColumn (buf, item.getRmAccountType().getAddress1());
			applyColumn (buf, item.getRmAccountType().getCity());
			applyColumn (buf, item.getRmAccountType().getState());
			applyColumn (buf, item.getRmAccountType().getCountry());
			applyColumn (buf, item.getContract());
			applyColumn (buf, item.getStartDate());
			applyColumn (buf, item.getEstimatedReturnDate());
			applyColumn (buf, item.getItem());
			applyColumn (buf, item.getQuantity());
			applyColumn (buf, item.getDescription());
			applyColumn (buf, item.getPickupTicket());
			applyColumn (buf, item.getOrderedBy());
			applyColumn (buf, item.getPurchaseOrder());
			if (item.isOverdueContract())
				applyColumn (buf, "Yes");
			else
				applyColumn (buf, "No");
			applyColumn (buf, item.getJobName());
			applyColumn (buf, item.getDayRate());
			applyColumn (buf, item.getWeekRate());
			applyColumn (buf, item.getMonthRate());
			if (equipmentOnRentInfo.isUS()) {
				applyColumn (buf, item.getTotalBilled());
				applyColumn (buf, item.getTotalAccrued());
				applyColumn (buf, item.getTotalEstimatedCost());
			}
			applyColumn (buf, " ", true);
		}
		return buf;
	}

	/**
	 * Get CSV data for the Open Invoices report
	 * 
	 * @param openInvoiceInfo			Open Invoice data
	 * @return							CSV data for the report
	 */
	public static StringBuffer getOpenInvoice (OpenInvoiceInfo openInvoiceInfo) {
		StringBuffer buf = new StringBuffer();
		applyColumn (buf, "Account#");
		applyColumn (buf, "Account Name");
		applyColumn (buf, "Address");
		applyColumn (buf, "City");
		applyColumn (buf, "State");
		applyColumn (buf, "Country");
		applyColumn (buf, "Invoice #");
		applyColumn (buf, "Inv Date");
		applyColumn (buf, "Loc");
		applyColumn (buf, "Status");
		applyColumn (buf, "Original");
		applyColumn (buf, "Balance");
		applyColumn (buf, "Purchase Order");
		applyColumn (buf, " ", true);
		buf.append("\n");

		boolean bFirst = true;
		for (Iterator iter = openInvoiceInfo.getItems(); iter.hasNext(); ) {
			OpenInvoiceItemInfo item = (OpenInvoiceItemInfo) iter.next();
			if (! bFirst) buf.append("\n");
			bFirst = false;

			applyColumn (buf, item.getRmAccountType().getAccountNumber());
			applyColumn (buf, item.getRmAccountType().getBusiness());
			applyColumn (buf, item.getRmAccountType().getAddress1());
			applyColumn (buf, item.getRmAccountType().getCity());
			applyColumn (buf, item.getRmAccountType().getState());
			applyColumn (buf, item.getRmAccountType().getCountry());
			applyColumn (buf, item.getContract() + "-" + item.getSequence());
			applyColumn (buf, item.getDuedate());
			applyColumn (buf, item.getLocation());
			applyColumn (buf, item.getStatus());
			applyColumn (buf, item.getOriginal());
			applyColumn (buf, item.getBalance());
			applyColumn (buf, item.getPurchaseOrder());
			applyColumn (buf, " ", true);
		}
		return buf; 
	}

	/**
	 * Apply data to the StringBuffer
	 * 
	 * @param buf		Data will be applied to this object
	 * @param data		Data
	 */
	public static void applyColumn (StringBuffer buf, String data) {
		applyColumn (buf, data, false);
	}

	/**
	 * Apply data to the StringBuffer
	 * 
	 * @param buf		Data will be applied to this object
	 * @param data		Data
	 * @param last		true if the last column of a row.
	 */
	public static void applyColumn (StringBuffer buf, String data, boolean last) {
		String str = data.replace('"', ' ');
		buf.append ("\"").append (str).append("\"");
		if (! last) buf.append (",");
	}
}
