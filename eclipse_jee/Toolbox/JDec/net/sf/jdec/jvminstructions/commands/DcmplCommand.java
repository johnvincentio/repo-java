/*
 *  DcmplCommand.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.blockhelpers.IFHelper;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.core.ShortcutAnalyser;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Constants;

public class DcmplCommand extends AbstractInstructionCommand {

	public DcmplCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {

		int currentForIndex = getCurrentInstPosInCode();
		OperandStack stack = getStack();
		byte[] info= getCode();
		int i = currentForIndex;
		Operand op = (Operand) stack.pop();
		Operand op1 = (Operand) stack.pop();
		Operand op2 = new Operand();
		ShortcutAnalyser sanalyser = getContext().getShortCutAnalyser();
		op2.setOperandType(Constants.IS_CONSTANT_INT);
		int j = i + 1;
		int nextInstruction = info[j];
		boolean sh = getBranchFinder().isIFShortcutORComp(j);
		boolean invert = sanalyser.isLastIfInChain(j);
		if (!invert) {
			java.lang.String connector = sanalyser.getConnector(j);
			if (connector != null
					&& connector.trim().equals(ShortcutAnalyser.AND)) {
				invert = true;
			}
		}
        if (!sh) {
            if (getGenericFinder().isThisInstrStart((currentForIndex + 1))) {
                if (getBranchFinder().isInstructionIF((currentForIndex + 1))){
                    int ifclose = IFHelper.getIfCloseNumberForThisIF(info, (currentForIndex + 1));
                    ifclose = ifclose - 3;
                    if (getGenericFinder().isThisInstrStart(ifclose)) {
                        if (getBranchFinder().isInstructionIF(ifclose) && ifclose != j) {
                            sh = true;
                        }
                    }
                }
            }

        }
		switch (nextInstruction) {
		case JvmOpCodes.IFEQ:
			if (!sh)
				op2.setOperandValue(op1.getOperandValue() + "!="
						+ op.getOperandValue());
			else {
				if (!invert)
					op2.setOperandValue(op1.getOperandValue() + "=="
							+ op.getOperandValue());
				else
					op2.setOperandValue(op1.getOperandValue() + "!="
							+ op.getOperandValue());
			}

			break;
		case JvmOpCodes.IFNE:
			if (!sh)
				op2.setOperandValue(op1.getOperandValue() + "=="
						+ op.getOperandValue());
			else {
				if (!invert)
					op2.setOperandValue(op1.getOperandValue() + "!="
							+ op.getOperandValue());
				else
					op2.setOperandValue(op1.getOperandValue() + "=="
							+ op.getOperandValue());
			}
			break;
		case JvmOpCodes.IFLT:
			if (!sh)
				op2.setOperandValue(op1.getOperandValue() + ">="
						+ op.getOperandValue());
			else {
				if (invert)
					op2.setOperandValue(op1.getOperandValue() + "<"
							+ op.getOperandValue());
				else
					op2.setOperandValue(op1.getOperandValue() + ">="
							+ op.getOperandValue());
			}
			break;
		case JvmOpCodes.IFGE:
			if (!sh)
				op2.setOperandValue(op1.getOperandValue() + "<"
						+ op.getOperandValue());
			else {
				if (invert)
					op2.setOperandValue(op1.getOperandValue() + ">="
							+ op.getOperandValue());
				else
					op2.setOperandValue(op1.getOperandValue() + "<"
							+ op.getOperandValue());
			}
			break;
		case JvmOpCodes.IFGT:
			if (!sh)
				op2.setOperandValue(op1.getOperandValue() + "<="
						+ op.getOperandValue());
			else {
				if (invert)
					op2.setOperandValue(op1.getOperandValue() + ">"
							+ op.getOperandValue());
				else
					op2.setOperandValue(op1.getOperandValue() + "<="
							+ op.getOperandValue());
			}
			break;

		case JvmOpCodes.IFLE:
			if (!sh)
				op2.setOperandValue(op1.getOperandValue() + ">"
						+ op.getOperandValue());
			else {
				if (invert)
					op2.setOperandValue(op1.getOperandValue() + "<="
							+ op.getOperandValue());
				else
					op2.setOperandValue(op1.getOperandValue() + ">"
							+ op.getOperandValue());
			}
			break;
		}
		stack.push(op2);
	}

}
