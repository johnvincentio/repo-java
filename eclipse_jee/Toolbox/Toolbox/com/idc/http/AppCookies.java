package com.idc.http;

/**
* @author John Vincent
*
*/

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import com.idc.trace.LogHelper;

public class AppCookies implements Serializable {
	private static final long serialVersionUID = 1;
	private ArrayList<AppCookieItem> m_list = new ArrayList<AppCookieItem>();

	public void receiveCookies (HttpURLConnection httpConnection) {
		addCookie (httpConnection.getHeaderFields());
	}
	private void addCookie (Map<String, List<String>> map) {
//		LogHelper.info(">>> AppCookies::addCookie");
		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (key == null) continue;
			if (! key.equals("Set-Cookie")) continue;
			List<String> keyList = (List<String>) map.get(key);
			Iterator<String> listIter = keyList.iterator();
			while (listIter.hasNext()) {
				String listValue = (String) listIter.next();
//				LogHelper.info("Key = " + key + " value = " + listValue);
				m_list.add (new AppCookieItem (listValue));
			}
		}
	}
	public Iterator<AppCookieItem> getItems() {return m_list.iterator();}
	public int getSize() {return m_list.size();}
	public boolean isCookied() {return getSize() > 0;}

	public void sendCookies (HttpURLConnection httpConnection) {
		if (isCookied()) {
			AppCookieItem appCookieItem;
			Iterator<AppCookieItem> iter = getItems();
			while (iter.hasNext()) {
				appCookieItem = (AppCookieItem) iter.next();
//				LogHelper.info("AppCookies::sendCookies; sending cookie; "+appCookieItem.getCookie());
				httpConnection.addRequestProperty("Cookie", appCookieItem.getCookie());
			}
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((AppCookieItem) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
