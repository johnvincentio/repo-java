package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.blockhelpers.IFHelper;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.core.ShortcutAnalyser;
import net.sf.jdec.reflection.Behaviour;

public class DcmpgCommand extends AbstractInstructionCommand {

	public DcmpgCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		int currentForIndex = getCurrentInstPosInCode();
	    int i=currentForIndex;
	    OperandStack stack = getStack();
	    byte [] info = getCode();
        Operand  op = (Operand)stack.pop();
        Operand      op1 = (Operand)stack.pop();
        Operand      op2 = new Operand();
        ShortcutAnalyser sanalyser = getContext().getShortCutAnalyser();
        int j=i+1;
        int nextInstruction=info[j];
        boolean sh=getBranchFinder().isIFShortcutORComp(j);
        boolean invert=sanalyser.isLastIfInChain(j);
        if(!invert){
            java.lang.String connector=sanalyser.getConnector(j);
            if(connector!=null && connector.trim().equals(ShortcutAnalyser.AND)){
                invert=true;
            }
        }
        if(!sh) {
            if (getGenericFinder().isThisInstrStart((currentForIndex + 1))) {
                if (getBranchFinder().isInstructionIF((currentForIndex + 1))){
                    int ifclose=IFHelper.getIfCloseNumberForThisIF(getCode(),(currentForIndex+1));
                    ifclose=ifclose-3;
                    if(getGenericFinder().isThisInstrStart(ifclose)) {
                        if(getBranchFinder().isInstructionIF(ifclose) && ifclose!= j) {
                            sh=true;
                        }
                    }
                }
            }

        }
        switch(nextInstruction) {
            case JvmOpCodes.IFEQ:
                if(!sh)
                    op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                else{
                    if(!invert)
                        op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                    else
                        op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                }
                break;
            case JvmOpCodes.IFNE:
                if(!sh)
                    op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                else{
                    if(!invert)
                        op2.setOperandValue(op1.getOperandValue()+"!="+op.getOperandValue());
                    else
                        op2.setOperandValue(op1.getOperandValue()+"=="+op.getOperandValue());
                }
                break;
            case JvmOpCodes.IFLT:
                if(!sh)
                    op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                else{
                    if(!invert)
                        op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                    else
                        op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                }
                break;
            case JvmOpCodes.IFGE:
                if(!sh)
                    op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                else{
                    if(!invert)
                        op2.setOperandValue(op1.getOperandValue()+">="+op.getOperandValue());
                    else
                        op2.setOperandValue(op1.getOperandValue()+"<"+op.getOperandValue());
                }
                break;
            case JvmOpCodes.IFGT:
                if(!sh)
                    op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                else{
                    if(!invert)
                        op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                    else
                        op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                }
                break;
            case JvmOpCodes.IFLE:
                if(!sh)
                    op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                else{
                    if(!invert)
                        op2.setOperandValue(op1.getOperandValue()+"<="+op.getOperandValue());
                    else
                        op2.setOperandValue(op1.getOperandValue()+">"+op.getOperandValue());
                }
                break;
        }
        stack.push(op2);
	}

}
