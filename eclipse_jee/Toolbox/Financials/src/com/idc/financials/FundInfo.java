package com.idc.financials;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class FundInfo implements Serializable {
	private static final long serialVersionUID = -5004602502626478736L;

	private double funds;
	public FundInfo (double funds) {
		this.funds = funds;
	}
	public double getFunds() {return funds;}

	private ArrayList<FundItemInfo> m_collection = new ArrayList<FundItemInfo> ();

	public void add (FundItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<FundItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public FundItemInfo getFundItemInfo (String symbol) {
		for (FundItemInfo fundItemInfo : m_collection) {
			if (fundItemInfo == null) continue;
			if (fundItemInfo.getSymbol().equals (symbol)) return fundItemInfo;
		}
		return null;
	}

	public void handlePrices() {
		for (FundItemInfo fundItemInfo : m_collection) {
			if (fundItemInfo == null) continue;
			if (! fundItemInfo.isSetPrice ()) fundItemInfo.setPrice (StocksHelper.doPriceLookup (fundItemInfo.getSymbol()));
		}
	}

	public void calculateShares() {
		for (FundItemInfo fundItemInfo : m_collection) {
			if (fundItemInfo == null) continue;
			fundItemInfo.calculateShares (funds);
		}		
	}

	public double getTotalPercent() {
		double total = 0;
		for (FundItemInfo fundItemInfo : m_collection) {
			if (fundItemInfo == null) continue;
			total += fundItemInfo.getPercentage();
		}
		return total;
	}

	public double getTotalCost() {
		double total = 0;
		for (FundItemInfo fundItemInfo : m_collection) {
			if (fundItemInfo == null) continue;
			total += fundItemInfo.getCost();
		}
		return total;
	}

	public double calculateCost() {
		double total = 0;
		for (FundItemInfo fundItemInfo : m_collection) {
			if (fundItemInfo == null) continue;
			total += (fundItemInfo.getShares() * fundItemInfo.getPrice());
		}
		return total;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((FundItemInfo) m_collection.get(i)).toString());
		return "("+getFunds()+"),"+"("+buf.toString()+")";
	}
}
