/*
 *  Dupx2Command.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.core.GlobalVariableStore;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.Operand;
import net.sf.jdec.core.OperandStack;
import net.sf.jdec.reflection.Behaviour;

public class Dupx2Command extends AbstractInstructionCommand {

	public Dupx2Command(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 0;
	}

	public void execute() {
		OperandStack opStack = getStack();
		int currentForIndex = getCurrentInstPosInCode();
		Operand op1 = (Operand) opStack.pop();
		Operand op2 = (Operand) opStack.pop();
		Operand op3 = (Operand) opStack.pop();
		boolean add = true;
		int next = currentForIndex + 1;
		int prev = currentForIndex - 1;
		byte[] code = getCode();
		boolean specialCase = false;
		if (code[next] == JvmOpCodes.FASTORE) {
			if (code[prev] == JvmOpCodes.FADD || code[prev] == JvmOpCodes.FSUB) {
				specialCase = true;
				if (code[prev] == JvmOpCodes.FSUB)
					add = false;
			}
		}
		if (!specialCase) {
			if (code[next] == JvmOpCodes.IASTORE) {
				if (code[prev] == JvmOpCodes.IADD
						|| code[prev] == JvmOpCodes.ISUB) {
					specialCase = true;
					if (code[prev] == JvmOpCodes.ISUB)
						add = false;
				}
			}
		}
		if (!specialCase) {
			if (code[next] == JvmOpCodes.SASTORE) {
				if (code[prev] == JvmOpCodes.I2S) {
					prev = prev - 1;
					if (code[prev] == JvmOpCodes.IADD
							|| code[prev] == JvmOpCodes.ISUB) {
						specialCase = true;
						if (code[prev] == JvmOpCodes.ISUB)
							add = false;
					}
				}
			}
		}
		if (!specialCase) {
			if (code[next] == JvmOpCodes.BASTORE) {
				if (code[prev] == JvmOpCodes.I2B) {
					prev = prev - 1;
					if (code[prev] == JvmOpCodes.IADD
							|| code[prev] == JvmOpCodes.ISUB) {
						specialCase = true;
						if (code[prev] == JvmOpCodes.ISUB)
							add = false;
					}
				}
			}
		}
		if (!specialCase) {
			if (code[next] == JvmOpCodes.CASTORE) {
				if (code[prev] == JvmOpCodes.I2C) {
					prev = prev - 1;
					if (code[prev] == JvmOpCodes.IADD
							|| code[prev] == JvmOpCodes.ISUB) {
						specialCase = true;
						if (code[prev] == JvmOpCodes.ISUB)
							add = false;
					}
				}
			}
		}
		if (!specialCase) {
			opStack.push(op1);
			opStack.push(op3);
			opStack.push(op2);
			opStack.push(op1);
		} else {

			// stack.push(op3);
			// stack.push(op4);
			java.lang.String temp = "";
			boolean skip = true;
			/*
			 * if(code[prev]==JvmOpCodes.FADD || code[prev]==JvmOpCodes.IADD){
			 * temp="++"+op3.getOperandValue()+"[" +op2.getOperandValue()+"]"; }
			 * else if(code[prev]==JvmOpCodes.FSUB ||
			 * code[prev]==JvmOpCodes.ISUB){ temp="--"+op3.getOperandValue()+"["
			 * +op2.getOperandValue()+"]"; } else{
			 * temp=op3.getOperandValue()+"[" +op2.getOperandValue()+"]";
			 * skip=false; }
			 */
			if (newfound() && GlobalVariableStore.getArraytimesstack().size() > 0) {
				java.lang.String v = "";
				if (add) {
					int br = op1.getOperandValue().indexOf("(");
					if (br == -1) {
						v = "++"
								+ op1.getOperandValue()
										.substring(
												0,
												op1.getOperandValue()
														.lastIndexOf("]") + 1);
						opStack.push(createOperand(v));
					} else {
						v = "++"
								+ op1.getOperandValue()
										.substring(
												br + 1,
												op1.getOperandValue()
														.lastIndexOf("]") + 1);
						opStack.push(createOperand(v));
					}
				} else {
					int br = op1.getOperandValue().indexOf("(");
					if (br == -1) {
						v = "--"
								+ op1.getOperandValue()
										.substring(
												0,
												op1.getOperandValue()
														.lastIndexOf("]") + 1);
						opStack.push(createOperand(v));
					} else {
						v = "--"
								+ op1.getOperandValue()
										.substring(
												br + 1,
												op1.getOperandValue()
														.lastIndexOf("]") + 1);
						opStack.push(createOperand(v));
					}
				}
				opStack.push(createOperand(v));
				opStack.push(op3);
				opStack.push(op2);
				opStack.push(createOperand(v));
			} else {
				temp = op3.getOperandValue() + "[" + op2.getOperandValue()
						+ "]";
				Operand otemp = new Operand();
				otemp.setOperandValue(temp);
				opStack.push(otemp);
				opStack.push(op3);
				opStack.push(op2);
				opStack.push(op1);
			}

			/*
			 * if(!skip){ Operand otemp=new Operand();
			 * otemp.setOperandValue(temp); opStack.push(otemp);
			 * opStack.push(op3); opStack.push(op2); opStack.push(op1); } else{
			 * Operand otemp=new Operand(); otemp.setOperandValue(temp);
			 * opStack.push(otemp); }
			 */
		}

	}

}
