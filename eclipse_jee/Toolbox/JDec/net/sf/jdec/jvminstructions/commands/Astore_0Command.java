/*
 *  Astore_0Command.java Copyright (c) 2006,07 Swaroop Belur
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

public class Astore_0Command extends AbstractInstructionCommand {

	public Astore_0Command(Behaviour context) {
		super(context);

	}


	public int getSkipBytes() {
		return 0;
	}

	public void execute() {

		int i = getCurrentInstPosInCode();
		int currentForIndex = i;
		int instructionPos = i;
		Behaviour behaviour = getContext();
		ArrayList methodTries = behaviour.getAllTriesForMethod();
		byte[] info = behaviour.getCode();
		boolean doNotPop = GlobalVariableStore.isDoNotPop();
		OperandStack opStack = getContext().getOpStack();
		boolean multinewfound = GlobalVariableStore.isMultinewfound();
		Hashtable variableDimAss= GlobalVariableStore.getVariableDimAss();
		
		boolean dupnothandled = GlobalVariableStore.isDupnothandled();
		
		
		boolean add = DecompilerHelper.checkForStartOfCatch(instructionPos, methodTries);
		if (add == true && !doNotPop) {

			LocalVariable local = DecompilerHelper.getLocalVariable(0, "store",
					"java.lang.Object", true, currentForIndex);
			if (local != null) {
				String signature = getContext().getGenericSignatureForLocalVariable(local.getIndexPos());
				String genericPart = "";
				if(signature != null){
					if(signature.indexOf("<") != -1 && signature.indexOf(">") != -1){
						genericPart = LocalVariableHelper.getGenericPartOfSignature(signature);
					}
				}

				boolean push = getGenericFinder().isPrevInstDup(currentForIndex);
				if (!push) {
					Operand objref = (Operand) opStack.pop(); // Pop The
																// Object Ref

					if (local.wasCreated() && objref != null
							&& objref.getClassType().trim().length() > 0)
						local.setDataType(objref.getClassType());
					java.lang.String temp = (java.lang.String) objref
							.getOperandValue(); // temp has new <Class
												// Name>[<size>]
					if (!local.isDeclarationGenerated()) {
						
						// temp=local.getDataType().replace('/','.')+"
						// "+local.getVarName()+" ="+temp; // This shbud contain
						// the actual Line in the method
						boolean yes = true;
						if (!multinewfound) {
							StringBuffer m = new StringBuffer("");

							DecompilerHelper.checkForImport(local.getDataType()
									.replace('/', '.'), m);
							if (local.getVarName().equals(temp) == true) {
								yes = false;
							}
							Object vaf = DecompilerHelper.newVariableAtFront(
									local.getVarName(), m.toString()+genericPart, "null");
							GlobalVariableStore.getVariablesatfront().add(vaf);
							temp = local.getVarName() + "=" + temp;
						} else {
							multinewfound = false;
							Integer n = (Integer) variableDimAss
									.get(new Integer(0));
							java.lang.String bracks = "";
							if (n != null) {
								for (int o = 0; o < n.intValue(); o++) {
									bracks += "[]";
								}
							}
							StringBuffer m = new StringBuffer("");
							DecompilerHelper.checkForImport(local.getDataType()
									.replace('/', '.'), m);
							Object vaf = DecompilerHelper.newVariableAtFront(
									local.getVarName(), m.toString()+genericPart + " "
											+ bracks + "  ", "null");
							GlobalVariableStore.getVariablesatfront().add(vaf);
							temp = local.getVarName() + "=" + temp;

						}
						if (yes) {
							behaviour.appendToBuffer( Util
									.formatDecompiledStatement(temp + ";\n"));

							// behaviour.appendToBuffer( ";\n";
						}
						local.setDeclarationGenerated(true);
					} else {
						if (temp != null
								&& temp.trim().equalsIgnoreCase("this")) {
							java.lang.String classname = ConsoleLauncher
									.getClazzRef().getClassName();
							temp = classname + "  " + local.getVarName() + " ="
									+ temp;
							behaviour.appendToBuffer( Util
									.formatDecompiledStatement(temp + ";\n"));
							// behaviour.appendToBuffer( ";\n";
						} else {
							temp = "  " + local.getVarName() + " =" + temp;
							behaviour.appendToBuffer( Util
									.formatDecompiledStatement(temp + ";\n"));
							// behaviour.appendToBuffer( ";\n";
						}
					}
				} else {
					Operand top = opStack.getTopOfStack();
					// boolean skipped=checkIFDUPWasSkipped(currentForIndex);
					if (!dupnothandled)
						opStack.getTopOfStack();
					else
						dupnothandled = false;

					java.lang.String temp = top.getOperandValue();
					java.lang.String typedecl = "";
					boolean yes = true;
					boolean waslocaldecalred = true;
					if (!local.isDeclarationGenerated()) {
						waslocaldecalred = false;
						if (!multinewfound) {
							StringBuffer m = new StringBuffer("");
							if (top.getClassType() != null) {
								local.setDataType(top.getClassType());
							}
							DecompilerHelper.checkForImport(local.getDataType()
									.replace('/', '.'), m);
							if (local.getVarName().equals(temp) == true) {
								yes = false;
							}
							typedecl = m.toString() +genericPart+ "  ";
						} else {
							multinewfound = false;
							Integer n = (Integer) variableDimAss
									.get(new Integer(0));
							java.lang.String bracks = "";
							if (n != null) {
								for (int o = 0; o < n.intValue(); o++) {
									bracks += "[]";
								}
							}
							StringBuffer m = new StringBuffer("");
							if (top.getClassType() != null) {
								local.setDataType(top.getClassType());
							}
							DecompilerHelper.checkForImport(local.getDataType()
									.replace('/', '.'), m);
							typedecl = m.toString() +genericPart+ bracks + " ";

						}
						local.setDeclarationGenerated(true);
						Object vaf = DecompilerHelper.newVariableAtFront(
								local.getVarName(), typedecl, "null");
						GlobalVariableStore.getVariablesatfront().add(vaf);
					}
					temp = local.getVarName() + " =" + temp + ";\n";
					if (local.wasCreated() && top != null
							&& top.getClassType().trim().length() > 0)
						local.setDataType(top.getClassType());
					if (!DecompilerHelper.isTernaryCondition(currentForIndex, info)) {
						if (!newfound()) {
							// TODO : This fix needs be tested....
							if (waslocaldecalred == false && !yes) {

							} else
								behaviour.appendToBuffer( Util
										.formatDecompiledStatement(temp));

							Operand op6 = createOperand(local.getVarName());// "("+local.getVarName()+"=("+op.getOperandValue()+"))");
							opStack.push(op6);
						} else {
							Operand op6 = createOperand(temp);
							opStack.push(op6);
						}
					} else {
						boolean ternEndfoundForParentIF = false;//isThisTernaryListEndForParentIF(currentForIndex);
						boolean ternEndfound = false;//isThisTernaryListEnd(currentForIndex);

						boolean dupStoreForTerIf = false;//isThisDUPSTOREAtEndOFTernaryIF(			currentForIndex, info, "store");
						boolean n = DecompilerHelper.anydupstoreinternarybesidesthis(
								currentForIndex, info);
						if (ternEndfound && n) {
							opStack.push(top);
						} else {
							Operand p = opStack.getTopOfStack();
							boolean end = false;//isTernaryEnd(currentForIndex);
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
							GlobalVariableStore.setTernList(new ArrayList());

					}
				}

			}

		}
		if (doNotPop == true)
			doNotPop = false;
		
		GlobalVariableStore.setDoNotPop(doNotPop);
		GlobalVariableStore.setMultinewfound(multinewfound);
		GlobalVariableStore.setVariableDimAss(variableDimAss);
		GlobalVariableStore.setDupnothandled(dupnothandled);

		
	}

}
