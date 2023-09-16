package io.johnvincent.mermaid.classlist;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import io.johnvincent.mermaid.filelist.FileItem;

public class ClassItem {
	private Class<?> m_clazz;
	private FileItem m_fileItem;

	private String m_name;
	private String m_simpleName;
	private String m_packageName;
	private Constructor[] m_constructors;
	private Method[] m_methods = null;
//	private Field[] m_fields = null;
	
	private FieldList m_fieldList = new FieldList();
	
	private URL m_urlfrom;

	public ClassItem(Class<?> clazz, FileItem fileItem) {
		m_clazz = clazz;
		m_fileItem = fileItem;
	}

	public FieldList getFieldList() {return m_fieldList;}
	
	public void calculate() {
		m_name = m_clazz.getName();
		m_simpleName = m_clazz.getSimpleName();
		m_packageName = m_clazz.getPackage().getName();
//		System.out.println("--- calculate; simpleName "+m_simpleName);
//		System.out.println("--- calculate; name "+m_name+" simpleName "+m_simpleName);
		
		ProtectionDomain pDomain = m_clazz.getProtectionDomain();
		CodeSource cSource = pDomain.getCodeSource();
		m_urlfrom = cSource.getLocation();

		handleConstructors();
		handleMethods();
		handleFields();
	}
	
	private void showError(NoClassDefFoundError ex, String type) {
		System.out.println("NoClassDefFoundError in "+type+" name "+m_name+
			"fileItem "+m_fileItem.toString()+" ex "+ex);	
	}
	
	private void handleConstructors() {
		try {
			m_constructors = m_clazz.getDeclaredConstructors();
		}
		catch (NoClassDefFoundError ex) {
			showError(ex, "handleConstructors");
		}
	}
	
	private void handleMethods() {
		try {
			m_methods = m_clazz.getDeclaredMethods();
		}
		catch (NoClassDefFoundError ex) {
			showError(ex, "handleMethods");
		}
	}
	
	private void handleFields() {
		try {
			m_fieldList.add(m_clazz.getDeclaredFields());
		}
		catch (NoClassDefFoundError ex) {
			showError(ex, "handleFields");
		}
	}
	
	public String getName() {
		return m_name;
	}
	public String getSimpleName() {
		if (m_simpleName != null && m_simpleName.length() > 0) return m_simpleName;
		return m_name;
	}
	public String getPackageName() {return m_packageName;}
	public Constructor[] getConstructors() {return m_constructors;}
	public Method[] getMethods() {return m_methods;}

	public void show() {
		System.out.println("Class: "+m_name);
		System.out.println("SimpleName: "+m_simpleName);
		System.out.println("Package: "+m_packageName);
		System.out.println("URL " + m_urlfrom.getFile());
		for (int i = 0; i < m_constructors.length; i++) {
			System.out.println("Constructor: " + m_constructors[i]);
		}
		for (int i = 0; i < m_methods.length; i++) {
			System.out.println("Method: " + m_methods[i]);
		}
		for (int i = 0; i < m_fieldList.getSize(); i++) {
			System.out.println("Field: " + m_fieldList.getItem(i).toString());
		}
	}
	
	public String toString() {
		return "("+getName()+","+getSimpleName()+","+getPackageName()+","+getConstructors()+","+getMethods()+","+getFieldList()+")";
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

