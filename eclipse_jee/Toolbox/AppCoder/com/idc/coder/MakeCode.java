package com.idc.coder;

import java.util.Iterator;

/**
 * @author John Vincent
 *
 */

public abstract class MakeCode {
	public static final String NL = "\n";
	public static final String TAB = "\t";
	public static final String QUOTE = "\"";

	private StringBuffer m_buf = new StringBuffer();
	private CodeTable m_codeTable;
	public MakeCode (CodeTable codeTable) {m_codeTable = codeTable;}
	public CodeTable getCodeTable() {return m_codeTable;}

	public void append (String s) {m_buf.append(s);}
	public void appendNL (String s) {append(s); append(NL);}
	public void appendNL() {append(NL);}
	public String getBuffer() {return m_buf.toString();}
	public void showbuf (String msg) {System.out.println(msg+" "+getBuffer());}

	public String getPackageName() {return m_codeTable.getPackageName();}
	public String getBaseClassName() {return m_codeTable.getBaseClassName();}
	public String getSuperClassName() {return m_codeTable.getSuperClass();}
	public boolean isSuperClass() {return m_codeTable.isSuperClass();}
	public String getBeanClassName() {return m_codeTable.getBaseClassName() + "ItemInfo";}
	public String getCollectionClassName() {return m_codeTable.getBaseClassName() + "Info";}
	public String getCollectionItemClassName() {return m_codeTable.getBaseClassName() + "ItemInfo";}

	public String getCollectionVariableName() {
		JVString s = new JVString(getCollectionClassName());
		s.initLower();
		return s.getString();
	}
	public String getBeanVariableName() {
		JVString s = new JVString(getBeanClassName());
		s.initLower();
		return s.getString();
	}
	public String getFormInfoClassName() {return m_codeTable.getBaseClassName() + "FormInfo";}
	public String getHttpPageInfoClassName() {return m_codeTable.getBaseClassName() + "HttpPageInfo";}
	public String getHttpInfoClassName() {return m_codeTable.getBaseClassName() + "HttpInfo";}

	public static String methodName (String s) {
		JVString jvstr = new JVString(s);
		jvstr.initUpper();
		return jvstr.getString();
	}
	public static String variableName (String s) {
		JVString jvstr = new JVString(s);
		jvstr.initLower();
		return jvstr.getString();
	}
	public static String methodGetter (CodePair codePair) {
		if ("boolean".equals(codePair.getType()))
			return "is" + methodName(codePair.getName()) + "()";
		return "get" + methodName(codePair.getName()) + "()";
	}
	public static String addQuotes (String s) {
		return QUOTE + s + QUOTE;
	}
	public static String addKeyPair (String key, String value) {
		return key + "=" + addQuotes (value);
	}

	public String makePackage() {return "package "+getPackageName()+";"+NL;}
	public String makeStartClass() {
		StringBuffer buf = new StringBuffer();
		buf.append("public class ").append(getBaseClassName());
		buf.append(" implements Serializable");
		if (isSuperClass()) buf.append(" extends ").append(getSuperClassName());
		buf.append (" {").append(NL);
		buf.append (TAB).append ("private static final long serialVersionUID = 1L;").append(NL).append(NL);
		return buf.toString();
	}
	public String makeEndClass() {return "}"+NL+NL;}

