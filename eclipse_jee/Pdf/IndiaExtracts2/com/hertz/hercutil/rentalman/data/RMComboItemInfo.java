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

package com.hertz.hercutil.rentalman.data;

import java.io.Serializable;
import java.util.Iterator;

import com.hertz.hercutil.company.ContractLocationsInfo;
import com.hertz.hercutil.company.ContractLocationsItemInfo;
import com.hertz.hercutil.company.ContractRentalsEquipmentInfo;
import com.hertz.hercutil.company.ContractRentalsEquipmentItemInfo;
import com.hertz.hercutil.company.RMAccountInfo;
import com.hertz.hercutil.company.RMAccountType;
import com.hertz.hercutil.company.RMNarpAccountInfo;

/**
 * Class to encapsulate the RMDataInfo objects for a given country
 * 
 * @author John Vincent
 */

public class RMComboItemInfo implements Serializable {
	private int countryCode;
	private RMNarpDataInfo rmNarpDataInfo = new RMNarpDataInfo();
	private RMAccountDataInfo rmAccountDataInfo = new RMAccountDataInfo();
	private RMContractLocationsDataInfo rmContractLocationsDataInfo = new RMContractLocationsDataInfo();
	private RMContractRentalsEquipmentDataInfo rmContractRentalsEquipmentDataInfo = new RMContractRentalsEquipmentDataInfo();

	public RMComboItemInfo (int countryCode) {this.countryCode = countryCode;}

	public RMComboItemInfo (int countryCode, RMNarpDataInfo narpInfo, RMAccountDataInfo accountInfo, 
			RMContractLocationsDataInfo locationsInfo, RMContractRentalsEquipmentDataInfo equipmentInfo) {
		this.countryCode = countryCode;
		this.rmNarpDataInfo = narpInfo;
		this.rmAccountDataInfo = accountInfo;
		this.rmContractLocationsDataInfo = locationsInfo;
		this.rmContractRentalsEquipmentDataInfo = equipmentInfo;
	}

	/**
	 * Handle adding RM account
	 * 
	 * @param demo				true if demo account
	 * @param rmAccountType		RM account object
	 */
	public void handleRMAccount (boolean demo, RMAccountType rmAccountType) {
		if (rmAccountType == null) return;
		if (rmAccountType.isNarp()) {
			RMNarpAccountInfo rmNarpAccountInfo = (RMNarpAccountInfo) rmAccountType;
			if (rmNarpAccountInfo == null) return;
			rmNarpDataInfo.add (demo, rmNarpAccountInfo);
			rmAccountDataInfo.add (demo, rmNarpAccountInfo);
		}
		else {
			RMAccountInfo rmAccountInfo = (RMAccountInfo) rmAccountType;
			if (rmAccountInfo != null) rmAccountDataInfo.add (demo, rmAccountInfo);
		}
	}

	/**
	 * Handle adding RM account. Add accounts that do not already exist in rmComboItemInfo
	 * 
	 * @param demo				true if demo account
	 * @param rmAccountType		RM account object
	 * @param rmComboItemInfo	RM data
	 */
	public void handleRMAccount (boolean demo, RMAccountType rmAccountType, RMComboItemInfo rmComboItemInfo) {
		if (rmAccountType == null) return;
		if (rmAccountType.isNarp()) {
			RMNarpAccountInfo rmNarpAccountInfo = (RMNarpAccountInfo) rmAccountType;
			if (rmNarpAccountInfo == null) return;
			if (! rmComboItemInfo.getRMNarpDataInfo().isExists(rmNarpAccountInfo))
				rmNarpDataInfo.add (demo, rmNarpAccountInfo);		// add narp
			Iterator iter = rmNarpAccountInfo.getItems();
			while (iter.hasNext()) {								// look for local narps to add
				RMAccountInfo rmAccountInfo = (RMAccountInfo) iter.next();
				if (rmAccountInfo == null) continue;
				if (! rmComboItemInfo.getRMAccountDataInfo().isExists(rmAccountInfo))
					rmAccountDataInfo.add (new RMAccountDataItemInfo (		// add local narp
									demo, 
									rmNarpAccountInfo.getNarpAccount().getAccountNumber(),
									rmAccountInfo));
			}
		}
		else {
			RMAccountInfo rmAccountInfo = (RMAccountInfo) rmAccountType;
			if (rmAccountInfo == null) return;
			if (! rmComboItemInfo.getRMAccountDataInfo().isExists(rmAccountInfo))
				rmAccountDataInfo.add (demo, rmAccountInfo);		// add account
		}
	}

