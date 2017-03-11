/*
 *  Behaviour.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jdec.blocks.IFBlock;
import net.sf.jdec.commonutil.Objects;
import net.sf.jdec.constantpool.AbstractMethodParamterAnnotaions;
import net.sf.jdec.constantpool.AbstractRuntimeAnnotation;
import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GeneratedIfTracker;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.LocalVariableStructure;
import net.sf.jdec.core.LocalVariableTypeTable;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.core.ShortcutAnalyser;
import net.sf.jdec.core.GeneratedIfTracker.IfStore;
import net.sf.jdec.util.Util;

/**
 * This Is a Class to indicate any code processing an Object of this type that
 * it is handling an object of a Class representing the behaviour of the Class.
 * According the Java terminology behaviour represents a method of a class <p/>
 * <p/> <p/> <br>
 * <br>
 * Example representation: Any Method or constructor<br>
 * Example Usage: In Disassembling Code Section
 */

public abstract class Behaviour {

	private StringBuffer codeAsBuffer = new StringBuffer();

	protected JavaClass classRef;

	private String signature = "";

	private Set methodIfs = new HashSet();

	protected Map datatypesForParams = new HashMap();

	private ArrayList variablesatfront = new ArrayList();

	private java.lang.String vmInstructions = "";

	private java.lang.String codeStatements = "";

	private OperandStack opStack = new OperandStack();

	private boolean hasBeenDissassembled = false;

	private Behaviour parentBehaviour = null;

	private LocalVariableStructure methodLocalVariables = null;

	public abstract java.lang.String getBehaviourName();

	public abstract void setShortCutAnalyser(ShortcutAnalyser sa);

	public abstract ShortcutAnalyser getShortCutAnalyser();

	private Map labels = new HashMap();

	private LocalVariableTypeTable typeTable;

	public LocalVariableTypeTable getTypeTable() {
		return typeTable;
	}

	public void setTypeTable(LocalVariableTypeTable typeTable) {
		this.typeTable = typeTable;
	}

	public abstract byte[] getCode();

	/**
	 * Called From Disassembled Section of the project
	 * 
	 * @param code
	 *            represents the actual blocks for this method interpreted from
	 *            the bytes of the code for this behaviour
	 */

	public void setVMInstructions(java.lang.String code) {
		vmInstructions = code;
	}

	public java.lang.String getVMInstructions() {
		return vmInstructions;
	}

	public java.lang.String getUserFriendlyMethodAccessors() {
		return null;
	}

	public java.lang.String getReturnType() {
		return null;
	}

	public java.lang.String getUserFriendlyMethodParams() {
		return null;
	}

	public abstract int getNumberofparamters();

	public abstract java.lang.String getStringifiedParameters();

	public abstract String[] getMethodParams();

	public OperandStack getOpStack() {
		return opStack;
	}

	public void setOpStack(OperandStack opStack) {
		this.opStack = opStack;
	}

	public boolean isHasBeenDissassembled() {
		return hasBeenDissassembled;
	}

	public void setHasBeenDissassembled(boolean hasBeenDissassembled) {
		this.hasBeenDissassembled = hasBeenDissassembled;
	}

	public Behaviour getParentBehaviour() {
		return parentBehaviour;
	}

	public void setParentBehaviour(Behaviour parentBehaviour) {
		this.parentBehaviour = parentBehaviour;
	}

	public java.lang.String getCodeStatements() {
		return codeStatements;
	}

	public void setVariablesAtFront(ArrayList list) {
		variablesatfront = list;
	}

	private void setCodeStatements(java.lang.String codeAsString) {

		this.codeStatements = codeAsString;
		this.codeStatements = removeDummyLabels();
		this.codeStatements = removeLeftOverMarkers0();
		this.codeStatements = removeLeftOverMarkers1();
		// this.codeStatements=correctAnyInvalidSuperPosition(this.codeStatements);
		this.codeStatements = removeExtraSpaces0(this.codeStatements);
		this.codeStatements = removeExtraSpaces1(this.codeStatements);
		this.codeStatements = removeExtraJdecLabels(this.codeStatements); // Hack
		this.codeStatements = this.codeStatements.replaceAll("this\\$",
				"This\\$");
		// this.codeStatements=removeOrphanJDECLabels(this.codeStatements);
		// TODO: Need to write better code for stripping extra spaces in code
		this.codeStatements = this.codeStatements.replaceAll("\\)   ", "\\)");
		this.codeStatements = this.codeStatements.replaceAll("\\)   ", "\\)");
		this.codeStatements = this.codeStatements.replaceAll("\\)   ", "\\)");
		this.codeStatements = addVariablesAtFront(this.codeStatements);
		// this.codeStatements = this.codeStatements.replaceAll("\r\n\r\n",
		// "\r\n");
		replaceDoWhileMarkersWithIfs();
		// this.codeStatements=Util.formatDecompiledStatement(this.codeStatements);
	}