	public String makeMembers() {return makeMembers (false);}
	public String makeMembers (boolean hash) {
		StringBuffer buf = new StringBuffer();
		CodePair codePair;
		Iterator<CodePair> iter = m_codeTable.getItems();
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			buf.append (TAB+codePair.getVisible()+" "+codePair.getType()+" "+codePair.getName());
			if (codePair.isValue()) buf.append(" = ").append(codePair.getValue());
			buf.append (";");
			if (codePair.isComment()) buf.append("\t\t// ").append(codePair.getComment());
			buf.append (NL);
		}
		if (hash) 
			buf.append (TAB+"private int hashCode = 0;"+NL);
		return buf.toString();
	}

	public String makeConstructor(String className) {
		if (m_codeTable.isEmpty()) return "";
		return TAB+"public "+className+"() {}"+NL;
	}
	public String makeConstructorArgs (String className) {return makeConstructorArgs (className, false);}
	public String makeConstructorArgs (String className, boolean hash) {
		if (m_codeTable.isEmpty()) return "";
		boolean bFirst = true;
		CodePair codePair;
		StringBuffer buf = new StringBuffer();
		buf.append (TAB+"public "+className+" (");
		Iterator<CodePair> iter = m_codeTable.getItems();
		while (iter.hasNext()) {	// declare the member variables
			codePair = (CodePair) iter.next();
			if (! bFirst) buf.append (", ");
			bFirst = false;
			buf.append (codePair.getType()+" "+codePair.getName());
		}
		buf.append (") {"+NL);

		iter = m_codeTable.getItems();
		while (iter.hasNext()) {			// copy params to member variables
			codePair = (CodePair) iter.next();
			buf.append (TAB+TAB+"this."+codePair.getName()+" = "+codePair.getName()+";"+NL);
		}

		if (hash) {
			bFirst = true;
			buf.append (TAB+TAB+"hashCode = (");
			iter = m_codeTable.getItems();
			while (iter.hasNext()) {			// make hashcode
				codePair = (CodePair) iter.next();
				if (! bFirst) buf.append (" + " + QUOTE+ ";" + QUOTE + " + ");
				if (codePair.getType().equalsIgnoreCase("string"))
					buf.append (codePair.getName());
				else if (codePair.getType().equalsIgnoreCase("int"))
					buf.append ("Integer.toString("+codePair.getName()+")");
				else if (codePair.getType().equalsIgnoreCase("long"))
					buf.append ("Long.toString("+codePair.getName()+")");
				else if (codePair.getType().equalsIgnoreCase("float"))
					buf.append ("Float.toString("+codePair.getName()+")");
				else if (codePair.getType().equalsIgnoreCase("double"))
					buf.append ("Double.toString("+codePair.getName()+")");
				else
					buf.append (codePair.getType()+".toString("+codePair.getName()+")");
				bFirst = false;
			}
			buf.append (").hashCode();"+NL);
		}

		buf.append (TAB+"}"+NL);
		return buf.toString();
	}

	public String makeGetters() {
		if (m_codeTable.isEmpty()) return "";
		CodePair codePair;
		StringBuffer buf = new StringBuffer();
		Iterator<CodePair> iter = m_codeTable.getItems();
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			buf.append (TAB+"public "+codePair.getType()+" ");
			buf.append (methodGetter(codePair));
			buf.append (" {return "+codePair.getName()+";}"+NL);
		}
		return buf.toString();
	}
	public String makeSetters() {
		if (m_codeTable.isEmpty()) return "";
		CodePair codePair;
		StringBuffer buf = new StringBuffer();
		Iterator<CodePair> iter = m_codeTable.getItems();
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			buf.append (TAB+"public void set");
			buf.append (methodName(codePair.getName()));
			buf.append (" ("+codePair.getType()+" "+codePair.getName()+") ");
			buf.append ("{this."+codePair.getName()+" = ");
			buf.append (codePair.getName()+";}"+NL);
		}
		return buf.toString();
	}

	public String makeToString() {
		if (m_codeTable.isEmpty()) return "";
		CodePair codePair;
		StringBuffer buf = new StringBuffer();
		boolean bFirst = true;
		buf.append (TAB+"public String toString() {"+NL);
		buf.append (TAB+TAB+"return ");
		if (! getCodeTable().isEmpty()) {
			buf.append (addQuotes("("));
			Iterator<CodePair> iter = m_codeTable.getItems();
			while (iter.hasNext()) {		// declare the member variables
				codePair = (CodePair) iter.next();
 				if (! bFirst) buf.append ("+"+addQuotes(","));
				bFirst = false;
				buf.append ("+"+methodGetter(codePair));
			}
			buf.append ("+"+addQuotes(")"));
		}
		buf.append (";"+NL);
		buf.append (TAB+"}"+NL);
		return buf.toString();
	}
	public String makeHttpInfo(String name) {
		StringBuffer buf = new StringBuffer();
		buf.append(TAB+"public static final String SESSION_ID = ");
		buf.append(name+".class.getPackage() + "+name+".class.getName();"+NL+NL);
		buf.append(TAB+"private "+name+"(){}"+NL);
		buf.append(TAB+"public static "+name+" getInstance (HttpSession session) {"+NL);
		buf.append(TAB+TAB+name+" httpInfo = ("+name+") ");
		buf.append("session.getAttribute(SESSION_ID);"+NL);
		buf.append(TAB+TAB+"if (httpInfo == null) {"+NL);
		buf.append(TAB+TAB+TAB+"httpInfo = new "+name+"();"+NL);
		buf.append(TAB+TAB+TAB+"session.setAttribute (SESSION_ID, httpInfo);"+NL);
		buf.append(TAB+TAB+"}"+NL);
		buf.append(TAB+TAB+"httpInfo.session = session;"+NL);
		buf.append(TAB+TAB+"return httpInfo;"+NL);
		buf.append(TAB+"}"+NL);
		buf.append(TAB+"public static void clearSession (HttpSession session) throws IllegalArgumentException {"+NL);
		buf.append(TAB+TAB+"if (session == null)"+NL);
		buf.append(TAB+TAB+TAB+"throw new IllegalArgumentException ("+QUOTE+"Session was null"+QUOTE+");"+NL);
//		buf.append(TAB+TAB+"else"+NL);
		buf.append(TAB+TAB+"session.removeAttribute (SESSION_ID);"+NL);
		buf.append(TAB+"}"+NL);
		return buf.toString();
	}
}
/*
	public String makeMembersInit() {
		StringBuffer buf = new StringBuffer();
		CodePair codePair;
		Iterator iter = m_codeTable.getItems();
		while (iter.hasNext()) {
			codePair = (CodePair) iter.next();
			buf.append (TAB+"private "+codePair.getType()+" "+codePair.getName());
			buf.append (" = "+addQuotes("")+";"+NL);
		}
		return buf.toString();
	}
	public String makeBoolean() {
	if (m_codeTable.isEmpty()) return "";
	CodePair codePair;
	StringBuffer buf = new StringBuffer();
	Iterator iter = m_codeTable.getItems();
	while (iter.hasNext()) {
		codePair = (CodePair) iter.next();
		if ("boolean".equals(codePair.getType())) {
//			codePair = (CodePair) iter.next();
			buf.append (TAB+"public "+codePair.getType());
			buf.append (" is"+methodName(codePair.getName())+"() ");
			buf.append ("{return "+codePair.getName());
			buf.append (" = true;}"+NL);
		}
	}
	return buf.toString();
}
*/
