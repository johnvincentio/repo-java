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

package com.hertz.hercutil.rentalman.reports.equipmentOnRent;

import java.io.Serializable;

/**
 * Class to encapsulate the Pending Extend Record
 * 
 * @author John Vincent
 */

public class PendingExtendItemInfo implements Serializable {

	private String contract;
	private String estimatedReturnDate;
	private String contractStatus;
	public PendingExtendItemInfo (String contract, String estimatedReturnDate, String contractStatus) {
		this.contract = contract;
		this.estimatedReturnDate = estimatedReturnDate;
		this.contractStatus = contractStatus;
	}
	public String getContract() {return contract;}
	public String getEstimatedReturnDate() {return estimatedReturnDate;}

	public boolean isPending() {
		if ("P".equalsIgnoreCase(contractStatus)) return true;
		return false;
	}
	public String toString() {
		return "("+contract+","+estimatedReturnDate+","+contractStatus+")";
	}
}
