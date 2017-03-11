/*
 *  DupCommand.java Copyright (c) 2006,07 Swaroop Belur
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
import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class DupCommand extends AbstractInstructionCommand {

	public DupCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		
		byte[] info = getCode();
		OperandStack opStack = getStack();
		boolean dupnothandled = GlobalVariableStore.isDupnothandled();
		int currentForIndex  = getCurrentInstPosInCode();
		if ((getGenericFinder().isThisInstrStart((currentForIndex - 3)) && info[(currentForIndex - 3)] == JvmOpCodes.ANEWARRAY)
				&& newfound()) {
			dupnothandled = true;
		} else if (newfound()
				&& (DecompilerHelper.checkForSizeOfArrayTimesStack() || DecompilerHelper.prevNewPresent() || GlobalVariableStore.getTernList().size() > 0)) {
			dupnothandled = true;
		}
		// else if(dupStoreForTerIf){}
		else {
			if (!GlobalVariableStore.isEmbeddedANEWARRAY() && !GlobalVariableStore.isEmbeddedNEWARRAY() && (opStack.size() > 0)) {
				Operand op1 = (Operand) opStack.pop();
				opStack.push(op1);
				opStack.push(op1);
			} else {
				if (GlobalVariableStore.isEmbeddedANEWARRAY())
					GlobalVariableStore.setEmbeddedANEWARRAY(false);
				if (GlobalVariableStore.isEmbeddedNEWARRAY())
					GlobalVariableStore.setEmbeddedNEWARRAY(false);

			}
			dupnothandled = false;
			// Test This
			if (GlobalVariableStore.isDoNotPop()) {
				GlobalVariableStore.setDoNotPop(false);
			}
		}
		
		GlobalVariableStore.setDupnothandled(dupnothandled);

	}

}
