package com.idc.rda.phase2.items;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *	Describe a KeyItemInfo
 *
 * @author John Vincent
 */
 
public class KeyItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private int jira;
	private ArrayList<String> keyList = new ArrayList<String>();
	private ArrayList<String> valueList = new ArrayList<String>();

	public KeyItemInfo (int jira) {this.jira = jira;}
	public void add (String key, String value) {
		keyList.add (key);
		valueList.add (value);
	}

	public int getJira() {return jira;}
	public ArrayList<String> getKeyList() {return keyList;}
	public ArrayList<String> getValueList() {return valueList;}

	public String toString() {
		return "("+getJira()+","+getKeyList()+","+getValueList()+")";
	}
}
