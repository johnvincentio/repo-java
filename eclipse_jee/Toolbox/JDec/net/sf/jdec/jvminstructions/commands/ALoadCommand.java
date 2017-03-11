
/*
 *  ALoadCommand.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.util.Constants;
import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;


/**
 * @author swaroop belur
 * @since 1.2.1
 */
public class ALoadCommand extends AbstractInstructionCommand{

    public ALoadCommand(Behaviour context) {
        super(context);
    }


    public int getSkipBytes() {
        return 1;
    }

    public void execute() {
        Operand op=null;
        OperandStack opStack = getContext().getOpStack();
        byte[] info=getContext().getCode();
        int currentForIndex;
        int i = currentForIndex = getCurrentInstPosInCode();
        int opValueI = info[++i];
        LocalVariable local= DecompilerHelper.getLocalVariable(opValueI, "load","java.lang.Object",false,currentForIndex);

        if(local!=null) {
            op = new Operand();
            op.setOperandName(local.getVarName());
            op.setOperandValue(local.getVarName());
            op.setOperandType(Constants.IS_OBJECT_REF);
            boolean r=false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
            /*if(r){
                if(opStack.size() > 0){
                    java.lang.String str=opStack.getTopOfStack().getOperandValue();
                    str=str+op.getOperandValue();
                    op.setOperandValue(str);
                }
            }*/
            opStack.push(op);
            op.setClassType(local.getDataType());

        } else {
            op = new Operand();
            op.setOperandName("this");
            op.setOperandValue("this");
            op.setOperandType(Constants.IS_OBJECT_REF);
            boolean r=false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
            if(r){
                if(opStack.size() > 0){
                    java.lang.String str=opStack.getTopOfStack().getOperandValue();
                    str=str+op.getOperandValue();
                    op.setOperandValue(str);
                }
            }

            opStack.push(op);
            op.setClassType(ConsoleLauncher.getClazzRef()!=null? ConsoleLauncher.getClazzRef().getClassName():"Object");
        }
    }
}
