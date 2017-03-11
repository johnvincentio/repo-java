package com.idc.refs.data;

import java.io.Serializable;

/**
 *	Describe a ClasspathItemInfo
 *
 * @author John Vincent
 */
 
public class ClasspathItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private boolean library;
	private boolean exported;
	public ClasspathItemInfo (String name, boolean library, boolean exported) {
		this.name = name;
		this.library = library;
		this.exported = exported;
	}

	public String getName() {return name;}
	public boolean isLibrary() {return library;}
	public boolean isExported() {return exported;}

	public String toString() {
		return "("+getName()+","+isLibrary()+","+isExported()+")";
	}
}
