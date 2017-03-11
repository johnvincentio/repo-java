package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class DaloadCommad extends AbstractInstructionCommand {

	public DaloadCommad(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack stack = getStack();
		Operand op = (Operand) stack.pop();
		Operand op1 = (Operand) stack.pop();
		Operand op2 = new Operand();
		op2.setOperandValue(op1.getOperandValue() + "[" + op.getOperandValue()
				+ "]");
		boolean r = false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
		if (r) {
			if (stack.size() > 0) {
				java.lang.String str = stack.getTopOfStack()
						.getOperandValue();
				str = str + op2.getOperandValue();
				op2.setOperandValue(str);
			}
		}
		stack.push(op2);
	}

}
