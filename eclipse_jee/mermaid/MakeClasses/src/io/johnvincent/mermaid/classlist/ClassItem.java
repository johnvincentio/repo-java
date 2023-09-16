package io.johnvincent.mermaid.classlist;

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

	private ConstructorList m_constructorList = new ConstructorList();
	private MethodList m_methodList = new MethodList();	
	private FieldList m_fieldList = new FieldList();
	
	private URL m_urlfrom;

	public ClassItem(Class<?> clazz, FileItem fileItem) {
		m_clazz = clazz;
		m_fileItem = fileItem;
	}

	public boolean hasAnyData() {
		if (m_constructorList.getSize() > 0) return true;
		if (m_methodList.getSize() > 0) return true;
		if (m_fieldList.getSize() > 0) return true;
		return false;
	}
	
	public ConstructorList getConstructorList() {return m_constructorList;}
	public MethodList getMethodList() {return m_methodList;}
	public FieldList getFieldList() {return m_fieldList;}
	
	public void calculate() {
		m_name = m_clazz.getName();
		m_simpleName = m_clazz.getSimpleName();
		m_packageName = m_clazz.getPackage().getName();
		
		ProtectionDomain pDomain = m_clazz.getProtectionDomain();
		CodeSource cSource = pDomain.getCodeSource();
		m_urlfrom = cSource.getLocation();

		handleConstructors();
		handleMethods();
		handleFields();
	}

	private String getClassFromError(NoClassDefFoundError ex) {
		String msg = ex.getMessage();
		String result = msg.substring(msg.lastIndexOf("/") + 1);
		return result;
	}
	
	private void showError(NoClassDefFoundError ex, String type) {
		System.out.println("NoClassDefFoundError in "+type+" name "+m_name+
			" fileItem "+m_fileItem.toString()+" ex "+ex);
	}
	
	private void handleConstructors() {
		try {
			m_constructorList.add(m_packageName, m_clazz.getDeclaredConstructors());
		}
		catch (NoClassDefFoundError ex) {
			showError(ex, "handleConstructors");
			m_constructorList.add(getClassFromError(ex));
		}
	}
	
	private void handleMethods() {
		try {
			m_methodList.add(m_clazz.getDeclaredMethods());
		}
		catch (NoClassDefFoundError ex) {
			showError(ex, "handleMethods");
			m_methodList.add(getClassFromError(ex));
		}
	}
	
	private void handleFields() {
		try {
			m_fieldList.add(m_clazz.getDeclaredFields());
		}
		catch (NoClassDefFoundError ex) {
			showError(ex, "handleFields");
			m_fieldList.add(getClassFromError(ex));
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

	public void show() {
		System.out.println("Class: "+m_name);
		System.out.println("SimpleName: "+m_simpleName);
		System.out.println("Package: "+m_packageName);
		System.out.println("URL " + m_urlfrom.getFile());
		for (int i = 0; i < m_constructorList.getSize(); i++) {
			System.out.println("Constructor: " + m_constructorList.getItem(i).toString());
		}
		for (int i = 0; i < m_methodList.getSize(); i++) {
			System.out.println("Method: " + m_methodList.getItem(i).toString());
		}
		for (int i = 0; i < m_fieldList.getSize(); i++) {
			System.out.println("Field: " + m_fieldList.getItem(i).toString());
		}
	}
	
	public String toString() {
		return "("+getName()+","+getSimpleName()+","+getPackageName()+","+
				getConstructorList()+","+getMethodList()+","+getFieldList()+")";
	}
}
