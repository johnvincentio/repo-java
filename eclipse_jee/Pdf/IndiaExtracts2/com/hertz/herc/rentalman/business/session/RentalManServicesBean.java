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

package com.hertz.herc.rentalman.business.session;

import java.sql.Connection;

import com.hertz.herc.rentalman.business.dao.DBRentalManConstants;
import com.hertz.herc.rentalman.reports.dao.EquipmentDetailHistoryDAO;
import com.hertz.herc.rentalman.reports.dao.EquipmentHistoryDAO;
import com.hertz.herc.rentalman.reports.dao.EquipmentOnRentDAO;
import com.hertz.herc.rentalman.reports.dao.OpenInvoiceDAO;
import com.hertz.herc.rentalman.reports.dao.PendingTransactionsDAO;
import com.hertz.hercutil.company.RMAccountType;
import com.hertz.hercutil.presentation.DAOUtils;
import com.hertz.hercutil.rentalman.reports.equipmentDetailHistory.EquipmentDetailHistoryInfo;
import com.hertz.hercutil.rentalman.reports.equipmentHistory.EquipmentHistoryInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.EquipmentOnRentInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.EquipmentOnRentPickupInfo;
import com.hertz.hercutil.rentalman.reports.equipmentOnRent.PendingTransactionsInfo;
import com.hertz.hercutil.rentalman.reports.openInvoice.OpenInvoiceInfo;
import com.hertz.irac.framework.HertzSystemException;

public class RentalManServicesBean {

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
	public EquipmentHistoryInfo getEquipmentHistoryInfo (RMAccountType rmAccountType,  
			String jobNumber, String selectedYear, 
			String orderBy, int start_num) throws HertzSystemException {

		Connection connection = null;
		try {
			connection = DAOUtils.getDSConnection (DBRentalManConstants.getDatasourceName(rmAccountType.getCountryCode()));
			EquipmentHistoryInfo equipmentHistoryInfo = 
				(new EquipmentHistoryDAO()).performExtract (connection, 
						DBRentalManConstants.getRentalManCode (rmAccountType.getCountryCode()), 
						rmAccountType, jobNumber, selectedYear, orderBy, start_num);
			return equipmentHistoryInfo;
		}
		catch (Exception ex) {
			throw new HertzSystemException("Exception in getEquipmentHistoryInfo) " + ex);
		}
		finally {
			try {
				if (connection != null) DAOUtils.closeConnection(connection);
			}
			catch (Exception cex) {}
		}
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
	public EquipmentDetailHistoryInfo getEquipmentDetailHistoryInfo (RMAccountType rmAccountType,  
			String category, String classification, String selectedYear,
			String jobNumber, String orderBy) throws HertzSystemException {

		Connection connection = null;
		try {
			connection = DAOUtils.getDSConnection (DBRentalManConstants.getDatasourceName(rmAccountType.getCountryCode()));
			EquipmentDetailHistoryInfo equipmentDetailHistoryInfo = 
				(new EquipmentDetailHistoryDAO()).performExtract (connection, 
						DBRentalManConstants.getRentalManCode(rmAccountType.getCountryCode()), 
						rmAccountType, 
						category, classification, selectedYear,
						jobNumber, orderBy);
			return equipmentDetailHistoryInfo;
		}
		catch (Exception ex) {
			throw new HertzSystemException("Exception in getEquipmentDetailHistoryInfo) " + ex);
		}
		finally {
			try {
				if (connection != null) DAOUtils.closeConnection(connection);
			}
			catch (Exception cex) {}
		}
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
	public OpenInvoiceInfo getOpenInvoiceInfo (RMAccountType rmAccountType,  
			String orderBy, int start_num) throws HertzSystemException {

		Connection connection = null;
		try {
			connection = DAOUtils.getDSConnection (DBRentalManConstants.getDatasourceName(rmAccountType.getCountryCode()));
			OpenInvoiceInfo openInvoiceInfo = 
				(new OpenInvoiceDAO()).performExtract (connection, 
						DBRentalManConstants.getRentalManCode(rmAccountType.getCountryCode()), 
						rmAccountType, orderBy, start_num);
			return openInvoiceInfo;
		}
		catch (Exception ex) {
			throw new HertzSystemException("Exception in getOpenInvoiceInfo) " + ex);
		}
		finally {
			try {
				if (connection != null) DAOUtils.closeConnection(connection);
			}
			catch (Exception cex) {}
		}
	}

	/**
	 * Get equipment on rent
	 * 
	 * @param bEquipmentOnRent					true if Equipment on rent, else Overdue Rentals
	 * @param bAllowReleases					true => equipment rental release is allowed
	 * @param bAllowExtend						true => equipment rental extension is allowed
	 * @param rmAccountType						Account
	 * @param jobnumber							Job number
	 * @param orderBy							Order by clause
	 * @return									Equipment on rent
	 * @throws HertzSystemException
	 */
	public EquipmentOnRentInfo getEquipmentOnRentInfo (
			boolean bEquipmentOnRent,
			boolean bAllowReleases, boolean bAllowExtend,
			RMAccountType rmAccountType, String jobnumber, String orderBy) throws HertzSystemException {
		
		Connection connection = null;
		PendingTransactionsInfo pendingTransactionsInfo;

		try {
			connection = DAOUtils.getDSConnection (DBRentalManConstants.getDatasourceName(rmAccountType.getCountryCode()));

			if (rmAccountType.getCountryCode() == 1 && 
					(bAllowReleases || bAllowExtend)) {
				pendingTransactionsInfo = (new PendingTransactionsDAO()).performExtract (
							connection, 
							DBRentalManConstants.getRentalManCode(rmAccountType.getCountryCode()),
							rmAccountType);
			}
			else
				pendingTransactionsInfo = new PendingTransactionsInfo();

			EquipmentOnRentPickupInfo equipmentOnRentPickupInfo = 
				(new EquipmentOnRentDAO()).getPickup (connection, 
						DBRentalManConstants.getRentalManCode(rmAccountType.getCountryCode()),
						rmAccountType);

			EquipmentOnRentInfo equipmentOnRentInfo = 
				(new EquipmentOnRentDAO()).performExtract (connection, 
						DBRentalManConstants.getRentalManCode(rmAccountType.getCountryCode()), 
						equipmentOnRentPickupInfo, pendingTransactionsInfo, bEquipmentOnRent, 
						rmAccountType, jobnumber, orderBy);
			return equipmentOnRentInfo;
		}
		catch (Exception ex) {
			throw new HertzSystemException("Exception in getEquipmentOnRentInfo) " + ex);
		}
		finally {
			try {
				if (connection != null) DAOUtils.closeConnection(connection);
			}
			catch (Exception cex) {}
		}
	}
}
