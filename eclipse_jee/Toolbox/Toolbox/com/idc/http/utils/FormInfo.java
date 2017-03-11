
package com.idc.http.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

abstract public class FormInfo {
	String dialect;
	FormErrorsHttpInfo errorInfo;
	
	public FormInfo() {
		Method[] methods = getClass().getMethods();
		Method method;
		Class<?>[] parameters;
		Class<?>[] scalar = {String.class};
//		Class[] array = {String[].class};
		String blankScalar = new String();
		String[] blankArray = {blankScalar};
		Object[] scalarInit = {blankScalar};
		Object[] arrayInit = {blankArray};
		
		for (int i=0; i<methods.length; i++) {
			method = methods[i];
			if (method.getName().startsWith("set")) {
				parameters = method.getParameterTypes();
				if (parameters.length == 1) {
					if (parameters[0].isInstance(blankScalar))
						invokeMethod(method, scalar, scalarInit);
					else if (parameters[0].isInstance(blankArray))
						invokeMethod(method, scalar, arrayInit);
				}
			}
		}
	}
	protected abstract void doValidation();
	
	public boolean validate (HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		FormErrorsHttpInfo.clearSession(session);
		errorInfo = FormErrorsHttpInfo.getInstance(session);
		populate (request);
		doValidation();
		return ! errorInfo.isErrors();
	}
	public void addError (String errorCode) {errorInfo.addError (errorCode, "enUS");}
	public boolean isErrors() {
		if (errorInfo != null) return errorInfo.isErrors();
		return false;
	}
	@SuppressWarnings("unchecked")
	private final void populate (HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		Iterator<Map.Entry<String, String[]>> parameters = params.entrySet().iterator();
		Class<?>[] scalar = {String.class};
		Class<?>[] array = {String[].class};
		while (parameters.hasNext()) {
			Map.Entry<String, String[]> entry = parameters.next();
			String key = (String) entry.getKey();
			String[] values = (String[]) entry.getValue();
			String setter = "set" + key.substring(0,1).toUpperCase() + key.substring(1,key.length());
			if (! invokeMethod (setter, scalar, values))
				invokeMethod (setter, array, new Object[]{values});
		}
	}
	private boolean invokeMethod (String setter, Class<?>[] signature, Object[] parameters) {
		try {
			return invokeMethod (getClass().getMethod(setter, signature), signature, parameters);
		}
		catch (NoSuchMethodException e) {
			System.out.println(getClass().toString() + " invalid property: "+setter);
			return false;
		}
	}
	private boolean invokeMethod (Method method, Class<?>[] signature, Object[] parameters) {
		try {
			method.invoke(this, parameters);
			System.out.println (getClass().toString() + " set property: "+method.getName());
			return true;
		}
		catch (Exception e) {
			System.out.println(getClass().toString() + " error: "+method.getName() + " " + e.getMessage());
			return false;
		}
	}
	
	
}
