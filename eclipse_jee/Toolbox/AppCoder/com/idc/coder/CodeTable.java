
package com.idc.coder;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author John Vincent
 *
 */

public class CodeTable {
	private String packageName = "com.idc.package";
	private String baseClassName;
	private String superClass;
	private String implementsClass;
	private ArrayList<CodePair> m_list = new ArrayList<CodePair>();

	public String getPackageName() {return packageName;}
	public void setPackageName (String s) {packageName = s;}
	public String getBaseClassName() {return baseClassName;}
	public void setBaseClassName (String s) {baseClassName = s;}
	public String getSuperClass() {return superClass;}
	public void setSuperClass (String s) {superClass = s;}
	public boolean isSuperClass() {
		if (superClass == null || superClass.length() < 1) return false;
		return true;
	}
	public String getImplementsClass() {return implementsClass;}
	public void setImplementsClass (String s) {implementsClass = s;}
	public boolean isImplementsClass() {
		if (implementsClass == null || implementsClass.length() < 1) return false;
		return true;
	}

	public void add (CodePair item) {m_list.add(item);}
	public Iterator<CodePair> getItems() {return m_list.iterator();}
	public int size() {return m_list.size();}
	public boolean isEmpty() {return size() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(Package) ("+getPackageName()+") \n\t(Class "+getBaseClassName()+") (SuperClass "+
				getSuperClass()+") (Implements "+getImplementsClass()+")\n");
		for (int i=0; i<m_list.size(); i++)
			buf.append(((CodePair) m_list.get(i)).toString());
		return buf.toString();
	}
}
