package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;
import net.sf.jdec.util.ExecutionState;
import net.sf.jdec.core.*;
import net.sf.jdec.lookup.IFinder;

import java.util.ArrayList;

/*
*  AALoadCommand.java Copyright (c) 2006,07 Swaroop Belur
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
public class AALoadCommand extends AbstractInstructionCommand {

    private IFinder genericFinder = getGenericFinder();

    public AALoadCommand(Behaviour context) {
        super(context);
    }

    public int getSkipBytes() {
        return 0;
    }

    public void execute() {
        OperandStack stack = getContext().getOpStack();
        Operand sourcePos = (Operand) stack.pop();
        Operand srcarrayRef = (Operand) stack.pop(); //
        Operand op = createOperand(srcarrayRef.getOperandValue() + "[" + sourcePos.getOperandValue() + "]", Constants.IS_OBJECT_REF, Constants.CATEGORY1);
        boolean r = false;//checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
        /*  if(r){
            if(opStack.size() > 0){
                java.lang.String str=opStack.getTopOfStack().getOperandValue();
                str=str+op.getOperandValue();
                op.setOperandValue(str);
            }
        }*/
        stack.push(op);

        // check to adjust local variable type
        int cur = ExecutionState.getCurrentInstructionPosition();
        byte info[] = getContext().getCode();

        int prev = genericFinder.getPrevStartOfInst(cur);
        prev = genericFinder.getPrevStartOfInst(prev);
        StringBuffer index = new StringBuffer("");
        if (getLoadFinder().isInstAload(prev, index) != -1) {
            int varIndex = Integer.parseInt(index.toString());
            ArrayList variables = getContext().getLocalVariables().getMethodLocalVaribales();
            boolean flag = false;
            boolean complex = false;
            int astoreIndex = -1;
            if (genericFinder.isThisInstrStart(cur + 1)) {
                switch (info[cur + 1]) {
                    case JvmOpCodes.ASTORE:
                        flag = true;
                        complex = true;
                        astoreIndex = info[(cur + 2)];
                        break;

                    case JvmOpCodes.ASTORE_0:
                        flag = true;
                        astoreIndex = 0;
                        break;
                    case JvmOpCodes.ASTORE_1:
                        flag = true;
                        astoreIndex = 1;
                        break;
                    case JvmOpCodes.ASTORE_2:
                        flag = true;
                        astoreIndex = 2;
                        break;
                    case JvmOpCodes.ASTORE_3:
                        flag = true;
                        astoreIndex = 3;
                        break;

                }
            }
            if (flag) {
                for (int z = 0; z < variables.size(); z++) {
                    LocalVariable lv = (LocalVariable) variables.get(z);
                    if (lv.getIndexPos() == varIndex) {
                        java.lang.String type = lv.getDataType();
                        java.lang.String typeCopy = type;
                        if (type != null && type.trim().length() > 0) {
                            type = type.replaceAll("\\[", "");
                            type = type.replaceAll("\\]", "");
                            type = type.trim();
                            LocalVariable local = null;
                            if (complex) {
                                local = DecompilerHelper.getLocalVariable(astoreIndex, "store", "java.lang.Object", false, ExecutionState.getCurrentInstructionPosition());
                            } else {
                                local = DecompilerHelper.getLocalVariable(astoreIndex, "store", "java.lang.Object", true, ExecutionState.getCurrentInstructionPosition());
                            }
                            if (local != null) {
                                int bracket = typeCopy.indexOf("[");
                                int bracketCount = 0;
                                while (bracket != -1) {
                                    bracketCount++;
                                    bracket = typeCopy.indexOf("[", bracket + 1);
                                }
                                java.lang.String bracketStr = "";
                                for (int x = 0; x < bracketCount - 1; x++) {
                                    bracketStr += "[]";
                                }
                                local.setDataType(type + " " + bracketStr);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
