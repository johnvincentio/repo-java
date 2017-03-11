package com.idc.file.dir;

public class App {
	public static void main(String[] arg) throws Exception {
//		String strDir = "C:\\irac\\src\\herc_web\\Hcm_Herc\\Herc\\Data";
		String strDir = "C:\\irac\\src\\herc_web";
		String outputFile = "c:\\tmp\\all.txt";
		MyTest1 test1 = new MyTest1(strDir,outputFile,false,true);
		test1.doit();
		System.out.println("Done");
	}
}
