package com.idc.launcher;

import java.io.Serializable;

public class MapItemInfo implements Serializable, Comparable<MapItemInfo> {
	private static final long serialVersionUID = 1L;

	private String key;
	private String value;

	public MapItemInfo (String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {return key;}
	public String getValue() {return value;}

	public String toString() {
		return "(" + getKey() + "," + getValue() + ")";
	}

	public int compareTo (MapItemInfo item) {
		return key.compareTo(item.getKey());
	}
}
