package com.idc.file.dump;

public class App {
	public static void main (String[] args) {
		(new App()).doTest3();
	}
	public void doTest() {
		String sfile = "c:/work108/herc-11.5-release/HercJobEJB/ejbModule/com/hertz/hercjob/datamigration/StringTokenizer.java";		// 1
		sfile = "c:/work108/irac11.07/HercJobEJB/ejbModule/com/hertz/hercjob/datamigration/StringTokenizer.java";						// 2
		CharDump dump = new CharDump (sfile, 80);
		dump.dump();
	}
	public void doTest1() {
		String sfile = "C:/work108/herc-11.5-release/Ant/zip/build.xml";																// 3
		sfile = "C:/work108/irac11.07/Ant/zip/build.xml";																				// 4
		OctalDump dump = new OctalDump (sfile, 80);
		dump.dump();
	}
	public void doTest2() {
		String sfile = "C:/work108/herc-11.5-release/Ant/zip/build.xml";																// 3a
		sfile = "C:/work108/herc-11.5-release/Ant/zip/build.xml";			// 4a
		CharDump dump = new CharDump (sfile, 80);
		dump.dump();
	}
	public void doTest3() {
		String sfile = "C:/development/herc-12.1-dev/properties/herc/content/hercWeb/US/enUS/integrated/specialProgramsView.xml";			// 4a
		CharDump dump = new CharDump (sfile, 80);
		dump.dump();
	}
}
//C:\work108\irac11.07\HercJobEJB\ejbModule\com\hertz\hercjob\datamigration

//C:\work108\herc-11.5-release\Ant\zip