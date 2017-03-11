package com.idc.refs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.idc.file.JVFile;
import com.idc.refs.data.ClasspathInfo;
import com.idc.refs.data.ClasspathItemInfo;
import com.idc.refs.data.ManifestmfInfo;
import com.idc.refs.data.ManifestmfItemInfo;
import com.idc.refs.data.ModulemapsInfo;
import com.idc.refs.data.ModulemapsItemInfo;
import com.idc.refs.data.ProjectInfo;
import com.idc.refs.data.ProjectItemInfo;

public class Utils {

	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_EAR = 1;
	public static final int TYPE_EJB = 2;
	public static final int TYPE_EJBCLIENT = 3;
	public static final int TYPE_WEB = 4;
	public static final int TYPE_UTIL = 5;
	
	/**
	 * Determine the type of the project.
	 * 
	 * @param dir		Project directory
	 * @return			project type.
	 */
	public static int determineType (File dir) {
//		System.out.println(">>> Utils.determineType; dir :"+dir.getPath()+":");

		boolean bWeb = isDirExists (dir, "WebContent");
//		System.out.println("bWeb "+bWeb);
		if (bWeb) return TYPE_WEB;

		boolean bBaseEjbModule = isDirExists (dir, "ejbModule");
//		System.out.println("bBaseEjbModule "+bBaseEjbModule);
		if (bBaseEjbModule) {
			boolean bEjbJar = isExists (dir, "ejbModule", "META-INF", "ejb-jar.xml");
//			System.out.println("bEjbJar "+bEjbJar);
			if (bEjbJar) return TYPE_EJB;
			return TYPE_EJBCLIENT;
		}

		boolean bProject = isExists (dir, ".project");
		boolean bClasspath = isExists (dir, ".classpath");
		boolean bBaseMetaInf = isDirExists (dir, "META-INF");
//		System.out.println("bProject "+bProject);
//		System.out.println("bClasspath "+bClasspath);
//		System.out.println("bBaseMetaInf "+bBaseMetaInf);
		if (bProject && ! bClasspath && bBaseMetaInf) return TYPE_EAR;
		if (bProject && bClasspath) return TYPE_UTIL;
		return TYPE_UNKNOWN;
	}

	private static boolean isExists (File dir, String name) {
		return makeFile (dir, name).exists();
	}
	private static boolean isExists (File dir, String dir1, String dir2, String name) {
		return makeFile (dir, dir1, dir2, name).exists();
	}
	private static boolean isDirExists (File dir, String name) {
		File file = makeFile (dir, name);
		if (! file.exists()) return false;
		return file.isDirectory();
	}

	public static File makeFile (File dir, String name) {
		return new File (dir.getPath() + File.separatorChar + name);
	}
	public static File makeFile (File dir, String dir1, String name) {
		return new File (dir.getPath() + File.separatorChar + dir1 + File.separatorChar + name);
	}
	public static File makeFile (File dir, String dir1, String dir2, String name) {
		return new File (dir.getPath() + File.separatorChar + dir1 + File.separatorChar + dir2 + File.separatorChar + name);
	}

	public static ProjectInfo getDotProject (File dir, String name) {
		String filename = dir.getPath() + File.separatorChar + name;
//		System.out.println(">>> Utils.getDotProject; file :"+filename+":");
		ProjectInfo projectInfo = new ProjectInfo();
		SAXBuilder builder = new SAXBuilder();
		Document document = null;

		try {
			document = builder.build (new FileInputStream (filename));
		} catch (FileNotFoundException e) {
//			System.out.println("Error; file not found "+e.getMessage());
			return projectInfo;
		} catch (JDOMException e) {
			System.out.println("Error; JDOMException "+e.getMessage());
			return projectInfo;
		} catch (IOException e) {
			System.out.println("Error; IOException "+e.getMessage());
			return projectInfo;
		}

		Element root = document.getRootElement();
		projectInfo.setName (root.getChildTextTrim ("name"));
		List<?> rows = root.getChild("projects").getChildren("project");
//		System.out.println("size "+rows.size());
		for (int i = 0; i < rows.size(); i++) {
			Element row = (Element) rows.get(i);
			projectInfo.add (new ProjectItemInfo (row.getTextTrim()));
		}

//		System.out.println("<<< Utils.getDotProject");
		return projectInfo;
	}

