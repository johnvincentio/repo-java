package com.idc.http;

/**
 * @author John Vincent
 */

public class AppException extends Exception {
	private static final long serialVersionUID = 1;
	public AppException (String msg) {
		super (msg);
	}
}
