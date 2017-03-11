package com.idc.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author John Vincent
 */

public class Form implements Serializable {
	private static final long serialVersionUID = 1;
	private static final String ENCODER = "UTF-8";

	private ArrayList<FormItem> m_list = new ArrayList<FormItem>();

	public Iterator<FormItem> getItems() {return m_list.iterator();}
	public void add (FormItem item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public String getFormItemsEncoded() {
		try {
			StringBuffer buf = new StringBuffer();
			FormItem formItem;
			for (int i=0; i<m_list.size(); i++) {
				formItem = (FormItem) m_list.get(i);
				if (i > 0) buf.append ("&");
				buf.append (URLEncoder.encode(formItem.getKey(), ENCODER));
				buf.append ("=");
				buf.append (URLEncoder.encode(formItem.getValue(), ENCODER));
			}
			return buf.toString();
		}
		catch (UnsupportedEncodingException ueex) {
//			LogBroker.debug(this,"getEncoded error; "+ueex.getMessage());
			return "";
		}
	}

	public String getFormItems() {
		StringBuffer buf = new StringBuffer();
		FormItem formItem;
		for (int i=0; i<m_list.size(); i++) {
			formItem = (FormItem) m_list.get(i);
			if (i > 0) buf.append ("&");
			buf.append (formItem.getKey());
			buf.append ("=");
			buf.append (formItem.getValue());
		}
		return buf.toString();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((FormItem) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}

//field1=value1&field2=value2&field3=value3

