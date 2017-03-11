package com.idc.rm.openInvoice;

import java.io.Serializable;

public class OpenInvoiceItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String account;
	private String accountName;
	private String contract;
	private String sequence;
	private String duedate;
	private String location;
	private String status;
	private String original;
	private String balance;
	private String purchaseOrder;
	private String banner;
	public OpenInvoiceItemInfo (OpenInvoiceBean openInvoiceBean, String account, String accountName) throws Exception {
		this.account = account;
		this.accountName = accountName;
		this.contract = openInvoiceBean.getColumn("AHINV#");

		this.sequence = openInvoiceBean.getColumn("AHISEQ");
		while (this.sequence.length() < 3) {
			this.sequence = "0" + this.sequence;
		}

		String str_duedate = openInvoiceBean.getColumn("AHDUED");
		if (! str_duedate.trim().equals("0") && str_duedate.length() == 8)  {
			str_duedate = str_duedate.substring(4, 6) + "/" + 
					str_duedate.substring(6, 8) + "/" + str_duedate.substring(2, 4);
		} else {
			str_duedate = "";
		}
		this.duedate = str_duedate;

		this.location = openInvoiceBean.getColumn("AHLOC");

		String str_status = "OPEN";
		if (openInvoiceBean.getColumn("AHSTTS").equals("OP")) {
			if (Double.valueOf(openInvoiceBean.getColumn("CurrBalance")).doubleValue() > 0)
				str_status = "PARTIAL";
			else
				str_status = "OPEN";
		}
		else {
			str_status = "ERR";
			if (openInvoiceBean.getColumn("AHSTTS").equals("PP")) str_status = "PARTIAL";
		}
		this.status = str_status;

		this.original = openInvoiceBean.getColumn("AHAMT$");
		this.balance = openInvoiceBean.getColumn("AHCBAL");
		this.purchaseOrder = openInvoiceBean.getColumn("RHPO#");

		String str_recordsource = openInvoiceBean.getColumn("AHSRC");
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

	public StringBuffer getCsvData() {
		StringBuffer buf = new StringBuffer();
		buf.append (account).append(",");
		buf.append (accountName).append(",");
		buf.append (contract).append("-").append(sequence).append(",");
		buf.append (duedate).append(",");
		buf.append (location).append(",");
		buf.append (status).append(",");
		buf.append (original).append(",");
		buf.append (balance).append(",");
		buf.append (purchaseOrder).append(",");
		buf.append (banner);
		return buf;
	}
}
