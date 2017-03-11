/*
 *  LocalVariable.java Copyright (c) 2006,07 Swaroop Belur 
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
package net.sf.jdec.core;

import net.sf.jdec.commonutil.Objects;
import net.sf.jdec.config.Configuration;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.settings.SettingsStore;

/**
 * 
 * @author swaroop belur (belurs)
 * 
 */
public class LocalVariable implements Comparable {

	private int loadStorePosUsedToCreateVraiable = -1;

	private java.lang.String passedDataTypeWhileCreatingWithOutMinusG = "";

	private boolean nameAlreadyReset = false;

	private boolean methodParameterVariable = false;

	private java.lang.String methodName = "";

	private java.lang.String dataType = "";

	private java.lang.String varName = "";

	private java.lang.String tempVarName = null;

	private int indexPos = -1;

	private boolean declarationGenerated = false;

	private boolean isVisited = false;

	private boolean isPrimitive = false;

	private boolean isBoolean = false;

	private boolean isWithinBlock = false;

	private int blockIndex = -1;

	// Used to indicate that variable's info
	// was not present in code
	// Code was eithe compiled with -g:none OR for some reason this
	// Variables's info was not present in compiled code
	private boolean wasCreated = false;

	boolean primitive = false;

	int numberOfBrackets = -1;

	private boolean iamprimitive = false;

	private int blockStart = -1;

	private int blockEnd = -1;

	public boolean wasCreated() {
		return wasCreated;
	}

	public void setWasCreated(boolean wasCreated) {
		this.wasCreated = wasCreated;
	}

	public LocalVariable() {
		super();

	}

	public LocalVariable(java.lang.String mname, java.lang.String vname,
			int index) {
		this.methodName = mname;
		this.varName = vname;
		this.indexPos = index;
	}

	public LocalVariable(java.lang.String methodName,
			java.lang.String dataType, java.lang.String varName, int index) {
		this.methodName = methodName;
		this.dataType = parse(dataType);
		this.varName = varName;
		indexPos = index;
		String dt = this.dataType;

		// For Imports
		if (!iamprimitive) {
			String si = Configuration.getShowImport();
			if (si.equalsIgnoreCase("true")) {
				String name = dt;
				if (name.indexOf("/") != -1)
					name = name.replaceAll("/", ".");
				ConsoleLauncher.addImportClass(name);
				String simpleName = "";
				int in = name.lastIndexOf(".");
				if (in != -1) {
					simpleName = name.substring(in + 1);
				} else
					simpleName = name;
				dt = simpleName;
			} else {
				dt = this.dataType;
			}
			this.dataType = dt;
		}

	}

	public LocalVariable(java.lang.String methodName,
			java.lang.String dataType, java.lang.String varName, int index,
			boolean declarationGenerated) {
		this.methodName = methodName;
		this.dataType = parse(dataType);
		this.varName = varName;
		indexPos = index;
		this.declarationGenerated = declarationGenerated;

		if (!iamprimitive) {
			String dt = this.dataType;
			// For Imports
			String si = Configuration.getShowImport();
			if (si.equalsIgnoreCase("true")) {
				String name = dt;
				if (name.indexOf("/") != -1)
					name = name.replaceAll("/", ".");
				ConsoleLauncher.addImportClass(name);
				String simpleName = "";
				int in = name.lastIndexOf(".");
				if (in != -1) {
					simpleName = name.substring(in + 1);
				} else
					simpleName = name;
				dt = simpleName;
			} else {
				dt = this.dataType;
			}
			this.dataType = dt;
		}

	}

