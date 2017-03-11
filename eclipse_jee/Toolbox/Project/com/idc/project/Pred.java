package com.idc.project;

public class Pred {
	private String uid;
	private String type;
	private String lag;
	
	public String getUid() {return uid;}
	public String getType() {return type;}
	public String getLag() {return lag;}
	
	public void setUid (String s) {uid = s;}
	public void setType (String s) {type = s;}
	public void setLag (String s) {lag = s;}
	
	public String toString() {
		return "(" + getUid() + "," + getType() + "," + getLag() + ")";
	}
}
