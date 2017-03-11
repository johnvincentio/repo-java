package com.idc.webxml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class JVxml {
	public Webapp parse(String strXML) {
//		System.out.println("JVxml::parse; file :"+strXML+":");
		InputSource source = new InputSource(new StringReader(strXML));
		return handle (source);
	}
	public Webapp parse (File file) {
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
	private Webapp handle(InputSource source) {
		DocumentBuilderFactory docBuilderFactory;
		DocumentBuilder docBuilder;
		Document doc;

//		System.out.println(">>> JVxml::handle");
		Webapp webapp = new Webapp();
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(source);
			doc.getDocumentElement().normalize();
			webapp = handleWebapp (webapp, doc);
		}
		catch (Exception ex) {
			System.out.println("Exception "+ex.getMessage());
		}
//		System.out.println("<<< JVxml::handle");
		return webapp;
	}
	private Webapp handleWebapp (Webapp webapp, Document doc) throws AppException {
//		System.out.println(">>> JVxml::handleWebapp");
		Element elem = doc.getDocumentElement();
//		System.out.println ("(1) nodeName "+elem.getNodeName());
		if (! elem.getNodeName().equals("web-app")) {
			throw new AppException ("Not a web-app");
		}

		NodeList nodeList = elem.getChildNodes();
		Node node;
		Servlets servlets = new Servlets();
		Mappings mappings = new Mappings();
		Welcome welcome = new Welcome();
		int len = (nodeList != null) ? nodeList.getLength() : 0;
		for (int i=0; i<len; i++) {
			node = nodeList.item(i);
			if (isThisNode (node, "display-name"))
			webapp.setDisplay(getNodeValue(node));
			else if (isThisNode (node, "servlet")) {
				servlets.add (handleServlet (node));
			}
			else if (isThisNode (node, "servlet-mapping")) {
				mappings.add (handleMapping (node));
			}
			else if (isThisNode (node, "welcome-file-list")) {
				welcome = handleWelcome (welcome, node);
			}
		}
		webapp.setServlets(servlets);
		webapp.setMappings(mappings);
		webapp.setWelcome(welcome);
//		System.out.println("<<< JVxml::handleWebapp");
		return webapp;
	}
	private Servlet handleServlet (Node pNode) {
		Servlet servlet = new Servlet();
		NodeList nodeList = pNode.getChildNodes();
		Node node;
		int len = (nodeList != null) ? nodeList.getLength() : 0;
		for (int i=0; i<len; i++) {
			node = nodeList.item(i);
			if (isThisNode (node, "display-name"))
			servlet.setName(getNodeValue(node));
			else if (isThisNode (node, "servlet-name"))
			servlet.setServlet(getNodeValue(node));
			else if (isThisNode (node, "servlet-class"))
			servlet.setSclass(getNodeValue(node));
		}
//		servlet.show();
		return servlet;
	}
	private Mapping handleMapping (Node pNode) {
		Mapping mapping = new Mapping();
		NodeList nodeList = pNode.getChildNodes();
		Node node;
		int len = (nodeList != null) ? nodeList.getLength() : 0;
		for (int i=0; i<len; i++) {
			node = nodeList.item(i);
			if (isThisNode (node, "servlet-name"))
			mapping.setName(getNodeValue(node));
			else if (isThisNode (node, "url-pattern"))
			mapping.setUrl(getNodeValue(node));
		}
		return mapping;
	}
	private Welcome handleWelcome (Welcome welcome, Node pNode) {
		NodeList nodeList = pNode.getChildNodes();
		Node node;
		int len = (nodeList != null) ? nodeList.getLength() : 0;
		for (int i=0; i<len; i++) {
			node = nodeList.item(i);
			if (isThisNode (node, "welcome-file"))
			welcome.add (getNodeValue(node));
		}
		return welcome;
	}
	private boolean isThisNode (Node node, String name) {
		if (node.getNodeType() != Node.ELEMENT_NODE) return false;
		if (node.getNodeName().equals(name)) return true;
		return false;
	}
	private String getNodeValue (Node node) {
		String strNodeValue = node.getFirstChild().getNodeValue().trim();
//		System.out.println("node value :"+strNodeValue+":");
		return strNodeValue;
	}
}
/*
	private String getAttrValue (String name, Node node) {
		String strReturn = null;

		NamedNodeMap attrs = node.getAttributes();
		int attrCount = (attrs != null) ? attrs.getLength() : 0;
		for (int i=0; i<attrCount; i++) {
			Node attr = attrs.item(i);
//		 		 		 System.out.println("Name "+attr.getNodeName()+" value
//			"+attr.getNodeValue());
			if (name.equals(attr.getNodeName())) {
				strReturn = attr.getNodeValue();
				break;
			}
		}
		return strReturn;
	}
*/