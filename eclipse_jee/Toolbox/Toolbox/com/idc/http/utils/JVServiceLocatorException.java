package com.idc.http.utils;

public class JVServiceLocatorException extends Exception {
	private static final long serialVersionUID = 1;
	
	private String message;
	public JVServiceLocatorException (String message) {this.message = message;}
	public JVServiceLocatorException (Exception e) {this.message = e.getMessage();}
	public String getMessage() {return message;}
}
