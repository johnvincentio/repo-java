package com.idc.rda.phase2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idc.rda.phase1.JiraInfo;
import com.idc.rda.phase1.JiraItemInfo;

public class OuterInfo implements Serializable {
	private static final long serialVersionUID = -7792165261815273205L;

	private ArrayList<AlphaInfo> m_collection = new ArrayList<AlphaInfo>();

	public OuterInfo (JiraInfo jiraInfo) {
		Iterator<JiraItemInfo> iter = jiraInfo.getItems();
		while (iter.hasNext()) {
			JiraItemInfo jiraItemInfo = iter.next();
			if (jiraItemInfo == null) continue;
			HashMap<String, HashMap<String, String[]>> map = jiraItemInfo.getSections();
			Iterator<Map.Entry<String, HashMap<String, String[]>>> parameters = map.entrySet().iterator();
			while (parameters.hasNext()) {
				Map.Entry<String, HashMap<String, String[]>> entry = parameters.next();
				String sectionName = entry.getKey();
				AlphaItemInfo alphaItemInfo = new AlphaItemInfo (jiraItemInfo.getSheetNumber(), entry.getValue());
				add (sectionName, alphaItemInfo);
			}
		}
	}
	private void add (String sectionName, AlphaItemInfo alphaItemInfo) {
		AlphaInfo alphaInfo = getAlphaInfo (sectionName);
		if (alphaInfo == null) {
			alphaInfo = new AlphaInfo (sectionName);
			m_collection.add (alphaInfo);
		}
		alphaInfo.add (alphaItemInfo);
	}
	private AlphaInfo getAlphaInfo (String sectionName) {
		Iterator<AlphaInfo> iter = getItems();
		while (iter.hasNext()) {
			AlphaInfo alphaInfo = iter.next();
			if (alphaInfo == null) continue;
			if (alphaInfo.getSection().equals(sectionName)) return alphaInfo;
		}
		return null;
	}

	public Iterator<AlphaInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append (m_collection.get(i).toString()).append("\n");
		return "("+buf.toString()+")\n";
	}
}
