package com.idc.parse;

//resources.jdbc:JDBCProvider

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class JVxml {
	public static int MULTIPLIER = 10000;

	public JdbcInfo parse(File file) {
		InputSource source = null;
		try {
			source = new InputSource(new FileInputStream(file));
		} catch (FileNotFoundException ex) {
			System.out.println("--- FileNotFoundException ---:");
			System.out.println(ex.getMessage());
		}
		return handle(source);
	}

	private JdbcInfo handle(InputSource source) {
		DocumentBuilderFactory docBuilderFactory;
		DocumentBuilder docBuilder;
		Document doc;
		NodeList nodeList2, nodeList3, nodeList4, nodeList5;
		Node node2, node3, node4, node5;
		int len2, len3, len4, len5;
		String nodeName, nodeValue;

		JdbcInfo jdbcInfo = new JdbcInfo();
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(source);
			doc.getDocumentElement().normalize();

			Element elem1 = doc.getDocumentElement();
			System.out.println ("(1) nodeName "+elem1.getNodeName());
			if (! elem1.getNodeName().equals("resources.jdbc:JDBCProvider")) {
				throw new Exception("Not a resources.jdbc:JDBCProvider");
			}

			nodeList2 = elem1.getChildNodes();
			len2 = (nodeList2 != null) ? nodeList2.getLength() : 0;
			for (int i2 = 0; i2 < len2; i2++) {
				node2 = nodeList2.item(i2);
				System.out.println("has child (2) node "+node2.getNodeName());
				if (node2.getNodeName().equals("classpath")) {
					System.out.println("node value :"+getNodeValue(node2).trim()+":");
					jdbcInfo.setClasspath(getNodeValue(node2).trim());
				}
				else if (node2.getNodeName().equals("factories")) {
					nodeList3 = node2.getChildNodes();
					len3 = (nodeList3 != null) ? nodeList3.getLength() : 0;
					for (int i3 = 0; i3 < len3; i3++) {
						node3 = nodeList3.item(i3);
						System.out.println("has child (3) node "+node3.getNodeName());
						if (node3.getNodeType() == Node.ELEMENT_NODE && node3.getNodeName().equals("Task")) {
							
							nodeList4 = node3.getChildNodes();
							len4 = (nodeList4 != null) ? nodeList4.getLength() : 0;
							// task = new Task();
							for (int i4 = 0; i4 < len4; i4++) {
								node4 = nodeList4.item(i4);
								nodeName = node4.getNodeName();
								nodeValue = node4.getFirstChild()
										.getNodeValue();

							}
//							project.getTasks().add(task);
						}
					}
				}
				/*
				else if (node2.getNodeName().equals("Name"))
					project.setName(getNodeValue(node2).trim());
				else if (node2.getNodeName().equals("Title"))
					project.setTitle(getNodeValue(node2).trim());
				else if (node2.getNodeName().equals("Company"))
					project.setCompany(getNodeValue(node2).trim());
				else if (node2.getNodeName().equals("CreationDate"))
					project.setCreationdate(getNodeValue(node2).trim());
				else if (node2.getNodeName().equals("LastSaved"))
					project.setLastsaved(getNodeValue(node2).trim());
				else if (node2.getNodeName().equals("StartDate"))
					project.setStartdate(getNodeValue(node2).trim());
				else if (node2.getNodeName().equals("FinishDate"))
					project.setFinishdate(getNodeValue(node2).trim());
					*/
				else {
					System.out.println("ignored node " + node2.getNodeName());
					// throw new Exception ("Unknown node");
				}
			}
		} catch (Exception ex) {
			System.out.println("Exception " + ex.getMessage());
		}
		return jdbcInfo;
	}

	private String getNodeValue(Node node) {
		String strNodeValue = node.getFirstChild().getNodeValue();
		// System.out.println("node value :"+strNodeValue+":");
		return strNodeValue;
	}

	private static int formatDuration(String dur) {
		if (dur.length() < 3)
			return 0;
		String s1 = dur.substring(2);
		int i1 = s1.indexOf('H');
		String s2 = s1.substring(0, i1);
		int iDur = (Integer.parseInt(s2) * MULTIPLIER) / 8;
		return iDur;
	}

	private static int formatNumber(String num) {
		if (num.length() < 1)
			return 0;
		return Integer.parseInt(num);
	}

	private static int formatPercentComplete(String num) {
		if (num.length() < 1)
			return 0;
		return Integer.parseInt(num) * MULTIPLIER;
	}
}
