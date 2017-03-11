
package com.idc.coder;

import java.io.File;
import java.util.Iterator;

/**
 * @author John Vincent
 *
 */

public class MakeJsp extends MakeCode {
	public MakeJsp (CodeTable codeTable) {
		super (codeTable);
	}

	public String makeCode() {
		CodePair codePair;
		appendNL();
		String strFile = "JSPHeader";
		Template template = new Template(new File(strFile));
		template.process();
		appendNL (template.getTemplate());

		append ("<%@ page "+addKeyPair("language","java"));
		append (" "+addKeyPair("contentType","text/html; charset=utf-8"));
		appendNL (" "+addKeyPair("pageEncoding","utf-8")+" %>");
		appendNL ("<%@ taglib "+addKeyPair("uri","/WEB-INF/c.tld")+" "+addKeyPair("prefix","c")+" %>");
		appendNL ("<%@ taglib "+addKeyPair("uri","com.hertz.hercutil.presentation.tags")+" "+addKeyPair("prefix","h")+" %>");
		appendNL ("<%@ page "+addKeyPair("import","com.hertz.herc.framework.page.MyPageBroker")+" %>");
		appendNL ("<%@ page "+addKeyPair("import","com.hertz.herc.framework.MyBroker")+" %>");
		appendNL();

		append ("<c:set "+addKeyPair("var","info"));
		append (" "+addKeyPair("value","<%= MyPageBroker.getMethodHttpPageInfo(request, true) %>"));
		appendNL (" />");
		append ("<c:set "+addKeyPair("var","info"));
		append (" "+addKeyPair("value","<%= MyBroker.getMethodInfo(request) %>"));
		appendNL (" />");
		appendNL();
		appendNL ("<% com.hertz.hercutil.framework.AppBroker.setCurrentURL(request); %>");
		appendNL();
		appendNL ("<h2>CHANGE_THIS</h2>");
		appendNL();
		appendNL ("<div>");
		appendNL (TAB+"<jsp:include page="+addQuotes("/helpers/enumerateErrorsView.jsp")+" />");
		appendNL ("</div>");
		appendNL();
		append("<br/><a "+addKeyPair("href","<h:url "+addKeyPair("target","nextView.jsp")+" />"));
		appendNL (">Do something</a><br/><br/>");
		appendNL();

		appendNL ("<c:url "+addKeyPair("var","nextURL")+" "+addKeyPair("value","/section/index.jsp"));
		appendNL (TAB+"<c:param "+addKeyPair("name","targetPage")+" "+addKeyPair("value","nextView.jsp"));
		appendNL ("</c:url>");
		append("<br/><a "+addKeyPair("href","${nextURL}"));
		appendNL (">Do something else</a><br/><br/>");
		appendNL();

		appendNL ("<div>");
		appendNL (TAB+"<b>Section - CHANGE THIS:</b><br/>");

		appendNL (TAB+"<form "+addKeyPair("method","post")+
				" "+addKeyPair("action","<c:url "+addKeyPair("value","/handlers/MyHandler")+" />")+">");
		append (TAB+TAB);
		appendNL ("<input "+addKeyPair("name","action")+" "+addKeyPair("value","create")+" "+addKeyPair("type","hidden")+" />");
		appendNL();
		appendNL (TAB+TAB+"<table "+addKeyPair("cellpadding","0")+" "+addKeyPair("cellspacing","0")+" "+addKeyPair("border","0")+">");
		appendNL (TAB+TAB+TAB+"<tr>");
		if (! getCodeTable().isEmpty()) {
			Iterator<CodePair> iter = getCodeTable().getItems();
			while (iter.hasNext()) {
				codePair = (CodePair) iter.next();
				appendNL (TAB+TAB+TAB+TAB+"<td>"+codePair.getName()+"</td>");
			}
		}		
		appendNL (TAB+TAB+TAB+"</tr>");

		appendNL (TAB+TAB+TAB+"<tr>");
		if (! getCodeTable().isEmpty()) {
			Iterator<CodePair> iter = getCodeTable().getItems();
			while (iter.hasNext()) {
				codePair = (CodePair) iter.next();
				appendNL (TAB+TAB+TAB+TAB+"<td>");
				append (TAB+TAB+TAB+TAB+TAB);
				append ("<input "+addKeyPair("name",codePair.getName()));
				append (" "+addKeyPair("type","text"));
				appendNL (" "+addKeyPair("value","${pageInfo."+codePair.getName()+"}")+" />");
				appendNL (TAB+TAB+TAB+TAB+"</td>");
			}
		}
		appendNL (TAB+TAB+TAB+"</tr>");
		appendNL (TAB+TAB+"</table>");
		appendNL (TAB+TAB+"<input "+addKeyPair("type","submit")+
				" "+addKeyPair("name","key")+
				" "+addKeyPair("value","CHANGE_BUTTON_LABEL")+" />");
		appendNL (TAB+"</form>");
		appendNL ("</div>");
		return getBuffer();
	}
}
