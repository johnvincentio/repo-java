package com.idc.rm.equipmentOnRent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class EquipmentOnRentInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList m_list = new ArrayList();
	private int countryCode;
	public EquipmentOnRentInfo (int countryCode) {
		this.countryCode = countryCode;
	}
	public boolean isUS() {return countryCode == 1;}

	public Iterator getItems() {return m_list.iterator();}
	public void add (EquipmentOnRentItemInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public StringBuffer getCSV (boolean bHeader) {
		StringBuffer buf = new StringBuffer();
		if (bHeader) buf.append (getCsvHeader()).append("\n");
		boolean bFirst = true;
		for (Iterator iter = getItems(); iter.hasNext(); ) {
			EquipmentOnRentItemInfo item = (EquipmentOnRentItemInfo) iter.next();
			if (! bFirst) buf.append("\n");
			bFirst = false;
			buf.append (item.getCsvData(isUS()));
		}
		return buf;
	}

	private String getCsvHeader() {
		if (isUS())
			return "Account#, Account Name, Contract#, Start Date, Est Return Date, Equipment#, Quantity, Description, " +
					"On Pickup Tkt, Ordered By, Purchase Order, Overdue, Job Name, Daily rate, Weekly rate, 4 Week rate, " +
					"Total Billed, Est. Cost to Date, Total Est. Cost";
		else
			return "Account#, Account Name, Contract#, Start Date, Est Return Date, Equipment#, Quantity, Description, " +
				"On Pickup Tkt, Ordered By, Purchase Order, Overdue, Job Name, Daily rate, Weekly rate, 4 Week rate";
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((EquipmentOnRentItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
