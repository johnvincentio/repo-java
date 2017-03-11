
package com.idc.appcoder.template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import com.idc.appcoder.coder.MakeCode;

/**
 * @author John Vincent
 *
 */

public class Template {
	private static final String DELIM = "`";
	private static final String COMMA = ",";

	public static String process (MakeCode makeCode, final String str) {
		return process (makeCode, new File (str));
	}
	public static String process (MakeCode makeCode, final File file) {
		StringBuffer new_buf = new StringBuffer();
		StringBuffer orig_buf = read (file);

		Pattern pattern = Pattern.compile (DELIM);
		String[] splitStrings = pattern.split (orig_buf);
		boolean bCode = true;
		for (int num = 0; num < splitStrings.length; num++) {
			if (bCode)
				new_buf.append(splitStrings[num]);
			else {
				new_buf.append (handleMethod(makeCode, splitStrings[num]));
			}
			bCode = ! bCode;
		}
		return new_buf.toString();
	}

	private static String handleMethod (MakeCode makeCode, String keyName) {
		System.out.println(">>> handleMethod; keyName :"+keyName+":");
		int s1 = keyName.indexOf('(');
		int e1 = keyName.lastIndexOf(')');
		if (s1 < 0 || e1 < 0) return keyName;		// not a method

		if (e1 - s1 <= 1) return invokeMethod (makeCode, keyName);		// no parameters

		String method = keyName.substring(0, s1).trim();				// method name
		System.out.println("method :"+method+": s1 "+s1+" e1 "+e1);

		String strs = keyName.substring(s1 + 1, e1).trim();					// parameter(s)
		System.out.println("strs :"+strs+":");
		Pattern pattern = Pattern.compile (COMMA);		// looking for parameters
		String[] splitStrings = pattern.split (strs);
		System.out.println("splitStrings.length "+splitStrings.length);

		Object[] sArr = new Object[splitStrings.length];
		for (int num = 0; num < splitStrings.length; num++) {
			System.out.println(" num "+num+" splitStrings[num] :"+splitStrings[num]+":");
			sArr[num] = handleMethod (makeCode, splitStrings[num].trim());
			if ("true".equals((String) sArr[num]) || "false".equals((String) sArr[num]))
				sArr[num] = new Boolean ((String) sArr[num]);
		}
		return invokeMethod (makeCode, method, sArr);
	}

	private static String invokeMethod (MakeCode makeCode, String name) {
		return invokeMethod (makeCode, name, new Object[0]);
	}
	private static String invokeMethod (MakeCode makeCode, String name, Object[] parameters) {
		System.out.println(">>> invokeMethod; methodName :"+name+":");
		Class<?>[] types;
		if (parameters.length > 0) {
			types = new Class[parameters.length];
			for (int num = 0; num < parameters.length; num++) {
				if (parameters[num] instanceof Boolean)
					types[num] = Boolean.class;
				else
					types[num] = String.class;
				System.out.println("parameter; num "+num+" value :"+parameters[num]+": type "+types[num]);
			}
		}
		else {
			types = new Class[] {};
		}
		String methodName = name;
		int e1 = methodName.indexOf('(');
		if (e1 > -1) methodName = name.substring(0, e1);
		System.out.println("methodName :"+methodName+":");

		try {
			Method method = makeCode.getClass().getMethod (methodName, types);
			Object result = method.invoke (makeCode, parameters);
			return (String) result;
		}
		catch (Exception ex) {
			System.out.println("exception; "+ex.getMessage());
			return methodName;
		}
	}

	private static StringBuffer read (final File file) {
		StringBuffer buf = new StringBuffer();
		BufferedReader bufferedReader = null;
		String line;
		try {
			bufferedReader = new BufferedReader (new FileReader(file));
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("--")) continue;
				buf.append(line+"\n");
			}
			return buf;
		}
		catch (IOException exception) {
			System.out.println("Exception "+exception.getMessage());
			System.out.println("Trouble reading file "+file.getPath());
			return null;
		}
		finally {
			try {
				if (bufferedReader != null) bufferedReader.close();
			}
			catch (IOException exception2) {
				System.out.println("Exception "+exception2.getMessage());
				System.out.println("Trouble closing file "+file.getPath());
				exception2.printStackTrace();
			}
		}		
	}
}
