/*
 * Util.java Copyright (c) 2006,07 Swaroop Belur
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package net.sf.jdec.util;

import net.sf.jdec.config.Configuration;
import net.sf.jdec.constantpool.MethodInfo;
import net.sf.jdec.core.LocalVariableHelper;
import net.sf.jdec.exceptions.IOError;
import net.sf.jdec.exceptions.InvalidInputException;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.ui.util.LogWriter;
import net.sf.jdec.ui.util.UIUtil;

import java.io.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class Util {

	private static java.lang.String classFilePath = "***Not-Initialized***";

	/***************************************************************************
	 * // Assumption : input represents The Full Path off The Class File
	 * 
	 * @param input
	 */

	public static void registerInputs(java.lang.String[] input)
			throws InvalidInputException, IOException {
		boolean inputSpecified = false;
		boolean jarSpecified = false;
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
		}
		if (inputSpecified == false) {
			classFilePath = Configuration.getJavaClassFile();
		}

	}

	public static void showUsage() throws IOException {
		net.sf.jdec.io.Writer writer = net.sf.jdec.io.Writer.getWriter("log");
		if (writer == null) {
			throw new IOError("**** Error While Creating Writer Object....");
		} else {
			try {
				writer.writeLog(" \n\nInvalid Usage of the Decompiler....\n");
				writer
						.writeLog(" Please use the help option to learn how to use the tool...");
				writer.writeLog(" \n***Way to Run Help....\n");
				writer.writeLog(" ---> 1> Type help as the argument Or\n");
				writer.writeLog(" ---> 2> Type /? as the argument\n");
				writer.writeLog(" ---> 1> Type -help as the argument\n\n");
				writer.flush();
				Thread.sleep(5000);
				System.exit(1);
			} catch (InterruptedException ie) {
				// Leave Blank
			}

		}

	}

	public static java.lang.String getClassPath() {

		return classFilePath;

	}

	public static java.lang.String[] parseAccessSpecifiers(Integer code,
			int type) {
		java.lang.String specifiers[] = null;
		if (type == Constants.FIELD_ACC) {
			specifiers = decodeFieldSpecifiers(code);
		}
		if (type == Constants.METHOD_ACC) {
			specifiers = decodeMethodSpecifiers(code);
		}
		return specifiers;
	}

	public static java.lang.String[] decodeFieldSpecifiers(Integer code) {
		int numberOfSpecifiers = -1;
		java.lang.String[] specifiers = null;
		switch (code.intValue()) {

		case Constants.ACC_PUB_F:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			break;
		case Constants.ACC_PUB_STATIC:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			break;
		case Constants.ACC_PUB_F_FINAL:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "final";
			break;
		case Constants.ACC_PUB_STATIC_FINAL:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "final";
			break;
		case Constants.ACC_PUB_STATIC_VOLATILE:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "volatile";
			break;
		case Constants.ACC_PUB_STATIC_TRANSIENT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "transient";
			break;
		case Constants.ACC_PUB_TRANSIENT:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "transient";
			break;
		case Constants.ACC_PUB_STATIC_FINAL_TRANSIENT:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "transient";
			break;
		case Constants.ACC_PUB_FINAL_TRANSIENT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "final";
			specifiers[2] = "transient";
			break;
		case Constants.ACC_PRI:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			break;

		case Constants.ACC_PRI_STATIC:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";

			break;
		case Constants.ACC_PRI_FINAL:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "final";
			break;

		case Constants.ACC_PRI_STATIC_FINAL:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "final";
			break;

		case Constants.ACC_PRI_STATIC_VOLATILE:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "volatile";
			break;

		case Constants.ACC_PRI_STATIC_TRANSIENT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "transient";
			break;
		case Constants.ACC_PRI_TRANSIENT:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "transient";
			break;

		case Constants.ACC_PRI_STATIC_FINAL_TRANSIENT:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "transient";
			break;

		case Constants.ACC_PRI_FINAL_TRANSIENT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "final";
			specifiers[2] = "transient";
			break;

		case Constants.ACC_PRO:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			break;
		case Constants.ACC_PRO_STATIC:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			break;

		case Constants.ACC_PRO_FINAL:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "final";
			break;

		case Constants.ACC_PRO_STATIC_FINAL:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "final";
			break;
		case Constants.ACC_PRO_STATIC_VOLATILE:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "volatile";
			break;

		case Constants.ACC_PRO_STATIC_TRANSIENT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "transient";
			break;

		case Constants.ACC_PRO_TRANSIENT:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "transient";
			break;

		case Constants.ACC_PRO_STATIC_FINAL_TRANSIENT:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "transient";
			break;

		case Constants.ACC_PRO_FINAL_TRANSIENT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "final";
			specifiers[2] = "transient";
			break;

		case Constants.ACC_FINAL_VOLATILE:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "final";
			specifiers[1] = "volatile";
			break;

		case Constants.ACC_FINAL_TRANSIENT:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[1] = "final";
			specifiers[2] = "transient";
			break;

		case Constants.ACC_FINAL_STATIC:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "final";
			specifiers[1] = "static";
			break;

		case Constants.ACC_FINAL_STATIC_VOLTILE:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "final";
			specifiers[1] = "static";
			specifiers[2] = "volatile";
			break;

		case Constants.ACC_FINAL_STATIC_TRANSIENT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "final";
			specifiers[1] = "static";
			specifiers[2] = "transient";
			break;

		case Constants.ACC_FINAL_VOLATILE_TRANSIENT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "final";
			specifiers[1] = "volatile";
			specifiers[2] = "transient";
			break;

		case Constants.ACC_STATIC_VOLATILE:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			specifiers[1] = "volatile";
			break;

		case Constants.ACC_STATIC_TRANSIENT:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			specifiers[1] = "transient";
			break;

		case Constants.ACC_VOLATILE_TRANSIENT:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "volatile";
			specifiers[1] = "transient";
			break;

		case Constants.ACC_F_STATIC:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			break;

		case Constants.ACC_F_FINAL:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "final";
			break;

		case Constants.ACC_VOLATILE:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "volatile";
			break;

		case Constants.ACC_TRANSIENT:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "transient";
			break;

		default:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			// specifiers[0]=Constants.UNKNOWNACCESSORS; // NOt Correct For
			// Default Access
			specifiers[0] = "";
			break;

		}
		return specifiers;
	}

	public static java.lang.String[] decodeMethodSpecifiers(Integer code) {
		int numberOfSpecifiers = -1;
		java.lang.String[] specifiers = null;
		switch (code.intValue()) {

		case Constants.M_PUB:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			break;

		case Constants.M_PUB_ABS:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "abstract";
			break;

		case Constants.M_PUB_FINAL:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "final";
			break;

		case Constants.M_PUB_NATIVE:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "native";
			break;

		case Constants.M_PUB_STATIC:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			break;

		case Constants.M_PUB_STATIC_FINAL:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "final";
			break;

		case Constants.M_PUB_STATIC_FINAL_NATIVE:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "native";
			break;

		case Constants.M_PUB_STATIC_FINAL_STRICT:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "strictfp";
			break;

		case Constants.M_PUB_STATIC_FINAL_SYNCH:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "synchronized";
			break;

		case Constants.M_PUB_STATIC_FINAL_SYNCH_NATIVE:
			numberOfSpecifiers = 5;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "native";
			specifiers[4] = "synchronized";
			break;

		case net.sf.jdec.util.Constants.M_PUB_STATIC_FINAL_SYNCH_STRICT:
			numberOfSpecifiers = 5;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "synchronized";
			specifiers[4] = "strictfp";
			break;
		case net.sf.jdec.util.Constants.M_PUB_STATIC_NATIVE:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "native";
			break;
		case net.sf.jdec.util.Constants.M_PUB_STATIC_STRICT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "strictfp";
			break;
		case Constants.M_PUB_STATIC_SYNCHRONIZED:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "static";
			specifiers[2] = "synchronized";
			break;
		case Constants.M_PUB_SYNCHRONIZED:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "synchronized";

			break;
		case Constants.M_PUB_STRICT:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "public";
			specifiers[1] = "strictfp";

			break;

		case Constants.M_PRIVATE:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			break;

		case Constants.M_PRIVATE_FINAL:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "final";
			break;
		case Constants.M_PRIVATE_NATIVE:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "native";

			break;
		case Constants.M_PRIVATE_STATIC:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";

			break;
		case Constants.M_PRIVATE_STATIC_FINAL:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "final";

			break;
		case Constants.M_PRIVATE_STATIC_FINAL_NATIVE:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "native";
			break;
		case Constants.M_PRIVATE_STATIC_FINAL_STRICT:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "strictfp";

			break;
		case Constants.M_PRIVATE_STATIC_FINAL_SYNCH:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "synchronized";

			break;
		case Constants.M_PRIVATE_STATIC_FINAL_SYNCH_NATIVE:
			numberOfSpecifiers = 5;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "native";
			specifiers[4] = "synchronized";

			break;

		case Constants.M_PRIVATE_STATIC_FINAL_SYNCH_STRICT:
			numberOfSpecifiers = 5;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "synchronized";
			specifiers[4] = "strictfp";
			break;
		case Constants.M_PRIVATE_STATIC_NATIVE:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "native";
			break;
		case Constants.M_PRIVATE_STATIC_STRICT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "strictfp";
			break;
		case Constants.M_PRIVATE_STATIC_SYNCHRONIZED:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "static";
			specifiers[2] = "synchronized";
			break;
		case Constants.M_PRIVATE_SYNCHRONIZED:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "synchronized";

			break;
		case Constants.M_PRIVATE_STRICT:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "private";
			specifiers[1] = "strictfp";

			break;

		case Constants.M_PROTECTED:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			break;
		case Constants.M_PROTECTED_ABS:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "abstract";
			break;
		case Constants.M_PROTECTED_FINAL:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "final";
			break;
		case Constants.M_PROTECTED_NATIVE:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "native";

			break;
		case Constants.M_PROTECTED_STATIC:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";

			break;
		case Constants.M_PROTECTED_STATIC_FINAL:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "final";

			break;
		case Constants.M_PROTECTED_STATIC_FINAL_NATIVE:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "native";
			break;
		case Constants.M_PROTECTED_STATIC_FINAL_STRICT:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "strictfp";

			break;
		case Constants.M_PROTECTED_STATIC_FINAL_SYNCH:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "synchronized";

			break;
		case Constants.M_PROTECTED_STATIC_FINAL_SYNCH_NATIVE:
			numberOfSpecifiers = 5;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "native";
			specifiers[4] = "synchronized";

			break;

		case Constants.M_PROTECTED_STATIC_FINAL_SYNCH_STRICT:
			numberOfSpecifiers = 5;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "final";
			specifiers[3] = "synchronized";
			specifiers[4] = "strictfp";
			break;

		case Constants.M_PROTECTED_STATIC_NATIVE:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "native";
			break;
		case Constants.M_PROTECTED_STATIC_STRICT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "strictfp";
			break;
		case Constants.M_PROTECTED_STATIC_SYNCHRONIZED:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "static";
			specifiers[2] = "synchronized";
			break;
		case Constants.M_PROTECTED_SYNCHRONIZED:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "synchronized";
			break;

		case Constants.M_PROTECTED_STRICT:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "protected";
			specifiers[1] = "strictfp";
			break;

		case Constants.M_STATIC:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			break;

		case Constants.M_FINAL:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "final";
			break;

		case Constants.M_SYNCHRONIZED:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "synchronized";
			break;

		case Constants.M_NATIVE:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "native";
			break;

		case Constants.M_ABS:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "abstract";
			break;

		case Constants.M_STRICT:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "strictfp";
			break;

		case Constants.M_STATIC_FINAL:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			specifiers[1] = "final";
			break;

		case Constants.M_STATIC_SYNCHRONIZED:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			specifiers[1] = "synchronized";
			break;

		case Constants.M_STATIC_NATIVE:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			specifiers[1] = "native";
			break;

		case Constants.M_STATIC_STRICT:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			specifiers[1] = "strictfp";
			break;

		case Constants.M_STATIC_FINAL_SYNCH:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			specifiers[1] = "final";
			specifiers[2] = "synchronized";
			break;

		case Constants.M_STATIC_FINAL_NATIVE:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			specifiers[1] = "final";
			specifiers[2] = "native";
			break;

		case Constants.M_STATIC_FINAL_STRICT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			specifiers[1] = "final";
			specifiers[2] = "strictfp";
			break;
		case Constants.M_STATIC_FINAL_SYNCH_NATIVE:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			specifiers[1] = "final";
			specifiers[2] = "synchronized";
			specifiers[3] = "native";
			break;
		case Constants.M_STATIC_FINAL_SYNCH_STRICT:
			numberOfSpecifiers = 4;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "static";
			specifiers[1] = "final";
			specifiers[2] = "synchronized";
			specifiers[3] = "strictfp";
			break;

		case Constants.M_FINAL_SYNCH:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "final";
			specifiers[1] = "synchronized";
			break;

		case Constants.M_FINAL_NATIVE:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "final";
			specifiers[1] = "native";
			break;

		case Constants.M_FINAL_STRICT:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "final";
			specifiers[1] = "strictfp";
			break;

		case Constants.M_FINAL_SYNCH_NATIVE:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "final";
			specifiers[1] = "synchronized";
			specifiers[2] = "native";
			break;

		case Constants.M_FINAL_SYNCH_STRICT:
			numberOfSpecifiers = 3;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "final";
			specifiers[1] = "synchronized";
			specifiers[2] = "strictfp";
			break;

		case Constants.M_SYNCH_NATIVE:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "native";
			specifiers[1] = "synchronized";
			break;

		case Constants.M_SYNCH_STRICT:
			numberOfSpecifiers = 2;
			specifiers = new java.lang.String[numberOfSpecifiers];
			specifiers[0] = "strictfp";
			specifiers[1] = "synchronized";
			break;
		default:
			numberOfSpecifiers = 1;
			specifiers = new java.lang.String[numberOfSpecifiers];
			// specifiers[0]=Constants.UNKNOWNACCESSORS; Not Correct for Default
			// Access
			specifiers[0] = "";

		}
		return specifiers;
	}

	// METHOD BUGGY NEED TO FIX
	public static int getNumberOfParameters(String parameterString) {
		/*
		 * parameterString = parameterString.substring(1); parameterString =
		 * parameterString.substring(0,parameterString.length()-1);
		 */
		int charIndex = 0;
		ArrayList parameters = new ArrayList();
		while (parameterString.length() > 0) {
			if (parameterString.startsWith("L")) {
				java.lang.String objectString = parameterString.substring(0,
						parameterString.indexOf(";"));
				objectString = objectString.replace('/', '.');
				parameters.add(objectString);
				charIndex = charIndex + (objectString.length() + 2);
				parameterString = parameterString.substring(parameterString
						.indexOf(";") + 1);
			} else {
				char parameterChar = parameterString.charAt(0);
				if (parameterChar == ')')
					break;
				if (parameterChar == '[') {
					java.lang.String arrString = "[";
					if (parameterString.indexOf(";") != -1) {
						java.lang.String objectString = parameterString
								.substring(0, parameterString.indexOf(";"));
						objectString = objectString.replace('/', '.');
						parameters.add(objectString);
						parameterString = parameterString
								.substring(parameterString.indexOf(";") + 1);
					} else {
						parameterString = parameterString
								.substring(parameterString.indexOf("[") + 1);
					}
				} else {
					if (parameterChar == 'I') {
						parameters.add("int");
					}
					if (parameterChar == 'B') {
						parameters.add("byte");
					}
					if (parameterChar == 'C') {
						parameters.add("char");
					}
					if (parameterChar == 'D') {
						parameters.add("double");
					}
					if (parameterChar == 'F') {
						parameters.add("float");
					}
					if (parameterChar == 'J') {
						parameters.add("long");
					}
					if (parameterChar == 'S') {
						parameters.add("short");
					}
					if (parameterChar == 'Z') {
						parameters.add("boolean");
					}
					parameterString = parameterString.substring(1);
				}
			}
		}
		return parameters.size();
	}

	public static String getPrevCurSpace() {
		return prevCurSpace;
	}

	// Following code are used for formatting the decompiled code
	// for user friendliness.

	private static String prevCurSpace = " ";

	private final static String initialMethodSpace = " ";

	private final static java.lang.String incrementSpace = " ";

	private static java.lang.String currentSpace = initialMethodSpace
			.concat(incrementSpace);

	private static final java.lang.String disSpace = initialMethodSpace
			.concat(incrementSpace);

	public static void resetCurrentSpace() {
		currentSpace = initialMethodSpace.concat(incrementSpace);
	}

	/***************************************************************************
	 * This is the amount of space to be used to output to the result before
	 * adding on something new to codeStatements in parseJvmCode method of
	 * Decompiler Class.
	 * 
	 */

	public static java.lang.String getCurrentSpace() {
		return currentSpace;
	}

	/***************************************************************************
	 * This is the amount of space which will be used to add on to the
	 * currentSpace Or the amount of space to be removed from the currentSpace
	 * for the cases when '{' and '}' respectively
	 * 
	 */

	public static java.lang.String getIncrementSpace() {
		return incrementSpace;
	}

	/***************************************************************************
	 * Returns the space to be added before declaration of a method
	 * 
	 */
	public static String getInitialMethodSpace() {
		return initialMethodSpace;
	}

	// The only setter method
	/***************************************************************************
	 * Intentionally the only setter method. Use this to set the currentSpace
	 * when the amount of space has to be changed. ex:
	 * Util.setCurrentSpace(Util.getCurrentSpace().concat(Util.getIncrementSpace());
	 */

	public static void setCurrentSpace(java.lang.String newSpace) {
		currentSpace = newSpace;
	}

	/***************************************************************************
	 * NOTE: For proper result, ensure that the input to this method contains
	 * "\n" already. That is , if the input contains a { then ensure that { is
	 * preceded and followed by a \n character
	 * 
	 * Also Dont pass as input a statement like while(true){} In this case pass
	 * while(true)\n{\n first time and make second call as }\n
	 * 
	 * Even for simple statment like int i=1 pass it as int i=1\n
	 * 
	 * Purpose of this method is just to format the content in a user friendly
	 * readable manner and not take into account conditions where the input does
	 * not have a delimiter like line separator character.
	 * 
	 * Need to take care of the above while passing input
	 * 
	 * Keep these points in mind generally when using this function:
	 * 
	 * 1> Try not use a '\n' as the character before '}' (check example below)
	 * 2> Try to include a '\n' before and after a '{' as this will ensure line
	 * separbility between 2 lines. NOTE: this function does not take trouble of
	 * introducing a new line character . SO user should include '\n' in the
	 * input itself 3> Very Importans point: When using a '} ' in the input
	 * ensure that there is NO more than a SINGLE of }. So when there are 2 or
	 * more of '}' just use a temporary strings to include these '}' and format
	 * separately. Else the output may not be formatted neatly. Example:
	 * 
	 * codeStatements+="}\n\n}"; for the baove: String temp1="}"; // Assuming
	 * previous line has included new line character
	 * temp1=Util.formatDecompiledStatement(temp1); codeStatements+=temp1;
	 * codeStatements+="\n\n"; temp1="}";
	 * temp1=Util.formatDecompiledStatement(temp1); codeStatements+=temp1;
	 * 
	 * It is bit laborious but comes correctly (Remember it was fully
	 * intentional to keep it simple but effective -:) )
	 * 
	 * @param input
	 * 
	 */

	public static String formatDecompiledStatement_DONOTUSE(String input) {
		String formattedOutput = "";
		int openFlowerBracket = input.indexOf("{");
		int closeFlowerBracket = input.indexOf("}");
		int slashn = input.indexOf("\n");
		java.lang.String part1 = "";
		java.lang.String part2 = "";
		if (openFlowerBracket != -1 && closeFlowerBracket == -1) {
			boolean slashnAt0 = false;
			input = Util.trimString(input);
			slashn = input.indexOf("\n");
			if (slashn == 0) {
				slashnAt0 = true;
				formattedOutput += "\n";
				input = input.substring(1);
			}
			formattedOutput += currentSpace;
			/*
			 * if(slashnAt0) part1=input.substring(0,openFlowerBracket-1); else
			 * part1=input.substring(0,openFlowerBracket);
			 */
			openFlowerBracket = input.indexOf("{");
			part1 = input.substring(0, openFlowerBracket);
			part2 = input.substring(openFlowerBracket);
			part2 = part2.trim();
			formattedOutput += part1;
			formattedOutput += currentSpace;
			if (part2.indexOf("{") == 0) {
				boolean include = false;
				int t = 1;
				if (part2.length() > t) {
					char c = part2.charAt(t);
					while (c == '\n') {
						include = true;
						t++;
						if (part2.length() > t)
							c = part2.charAt(t);
						else
							break;
					}
				}
				if (t < part2.length())
					part2 = part2.substring(t);
				if (include) {
					formattedOutput += "{";
					formattedOutput += "\n" + currentSpace + "  " + part2
							+ "\n";
				} else
					formattedOutput += "\n" + currentSpace + part2 + "\n";
			} else
				formattedOutput += part2;

			updateCurrentSpace(Util.getIncrementSpace(), "add");
			return formattedOutput;
		}
		if (openFlowerBracket == -1 && closeFlowerBracket != -1) {
			updateCurrentSpace(Util.getIncrementSpace(), "remove");
			formattedOutput += Util.getCurrentSpace();
			boolean partsFormed = false;
			if (slashn != -1 && slashn < closeFlowerBracket) {
				part1 = input.substring(0, closeFlowerBracket);
				part2 = input.substring(closeFlowerBracket);
				partsFormed = true;
			}
			if (partsFormed) {
				formattedOutput += part1;
				formattedOutput += Util.getCurrentSpace();
				formattedOutput += part2;
			} else
				formattedOutput += input;
			return formattedOutput;
		}
		if (openFlowerBracket == -1 && closeFlowerBracket == -1
				&& input.trim().length() > 0) {
			input = Util.trimString(input);
			boolean partsFormed = false;
			slashn = input.indexOf("\n");
			if (slashn == 0) {
				part1 = input.substring(0, slashn + 1);
				part2 = input.substring(slashn + 1);
				partsFormed = true;
			}
			if (partsFormed) {
				formattedOutput += part1;
				formattedOutput += Util.getCurrentSpace();
				formattedOutput += part2;
			}
			if (!partsFormed) {
				formattedOutput += currentSpace;
				formattedOutput += input;
			}
			return formattedOutput;
		}
		//
		if (openFlowerBracket != -1 && closeFlowerBracket != -1) {
			boolean slashnAt0 = false;
			input = Util.trimString(input);
			slashn = input.indexOf("\n");
			if (slashn == 0) {
				slashnAt0 = true;
				formattedOutput += "\n";
				input = input.substring(1);
			}
			formattedOutput += currentSpace;
			/*
			 * if(slashnAt0) part1=input.substring(0,openFlowerBracket-1); else
			 * part1=input.substring(0,openFlowerBracket);
			 */
			openFlowerBracket = input.indexOf("{");
			part1 = input.substring(0, openFlowerBracket);
			part2 = input.substring(openFlowerBracket);
			part2 = part2.trim();
			formattedOutput += part1;
			formattedOutput += currentSpace;

			boolean include = false;
			int t = 1;
			if (part2.length() > t) {
				char c = part2.charAt(t);
				while (c == '\n') {
					include = true;
					t++;
					if (part2.length() > t)
						c = part2.charAt(t);
					else
						break;
				}
			}
			if (t < part2.length())
				part2 = part2.substring(t);
			if (include) {
				formattedOutput += "{";
				if (part2.indexOf("}") != -1) {
					java.lang.String sub = part2.substring(0, part2
							.indexOf("}"));
					formattedOutput += "\n" + currentSpace + "  " + sub + "\n";
					updateCurrentSpace(Util.getIncrementSpace(), "remove");
					formattedOutput += currentSpace + "}\n";

				} else
					formattedOutput += "\n" + currentSpace + "  " + part2
							+ "\n";
			} else {
				formattedOutput += "{";
				if (part2.indexOf("}") != -1) {
					java.lang.String sub = part2.substring(0, part2
							.indexOf("}"));
					formattedOutput += "\n" + currentSpace + "  " + sub + "\n";
					updateCurrentSpace(Util.getIncrementSpace(), "remove");
					formattedOutput += currentSpace + "  " + "}\n";

				} else {
					if (part2.equals("{") == false)
						formattedOutput += "\n" + currentSpace + "  " + part2
								+ "\n";
					if (part2.indexOf("{") >= 0) {
						int t1 = part2.indexOf("{");
						// if((t1+1) != part2.length())
						// {
						if ((t1 + 1) < part2.length()) {
							part2 = part2.substring(t1 + 1);
							formattedOutput += "\n" + currentSpace + "  "
									+ part2 + "\n";
						}
						// }

					}
				}
			}

			updateCurrentSpace(Util.getIncrementSpace(), "add");
			return formattedOutput;
		}

		return input; // Default Case;
	}

	public static void updateCurrentSpace(java.lang.String changeValue,
			java.lang.String typeofupdate) {
		prevCurSpace = Util.getCurrentSpace();
		if (typeofupdate.equals("add")) {
			setCurrentSpace(Util.getCurrentSpace().concat(changeValue));
		}
		if (typeofupdate.equals("remove")) {
			/*
			 * int numberOfTabsReqd=Util.getCurrentSpace().lastIndexOf("\t");
			 * java.lang.String tab="\t";
			 */
			java.lang.String temp = "";
			int incrLength = incrementSpace.length();
			int upperLimit = Util.getCurrentSpace().length() - incrLength;
			if (upperLimit < Util.getCurrentSpace().length() && upperLimit >= 0)
				temp = Util.getCurrentSpace().substring(0, upperLimit);
			else
				temp = Util.getCurrentSpace();
			/*
			 * for(int s=1;s<=numberOfTabsReqd;s++) temp+=tab;
			 */
			Util.setCurrentSpace(temp);
		}

	}

	public static java.lang.String formatFieldsOrMethodHeader(
			java.lang.String input, java.lang.String type) {
		if (input != null && input.trim().length() > 0) {

			/*
			 * input=input.trim(); if(input.charAt(0)=='\n')
			 * input=input.substring(1);
			 */

			input = trimString(input);
			java.lang.String formatttedInputStmt = "";
			if (type.indexOf("field") != -1 && type.indexOf("method") == -1)
				formatttedInputStmt += Util.getInitialMethodSpace();
			java.lang.String part1 = "";
			java.lang.String part2 = "";

			int openFlowerBracket = input.indexOf("{");
			int closeFlowerBracket = input.indexOf("}");
			if (openFlowerBracket != -1) {
				formatttedInputStmt += Util.getInitialMethodSpace();
				part1 = input.substring(0, openFlowerBracket);
				part2 = input.substring(openFlowerBracket);
				formatttedInputStmt += part1;
				formatttedInputStmt += Util.getInitialMethodSpace();
				formatttedInputStmt += part2;
				return formatttedInputStmt;
			}
			if (closeFlowerBracket != -1) {
				formatttedInputStmt += Util.getInitialMethodSpace();
				part1 = input.substring(0, closeFlowerBracket);
				part2 = input.substring(closeFlowerBracket);
				formatttedInputStmt += part1;
				if (part1.trim().length() > 0)
					formatttedInputStmt += Util.getInitialMethodSpace();
				formatttedInputStmt += part2;
				return formatttedInputStmt;
			}
			if (type.indexOf("method") != -1 && type.indexOf("field") == -1)
				formatttedInputStmt += Util.getInitialMethodSpace();

			formatttedInputStmt += input;
			return formatttedInputStmt;
		}

		else
			return input;
	}

	public static java.lang.String trimString(String input) {
		if (input == null)
			return input;
		String trimmedString = "";
		boolean skip = false;
		int start = 0;

		if (input.length() > 0) {
			char space = input.charAt(0);
			char tab = input.charAt(0);
			if (space == ' ' || tab == '\t')
				skip = true;

			while (skip) {
				start = start + 1;
				if (input.length() > start) {
					space = input.charAt(start);
					tab = input.charAt(start);
					if (space == ' ' || tab == '\t')
						skip = true;
					else
						skip = false;
				} else
					skip = false;
			}
		}
		trimmedString = input.substring(start);
		return trimmedString;

	}

	// Expects String as a member of Array
	public static ArrayList removeDuplicates(ArrayList input) {
		ArrayList updatedList = new ArrayList();
		if (input != null && input.size() > 0) {
			String[] fullList = (java.lang.String[]) input
					.toArray(new java.lang.String[] {});
			java.util.Arrays.sort(fullList);
			updatedList.add(fullList[0]);
			for (int s = 1; s < fullList.length; s++) {
				if (s < fullList.length) {
					String currentItem = fullList[s];
					currentItem = currentItem.trim();
					int prev = s - 1;
					String temp = "";
					if (prev >= 0 && prev < fullList.length) {
						temp = fullList[prev];
						temp = temp.trim();
						if (temp.equals(currentItem) == false) {
							if (currentItem.indexOf("[") == -1)
								updatedList.add(currentItem);
						}
					}

				}

			}

		}
		return updatedList;

	}

	// sbelur:
	/***************************************************************************
	 * Be careful while using this method. Ensure that the individual objects in
	 * input AraryList implement Comparable Interface This method was written
	 * when unique Integer Objects were required as the earlier function ws only
	 * meant for java.lang.String Objects
	 */

	public static List genericRemoveDuplicates(List input) {
		try {
			ArrayList updatedList = new ArrayList();
			if (input != null && input.size() > 0) {
				Object[] fullList = input.toArray();
				java.util.Arrays.sort(fullList);
				updatedList.add(fullList[0]);
				for (int s = 1; s < fullList.length; s++) {
					if (s < fullList.length) {
						Object currentItem = fullList[s];
						int prev = s - 1;
						Object temp;
						if (prev >= 0 && prev < fullList.length) {
							temp = fullList[prev];

							if (temp.equals(currentItem) == false) {
								updatedList.add(currentItem);
							}
						}

					}

				}

			}
			return updatedList;
		} catch (Throwable t) {

			return input;

		}

	}

	public static Object reInitializeStructure(Object o) {

		if (o instanceof ArrayList) {
			return new ArrayList(); // Just send a empty ArrayList back
		} else {
			// PlaceHolder for others like HashMap etc
			return null;
		}

	}

	static ArrayList parsedSignature = null;

	static ArrayList returnSignature = null;

	static boolean isParameterArray = false;

	public static ArrayList getParsedSignatureAsList() {
		return parsedSignature;
	}

	public static void parseDescriptor(java.lang.String descriptor) {
		ArrayList parameters = new ArrayList();
		parsedSignature = parameters;
		int charIndex = 0;
		java.lang.String parameterString = descriptor.substring(1, descriptor
				.lastIndexOf(")"));
		/*
		 * java.lang.String returnString =
		 * descriptor.substring(descriptor.lastIndexOf(")")+1);
		 * if(returnString.trim().length() > 0){
		 * if(returnString.trim().charAt(0)=='L' ||
		 * returnString.trim().charAt(0)=='[')
		 * this.setReturnTypeAsObjectOrArrayType(true); else
		 * this.setReturnTypeAsObjectOrArrayType(false); }
		 */

		java.lang.String arrString = "";
		// while(charIndex < parameterString.length())
		while (parameterString.length() > 0) {
			if (parameterString.startsWith("L")) {
				java.lang.String objectString = parameterString.substring(0,
						parameterString.indexOf(";"));
				objectString = objectString.replace('/', '.');
				parameters.add(objectString);
				charIndex = charIndex + (objectString.length() + 2);
				parameterString = parameterString.substring(parameterString
						.indexOf(";") + 1);
			} else {
				char parameterChar = parameterString.charAt(0);
				if (parameterChar == '[') {
					arrString = "";
					while (parameterString.startsWith("[")) {
						arrString += "[";
						parameterString = parameterString.substring(1,
								parameterString.length());
						isParameterArray = true;
					}
					java.lang.String objectString = "";
					if (parameterString.charAt(0) == 'L') {
						objectString = parameterString.substring(0,
								parameterString.indexOf(";"));
						parameterString = parameterString
								.substring(parameterString.indexOf(";") + 1);
						objectString = objectString.replace('/', '.');
						parameters.add(arrString + objectString);
						isParameterArray = false;
					}
					/*
					 * while(objectString.startsWith("[")) { arrString += "[";
					 * objectString =
					 * objectString.substring(1,objectString.length()); }
					 * objectString = objectString.substring(1); objectString =
					 * objectString.replace('/','.');
					 * parameters.add(objectString); charIndex = charIndex +
					 * (objectString.length() + 2);
					 * parameters.add(arrString+objectString);
					 */

				} else {
					if (parameterChar == 'I') {
						if (isParameterArray) {
							parameters.add(arrString + "int");
							isParameterArray = false;
						} else {
							parameters.add("int");
						}
					}
					if (parameterChar == 'B') {
						if (isParameterArray) {
							parameters.add(arrString + "byte");
							isParameterArray = false;
						} else {
							parameters.add("byte");
						}
					}
					if (parameterChar == 'C') {
						if (isParameterArray) {
							parameters.add(arrString + "char");
							isParameterArray = false;
						} else {
							parameters.add("char");
						}
					}
					if (parameterChar == 'D') {
						if (isParameterArray) {
							parameters.add(arrString + "double");
							isParameterArray = false;
						} else {
							parameters.add("double");
						}
					}
					if (parameterChar == 'F') {
						if (isParameterArray) {
							parameters.add(arrString + "float");
							isParameterArray = false;
						} else {
							parameters.add("float");
						}
					}
					if (parameterChar == 'J') {
						if (isParameterArray) {
							parameters.add(arrString + "long");
							isParameterArray = false;
						} else {
							parameters.add("long");
						}
					}
					if (parameterChar == 'S') {
						if (isParameterArray) {
							parameters.add(arrString + "short");
							isParameterArray = false;
						} else {
							parameters.add("short");
						}
					}
					if (parameterChar == 'Z') {
						if (isParameterArray) {
							parameters.add(arrString + "boolean");
							isParameterArray = false;
						} else {
							parameters.add("boolean");
						}
					}
					parameterString = parameterString.substring(1);
				}

			}
		}

		/*
		 * if(returnString.indexOf(";") !=-1) { returnString =
		 * returnString.substring(0,returnString.indexOf(";")); }
		 * 
		 * while(returnString.length() > 0) { // System.out.println();
		 * if(returnString.startsWith("L")) { //System.out.println(returnString + "
		 * "+returnType.length()); returnType =
		 * returnString.substring(1,returnString.length()); returnType =
		 * returnType.replace('/','.'); returnString=""; } else {
		 * if(returnString.equals("I")) { returnType = "int"; returnString = ""; }
		 * if(returnString.equals("B")) { returnType = "byte"; returnString =
		 * ""; } if(returnString.equals("C")) { returnType = "char";
		 * returnString = ""; } if(returnString.equals("D")) { returnType =
		 * "double"; returnString = ""; } if(returnString.equals("F")) {
		 * returnType = "float"; returnString = ""; }
		 * if(returnString.equals("J")) { returnType = "long"; returnString =
		 * ""; } if(returnString.equals("S")) { returnType = "short";
		 * returnString = ""; } if(returnString.equals("Z")) { returnType =
		 * "boolean"; returnString = ""; } if(returnString.equals("V")) {
		 * returnType = "void"; returnString = ""; }
		 * if(returnString.startsWith("[")) { returnTypeIsArray = true;
		 * returnTypeArrayDimension = returnString.lastIndexOf("[")+1;
		 * if(returnString.indexOf("L")!=-1) { returnType =
		 * returnString.substring(returnString.lastIndexOf("[")+2); returnType =
		 * returnType.replace('/','.'); returnString = ""; //returnString
		 * =returnType; } else { returnString =
		 * returnString.substring(returnString.lastIndexOf("[")+1);
		 * //returnString = ""; } } } }
		 */

	}

	public static boolean forceNewLine = true;

	public static boolean forceStartSpace = true;

	public static boolean forceBeginStartSpace = true;

	public static boolean newlinebeg = true;

	// belurs:
	// RE Wrote This function as the earlier function was not covering all cases
	// very well
	// The purpose of this function is to take any input and return a formatted
	// output

	public static boolean forceTrimString = true;

	public static boolean forceTrimLines = true;

	public static String formatDecompiledStatement(String input) {

		// if(input.indexOf("[]")!=-1){
		// forceNewLine=false;forceStartSpace=false;};
		// if(input.indexOf("[ ]")!=-1){
		// forceNewLine=false;forceStartSpace=false;}

		String formattedOutput = "";
		java.lang.String temp = "";
		char c;
		char prev = '#'; // default
		char next = '#';
		if (input == null || input.trim().length() == 0) {
			forceNewLine = true;
			forceStartSpace = true;
			return formattedOutput;
		}

		int sqbr = input.indexOf("[]");
		if (sqbr == -1)
			sqbr = input.indexOf("[ ]");
		if (sqbr != -1) {
			int curbr = input.indexOf("{");
			if (curbr != -1 && curbr > sqbr) {
				forceNewLine = true;
				forceStartSpace = true;
				newlinebeg = true;
				if (input.trim().endsWith(",")) {
					return currentSpace + input
							+ System.getProperty("line.separator")
							+ currentSpace;
				} else
					return input.trim();
			}
		}

		if (forceTrimString)
			input = trimString(input);
		if (forceTrimLines)
			input = trimNewLines(input);
		// input=input.replaceAll("\n\n","\n");
		input = input.replaceAll("\t\t", "\t");
		if (forceTrimString)
			input = input.replaceAll("\t", " ");
		if (input.endsWith(";\n;\n")) {
			int d = input.indexOf(";\n;\n");
			if (d != -1) {
				input = input.substring(0, d);
				input += ";\n";
			}
		}
		for (int s = 0; s < input.length(); s++) {

			c = input.charAt(s);
			if (c == '"') {
				int endQuote = input.indexOf("\"", s + 1);
				if (endQuote != -1) {
					int endIndex = endQuote + 1;
					if (endIndex > input.length()) {
						endIndex = endQuote;
					}
					if (endIndex <= input.length()) {
						String quoteString = input.substring(s, endIndex);
						temp += quoteString;
						s = endQuote;
						continue;
					}
				}
			
				
			}

			if (s == 0 && newlinebeg && forceBeginStartSpace)
				temp += currentSpace; // updateCurrentSpace(Util.getIncrementSpace(),"add");
			c = input.charAt(s);
			if (s > 0)
				prev = input.charAt((s - 1));
			if ((s + 1) < input.length())
				next = input.charAt((s + 1));
			if (c == '}') {
				// ObjectStreamClass$FieldReflector
				if (prev != '\n') {
					if (forceNewLine)
						temp += "\n";
					if (forceStartSpace)
						temp += currentSpace;
					updateCurrentSpace(Util.getIncrementSpace(), "remove");

					if (forceStartSpace)
						temp += currentSpace;
					temp += c;
					if (next != '\n' && forceNewLine) {
						temp += "\n";
					}

				} else {
					// updateCurrentSpace(Util.getIncrementSpace(),"remove");
					if (forceStartSpace)
						temp += currentSpace;
					temp += c;
					if (next != '\n' && forceNewLine) {
						temp += "\n";
					}
				}
				int n = temp.indexOf("\n");
				if (n != -1) {
					String s1 = temp.substring(0, n + 1);
					if (n + 3 < temp.length()) {
						temp = temp.substring(1);
						String s2 = temp.substring(n + 3);
						temp = s1 + "  " + s2;

					}
				}

			} else if (c == '{') {
				if (prev == '\n') {
					if (forceStartSpace)
						temp += currentSpace;
					// sbelur
					// Hack ... until a better and new indentation algorithm is
					// designed [ Commented for 1.2.1]
					// if(input.indexOf("{\nbreak;")!=-1)
					// temp+=" ";
					// end
					temp += c;
					updateCurrentSpace(Util.getIncrementSpace(), "add");
					if (next != '\n' && forceNewLine) {
						temp += "\n";
					}
				} else {
					if (forceNewLine)
						temp += "\n";

					if (forceStartSpace && prev != ']')
						temp += currentSpace;
					// temp+=" ";
					temp += c;
					updateCurrentSpace(Util.getIncrementSpace(), "add");
					if (next != '\n' && forceNewLine) {
						temp += "\n";
					}

				}
			} else if (c == '\n') {

				boolean withinQuotes = isNewLineWithinQuotes(input, s);
				if (withinQuotes) {
					temp += "\\n";
				} else {

					if (next == '{') {
						temp += c;
						// temp+=" ";
						if (forceStartSpace)
							temp += currentSpace;
					} else if (next == '}') {
						if (trimString(input).charAt(0) == '\n') {
							temp = c + temp;
						} else
							temp += c;
						updateCurrentSpace(Util.getIncrementSpace(), "remove");
						// updateCurrentSpace(Util.getIncrementSpace(),"remove");
						if (forceStartSpace)
							temp += currentSpace;

					} else {
						if (!(s == input.length() - 1) && c == '\n') {
							temp += c;
							if (forceStartSpace)
								temp += currentSpace;

						} else {
							temp += c;
							// if(forceStartSpace) // Added here also bcoz of
							// indentation issue
							// temp+=currentSpace;
						}

					}
				}

			} else {
				if (c != ' ') {
					boolean withinQuotes = isNewLineWithinQuotes(input, s - 1);
					if (!withinQuotes) {

						if (prev == '\n' && forceStartSpace)
							temp += currentSpace;
					}
					if (s == 0 && newlinebeg && forceBeginStartSpace)
						temp += currentSpace;

				}
				if (s == input.length() - 1 && c == ' ') {

				} else
					temp += c;

			}

		}
		int anysc = temp.indexOf(";");
		int lastsc = temp.lastIndexOf(";");
		if (anysc != lastsc && (anysc + 1 == lastsc)) {
			temp = temp.substring(0, lastsc);

		}
		forceNewLine = true;
		forceStartSpace = true;
		newlinebeg = true;
		return temp;
	}

	// Removes from beginning

	public static java.lang.String trimNewLines(java.lang.String in) {

		if (in == null || in.length() == 0) {
			return in;
		} else {
			int pos = -1;
			for (int s = 0; s < in.length(); s++) {
				char c = in.charAt(s);
				if (c == '\n')
					continue;
				else {
					pos = s;
					break;
				}
			}

			return in.substring(pos);
		}

	}

	public static ArrayList getreturnSignatureAsList() {
		return returnSignature;
	}

	public static void parseReturnType(java.lang.String descriptor) {
		ArrayList parameters = new ArrayList();
		returnSignature = parameters;
		java.lang.String orig = descriptor;
		int charIndex = 0;
		java.lang.String parameterString = descriptor;
		/*
		 * java.lang.String returnString =
		 * descriptor.substring(descriptor.lastIndexOf(")")+1);
		 * if(returnString.trim().length() > 0){
		 * if(returnString.trim().charAt(0)=='L' ||
		 * returnString.trim().charAt(0)=='[')
		 * this.setReturnTypeAsObjectOrArrayType(true); else
		 * this.setReturnTypeAsObjectOrArrayType(false); }
		 */

		java.lang.String arrString = "";
		// while(charIndex < parameterString.length())
		while (parameterString.length() > 0) {
			if (parameterString.startsWith("L")) {
				java.lang.String objectString = parameterString.substring(1,
						parameterString.indexOf(";"));
				objectString = objectString.replace('/', '.');
				parameters.add(objectString);
				charIndex = charIndex + (objectString.length() + 2);
				parameterString = parameterString.substring(parameterString
						.indexOf(";") + 1);
			} else {
				char parameterChar = parameterString.charAt(0);
				if (parameterChar == '[') {
					arrString = "";
					while (parameterString.startsWith("[")) {
						arrString += "[]";
						parameterString = parameterString.substring(1,
								parameterString.length());
						isParameterArray = true;
					}
					java.lang.String objectString = "";
					if (parameterString.charAt(0) == 'L') {
						objectString = parameterString.substring(1,
								parameterString.indexOf(";"));
						parameterString = parameterString
								.substring(parameterString.indexOf(";") + 1);
						objectString = objectString.replace('/', '.');
						parameters.add(objectString + " " + arrString);
						isParameterArray = false;
					}
					/*
					 * while(objectString.startsWith("[")) { arrString += "[";
					 * objectString =
					 * objectString.substring(1,objectString.length()); }
					 * objectString = objectString.substring(1); objectString =
					 * objectString.replace('/','.');
					 * parameters.add(objectString); charIndex = charIndex +
					 * (objectString.length() + 2);
					 * parameters.add(arrString+objectString);
					 */

				} else {
					if (parameterChar == 'V') {
						if (orig.indexOf("L") == -1 || orig.indexOf("[") == -1)
							parameters.add("void");
						else
							parameters.add("V");

					}
					if (parameterChar == 'I') {
						if (isParameterArray) {
							parameters.add("int " + arrString);
							isParameterArray = false;
						} else {
							parameters.add("int");
						}
					}
					if (parameterChar == 'B') {
						if (isParameterArray) {
							parameters.add("byte " + arrString);
							isParameterArray = false;
						} else {
							parameters.add("byte");
						}
					}
					if (parameterChar == 'C') {
						if (isParameterArray) {
							parameters.add("char  " + arrString);
							isParameterArray = false;
						} else {
							parameters.add("char");
						}
					}
					if (parameterChar == 'D') {
						if (isParameterArray) {
							parameters.add("double  " + arrString);
							isParameterArray = false;
						} else {
							parameters.add("double");
						}
					}
					if (parameterChar == 'F') {
						if (isParameterArray) {
							parameters.add("float  " + arrString);
							isParameterArray = false;
						} else {
							parameters.add("float");
						}
					}
					if (parameterChar == 'J') {
						if (isParameterArray) {
							parameters.add("long  " + arrString);
							isParameterArray = false;
						} else {
							parameters.add("long");
						}
					}
					if (parameterChar == 'S') {
						if (isParameterArray) {
							parameters.add("short  " + arrString);
							isParameterArray = false;
						} else {
							parameters.add("short");
						}
					}
					if (parameterChar == 'Z') {
						if (isParameterArray) {
							parameters.add("boolean  " + arrString);
							isParameterArray = false;
						} else {
							parameters.add("boolean");
						}
					}
					parameterString = parameterString.substring(1);
				}

			}
		}

		/*
		 * if(returnString.indexOf(";") !=-1) { returnString =
		 * returnString.substring(0,returnString.indexOf(";")); }
		 * 
		 * while(returnString.length() > 0) { // System.out.println();
		 * if(returnString.startsWith("L")) { //System.out.println(returnString + "
		 * "+returnType.length()); returnType =
		 * returnString.substring(1,returnString.length()); returnType =
		 * returnType.replace('/','.'); returnString=""; } else {
		 * if(returnString.equals("I")) { returnType = "int"; returnString = ""; }
		 * if(returnString.equals("B")) { returnType = "byte"; returnString =
		 * ""; } if(returnString.equals("C")) { returnType = "char";
		 * returnString = ""; } if(returnString.equals("D")) { returnType =
		 * "double"; returnString = ""; } if(returnString.equals("F")) {
		 * returnType = "float"; returnString = ""; }
		 * if(returnString.equals("J")) { returnType = "long"; returnString =
		 * ""; } if(returnString.equals("S")) { returnType = "short";
		 * returnString = ""; } if(returnString.equals("Z")) { returnType =
		 * "boolean"; returnString = ""; } if(returnString.equals("V")) {
		 * returnType = "void"; returnString = ""; }
		 * if(returnString.startsWith("[")) { returnTypeIsArray = true;
		 * returnTypeArrayDimension = returnString.lastIndexOf("[")+1;
		 * if(returnString.indexOf("L")!=-1) { returnType =
		 * returnString.substring(returnString.lastIndexOf("[")+2); returnType =
		 * returnType.replace('/','.'); returnString = ""; //returnString
		 * =returnType; } else { returnString =
		 * returnString.substring(returnString.lastIndexOf("[")+1);
		 * //returnString = ""; } } } }
		 */

	}

	public static java.lang.String checkForImport(java.lang.String input,
			StringBuffer sb) {
		if (input.indexOf("JdecGenerated") != -1) {
			sb.append(input);
			return "";
		}

		// if(input.indexOf(".")==-1 && input.indexOf("/")==-1)
		// {
		// input=input.replaceAll("\\$",".");
		// sb.append(input);
		// return "";
		// }
		if (Configuration.getShowImport().equalsIgnoreCase("false")) {

			sb.append(input);
			return "";
		}
		if (Configuration.getShowImport().equalsIgnoreCase("true")) {
			java.lang.String simplename = "";
			java.lang.String fullName = input;
			int lastSlash = fullName.lastIndexOf("/");
			int dollar = input.lastIndexOf("$");
			java.lang.String sname = null;
			if (lastSlash != -1) {
				if (input.indexOf("$") != -1) {
					// sname=input.substring(dollar+1);
					input = input.replaceAll("\\$", "/");
				}
			} else {
				lastSlash = fullName.lastIndexOf(".");
				if (lastSlash != -1) {
					if (input.indexOf("$") != -1) {
						// sname=input.substring(dollar+1);
						input = input.replaceAll("\\$", ".");
					}
				}
			}

			if (lastSlash != -1) {

				if (sname == null)
					simplename = fullName.substring(lastSlash + 1);
				else
					simplename = sname;
			} else
				simplename = fullName;
			fullName = fullName.replace('/', '.');
			fullName = fullName.replaceAll("\\$", ".");

			StringBuffer fulltype = new StringBuffer();
			boolean present = LocalVariableHelper
					.simpleTypeAlreadyAddedToImports(simplename, fulltype);
			boolean addim = true;
			if (present) {
				if (fullName.trim().equals(fulltype.toString().trim())) {
					addim = true;
				} else {
					addim = false;
				}
			}

			if (addim) {
				ConsoleLauncher.addImportClass(fullName);
				sb.append(simplename);
			} else {
				sb.append(fullName);
			}
			return fullName;

		}
		// Default
		sb.append(input);
		return "";

	}

	public static void checkForImport(java.lang.String input, StringBuffer sb,
			boolean force) {
		// if(input.indexOf("$")!=-1){sb.append(input);return;};
		if (input.indexOf("JdecGenerated") != -1) {
			sb.append(input);

		}
		if (input.indexOf(".") == -1 && input.indexOf("/") == -1) {
			// input=input.replaceAll("\\$",".");
			// sb.append(input);
			// return ;
		}
		if (Configuration.getShowImport().equalsIgnoreCase("false") && !force) {

			sb.append(input);
			return;
		}
		if (Configuration.getShowImport().equalsIgnoreCase("true")) {
			java.lang.String simplename = "";
			java.lang.String fullName = input;
			int lastSlash = fullName.lastIndexOf("/");
			int dollar = input.lastIndexOf("$");
			java.lang.String sname = null;
			if (lastSlash != -1) {
				if (input.indexOf("$") != -1) {
					// sname=input.substring(dollar+1);
					// sname=input.repl
					// sname=input;
					input = input.replaceAll("\\$", "/");

				}
			} else {
				lastSlash = fullName.lastIndexOf(".");
				if (lastSlash != -1) {
					if (input.indexOf("$") != -1) {
						// sname=input.substring(dollar+1);
						// sname=input;
						input = input.replaceAll("\\$", ".");
						// sname=input;
					}
				}
			}

			if (lastSlash != -1) {
				if (sname == null)
					simplename = fullName.substring(lastSlash + 1);
				else
					simplename = sname;
			} else
				simplename = fullName;
			fullName = fullName.replace('/', '.');
			fullName = fullName.replaceAll("\\$", ".");

			StringBuffer fulltype = new StringBuffer();
			boolean present = LocalVariableHelper
					.simpleTypeAlreadyAddedToImports(simplename, fulltype);
			boolean addim = true;
			if (present) {
				if (fullName.trim().equals(fulltype.toString().trim())) {
					addim = true;
				} else {
					addim = false;
				}
			}

			if (addim) {
				ConsoleLauncher.addImportClass(fullName);
				sb.append(simplename);
			} else {
				sb.append(fullName);
			}
			return;

		}

		if (Configuration.getShowImport().equalsIgnoreCase("false") && force) {
			java.lang.String simplename = "";
			java.lang.String fullName = input;
			int lastSlash = fullName.lastIndexOf("/");
			if (lastSlash == -1) {
				lastSlash = fullName.lastIndexOf(".");
			}
			if (lastSlash != -1) {
				simplename = fullName.substring(lastSlash + 1);
			} else
				simplename = fullName;
			fullName = fullName.replace('/', '.');

			StringBuffer fulltype = new StringBuffer();
			boolean present = LocalVariableHelper
					.simpleTypeAlreadyAddedToImports(simplename, fulltype);
			boolean addim = true;
			if (present) {
				if (fullName.trim().equals(fulltype.toString().trim())) {
					addim = true;
				} else {
					addim = false;
				}
			}

			if (addim) {
				ConsoleLauncher.addImportClass(fullName);
				sb.append(simplename);
			} else {
				sb.append(fullName);
			}
			return;

		}
		// Default
		sb.append(input);
		return;

	}

	public static ArrayList getTablesSortedByGuardStart(ArrayList list) {

		ArrayList a = new ArrayList();
		if (list == null || list.size() == 0)
			return null;
		HashMap map = new HashMap();
		for (int z = 0; z < list.size(); z++) {
			MethodInfo.ExceptionTable t = (MethodInfo.ExceptionTable) list
					.get(z);
			map.put(new Integer(t.getStartPC()), t);
		}
		Collection c = map.keySet();
		Integer spcs[] = (Integer[]) c.toArray(new Integer[map.size()]);
		Arrays.sort(spcs);
		for (int z = 0; z < spcs.length; z++) {
			Integer in = spcs[z];
			MethodInfo.ExceptionTable tab = (MethodInfo.ExceptionTable) map
					.get(in);
			a.add(tab);
		}

		return a;

	}

	public static ArrayList getAllTablesWithFinallyAsHandler(ArrayList input) {
		ArrayList a = new ArrayList();
		if (input == null || input.size() == 0) {
			return null;
		}
		for (int z = 0; z < input.size(); z++) {

			MethodInfo.ExceptionTable t = (MethodInfo.ExceptionTable) input
					.get(z);
			java.lang.String str = t.getHandlerBlockName();
			if (str.equals("FinallyBlock")) {
				a.add(t);
			}
		}

		return a;
	}

	public static ArrayList getAllTablesWithFinallyAsHandler(ArrayList input,
			int hpc) {
		ArrayList a = new ArrayList();
		if (input == null || input.size() == 0) {
			return null;
		}
		for (int z = 0; z < input.size(); z++) {

			MethodInfo.ExceptionTable t = (MethodInfo.ExceptionTable) input
					.get(z);
			java.lang.String str = t.getHandlerBlockName();
			if (str.equals("FinallyBlock") && t.getStartOfHandler() == hpc) {
				a.add(t);
			}
		}

		return a;
	}

	// Todo
	public static java.lang.String formatDisassembledOutput(
			java.lang.String input) {

		java.lang.String temp = "";

		if (input.charAt(0) == '\n') {
			if (input.length() > 1) {
				temp = "\n";
				temp += disSpace + input.substring(1);
			}

		} else {
			temp = disSpace + input;
		}
		return temp;

	}

	private static boolean isNewLineWithinQuotes(java.lang.String input, int s) {

		boolean yes = false;
		int start = s - 1;
		int end = 0;
		boolean before = false;
		boolean after = false;
		if (s < 0 || s > input.length())
			return false;
		if (input.charAt(s) != '\n')
			return false;

		for (int k = start; k >= end; k--) {
			char c = input.charAt(k);
			if (c == '"') {
				before = true;
				break;
			}

		}
		start = s + 1;
		end = input.length();
		for (int k = start; k < end; k++) {
			char c = input.charAt(k);
			if (c == '"') {
				after = true;
				break;
			}

		}
		if (before && after) {
			return true;

		}

		return yes;
	}

	public static java.lang.String checkForUTF(char c) {
		if (c >= 127)
			return "UTF";
		else if (c < 32)
			return "octal";
		return "ascii";
	}

	public static java.lang.String interpretCharForUTF(char ch, StringBuffer tp) {
		String res = "" + ch;
		char i;
		String type = "";
		String forcenonascii = UIUtil.getUIUtil().getForceNonAscii();
		if (forcenonascii.equals("true")) {
			type = tp.toString();// checkForUTF(ch);
		} else {
			type = checkForUTF(ch);
		}
		if (type.equals("UTF")) {
			String hexrep = Integer.toHexString(ch);
			int length = hexrep.length();
			int diff = 4 - length;
			String temp = "";
			for (int x = 1; x <= diff; x++) {
				temp += "0";
			}
			res = "\\u" + temp + hexrep;

		} else if (type.equals("octal")) {
			String octrep = Integer.toOctalString(ch);
			int length = octrep.length();
			int diff = 3 - length;
			String temp = "";
			for (int x = 1; x <= diff; x++) {
				temp += "0";
			}

			res = "\\" + temp + octrep;

		} else {
			// No Action for ascii
		}
		return res;
	}

	private char[] keepAsIsChars = {

	};

	public static java.lang.String formatForUTF(String value, String dataType,
			StringBuffer tp) {
		String s = value;
		if (dataType.equals("char") || dataType.equals("String")) {
			String forcenonascii = UIUtil.getUIUtil().getForceNonAscii();
			if (dataType.equals("char")) {
				char c = (char) Integer.parseInt(value);
				java.lang.String charType = "";
				if (forcenonascii.equals("true")) {
					charType = tp.toString();
				} else {
					charType = checkForUTF(c);
				}
				charType = tp.toString();// Util.checkForUTF(c);
				if (charType.equals("UTF")) {

					String hexrep = Integer.toHexString(c);
					int length = hexrep.length();
					int diff = 4 - length;
					String temp = "";
					for (int x = 1; x <= diff; x++) {
						temp += "0";
					}

					s = "\'" + "\\u" + temp + hexrep + "\'";

				} else if (charType.equals("octal")) {
					String octrep = Integer.toOctalString(c);
					int length = octrep.length();
					int diff = 3 - length;
					String temp = "";
					for (int x = 1; x <= diff; x++) {
						temp += "0";
					}
					s = "'\\" + temp + octrep + '\'';
				} else {

					s = value;// Ascii
				}

			} else// String
			{
				StringBuffer sbf = new StringBuffer();
				for (int z = 0; z < value.length(); z++) {
					char ch = value.charAt(z);
					String str = Util.interpretCharForUTF(ch, tp);
					sbf.append(str);
				}
				// s="\""+sbf.toString()+"\"";
				s = sbf.toString();
			}
		}
		return s;
	}

	public static String returnUnInterpretedNonAscii(String value) {
		String str1 = value.toString();
		StringBuffer sbf1 = new StringBuffer("");
		for (int z = 0; z < str1.length(); z++) {
			char c1 = str1.charAt(z);
			if (c1 >= 32 && c1 < 127) {
				sbf1.append(c1);
			} else {
				sbf1.append("?");// c1); // removed ?
			}

		}
		String v1 = sbf1.toString();
		return v1;

	}

	public static ArrayList getRegisteredClasses() {
		return registeredClasses;
	}

	private static ArrayList registeredClasses = null;

	public static java.lang.String explodeJar(File f) {

		if (f == null || !f.exists())
			return "";
		try {
			registeredClasses = new ArrayList();
			JarFile jarFile = new JarFile(f.getAbsolutePath());
			Enumeration e = jarFile.entries();
			String projectTempDir = Configuration.getTempDir();
			if (projectTempDir == null || projectTempDir.trim().length() == 0)
				projectTempDir = System.getProperty("user.dir");

			String name = f.getName();
			if (name.indexOf(".") != -1)
				name = name.substring(0, name.indexOf("."));
			File root = new File(projectTempDir + File.separator + name);
			if (root.exists() == false)
				root.mkdir();

			while (e.hasMoreElements()) {

				ZipEntry entry = (ZipEntry) e.nextElement();
				File currentDir = null;

				if (entry.isDirectory()) {
					currentDir = new File(root.getAbsolutePath()
							+ File.separator + entry.getName());
					if (!currentDir.exists())
						currentDir.mkdirs();

				} else {
					boolean b = checkFilterSetting(entry);
					if (!b)
						continue;
					if (currentDir == null) {

						currentDir = root;
					}
					File currentFile = new File(currentDir + File.separator
							+ entry.getName());
					boolean write = false;
					try {
						currentFile.createNewFile();
						write = true;
						System.out.println("Registered File: "
								+ currentFile.getAbsolutePath());
						writeToFile(currentFile, entry, jarFile);
						registeredClasses.add(currentFile);
						write = false;
					} catch (Exception e2) {

						String path = currentFile.getAbsolutePath();
						int lastSlash = path.lastIndexOf(File.separator);
						String s = path.substring(0, lastSlash);
						File f3 = new File(s);
						if (!f3.exists())
							f3.mkdirs();
						currentFile.createNewFile(); // Should not throw
						// Exception
						if (write) {
							System.out.println("Registered File: "
									+ currentFile.getAbsolutePath());
							writeToFile(currentFile, entry, jarFile);
						}

					}

				}

			}
			return root.getAbsolutePath();
		} catch (Exception e) {

			AllExceptionHandler handler = new AllExceptionHandler(e);
			handler.reportException();
			return "";

		}

	}

	private static boolean checkFilterSetting(ZipEntry entry) {

		Configuration.getInCludedPkgs();

		String name = entry.getName();
		if (entry.isDirectory()) {
			name = name.replaceAll("/", ".");
			ArrayList list = ConsoleLauncher.getInclList();
			if (list == null || list.size() == 0)
				return true;
			if (list.get(0).toString().equals("pkg-all")
					|| list.get(0).toString().equals("All")
					|| list.get(0).toString().equals("[]")
					|| list.get(0).toString().equals("[ ]")) {
				return true;
			}
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				String element = (String) iter.next();
				if (element.equals(name)) {
					return true;
				}
			}
			return false;
		} else {
			if (name.endsWith(".class")) {
				String defaultClz = Configuration.getProcessOnlyDefault();
				int sl = name.indexOf("/");
				if (sl == -1)
					return true;
				if (defaultClz != null && defaultClz.equals("true")) {
					if (sl != -1) {
						return false;
					} else
						return true;
				}
				name = name.substring(0, name.lastIndexOf("/"));
				name = name.replaceAll("/", ".");
				ArrayList list = ConsoleLauncher.getInclList();
				if (list == null || list.size() == 0)
					return true;
				if (list.get(0).toString().equals("pkg-all")
						|| list.get(0).toString().equals("All")
						|| list.get(0).toString().equals("[]")
						|| list.get(0).toString().equals("[ ]")) {
					return true;
				}
				for (Iterator iter = list.iterator(); iter.hasNext();) {
					String element = (String) iter.next();
					if (element.equals(name)) {
						return true;
					}
				}
				return false;

			} else {
				String enclosedArch = Configuration
						.getProcessEnclosedArchives();
				int dot = name.indexOf(".");
				if (dot != -1) {
					String ext = name.substring(dot + 1);
					if (enclosedArch != null
							&& enclosedArch.equalsIgnoreCase("true")) {
						String types = Configuration
								.getRegsiteredArchiveTypes();
						int startbr = types.indexOf("[");
						int endbr = types.indexOf("]");
						if (startbr != -1 && endbr != -1) {
							types = types.substring(startbr + 1, endbr);
							StringTokenizer st = new StringTokenizer(types, ",");
							while (st.hasMoreTokens()) {
								String c = (String) st.nextToken();
								if (c.equalsIgnoreCase(ext)) {
									return true;
								}
							}
							return false;
						}

					} else
						return false;

				}
				return false;

			}
		}

	}

	private static void writeToFile(File currentFile, ZipEntry entry,
			JarFile jarFile) {
		ArrayList bytes = new ArrayList();
		try {
			InputStream is = jarFile.getInputStream(entry);
			OutputStream os = new FileOutputStream(currentFile
					.getAbsolutePath());
			// DataOutputStream dos=new DataOutputStream(os);
			int x = is.read();
			while (x != -1) {
				os.write(x);
				x = is.read();
			}
			os.flush();
			os.close();

		} catch (IOException ioe) {
			try {
				LogWriter lg = LogWriter.getInstance();
				lg
						.writeLog("[ERROR]: Method: writeToFile\n\tClass: UIUtil.class");
				lg.writeLog("------------------------------------------------");
				lg.writeLog("Exception Stack Trace");
				ioe.printStackTrace(lg.getPrintWriter());
				lg.flush();
			} catch (Exception e) {
				//
			}

		}

	}

}
