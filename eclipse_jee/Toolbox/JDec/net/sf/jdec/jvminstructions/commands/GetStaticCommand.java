package net.sf.jdec.jvminstructions.commands;

import java.util.ArrayList;

import net.sf.jdec.config.Configuration;
import net.sf.jdec.constantpool.ClassDescription;
import net.sf.jdec.constantpool.FieldRef;
import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.Operand;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class GetStaticCommand extends AbstractInstructionCommand {

	public GetStaticCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		int currentForIndex = getCurrentInstPosInCode();
		int i=currentForIndex;
		int  classIndex=getGenericFinder().getOffset(i);
		ClassDescription cd = getContext().getClassRef().getCd();
		byte[] info = getCode();
		boolean skip=DecompilerHelper.skipGetStaticCall(info,currentForIndex);
        if(skip)
            return;
        
        
        FieldRef fref = cd.getFieldRefAtCPoolPosition(classIndex);
        StringBuffer stbf=new StringBuffer("");
        java.lang.String ft=fref.getTypeoffield();
        if(ft.startsWith("L")) {
            ft=ft.substring(1);
        }
        if(ft.indexOf(";")!=-1) {
            ft=ft.substring(0,ft.indexOf(";"));
        }
        Util.checkForImport(ft,stbf);
        
        Operand op = new Operand();
        //(Constants.CATEGORY1);
       /* if(opStack.size() > 0 && isThisInstrStart(behaviour.getInstructionStartPositions(),(currentForIndex-1)) && (info[currentForIndex-1]==JvmOpCodes.POP || info[currentForIndex-1]==JvmOpCodes.POP2 && opStack.size() > 0)) {
            java.lang.String opv=opStack.getTopOfStack().getOperandValue();
            if(opv!=null) {
                
                
                StringBuffer getst=new StringBuffer("");
                java.lang.String x=opv.replace('/','.');
                int dot=x.indexOf(".");
                if(dot!=-1) {
                    x=x.substring(0,dot);
                    Util.checkForImport(x,getst);
                    
                }
                if(Configuration.getShowImport().equalsIgnoreCase("true")) {
                    op.setOperandValue(fref.getFieldName());
                } else {
                    
                    op.setOperandValue(opv.replace('/','.')+"."+fref.getFieldName());
                }
                
                
            }
            
            else {
                StringBuffer getst=new StringBuffer("");
                Util.checkForImport(fref.getClassname().replace('/','.'),getst);
                if(Configuration.getShowImport().equalsIgnoreCase("true")) {
                    op.setOperandValue(fref.getFieldName());
                } else {
                    
                    op.setOperandValue(fref.getClassname().replace('/','.')+"."+fref.getFieldName());
                }
            }
            
            
            
        } else {*/
            
            StringBuffer getst=new StringBuffer("");
            Util.checkForImport(fref.getClassname().replace('/','.'),getst);
            if(Configuration.getShowImport().equalsIgnoreCase("true")) {
                op.setOperandValue(getst.toString()+"."+fref.getFieldName());
            } else {
                
                op.setOperandValue(fref.getClassname().replace('/','.')+"."+fref.getFieldName());
            }
            
            
            
        //}
        Util.parseReturnType(fref.getTypeoffield());
        ArrayList returntype=Util.getreturnSignatureAsList();
        
        if(returntype.size() > 0) {
            java.lang.Object tempv=returntype.get(0);
            op.setClassType((java.lang.String)tempv);
        }
        boolean  r=false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
        if(r){
           /* if(opStack.size() > 0){
                java.lang.String str=opStack.getTopOfStack().getOperandValue();
                str=str+op.getOperandValue();
                op.setOperandValue(str);
            }*/
        }
        getStack().push(op);
        java.lang.String fieldname=fref.getFieldName();
        java.lang.String fieldclassname=fref.getClassname();
        if(fieldname!=null && fieldname.equals("TYPE") && fieldclassname!=null) {
            
            java.lang.String temp=fieldclassname.replaceAll("/",".");
            StringBuffer csb=new StringBuffer("");
            Util.checkForImport(temp,csb);
            java.lang.String cname=csb.toString()+"."+fieldname;
            getStack().peekTopOfStack().setOperandValue(cname);
        }
        
	}

}
