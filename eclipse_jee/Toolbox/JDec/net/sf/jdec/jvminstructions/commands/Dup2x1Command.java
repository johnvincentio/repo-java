package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class Dup2x1Command extends AbstractInstructionCommand {

	public Dup2x1Command(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack opStack = getStack();
		Operand op1 = (Operand) opStack.pop();
		Operand op2 = (Operand) opStack.pop();
		Operand op3 = (Operand) opStack.pop();
		opStack.push(op2);
		opStack.push(op1);
		opStack.push(op3);
		opStack.push(op2);
		opStack.push(op1);
		if (GlobalVariableStore.isDoNotPop()) {
			GlobalVariableStore.setDoNotPop(false);
		}
	}

}
