package com.idc.grepgui;

import java.io.IOException;

import com.idc.utils.JVString;

public class TestExecCmd {

	public static void main(String[] args) {
		(new TestExecCmd()).doApp();
	}
	private void doApp() {
		String strEditor;
		String strFile;

		strEditor = "open -n -a /Applications/TextEditor.app";
		strEditor = "/usr/local/bin/gvim";
		strFile = "/Users/jv/tmp/x.dif";
//		strFile = "/Users/jv/tmp/x.1";
//		strFile = "/Users/jv/Desktop/MyDevelopment/repo_ui_1/brackets/templates/ui/vendors/3rd-party/bootstrap-3.3.6/js/.jshintrc";

		String[] strCmd = JVString.createStringArrayForExecCmd (strEditor, strFile);

		int num = strCmd.length;
		System.out.println("num "+num);
		for (String s : strCmd) {
			System.out.println("String  :"+s+":");
		}
		try {
			Runtime.getRuntime().exec(strCmd);
		}
		catch (IOException e) {
			System.out.println("Exception; "+e.getMessage());
		}
	}
}
