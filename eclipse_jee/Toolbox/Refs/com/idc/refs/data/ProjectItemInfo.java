package com.idc.refs.data;

import java.io.Serializable;

/**
 *	Describe a ProjectItemInfo
 *
 * @author John Vincent
 */
 
public class ProjectItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	public ProjectItemInfo (String name) {this.name = name;}
	public String getName() {return name;}
	public String toString() {
		return "("+getName()+")";
	}
}
