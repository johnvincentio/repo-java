package com.idc.http;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * @author John Vincent
 */

public class AppURL {
	private String m_strURL;
	private URL m_url;
	public AppURL (String s) throws AppException {
		m_strURL = s;
		try {
			m_url = new URL (m_strURL);
		}
		catch (MalformedURLException mfex) {
			throw new AppException ("URL "+m_strURL+" error; "+mfex.getMessage());
		}
	}
	public URL getURL() {return m_url;}
	public String getProtocol() {return m_url.getProtocol();}
	public String getFile() {return m_url.getFile();}
	public String getHost() {return m_url.getHost();}
	public String getPath() {return m_url.getPath();}

	public int getPort() {return m_url.getPort();}
	public int getDefaultPort() {return m_url.getDefaultPort();}
	public String getQuery() {return m_url.getQuery();}
	public String getRef() {return m_url.getRef();}
	public String getAuthority() {return m_url.getAuthority();}
	public String getUserInfo() {return m_url.getUserInfo();}
	
	public String toString() {
		return "("+getProtocol()+","+getFile()+","+getHost()+","+getPath()+","+
				getPort()+","+getDefaultPort()+","+getQuery()+","+getRef()+","+
				getAuthority()+","+getUserInfo()+")";
	}
}
