package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class SaloadCommand extends AbstractInstructionCommand {

	public SaloadCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack opStack = getStack();
		Operand index = opStack.getTopOfStack();
		Operand arRef = opStack.getTopOfStack();
		java.lang.String result = arRef.getOperandValue() + "["
				+ index.getOperandValue() + "]";
		Operand op = new Operand();
		op.setOperandValue(result);
		boolean r = false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
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
