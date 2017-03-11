package com.idc.rm.equipmentOnRent;

import java.io.Serializable;

public class PendingReleaseItemInfo implements Serializable {
	private String contract;
	private String sequence;
	private String quantity;
	public PendingReleaseItemInfo (String contract, String sequence, String quantity) {
		this.contract = contract;
		this.sequence = sequence;
		this.quantity = quantity;
	}
	public String getContract() {return contract;}
	public String getSequence() {return sequence;}
	public String getQuantity() {return quantity;}

	public String toString() {
		return "("+getContract()+","+getSequence()+","+getQuantity()+")";
	}
}
