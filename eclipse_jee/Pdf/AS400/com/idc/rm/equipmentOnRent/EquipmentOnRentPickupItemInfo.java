package com.idc.rm.equipmentOnRent;

import java.io.Serializable;

public class EquipmentOnRentPickupItemInfo implements Serializable {
	private String contract;
	private String sequence;
	private String equipment;
	private String ticket;
	public EquipmentOnRentPickupItemInfo (EquipmentOnRentBean equipmentOnRentBean) throws Exception {
		contract = equipmentOnRentBean.getColumn3("RUCON#").trim();
		sequence = equipmentOnRentBean.getColumn3("RUSEQ#").trim();
		equipment = equipmentOnRentBean.getColumn3("RUEQP#").trim();
		ticket = equipmentOnRentBean.getColumn3("RUPKU#").trim();
	}
	public String getContract() {return contract;}
	public String getSequence() {return sequence;}
	public String getEquipment() {return equipment;}
	public String getTicket() {return ticket;}

	public String getRAReleases() {return "**" +contract + "-" + sequence + "-" + equipment + "**";}
	public String getRATicket() {return ticket;}

	public String toString() {
		return "("+contract+","+sequence+","+equipment+","+ticket+")";
	}
}
