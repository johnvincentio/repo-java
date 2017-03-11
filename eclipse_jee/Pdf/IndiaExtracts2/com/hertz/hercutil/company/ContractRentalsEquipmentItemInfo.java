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
 */

public class ContractRentalsEquipmentItemInfo implements Serializable {
	private int category;
	private int classification;
	public ContractRentalsEquipmentItemInfo (String category, String classification) {
		this.category = Integer.parseInt(category);
		this.classification = Integer.parseInt(classification);
	}
	public ContractRentalsEquipmentItemInfo (int category, int classification) {
		this.category = category;
		this.classification = classification;
	}
	public ContractRentalsEquipmentItemInfo (ContractRentalsEquipmentItemInfo info) {
		this.category = info.category;
		this.classification = info.classification;
	}
	public int getCategory() {return category;}
	public int getClassification() {return classification;}

	public String toString() {return "("+getCategory()+","+getClassification()+")";}
}
