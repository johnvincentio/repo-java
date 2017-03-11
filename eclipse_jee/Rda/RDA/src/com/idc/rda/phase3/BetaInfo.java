package com.idc.rda.phase3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.rda.phase2.AlphaInfo;
import com.idc.rda.phase2.OuterInfo;

public class BetaInfo implements Serializable {
	private static final long serialVersionUID = -8555662465062329539L;

	private ArrayList<BetaItemInfo> m_collection = new ArrayList<BetaItemInfo>();
	public BetaInfo (OuterInfo outerInfo) {
		Iterator<AlphaInfo> iter = outerInfo.getItems();
		while (iter.hasNext()) {
			AlphaInfo alphaInfo = iter.next();
			if (alphaInfo == null) continue;
			add (new BetaItemInfo (alphaInfo));
		}
	}
	public Iterator<DeltaItemInfo> getData (String section, String subSection) {
		BetaItemInfo betaItemInfo = getBetaItemInfo (section);
		if (betaItemInfo == null) return null;
		return betaItemInfo.getData (subSection);
	}
	private BetaItemInfo getBetaItemInfo (String section) {
		Iterator<BetaItemInfo> iter = getItems();
		while (iter.hasNext()) {
			BetaItemInfo betaItemInfo = iter.next();
			if (betaItemInfo == null) continue;
			if (section.equals (betaItemInfo.getSection())) return betaItemInfo;
		}
		return null;
	}

	public void add (BetaItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<BetaItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append (m_collection.get(i).toString());
		return "("+buf.toString()+")";
	}
}
