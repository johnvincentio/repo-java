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

package com.hertz.hercutil.rentalman.reports.equipmentHistory;

import java.io.Serializable;

import com.hertz.hercutil.company.RMAccountType;

/**
 * Class to encapsulate one Equipment History record 
 * 
 * @author John Vincent
 */

public class EquipmentHistoryItemInfo implements Serializable {
	private RMAccountType rmAccountType;
	private String category;
	private String classification;
	private String description;
	private String rentalDays;
	private String transactions;
	private String rentalAmount;
	private String rentalYear;
	public EquipmentHistoryItemInfo (RMAccountType rmAccountType, 
			String category, String classification, String description,
			String rentalDays, String transactions, String rentalAmount,
			String rentalYear) {
		this.rmAccountType = rmAccountType;
		this.category = category;
		while (this.category.length() < 3) {
			this.category = "0" + this.category;
		}
		this.classification = classification;
		while (this.classification.length() < 4) {
			this.classification = "0" + this.classification;
		}
		this.description = description;
		this.rentalDays = rentalDays;
		this.transactions = transactions;
		this.rentalAmount = rentalAmount;
		this.rentalYear = rentalYear;
	}

	public RMAccountType getRmAccountType() {return rmAccountType;}
	public String getCategory() {return category;}
	public String getClassification() {return classification;}
	public String getDescription() {return description;}
	public String getRentalDays() {return rentalDays;}
	public String getTransactions() {return transactions;}
	public String getRentalAmount() {return rentalAmount;}
	public String getRentalYear() {return rentalYear;}

	public String toString() {
		return "("+getRmAccountType()+","+getCategory()+","+getClassification()+","+getDescription()+","+
			getRentalDays()+","+getTransactions()+","+getRentalAmount()+","+getRentalYear()+")";
	}
}
