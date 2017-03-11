package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;
/*
 *  Aload_3Command.java Copyright (c) 2006,07 Swaroop Belur
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
public class Aload_3Command extends AbstractInstructionCommand{

    public Aload_3Command(Behaviour context) {
        super(context);
    }

    public int getSkipBytes() {
        return 0;
    }

    public void execute() {
        OperandStack opStack = getContext().getOpStack();
        int currentForIndex= getCurrentInstPosInCode();
        LocalVariable local= DecompilerHelper.getLocalVariable(3,"load","java.lang.Object",true,currentForIndex);
        if(local==null) {
            local=new LocalVariable();
            local.setVarName("this");
        }


        Operand op = new Operand();
        op.setOperandName(local.getVarName());
        op.setOperandValue(local.getVarName());
        op.setOperandType(Constants.IS_OBJECT_REF);
        boolean r=false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
        if(r){
            if(opStack.size() > 0){
                String str=opStack.getTopOfStack().getOperandValue();
                str=str+op.getOperandValue();
                op.setOperandValue(str);
            }
        }

        opStack.push(op);
        op.setClassType(local.getDataType());
    }
}
