package com.idc.refs.data;

import java.io.Serializable;

/**
 *	Describe a ModulemapsItemInfo
 *
 * @author John Vincent
 */
 
public class ModulemapsItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	public ModulemapsItemInfo (String name) {this.name = name;}
	public String getName() {return name;}
	public String toString() {return "("+getName()+")";}
}