	public static ClasspathInfo getDotClasspath (File dir, String name) {
		String filename = dir.getPath() + File.separatorChar + name;
//		System.out.println(">>> Utils.getDotClasspath; file :"+filename+":");
		ClasspathInfo classpathInfo = new ClasspathInfo();
		SAXBuilder builder = new SAXBuilder();
		Document document = null;

		try {
			document = builder.build (new FileInputStream (filename));
		} catch (FileNotFoundException e) {
//			System.out.println("Error; file not found "+e.getMessage());
			return classpathInfo;
		} catch (JDOMException e) {
			System.out.println("Error; JDOMException "+e.getMessage());
			return classpathInfo;
		} catch (IOException e) {
			System.out.println("Error; IOException "+e.getMessage());
			return classpathInfo;
		}

		Element root = document.getRootElement();
		List<?> rows = root.getChildren("classpathentry");
//		System.out.println("size "+rows.size());
		for (int i = 0; i < rows.size(); i++) {
			Element row = (Element)rows.get(i);
			String path = row.getAttributeValue("path");
			if (path == null) continue;
			if (path.trim().length() < 1) continue;
//			System.out.println("path :"+path+":");
			String kind = row.getAttributeValue("kind");
//			System.out.println("kind :"+kind+":");
			boolean bSrc = (kind != null && kind.equalsIgnoreCase("src"));
			boolean bLib = (kind != null && kind.equalsIgnoreCase("lib"));
			if ((! bSrc) && (! bLib)) continue;
			if (bSrc) {
				if (path.equalsIgnoreCase("ejbmodule")) continue;
				if (path.equalsIgnoreCase("gen/src")) continue;
			}
			if (bSrc && path.startsWith("/")) path = path.substring(1);

			String exported = row.getAttributeValue("exported");
//			System.out.println("exported :"+exported+":");
			boolean bExported = (exported != null && exported.equalsIgnoreCase("true"));
			classpathInfo.add (new ClasspathItemInfo (path, bLib, bExported));
		}
//		System.out.println("<<< Utils.getDotClasspath");
		return classpathInfo;
	}

	public static ManifestmfInfo getManifestmf (File file) {
//		System.out.println(">>> Utils.getManifestmf; "+file.getPath());
		ManifestmfInfo manifestmfInfo = new ManifestmfInfo();
		StringBuffer buf = JVFile.readFile (file.getPath());
		if (buf != null) {
			int p1 = buf.indexOf("Class-Path:");
//			System.out.println("p1 "+p1);
			if (p1 > -1) {
				buf.delete(0, p1 + 11);
				String[] sa = buf.toString().split(" ");
				for (int i = 0; i < sa.length; i++) {
					String str = sa[i].trim();
					if (str.length() < 1) continue;
//					System.out.println("1 - str :"+str+":");
					if (str.endsWith(".jar")) str = str.substring(0, str.length() - 4);
//					System.out.println("2 - str :"+str+":");
					manifestmfInfo.add (new ManifestmfItemInfo (str));
				}
			}
		}
//		System.out.println("<<< Utils.getManifestmf");
		return manifestmfInfo;
	}

	public static ModulemapsInfo getModulemaps (File file) {
//		System.out.println(">>> Utils.getModulemaps; "+file.getPath());
		ModulemapsInfo modulemapsInfo = new ModulemapsInfo();
		SAXBuilder builder = new SAXBuilder();
		Document document = null;

		try {
			document = builder.build (new FileInputStream (file));
		} catch (FileNotFoundException e) {
//			System.out.println("Error; file not found "+e.getMessage());
			return modulemapsInfo;
		} catch (JDOMException e) {
			System.out.println("Error; JDOMException "+e.getMessage());
			return modulemapsInfo;
		} catch (IOException e) {
			System.out.println("Error; IOException "+e.getMessage());
			return modulemapsInfo;
		}

		Element root = document.getRootElement();
		List<?> rows = root.getChildren("mappings");
//		System.out.println("size "+rows.size());
		for (int i = 0; i < rows.size(); i++) {
			Element row = (Element) rows.get(i);
			String name = row.getAttributeValue("projectName");
//			System.out.println("name :"+name+":");
			modulemapsInfo.add (new ModulemapsItemInfo (name));
		}

		rows = root.getChildren("utilityJARMappings");
//		System.out.println("size "+rows.size());
		for (int i = 0; i < rows.size(); i++) {
			Element row = (Element) rows.get(i);
			String name = row.getAttributeValue("projectName");
//			System.out.println("name :"+name+":");
			modulemapsInfo.add (new ModulemapsItemInfo (name));
		}
//		System.out.println("<<< Utils.getModulemaps");
		return modulemapsInfo;
	}
}
