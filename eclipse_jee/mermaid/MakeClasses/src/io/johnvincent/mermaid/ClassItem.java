package io.johnvincent.mermaid;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public class ClassItem {
	private Class<?> m_clazz;

	private String m_name;
	private String m_packageName;
	private Constructor[] m_constructors;
	private Method[] m_methods;
	private Field[] m_fields;
	
	private URL m_urlfrom;

	public ClassItem(Class<?> clazz) {
		m_clazz = clazz;
		
		m_name = clazz.getSimpleName();
		m_packageName = clazz.getPackage().getName();
		m_constructors = clazz.getDeclaredConstructors();
		m_methods = clazz.getDeclaredMethods();
		m_fields = clazz.getDeclaredFields();
		
		ProtectionDomain pDomain = clazz.getProtectionDomain();
		CodeSource cSource = pDomain.getCodeSource();
		m_urlfrom = cSource.getLocation();
	}

	public String getName() {return m_name;}
	public String getPackageName() {return m_packageName;}
	public Constructor[] getConstructors() {return m_constructors;}
	public Method[] getMethods() {return m_methods;}
	public Field[] getFields() {return m_fields;}

	public void show() {
		System.out.println("Class: "+m_name);
		System.out.println("Package: "+m_packageName);
		System.out.println("URL " + m_urlfrom.getFile());
		for (int i = 0; i < m_constructors.length; i++) {
			System.out.println("Constructor: " + m_constructors[i]);
		}
		for (int i = 0; i < m_methods.length; i++) {
			System.out.println("Method: " + m_methods[i]);
		}
		for (int i = 0; i < m_fields.length; i++) {
			System.out.println("Field: " + m_fields[i].toString());
		}
	}
	
	public String toString() {
		return "("+getName()+","+getPackageName()+","+getConstructors()+","+getMethods()+","+getFields()+")";
	}
}

//public void setName (String name) {m_name = name;}
//public void setPackageName (String packageName) {this.m_packageName = packageName;}
//public void setConstructors (Constructor[] constructors) {m_constructors = constructors;}
//public void setMethods (Method[] methods) {m_methods = methods;}
//public void setFields (Field[] fields) {m_fields = fields;}

/*
	Package p = cls.getPackage();
	System.out.println("package: " + p.getName());

	Constructor[] constructors = cls.getDeclaredConstructors();
	for (int i = 0; i < constructors.length; i++) {
		System.out.println("Constructor: " + constructors[i]);
	}

	Method[] methods = cls.getDeclaredMethods();
	for (int i = 0; i < methods.length; i++) {
		System.out.println("Method: " + methods[i]);
	}

	Field[] fields = cls.getDeclaredFields();
	for (int i = 0; i < fields.length; i++) {
		System.out.println("Field: " + fields[i].toString());
	}
*/


