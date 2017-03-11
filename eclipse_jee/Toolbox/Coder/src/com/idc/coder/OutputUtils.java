package com.idc.coder;

import java.io.File;
import java.util.Iterator;

import com.idc.coder.model.ClazzItemInfo;
import com.idc.coder.model.FieldInfo;
import com.idc.coder.model.FieldItemInfo;
import com.idc.coder.model.InterfaceItemInfo;
import com.idc.coder.output.OutputInfo;
import com.idc.coder.output.OutputItemInfo;
import com.idc.file.JVFile;

public class OutputUtils {
	public static void createOutput (ClazzItemInfo clazzItemInfo, OutputInfo outputInfo) {
		addHeader (outputInfo);
		addPackage (clazzItemInfo, outputInfo);
		addImports (clazzItemInfo, outputInfo);
		addAuthor (outputInfo);
		addClassSpecification (clazzItemInfo, outputInfo);
		addProperties (clazzItemInfo, outputInfo);
	}

	public static boolean handleOutput (File outFile, OutputInfo outputInfo) {
		StringBuffer buf = new StringBuffer();
		Iterator<OutputItemInfo> iter = outputInfo.getItems();
		while (iter.hasNext()) {
			buf.append (iter.next().getOutput()).append("\n");
		}
		return JVFile.writeFile (buf.toString(), outFile);
	}
	private static void addHeader (OutputInfo outputInfo) {
		outputInfo.add ("/********************************************************************");
		outputInfo.add ("*			Copyright (c) 2011 The Hertz Corporation				*");
		outputInfo.add ("*			  All Rights Reserved.  (Unpublished.)					*");
		outputInfo.add ("*																	*");
		outputInfo.add ("*		The information contained herein is confidential and		*");
		outputInfo.add ("*		proprietary to The Hertz Corporation and may not be			*");
		outputInfo.add ("*		duplicated, disclosed to third parties, or used for any		*");
		outputInfo.add ("*		purpose not expressly authorized by it.  Any unauthorized	*");
		outputInfo.add ("*		use, duplication, or disclosure is prohibited by law.		*");
		outputInfo.add ("*																	*");
		outputInfo.add ("*********************************************************************/");
		outputInfo.add ("");
	}
	private static void addPackage (ClazzItemInfo clazzItemInfo, OutputInfo outputInfo) {
		outputInfo.add ("package "+clazzItemInfo.getPackage()+";");
		outputInfo.add ("");
	}
	private static void addImports (ClazzItemInfo clazzItemInfo, OutputInfo outputInfo) {
		outputInfo.add ("import java.io.Serializable;");
		outputInfo.add ("import java.util.Map;");
		outputInfo.add ("import java.util.ArrayList;");
		outputInfo.add ("import java.util.Iterator;");
		outputInfo.add (" ");
		if (clazzItemInfo.isSuperClazz()) 
			outputInfo.add ("import "+clazzItemInfo.getSuperClazz().getName()+";");
		if (clazzItemInfo.getInterfaceInfo().isInterface()) {
			Iterator<InterfaceItemInfo> iter = clazzItemInfo.getInterfaceInfo().getItems();
			while (iter.hasNext()) {
				outputInfo.add ("import "+iter.next().getInterfaze().getName()+";");
			}
		}
		if (clazzItemInfo.isSuperClazz() || clazzItemInfo.getInterfaceInfo().isInterface())
			outputInfo.add (" ");
	}

	private static void addAuthor (OutputInfo outputInfo) {
		outputInfo.add ("/**");
		outputInfo.add ("* @author John Vincent");
		outputInfo.add ("*");
		outputInfo.add ("*/");
		outputInfo.add ("");
	}
	private static void addClassSpecification (ClazzItemInfo clazzItemInfo, OutputInfo outputInfo) {
		outputInfo.add (clazzItemInfo.createDefinition());
	}

	private static void addProperties (ClazzItemInfo clazzItemInfo, OutputInfo outputInfo) {
		FieldInfo fieldInfo = clazzItemInfo.getFieldInfo();
		Iterator<FieldItemInfo> iter = fieldInfo.getItems();
		while (iter.hasNext()) {
			FieldItemInfo item = iter.next();
			outputInfo.add ("\t"+item.createDefinition());
		}
	}
}
