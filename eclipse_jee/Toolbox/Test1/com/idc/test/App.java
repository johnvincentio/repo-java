package com.idc.test;

import java.io.File;

public class App {
	public static void main (String[] args) {
		App app = new App();
		app.dotest2();
	}
/*
	private void dotest() {
		Extensions extensions = new Extensions();
		extensions.add (new Extension(".*?\\.j2ee",true));

		isMatch (extensions, "C:\\tmp101\\c\\.j2ee");
		isMatch (extensions, "C:\\tmp101\\c\\abc.j2ee");
		isMatch (extensions, "C:\\tmp101\\c\\.jlv");
	}
	private void dotest1() {
		Extensions extensions = new Extensions();
		extensions.add (new Extension("EJS.*?\\.java",true));

		isMatch (extensions, "C:\\tmp101\\c\\EJSStatelessDataServicesHomeBean_82bb28df.java");
	}
*/
	private void dotest2() {
		Extensions extensions = new Extensions();
		extensions.add (new Extension("_.*?\\.java",true));

		isMatch (extensions, "C:\\tmp101\\c\\_EJSRemoteStatelessDataServicesHome_82bb28df_Tie.java");
	}
	private void isMatch (Extensions extensions, String strFile) {
		boolean bMatch = extensions.isMatchAndChecked (new File(strFile));
		if (bMatch)
			System.out.println("File "+strFile+" matches ");
		else
			System.out.println("File "+strFile+" does not match ");
	}
}
/*
		m_extDelete.add (new Extension(".*?\\.swp",true));
		m_extDelete.add (new Extension(".*?\\.zip",true));
		m_extDelete.add (new Extension("_.*?\\.java",true));
		m_extDelete.add (new Extension("EJS*.java",true));
*/
