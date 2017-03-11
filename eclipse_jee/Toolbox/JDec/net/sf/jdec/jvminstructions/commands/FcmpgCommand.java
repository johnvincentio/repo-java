package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.blockhelpers.IFHelper;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.core.ShortcutAnalyser;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;

public class FcmpgCommand extends AbstractInstructionCommand {

    public FcmpgCommand(Behaviour context) {
        super(context);

    }

    public int getSkipBytes() {
        return 0;
    }

    public void execute() {
        int currentForIndex=getCurrentInstPosInCode();
        OperandStack stack  = getStack();
        int i=currentForIndex;
        Operand op = (Operand)stack.pop();
        Operand     op1 = (Operand)stack.pop();
        Operand     op2 = new Operand();
        //op2.setCategory(Constants.CATEGORY1);
        op2.setOperandType(Constants.IS_CONSTANT_INT);
        int j=i+1;
        byte[] info=getCode();
        int nextInstruction=info[j];
        boolean sh=IFHelper.isIFShortcutORComp(info,j);
        ShortcutAnalyser sanalyser = getContext().getShortCutAnalyser();
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
                    int ifclose=IFHelper.getIfCloseNumberForThisIF(info,(currentForIndex+1));
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
