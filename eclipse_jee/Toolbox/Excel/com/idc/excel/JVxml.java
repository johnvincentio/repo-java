package com.idc.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

public class JVxml {
	public Workbook parse (File file) {
		InputSource source = null;
		try {
			source = new InputSource(new FileInputStream(file));
		}
		catch (FileNotFoundException ex) {
			System.out.println("--- FileNotFoundException ---:");
			System.out.println(ex.getMessage());
		}
		return handle (source);
	}
	private Workbook handle(InputSource source) {
		DocumentBuilderFactory docBuilderFactory;
		DocumentBuilder docBuilder;
		Document doc;

		Workbook workbook = new Workbook();
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(source);   
			doc.getDocumentElement().normalize();
			workbook = handleWorkBook (workbook, doc);
		}
		catch (Exception ex) {
			System.out.println("Exception "+ex.getMessage());
		}
		return workbook;
	}
	private Workbook handleWorkBook (Workbook workbook, Document doc) 
				throws AppException {
		Element elem = doc.getDocumentElement();
//		System.out.println ("(1) nodeName "+elem.getNodeName());
		if (! elem.getNodeName().equals("Workbook")) {
			throw new AppException ("Not a Workbook");
		}
		NodeList nodeList = elem.getChildNodes();
		Node node;
		DocumentProperties documentProperties;
		Worksheet worksheet;
		int len = (nodeList != null) ? nodeList.getLength() : 0;
		for (int i=0; i<len; i++) {
			node = nodeList.item(i);
//			System.out.println("handleWorkBook has child node "+node.getNodeName());
			if (node.getNodeType() == Node.ELEMENT_NODE &&
					node.getNodeName().equals("DocumentProperties")) {
//				System.out.println("handleWorkBook has DocumentProperties");
				documentProperties = handleDocumentProperties(node);
				workbook.setDocumentProperties(documentProperties);
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE &&
					node.getNodeName().equals("Worksheet")) {
//				System.out.println("handleWorkBook has Worksheet");
				worksheet = handleWorksheet(node);
				workbook.addWorksheet(worksheet);
			}
		}
		return workbook;
	}
	private DocumentProperties handleDocumentProperties (Node pNode) {
		DocumentProperties obj = new DocumentProperties();

		NodeList nodeList = pNode.getChildNodes();
		Node node;
		String nodeName, nodeValue;
		int len = (nodeList != null) ? nodeList.getLength() : 0;
		for (int i=0; i<len; i++) {
			node = nodeList.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) continue;
//			System.out.println("DocumentProperties has child node "+node.getNodeName());
			nodeName = node.getNodeName();
			nodeValue = node.getFirstChild().getNodeValue();
			if (nodeName.equals("Author"))
				obj.setAuthor(nodeValue);
			else if (nodeName.equals("Company"))
				obj.setCompany(nodeValue);
		}
