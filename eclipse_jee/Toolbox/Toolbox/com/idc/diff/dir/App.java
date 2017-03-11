
package com.idc.diff.dir;

import java.io.File;
import java.util.Iterator;

public class App {
	public static void main(String[] args) {
		(new App()).doApp();
	}
	private void doApp() {
//		String basedir = "c:\\irac\\src\\herc_st";
		String basedir = "C:\\tmp\\kv1\\src";
		BaseInfo basedirInfo = new BaseInfo(basedir);
		doDirectory (new File(basedir), basedirInfo);
//		System.out.println("basedirInfo "+basedirInfo.toString());
		System.out.println("size "+basedirInfo.getSize());

//		String compdir = "c:\\tmp\\herc01\\herc_st";
		String compdir = "C:\\tmp\\kv2\\src";
		BaseInfo compdirInfo = new BaseInfo(compdir);
		doDirectory (new File(compdir), compdirInfo);
//		System.out.println("compdirInfo "+compdirInfo.toString());
		System.out.println("size "+compdirInfo.getSize());

		Output output1 = new Output("c:\\tmp\\file1.bat");
		Output output2 = new Output("c:\\tmp\\file2.bat");
		Output report = new Output("c:\\tmp\\report.txt");
/*
 * Pass 1 - look for each file in the same directory structure
 */
		BaseItemInfo baseItemInfo, compItemInfo;
		basedirInfo.sortByRelative();
		compdirInfo.sortByRelative();
		Iterator<BaseItemInfo> iter = compdirInfo.getItems();
		while (iter.hasNext()) {
			compItemInfo = (BaseItemInfo) iter.next();
			baseItemInfo = basedirInfo.getByRelativeName(compItemInfo);
			if (baseItemInfo == null) {
				report.println("File "+compItemInfo.getRelative()+" not found in baseline");				
			}
			else {
				compareFiles (output1, baseItemInfo, compItemInfo);
				compItemInfo.setComplete();
			}
		}
/*
 * Pass 2 - look for each remaining file anywhere in the directory structure
 */
		BaseInfo myBaseInfo;
		BaseItemInfo myBaseItemInfo;
		iter = compdirInfo.getItems();
		while (iter.hasNext()) {
			compItemInfo = (BaseItemInfo) iter.next();
			if (compItemInfo.isComplete()) continue;
			myBaseInfo = basedirInfo.getByName(compItemInfo);
			if (myBaseInfo == null || myBaseInfo.getSize() < 1) {
				report.println("File "+compItemInfo.getName()+" not found anywhere in the baseline");				
			}
			else {
//				System.out.println("myBaseInfo size "+myBaseInfo.getSize());
				Iterator<BaseItemInfo> iter2 = myBaseInfo.getItems();
				while (iter2.hasNext()) {
					myBaseItemInfo = (BaseItemInfo) iter2.next();
					compareFiles (output2, myBaseItemInfo, compItemInfo);
				}
				compItemInfo.setComplete();
			}
		}	

		output1.close();
		output2.close();
		report.close();
	}

	private void compareFiles (Output output, BaseItemInfo base, BaseItemInfo comp) {
		StringBuffer buf = new StringBuffer();
		buf.append ("fc /w ");
		buf.append (base.getPath());
		buf.append (" ");
		buf.append (comp.getPath());
		output.println(buf.toString());
	}

	private void doDirectory (File diffDir, BaseInfo baseInfo) {
//		System.out.println(">>> doDirectory; "+diffDir.getPath());
		if (! diffDir.isDirectory()) return;

		File file;
		File[] allFiles = diffDir.listFiles();
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isFile() && isIncludedFileType(file))
				baseInfo.add (new BaseItemInfo(baseInfo.getBase(), file));
		}
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) doDirectory(file, baseInfo);
		}
//		System.out.println("<<< doDirectory");
	}

	private boolean isIncludedFileType (File file) {
		String ext = getExtension(file.getName());
		if (ext != null && ext.trim().toLowerCase().equals("java")) return true;
		if (ext != null && ext.trim().toLowerCase().equals("xml")) return true;
		return false;
	}
    private String getExtension(String name) {
    	int n1 = name.lastIndexOf('.');
    	if (n1 < 0) return "";
    	return name.substring(n1+1);
    }
}
