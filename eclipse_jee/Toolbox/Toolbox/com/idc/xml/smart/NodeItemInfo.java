package com.idc.xml.smart;

import java.io.Serializable;
import java.util.Iterator;

public class NodeItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private NodeInfo nodeInfo = new NodeInfo();
	private AttributeInfo attributeInfo;
	public NodeItemInfo (String name) {
		this.name = name;
	}
	public String getName() {return name;}
	public NodeInfo getNodeInfo() {return nodeInfo;}
	public void setAttributeInfo (AttributeInfo attributeInfo) {this.attributeInfo = attributeInfo;}
	public AttributeInfo getAttributeInfo() {return attributeInfo;}

	public void setName (String name) {this.name = name;}

	public String toString() {
		return "("+getName()+","+getAttributeInfo()+","+nodeInfo.toString()+")";
	}

	public static void show (int indent, NodeItemInfo nodeItemInfo) {
		for (int i = 0; i < indent; i++) System.out.print("\t");
		++indent;
		System.out.println (nodeItemInfo.getName()+","+nodeItemInfo.getAttributeInfo());
		for (Iterator<NodeItemInfo> iter = nodeItemInfo.getNodeInfo().getItems(); iter.hasNext(); ) {
			NodeItemInfo item = (NodeItemInfo) iter.next();
			show (indent, item);
		}
	}
}
