/*
 *  AstoreCommand.java Copyright (c) 2006,07 Swaroop Belur
 *
 * This program is free software; you can redistribute it and/itor
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
package net.sf.jdec.jvminstructions.commands;

import java.util.ArrayList;
import java.util.Hashtable;

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.LocalVariableHelper;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class AstoreCommand extends AbstractInstructionCommand {

	public AstoreCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {

		return 1;
	}

	public void execute() {

		int i = getCurrentInstPosInCode();
		int currentForIndex = i;
		int instructionPos = i;
		Behaviour behaviour = getContext();
		boolean doNotPop = GlobalVariableStore.isDoNotPop();
		boolean multinewfound = GlobalVariableStore.isMultinewfound();
		ArrayList methodTries = getContext().getAllTriesForMethod();
		byte[] info = getContext().getCode();
		int classIndex = info[++i];
		boolean add = DecompilerHelper.checkForStartOfCatch(instructionPos,
				methodTries);
		OperandStack opStack = getContext().getOpStack();

		ArrayList ternList = GlobalVariableStore.getTernList();
		boolean dupnothandled = GlobalVariableStore.isDupnothandled();
		if (add == true && !doNotPop) {

			LocalVariable local = DecompilerHelper.getLocalVariable(classIndex,
					"store", "java.lang.Object", false, currentForIndex);
			if (local != null) {
				String signature = getContext().getGenericSignatureForLocalVariable(local.getIndexPos());
				String genericPart = "";
				if(signature != null){
					if(signature.indexOf("<") != -1 && signature.indexOf(">") != -1){
						genericPart = LocalVariableHelper.getGenericPartOfSignature(signature);
					}
				}
				// temp has new <Class Name>[<size>]
				boolean push = getGenericFinder()
						.isPrevInstDup(currentForIndex);
				if (!push) {
					Operand objref = (Operand) opStack.pop(); // Pop The
					// Object Ref
					if (local.wasCreated() && objref != null
							&& objref.getClassType().trim().length() > 0)
						local.setDataType(objref.getClassType());
					java.lang.String temp = (java.lang.String) objref
							.getOperandValue();
					if (!local.isDeclarationGenerated()) {
						local.setBlockIndex(i - 1);
						boolean yes = true;
						if (!multinewfound) {
							StringBuffer m = new StringBuffer("");
							DecompilerHelper.checkForImport(local.getDataType()
									.replace('/', '.'), m);
							String typegenerified = m.toString()+genericPart;
							if(m.toString().indexOf("[") != -1){
								int start = m.toString().indexOf("[");
								int end = m.toString().indexOf("]");
								String part1 = m.toString().substring(0,start).trim();
								typegenerified = part1 + genericPart + m.toString().substring(start,end+1);
							}
							Object vaf = DecompilerHelper.newVariableAtFront(
									local.getVarName(), typegenerified , "null");
							GlobalVariableStore.getVariablesatfront().add(vaf);
							temp = local.getVarName() + "=" + temp;
							if (local.getVarName().equals(temp) == true) {
								yes = false;
							}
						} else {
							multinewfound = false;
							if (GlobalVariableStore.getVariablesatfront() != null) {
								// Integer
								// n=(Integer)(GlobalVariableStore.getVariablesatfront().get(new
								// Integer(classIndex).intValue()));
								Hashtable variableDimAss = GlobalVariableStore
										.getVariableDimAss();
								Integer n = (Integer) variableDimAss
										.get(new Integer(classIndex));
								java.lang.String bracks = "";
								if (n != null) {
									for (int o = 0; o < n.intValue(); o++) {
										bracks += "[]";
									}
								}
								StringBuffer m = new StringBuffer("");
								DecompilerHelper.checkForImport(local
										.getDataType().replace('/', '.'), m);
								Object vaf = DecompilerHelper
										.newVariableAtFront(local.getVarName(),
												m +genericPart+ " " + bracks + "  ", "null");
								GlobalVariableStore.getVariablesatfront().add(
										vaf);
								temp = local.getVarName() + "=" + temp;
							} else {
								// TODO: IMPORTANT FIXME
								Object vaf = DecompilerHelper
										.newVariableAtFront(local.getVarName(),
												local.getDataType().replace(
														'/', '.')+genericPart, "null");
								GlobalVariableStore.getVariablesatfront().add(
										vaf);
								temp = local.getVarName() + "=" + temp;
							}
						}
						local.setDeclarationGenerated(true);
						if (yes) {
							behaviour.appendToBuffer(Util
									.formatDecompiledStatement(temp + ";\n"));

						}
					} else {

						if (temp != null
								&& temp.trim().equalsIgnoreCase("this")) {

							java.lang.String classname = ConsoleLauncher
									.getClazzRef().getClassName();
							temp = classname + "  " + local.getVarName() + " ="
									+ temp;
							behaviour.appendToBuffer(Util
									.formatDecompiledStatement(temp + ";\n"));
							// behaviour.appendToBuffer( ";\n";
						} else {
							temp = "  " + local.getVarName() + " =" + temp;
							behaviour.appendToBuffer(Util
									.formatDecompiledStatement(temp + ";\n"));
							// behaviour.appendToBuffer( ";\n";
						}
					}
				} else {

					Operand top = opStack.getTopOfStack();
					if (!dupnothandled)
						opStack.getTopOfStack();
					else
						dupnothandled = false;
					if (local.wasCreated() && top != null
							&& top.getClassType().trim().length() > 0)
						local.setDataType(top.getClassType());
					java.lang.String temp = top.getOperandValue();
					temp = local.getVarName() + " =" + "(" + temp + ")";
					if (!DecompilerHelper.isTernaryCondition(currentForIndex,
							info)) {
						if (newfound() == false) {
							behaviour.appendToBuffer(Util
									.formatDecompiledStatement(temp + ";\n"));
							// behaviour.appendToBuffer( ";\n";
							Operand op6 = createOperand(local.getVarName());
							opStack.push(op6);
						} else {
							Operand op6 = createOperand(temp);
							opStack.push(op6);
						}
						Object vaf = DecompilerHelper.newVariableAtFront(local
								.getVarName(), local.getDataType().replace('/',
								'.')+genericPart, "null");
						GlobalVariableStore.getVariablesatfront().add(vaf);
					} else {
						boolean ternEndfoundForParentIF = false;// isThisTernaryListEndForParentIF(currentForIndex);
						boolean ternEndfound = false;// isThisTernaryListEnd(currentForIndex);

						boolean dupStoreForTerIf = false;// isThisDUPSTOREAtEndOFTernaryIF(currentForIndex,info,"store");
						boolean n = DecompilerHelper
								.anydupstoreinternarybesidesthis(
										currentForIndex, info);
						if (ternEndfound && n) {
							opStack.push(top);
						} else {
							Operand p = opStack.getTopOfStack();
							boolean end = false;// isTernaryEnd(currentForIndex);
							if (dupStoreForTerIf) {
								if (!end)
									p.setOperandValue(p.getOperandValue() + "("
											+ local.getVarName() + "="
											+ top.getOperandValue() + ")");
								else
									p.setOperandValue(p.getOperandValue() + "("
											+ local.getVarName() + "="
											+ top.getOperandValue() + "))");
							}
							if (!dupStoreForTerIf) {
								if (!end)
									p.setOperandValue(p.getOperandValue() + "("
											+ top.getOperandValue() + ")");
								else
									p.setOperandValue(p.getOperandValue()
											+ top.getOperandValue() + "))");
							}
							opStack.push(p);
						}
						if (ternEndfoundForParentIF)
							ternList = new ArrayList();

					}
				}

			}

		}
		if (doNotPop)
			doNotPop = false;
		GlobalVariableStore.setDoNotPop(doNotPop);
		GlobalVariableStore.setMultinewfound(multinewfound);
		GlobalVariableStore.setTernList(ternList);
		GlobalVariableStore.setDupnothandled(dupnothandled);
	}

}
