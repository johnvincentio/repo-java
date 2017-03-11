package com.idc.cycles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

public class JVxml {
	public ArrayList<String> parse(File file) {
		InputSource source = null;
		try {
			source = new InputSource(new FileInputStream(file));
		} catch (FileNotFoundException ex) {
			System.out.println("--- FileNotFoundException ---:");
			System.out.println(ex.getMessage());
		}
		return handle(source);
	}

	private ArrayList<String> handle(InputSource source) {
		DocumentBuilderFactory docBuilderFactory;
		DocumentBuilder docBuilder;
		Document doc;
		ArrayList<String> refs = new ArrayList<String>();
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(source);
			doc.getDocumentElement().normalize();

			Element elem1 = doc.getDocumentElement();
			// System.out.println ("(1) nodeName "+elem1.getNodeName());
			if (!elem1.getNodeName().equals("classpath")) {
				throw new Exception("Not a classpath");
			}

			NodeList nodeList2 = elem1.getChildNodes();
			int len2 = (nodeList2 != null) ? nodeList2.getLength() : 0;
			for (int i2 = 0; i2 < len2; i2++) {
				Node node2 = nodeList2.item(i2);
				if (node2.getNodeType() == Node.ELEMENT_NODE
						&& node2.getNodeName().equals("classpathentry")) {
					// System.out.println("has child (2) node "+node2.getNodeName());
					String str1 = getAttrValue("path", node2).trim();
					if ( str1.length() < 1) continue;
					if (! str1.substring(0, 1).equals("/")) continue;
					String str2 = str1.substring(1);
					// System.out.println(" str2 :"+str2+":");
					if (str2.indexOf('/') > -1) {
						// System.out.println("Found a trouble maker");
						continue;
					}
					refs.add(str2);
				}
			}
		} catch (Exception ex) {
			System.out.println("Exception " + ex.getMessage());
		}
		return refs;
	}

	private String getAttrValue(String name, Node node) {
		String strReturn = null;
		NamedNodeMap attrs = node.getAttributes();
		int attrCount = (attrs != null) ? attrs.getLength() : 0;
		for (int i = 0; i < attrCount; i++) {
			Node attr = attrs.item(i);
			// System.out.println("Name "+attr.getNodeName()+" value"+attr.getNodeValue());
			if (name.equals(attr.getNodeName())) {
				strReturn = attr.getNodeValue();
				break;
			}
		}
		return strReturn;
	}
}
