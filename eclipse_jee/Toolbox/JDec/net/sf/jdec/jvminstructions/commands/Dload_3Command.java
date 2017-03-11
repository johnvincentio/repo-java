/*
 *  DloadCommand.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.core.DecompilerHelper;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class Dload_3Command extends AbstractInstructionCommand {

	public Dload_3Command(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		int currentForIndex = getCurrentInstPosInCode();
		int index = 3;
		OperandStack stack = getStack();
		LocalVariable local = DecompilerHelper.getLocalVariable(index, "load",
				"double", true, currentForIndex);
		if (local != null) {
			Operand op = new Operand();
			byte[] info = getCode();
			StringBuffer addsub = new StringBuffer("");
			boolean bo = DecompilerHelper.checkForPostIncrForLoadCase(info,
					currentForIndex, "category2", false, index, addsub);
			if (!bo)
				op.setOperandValue(local.getVarName());
			else
				op.setOperandValue(local.getVarName() + addsub.toString());

			boolean r = false;// checkIFLoadInstIsPartOFTernaryCond(currentForIndex);
			if (r) {
				if (stack.size() > 0) {
					java.lang.String str = stack.getTopOfStack()
							.getOperandValue();
					str = str + op.getOperandValue();
					op.setOperandValue(str);
				}
			}
			stack.push(op);

		}

	}

}
