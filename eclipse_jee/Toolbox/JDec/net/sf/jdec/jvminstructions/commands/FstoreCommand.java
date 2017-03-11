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

public class FstoreCommand extends AbstractInstructionCommand {

	public FstoreCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 1;
	}

	public void execute() {
		java.lang.String tempString = "";
		byte[]  info = getCode();
		int currentForIndex =getCurrentInstPosInCode();
		int classIndex = info[currentForIndex + 1];
		boolean doNotPop = GlobalVariableStore.isDoNotPop();
		LocalVariable local = DecompilerHelper.getLocalVariable(classIndex, "store", "float",
				false, currentForIndex);
		OperandStack opStack = getStack();
		Behaviour behavior = getContext();
		boolean dupnothandled = GlobalVariableStore.isDupnothandled();
		
		if (local != null && !doNotPop) {
			Operand op = (Operand) opStack.pop();
			boolean push = getGenericFinder().isPrevInstDup(currentForIndex);
			if (!push) {
				if (!local.isDeclarationGenerated()) {
					Object vaf = DecompilerHelper.newVariableAtFront(
							local.getVarName(), local.getDataType(), "0f");
					GlobalVariableStore.getVariablesatfront().add(vaf);
					tempString = local.getVarName() + "="
							+ op.getOperandValue() + ";\n";
					behavior.appendToBuffer( Util
							.formatDecompiledStatement(tempString));
					local.setDeclarationGenerated(true);
				} else {
					tempString = local.getVarName() + "="
							+ op.getOperandValue() + ";\n";
					behavior.appendToBuffer( Util
							.formatDecompiledStatement(tempString));
				}
			}

			else {
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
									local.getVarName(), local.getDataType(),
									"0f");
							GlobalVariableStore.getVariablesatfront().add(vaf);
							// decl=local.getDataType()+"\t";//+local.getVarName()+"="+"#VALUE"+classIndex+"#"+op.getOperandValue()+";\n";
							local.setDeclarationGenerated(true);
						}
						behavior.appendToBuffer( Util.formatDecompiledStatement(decl
								+ local.getVarName() + "="
								+ op.getOperandValue() + ";\n"));
						Operand op5 = createOperand(local.getVarName());
						opStack.push(op5);
					} else {
						Operand op5 = createOperand(local.getVarName() + "="
								+ op.getOperandValue());
						opStack.push(op5);
					}
				} else {
					boolean ternEndfoundForParentIF = false;//isThisTernaryListEndForParentIF(currentForIndex);
					boolean ternEndfound = false;//isThisTernaryListEnd(currentForIndex);

					boolean dupStoreForTerIf = false;//isThisDUPSTOREAtEndOFTernaryIF(							currentForIndex, info, "store");
					boolean n = false;// anydupstoreinternarybesidesthis(							currentForIndex, info);
					if (ternEndfound && n) {
						opStack.push(op);
					} else {
						Operand p = opStack.getTopOfStack();
						boolean end = false;//isTernaryEnd(currentForIndex);
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
						opStack.push(p);
					}

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
