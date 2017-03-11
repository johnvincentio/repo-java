/*
 *  AthrowCommand.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.blocks.FinallyBlock;
import net.sf.jdec.blocks.TryBlock;
import net.sf.jdec.core.*;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.util.Util;

import java.util.ArrayList;

public class AthrowCommand extends AbstractInstructionCommand {

	public AthrowCommand(Behaviour context) {
		super(context);

	}

	
	public int getSkipBytes() {
		return 0;
	}

	public void execute() {

        boolean add = true;
		Behaviour behavior = getContext();
		int currentForIndex = getCurrentInstPosInCode();
		int i = currentForIndex;
		boolean aloadPresent = getLoadFinder().isPrevInstructionAload(i,
				new StringBuffer(""));
		OperandStack opStack = getContext().getOpStack();

		// Problem:
		// Some Defect Here. If Exception thrown as the last stmt in finally in
		// java code
		// Then it will not show it...
		// TODO: Keep Testing This.

		ArrayList methodTries = getContext().getAllTriesForMethod();
		if (aloadPresent == true) // Is this enough to make add=false ?
		{
			// The following is just to confirm that this is indeed the end of
			// finally
			if (methodTries != null) {
				for (int st = 0; st < methodTries.size(); st++) {
					TryBlock tryblk = (TryBlock) methodTries.get(st);
					if (tryblk != null) {
						FinallyBlock fin = tryblk.getFinallyBlock();
						if (fin != null) {

							int finEnd = fin.getEnd();
							if (finEnd == i) {
								add = false;
								break;
							} else if (finEnd != i) {
								int finStart = fin.getStart();
								int fromWhere = i;
								int tillWhere = finStart;
								while (fromWhere >= tillWhere) {
									byte[] info = getContext().getCode();
									int inst = info[fromWhere];
									if (inst == JvmOpCodes.JSR) // TODO : Check
																// for Jsr_w
									{
										add = false;
										break;
									}
									fromWhere--;
								}

							} else
								add = true;
						}
					}

				}
			}
		}

		if (add && DecompilerHelper.addATHROWOutput(currentForIndex)) {
			Operand op = (Operand) opStack.pop();
			opStack.push(op);
			String tempString = "throw " + op.getOperandValue() + ";\n";
			behavior.appendToBuffer("\n"+Util.formatDecompiledStatement(tempString));
            GlobalVariableStore.setAthrowseen(true);
            GlobalVariableStore.setAthrowseenpos(currentForIndex);
        }
	}

}
