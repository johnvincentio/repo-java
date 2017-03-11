package com.idc.rda.phase3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idc.rda.phase2.AlphaItemInfo;

public class GammaInfo implements Serializable {
	private static final long serialVersionUID = -2965543311212760741L;
	
	private ArrayList<DeltaInfo> m_collection = new ArrayList<DeltaInfo>();

	public void add (AlphaItemInfo alphaItemInfo) {
		int sheetNumber = alphaItemInfo.getSheetNumber();
		HashMap<String, String[]> data = alphaItemInfo.getData();
		Iterator<Map.Entry<String, String[]>> parameters = data.entrySet().iterator();
		while (parameters.hasNext()) {
			Map.Entry<String, String[]> entry = parameters.next();
			String subSection = entry.getKey();
			DeltaInfo deltaInfo = getDeltaInfo (subSection);
			if (deltaInfo == null) {
				deltaInfo = new DeltaInfo (subSection);
				add (deltaInfo);
			}
			deltaInfo.add (new DeltaItemInfo (sheetNumber, entry.getValue()));
		}
	}
	public DeltaInfo getDeltaInfo (String subSection) {
		Iterator<DeltaInfo> iter = getItems();
		while (iter.hasNext()) {
			DeltaInfo deltaInfo = iter.next();
			if (deltaInfo == null) continue;
			if (subSection.equals (deltaInfo.getSubSection())) return deltaInfo;
		}
		return null;
	}

	public void add (DeltaInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<DeltaInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(m_collection.get(i).toString());
		return "("+buf.toString()+")";
	}
}


