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

package com.hertz.hercutil.company;

import java.io.Serializable;

/**
 * @author John Vincent
 *
 * Describe a contract location. Branch is a Herc location.
 */

public class ContractLocationsItemInfo implements Serializable {
	private String branch;
	private int hashCode = 0;
	public ContractLocationsItemInfo (String branch) {
		this.branch = branch;
		hashCode = branch.hashCode();
	}
	public ContractLocationsItemInfo (ContractLocationsItemInfo info) {
		this.branch = info.getBranch();
	}
	public String getBranch() {return branch;}
	public boolean equals (Object obj) {
		if (obj == null || ! (obj instanceof ContractLocationsItemInfo)) return false;
		if (this.hashCode != obj.hashCode()) return false;
		return true;
	}
	public int hashCode() {return hashCode;}

	public String toString() {return "("+hashCode()+","+getBranch()+")";}
}
