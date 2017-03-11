package com.idc.db2xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class TableInfo implements Serializable {
	private static final long serialVersionUID = 1;
	private String name;
	private String displayName;
	private String dialectName;
	private String translationType;
	private String typeOption;
	private ArrayList<String> pk = new ArrayList<String>();
	private ArrayList<ColumnInfo> col = new ArrayList<ColumnInfo>();
	private boolean dialect = false;
	private boolean delete = false;
	
	public TableInfo (String table) {
		name = table;
	}
	public String getName() {return name;}
	public String getDisplayName() {return displayName;}
	public String getDialectName() {return dialectName;}	
	public String getTranslationType() {return translationType;}
	public String getTypeOption() {return typeOption;}
	public boolean getDelete() {return delete;}
	public boolean getDialect() {return dialect;}	
	public ArrayList<String> getPklist() {return pk;}
	public ArrayList<ColumnInfo> getCollist() {return col;}
	
	public void setName (String str) {name = str;}
	public void setDisplayName (String str) {displayName = str;}
	public void setDialectName (String str) {dialectName = str;}	
	public void setTranslationType (String str) {translationType = str;}
	public void setTypeOption (String str) {typeOption = str;}
	public void setDelete() {delete = true;}
	public void setDialect() {dialect = true;}	
		
	public void addPklist (ArrayList<String> list) {pk = list;}
	public void addColumnlist (ArrayList<ColumnInfo> list) {col = list;}
	
	public ColumnInfo getColumn(String name) {
		ColumnInfo info;
		Iterator<ColumnInfo> iterator = col.iterator();
		while (iterator.hasNext()) {
			info = (ColumnInfo) iterator.next();
			if ((info.getName()).equals(name)) return info;
		}
		return null;
	}
	
	public void doShowInfo() {
		if (getDelete()) {
			System.out.println("Deleted Table :"+getName()+":");
			return;
		}
		if (getDialect()) {
			System.out.println("Dialect Table: ("+getName()+")("+getDisplayName()+")("+getTranslationType()+
				")("+getTypeOption()+")");
		}
		else {
			System.out.println("Control Table: ("+getName()+")("+getDisplayName()+")("+getTranslationType()+
				")("+getTypeOption()+")");			
		}
		doShowPrimaryKeys();
		doShowColumnInfo();
	}

	private void doShowPrimaryKeys() {
		String column;
		Iterator<String> iterator = pk.iterator();
		while (iterator.hasNext()) {
			column = (String) iterator.next();
			System.out.println("Primary key: "+column);
		}		
	}
	private void doShowColumnInfo() {
		ColumnInfo info;
		Iterator<ColumnInfo> iterator = col.iterator();
		while (iterator.hasNext()) {
			info = (ColumnInfo) iterator.next();
			info.doShowInfo();
		}		
	}
}

	