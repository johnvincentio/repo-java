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

package com.hertz.hercutil.rentalman.reports.openInvoice;

import java.io.Serializable;

import com.hertz.hercutil.company.RMAccountType;

/**
 * Class to encapsulate one Open Invoice record 
 * 
 * @author John Vincent
 */

public class OpenInvoiceItemInfo implements Serializable {

	private RMAccountType rmAccountType;

	private String contract;
	private String sequence;
	private String duedate;
	private String location;
	private String status;
	private String original;
	private String balance;
	private String purchaseOrder;
	private String banner;

	public OpenInvoiceItemInfo (RMAccountType rmAccountType, String contract, String sequence,
			String duedate, String location, String s1, String s2,
			String original, String balance, String purchaseOrder, String recordSource) {
		this.rmAccountType = rmAccountType;
		this.contract = contract;

		this.sequence = sequence;
		while (this.sequence.length() < 3) {
			this.sequence = "0" + this.sequence;
		}

		String str_duedate = duedate;
		if (! str_duedate.trim().equals("0") && str_duedate.length() == 8)  {
			str_duedate = str_duedate.substring(4, 6) + "/" + str_duedate.substring(6, 8) + "/" + str_duedate.substring(2, 4);
		} else {
			str_duedate = "";
		}
		this.duedate = str_duedate;

		this.location = location;

		String str_status = "OPEN";
		if (s1.equals("OP")) {
			if (Double.valueOf(s2).doubleValue() > 0)
				str_status = "PARTIAL";
			else
				str_status = "OPEN";
		}
		else {
			str_status = "ERR";
			if (s1.equals("PP")) str_status = "PARTIAL";
		}
		this.status = str_status;

		this.original = original;
		this.balance = balance;
		this.purchaseOrder = purchaseOrder;

		String str_recordsource = recordSource;
		String str_banner ="";
		if (str_recordsource.equals("R1"))
			str_banner = "FULL RETURN";
		else if (str_recordsource.equals("R2"))
			str_banner = "PARTIAL RETURN";
		else if (str_recordsource.equals("R4"))
			str_banner = "EXCHANGE";
		else if (str_recordsource.equals("R5"))
			str_banner = "CREDIT MEMO";
		else if (str_recordsource.equals("R6"))
			str_banner = "CYCLE BILL";
		else if (str_recordsource.equals("R7"))
			str_banner = "RENTL PURCHS";
		else if (str_recordsource.equals("S1"))
			str_banner = "EQUIP SALE";
		else if (str_recordsource.equals("S2"))
			str_banner = "PARTS/MERCHD SALE";
		else 
			str_banner = "";
		this.banner = str_banner;
	}

	public RMAccountType getRmAccountType() {return rmAccountType;}
	public String getContract() {return contract;}
	public String getSequence() {return sequence;}
	public String getDuedate() {return duedate;}
	public String getLocation() {return location;}
	public String getStatus() {return status;}
	public String getOriginal() {return original;}
	public String getBalance() {return balance;}
	public String getPurchaseOrder() {return purchaseOrder;}
	public String getBanner() {return banner;}

	public String toString() {
		return "("+getRmAccountType()+","+getContract()+","+getSequence()+","+getDuedate()+","+
			getLocation()+","+getStatus()+","+getOriginal()+","+getBalance()+","+getPurchaseOrder()+","+getBanner()+")";
	}
}
