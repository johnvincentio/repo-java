package com.idc.http;

import java.net.HttpURLConnection;

/**
 * @author John Vincent
 */

public class Receiver {
	private StringBuffer m_body = null;
	private int m_responseCode;
//	private Map m_map = new HashMap();
	private String m_location = "";
	
	public StringBuffer getBody() {return m_body;}
	public boolean isBody (String strCompare) {
		if (strCompare == null || strCompare.length() < 1) return false;
		if (m_body == null || m_body.length() < 1) return false;
		if (m_body.indexOf(strCompare) > -1) return true;
		return false;
	}

	public int getResponseCode() {return m_responseCode;}
	public void setBody(String s) {m_body = new StringBuffer(s);}
	public void setBody(StringBuffer sb) { m_body = sb; }
	public void setResponseCode (int n) {m_responseCode = n;}
	
	public String getLocation() {return m_location;}
	public void setLocation (HttpURLConnection httpConnection) {
		m_location = httpConnection.getHeaderField ("Location");
	}
	public void setLocation(Sender sender) {
		m_location = sender.getSenderURL().getPath();
	}

	public String toString() {
		return "("+getResponseCode()+","+getLocation()+")";
	}
}
