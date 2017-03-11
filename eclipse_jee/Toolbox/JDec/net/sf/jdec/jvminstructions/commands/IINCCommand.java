package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class IINCCommand extends AbstractInstructionCommand {

	public IINCCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		int currentForIndex = getCurrentInstPosInCode();
		int i = getCurrentInstPosInCode();
		byte[] info = getCode();
		int classIndex = info[++i];
		java.lang.String constantStr = "";
		int constant = info[++i];
		java.lang.String varName = "";
		String tempString = "";
		Behaviour behavior = getContext();
		OperandStack opStack = getStack();
		// NOTE: passing load is ok as the rangeindex used to query will be
		// correct
		LocalVariable local = DecompilerHelper.getLocalVariable(classIndex,
				"load", "int", true, currentForIndex);

		if (local != null) {

			boolean ad = DecompilerHelper.checkForLoadAfterIINC(info, opStack,
					currentForIndex, local, classIndex, "" + constant);
			if (!ad) {
				ad = DecompilerHelper.checkForLoadBeforeIINC(info, opStack,
						currentForIndex, local, classIndex, "" + constant);
			}
			if (!ad) {

				varName = local.getVarName();

				if (local.isDeclarationGenerated() == false) {
					if (constant < 0)
						constantStr = "(" + constant + ")";
					else
						constantStr = "" + constant;
					Object vaf = DecompilerHelper.newVariableAtFront(varName,
							local.getDataType().replace('/', '.'), "0");
					GlobalVariableStore.getVariablesatfront().add(vaf);
					tempString = varName + " = " + varName + "+" + constantStr
							+ ";\n";
					behavior.appendToBuffer( Util
							.formatDecompiledStatement(tempString));
					local.setDeclarationGenerated(true);
				} else {
					if (constant < 0)
						constantStr = "(" + constant + ")";
					else
						constantStr = "" + constant;
					tempString = varName + " = " + varName + "+" + constantStr
							+ ";\n";
					behavior.appendToBuffer( Util
							.formatDecompiledStatement(tempString));
				}
			}
		}
		
	}

}
