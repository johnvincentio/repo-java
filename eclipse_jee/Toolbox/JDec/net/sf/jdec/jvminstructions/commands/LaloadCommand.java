package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;

public class LaloadCommand extends AbstractInstructionCommand {

	public LaloadCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack opStack = getStack();
		Operand index = opStack.getTopOfStack();
		Operand arRef = opStack.getTopOfStack();
		String result = arRef.getOperandValue() + "[" + index.getOperandValue()
				+ "]";
		Operand op = new Operand();
		op.setOperandType(Constants.IS_CONSTANT_LONG);
		op.setOperandValue(result);
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
