
package com.idc.loadtest;

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

import com.idc.http.FormItem;

public class JVxml {
	public Stages parse (File file) {
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
	private Stages handle(InputSource source) {
		DocumentBuilderFactory docBuilderFactory;
		DocumentBuilder docBuilder;
		Document doc;
		NodeList nodeList1, nodeList2, nodeList3;
		Node node1, node2, node3;
		Stage stage;
		Stages stages = new Stages();
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(source);
			doc.getDocumentElement().normalize();

			Element elem1 = doc.getDocumentElement();
//			System.out.println ("(1) nodeName "+elem1.getNodeName());
			if (! elem1.getNodeName().equals("loadtest")) {
				throw new Exception ("Not a loadtest");
			}
			nodeList1 = elem1.getChildNodes();

			for (int i1=0; i1<getNodeSize(nodeList1); i1++) {
				node1 = nodeList1.item(i1);
//				System.out.println("has child (1) node "+node1.getNodeName());
				if (node1.getNodeName().equals("stages")) {
					stages.setServer(getAttrValue ("server", node1));
					stages.setPort(getAttrValue ("port", node1));
					stages.setContextRoot(getAttrValue ("contextroot", node1));
					stages.setProtocol(getAttrValue ("protocol", node1));

					nodeList2 = node1.getChildNodes();
					for (int i2=0; i2<getNodeSize(nodeList2); i2++) {
						node2 = nodeList2.item(i2);
//						System.out.println("has child (2) node "+node2.getNodeName());
						if (node2.getNodeName().equals("stage")) {
							stage = new Stage();
							stage.setUrl(getAttrValue ("url", node2));
							stage.setPost(getAttrValue ("method", node2));
							stage.setOutput(getAttrValue ("output", node2));
							stage.setDescription(getAttrValue ("description", node2));

							nodeList3 = node2.getChildNodes();
							for (int i3=0; i3<getNodeSize(nodeList3); i3++) {
								node3 = nodeList3.item(i3);
//								System.out.println("has child (3) node "+node3.getNodeName());
								if (node3.getNodeName().equals("form")) {
									String name = getAttrValue ("name", node3);
									String value = getAttrValue ("value", node3);
									stage.addFormItem (new FormItem (name, value));
								}
							}
							stages.add(stage);
						}
					}
				}
			}
		}
		catch (Exception ex) {
			System.out.println("Exception "+ex.getMessage());
		}
//		System.out.println("stages "+stages.toString());
		return stages;
	}

	private int getNodeSize(NodeList nodeList) {
		return (nodeList != null) ? nodeList.getLength() : 0;
	}

	protected String getNodeValue (Node node) {
		 String strNodeValue = node.getFirstChild().getNodeValue();
//		 System.out.println("node value :"+strNodeValue+":");
		 return strNodeValue;
	}

	private String getAttrValue (String name, Node node) {
		String strReturn = null;
		NamedNodeMap attrs = node.getAttributes();
		int attrCount = (attrs != null) ? attrs.getLength() : 0;
		for (int i=0; i<attrCount; i++) {
			Node attr = attrs.item(i);
//			System.out.println("Name "+attr.getNodeName()+"value"+attr.getNodeValue());
			if (name.equals(attr.getNodeName())) {
				strReturn = attr.getNodeValue();
				break;
			}
		}
		return strReturn;
	}

	protected void listAttributes (Node node) {
		NamedNodeMap attrs = node.getAttributes();
		int attrCount = (attrs != null) ? attrs.getLength() : 0;
		System.out.println("attrCount "+attrCount);
		for (int i=0; i<attrCount; i++) {
			Node attr = attrs.item(i);
			System.out.println("Name :"+attr.getNodeName()+": value :"+
					attr.getNodeValue()+":");
		}
	}
}
