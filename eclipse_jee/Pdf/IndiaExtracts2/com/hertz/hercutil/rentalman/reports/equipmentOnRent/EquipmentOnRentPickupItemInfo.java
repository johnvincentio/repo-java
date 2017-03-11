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

package com.hertz.hercutil.rentalman.reports.equipmentOnRent;

import java.io.Serializable;

/**
 * Class to encapsulate the Equipment On Rent Pickup Record
 * 
 * @author John Vincent
 */

public class EquipmentOnRentPickupItemInfo implements Serializable {
	private String contract;
	private String sequence;
	private String equipment;
	private String ticket;

	public EquipmentOnRentPickupItemInfo (String contract, String sequence, String equipment, String ticket) {
		this.contract = contract;
		this.sequence = sequence;
		this.equipment = equipment;
		this.ticket = ticket;
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
