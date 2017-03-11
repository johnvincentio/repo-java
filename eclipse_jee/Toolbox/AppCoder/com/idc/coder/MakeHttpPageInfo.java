
package com.idc.coder;

/**
 * @author John Vincent
 *
 */

public class MakeHttpPageInfo extends MakeCode {
	public MakeHttpPageInfo (CodeTable codeTable) {
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
		appendNL ("public class "+getHttpPageInfoClassName()+" extends HTTPInfo implements Serializable {");
		append (makeHttpInfo(getHttpPageInfoClassName()));
		appendNL();
		appendNL(TAB+"private boolean initialLoad = true;");
		appendNL(TAB+"public boolean isInitialLoad() {return initialLoad;}");
		appendNL(TAB+"private boolean error = false;");
		appendNL(TAB+"public boolean isError() {return error;}");
		appendNL(TAB+"public void setError (boolean error) {this.error = error;}");
		appendNL();
		append (makeMembers());
		appendNL();
		append (makeGetters());
		appendNL();
		append (makeSetters());
		appendNL();
		appendNL (TAB+"public void copyFormInfo ("+getFormInfoClassName()+" formInfo)	{");
		appendNL (TAB+TAB+"formInfo.copyTo(this);");
		appendNL (TAB+TAB+"initialLoad = false;");
		appendNL (TAB+"}");
		appendNL();
		append (makeToString());
		append (makeEndClass());
		return getBuffer();
	}
}
