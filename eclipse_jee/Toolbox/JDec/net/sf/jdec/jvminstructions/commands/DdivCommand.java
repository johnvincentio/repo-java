/*
 *  DdivCommand.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class DdivCommand extends AbstractInstructionCommand {

	public DdivCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack stack = getStack();
	    Operand    op = (Operand)stack.pop();
        Operand     op1 = (Operand)stack.pop();
        Operand    op2 = new Operand();
        op2.setOperandValue("("+op1.getOperandValue()+"/"+op.getOperandValue()+")");
        boolean r=false;//checkIFLoadInstIsPartOFTernaryCond(getCurrentInstPosInCode());
        if(r){
            if(stack.size() > 0){
                java.lang.String str=stack.getTopOfStack().getOperandValue();
                str=str+op2.getOperandValue();
                op2.setOperandValue(str);
            }
        }
        stack.push(op2);
		
	}

}
