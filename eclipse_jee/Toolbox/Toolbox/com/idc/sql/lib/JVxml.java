
package com.idc.sql.lib;

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

import com.idc.trace.LogHelper;

public class JVxml {
	public Scenarios parse (File file) {
		InputSource source = null;
		try {
			source = new InputSource(new FileInputStream(file));
		}
		catch (FileNotFoundException ex) {
			LogHelper.error("--- FileNotFoundException ---:");
			LogHelper.error(ex.getMessage());
		}
		return handle (source);
	}
	private Scenarios handle(InputSource source) {
		DocumentBuilderFactory docBuilderFactory;
		DocumentBuilder docBuilder;
		Document doc;
		NodeList nodeList1, nodeList2;
		Node node1, node2;

		Scenarios scenarios = new Scenarios();
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(source);
			doc.getDocumentElement().normalize();

			Element elem1 = doc.getDocumentElement();
//			LogHelper.info ("(1) nodeName "+elem1.getNodeName());
			if (! elem1.getNodeName().equals("dbtool")) {
				throw new Exception ("Not a dbtool");
			}
			nodeList1 = elem1.getChildNodes();

			for (int i1=0; i1<getNodeSize(nodeList1); i1++) {
				node1 = nodeList1.item(i1);
//				LogHelper.info("has child (1) node "+node1.getNodeName());
				if (node1.getNodeName().equals("scenario")) {
					Scenario scenario = new Scenario();
					nodeList2 = node1.getChildNodes();
					for (int i2=0; i2<getNodeSize(nodeList2); i2++) {
						node2 = nodeList2.item(i2);
						String nodeName = node2.getNodeName();
						if (nodeName.equals("#text")) continue;
//						LogHelper.info("nodeName :"+nodeName+":");

						String nodeValue = getNodeValue(node2);
//						LogHelper.info("nodeValue :"+nodeValue+":");
						if (nodeName.equals("name"))
							scenario.setName(nodeValue);
						else if (nodeName.equals("driver"))
							scenario.setDriver(nodeValue);
						else if (nodeName.equals("url"))
							scenario.setUrl(nodeValue);
						else if (nodeName.equals("username"))
							scenario.setUsername(nodeValue);
						else if (nodeName.equals("password"))
							scenario.setPassword(nodeValue);
						else if (nodeName.equals("databasename"))
							scenario.setDatabasename(nodeValue);
						else if (nodeName.equals("schema"))
							scenario.setSchema(nodeValue);
						else if (nodeName.equals("system"))
							scenario.setSystem(nodeValue);
					}
					scenarios.add(scenario);
				}
			}
		}
		catch (Exception ex) {
			LogHelper.error("Exception "+ex.getMessage());
		}
//		LogHelper.info("scenarios "+scenarios.toString());
		return scenarios;
	}

	private int getNodeSize(NodeList nodeList) {
		return (nodeList != null) ? nodeList.getLength() : 0;
	}

	protected String getNodeValue (Node node) {
		 String strNodeValue = node.getFirstChild().getNodeValue();
//		 LogHelper.info("node value :"+strNodeValue+":");
		 return strNodeValue;
	}
}
