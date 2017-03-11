package com.idc.test;

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

import com.idc.trace.LogHelper;

public class JVxml2 {
	public void parse (String strFile) {
		parse (new File(strFile));
	}
	public void parse (File file) {
		InputSource source = null;
		try{
			source = new InputSource(new FileInputStream(file));
		}
		catch(FileNotFoundException ex) {
			LogHelper.error("--- FileNotFoundException ---:");
			LogHelper.error(ex.getMessage());
		}
		handle (source);
	}
	private void handle(InputSource source) {
		DocumentBuilderFactory docBuilderFactory;
		DocumentBuilder docBuilder;
		Document doc;
//		Element elem;

		try{
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(source);
			doc.getDocumentElement().normalize();

			Element elem1 = doc.getDocumentElement();
//			LogHelper.info ("(1) nodeName "+elem1.getNodeName());
			if(! elem1.getNodeName().equals("xmi:XMI")) {
				throw new Exception ("Not a xmi:XMI");
			}
			NodeList nodeList2 = elem1.getChildNodes();
			int len2 = (nodeList2 != null) ? nodeList2.getLength() : 0;
			for(int i2=0; i2<len2; i2++) {
				Node node2 = nodeList2.item(i2);
//				LogHelper.info("has child (2) node "+node2.getNodeName());
				if(node2.getNodeType() == Node.ELEMENT_NODE && node2.getNodeName().equals("namebindings:EjbNameSpaceBinding")) {
					String str1 = getAttrValue ("name", node2);
					String str2 = getAttrValue ("nameInNameSpace", node2);
					String str3 = getAttrValue ("bindingLocation", node2);
					String str4 = getAttrValue ("ejbJndiName", node2);
					makeCmd (str1, str2, str3, str4);
				}
			}
		}
		catch(Exception ex) {
			LogHelper.error("Exception "+ex.getMessage());
		}
	}
	private void makeCmd (String s1, String s2, String s3, String s4) {
		String str1 = s1;
		if(s2.equals(s1)) str1 = "";
		String str4 = s3;
		if(s4.equals(s2)) str4 = "";
		StringBuffer sb = new StringBuffer();
		sb.append("JVaddNSBEjb $cellName $nodeName $serverName ");
		sb.append(makeString(str1));
		sb.append(makeSpace()).append(makeString(s2));
//		sb.append(makeSpace()).append(makeString(s3));
		sb.append(" $bindingLocation ");
		sb.append(makeSpace()).append(makeString(str4));
		sb.append(";");
		LogHelper.info(sb.toString());
	}
	private String makeString(String s1) {
		return"\""+ s1 + "\"";
	}
	private String makeSpace() {return" ";}
	private String getAttrValue (String name, Node node) {
		String strReturn = null;

		NamedNodeMap attrs = node.getAttributes();
		int attrCount = (attrs != null) ? attrs.getLength() : 0;
		for(int i=0; i<attrCount; i++) {
			Node attr = attrs.item(i);
//			LogHelper.info("Name "+attr.getNodeName()+" value "+attr.getNodeValue());
			if(name.equals(attr.getNodeName())) {
				strReturn = attr.getNodeValue();
				break;
			}
		}
		return strReturn;
	}
}
