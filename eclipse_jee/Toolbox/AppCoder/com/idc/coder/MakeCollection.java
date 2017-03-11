
package com.idc.coder;

import java.util.Iterator;

/**
 * @author John Vincent
 *
 */

public class MakeCollection extends MakeCode {
	public MakeCollection (CodeTable codeTable) {
		super (codeTable);
	}

	public String makeCode() {
		appendNL();
		append (makePackage());
		appendNL();
		appendNL ("import java.util.ArrayList;");
		appendNL ("import java.util.Iterator;");
		appendNL ("import java.io.Serializable;");
		appendNL();
		append (makeStartClass());

		appendNL (TAB+"private ArrayList<"+getCollectionItemClassName()+"> m_collection = new ArrayList<"+getCollectionItemClassName()+">();");
		append (makeMembers());
		appendNL();

		append (makeConstructorArgs(getBaseClassName()));
		appendNL();

		appendNL (TAB+"public Iterator getItems() {return m_collection.iterator();}");
		appendNL (TAB+"public void add ("+getCollectionItemClassName()+" item) {if (item != null) m_collection.add(item);}");
		appendNL (TAB+"public int getSize() {return m_collection.size();}");
		appendNL (TAB+"public boolean isNone() {return getSize() < 1;}");
		appendNL();
		append (makeGetters());
		append (makeSetters());

		CodePair codePair;
		boolean bFirst = true;
		appendNL();
		appendNL (TAB+"public String toString() {");
		appendNL (TAB+TAB+"StringBuffer buf = new StringBuffer();");
		appendNL (TAB+TAB+"for (int i = 0; i < m_collection.size(); i++)");
		appendNL (TAB+TAB+TAB+"buf.append((("+getCollectionItemClassName()+") m_collection.get(i)).toString());");
		append (TAB+TAB+"return ");
		if (! getCodeTable().isEmpty()) {
			append (QUOTE+"("+QUOTE);
			Iterator<CodePair> iter = getCodeTable().getItems();
			while (iter.hasNext()) {			// declare the member variables
				codePair = (CodePair) iter.next();
				if (! bFirst) append ("+"+QUOTE+","+QUOTE);
				bFirst = false;
				append ("+"+methodGetter(codePair));
			}
			append ("+"+QUOTE+"),"+QUOTE+"+");
		}
		append (QUOTE+"("+QUOTE+"+");
		append ("buf.toString()");
		append ("+"+QUOTE+")"+QUOTE);
		appendNL (";");
		appendNL (TAB+"}");

		append (makeEndClass());
		return getBuffer();
	}
}
