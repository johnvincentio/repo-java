package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;

public class F2dCommand extends AbstractInstructionCommand {

	public F2dCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack opStack = getStack();
        Operand op =(Operand)opStack.pop();
        op.setOperandValue("(double)"+"("+op.getOperandValue()+")");
        op.setOperandType(Constants.IS_CONSTANT_DOUBLE);
        boolean r=false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
        if(r){
            if(opStack.size() > 0){
                java.lang.String str=opStack.getTopOfStack().getOperandValue();
                str=str+op.getOperandValue();
                op.setOperandValue(str);
            }
        }
        opStack.push(op);
	}

}
