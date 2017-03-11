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
 * Class to encapsulate the Pending Release Record
 * 
 * @author John Vincent
 */

public class PendingReleaseItemInfo implements Serializable {
	private String contract;
	private String sequence;
	private String quantity;
	public PendingReleaseItemInfo (String contract, String sequence, String quantity) {
		this.contract = contract;
		this.sequence = sequence;
		this.quantity = quantity;
		int i = this.quantity.indexOf(".");
		if (i > 0) this.quantity = this.quantity.substring(0,i);
	}
	public String getContract() {return contract;}
	public String getSequence() {return sequence;}
	public String getQuantity() {return quantity;}

	public String toString() {
		return "("+getContract()+","+getSequence()+","+getQuantity()+")";
	}
}
