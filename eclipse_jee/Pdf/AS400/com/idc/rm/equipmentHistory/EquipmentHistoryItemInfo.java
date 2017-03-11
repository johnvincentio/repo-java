package com.idc.rm.equipmentHistory;

import java.io.Serializable;

public class EquipmentHistoryItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String account;
	private String accountName;
	private String category;
	private String classification;
	private String description;
	private String rentalDays;
	private String transactions;
	private String rentalAmount;
	private String rentalYear;
	public EquipmentHistoryItemInfo (EquipmentHistoryBean equipmentHistoryBean, 
			String account, String accountName, String rentalYear) throws Exception {
		this.account = account;
		this.accountName = accountName;
		this.category = equipmentHistoryBean.getColumn("RDCATG");
		while (this.category.length() < 3) {
			this.category = "0" + this.category;
		}
		this.classification = equipmentHistoryBean.getColumn("RDCLAS");
		while (this.classification.length() < 4) {
			this.classification = "0" + this.classification;
		}
		this.description = equipmentHistoryBean.getColumn("ECDESC").replace('"', ' ');
		this.rentalDays = equipmentHistoryBean.getColumn("totalrentdays");
		this.transactions = equipmentHistoryBean.getColumn("totalTransactions");
		this.rentalAmount = equipmentHistoryBean.getColumn("totalrentamount");
		this.rentalYear = rentalYear;
	}

	public StringBuffer getCsvData() {
		StringBuffer buf = new StringBuffer();
		buf.append (account).append(",");
		buf.append (accountName).append(",");
		buf.append (category).append("-").append(classification).append(",");
		buf.append (description).append(",");
		buf.append (rentalDays).append(",");
		buf.append (transactions).append(",");
		buf.append (rentalAmount).append(",");
		buf.append (rentalYear);
		return buf;
	}
}
