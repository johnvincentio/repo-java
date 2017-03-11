package com.idc.sql.lib;

import java.io.Serializable;
import java.sql.Types;

import com.idc.utils.UtilHelper;

/**
* @author John Vincent
*/

public class QueryColumnItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private int column;
	private String name;
	private int type;
	private int length;
	private String typeName;
	private boolean isNullable = true;
	private int precision = 0;
	private int scale = 0;
	public QueryColumnItemInfo (int column, String name, int type, int length) {
		this.column = column;
		this.name = name;
		this.type = type;
		this.length = length;
	}
	public QueryColumnItemInfo (int column, String name, int type, int length,
			String typeName, int isNullable, int precision, int scale) {
		this.column = column;
		this.name = name;
		this.type = type;
		this.length = length;
		this.typeName = typeName;
		this.isNullable = isNullable != 0;
		this.precision = precision;
		this.scale = scale;
		if (type == -5) this.precision = 8;
	}
	public int getColumn() {return column;}
	public String getName() {return name;}
	public int getType() {return type;}
	public int getLength() {return length;}
	public String getTypeName() {return typeName;}
	public boolean isNullable() {return isNullable;}
	public int getPrecision() {return precision;}
	public int getScale() {return scale;}

	public String getDisplayName() {
		int fieldLength = getLength();
		if (isQuotesNeeded()) fieldLength += 2;
		int padding = 0;
		if (getName().length() < fieldLength) padding = fieldLength - getName().length();
//		System.out.println(">>> getDisplayName; getName() "+getName()+" getLength() "+
//				getLength()+" getName().length() "+getName().length()+" padding "+padding);
		return getName() + UtilHelper.makeSpace (padding);
	}

	public String getDisplayValue (String value) {
		int displayLength = getDisplayName().length();
		int padding = displayLength - value.length();
		if (isQuotesNeeded()) padding -= 2;
//		System.out.println(">>> getDisplayValue; value "+value+" displayLength "+displayLength+
//				" value.length() "+value.length()+" plen "+padding);
		if (isQuotesNeeded()) return "'" + value + "'" + UtilHelper.makeSpace (padding);
		return value + UtilHelper.makeSpace (padding);
	}

	private boolean isQuotesNeeded() {
		switch (getType()) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.CLOB:
			case Types.DECIMAL:
			case Types.DATE:
			case Types.TIMESTAMP:
			case Types.TIME:
			return true;
		}
		return false;
	}

	public String toString() {
		return "("+getColumn()+","+getName()+","+getType()+","+getLength()+
		","+getTypeName()+","+isNullable()+","+getPrecision()+","+getScale()+")\n";
	}
}
