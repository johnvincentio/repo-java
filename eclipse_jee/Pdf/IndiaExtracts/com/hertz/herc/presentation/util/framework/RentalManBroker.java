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

import com.hertz.herc.rentalman.business.session.RentalManServicesBean;
import com.hertz.hercutil.company.RMAccountType;
import com.hertz.hercutil.rentalman.reports.equipmentDetailHistory.EquipmentDetailHistoryInfo;
import com.hertz.hercutil.rentalman.reports.equipmentHistory.EquipmentHistoryInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.EquipmentOnRentInfo;
import com.hertz.hercutil.rentalman.reports.openInvoice.OpenInvoiceInfo;
import com.hertz.irac.framework.HertzSystemException;

/**
 * Broker for RentalMan access.
 * 
* @author John Vincent
*/
public class RentalManBroker {

	/**
	 * Get Rental Equipment History
	 * 
	 * @param rmAccountType		Account(s)
	 * @param jobnumber			Job Number
	 * @param selectedYear		Year
	 * @param str_orderBy		Order by clause
	 * @param start_num			Starting record
	 * @return					Rental Equipment History
	 * @throws HertzSystemException
	 */
	public static EquipmentHistoryInfo getEquipmentHistoryInfo (RMAccountType rmAccountType,
			String jobnumber, String selectedYear, 
			String str_orderBy, int start_num) throws HertzSystemException {
		return (new RentalManServicesBean()).getEquipmentHistoryInfo (rmAccountType,
			jobnumber, selectedYear, str_orderBy, start_num);
	}

	/**
	 * Get Detailed Rental Equipment History
	 * 
	 * @param rmAccountType		Account(s)
	 * @param category			Equipment category
	 * @param classification	Equipment classification
	 * @param selectedYear		Year
	 * @param jobNumber			Job Number
	 * @param orderBy			Order by clause
	 * @return					Detailed Rental Equipment History
	 * @throws HertzSystemException
	 */
	public static EquipmentDetailHistoryInfo getEquipmentDetailHistoryInfo (RMAccountType rmAccountType,
			String category, String classification, String selectedYear,
			String jobNumber, String orderBy) throws HertzSystemException {
		return (new RentalManServicesBean()).getEquipmentDetailHistoryInfo (rmAccountType,
			category, classification, selectedYear, jobNumber, orderBy);
	}

	/**
	 * Get Open Invoices
	 * 
	 * @param rmAccountType		Account(s)
	 * @param orderBy			Order by clause
	 * @param start_num			Starting record
	 * @return					Open Invoices
	 * @throws HertzSystemException
	 */
	public static OpenInvoiceInfo getOpenInvoiceInfo (RMAccountType rmAccountType,
			String str_orderBy, int start_num) throws HertzSystemException {
		return (new RentalManServicesBean()).getOpenInvoiceInfo (rmAccountType, str_orderBy, start_num);
	}

	/**
	 * Get equipment on rent
	 * 
	 * @param bEquipmentOnRent					true if Equipment on rent, else Overdue Rentals
	 * @param bEquipmentChangeAuthorization		true if authorized to change a rental
	 * @param bAllowReleases					true => equipment rental release is allowed
	 * @param bAllowExtend						true => equipment rental extension is allowed
	 * @param rmAccountType						Account
	 * @param jobnumber							Job number
	 * @param orderBy							Order by clause
	 * @return									Equipment on rent
	 * @throws HertzSystemException
	 */
	public static EquipmentOnRentInfo getEquipmentOnRentInfo (
			boolean bEquipmentOnRent, 
			boolean bEquipmentChangeAuthorization, boolean bAllowReleases, boolean bAllowExtend,
			RMAccountType rmAccountType,
			String jobnumber, String str_orderBy) throws HertzSystemException {
		return (new RentalManServicesBean()).getEquipmentOnRentInfo (
					bEquipmentOnRent, 
					bEquipmentChangeAuthorization, bAllowReleases, bAllowExtend,
					rmAccountType, jobnumber, str_orderBy);
	}
}
