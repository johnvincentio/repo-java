package com.idc.rm.detailHistory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class EquipmentDetailHistoryInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList m_list = new ArrayList();

	public Iterator getItems() {return m_list.iterator();}
	public void add (EquipmentDetailHistoryItemInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public StringBuffer getCSV (boolean bHeader) {
		StringBuffer buf = new StringBuffer();
		if (bHeader) buf.append (getCsvHeader()).append("\n");
		boolean bFirst = true;
		for (Iterator iter = getItems(); iter.hasNext(); ) {
			EquipmentDetailHistoryItemInfo item = (EquipmentDetailHistoryItemInfo) iter.next();
			if (! bFirst) buf.append("\n");
			bFirst = false;
			buf.append (item.getCsvData());
		}
		return buf;
	}

	private String getCsvHeader() {
		return "Account#, Account Name, Cat-Class, Description, Equip#, Invoice, Invoice Date, Start Date, " + 
					"Return Date, Rate Used, Rental Amount, Rental Year"; 
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((EquipmentDetailHistoryItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
