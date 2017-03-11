
package com.idc.coder;

/**
 * @author John Vincent
 *
 */

public class MakeBeanSet extends MakeCode {
	public MakeBeanSet (CodeTable codeTable) {
		super (codeTable);
	}

	public String makeCode() {
		appendNL();
		append (makePackage());
		appendNL();
		append ("import java.io.Serializable;");
		appendNL();
		appendNL();
		append (makeStartClass());
		append (makeMembers(true));
//		append (makeConstructor(getBeanClassName()));
		append (makeConstructorArgs(getBaseClassName(), true));
		append (makeGetters());
		appendNL();
		append (makeSetters());
		appendNL();
		appendNL (TAB+"public int hashCode() {return hashCode;}");
		appendNL (TAB+"public boolean equals (Object obj) {");
		appendNL (TAB+TAB+"if (obj == null || ! (obj instanceof "+getCollectionItemClassName()+")) return false;");
		appendNL (TAB+TAB+"if (this.hashCode != obj.hashCode()) return false;");
		appendNL (TAB+TAB+"return true;");
		appendNL (TAB+"}");

//		append (makeBoolean());
		append (makeToString());
		append (makeEndClass());
		return getBuffer();
	}
}