	private void replaceDoWhileMarkersWithIfs() {
		if (GlobalVariableStore.getDowhileloopmarkers().size() > 0) {
			int size = GlobalVariableStore.getDowhileloopmarkers().size();
			String ls = "//INSERT IFCODE FOR DOWHILE HERE";
			String ifs = "<IF_AT_LOOP_END_BEGIN_";
			String ife = "<IF_AT_LOOP_END_END_";
			for (int i = 0; i < size; i++) {
				int temp = ((Integer) GlobalVariableStore
						.getDowhileloopmarkers().get(i)).intValue();
				ls = ls + temp;
				ifs = ifs + temp + "_>";
				ife = ife + temp + "_>";
				int lindex = codeStatements.indexOf(ls);
				if (lindex != -1) {
					int start = codeStatements.indexOf(ifs, lindex + 1);
					if (start != -1) {
						int end = codeStatements.indexOf(ife, start + 1);
						if (end != -1) {
							int tempStart = start;
							start = start + ifs.length();
							String reqdif = codeStatements
									.substring(start, end);
							String sub1 = codeStatements
									.substring(0, tempStart);
							String sub2 = codeStatements.substring(end
									+ ife.length());
							codeStatements = sub1 + sub2;
							reqdif = reqdif.replaceAll("\\$", "\\\\$");
							codeStatements = codeStatements.replaceFirst(ls,
									reqdif);

						}
					} else {
						codeStatements = codeStatements.replaceFirst(ls, "");
					}
				}

				ifs = "<IF_AT_LOOP_END_BEGIN_";
				ife = "<IF_AT_LOOP_END_END_";
				ls = "//INSERT IFCODE FOR DOWHILE HERE";

			}
		}
	}

	private void removeInvalidElseBreaksAtIfEnd() {
		Iterator iterator = getMethodIfs().iterator();
		while (iterator.hasNext()) {
			IFBlock ifb = (IFBlock) iterator.next();
			if (ifb.isElsebreakinvalid()) {
				int start = ifb.getIfStart();
				String marker = "<elsebreak" + start + ">";
				String endmarker = "</elsebreak" + start + ">";
				int markerIndex = codeAsBuffer.indexOf(marker);
				if (markerIndex != -1) {
					int endmarkerIndex = codeAsBuffer.indexOf(endmarker,
							markerIndex + 1);
					if (endmarkerIndex != -1) {
						endmarkerIndex += endmarker.length();
						/*
						 * if ((endmarkerIndex + 1) < codeAsBuffer.length()) {
						 * endmarkerIndex = endmarkerIndex + 1; }
						 */
						if (endmarkerIndex < codeAsBuffer.length()) {
							codeAsBuffer.replace(markerIndex, endmarkerIndex,
									"");
						}

					}

				}
			} else {
				int start = ifb.getIfStart();
				String marker = "<elsebreak" + start + ">";
				int markerIndex = codeAsBuffer.indexOf(marker);
				if (markerIndex != -1) {
					int endmarkerIndex = markerIndex + marker.length();
					// endmarkerIndex = endmarkerIndex + 1;
					codeAsBuffer.replace(markerIndex, endmarkerIndex, "");

				}

				marker = "</elsebreak" + start + ">";
				markerIndex = codeAsBuffer.indexOf(marker);
				if (markerIndex != -1) {
					int endmarkerIndex = markerIndex + marker.length();
					// endmarkerIndex = endmarkerIndex + 1;
					codeAsBuffer.replace(markerIndex, endmarkerIndex, "");

				}

			}
		}
	}

