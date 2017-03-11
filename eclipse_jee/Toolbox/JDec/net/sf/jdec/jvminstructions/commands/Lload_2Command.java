package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;

public class Lload_2Command extends AbstractInstructionCommand {

	public Lload_2Command(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		int index = 2;
		int currentForIndex = getCurrentInstPosInCode();
		LocalVariable local = DecompilerHelper.getLocalVariable(index, "load",
				"long", true, currentForIndex);
		if (local != null) {

			Operand op = new Operand();
			op.setOperandType(Constants.IS_CONSTANT_LONG);
			// (Constants.CATEGORY2);
			byte[] info = getCode();
			StringBuffer addsub = new StringBuffer("");
			boolean bo = DecompilerHelper.checkForPostIncrForLoadCase(info,
					currentForIndex, "category2", false, index, addsub);
			if (!bo)
				op.setOperandValue(local.getVarName());
			else
				op.setOperandValue(local.getVarName() + addsub.toString());
			boolean r = false;// checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
			OperandStack opStack = getStack();
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
