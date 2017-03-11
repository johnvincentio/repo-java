package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;

public class Lconst_0Command extends AbstractInstructionCommand {

	public Lconst_0Command(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack opStack = getStack();
		Operand op = new Operand();
		op.setOperandValue(new Long("0"));
		op.setOperandType(Constants.IS_CONSTANT_LONG);
		boolean r = false;// checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
		if (r) {
			if (opStack.size() > 0) {
				java.lang.String string = opStack.getTopOfStack()
						.getOperandValue();
				string = string + op.getOperandValue();
				op.setOperandValue("0");
			}
		}
		opStack.push(op);
	}

}
