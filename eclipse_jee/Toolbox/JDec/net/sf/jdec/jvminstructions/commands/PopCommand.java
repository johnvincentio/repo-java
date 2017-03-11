package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class PopCommand extends AbstractInstructionCommand {

	public PopCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack opStack = getStack();
		if(opStack.size() > 0) // removed add==true
        {
                //if(info[i+1]!=JvmOpCodes.GETSTATIC)  ??
                opStack.getTopOfStack();
        }
	}

}
