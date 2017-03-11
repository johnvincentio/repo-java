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

package com.hertz.hercutil.rentalman.reports.equipmentDetailHistory;

import java.io.Serializable;

import com.hertz.hercutil.company.RMAccountType;
import com.hertz.hercutil.framework.DateHelper;

/**
 * Class to encapsulate the Equipment Detail History Record
 * 
 * @author John Vincent
 */

public class EquipmentDetailHistoryItemInfo implements Serializable {

	private RMAccountType rmAccountType;

	private String category;
	private String classification;

	private String description;
	private String equipmentNumber;
	private String contractNumber;
	private String sequence;
	private String detailSequence;
	private String invoiceDate;
	private String startDate;
	private String returnDate;
	private String rateUsed;
	private String rentalAmount;
	private String rentalYear;

	public EquipmentDetailHistoryItemInfo (RMAccountType rmAccountType, 
			String category, String classification, 
			String equipmentNumber, String contractNumber, String sequence, String detailSequence, 
			String invoiceDate, String startDate, String returnDate, 
			String rateUsed, String rentalAmount, String rentalYear, String description) {

		this.rmAccountType = rmAccountType;
		this.category = category;
		this.classification = classification;
		this.description = "";
		this.equipmentNumber = equipmentNumber;
		this.contractNumber = contractNumber;
		this.sequence = sequence;
		while (this.sequence.length() < 3 ) {
			this.sequence = "0" + this.sequence;
		}

		this.detailSequence = detailSequence;

		this.invoiceDate = DateHelper.formatEtrieveDate(invoiceDate);

		this.startDate = DateHelper.formatEtrieveDate(startDate);

		this.returnDate = DateHelper.formatEtrieveDate(returnDate);

		this.rateUsed = rateUsed;
		if (this.rateUsed.equals("H") ) 
			this.rateUsed = "DAY";
		else if (this.rateUsed.equals("D")) 
			this.rateUsed = "DAY";
		else if (this.rateUsed.equals("W")) 
			this.rateUsed = "WEEK";
		else if (this.rateUsed.equals("M")) 
			this.rateUsed = "MONTH";

		this.rentalAmount = rentalAmount;

		this.rentalYear = rentalYear;
		
		this.description = description;
	}

	public boolean isReRentItem() {
		if ("975".equals(category) && "0001".equals(classification)) return true;
		return false;
	}

	public void setItemComments (String itemComments) {
		if (itemComments == null) return;
		if (itemComments.trim().length() < 1) return;
		description += " *** COMMENT: " + itemComments.trim();
	}

	public RMAccountType getRmAccountType() {return rmAccountType;}
	public String getCategory() {return category;}
	public String getClassification() {return classification;}
	public String getDescription() {return description;}
	public String getEquipmentNumber() {return equipmentNumber;}
	public String getContractNumber() {return contractNumber;}
	public String getSequence() {return sequence;}
	public String getDetailSequence() {return detailSequence;}
	public String getInvoiceDate() {return invoiceDate;}
	public String getStartDate() {return startDate;}
	public String getReturnDate() {return returnDate;}
	public String getRateUsed() {return rateUsed;}
	public String getRentalAmount() {return rentalAmount;}
	public String getRentalYear() {return rentalYear;}

	public String toString() {
		return "("+getRmAccountType()+","+getCategory()+","+getClassification()+","+
			getDescription()+","+getEquipmentNumber()+","+getContractNumber()+","+getSequence()+","+getDetailSequence()+","+
			getInvoiceDate()+","+getStartDate()+","+getReturnDate()+","+getRateUsed()+","+getRentalAmount()+","+getRentalYear()+")";
	}
}
