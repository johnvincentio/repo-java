
package com.idc.api;

import java.io.File;

public class DirItemInfo {
	private File file;
//	private boolean valid = false;
	private int type = 0;
	public DirItemInfo (File f) {
		file = f;
		type = createType();
//		System.out.println(" file "+file.getAbsolutePath()+" type "+type);
	}
	public String getName() {return file.getName();}
	public String getFullName() {return file.getAbsolutePath();}
	public boolean isSource() {return (type > 0) ? true : false;}
	public boolean isParser () {
		String name = file.getName().trim();
		if (name.toUpperCase().endsWith("EAR")) return false;
		if (name.toUpperCase().endsWith("WSDL")) return false;
		if (name.toUpperCase().endsWith("DATA")) return false;
		if (name.toUpperCase().endsWith("LOST+FOUND")) return false;
		if (isSource()) return false;
		return true;
	}
	public String getAntPath() {
		String path1 = JVString.replace (getFullName(), "\\","/");
		String path2 = JVString.replace (path1, "C:/irac/src/qcr_int", "${qcr.dir}");
		if (type == 2 || type == 3)
			path2 = path2 + "/ejbModule";
		else if (type == 5)
			path2 = path2 + "/JavaSource";
		return path2;
	}
	public String toString() {return "(" + getFullName()+ "," + isSource() + ")";}

	private int createType() {
		int num = 0;
		String name = file.getName().trim();
//		System.out.println("name :"+name+":");
		num++; if (name.toUpperCase().endsWith("APP")) return num;
		num++; if (name.toUpperCase().endsWith("EJBCLIENT")) return num;
		num++; if (name.toUpperCase().endsWith("EJB")) return num;
		num++; if (name.toUpperCase().endsWith("UTIL")) return num;
		num++; if (name.toUpperCase().endsWith("WEB")) return num;

		num++; if (name.toUpperCase().endsWith("DYNAMICTRANSLATIONAGENT")) return num;
		num++; if (name.toUpperCase().endsWith("EMAILCONFIRMATIONCLIENT")) return num;
		num++; if (name.toUpperCase().endsWith("GLOBALLDAPSERVICES")) return num;
		num++; if (name.toUpperCase().endsWith("IRACMULTIPLETHREADER")) return num;
		num++; if (name.toUpperCase().endsWith("TEMPLATE_GENERATION")) return num;
		num++; if (name.toUpperCase().endsWith("FILESYTEMGENERATOR")) return num;
		num++; if (name.toUpperCase().endsWith("DATAMIGRATION")) return num;
		num++; if (name.toUpperCase().endsWith("MAPPOINTDATADATAMIGRATION")) return num;
		num++; if (name.toUpperCase().endsWith("MIGRATIONXMLGENERATE")) return num;
		num++; if (name.toUpperCase().endsWith("CAR SALES")) return num;
		num++; if (name.toUpperCase().endsWith("CTMRMISERVER")) return num;
		num++; if (name.toUpperCase().endsWith("EMAILWEBSERVICE")) return num;
		num++; if (name.toUpperCase().endsWith("UNSUBSCRIBE")) return num;
		return 0;
	}
}

