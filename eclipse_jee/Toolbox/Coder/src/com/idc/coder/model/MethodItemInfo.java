package com.idc.coder.model;

import java.lang.reflect.Method;

public class MethodItemInfo {
	private Method method;
	public MethodItemInfo (Method method) {this.method = method;}
	public Method getMethod() {return method;}
	public String toString() {
		return "("+getMethod()+")";
	}
}
