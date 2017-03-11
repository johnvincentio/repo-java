package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class SwapCommand extends AbstractInstructionCommand {

	public SwapCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack stack = getStack();
		Operand op1 = stack.getTopOfStack();
		Operand op2 = stack.getTopOfStack();
		stack.push(op2);
		stack.push(op1);
	}

}
