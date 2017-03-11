package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class Pop2Command extends AbstractInstructionCommand{

	

	public Pop2Command(Behaviour context) {
		super(context);
		
	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack opStack = getStack();
		 if(opStack.size() >= 3){
             opStack.getTopOfStack();
             opStack.getTopOfStack();
             opStack.getTopOfStack();
         }
	}

}
