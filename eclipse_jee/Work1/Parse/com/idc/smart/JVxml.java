package com.idc.smart;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class JVxml {

	public static NodeItemInfo loadXML (File file) {
		InputSource source = null;
		try {
			source = new InputSource (new FileInputStream(file));
			Document document = getDocument (source);
			return getNode (document.getDocumentElement());
		} catch (Exception ex) {
			System.out.println ("--- Exception ---:");
			System.out.println (ex.getMessage());
			return null;
		}
	}
	private static Document getDocument (InputSource source) throws Exception {
		DocumentBuilderFactory docBuilderFactory;
		DocumentBuilder docBuilder;
		Document document;
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			docBuilder = docBuilderFactory.newDocumentBuilder();
			document = docBuilder.parse(source);
			document.getDocumentElement().normalize();
			return document;
		} catch (Exception ex) {
			throw new Exception ("Cannot handle document; "+ ex.getMessage());
		}
	}

	private static NodeItemInfo getNode (Node node) {
		String name = node.getNodeName();
//		System.out.println("Node Name :"+name+":");
		NodeItemInfo nodeItemInfo = new NodeItemInfo (name);
		nodeItemInfo.setAttributeInfo (getAttributes (node));
		NodeList nodeList = node.getChildNodes();
		int len = (nodeList != null) ? nodeList.getLength() : 0;
		for (int i = 0; i < len; i++) {
			node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) nodeItemInfo.getNodeInfo().add (getNode (node));
		}
		nodeItemInfo.getNodeInfo().sort();
		return nodeItemInfo;
	}

	private static AttributeInfo getAttributes (Node node) {
		AttributeInfo attributeInfo = new AttributeInfo();
		NamedNodeMap attrs = node.getAttributes();
		int attrCount = (attrs != null) ? attrs.getLength() : 0;
		for (int i = 0; i < attrCount; i++) {
			Node attr = attrs.item(i);
			attributeInfo.add (new AttributeItemInfo (attr.getNodeName(), attr.getNodeValue()));
//			System.out.println("Attribute Name :"+attr.getNodeName()+": value :"+attr.getNodeValue()+":");
		}
		attributeInfo.sort();
		return attributeInfo;
	}
}