	/**
	 * Handle adding ContractLocations
	 * 
	 * @param rmAccountDataItemInfo			RM account object
	 * @param contractLocationsInfo			RM contract locations
	 */
	public void handleRMContractLocations (RMAccountDataItemInfo rmAccountDataItemInfo, ContractLocationsInfo contractLocationsInfo) {
		rmContractLocationsDataInfo.add (rmAccountDataItemInfo.getCountryCode(), rmAccountDataItemInfo.getAccount(), contractLocationsInfo);
	}

	/**
	 * Handle adding ContractRentalsEquipment
	 * 
	 * @param rmAccountDataItemInfo			RM account object
	 * @param contractRentalsEquipmentInfo	RM contract equipment
	 */
	public void handleRMContractRentalsEquipment (RMAccountDataItemInfo rmAccountDataItemInfo, ContractRentalsEquipmentInfo contractRentalsEquipmentInfo) {
		rmContractRentalsEquipmentDataInfo.add (rmAccountDataItemInfo.getCountryCode(), rmAccountDataItemInfo.getAccount(), contractRentalsEquipmentInfo);
	}

	public int getCountryCode() {return countryCode;}
	public RMNarpDataInfo getRMNarpDataInfo() {return rmNarpDataInfo;}
	public RMAccountDataInfo getRMAccountDataInfo() {return rmAccountDataInfo;}
	public RMContractLocationsDataInfo getRMContractLocationsDataInfo() {return rmContractLocationsDataInfo;}
	public RMContractRentalsEquipmentDataInfo getRMContractRentalsEquipmentDataInfo() {return rmContractRentalsEquipmentDataInfo;}

	/**
	 * Get Contract Locations for a given account
	 * 
	 * @param account	account number
	 * @return			Contract Locations
	 */
	public ContractLocationsInfo getContractLocations (String account) {
		ContractLocationsInfo contractLocationsInfo = new ContractLocationsInfo();
		Iterator iter = getRMContractLocationsDataInfo().getItems();
		while (iter.hasNext()) {
			RMContractLocationsDataItemInfo info = (RMContractLocationsDataItemInfo) iter.next();
			if (info == null) continue;
			if (info.equals(account)) 
				contractLocationsInfo.add (new ContractLocationsItemInfo(info.getBranch()));
		}
		return contractLocationsInfo;
	}

	/**
	 * Get Contract Rental Items for a given account
	 * 
	 * @param account	account number
	 * @return			Contract Rental Items
	 */
	public ContractRentalsEquipmentInfo getContractRentalItems (String account) {
		ContractRentalsEquipmentInfo contractRentalsEquipmentInfo = new ContractRentalsEquipmentInfo();
		Iterator iter = getRMContractRentalsEquipmentDataInfo().getItems();
		while (iter.hasNext()) {
			RMContractRentalsEquipmentDataItemInfo info = (RMContractRentalsEquipmentDataItemInfo) iter.next();
			if (info == null) continue;
			if (info.equals(account)) 
				contractRentalsEquipmentInfo.add (new ContractRentalsEquipmentItemInfo (info.getCategory(), info.getClassification()));
		}
		return contractRentalsEquipmentInfo;
	}

	public String toString() {
		return "\n("+getCountryCode()+","+getRMNarpDataInfo()+",\n"+getRMAccountDataInfo()+",\n"+
					getRMContractLocationsDataInfo()+",\n"+getRMContractRentalsEquipmentDataInfo()+")";
	}
}
