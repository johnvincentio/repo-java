/*
 *  Dstore_0.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class Dstore_1Command extends AbstractInstructionCommand {

	public Dstore_1Command(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		int currentForIndex = getCurrentInstPosInCode();
		Behaviour behaviour = getContext();
		int index = 1;
		LocalVariable local = DecompilerHelper.getLocalVariable(index, "store", "double", true,
				currentForIndex);
		java.lang.String tempString = "";
		boolean doNotPop = GlobalVariableStore.isDoNotPop();
		OperandStack opStack = getStack();
		byte[] info=getCode();
		boolean dupnothandled = GlobalVariableStore.isDupnothandled();
		
		if (local != null && !doNotPop) {
			Operand op = (Operand) opStack.pop();
			boolean push = getGenericFinder().isPrevInstDup(currentForIndex);
			if (!push) {
				if (!local.isDeclarationGenerated()) {
					
					int semiColon = op.getOperandValue().toString()
							.indexOf(";");
					Object vaf = DecompilerHelper.newVariableAtFront(
							local.getVarName(), local.getDataType().replace(
									'/', '.'), "0d");
					GlobalVariableStore.getVariablesatfront().add(vaf);
					if (semiColon == -1) {
						tempString = local.getVarName() + "="
								+ op.getOperandValue() + ";\n";
						behaviour.appendToBuffer( (Util
								.formatDecompiledStatement(tempString)));
					} else {
						tempString = local.getVarName() + "="
								+ op.getOperandValue();
						behaviour.appendToBuffer( (Util
								.formatDecompiledStatement(tempString)));
					}
					local.setDeclarationGenerated(true);
				} else {
					int semiColon = op.getOperandValue().toString()
							.indexOf(";");
					if (semiColon == -1) {
						tempString = local.getVarName() + "="
								+ op.getOperandValue() + ";\n";
						behaviour.appendToBuffer( (Util
								.formatDecompiledStatement(tempString)));
					} else {
						tempString = local.getVarName() + "="
								+ op.getOperandValue();
						behaviour.appendToBuffer( (Util
								.formatDecompiledStatement(tempString)));
					}
				}
			} else {
				if (getGenericFinder().isThisInstrStart(currentForIndex - 1)
						&& info[currentForIndex - 1] == JvmOpCodes.DUP
						&& !dupnothandled)
					opStack.getTopOfStack();
				if (getGenericFinder().isThisInstrStart(currentForIndex - 1)
						&& info[currentForIndex - 1] == JvmOpCodes.DUP2
						&& !dupnothandled) {
					opStack.getTopOfStack();
					opStack.getTopOfStack();
				}
				if (dupnothandled)
					dupnothandled = false;
				if (!DecompilerHelper.isTernaryCondition(currentForIndex, info)) {
					if (!newfound()) {
						java.lang.String decl = "";
						if (!local.isDeclarationGenerated()) {
							Object vaf = DecompilerHelper.newVariableAtFront(
									local.getVarName(), local.getDataType()
											.replace('/', '.'), "0d");
							GlobalVariableStore.getVariablesatfront().add(vaf);
							// decl=local.getDataType()+"\t";//+local.getVarName()+"="+"#VALUE"+classIndex+"#"+op.getOperandValue()+";\n";
							local.setDeclarationGenerated(true);
						}
						behaviour.appendToBuffer( (Util.formatDecompiledStatement(decl
								+ local.getVarName() + "="
								+ op.getOperandValue() + ";\n")));
						Operand op2 = createOperand(local.getVarName());
						opStack.push(op2);
					} else {
						Operand op2 = createOperand(local.getVarName() + "="
								+ op.getOperandValue());
						opStack.push(op2);
					}
				} else {
					boolean ternEndfoundForParentIF = false;//isThisTernaryListEndForParentIF(currentForIndex);
					boolean ternEndfound = false;//isThisTernaryListEnd(currentForIndex);
					boolean dupStoreForTerIf = false;//isThisDUPSTOREAtEndOFTernaryIF(							currentForIndex, info, "store");
					boolean n = false;//anydupstoreinternarybesidesthis(							currentForIndex, info);
					if (ternEndfound && n) {
						opStack.push(op);
					} else {

						Operand p = opStack.getTopOfStack();
						boolean end = false;//isTernaryEnd(currentForIndex);
						if (dupStoreForTerIf) {
							if (!end)
								p.setOperandValue(p.getOperandValue()
										+ local.getVarName() + "="
										+ op.getOperandValue());
							else
								p.setOperandValue(p.getOperandValue()
										+ local.getVarName() + "="
										+ op.getOperandValue() + ")");
						}
						if (!dupStoreForTerIf) {
							if (!end)
								p.setOperandValue(p.getOperandValue()
										+ op.getOperandValue());
							else
								p.setOperandValue(p.getOperandValue()
										+ op.getOperandValue() + "))");
						}
						opStack.push(p);
					}
					if (ternEndfoundForParentIF)
						GlobalVariableStore.setTernList(new ArrayList());
				}
			}
		}
		if (doNotPop == true)
			doNotPop = false;
		
		GlobalVariableStore.setDoNotPop(doNotPop);
		GlobalVariableStore.setDupnothandled(dupnothandled);
		
		
	}

}
