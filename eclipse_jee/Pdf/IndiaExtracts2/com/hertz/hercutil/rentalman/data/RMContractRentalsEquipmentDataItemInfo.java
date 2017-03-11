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

/**
 * @author John Vincent
 *
 */

public class RMContractRentalsEquipmentDataItemInfo implements Serializable {
	private int countryCode;
	private String account;
	private int category;
	private int classification;
	private int hashCode = 0;
	public RMContractRentalsEquipmentDataItemInfo (int countryCode, String account, int category, int classification) {
		this.countryCode = countryCode;
		this.account = account;
		this.category = category;
		this.classification = classification;
		hashCode = (Integer.toString(countryCode) + ";" + account + ";" + 
				Integer.toString(category) + ";" + Integer.toString(classification)).hashCode();
	}
	public int getCountryCode() {return countryCode;}
	public String getAccount() {return account;}
	public int getCategory() {return category;}
	public int getClassification() {return classification;}
	public int hashCode() {return hashCode;}
	public boolean equals (Object obj) {
		if (obj == null || ! (obj instanceof RMContractRentalsEquipmentDataItemInfo)) return false;
		if (this.hashCode != obj.hashCode()) return false;
		return true;
	}
	public String toString() {
		return "("+hashCode()+","+getCountryCode()+","+getAccount()+","+getCategory()+","+getClassification()+")";
	}
}
