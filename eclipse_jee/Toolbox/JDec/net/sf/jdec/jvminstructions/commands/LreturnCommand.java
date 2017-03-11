package net.sf.jdec.jvminstructions.commands;

import java.util.Iterator;
import java.util.Map;

import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class LreturnCommand extends AbstractInstructionCommand {

	public LreturnCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		 Map returnsAtI = GlobalVariableStore.getReturnsAtI();
		 int i = getCurrentInstPosInCode();
		 boolean oktoadd=true;
         Iterator  mapIT=returnsAtI.entrySet().iterator();
         while(mapIT.hasNext()) {
             Map.Entry entry=(Map.Entry)mapIT.next();
             Object key=entry.getKey();
             Object retStatus=entry.getValue().toString();
             if(key instanceof Integer) {
                 Integer pos=(Integer)key;
                 int temp=pos.intValue();
                 if(temp==i) {
                     if(retStatus.equals("true")) {
                         
                         oktoadd=false;
                         break;
                     }
                 }
             }
             
         }
         
         
         if(!oktoadd) {
             returnsAtI.remove(new Integer(i));
         }
         OperandStack opStack = getStack();
         if(oktoadd && opStack.size() > 0){
             Operand op = opStack.getTopOfStack();
             String tempString="return "+op.getOperandValue().toString()+";\n";
             Behaviour behavior = getContext();
             behavior.appendToBuffer(Util.formatDecompiledStatement(tempString));
             
         }
         
         
	}

}
