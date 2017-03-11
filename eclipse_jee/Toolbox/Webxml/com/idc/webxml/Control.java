package com.idc.webxml;

import java.io.File;

import com.idc.file.exec.OutputLine;

public class Control {
	private Webapp m_webapp;
	private OutputLine m_output;
	public Control (OutputLine output) {m_output = output;}
	public void convertWebXML(String strFile) {
		makeData(strFile);
		makeXML();
	}
	private void makeData(String strFile) {
		JVxml jvxml = new JVxml();
		File file = new File (strFile);
		m_webapp = jvxml.parse(file);
//		m_webapp.show();
	}
	private void makeXML() {
		output (0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		output (0, "<web-app id=\"WebApp_ID\" version=\"2.4\"xmlns=\"http://java.sun.com/xml/ns/j2ee\"		xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"		xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee		http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd\">");
		output (1, makeNode ("display-name", m_webapp.getDisplay()));
		Servlets servlets = m_webapp.getServlets();
		Servlet servlet;
		while (servlets.hasNext()) {
			servlet = servlets.getServlet();
			output (1, "<servlet>");
			output (2, makeNode ("description", ""));
			output (2, makeNode ("display-name", servlet.getName()));
			output (2, makeNode ("servlet-name", servlet.getServlet()));
			output (2, makeNode ("servlet-class", servlet.getSclass()));
			output (1, "</servlet>");
			servlets.getNext();
		}
		Mappings mappings = m_webapp.getMappings();
		Mapping mapping;
		while (mappings.hasNext()) {
			mapping = mappings.getMapping();
			output (1, "<servlet-mapping>");
			output (2, makeNode ("servlet-name", mapping.getName()));
			output (2, makeNode ("url-pattern", mapping.getUrl()));
			output (1, "</servlet-mapping>");
			mappings.getNext();
		}
		Welcome welcome = m_webapp.getWelcome();
		String strFile;
		output (1, "<welcome-file-list>");
		while (welcome.hasNext()) {
			strFile = welcome.getFile();
			output (2, makeNode ("welcome-file", strFile));
			welcome.getNext();
		}
		output (1, "</welcome-file-list>");
		output (0, "</web-app>");
	}
	private String makeNode (String name, String value) {
		return "<"+name+">"+value+"</"+name+">";
	}
	private void output (int tabs, String msg) {
		m_output.println(makeTabs(tabs)+msg);
	}
	private String makeTabs (int tabs) {
		String str = "";
		for (int i=0; i<tabs; i++) str += "\t";
		return str;
	}
}
/*
	private String makeNode (String name, int value) {
		return "<"+name+">"+Integer.toString(value)+"</"+name+">";
	}
*/