	private java.lang.String parse(java.lang.String input) {
		java.lang.String type = "";
		java.lang.String brackets = "";
		if (input.equals("I")) {
			// System.out.println(type);
			type = "int";
			iamprimitive = true;
		} else if (input.equals("B")) {
			type = "byte";
			iamprimitive = true;
		} else if (input.equals("C")) {
			type = "char";
			iamprimitive = true;
		} else if (input.equals("S")) {
			type = "short";
			iamprimitive = true;
		} else if (input.equals("Z")) {
			type = "boolean";
			iamprimitive = true;
		} else if (input.equals("F")) {
			type = "float";
			iamprimitive = true;
		} else if (input.equals("D")) {
			type = "double";
			iamprimitive = true;
		} else if (input.equals("J")) {
			type = "long";
			iamprimitive = true;
		} else if (input.startsWith("L")) {
			type = input.substring(1);
			if (type.indexOf(";") != -1)
				type = type.substring(0, type.indexOf(";"));
		} else if (input.startsWith("[")) {
			// System.out.println(type);
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
			if (b == true) {
				primitive = true;
				numberOfBrackets = lastBracket + 1;
				type = parse(className);
			} else {
				java.lang.String temp = "";
				for (int c = 0; c < lastBracket + 1; c++)
					temp += "[]";
				type = className + " " + temp;
			}

		}
		if (primitive) {
			for (int s = 0; s < numberOfBrackets; s++)
				brackets += "[]";
			primitive = false;
		}
		return type + " " + brackets;

	}

	public boolean isPrimitive() {
		return isPrimitive;
	}

	public boolean isBoolean() {
		return isBoolean;
	}

	private boolean AmIPrimitive(String className) {
		AmIBoolean(className);
		if (className.equals("I") || className.equals("B")
				|| className.equals("C") || className.equals("S")
				|| className.equals("F") || className.equals("D")
				|| className.equals("J") || className.equals("Z")) {
			isPrimitive = true;
			return true;
		} else {
			isPrimitive = false;
			return false;
		}

	}

	private boolean AmIBoolean(String className) {
		if (className.equals("Z")) {
			isBoolean = true;
			return true;
		} else {
			isBoolean = false;
			return false;
		}
	}

	public java.lang.String toString() {
		java.lang.String desc = "[";
		desc += this.dataType + "  " + this.varName + "  " + this.blockStart
				+ "  " + this.blockEnd + "  " + this.blockIndex + "   "
				+ this.indexPos + "[--Index Pos--]";
		desc += "]";
		return desc;

	}

	public java.lang.String getDataType() {
		return dataType.trim();
	}

	public void setDataType(java.lang.String passedDataType) {

		/***********************************************************************
		 * Enhancement Request for 1.2.1 Changes for showing better local var
		 * names
		 * 
		 */
		// Start
	    boolean check = true;
		if(passedDataType != null && passedDataType.indexOf(".") != -1){
			int lastdot= passedDataType.lastIndexOf(".");
			if(lastdot != -1 && (lastdot+1) < passedDataType.length()){
				String tmp = passedDataType.substring(lastdot+1);
				if(this.dataType != null && tmp != null && tmp.trim().equals(this.dataType.trim())){
					check = false;
				}
			}
			
		}
		if (check && passedDataType != null
				&& passedDataType.equals(this.dataType) == false
				&& nameAlreadyReset) {
			nameAlreadyReset = false;
		}
		
		

		String name = null;
		boolean resetname = true;
		if (varName != null && varName.trim().indexOf("this") != -1) {
			resetname = false;
		}
		ClassDescription cd =ClassDescription.ref;
		if (!cd
				.isClassCompiledWithMinusG()
				&& !nameAlreadyReset && resetname) {
			StringBuffer classn = new StringBuffer();
			String pkg = getPkgFromType(passedDataType, classn);

			String prefix = SettingsStore.getVariablesettings().getPrefixName(
					pkg, classn.toString(), passedDataType);
			if (prefix != null) {
				int num = SettingsStore.getVariablesettings()
						.getPrefixOccurence(prefix);
				setVarName(prefix + num);
				nameAlreadyReset = true;
			}
		}
		// End change

		String si = Configuration.getShowImport();

		String typename = passedDataType;
		if (typename.indexOf("/") != -1)
			typename = name.replaceAll("/", ".");
		String simpleName = "";
		int index = typename.lastIndexOf(".");
		if (index != -1) {
			simpleName = typename.substring(index + 1);
		} else
			simpleName = typename;
		StringBuffer fulltype = new StringBuffer();
		if (si.equalsIgnoreCase("true")
				&& !LocalVariableHelper.simpleTypeAlreadyAddedToImports(
						simpleName, fulltype)) {
			ConsoleLauncher.addImportClass(typename);
			this.dataType = simpleName;
		} else {
			if (fulltype.toString().equals(passedDataType)
					&& si.equalsIgnoreCase("true")) {
				ConsoleLauncher.addImportClass(typename);
				this.dataType = simpleName;
			} else {
				this.dataType = passedDataType;
			}

		}

	}

