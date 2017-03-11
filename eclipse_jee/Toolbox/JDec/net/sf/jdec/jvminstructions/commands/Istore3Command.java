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

public class Istore3Command extends AbstractInstructionCommand {

	public Istore3Command(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		byte[] info = getCode();
		handleISTORE(getStack(), info, 3, true);

	}

	private void handleISTORE(OperandStack stack, byte[] info, int classIndex,
			boolean b) {
		Behaviour behavior = getContext();
		boolean dupnothandled = GlobalVariableStore.isDupnothandled();
		int currentForIndex = getCurrentInstPosInCode();
		LocalVariable local = DecompilerHelper.getLocalVariable(classIndex,
				"store", "int", b, currentForIndex);
		boolean doNotPop = GlobalVariableStore.isDoNotPop();
		if (local != null && !doNotPop) {
			Operand op = null;
			op = (Operand) stack.pop();

			if (local.getDataType().equalsIgnoreCase("boolean")) {
				if (op.getOperandValue().toString().equals("0")) {
					op.setOperandValue("false");
				}
				if (op.getOperandValue().toString().equals("1")) {
					op.setOperandValue("true");
				}
			}

			boolean push = getGenericFinder().isPrevInstDup(currentForIndex);
			if (!push) {
				if (!local.isDeclarationGenerated()) {
					Object vaf = DecompilerHelper.newVariableAtFront(local
							.getVarName(), local.getDataType(), "0");
					GlobalVariableStore.getVariablesatfront().add(vaf);
					java.lang.String tempString = "#REPLACE_INT_" + classIndex
							+ "#" + local.getVarName() + "=" + "#VALUE"
							+ classIndex + "#" + op.getOperandValue() + ";\n";
					behavior.appendToBuffer( Util
							.formatDecompiledStatement(tempString));
					local.setDeclarationGenerated(true);

				} else {
					java.lang.String tempString = local.getVarName() + "="
							+ op.getOperandValue() + ";\n";
					behavior.appendToBuffer( Util
							.formatDecompiledStatement(tempString));// Change
					// Here
					// op->k-1
					// ,top->k
				}
			} else {
				if (getGenericFinder().isThisInstrStart(currentForIndex - 1)
						&& info[currentForIndex - 1] == JvmOpCodes.DUP
						&& !dupnothandled)
					stack.getTopOfStack();
				if (getGenericFinder().isThisInstrStart(currentForIndex - 1)
						&& info[currentForIndex - 1] == JvmOpCodes.DUP2
						&& !dupnothandled) {
					stack.getTopOfStack();
					stack.getTopOfStack();
				}
				if (dupnothandled)
					dupnothandled = false;
				if (!DecompilerHelper.isTernaryCondition(currentForIndex, info)) {// BUG
					// No
					// Declaration
					if (!newfound()) {
						java.lang.String decl = "";
						if (!local.isDeclarationGenerated()) {
							Object vaf = DecompilerHelper.newVariableAtFront(
									local.getVarName(), local.getDataType(),
									"0");
							GlobalVariableStore.getVariablesatfront().add(vaf);
							decl = "#REPLACE_INT_" + classIndex + "#";// +local.getVarName()+"="+"#VALUE"+classIndex+"#"+op.getOperandValue()+";\n";
							local.setDeclarationGenerated(true);
						}
						behavior.appendToBuffer( Util.formatDecompiledStatement(decl
								+ local.getVarName() + "="
								+ op.getOperandValue() + ";\n"));
						op = createOperand(local.getVarName());
						stack.push(op);
					} else {
						java.lang.String str = local.getVarName() + "="
								+ op.getOperandValue() + "";
						op = createOperand(str);
						stack.push(op);
					}
				} else {
					boolean ternEndfoundForParentIF = false;// isThisTernaryListEndForParentIF(currentForIndex);
					boolean ternEndfound = false;// isThisTernaryListEnd(currentForIndex);

					boolean dupStoreForTerIf = false;// isThisDUPSTOREAtEndOFTernaryIF(
														// currentForIndex,
														// info, "store");
					// FIXME : have to add a check here whether or not push op
					// itself back on stack
					boolean n = false;// anydupstoreinternarybesidesthis(
										// currentForIndex, info);
					if (ternEndfound && n) {
						stack.push(op);
					} else {
						Operand p = stack.getTopOfStack();
						boolean end = false;// isTernaryEnd(currentForIndex);
						if (!dupStoreForTerIf) {
							if (!end)
								p.setOperandValue(p.getOperandValue()
										+ local.getVarName() + "="
										+ op.getOperandValue());
							else
								p.setOperandValue(p.getOperandValue()
										+ local.getVarName() + "="
										+ op.getOperandValue() + ")");

						}
						if (dupStoreForTerIf) {
							if (!end)
								p.setOperandValue(p.getOperandValue()
										+ op.getOperandValue());
							else
								p.setOperandValue(p.getOperandValue()
										+ op.getOperandValue() + ")");
						}
						stack.push(p);
					}
					/*
					 * } else{ }
					 */
					if (ternEndfoundForParentIF) {
						GlobalVariableStore.setTernList(new ArrayList());
						// adjustBracketCount(opStack);
					}
				}
			}
		}

		if (doNotPop == true)
			doNotPop = false;

		GlobalVariableStore.setDoNotPop(doNotPop);
		GlobalVariableStore.setDupnothandled(dupnothandled);
		
	}

}
