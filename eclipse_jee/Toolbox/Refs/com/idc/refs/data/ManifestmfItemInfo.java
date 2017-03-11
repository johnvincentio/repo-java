package com.idc.refs.data;

import java.io.Serializable;

/**
 *	Describe a ManifestmfItemInfo
 *
 * @author John Vincent
 */
 
public class ManifestmfItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	public ManifestmfItemInfo (String name) {this.name = name;}
	public String getName() {return name;}
	public String toString() {return "("+getName()+")";}
}
