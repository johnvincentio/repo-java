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
 * Describe a contract location. Branch is a Herc location.
 */

public class RMContractLocationsDataItemInfo implements Serializable {
	private int countryCode;
	private String account;
	private String branch;
	private int hashCode = 0;
	public RMContractLocationsDataItemInfo (int countryCode, String account, String branch) {
		this.countryCode = countryCode;
		this.account = account;
		this.branch = branch;
		hashCode = (Integer.toString(countryCode) + ";" + account + ";" + branch).hashCode();
	}
	public int getCountryCode() {return countryCode;}
	public String getAccount() {return account;}
	public String getBranch() {return branch;}

	public boolean equals (Object obj) {
		if (obj == null || ! (obj instanceof RMContractLocationsDataItemInfo)) return false;
		if (this.hashCode != obj.hashCode()) return false;
		return true;
	}
	public int hashCode() {return hashCode;}

	public String toString() {
		return "("+hashCode()+","+getCountryCode()+","+getAccount()+","+getBranch()+")";
	}
}
