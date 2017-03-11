package com.idc.smart;

import java.io.File;
import java.util.Iterator;

public class App {
	public static void main (String[] args) {
		(new App()).work(args);
	}

	private static int SCENARIO = 7;

	public void work (String[] args) {
		String strFile;
		switch (SCENARIO) {
			case 1:
				strFile = "C:\\irac7\\wrkspc\\Work1\\JV1\\Working 1\\resources.xml";
				doit (strFile);
				break;
			case 2:
				strFile = "C:\\irac7\\wrkspc\\Work1\\JV1\\JV Server\\resources.xml";
				doit (strFile);
				break;
			case 3:
				strFile = "C:\\irac7\\wrkspc\\Work1\\JV1\\Prod Servers\\PP_resources.xml";
				doit (strFile);
				break;
			case 4:
				strFile = "C:\\irac7\\wrkspc\\Work1\\JV1\\Prod Servers\\DC_resources.xml";
				doit (strFile);
				break;
			case 5:
				strFile = "C:\\irac7\\wrkspc\\Work1\\JV1\\Prod Servers\\DC2_resources.xml";
				doit (strFile);
				break;
			case 6:
				strFile = "C:\\irac7\\wrkspc\\Work1\\JV1\\Prod Servers\\QUAL_resources.xml";
				doit (strFile);
				break;
			case 7:
				strFile = "C:\\irac7\\wrkspc\\Work1\\JV1\\Prod Servers\\RC_resources.xml";
				doit (strFile);
				break;
			default:
				break;
		}
	}

	private void doit (String strFile) {
		 System.out.println("Loading xml file "+strFile);
		 File file = new File (strFile);
		 if (! file.isFile()) return;
		 if (! file.exists()) return;
		 NodeItemInfo nodeItemInfo = JVxml.loadXML(file);
//		 NodeItemInfo.show (0, nodeItemInfo);
		 showResources (false, 0, nodeItemInfo);
		 System.out.println("Load is complete");
	}

	public void showResources (boolean bTrace, int indent, NodeItemInfo nodeItemInfo) {
		String nodeName = nodeItemInfo.getName();
		if (nodeName.equals("resources.jdbc:JDBCProvider") &&
				(nodeItemInfo.getAttributeInfo().isFound("name", "Herc RentalMan") ||
						nodeItemInfo.getAttributeInfo().isFound("name", "DB2 UDB for iSeries (Toolbox)"))) {
			bTrace = true;
		}
		boolean bDetailed = false;
		if (nodeName.equals("resourceProperties")) bDetailed = true;

		if (bTrace) {
			for (int i = 0; i < indent; i++) System.out.print("\t");
			++indent;
			if (! bDetailed) 
				System.out.print (nodeItemInfo.getName()+",");
			System.out.println (showAttributes (bDetailed,nodeItemInfo.getAttributeInfo()));
		}
		for (Iterator iter = nodeItemInfo.getNodeInfo().getItems(); iter.hasNext(); ) {
			NodeItemInfo item = (NodeItemInfo) iter.next();
			showResources (bTrace, indent, item);
		}
	}

	public String showAttributes (boolean bDetailed,AttributeInfo attributeInfo) {
		StringBuffer buf = new StringBuffer();
		for (Iterator iter = attributeInfo.getItems(); iter.hasNext(); ) {
			AttributeItemInfo item = (AttributeItemInfo) iter.next();
			if (item.getName().equalsIgnoreCase("description")) continue;
			if (item.getName().equalsIgnoreCase("xmi:id")) continue;
			if (item.getName().equalsIgnoreCase("xmi:type")) continue;
			if (bDetailed) {
				buf.append (item.getValue()+" ");
			}
			else
				buf.append (item);
		}
		if (bDetailed) return buf.toString();
		return "("+buf.toString()+")";
	}

	public static void show (int indent, NodeItemInfo nodeItemInfo) {
		for (int i = 0; i < indent; i++) System.out.print("\t");
		++indent;
		System.out.println (nodeItemInfo.getName()+","+nodeItemInfo.getAttributeInfo());
		for (Iterator iter = nodeItemInfo.getNodeInfo().getItems(); iter.hasNext(); ) {
			NodeItemInfo item = (NodeItemInfo) iter.next();
			show (indent, item);
		}
	}
}
