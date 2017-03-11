package com.idc.coder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.idc.coder.model.ClazzItemInfo;
import com.idc.file.JVFile;

public class Utils {
	public static ClazzItemInfo loadClass (String project, String fullname) {
		File file = new File (project);
		try {
			URL url = file.toURL();					// Convert File to a URL
			URL[] urls = new URL[] {url};
			ClassLoader cl = new URLClassLoader(urls);		// Create a new class loader with the directory
			Class<?> clazz = cl.loadClass (fullname);
			return new ClazzItemInfo (clazz);
		} catch (MalformedURLException e) {
			System.err.println("MalformedURLException; "+e.getMessage());
			return null;
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException; "+e.getMessage());
			return null;
		}
	}

	public static File createDirectories (String baseDir) {
		File myDir = JVFile.makeWorkingDirectory (new File (baseDir));
		System.out.println("workingDirectory :"+myDir.getPath()+":");
		JVFile.makeFullDirectories (myDir);
		return myDir;
	}
}