	public void filterCode(GeneratedIfTracker generatedIfTracker) {
		removeInvalidElseBreaksAtIfEnd();
		setCodeStatements(codeAsBuffer.toString());
		removeInvalidGeneratedIfs(generatedIfTracker);
	}

	private void removeInvalidGeneratedIfs(GeneratedIfTracker generatedIfTracker) {
		Map ifs = generatedIfTracker.getIfs();
		Iterator iterator = ifs.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			IfStore aif = (IfStore) entry.getKey();
			if (aif.isInvalid()) {
				String name = aif.getName();
				String toRemove = name + "=false;";
				this.codeStatements = codeStatements.replaceAll(toRemove, "");
				toRemove = "boolean " + name + "= true;";
				this.codeStatements = codeStatements.replaceAll(toRemove, "");
			}
		}

	}

	public LocalVariableStructure getLocalVariables() {
		return methodLocalVariables;
	}

	public void setMethodLocalVariables(
			LocalVariableStructure methodLocalVariables) {
		this.methodLocalVariables = methodLocalVariables;
	}

	public abstract java.lang.String[] getMethodAccessors();

	public abstract void setExceptionTableList(ArrayList tables);

	public abstract ArrayList getExceptionTableList();

	public abstract ArrayList getAllTriesForMethod();

	public abstract void setAllTriesForMethod(ArrayList alltries);

	public abstract void setCreatedTableList(ArrayList list);

	public abstract ArrayList getCreatedTableList();

	protected ArrayList exceptionTables;

	public abstract String[] getExceptionTypes();

	public abstract java.lang.String getDeclaringClass();

	public abstract ArrayList getAllSwitchBlks();

	public abstract void setSynchronizedTableEntries(ArrayList list);

	public abstract ArrayList getSynchronizedEntries();

	public abstract ArrayList getBehaviourLoops();

	public abstract void setBehaviourLoops(ArrayList list);

	public abstract ArrayList getInstructionStartPositions();

	public Map getLabels() {
		return labels;
	}

	public void setLabels(Map labels) {
		this.labels = labels;
	}

	protected abstract java.lang.String removeDummyLabels();

	protected boolean isConstructor = false;

	// public abstract boolean isMethodConstructor();
	// public abstract void setMethodAsConstructor(boolean c);

	private java.lang.String correctAnyInvalidSuperPosition(
			java.lang.String codeS) {
		java.lang.String temp4 = codeS.trim();
		int index = temp4.indexOf("super(");
		if (index == 0)
			return codeS;

		index = codeS.indexOf("super(");
		if (index != -1) {
			java.lang.String temp = codeS.substring(0, index);
			int semi = codeS.indexOf(";", index);
			if (semi != -1) {
				java.lang.String temp2 = codeS.substring(semi + 1);
				java.lang.String temp3 = temp + temp2;
				java.lang.String s = codeS.substring(index, semi + 1);
				return "\t" + "\t" + "\t" + "\t" + s + "\n" + temp3;

			} else
				return codeS; // Invalid return actually
		} else
			return codeS;

	}

	public java.lang.String getShortDescription() {

		java.lang.String desc = "";
		desc += "\n[Name ]:" + this.getBehaviourName() + "\n";
		desc += "[Parameters ]:" + this.getUserFriendlyMethodParams() + "\n";
		desc += "[Accessors ]:" + this.getUserFriendlyMethodAccessors() + "\n";
		desc += "[Return TYpe ]:" + this.getReturnType() + "\n";
		desc += "[Belonging Class ]:" + this.getDeclaringClass() + "\n";
		return desc;

	}

	public java.lang.String removeLeftOverMarkers0() {
		ArrayList codeList = new ArrayList();
		java.lang.String temp = getCodeStatements();
		// if(getLabels()==null || getLabels().size()==0)return temp;
		int i = -1;
		int start = 0;
		String copy = getCodeStatements();
		i = copy.indexOf("#REPLACE_INT_", start);
		if (i == -1) {
			String sr = copy;
			sr = sr.replaceAll("\n\n", "\n");
			return sr;

		}
		while (i != -1) {
			int j = i + 13;
			int hash = copy.indexOf("#", (i + 1));
			java.lang.String num = copy.substring(j, hash);
			java.lang.String dummy = copy.substring(i, (hash + 1));
			boolean excep = false;
			try {
				int inum = Integer.parseInt(num);
			} catch (NumberFormatException ne) {
				excep = true;

			}
			if (!excep) {
				String tempCode = copy.substring(0, i);
				copy = copy.substring(i);
				copy = copy.replaceFirst(dummy, "");
				codeList.add(tempCode);
				i = copy.indexOf("#REPLACE_INT_", start);
				if (i == -1) {
					codeList.add(copy);
				}
			} else {
				String tempCode = copy.substring(0, j);
				copy = copy.substring(j);
				codeList.add(tempCode);
				i = copy.indexOf("#REPLACE_INT_", start);
				if (i == -1) {
					codeList.add(copy);
				}
			}

		}
		StringBuffer buf = new StringBuffer("");
		for (int k = 0; k < codeList.size(); k++) {
			buf.append(codeList.get(k).toString());
		}

		temp = buf.toString();
		temp = temp.replaceAll("\n\n", "\n");

		return temp;

	}

	public java.lang.String removeLeftOverMarkers1() {
		ArrayList codeList = new ArrayList();
		java.lang.String temp = getCodeStatements();
		// if(getLabels()==null || getLabels().size()==0)return temp;
		int i = -1;
		int start = 0;
		String copy = getCodeStatements();
		i = copy.indexOf("#VALUE", start);
		if (i == -1) {
			String sr = copy;
			sr = sr.replaceAll("\n\n", "\n");
			return sr;
		}

		while (i != -1) {
			int j = i + 6;
			int hash = copy.indexOf("#", (i + 1));
			java.lang.String num = copy.substring(j, hash);
			java.lang.String dummy = copy.substring(i, (hash + 1));
			boolean excep = false;
			try {
				int inum = Integer.parseInt(num);
			} catch (NumberFormatException ne) {
				excep = true;

			}
			if (!excep) {
				String tempCode = copy.substring(0, i);
				copy = copy.substring(i);
				copy = copy.replaceFirst(dummy, "");
				codeList.add(tempCode);
				i = copy.indexOf("#VALUE", start);
				if (i == -1) {
					codeList.add(copy);
				}
			} else {
				String tempCode = copy.substring(0, j);
				copy = copy.substring(j);
				codeList.add(tempCode);
				i = copy.indexOf("#VALUE", start);
				if (i == -1) {
					codeList.add(copy);
				}
			}

		}
		StringBuffer buf = new StringBuffer("");
		for (int k = 0; k < codeList.size(); k++) {
			buf.append(codeList.get(k).toString());
		}

		temp = buf.toString();
		temp = temp.replaceAll("\n\n", "\n");

		return temp;

	}

	public abstract boolean isMethodConstructor();

	private java.lang.String removeExtraSpaces0(java.lang.String codes) {

		java.lang.String temp = codes;
		int j = temp.indexOf("||");
		if (j == -1)
			return temp;
		else {
			while (j != -1) {
				java.lang.String temp1 = temp.substring(0, j + 2);
				int k = j + 2;
				if (k < temp.length()) {
					char c = temp.charAt(k);
					while (c == ' ') {
						k++;
						if (k >= temp.length())
							return codes;// Should Not happen
						c = temp.charAt(k);
					}
					temp1 = temp1 + " ";
					java.lang.String temp2 = temp.substring(k);
					temp1 = temp1 + temp2;
					temp = temp1;
					j = temp.indexOf("||", j + 2);

				} else {
					return codes; // should not happen
				}
			}

		}
		// temp=temp.replaceAll(" "," ");
		return temp;

	}

	private java.lang.String removeExtraSpaces1(java.lang.String codes) {

		java.lang.String temp = codes;
		int j = temp.indexOf("||");
		if (j == -1)
			return codes;
		else {
			while (j != -1) {
				int next = j + 2;
				if (next >= temp.length())
					return codes;
				char c = temp.charAt(next);
				while (c == ' ') {
					next++;
					if (next >= temp.length())
						return codes;
					c = temp.charAt(next);
				}
				if (c == ';') {

					do {
						next++;
						if (next >= temp.length())
							return codes;
						c = temp.charAt(next);
					} while (c == ' ' || c == '\n' || c == '\r' || c == '\t');

				}
				while (c != ' ') {
					if (c == ';') {
						do {
							next++;
							if (next >= temp.length())
								return codes;
							c = temp.charAt(next);
						} while (c == ' ' || c == '\n' || c == '\r'
								|| c == '\t');
					}

					next++;
					if (next >= temp.length())
						return codes;
					c = temp.charAt(next);
				}
				// next++;
				if ((next + 1) >= temp.length())
					return codes;

				java.lang.String temp1 = temp.substring(0, next);
				while (c == ' ') {
					next++;
					if (next >= temp.length())
						return codes;
					c = temp.charAt(next);
				}
				temp1 = temp1 + " ";
				temp1 = temp1 + temp.substring(next);
				temp = temp1;
				j = temp.indexOf("||", j + 2);
			}

		}
		return temp;
	}

	private java.lang.String removeOrphanJDECLabels(java.lang.String codes) {

		java.lang.String temp = codes;

		int fromwhere = temp.indexOf("jdecLABEL");
		if (fromwhere == -1)
			return codes;
		boolean processed = false;
		do {
			int index = fromwhere;
			int colon = temp.indexOf(":", index);
			int start = index + 9;
			java.lang.String lbl = "jdecLABEL" + temp.substring(start, colon);
			int nextIndex = temp.indexOf(lbl, index + 1);
			if (nextIndex == -1) {
				temp = temp.replaceAll(lbl + ":", "");
				processed = true;
			}
		} while ((fromwhere = temp.indexOf("jdecLABEL", fromwhere + 9)) != -1);

		return (processed == true) ? temp : codes;

	}

	public abstract void createExceptionTableInStringifiedForm();

	public abstract String getShortExceptionTableDetails();

	public abstract String getDetailedExceptionTableDetails();

	public abstract String getSynchTableDetails();

	private ArrayList varatfrontTemp = new ArrayList();

	private java.lang.String removeExtraJdecLabels(java.lang.String input) {

		java.lang.String output = input;
		if (output.indexOf("jdecLABEL") == -1)
			return output;
		int i = output.indexOf("jdecLABEL");
		while (i != -1) {
			int colon = output.indexOf(":", i);
			if (colon != -1) {
				java.lang.String lbl = output.substring(i, colon);
				try {
					String number = output.substring(i + 9, colon);
					int num = Integer.parseInt(number);
					int j = output.indexOf(lbl, colon + 1);
					if (j == -1) {
						output = output.replaceFirst(lbl + ":", "");
					}
					i = output.indexOf("jdecLABEL", colon);
				} catch (NumberFormatException ne) {
					i = output.indexOf("jdecLABEL", colon);
				}

			} else {
				i = output.indexOf("jdecLABEL", i + 8);
			}

		}
		return output;
	}

	// Bug Here
	private String addVariablesAtFront(String code) {
		if (variablesatfront == null || variablesatfront.size() == 0)
			return code;
		String front = "";
		for (int z = 0; z < variablesatfront.size(); z++) {
			DecompilerHelper.VariableAtFront v = (DecompilerHelper.VariableAtFront) variablesatfront
					.get(z);
			String temp = v.getType() + " " + v.getName() + "= "
					+ v.getInitial() + ";\n";
			temp = Util.formatFieldsOrMethodHeader(temp, "field");
			varatfrontTemp.add("   " + temp);
		}
		varatfrontTemp = removeDuplicates(varatfrontTemp);
		for (int k = 0; k < varatfrontTemp.size(); k++) {
			String t = (String) varatfrontTemp.get(k);
			front += t;
			if (!t.endsWith("\n")) {
				front += "\n";
			}
		}
		boolean added = false;
		if (this instanceof ConstructorMember) {

			int m = code.indexOf("super(");
			if (m == -1) {
				m = code.indexOf("this(");
			}
			if (m != -1) {

				int semi = code.indexOf(";", m);
				if (semi != -1) {
					String t1 = code.substring(0, semi + 1);
					String t2 = code.substring(semi + 1);
					code = t1 + "\n" + front + t2;
					added = true;
				}
			}
		}
		if (added == false)
			code = front + code;
		return code;

	}

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
					// currentItem=currentItem.trim();
					int prev = s - 1;
					String temp = "";
					if (prev >= 0 && prev < fullList.length) {
						temp = fullList[prev];
						// /temp=temp.trim();
						if (temp.equals(currentItem) == false) {
							// if(currentItem.indexOf("[")==-1) ??
							updatedList.add(currentItem);
						}
					}

				}

			}

		}
		return updatedList;

	}

	public JavaClass getClassRef() {
		return classRef;
	}

	public abstract void setClassRef(JavaClass classRef);

	public Map getDatatypesForParams() {
		return datatypesForParams;
	}

	public abstract void setDatatypesForParams(Map datatypesForParams);

	public Set getMethodIfs() {
		return methodIfs;
	}

	public String appendToBuffer(String string) {
		// if(string.indexOf("}") !=
		// -1)string=string+ExecutionState.getCurrentInstructionPosition();
		codeAsBuffer.append(string);
		return null;
	}

	public void replaceBuffer(String string) {
		codeAsBuffer = new StringBuffer(string);
	}

	public void replaceBuffer(StringBuffer stringbuffer) {
		codeAsBuffer = stringbuffer;
	}

	public StringBuffer getCodeAsBuffer() {
		return codeAsBuffer;
	}

	private AbstractRuntimeAnnotation rtvisiblea;

	private AbstractRuntimeAnnotation rtinvisiblea;

	private AbstractMethodParamterAnnotaions rvpa;

	private AbstractMethodParamterAnnotaions rinvpa;

	public AbstractRuntimeAnnotation getRuntimeInvisibleAnnotations() {
		return rtinvisiblea;
	}

	public void setRuntimeInvisibleAnnotations(AbstractRuntimeAnnotation rva) {
		this.rtinvisiblea = rva;
	}

	public AbstractRuntimeAnnotation getRuntimeVisibleAnnotations() {
		return rtvisiblea;
	}

	public void setRuntimeVisibleAnnotations(AbstractRuntimeAnnotation rva) {
		this.rtvisiblea = rva;
	}

	public AbstractMethodParamterAnnotaions getRuntimeVisibleParameterAnnotations() {
		return rvpa;
	}

	public void setRuntimeVisibleParameterAnnotations(
			AbstractMethodParamterAnnotaions rvpa) {
		this.rvpa = rvpa;
	}

	public AbstractMethodParamterAnnotaions getRuntimeInvisibleParameterAnnotations() {
		return rinvpa;
	}

	public void setRuntimeInvisibleParameterAnnotations(
			AbstractMethodParamterAnnotaions rvpa) {
		this.rinvpa = rvpa;
	}

	public String getGenericSignatureForLocalVariable(int index) {
		if (typeTable != null) {
			return typeTable.getGenericSignatureForLocalVariable(index);
		}
		return null;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	String genericRetType = "--";

	public String getReturnTypeMarker() {
		if (signature != null) {
			int brk = signature.indexOf("(");
			if (brk != -1) {
				String marker = signature.substring(0, brk);
				if (marker != null && marker.length() > 0) {
					if (marker.indexOf("<") != -1) {
						return "<"+Objects.getGenericPartOfGenericSignature(marker)+">";
						
					}
				}
			}
		}
		return "";
	}

	public String getGenericPartForReturnType() {
		if (!genericRetType.equals("--"))
			return genericRetType;
		if (signature != null) {
			int from = signature.indexOf(")");
			if (from != -1) {
				int start = signature.indexOf("<", from);
				if (start != -1) {
					int end = signature.lastIndexOf(">");
					if (end != -1) {
						genericRetType = signature.substring(start, end + 1);
						genericRetType = Objects
								.getGenericPartOfGenericSignature(genericRetType);
						return "<"+genericRetType+">";
					}
				} else {
					genericRetType = signature.substring(from + 1);
					if (!Objects.isPrimitive(genericRetType)) {
						genericRetType = Objects
								.getGenericPartOfGenericSignature(genericRetType);
						genericRetType = genericRetType.replaceAll("\\[", "[]");
						int close = genericRetType.lastIndexOf("]");
						if (close != -1) {
							String part1 = genericRetType.substring(close + 1);
							String part2 = genericRetType.substring(0,
									close + 1);
							return part1 + part2;
						}
						return genericRetType;
					} else {
						return "";
					}
				}
			}

		}
		return genericRetType = "";
	}

	private List variableSignatures = new ArrayList();

	public String getGenericPartForVariablePos(int pos) {
		if (variableSignatures.size() == 0)
			return "";
		if (pos < variableSignatures.size())
			return (String) variableSignatures.get(pos);
		return "";
	}

	public List getVariableSignatures() {
		return variableSignatures;
	}

	// For -g:none
	public void parseGenericSigature() {
		if (signature != null) {
			String args = "";
			if (signature.indexOf("(") != -1) {
				args = signature.substring(signature.indexOf("(") + 1,
						signature.indexOf(")"));
			}

			outer: for (int z = 0; z < args.length(); z++) {
				char ch = args.charAt(z);

				if (ch == 'I') {
					variableSignatures.add("");
				} else if (ch == 'B') {
					variableSignatures.add("");
				} else if (ch == 'C') {
					variableSignatures.add("");
				} else if (ch == 'S') {
					variableSignatures.add("");
				} else if (ch == 'F') {
					variableSignatures.add("");
				} else if (ch == 'D') {
					variableSignatures.add("");
				} else if (ch == 'J') {
					variableSignatures.add("");
				} else if (ch == 'Z') {
					variableSignatures.add("");
				} else if (ch == 'L') {

					char next = '%';
					int start = args.indexOf("<", z);
					int semicol = args.indexOf(";", z);
					int close = args.indexOf(">", start);
					if (start != -1 && close != -1 && semicol > start
							&& semicol < close) {

						while (semicol != -1) {
							if ((semicol + 1) < args.length()) {
								next = args.charAt(semicol + 1);
								if (next != '>') {
									semicol = args.indexOf(";", semicol + 1);
									continue;
								} else {
									String temp = args.substring(start + 1,
											semicol + 1);
									temp = Objects
											.getGenericPartOfGenericSignature(temp);
									variableSignatures.add("<" + temp + ">");
									z = semicol + 2;
									continue outer;
								}
							} else {
								String temp = args.substring(start, semicol);
								temp = Objects
										.getGenericPartOfGenericSignature(temp);
								variableSignatures.add(temp);
								z = semicol;
								continue outer;
							}
						}
					} else {

						variableSignatures.add("");
						z = semicol;
						continue outer;
					}

				} else if (ch == 'T') {
					int semi = args.indexOf(";", z);
					String temp = args.substring(z, semi);
					temp = Objects.getGenericPartOfGenericSignature(temp);
					variableSignatures.add(temp);
					z = semi;
					continue outer;
				} else if (ch == '[') {
					while(args.charAt(z) == '['){
						z = z+1;
					}
					
					if (args.charAt(z + 1) == 'L') {
						char next = '%';
						int start = args.indexOf("<", z);
						int semicol = args.indexOf(";", z);
						int close = args.indexOf(">", start);
						if (start != -1 && close != -1 && semicol > start
								&& semicol < close) {

							while (semicol != -1) {
								if ((semicol + 1) < args.length()) {
									next = args.charAt(semicol + 1);
									if (next != '>') {
										semicol = args
												.indexOf(";", semicol + 1);
										continue;
									} else {
										String temp = args.substring(start + 1,
												semicol + 1);
										temp = Objects
												.getGenericPartOfGenericSignature(temp);
										variableSignatures
												.add("<" + temp + ">");
										z = semicol + 2;
										continue outer;
									}
								} else {
									String temp = args
											.substring(start, semicol);
									temp = Objects
											.getGenericPartOfGenericSignature(temp);
									variableSignatures.add(temp);
									z = semicol;
									continue outer;
								}
							}
						} else {

							variableSignatures.add("");
							z = semicol;
							continue outer;
						}
					} else { // T
						int semi = args.indexOf(";", z);
						String temp = args.substring(z, semi);
						if(temp.indexOf("<") == -1){
							temp = Objects.getGenericPartOfGenericSignature(temp);
						}
						else{
							temp = Objects.getGenericPartOfGenericSignature(temp.substring(temp.indexOf("<")+1));
							temp = "<"+temp+">";
						}
						variableSignatures.add(temp);
						z = semi;
						continue outer;
					}
				} else {
					int semicol = args.indexOf(";", z);
					if (semicol != -1) {
						String temp = args.substring(z, semicol);
						temp = Objects.getGenericPartOfGenericSignature(temp);
						variableSignatures.add(temp);
						z = semicol;
						continue outer;
					} else {

						variableSignatures.add(""); // ??
					}
				}
			}

		}
	}

}