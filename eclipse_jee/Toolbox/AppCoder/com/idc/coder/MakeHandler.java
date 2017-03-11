
package com.idc.coder;

import java.util.Iterator;

/**
 * @author John Vincent
 *
 */

public class MakeHandler extends MakeCode {
	public MakeHandler (CodeTable codeTable) {
		super (codeTable);
	}

	public String makeCode() {
		CodePair codePair;
		String name = getBaseClassName()+"Handler";
		appendNL();
		append (makePackage());
		appendNL();
		appendNL ("import java.io.IOException;");
		appendNL ("import javax.servlet.Servlet;");
		appendNL ("import javax.servlet.ServletException;");
		appendNL ("import javax.servlet.http.HttpServlet;");
		appendNL ("import javax.servlet.http.HttpServletRequest;");
		appendNL ("import javax.servlet.http.HttpServletResponse;");
		appendNL();
		appendNL ("import com.hertz.herc.framework.UserBroker;");
		appendNL ("import com.hertz.hercutil.framework.LogBroker;");
		appendNL ("import com.hertz.hercwebutil.framework.AppBroker;");
		appendNL ("import com.hertz.hercwebutil.framework.ErrorPageBroker;");
		appendNL ("import com.hertz.hercutil.presentation.UtilHelper;");
		appendNL ("import com.hertz.hercwebutil.presentation.URLConstants;");
		appendNL ("import com.hertz.hercwebutil.presentation.URLHelper;");
		appendNL ("import com.hertz.irac.framework.HertzSystemException;");
		appendNL();
		appendNL ("import com.hertz.hercwebutil.presentation.JVRequest;");
		appendNL ("import "+getPackageName()+"."+getHttpPageInfoClassName()+";");
		appendNL ("import "+getPackageName()+"."+getFormInfoClassName()+";");
		appendNL();
		appendNL ("public class "+name+" extends HttpServlet implements Servlet {");

		appendNL (TAB+"protected void doGet(HttpServletRequest request, HttpServletResponse response)");
		appendNL (TAB+TAB+TAB+TAB+"throws ServletException, IOException {");
		appendNL (TAB+TAB+"ErrorPageBroker.goError(this, request, response, \"doGet used in \" + getServletName());");
		appendNL (TAB+"}");

		appendNL (TAB+"protected void doPost(HttpServletRequest request, HttpServletResponse response)");
		appendNL (TAB+TAB+TAB+TAB+"throws ServletException, IOException {");
		appendNL (TAB+TAB+"LogBroker.debug(this, \">>> \" + getServletName() + \"::doPost\");");
		appendNL (TAB+TAB+"try {");
		appendNL (TAB+TAB+TAB+"doWork (request, response);");
		appendNL (TAB+TAB+TAB+"LogBroker.debug(this, \"<<< \" + getServletName() + \"::doPost\");");
		appendNL (TAB+TAB+"}");
		appendNL (TAB+TAB+"catch (HertzSystemException ex) {");
		appendNL (TAB+TAB+TAB+"ErrorPageBroker.goError(this, request, response, ex);");
		appendNL (TAB+TAB+"}");
		appendNL (TAB+"}");

		appendNL (TAB+"private void doWork (HttpServletRequest request, HttpServletResponse response)");
		appendNL (TAB+TAB+TAB+TAB+"throws HertzSystemException, ServletException, IOException {");
		appendNL (TAB+TAB+"JVRequest.getInstance().showAll(request,"+addQuotes(name)+");");
		appendNL ("//"+TAB+TAB+"String action = request.getParameter("+addQuotes("action")+");");
		appendNL ("//"+TAB+TAB+"System.out.println("+addQuotes("action :")+"+action);");

		appendNL (TAB+TAB+"boolean bError = true;");
		appendNL (TAB+TAB+"boolean bValidated = false;");
		appendNL (TAB+TAB+getHttpPageInfoClassName()+" httpPageInfo = "+getHttpPageInfoClassName()+".getInstance(request.getSession());");
		appendNL (TAB+TAB+getFormInfoClassName()+" formInfo = new "+getFormInfoClassName()+"();");
		appendNL();

		appendNL("//"+TAB+TAB+"if (UtilHelper.isSet(formInfo.getApprove())) bApproved = true;");
		appendNL ("//"+TAB+TAB+"if ("+addQuotes("create")+".equals(action)) {");
		appendNL (TAB+TAB+"bValidated = formInfo.validate(request);");
		appendNL (TAB+TAB+"if (bValidated) bError = false;");
		appendNL (TAB+TAB+"if (! bError) {");
		appendNL ("/*");
		if (! getCodeTable().isEmpty()) {
			Iterator<CodePair> iter = getCodeTable().getItems();
			while (iter.hasNext()) {
				codePair = (CodePair) iter.next();
				appendNL ("formInfo."+methodGetter(codePair)+";");
			}
		}
		appendNL (TAB+TAB+TAB+"if (.... more validation ... ) {");
		appendNL (TAB+TAB+TAB+TAB+"formInfo.addError ("+addQuotes("There is an error somewhere!")+");");
		appendNL (TAB+TAB+TAB+"}");
		appendNL (TAB+TAB+TAB+"else {");
		appendNL (TAB+TAB+TAB+TAB+"all is ok...maybe add a record!");
		appendNL (TAB+TAB+TAB+TAB+"bError = false;");
		appendNL (TAB+TAB+TAB+"}");
		appendNL ("*/");
		appendNL (TAB+TAB+"}");

		appendNL (TAB+TAB+"String strURL;");
		appendNL (TAB+TAB+"if (formInfo.isErrors()) bError = true;");
		appendNL (TAB+TAB+"if (bError) {");
		appendNL (TAB+TAB+TAB+"httpPageInfo.copyFormInfo(formInfo);");
		append (TAB+TAB+TAB+"strURL = URLHelper.buildIndexPath (request, URLConstants.PATH_RENTALS,");
		appendNL (addQuotes("myErrorPageView.jsp")+");");
		appendNL (TAB+TAB+"} else {");
		append (TAB+TAB+TAB+"strURL = URLHelper.buildIndexPath (request, URLConstants.PATH_RENTALS,");
		appendNL (addQuotes("myNextPageView.jsp")+");");
		appendNL (TAB+TAB+TAB+getHttpPageInfoClassName()+".clearSession(request.getSession());");
		appendNL (TAB+TAB+"}");
		appendNL (TAB+TAB+"response.sendRedirect (strURL);");
		appendNL (TAB+"}");
		append (makeEndClass());
		appendNL();
		return getBuffer();
	}
}
/*
public class CreateCompany {
    
	private String companyName1;
	private String companyName2;
	private String companyDescription;
	private String annualVolume;
	private String ytdVolume;
	private String potentialVolume;
	private String existingCompany;
	
	private String create;
	private String existing;
*/
