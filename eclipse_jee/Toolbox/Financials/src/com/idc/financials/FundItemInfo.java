package com.idc.financials;

import java.io.Serializable;

public class FundItemInfo implements Serializable {
	private static final long serialVersionUID = 1723989847785025454L;

	private String symbol;
	private float percentage = -1;
	private double price = -1;
	private double shares = -1;
	private double cost = -1;

	public FundItemInfo (String symbol, float percentage) {
		this.symbol = symbol;
		this.percentage = percentage;
	}
	public FundItemInfo (String symbol, float percentage, double price) {
		this.symbol = symbol;
		this.percentage = percentage;
		this.price = price;
	}
	public FundItemInfo (String symbol, double cost) {
		this.symbol = symbol;
		this.cost = cost;
	}

	public String getSymbol() {return symbol;}
	public float getPercentage() {return percentage;}
	public double getPrice() {return price;}
	public double getShares() {return shares;}
	public double getCost() {return cost;}

	public void setPrice (double price) {this.price = price;}
	public void setShares (double shares) {this.shares = shares;}
	public void calculateShares (double funds) {
		if (cost < 0) {
			cost = funds * percentage / 100;
			shares = cost / price;
		}
		else {
			shares = cost / price;
			percentage = (float) (cost / funds) * 100;
		}
	}
	public boolean isSetPrice() {
		if (price == -1) return false;
		return true;
	}

	public String toString() {
		return "("+getSymbol()+","+getPrice()+","+getPercentage()+","+getShares()+","+getCost()+")";
	}
}