	private String getPkgFromType(String dataType, StringBuffer className) {
		if (Objects.isEmpty(dataType))
			return null;
		int dot = dataType.lastIndexOf(".");
		if (dot == -1) {
			dot = dataType.lastIndexOf("/");
		}
		if (dot == -1) {
			return null;
		}
		className.append(dataType.substring(dot + 1));
		return dataType.substring(0, dot);
	}

	public int getIndexPos() {
		return indexPos;
	}

	public void setIndexPos(int indexPos) {
		this.indexPos = indexPos;
	}

	public java.lang.String getMethodName() {
		return methodName;
	}

	public void setMethodName(java.lang.String methodName) {
		this.methodName = methodName;
	}

	public java.lang.String getVarName() {
		return varName;
	}

	public void setVarName(java.lang.String varName) {
		this.varName = varName;
	}

	public boolean isDeclarationGenerated() {
		return declarationGenerated;
	}

	public void setDeclarationGenerated(boolean declarationGenerated) {
		this.declarationGenerated = declarationGenerated;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public boolean isWithinBlock() {
		return isWithinBlock;
	}

	public void setWithinBlock(boolean isWithinBlock) {
		this.isWithinBlock = isWithinBlock;
	}

	public int getBlockIndex() {
		return blockIndex;
	}

	public void setBlockIndex(int blockIndex) {
		this.blockIndex = blockIndex;
	}

	public int getBlockEnd() {
		return blockEnd;
	}

	public void setBlockEnd(int blockEnd) {
		this.blockEnd = blockEnd;
	}

	public int getBlockStart() {
		return blockStart;
	}

	public void setBlockStart(int blockStart) {
		this.blockStart = blockStart;
	}

	public void setPrimitive(boolean p) {
		this.primitive = p;

	}

	public int getLoadOrStorePosUsedToCreateVraiable() {
		return loadStorePosUsedToCreateVraiable;
	}

	public void setLoadORStorePosUsedToCreateVraiable(
			int loadPosUsedToCreateVraiable) {
		this.loadStorePosUsedToCreateVraiable = loadPosUsedToCreateVraiable;
	}

	public int compareTo(Object o) {

		int cmp = -1;
		if (o instanceof LocalVariable) {
			LocalVariable local = (LocalVariable) o;
			int index = local.getIndexPos();
			int thisIndex = this.getIndexPos();
			if (thisIndex < index)
				return -1;
			if (thisIndex > index)
				return 1;
			else
				return 0;
		} else
			return -1;

	}

	public String getTempVarName() {
		return tempVarName;
	}

	public void setTempVarName(String tempVarName) {
		this.tempVarName = tempVarName;
	}

	public String getPassedDataTypeWhileCreatingWithOutMinusG() {
		return passedDataTypeWhileCreatingWithOutMinusG;
	}

	public void setPassedDataTypeWhileCreatingWithOutMinusG(
			String passedDataTypeWhileCreatingWithOutMinusG) {
		this.passedDataTypeWhileCreatingWithOutMinusG = passedDataTypeWhileCreatingWithOutMinusG;
	}

	public boolean isMethodParameterVariable() {
		return methodParameterVariable;
	}

	public void setMethodParameterVariable(boolean isMethodParam) {
		this.methodParameterVariable = isMethodParam;
	}

}
