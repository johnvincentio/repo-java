package net.sf.jdec.jvminstructions.commands;

import java.util.Hashtable;

import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.ClassInfo;
import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.LocalVariableStructure;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;

public class MultiAnewArrayCommand extends AbstractInstructionCommand {

	public MultiAnewArrayCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 3;
	}

	public void execute() {
		GlobalVariableStore.setMultinewfound(true);
		int i = getCurrentInstPosInCode();
        int classIndex=getGenericFinder().getOffset(i);//(info[++i] << 8) | info[++i]);
        i+=2;
        ClassDescription cd = getContext().getClassRef().getCd();
        ClassInfo cinfo=cd.getClassInfoAtCPoolPosition(classIndex);
        java.lang.String temp=cd.getUTF8String(cinfo.getUtf8pointer());;
        LocalVariableStructure structure = getContext().getLocalVariables();
        int lastBracket=temp.lastIndexOf("[");
        temp=temp.substring(lastBracket+1);
        temp=DecompilerHelper.getArrayType(temp);
        Hashtable variableDimAss = GlobalVariableStore.getVariableDimAss();
        byte[] info = getCode();
        int dimensions=info[++i];
        OperandStack opStack = getStack();
        int Temp=i+1;
        int nextindex=-1;
        boolean ty=false;
        if(info[Temp] == JvmOpCodes.ASTORE) {
            nextindex=info[(Temp+1)];
            ty=true;
        }
        if(info[Temp] == JvmOpCodes.ASTORE_0) {
            
            nextindex=0;
        }
        if(info[Temp] == JvmOpCodes.ASTORE_1) {
            
            nextindex=1;
        }
        if(info[Temp] == JvmOpCodes.ASTORE_2) {
            
            nextindex=2;
        }
        if(info[Temp] == JvmOpCodes.ASTORE_3) {
            
            nextindex=3;
        }
        int d=-1;
        int cnt=-1;
        if(variableDimAss!=null) {
            Integer n=(Integer)variableDimAss.get(new Integer(nextindex));
            d=-1;
            
            if(n!=null) {
                d=n.intValue();
            }
        } else {
            if(cd.isClassCompiledWithMinusG() ) {
                LocalVariable lv;
                java.lang.String tpe=null;
                
                if(ty) {
                    lv=structure.getVariabelAtIndex(nextindex,Temp+2) ;
                    if(lv!=null)
                        tpe=lv.getDataType();
                    
                } else {
                    lv=structure.getVariabelAtIndex(nextindex,Temp+1) ;
                    if(lv!=null)
                        tpe=lv.getDataType();
                }
                if(tpe!=null) {
                    int openb=tpe.indexOf("[");
                    
                    if(openb!=-1) {
                        cnt=1;
                        
                        while((openb+1) < tpe.length()) {
                            
                            char ch=tpe.charAt((openb+1));
                            if(ch=='[') {
                                cnt++;
                                
                            }
                            openb++;
                        }
                        
                    }
                }
            }
            
        }
        
        
        Operand ops[]=new Operand[dimensions];
        java.lang.String dimenPart="[";
        for(int indx=0;indx<dimensions;indx++) {
            ops[indx]=opStack.getTopOfStack();
            
        }
        
        // Reverse Array
        Operand opsTemp[]=new Operand[dimensions];
        int lastPos=dimensions-1;
        for(int indx=0;indx<dimensions;indx++) {
            opsTemp[indx]=ops[lastPos];
            lastPos--;
            
        }
        for(int indx=0;indx<dimensions;indx++) {
            dimenPart+=opsTemp[indx].getOperandValue()+"]";
            if(indx!=dimensions-1)dimenPart+="[";
        }
        if(d > dimensions  ) {
            int rem=d-dimensions;
            for(int zz=0;zz<rem;zz++) {
                dimenPart+="[]";
            }
        }
        
        if(cnt > dimensions  && d==-1) {
            int rem=cnt-dimensions;
            for(int zz=0;zz<rem;zz++) {
                dimenPart+="[]";
            }
        }
        if(cd.isClassCompiledWithMinusG()==false) {
            variableDimAss.put(new Integer(nextindex),new Integer(dimensions));
        }
        
        Operand op2 = new Operand();
        op2.setClassType(temp);
        temp="new "+temp+dimenPart;
        
        op2.setCategory(Constants.CATEGORY1);
        op2.setOperandType(Constants.IS_ARRAY_REF);
        op2.setOperandValue(temp);
        op2.setMultiDimension(true);
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
