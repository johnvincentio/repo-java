package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.util.Util;
import net.sf.jdec.reflection.Behaviour;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
/*
 *  AReturnCommand.java Copyright (c) 2006,07 Swaroop Belur
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

/**
 * @author swaroop belur
 * @since 1.2.1
 */
public class AReturnCommand extends AbstractInstructionCommand{
    public AReturnCommand(Behaviour context) {
        super(context);
    }

    public int getSkipBytes() {
        return 0;
    }

    public void execute() {
    	Behaviour behavior = getContext();
        boolean oktoadd=true;
        int i = getCurrentInstPosInCode();
        
        HashMap returnsAtI = GlobalVariableStore.getReturnsAtI();
        OperandStack opStack = getContext().getOpStack();
        Iterator mapIT=returnsAtI.entrySet().iterator();
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
        Object obj=returnsAtI.get(new Integer(i));
        java.lang.String tempString="";
        if(oktoadd && opStack.size() > 0){
            //System.out.println(currentForIndex+"i"+behaviour.getBehaviourName());
            Operand op = (Operand)opStack.pop();

            if(op.getOperandValue()!=null && !op.getOperandValue().toString().trim().endsWith(";"))
                tempString="return "+op.getOperandValue()+";\n";
            else
                tempString="return "+op.getOperandValue()+"\n";
            behavior.appendToBuffer( Util.formatDecompiledStatement(tempString));


        }
        GlobalVariableStore.setReturnsAtI(returnsAtI);
       
    }
}
