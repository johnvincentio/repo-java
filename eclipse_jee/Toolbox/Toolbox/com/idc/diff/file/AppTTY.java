package com.idc.diff.file;

import com.idc.file.exec.OutputLine;
import com.idc.file.exec.PrintLine;

public class AppTTY {
	AppTTY() {}
	public static void main(String args[]) {
		if (args.length != 2) {
			System.err.println("Usage: diff oldfile newfile");
			System.exit(1);
		}
		OutputLine cout = new PrintLine();
		Diff diff = new Diff(cout);
		boolean bDiff = diff.doDiff(args[0], args[1]);
		if (bDiff) System.exit(0);
		System.exit(1);
	}
}
