/*
 *  DreturnCommand.java Copyright (c) 2006,07 Swaroop Belur
 *
 * This program is free software; you can redistribute it and/itor
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
package net.sf.jdec.jvminstructions.commands;

import java.util.Iterator;
import java.util.Map;

import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.Operand;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

public class DreturnCommand extends AbstractInstructionCommand {

	public DreturnCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
	    boolean oktoadd=true;
	    Behaviour behavior = getContext();
	    
        Iterator mapIT=GlobalVariableStore.getReturnsAtI().entrySet().iterator();
        int i=getCurrentInstPosInCode();
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
            GlobalVariableStore.getReturnsAtI().remove(new Integer(i));
        }
        
        if(oktoadd && getStack().size() > 0){
            //System.out.println(currentForIndex+"i"+behaviour.getBehaviourName());
            Operand op = (Operand)getStack().pop();
            String tempString="return "+op.getOperandValue().toString()+";\n";
            behavior.appendToBuffer(Util.formatDecompiledStatement(tempString));
        }
        
      
        
	}

}
