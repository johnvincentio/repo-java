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

public class Lstore_3Command extends AbstractInstructionCommand {

	public Lstore_3Command(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		boolean doNotPop = GlobalVariableStore.isDoNotPop();
		boolean dupnothandled = GlobalVariableStore.isDupnothandled();
		OperandStack stack = getStack();
		Behaviour behavior = getContext();
		int currentForIndex = getCurrentInstPosInCode();
		int i = currentForIndex;
		byte[] info = getCode();
		java.lang.String tempString = "";
		LocalVariable local;
		int index = 3;
		local = DecompilerHelper.getLocalVariable(index, "store", "long", true,
				currentForIndex);

		if (local != null && !doNotPop) {
			Operand op = stack.getTopOfStack();
			boolean push = getGenericFinder().isPrevInstDup(currentForIndex);
			if (!push) {
				if (!local.isDeclarationGenerated()) {
					Object vaf = DecompilerHelper.newVariableAtFront(
							local.getVarName(), local.getDataType().replace(
									'/', '.'), "0L");
					GlobalVariableStore.getVariablesatfront().add(vaf);
					tempString = local.getVarName() + "="
							+ op.getOperandValue() + ";\n";
					behavior.appendToBuffer( (Util
							.formatDecompiledStatement(tempString)));
					local.setDeclarationGenerated(true);
				} else {
					tempString = local.getVarName() + "="
							+ op.getOperandValue() + ";\n";
					behavior.appendToBuffer( (Util
							.formatDecompiledStatement(tempString)));
				}
			} else {
				if (getGenericFinder().isThisInstrStart(
						currentForIndex - 1)
						&& info[currentForIndex - 1] == JvmOpCodes.DUP
						&& !dupnothandled)
					stack.getTopOfStack();
				if (getGenericFinder().isThisInstrStart(
						currentForIndex - 1)
						&& info[currentForIndex - 1] == JvmOpCodes.DUP2
						&& !dupnothandled) {
					stack.getTopOfStack();
					stack.getTopOfStack();
				}
				if (dupnothandled)
					dupnothandled = false;
				if (!DecompilerHelper.isTernaryCondition(currentForIndex, info)) {
					if (!newfound()) {
						java.lang.String decl = "";
						if (!local.isDeclarationGenerated()) {
							Object vaf = DecompilerHelper.newVariableAtFront(
									local.getVarName(), local.getDataType()
											.replace('/', '.'), "0L");
							GlobalVariableStore.getVariablesatfront().add(vaf);
							// decl=local.getDataType()+"\t";//+local.getVarName()+"="+"#VALUE"+classIndex+"#"+op.getOperandValue()+";\n";
							local.setDeclarationGenerated(true);
						}
						behavior.appendToBuffer( behavior.appendToBuffer( (Util
								.formatDecompiledStatement(decl
										+ local.getVarName() + "="
										+ op.getOperandValue() + ";\n"))));
						Operand op2 = createOperand(local.getVarName());
						stack.push(op2);
					} else {
						Operand op2 = createOperand(local.getVarName() + "="
								+ op.getOperandValue());
						stack.push(op2);
					}
				} else {
					boolean ternEndfoundForParentIF = false;//isThisTernaryListEndForParentIF(currentForIndex);
					boolean ternEndfound = false;//isThisTernaryListEnd(currentForIndex);

					boolean dupStoreForTerIf =false;// isThisDUPSTOREAtEndOFTernaryIF(							currentForIndex, info, "store");
					boolean n = false;//anydupstoreinternarybesidesthis(							currentForIndex, info);
					if (ternEndfound && n) {
						stack.push(op);
					} else {
						Operand p = stack.getTopOfStack();
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
						stack.push(p);
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
