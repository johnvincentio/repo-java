package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class L2dCommand extends AbstractInstructionCommand {

	public L2dCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack opStack = getStack();
		Operand operand = (Operand) opStack.pop();
		operand.setOperandValue(operand.getOperandValue());

		boolean r = false;// checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
		if (r) {
			if (opStack.size() > 0) {
				java.lang.String str = opStack.getTopOfStack()
						.getOperandValue();
				str = str + operand.getOperandValue();
				operand.setOperandValue(str);
			}
		}
		opStack.push(operand);
	}

}
