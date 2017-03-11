package com.idc.rda.phase2;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.idc.rda.phase2.items.KeyInfo;
import com.idc.rda.phase2.items.KeyItemInfo;
import com.idc.rda.phase2.items.SectionInfo;
import com.idc.rda.phase2.items.SectionItemInfo;

public class JVxml {
	public static SectionInfo parse (SectionInfo sectionInfo, File file, boolean bSort) throws Exception {
		InputSource source = null;
		try {
			source = new InputSource (new FileInputStream(file));
			return handle (sectionInfo, source);
		} catch (Exception ex) {
			System.out.println ("--- Exception ---:");
			throw new Exception (ex.getMessage());
		}
	}
	private static SectionInfo handle (SectionInfo sectionInfo, InputSource source) throws Exception {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document document = docBuilder.parse(source);
			document.getDocumentElement().normalize();

			Element elem1 = document.getDocumentElement();
			System.out.println ("(0) nodeName "+elem1.getNodeName());
			if (! elem1.getNodeName().equals("jira")) {
				throw new Exception ("Not a jira");
			}

			NodeList nodeList1 = elem1.getChildNodes();
			for (int i1 = 0; i1 < getNodeSize (nodeList1); i1++) {
				Node node1 = nodeList1.item(i1);
				String sectionName = node1.getNodeName();
				if (sectionName.equals("#text")) continue;
				System.out.println("(1) nodeName "+sectionName);

				SectionItemInfo sectionItemInfo = sectionInfo.getSectionItemInfo (sectionName);

				NodeList nodeList2 = node1.getChildNodes();
				for (int i2 = 0; i2 < getNodeSize(nodeList2); i2++) {
					Node node2 = nodeList2.item(i2);
					String nodeName2 = node2.getNodeName();
					if (! nodeName2.equals("key")) continue;
					System.out.println("\t(2) nodeName :"+nodeName2+":");

					String attr2 = getAttrValue ("type", node2);
					System.out.println("\t(2) nodeType "+attr2);
					KeyInfo keyInfo = sectionItemInfo.getKeyInfo (attr2);

					NodeList nodeList3 = node2.getChildNodes();
					for (int i3 = 0; i3 <getNodeSize(nodeList3); i3++) {
						Node node3 = nodeList3.item(i3);
						String nodeName3 = node3.getNodeName();
						if (nodeName3.equals("#text")) continue;
						System.out.println("\t\t(3) nodeName :"+nodeName3+":");
						if (! nodeName3.equals("jira")) continue;
						String jiraValue = getAttrValue ("value", node3);
						System.out.println("\t\t(3) jiraValue "+jiraValue);
						KeyItemInfo keyItemInfo = new KeyItemInfo (Integer.parseInt (jiraValue));

						NodeList nodeList4 = node3.getChildNodes();
						for (int i4 = 0; i4 <getNodeSize(nodeList4); i4++) {
							Node node4 = nodeList4.item(i4);
							String nodeName4 = node4.getNodeName();
							if (nodeName4.equals("#text")) continue;
							System.out.println("\t\t\t(4) nodeName :"+nodeName4+":");

							String nodeValue4 = getNodeValue (node4);
							System.out.println("\t\t\t(4) nodeValue :"+nodeValue4+":");
							keyItemInfo.add (nodeName4, nodeValue4);
						}
						keyInfo.add (keyItemInfo);
					}
				}
			}
			return sectionInfo;
		}
		catch (Exception ex) {
			throw new Exception ("Cannot handle document; "+ ex.getMessage());
		}
	}

	private static int getNodeSize(NodeList nodeList) {
		return (nodeList != null) ? nodeList.getLength() : 0;
	}

	private static String getNodeValue (Node node) {
		if (node == null) return null;
		if (node.getFirstChild() == null) return null;
		String strNodeValue = node.getFirstChild().getNodeValue();
//		LogHelper.info("node value :"+strNodeValue+":");
		return strNodeValue;
	}
	private static String getAttrValue (String name, Node node) {
		String strReturn = null;

		NamedNodeMap attrs = node.getAttributes();
		int attrCount = (attrs != null) ? attrs.getLength() : 0;
		for (int i=0; i<attrCount; i++) {
			Node attr = attrs.item(i);
//			System.out.println("Name "+attr.getNodeName()+" value "+attr.getNodeValue());
			if (name.equals(attr.getNodeName())) {
				strReturn = attr.getNodeValue();
				break;
			}
		}
		return strReturn;
	}
}
