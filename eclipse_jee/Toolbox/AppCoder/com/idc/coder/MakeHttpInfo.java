
package com.idc.coder;

/**
 * @author John Vincent
 *
 */

public class MakeHttpInfo extends MakeCode {
	public MakeHttpInfo (CodeTable codeTable) {
		super (codeTable);
	}

	public String makeCode() {
		appendNL();
		append (makePackage());
		appendNL();
		appendNL ("import java.io.Serializable;");
		appendNL ("import javax.servlet.http.HttpSession;");
		appendNL();
		appendNL ("import com.hertz.irac.framework.presentation.HTTPInfo;");
		appendNL();
		appendNL ("public class "+getHttpInfoClassName()+" extends HTTPInfo implements Serializable {");
		append (makeHttpInfo(getHttpInfoClassName()));
		appendNL();
		append (makeMembers());
		appendNL();
		append (makeGetters());
		appendNL();
		append (makeSetters());
		appendNL();
		append (makeToString());
		append (makeEndClass());
		return getBuffer();
	}
}
