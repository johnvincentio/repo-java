package com.idc.db2xml;

import java.io.Serializable;

public class ColumnInfo implements Serializable {
	private static final long serialVersionUID = 1;
	private String name;
	private int type;
	private int len;
	private boolean pk;
	
	public ColumnInfo() {
		len = -1;
		pk = false;
	}
	
	public String getName() {return name;}
	public int getType() {return type;}
	public int getLen() {return len;}
	public boolean getPk() {return pk;}
	
	public void setName (String str) {name = str;}
	public void setType (int num) {type = num;}
	public void setLen (int num) {len = num;}
	public void setPk (boolean b) {pk = b;}
	
	public void doShowInfo() {
		System.out.println("COLUMN: ("+getName()+")("+getType()+")("+getLen()+
			")("+getPk()+")");
	}

}
