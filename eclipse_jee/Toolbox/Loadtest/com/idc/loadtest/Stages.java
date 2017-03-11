
package com.idc.loadtest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Stages implements Serializable {
	private static final long serialVersionUID = 1;
	private String server;
	private String port;
	private String contextRoot;
	private String protocol = "http";

	public String getServer() {return server;}
	public String getPort() {
		if (port == null || port.trim().length() < 1) port = "80";
		return port;
	}
	public String getContextRoot() {return contextRoot;}
	public String getProtocol() {return protocol;}

	public void setServer (String server) {this.server = server;}
	public void setPort (String port) {this.port = port;}
	public void setContextRoot (String contextRoot) {this.contextRoot = contextRoot;}
	public void setProtocol (String ssl) {
		if (ssl.equalsIgnoreCase("https")) protocol = "https";
	}
	public String getUrl() {
		return getProtocol() + "://"+getServer() + ":" + getPort() + "/" + getContextRoot();
	}
	public String getUrlNoPort() {
		return getProtocol() + "://"+getServer() + "/" + getContextRoot();
	}
	private ArrayList<Stage> m_list = new ArrayList<Stage>();
	public Iterator<Stage> getItems() {return m_list.iterator();}
	public void add (Stage item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((Stage) m_list.get(i)).toString());
		return "("+getProtocol()+","+getServer()+","+getPort()+","+getContextRoot()+"),"+"("+buf.toString()+")";
	}
}
