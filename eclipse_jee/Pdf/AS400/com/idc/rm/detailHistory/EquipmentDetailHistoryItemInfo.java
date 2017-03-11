package com.idc.rm.detailHistory;

public class EquipmentDetailHistoryItemInfo {

	private String account;
	private String accountName;

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

	public EquipmentDetailHistoryItemInfo (EquipmentDetailHistoryBean equipmentDetailHistoryBean, 
			String account, String accountName,
			String category, String classification, String rentalYear) throws Exception {

		this.account = account;
		this.accountName = accountName;

		this.category = category;
		this.classification = classification;

		this.equipmentNumber = equipmentDetailHistoryBean.getColumn("RDITEM");

		this.contractNumber = equipmentDetailHistoryBean.getColumn("RDCON#");

		this.sequence = equipmentDetailHistoryBean.getColumn("RDISEQ");
		while (this.sequence.length() < 3 ) {
			this.sequence = "0" + this.sequence;
		}

		this.detailSequence = equipmentDetailHistoryBean.getColumn("RDSEQ#");

		this.invoiceDate = equipmentDetailHistoryBean.getColumn("RDSYSD");
		if (! this.invoiceDate.trim().equals("0") && this.invoiceDate.length() == 8)
			this.invoiceDate = this.invoiceDate.substring(4, 6) + "/" + this.invoiceDate.substring(6, 8) + "/" + this.invoiceDate.substring(2, 4);
		else
			this.invoiceDate = "";

		startDate = equipmentDetailHistoryBean.getColumn("RDDATO");
		if (! startDate.trim().equals("0") && startDate.length() == 8)
			startDate = startDate.substring(4, 6) + "/" + startDate.substring(6, 8) + "/" + startDate.substring(2, 4);
		else
			startDate = "";

		String returnDate = equipmentDetailHistoryBean.getColumn("RDDATI");
		if (! returnDate.trim().equals("0") && returnDate.length() == 8)
			returnDate = returnDate.substring(4, 6) + "/" + returnDate.substring(6, 8) + "/" + returnDate.substring(2, 4);
		else
			returnDate = "";

		this.rateUsed = equipmentDetailHistoryBean.getColumn("RDRATU");
		if (this.rateUsed.trim().equals("H") ) 
			this.rateUsed = "DAY";
		else if (this.rateUsed.equals("D")) 
			this.rateUsed = "DAY";
		else if (this.rateUsed.equals("W")) 
			this.rateUsed = "WEEK";
		else if (this.rateUsed.equals("M")) 
			this.rateUsed = "MONTH";

		this.rentalAmount = equipmentDetailHistoryBean.getColumn("RDAMT$");

		this.rentalYear = rentalYear;
	}

	public boolean isReRentItem() {
		if ("975".equals(category) && "0001".equals(classification)) return true;
		return false;
	}
	public String getDetailSequence() {return detailSequence;}
	public String getContractNumber() {return contractNumber;}
	public String getSequence() {return sequence;}
	public void setItemComments (String itemComments) {
		if (itemComments == null) return;
		if (itemComments.trim().length() < 1) return;
		description += " *** COMMENT: " + itemComments.trim();
	}

	public StringBuffer getCsvData() {
		StringBuffer buf = new StringBuffer();
		buf.append (account).append(",");
		buf.append (accountName).append(",");
		buf.append (category).append("-").append(classification).append(",");
		buf.append (description).append(",");
		buf.append (equipmentNumber).append(",");
		buf.append (contractNumber).append("-").append(sequence).append(",");
		buf.append (invoiceDate).append(",");
		buf.append (startDate).append(",");
		buf.append (returnDate).append(",");
		buf.append (rateUsed).append(",");
		buf.append (rentalAmount).append(",");
		buf.append (rentalYear);
		return buf;
	}

	public String toString() {
		return "("+account+","+accountName+","+category+","+classification+","+description+","+
				equipmentNumber+","+contractNumber+","+sequence+","+detailSequence+","+invoiceDate+","+
				startDate+","+returnDate+","+rateUsed+","+rentalAmount+","+rentalYear+")";
	}
}
