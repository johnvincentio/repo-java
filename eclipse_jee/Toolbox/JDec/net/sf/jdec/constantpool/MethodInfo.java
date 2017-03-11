/*
 *  MethodInfo.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.constantpool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jdec.blocks.Switch;
import net.sf.jdec.config.Configuration;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.LocalVariableStructure;
import net.sf.jdec.core.LocalVariableTypeTable;
import net.sf.jdec.core.ShortcutAnalyser;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.util.Constants;
import net.sf.jdec.util.Util;

public class MethodInfo {

	private String signature;
	
	private ShortcutAnalyser shortCutA = null;

	private LocalVariableStructure structure;

	private LocalVariableTypeTable typeStructure;
	
	private java.lang.String[] accessSpecifiers = null;

	private ArrayList parameters = new ArrayList();

	private java.lang.String returnType = "";

	private java.lang.String methodName = "";

	private ArrayList methodType = new ArrayList();

	private java.lang.String belongsToClass = "";

	private boolean returnTypeIsArray = false;

	private int returnTypeArrayDimension = -1;

	private ArrayList exceptions = new ArrayList();

	private byte[] code = null;

	private boolean isConstructor = false;

	private java.lang.String key = "";

	private boolean isParameterArray = false;

	private boolean isExceptionTableSet = false;

	private ArrayList allTryBlocksForMethod = new ArrayList();

	private ArrayList exceptionTablesCreated = new ArrayList();

	private ArrayList allswitchesformethod = new ArrayList();

	private ArrayList behaviourLoops = new ArrayList();

	public ShortcutAnalyser getShortCutAanalyser() {
		return shortCutA;
	}

	public void setShortCutAnalyser(ShortcutAnalyser shortCutA) {
		this.shortCutA = shortCutA;
	}

	public ArrayList getInststartpos() {
		return inststartpos;
	}

	public void setInststartpos(ArrayList inststartpos) {
		this.inststartpos = inststartpos;
	}

	private ArrayList inststartpos = new ArrayList();

	/*
	 * public ArrayList getExceptions() { return exceptions; }
	 */

	public void setExceptions(ArrayList exceptions) {
		this.exceptions = exceptions;
	}

	public void addExceptions(java.lang.String exceptionStr) {
		if (Configuration.getShowImport().equalsIgnoreCase("true")) {
			java.lang.String simplename = "";
			java.lang.String fullName = exceptionStr;
			int lastSlash = fullName.lastIndexOf("/");
			if (lastSlash == -1) {
				lastSlash = fullName.lastIndexOf(".");
			}
			if (lastSlash != -1) {
				simplename = fullName.substring(lastSlash + 1);
			} else
				simplename = fullName;
			fullName = fullName.replace('/', '.');
			ConsoleLauncher.addImportClass(fullName);
			exceptions.add(simplename);

		} else
			exceptions.add(exceptionStr);
	}

	public java.lang.String[] getExceptions() {
		java.lang.String[] exception_arr = new java.lang.String[exceptions
				.size()];
		int index = 0;
		Iterator i = exceptions.iterator();
		while (i.hasNext()) {
			exception_arr[index++] = (java.lang.String) i.next();
		}
		// System.out.println();
		return exception_arr;
	}

	public int getReturnTypeArrayDimension() {
		return returnTypeArrayDimension;
	}

	public void setReturnTypeArrayDimension(int returnTypeArrayDimension) {
		this.returnTypeArrayDimension = returnTypeArrayDimension;
	}

	public boolean isReturnTypeIsArray() {
		return returnTypeIsArray;
	}

	public void setReturnTypeIsArray(boolean returnTypeIsArray) {
		this.returnTypeIsArray = returnTypeIsArray;
	}

	public void setAccessSpecifiers(java.lang.String[] accessSpecifiers) {
		this.accessSpecifiers = accessSpecifiers;
	}

	public java.lang.String getBelongsToClass() {
		return belongsToClass;
	}

	public void setBelongsToClass(java.lang.String belongsToClass) {
		this.belongsToClass = belongsToClass;
	}

	public java.lang.String[] getAccessSpecifiers() {
		return accessSpecifiers;
	}

	public void setAccessSpecifiers(int al) {
		this.accessSpecifiers = Util.parseAccessSpecifiers(new Integer(al),
				Constants.METHOD_ACC);
	}

	public java.lang.String getMethodName() {
		return methodName;
	}

	public void setMethodName(java.lang.String methodName) {
		this.methodName = methodName;
	}

	public ArrayList getMethodType() {
		return methodType;
	}

	public void setMethodType(ArrayList methodType) {
		this.methodType = methodType;
	}

	public void addMethodType(java.lang.String methodType) {
		this.methodType.add(methodType);
	}

	/*
	 * public ArrayList getParameters() { return parameters; }
	 */

	public java.lang.String[] getParameters() {
		java.lang.String[] parameters_arr = new java.lang.String[parameters
				.size()];
		int index = 0;
		Iterator i = parameters.iterator();
		while (i.hasNext()) {
			parameters_arr[index++] = (java.lang.String) i.next();
		}
		return parameters_arr;
	}

	/**
	 * Used While populating localVariabletable
	 * 
	 * @param parameter
	 */

	public java.lang.String getStringifiedParameters() {
		java.lang.String temp = "";

		Iterator i = parameters.iterator();
		if (i.hasNext() == false) {
			temp += "()";
			return temp;
		} else {
			temp += "(";
			while (i.hasNext()) {
				temp += (java.lang.String) i.next();
				if (i.hasNext())
					temp += ",";
			}
			temp += ")";
			return temp;
		}
	}

	public void addParameters(java.lang.String parameter) {
		this.parameters.add(parameter);
	}

	public void setParameters(ArrayList parameters) {
		this.parameters = parameters;
	}

	public java.lang.String getReturnType() {

		boolean isArray = this.isReturnTypeIsArray();
		java.lang.String temp = "";
		if (isArray == true) {
			for (int count = 1; count <= this.getReturnTypeArrayDimension(); count++) {
				temp += "[]";
			}
			return returnType + temp;

		} else {
			return returnType;
		}
	}

	public void setReturnType(java.lang.String returnType) {
		this.returnType = returnType;
	}

	public void parseDescriptor(java.lang.String descriptor) {
		if (methodName.equalsIgnoreCase("<clinit>")) {
			methodName = "static";
			descriptor = "";
			accessSpecifiers = new java.lang.String[] { "" };
			return;
		}
		int charIndex = 0;
		java.lang.String parameterString = descriptor.substring(1, descriptor
				.lastIndexOf(")"));
		java.lang.String returnString = descriptor.substring(descriptor
				.lastIndexOf(")") + 1);
		if (returnString.trim().charAt(0) == 'L'
				|| returnString.trim().charAt(0) == '[')
			this.setReturnTypeAsObjectOrArrayType(true);
		else
			this.setReturnTypeAsObjectOrArrayType(false);

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

		if (returnString.indexOf(";") != -1) {
			returnString = returnString.substring(0, returnString.indexOf(";"));
		}

		while (returnString.length() > 0) {
			// System.out.println();
			if (returnString.startsWith("L")) {
				// System.out.println(returnString + " "+returnType.length());
				returnType = returnString.substring(1, returnString.length());
				returnType = returnType.replace('/', '.');
				returnString = "";
			} else {
				if (returnString.equals("I")) {
					returnType = "int";
					returnString = "";
				}
				if (returnString.equals("B")) {
					returnType = "byte";
					returnString = "";
				}
				if (returnString.equals("C")) {
					returnType = "char";
					returnString = "";
				}
				if (returnString.equals("D")) {
					returnType = "double";
					returnString = "";
				}
				if (returnString.equals("F")) {
					returnType = "float";
					returnString = "";
				}
				if (returnString.equals("J")) {
					returnType = "long";
					returnString = "";
				}
				if (returnString.equals("S")) {
					returnType = "short";
					returnString = "";
				}
				if (returnString.equals("Z")) {
					returnType = "boolean";
					returnString = "";
				}
				if (returnString.equals("V")) {
					returnType = "void";
					returnString = "";
				}
				if (returnString.startsWith("[")) {
					returnTypeIsArray = true;
					returnTypeArrayDimension = returnString.lastIndexOf("[") + 1;
					if (returnString.indexOf("L") != -1) {
						returnType = returnString.substring(returnString
								.lastIndexOf("[") + 2);
						returnType = returnType.replace('/', '.');
						returnString = "";
						// returnString =returnType;
					} else {
						returnString = returnString.substring(returnString
								.lastIndexOf("[") + 1);
						// returnString = "";

					}
				}
			}
		}

	}

	public void setCode(byte[] code) {
		this.code = code;
	}

	public byte[] getCode() {
		return this.code;
	}

	public boolean isConstructor() {
		return isConstructor;
	}

	public void setConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public java.lang.String getKey() {
		return key;
	}

	public void setKey(java.lang.String key) {
		this.key = key;
	}

	/***************************************************************************
	 * Call this method when handling code attribute of methodInfo class
	 * 
	 * @param p
	 *            true or false. True is only set for Try Catch Handlers
	 */

	public void setExceptionTablePresent(boolean p) {
		this.isExceptionTableSet = p;
	}

	/***************************************************************************
	 * True is returned only when a try catch Block is present NOT when there is
	 * only synchronized statement
	 * 
	 */

	public boolean isExceptionTableSet() {
		return this.isExceptionTableSet;
	}

	public class ExceptionTable {

		
		
		private boolean ignore = false;
		
		private boolean wasSynchClosed = false;
		
		/***********************************************************************
		 * 
		 * Fields to represent Data as got from the code and according to the
		 * spec
		 * 
		 */
		
		private int startPc = -1;

		private int endPc = -1;

		private int handlerPc = -1;

		private int catchType = -1;

		/***********************************************************************
		 * Interpretation for handing Try/Catch/Finally TODO: Need to modify if
		 * necessary to handle monitorEnter and MonitorExit blocks
		 */

		private int startOfGaurdRegion = -1;

		private int endOfGuardRegion = -1;

		private int startOfHandlerForGuardRegion = -1;

		private int endOfHandlerForGuardRegion = -1;

		private java.lang.String typeOfGuard = "";

		// exceptionName can ne <any> or String denoting an Exception Class Like
		// java/lang/Exception
		private java.lang.String exceptionName = "";

		// nextTable Populate for time being
		// TODO: May not be necesary to have...
		private ExceptionTable nextTable = null;

		// Used to denote catch or finally
		// TODO: check for monitor later on

		java.lang.String handlerType = "";

		ExceptionTable(int spc, int epc, int hpc, int ctype) {
			startPc = spc;
			endPc = epc;
			handlerPc = hpc;
			catchType = ctype;
		}

		ExceptionTable(int spc, int epc, int hpc, java.lang.String eName) {
			startPc = spc;
			endPc = epc;
			handlerPc = hpc;
			exceptionName = eName;
		}

		public int getStartPC() {
			return startPc;
		}

		public int getEndPC() {
			return endPc;
		}

		public int getStartOfHandler() {
			return handlerPc;
		}

		public int getCatchType() {
			return catchType;
		}

		public java.lang.String getExceptionName() {
			return exceptionName;
		}

		public void setExceptionName(java.lang.String eName) {
			exceptionName = eName;
		}

		public ExceptionTable getNextTable() {
			return nextTable;
		}

		public void setNextTable(ExceptionTable nextTable) {
			this.nextTable = nextTable;
		}

		public java.lang.String getHandlerType() {
			return handlerType;
		}

		public void setHandlerType(java.lang.String handlerType) {
			this.handlerType = handlerType;
		}

		/***********************************************************************
		 * Important Methods
		 * 
		 */

		public int getStartOfGuardRegion() {
			if (startOfGaurdRegion == -1) {
				startOfGaurdRegion = getStartPC();
			}
			return startOfGaurdRegion;
		}

		public int getEndOfGuardRegion() {
			if (endOfGuardRegion == -1) {
				endOfGuardRegion = getEndPC();
			}
			return endOfGuardRegion;
		}

		public void setEndOfGuardRegion(int end) {
			endOfGuardRegion = end;
		}

		public void setStartOfHandlerForGuardRegion(
				int startOfHandlerForGuardRegion) {
			this.startOfHandlerForGuardRegion = startOfHandlerForGuardRegion;
		}

		public int getStartOfHandlerForGuardRegion() {
			if (startOfHandlerForGuardRegion == -1) {
				startOfHandlerForGuardRegion = getStartOfHandler();
			}
			return startOfHandlerForGuardRegion;
		}

		/***********************************************************************
		 * 
		 * This method returns the type of Guard Region Guard Region can be try
		 * block or catch Block Necessary in order to print either "try{ or
		 * print "catch(<Exception Type>){
		 * 
		 * @return Either "try" or "catch"
		 * 
		 */

		// NOTE: This is only for case where finally is Handler
		// Because we do know whether the Guard Region is of Try or Catch
		// TODO : Need to check what Happens when we have synchronized block
		// IMPORTANT
		// NOTE: Assumption : All ExceptionTables have already Been populated
		public java.lang.String getTypeOfGuardRegion() {
			return typeOfGuard;
		}

		public void setTypeOfGuardRegion(java.lang.String type) {
			typeOfGuard = type;
		}

		public int getEndOfHandlerForGuardRegion() {
			return endOfHandlerForGuardRegion;
		}

		public void setEndOfHandlerForGuardRegion(int endOfHandlerForGuardRegion) {
			this.endOfHandlerForGuardRegion = endOfHandlerForGuardRegion;
		}

		public java.lang.String getTypeOfHandlerForGuardRegion() {
			if (exceptionName == null || exceptionName.trim().length() == 0) {
				exceptionName = getExceptionName();
			}
			if (exceptionName != null && exceptionName.trim().length() != 0) {
				if (exceptionName.equals("<any>")) {
					setTypeOfHandlerForGuardRegion("FinallyBlock");
					return "FinallyBlock";
				} else {
					setTypeOfHandlerForGuardRegion("CatchBlock");
					return "CatchBlock";
				}
			} else {
				setTypeOfHandlerForGuardRegion("UNKNOWN HANDLER");
				return "UNKNOWN HANDLER";
			}

		}

		public boolean equals(Object o) {
			if (!(o instanceof ExceptionTable))
				return false;
			else {
				ExceptionTable table = (ExceptionTable) o;
				int guardStart = table.getStartOfGuardRegion();
				int myGuardStart = getStartOfGuardRegion();
				int guardEnd = table.getEndOfGuardRegion();
				int myGuardEnd = getEndOfGuardRegion();
				int handlerStart = table.getStartOfHandlerForGuardRegion();
				int myHandlerStart = getStartOfHandlerForGuardRegion();
				java.lang.String ExceptionName = table.getExceptionName();
				java.lang.String myExceptionName = getExceptionName();

				// Check
				if (guardStart == myGuardStart && guardEnd == myGuardEnd
						&& handlerStart == myHandlerStart
						&& ExceptionName.equals(myExceptionName)) {
					return true;
				}

				else {
					return false;
				}
			}

		}

		public java.lang.String getHandlerBlockName() {
			return handlerBlockName;
		}

		private java.lang.String handlerBlockName = "";

		public void setTypeOfHandlerForGuardRegion(java.lang.String type) {
			handlerBlockName = type;
		}

		// belurs:

		/**
		 * The following two attributes of ExceptionTable are only meant for
		 * Synchronized Blocks.
		 */
		private int monitorEnterPosInCode = -1;

		private int synchEnd = -1;

		public int getMonitorEnterPosInCode() {
			return monitorEnterPosInCode;
		}

		public void setMonitorEnterPosInCode(int monnitorEnterPosInCode) {
			this.monitorEnterPosInCode = monnitorEnterPosInCode;
		}

		public int getSynchEnd() {
			if (synchEnd == -1)
				return endPc;
			return synchEnd;
		}

		public void setSynchEnd(int synchEnd) {
			byte[] code = getCode();
			if (code != null && getInststartpos() != null
					&& getInststartpos().size() > 0 && code.length > 0) {
				List list = getInststartpos();

				if (list.contains(new Integer(synchEnd + 2))
						&& list.contains(new Integer(synchEnd + 1))) {
					int i = synchEnd + 2;
					if (code[i - 1] == JvmOpCodes.ARETURN) {
						synchEnd = synchEnd + 2;
					}
					if (code[i - 1] == JvmOpCodes.IRETURN) {
						synchEnd = synchEnd + 2;
					}
					if (code[i - 1] == JvmOpCodes.LRETURN) {
						synchEnd = synchEnd + 2;
					}
					if (code[i - 1] == JvmOpCodes.FRETURN) {
						synchEnd = synchEnd + 2;
					}
					if (code[i - 1] == JvmOpCodes.DRETURN) {
						synchEnd = synchEnd + 2;
					}

				}
			}
			this.synchEnd = synchEnd;
		}

		public boolean isWasSynchClosed() {
			return wasSynchClosed;
		}

		public void setWasSynchClosed(boolean wasSynchClosed) {
			this.wasSynchClosed = wasSynchClosed;
		}

		public boolean isIgnore() {
			return ignore;
		}

		public void setIgnore(boolean ignore) {
			this.ignore = ignore;
		}

	}

	public boolean containsStatic() {
		boolean flag = false;

		for (int indx = 0; indx < accessSpecifiers.length; indx++) {
			if (accessSpecifiers[indx].equals("static")) {
				flag = true;
			}
		}

		return flag;
	}

	public void storeAllExcepptionTableRefs(ArrayList al) {
		this.AllExceptionTables = al;
	}

	public ArrayList getAllExceptionTables() {
		return AllExceptionTables;
	}

	private ArrayList AllExceptionTables = null;

	public int getParametersArrayLength() {
		return parameters.size();
	}

	/***************************************************************************
	 * Some util methods Related to ExceptionTable Written to avoid cluttering
	 * the code in elsewhere in many places with the same piece of code
	 * 
	 */

	// EXCEPTIONTABLE UTILITY METHOD
	// List may itself contain many arraylists
	public ArrayList getExceptionTablesSortedByStartHandlerPC() {
		ArrayList completeList = new ArrayList();
		Iterator it = AllExceptionTables.iterator();
		while (it.hasNext()) {
			ArrayList list = new ArrayList();
			ArrayList prevList = null;
			ExceptionTable table = (ExceptionTable) it.next();
			int hpc = table.getStartOfHandlerForGuardRegion();

			// Now we Iterate again to capture the set of tables
			// having the handler PC value=to hpc

			for (int s = 0; s < AllExceptionTables.size(); s++) {

				ExceptionTable tab = (ExceptionTable) AllExceptionTables.get(s);
				if (tab.getStartOfHandlerForGuardRegion() == hpc)
					list.add(tab);

			}

			Iterator compIt = completeList.iterator();
			if (compIt.hasNext()) {
				boolean p = false;
				while (compIt.hasNext()) {
					ArrayList list1 = (ArrayList) compIt.next();
					boolean identical = compare(list1, list);
					if (identical == false) {
						p = false;
						continue;
					} else if (identical == true) {
						p = true;
						break;
					}
				}
				if (p == false)
					completeList.add(list);

			} else
				completeList.add(list);
		}

		return completeList;
	}

	// EXCEPTIONTABLE UTILITY METHOD
	public ExceptionTable getExceptionTableForSmallestStartOfGuard(
			ArrayList list) {
		ExceptionTable table = null;
		if (list.size() > 0) {
			Iterator it = list.iterator();
			int smallestStartOfGuard = ((ExceptionTable) list.get(0))
					.getStartOfGuardRegion();
			table = (ExceptionTable) list.get(0);
			while (it.hasNext()) {
				ExceptionTable tab = (ExceptionTable) it.next();
				int temp = tab.getStartOfGuardRegion();
				if (temp < smallestStartOfGuard) {
					smallestStartOfGuard = temp;
					table = tab;
				}
			}
		}
		return table;

	}

	// EXCEPTIONTABLE UTILITY METHOD
	public ArrayList getSortedExceptionTablesForEndOfGuard(ArrayList input) {
		ArrayList output = new ArrayList();
		HashMap map = new HashMap();
		int counter = 0;
		if (input.size() > 0) {
			int ends[] = new int[input.size()];
			Iterator it = input.iterator();
			while (it.hasNext()) {
				ExceptionTable tab = (ExceptionTable) it.next();
				int temp = tab.getEndOfGuardRegion();
				map.put(tab, new Integer(temp));
				ends[counter] = temp;
				counter++;
			}
			Arrays.sort(ends);
			for (int s = 0; s < ends.length; s++) {
				int current = ends[s];
				Iterator mapIt = map.entrySet().iterator();
				while (mapIt.hasNext()) {
					Map.Entry entry = (Map.Entry) mapIt.next();
					ExceptionTable key = (ExceptionTable) entry.getKey();
					Integer val = (Integer) entry.getValue();
					if (val.intValue() == current) {
						output.add(key);
						break;
					}
				}

			}

		}

		return output;
	}

	// EXCEPTIONTABLE UTILITY METHOD
	public ArrayList getSortedExceptionTablesForStartOfGuard(ArrayList input) {
		ArrayList output = new ArrayList();
		HashMap map = new HashMap();
		int counter = 0;
		if (input.size() > 0) {
			int starts[] = new int[input.size()];
			Iterator it = input.iterator();
			while (it.hasNext()) {
				ExceptionTable tab = (ExceptionTable) it.next();
				int temp = tab.getStartOfGuardRegion();
				map.put(tab, new Integer(temp));
				starts[counter] = temp;
				counter++;
			}
			Arrays.sort(starts);
			for (int s = 0; s < starts.length; s++) {
				int current = starts[s];
				Iterator mapIt = map.entrySet().iterator();
				while (mapIt.hasNext()) {
					Map.Entry entry = (Map.Entry) mapIt.next();
					ExceptionTable key = (ExceptionTable) entry.getKey();
					Integer val = (Integer) entry.getValue();
					if (val.intValue() == current) {
						output.add(key);
						break;
					}
				}

			}

		}

		return output;
	}

	// EXCEPTIONTABLE UTILITY METHOD
	private boolean compare(ArrayList list1, ArrayList list2) {
		boolean identical = false;
		if (list1.size() == list2.size()) {
			Iterator it1 = list1.iterator();
			Iterator it2 = list2.iterator();
			while (it1.hasNext() && it2.hasNext()) {
				ExceptionTable table1 = (ExceptionTable) it1.next();
				ExceptionTable table2 = (ExceptionTable) it2.next();
				if (table1.equals(table2)) {
					identical = true;
					continue;
				} else {
					identical = false;
					break;
				}
			}

		}
		return identical;

	}

	public ArrayList getAllTablesWhoseGuardsAreTries() {
		ArrayList list = new ArrayList();
		Iterator it = AllExceptionTables.iterator();
		while (it.hasNext()) {
			ExceptionTable table = (ExceptionTable) it.next();
			int ii = 1;
			if (table.getTypeOfGuardRegion().equals("try")) {
				boolean invalid = checkForInvalidTryBlock(table,
						AllExceptionTables); // Check 1
				if (!invalid) // Check 2
				{
					int thisEndPc = table.getEndPC();
					for (int x = 0; x < list.size(); x++) {
						ExceptionTable tab = (ExceptionTable) list.get(x);
						int endg = tab.getEndOfGuardRegion();
						if (endg == thisEndPc) {
							int gst = tab.getStartOfGuardRegion();
							int thisGst = table.getStartOfGuardRegion();
							if (thisGst > gst) {
								invalid = true;
								break;
							}

						}

					}
				}
				if (!invalid)
					list.add(table);
				else
					table.setTypeOfGuardRegion("");
			}
		}
		return (list.size() > 0) ? list : null;
	}

	public ArrayList getTablesWithGuardRange(int start, int end) {
		ArrayList tabList = new ArrayList();
		Iterator it = AllExceptionTables.iterator();
		while (it.hasNext()) {
			ExceptionTable t = (ExceptionTable) it.next();
			if (t.getStartOfGuardRegion() == start
					&& t.getEndOfGuardRegion() == end) {
				tabList.add(t);
			}
		}
		if (tabList.size() > 0)
			return tabList;
		else
			return null;
	}

	public ArrayList getCreatedTablesWithGuardRange(int start, int end) {
		ArrayList tabList = new ArrayList();
		Iterator it = getCreatedTableList().iterator();
		while (it.hasNext()) {
			ExceptionTable t = (ExceptionTable) it.next();
			if (t.getStartOfGuardRegion() == start
					&& t.getEndOfGuardRegion() == end) {
				tabList.add(t);
			}
		}
		if (tabList.size() > 0)
			return tabList;
		else
			return null;
	}

	public int getNumberOfCatchesForTry(ArrayList al) {
		int number = 0;
		Iterator it = al.iterator();
		while (it.hasNext()) {
			ExceptionTable table = (ExceptionTable) it.next();
			if (table.getExceptionName().equals("<any>") == false) {
				number++;
			}
		}
		return number;

	}

	public ArrayList sortTableListByStartOfHanlder(ArrayList al) {
		ArrayList sorted = new ArrayList();
		Iterator it = al.iterator();
		ExceptionTable first = null;
		Hashtable ht = new Hashtable();
		int allstarts[] = new int[al.size()];
		int counter = 0;
		if (al.size() > 0) {
			while (it.hasNext()) {
				ExceptionTable table = (ExceptionTable) it.next();
				int start = table.getStartOfHandlerForGuardRegion();
				ht.put(table, new Integer(start));
				allstarts[counter] = start;
				counter++;

			}
			Arrays.sort(allstarts);

			for (int k = 0; k < allstarts.length; k++) {
				int s = allstarts[k];
				Iterator htIt = ht.entrySet().iterator();
				while (htIt.hasNext()) {
					Map.Entry entry = (Map.Entry) htIt.next();
					ExceptionTable tb = ((ExceptionTable) entry.getKey());
					Integer val = (Integer) entry.getValue();
					if (val.intValue() == s)
						sorted.add(tb);
				}
			}

		}
		return (al.size() > 0) ? sorted : null;

	}

	public boolean doesTryHaveFinally(ArrayList al) {
		boolean hasFianlly = false;
		Iterator it = al.iterator();
		while (it.hasNext()) {
			ExceptionTable table = (ExceptionTable) it.next();
			if (table.getExceptionName().equals("<any>") == true) {
				hasFianlly = true;
				break;
			}
		}
		return hasFianlly;

	}

	public ExceptionTable getCatchBlk(int i) {
		ExceptionTable t = null;
		for (int s = 0; s < AllExceptionTables.size(); s++) {
			ExceptionTable table = (ExceptionTable) AllExceptionTables.get(s);
			if (table.getStartOfGuardRegion() == i
					&& table.getExceptionName().equals("<any>") == true) {

				t = table;
				break;

			}

		}
		return t;

	}

	public ArrayList getAllTryBlocksForMethod() {
		return allTryBlocksForMethod;
	}

	public void setAllTryBlocksForMethod(ArrayList allTryBlocksForMethod) {
		this.allTryBlocksForMethod = allTryBlocksForMethod;
	}

	public ExceptionTable getExceptionTableWithBiggestStartOfGuard(
			ArrayList subList) {

		ExceptionTable table = null;
		if (subList != null && subList.size() > 0) {
			ExceptionTable t = (ExceptionTable) subList.get(0);
			int start = t.getStartOfGuardRegion();
			for (int c = 1; c < subList.size(); c++) {
				ExceptionTable t1 = (ExceptionTable) subList.get(c);
				if (t1.getStartOfGuardRegion() > start) {
					start = t1.getStartOfGuardRegion();
					table = t1;
				}
			}
		}
		return table;
	}

	public ArrayList getTableListWithGuardsSpecified(int startOfGuard,
			int endOfGuard) {

		ArrayList list = new ArrayList();
		Iterator all = this.AllExceptionTables.iterator();
		while (all.hasNext()) {
			ExceptionTable t = (ExceptionTable) all.next();
			if (t.getStartOfGuardRegion() == startOfGuard
					&& t.getEndOfGuardRegion() == endOfGuard) {
				list.add(t);
			}
		}

		return (list.size() > 0) ? list : null;

	}

	public void addNewExceptionTable(ExceptionTable t) {
		exceptionTablesCreated.add(t);
	}

	public ArrayList getCreatedTableList() {
		return (exceptionTablesCreated.size() > 0) ? exceptionTablesCreated
				: null;

	}

	public ArrayList getAllCreatedTablesWhoseGuardsAreTries() {
		ArrayList temp = new ArrayList();
		ArrayList created = this.getCreatedTableList();
		Iterator it = created.iterator();
		while (it.hasNext()) {
			ExceptionTable table = (ExceptionTable) it.next();
			if (table.getTypeOfGuardRegion().equals("try"))
				temp.add(table);
		}

		return temp;
	}

	public ArrayList getTablesWithFinally() {
		ArrayList temp = new ArrayList();
		Iterator t = AllExceptionTables.iterator();
		while (t.hasNext()) {
			ExceptionTable t1 = (ExceptionTable) t.next();
			if (t1.getExceptionName().equals("<any>"))
				temp.add(t1);
		}
		return (temp.size() > 0) ? temp : null;
	}

	public ArrayList getTryTablesWithPCRange(int start, int end) {
		ArrayList temp = new ArrayList();
		Iterator t = AllExceptionTables.iterator();
		while (t.hasNext()) {
			ExceptionTable t1 = (ExceptionTable) t.next();
			if (t1.getExceptionName().equals("<any>") == false
					&& t1.getStartPC() == start && t1.getEndPC() == end)
				temp.add(t1);
		}
		return (temp.size() > 0) ? temp : null;

	}

	public void addSwitchBlock(Switch switchblk) {
		if (switchblk != null)
			allswitchesformethod.add(switchblk);
	}

	public ArrayList getAllSwitchBlksForMethod() {
		if (allswitchesformethod.size() > 0)
			return allswitchesformethod;
		else
			return null;
	}

	private void setReturnTypeAsObjectOrArrayType(boolean b) {
		returnTypeAsObjectType = b;
	}

	public boolean isReturnTypeAsObjectType() {
		return returnTypeAsObjectType;
	}

	private boolean returnTypeAsObjectType;

	public void setTablesForSynchronizedBlks(ArrayList synchronizedTables) {
		this.synchronizedTables = synchronizedTables;
	}

	public ArrayList getSynchronizedTables() {
		return synchronizedTables;
	}

	private ArrayList synchronizedTables;

	public ArrayList getBehaviourLoops() {
		return behaviourLoops;
	}

	public void setLoops(ArrayList loop) {
		if (loop != null)
			this.behaviourLoops = loop;
	}

	public ArrayList getTablesWithGuardStart(int start) {
		ArrayList temp = new ArrayList();
		ArrayList alltables = this.getAllExceptionTables();
		for (int s = 0; s < alltables.size(); s++) {
			ExceptionTable table = (ExceptionTable) alltables.get(s);
			if (table.getStartOfGuardRegion() == start) {
				temp.add(table);
			}
		}

		return temp;
	}

	public LocalVariableStructure getStructure() {
		return structure;
	}

	public void setStructure(LocalVariableStructure structure) {
		this.structure = structure;
	}

	private boolean checkForInvalidTryBlock(ExceptionTable table, ArrayList list) {

		for (int z = 0; z < list.size(); z++) {
			ExceptionTable t = (ExceptionTable) list.get(z);
			if (t != table) {
				java.lang.String type = t.getHandlerBlockName();
				if (type != null && type.indexOf("Catch") != -1) {
					int cstart = t.getStartOfHandler();
					int tend = t.getEndPC();
					int thisStart = table.getStartPC();
					// java.lang.String thisType=table.getHandlerBlockName();
					if (thisStart > tend && thisStart < cstart
							&& cstart == table.getStartOfHandler()) {
						return true;

					}

				}
			}
		}
		return false;

	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	private AbstractRuntimeAnnotation rtvisiblea;
	
	private AbstractRuntimeAnnotation rtinvisiblea;
	
	private AbstractMethodParamterAnnotaions rvpa;
	
	private AbstractMethodParamterAnnotaions rinvpa;

	public AbstractRuntimeAnnotation getRuntimeVisibleAnnotations() {
		return rtvisiblea;
	}

	public void setRuntimeVisibleAnnotations(AbstractRuntimeAnnotation rva) {
		this.rtvisiblea = rva;
	}
	public AbstractRuntimeAnnotation getRuntimeInvisibleAnnotations() {
		return rtinvisiblea;
	}

	public void setRuntimeInvisibleAnnotations(AbstractRuntimeAnnotation rva) {
		this.rtinvisiblea = rva;
	}
	public AbstractMethodParamterAnnotaions getRuntimeVisibleParameterAnnotations() {
		return rvpa;
	}

	public void setRuntimeVisibleParameterAnnotations(AbstractMethodParamterAnnotaions rvpa) {
		this.rvpa = rvpa;
	}
	public AbstractMethodParamterAnnotaions getRuntimeInvisibleParameterAnnotations() {
		return rinvpa;
	}

	public void setRuntimeInvisibleParameterAnnotations(AbstractMethodParamterAnnotaions rvpa) {
		this.rinvpa = rvpa;
	}

	public LocalVariableTypeTable getTypeStructure() {
		return typeStructure;
	}

	public void setTypeStructure(LocalVariableTypeTable typeStructure) {
		this.typeStructure = typeStructure;
	}

	public String getGenericSignatureForLocalVariable(int index){
		if(typeStructure != null){
			return typeStructure.getGenericSignatureForLocalVariable(index);
		}
		return null;
	}
}
