package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;

public class LshrCommand extends AbstractInstructionCommand {

	public LshrCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack opStack = getStack();
		Operand operand1 = opStack.getTopOfStack();
		Operand operand2 = opStack.getTopOfStack();

		Operand op = new Operand();
		op.setOperandType(Constants.IS_CONSTANT_LONG);
		op.setOperandValue("(" + operand2.getOperandValue() + ">>"
				+ operand1.getOperandValue() + ")");
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
