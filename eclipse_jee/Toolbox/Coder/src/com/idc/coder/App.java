package com.idc.coder;

import java.io.File;
import java.util.Iterator;

import com.idc.coder.model.ClazzItemInfo;
import com.idc.coder.model.FieldInfo;
import com.idc.coder.model.FieldItemInfo;
import com.idc.coder.output.OutputInfo;

/*
Handle most testing through request/response noddy.
How to test emails?
How to test reports?
How to test batch jobs?
How to test jsps?
*/
public class App {
	public static void main(String[] args) {
		(new App()).doApp2();
	}

	@SuppressWarnings("unused")
	private void doApp() {
		String[] files = new String[4];
		files[0] = "C:/development/herc_11_02/HercUTIL/com/hertz/hercutil/member/data/MemberDataAccountInfo.class";
		files[1] = "C:/development/herc_11_02/HercUTIL/com/hertz/hercutil/member/data/MemberDataAccountItemInfo.class";
		files[2] = "C:/development/herc_11_02/HercUTIL/com/hertz/hercutil/member/data/MemberDataItemInfo.class";
		files[3] = "C:/development/herc_11_02/HercUTIL/com/hertz/hercutil/member/data/MemberDataInfo.class";
		for (int num = 0; num < files.length; num++) {
			handleFile(files[num]);
		}
	}

	private void handleFile(String file) {
		// Class<?> classRef
	}

//TODO; RMAccountDataItemInfo; uses HercDate; how to do this?
//TODO; Constructor HashMap; assume data object; get properties from the hashmap; test all getters.
//TODO; isSame method; test all combinations.
//TODO; recognize type of pattern. different treatment.
//TODO; choose ItemInfo, handle all. then move on to Info
//TODO; build code to create test objects.
//TODO; do I need to do anything with the a superclass?
//TODO; ClazzItemInfo; what type of a class is it? need a property.

	private static final String APP_WORKING_DIRECTORY = "c:/jvReflection";

	private void doApp2() {
		System.out.println(">>>doApp2");
		File myDir = Utils.createDirectories (APP_WORKING_DIRECTORY);
		String project = "C:/development/herc_11_02/HercUTIL";
		String fullname = "com.hertz.hercutil.member.data.MemberDataAccountItemInfo";
		ClazzItemInfo clazzItemInfo = Utils.loadClass (project, fullname);
		System.out.println("clazzItemInfo "+clazzItemInfo);

//		play1 (clazzItemInfo);

		OutputInfo outputInfo = new OutputInfo();
		OutputUtils.createOutput (clazzItemInfo, outputInfo);
		System.out.println("mydir "+myDir.getPath());

		File outFile = new File (myDir.getPath() + File.separatorChar + clazzItemInfo.getName() + ".java");
		System.out.println("Writing to file "+outFile.getPath());
		OutputUtils.handleOutput (outFile, outputInfo);

	}
	@SuppressWarnings("unused")
	private void play1 (ClazzItemInfo clazzItemInfo) {
		FieldInfo fieldInfo = clazzItemInfo.getFieldInfo();
		Iterator<FieldItemInfo> iter = fieldInfo.getItems();
		while (iter.hasNext()) {
			FieldItemInfo item = iter.next();
			Class<?> jv = item.getField().getType();
			System.out.println("field type :"+jv.getName());
			System.out.println("field type :"+jv.getSimpleName());
			//Modifier.is(jv);
			jv.isPrimitive();
			jv.isArray();
		}
	}
}

