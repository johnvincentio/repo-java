package com.idc.parse;

import java.io.File;

public class Control {
	private static int SCENARIO = 1;

	public void work (String[] args) {
		String strFile;
		switch (SCENARIO) {
			case 1:
				strFile = "C:\\irac7\\wrkspc\\Work1\\JV1\\Working 1\\resources.xml";
				doit (strFile);
				break;
			default:
				break;
		}

	}
	private void doit(String strFile) {
		 System.out.println("Loading xml file "+strFile);
		 JdbcInfo jdbcInfo = makeData(strFile);
		 System.out.println(jdbcInfo.toString());
		 System.out.println("All reports complete");
	}

	private JdbcInfo makeData(String strFile) {
		 File file = new File (strFile);
		 if (! file.isFile()) return null;
		 if (! file.exists()) return null;
		 JVxml jvxml = new JVxml();
		 return jvxml.parse(file);
	}
}
