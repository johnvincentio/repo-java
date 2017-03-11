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

package com.hertz.herc.presentation.util.framework;

import java.util.Iterator;

import com.hertz.hercutil.company.RMAccountType;
import com.hertz.hercutil.company.RMNarpAccountInfo;
import com.hertz.hercutil.rentalman.reports.equipmentDetailHistory.EquipmentDetailHistoryInfo;
import com.hertz.hercutil.rentalman.reports.equipmentHistory.EquipmentHistoryInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.EquipmentOnRentInfo;
import com.hertz.hercutil.rentalman.reports.openInvoice.OpenInvoiceInfo;
import com.hertz.irac.framework.HertzSystemException;

/**
 * Encapsulate tasks related to RentalMan data extracts.
 * 
 * @author John Vincent
 */

public class RentalManDataExtractHelper {
	private static Class classRef = RentalManDataExtractHelper.class;

	/**
	 * Get Rental Equipment History
	 * 
	 * @param rmAccountType		Account(s)
	 * @param selectedYear		Year
	 * @return					Rental Equipment History
	 * @throws HertzSystemException
	 */
	public static EquipmentHistoryInfo doReportEquipmentHistory (RMAccountType rmAccountType, String selectedYear) throws HertzSystemException {
//		LogBroker.debug(classRef, ">>> RentalManDataExtractHelper::doReportEquipmentHistory");

		EquipmentHistoryInfo equipmentHistoryInfo = new EquipmentHistoryInfo();

		String jobNumber = "";
		String orderBy = "";

		if (rmAccountType.isNarp()) {
			RMNarpAccountInfo rmNarpAccountInfo = (RMNarpAccountInfo) rmAccountType;
			Iterator iter = rmNarpAccountInfo.getItems();
			while (iter.hasNext()) {
				RMAccountType rmLocalAccount = (RMAccountType) iter.next();
				if (rmLocalAccount == null) continue;
				equipmentHistoryInfo.add (RentalManBroker.getEquipmentHistoryInfo (rmLocalAccount,
						jobNumber, selectedYear, orderBy, 0));
			}
		}
		else {
			equipmentHistoryInfo.add (RentalManBroker.getEquipmentHistoryInfo (rmAccountType,
					jobNumber, selectedYear, orderBy, 0));
		}
//		LogBroker.debug(classRef, "<<< RentalManDataExtractHelper::doReportEquipmentHistory");
		return equipmentHistoryInfo;
	}

	/**
	 * Get Detailed Rental Equipment History
	 * 
	 * @param rmAccountType		Account(s)
	 * @param category			Equipment category
	 * @param classification	Equipment classification
	 * @param selectedYear		Year
	 * @return					Detailed Rental Equipment History
	 * @throws HertzSystemException
	 */
	public static EquipmentDetailHistoryInfo doEquipmentDetailHistoryInfo (RMAccountType rmAccountType, 
			String category, String classification, String selectedYear) throws HertzSystemException {
//		LogBroker.debug(classRef, ">>> RentalManDataExtractHelper::doEquipmentDetailHistoryInfo");

		EquipmentDetailHistoryInfo equipmentDetailHistoryInfo = new EquipmentDetailHistoryInfo();

		String jobNumber = "";
		String orderBy = "";

		if (rmAccountType.isNarp()) {
			RMNarpAccountInfo rmNarpAccountInfo = (RMNarpAccountInfo) rmAccountType;
			Iterator iter = rmNarpAccountInfo.getItems();
			while (iter.hasNext()) {
				RMAccountType rmLocalAccount = (RMAccountType) iter.next();
				if (rmLocalAccount == null) continue;
				equipmentDetailHistoryInfo.add (RentalManBroker.getEquipmentDetailHistoryInfo (rmLocalAccount,
						category, classification, selectedYear,
						jobNumber, orderBy));
			}
		}
		else {
			equipmentDetailHistoryInfo.add (RentalManBroker.getEquipmentDetailHistoryInfo (rmAccountType,
					category, classification, selectedYear,
					jobNumber, orderBy));
		}
//		LogBroker.debug(classRef, equipmentDetailHistoryInfo.toString());
//		LogBroker.debug(classRef, "<<< RentalManDataExtractHelper::doEquipmentDetailHistoryInfo");
		return equipmentDetailHistoryInfo;
	}

	/**
	 * Get Open Invoices
	 * 
	 * @param rmAccountType		Account(s)
	 * @return					Open Invoices
	 * @throws HertzSystemException
	 */
	public static OpenInvoiceInfo doOpenInvoiceInfo (RMAccountType rmAccountType) throws HertzSystemException {
//		LogBroker.debug(classRef, ">>> RentalManDataExtractHelper::doOpenInvoiceInfo");

		OpenInvoiceInfo openInvoiceInfo = new OpenInvoiceInfo();

		String orderBy = "";

		if (rmAccountType.isNarp()) {
			RMNarpAccountInfo rmNarpAccountInfo = (RMNarpAccountInfo) rmAccountType;
			Iterator iter = rmNarpAccountInfo.getItems();
			while (iter.hasNext()) {
				RMAccountType rmLocalAccount = (RMAccountType) iter.next();
				if (rmLocalAccount == null) continue;
				openInvoiceInfo.add (RentalManBroker.getOpenInvoiceInfo (rmLocalAccount, orderBy, 0));
			}
		}
		else {
			openInvoiceInfo.add (RentalManBroker.getOpenInvoiceInfo (rmAccountType, orderBy, 0));
		}
//		LogBroker.debug(classRef, "<<< RentalManDataExtractHelper::doOpenInvoiceInfo");
		return openInvoiceInfo;
	}

	/**
	 * Get equipment on rent
	 * 
	 * @param bEquipmentOnRent					true if Equipment on rent, else Overdue Rentals
	 * @param bAllowReleases					true => equipment rental release is allowed
	 * @param bAllowExtend						true => equipment rental extension is allowed
	 * @param rmAccountType						Account
	 * @return									Equipment on rent
	 * @throws HertzSystemException
	 */
	public static EquipmentOnRentInfo doEquipmentOnRentInfo (
			boolean bEquipmentOnRent, 
			boolean bAllowReleases, boolean bAllowExtend,
			RMAccountType rmAccountType) throws HertzSystemException {
//		LogBroker.debug(classRef, ">>> RentalManDataExtractHelper::doEquipmentOnRentInfo");

		EquipmentOnRentInfo equipmentOnRentInfo = new EquipmentOnRentInfo(rmAccountType.getCountryCode());

		String jobNumber = "";
		String orderBy = "";

		if (rmAccountType.isNarp()) {
			RMNarpAccountInfo rmNarpAccountInfo = (RMNarpAccountInfo) rmAccountType;
			Iterator iter = rmNarpAccountInfo.getItems();
			while (iter.hasNext()) {
				RMAccountType rmLocalAccount = (RMAccountType) iter.next();
				if (rmLocalAccount == null) continue;
				equipmentOnRentInfo.add (RentalManBroker.getEquipmentOnRentInfo (
						bEquipmentOnRent, 
						bAllowReleases, bAllowExtend,
						rmLocalAccount,
						jobNumber, orderBy));
			}
		}
		else {
			equipmentOnRentInfo.add (RentalManBroker.getEquipmentOnRentInfo (
					bEquipmentOnRent, 
					bAllowReleases, bAllowExtend,
					rmAccountType,
					jobNumber, orderBy));
		}
//		LogBroker.debug(classRef, "<<< RentalManDataExtractHelper::doEquipmentOnRentInfo");
		return equipmentOnRentInfo;
	}
}
