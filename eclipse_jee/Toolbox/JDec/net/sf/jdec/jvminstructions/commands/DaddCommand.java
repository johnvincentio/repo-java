package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;

public class DaddCommand extends AbstractInstructionCommand {

	public DaddCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
			OperandStack opStack = getStack();
	        Operand op= (Operand)opStack.pop();
	        Operand op1 = (Operand)opStack.pop();
	        Operand op2 = new Operand();
	        op2.setOperandValue("("+op1.getOperandValue()+")" +"+"+"("+op.getOperandValue()+")");
	        op2.setOperandType(Constants.IS_CONSTANT_DOUBLE);
	        boolean r=false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
	        if(r){
	            if(opStack.size() > 0){
	                java.lang.String str=opStack.getTopOfStack().getOperandValue();
	                str=str+op2.getOperandValue();
	                op2.setOperandValue(str);
	            }
	        }
	        opStack.push(op2);
	}

}
