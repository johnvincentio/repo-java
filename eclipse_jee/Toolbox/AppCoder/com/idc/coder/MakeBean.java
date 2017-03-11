
package com.idc.coder;

/**
 * @author John Vincent
 *
 */

public class MakeBean extends MakeCode {
	public MakeBean (CodeTable codeTable) {
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
		append (makeMembers());
//		append (makeConstructor(getBeanClassName()));
		append (makeConstructorArgs(getBaseClassName()));
		append (makeGetters());
		appendNL();
		append (makeSetters());
		appendNL();
//		append (makeBoolean());
		append (makeToString());
		append (makeEndClass());
		return getBuffer();
	}
}
