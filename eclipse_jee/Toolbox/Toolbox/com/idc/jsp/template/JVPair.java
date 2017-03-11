
package com.idc.jsp.template;

public class JVPair {
	private String m_strLink;
	private String m_strText;
	public JVPair (String strLink, String strText) {
		m_strLink = strLink;
		m_strText = strText;
	}
	public String getLink() {return m_strLink;}
	public String getText() {return m_strText;}
}
