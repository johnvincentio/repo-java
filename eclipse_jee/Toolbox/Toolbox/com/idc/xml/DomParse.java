
package com.idc.xml;

import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
public class DomParse {

	public void parse (String strFile) {
		DocumentBuilderFactory docBuilderFactory;
		DocumentBuilder docBuilder;
		Document doc;
//		Element elem;

		System.out.println("File "+strFile);
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
/*
//			docBuilderFactory.setNamespaceAware(true);
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
*/
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new File (strFile));   
			doc.getDocumentElement().normalize();

			Element root = doc.getDocumentElement();
			showElement (root, "");
		}
		catch (ParserConfigurationException pcex) {
			System.err.println("Failed to create DOM parser:"+pcex.getMessage());
		}
		catch (SAXException sex) {
			System.err.println("General SAX exception:"+sex.getMessage());
		}
		catch (IOException iex) {
			System.err.println("IO exception:"+iex.getMessage());
		}
	}

	public void showElement (Node node, String strIndent) {
		StringBuffer buf = new StringBuffer(strIndent);
		buf.append('<');

//		System.out.println("nodeName "+node.getNodeName());
		int tag_start = buf.length();
		buf.append (node.getNodeName());

		NamedNodeMap attrs = node.getAttributes();
		int attrCount = (attrs != null) ? attrs.getLength() : 0;
		StringBuffer abuf = new StringBuffer();
		for (int i=0; i<attrCount; i++) {
			Node attr = attrs.item(i);
			abuf.append(' ');
			abuf.append(attr.getNodeName());
			abuf.append("=\"");
			abuf.append(attr.getNodeValue());
			abuf.append('"');
		}
		System.out.print (buf.toString());
		System.out.print (abuf.toString());
		System.out.println (">");
		buf.append('>');

		NodeList children = node.getChildNodes();
		int len = (children != null) ? children.getLength() : 0;
		strIndent += "    ";
		for (int i=0; i<len; i++) {
			Node node2 = children.item(i);
			switch (node2.getNodeType()) {
				case Node.TEXT_NODE:
					showText (node2, strIndent);
					break;
				case Node.ELEMENT_NODE:
					showElement (node2, strIndent);
					break;
			}
		}
		buf.insert (tag_start, '/');
		System.out.println (buf.toString());
	}

	public void showText (Node node, String strIndent) {
		String value = node.getNodeValue().trim();
		if (value.length() > 0) {
			System.out.print (strIndent);
			StringTokenizer xmlTokens = new StringTokenizer (value,"&<>'\"",true);
			while (xmlTokens.hasMoreTokens()) {
				String t = xmlTokens.nextToken();
				if (t.length() == 1) {
					if (t.equals ("&"))
						System.out.print ("&amp;");
					else if (t.equals ("<"))
						System.out.print ("&lt;");
					else if (t.equals (">"))
						System.out.print ("&gt;");
					else if (t.equals ("'"))
						System.out.print ("&apos;");
					else if (t.equals ("\""))
						System.out.print ("&quot;");
					else
						System.out.print (t);
				}
				else
					System.out.print (t);
			}
			System.out.println ();
		}
	}

	public static void main (String args[]) {
		if (args.length != 1) {
			System.err.println("Usage: DomParse filename");
			System.exit(1);
		}
		DomParse domparse = new DomParse();
		domparse.parse (args[0]);
	}
}
