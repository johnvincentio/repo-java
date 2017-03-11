package com.idc.rm.equipmentOnRent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class PendingTransactionsInfo implements Serializable {
	private ArrayList m_list_release = new ArrayList();
	private ArrayList m_list_extend = new ArrayList();

	public void add (PendingTransactionsBean pendingTransactionsBean) throws Exception {
		String transaction_type = pendingTransactionsBean.getColumn("THTYPE").trim().toUpperCase();
		if (transaction_type.equals("REL")) {
			String str_openqty = pendingTransactionsBean.getColumn("TDQTY").trim();
			int i = str_openqty.indexOf(".");
			if (i > 0) str_openqty = str_openqty.substring(0,i);
			m_list_release.add (new PendingReleaseItemInfo (
					pendingTransactionsBean.getColumn("THCON#").trim(),
					pendingTransactionsBean.getColumn("TDSEQ#").trim(),
					str_openqty));
		} else if (transaction_type.equals("EXT")) {
			m_list_extend.add (new PendingExtendItemInfo (
					pendingTransactionsBean.getColumn("THCON#").trim(),
					pendingTransactionsBean.getColumn("THERDT").trim(),
					pendingTransactionsBean.getColumn("THSTAT").trim()));
		}
	}
	public boolean isRelease (String contract, String detailSequence) {
		for (Iterator iter = m_list_release.iterator(); iter.hasNext(); ) {
			PendingReleaseItemInfo item = (PendingReleaseItemInfo) iter.next();
			if (item == null) continue;
			if (contract.equals (item.getContract()) && detailSequence.equals (item.getSequence())) return true;
		}
		return false;
	}
	public PendingExtendItemInfo getExtend (String contract) {
		for (Iterator iter = m_list_extend.iterator(); iter.hasNext(); ) {
			PendingExtendItemInfo item = (PendingExtendItemInfo) iter.next();
			if (item == null) continue;
			if (contract.equals (item.getContract())) return item;
		}
		return null;
	}
/*	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((PendingTransactionsItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
*/
}

