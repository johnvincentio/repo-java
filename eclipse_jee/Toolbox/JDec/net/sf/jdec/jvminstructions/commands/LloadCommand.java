package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;

public class LloadCommand extends AbstractInstructionCommand {

	public LloadCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 1;
	}

	public void execute() {
		int currentForIndex = getCurrentInstPosInCode();
		byte[] info = getCode();
		int i = currentForIndex;
		int opValueI = info[++i];
		if (opValueI < 0)
			opValueI += 256;
		OperandStack opStack = getStack();
		LocalVariable local = DecompilerHelper.getLocalVariable(opValueI,
				"load", "long", false, currentForIndex);
		if (local != null) {
			Operand op = new Operand();
			op.setOperandType(Constants.IS_CONSTANT_LONG);
			// (Constants.CATEGORY2);
			StringBuffer addsub = new StringBuffer("");
			boolean bo = DecompilerHelper.checkForPostIncrForLoadCase(info,
					currentForIndex, "category2", true, opValueI, addsub);
			if (!bo)
				op.setOperandValue(local.getVarName());
			else
				op.setOperandValue(local.getVarName() + addsub.toString());
			boolean r = false;// checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
			if (r) {
				if (opStack.size() > 0) {
					java.lang.String str = opStack.getTopOfStack()
							.getOperandValue();
					str = str + op.getOperandValue();
					op.setOperandValue(str);
				}
			}
			opStack.push(op);

		}
	}

}
