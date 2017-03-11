/*
 *  Dconst_0.java Copyright (c) 2006,07 Swaroop Belur
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
import net.sf.jdec.util.Constants;

public class Dconst_0Command extends AbstractInstructionCommand {

	public Dconst_0Command(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		Operand op = new Operand();
        java.lang.String str=""+0.0;
        op.setOperandValue(new Double(str));
        op.setOperandType(Constants.IS_CONSTANT_DOUBLE);
        OperandStack stack= getStack();
        boolean r=false;//checkIFLoadInstIsPartOFTernaryCond(getCurrentInstPosInCode());
        if(r){
            if(stack.size() > 0){
                java.lang.String string=stack.getTopOfStack().getOperandValue();
                string=string+op.getOperandValue();
                op.setOperandValue(str);
            }
        }
        stack.push(op);
	}

}