//		System.out.println("(1) :"+obj.toString()+":");
		return obj;
	}
	private Worksheet handleWorksheet (Node pNode) {
		String attrValue = getAttrValue ("ss:Name", pNode);
		if (attrValue.equals("Totals")) return null;	// ignore
		if (attrValue.equals("Statistics")) return null;	// ignore
/*
		if (attrValue.equals("A. Account Services")) return null;	// ignore
		if (attrValue.equals("B. Locations")) return null;	// ignore
		if (attrValue.equals("E. Our Company")) return null;	// ignore
		if (attrValue.equals("H. Help")) return null;	// ignore
		if (attrValue.equals("L. Login")) return null;	// ignore
		if (attrValue.equals("M. Member")) return null;	// ignore
		if (attrValue.equals("N. Global Navigation")) return null;	// ignore
		if (attrValue.equals("R. Rentals")) return null;	// ignore
		if (attrValue.equals("S. Sales")) return null;	// ignore
		if (attrValue.equals("AdminTool")) return null;	// ignore
		if (attrValue.equals("Database")) return null;	// ignore
		if (attrValue.equals("Email")) return null;	// ignore
		if (attrValue.equals("LDAP")) return null;	// ignore
		if (attrValue.equals("RPG")) return null;	// ignore
		if (attrValue.equals("LoadTesting")) return null;	// ignore
		if (attrValue.equals("Implementation")) return null;	// ignore
*/
		Worksheet obj = new Worksheet();
		obj.setWorksheetName(attrValue);
		Sections sections;

		NodeList nodeList = pNode.getChildNodes();
		Node node;
//		String nodeName, nodeValue;
		int len = (nodeList != null) ? nodeList.getLength() : 0;
		for (int i=0; i<len; i++) {
			node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE &&
					node.getNodeName().equals("Table")) {
//				System.out.println("handleWorkBook has DocumentProperties");
				sections = handleTable(node);
				obj.setSections(sections);
			}
		}
		return obj;
	}
	private Sections handleTable (Node pNode) {
		Sections obj = new Sections();
		Section section = null;
		Task task;
		int rowCntr = 0;
		int status = 1;	// 1 = find section header, 2 = find tasks

		NodeList nodeList = pNode.getChildNodes();
		Node node;
//		String nodeName, nodeValue;
		int len = (nodeList != null) ? nodeList.getLength() : 0;
		for (int i=0; i<len; i++) {
			node = nodeList.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE ||
					! node.getNodeName().equals("Row")) continue;
//			System.out.println("handleTable has child node "+node.getNodeName());
			if (rowCntr++ < 4) continue;	// skip startup rows
			task = handleRow(node);
//			System.out.println("task :"+task.toString()+":");
			if (task.isEmpty()) continue;
			if (task.isHeader()) continue;
			switch (status) {
				case 1:
					section = new Section();
					section.setName(task.getOwner());
					section.setDs(task.getName());
					status = 2;
					break;
				case 2:
					if (task.isSubtotal()) {
//						System.out.println("found a subtotal");
						obj.add(section);
						status = 1;
					}
					else
						section.add(task);
					break;
				default:
					break;
			}
		}
//		System.out.println("(1) :"+obj.toString()+":");
		return obj;
	}
	private Task handleRow (Node pNode) {
		Task task = new Task();

		NodeList nodeList = pNode.getChildNodes();
		Node node;
		String cellValue;
		int cellCntr = 0;
		int len = (nodeList != null) ? nodeList.getLength() : 0;
		for (int i=0; i<len; i++) {
			if (cellCntr > 3) break;		// just get the first four cells
			node = nodeList.item(i);
//			System.out.println("handleRow has child node "+node.getNodeName());
			if (node.getNodeType() != Node.ELEMENT_NODE ||
					! node.getNodeName().equals("Cell")) continue;
			cellValue = handleCell(node);
//			System.out.println("cell number "+cellCntr+" value :"+cellValue+":");
			switch (cellCntr) {
			case 3:
				task.setQcr(cellValue);
				break;
			case 2:
				task.setDur(cellValue);
				break;
			case 1:
				task.setName(cellValue);
				break;
			case 0:
				task.setOwner(cellValue);
				break;
			default:
				break;
			}
			cellCntr++;
		}
//		System.out.println("(1) :"+obj.toString()+":");
		return task;
	}
	private String handleCell (Node pNode) {
		Node node;
		String nodeValue = "";
		NodeList nodeList = pNode.getChildNodes();
		int len = (nodeList != null) ? nodeList.getLength() : 0;
		for (int i=0; i<len; i++) {
			node = nodeList.item(i);
//			System.out.println("handleCell has child node "+node.getNodeName());
			if (node.getNodeType() != Node.ELEMENT_NODE ||
					! node.getNodeName().equals("Data")) continue;
			nodeValue = node.getFirstChild().getNodeValue();
		}
//		System.out.println("<<< handleCell; :"+nodeValue+":");
		return nodeValue;
	}
	private String getAttrValue (String name, Node node) {
		String strReturn = null;

		NamedNodeMap attrs = node.getAttributes();
		int attrCount = (attrs != null) ? attrs.getLength() : 0;
		for (int i=0; i<attrCount; i++) {
			Node attr = attrs.item(i);
//			System.out.println("Name :"+attr.getNodeName()+": value :"+
//					attr.getNodeValue()+":");
			if (name.equals(attr.getNodeName())) {
				strReturn = attr.getNodeValue();
				break;
			}
		}
//		System.out.println("returned :"+strReturn+":");
		return strReturn;
	}
/*
	private String getNodeValue (Node node) {
		String strNodeValue = node.getFirstChild().getNodeValue();
//		System.out.println("node value :"+strNodeValue+":");
		return strNodeValue;
	}
*/
}
