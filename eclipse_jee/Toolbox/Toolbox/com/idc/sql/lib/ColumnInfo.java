package com.idc.sql.lib;

/**
* @author John Vincent
*/

import java.io.Serializable;

import com.idc.trace.LogHelper;

public class ColumnInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private int type;
	private int len;
	private int scale;
	private boolean pk;
	private boolean nullable;
	
	public ColumnInfo() {
		len = -1;
		pk = false;
	}
	
	public String getName() {return name;}
	public int getType() {return type;}
	public int getLen() {return len;}
	public int getScale() {return scale;}
	public boolean getPk() {return pk;}
	public boolean getNullable() {return nullable;}
	
	public void setName (String str) {name = str;}
	public void setType (int num) {type = num;}
	public void setLen (int num) {len = num;}
	public void setScale (int num) {scale = num;}
	public void setPk (boolean b) {pk = b;}
	public void setNullable (int num) {
		if (num == 0)
			nullable = true;
		else
			nullable = false;
	}
	
	public void doShowInfo() {
		LogHelper.info("COLUMN: ("+getName()+")("+getType()+")("+getLen()+
				")("+getScale()+")("+getPk()+") ("+getNullable()+")");
	}
}
