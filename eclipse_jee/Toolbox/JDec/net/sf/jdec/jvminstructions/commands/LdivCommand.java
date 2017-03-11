package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class LdivCommand extends AbstractInstructionCommand {

	public LdivCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack stack = getStack();
		Operand operand1 = stack.getTopOfStack();
		Operand operand2 = stack.getTopOfStack();
		java.lang.String result = "(" + operand2.getOperandValue() + "/"
				+ operand1.getOperandValue() + ")";
		Operand op = new Operand();
		op.setOperandValue(result);

		boolean r = false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
		if (r) {
			if (stack.size() > 0) {
				java.lang.String str = stack.getTopOfStack().getOperandValue();
				str = str + op.getOperandValue();
				op.setOperandValue(str);
			}
		}
		stack.push(op);
	}

}
