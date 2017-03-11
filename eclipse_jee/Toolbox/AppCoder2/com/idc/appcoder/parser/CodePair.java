
package com.idc.appcoder.parser;

/**
 * @author John Vincent
 *
 */

public class CodePair {
	private String visible = "private";
	private String type;
	private String name;
	private String value = "";
	private String comment = "";

	public String getVisible() {return visible;}
	public String getType() {return type;}
	public String getName() {return name;}
	public String getValue() {return value;}
	public String getComment() {return comment;}

	public boolean isValue() {
		if (value == null || value.length() < 1) return false;
		return true;
	}
	public boolean isComment() {
		if (comment == null || comment.length() < 1) return false;
		return true;
	}

	public void setVisible(String s) {visible = s;}
	public void setType(String s) {type = s;}
	public void setName(String s) {name = s;}
	public void setValue(String s) {value = s;}
	public void setComment(String s) {comment = s;}

	public String toString() {return "(CodePair) ("+getVisible()+","+getType()+","+getName()+","+getValue()+","+getComment()+")\n";}
}
	