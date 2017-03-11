package com.idc.rm.openInvoice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class OpenInvoiceInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList m_list = new ArrayList();

	public Iterator getItems() {return m_list.iterator();}
	public void add (OpenInvoiceItemInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public StringBuffer getCSV (boolean bHeader) {
		StringBuffer buf = new StringBuffer();
		if (bHeader) buf.append (getCsvHeader()).append("\n");
		boolean bFirst = true;
		for (Iterator iter = getItems(); iter.hasNext(); ) {
			OpenInvoiceItemInfo item = (OpenInvoiceItemInfo) iter.next();
			if (! bFirst) buf.append("\n");
			bFirst = false;
			buf.append (item.getCsvData());
		}
		return buf;
	}

	private String getCsvHeader() {
		return "Account#, Account Name, Invoice #, Inv Date, Loc, Status, Original, Balance, Purchase Order, Banner"; 
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((OpenInvoiceItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}

