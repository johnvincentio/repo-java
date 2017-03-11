
package com.idc.http.utils;

import java.util.ArrayList;
import javax.servlet.http.HttpSession;

public class FormErrorsHttpInfo extends HTTPInfo {
	private static final long serialVersionUID = 1;
    public static final String SESSION_NAME = FormErrorsHttpInfo.class.toString();
    public static FormErrorsHttpInfo getInstance(HttpSession session) {
    	FormErrorsHttpInfo httpInfo = (FormErrorsHttpInfo) session.getAttribute(SESSION_NAME);
        if (httpInfo == null) {
            httpInfo = new FormErrorsHttpInfo();
            session.setAttribute(SESSION_NAME, httpInfo);
        }
        httpInfo.session = session;
        return httpInfo;
    }
    public static void clearSession(HttpSession session) throws IllegalArgumentException {
        if (session == null)
            throw new IllegalArgumentException("Session was null");
        else
            session.removeAttribute(SESSION_NAME);
    }

    private ArrayList<String> errors = new ArrayList<String>();
    public ArrayList<String> getErrors() {return errors;}
    public boolean isErrors() {return errors.isEmpty();}
    public void addError (String errorCode, String dialect) {errors.add(errorCode);}
}
