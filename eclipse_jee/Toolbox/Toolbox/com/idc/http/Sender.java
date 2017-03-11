package com.idc.http;

import java.util.Iterator;
import java.util.Map;

/**
 * @author John Vincent
 */

public class Sender {
	private AppURL m_senderURL = null;
	private boolean m_bPostMethod = false;
	private Form m_form = new Form();

	public Sender (String s) throws AppException {
		setSenderURL(s);
	}
	public AppURL getSenderURL() {return m_senderURL;}
	public void setSenderURL (String s) throws AppException {
		m_senderURL = new AppURL (s);		
	}
	public boolean isUrlSSL() {
		if (m_senderURL == null) return false;
		String str = m_senderURL.getProtocol();
		if (str.equalsIgnoreCase("https")) return true;
		return false;
	}
	
	public void setPostMethod() {m_bPostMethod = true;}
	public void setGetMethod() {m_bPostMethod = false;}
	public boolean isPostMethod() {return m_bPostMethod;}

	public void addFormItem (FormItem pairInfo) {m_form.add (pairInfo);}
	public void addFormItem (String key, String value) {m_form.add (new FormItem (key,value));}
	public void addFormItem (Map<String, String[]> map) {
		Iterator<Map.Entry<String, String[]>> iterator = map.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) iterator.next();
			String [] values = (String []) entry.getValue();
			String key = (String) entry.getKey();
			for (int i = 0; i < values.length; i++) {
				addFormItem(key, values[i]);
			}
		}
	}

	public String getEncodedBody() {return m_form.getFormItemsEncoded();}
	public String getBody() {return m_form.getFormItems();}
	public int getEncodedBodyLength() {return getEncodedBody().length();}

	public String toString() {
		return "(AppURL :"+m_senderURL.toString()+": AppURL.getURL() :"+m_senderURL.getURL().getPath()+": "+
			"isPost "+isPostMethod()+" isSSL "+isUrlSSL()+" body :"+getBody()+": length "+getEncodedBodyLength()+
			" Form :"+m_form.toString()+":)";
	}
}
