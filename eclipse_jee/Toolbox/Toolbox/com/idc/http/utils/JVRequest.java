package com.idc.http.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.Iterator;

public class JVRequest {
	private static JVRequest instance = null;

	private JVRequest() {
	}

	public static JVRequest getInstance() {
		if (instance == null)
			instance = new JVRequest();
		return instance;
	}

	public void junk() {
		System.out.println("junk()");
	}

	public void junk2(String msg) {
	}

	public void showAll(HttpServletRequest req, String msg) {
		showRequest(req, msg);
		showAttributes(req, msg);
		showParameters(req, msg);
		showHeaders(req, msg);
		showCookies(req, msg);
	}

	public void showRequest(HttpServletRequest req, String msg) {
		System.out.println(">>> showRequest; " + msg);
		System.out.println("server :" + req.getServerName());
		System.out.println("contextpath :" + req.getContextPath());
		System.out.println("authtype :" + req.getAuthType());
		System.out.println("contentlength :" + req.getContentLength());
		System.out.println("contentType :" + req.getContentType());
		System.out.println("pathinfo :" + req.getPathInfo());
		System.out.println("protocol :" + req.getProtocol());
		System.out.println("remoteaddr :" + req.getRemoteAddr());
		System.out.println("remotehost :" + req.getRemoteHost());
		System.out.println("remoteuser :" + req.getRemoteUser());
		System.out.println("sessionid :" + req.getRequestedSessionId());
		System.out.println("uri :" + req.getRequestURI());
		System.out.println("url :" + req.getRequestURL().toString());
		System.out.println("scheme :" + req.getScheme());
		System.out.println("servername :" + req.getServerName());
		System.out.println("serverport :" + req.getServerPort());
		System.out.println("servletpath :" + req.getServletPath());
		System.out.println("isRequestedSessionIdFromCookie :"
				+ req.isRequestedSessionIdFromCookie());
		System.out.println("isRequestedSessionIdFromURL :"
				+ req.isRequestedSessionIdFromURL());
		System.out.println("isRequestedSessionIdValid :"
				+ req.isRequestedSessionIdValid());
		System.out.println("isSecure :" + req.isSecure());
		System.out.println("<<< showRequest");
	}

	public void showAttributes(HttpServletRequest req, String msg) {
		System.out.println(">>> showAttributes; " + msg);
		String strElement, strValue;
		Object obj;

		System.out.println(">>> (Request)");
		Enumeration<?> enumjv = req.getAttributeNames();
		if (enumjv != null) {
			while (enumjv.hasMoreElements()) {
				strElement = (String) enumjv.nextElement();
				strValue = "";
				obj = req.getAttribute(strElement);
				if (obj instanceof String)
					strValue = (String) obj;
				else
					strValue = obj.toString();
				System.out.println("Element (" + strElement + ") Value ("
						+ strValue + ")");
			}
		}
		System.out.println("<<< (Request)");

		System.out.println(">>> (Session)");
		HttpSession session = req.getSession();
		enumjv = session.getAttributeNames();
		if (enumjv != null) {
			while (enumjv.hasMoreElements()) {
				strElement = (String) enumjv.nextElement();
				strValue = "";
				obj = session.getAttribute(strElement);
				if (obj instanceof String)
					strValue = (String) obj;
				else
					strValue = obj.toString();
				System.out.println("Element (" + strElement + ") Value ("
						+ strValue + ")");
			}
		}
		System.out.println("<<< (Session)");

		System.out.println("<<< showAttributes");
	}

	@SuppressWarnings("unchecked")
	public void showParameters (HttpServletRequest req, String msg) {
		System.out.println(">>> showParameters; " + msg);
		Map<String, String[]> params = req.getParameterMap();
		Iterator<Map.Entry<String, String[]>> iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String[]> me = iter.next();
			System.out.println("Parameter :" + me.getKey() + ":");
			String vals[] = me.getValue();
			for (int i = 0; i < vals.length; i++)
				System.out.println("Value :" + vals[i] + ":");
		}
		System.out.println("<<< showParameters; " + msg);
	}

	public void showHeaders(HttpServletRequest req, String msg) {
		System.out.println(">>> showHeaders; " + msg);
		String strElement, strValue;

		Enumeration<?> enumjv = req.getHeaderNames();
		if (enumjv != null) {
			while (enumjv.hasMoreElements()) {
				strElement = (String) enumjv.nextElement();
				strValue = req.getHeader(strElement);
				System.out.println("Element (" + strElement + ") Value ("
						+ strValue + ")");
			}
		}
		System.out.println("<<< showHeaders; " + msg);
	}

	public void showCookies(HttpServletRequest req, String msg) {
		System.out.println(">>> showCookies; " + msg);
		Cookie[] cookies = req.getCookies();
		for (int i = 0; cookies != null && i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			System.out.println("Name " + cookie.getName() + " Value "
					+ cookie.getValue());
			System.out.println("   Domain " + cookie.getDomain());
			System.out.println("   Path " + cookie.getPath());
			System.out.println("   Secure " + cookie.getSecure());
			System.out.println("   Version " + cookie.getVersion());
			System.out.println("   Comment " + cookie.getComment());
			System.out.println("   MaxAge " + cookie.getMaxAge());
		}
		System.out.println("<<< showCookies; " + msg);
	}
}
