package com.idc.http;

import java.io.IOException;

/**
 * @author John Vincent
 *
 */

public class Browser {

	public static void start (String[] cmd) throws AppException {
		try {
//			Process process = Runtime.getRuntime().exec(cmd);
			Runtime.getRuntime().exec(cmd);
		}
		catch (IOException ioex) {
			throw new AppException ("Cannot start process "+ioex.getMessage());
		}
	}
}
