package com.idc.explorer.abc8;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;

import com.idc.file.JVFile;

public class NodeItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private File file;
	private boolean expanded = false;
	private int type;
	private NodeInfo nodeInfo = new NodeInfo();

	public NodeItemInfo (File file) {
		this.file = file;
		this.type = getFileType();
	}
	public File getFile() {return file;}
	public String getName() {return file.getName();}
	public String getPath() {return file.getPath();}
	public int getType() {return type;}
	public NodeInfo getNodeInfo() {return nodeInfo;}
	public boolean isExpanded() {return expanded;}
	public boolean isExpandable() {
		if (type == 1) return false;
		if (type == 7) return false;
		return true;
	}

	/*
	 * 1 = file
	 * 2 = directory
	 * 3 = zip
	 * 4 = jar
	 * 5 = gz
	 * 6 = tar
	 * 7 = html
	 */
	private int getFileType() {
		if (file.isDirectory()) return 2;
		String extension = (JVFile.getExtension (file.getName())).toLowerCase();
		if (extension.length() < 2) return 1;
		if ("zip".equals(extension)) return 3;
		if ("jar".equals(extension)) return 4;
		if ("ear".equals(extension)) return 4;
		if ("war".equals(extension)) return 4;
		if ("gz".equals(extension)) return 5;
		if ("tar".equals(extension)) return 6;
		if ("html".equals(extension)) return 7;
		return 1;
	}
	public boolean isEditable() {return type == 1 || type == 7;}
	public boolean isBrowser() {return type == 7;}
	public boolean isJavaDecompilable() {
		if (type != 1) return false;
		String extension = (JVFile.getExtension (file.getName())).toLowerCase();
		if (extension == null) return false;
		if (extension.equals("class")) return true;
		return false;
	}

	public void setNodeInfo (NodeInfo nodeInfo) {this.nodeInfo = nodeInfo;}
	public void setExpanded (boolean expanded) {this.expanded = expanded;}

	public String toString() {
		return "("+getName()+","+nodeInfo.toString()+")";
	}

	public static void show (int indent, NodeItemInfo nodeItemInfo) {
		for (int i = 0; i < indent; i++) System.out.print("\t");
		++indent;
		System.out.println (nodeItemInfo.getName()+","+nodeItemInfo.getPath()+","+nodeItemInfo.getFileType());
		for (Iterator iter = nodeItemInfo.getNodeInfo().getItems(); iter.hasNext(); ) {
			NodeItemInfo item = (NodeItemInfo) iter.next();
			show (indent, item);
		}
	}
}
