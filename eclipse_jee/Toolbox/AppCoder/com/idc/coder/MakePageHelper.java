
package com.idc.coder;

/**
 * @author John Vincent
 *
 */

public class MakePageHelper extends MakeCode {
	public MakePageHelper (CodeTable codeTable) {
		super (codeTable);
	}

	public String makeCode() {
		appendNL();
		append (TAB+"public static "+getHttpPageInfoClassName()+" get" + getHttpPageInfoClassName());
		appendNL (" (HttpServletRequest request, boolean clearSession) {");
		appendNL (TAB+TAB+getHttpPageInfoClassName()+" pageInfo = "+getHttpPageInfoClassName()+".getInstance(request.getSession());");
		appendNL (TAB+TAB+"if (clearSession)");
		appendNL (TAB+TAB+TAB+getHttpPageInfoClassName()+".clearSession(request.getSession());");
		appendNL (TAB+TAB+"if (pageInfo.isInitialLoad()) {");
		appendNL (TAB+TAB+"}");
		appendNL (TAB+TAB+"return pageInfo;");
		appendNL (TAB+"}");
		return getBuffer();
	}
}
