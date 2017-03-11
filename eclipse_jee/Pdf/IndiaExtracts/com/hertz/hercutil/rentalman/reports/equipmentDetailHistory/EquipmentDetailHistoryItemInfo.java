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
			String rateUsed, String rentalAmount, String rentalYear) {

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

		this.invoiceDate = invoiceDate;
		if (! this.invoiceDate.equals("0") && this.invoiceDate.length() == 8)
			this.invoiceDate = this.invoiceDate.substring(4, 6) + "/" + this.invoiceDate.substring(6, 8) + "/" + this.invoiceDate.substring(2, 4);
		else
			this.invoiceDate = "";

		this.startDate = startDate;
		if (! startDate.equals("0") && startDate.length() == 8)
			startDate = startDate.substring(4, 6) + "/" + startDate.substring(6, 8) + "/" + startDate.substring(2, 4);
		else
			startDate = "";

		this.returnDate = returnDate;
		if (! returnDate.equals("0") && returnDate.length() == 8)
			returnDate = returnDate.substring(4, 6) + "/" + returnDate.substring(6, 8) + "/" + returnDate.substring(2, 4);
		else
			returnDate = "";

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
