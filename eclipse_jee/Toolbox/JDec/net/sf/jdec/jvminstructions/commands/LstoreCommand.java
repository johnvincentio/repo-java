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

public class LstoreCommand extends AbstractInstructionCommand {

	public LstoreCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 1;
	}

	public void execute() {
		int currentForIndex = getCurrentInstPosInCode();
		int i = currentForIndex;
		byte[] info = getCode();
		int pos = info[++i];
		if (pos < 0)
			pos += 256;
		boolean doNotPop = GlobalVariableStore.isDoNotPop();
		OperandStack opStack = getStack();
		Behaviour behavior = getContext();
		boolean dupnothandled = GlobalVariableStore.isDupnothandled();
		LocalVariable local = DecompilerHelper.getLocalVariable(pos, "store",
				"long", false, currentForIndex);
		if (local != null && !doNotPop) {
			Operand operand1 = opStack.getTopOfStack();
			boolean push = getGenericFinder().isPrevInstDup(currentForIndex);
			if (!push) {
				if (!local.isDeclarationGenerated()) {
					Object vaf = DecompilerHelper.newVariableAtFront(local
							.getVarName(), local.getDataType(), "0L");
					GlobalVariableStore.getVariablesatfront().add(vaf);
					String tempString = local.getVarName() + "="
							+ operand1.getOperandValue() + ";\n";
					behavior.appendToBuffer( Util
							.formatDecompiledStatement(tempString));
					local.setDeclarationGenerated(true);
				} else {
					String tempString = local.getVarName() + "="
							+ operand1.getOperandValue() + ";\n";
					behavior.appendToBuffer( Util
							.formatDecompiledStatement(tempString));
				}
			} else {
				if (info[currentForIndex - 1] == JvmOpCodes.DUP
						&& !dupnothandled)
					opStack.getTopOfStack();
				if (info[currentForIndex - 1] == JvmOpCodes.DUP2
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
									"0L");
							GlobalVariableStore.getVariablesatfront().add(vaf);
							// decl=local.getDataType()+"\t";//+local.getVarName()+"="+"#VALUE"+classIndex+"#"+op.getOperandValue()+";\n";
							local.setDeclarationGenerated(true);
						}
						behavior.appendToBuffer( Util.formatDecompiledStatement(decl
								+ local.getVarName() + "="
								+ operand1.getOperandValue() + ";\n"));
						Operand op = createOperand(local.getVarName());
						opStack.push(op);
					} else {
						Operand op = createOperand(local.getVarName() + "="
								+ operand1.getOperandValue());
						opStack.push(op);
					}
				} else {
					boolean ternEndfoundForParentIF = false;// isThisTernaryListEndForParentIF(currentForIndex);
					boolean ternEndfound = false;// isThisTernaryListEnd(currentForIndex);

					boolean dupStoreForTerIf = false;// isThisDUPSTOREAtEndOFTernaryIF(currentForIndex,info,"store");
					boolean n = false;// anydupstoreinternarybesidesthis(currentForIndex,info);
					if (ternEndfound && n) {
						opStack.push(operand1);
					} else {
						Operand p = opStack.getTopOfStack();
						boolean end = false;// isTernaryEnd(currentForIndex);
						if (dupStoreForTerIf) {
							if (!end)
								p.setOperandValue(p.getOperandValue()
										+ local.getVarName() + "="
										+ operand1.getOperandValue());
							else
								p.setOperandValue(p.getOperandValue()
										+ local.getVarName() + "="
										+ operand1.getOperandValue() + ")");
						}
						if (!dupStoreForTerIf) {
							if (!end)
								p.setOperandValue(p.getOperandValue()
										+ operand1.getOperandValue());
							else
								p.setOperandValue(p.getOperandValue()
										+ operand1.getOperandValue() + ")");
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
