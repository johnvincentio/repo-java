
package com.idc.coder;


/**
 * @author John Vincent
 *
 */

public class MakeCollectionSet extends MakeCode {
	public MakeCollectionSet (CodeTable codeTable) {
		super (codeTable);
	}

	public String makeCode() {
		appendNL();
		append (makePackage());
		appendNL();
		appendNL ("import java.io.Serializable;");
		appendNL ("import java.util.HashSet;");
		appendNL ("import java.util.Set;");
		appendNL ("import java.util.Iterator;");

		append (makeStartClass());

		appendNL (TAB+"private Set<"+getCollectionItemClassName()+"> m_collection = new HashSet<"+getCollectionItemClassName()+">();");
		append (makeMembers());
		appendNL();

		append (makeConstructorArgs(getBaseClassName()));
		appendNL();

		appendNL (TAB+"public Iterator getItems() {return m_collection.iterator();}");
		appendNL (TAB+"public void add ("+getCollectionItemClassName()+" item) {if (item != null) m_collection.add (item);}");
		appendNL (TAB+"public void add ("+getCollectionClassName()+" info) {if (info != null) m_collection.addAll (info.getCollection());}");
		appendNL (TAB+"public int getSize() {return m_collection.size();}");
		appendNL (TAB+"public boolean isNone() {return getSize() < 1;}");
		appendNL();
		append (makeGetters());
		append (makeSetters());

		appendNL (TAB+"protected Set<"+getCollectionItemClassName()+"> getCollection() {return m_collection;}");
		appendNL (TAB+"public boolean isExists ("+getCollectionItemClassName()+" item) {return m_collection.contains (item);}");

		appendNL();
		appendNL (TAB+"public String toString() {");
		appendNL (TAB+TAB+"StringBuffer buf = new StringBuffer();");
		appendNL (TAB+TAB+"buf.append (" + QUOTE+ "(" + QUOTE + ");");
		appendNL (TAB+TAB+"Iterator iter = m_collection.iterator();");
		appendNL (TAB+TAB+"while (iter.hasNext()) {");
		appendNL (TAB+TAB+TAB+getCollectionItemClassName()+" item = ("+getCollectionItemClassName()+") iter.next();");
		appendNL (TAB+TAB+TAB+"if (item != null) buf.append (item);");
		appendNL (TAB+TAB+"}");
		appendNL (TAB+TAB+"buf.append ("+QUOTE+")"+QUOTE+");");
		appendNL (TAB+TAB+"return buf.toString();");
		appendNL (TAB+"}");		

		append (makeEndClass());
		return getBuffer();
	}
}
