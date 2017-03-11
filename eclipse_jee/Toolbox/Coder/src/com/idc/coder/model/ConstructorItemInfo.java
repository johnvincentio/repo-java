package com.idc.coder.model;

import java.lang.reflect.Constructor;

public class ConstructorItemInfo {
	private Constructor<?> constructor;
	public ConstructorItemInfo (Constructor<?> constructor) {this.constructor = constructor;}
	public Constructor<?> getConstructor() {return constructor;}
	public String toString() {
		return "("+getConstructor()+")";
	}
}
