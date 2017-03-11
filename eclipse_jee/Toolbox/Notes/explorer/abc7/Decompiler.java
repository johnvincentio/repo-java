package com.idc.explorer.abc7;

import java.io.File;

import net.sf.jdec.main.ConsoleLauncher;

public class Decompiler {
	private File m_workingDirectory;

	public Decompiler (File workingDirectory) {
		m_workingDirectory = workingDirectory;
	}

	public NodeItemInfo decompile (NodeItemInfo item) {
		System.out.println(">>> Decompiler::decompile");
		File workingDirectory = Utils.makeWorkingDirectory (m_workingDirectory);
		System.out.println("workingDirectory :"+workingDirectory+":");
		Explorer expander = new Explorer (m_workingDirectory);
		expander.makeFullDirectories (workingDirectory);

		String[] args = new String[20];
		int pos = 0;
		args[pos++] = "-showImports"; args[pos++] = "true";
		args[pos++] = "-outputMode"; args[pos++] = "file";
		args[pos++] = "-logMode"; args[pos++] = "file";
		args[pos++] = "-outputFolder"; args[pos++] = workingDirectory.getPath();
		args[pos++] = "-logLevel"; args[pos++] = "1";
		args[pos++] = "-logPath"; args[pos++] = "c:/tmp/jdec/log/log.txt";
		args[pos++] = "-tempDir"; args[pos++] = "c:/tmp/jdec/tmp";
		args[pos++] = "-innerDepth"; args[pos++] = "1";
		args[pos++] = "-input"; args[pos++] = item.getPath();
		args[pos++] = "-option"; args[pos++] = "dc";

		System.out.println("before compile");
		ConsoleLauncher.main(args);
		System.out.println("after compile");

		NodeItemInfo nodeItemInfo = expander.listDir (workingDirectory);
		System.out.println("nodeItemInfo "+nodeItemInfo);

		System.out.println("<<< Decompiler::decompile");
		return nodeItemInfo;
	}
}
/*
-option dc
-jar c:/tmp/2/toolbox.jar
-outputFolder c:/tmp/dec/output
-showImports true

Output_Mode=file
Log_Mode=file
Output_Folder_Path=c:/tmp/jdec/output
Output_File_Extension=jdec
LOG_LEVEL=1
Log_File_Path=c:/tmp/jdec/log/log.txt
UI_LOG_FILE_PATH=c:/tmp/jdec/log/uilog.txt
JAVA_CLASS_FILE=null
JAR_FILE_PATH=null
Show_Imports=true
JDec_Option=dc
Temp_Dir=c:/tmp/jdec/tmp
Inner_Depth=1
Inline_Anonymous_Inner_Class_Content=false
Interpret_Exception_Table=false
Interpret_Non_ASCII=true
Force_Non_ASCII=false
Skip_Class_Version_Check=true
Formatting_Show_Flower_Bracket_at_same_line=true
		
		for (int i = 0; i < input.length; i++) {

			if (input[i].equalsIgnoreCase("-showImports")) {
				java.lang.String showImport = input[i + 1];
				Configuration.setShowImport(showImport);

			}
			if (input[i].equalsIgnoreCase("-outputMode")) {
				java.lang.String opMode = input[i + 1];
				Configuration.setOutputMode(opMode);

			}
			if (input[i].equalsIgnoreCase("-logMode")) {
				java.lang.String logMode = input[i + 1];
				Configuration.setLogMode(logMode);

			}
			if (input[i].equalsIgnoreCase("-outputFolder")) {
				java.lang.String folder = input[i + 1];
				Configuration.setOutputFolderPath(folder);
				Configuration.backupOriginalOutputFilePath(folder);
			}
			if (input[i].equalsIgnoreCase("-logLevel")) {
				java.lang.String logl = input[i + 1];
				Configuration.setLogLevel(logl);

			}
			if (input[i].equalsIgnoreCase("-logPath")) {
				java.lang.String logP = input[i + 1];
				Configuration.setLogFilePath(logP);
			}
			if (input[i].equalsIgnoreCase("-tempDir")) {
				java.lang.String temp = input[i + 1];
				Configuration.setTempDir(temp);
			}
			if (input[i].equalsIgnoreCase("-innerDepth")) {
				java.lang.String temp = input[i + 1];
				Configuration.setInnerdepth(temp);
			}

			if (input[i].equals("-jar")) {
				java.lang.String jarPath = input[i + 1];
				Configuration.setJarPath(jarPath);
				Configuration.setDecompileroption("jar");
				jarSpecified = true;
				Configuration.setJarSpecified(true);

			}

			if (input[i].equals("-input")) {
				classFilePath = input[i + 1];
				inputSpecified = true;
				Configuration.setSingleClassSpecified(true);
				Configuration.setJavaClassFile(classFilePath);
				Configuration.setClassFilePath(classFilePath);
			}
			if (input[i].equals("-option")) {
				java.lang.String option = input[i + 1];
				if (option.equals("vcp")) {
					Configuration.setDecompileroption("vcp");
				} else if (option.equals("disassemble") || option.equals("dis")) {
					Configuration.setDecompileroption("dis");
				} else if (option.equals("dc")) {
					Configuration.setDecompileroption("dc");
				} else if (option.equals("dc_nocode")) {
					Configuration.setDecompileroption("nocode");
				} else if (option.equals("help")) {
					Configuration.setDecompileroption("help");
				} else if (option.equals("llv")
						|| option.equalsIgnoreCase("listlocalvar")) {
					Configuration.setDecompileroption("llv");
				}

				else {
					throw new InvalidInputException(
							"Invalid Option specified to the Decompiler..."
									+ option);
				}
			}

*/
