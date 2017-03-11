package com.idc.http;

import java.io.Serializable;

/**
 * @author John Vincent
 *
 */

public class AppCookieItem implements Serializable {
	private static final long serialVersionUID = 1;
	private String cookie;
	public AppCookieItem (String cookie) {
		this.cookie = cookie;
	}
	public String getCookie() {return cookie;}
	public void setCookie (String cookie) {this.cookie = cookie;}
	public String toString() {
		return "("+getCookie()+")";
	}
}
