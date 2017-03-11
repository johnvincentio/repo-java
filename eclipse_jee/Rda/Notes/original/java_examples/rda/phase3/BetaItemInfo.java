package com.idc.rda.phase3;

import java.io.Serializable;
import java.util.Iterator;

import com.idc.rda.phase2.AlphaInfo;
import com.idc.rda.phase2.AlphaItemInfo;

public class BetaItemInfo implements Serializable {
	private static final long serialVersionUID = 2815545937134186092L;

	private String section;
	private GammaInfo gammaInfo = new GammaInfo();

	public BetaItemInfo (AlphaInfo alphaInfo) {
		this.section = alphaInfo.getSection();
		Iterator<AlphaItemInfo> iter = alphaInfo.getItems();
		while (iter.hasNext()) {
			AlphaItemInfo alphaItemInfo = iter.next();
			if (alphaItemInfo == null) continue;
			gammaInfo.add (alphaItemInfo);
		}
	}

	public String getSection() {return section;}
	public GammaInfo getGammaInfo() {return gammaInfo;}

	public Iterator<DeltaItemInfo> getData (String subSection) {
		DeltaInfo deltaInfo = gammaInfo.getDeltaInfo (subSection);
		if (deltaInfo == null) return null;
		return deltaInfo.getItems();
	}

	public String toString() {
		return "("+getSection()+","+getGammaInfo()+")";
	}
}
