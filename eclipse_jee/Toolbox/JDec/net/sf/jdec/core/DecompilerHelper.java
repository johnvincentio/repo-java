package net.sf.jdec.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jdec.blockhelpers.IFHelper;
import net.sf.jdec.blockhelpers.LoopHelper;
import net.sf.jdec.blockhelpers.TryHelper;
import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.config.Configuration;
import net.sf.jdec.constantpool.CPString;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.InterfaceMethodRef;
import net.sf.jdec.constantpool.MethodRef;
import net.sf.jdec.io.Writer;
import net.sf.jdec.lookup.FinderFactory;
import net.sf.jdec.lookup.IFinder;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.ExecutionState;
import net.sf.jdec.util.Util;

/*
 *  DecompilerHelper.java Copyright (c) 2006,07 Swaroop Belur
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
public class DecompilerHelper {

	private static final IFinder genericFinder = FinderFactory
			.getFinder(IFinder.BASE);

	/**
	 * @param index
	 *            localvarindex
	 * @param insttype
	 *            pass it as store or load
	 * @return
	 */

	// [NOTE:] THIS METHOD CAN RETURN NULL: SO HANDLE WITH CARE IN CALLING
	// METHOD....[belurs]
	public static LocalVariable getLocalVariable(int index,
			java.lang.String insttype, java.lang.String dataType,
			boolean simpleLoadStore, int instpos) {
		Behaviour behaviour = ExecutionState.getMethodContext();
		ClassDescription cd = behaviour.getClassRef().getCd();
		if (index < 0) {
			index += 256;
		}
		LocalVariable l = null;
		LocalVariableStructure structure = behaviour.getLocalVariables();
		if (cd.isClassCompiledWithMinusG()) {
			if (structure != null)// Just a double check.. Need not check
			// actually
			{
				int rangeIndex = -1;
				if (insttype.equals("store")) {

					if (simpleLoadStore == true)
						rangeIndex = instpos + 1;
					else
						rangeIndex = instpos + 2;
					LocalVariable var = structure.getVariabelAtIndex(index,
							rangeIndex);
					if (var == null) {
						Object o = cd.getMethod_name_storeNLoad_Map().get(
								behaviour.getBehaviourName().concat(
										behaviour.getStringifiedParameters()));
						if (o instanceof Hashtable) {
							Hashtable h = (Hashtable) o;
							if (h != null && h.size() > 0) {
								Integer il = (Integer) h.get(new Integer(
										instpos));
								if (il != null) {
									int loadpos = il.intValue();
									var = structure.getVariabelAtIndex(index,
											loadpos);
								}
							}
						}
					}
					if (var == null) {
						// Create a veriable here
						// This probably indicates the variables is unused in
						// code
						// TODO Fix Required over here

						var = new LocalVariable(behaviour.getBehaviourName()
								.concat(behaviour.getStringifiedParameters()),
								"Var_" + index, index);
						boolean duplicateVarName = isLocalVariableWithNameAlreadyPresent(
								"jdec_var_" + index, behaviour
										.getLocalVariables()
										.getMethodLocalVaribales());
						if (!duplicateVarName)
							var.setDeclarationGenerated(false);
						else
							var.setDeclarationGenerated(true);
						var.setDataType(dataType);
						var.setWasCreated(true);
						structure.addLocalVariable(var);

					}
					return var;

				} else // This is for load
				{
					LocalVariable var = structure.getVariabelAtIndex(index,
							instpos);
					if (var == null) {
						// NOT Sure what to do here// SHOULD NEVER COME
						// HERE.....
						// Possible due to a finally block
						try {
							Writer wr = Writer.getWriter("log");
							wr
									.writeLog("Could not obtain local variable While decompiling "
											+ behaviour
													.getBehaviourName()
													.concat(
															behaviour
																	.getStringifiedParameters()));
							wr.writeLog("\nDetails.......");
							wr.writeLog("\n[Index Pos " + index
									+ ",Instruction Pos " + instpos
									+ " INSTRUCTION CODE: "
									+ behaviour.getCode()[instpos] + "]\n");
							wr.flush();
						} catch (Exception ex) {
						}

					}
					return var;

				}

			} else
				return null;
		} else {
			ConsoleLauncher.setCurrentClassCompiledWithDebugInfo(false);
			LocalVariable toreturn = null;
			if (behaviour.getLocalVariables() == null) // Again shud not
			// happen...
			{
				java.lang.String methodName = behaviour.getBehaviourName();
				structure = new LocalVariableStructure();
				behaviour.setMethodLocalVariables(structure);
				structure.setMethodDescription(methodName.concat(behaviour
						.getStringifiedParameters()));
				LocalVariableTable localVarTable = LocalVariableTable
						.getInstance();
				localVarTable.addEntry(methodName.concat(behaviour
						.getStringifiedParameters().concat(
								"" + behaviour.isMethodConstructor())),
						structure);
			}
			l = structure.getVariabelAtIndex(index, dataType, behaviour
					.getDatatypesForParams());

			LocalVariable tmp = null;
			if (l == null) // Create and Add
			{
				java.lang.String variableName = "Var" + "_" + instpos + "_"
						+ index;
				if ((behaviour.getUserFriendlyMethodAccessors().indexOf(
						"static") == -1 && !behaviour.getBehaviourName().trim()
						.equals("static"))
						&& (index == 0))
					variableName = "this";
				l = new LocalVariable(behaviour.getBehaviourName().concat(
						behaviour.getStringifiedParameters()), variableName,
						index);
				l.setDeclarationGenerated(false);
				l.setDataType(dataType);
				l.setWasCreated(true);
				structure.addLocalVariable(l);
				toreturn = l;
				l.setPassedDataTypeWhileCreatingWithOutMinusG(dataType);
				
			} else {
				
				if (structure.getNumberOfSimilarIndexVars(index) > 1) {
					Object o = cd.getMethod_name_storeNLoad_Map().get(
							behaviour.getBehaviourName().concat(
									behaviour.getStringifiedParameters()));
					if (o instanceof Hashtable) {
						Hashtable h = (Hashtable) o;
						if (h != null && h.size() > 0) {
							Integer il = (Integer) h.get(new Integer(instpos));
							if (il != null) {
								int loadpos = il.intValue();
								tmp = structure.getVariableForLoadOrStorePos(
										index, loadpos);
							}
						}
					}

				}

				if (tmp == null)
					toreturn = l;
				else
					toreturn = tmp;

			}

			java.lang.String dt = getStoredDataType(toreturn.getIndexPos(),
					behaviour.getDatatypesForParams());
			if (dt != null && dt.trim().length() != 0)
				toreturn.setDataType(dt.trim());
			if (behaviour.getUserFriendlyMethodAccessors().indexOf("static") == -1
					&& !behaviour.getBehaviourName().trim().equals("static")
					&& (toreturn.getIndexPos() == 0)) {
				if (toreturn.getVarName().equals("this") == false) {
					toreturn.setVarName("this");
				}
			}
			return toreturn;

		}

	}

	private static boolean isLocalVariableWithNameAlreadyPresent(
			java.lang.String name, ArrayList list) {
		boolean b = false;
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			LocalVariable element = (LocalVariable) iter.next();
			if (element.getVarName().equals(name)) {
				b = true;
				break;
			}
		}
		return b;
	}

	private static java.lang.String getStoredDataType(int index,
			Map datatypesForParams) {
		java.lang.String dt = "";
		if (datatypesForParams != null && datatypesForParams.size() > 0) {
			return (java.lang.String) datatypesForParams
					.get(new Integer(index));
		}
		return dt;

	}

	private static boolean AmIPrimitive(java.lang.String className) {
		if (className.equals("I") || className.equals("B")
				|| className.equals("C") || className.equals("S")
				|| className.equals("F") || className.equals("D")
				|| className.equals("J") || className.equals("Z")) {
			return true;
		} else
			return false;

	}

	// Copied from LocalVariable Class
	public static java.lang.String parse(java.lang.String input) {
		java.lang.String type = "";
		if (input.equals("I")) {
			type = "int";
		} else if (input.equals("B")) {
			type = "byte";
		} else if (input.equals("C")) {
			type = "char";
		} else if (input.equals("S")) {
			type = "short";
		} else if (input.equals("Z")) {
			type = "boolean";
		} else if (input.equals("F")) {
			type = "float";
		} else if (input.equals("D")) {
			type = "double";
		} else if (input.equals("J")) {
			type = "long";
		} else if (input.startsWith("L")) {
			type = input.substring(1);
			if (type.indexOf(";") != -1)
				type = type.substring(0, type.indexOf(";"));
		} else if (input.startsWith("[")) {
			int lastBracket = input.lastIndexOf("[");
			int objectType = input.indexOf("L");
			java.lang.String className = "";
			if (objectType != -1)
				className = input.substring(objectType + 1);
			else
				className = input.substring(lastBracket + 1);
			if (className.indexOf(";") != -1)
				className = className.substring(0, className.indexOf(";"));
			boolean b = AmIPrimitive(className);
			if (b == true)
				type = parse(className);
			else {
				java.lang.String temp = "";
				for (int c = 0; c < lastBracket + 1; c++)
					temp += "[]";
				type = className + " " + temp;
			}

		} else {
			type = input;
		}

		return type;

	}

	public static boolean isInstDup(byte[] info, int cur) {
		return genericFinder.isInstDup(cur);
	}

	public static boolean isThisInstrStart(int pos) {
		return genericFinder.isThisInstrStart(pos);

	}

	public static boolean isAnewArrayEmbedded(int currentForIndex, byte[] info) {

		int prev = currentForIndex - 1;
		boolean b = false;
		int prevPos = getPrevStartCodePos(currentForIndex);
		if (prevPos < 0)
			return b;
		boolean cont = false;
		switch (info[prevPos]) {
		case JvmOpCodes.ICONST_0:
		case JvmOpCodes.ICONST_1:
		case JvmOpCodes.ICONST_2:
		case JvmOpCodes.ICONST_3:
		case JvmOpCodes.ICONST_4:
		case JvmOpCodes.ICONST_5:
		case JvmOpCodes.BIPUSH:
		case JvmOpCodes.SIPUSH:
			cont = true;
			break;
		}
		if (!cont)
			return b;
		cont = false;
		prevPos = getPrevStartCodePos(prevPos);
		if (prevPos < 0)
			return b;
		switch (info[prevPos]) {
		case JvmOpCodes.ICONST_0:
		case JvmOpCodes.ICONST_1:
		case JvmOpCodes.ICONST_2:
		case JvmOpCodes.ICONST_3:
		case JvmOpCodes.ICONST_4:
		case JvmOpCodes.ICONST_5:
		case JvmOpCodes.BIPUSH:
		case JvmOpCodes.SIPUSH:
			cont = true;
			break;
		}
		if (!cont)
			return b;
		prevPos = getPrevStartCodePos(prevPos);
		if (prevPos < 0)
			return b;
		for (int z = prevPos; z >= 0; z--) {

			if (!isThisInstrStart(z))
				continue;
			switch (info[z]) {

			case JvmOpCodes.ASTORE:
			case JvmOpCodes.ASTORE_0:
			case JvmOpCodes.ASTORE_1:
			case JvmOpCodes.ASTORE_2:
			case JvmOpCodes.ASTORE_3:
				return b;
			case JvmOpCodes.ANEWARRAY:
				return !b;

			}

		}
		return b;

	}

	// Duplicate Method
	private static int getPrevStartCodePos(int i) {
		int current = i;
		int z;
		for (z = current - 1; z >= 0; z--) {

			boolean ok = isThisInstrStart(z);
			if (ok) {
				return z;
			}
		}
		return z;

	}

	// example String s[]={"1","2"}
	public static boolean getArrayDimensionForAnewArrayCase(int current) {

		Behaviour behaviour = ExecutionState.getMethodContext();
		byte[] code = behaviour.getCode();
		ArrayList starts = behaviour.getInstructionStartPositions();
		int prev = getPrevStartCodePos(current);
		int size = -1;
		if (prev >= 0) {

			switch (code[prev]) {
			case JvmOpCodes.ICONST_0:
				size = 0;
				break;
			case JvmOpCodes.ICONST_1:
				size = 1;
				break;
			case JvmOpCodes.ICONST_2:
				size = 2;
				break;
			case JvmOpCodes.ICONST_3:
				size = 3;
				break;
			case JvmOpCodes.ICONST_4:
				size = 4;
				break;
			case JvmOpCodes.ICONST_5:
				size = 5;
				break;

			case JvmOpCodes.BIPUSH:
				size = code[(prev + 1)];
				break;
			case JvmOpCodes.SIPUSH:
				size = getGenericFinder().getOffset(prev);
				break;

			}

			if (size != -1) {

				if (isThisInstrStart((current + 3))) {

					int x = current + 3;
					if (code[x] == JvmOpCodes.DUP_X2
							|| code[x] == JvmOpCodes.DUP
							|| code[x] == JvmOpCodes.DUP2
							|| code[x] == JvmOpCodes.DUP_X1
							|| code[x] == JvmOpCodes.DUP2_X1
							|| code[x] == JvmOpCodes.DUP2_X2) {

						return true;

					}
				}

			}

		}

		return false;

	}

	private static IFinder getGenericFinder() {
		return genericFinder;
	}

	private static IFinder getStoreFinder() {
		return FinderFactory.getFinder(IFinder.STORE);
	}

	public static boolean checkForPostIncrForLoadCase(byte[] info, int current,
			java.lang.String type, boolean insttype, int index,
			StringBuffer addsub) {
		boolean b = false;
		int next = -1;
		int[] skipsWRTPostIncr = GlobalVariableStore.getSkipsWRTPostIncr();
		int instPos = -1;
		int startFrom = -1;
		if (type.equals("category1")) {
			if (insttype) {
				next = current + 1 + 1;
				startFrom = next;
			} else {
				next = current + 1;
				startFrom = next;
			}
			if (info[next] == JvmOpCodes.DUP) {
				next = next + 1;
				if ((info[next] == JvmOpCodes.ICONST_1)
						|| (info[next] == JvmOpCodes.FCONST_1)) {
					next = next + 1;
					if ((info[next] == JvmOpCodes.ISUB)
							|| (info[next] == JvmOpCodes.FSUB)
							|| (info[next] == JvmOpCodes.IADD)
							|| (info[next] == JvmOpCodes.FADD)) {
						StringBuffer varindex = new StringBuffer("");
						boolean ok = getStoreFinder()
								.isNextInstructionPrimitiveStoreInst(next + 1,
										varindex);
						if (ok) {
							try {
								int x = Integer.parseInt(varindex.toString());
								if (x == index) {
									b = true;
									instPos = next + 1;
									if ((info[next] == JvmOpCodes.ISUB)
											|| (info[next] == JvmOpCodes.FSUB)) {
										addsub.append("--");
									}
									if ((info[next] == JvmOpCodes.IADD)
											|| (info[next] == JvmOpCodes.FADD)) {
										addsub.append("++");
									}
								}
							} catch (Exception exp) {
								b = false;
							}
						}
					}

				}

			}

		} else if (type.equals("category2")) {
			if (insttype) {
				next = current + 1 + 1;
				startFrom = next;
			} else {
				next = current + 1;
				startFrom = next;
			}
			if (info[next] == JvmOpCodes.DUP2) {
				next = next + 1;
				if ((info[next] == JvmOpCodes.LCONST_1)
						|| (info[next] == JvmOpCodes.DCONST_1)) {
					next = next + 1;
					if ((info[next] == JvmOpCodes.LSUB)
							|| (info[next] == JvmOpCodes.DSUB)
							|| (info[next] == JvmOpCodes.LADD)
							|| (info[next] == JvmOpCodes.DADD)) {
						StringBuffer varindex = new StringBuffer("");
						boolean ok = getStoreFinder()
								.isNextInstructionPrimitiveStoreInst(next + 1,
										varindex);
						if (ok) {
							try {
								int x = Integer.parseInt(varindex.toString());
								if (x == index) {
									b = true;
									instPos = next + 1;
									if ((info[next] == JvmOpCodes.LSUB)
											|| (info[next] == JvmOpCodes.DSUB)) {
										addsub.append("--");
									}
									if ((info[next] == JvmOpCodes.LADD)
											|| (info[next] == JvmOpCodes.DADD)) {
										addsub.append("++");
									}
								}
							} catch (Exception exp) {
								b = false;
							}
						}
					}

				}

			}
		}

		if (instPos != -1 && startFrom != -1) {
			int diff = (instPos - startFrom);
			skipsWRTPostIncr = new int[diff];
			int s = startFrom + 1;
			for (int i = 0; i < skipsWRTPostIncr.length; i++) {
				skipsWRTPostIncr[i] = s;
				s++;
			}

		}
		GlobalVariableStore.setSkipsWRTPostIncr(skipsWRTPostIncr);
		return b;
	}

	public static void checkForImport(java.lang.String input, StringBuffer sb) {

		if (input.indexOf("(") != -1) {
			sb.append(input);
			return;
		}
	
		if (input.indexOf(")") != -1) {
			sb.append(input);
			return;
		}
	
		if (input.indexOf("JdecGenerated") != -1) {
			sb.append(input);
			return;
		}

		// if(input.indexOf("$")!=-1){sb.append(input);return;};
		if (input.indexOf(".") == -1 && input.indexOf("/") == -1) {
			input = input.replaceAll("\\$", ".");
			sb.append(input);
			return;
		}
		if (Configuration.getShowImport().equalsIgnoreCase("false")) {

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
					sname = input.substring(dollar + 1);
					input = input.replaceAll("\\$", "/");
				}
			} else {
				lastSlash = fullName.lastIndexOf(".");
				if (lastSlash != -1) {
					if (input.indexOf("$") != -1) {
						sname = input.substring(dollar + 1);
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
				if(fullName.trim().equals(fulltype.toString().trim())){
					addim = true;
				}
				else{
					addim = false;
				}
			}
			if (addim) {
				ConsoleLauncher.addImportClass(fullName);
				sb.append(simplename);
			}
			else{
				sb.append(fullName);
			}
			return;

		}
		// Default
		sb.append(input);
		return;

	}

	public static boolean checkForStartOfCatch(int instructionPos,
			ArrayList methodTries) {
		return TryHelper.checkForStartOfCatch(instructionPos, methodTries);
	}

	public static class VariableAtFront {
		java.lang.String name;

		java.lang.String type;

		java.lang.String initial;

		public VariableAtFront(java.lang.String s1, java.lang.String s2,
				java.lang.String s3) {
			name = s1;
			type = s2;
			initial = s3;
		}

		public java.lang.String getInitial() {
			if (initial != null) {
				return initial;
			}

			if (type != null && type.equals("int")) {
				return "0";
			}
			if (type != null && type.equals("boolean")) {
				return "false";
			}
			if (type != null && type.equals("float")) {
				return "0f";
			}
			if (type != null && type.equals("double")) {
				return "0d";
			}
			if (type != null && type.equals("long")) {
				return "0L";
			}
			if (type != null && type.equals("byte")) {
				return "0";
			}
			if (type != null && type.equals("short")) {
				return "0";
			}
			if (type != null && type.equals("char")) {
				return "(char)0";
			}
			return initial;
		}

		public java.lang.String getName() {
			return name;
		}

		public java.lang.String getType() {
			return type;
		}

	}

	public static VariableAtFront newVariableAtFront(java.lang.String name,
			java.lang.String type, java.lang.String initial) {

		return new VariableAtFront(name, type, initial);

	}

	public static boolean anydupstoreinternarybesidesthis(int current,
			byte[] info) {
		boolean yes = false;
		IFBlock iF = null;// getParentIFInTernaryList();
		if (iF == null)
			return false;
		int x = iF.getIfStart() + 3;
		while (x < current - 1) {
			if (getGenericFinder().isThisInstrStart(x)) {
				boolean dup = false;
				if (info[x] == JvmOpCodes.DUP || info[x] == JvmOpCodes.DUP2) {
					int next = x + 1;
					if (getStoreFinder().isStoreInst(next,
							new StringBuffer(""), new StringBuffer(""))) {
						return true;
					}
				}
			}
			x++;

		}

		return yes;

	}

	private static Behaviour getContext() {
		return ExecutionState.getMethodContext();
	}

	public static boolean isTernaryCondition(int i, byte[] info) {
		return false;
	}

	public static boolean addATHROWOutput(int i) {
		if (GlobalVariableStore.getAthrowmap().size() == 0)
			return true;
		Set entries = GlobalVariableStore.getAthrowmap().entrySet();
		Iterator it = entries.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Integer pos = (Integer) entry.getKey();
			if (pos.intValue() == i) {
				java.lang.String str = (java.lang.String) entry.getValue();
				if (str.equals("true")) {
					return false;
				}
			}
		}

		return true;

	}

	public static boolean checkForArrayPostIncrement(int current,
			OperandStack stack, byte[] code, java.lang.String type,
			StringBuffer addtype) {
		boolean b = false;

		if (type.equals("category1")) {

			boolean proceed = checkAheadForArrayStore(current, code);
			if (proceed) {
				proceed = checkBeforeForAddSubInstruction(current, code,
						"category1", addtype);
				if (proceed) {
					return !b;
				} else {
					return b;
				}
			} else {
				return b;
			}

		} else if (type.equals("category2")) {
			boolean proceed = checkAheadForArrayStore(current, code);
			if (proceed) {
				proceed = checkBeforeForAddSubInstruction(current, code,
						"category2", addtype);
				if (proceed) {
					return !b;
				} else {
					return b;
				}
			} else {
				return b;
			}
		} else
			b = false;
		return b;

	}

	private static boolean checkAheadForArrayStore(int current, byte[] code) {

		boolean b = false;
		int pos = current + 1;
		boolean exit = false;
		boolean found = false;
		for (; pos < code.length; pos++) {

			boolean start = isThisInstrStart(pos);
			if (!start)
				continue;
			switch (code[pos]) {
			case JvmOpCodes.I2B:
			case JvmOpCodes.I2C:
			case JvmOpCodes.I2D:
			case JvmOpCodes.I2F:
			case JvmOpCodes.I2L:
			case JvmOpCodes.I2S:
			case JvmOpCodes.L2D:
			case JvmOpCodes.L2F:
			case JvmOpCodes.L2I:
			case JvmOpCodes.F2D:
			case JvmOpCodes.F2I:
			case JvmOpCodes.F2L:
			case JvmOpCodes.D2F:
			case JvmOpCodes.D2I:
			case JvmOpCodes.D2L: {
				exit = false;
				break;
			}

			case JvmOpCodes.DUP_X2:
			case JvmOpCodes.DUP2_X2: {
				exit = false;
				break;
			}

			case JvmOpCodes.IASTORE:
			case JvmOpCodes.BASTORE:
			case JvmOpCodes.CASTORE:
			case JvmOpCodes.SASTORE:
			case JvmOpCodes.FASTORE:
			case JvmOpCodes.LASTORE:
			case JvmOpCodes.DASTORE: {
				exit = true;
				found = true;
				break;
			}

			default: {
				exit = true;
				break;
			}

			}
			if (exit && !found) {
				b = false;
				break;
			}
			if (exit && found) {
				b = true;
				break;
			}

		}
		return b;
	}

	private static boolean checkBeforeForAddSubInstruction(int current,
			byte[] code, java.lang.String type, StringBuffer addtype) {
		boolean b = false;
		int pos = current - 1;
		boolean exit = false;
		boolean found = false;
		ArrayList inststarts = getContext().getInstructionStartPositions();
		if (type.equals("category2")) {
			for (; pos >= 0; pos--) {
				boolean start = isThisInstrStart(pos);
				if (!start)
					continue;
				switch (code[pos]) {
				case JvmOpCodes.I2B:
				case JvmOpCodes.I2C:
				case JvmOpCodes.I2D:
				case JvmOpCodes.I2F:
				case JvmOpCodes.I2L:
				case JvmOpCodes.I2S:
				case JvmOpCodes.L2D:
				case JvmOpCodes.L2F:
				case JvmOpCodes.L2I:
				case JvmOpCodes.F2D:
				case JvmOpCodes.F2I:
				case JvmOpCodes.F2L:
				case JvmOpCodes.D2F:
				case JvmOpCodes.D2I:
				case JvmOpCodes.D2L: {
					exit = false;
					break;
				}

				case JvmOpCodes.DUP_X2:
				case JvmOpCodes.DUP2_X2: {
					exit = true;
					break;
				}

				case JvmOpCodes.DADD:
				case JvmOpCodes.LADD: {
					exit = true;
					found = true;
					addtype.append("add");
					break;
				}
				case JvmOpCodes.DSUB:
				case JvmOpCodes.LSUB: {
					exit = true;
					found = true;
					addtype.append("sub");
					break;
				}

				default: {
					exit = true;
					break;
				}

				}
				if (exit && !found) {
					b = false;
					break;
				}
				if (exit && found) {
					b = true;
					break;
				}

			}
		} else if (type.equals("category1")) {
			for (; pos >= 0; pos--) {
				boolean start = isThisInstrStart(pos);
				if (!start)
					continue;
				switch (code[pos]) {
				case JvmOpCodes.I2B:
				case JvmOpCodes.I2C:
				case JvmOpCodes.I2D:
				case JvmOpCodes.I2F:
				case JvmOpCodes.I2L:
				case JvmOpCodes.I2S:
				case JvmOpCodes.L2D:
				case JvmOpCodes.L2F:
				case JvmOpCodes.L2I:
				case JvmOpCodes.F2D:
				case JvmOpCodes.F2I:
				case JvmOpCodes.F2L:
				case JvmOpCodes.D2F:
				case JvmOpCodes.D2I:
				case JvmOpCodes.D2L: {
					exit = false;
					break;
				}

				case JvmOpCodes.DUP_X2:
				case JvmOpCodes.DUP2_X2: {
					exit = true;
					break;
				}

				case JvmOpCodes.IADD:
				case JvmOpCodes.FADD: {
					exit = true;
					found = true;
					addtype.append("add");
					break;
				}
				case JvmOpCodes.ISUB:
				case JvmOpCodes.FSUB: {
					exit = true;
					found = true;
					addtype.append("sub");
					break;
				}

				default: {
					exit = true;
					break;
				}

				}
				if (exit && !found) {
					b = false;
					break;
				}
				if (exit && found) {
					b = true;
					break;
				}

			}

		}

		else

			b = false;

		return b;
	}

	public static boolean checkForArrayMultiAssignablePostIncrement(
			int current, OperandStack stack, byte[] code, java.lang.String type) {
		boolean b = false;
		if (type.equals("category1")) {

			boolean proceed = checkAheadForArrayStore(current, code);
			if (proceed) {
				proceed = checkBeforeForAddSubInstruction_Multi(current, code,
						"category1");
				if (proceed) {
					return !b;
				} else {
					return b;
				}
			} else {
				return b;
			}

		} else if (type.equals("category2")) {
			boolean proceed = checkAheadForArrayStore(current, code);
			if (proceed) {
				proceed = checkBeforeForAddSubInstruction_Multi(current, code,
						"category2");
				if (proceed) {
					return !b;
				} else {
					return b;
				}
			} else {
				return b;
			}
		} else
			b = false;
		return b;

	}

	// TODO: Refactor Method
	// Also Add more logic
	public static boolean checkBeforeForAddSubInstruction_Multi(int current,
			byte[] code, java.lang.String type) {
		boolean b = false;
		int pos = current - 1;
		boolean exit = false;
		boolean found = false;
		ArrayList inststarts = getContext().getInstructionStartPositions();
		if (type.equals("category2")) {
			for (; pos >= 0; pos--) {
				boolean start = isThisInstrStart(pos);
				if (!start)
					continue;
				switch (code[pos]) {
				case JvmOpCodes.I2B:
				case JvmOpCodes.I2C:
				case JvmOpCodes.I2D:
				case JvmOpCodes.I2F:
				case JvmOpCodes.I2L:
				case JvmOpCodes.I2S:
				case JvmOpCodes.L2D:
				case JvmOpCodes.L2F:
				case JvmOpCodes.L2I:
				case JvmOpCodes.F2D:
				case JvmOpCodes.F2I:
				case JvmOpCodes.F2L:
				case JvmOpCodes.D2F:
				case JvmOpCodes.D2I:
				case JvmOpCodes.D2L: {
					exit = false;
					break;
				}

				case JvmOpCodes.DUP_X2:
				case JvmOpCodes.DUP2_X2: {
					boolean ok = false;
					floop: for (int cur = pos - 1; cur >= 0; cur--) {
						start = isThisInstrStart(cur);
						if (start == false)
							continue;
						int inst = code[cur];
						switch (inst) {
						case JvmOpCodes.IASTORE:
						case JvmOpCodes.BASTORE:
						case JvmOpCodes.CASTORE:
						case JvmOpCodes.SASTORE:
						case JvmOpCodes.FASTORE:
						case JvmOpCodes.LASTORE:
						case JvmOpCodes.DASTORE: {
							ok = true;
							break floop;
						}
						}
						boolean b3 = getGenericFinder().isCategory2AddSub(pos);
						if (b3) {
							ok = false;
							break;
						}

					}
					exit = true;
					if (ok) {
						found = true;
					} else {
						found = false;
					}
					break;
				}

				default: {
					exit = true;
					break;
				}

				}
				if (exit && !found) {
					b = false;
					break;
				}
				if (exit && found) {
					b = true;
					break;
				}

			}
		} else if (type.equals("category1")) {
			for (; pos >= 0; pos--) {
				boolean start = isThisInstrStart(pos);
				if (!start)
					continue;
				switch (code[pos]) {
				case JvmOpCodes.I2B:
				case JvmOpCodes.I2C:
				case JvmOpCodes.I2D:
				case JvmOpCodes.I2F:
				case JvmOpCodes.I2L:
				case JvmOpCodes.I2S:
				case JvmOpCodes.L2D:
				case JvmOpCodes.L2F:
				case JvmOpCodes.L2I:
				case JvmOpCodes.F2D:
				case JvmOpCodes.F2I:
				case JvmOpCodes.F2L:
				case JvmOpCodes.D2F:
				case JvmOpCodes.D2I:
				case JvmOpCodes.D2L: {
					exit = false;
					break;
				}

				case JvmOpCodes.DUP_X2:
				case JvmOpCodes.DUP2_X2: {
					boolean ok = false;
					floop: for (int cur = pos - 1; cur >= 0; cur--) {
						start = isThisInstrStart(cur);
						if (start == false)
							continue;
						int inst = code[cur];
						switch (inst) {
						case JvmOpCodes.IASTORE:
						case JvmOpCodes.BASTORE:
						case JvmOpCodes.CASTORE:
						case JvmOpCodes.SASTORE:
						case JvmOpCodes.FASTORE:
						case JvmOpCodes.LASTORE:
						case JvmOpCodes.DASTORE: {
							ok = true;
							break floop;
						}
						}
						boolean b3 = getGenericFinder().isCategory1AddSub(pos);
						if (b3) {
							ok = false;
							break;
						}

					}

					exit = true;
					if (ok) {
						found = true;
					} else {
						found = false;
					}
					break;
				}

				default: {
					exit = true;
					break;
				}

				}
				if (exit && !found) {
					b = false;
					break;
				}
				if (exit && found) {
					b = true;
					break;
				}

			}

		}

		else

			b = false;

		return b;
	}

	// TODO: find where all this is called
	// send back a string abt cast like (byte) etc
	// and prepend to that String
	public static boolean isArrayElement(int i) {

		byte info[] = getContext().getCode();
		int current = i - 1;
		boolean dontadd = false;
		boolean b = isThisInstrStart(i);
		if (!b)
			return false;
		for (int z = i; z < info.length; z++) {
			b = isThisInstrStart(z);
			if (!b)
				return false;
			/*
			 * switch(info[z]){ case JvmOpCodes.DUP: case JvmOpCodes.DUP2: case
			 * JvmOpCodes.DUP_X1: case JvmOpCodes.DUP_X2: case
			 * JvmOpCodes.DUP2_X1: case JvmOpCodes.DUP2_X2: return false; }
			 */
			switch (info[z]) {

			case JvmOpCodes.IASTORE:
			case JvmOpCodes.BASTORE:
			case JvmOpCodes.SASTORE:
			case JvmOpCodes.CASTORE:
			case JvmOpCodes.FASTORE:
			case JvmOpCodes.LASTORE:
			case JvmOpCodes.DASTORE: {
				int prevpos1 = z - 1;
				int prevpos2 = z - 2;
				if (isThisInstrStart(prevpos1)
						&& getStoreFinder().isInstPrimitiveArrayStore(
								prevpos1) && isThisInstrStart(prevpos2)
						&& getGenericFinder().isInstructionAnyDUP(prevpos2)) {
					// return false;
				}
				if (!dontadd) {
					GlobalVariableStore.getSkipPrimitiveArrayStores().add(
							new Integer(z));
				}
				return true;

			}

			}

			int j = getGenericFinder().isNextInstructionConversionInst(z,
					new StringBuffer());
			if (j != -1) {
				continue;
			} else {
				if (isThisInstrStart(z)
						&& getGenericFinder().isInstructionAnyDUP(z)) {
					dontadd = true;
					continue;
				}
				break;
			}

		}
		return false;

	}

	// Primarily use it for goto and some special cases where appplicable
	public static int getJumpAddress(byte[] info, int counter) {
		return genericFinder.getJumpAddress(counter);
	}

	/*
	 * public static int getIfCloseNumberForThisIF(byte[] info, int k) { return
	 * IFHelper.getIfCloseNumberForThisIF(info, k); }
	 */

	public static int findCodeIndexFromInfiniteLoop(IFBlock ifst,
			ArrayList loopTable, int codeIndex) {
		return LoopHelper.findCodeIndexFromInfiniteLoop(ifst, loopTable,
				codeIndex);
	}

	public static int getloopEndForStart(ArrayList list, int start) {

		return LoopHelper.getloopEndForStart(list, start);

	}

	public static boolean isByteCodeALoopStart(ArrayList loops, int bytecodeend) {

		return LoopHelper.isPositionALoopStart(loops, bytecodeend);
	}

	public static int checkLoopsAndSwitchForIfEnd(int end, IFBlock ifs,
			Behaviour behaviour) {
		byte[] info = behaviour.getCode();
		int reqdEnd = -1;
		ArrayList loops = behaviour.getBehaviourLoops();
		if (loops != null && loops.size() > 0) {

			Object[] sortedLoops = LoopHelper.sortLoops(loops);
			int parentLoopStart = LoopHelper.getParentLoopStartForIf(
					sortedLoops, ifs.getIfStart());
			int loopend = getloopEndForStart(loops, parentLoopStart);
			if (ifs.getIfStart() < loopend
					&& (end > loopend || end < ifs.getIfStart())){
				ifs.setIfCloseLineNumber(loopend);
				Loop pLoop = LoopHelper.getLoopGivenEnd(loopend);
				if(pLoop != null && loopend < pLoop.getLoopEndForBracket()){
					ifs.setIfCloseLineNumber(pLoop.getLoopEndForBracket());
				}
			}
			reqdEnd = ifs.getIfCloseLineNumber();

		}

		ArrayList allswicthes = behaviour.getAllSwitchBlks();
		if (allswicthes != null && allswicthes.size() > 0) {

			int newifend = -1;
			newifend = IFHelper.resetEndofIFElseWRTSwitch(allswicthes, ifs,
					reqdEnd, ExecutionState.getCurrentInstructionPosition(),
					"if");
			boolean valid = IFHelper.isNewEndValid(newifend, ifs, "if", end);
			if (valid) {
				ifs.setIfCloseLineNumber(newifend);
				reqdEnd = ifs.getIfCloseLineNumber();
				// Need to check here for a goto before the case end
				int start = ifs.getIfStart();
				int bye = ifs.getIfCloseFromByteCode();
				ArrayList starts = behaviour.getInstructionStartPositions();
				for (int z = reqdEnd; z > start; z--) {

					int inst = info[z];
					boolean isSt = isThisInstrStart(z);
					if (isSt && (inst == JvmOpCodes.GOTO)
							&& (getJumpAddress(info, z) == bye)) {
						reqdEnd = z;
						break;
					}

				}

			} else {
				if (ifs.getElseCloseLineNumber() == -1) {
					ifs.setIfCloseLineNumber(ifs.getIfCloseFromByteCode());
					reqdEnd = ifs.getIfCloseLineNumber();// here
				} else {
					reqdEnd = ifs.getIfCloseLineNumber();
				}
			}

		}
		return reqdEnd;

	}

	public static boolean checkForSizeOfArrayTimesStack() {

		if (GlobalVariableStore.getArraytimesstack().size() > 0) {
			return true;
		} else
			return false;
	}

	public static boolean prevNewPresent() {
		if (GlobalVariableStore.getNewfoundstack().size() < 2)
			return false;
		return true;
	}

	public static boolean skipGetStaticCall(byte[] info, int currentForIndex) {
		int n = currentForIndex + 3;
		if (n >= info.length)
			return false;
		int l = info.length;
		OperandStack opStack = getContext().getOpStack();
		ClassDescription cd = getContext().getClassRef().getCd();
		if (info[n] == JvmOpCodes.DUP || info[n] == JvmOpCodes.DUP2) {
			n = n + 1;
			boolean b = getGenericFinder().isThisInstrStart(n);
			if (b) {

				boolean isif = info[n] == JvmOpCodes.IFNONNULL;
				if (isif) {
					int j = getJumpAddress(info, n);
					if (j < n)
						return false;
					boolean ok = getGenericFinder().isThisInstrStart(j);
					if (ok) {
						ok = info[j] == JvmOpCodes.AASTORE;
						if (ok) {
							for (int x = n + 3; x < l; x++) {
								boolean b2 = getGenericFinder()
										.isThisInstrStart(x);
								if (b2) {
									if (info[x] == JvmOpCodes.LDC) {

										int offset = info[x + 1];
										if (offset < 0)
											offset += 256;
										CPString constString = cd
												.getStringsAtCPoolPosition(offset);
										if (constString == null) {
											return false;
										}
										java.lang.String stringLiteral = cd
												.getUTF8String(constString
														.getUtf8pointer());
										if (stringLiteral == null) {
											return false;
										}
										StringBuffer sb = new StringBuffer("");
										Util.checkForImport(stringLiteral, sb);
										java.lang.String classToLoad = sb
												.toString()
												+ ".class";
										Operand op = new Operand();
										op.setOperandValue(classToLoad);
										opStack.push(op);
										op.setClassType("Class");
										GlobalVariableStore
												.getSkipWRTClassLoadIf()
												.put(
														new Integer(
																currentForIndex + 3),
														new Integer(j));
										GlobalVariableStore
												.getSkipWRTClassLoadIfUpperLimits()
												.add(new Integer(j));
										return true;

									}
									if (info[x] == JvmOpCodes.LDC_W) {
										int offset = getGenericFinder()
												.getOffset(x);
										CPString constString = cd
												.getStringsAtCPoolPosition(offset);
										if (constString == null) {
											return false;
										}
										java.lang.String stringLiteral = cd
												.getUTF8String(constString
														.getUtf8pointer());
										if (stringLiteral == null) {
											return false;
										}

										Operand op = new Operand();
										StringBuffer sb = new StringBuffer("");
										Util.checkForImport(stringLiteral, sb);
										java.lang.String classToLoad = sb
												.toString()
												+ ".class";
										op.setOperandValue(classToLoad);
										opStack.push(op);
										op.setClassType("Class");
										GlobalVariableStore
												.getSkipWRTClassLoadIf()
												.put(
														new Integer(
																currentForIndex + 3),
														new Integer(j));
										GlobalVariableStore
												.getSkipWRTClassLoadIfUpperLimits()
												.add(new Integer(j));
										return true;
									}

								}

							}

						}
					}
				}

			}
		}
		return false;

	}

	public static boolean isGotoPrecededByDUPSTORE(int current, byte[] info) // FIXME
	{
		boolean yes = false;
		int prev = current - 1;

		boolean store = false;
		boolean dup = false;
		int x = -1;
		while (prev > 0) {
			if (getGenericFinder().isThisInstrStart(prev)) {
				store = getStoreFinder().isNextInstructionStore(prev);
				x = prev;
				if (store) {
					dup = getGenericFinder().isPrevInstDup(x);
					if (dup)
						return true;
				}

				if (info[prev] == JvmOpCodes.GOTO) {
					return false;
				}
			}
			prev--;
		}

		return yes;
	}

	public static class BranchLabel {
		IFBlock IF;

		java.lang.String l; // continue or break or empty

		public java.lang.String getBrlbl() {
			return brlbl;
		}

		java.lang.String brlbl; // jmpindexlabel

		public BranchLabel(IFBlock ifst, java.lang.String label,
				java.lang.String brlbl) {
			IF = ifst;
			l = label;
			this.brlbl = brlbl;
		}

		public IFBlock getIF() {
			return IF;
		}

		public java.lang.String getLBL() {
			return l;
		}
	}

	public static BranchLabel newBranchLabel(IFBlock ifst,
			java.lang.String label, java.lang.String brlbl) {
		return new BranchLabel(ifst, label, brlbl);
	}

	public static int getStoreInstPosInCode(byte[] info, int lend, int loadIndex)

	{
		for (int s = lend; s >= 0; s--) {
			if (getGenericFinder().isThisInstrStart(s)) {
				int curinst = info[s];
				boolean b = getStoreFinder().isCurrentInstStore(curinst);
				if (b) {
					int temp = s + 1;
					int storeindex = info[temp];
					if (storeindex == loadIndex)
						return s;
				}
			}

		}
		return -1;
	}

	/***************************************************************************
	 * NOTE: This is not general purpose method tofind load index inst...Skips
	 * certain loads see usages
	 * 
	 * @param inst
	 * @param info
	 * @param s
	 * @return
	 */
	public static int getLoadInstIndex(int inst, byte info[], int s) {
		// chkIndex is the index of the goto instruction.

		switch (inst) {

		case JvmOpCodes.ALOAD:
			return info[++s];

		case JvmOpCodes.ALOAD_0:
			return 0;

		case JvmOpCodes.ALOAD_1:
			return 1;

		case JvmOpCodes.ALOAD_2:
			return 2;

		case JvmOpCodes.ALOAD_3:
			return 3;

		case JvmOpCodes.DLOAD:
			return info[++s];

		case JvmOpCodes.DLOAD_0:
			return 0;

		case JvmOpCodes.DLOAD_1:
			return 1;

		case JvmOpCodes.DLOAD_2:
			return 2;

		case JvmOpCodes.DLOAD_3:
			return 3;

		case JvmOpCodes.FLOAD:
			return info[++s];

		case JvmOpCodes.FLOAD_0:
			return 0;

		case JvmOpCodes.FLOAD_1:
			return 1;

		case JvmOpCodes.FLOAD_2:
			return 2;

		case JvmOpCodes.FLOAD_3:
			return 3;

		case JvmOpCodes.ILOAD:
			return info[++s];

		case JvmOpCodes.ILOAD_0:
			return 0;

		case JvmOpCodes.ILOAD_1:
			return 1;

		case JvmOpCodes.ILOAD_2:
			return 2;

		case JvmOpCodes.ILOAD_3:
			return 3;

		case JvmOpCodes.LLOAD:
			return info[++s];

		case JvmOpCodes.LLOAD_0:
			return 0;
		case JvmOpCodes.LLOAD_1:
			return 1;
		case JvmOpCodes.LLOAD_2:
			return 2;
		case JvmOpCodes.LLOAD_3:
			return 3;

		}

		return -1;
	}

	public static java.lang.String getArrayType(java.lang.String temp) {

		if (temp.indexOf("L") != -1) {
			java.lang.String tmp = temp.substring(temp.indexOf("L") + 1, temp
					.indexOf(";"));
			tmp = tmp.replace('/', '.');
			return tmp;

		}

		if (temp.equals("Z")) {
			return "boolean";
		}
		if (temp.equals("D")) {
			return "double";
		}
		if (temp.equals("J")) {
			return "long";
		}
		if (temp.equals("F")) {
			return "float";
		}
		if (temp.equals("B")) {
			return "byte";
		}
		if (temp.equals("I")) {
			return "int";
		}

		if (temp.equals("C")) {
			return "char";
		}
		if (temp.equals("S")) {
			return "short";
		}
		return null;
	}

	public static boolean isNewFollowedByNew(byte[] info, int i) {
		int pos = i + 2 + 1;
		do {
			if (isThisInstrStart(pos)) {
				if (info[pos] == JvmOpCodes.NEW)
					return true;
				StringBuffer temp = new StringBuffer("");
				boolean b = getGenericFinder().isNextInstructionAnyInvoke(pos,
						temp);
				if (b)
					break;
				pos++;
			} else {
				pos++;
			}
		} while (true && pos < info.length);
		return false;
	}

	public static void registerInnerClassIfAny(java.lang.String Type) {
		java.lang.String type = Type.trim();
		if (type.indexOf("$") > 0) {

			java.lang.String path = Configuration.getPathForCurrentClassFile();
			java.lang.String name = ConsoleLauncher.getClassName(path);
			int numberof$inmain = 0;
			for (int j = 0; j < name.length(); j++) {
				char c = name.charAt(j);
				if (c == '$')
					numberof$inmain++;
			}
			int numberof$intype = 0;
			for (int j = 0; j < Type.length(); j++) {
				char c = Type.charAt(j);
				if (c == '$')
					numberof$intype++;
			}
			// / if(numberof$inmain+1==numberof$intype) // Need to register
			// inner class
			// {
			InnerClassTracker tracker = ConsoleLauncher.getTracker();
			if (tracker != null) {
				InnerClassTracker.Node currentRoot = ConsoleLauncher
						.getCurrentRootAdded(); // Parent
				java.lang.String dir = ConsoleLauncher.getClassDir();
				if (type.endsWith(".class") == false)
					type += ".class";
				type = ConsoleLauncher.getClassName(type);
				InnerClassTracker.Node child = tracker.createNode(dir
						+ File.separator + type, type);
				boolean add = ConsoleLauncher.getClazzRef().assertInnerClass(child.getClassName());
				if(add)
					tracker.registerChildNode(currentRoot, child);
			}
			// }

		} else
			return;

	}

	public static boolean isNewArrayEmbedded(byte[] info) {
		int currentForIndex = ExecutionState.getCurrentInstructionPosition();
		int prev = currentForIndex - 1;
		boolean b = false;
		int prevPos = getGenericFinder().getPrevStartOfInst(currentForIndex);
		if (prevPos < 0)
			return b;
		boolean cont = false;
		switch (info[prevPos]) {
		case JvmOpCodes.ICONST_0:
		case JvmOpCodes.ICONST_1:
		case JvmOpCodes.ICONST_2:
		case JvmOpCodes.ICONST_3:
		case JvmOpCodes.ICONST_4:
		case JvmOpCodes.ICONST_5:
		case JvmOpCodes.BIPUSH:
		case JvmOpCodes.SIPUSH:
			cont = true;
			break;
		}
		if (!cont)
			return b;
		cont = false;
		prevPos = getGenericFinder().getPrevStartOfInst(prevPos);
		if (prevPos < 0)
			return b;
		switch (info[prevPos]) {
		case JvmOpCodes.ICONST_0:
		case JvmOpCodes.ICONST_1:
		case JvmOpCodes.ICONST_2:
		case JvmOpCodes.ICONST_3:
		case JvmOpCodes.ICONST_4:
		case JvmOpCodes.ICONST_5:
		case JvmOpCodes.BIPUSH:
		case JvmOpCodes.SIPUSH:
			cont = true;
			break;
		}
		if (!cont)
			return b;
		prevPos = getGenericFinder().getPrevStartOfInst(prevPos);
		if (prevPos < 0)
			return b;
		for (int z = prevPos; z >= 0; z--) {

			if (!getGenericFinder().isThisInstrStart(z))
				continue;
			switch (info[z]) {

			case JvmOpCodes.ASTORE:
			case JvmOpCodes.ASTORE_0:
			case JvmOpCodes.ASTORE_1:
			case JvmOpCodes.ASTORE_2:
			case JvmOpCodes.ASTORE_3:
				return b;
			case JvmOpCodes.ANEWARRAY:
			case JvmOpCodes.AASTORE:
				return !b;

			}

		}
		return b;

	}

	public static Operand createOperand(Object val) {
		Operand opr = new Operand();
		opr.setOperandValue(val);
		return opr;

	}

	public static java.lang.String getReturnTypeIfPreviousInvoke(int j,
			byte[] info) {

		// Terminator t;
		java.lang.String s = "0";
		ClassDescription cd = getContext().getClassRef().getCd();
		Hashtable invokeStartEnd = GlobalVariableStore.getInvokeStartEnd();
		if (invokeStartEnd != null && invokeStartEnd.size() > 0) {
			Integer in = (Integer) invokeStartEnd.get(new Integer(j - 1));
			if (in != null) {
				int iin = in.intValue();
				switch (info[iin]) {

				case JvmOpCodes.INVOKEINTERFACE:
					int classIndex = getGenericFinder().getOffset(iin);
					InterfaceMethodRef iref = cd
							.getInterfaceMethodAtCPoolPosition(classIndex);
					java.lang.String classname = iref.getClassname();
					java.lang.String typeofmet = iref.getTypeofmethod();
					int br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

					break;

				case JvmOpCodes.INVOKESPECIAL:

					classIndex = getGenericFinder().getOffset(iin);
					MethodRef mref = cd.getMethodRefAtCPoolPosition(classIndex);
					java.lang.String classtype = mref.getClassname();
					typeofmet = mref.getTypeofmethod();
					br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

					break;

				case JvmOpCodes.INVOKESTATIC:
					classIndex = getGenericFinder().getOffset(iin);
					mref = cd.getMethodRefAtCPoolPosition(classIndex);
					classname = mref.getClassname();
					typeofmet = mref.getTypeofmethod();
					br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

					break;

				case JvmOpCodes.INVOKEVIRTUAL:
					classIndex = getGenericFinder().getOffset(iin);
					mref = cd.getMethodRefAtCPoolPosition(classIndex);
					classname = mref.getClassname();
					typeofmet = mref.getTypeofmethod();
					br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

					break;

				default:
					return s;

				}
			}

			return s;

		} else {
			int iin = j - 3;
			int classIndex = -1;
			boolean isS = getGenericFinder().isThisInstrStart(iin);
			if (isS) {
				switch (info[iin]) {

				/*
				 * case JvmOpCodes.INVOKEINTERFACE: int
				 * classIndex=getGenericFinder().getOffset(iin);
				 * InterfaceMethodRef
				 * iref=cd.getInterfaceMethodAtCPoolPosition(classIndex);
				 * java.lang.String classname=iref.getClassname();
				 * java.lang.String typeofmet=iref.getTypeofmethod(); int
				 * br=typeofmet.indexOf(")"); if(br!=-1) { java.lang.String
				 * ret=typeofmet.substring(br+1); if(ret.trim().equals("Z")) {
				 * return "false"; } }
				 * 
				 * break;
				 */

				case JvmOpCodes.INVOKESPECIAL:

					classIndex = getGenericFinder().getOffset(iin);
					MethodRef mref = cd.getMethodRefAtCPoolPosition(classIndex);
					java.lang.String classtype = mref.getClassname();
					java.lang.String typeofmet = mref.getTypeofmethod();
					int br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

					break;

				case JvmOpCodes.INVOKESTATIC:
					classIndex = getGenericFinder().getOffset(iin);
					mref = cd.getMethodRefAtCPoolPosition(classIndex);
					java.lang.String classname = mref.getClassname();
					typeofmet = mref.getTypeofmethod();
					br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

					break;

				case JvmOpCodes.INVOKEVIRTUAL:
					classIndex = getGenericFinder().getOffset(iin);
					mref = cd.getMethodRefAtCPoolPosition(classIndex);
					classname = mref.getClassname();
					typeofmet = mref.getTypeofmethod();
					br = typeofmet.indexOf(")");
					if (br != -1) {
						java.lang.String ret = typeofmet.substring(br + 1);
						if (ret.trim().equals("Z")) {
							return "false";
						}

					}

				}
			}
			if (getGenericFinder().isThisInstrStart(j - 5)
					&& info[j - 5] == JvmOpCodes.INVOKEINTERFACE) {
				iin = j - 5;
				classIndex = getGenericFinder().getOffset(iin);
				InterfaceMethodRef iref = cd
						.getInterfaceMethodAtCPoolPosition(classIndex);
				java.lang.String classname = iref.getClassname();
				java.lang.String typeofmet = iref.getTypeofmethod();
				int br = typeofmet.indexOf(")");
				if (br != -1) {
					java.lang.String ret = typeofmet.substring(br + 1);
					if (ret.trim().equals("Z")) {
						return "false";
					}

				}
			}
		}
		return s;
	}

	public static boolean checkForLoadAfterIINC(byte[] info,
			OperandStack opStack, int current, LocalVariable local, int index,
			java.lang.String c) {

		boolean b = false;
		int j = current + 1 + 1 + 1;
		int loadindex = -1;
		boolean ok = false;
		boolean iloadfnd = false;
		if (isThisInstrStart(j)) {

			switch (info[j]) {
			case JvmOpCodes.ILOAD:
				loadindex = info[(j + 1)];
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_0:
				loadindex = 0;
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_1:
				loadindex = 1;
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_2:
				loadindex = 2;
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_3:
				loadindex = 3;
				iloadfnd = true;
				break;

			case JvmOpCodes.IALOAD:
				loadindex = -1;
				ok = true;
				break;
			}

			if (loadindex != -1) {
				if (loadindex == index) {

					if (c.trim().indexOf("-1") != -1) {
						b = true;
						local.setTempVarName("--" + local.getVarName());
					} else if (c.trim().indexOf("1") != -1) {
						b = true;
						local.setTempVarName("++" + local.getVarName());
					} else {
						b = false;
					}
				}
			} else {
				b = false;
			}

			if (ok) {
				if (c.trim().indexOf("-1") != -1) {
					b = true;
					Operand currentTop = opStack.peekTopOfStack();
					if (currentTop != null) {
						java.lang.String topval = currentTop.getOperandValue();
						topval = topval + "--";
						currentTop.setOperandValue(topval);
						b = true;
					} else {
						b = false;
					}
				} else if (c.trim().indexOf("1") != -1) {
					b = true;
					Operand currentTop = opStack.peekTopOfStack();
					if (currentTop != null) {
						java.lang.String topval = currentTop.getOperandValue();
						topval = topval + "++";
						currentTop.setOperandValue(topval);
						b = true;
					} else {
						b = false;
					}
				} else {
					b = false;
				}
			}
			/*
			 * if(!b && !iloadfnd) { j=current+1+1+1; StringBuffer buf=new
			 * StringBuffer(""); boolean
			 * istore=isThisInstructionIStoreInst(info,j,buf); if(istore==false) {
			 * if(c.trim().indexOf("-1")!=-1) { b=true; Operand
			 * currentTop=opStack.peekTopOfStack(); if(currentTop!=null) {
			 * java.lang.String topval=currentTop.getOperandValue();
			 * topval=topval+"--"; currentTop.setOperandValue(topval); b=true; }
			 * else { b=false; } } else if(c.trim().indexOf("1")!=-1) { b=true;
			 * Operand currentTop=opStack.peekTopOfStack(); if(currentTop!=null) {
			 * java.lang.String topval=currentTop.getOperandValue();
			 * topval=topval+"++"; currentTop.setOperandValue(topval); b=true; }
			 * else { b=false; } } else { b=false; } } }
			 */

		}

		return b;
	}

	public static boolean checkForLoadBeforeIINC(byte[] info,
			OperandStack opStack, int current, LocalVariable local, int index,
			java.lang.String c) {

		boolean b = false;
		int loadindex = -1;
		boolean ok = false;
		boolean iloadfnd = false;
		int j = getGenericFinder().getPrevStartOfInst(current);
		if (isThisInstrStart(j)) {

			switch (info[j]) {

			case JvmOpCodes.ILOAD:
				loadindex = info[(j + 1)];
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_0:
				loadindex = 0;
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_1:
				loadindex = 1;
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_2:
				loadindex = 2;
				iloadfnd = true;
				break;

			case JvmOpCodes.ILOAD_3:
				loadindex = 3;
				iloadfnd = true;
				break;

			case JvmOpCodes.IALOAD:
				loadindex = -1;
				ok = true;
				break;

			}

			if (loadindex != -1) {
				if (loadindex == index) {

					if (c.trim().indexOf("-1") != -1) {
						b = true;
						local.setTempVarName(local.getVarName() + "--");
						opStack.peekTopOfStack().setOperandValue(
								local.getTempVarName());
					} else if (c.trim().indexOf("1") != -1) {
						b = true;
						local.setTempVarName(local.getVarName() + "++");
						opStack.peekTopOfStack().setOperandValue(
								local.getTempVarName());

					} else {
						b = false;
					}
				}
			} else {
				b = false;
			}

			if (ok) {
				if (c.trim().indexOf("-1") != -1) {
					b = true;
					Operand currentTop = opStack.peekTopOfStack();
					if (currentTop != null) {
						java.lang.String topval = currentTop.getOperandValue();
						topval = topval + "--";
						currentTop.setOperandValue(topval);
						b = true;
					} else {
						b = false;
					}
				} else if (c.trim().indexOf("1") != -1) {
					b = true;
					Operand currentTop = opStack.peekTopOfStack();
					if (currentTop != null) {
						java.lang.String topval = currentTop.getOperandValue();
						topval = topval + "++";
						currentTop.setOperandValue(topval);
						b = true;
					} else {
						b = false;
					}
				} else {
					b = false;
				}
			}
			/*
			 * if(!b && !iloadfnd) { j=current+3; StringBuffer buf=new
			 * StringBuffer(""); boolean
			 * istore=isThisInstructionIStoreInst(info,j,buf); if(istore==false) {
			 * if(c.trim().indexOf("-1")!=-1) { b=true; Operand
			 * currentTop=opStack.peekTopOfStack(); if(currentTop!=null) {
			 * java.lang.String topval=currentTop.getOperandValue();
			 * topval=topval+"--"; currentTop.setOperandValue(topval); b=true; }
			 * else { b=false; } } else if(c.trim().indexOf("1")!=-1) { b=true;
			 * Operand currentTop=opStack.peekTopOfStack(); if(currentTop!=null) {
			 * java.lang.String topval=currentTop.getOperandValue();
			 * topval=topval+"++"; currentTop.setOperandValue(topval); b=true; }
			 * else { b=false; } } else { b=false; } } }
			 */

		}

		return b;
	}

	public static boolean checkForValueReturn(byte[] code, int i) {
		boolean present = false;
		int jvmInst = code[i];
		switch (jvmInst) {
		case JvmOpCodes.ARETURN:
		case JvmOpCodes.IRETURN:
		case JvmOpCodes.FRETURN:
		case JvmOpCodes.DRETURN:
		case JvmOpCodes.LRETURN:
			present = true;
			break;
		default:
			present = false;
			break;
		}
		return present;
	}

	public static void resetOperandValueIfNecessary(ArrayList paramlist,
			int indx, Operand op2) {

		boolean boolparam = isParameterTypeBoolean(paramlist, indx);
		if (boolparam) {
			if (op2 != null && op2.getOperandValue() != null
					&& op2.getOperandValue().toString().trim().equals("1"))
				op2.setOperandValue("true");
			if (op2 != null && op2.getOperandValue() != null
					&& op2.getOperandValue().toString().trim().equals("0"))
				op2.setOperandValue("false");

		}
	}

	private static boolean isParameterTypeBoolean(List paramlist, int indx) {
		java.lang.String type = (java.lang.String) paramlist.get(indx);
		if (type.equalsIgnoreCase("boolean")) {
			return true;
		} else {
			return false;
		}
	}

	public static void resetMethodParameters(OperandStack stack,
			ArrayList methodParams, int j) {
		Behaviour behavior = getContext();
		ArrayList problematicInvokes = GlobalVariableStore
				.getProblematicInvokes();
		// Check if reset needs to be done in the first place
		if (problematicInvokes == null || problematicInvokes.size() == 0)
			return;
		boolean ok = false;
		for (int n = 0; n < problematicInvokes.size(); n++) {

			Integer in = (Integer) problematicInvokes.get(n);
			if (in.intValue() == j) {
				ok = true; // Yes jdec
				break;
			} else
				ok = false;

		}
		if (!ok)
			return;

		// some validations
		if (methodParams == null || stack == null)
			return;
		int count = methodParams.size();
		if (count == 0)
			return;
		if (stack.size() < count)
			return; // Should Not happen: A Bug in jdec
		Operand reqdops[] = new Operand[count];
		boolean needtoresetstack = false;
		for (int h = 0; h < count; h++) {
			reqdops[h] = stack.getTopOfStack();
			needtoresetstack = true;
		}

		// IMPORTANT : IF needtoresetstack IS TRUE method shud return only after
		// resetting stack .NOT before that

		// start Resetting
		int opstart = 0;
		for (int z = count - 1; z >= 0; z--) {

			Operand current = reqdops[opstart];
			opstart++;
			if (current != null) {
				java.lang.String type = current.getLocalVarType();
				boolean needtoreset = false;
				java.lang.String param = (java.lang.String) methodParams.get(z);
				if (type != null && type.trim().equals("int")) {

					if (param.trim().equals("byte")) {
						needtoreset = true;
					} else if (param.trim().equals("boolean")) {
						needtoreset = true;
					} else if (param.trim().equals("short")) {
						needtoreset = true;
					} else if (param.trim().equals("char")) {
						needtoreset = true;
					} else {
						needtoreset = false;
					}
					if (needtoreset) {
						int localIndex = current.getLocalVarIndex();
						java.lang.String searchFor = "#REPLACE_INT_"
								+ localIndex + "#";
						int len = searchFor.length();
						java.lang.String codeAsString = getContext()
								.getCodeAsBuffer().toString();
						int start = codeAsString.indexOf(searchFor);
						java.lang.String name = "";
						if (start != -1) {
							java.lang.String tp = codeAsString.substring(start
									+ len, start + len + 3);
							int equalTo = -1;
							StringBuffer newpos = new StringBuffer("");
							if (tp.equals("int")) {
								// java.lang.String=codeStatements.replaceFirst(searchFor+"int",param.trim());
								name = (java.lang.String) current
										.getOperandValue();

								java.lang.String afterreplace = getStringAfterReplacement(
										start, searchFor + "int", param.trim(),
										newpos, name, false, param.trim());
								getContext().replaceBuffer(afterreplace);

							}
							try {
								equalTo = Integer.parseInt(newpos.toString());
							} catch (NumberFormatException ne) {
								equalTo = -1;
							}

							if (equalTo != -1) {
								java.lang.String codeasstring = behavior
										.getCodeAsBuffer().toString();
								int valueIn = codeasstring.indexOf("#VALUE"
										+ localIndex + "#", equalTo);
								if (valueIn != -1) {
									java.lang.String valuehash = "#VALUE"
											+ localIndex + "#";
									java.lang.String val = codeasstring
											.substring(valueIn
													+ valuehash.length(),
													valueIn
															+ valuehash
																	.length()
															+ 1);

									java.lang.String afterreplace = getStringAfterReplacement(
											valueIn, "#VALUE" + localIndex
													+ "#", val, newpos, name,
											true, param.trim());
									getContext().replaceBuffer(afterreplace);
								}
							}
						}

					}

				} else if (type != null && type.trim().equals("")) {

					if (param.trim().equals("byte")
							|| param.trim().equals("short")
							|| param.trim().equals("char")) {

						current.setOperandValue("(" + param + ")"
								+ current.getOperandValue());

					}
				}
				// Check for multidimensional.....

				if (param.trim().indexOf("[") != -1) {
					int first = param.trim().indexOf("[");
					int last = param.trim().lastIndexOf("[");
					int howmany = last - first + 1;
					boolean isMulti = current.isMultiDimension();
					if (isMulti) {
						java.lang.String value = (java.lang.String) current
								.getOperandValue();
						/*
						 * if(value.indexOf("[")!=-1) { /*int cnt=1; int
						 * start=value.indexOf("["); int next=start+1;
						 * while(next < value.length()) {
						 * 
						 * if(value.charAt(next)=='[') { cnt++; } next++; }
						 * 
						 * int total=cnt+howmany;
						 */
						java.lang.String bracks = "";
						for (int s = 0; s < howmany; s++) {

							bracks += "[]";
						}
						value += bracks;
						current.setOperandValue(value);

					}

				}

			} else // Again should not happen at all. Returning if this happens
			// , so that jdec will not reset a wrong operand's
			// value
			{
				// Restore Stack
				for (int l = reqdops.length - 1; l >= 0; l--) {
					Operand op = reqdops[l];
					stack.push(op);
				}

				return;
			}

		}

		// This should be final step before returning
		if (needtoresetstack) {
			// Restore Stack
			for (int l = reqdops.length - 1; l >= 0; l--) {
				Operand op = reqdops[l];
				stack.push(op);
			}
		}

	}

	private static java.lang.String getStringAfterReplacement(int fromwhere,
			java.lang.String lookfor, java.lang.String replaceString,
			StringBuffer sb, java.lang.String name, boolean skipone,
			java.lang.String methodparam) {
		int equal = -1;
		java.lang.String codeAsString = getContext().getCodeAsBuffer()
				.toString();
		java.lang.String orig = codeAsString;
		java.lang.String temp1 = codeAsString.substring(0, fromwhere);
		java.lang.String temp2 = replaceString;
		java.lang.String temp3 = "";
		if (skipone) {
			if (methodparam.equalsIgnoreCase("boolean")) {

				if (replaceString.trim().equalsIgnoreCase("0")) {
					temp2 = "false";
				} else if (replaceString.trim().equalsIgnoreCase("1")) {
					temp2 = "true";
				} else {

				}

			}
			temp3 = codeAsString.substring(fromwhere + lookfor.length() + 1);
		} else {
			temp3 = codeAsString.substring(fromwhere + lookfor.length());
		}

		orig = temp1 + temp2 + temp3;
		equal = orig.indexOf("=", orig.indexOf(replaceString + "\t" + name));
		sb.append(equal);
		return orig;
	}

	public static void adjustBracketCount(Operand op) {
		java.lang.String value = op.getOperandValue();
		int openB = 0;
		int closeB = 0;
		if (value != null && value.indexOf("(") != -1
				&& value.indexOf(")") != -1) {
			for (int c = 0; c < value.length(); c++) {
				char ch = value.charAt(c);
				if (ch == ')')
					closeB++;
				else if (ch == '(')
					openB++;
			}
			if (closeB > openB) {

				java.lang.String temp = value.trim();
				int number = closeB - openB;
				if (temp.charAt(temp.length() - 1) == ')') {
					if (temp.length() - number < 0)
						number = 1; // A Crude way of avoiding Exception If any
					java.lang.String str = value.substring(0, temp.length()
							- number);
					op.setOperandValue(str);
				}

			}
			if (closeB < openB) {

				java.lang.String temp = value.trim();
				int number = openB - closeB;
				java.lang.String str = "";
				for (int z = 1; z <= number; z++)
					str += ")";
				op.setOperandValue(temp + str);
			}

		}

	}

	public static boolean isMethodRetBoolean(Behaviour b) {

		java.lang.String type = b.getReturnType();
		if (type.equals("boolean"))
			return true;
		return false;

	}

	public static int getNextInstrPos(int index) {
		byte[] code = getContext().getCode();
		for (int i = index + 1; i < code.length; i++) {
			boolean start = getGenericFinder().isThisInstrStart(i);
			if (start) {
				return i;
			}
		}
		return -1;
	}

}
