package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.Operand;
import net.sf.jdec.jvminstructions.util.TypeConstants;
import net.sf.jdec.reflection.Behaviour;

/*
*  ILoad_0Command.java Copyright (c) 2006,07 Swaroop Belur
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
public class ILoad_2Command extends AbstractInstructionCommand {


    private static final int commandIndex = 2;


    public ILoad_2Command(Behaviour context) {
        super(context);
    }

    public int getSkipBytes() {
        return 0;
    }


    public void execute() {
        LocalVariable local = DecompilerHelper.getLocalVariable(commandIndex, TypeConstants.LOAD, TypeConstants.INT, true, getCurrentInstPosInCode());
        if (local != null) {
            boolean terEnd = false;//isLoadTernaryEnd(getCurrentInstPosInCode());
            Operand op = new Operand();
            java.lang.String ltmp = local.getTempVarName();
            boolean bo = getGenericFinder().isPrevInstIINC(getCurrentInstPosInCode(), commandIndex);
            boolean bo2 = false;
            StringBuffer addsub = new StringBuffer("");
            if (!bo) {
                bo = getGenericFinder().isNextInstIINC(getCurrentInstPosInCode(), commandIndex, TypeConstants.SIMPLE);
            }
            if (!bo) {

                bo = DecompilerHelper.checkForPostIncrForLoadCase(getCode(), getCurrentInstPosInCode(), TypeConstants.CATEGORY1, false, commandIndex, addsub);
                bo2 = bo;
            }
            if (!terEnd) {
                if (bo && ltmp != null) {
                    op.setOperandValue(ltmp);
                } else {
                    if (!bo2)
                        op.setOperandValue(local.getVarName());
                    else {
                        op.setOperandValue(local.getVarName() + addsub.toString());
                    }

                }
            } else {
                if (bo) {
                    java.lang.String v1 = getTopOfStack().getOperandValue();
                    op.setOperandValue(v1 + ltmp);
                } else {
                    java.lang.String v1 = getTopOfStack().getOperandValue();
                    op.setOperandValue(v1 + local.getVarName());
                }

            }

            op.setLocalVarIndex(commandIndex);
            op.setLocalVarType(local.getDataType());
            boolean r = false;//checkIFLoadInstIsPartOFTernaryCond(getCurrentInstPosInCode());
            if (r) {
                if (getStack().size() > 0) {
                    java.lang.String str = getStack().getTopOfStack().getOperandValue();
                    str = str + op.getOperandValue();
                    op.setOperandValue(str);
                }
            }
            getStack().push(op);
        }
    }
}
