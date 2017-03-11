package com.idc.rm.equipmentOnRent;

import java.io.Serializable;

public class PendingExtendItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

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
