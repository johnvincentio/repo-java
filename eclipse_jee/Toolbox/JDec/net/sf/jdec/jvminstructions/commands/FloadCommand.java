package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class FloadCommand extends AbstractInstructionCommand {

	public FloadCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 1;
	}

	public void execute() {
		byte[] info = getCode();
		int i = getCurrentInstPosInCode();
		int currentForIndex = i;
		int opValueI = info[i + 1];
		boolean b = false;
		OperandStack opStack = getStack();
		LocalVariable local = DecompilerHelper.getLocalVariable(opValueI,
				"load", "float", b, currentForIndex);
		if (local != null) {

			Operand op = new Operand();
			StringBuffer addsub = new StringBuffer("");
			boolean bo = DecompilerHelper.checkForPostIncrForLoadCase(info,
					currentForIndex, "category1", !b, opValueI, addsub);
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
