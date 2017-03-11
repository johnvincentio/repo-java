package com.idc.sql.lib;

import java.io.Serializable;

/**
 *	Describe a IncludeTableItemInfo
 *
 * @author John Vincent
 */
 
public class IncludeTableItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String select;
	private String clause;
	private String order;

	public IncludeTableItemInfo (String name) {this (name, "", "", "");}
	public IncludeTableItemInfo (String name, String select, String clause) {this (name, select, clause, "");}
	public IncludeTableItemInfo (String name, String select, String clause, String order) {
		this.name = name;
		this.select = select;
		this.clause = clause;
		this.order = order;
	}

	public String getName() {return name;}
	public String getSelect() {return select;}
	public String getClause() {return clause;}
	public String getOrder() {return order;}

	public String toString() {
		return "("+getName()+","+getSelect()+","+getClause()+","+getOrder()+")";
	}
}
