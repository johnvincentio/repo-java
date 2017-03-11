
package com.idc.excel;

public class Task {
	private String owner;
	private String name;
	private String dur;
	private String qcr;
	public String getOwner() {return owner;}
	public String getName() {return name;}
	public String getDur() {return dur;}
	public String getQcr() {return qcr;}
	public void setOwner (String owner) {this.owner = owner;}
	public void setName (String name) {this.name = name;}
	public void setDur (String dur) {this.dur = dur;}
	public void setQcr (String qcr) {this.qcr = qcr;}
	public String toString() {
		return "("+getOwner()+","+getName()+","+getDur()+","+getQcr()+")";
	}
	public boolean isEmpty() {
		boolean null1, null2;
		null1 = null2 = false;
		if (owner == null || owner.length() < 1) null1 = true;
		if (name == null || name.length() < 1) null2 = true;
		return null1 && null2;
	}
	public boolean isHeader() {
		return owner.equals("Owner") && name.equals("Project Item") && 
			dur.equals("Item Days") && qcr.equals("QCR Days");
	}
	public boolean isSubtotal() {return owner.equals("Subtotal");}
}
