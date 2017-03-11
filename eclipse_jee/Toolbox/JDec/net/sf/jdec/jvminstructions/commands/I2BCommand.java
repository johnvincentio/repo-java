package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class I2BCommand extends AbstractInstructionCommand {

	public I2BCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack opStack=getStack();
		Operand op =(Operand)opStack.pop();
        op.setOperandValue("(byte)"+"("+op.getOperandValue()+")");
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
